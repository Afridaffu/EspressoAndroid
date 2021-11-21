package com.greenbox.coyni.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;

import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdDisabled_BottomSheet;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.UUID;

public class AuthLoginActivity extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    Cursor dsPermanentToken, dsFacePin, dsTouchID;
    String strToken = "", strDeviceID = "";
    Boolean isFaceLock = false, isTouchId = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    Boolean isBiometric = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_auth_login);
            if (!isDeviceID()) {
                generateUUID();
            }
            strDeviceID = Utils.getDeviceID();

            String url = BuildConfig.URL_PRODUCTION;
            String refererUrl = BuildConfig.Referer;
            Utils.setStrCCode(BuildConfig.Country_Code);
            if (!url.equals("")) {
                Utils.setStrURL_PRODUCTION(url);
                Utils.setStrReferer(refererUrl);
            }
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            if (Utils.checkBiometric(AuthLoginActivity.this) && Utils.checkAuthentication(AuthLoginActivity.this)) {
                if (Utils.isFingerPrint(AuthLoginActivity.this)) {
                    Utils.setIsTouchEnabled(true);
                    Utils.setIsFaceEnabled(false);
                } else {
                    Utils.setIsTouchEnabled(false);
                    Utils.setIsFaceEnabled(true);
                }
            } else {
                Utils.setIsTouchEnabled(false);
                Utils.setIsFaceEnabled(false);
            }
            SetToken();
            SetFaceLock();
            SetTouchId();
            isBiometric = Utils.checkBiometric(AuthLoginActivity.this);
            initObserver();
            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(AuthLoginActivity.this)) {
                if (isBiometric && ((isTouchId && Utils.isFingerPrint(AuthLoginActivity.this)) || (isFaceLock))) {
                    Utils.checkAuthentication(AuthLoginActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                } else {
                    FaceIdDisabled_BottomSheet faceIdDisable_bottomSheet = FaceIdDisabled_BottomSheet.newInstance(isTouchId, isFaceLock);
                    faceIdDisable_bottomSheet.show(getSupportFragmentManager(), faceIdDisable_bottomSheet.getTag());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.checkInternet(AuthLoginActivity.this)) {
                            if (!strToken.equals("") && !strDeviceID.equals("")) {
                                login();
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), AuthLoginActivity.this, "");
                        }
                    }
                });
            } else {
                Intent i = new Intent(AuthLoginActivity.this, LoginActivity.class);
                startActivity(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                dialog.dismiss();
                try {
                    if (loginResponse != null) {
                        if (!loginResponse.getStatus().toLowerCase().equals("error")) {
                            if (loginResponse.getData().getPasswordExpired()) {
                                Intent i = new Intent(AuthLoginActivity.this, PINActivity.class);
                                i.putExtra("screen", "loginExpiry");
                                i.putExtra("TYPE", "ENTER");
                                startActivity(i);
                            } else {
                                Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                Intent i = new Intent(AuthLoginActivity.this, DashboardActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } else {
                            if (loginResponse.getData() != null) {
                                if (!loginResponse.getData().getMessage().equals("") && loginResponse.getData().getPasswordFailedAttempts() > 0) {
                                    Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                                    emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());
                                }
                            } else {
                                Utils.displayAlert(loginResponse.getError().getErrorDescription(), AuthLoginActivity.this, "");
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void SetToken() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsPermanentToken = mydatabase.rawQuery("Select * from tblPermanentToken", null);
            dsPermanentToken.moveToFirst();
            if (dsPermanentToken.getCount() > 0) {
                strToken = dsPermanentToken.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetFaceLock() {
        try {
            isFaceLock = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                } else {
                    isFaceLock = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetTouchId() {
        try {
            isTouchId = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsTouchID.moveToFirst();
            if (dsTouchID.getCount() > 0) {
                String value = dsTouchID.getString(1);
                if (value.equals("true")) {
                    isTouchId = true;
                } else {
                    isTouchId = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void login() {
        try {
            dialog = new ProgressDialog(AuthLoginActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            BiometricLoginRequest request = new BiometricLoginRequest();
            request.setDeviceId(strDeviceID);
            request.setEnableBiometic(true);
            request.setMobileToken(strToken);
            loginViewModel.biometricLogin(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generateUUID() {
        try {
            String uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = getSharedPreferences("DeviceID", MODE_PRIVATE).edit();
            editor.putString("deviceId", uuid);
            editor.putBoolean("isDevice", true);
            editor.apply();
            Utils.setDeviceID(uuid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean isDeviceID() {
        Boolean value = false;
        SharedPreferences prefs = getSharedPreferences("DeviceID", MODE_PRIVATE);
        value = prefs.getBoolean("isDevice", false);
        if (value) {
            Utils.setDeviceID(prefs.getString("deviceId", ""));
        }
        return value;
    }
}
package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AutoScrollPagerAdapter;
import com.greenbox.coyni.fragments.FaceIdDisabled_BottomSheet;
import com.greenbox.coyni.fragments.FaceIdNotAvailable_BottomSheet;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Singleton;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.UUID;

public class OnboardActivity extends AppCompatActivity {
    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 3000;
    LinearLayout getStarted, layoutLogin;
    Long mLastClickTime = 0L;
    SQLiteDatabase mydatabase;
    Cursor dsPermanentToken, dsFacePin, dsTouchID;
    String strToken = "", strDeviceID = "";
    Boolean isFaceLock = false, isTouchId = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    public static OnboardActivity onboardActivity;
    Boolean isBiometric = false;
    RelativeLayout layoutOnBoarding, layoutAuth;
    MyApplication objMyApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_onboard);
            onboardActivity = this;
            layoutOnBoarding = findViewById(R.id.layoutOnBoarding);
            layoutAuth = findViewById(R.id.layoutAuth);
            objMyApplication = (MyApplication) getApplicationContext();
            if (Utils.checkBiometric(OnboardActivity.this) && Utils.checkAuthentication(OnboardActivity.this)) {
                if (Utils.isFingerPrint(OnboardActivity.this)) {
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
            isBiometric = Utils.checkBiometric(OnboardActivity.this);
            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(OnboardActivity.this)) {
                if (isBiometric && ((isTouchId && Utils.isFingerPrint(OnboardActivity.this)) || (isFaceLock))) {
                    layoutOnBoarding.setVisibility(View.GONE);
                    layoutAuth.setVisibility(View.VISIBLE);
                    Utils.checkAuthentication(OnboardActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                } else {
                    FaceIdDisabled_BottomSheet faceIdDisable_bottomSheet = FaceIdDisabled_BottomSheet.newInstance(isTouchId, isFaceLock);
                    faceIdDisable_bottomSheet.show(getSupportFragmentManager(), faceIdDisable_bottomSheet.getTag());
                }
            } else {
                layoutOnBoarding.setVisibility(View.VISIBLE);
                layoutAuth.setVisibility(View.GONE);
            }

            if (!isDeviceID()) {
                generateUUID();
            }
            strDeviceID = Utils.getDeviceID();
            getStarted = findViewById(R.id.getStartedLL);
            layoutLogin = findViewById(R.id.layoutLogin);
            String url = BuildConfig.URL_PRODUCTION;
            String refererUrl = BuildConfig.Referer;
            Utils.setStrCCode(BuildConfig.Country_Code);
            if (!url.equals("")) {
                Utils.setStrURL_PRODUCTION(url);
                Utils.setStrReferer(refererUrl);
            }
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            AutoScrollPagerAdapter autoScrollPagerAdapter =
                    new AutoScrollPagerAdapter(getSupportFragmentManager());
            AutoScrollViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(autoScrollPagerAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
            viewPager.setStopScrollWhenTouch(true);
            // start auto scroll
            viewPager.startAutoScroll();

            // set auto scroll time in mili
            viewPager.setInterval(AUTO_SCROLL_THRESHOLD_IN_MILLI);
            // enable recycling using true
            viewPager.setCycle(true);
//            viewPager.setStopScrollWhenTouch(false);

            getStarted.setOnClickListener(view -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(OnboardActivity.this, AccountTypeActivity.class));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            layoutLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if ((isFaceLock || isTouchId) && Utils.checkAuthentication(OnboardActivity.this)) {
                            if (isBiometric && ((isTouchId && Utils.isFingerPrint(OnboardActivity.this)) || (isFaceLock))) {
                                Utils.checkAuthentication(OnboardActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                            } else {
                                FaceIdDisabled_BottomSheet faceIdDisable_bottomSheet = FaceIdDisabled_BottomSheet.newInstance(isTouchId, isFaceLock);
                                faceIdDisable_bottomSheet.show(getSupportFragmentManager(), faceIdDisable_bottomSheet.getTag());
                            }
                        } else {
                            Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.checkInternet(OnboardActivity.this)) {
                            if (!strToken.equals("") && !strDeviceID.equals("")) {
                                login();
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), OnboardActivity.this, "");
                        }
                    }
                });
            } else {
                Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
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
                                Intent i = new Intent(OnboardActivity.this, PINActivity.class);
                                i.putExtra("screen", "loginExpiry");
                                i.putExtra("TYPE", "ENTER");
                                startActivity(i);
                            } else {
                                Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                Intent i = new Intent(OnboardActivity.this, DashboardActivity.class);
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
                                Utils.displayAlert(loginResponse.getError().getErrorDescription(), OnboardActivity.this, "");
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
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFaceLock = false;
                    objMyApplication.setLocalBiometric(false);
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
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouchId = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void login() {
        try {
            dialog = new ProgressDialog(OnboardActivity.this, R.style.MyAlertDialogStyle);
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
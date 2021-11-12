package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class EnableAuthID extends AppCompatActivity {

    MaterialCardView enableFaceCV, enableTouchCV, successGetStartedCV;
    TextView notNowFaceTV, notNowTouchTV, notNowSuccessTV;
    RelativeLayout faceIDRL, touchIDRL, successRL;
    String enableType;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    SQLiteDatabase mydatabase;
    ImageView succesCloseIV;
    CoyniViewModel coyniViewModel;
    ProgressDialog dialog;
    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_enable_auth_id);

            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            enableFaceCV = findViewById(R.id.enableFaceCV);
            notNowFaceTV = findViewById(R.id.notNowFaceTV);
            successGetStartedCV = findViewById(R.id.successGetStartedCV);

            enableTouchCV = findViewById(R.id.enableTouchCV);
            notNowTouchTV = findViewById(R.id.notNowTouchTV);
            notNowSuccessTV = findViewById(R.id.notNowSuccessTV);

            faceIDRL = findViewById(R.id.faceIDRL);
            touchIDRL = findViewById(R.id.touchIDRL);

            successRL = findViewById(R.id.successRL);
            succesCloseIV = findViewById(R.id.succesCloseIV);

            enableType = getIntent().getStringExtra("ENABLE_TYPE");

            switch (enableType) {
                case "TOUCH":
                    faceIDRL.setVisibility(View.GONE);
                    touchIDRL.setVisibility(View.VISIBLE);
                    successRL.setVisibility(View.GONE);
                    break;
                case "FACE":
                    faceIDRL.setVisibility(View.VISIBLE);
                    touchIDRL.setVisibility(View.GONE);
                    successRL.setVisibility(View.GONE);
                    break;
                case "SUCCESS":
                    faceIDRL.setVisibility(View.GONE);
                    touchIDRL.setVisibility(View.GONE);
                    successRL.setVisibility(View.VISIBLE);
                    break;
            }

            initObserver();
            enableFaceCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                BiometricRequest biometricRequest = new BiometricRequest();
                biometricRequest.setBiometricEnabled(true);
                biometricRequest.setDeviceId(Utils.getDeviceID());
                coyniViewModel.saveBiometric(biometricRequest);
            });

            notNowFaceTV.setOnClickListener(view -> {
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.VISIBLE);
            });

            enableTouchCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.isHardwareDetected()) {
                        Log.e("Not support", "Not support");
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG);
                        startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                    } else {
                        dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                }
            });

            notNowTouchTV.setOnClickListener(view -> {
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.VISIBLE);
            });

            succesCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (enableType.equals("TOUCH")) {
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.VISIBLE);
                        successRL.setVisibility(View.GONE);
                    } else {
                        faceIDRL.setVisibility(View.VISIBLE);
                        touchIDRL.setVisibility(View.GONE);
                        successRL.setVisibility(View.GONE);
                    }
                }
            });

            notNowSuccessTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(EnableAuthID.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

            successGetStartedCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(EnableAuthID.this, IdentityVerificationActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initObserver() {
        coyniViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricResponse>() {
            @Override
            public void onChanged(BiometricResponse biometricResponse) {
                dialog.dismiss();
                if (biometricResponse != null) {
                    Log.e("bio resp", new Gson().toJson(biometricResponse));
                    saveToken(biometricResponse.getData().getToken());
                    if (enableType.equals("FACE")) {
                        saveFace("true");
                        saveThumb("false");
                        Utils.showCustomToast(EnableAuthID.this, "Face ID has been turned on", R.drawable.ic_faceid, "authid");
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.GONE);
                        successRL.setVisibility(View.VISIBLE);
                    } else if (enableType.equals("TOUCH")) {
                        saveFace("false");
                        saveThumb("true");
                        Utils.showCustomToast(EnableAuthID.this, "Touch ID has been turned on", R.drawable.ic_touch_id, "authid");
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.GONE);
                        successRL.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE && resultCode == RESULT_OK) {

                dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                BiometricRequest biometricRequest = new BiometricRequest();
                biometricRequest.setBiometricEnabled(true);
                biometricRequest.setDeviceId(Utils.getDeviceID());
                coyniViewModel.saveBiometric(biometricRequest);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveThumb(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblThumbPinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFace(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveToken(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
            mydatabase.execSQL("INSERT INTO tblPermanentToken(id,perToken) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(EnableAuthID.this, OnboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class EnableAuthID extends AppCompatActivity {
    CardView enableFaceCV, enableTouchCV, successGetStartedCV, businessGetStartedCV;
    TextView notNowSuccessTV, dontRemindTouchTV, dontRemindFace, tvEnableFace, tvDisableTouch;
    RelativeLayout faceIDRL, touchIDRL, successRL, businessSuccessRL;
    String enableType, strScreen = "";
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    SQLiteDatabase mydatabase;
    ImageView succesCloseIV;
    CoyniViewModel coyniViewModel;
    ProgressDialog dialog;
    Long mLastClickTime = 0L;
    LinearLayout layoutNotnow, layoutNotnowFace;
    MyApplication objMyApplication;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_enable_auth_id);

            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            enableFaceCV = findViewById(R.id.enableFaceCV);
            dontRemindFace = findViewById(R.id.dontRemindFace);
            successGetStartedCV = findViewById(R.id.successGetStartedCV);

            enableTouchCV = findViewById(R.id.enableTouchCV);
            layoutNotnow = findViewById(R.id.layoutNotnow);
            layoutNotnowFace = findViewById(R.id.layoutNotnowFace);
            notNowSuccessTV = findViewById(R.id.notNowSuccessTV);
            dontRemindTouchTV = findViewById(R.id.dontRemindTouchTV);
            tvEnableFace = findViewById(R.id.tvEnableFace);
            tvDisableTouch = findViewById(R.id.tvDisableTouch);

            faceIDRL = findViewById(R.id.faceIDRL);
            touchIDRL = findViewById(R.id.touchIDRL);

            successRL = findViewById(R.id.successRL);
            succesCloseIV = findViewById(R.id.succesCloseIV);

            businessSuccessRL = findViewById(R.id.businessSuccessRL);
            businessGetStartedCV = findViewById(R.id.businessGetStartedCV);

            enableType = getIntent().getStringExtra("ENABLE_TYPE");
            objMyApplication = (MyApplication) getApplicationContext();
            if (getIntent().getStringExtra("screen") != null) {
                strScreen = getIntent().getStringExtra("screen");
            }
            switch (enableType) {
                case "TOUCH":
                    if (strScreen.equals("login")) {
                        tvDisableTouch.setText("Enable Touch ID");
                        dontRemindTouchTV.setVisibility(View.VISIBLE);
                        layoutNotnow.setBackgroundResource(R.drawable.shape_green_round_rect_core);
                    } else {
                        tvDisableTouch.setText("Enable");
                        dontRemindTouchTV.setVisibility(View.GONE);
                        layoutNotnow.setBackgroundResource(0);
                    }
                    faceIDRL.setVisibility(View.GONE);
                    touchIDRL.setVisibility(View.VISIBLE);
                    successRL.setVisibility(View.GONE);
                    businessSuccessRL.setVisibility(View.GONE);
                    break;
                case "FACE":
                    if (strScreen.equals("login")) {
                        tvEnableFace.setText("Enable Face ID");
                        dontRemindFace.setVisibility(View.VISIBLE);
                        layoutNotnowFace.setBackgroundResource(R.drawable.shape_green_round_rect_core);
                    } else {
                        tvEnableFace.setText("Enable");
                        dontRemindFace.setVisibility(View.GONE);
                        layoutNotnowFace.setBackgroundResource(0);
                    }
                    faceIDRL.setVisibility(View.VISIBLE);
                    touchIDRL.setVisibility(View.GONE);
                    successRL.setVisibility(View.GONE);
                    businessSuccessRL.setVisibility(View.GONE);
                    break;
                case "SUCCESS":
                    faceIDRL.setVisibility(View.GONE);
                    touchIDRL.setVisibility(View.GONE);
                    enableType = "SUCCESS";
                    showSuccessLayout();
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

            dontRemindFace.setOnClickListener(view -> {
                try {
                    if (strScreen.equals("login")) {
                        saveDontRemind("true");
                        launchDashboard();
                    } else {
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.GONE);
                        enableType = "SUCCESS";
                        showSuccessLayout();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            dontRemindTouchTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (strScreen.equals("login")) {
                            saveDontRemind("true");
                            launchDashboard();
                        } else {
                            faceIDRL.setVisibility(View.GONE);
                            touchIDRL.setVisibility(View.GONE);
                            enableType = "SUCCESS";
                            showSuccessLayout();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            enableTouchCV.setOnClickListener(view -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            layoutNotnow.setOnClickListener(view -> {
                try {
                    if (strScreen.equals("login")) {
                        launchDashboard();
                    } else {
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.GONE);
                        enableType = "SUCCESS";
                        showSuccessLayout();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            layoutNotnowFace.setOnClickListener(view -> {
                try {
                    if (strScreen.equals("login")) {
                        launchDashboard();
                    } else {
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.GONE);
                        enableType = "SUCCESS";
                        showSuccessLayout();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            succesCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (enableType.equals("TOUCH")) {
                        faceIDRL.setVisibility(View.GONE);
                        touchIDRL.setVisibility(View.VISIBLE);
                        successRL.setVisibility(View.GONE);
                        businessSuccessRL.setVisibility(View.GONE);
                    } else {
                        faceIDRL.setVisibility(View.VISIBLE);
                        touchIDRL.setVisibility(View.GONE);
                        successRL.setVisibility(View.GONE);
                        businessSuccessRL.setVisibility(View.GONE);
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
                    launchDashboard();
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

            businessGetStartedCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(EnableAuthID.this, BusinessRegistrationTrackerActivity.class));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initObserver() {
        try {

            coyniViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricResponse>() {
                @Override
                public void onChanged(BiometricResponse biometricResponse) {
                    dialog.dismiss();
                    if (biometricResponse != null) {
                        Log.e("bio resp", new Gson().toJson(biometricResponse));
                        saveToken(biometricResponse.getData().getToken());
                        Utils.generateUUID(EnableAuthID.this);
                        if (enableType.equals("FACE")) {
                            saveFace("true");
                            saveThumb("false");
                            Utils.showCustomToast(EnableAuthID.this, "Face ID has been turned on", R.drawable.ic_faceid, "authid");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        faceIDRL.setVisibility(View.GONE);
                                        touchIDRL.setVisibility(View.GONE);
                                        if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("login")) {
                                            launchDashboard();
                                        } else {
                                            //                                        successRL.setVisibility(View.VISIBLE);
                                            enableType = "SUCCESS";
                                            showSuccessLayout();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, 2000);
                        } else if (enableType.equals("TOUCH")) {
                            saveFace("false");
                            saveThumb("true");
                            Utils.showCustomToast(EnableAuthID.this, "Touch ID has been turned on", R.drawable.ic_touch_id, "authid");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        faceIDRL.setVisibility(View.GONE);
                                        touchIDRL.setVisibility(View.GONE);
                                        if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("login")) {
                                            launchDashboard();
                                        } else {
                                            //                                        successRL.setVisibility(View.VISIBLE);
                                            enableType = "SUCCESS";
                                            showSuccessLayout();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, 2000);
                        }
                        objMyApplication.setBiometric(true);
                    }
                }
            });

            businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
                @Override
                public void onChanged(BusinessTrackerResponse btResp) {

                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTrackerResponse(btResp);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            mydatabase.execSQL("Delete from tblThumbPinLock");
            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFace(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("Delete from tblFacePinLock");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveToken(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
            mydatabase.execSQL("Delete from tblPermanentToken");
            mydatabase.execSQL("INSERT INTO tblPermanentToken(id,perToken) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveDontRemind(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblDontRemind(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isDontRemind TEXT);");
            mydatabase.execSQL("Delete from tblDontRemind");
            mydatabase.execSQL("INSERT INTO tblDontRemind(id,isDontRemind) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (enableType.equals("SUCCESS") && objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {

        } else {
            try {
                Intent intent = new Intent(EnableAuthID.this, OnboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void toastTimer(Dialog dialog) {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3500);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dialog.dismiss();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            ;
        }.start();
    }

    private void showSuccessLayout() {
        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            successRL.setVisibility(View.VISIBLE);
            businessSuccessRL.setVisibility(View.GONE);
        } else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
            businessIdentityVerificationViewModel.getBusinessTracker();
            successRL.setVisibility(View.GONE);
            businessSuccessRL.setVisibility(View.VISIBLE);
        }
    }

    private void launchDashboard() {
        Intent dashboardIntent = new Intent(EnableAuthID.this, DashboardActivity.class);
        if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
            BusinessTrackerResponse btr = objMyApplication.getBusinessTrackerResponse();
            if (btr != null && btr.getData().isCompanyInfo() && btr.getData().isDbaInfo() && btr.getData().isBeneficialOwners()
                    && btr.getData().isIsbankAccount() && btr.getData().isAgreementSigned()) {
                dashboardIntent = new Intent(EnableAuthID.this, BusinessDashboardActivity.class);
            } else {
                dashboardIntent = new Intent(EnableAuthID.this, BusinessRegistrationTrackerActivity.class);
            }
        }
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(dashboardIntent);
    }
}
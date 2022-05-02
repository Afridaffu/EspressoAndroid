package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
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
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class EnableAuthID extends AppCompatActivity {
    CardView enableFaceCV, enableTouchCV, successGetStartedCV, businessGetStartedCV;
    TextView notNowSuccessTV, dontRemindTouchTV, dontRemindFace, tvEnableFace, tvDisableTouch;
    RelativeLayout faceIDRL, touchIDRL, successRL, businessSuccessRL;
    String enableType, enableTypeCopy, strScreen = "";
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    SQLiteDatabase mydatabase;
    DatabaseHandler dbHandler;
    ImageView succesCloseIV;
    CoyniViewModel coyniViewModel;
    ProgressDialog dialog;
    Long mLastClickTime = 0L;
    LinearLayout layoutNotnow, layoutNotnowFace;
    MyApplication objMyApplication;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 512;

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
            dbHandler = DatabaseHandler.getInstance(EnableAuthID.this);
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

            enableType = enableTypeCopy = getIntent().getStringExtra("ENABLE_TYPE");
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

            isBiometric = Utils.getIsBiometric();
            setFaceLock();
            setTouchId();

            initObserver();
            enableFaceCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(EnableAuthID.this)) {
                    if (isBiometric && ((isTouchId && Utils.isFingerPrint(EnableAuthID.this)) || (isFaceLock))) {
                        Utils.checkAuthentication(EnableAuthID.this, CODE_AUTHENTICATION_VERIFICATION);
                    } else {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                } else {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }

//                dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
//                dialog.setIndeterminate(false);
//                dialog.setMessage("Please wait...");
//                dialog.show();
//                BiometricRequest biometricRequest = new BiometricRequest();
//                biometricRequest.setBiometricEnabled(true);
//                biometricRequest.setDeviceId(Utils.getDeviceID());
//                coyniViewModel.saveBiometric(biometricRequest);
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

                    if (Utils.checkAuthentication(EnableAuthID.this)) {
                        if (isBiometric && ((Utils.isFingerPrint(EnableAuthID.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(EnableAuthID.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                            if (!fingerprintManager.isHardwareDetected()) {
                                Log.e("Not support", "Not support");
                            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                                final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG);
                                startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                            }
                        }
                    } else {
                        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                        if (!fingerprintManager.isHardwareDetected()) {
                            Log.e("Not support", "Not support");
                        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                            final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                            enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG);
                            startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                        }
                    }


//                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
//                    if (!fingerprintManager.isHardwareDetected()) {
//                        Log.e("Not support", "Not support");
//                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
//                        final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
//                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                                BIOMETRIC_STRONG);
//                        startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
//                    } else {
//
//                        dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
//                        dialog.setIndeterminate(false);
//                        dialog.setMessage("Please wait...");
//                        dialog.show();
//                        BiometricRequest biometricRequest = new BiometricRequest();
//                        biometricRequest.setBiometricEnabled(true);
//                        biometricRequest.setDeviceId(Utils.getDeviceID());
//                        try {
//                            coyniViewModel.saveBiometric(biometricRequest);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
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
                    if (enableTypeCopy.equals("TOUCH")) {
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
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        startActivity(new Intent(EnableAuthID.this, BusinessRegistrationTrackerActivity.class).putExtra("FROM", "signup"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
                try {
                    dialog.dismiss();
                    if (biometricResponse != null) {
                        Log.e("bio resp", new Gson().toJson(biometricResponse));
                        saveToken(biometricResponse.getData().getToken());
                        //                        Utils.generateUUID(EnableAuthID.this);
                        if (!objMyApplication.isDeviceID()) {
                            Utils.generateUUID(EnableAuthID.this);
                        }
                        if (enableTypeCopy.equals("FACE")) {
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
                        } else if (enableTypeCopy.equals("TOUCH")) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
            @Override
            public void onChanged(BusinessTrackerResponse btResp) {

                try {
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTrackerResponse(btResp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE) {
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            } else if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                if (resultCode == RESULT_OK) {
                    dialog = new ProgressDialog(EnableAuthID.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    try {
                        coyniViewModel.saveBiometric(biometricRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void saveThumb(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblThumbPinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
//            mydatabase.execSQL("Delete from tblThumbPinLock");
//            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void saveFace(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
//            mydatabase.execSQL("Delete from tblFacePinLock");
//            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void saveToken(String value) {
//        try {
//            objMyApplication.setStrMobileToken(value);
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
//            mydatabase.execSQL("Delete from tblPermanentToken");
//            mydatabase.execSQL("INSERT INTO tblPermanentToken(id,perToken) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void saveDontRemind(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblDontRemind(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isDontRemind TEXT);");
//            mydatabase.execSQL("Delete from tblDontRemind");
//            mydatabase.execSQL("INSERT INTO tblDontRemind(id,isDontRemind) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void saveThumb(String value) {
        try {
            dbHandler.clearThumbPinLockTable();
            dbHandler.insertThumbPinLock(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFace(String value) {
        try {
            dbHandler.clearFacePinLockTable();
            dbHandler.insertFacePinLock(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveToken(String value) {
        try {
            objMyApplication.setStrMobileToken(value);
            dbHandler.clearPermanentTokenTable();
            dbHandler.insertPermanentToken(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveDontRemind(String value) {
        try {
            dbHandler.clearTableDontRemind();
            dbHandler.insertTableDontRemind(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        if (enableType.equals("SUCCESS") && objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
//
//        } else {
//            try {
//                Intent intent = new Intent(EnableAuthID.this, OnboardActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }

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
        if (objMyApplication.checkForDeclinedStatus()) {
            objMyApplication.launchDeclinedActivity(this);
        } else {
            objMyApplication.launchDashboard(this, strScreen);
        }
    }

    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTouchId() {
        try {
            isTouchId = false;
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                isTouchId = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
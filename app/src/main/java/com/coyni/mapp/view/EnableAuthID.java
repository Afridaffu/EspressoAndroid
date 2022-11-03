package com.coyni.mapp.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.dialogs.OnAgreementsAPIListener;
import com.coyni.mapp.model.FilteredAgreements;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.view.business.BusinessCreateAccountsActivity;
import com.coyni.mapp.view.business.BusinessDashboardActivity;
import com.coyni.mapp.view.business.SignAgreementsActivity;
import com.google.gson.Gson;
import com.coyni.mapp.R;
import com.coyni.mapp.model.biometric.BiometricRequest;
import com.coyni.mapp.model.biometric.BiometricResponse;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.business.BusinessRegistrationTrackerActivity;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.CoyniViewModel;

public class EnableAuthID extends BaseActivity {
    private CardView enableFaceCV, enableTouchCV, successGetStartedCV, businessGetStartedCV;
    private TextView notNowSuccessTV, dontRemindTouchTV, dontRemindFace, tvEnableFace, tvDisableTouch;
    private RelativeLayout faceIDRL, touchIDRL, successRL, businessSuccessRL;
    private String enableType, enableTypeCopy, strScreen = "", strFCMToken = "";
    private int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    private DatabaseHandler dbHandler;
    private ImageView succesCloseIV;
    private CoyniViewModel coyniViewModel;
    private Long mLastClickTime = 0L;
    private LinearLayout layoutNotnow, layoutNotnowFace;
    private MyApplication objMyApplication;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
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
                    if (strScreen.equals("login") || strScreen.equalsIgnoreCase("ForgotPIN")) {
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
                    if (strScreen.equals("login") || strScreen.equalsIgnoreCase("ForgotPIN")) {
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
            objMyApplication.initializeDBHandler(EnableAuthID.this);
            isFaceLock = objMyApplication.setFaceLock();
            isTouchId = objMyApplication.setTouchId();
            if (isFaceLock || isTouchId) {
                objMyApplication.setLocalBiometric(true);
            } else {
                objMyApplication.setLocalBiometric(false);
            }
            //firebaseToken();
            initObserver();
            enableFaceCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (isBiometric && (Utils.getIsFaceEnabled() || Utils.getIsTouchEnabled()) && Utils.checkAuthentication(EnableAuthID.this)) {
                    Utils.checkAuthentication(EnableAuthID.this, CODE_AUTHENTICATION_VERIFICATION);
                } else {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            });

            dontRemindFace.setOnClickListener(view -> {
                try {
                    if (strScreen.equals("login") || strScreen.equalsIgnoreCase("ForgotPIN")) {
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
                        if (strScreen.equals("login") || strScreen.equalsIgnoreCase("ForgotPIN")) {
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

                    if (isBiometric && (Utils.getIsFaceEnabled() || Utils.getIsTouchEnabled()) && Utils.checkAuthentication(EnableAuthID.this)) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            layoutNotnow.setOnClickListener(view -> {
                try {
//                    if (strScreen.equals("login")) {
                    if (strScreen.equals("login") || strScreen.equals("login_SET_PIN") || strScreen.equalsIgnoreCase("ForgotPIN")) {
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
//                    if (strScreen.equals("login")) {
                    if (strScreen.equals("login") || strScreen.equals("login_SET_PIN") || strScreen.equalsIgnoreCase("ForgotPIN")) {
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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    launchDashboard();
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
                        startActivity(new Intent(EnableAuthID.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra("FROM", "signup")
                                .putExtra(Utils.IS_TRACKER, true));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            setOnAgreementsAPIListener(new OnAgreementsAPIListener() {
                @Override
                public void onAgreementsAPIResponse(SignAgreementsResp signAgreementsResp) {
                    dismissDialog();
                    FilteredAgreements filteredAgreements = Utils.getFilteredAgreements(signAgreementsResp.getData());
                    if (filteredAgreements.getAgreements().size() > 0) {
                        Utils.launchAgreements(EnableAuthID.this, filteredAgreements.isMerchantAgreement());
                        finish();
                    } else {
                        launchDasboardFromBase();
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
                    dismissDialog();
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
//                                        if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("login")) {
                                        if (getIntent().getStringExtra("screen") != null && (getIntent().getStringExtra("screen").equals("login")
                                                || getIntent().getStringExtra("screen").equals("login_SET_PIN")
                                                || getIntent().getStringExtra("screen").equalsIgnoreCase("ForgotPIN"))) {
                                            launchDashboard();
                                        } else {
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
                                        if (getIntent().getStringExtra("screen") != null && (getIntent().getStringExtra("screen").equals("login")
                                                || getIntent().getStringExtra("screen").equals("login_SET_PIN")
                                                || getIntent().getStringExtra("screen").equalsIgnoreCase("ForgotPIN"))) {
                                            launchDashboard();
                                        } else {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE) {
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    showProgressDialog();
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            } else if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                if (resultCode == RESULT_OK) {
                    dismissDialog();
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
        try {
            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                successRL.setVisibility(View.VISIBLE);
                businessSuccessRL.setVisibility(View.GONE);
            } else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                successRL.setVisibility(View.GONE);
                businessSuccessRL.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void launchDashboard() {
        if (objMyApplication.isAgreementSigned()) {
            if (objMyApplication.checkForDeclinedStatus()) {
                objMyApplication.setIsLoggedIn(true);
                objMyApplication.launchDeclinedActivity(this);
            } else {
                objMyApplication.setIsLoggedIn(true);
                objMyApplication.launchDashboard(this, strScreen);
            }
        } else {
            if (objMyApplication.getInitializeResponse().getData().getBusinessTracker() == null || objMyApplication.getInitializeResponse().getData().getBusinessTracker().isIsAgreementSigned())
                Utils.launchAgreements(EnableAuthID.this, false);
            else if (!objMyApplication.getInitializeResponse().getData().getBusinessTracker().isIsAgreementSigned()) {
                showProgressDialog();
                callHasToSignAPI();
            }
        }
    }

//    private void firebaseToken() {
//        try {
//            FirebaseMessaging.getInstance().getToken()
//                    .addOnCompleteListener(new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(@NonNull Task<String> task) {
//                            if (!task.isSuccessful()) {
//                                Log.w("", "Fetching FCM registration token failed", task.getException());
//                                return;
//                            }
//
//                            // Get new FCM registration token
//                            strFCMToken = task.getResult();
//                            Log.d("Token", "Token - " + strFCMToken);
//                        }
//                    });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

}
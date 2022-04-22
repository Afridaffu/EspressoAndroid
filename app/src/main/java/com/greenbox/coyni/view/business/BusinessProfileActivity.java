package com.greenbox.coyni.view.business;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.logout.LogoutResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AccountLimitsActivity;
import com.greenbox.coyni.view.AgreementsActivity;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessReceivePaymentActivity;
import com.greenbox.coyni.view.ConfirmPasswordActivity;
import com.greenbox.coyni.view.CustomerProfileActivity;
import com.greenbox.coyni.view.OnboardActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.view.PreferencesActivity;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import org.jetbrains.annotations.Nullable;

public class BusinessProfileActivity extends BaseActivity {

    private LinearLayout feesLL, teamLL, bpbackBtn, switchOffLL, switchOnLL,
            paymentMethodsLL, cpagreeementsLL, companyinfoLL, dbainfoLL, accountlimitsLL,
            businessResetPin, preferencesLL, beneficialOwnersLL;
    private ConstraintLayout userProfileCL;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    static String strToken = "";
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private final int CODE_AUTHENTICATION_VERIFICATION = 251, CODE_AUTHENTICATION = 512;
    private final int CODE_AUTHENTICATION_VERIFICATION_RESET_PIN = 252;
    boolean isTogleBtn = false;
    private CardView business_userProfileCV, statusDot, cardViewSetting, cvLogout;
    private DashboardViewModel dashboardViewModel;
    private MyApplication myApplication;
    private Boolean isSwitchEnabled = false;
    private CoyniViewModel coyniViewModel;
    private ImageView profileImage;
    private TextView profileText, account_status, account_id, userFullname, b_tvBMSetting;
    private Dialog enablePopup;
    private DatabaseHandler dbHandler;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    boolean isLoggedOut = false;
    //    private LinearLayout feesLL, teamLL, bpbackBtn, switchOffLL, switchOnLL, paymentMethodsLL;
    private Long mLastClickTime = 0L;
    private TextView tvVersion;
    private ScrollView profileSV;
    private String fullname = "";
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_profile);

        try {
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFields() {
        try {
            accountlimitsLL = findViewById(R.id.cpAccountLimitsLL);
            feesLL = findViewById(R.id.feesLL);
            teamLL = findViewById(R.id.teamLL);
            paymentMethodsLL = findViewById(R.id.paymentMethodsLL);
            bpbackBtn = findViewById(R.id.b_backBtn);
            cvLogout = findViewById(R.id.cvLogout);
            switchOnLL = findViewById(R.id.switchOn);
            businessResetPin = findViewById(R.id.businessResetPin);
            dbainfoLL = findViewById(R.id.DBAInformationLL);
            profileSV = findViewById(R.id.profileSV);
            cardViewSetting = findViewById(R.id.cardviewSetting);
            userProfileCL = findViewById(R.id.linearLayout);

            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            companyinfoLL = findViewById(R.id.companyInformationLL);
            cpagreeementsLL = findViewById(R.id.cpAgreementsLL);
            preferencesLL = findViewById(R.id.PreferencesLL);
            beneficialOwnersLL = findViewById(R.id.beneficialOwnersLL);
            switchOffLL = findViewById(R.id.switchOff);
            profileImage = findViewById(R.id.b_profileIV);
            profileText = findViewById(R.id.b_imageTextTV);
            account_status = findViewById(R.id.b_tvACStatus);
            statusDot = findViewById(R.id.b_statusDotCV);
            account_id = findViewById(R.id.b_accountIDTV);
            userFullname = findViewById(R.id.b_nameTV);
            b_tvBMSetting = findViewById(R.id.b_tvBMSetting);
            tvVersion = findViewById(R.id.tvVersion);
            dbHandler = DatabaseHandler.getInstance(BusinessProfileActivity.this);
            myApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            business_userProfileCV = findViewById(R.id.business_userProfileCV);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            isBiometric = Utils.getIsBiometric();
            setToken();
            setFaceLock();
            setTouchId();
            enableDisableMerchantSettings();

            preferencesLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, PreferencesActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            beneficialOwnersLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, MerchantSettingsBeneficialOwnersActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            businessResetPin.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BusinessProfileActivity.this)) {
                    if (isBiometric && ((isTouchId && Utils.isFingerPrint(BusinessProfileActivity.this)) || (isFaceLock))) {
                        Utils.checkAuthentication(BusinessProfileActivity.this, CODE_AUTHENTICATION_VERIFICATION_RESET_PIN);
                    } else {
                        startActivity(new Intent(BusinessProfileActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "ResetPIN"));
                    }
                } else {
                    startActivity(new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ResetPIN"));
                }

            });

            cpagreeementsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, AgreementsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            companyinfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, CompanyInfoDetails.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dbainfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, DBAInfoDetails.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            switchOffLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if (Utils.checkAuthentication(BusinessProfileActivity.this)) {
                        if (isBiometric && ((Utils.isFingerPrint(BusinessProfileActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(BusinessProfileActivity.this, CODE_AUTHENTICATION);
                        } else {
                            if (b_tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                                enablePopup = showFaceTouchEnabledDialog(BusinessProfileActivity.this, "TOUCH");
                            } else {
                                enablePopup = showFaceTouchEnabledDialog(BusinessProfileActivity.this, "FACE");
                            }
                        }
                    } else {
                        if (b_tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                            enablePopup = showFaceTouchEnabledDialog(BusinessProfileActivity.this, "TOUCH");
                        } else {
                            enablePopup = showFaceTouchEnabledDialog(BusinessProfileActivity.this, "FACE");
                        }
                    }

//                    isSwitchEnable();
                }
            });

            switchOnLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    isSwitchEnable();
                }
            });

            if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                b_tvBMSetting.setText(getString(R.string.security_touchid));
            } else {
                b_tvBMSetting.setText(getString(R.string.security_faceid));
            }

            if (getLocalBiometricEnabled()) {
                isSwitchEnabled = true;
                switchOffLL.setVisibility(View.GONE);
                switchOnLL.setVisibility(View.VISIBLE);
            } else {
                isSwitchEnabled = false;
                switchOffLL.setVisibility(View.VISIBLE);
                switchOnLL.setVisibility(View.GONE);
            }

            bpbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            teamLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, TeamActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            feesLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, FeesActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            accountlimitsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, AccountLimitsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            paymentMethodsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this, BusinessPaymentMethodsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            userFullname.setOnClickListener(view -> {
                if (userFullname.getText().toString().contains("...")) {
                    if (fullname.length() == 21 || fullname.length() > 21) {
                        userFullname.setText(fullname.substring(0, 20));
                    } else {
                        userFullname.setText(fullname);
                    }
                } else {
                    if (fullname.length() == 21) {
                        userFullname.setText(fullname.substring(0, 20) + "...");
                    } else if (fullname.length() > 22) {
                        userFullname.setText(fullname.substring(0, 22) + "...");
                    } else {
                        userFullname.setText(fullname);
                    }
                }
            });


            findViewById(R.id.business_UserDetailsLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        startActivity(new Intent(BusinessProfileActivity.this, UserDetailsActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.b_cpChangePassword).setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
//                Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
//                        .putExtra("TYPE", "ENTER")
//                        .putExtra("screen", "ChangePassword");
//                startActivity(i);

                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BusinessProfileActivity.this)) {
                    if (isBiometric && ((isTouchId && Utils.isFingerPrint(BusinessProfileActivity.this)) || (isFaceLock))) {
                        Utils.checkAuthentication(BusinessProfileActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                    } else {
                        Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "ChangePassword");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ChangePassword");
                    startActivity(i);
                }
            });

            business_userProfileCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        startActivity(new Intent(BusinessProfileActivity.this, BusinessReceivePaymentActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            cvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        showProgressDialog();
                        loginViewModel.logout();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            tvVersion.setText("Version " + Utils.getAppVersion().replace("Android : ", ""));
            tvVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String strEndPoint = "";
                        strEndPoint = "End Point Url - " + Utils.getStrURL_PRODUCTION();
                        Utils.displayAlert(strEndPoint, BusinessProfileActivity.this, "API Details", "");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


            if (myApplication.getMyProfile().getData().getAccountStatus() != null) {
                try {
                    String accountStatus = myApplication.getMyProfile().getData().getAccountStatus();
                    if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                        account_status.setTextColor(getResources().getColor(R.color.active_green));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.active_green));
                    } else if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
                        account_status.setTextColor(getResources().getColor(R.color.orange));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.orange));
                    } else if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                        account_status.setTextColor(getResources().getColor(R.color.under_review_blue));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.under_review_blue));
                    } else if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                            || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
                        account_status.setTextColor(getResources().getColor(R.color.error_red));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.error_red));
                    }
                    //                    if (myApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                    //                        cardviewYourAccount.setVisibility(View.VISIBLE);
                    //                    } else {
                    //                        cardviewYourAccount.setVisibility(View.GONE);
                    //                    }
                    account_status.setText(accountStatus);
                    account_id.setText("Account ID M-" + myApplication.getMyProfile().getData().getId());
                    fullname = Utils.capitalize(myApplication.getMyProfile().getData().getFirstName() + " " + myApplication.getMyProfile().getData().getLastName());
                    userFullname.setText(fullname);

                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                account_status.setText("");
            }
            bindImage(myApplication.getMyProfile().getData().getImage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableDisableMerchantSettings() {
        boolean isEnable = false;
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getAccountStatus() != null) {
            String accountStatus = myApplication.getMyProfile().getData().getAccountStatus();
            if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
                isEnable = false;
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                isEnable = true;
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                    || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
                isEnable = false;
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                isEnable = true;
            }
        }
        disableLayout(companyinfoLL, isEnable);
        disableLayout(dbainfoLL, isEnable);
        disableLayout(beneficialOwnersLL, isEnable);
        disableLayout(teamLL, isEnable);
        disableLayout(paymentMethodsLL, isEnable);
        disableLayout(preferencesLL, isEnable);
        disableLayout(feesLL, isEnable);
        disableLayout(accountlimitsLL, isEnable);
        disableLayout(cpagreeementsLL, isEnable);
    }

    public void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                myApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                myApplication.setLocalBiometric(false);
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
                myApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                myApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObservers() {
        coyniViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricResponse>() {
            @Override
            public void onChanged(BiometricResponse biometricResponse) {
                try {
                    if (enablePopup != null) {
                        enablePopup.dismiss();
                    }
                    if (biometricResponse != null) {
                        saveToken(biometricResponse.getData().getToken());
//                            Utils.generateUUID(BusinessProfileActivity.this);
                        if (!myApplication.isDeviceID()) {
                            Utils.generateUUID(BusinessProfileActivity.this);
                        }
                        if (!isSwitchEnabled) {
                            if (b_tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                                if (!isLoggedOut) {
                                    saveFace("false");
                                    saveThumb("true");
                                    Utils.showCustomToast(BusinessProfileActivity.this, "Touch ID has been turned on", R.drawable.ic_touch_id, "authid");
                                }
                            } else {
                                //                                saveFace("true");
                                //                                saveThumb("false");
                                if (!isLoggedOut) {
                                    saveFace("true");
                                    saveThumb("false");
                                    Utils.showCustomToast(BusinessProfileActivity.this, "Face ID has been turned on", R.drawable.ic_faceid, "authid");
                                }
                            }

                            isSwitchEnabled = true;
                            switchOnLL.setVisibility(View.VISIBLE);
                            switchOffLL.setVisibility(View.GONE);
                            myApplication.setBiometric(true);
                        } else {
                            if (b_tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                                if (!isLoggedOut)
                                    Utils.showCustomToast(BusinessProfileActivity.this, "Touch ID has been turned off", R.drawable.ic_touch_id, "authid");
                            } else {
                                if (!isLoggedOut)
                                    Utils.showCustomToast(BusinessProfileActivity.this, "Face ID has been turned off", R.drawable.ic_faceid, "authid");
                            }
                            myApplication.setBiometric(false);
                            if (!isLoggedOut) {
                                saveFace("false");
                                saveThumb("false");
                            }
                            isSwitchEnabled = false;
                            switchOnLL.setVisibility(View.GONE);
                            switchOffLL.setVisibility(View.VISIBLE);
                            Log.e("isFace1", isFaceEnabled() + "");
                            Log.e("isTouch1", isTouchEnabled() + "");
                        }

                        setToken();
                        setFaceLock();
                        setTouchId();

                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                try {
                    if (profile != null) {
                        myApplication.setMyProfile(profile);
                        bindImage(profile.getData().getImage());

                        if (profile.getData().getAccountStatus() != null) {
                            try {
                                if (profile.getData().getAccountStatus().equals("Active")) {
                                    account_status.setTextColor(getResources().getColor(R.color.active_green));
                                    statusDot.setCardBackgroundColor(getResources().getColor(R.color.active_green));
                                } else if (profile.getData().getAccountStatus().equals("Unverified")) {
                                    account_status.setTextColor(getResources().getColor(R.color.orange));
                                    statusDot.setCardBackgroundColor(getResources().getColor(R.color.orange));
                                } else if (profile.getData().getAccountStatus().equals("Under Review")) {
                                    account_status.setTextColor(getResources().getColor(R.color.under_review_blue));
                                    statusDot.setCardBackgroundColor(getResources().getColor(R.color.under_review_blue));
                                }
                                //                    if (myApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                                //                        cardviewYourAccount.setVisibility(View.VISIBLE);
                                //                    } else {
                                //                        cardviewYourAccount.setVisibility(View.GONE);
                                //                    }
                                account_status.setText(profile.getData().getAccountStatus());
                                account_id.setText("Account ID M-" + profile.getData().getId());
                                String fullname = Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
                                userFullname.setText(fullname);

                                if (userFullname != null && userFullname.length() > 22) {
                                    userFullname.setText(fullname.substring(0, 22) + " ");
                                } else {
                                    userFullname.setText(fullname);
                                }
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }

                        } else {
                            account_status.setText("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        businessIdentityVerificationViewModel.getGetDBAInfoResponse().observe(this, new Observer<DBAInfoResp>() {
            @Override
            public void onChanged(DBAInfoResp dbaInfoResp) {
                try {
                    myApplication.setDbaInfoResp(dbaInfoResp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
            @Override
            public void onChanged(BusinessTypeResp businessTypeResp) {
                try {
                    myApplication.setBusinessTypeResp(businessTypeResp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loginViewModel.getLogoutLiveData().observe(this, new Observer<LogoutResponse>() {
            @Override
            public void onChanged(LogoutResponse logoutResponse) {
                dismissDialog();
                if (logoutResponse != null) {
                    if (logoutResponse.getStatus().toLowerCase().equals("success")) {
                        onLogoutSuccess();
                    } else {
                        if (!logoutResponse.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(logoutResponse.getError().getErrorDescription(), BusinessProfileActivity.this, "", "");
                        } else {
                            Utils.displayAlert(logoutResponse.getError().getFieldErrors().get(0), BusinessProfileActivity.this, "", "");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            profileSV.scrollTo(0, 0);
            dashboardViewModel.meProfile();
            businessIdentityVerificationViewModel.getDBAInfo();
            businessIdentityVerificationViewModel.getBusinessType();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLogoutSuccess() {
        isLoggedOut = true;
        myApplication.setStrRetrEmail("");
        dropAllTables();
        Intent i = new Intent(BusinessProfileActivity.this, OnboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void bindImage(String imageString) {
        try {
            profileImage.setVisibility(View.GONE);
            profileText.setVisibility(View.VISIBLE);
            String imageTextNew = "";
            imageTextNew = imageTextNew + myApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    myApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            profileText.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                profileImage.setVisibility(View.VISIBLE);
//                userProfileCL.setBackground(getResources().getDrawable(R.drawable.corecircle));
                profileText.setVisibility(View.GONE);
                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(profileImage);
            } else {
                profileImage.setVisibility(View.GONE);
                profileText.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + myApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        myApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                profileText.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
//            if (enablePopup != null) {
//                enablePopup.dismiss();
//            }
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE) {
//                dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
//                dialog.setIndeterminate(false);
//                dialog.setMessage("Please wait...");
//                dialog.show();
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            } else if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                if (resultCode == RESULT_OK) {
                    Intent cp = new Intent(BusinessProfileActivity.this, ConfirmPasswordActivity.class);
                    startActivity(cp);
                } else {
                    Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ChangePassword");
                    startActivity(i);
                }
            } else if (requestCode == CODE_AUTHENTICATION_VERIFICATION_RESET_PIN) {
                if (resultCode == RESULT_OK) {
                    Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "CHOOSE")
                            .putExtra("screen", "ResetPIN");
                    startActivity(i);
                } else {
                    Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ResetPIN");
                    startActivity(i);
                }
            }else if (requestCode == CODE_AUTHENTICATION) {
                if (resultCode == RESULT_OK) {
                    isSwitchEnable();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dropAllTables() {
        try {
//            enableBiometric(false);
            dbHandler.clearAllTables();
            SharedPreferences prefs = getSharedPreferences("DeviceID", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableBiometric(Boolean value) {
        try {
            BiometricRequest biometricRequest = new BiometricRequest();
            biometricRequest.setBiometricEnabled(value);
            biometricRequest.setDeviceId(Utils.getDeviceID());
            coyniViewModel.saveBiometric(biometricRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void isSwitchEnable() {
        try {
            if (!isSwitchEnabled) {
//                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                if (b_tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        enablePopup = showFaceTouchEnabledDialog(this, "TOUCH");
                    } else {
//                        dialog = Utils.showProgressDialog(this);
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                } else {

                    if (Utils.checkBiometric(BusinessProfileActivity.this)) {
                        if (!Utils.isFingerPrint(BusinessProfileActivity.this)) {
//                            dialog = Utils.showProgressDialog(this);
                            BiometricRequest biometricRequest = new BiometricRequest();
                            biometricRequest.setBiometricEnabled(true);
                            biometricRequest.setDeviceId(Utils.getDeviceID());
                            coyniViewModel.saveBiometric(biometricRequest);
                        }
                    } else {
                        enablePopup = showFaceTouchEnabledDialog(this, "FACE");
                    }
                }
            } else {
//                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                if (b_tvBMSetting.getText().toString().toLowerCase().contains("face")) {
//                    dialog = Utils.showProgressDialog(this);
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                } else {
//                    dialog = Utils.showProgressDialog(this);
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveThumb(String value) {
        dbHandler.clearThumbPinLockTable();
        dbHandler.insertThumbPinLock(value);
    }

    private void saveFace(String value) {
        dbHandler.clearFacePinLockTable();
        dbHandler.insertFacePinLock(value);
    }

    private void saveToken(String value) {
        myApplication.setStrMobileToken(value);
        dbHandler.clearPermanentTokenTable();
        dbHandler.insertPermanentToken(value);
    }

    private void disableLayout(LinearLayout layout, boolean isEnable) {
        if (layout == null) {
            return;
        }
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(isEnable);
        }
        layout.setEnabled(isEnable);
    }

    public Dialog showFaceTouchEnabledDialog(final Context context, String type) {
        // custom dialog
        final Dialog dDialog = new Dialog(context);
        dDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dDialog.setContentView(R.layout.enable_disable_face_touch_dialog);
        dDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        CardView enableCV = dDialog.findViewById(R.id.enableCV);
        TextView tvEnable = dDialog.findViewById(R.id.tvEnable);
        TextView tvHead = dDialog.findViewById(R.id.tvHead);
        TextView tvMessage = dDialog.findViewById(R.id.tvMessage);
        LinearLayout notNowLL = dDialog.findViewById(R.id.notNowLL);

        if (type.equals("FACE")) {
            tvHead.setText(context.getString(R.string.set_up_face_id));
            tvEnable.setText(context.getString(R.string.set_up_face_id));
            tvMessage.setText(context.getString(R.string.enable_face_message));
        } else {
            tvHead.setText(context.getString(R.string.set_up_touch_id));
            tvEnable.setText(context.getString(R.string.set_up_touch_id));
            tvMessage.setText(context.getString(R.string.enable_touch_message));
        }

        notNowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dDialog.dismiss();
            }
        });

        enableCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (type.equals("TOUCH")) {
                        try {
                            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
                            if (!fingerprintManager.hasEnrolledFingerprints()) {
                                final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG);
                                startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                            } else {
                                //                        dialog = Utils.showProgressDialog(context);
                                BiometricRequest biometricRequest = new BiometricRequest();
                                biometricRequest.setBiometricEnabled(true);
                                biometricRequest.setDeviceId(Utils.getDeviceID());
                                coyniViewModel.saveBiometric(biometricRequest);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Window window = dDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dDialog.setCanceledOnTouchOutside(true);
        dDialog.show();

        return dDialog;
    }

    private boolean getLocalBiometricEnabled() {
        boolean isFace = false;
        boolean isTouch = false;
        boolean isBiometric = false;
        try {
            String faceValue = dbHandler.getFacePinLock();
            if (faceValue != null && faceValue.equals("true")) {
                isFace = true;
                myApplication.setLocalBiometric(true);
            } else {
                isFace = false;
                myApplication.setLocalBiometric(false);
            }

            String thumbValue = dbHandler.getThumbPinLock();
            if (thumbValue != null && thumbValue.equals("true")) {
                isTouch = true;
                myApplication.setLocalBiometric(true);
            } else {
                isTouch = false;
                myApplication.setLocalBiometric(false);
            }

            isBiometric = isFace || isTouch;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isBiometric;
    }

    public boolean isTouchEnabled() {
        boolean touch = false;
        try {
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                touch = true;
            } else {
                touch = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return touch;
    }

    public boolean isFaceEnabled() {
        boolean face = false;
        String value = dbHandler.getFacePinLock();
        if (value != null && value.equals("true")) {
            face = true;
        } else {
            face = false;
        }
        return face;
    }
}
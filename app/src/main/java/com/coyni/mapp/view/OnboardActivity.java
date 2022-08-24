package com.coyni.mapp.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.BuildConfig;
import com.coyni.mapp.R;
import com.coyni.mapp.adapters.AutoScrollPagerAdapter;
import com.coyni.mapp.fragments.FaceIdDisabled_BottomSheet;
import com.coyni.mapp.intro_slider.AutoScrollViewPager;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.business_id_verification.BusinessTrackerResponse;
import com.coyni.mapp.model.check_out_transactions.CheckOutModel;
import com.coyni.mapp.model.login.BiometricLoginRequest;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.business.BusinessDashboardActivity;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OnboardActivity extends BaseActivity {
    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 3000;
    private LinearLayout getStarted, layoutLogin;
    private Long mLastClickTime = 0L;
    private DatabaseHandler dbHandler;
    private String strToken = "", strDeviceID = "", strFirstUser = "";
    private Boolean isFaceLock = false, isTouchId = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    private LoginViewModel loginViewModel;
    public static OnboardActivity onboardActivity;
    private Boolean isBiometric = false;
    private RelativeLayout layoutOnBoarding, layoutAuth;
    private MyApplication objMyApplication;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_onboard);
            onboardActivity = this;
            dbHandler = DatabaseHandler.getInstance(OnboardActivity.this);
            layoutOnBoarding = findViewById(R.id.layoutOnBoarding);
            layoutAuth = findViewById(R.id.layoutAuth);
            objMyApplication = (MyApplication) getApplicationContext();
            objMyApplication.setIsLoggedIn(false);
            getVersionName();
            setDB();
            setToken();
            objMyApplication.initializeDBHandler(OnboardActivity.this);
            isFaceLock = objMyApplication.setFaceLock();
            isTouchId = objMyApplication.setTouchId();
            if (isFaceLock || isTouchId) {
                objMyApplication.setLocalBiometric(true);
            } else {
                objMyApplication.setLocalBiometric(false);
            }

            isBiometric = Utils.checkBiometric(OnboardActivity.this);
            Utils.setIsBiometric(isBiometric);

            if (!objMyApplication.isDeviceID()) {
                Utils.generateUUID(OnboardActivity.this);
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

            CheckOutModel checkOutModel = objMyApplication.getCheckOutModel();
//            if (Utils.getStrAuth() != null && !Utils.getStrAuth().equals("")) {
//                Intent dashboardIntent = new Intent(OnboardActivity.this, DashboardActivity.class);
//                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
//                    dashboardIntent = new Intent(OnboardActivity.this, BusinessDashboardActivity.class);
//                }
//                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(dashboardIntent);
//            } else {
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
                if (checkOutModel != null && checkOutModel.isCheckOutFlag()) {
                    startActivity(new Intent(OnboardActivity.this, LoginActivity.class));
                    finish();
                } else if (strFirstUser.equals("")) {
                    layoutOnBoarding.setVisibility(View.VISIBLE);
                    layoutAuth.setVisibility(View.GONE);
                } else {
                    Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                    i.putExtra("auth", "cancel");
                    startActivity(i);
                    finish();
                }
            }
//            }
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);

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


//            BiometricManager biometricManager = BiometricManager.from(getApplicationContext());
//            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
//                case BiometricManager.BIOMETRIC_SUCCESS:
//                    Log.e("BIOMETRIC_WEAK", "App can authenticate using biometrics.");
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                    Log.e("BIOMETRIC_WEAK", "No biometric features available on this device.");
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                    Log.e("BIOMETRIC_WEAK", "Biometric features are currently unavailable.");
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                    // Prompts the user to create credentials that your app accepts.
//                    Log.e("BIOMETRIC_WEAK", "BIOMETRIC_ERROR_NONE_ENROLLED.");
//                    break;
//            }
//
//            BiometricManager biometricManager2 = BiometricManager.from(getApplicationContext());
//            switch (biometricManager2.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
//                case BiometricManager.BIOMETRIC_SUCCESS:
//                    Log.e("BIOMETRIC_STRONG", "App can authenticate using biometrics.");
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                    Log.e("BIOMETRIC_STRONG", "No biometric features available on this device.");
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                    Log.e("BIOMETRIC_STRONG", "Biometric features are currently unavailable.");
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                    // Prompts the user to create credentials that your app accepts.
//                    Log.e("BIOMETRIC_STRONG", "BIOMETRIC_ERROR_NONE_ENROLLED.");
//                    break;
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.v(TAG, "OnNewIntent " + intent.getData());
    }

    @Override
    protected void onResume() {
        objMyApplication.setIsLoggedIn(false);
        super.onResume();
        try {
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(OnboardActivity.this);
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

            CheckOutModel checkOutModel = objMyApplication.getCheckOutModel();
            if (checkOutModel != null && checkOutModel.isCheckOutFlag()) {
                startActivity(new Intent(OnboardActivity.this, LoginActivity.class));
                finish();
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
                        if (Utils.checkInternet(OnboardActivity.this)) {
                            if (!strToken.equals("") && !strDeviceID.equals("")) {
                                login();
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), OnboardActivity.this, "", "");
                        }
                    }
                });
            } else {
                Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                i.putExtra("auth", "cancel");
                startActivity(i);
                finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        try {
            loginViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<LoginResponse>() {
                @Override
                public void onChanged(LoginResponse loginResponse) {
                    dismissDialog();
                    try {
                        if (loginResponse != null) {
                            if (!loginResponse.getStatus().toLowerCase().equals("error")) {
                                Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                objMyApplication.setStrEmail(loginResponse.getData().getEmail());
                                //                            objMyApplication.setUserId(loginResponse.getData().getUserId());
                                objMyApplication.setLoginUserId(loginResponse.getData().getUserId());
                                objMyApplication.setLoginResponse(loginResponse);
                                Utils.setUserEmail(OnboardActivity.this, loginResponse.getData().getEmail());
                                objMyApplication.setStrEmail(loginResponse.getData().getEmail());
                                objMyApplication.setBiometric(loginResponse.getData().getBiometricEnabled());
                                getStatesUrl(loginResponse.getData().getStateList().getUS());
                                objMyApplication.setAccountType(loginResponse.getData().getAccountType());
                                objMyApplication.setDbaOwnerId(loginResponse.getData().getDbaOwnerId());
                                if (loginResponse.getData().getPasswordExpired()) {
//                                    Intent i = new Intent(OnboardActivity.this, PINActivity.class);
//                                    i.putExtra("screen", "loginExpiry");
//                                    i.putExtra("TYPE", "ENTER");
//                                    startActivity(i);
                                    Intent i = new Intent(OnboardActivity.this, CreatePasswordActivity.class);
                                    i.putExtra("screen", "loginExpiry");
                                    startActivity(i);
                                    finish();
                                } else {
                                    Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                    objMyApplication.setIsLoggedIn(true);
//                                    if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
//                                        businessIdentityVerificationViewModel.getBusinessTracker();
//                                    } else {
//                                        Intent i = new Intent(OnboardActivity.this, DashboardActivity.class);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(i);
                                    launchDashboard();
//                                    }
//                                    Intent i = null;
//                                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT)
//                                        i = new Intent(OnboardActivity.this, DashboardActivity.class);
//                                    else
//                                        i = new Intent(OnboardActivity.this, BusinessDashboardActivity.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(i);
                                }
                            } else {
                                if (loginResponse.getData() != null) {
                                    if (!loginResponse.getData().getMessage().equals("") && loginResponse.getData().getPasswordFailedAttempts() > 0) {
                                        //                                    Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                                        //                                    emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());

                                        Utils.emailPasswordIncorrectDialog("", OnboardActivity.this, "");
                                    }
                                } else {
                                    Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
//                                    Utils.displayAlert(loginResponse.getError().getErrorDescription(), OnboardActivity.this, "", loginResponse.getError().getFieldErrors().get(0));
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
                @Override
                public void onChanged(BusinessTrackerResponse businessTrackerResponse) {

                    if (businessTrackerResponse != null) {
                        if (businessTrackerResponse.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTrackerResponse(businessTrackerResponse);
                            launchDashboard();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void launchDashboard() {
        if (objMyApplication.checkForDeclinedStatus()) {
            objMyApplication.setIsLoggedIn(true);
            objMyApplication.launchDeclinedActivity(this);
        } else {
            objMyApplication.setIsLoggedIn(true);
            objMyApplication.launchDashboard(this, "login");
        }
    }

    private void setDB() {
        String value = dbHandler.getTableUserDetails();

        if (value != null && !value.equals("")) {
            strFirstUser = value;
        }
    }

    private void setToken() {
        strToken = dbHandler.getPermanentToken();
        objMyApplication.setStrMobileToken(strToken);
    }

//    private void setFaceLock() {
//        try {
//            isFaceLock = false;
//            String value = dbHandler.getFacePinLock();
//            if (value != null && value.equals("true")) {
//                isFaceLock = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isFaceLock = false;
//                objMyApplication.setLocalBiometric(false);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void setTouchId() {
//        try {
//            isTouchId = false;
//            String value = dbHandler.getThumbPinLock();
//            if (value != null && value.equals("true")) {
//                isTouchId = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isTouchId = false;
////                objMyApplication.setLocalBiometric(false);
//                if (!isFaceLock) {
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void login() {
        try {
            showProgressDialog();
            BiometricLoginRequest request = new BiometricLoginRequest();
            request.setDeviceId(strDeviceID);
            request.setEnableBiometic(true);
//            request.setMobileToken(strToken);
            request.setMobileToken(objMyApplication.getStrMobileToken());
            loginViewModel.biometricLogin(request, objMyApplication);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getVersionName() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            Utils.setAppVersion("Android : " + info.versionName + "(" + info.versionCode + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getStatesUrl(String strCode) {
        try {
            byte[] valueDecoded = new byte[0];
            valueDecoded = Base64.decode(strCode.getBytes("UTF-8"), Base64.DEFAULT);
            objMyApplication.setStrStatesUrl(new String(valueDecoded));
            Log.e("States url", objMyApplication.getStrStatesUrl() + "   sdssd");
            try {
                new HttpGetRequest().execute("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(objMyApplication.getStrStatesUrl());
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(result, type);
            objMyApplication.setListStates(listStates);
        }
    }
}
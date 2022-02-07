package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AutoScrollPagerAdapter;
import com.greenbox.coyni.fragments.FaceIdDisabled_BottomSheet;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OnboardActivity extends AppCompatActivity {
    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 3000;
    LinearLayout getStarted, layoutLogin;
    Long mLastClickTime = 0L;
    SQLiteDatabase mydatabase;
    Cursor dsPermanentToken, dsFacePin, dsTouchID, dsUserDetails;
    String strToken = "", strDeviceID = "", strFirstUser = "";
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
            getVersionName();
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
            SetDB();
            SetToken();
            SetFaceLock();
            SetTouchId();
            isBiometric = Utils.checkBiometric(OnboardActivity.this);
            Utils.setIsBiometric(isBiometric);

            if (!isDeviceID()) {
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
                if (strFirstUser.equals("")) {
                    layoutOnBoarding.setVisibility(View.VISIBLE);
                    layoutAuth.setVisibility(View.GONE);
                } else {
                    Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                    i.putExtra("auth", "cancel");
                    startActivity(i);
                    finish();
                }
            }

//            if (!isDeviceID()) {
//                Utils.generateUUID(OnboardActivity.this);
//            }
//            strDeviceID = Utils.getDeviceID();
//            getStarted = findViewById(R.id.getStartedLL);
//            layoutLogin = findViewById(R.id.layoutLogin);
//            String url = BuildConfig.URL_PRODUCTION;
//            String refererUrl = BuildConfig.Referer;
//            Utils.setStrCCode(BuildConfig.Country_Code);
//            if (!url.equals("")) {
//                Utils.setStrURL_PRODUCTION(url);
//                Utils.setStrReferer(refererUrl);
//            }
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
        if (Utils.isKeyboardVisible)
            Utils.hideKeypad(OnboardActivity.this);
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
        loginViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                dialog.dismiss();
                try {
                    if (loginResponse != null) {
                        if (!loginResponse.getStatus().toLowerCase().equals("error")) {
                            Utils.setStrAuth(loginResponse.getData().getJwtToken());
                            objMyApplication.setStrEmail(loginResponse.getData().getEmail());
//                            objMyApplication.setUserId(loginResponse.getData().getUserId());
                            objMyApplication.setLoginUserId(loginResponse.getData().getUserId());
                            Utils.setUserEmail(OnboardActivity.this, loginResponse.getData().getEmail());
                            objMyApplication.setBiometric(loginResponse.getData().getBiometricEnabled());
                            getStatesUrl(loginResponse.getData().getStateList().getUS());
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
//                                    Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
//                                    emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());

                                    Utils.emailPasswordIncorrectDialog("", OnboardActivity.this, "");
                                }
                            } else {
                                Utils.displayAlert(loginResponse.getError().getErrorDescription(), OnboardActivity.this, "", loginResponse.getError().getFieldErrors().get(0));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void SetDB() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
            dsUserDetails.moveToFirst();
            if (dsUserDetails.getCount() > 0) {
                strFirstUser = dsUserDetails.getString(1);
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, email TEXT);");
            }
        }
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

    private Boolean isDeviceID() {
        Boolean value = false;
        SharedPreferences prefs = getSharedPreferences("DeviceID", MODE_PRIVATE);
        value = prefs.getBoolean("isDevice", false);
        if (value) {
            Utils.setDeviceID(prefs.getString("deviceId", ""));
        }
        return value;
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
package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdDisabled_BottomSheet;
import com.greenbox.coyni.fragments.FaceIdNotAvailable_BottomSheet;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
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

public class LoginActivity extends AppCompatActivity implements OnKeyboardVisibilityListener {
    TextInputLayout etlEmail, etlPassword;
    TextInputEditText etEmail, etPassword;
    CardView cvNext;
    LinearLayout layoutEmailError, layoutPwdError;
    TextView tvEmailError, tvPwdError, forgotpwd, tvRetEmail;
    String strEmail = "", strPwd = "", strMsg = "", strToken = "", strFirstUser = "";
    ProgressDialog dialog;
    LoginViewModel loginViewModel;
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails, dsFacePin, dsRemember, dsPermanentToken, dsTouchID;
    Boolean isFaceLock = false, isTouchId = false, isPwdEye = false;
    ImageView loginBGIV, endIconIV;
    CheckBox chkRemember;
    MyApplication objMyApplication;
    LinearLayout layoutClose;
    RelativeLayout layoutMain;
    private long mLastClickTime = 0;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    LoginResponse loginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_login);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        objMyApplication.setStrRetrEmail("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null) {
            dialog.dismiss();
        }
        isPwdEye = false;

        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            Cursor cursor = mydatabase.rawQuery("Select * from tblRemember", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String value = cursor.getString(1);
                etEmail.setText(value);
                etPassword.setText("");
            } else {
                etEmail.setText("");
                etPassword.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            etEmail.setText("");
            etPassword.setText("");
        }
        if (objMyApplication.getStrRetrEmail() != null && !objMyApplication.getStrRetrEmail().equals("")) {
            if (chkRemember.isChecked()) {
                etEmail.setText("");
                etPassword.setText("");
                chkRemember.setChecked(false);
            }
            etEmail.setText(objMyApplication.getStrRetrEmail());
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
                        if (Utils.checkInternet(LoginActivity.this)) {
                            if (!strToken.equals("") && !Utils.getDeviceID().equals("")) {
                                biometricLogin();
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), LoginActivity.this, "", "");
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            setKeyboardVisibilityListener(this);
            etlPassword = findViewById(R.id.etlPassword);
            etlEmail = findViewById(R.id.etlEmail);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            cvNext = findViewById(R.id.cvNext);
            layoutEmailError = findViewById(R.id.layoutEmailError);
            tvEmailError = findViewById(R.id.tvEmailError);
            layoutPwdError = findViewById(R.id.layoutPwdError);
            tvPwdError = findViewById(R.id.tvPwdError);
            forgotpwd = findViewById(R.id.forgotpwd);
            tvRetEmail = findViewById(R.id.tvRetEmail);
            layoutClose = findViewById(R.id.layoutClose);
            layoutMain = findViewById(R.id.layoutMain);
            chkRemember = findViewById(R.id.chkRemember);
            loginBGIV = findViewById(R.id.loginBGIV);
            endIconIV = findViewById(R.id.endIconIV);
            cvNext.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();

            etEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});

            etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});

            if (getIntent().getStringExtra("auth") != null && getIntent().getStringExtra("auth").equals("cancel")) {
                layoutClose.setVisibility(GONE);
            } else {
                layoutClose.setVisibility(VISIBLE);
            }

            endIconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (Utils.getIsTouchEnabled() || Utils.getIsFaceEnabled()) {
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(LoginActivity.this)) {
                                if ((isTouchId && Utils.isFingerPrint(LoginActivity.this)) || (isFaceLock)) {
                                    Utils.checkAuthentication(LoginActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    FaceIdDisabled_BottomSheet faceIdNotAvailable_bottomSheet = FaceIdDisabled_BottomSheet.newInstance(isTouchId, isFaceLock);
                                    faceIdNotAvailable_bottomSheet.show(getSupportFragmentManager(), faceIdNotAvailable_bottomSheet.getTag());
                                }
                            } else {
                                FaceIdNotAvailable_BottomSheet faceIdNotAvailable_bottomSheet = new FaceIdNotAvailable_BottomSheet();
                                faceIdNotAvailable_bottomSheet.show(getSupportFragmentManager(), faceIdNotAvailable_bottomSheet.getTag());
                            }
                        } else {
                            if (!isPwdEye) {
                                isPwdEye = true;
                                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_eyeopen));
                                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            } else {
                                isPwdEye = false;
                                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_eyeclose));
                                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            }
                            if (etPassword.getText().length() > 0) {
                                etPassword.setSelection(etPassword.getText().length());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
//                            if (etEmail.getText().toString().trim().length() > 5 && !Utils.isValidEmail(etEmail.getText().toString().trim())) {
                            if (etEmail.getText().toString().trim().length() != 0 && !Utils.isValidEmail(etEmail.getText().toString().trim())) {
                                etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                                layoutEmailError.setVisibility(VISIBLE);
                                tvEmailError.setText("Invalid Email");
                            } else if (etEmail.getText().toString().trim().length() == 0 || (etEmail.getText().toString().trim().length() > 5 && Utils.isValidEmail(etEmail.getText().toString().trim()))) {
                                etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_black));
                                layoutEmailError.setVisibility(GONE);
                            }
//                            else {
//                                etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
//                                layoutEmailError.setVisibility(VISIBLE);
//                                tvEmailError.setText("Field Required");
//                            }
                        } else {
                            etlEmail.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_green));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
                            if (etPassword.getText().toString().trim().length() < 8 && etPassword.getText().toString().trim().length() > 0) {
                                etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
                                layoutPwdError.setVisibility(VISIBLE);
                                tvPwdError.setText("Invalid Password");
                            } else if (etPassword.getText().toString().trim().length() == 0 || etPassword.getText().toString().trim().length() >= 8) {
                                etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_black));
                                layoutPwdError.setVisibility(GONE);
                            }
//                            else {
//                                etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
//                                layoutPwdError.setVisibility(VISIBLE);
//                                tvPwdError.setText("Field Required");
//                            }
                        } else {
                            etlPassword.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_green));
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String str = etEmail.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            etEmail.setText(etEmail.getText().toString().replaceAll(" ", ""));
                            etEmail.setSelection(etEmail.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (Utils.isValidEmail(etEmail.getText().toString().trim()) && etEmail.getText().toString().trim().length() > 5 && etPassword.getText().toString().length() >= 8) {
                            cvNext.setEnabled(true);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        } else {
                            layoutEmailError.setVisibility(GONE);
                            cvNext.setEnabled(false);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            etPassword.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            etPassword.setText(s.toString().trim());
                            etPassword.setSelection(s.toString().trim().length());
                        }
                        if (etPassword.getText().toString().length() >= 8) {
                            layoutPwdError.setVisibility(GONE);
                        }

                        if (s.length() == 0) {
                            // No entered text so will show hint
                            etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        } else {
                            etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (Utils.isValidEmail(etEmail.getText().toString().trim()) && etEmail.getText().toString().trim().length() > 5 && etPassword.getText().toString().length() >= 8) {
                            cvNext.setEnabled(true);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                            layoutEmailError.setVisibility(GONE);
                            layoutPwdError.setVisibility(GONE);
                        } else {
                            layoutPwdError.setVisibility(GONE);
                            cvNext.setEnabled(false);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            forgotpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    i.putExtra("screen", "ForgotPwd");
                    i.putExtra("email", etEmail.getText().toString().trim());
                    startActivity(i);
                }
            });

            tvRetEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, RetrieveEmailActivity.class);
                    startActivity(i);
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.hideKeypad(LoginActivity.this, v);
                        if (Utils.checkInternet(LoginActivity.this)) {
                            strEmail = etEmail.getText().toString().trim().toLowerCase();
                            strPwd = etPassword.getText().toString().trim();
                            if (compareCredentials()) {
                                login();
                            } else {
                                Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                                emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), LoginActivity.this, "", "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objMyApplication.setStrRetrEmail("");
                    onBackPressed();
                }
            });

            enableIcon();
            SetDB();
            SetToken();
            SetFaceLock();
            SetTouchId();
            SetRemember();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                    if (Utils.getIsTouchEnabled()) {
                        endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_id));
                    } else {
                        endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_faceid));
                    }
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
                    endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_id));
                } else {
                    isTouchId = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetRemember() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsRemember = mydatabase.rawQuery("Select * from tblRemember", null);
            dsRemember.moveToFirst();
            if (dsRemember.getCount() > 0) {
                etEmail.setText(dsRemember.getString(1));
                chkRemember.setChecked(true);
            } else {
                chkRemember.setChecked(false);
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblRemember;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblRemember(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT);");
            }
        }
    }

    private void initObserver() {
        loginViewModel.getLoginLiveData().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse login) {
                try {
                    dialog.dismiss();
                    if (login != null) {
                        if (!login.getStatus().toLowerCase().equals("error")) {
                            Utils.setStrAuth(login.getData().getJwtToken());
                            objMyApplication.setStrEmail(login.getData().getEmail());
                            objMyApplication.setUserId(login.getData().getUserId());
                            Utils.setUserEmail(LoginActivity.this, login.getData().getEmail());
                            objMyApplication.setBiometric(login.getData().getBiometricEnabled());
                            getStatesUrl(login.getData().getStateList().getUS());
                            if (login.getData().getPasswordExpired()) {
                                Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                i.putExtra("screen", "loginExpiry");
                                i.putExtra("TYPE", "ENTER");
                                startActivity(i);
                            } else {
                                if (chkRemember.isChecked()) {
                                    saveCredentials();
                                } else {
                                    mydatabase.execSQL("Delete from tblRemember");
                                }
                                saveFirstUser();
                                if (login.getData().getCoyniPin()) {
                                    Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                    i.putExtra("TYPE", "ENTER");
                                    i.putExtra("screen", "login");
                                    startActivity(i);
                                } else {
                                    loginResponse = login;
//                                    Intent i = new Intent(LoginActivity.this, OTPValidation.class);
//                                    i.putExtra("screen", "login");
//                                    i.putExtra("OTP_TYPE", "MOBILE");
//                                    i.putExtra("MOBILE", login.getData().getPhoneNumber());
//                                    i.putExtra("EMAIL", login.getData().getEmail());
//                                    i.putExtra("MASK_MOBILE", Utils.convertToUSFormat(login.getData().getPhoneNumber()));
//                                    startActivity(i);
                                    SMSResend resend = new SMSResend();
                                    resend.setCountryCode(Utils.getStrCCode());
                                    resend.setPhoneNumber(login.getData().getPhoneNumber());
                                    loginViewModel.smsotpresend(resend);

                                }
                            }
                        } else {
                            if (login.getData() != null) {
                                if (!login.getData().getMessage().equals("") && login.getData().getPasswordFailedAttempts() > 0) {
                                    Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                                    emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());
                                }
                            } else {
                                Utils.displayAlert(login.getError().getErrorDescription(), LoginActivity.this, "", login.getError().getFieldErrors().get(0));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
//                    if (!apiError.getError().getErrorDescription().equals("")) {
//                        Utils.displayAlert(apiError.getError().getErrorDescription(), LoginActivity.this);
//                    } else {
//                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), LoginActivity.this);
//                    }
                    Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                    emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());
                }
            }
        });

        loginViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                dialog.dismiss();
                try {
                    if (loginResponse != null) {
                        if (!loginResponse.getStatus().toLowerCase().equals("error")) {
                            Utils.setStrAuth(loginResponse.getData().getJwtToken());
                            objMyApplication.setStrEmail(loginResponse.getData().getEmail());
                            objMyApplication.setUserId(loginResponse.getData().getUserId());
                            Utils.setUserEmail(LoginActivity.this, loginResponse.getData().getEmail());
                            objMyApplication.setBiometric(loginResponse.getData().getBiometricEnabled());
                            getStatesUrl(loginResponse.getData().getStateList().getUS());
                            if (loginResponse.getData().getPasswordExpired()) {
                                Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                i.putExtra("screen", "loginExpiry");
                                i.putExtra("TYPE", "ENTER");
                                startActivity(i);
                            } else {
                                Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
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
                                Utils.displayAlert(loginResponse.getError().getErrorDescription(), LoginActivity.this, "", loginResponse.getError().getFieldErrors().get(0));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getSmsresendMutableLiveData().observe(this, new Observer<SMSResponse>() {
            @Override
            public void onChanged(SMSResponse smsResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (smsResponse != null) {
                    if (smsResponse.getStatus().toLowerCase().toString().equals("success")) {
                        Intent i = new Intent(LoginActivity.this, OTPValidation.class);
                        i.putExtra("screen", "login");
                        i.putExtra("OTP_TYPE", "MOBILE");
                        i.putExtra("MOBILE", loginResponse.getData().getPhoneNumber());
                        i.putExtra("EMAIL", loginResponse.getData().getEmail());
                        i.putExtra("MASK_MOBILE", Utils.convertToUSFormat(loginResponse.getData().getPhoneNumber()));
                        startActivity(i);
                    } else {
                        Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", LoginActivity.this, "Error", "");
                    }
                }

            }
        });

    }

    private Boolean emailValidation() {
        Boolean value = true;
        try {
            if (etEmail.getText().toString().trim().equals("")) {
                etlEmail.setErrorEnabled(true);
                etlEmail.setError(" ");
                layoutEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Please enter Email");
                return value = false;
            } else if (etEmail.getText().toString().trim().startsWith(" ")) {
                etlEmail.setErrorEnabled(true);
                etlEmail.setError(" ");
                layoutEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Please enter valid Email");
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                etlEmail.setErrorEnabled(true);
                etlEmail.setError(" ");
                layoutEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Please enter valid Email");
                return value = false;
            } else {
                etlEmail.setErrorEnabled(false);
                etlEmail.setError("");
                layoutEmailError.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private Boolean passwordValidation() {
        Boolean value = true;
        try {
            if (etPassword.getText().toString().equals("")) {
                etlPassword.setErrorEnabled(true);
                etlPassword.setError(" ");
                layoutPwdError.setVisibility(View.VISIBLE);
                tvPwdError.setText("Please enter Password");
                return value = false;
            } else if (etPassword.getText().toString().startsWith(" ")) {
                etlPassword.setErrorEnabled(true);
                etlPassword.setError(" ");
                layoutPwdError.setVisibility(View.VISIBLE);
                tvPwdError.setText("Please enter valid Password");
                return value = false;
            } else if (etPassword.getText().toString().trim().equals("")) {
                etlPassword.setErrorEnabled(true);
                etlPassword.setError(" ");
                layoutPwdError.setVisibility(View.VISIBLE);
                tvPwdError.setText("Please enter valid Password");
                return value = false;
            } else {
                etlPassword.setErrorEnabled(false);
                etlPassword.setError("");
                layoutPwdError.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void login() {
        try {
            dialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(strEmail);
            loginRequest.setPassword(strPwd);
            loginViewModel.login(loginRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void biometricLogin() {
        try {
            dialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            BiometricLoginRequest request = new BiometricLoginRequest();
            request.setDeviceId(Utils.getDeviceID());
            request.setEnableBiometic(true);
            request.setMobileToken(strToken);
            loginViewModel.biometricLogin(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveCredentials() {
        try {
//            if (strEmail.equals("") && strPwd.equals("")) {
//                strEmail = etEmail.getText().toString().trim().toLowerCase();
//                strPwd = etPassword.getText().toString().trim();
//            }
            if (strEmail.equals("")) {
                strEmail = etEmail.getText().toString().trim().toLowerCase();
            }
            mydatabase.execSQL("Delete from tblRemember");
            mydatabase.execSQL("INSERT INTO tblRemember(id,username) VALUES(null,'" + strEmail.toLowerCase() + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFirstUser() {
        try {
            if (strFirstUser.equals("")) {
                strFirstUser = etEmail.getText().toString().trim().toLowerCase();
            }
            mydatabase.execSQL("Delete from tblUserDetails");
            mydatabase.execSQL("INSERT INTO tblUserDetails(id,email) VALUES(null,'" + strFirstUser.toLowerCase() + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean compareCredentials() {
        Boolean value = true;
        try {
            if (!strFirstUser.equals("")) {
                if (!etEmail.getText().toString().trim().toLowerCase().equals("") && !etEmail.getText().toString().trim().toLowerCase().equals(strFirstUser.trim().toLowerCase())) {
                    value = false;
                } else {
                    value = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void enableIcon() {
        try {
            endIconIV.setVisibility(VISIBLE);
            if (Utils.getIsTouchEnabled()) {
                etlPassword.setPasswordVisibilityToggleEnabled(false);
                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_id));
                strMsg = "Do you want to register with Thumb/Pin.";
            } else if (Utils.getIsFaceEnabled()) {
                etlPassword.setPasswordVisibilityToggleEnabled(false);
                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_faceid));
                strMsg = "Do you want to register with FaceID/Pin.";
            } else {
                etlPassword.setPasswordVisibilityToggleEnabled(false);
//                etlPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_eyeclose));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            loginBGIV.setAlpha(0.2f);
        } else {
            loginBGIV.setAlpha(1.0f);
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
//            Log.e("result", result);
        }
    }
}
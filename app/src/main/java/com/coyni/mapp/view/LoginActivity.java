package com.coyni.mapp.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.dialogs.OnAgreementsAPIListener;
import com.coyni.mapp.model.FilteredAgreements;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.BuildConfig;
import com.coyni.mapp.R;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.login.BiometricLoginRequest;
import com.coyni.mapp.model.login.LoginRequest;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.register.SMSResend;
import com.coyni.mapp.model.register.SMSResponse;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MatomoConstants;
import com.coyni.mapp.utils.MatomoUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoginActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    private TextInputLayout etlEmail, etlPassword;
    private TextInputEditText etEmail, etPassword;
    private CardView cvNext;
    private LinearLayout layoutEmailError, layoutPwdError, layoutClose;
    private TextView tvEmailError, tvPwdError, forgotpwd, tvRetEmail;
    private String strEmail = "", strPwd = "", strMsg = "", strToken = "", strFirstUser = "";
    private LoginViewModel loginViewModel;
    private Boolean isFaceLock = false, isTouchId = false, isPwdEye = false, isExpiry = false;
    private ImageView loginBGIV, endIconIV, coyniLogoIV;
    private CheckBox chkRemember;
    private DatabaseHandler dbHandler;
    private MyApplication objMyApplication;
    private RelativeLayout layoutMain;
    private long mLastClickTime = 0;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    private BiometricSignIn loginResponse;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private boolean closeClicked = false;
    private int logoClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_login);
            MatomoUtility.getInstance().trackScreen(MatomoConstants.LOGIN_SCREEN);
            if (getIntent() != null && getIntent().getExtras() != null) {
                LogUtils.v("TAG", getIntent().getExtras() + "");
            }
            initialization();
            initObserver();
            setOnAgreementsAPIListener(new OnAgreementsAPIListener() {
                @Override
                public void onAgreementsAPIResponse(SignAgreementsResp signAgreementsResp, boolean isMerchantHide) {
                    dismissDialog();
                    if (isMerchantHide) {
                        FilteredAgreements filteredAgreements = Utils.getFilteredAgreements(signAgreementsResp.getData());
                        if (filteredAgreements.getAgreements().size() > 0) {
                            objMyApplication.setHasToSignAgreements(filteredAgreements.getAgreements());
                            Utils.launchAgreements(LoginActivity.this, isMerchantHide);
                        } else {
                            launchDasboardFromBase();
                        }
                    } else {
                        if (signAgreementsResp.getData().size() > 0) {
                            objMyApplication.setHasToSignAgreements(signAgreementsResp.getData());
                            Utils.launchAgreements(LoginActivity.this, isMerchantHide);
                        } else {
                            launchDasboardFromBase();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            objMyApplication.setStrRetrEmail("");
            new Handler().postDelayed((Runnable) super::onBackPressed, 100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissDialog();
        objMyApplication.setIsLoggedIn(false);
        isPwdEye = false;
        try {
            if (Utils.checkBiometric(LoginActivity.this) && Utils.checkAuthentication(LoginActivity.this)) {
                if (Utils.isFingerPrint(LoginActivity.this)) {
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
            enableIcon();
            setDB();
            setToken();
            setFaceLock();
            setTouchId();
            setRemember();
            if (!isExpiry) {
                String value = dbHandler.getTableRemember();

                if (value != null && !value.equals("")) {
                    etEmail.setText(value);
                    if (isEmailValid(etEmail.getText().toString().trim())) {
                        Utils.setUpperHintColor(etlEmail, getResources().getColor(R.color.primary_black));
                        etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        layoutEmailError.setVisibility(GONE);
                    }
                }
                clearPwdControl();
                if (objMyApplication.getStrRetrEmail() != null && !objMyApplication.getStrRetrEmail().equals("")) {
                    if (chkRemember.isChecked()) {
                        etPassword.setText("");
                        chkRemember.setChecked(false);
                        clearEmailControl();
                        Utils.setUpperHintColor(etlPassword, getColor(R.color.light_gray));
                        etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                    }
                    etEmail.setText(objMyApplication.getStrRetrEmail());
                    if (isEmailValid(etEmail.getText().toString().trim())) {
                        etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                        Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_black));
                        layoutEmailError.setVisibility(GONE);
                    }
                }
            } else {
                layoutClose.setVisibility(GONE);
                clearEmailControl();
                clearPwdControl();
                isExpiry = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            etPassword.setText("");
            etPassword.setHint("");
            Utils.setUpperHintColor(etlPassword, getColor(R.color.light_gray));
            etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
        }
        if (Utils.isKeyboardVisible)
            Utils.hideKeypad(LoginActivity.this);

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
            coyniLogoIV = findViewById(R.id.coyniLogoIV);
            cvNext.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            dbHandler = DatabaseHandler.getInstance(LoginActivity.this);

            etEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});

            etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
            etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
            etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));

            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            if ((getIntent().getStringExtra("auth") != null && getIntent().getStringExtra("auth").equals("cancel"))) {
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
                            if (Utils.isKeyboardVisible)
                                Utils.hideKeypad(LoginActivity.this);
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(LoginActivity.this)) {
                                if ((isTouchId && Utils.isFingerPrint(LoginActivity.this)) || (isFaceLock)) {
                                    Utils.checkAuthentication(LoginActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    if (isFaceLock) {
                                        Utils.displayAlert(getResources().getString(R.string.faceiddisable), LoginActivity.this, "Face ID Temporarily disabled", "");
                                    } else if (isTouchId) {
                                        Utils.displayAlert(getResources().getString(R.string.touchiddisable), LoginActivity.this, "Touch ID Temporarily disabled", "");
                                    }
                                }
                            } else {
                                if (Utils.getIsTouchEnabled()) {
                                    Utils.displayAlert(getResources().getString(R.string.touchidnotavaidescri), LoginActivity.this, "Touch ID Not available", "");
                                } else if (Utils.getIsFaceEnabled()) {
                                    Utils.displayAlert(getResources().getString(R.string.faceidnotavaidescri), LoginActivity.this, "Face ID Not available", "");
                                }
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
                        if (!closeClicked) {
                            if (!hasFocus) {
                                if ((etEmail.getText().toString().trim().length() != 0 && !Utils.isValidEmail(etEmail.getText().toString().trim()))
                                        || (etEmail.getText().toString().trim().length() > 0 && etEmail.getText().toString().trim().length() < 6)) {
                                    etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState(LoginActivity.this));
                                    Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                                    layoutEmailError.setVisibility(VISIBLE);
                                    tvEmailError.setText("Please enter a valid Email");
                                } else if (etEmail.getText().toString().trim().length() == 0) {
                                    etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                                    Utils.setUpperHintColor(etlEmail, getColor(R.color.light_gray));
                                    etEmail.setHint("");
                                    etlEmail.setHint("Email");
                                    layoutEmailError.setVisibility(GONE);
                                } else if ((etEmail.getText().toString().trim().length() > 5 && Utils.isValidEmail(etEmail.getText().toString().trim()))) {
                                    etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                                    Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_black));
                                    etEmail.setHint("");
                                    etlEmail.setHint("Email");
                                    layoutEmailError.setVisibility(GONE);
                                }
                            } else {
                                if (!Utils.isKeyboardVisible)
                                    Utils.shwForcedKeypad(LoginActivity.this);
                                etlEmail.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                                Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_green));
//                                etEmail.setHint((getResources().getString(R.string.email)));
                                layoutEmailError.setVisibility(GONE);
                            }
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
                        if (!closeClicked) {
                            if (!hasFocus) {
                                if (etPassword.getText().toString().trim().length() < 8 && etPassword.getText().toString().trim().length() > 0) {
                                    etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState(LoginActivity.this));
                                    Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
                                    layoutPwdError.setVisibility(VISIBLE);
                                    tvPwdError.setText("Please enter a valid Password");
                                } else if (etPassword.getText().toString().trim().length() == 0) {
                                    etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                                    Utils.setUpperHintColor(etlPassword, getColor(R.color.light_gray));
                                    etPassword.setHint("");
                                    layoutPwdError.setVisibility(GONE);
                                } else if (etPassword.getText().toString().trim().length() >= 8) {
                                    etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                                    Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_black));
                                    layoutPwdError.setVisibility(GONE);
                                }
                                etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            } else {

                                etlPassword.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                                Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_green));
                                layoutPwdError.setVisibility(GONE);
                                etPassword.setHint("\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605");

                                if (etPassword.getText().toString().length() > 0)
                                    etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                else
                                    etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                                if (!Utils.isKeyboardVisible) {
                                    Log.e("Utils.isKeyboardVisible Focus", Utils.isKeyboardVisible + "");
                                    Utils.shwForcedKeypad(LoginActivity.this);
                                }
                            }
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
                public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                    if (i2 - i1 > 1) {
                        etPassword.setText(s);
                        etPassword.setSelection(s.toString().length());
                    }
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
                            if (etPassword.hasFocus())
                                etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            else
                                etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideAndClearFocus();
                                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                                i.putExtra("screen", "ForgotPwd");
                                i.putExtra("email", etEmail.getText().toString().trim());
                                startActivity(i);
                            }

                        }, 1000);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            tvRetEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        hideAndClearFocus();
                        Intent i = new Intent(LoginActivity.this, RetrieveEmailActivity.class);
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MatomoUtility.getInstance().trackEvent(MatomoConstants.LOGIN, MatomoConstants.LOGIN_CLICKED);
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(LoginActivity.this);
                        hideAndClearFocus();
                        if (Utils.checkInternet(LoginActivity.this)) {
                            strEmail = etEmail.getText().toString().trim().toLowerCase();
                            strPwd = etPassword.getText().toString().trim();
                            if (compareCredentials()) {
                                login();
                            } else {
                                Utils.emailPasswordIncorrectDialog("", LoginActivity.this, "");
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
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(LoginActivity.this);

                    closeClicked = true;
                    objMyApplication.setStrRetrEmail("");
                    onBackPressed();
                }
            });

            coyniLogoIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!BuildConfig.FLAVOR.equals("sat") && !BuildConfig.FLAVOR.equals("uat") && !BuildConfig.FLAVOR.equals("beta")) {
                        try {
                            String strEndPoint = "";
                            strEndPoint = BuildConfig.FLAVOR + " " + BuildConfig.BUILD_TYPE + " " + BuildConfig.URL_PRODUCTION;
                            if (logoClickCount == 5) {
                                logoClickCount = 0;
                                Toast.makeText(LoginActivity.this, strEndPoint, Toast.LENGTH_LONG).show();
                            } else {
                                logoClickCount++;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

//            enableIcon();
//
//            setDB();
//            setToken();
//            setFaceLock();
//            setTouchId();
//            setRemember();
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                if (Utils.getIsTouchEnabled()) {
                    endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_id));
                } else {
                    endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_faceid));
                }
            } else {
                isFaceLock = false;
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
                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_id));

            } else {
                isTouchId = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setRemember() {
        String value = dbHandler.getTableRemember();
        if (value != null && !value.equals("")) {
            etEmail.setText(value);
            if (isEmailValid(etEmail.getText().toString().trim())) {
                Utils.setUpperHintColor(etlEmail, getResources().getColor(R.color.primary_black));
                etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
                layoutEmailError.setVisibility(GONE);
            }
            chkRemember.setChecked(true);
        } else {
            chkRemember.setChecked(false);
        }
    }


    private void initObserver() {
        try {
//            loginViewModel.getLoginLiveData().observe(this, new Observer<LoginResponse>() {
//                @Override
//                public void onChanged(LoginResponse login) {
//                    try {
//                        dismissDialog();
//                        if (login != null) {
//                            if (!login.getStatus().toLowerCase().equals("error")) {
//                                Utils.setStrAuth(login.getData().getJwtToken());
//                                objMyApplication.setStrEmail(login.getData().getEmail());
//                                //                            objMyApplication.setUserId(login.getData().getUserId());
//                                objMyApplication.setLoginUserId(login.getData().getUserId());
//                                objMyApplication.setLoginResponse(login);
//                                Utils.setUserEmail(LoginActivity.this, login.getData().getEmail());
//                                objMyApplication.setBiometric(login.getData().getBiometricEnabled());
//                                getStatesUrl(login.getData().getStateList().getUS());
//                                objMyApplication.setAccountType(login.getData().getAccountType());
//                                objMyApplication.setDbaOwnerId(login.getData().getDbaOwnerId());
//                                objMyApplication.setIsReserveEnabled(login.getData().isReserveEnabled());
//                                if (login.getData() != null) {
//                                    objMyApplication.setBusinessUserID(String.valueOf(login.getData().getBusinessUserId()));
//                                    objMyApplication.setOwnerImage(login.getData().getOwnerImage());
//                                }
////                                if (!strFCMToken.equals("")) {
////                                    loginViewModel.initializeDevice(strFCMToken);
////                                }
//                                if (login.getData().getPasswordExpired()) {
//                                    isExpiry = true;
//                                    Intent i = new Intent(LoginActivity.this, PINActivity.class);
//                                    i.putExtra("screen", "loginExpiry");
//                                    i.putExtra("TYPE", "ENTER");
//                                    startActivity(i);
//                                } else {
//                                    if (chkRemember.isChecked()) {
//                                        saveCredentials();
//                                    } else {
////                                        mydatabase.execSQL("Delete from tblRemember");
//                                        dbHandler.clearTableRemember();
//                                    }
//                                    saveFirstUser();
//                                    if (login.getData().getCoyniPin()) {
//                                        Intent i = new Intent(LoginActivity.this, PINActivity.class);
//                                        i.putExtra("TYPE", "ENTER");
//                                        i.putExtra("screen", "login");
//                                        i.putExtra(Utils.ACCOUNT_TYPE, login.getData().getAccountType());
//                                        startActivity(i);
//                                    } else {
//                                        loginResponse = login;
//                                        SMSResend resend = new SMSResend();
//                                        resend.setCountryCode(Utils.getStrCCode());
//                                        resend.setPhoneNumber(login.getData().getPhoneNumber());
//                                        loginViewModel.smsotpresend(resend);
//
//                                    }
//                                }
//                            } else {
//                                if (login.getData() != null) {
//                                    if (!login.getData().getMessage().equals("") && login.getData().getPasswordFailedAttempts() > 0) {
//                                        Utils.emailPasswordIncorrectDialog("", LoginActivity.this, "");
//                                    }
//                                } else {
//                                    Utils.displayAlert(login.getError().getErrorDescription(), LoginActivity.this, "", login.getError().getFieldErrors().get(0));
//                                }
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });

            loginViewModel.getLoginNewLiveData().observe(this, new Observer<BiometricSignIn>() {
                @Override
                public void onChanged(BiometricSignIn login) {
                    try {
                        dismissDialog();
                        if (login != null) {
                            if (!login.getStatus().toLowerCase().equals("error")) {
                                Utils.setStrAuth(login.getData().getJwtToken());
                                objMyApplication.setStrEmail(login.getData().getEmail());
                                try {
                                    if (login.getData().getDbaName() != null && !login.getData().getDbaName().equals(""))
                                        objMyApplication.setStrDBAName(login.getData().getDbaName());

                                    if (login.getData().getFirstName() != null && !login.getData().getFirstName().equals("") &&
                                            login.getData().getLastName() != null && !login.getData().getLastName().equals(""))
                                        objMyApplication.setStrUserName(Utils.capitalize(login.getData().getFirstName() + " " + login.getData().getLastName()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //                            objMyApplication.setUserId(login.getData().getUserId());
                                objMyApplication.setLoginUserId(Integer.parseInt(String.valueOf(login.getData().getUserId())));
                                objMyApplication.setLoginResponse(login);
                                Utils.setUserEmail(LoginActivity.this, login.getData().getEmail());
                                objMyApplication.setBiometric(login.getData().isBiometricEnabled());
//                                getStatesUrl(login.getData().getStateList().getUS());
                                objMyApplication.setAccountType(login.getData().getAccountType());
//                                objMyApplication.setDbaOwnerId(Integer.parseInt(String.valueOf(login.getData().getDbaOwnerId())));
//                                objMyApplication.setIsReserveEnabled(login.getData().isReserveEnabled());
//                                if (login.getData() != null) {
//                                    objMyApplication.setBusinessUserID(String.valueOf(login.getData().getBusinessUserId()));
//                                    objMyApplication.setOwnerImage(login.getData().getOwnerImage());
//                                }

//                                if (!strFCMToken.equals("")) {
//                                    loginViewModel.initializeDevice(strFCMToken);
//                                }
                                if (login.getData().isPasswordExpired()) {
                                    isExpiry = true;
                                    Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                    i.putExtra("screen", "loginExpiry");
                                    i.putExtra("code", login.getData().getRequestToken());
                                    i.putExtra("TYPE", "ENTER");
                                    startActivity(i);
                                } else {
                                    if (chkRemember.isChecked()) {
                                        saveCredentials();
                                    } else {
//                                        mydatabase.execSQL("Delete from tblRemember");
                                        dbHandler.clearTableRemember();
                                    }
                                    saveFirstUser();
                                    if (login.getData().isCoyniPin()) {
                                        Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                        i.putExtra("TYPE", "ENTER");
                                        i.putExtra("screen", "login");
                                        i.putExtra(Utils.ACCOUNT_TYPE, login.getData().getAccountType());
                                        startActivity(i);
                                    } else {
                                        if (Utils.isKeyboardVisible)
                                            Utils.hideKeypad(LoginActivity.this);
                                        loginResponse = login;
                                        SMSResend resend = new SMSResend();
                                        resend.setCountryCode(Utils.getStrCCode());
                                        resend.setPhoneNumber(login.getData().getPhoneNumber());
                                        loginViewModel.smsotpresend(resend);
//                                        loginViewModel.smsOTPResend();
                                    }
                                }
                            } else {
                                if (login.getData() != null) {
                                    if (!login.getData().getMessage().equals("") && login.getData().getPasswordFailedAttempts() > 0) {
                                        Utils.emailPasswordIncorrectDialog("", LoginActivity.this, "");
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
                    dismissDialog();
                    if (apiError != null) {
//                        Utils.emailPasswordIncorrectDialog("", LoginActivity.this, "");
                        Utils.displayAlert(apiError.getError().getErrorDescription(), LoginActivity.this, "", apiError.getError().getFieldErrors().get(0));
                    }
                }
            });

            loginViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricSignIn>() {
                @Override
                public void onChanged(BiometricSignIn loginResponse) {
                    dismissDialog();
                    try {
                        if (loginResponse != null) {
                            if (!loginResponse.getStatus().toLowerCase().equals("error")) {
                                Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                objMyApplication.setStrEmail(loginResponse.getData().getEmail());

                                try {
                                    if (loginResponse.getData().getDbaName() != null && !loginResponse.getData().getDbaName().equals(""))
                                        objMyApplication.setStrDBAName(loginResponse.getData().getDbaName());

                                    if (loginResponse.getData().getFirstName() != null && !loginResponse.getData().getFirstName().equals("") &&
                                            loginResponse.getData().getLastName() != null && !loginResponse.getData().getLastName().equals(""))
                                        objMyApplication.setStrUserName(Utils.capitalize(loginResponse.getData().getFirstName() + " " + loginResponse.getData().getLastName()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                objMyApplication.setLoginUserId(Integer.parseInt(String.valueOf(loginResponse.getData().getUserId())));
                                objMyApplication.setLoginResponse(loginResponse);
                                Utils.setUserEmail(LoginActivity.this, loginResponse.getData().getEmail());
                                objMyApplication.setBiometric(loginResponse.getData().isBiometricEnabled());
                                getStatesUrl(loginResponse.getData().getStateList().getUS());
                                objMyApplication.setAccountType(loginResponse.getData().getAccountType());
                                objMyApplication.setDbaOwnerId(Integer.parseInt(String.valueOf(loginResponse.getData().getDbaOwnerId())));
                                if (loginResponse.getData().isPasswordExpired()) {
//                                    Intent i = new Intent(LoginActivity.this, PINActivity.class);
//                                    i.putExtra("screen", "loginExpiry");
//                                    i.putExtra("TYPE", "ENTER");
//                                    i.putExtra(Utils.ACCOUNT_TYPE, loginResponse.getData().getAccountType());
//                                    startActivity(i);
                                    Intent i = new Intent(LoginActivity.this, CreatePasswordActivity.class);
                                    i.putExtra("screen", "loginExpiry");
                                    i.putExtra("code", loginResponse.getData().getRequestToken());
                                    startActivity(i);
                                } else {
                                    Utils.setStrAuth(loginResponse.getData().getJwtToken());
                                    objMyApplication.setIsLoggedIn(true);
                                    if (!loginResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())
                                            && !loginResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                                            && !loginResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {

                                        if (loginResponse.getData().getAccountType() == Utils.SHARED_ACCOUNT) {
                                            if (loginResponse.getData().getOwnerDetails() != null && !loginResponse.getData().getOwnerDetails().getTracker().isIsAgreementSigned()) {
                                                showProgressDialog();
                                                callHasToSignAPI(true);
                                            } else {
                                                launchDashboard();
                                            }
                                        } else {
                                            if (!loginResponse.getData().getTracker().isIsAgreementSigned()) {
                                                showProgressDialog();
//                                                if (loginResponse.getData().getBusinessTracker() == null || loginResponse.getData().getBusinessTracker().isIsAgreementSigned())
//                                                    callHasToSignAPI(false);
//                                                else if (!loginResponse.getData().getBusinessTracker().isIsAgreementSigned()) {
//                                                    callHasToSignAPI(true);
//                                                }
                                                if (loginResponse.getData().getBusinessTracker() == null || !loginResponse.getData().getBusinessTracker().isIsAgreementSigned())
                                                    callHasToSignAPI(true);
                                                else if (loginResponse.getData().getBusinessTracker().isIsAgreementSigned()) {
                                                    callHasToSignAPI(false);
                                                }
                                            } else {
                                                launchDashboard();
                                            }
                                        }
                                    } else
                                        launchDashboard();
                                }
                            } else {
                                if (loginResponse.getData() != null) {
                                    if (!loginResponse.getData().getMessage().equals("") && loginResponse.getData().getPasswordFailedAttempts() > 0) {
                                        Utils.emailPasswordIncorrectDialog("", LoginActivity.this, "");
                                    }
                                } else {
                                    if (loginResponse.getError().getErrorDescription().equalsIgnoreCase("invalid.Permanent.Token")) {
                                        Utils.displayAlert(getString(R.string.bio_token_expired), LoginActivity.this, "", loginResponse.getError().getFieldErrors().get(0));
                                    } else {
                                        Utils.displayAlert(loginResponse.getError().getErrorDescription(), LoginActivity.this, "", loginResponse.getError().getFieldErrors().get(0));
                                    }
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
                    dismissDialog();
                    if (smsResponse != null) {
                        if (smsResponse.getStatus().toLowerCase().toString().equals("success")) {
                            Intent i = new Intent(LoginActivity.this, OTPValidation.class);
                            i.putExtra("screen", "login_SET_PIN");
                            i.putExtra("OTP_TYPE", "MOBILE");
                            i.putExtra("MOBILE", loginResponse.getData().getPhoneNumber());
                            i.putExtra("EMAIL", loginResponse.getData().getEmail());
                            i.putExtra("screenName", "login");
                            i.putExtra("MASK_MOBILE", Utils.convertToUSFormat(loginResponse.getData().getPhoneNumber()));
                            startActivity(i);
                        } else {
                            Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", LoginActivity.this, "Error", "");
                        }
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private void login() {
        try {
            showProgressDialog();
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(strEmail);
            loginRequest.setPassword(strPwd);
            loginViewModel.loginNew(loginRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void biometricLogin() {
        try {
            showProgressDialog();
            BiometricLoginRequest request = new BiometricLoginRequest();
            request.setDeviceId(Utils.getDeviceID());
            request.setEnableBiometic(true);
            request.setMobileToken(strToken);
            loginViewModel.biometricLogin(request, objMyApplication);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveCredentials() {
        try {
            if (strEmail.equals("")) {
                strEmail = etEmail.getText().toString().trim().toLowerCase();
            }
            dbHandler.clearTableRemember();
            dbHandler.insertTableRemember(strEmail.toLowerCase());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFirstUser() {
        try {
            if (strFirstUser.equals("")) {
                strFirstUser = etEmail.getText().toString().trim().toLowerCase();
            }
//            mydatabase.execSQL("Delete from tblUserDetails");
//            mydatabase.execSQL("INSERT INTO tblUserDetails(id,email) VALUES(null,'" + strFirstUser.toLowerCase() + "')");
            dbHandler.clearTableUserDetails();
            dbHandler.insertTableUserDetails(strFirstUser.toLowerCase());
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
        Utils.isKeyboardVisible = visible;
        Log.e("keyboard", Utils.isKeyboardVisible + "");
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

    public void hideAndClearFocus() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etlEmail.setBoxStrokeColor(getResources().getColor(R.color.primary_green));

                etEmail.clearFocus();
                etPassword.clearFocus();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(LoginActivity.this);
            }

        }, 500);

    }

    private void clearEmailControl() {
        try {
            etEmail.setText("");
            Utils.setUpperHintColor(etlEmail, getColor(R.color.light_gray));
            etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearPwdControl() {
        try {
            etPassword.setText("");
            etPassword.setHint("");
            Utils.setUpperHintColor(etlPassword, getColor(R.color.light_gray));
            etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState(LoginActivity.this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideAndClearFocus();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("new Config", newConfig.toString());
    }

}
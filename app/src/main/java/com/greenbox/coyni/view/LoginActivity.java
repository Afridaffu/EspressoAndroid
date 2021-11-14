package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdNotAvailable_BottomSheet;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements OnKeyboardVisibilityListener {
    TextInputLayout etlEmail, etlPassword;
    TextInputEditText etEmail, etPassword;
    TextView faceidNotAvail;
    CardView cvNext, cvEmailOK;
    LinearLayout layoutEmailError, layoutPwdError;
    TextView tvEmailError, tvPwdError, forgotpwd, tvRetEmail;
    String strEmail = "", strPwd = "", strMsg = "", strToken = "";
    ProgressDialog dialog;
    LoginViewModel loginViewModel;
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails, dsFacePin, dsRemember, dsPermanentToken, dsTouchID;
    Boolean isFaceLock = false, isThumb = false, isTouchId = false;
    ImageView loginBGIV, endIconIV;
    CheckBox chkRemember;
    MyApplication objMyApplication;
    LinearLayout layoutClose;
    RelativeLayout layoutMain;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_login);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null) {
            dialog.dismiss();
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

            if (objMyApplication.getStrRetrEmail() != null && !objMyApplication.getStrRetrEmail().equals("")) {
                etEmail.setText(objMyApplication.getStrRetrEmail());
            }

            endIconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FaceIdNotAvailable_BottomSheet faceIdNotAvailable_bottomSheet = new FaceIdNotAvailable_BottomSheet();
                    faceIdNotAvailable_bottomSheet.show(getSupportFragmentManager(), faceIdNotAvailable_bottomSheet.getTag());
                }
            });

            etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
//                        if (emailValidation() && passwordValidation()) {
//                            cvNext.setEnabled(true);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
//                        } else {
//                            cvNext.setEnabled(false);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }
//                        if (!hasFocus && etEmail.getText().toString().trim().equals("")) {
//                            emailValidation();
//                        }

                        if (!hasFocus) {
                            if (etEmail.getText().toString().trim().length() > 0 && !Utils.isValidEmail(etEmail.getText().toString().trim())) {
                                etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                                layoutEmailError.setVisibility(VISIBLE);
                                tvEmailError.setText("Invalid Email");
                            } else if (etEmail.getText().toString().trim().length() > 0 && Utils.isValidEmail(etEmail.getText().toString().trim())) {
                                etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_black));
                                layoutEmailError.setVisibility(GONE);
                            } else {
                                etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                                layoutEmailError.setVisibility(VISIBLE);
                                tvEmailError.setText("Field Required");
                            }
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
////                        if (emailValidation() && passwordValidation()) {
////                            cvNext.setEnabled(true);
////                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
////                        } else {
////                            cvNext.setEnabled(false);
////                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
////                        }
//                        if (!hasFocus && etPassword.getText().toString().trim().equals("")) {
//                            passwordValidation();
//                        }
                        if (!hasFocus) {
                            if (etPassword.getText().toString().trim().length() < 8 && etPassword.getText().toString().trim().length() > 0 ) {
                                etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
                                layoutPwdError.setVisibility(VISIBLE);
                                tvPwdError.setText("Invalid Password");
                            } else if (etPassword.getText().toString().trim().length() >= 8) {
                                etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_black));
                                layoutPwdError.setVisibility(GONE);
                            } else {
                                etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
                                layoutPwdError.setVisibility(VISIBLE);
                                tvPwdError.setText("Field Required");
                            }
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
//                        if (emailValidation() && passwordValidation()) {
//                            cvNext.setEnabled(true);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
//                        } else if (emailValidation()) {
//                            etlEmail.setErrorEnabled(false);
//                            etlEmail.setError("");
//                            layoutEmailError.setVisibility(View.GONE);
//                        } else {
//                            cvNext.setEnabled(false);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }
//                        if (s.toString().length() == 0) {
//                            etlEmail.setErrorEnabled(false);
//                            etlEmail.setError("");
//                            layoutEmailError.setVisibility(View.GONE);
//                            cvNext.setEnabled(false);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }

                        if (Utils.isValidEmail(etEmail.getText().toString().trim()) && etPassword.getText().toString().length() >= 8) {
                            cvNext.setEnabled(true);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        } else {
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
//                        if (emailValidation() && passwordValidation()) {
//                            cvNext.setEnabled(true);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
//                        } else if (passwordValidation()) {
//                            etlPassword.setErrorEnabled(false);
//                            etlPassword.setError("");
//                            layoutPwdError.setVisibility(View.GONE);
//                        } else {
//                            cvNext.setEnabled(false);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }
//                        if (s.toString().length() == 0) {
//                            etlPassword.setErrorEnabled(false);
//                            etlPassword.setError("");
//                            layoutPwdError.setVisibility(View.GONE);
//                            cvNext.setEnabled(false);
//                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }

                        if (Utils.isValidEmail(etEmail.getText().toString().trim()) && etPassword.getText().toString().length() >= 8) {
                            cvNext.setEnabled(true);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                            layoutEmailError.setVisibility(GONE);
                            layoutPwdError.setVisibility(GONE);
                        } else {
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 100000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.hideKeypad(LoginActivity.this, v);
                        if (Utils.checkInternet(LoginActivity.this)) {
                            strEmail = etEmail.getText().toString().trim().toLowerCase();
                            strPwd = etPassword.getText().toString().trim();
                            if (chkRemember.isChecked()) {
                                saveCredentials();
                            } else {
                                mydatabase.execSQL("Delete from tblRemember");
                            }
                            login();
                        } else {
                            Utils.displayAlert(getString(R.string.internet), LoginActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            chkRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        if (isChecked) {
                            if (!etEmail.getText().toString().trim().equals("") && !etPassword.getText().toString().trim().equals("")) {
                                saveCredentials();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            layoutMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Utils.hideKeypad(LoginActivity.this);
                    }
                    return false;
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
            if (dsUserDetails.getCount() > 0 && Utils.checkAuthentication(LoginActivity.this)) {
                strEmail = dsUserDetails.getString(1);
                strPwd = dsUserDetails.getString(2);
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
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
                etPassword.setText(dsRemember.getString(2));
                chkRemember.setChecked(true);
            } else {
                chkRemember.setChecked(false);
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblRemember;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblRemember(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
            }
        }
    }

    private void initObserver() {
        loginViewModel.getLoginLiveData().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse login) {
                try {
                    dialog.dismiss();
                    mLastClickTime = 0;
                    if (login != null) {
                        if (!login.getStatus().toLowerCase().equals("error")) {
                            Utils.setStrAuth(login.getData().getJwtToken());
                            if (login.getData().getPasswordExpired()) {
                                Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                i.putExtra("screen", "loginExpiry");
                                i.putExtra("TYPE", "ENTER");
                                startActivity(i);
                            } else {
                                if (login.getData().getCoyniPin()) {
                                    Intent i = new Intent(LoginActivity.this, PINActivity.class);
                                    i.putExtra("TYPE", "ENTER");
                                    i.putExtra("screen", "login");
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(LoginActivity.this, OTPValidation.class);
                                    i.putExtra("screen", "Login");
                                    i.putExtra("OTP_TYPE", "MOBILE");
                                    i.putExtra("MOBILE", login.getData().getPhoneNumber());
                                    i.putExtra("EMAIL", login.getData().getEmail());
                                    i.putExtra("MASK_MOBILE", Utils.convertToUSFormat(login.getData().getPhoneNumber()));
                                    startActivity(i);
                                }
                            }
                        } else {
                            if (login.getData() != null) {
                                if (!login.getData().getMessage().equals("") && login.getData().getPasswordFailedAttempts() > 0) {
                                    Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                                    emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());
                                }
                            } else {
                                Utils.displayAlert(login.getError().getErrorDescription(), LoginActivity.this);
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
                mLastClickTime = 0;
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

    private void saveCredentials() {
        try {
            if (strEmail.equals("") && strPwd.equals("")) {
                strEmail = etEmail.getText().toString().trim().toLowerCase();
                strPwd = etPassword.getText().toString().trim();
            }
            mydatabase.execSQL("Delete from tblRemember");
            mydatabase.execSQL("INSERT INTO tblRemember(id,username,password) VALUES(null,'" + strEmail.toLowerCase() + "','" + strPwd + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableIcon() {
        try {
            if (Utils.getIsTouchEnabled()) {
                etlPassword.setPasswordVisibilityToggleEnabled(false);
                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_id));
                strMsg = "Do you want to register with Thumb/Pin.";
            } else if (Utils.getIsFaceEnabled()) {
                etlPassword.setPasswordVisibilityToggleEnabled(false);
                endIconIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_faceid));
                strMsg = "Do you want to register with FaceID/Pin.";
            } else {
                etlPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
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
}
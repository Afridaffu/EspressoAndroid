package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordRequest;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordResponse;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.regex.Pattern;

public class CreatePasswordActivity extends AppCompatActivity {
    ImageView imgClose;
    CardView cvSave, cvLogin;
    LinearLayout layoutNewPassword;
    RelativeLayout layoutDone;
    TextInputLayout etlPassword, etlCPassword;
    TextInputEditText etPassword, etCPassword;
    TextView tvPasswordInfo, tvHead, tvMessage, tvchangepass;
    TextInputLayout passwordTIL, confPasswordTIL, currentPass;
    TextInputEditText passwordET, confirmPasswordET, currentPassET;
    LinearLayout layoutIndicator;
    ProgressDialog dialog;
    LinearLayout mismatchErrorTV;
    private Pattern strong, medium;
    Boolean isPassword = false, isConfirm = false, isPwdEye = false, isCPwdEye = false, isOldPwd = true;
    //    !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~]).{8,})";

    private static final String MEDIUM_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~]).{6,})";

    View strengthOne, strengthTwo, strengthThree;
    String strCode = "", strNewPwd = "";
    LoginViewModel loginViewModel;
    DashboardViewModel dashboardViewModel;
    LinearLayout layoutMain;
    boolean isSuccessLayout = false;
    String strScreen = "", oldPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_password);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            passwordET.requestFocus();
            isPwdEye = false;
            isCPwdEye = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            imgClose = findViewById(R.id.imgClose);
            cvSave = findViewById(R.id.cvSave);
            cvLogin = findViewById(R.id.cvLogin);
            passwordTIL = findViewById(R.id.etlPassword);
            confPasswordTIL = findViewById(R.id.etlCPassword);
            passwordET = findViewById(R.id.etPassword);
            currentPassET = findViewById(R.id.currentPassET);
            currentPass = findViewById(R.id.currentPassTIL);
            confirmPasswordET = findViewById(R.id.etCPassword);
            tvPasswordInfo = findViewById(R.id.tvPasswordInfo);
            strengthOne = findViewById(R.id.strengthOne);
            strengthTwo = findViewById(R.id.strengthTwo);
            mismatchErrorTV = findViewById(R.id.oldnewMatchTV);
            strengthThree = findViewById(R.id.strengthThree);
            layoutNewPassword = findViewById(R.id.layoutNewPassword);
            layoutDone = findViewById(R.id.layoutDone);
            layoutMain = findViewById(R.id.layoutMain);
            layoutIndicator = findViewById(R.id.layoutIndicator);
            tvchangepass = findViewById(R.id.tvMessageChangePass);
            tvHead = findViewById(R.id.tvHead);
            tvMessage = findViewById(R.id.tvMessage);
            strong = Pattern.compile(STRONG_PATTERN);
            medium = Pattern.compile(MEDIUM_PATTERN);
            cvSave.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            Utils.statusBar(CreatePasswordActivity.this, "#FFFFFF");

            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            currentPass.setBoxStrokeColorStateList(Utils.getNormalColorState());

            strScreen = getIntent().getStringExtra("screen");
            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("loginExpiry")) {
                tvMessage.setVisibility(VISIBLE);
                tvHead.setText("Welcome Back!");
            } else if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ConfirmPassword")) {
                tvHead.setText("Change Password");
                tvchangepass.setVisibility(VISIBLE);
                tvMessage.setVisibility(GONE);
            } else {
                tvMessage.setVisibility(View.GONE);
                tvHead.setText("Create New Password");
                tvchangepass.setVisibility(GONE);
            }
            if (getIntent().getStringExtra("code") != null && !getIntent().getStringExtra("code").equals("")) {
                strCode = getIntent().getStringExtra("code");
            }

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });

            passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        try {
                            passwordET.setHint("8-12 Characters");
                            layoutIndicator.setVisibility(VISIBLE);
                            passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
//                            if ((getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ConfirmPassword"))
//                                    || (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ForgotPwd"))) {
//                                passwordTIL.setHint("New Password");
//                            } else {
//                                passwordTIL.setHint("Password");
//                            }
                            passwordTIL.setHint("New Password");
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        passwordET.setHint("");
                        layoutIndicator.setVisibility(GONE);

//                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
//                        } else {
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
//                        }
//
//                        if (passwordET.getText().toString().trim().length() == 0 || !strong.matcher(passwordET.getText().toString().trim()).matches()) {
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
//                        } else {
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
//                        }
                        if (passwordET.getText().toString().trim().length() == 0) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.light_gray));
                        } else if (!strong.matcher(passwordET.getText().toString().trim()).matches()) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                        } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                        } else if (passwordET.getText().toString().length() > 0 && confirmPasswordET.getText().toString().length() > 0 && !passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
//                            passwordTIL.setHint("Password doesn’t match");
                            confPasswordTIL.setHint("Password doesn’t match");
                        } else {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                        }
                    }
                }
            });

            confirmPasswordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        confirmPasswordET.setHint("Confirm Password");
                        confPasswordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                        confPasswordTIL.setHint("Confirm Password");
                    } else {
                        confirmPasswordET.setHint("");
//                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
//                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
//                        } else {
//                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
//                        }
                        if (confirmPasswordET.getText().toString().trim().length() == 0) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));
                        } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                        } else if (passwordET.getText().toString().length() > 0 && confirmPasswordET.getText().toString().length() > 0 && !passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
//                            passwordTIL.setHint("Password doesn’t match");
                            confPasswordTIL.setHint("Password doesn’t match");
                        } else {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                        }
                    }

                }
            });

            passwordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    try {
                        if (getCurrentFocus().getId() == passwordET.getId()) {

                            if (strong.matcher(charSequence).matches()) {
                                strengthOne.setVisibility(VISIBLE);
                                strengthTwo.setVisibility(VISIBLE);
                                strengthThree.setVisibility(VISIBLE);
                                strengthOne.setBackgroundColor(getResources().getColor(R.color.primary_color));
                                strengthTwo.setBackgroundColor(getResources().getColor(R.color.primary_color));
                                strengthThree.setBackgroundColor(getResources().getColor(R.color.primary_color));
                                tvPasswordInfo.setVisibility(GONE);
                            } else if (medium.matcher(charSequence).matches()) {
                                strengthOne.setVisibility(VISIBLE);
                                strengthTwo.setVisibility(VISIBLE);
                                strengthThree.setVisibility(INVISIBLE);
                                strengthOne.setBackgroundColor(getResources().getColor(R.color.orange));
                                strengthTwo.setBackgroundColor(getResources().getColor(R.color.orange));
                                tvPasswordInfo.setVisibility(VISIBLE);
                                tvPasswordInfo.setTextColor(getResources().getColor(R.color.error_red));

                            } else {
                                strengthOne.setVisibility(VISIBLE);
                                strengthTwo.setVisibility(INVISIBLE);
                                strengthThree.setVisibility(INVISIBLE);
                                strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                                tvPasswordInfo.setVisibility(VISIBLE);
                                tvPasswordInfo.setTextColor(getResources().getColor(R.color.error_red));
                            }

                            if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
                                isPassword = true;
                            } else {
                                isPassword = false;
                            }
                            if (passwordET.getText().toString().length() == 0) {
                                passwordTIL.setHint("New Password");
                                confPasswordTIL.setHint("Confirm Password");

                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                if (confirmPasswordET.getText().toString().trim().length() == 0)
                                    Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));
                                else
                                    Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                            } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                isConfirm = true;

                                passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                passwordTIL.setHint("New Password");
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                confPasswordTIL.setHint("Confirm Password");
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                            } else {
                                isConfirm = false;

                                if (confirmPasswordET.getText().toString().trim().length() > 0) {
                                    passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                    //                                passwordTIL.setHint("Password doesn’t match");
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                                    confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                    confPasswordTIL.setHint("Password doesn’t match");
                                    Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                                } else if (confirmPasswordET.getText().toString().trim().length() == 0) {
                                    confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    confPasswordTIL.setHint("Confirm Password");
                                    Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));

                                    passwordTIL.setHint("New Password");
                                }
                            }
                            enableOrDisableNext();
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            passwordET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            passwordET.setText(s.toString().trim());
                            passwordET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confirmPasswordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    try {
                        if (getCurrentFocus().getId() == confirmPasswordET.getId()) {

                            if (confirmPasswordET.getText().toString().length() == 0) {
                                passwordTIL.setHint("New Password");
                                confPasswordTIL.setHint("Confirm Password");
                                if (strong.matcher(passwordET.getText().toString().trim()).matches()) {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                                } else {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                                }
                            } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                isConfirm = true;

                                passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                passwordTIL.setHint("New Password");
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                                confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                confPasswordTIL.setHint("Confirm Password");
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));

                            } else {
                                isConfirm = false;

                                //                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                //                            passwordTIL.setHint("Password doesn’t match");
                                //                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));

                                confPasswordTIL.setHint("Password doesn’t match");

                            }
                            enableOrDisableNext();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            confirmPasswordET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            confirmPasswordET.setText(s.toString().trim());
                            confirmPasswordET.setSelection(s.toString().trim().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            passwordTIL.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isPwdEye) {
                            isPwdEye = true;
                            passwordTIL.setEndIconDrawable(R.drawable.ic_eyeopen);
                            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isPwdEye = false;
                            passwordTIL.setEndIconDrawable(R.drawable.ic_eyeclose);
                            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        if (passwordET.getText().length() > 0) {
                            passwordET.setSelection(passwordET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confPasswordTIL.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isCPwdEye) {
                            isCPwdEye = true;
                            confPasswordTIL.setEndIconDrawable(R.drawable.ic_eyeopen);
                            confirmPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isCPwdEye = false;
                            confPasswordTIL.setEndIconDrawable(R.drawable.ic_eyeclose);
                            confirmPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        if (confirmPasswordET.getText().length() > 0) {
                            confirmPasswordET.setSelection(confirmPasswordET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(CreatePasswordActivity.this, v);
                        dialog = new ProgressDialog(CreatePasswordActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        strNewPwd = passwordET.getText().toString().trim();
                        if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("loginExpiry")) {
                            ManagePasswordRequest request = new ManagePasswordRequest();
                            request.setPassword(passwordET.getText().toString().trim());
                            loginViewModel.setExpiryPassword(request);
                        } else if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ConfirmPassword")) {
//                            ManagePasswordRequest request = new ManagePasswordRequest();
//                            request.setPassword(etPassword.getText().toString().trim());
//                            loginViewModel.setExpiryPassword(request);
//                            dashboardViewModel.se;
                            ChangePasswordRequest request = new ChangePasswordRequest();
                            request.setOldPassword(getIntent().getStringExtra("oldpassword"));
                            request.setNewPassword(confirmPasswordET.getText().toString().trim());
                            dashboardViewModel.meChangePassword(request);

                        } else {
                            SetPassword setPassword = new SetPassword();
                            setPassword.setCode(strCode);
                            setPassword.setPassword(passwordET.getText().toString().trim());
                            loginViewModel.setPassword(setPassword);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreatePasswordActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

//            layoutMain.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        Utils.hideKeypad(CreatePasswordActivity.this);
//                    }
//                    return false;
//                }
//            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (getIntent().getStringExtra("screen").equals("ConfirmPassword")) {
                oldPassword = getIntent().getStringExtra("oldpassword");
                currentPass.setVisibility(VISIBLE);
                currentPassET.setText(oldPassword);
                currentPass.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!isOldPwd) {
                                isOldPwd = true;
                                currentPass.setEndIconDrawable(R.drawable.ic_eyeopen);
                                currentPassET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            } else {
                                isOldPwd = false;
                                currentPass.setEndIconDrawable(R.drawable.ic_eyeclose);
                                currentPassET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            }
                            if (passwordET.getText().length() > 0) {
                                currentPassET.setSelection(currentPassET.getText().length());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                //changes
                passwordET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        try {
                            if (getCurrentFocus().getId() == passwordET.getId()) {

                                if (strong.matcher(charSequence).matches()) {
                                    strengthOne.setVisibility(VISIBLE);
                                    strengthTwo.setVisibility(VISIBLE);
                                    strengthThree.setVisibility(VISIBLE);
                                    strengthOne.setBackgroundColor(getResources().getColor(R.color.primary_color));
                                    strengthTwo.setBackgroundColor(getResources().getColor(R.color.primary_color));
                                    strengthThree.setBackgroundColor(getResources().getColor(R.color.primary_color));
                                    tvPasswordInfo.setVisibility(GONE);
                                } else if (medium.matcher(charSequence).matches()) {
                                    strengthOne.setVisibility(VISIBLE);
                                    strengthTwo.setVisibility(VISIBLE);
                                    strengthThree.setVisibility(INVISIBLE);
                                    strengthOne.setBackgroundColor(getResources().getColor(R.color.orange));
                                    strengthTwo.setBackgroundColor(getResources().getColor(R.color.orange));
                                    tvPasswordInfo.setVisibility(VISIBLE);
                                    tvPasswordInfo.setTextColor(getResources().getColor(R.color.error_red));

                                } else {
                                    strengthOne.setVisibility(VISIBLE);
                                    strengthTwo.setVisibility(INVISIBLE);
                                    strengthThree.setVisibility(INVISIBLE);
                                    strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                                    tvPasswordInfo.setVisibility(VISIBLE);
                                    tvPasswordInfo.setTextColor(getResources().getColor(R.color.error_red));
                                }

                                if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
                                    if (passwordET.getText().toString().equals(getIntent().getStringExtra("oldpassword"))) {
                                        mismatchErrorTV.setVisibility(VISIBLE);
                                        isPassword = false;
                                    } else {
                                        isPassword = true;
                                        mismatchErrorTV.setVisibility(GONE);
                                    }
                                } else {
                                    isPassword = false;
                                    mismatchErrorTV.setVisibility(GONE);
                                }
                                if (passwordET.getText().toString().length() == 0) {
                                    passwordTIL.setHint("New Password");
                                    confPasswordTIL.setHint("Confirm Password");

                                    confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    if (confirmPasswordET.getText().toString().trim().length() == 0)
                                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));
                                    else
                                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                                } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                    isConfirm = true;

                                    passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                    passwordTIL.setHint("New Password");
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                                    confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    confPasswordTIL.setHint("Confirm Password");
                                    Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                                } else {
                                    isConfirm = false;

                                    if (confirmPasswordET.getText().toString().trim().length() > 0) {
                                        passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                        //                                passwordTIL.setHint("Password doesn’t match");
                                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                                        confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                        confPasswordTIL.setHint("Password doesn’t match");
                                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                                    } else if (confirmPasswordET.getText().toString().trim().length() == 0) {
                                        confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                        confPasswordTIL.setHint("Confirm Password");
                                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));

                                        passwordTIL.setHint("New Password");
                                    }
                                }
                                enableOrDisableNext();
                            }
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if (s.length() > 0 && s.toString().trim().length() == 0) {
                                passwordET.setText("");
                            } else if (s.length() > 0 && s.toString().contains(" ")) {
                                passwordET.setText(s.toString().trim());
                                passwordET.setSelection(s.toString().trim().length());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                confirmPasswordET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        try {
                            if (getCurrentFocus().getId() == confirmPasswordET.getId()) {

                                if (confirmPasswordET.getText().toString().length() == 0) {
                                    passwordTIL.setHint("New Password");
                                    confPasswordTIL.setHint("Confirm Password");
                                    if (strong.matcher(passwordET.getText().toString().trim()).matches()) {
                                        passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                                    } else {
                                        passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                        //                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                                    }
                                } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {

                                    if (passwordET.getText().toString().equals(getIntent().getStringExtra("oldpassword"))) {
                                        isConfirm = false;
                                        passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                        passwordTIL.setHint("New Password");
                                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));

                                        confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                        confPasswordTIL.setHint("Confirm Password");
                                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                                    } else if (strong.matcher(passwordET.getText().toString().trim()).matches()) {
                                        isConfirm = true;

                                        passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                        passwordTIL.setHint("New Password");
                                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                                        confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                        confPasswordTIL.setHint("Confirm Password");
                                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                                    } else {
                                        passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                        passwordTIL.setHint("New Password");
                                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                                    }

                                } else {
                                    isConfirm = false;

                                    //                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                    //                            passwordTIL.setHint("Password doesn’t match");
                                    //                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));

                                    confPasswordTIL.setHint("Password doesn’t match");

                                }
                                enableOrDisableNext();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if (s.length() > 0 && s.toString().trim().length() == 0) {
                                confirmPasswordET.setText("");
                            } else if (s.length() > 0 && s.toString().contains(" ")) {
                                confirmPasswordET.setText(s.toString().trim());
                                confirmPasswordET.setSelection(s.toString().trim().length());
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            try {
                                passwordET.setHint("8-12 Characters");
                                layoutIndicator.setVisibility(VISIBLE);
                                passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
                                //                            if ((getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ConfirmPassword"))
                                //                                    || (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ForgotPwd"))) {
                                //                                passwordTIL.setHint("New Password");
                                //                            } else {
                                //                                passwordTIL.setHint("Password");
                                //                            }
                                passwordTIL.setHint("New Password");
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            passwordET.setHint("");
                            layoutIndicator.setVisibility(GONE);

                            //                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            //                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            //                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                            //                        } else {
                            //                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            //                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                            //                        }
                            //
                            //                        if (passwordET.getText().toString().trim().length() == 0 || !strong.matcher(passwordET.getText().toString().trim()).matches()) {
                            //                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            //                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                            //                        } else {
                            //                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            //                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                            //                        }
                            if (passwordET.getText().toString().trim().length() == 0) {
                                passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.light_gray));
                            } else if (!strong.matcher(passwordET.getText().toString().trim()).matches()) {
                                passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                            } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                if (passwordET.getText().toString().equals(getIntent().getStringExtra("oldpassword"))) {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                                } else {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                                }
                            } else if (passwordET.getText().toString().length() > 0 && confirmPasswordET.getText().toString().length() > 0 && !passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                //                                passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                if (passwordET.getText().toString().equals(getIntent().getStringExtra("oldpassword"))) {
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                                } else {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                                }
                                //                            passwordTIL.setHint("Password doesn’t match");
                                confPasswordTIL.setHint("Password doesn’t match");
                            } else if (passwordET.getText().toString().equals(getIntent().getStringExtra("oldpassword"))) {
                                passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                            } else {
                                passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                            }
                        }
                    }
                });

                confirmPasswordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            confirmPasswordET.setHint("Confirm Password");
                            confPasswordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                            confPasswordTIL.setHint("Confirm Password");
                        } else {
                            confirmPasswordET.setHint("");
                            //                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            //                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            //                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                            //                        } else {
                            //                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            //                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                            //                        }
                            if (confirmPasswordET.getText().toString().trim().length() == 0) {
                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));
                            } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                            } else if (passwordET.getText().toString().length() > 0 && confirmPasswordET.getText().toString().length() > 0 && !passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                                //                            passwordTIL.setHint("Password doesn’t match");
                                confPasswordTIL.setHint("Password doesn’t match");
                            } else {
                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                            }
                        }

                    }
                });
            } else {
                currentPass.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getSetpwdLiveData().observe(this, new Observer<SetPasswordResponse>() {
            @Override
            public void onChanged(SetPasswordResponse login) {
                dialog.dismiss();
                if (login != null) {
                    if (login.getStatus().toLowerCase().equals("success")) {
                        layoutNewPassword.setVisibility(View.GONE);
                        layoutDone.setVisibility(VISIBLE);
                        isSuccessLayout = true;
                    }
                }
            }
        });

        loginViewModel.getManagePasswordResponseMutableLiveData().observe(this, new Observer<ManagePasswordResponse>() {
            @Override
            public void onChanged(ManagePasswordResponse managePasswordResponse) {
                dialog.dismiss();
                if (managePasswordResponse != null) {
                    if (managePasswordResponse.getStatus().toLowerCase().equals("success")) {
                        layoutNewPassword.setVisibility(View.GONE);
                        layoutDone.setVisibility(VISIBLE);
                    }
                }
            }
        });
        dashboardViewModel.getChangePasswordMutableLiveData().observe(this, new Observer<ChangePassword>() {
            @Override
            public void onChanged(ChangePassword changePassword) {
                dialog.dismiss();
                if (changePassword != null) {
                    if (changePassword.getStatus().equals("SUCCESS")) {

                        startActivity(new Intent(CreatePasswordActivity.this, BindingLayoutActivity.class)
                                .putExtra("screen", "ChangePassword")
                        );


                    } else {
                        Utils.displayAlert(changePassword.getError().getErrorDescription(), CreatePasswordActivity.this, "", changePassword.getError().getFieldErrors().get(0));
                    }
                }


            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getStatus().equals("SUCCESS")) {
                        Utils.displayAlertNew(apiError.getError().getErrorDescription(), CreatePasswordActivity.this, "");
                    }
                }
            }
        });
    }

    public void enableOrDisableNext() {

        try {
            if (isConfirm && isPassword) {
                cvSave.setEnabled(true);
                cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
            } else {
                cvSave.setEnabled(false);
                cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (!isSuccessLayout) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
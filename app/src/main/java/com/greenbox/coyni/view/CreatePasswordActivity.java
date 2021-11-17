package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordRequest;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordResponse;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.regex.Pattern;

public class CreatePasswordActivity extends AppCompatActivity {
    ImageView imgClose;
    CardView cvSave, cvLogin;
    RelativeLayout layoutNewPassword, layoutDone;
    TextInputLayout passwordTIL, confPasswordTIL;
    TextInputEditText passwordET, confirmPasswordET;
    TextView tvPasswordInfo, tvHead, tvMessage;
    LinearLayout layoutIndicator;
    ProgressDialog dialog;
    private Pattern strong, medium;
    Boolean isPassword = false, isConfirm = false;
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})";

    private static final String MEDIUM_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,})";
    View strengthOne, strengthTwo, strengthThree;
    String strCode = "", strNewPwd = "";
    LoginViewModel loginViewModel;
    RelativeLayout layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_password);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            imgClose = findViewById(R.id.imgClose);
            cvSave = findViewById(R.id.cvSave);
            cvLogin = findViewById(R.id.cvLogin);
            passwordTIL = findViewById(R.id.etlPassword);
            confPasswordTIL = findViewById(R.id.etlCPassword);
            passwordET = findViewById(R.id.etPassword);
            confirmPasswordET = findViewById(R.id.etCPassword);
            tvPasswordInfo = findViewById(R.id.tvPasswordInfo);
            strengthOne = findViewById(R.id.strengthOne);
            strengthTwo = findViewById(R.id.strengthTwo);
            strengthThree = findViewById(R.id.strengthThree);
            layoutNewPassword = findViewById(R.id.layoutNewPassword);
            layoutDone = findViewById(R.id.layoutDone);
            layoutMain = findViewById(R.id.layoutMain);
            layoutIndicator = findViewById(R.id.layoutIndicator);
            tvHead = findViewById(R.id.tvHead);
            tvMessage = findViewById(R.id.tvMessage);
            strong = Pattern.compile(STRONG_PATTERN);
            medium = Pattern.compile(MEDIUM_PATTERN);
            cvSave.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            Utils.statusBar(CreatePasswordActivity.this, "#FFFFFF");
            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("loginExpiry")) {
                tvMessage.setVisibility(VISIBLE);
                tvHead.setText("Welcome Back!");
            } else {
                tvMessage.setVisibility(View.GONE);
                tvHead.setText("Create New Password");
            }
            if (getIntent().getStringExtra("code") != null && !getIntent().getStringExtra("code").equals("")) {
                strCode = getIntent().getStringExtra("code");
            }
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

//            etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
////                    if (hasFocus) {
////                        layoutIndicator.setVisibility(VISIBLE);
////                        //tvPasswordInfo.setVisibility(VISIBLE);
////                    } else {
////                        layoutIndicator.setVisibility(View.GONE);
////                        tvPasswordInfo.setVisibility(View.GONE);
////                    }
//
//                    if (hasFocus) {
//                        layoutIndicator.setVisibility(VISIBLE);
//                        etlPassword.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_green));
//                    } else {
//                        layoutIndicator.setVisibility(GONE);
//                        if (strong.matcher(etPassword.getText().toString()).matches()) {
//                            etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_black));
//                        } else {
//                            etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
//                        }
//                    }
//                }
//            });
//
//            etCPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    if (b) {
//                        etlCPassword.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(etlCPassword, getColor(R.color.primary_green));
//                    } else {
//                        if (etPassword.getText().toString().trim().equals(etCPassword.getText().toString().trim())) {
//                            etlCPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            Utils.setUpperHintColor(etlCPassword, getColor(R.color.primary_black));
//                        } else {
//                            etlCPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(etlCPassword, getColor(R.color.error_red));
//                        }
//                    }
//                }
//            });


            passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
                        layoutIndicator.setVisibility(VISIBLE);
                    } else {
                        layoutIndicator.setVisibility(GONE);
                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                        } else {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                        }
                    }
                }
            });



            confirmPasswordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        confPasswordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                    } else {
                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                        } else {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                        }
                    }

                }
            });



//            passwordET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                    try {
//                        if (strong.matcher(charSequence).matches()) {
//                            strengthOne.setVisibility(View.VISIBLE);
//                            strengthTwo.setVisibility(View.VISIBLE);
//                            strengthThree.setVisibility(View.VISIBLE);
//                            strengthOne.setBackgroundColor(getResources().getColor(R.color.primary_color));
//                            strengthTwo.setBackgroundColor(getResources().getColor(R.color.primary_color));
//                            strengthThree.setBackgroundColor(getResources().getColor(R.color.primary_color));
//                            tvPasswordInfo.setVisibility(View.GONE);
//                            isPassword = true;
//                        } else if (medium.matcher(charSequence).matches()) {
//                            strengthOne.setVisibility(View.VISIBLE);
//                            strengthTwo.setVisibility(View.VISIBLE);
//                            strengthThree.setVisibility(View.INVISIBLE);
//                            strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
//                            strengthTwo.setBackgroundColor(getResources().getColor(R.color.error_red));
//                            tvPasswordInfo.setVisibility(View.VISIBLE);
//                            isPassword = false;
//                            cvSave.setEnabled(false);
//                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        } else {
//                            strengthOne.setVisibility(View.VISIBLE);
//                            strengthTwo.setVisibility(View.INVISIBLE);
//                            strengthThree.setVisibility(View.INVISIBLE);
//                            strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
//                            tvPasswordInfo.setVisibility(VISIBLE);
//                            isPassword = false;
//                            cvSave.setEnabled(false);
//                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }
//
//                        if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
//                            isPassword = true;
//                        } else {
//                            isPassword = false;
//                        }
//                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
//                            isConfirm = true;
//                        } else {
//                            isConfirm = false;
//                        }
////                        enableOrDisableNext();
//
//                        if (isConfirm && isPassword) {
//                            cvSave.setEnabled(true);
//                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
//                        } else {
//                            cvSave.setEnabled(false);
//                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                        }
//
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//
//            confirmPasswordET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//
//                    if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
//                        isPassword = true;
//                    } else {
//                        isPassword = false;
//                    }
//                    if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
//                        isConfirm = true;
//                    } else {
//                        isConfirm = false;
//                    }
////                        enableOrDisableNext();
//
//                    if (isConfirm && isPassword) {
//                        cvSave.setEnabled(true);
//                        cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
//                    } else {
//                        cvSave.setEnabled(false);
//                        cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                    }
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try {
//
//                        if (s.length() > 0 && s.toString().trim().length() == 0) {
//                            confirmPasswordET.setText("");
//                        } else if (s.length() > 0 && s.toString().contains(" ")) {
//                            confirmPasswordET.setText(s.toString().trim());
//                            confirmPasswordET.setSelection(s.toString().trim().length());
//                        }
//
//                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
//                            isConfirm = true;
//
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            passwordTIL.setHint("New Password");
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
//
//                            confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
//                            confPasswordTIL.setHint("Confirm Password");
//                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
//
//                        } else {
//                            isConfirm = false;
//
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            passwordTIL.setHint("Password doesn’t match");
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
//
//                            confPasswordTIL.setHint("Password doesn’t match");
//
//                            if (passwordET.getText().toString().trim().length() == confirmPasswordET.getText().toString().trim().length()) {
//                                confirmPasswordET.clearFocus();
//                            }
//
//                        }
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });


            passwordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(getCurrentFocus().getId()==passwordET.getId()){

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
                            strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                            strengthTwo.setBackgroundColor(getResources().getColor(R.color.error_red));
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
                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            isConfirm = true;

                            passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            passwordTIL.setHint("Password");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            confPasswordTIL.setHint("Confirm Password");
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                        } else {
                            isConfirm = false;

                            if(confirmPasswordET.getText().toString().trim().length() > 0){
                                passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                passwordTIL.setHint("Password doesn’t match");
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                confPasswordTIL.setHint("Password doesn’t match");
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                            }else if(confirmPasswordET.getText().toString().trim().length() == 0){
                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                confPasswordTIL.setHint("Confirm Password");
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                                passwordTIL.setHint("Password");
                            }
                        }
                        enableOrDisableNext();
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

                    if(getCurrentFocus().getId()==confirmPasswordET.getId()){

                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            isConfirm = true;

                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            passwordTIL.setHint("Password");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                            confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            confPasswordTIL.setHint("Confirm Password");
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));

                            getCurrentFocus().getId();
                        } else {
                            isConfirm = false;

                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            passwordTIL.setHint("Password doesn’t match");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));

                            confPasswordTIL.setHint("Password doesn’t match");

                        }
                        enableOrDisableNext();
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
}
package com.greenbox.coyni.view;

import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.regex.Pattern;

public class CreatePasswordActivity extends AppCompatActivity {
    ImageView imgClose;
    CardView cvSave, cvLogin;
    RelativeLayout layoutNewPassword, layoutDone;
    TextInputLayout etlPassword, etlCPassword;
    TextInputEditText etPassword, etCPassword;
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
            etlPassword = findViewById(R.id.etlPassword);
            etlCPassword = findViewById(R.id.etlCPassword);
            etPassword = findViewById(R.id.etPassword);
            etCPassword = findViewById(R.id.etCPassword);
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
            etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        layoutIndicator.setVisibility(VISIBLE);
                        //tvPasswordInfo.setVisibility(VISIBLE);
                    } else {
                        layoutIndicator.setVisibility(View.GONE);
                        tvPasswordInfo.setVisibility(View.GONE);
                    }
                }
            });
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (strong.matcher(s).matches()) {
                            strengthOne.setVisibility(View.VISIBLE);
                            strengthTwo.setVisibility(View.VISIBLE);
                            strengthThree.setVisibility(View.VISIBLE);
                            strengthOne.setBackgroundColor(getResources().getColor(R.color.primary_color));
                            strengthTwo.setBackgroundColor(getResources().getColor(R.color.primary_color));
                            strengthThree.setBackgroundColor(getResources().getColor(R.color.primary_color));
                            tvPasswordInfo.setVisibility(View.GONE);
                            isPassword = true;
                        } else if (medium.matcher(s).matches()) {
                            strengthOne.setVisibility(View.VISIBLE);
                            strengthTwo.setVisibility(View.VISIBLE);
                            strengthThree.setVisibility(View.INVISIBLE);
                            strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                            strengthTwo.setBackgroundColor(getResources().getColor(R.color.error_red));
                            tvPasswordInfo.setVisibility(View.VISIBLE);
                            isPassword = false;
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        } else {
                            strengthOne.setVisibility(View.VISIBLE);
                            strengthTwo.setVisibility(View.INVISIBLE);
                            strengthThree.setVisibility(View.INVISIBLE);
                            strengthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                            tvPasswordInfo.setVisibility(VISIBLE);
                            isPassword = false;
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etCPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && etPassword.getText().toString().equals(etCPassword.getText().toString())) {
                            cvSave.setEnabled(true);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                            etlCPassword.setHint("Confirm Password");
                            etlPassword.setHint("New Password");
                            etlPassword.setErrorEnabled(false);
                            etlPassword.setError("");
                            etlCPassword.setErrorEnabled(false);
                            etlCPassword.setError("");
                        } else if (s.length() == 0) {
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                            etlCPassword.setHint("Confirm Password");
                            etlPassword.setHint("New Password");
                            etlPassword.setErrorEnabled(false);
                            etlPassword.setError("");
                            etlCPassword.setErrorEnabled(false);
                            etlCPassword.setError("");
                        } else if (!etPassword.getText().toString().trim().equals("")) {
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                            etlCPassword.setHint("Password doesn’t match");
                            etlPassword.setHint("Password doesn’t match");
                            etlPassword.setErrorEnabled(true);
                            etlPassword.setError(" ");
                            etlCPassword.setErrorEnabled(true);
                            etlCPassword.setError(" ");
                        }
                        if (strong.matcher(s).matches()) {
                            isConfirm = true;
                        } else if (medium.matcher(s).matches()) {
                            isConfirm = false;
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        } else {
                            isConfirm = false;
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
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
                        strNewPwd = etPassword.getText().toString().trim();
                        SetPassword setPassword = new SetPassword();
                        setPassword.setCode(strCode);
                        setPassword.setPassword(etPassword.getText().toString().trim());
                        loginViewModel.setPassword(setPassword);
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

            layoutMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Utils.hideKeypad(CreatePasswordActivity.this);
                    }
                    return false;
                }
            });
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
    }

}
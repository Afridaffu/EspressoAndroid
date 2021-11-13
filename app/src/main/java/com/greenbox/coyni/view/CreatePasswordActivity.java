package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
//                    if (hasFocus) {
//                        layoutIndicator.setVisibility(VISIBLE);
//                        //tvPasswordInfo.setVisibility(VISIBLE);
//                    } else {
//                        layoutIndicator.setVisibility(View.GONE);
//                        tvPasswordInfo.setVisibility(View.GONE);
//                    }

                    if (hasFocus) {
                        layoutIndicator.setVisibility(VISIBLE);
                        etlPassword.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_green));
                    } else {
                        layoutIndicator.setVisibility(GONE);
                        if(strong.matcher(etPassword.getText().toString()).matches()){
                            etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_black));
                        }else{
                            etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));
                        }
                    }
                }
            });

            etCPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        etlCPassword.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlCPassword, getColor(R.color.primary_green));
                    } else {
                        if(etPassword.getText().toString().trim().equals(etCPassword.getText().toString().trim())){
                            etlCPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(etlCPassword, getColor(R.color.primary_black));
                        }else{
                            etlCPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(etlCPassword, getColor(R.color.error_red));
                        }
                    }
                }
            });

            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    try {
                        if (strong.matcher(charSequence).matches()) {
                            strengthOne.setVisibility(View.VISIBLE);
                            strengthTwo.setVisibility(View.VISIBLE);
                            strengthThree.setVisibility(View.VISIBLE);
                            strengthOne.setBackgroundColor(getResources().getColor(R.color.primary_color));
                            strengthTwo.setBackgroundColor(getResources().getColor(R.color.primary_color));
                            strengthThree.setBackgroundColor(getResources().getColor(R.color.primary_color));
                            tvPasswordInfo.setVisibility(View.GONE);
                            isPassword = true;
                        } else if (medium.matcher(charSequence).matches()) {
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

                        if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
                            isPassword = true;
                        } else {
                            isPassword = false;
                        }
                        if (etPassword.getText().toString().trim().equals(etCPassword.getText().toString().trim())) {
                            isConfirm = true;
                        } else {
                            isConfirm = false;
                        }
//                        enableOrDisableNext();

                        if(isConfirm && isPassword){
                            cvSave.setEnabled(true);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        }else{
                            cvSave.setEnabled(false);
                            cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            etCPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                    if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
                        isPassword = true;
                    } else {
                        isPassword = false;
                    }
                    if (etPassword.getText().toString().trim().equals(etCPassword.getText().toString().trim())) {
                        isConfirm = true;
                    } else {
                        isConfirm = false;
                    }
//                        enableOrDisableNext();

                    if(isConfirm && isPassword){
                        cvSave.setEnabled(true);
                        cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                    }else{
                        cvSave.setEnabled(false);
                        cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {

                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            etCPassword.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            etCPassword.setText(s.toString().trim());
                            etCPassword.setSelection(s.toString().trim().length());
                        }

                        if (etPassword.getText().toString().trim().equals(etCPassword.getText().toString().trim())) {
                            isConfirm = true;

                            etlPassword.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            etlPassword.setHint("New Password");
                            Utils.setUpperHintColor(etlPassword, getColor(R.color.primary_black));

                            etlCPassword.setBoxStrokeColor(getColor(R.color.primary_green));
                            etlCPassword.setHint("Confirm Password");
                            Utils.setUpperHintColor(etlCPassword, getColor(R.color.primary_green));

                        } else {
                            isConfirm = false;

                            etlPassword.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            etlPassword.setHint("Password doesn’t match");
                            Utils.setUpperHintColor(etlPassword, getColor(R.color.error_red));

                            etlCPassword.setHint("Password doesn’t match");

                            if(etPassword.getText().toString().trim().length() == etCPassword.getText().toString().trim().length()){
                                etCPassword.clearFocus();
                            }

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
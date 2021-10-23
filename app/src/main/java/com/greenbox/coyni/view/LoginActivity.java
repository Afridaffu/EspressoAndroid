package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout etlEmail, etlPassword;
    TextInputEditText etEmail, etPassword;
    TextView faceidNotAvail;
    CardView cvNext,cvEmailOK;
    LinearLayout layoutEmailError, layoutPwdError;
    TextView tvEmailError, tvPwdError;
    String strEmail = "", strPwd = "";
    ProgressDialog dialog;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_login);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            etlPassword = findViewById(R.id.etlPassword);
            etlEmail = findViewById(R.id.etlEmail);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            cvNext = findViewById(R.id.cvNext);
            layoutEmailError = findViewById(R.id.layoutEmailError);
            tvEmailError = findViewById(R.id.tvEmailError);
            layoutPwdError = findViewById(R.id.layoutPwdError);
            tvPwdError = findViewById(R.id.tvPwdError);
            cvNext.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            etlPassword.setEndIconOnClickListener(view -> {
                FaceIdNotAvailable_BottomSheet faceIdNotAvailable_bottomSheet = new FaceIdNotAvailable_BottomSheet();
                faceIdNotAvailable_bottomSheet.show(getSupportFragmentManager(), faceIdNotAvailable_bottomSheet.getTag());

            });
            faceidNotAvail = findViewById(R.id.faceidNotAvaiSheet);

            faceidNotAvail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FaceIdNotAvailable_BottomSheet faceIdNotAvailable_bottomSheet = new FaceIdNotAvailable_BottomSheet();
                    faceIdNotAvailable_bottomSheet.show(getSupportFragmentManager(), faceIdNotAvailable_bottomSheet.getTag());
                }
            });
            etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (emailValidation() && passwordValidation()) {
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

            etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (emailValidation() && passwordValidation()) {
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

            etEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (emailValidation() && passwordValidation()) {
                            cvNext.setEnabled(true);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        } else if (emailValidation()) {
                            etlEmail.setErrorEnabled(false);
                            etlEmail.setError("");
                            layoutEmailError.setVisibility(View.GONE);
                        } else {
                            cvNext.setEnabled(false);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                        if (s.toString().length() == 0) {
                            etlEmail.setErrorEnabled(false);
                            etlEmail.setError("");
                            layoutEmailError.setVisibility(View.GONE);
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

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (emailValidation() && passwordValidation()) {
                            cvNext.setEnabled(true);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        } else if (passwordValidation()) {
                            etlPassword.setErrorEnabled(false);
                            etlPassword.setError("");
                            layoutPwdError.setVisibility(View.GONE);
                        } else {
                            cvNext.setEnabled(false);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                        if (s.toString().length() == 0) {
                            etlPassword.setErrorEnabled(false);
                            etlPassword.setError("");
                            layoutPwdError.setVisibility(View.GONE);
                            cvNext.setEnabled(false);
                            cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(LoginActivity.this, v);
                        if (Utils.checkInternet(LoginActivity.this)) {
                            strEmail = etEmail.getText().toString().trim().toLowerCase();
                            strPwd = etPassword.getText().toString().trim();
                            login();
                        } else {
                            Utils.displayAlert(getString(R.string.internet), LoginActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
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
                            if (login.getData().getPasswordExpired()) {
                                //showPwdExpiredPopup();
                            } else {
                                Utils.setStrAuth(login.getData().getJwtToken());
                                Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } else {
                            if (login.getData() != null) {
//                                Utils.displayAlert(login.getData().getMessage(), LoginActivity.this);
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
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        Utils.displayAlert(apiError.getError().getErrorDescription(), LoginActivity.this);
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), LoginActivity.this);
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
}
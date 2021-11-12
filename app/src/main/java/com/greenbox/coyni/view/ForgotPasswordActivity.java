package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxEditText;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    CardView cvNext;
    TextInputEditText etEmail;
    LoginViewModel loginViewModel;
    TextInputLayout etlEmail;
    ProgressDialog dialog;
    LinearLayout layoutEmailError, llClose;
    TextView tvEmailError, tvMessage, tvHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_password);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            llClose = findViewById(R.id.llClose);
            cvNext = findViewById(R.id.cvNext);
            etEmail = findViewById(R.id.etEmail);
            etlEmail = findViewById(R.id.etlEmail);
            layoutEmailError = findViewById(R.id.layoutEmailError);
            tvEmailError = findViewById(R.id.tvEmailError);
            tvMessage = findViewById(R.id.tvMessage);
            tvHead = findViewById(R.id.tvHead);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            Utils.statusBar(ForgotPasswordActivity.this, "#FFFFFF");
            etEmail.requestFocus();
            llClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ForgotPwd")) {
                tvHead.setText("Forgot Your Password?");
                tvMessage.setText("Before we can reset your password, we will need to verify your identity.\nPlease enter the email register with your account.");
            } else {
                tvHead.setText("Forgot Your PIN?");
                tvMessage.setText("Before we can reset your PIN, we will need to verify your identity.\nPlease enter the email register with your account.");
            }

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
                        etlEmail.setErrorEnabled(false);
                        etlEmail.setError("");
                        layoutEmailError.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(ForgotPasswordActivity.this, v);
                        if (validation()) {
                            dialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            loginViewModel.emailotpresend(etEmail.getText().toString().trim());
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
        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                dialog.dismiss();
                if (emailResponse != null) {
                    if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                        Intent i = new Intent(ForgotPasswordActivity.this, OTPValidation.class);
                        i.putExtra("OTP_TYPE", "EMAIL");
                        i.putExtra("EMAIL", etEmail.getText().toString().trim());
                        i.putExtra("screen", getIntent().getStringExtra("screen"));
                        startActivity(i);
                    } else {
                        Utils.displayAlert(emailResponse.getError().getErrorDescription(), ForgotPasswordActivity.this);
                    }
                }

            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etEmail.getText().toString().equals("")) {
                etlEmail.setErrorEnabled(true);
                etlEmail.setError(" ");
                layoutEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Please enter Email");
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                etlEmail.setErrorEnabled(true);
                etlEmail.setError(" ");
                layoutEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Please enter valid Email");
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
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
    ImageView imgClose;
    CardView cvNext;
    TextInputEditText etEmail;
    LoginViewModel loginViewModel;
    TextInputLayout etlEmail;
    ProgressDialog dialog;
    LinearLayout layoutEmailError;
    TextView tvEmailError;

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
            imgClose = findViewById(R.id.imgClose);
            cvNext = findViewById(R.id.cvNext);
            etEmail = findViewById(R.id.etEmail);
            etlEmail = findViewById(R.id.etlEmail);
            layoutEmailError = findViewById(R.id.layoutEmailError);
            tvEmailError = findViewById(R.id.tvEmailError);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            Utils.statusBar(ForgotPasswordActivity.this, "#FFFFFF");
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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
                        i.putExtra("screen", "ForgotPwd");
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
                //Utils.displayAlert("Please enter Email", ForgotPasswordActivity.this);
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                etlEmail.setErrorEnabled(true);
                etlEmail.setError(" ");
                layoutEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Please enter valid Email");
                //Utils.displayAlert("Please enter valid Email", ForgotPasswordActivity.this);
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
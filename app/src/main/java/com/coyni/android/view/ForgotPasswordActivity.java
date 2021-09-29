package com.coyni.android.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.model.register.EmailResendResponse;
import com.coyni.android.model.register.ForgotPassword;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputEditText etEmail, etPassword;
    MyApplication objMyApplication;
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    ImageView imgBack;
    CardView cvSendInstructions;

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
            imgBack = (ImageView) findViewById(R.id.imgBack);
            etEmail = (TextInputEditText) findViewById(R.id.etEmail);
            cvSendInstructions = (CardView) findViewById(R.id.cvSendInstructions);

            objMyApplication = (MyApplication) getApplicationContext();
            Utils.statusBar(ForgotPasswordActivity.this);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").equals("")) {
                etEmail.setText(getIntent().getStringExtra("email"));
                etEmail.setEnabled(false);
            } else {
                etEmail.setText("");
                etEmail.setEnabled(true);
            }
            cvSendInstructions.setOnClickListener(new View.OnClickListener() {
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
        loginViewModel.getForgotPasswordLiveData().observe(this, new Observer<ForgotPassword>() {
            @Override
            public void onChanged(ForgotPassword forgotPwdResponse) {
                dialog.dismiss();
                if (forgotPwdResponse != null && forgotPwdResponse.getError() == null) {

                } else {
                    Utils.displayAlert(loginViewModel.getErrorMessage1(), ForgotPasswordActivity.this);
                }
            }
        });
        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                dialog.dismiss();
                if (emailResponse != null) {
                    if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                        Intent i = new Intent(ForgotPasswordActivity.this, EmailOtpActivity.class);
                        i.putExtra("From", "forgotPwd");
                        i.putExtra("email", etEmail.getText().toString());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                Utils.displayAlert("Please enter Email", ForgotPasswordActivity.this);
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                Utils.displayAlert("Please enter valid Email", ForgotPasswordActivity.this);
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
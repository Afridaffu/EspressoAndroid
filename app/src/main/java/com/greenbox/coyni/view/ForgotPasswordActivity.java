package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
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
    ProgressDialog dialog;

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
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            Utils.statusBar(ForgotPasswordActivity.this, "#FFFFFF");
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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
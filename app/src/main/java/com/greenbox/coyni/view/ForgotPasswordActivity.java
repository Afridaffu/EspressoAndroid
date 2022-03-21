package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
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
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    CardView cvNext;
    TextInputEditText etEmail;
    LoginViewModel loginViewModel;
    TextInputLayout etlEmail;
    ProgressDialog dialog;
    LinearLayout layoutEmailError, llClose;
    TextView tvEmailError, tvMessage, tvHead;
    RelativeLayout layoutMain;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    String fromStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
            layoutMain = findViewById(R.id.layoutMain);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            Utils.statusBar(ForgotPasswordActivity.this, "#FFFFFF");
            objMyApplication = (MyApplication) getApplicationContext();
            etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

            llClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            fromStr = getIntent().getStringExtra("screen");
            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ForgotPwd")) {
                tvHead.setText("Forgot Your Password?");
                tvMessage.setText("Before we can reset your password, we will need to verify your identity.\nPlease enter the email register with your account.");
            } else {
                tvHead.setText("Forgot Your PIN?");
                tvMessage.setText("Before we can reset your PIN, we will need to verify your identity.\nPlease enter the email register with your account.");
                etEmail.setText(Utils.getUserEmail(this));
                Utils.setUpperHintColor(etlEmail, getColor(R.color.text_color));
                etEmail.setEnabled(false);
            }

            if (getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").equals("")) {
                etEmail.setText(getIntent().getStringExtra("email"));
                Utils.setUpperHintColor(etlEmail, getColor(R.color.text_color));
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
                        String str = etEmail.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            etEmail.setText(etEmail.getText().toString().replaceAll(" ", ""));
                            etEmail.setSelection(etEmail.getText().length());
                        } else if (s.length() == 0) {
                            layoutEmailError.setVisibility(GONE);
                        } else if (Utils.isValidEmail(etEmail.getText().toString().trim()) && etEmail.getText().toString().trim().length() > 5) {
                            layoutEmailError.setVisibility(GONE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(ForgotPasswordActivity.this);
//                        etEmail.setHint("Email");
                        etlEmail.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_green));
                        layoutEmailError.setVisibility(GONE);
                    } else {
                        etEmail.setHint("");
                    }
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.hideKeypad(ForgotPasswordActivity.this, v);
                        if (etEmail.getText().toString().trim().length() > 5 && !Utils.isValidEmail(etEmail.getText().toString().trim())) {
                            etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                            layoutEmailError.setVisibility(VISIBLE);
                            tvEmailError.setText("Please enter a valid Email");
                            etEmail.clearFocus();
                        } else if (etEmail.getText().toString().trim().length() > 5 && Utils.isValidEmail(etEmail.getText().toString().trim())) {
                            etlEmail.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlEmail, getColor(R.color.primary_black));
                            layoutEmailError.setVisibility(GONE);
                            etEmail.clearFocus();
                            dialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            loginViewModel.emailotpresend(etEmail.getText().toString().trim());
                        } else if (etEmail.getText().toString().trim().length() > 0 && etEmail.getText().toString().trim().length() <= 5) {
                            etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                            layoutEmailError.setVisibility(VISIBLE);
                            tvEmailError.setText("Please enter a valid Email");
                            etEmail.clearFocus();
                        } else {
                            etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                            Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                            Utils.setUpperHintColor(etlEmail, getColor(R.color.light_gray));
                            layoutEmailError.setVisibility(VISIBLE);
                            tvEmailError.setText("Field Required");
                            etEmail.clearFocus();
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
                        if (emailResponse.getError().getErrorDescription().toLowerCase().contains("not found")) {
                            etlEmail.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlEmail, getColor(R.color.error_red));
                            layoutEmailError.setVisibility(VISIBLE);
                            tvEmailError.setText("Incorrect information");
                            etEmail.clearFocus();
                        } else {
                            String message = getString(R.string.something_went_wrong);
                            if (emailResponse.getError().getFieldErrors().size() > 0) {
                                message = emailResponse.getError().getFieldErrors().get(0);
                            }
                            Utils.displayAlert(emailResponse.getError().getErrorDescription(), ForgotPasswordActivity.this, "", message);
                        }

                    }
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (fromStr.equals("ForgotPwd")) {
                etEmail.requestFocus();
                if(!Utils.isKeyboardVisible)
                    Utils.shwForcedKeypad(this);
            } else {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
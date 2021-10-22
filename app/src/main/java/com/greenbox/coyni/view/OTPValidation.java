package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.utils.otpview.PinView;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.Locale;

public class OTPValidation extends AppCompatActivity {
    TextView resendTV, newCodeTV, subHeaderTV, headerTV;
    PinView otpPV;
    ImageView otpValidationCloseIV;
    int resendCounter = 0;
    private Vibrator vibrator;
    String OTP_TYPE = "", MOBILE = "", EMAIL = "", strScreen = "";
    LinearLayout layoutEntry, layoutFailure;
    MaterialCardView tryAgainCV;
    ProgressDialog dialog;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_otpvalidation);

            OTP_TYPE = getIntent().getStringExtra("OTP_TYPE");
            MOBILE = getIntent().getStringExtra("MOBILE");
            EMAIL = getIntent().getStringExtra("EMAIL");
            strScreen = getIntent().getStringExtra("screen");

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            resendTV = findViewById(R.id.resendTV);
            headerTV = findViewById(R.id.headerTV);
            subHeaderTV = findViewById(R.id.subHeaderTV);
            newCodeTV = findViewById(R.id.newCodeTV);
            otpPV = findViewById(R.id.otpPV);
            otpValidationCloseIV = findViewById(R.id.otpValidationCloseIV);
            layoutEntry = findViewById(R.id.layoutEntry);
            layoutFailure = findViewById(R.id.layoutFailure);
            tryAgainCV = findViewById(R.id.tryAgainCV);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            layoutEntry.setVisibility(View.VISIBLE);
            layoutFailure.setVisibility(View.GONE);

            resendTV.setPaintFlags(resendTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            otpPV.setAnimationEnable(true);

            if (strScreen != null && !strScreen.equals("") && strScreen.equals("ForgotPwd")) {
                otpValidationCloseIV.setImageResource(R.drawable.ic_close);
                headerTV.setText("Verify Email");
                subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + EMAIL);
            } else {
                otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                if (OTP_TYPE.equals("MOBILE")) {
                    headerTV.setText("Please Verify your Phone Number");
                    subHeaderTV.setText("We sent you a 6-digit code to the register phone number: " + MOBILE);
                } else if (OTP_TYPE.equals("EMAIL")) {
                    headerTV.setText("Please Verify your Email");
                    subHeaderTV.setText("We sent you a 6-digit code to the register email address: " + EMAIL);
                }
            }

            resendTV.setOnClickListener(view -> {
                try {
                    if (resendCounter < 5) {
                        resendTV.setVisibility(View.GONE);
                        newCodeTV.setVisibility(View.VISIBLE);
                        resendCounter++;
                        startTimer();

                    } else {
                        layoutEntry.setVisibility(View.GONE);
                        layoutFailure.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            otpValidationCloseIV.setOnClickListener(view -> {
                finish();
            });

            otpPV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() == 0) {
                            otpPV.setLineColor(getResources().getColor(R.color.line_colors));
                        } else {
                            if (strScreen != null && !strScreen.equals("") && strScreen.equals("ForgotPwd")) {
                                if (charSequence.length() == 6) {
                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.show();
                                    SmsRequest smsRequest = new SmsRequest();
                                    smsRequest.setEmail(EMAIL.trim());
                                    smsRequest.setOtp(charSequence.toString().trim());
                                    loginViewModel.emailotp(smsRequest);
                                }
                            } else {
                                if (OTP_TYPE.equals("MOBILE")) {
                                    if (charSequence.length() == 6) {
                                        if (charSequence.toString().equals("123456")) {
                                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                            shakeAnimateUpDown();
                                            startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                                    .putExtra("OTP_TYPE", "EMAIL")
                                                    .putExtra("MOBILE", MOBILE)
                                                    .putExtra("EMAIL", EMAIL)
                                            );
                                        } else {
                                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                            shakeAnimateLeftRight();
                                        }
                                    }
                                } else {
                                    if (charSequence.length() == 6) {
                                        if (charSequence.toString().equals("123456")) {
                                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                            shakeAnimateUpDown();
                                            startActivity(new Intent(OTPValidation.this, SecureAccount.class));
                                        } else {
                                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                            shakeAnimateLeftRight();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            tryAgainCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OTPValidation.this, OnboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void startTimer() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resendTV.setVisibility(View.VISIBLE);
                                newCodeTV.setVisibility(View.GONE);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            ;
        }.start();
    }

    private void initObserver() {
        loginViewModel.getEmailotpLiveData().observe(this, new Observer<EmailResponse>() {
            @Override
            public void onChanged(EmailResponse emailResponse) {
                dialog.dismiss();
                if (emailResponse != null) {
                    if (emailResponse.getStatus().toLowerCase().equals("error")) {
                        otpPV.setLineColor(getResources().getColor(R.color.error_red));
                        shakeAnimateLeftRight();
                    } else {
                        otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                        shakeAnimateUpDown();
                        startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class));
                    }
                }
            }
        });
    }

    public void shakeAnimateLeftRight() {
        vibrateAction();
        otpPV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void shakeAnimateUpDown() {
        vibrateAction();
        otpPV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_up_down));
    }

    public void vibrateAction() {
        try {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(600);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
}
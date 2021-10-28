package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.otpview.PinView;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.Locale;

public class OTPValidation extends AppCompatActivity {
    TextView resendTV, newCodeTV, subHeaderTV, headerTV;
    PinView otpPV;
    ImageView otpValidationCloseIV;
    int resendCounter = 0;
    private Vibrator vibrator;
    String OTP_TYPE = "", MOBILE = "", EMAIL = "", strScreen = "", countryCode = "", maskedPhone = "";
    LinearLayout layoutEntry, layoutFailure;
    MaterialCardView tryAgainCV;
    ProgressDialog dialog;
    LoginViewModel loginViewModel;

    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_otpvalidation);

            SmsRetriever.getClient(this).startSmsUserConsent(null);

            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            registerReceiver(smsVerificationReceiver, intentFilter);

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

//            if (strScreen != null && !strScreen.equals("") && strScreen.equals("ForgotPwd")) {
//                otpValidationCloseIV.setImageResource(R.drawable.ic_close);
//                headerTV.setText("Verify Email");
//                subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + EMAIL);
//            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("SignUp")) {
//                countryCode = getIntent().getStringExtra("COUNTRY_CODE");
//                maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
//                otpValidationCloseIV.setImageResource(R.drawable.ic_back);
//                if (OTP_TYPE.equals("MOBILE")) {
//                    headerTV.setText("Please Verify your Phone Number");
//                    subHeaderTV.setText("We sent you a 6-digit code to the register phone number: " + maskedPhone);
//                } else if (OTP_TYPE.equals("EMAIL")) {
//                    headerTV.setText("Please Verify your Email");
//                    subHeaderTV.setText("We sent you a 6-digit code to the register email address: " + EMAIL);
//                }
//            }
            if (strScreen != null && !strScreen.equals("")) {
                switch (strScreen) {
                    case "ForgotPwd":
                        otpValidationCloseIV.setImageResource(R.drawable.ic_close);
                        headerTV.setText("Verify Email");
                        subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + EMAIL);
                        break;
                    case "retEmail":
                        countryCode = Utils.strCCode;
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        headerTV.setText("Please Verify your Phone Number");
                        subHeaderTV.setText("We have sent you a 6-digit code sent to the register phone number " + maskedPhone);
                        break;
                    case "SignUp":
                        countryCode = getIntent().getStringExtra("COUNTRY_CODE");
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (OTP_TYPE.equals("MOBILE")) {
                            headerTV.setText("Please Verify your Phone Number");
                            subHeaderTV.setText("We sent you a 6-digit code to the register phone number: " + maskedPhone);
                        } else if (OTP_TYPE.equals("EMAIL")) {
                            headerTV.setText("Please Verify your Email");
                            subHeaderTV.setText("We sent you a 6-digit code to the register email address: " + EMAIL);
                        }
                        break;
                }
            }

            resendTV.setOnClickListener(view -> {
                try {
                    if (resendCounter < 5) {
                        resendTV.setVisibility(View.GONE);
                        newCodeTV.setVisibility(View.VISIBLE);
                        resendCounter++;
                        startTimer();
                        Utils.hideKeypad(OTPValidation.this, view);
                        if ((strScreen != null && !strScreen.equals("") && strScreen.equals("ForgotPwd")) || (OTP_TYPE.equals("EMAIL"))) {
                            dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            loginViewModel.emailotpresend(EMAIL.trim());
                        } else if (OTP_TYPE.equals("MOBILE")) {
                            dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            SMSResend resend = new SMSResend();
                            resend.setCountryCode(Utils.strCCode);
                            resend.setPhoneNumber(MOBILE);
                            loginViewModel.smsotpresend(resend);
                        }

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
                                    loginViewModel.emailotpValidate(smsRequest);
                                }
                            } else {
                                if (OTP_TYPE.equals("MOBILE")) {
                                    if (charSequence.length() == 6) {
                                        if (charSequence.toString().equals("123456")) {
                                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                            shakeAnimateUpDown();
//                                            startActivity(new Intent(OTPValidation.this, OTPValidation.class)
//                                                    .putExtra("OTP_TYPE", "EMAIL")
//                                                    .putExtra("MOBILE", MOBILE)
//                                                    .putExtra("EMAIL", EMAIL)
//                                            );
                                            if (strScreen != null && strScreen.equals("retEmail")) {
                                                Intent x = new Intent(OTPValidation.this, BindingLayoutActivity.class);
                                                x.putExtra("screen", "retEmailfound");
                                                startActivity(x);
                                            }
                                        } else {
                                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                            shakeAnimateLeftRight();
                                        }
//                                        SmsRequest smsRequest = new SmsRequest();
//                                        smsRequest.setEmail(objMyApplication.getStrEmail().trim());
//                                        smsRequest.setOtp(etOtp1.getText().toString() + etOtp2.getText().toString() + etOtp3.getText().toString() + etOtp4.getText().toString() +
//                                                etOtp5.getText().toString() + etOtp6.getText().toString());
//                                        loginViewModel.smsotp(smsRequest);

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

    private void startTimer() {
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

        loginViewModel.getEmailValidateResponseMutableLiveData().observe(this, new Observer<EmailValidateResponse>() {
            @Override
            public void onChanged(EmailValidateResponse emailValidateResponse) {
                try {
                    dialog.dismiss();
                    if (emailValidateResponse != null) {
                        if (emailValidateResponse.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class).putExtra("code", emailValidateResponse.getData().getCode()));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // ...
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    // Get SMS message content
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // `sms` contains the entire text of the SMS message, so you will need
                    // to parse the string.
//                    String oneTimeCode = parseOneTimeCode(message); // define this function

                    Log.e("Message", message);
                    // send one time code to the server
                } else {
                    // Consent canceled, handle the error ...
                }
                break;
        }
    }

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };
}
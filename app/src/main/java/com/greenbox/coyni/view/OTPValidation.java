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
import android.os.SystemClock;
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
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailValidateRequest;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.InitCustomerRequest;
import com.greenbox.coyni.model.register.InitializeCustomerResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.model.register.SMSValidate;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.otpview.PinView;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPValidation extends AppCompatActivity {
    TextView resendTV, newCodeTV, subHeaderTV, headerTV;
    PinView otpPV;
    ImageView otpValidationCloseIV;
    int resendCounter = 0;
    private Vibrator vibrator;
    String OTP_TYPE = "", MOBILE = "", EMAIL = "", strScreen = "", maskedPhone = "", oldEmail = "", newEmail = "", isOldEmail;
    LinearLayout layoutEntry, layoutFailure;
    MaterialCardView tryAgainCV;
    ProgressDialog dialog;
    LoginViewModel loginViewModel;
    RelativeLayout secureAccountRL;
    MaterialCardView secureNextCV;
    MyApplication objMyApplication;

    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code

    String layoutType = "OTP"; //SECURE: if VISIBLITY ON FOR SECURE ACCOUNT SCREEN AFTER API CALL
    Long mLastClickTime = 0L;

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
            secureAccountRL = findViewById(R.id.secureAccountRL);
            secureNextCV = findViewById(R.id.secureNextCV);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            layoutEntry.setVisibility(View.VISIBLE);
            layoutFailure.setVisibility(View.GONE);

            resendTV.setPaintFlags(resendTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            otpPV.setAnimationEnable(true);
            otpPV.requestFocus();
            objMyApplication = (MyApplication) getApplicationContext();

            if (strScreen != null && !strScreen.equals("")) {
                switch (strScreen) {
                    case "ForgotPwd":
                        otpValidationCloseIV.setImageResource(R.drawable.ic_close);
                        headerTV.setText("Verify Email");
                        subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + EMAIL);
                        break;
                    case "ForgotPin":
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        headerTV.setText("Verify Email");
                        subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + EMAIL);
                        break;
                    case "retEmail":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        headerTV.setText("Please Verify your Phone Number");
                        subHeaderTV.setText("We have sent you a 6-digit code sent to the register phone number " + maskedPhone);
                        break;
                    case "SignUp":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (OTP_TYPE.equals("MOBILE")) {
                            headerTV.setText("Please Verify your Phone Number");
                            subHeaderTV.setText("We sent you a 6-digit code to the register phone number " + maskedPhone);
                        } else if (OTP_TYPE.equals("EMAIL")) {
                            headerTV.setText("Please Verify your Email");
                            subHeaderTV.setText("We sent you a 6-digit code sent to the register email address: " + EMAIL);
                        } else if (OTP_TYPE.equals("SECURE")) {
                            Utils.hideKeypad(OTPValidation.this, otpPV.getRootView());
                            secureAccountRL.setVisibility(View.VISIBLE);
                            layoutType = "SECURE";
                            layoutEntry.setVisibility(View.GONE);
                            layoutFailure.setVisibility(View.GONE);
                        }
                        break;
                    case "Login":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_close);
                        if (OTP_TYPE.equals("MOBILE")) {
                            headerTV.setText("Please Verify your Phone Number");
                            subHeaderTV.setText("We have sent you 6 digits code sent to the register phone number " + maskedPhone);
                        }
                        SMSResend resend = new SMSResend();
                        resend.setCountryCode(Utils.getStrCCode());
                        resend.setPhoneNumber(MOBILE);
                        loginViewModel.smsotpresend(resend);
                        break;
                    case "EditEmail":
                        isOldEmail = getIntent().getStringExtra("IS_OLD_EMAIL");
                        oldEmail = getIntent().getStringExtra("OLD_EMAIL");
                        newEmail = getIntent().getStringExtra("NEW_EMAIL");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (isOldEmail.equals("true")) {
                            headerTV.setText("Please Verify your Current Email");
                            subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + oldEmail);
                        } else {
                            headerTV.setText("Please Verify New Email");
                            subHeaderTV.setText("We have sent you a 6-digit code sent to the register email address: " + newEmail);
                        }
                        break;
                }
            }

            resendTV.setOnClickListener(view -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (resendCounter < 5) {
                        Utils.hideKeypad(OTPValidation.this, view);
                        if ((strScreen != null && !strScreen.equals("") && (strScreen.equals("ForgotPwd") || strScreen.equals("ForgotPin") || strScreen.equals("EditEmail"))) || (OTP_TYPE.equals("EMAIL"))) {
                            dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            if (strScreen.equals("EditEmail")) {
                                if (isOldEmail.equals("true")) {
                                    loginViewModel.emailotpresend(oldEmail);
                                } else {
                                    loginViewModel.emailotpresend(oldEmail);
                                }
                            } else {
                                loginViewModel.emailotpresend(EMAIL.trim());
                            }

                        } else if (OTP_TYPE.equals("MOBILE")) {
                            dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            SMSResend resend = new SMSResend();
                            resend.setCountryCode(Utils.getStrCCode());
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
                                    Utils.hideKeypad(OTPValidation.this);
                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.show();
                                    SmsRequest smsRequest = new SmsRequest();
                                    smsRequest.setEmail(EMAIL.trim());
                                    smsRequest.setOtp(charSequence.toString().trim());
                                    loginViewModel.emailotpValidate(smsRequest);
                                }
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("retEmail")) {
                                if (charSequence.length() == 6) {
                                    Utils.hideKeypad(OTPValidation.this);
                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.show();
                                    String phoneNumber = "";
                                    if (MOBILE.contains("(")) {
                                        phoneNumber = MOBILE.substring(1, 4) + MOBILE.substring(6, 9) + MOBILE.substring(10, MOBILE.length());
                                    } else {
                                        phoneNumber = MOBILE;
                                    }
                                    RetrieveUsersRequest request = new RetrieveUsersRequest();
                                    request.setCountryCode(Utils.getStrCCode());
                                    request.setPhoneNumber(phoneNumber);
                                    loginViewModel.retrieveUsers(request, charSequence.toString().trim());
                                }
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("EditEmail")) {
                                if (charSequence.length() == 6) {
                                    Utils.hideKeypad(OTPValidation.this);
                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.show();
                                    if (isOldEmail.equals("true")) {
                                        UpdateEmailValidateRequest updateEmailValidateRequest = new UpdateEmailValidateRequest();
                                        updateEmailValidateRequest.setOldEmail(true);
                                        updateEmailValidateRequest.setTrackerId(objMyApplication.getUpdateEmailResponse().getData().getTrackerId());
                                        updateEmailValidateRequest.setOtp(charSequence.toString().trim());
                                        loginViewModel.updateEmailotpValidate(updateEmailValidateRequest);
                                    } else {
                                        UpdateEmailValidateRequest updateEmailValidateRequest = new UpdateEmailValidateRequest();
                                        updateEmailValidateRequest.setOldEmail(false);
                                        updateEmailValidateRequest.setTrackerId(objMyApplication.getUpdateEmailResponse().getData().getTrackerId());
                                        updateEmailValidateRequest.setOtp(charSequence.toString().trim());
                                        loginViewModel.updateEmailotpValidate(updateEmailValidateRequest);
                                    }
                                }
                            } else {
                                if (OTP_TYPE.equals("MOBILE")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
                                        dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                        dialog.setIndeterminate(false);
                                        dialog.setMessage("Please wait...");
                                        dialog.show();
                                        SmsRequest smsRequest = new SmsRequest();
                                        smsRequest.setEmail(EMAIL.trim());
                                        smsRequest.setOtp(charSequence.toString().trim());
                                        loginViewModel.smsotp(smsRequest);
                                    }
                                } else if (OTP_TYPE.equals("EMAIL")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
                                        dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                        dialog.setIndeterminate(false);
                                        dialog.setMessage("Please wait...");
                                        dialog.show();
                                        SmsRequest smsRequest = new SmsRequest();
                                        smsRequest.setEmail(EMAIL.trim());
                                        smsRequest.setOtp(charSequence.toString().trim());
                                        loginViewModel.emailotp(smsRequest);
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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(OTPValidation.this, OnboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

            secureNextCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(OTPValidation.this, PINActivity.class)
                        .putExtra("screen", "SignUp")
                        .putExtra("TYPE", "CHOOSE"));
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
                    Log.e("Email OTP Validate", new Gson().toJson(emailResponse));
                    if (emailResponse.getStatus().toLowerCase().equals("error")) {
                        otpPV.setLineColor(getResources().getColor(R.color.error_red));
                        shakeAnimateLeftRight();
                    } else {
                        if (strScreen != null && !strScreen.equals("")) {
                            switch (strScreen) {
                                case "ForgotPwd":
                                    otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                    shakeAnimateUpDown();
                                    startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class));
                                    break;
                                case "ForgotPin":
                                    otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                    shakeAnimateUpDown();
                                    startActivity(new Intent(OTPValidation.this, PINActivity.class).putExtra("TYPE", "CHOOSE")
                                            .putExtra("screen", getIntent().getStringExtra("screen")));
                                    break;
                                case "retEmail":

                                    break;
                                case "SignUp":
                                    if (OTP_TYPE.equals("EMAIL")) {
                                        otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                        shakeAnimateUpDown();
                                        Utils.hideKeypad(OTPValidation.this, otpPV.getRootView());

                                        dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                                        dialog.setIndeterminate(false);
                                        dialog.setMessage("Please wait...");
                                        dialog.show();
                                        InitCustomerRequest initCustomerRequest = new InitCustomerRequest();
                                        initCustomerRequest.setCode(emailResponse.getData().getCode());
                                        loginViewModel.initializeCustomer(initCustomerRequest);
                                    }
                                    break;
                            }
                        }

                    }
                }
            }
        });

        loginViewModel.getSmsotpLiveData().observe(this, new Observer<SMSValidate>() {
            @Override
            public void onChanged(SMSValidate smsValidate) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (smsValidate != null) {
                    if (smsValidate.getStatus().toLowerCase().equals("error")) {
                        otpPV.setLineColor(getResources().getColor(R.color.error_red));
                        shakeAnimateLeftRight();
                    } else {
                        if (strScreen != null && !strScreen.equals("")) {
                            switch (strScreen) {
                                case "SignUp":
                                    if (OTP_TYPE.equals("MOBILE")) {
                                        otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                        shakeAnimateUpDown();
                                        resendCounter = 0;
                                        startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                                .putExtra("screen", "SignUp")
                                                .putExtra("OTP_TYPE", "EMAIL")
                                                .putExtra("MOBILE", MOBILE)
                                                .putExtra("EMAIL", EMAIL));
                                        finish();
                                    }
                                    break;
                                case "Login":
                                    secureAccountRL.setVisibility(View.VISIBLE);
                                    layoutType = "SECURE";
                                    layoutEntry.setVisibility(View.GONE);
                                    layoutFailure.setVisibility(View.GONE);
                                    break;
                            }
                        }

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

        loginViewModel.getSmsresendMutableLiveData().observe(this, new Observer<SMSResponse>() {
            @Override
            public void onChanged(SMSResponse smsResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (smsResponse != null) {
                    if (smsResponse.getStatus().toLowerCase().toString().equals("success")) {
                        if (strScreen != null && !strScreen.equals("") && !strScreen.equals("Login")) {
                            resendTV.setVisibility(View.GONE);
                            newCodeTV.setVisibility(View.VISIBLE);
                            resendCounter++;
                            startTimer();
                        }
                    } else {
                        Utils.displayAlert(smsResponse.getError().getErrorDescription(), OTPValidation.this);
                    }
                }

            }
        });

        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                dialog.dismiss();
                if (emailResponse != null) {
                    if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                        resendTV.setVisibility(View.GONE);
                        newCodeTV.setVisibility(View.VISIBLE);
                        resendCounter++;
                        startTimer();
                    } else {
                        Utils.displayAlert(emailResponse.getError().getErrorDescription(), OTPValidation.this);
                    }
                }
            }
        });

        loginViewModel.getInitCustomerLiveData().observe(this, new Observer<InitializeCustomerResponse>() {
            @Override
            public void onChanged(InitializeCustomerResponse initializeCustomerResponse) {
                dialog.dismiss();
                if (initializeCustomerResponse != null) {
                    if (initializeCustomerResponse.getStatus().toLowerCase().toString().equals("success")) {
                        Utils.setStrAuth(initializeCustomerResponse.getData().getJwtToken());
                        secureAccountRL.setVisibility(View.VISIBLE);
                        layoutType = "SECURE";
                        layoutEntry.setVisibility(View.GONE);
                        layoutFailure.setVisibility(View.GONE);
                    } else {
                        Utils.displayAlert(initializeCustomerResponse.getError().getErrorDescription(), OTPValidation.this);
                    }
                }
            }
        });

        loginViewModel.getRetrieveUsersResponseMutableLiveData().observe(this, new Observer<RetrieveUsersResponse>() {
            @Override
            public void onChanged(RetrieveUsersResponse retrieveUsersResponse) {
                try {
                    dialog.dismiss();
                    if (retrieveUsersResponse != null) {
                        if (!retrieveUsersResponse.getStatus().toLowerCase().equals("error")) {
                            objMyApplication.setObjRetUsers(retrieveUsersResponse);
                            startActivity(new Intent(OTPValidation.this, BindingLayoutActivity.class)
                                    .putExtra("screen", "retEmailfound"));
                        } else {
                            Utils.displayAlert(retrieveUsersResponse.getError().getErrorDescription(), OTPValidation.this);
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
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        Utils.displayAlert(apiError.getError().getErrorDescription(), OTPValidation.this);
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), OTPValidation.this);
                    }
                }
            }
        });

        loginViewModel.getUpdateEmailValidateResponse().observe(this, new Observer<UpdateEmailResponse>() {
            @Override
            public void onChanged(UpdateEmailResponse updateEmailResponse) {
                try {
                    dialog.dismiss();
                    if (updateEmailResponse != null) {
                        if (updateEmailResponse.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            resendCounter = 0;
                            startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                    .putExtra("screen", "EditEmail")
                                    .putExtra("IS_OLD_EMAIL", "true")
                                    .putExtra("OLD_EMAIL", oldEmail)
                                    .putExtra("NEW_EMAIL", newEmail));
                            finish();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void shakeAnimateLeftRight() {
//        vibrateAction();
        otpPV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void shakeAnimateUpDown() {
//        vibrateAction();
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
                    setOtpFromMessage(message);
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

    @Override
    public void onBackPressed() {
        try {
            switch (layoutType) {
                case "SECURE":
                    Intent intent = new Intent(OTPValidation.this, OnboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                case "OTP": {
                    super.onBackPressed();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            otpPV.setText(matcher.group(0));
        }
    }
}
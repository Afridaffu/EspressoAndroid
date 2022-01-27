package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailValidateRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneValidateRequest;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.InitializeCustomerResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.model.register.SMSValidate;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendOTPResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.otpview.PinView;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPValidation extends AppCompatActivity implements OnKeyboardVisibilityListener {
    TextView resendTV, newCodeTV, subHeaderTV, headerTV;
    PinView otpPV;
    ImageView otpValidationCloseIV;
    int resendCounter = 0;
    private Vibrator vibrator;
    String OTP_TYPE = "", MOBILE = "", EMAIL = "", strScreen = "", maskedPhone = "",
            oldEmail = "", newEmail = "", isOldEmail = "", oldPhone = "", newPhone = "", oldPhoneMasked = "", newPhoneMasked = "", isOldPhone = "";
    LinearLayout layoutEntry, layoutFailure, layoutMain;
    CardView tryAgainCV;
    ProgressDialog dialog;
    LoginViewModel loginViewModel;
    RelativeLayout secureAccountRL;
    CardView secureNextCV;
    MyApplication objMyApplication;
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails;
    String strFirstUser = "";
    private int mAccountType = Utils.PERSONAL_ACCOUNT;
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
            setKeyboardVisibilityListener(OTPValidation.this);
            SmsRetriever.getClient(this).startSmsUserConsent(null);

            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            registerReceiver(smsVerificationReceiver, intentFilter);

//            setKeyboardVisibilityListener(this);
            layoutType = "OTP";
            OTP_TYPE = getIntent().getStringExtra("OTP_TYPE");
            MOBILE = getIntent().getStringExtra("MOBILE");
            EMAIL = getIntent().getStringExtra("EMAIL");
            strScreen = getIntent().getStringExtra("screen");
            if(getIntent() != null) {
                mAccountType = getIntent().getIntExtra(Utils.ACCOUNT_TYPE, Utils.PERSONAL_ACCOUNT);
            }
            SetDB();
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            resendTV = findViewById(R.id.resendTV);
            headerTV = findViewById(R.id.headerTV);
            subHeaderTV = findViewById(R.id.subHeaderTV);
            newCodeTV = findViewById(R.id.newCodeTV);
            otpPV = findViewById(R.id.otpPV);
            otpValidationCloseIV = findViewById(R.id.otpValidationCloseIV);
            layoutEntry = findViewById(R.id.layoutEntry);
            layoutFailure = findViewById(R.id.layoutFailure);
            layoutMain = findViewById(R.id.layoutMain);
            tryAgainCV = findViewById(R.id.tryAgainCV);
            secureAccountRL = findViewById(R.id.secureAccountRL);
            secureNextCV = findViewById(R.id.secureNextCV);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            layoutEntry.setVisibility(View.VISIBLE);
            layoutFailure.setVisibility(View.GONE);

            resendTV.setPaintFlags(resendTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            otpPV.setAnimationEnable(true);
            objMyApplication = (MyApplication) getApplicationContext();
            objMyApplication.setStrEmail(EMAIL);
            if (strScreen != null && !strScreen.equals("")) {
                switch (strScreen) {
                    case "ForgotPwd":
                        otpValidationCloseIV.setImageResource(R.drawable.ic_close);
                        headerTV.setText("Verify Email");
                        subHeaderTV.setText("We have sent you a 6-digit code to the registered email address: " + EMAIL);
                        break;
                    case "ForgotPin":
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        headerTV.setText("Verify Email");
                        subHeaderTV.setText("We have sent you a 6-digit code to the registered email address: " + EMAIL);
                        break;
                    case "retEmail":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        headerTV.setText("Please Verify your Phone Number");
                        subHeaderTV.setText("We have sent you a 6-digit code to the registered phone number " + maskedPhone);
                        break;
                    case "SignUp":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (OTP_TYPE.equals("MOBILE")) {
                            headerTV.setText("Please Verify your Phone Number");
                            subHeaderTV.setText("We sent you a 6-digit code to the registered phone number " + maskedPhone);
                        } else if (OTP_TYPE.equals("EMAIL")) {
                            headerTV.setText("Please Verify your Email");
                            subHeaderTV.setText("We sent you a 6-digit code to the registered email address: " + EMAIL);
                        } else if (OTP_TYPE.equals("SECURE")) {
                            Utils.hideKeypad(OTPValidation.this, otpPV.getRootView());
                            secureAccountRL.setVisibility(View.VISIBLE);
                            layoutMain.setClickable(false);
                            layoutMain.setEnabled(false);
                            layoutType = "SECURE";
                            layoutEntry.setVisibility(View.GONE);
                            layoutFailure.setVisibility(View.GONE);
                        }
                        break;
                    case "login":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_close);
                        if (OTP_TYPE.equals("MOBILE")) {
                            headerTV.setText("Please Verify your Phone Number");
                            subHeaderTV.setText("We have sent you 6-digit code to the registered phone number " + maskedPhone);
                        }
                        resendCounter = resendCounter + 1;
//                        SMSResend resend = new SMSResend();
//                        resend.setCountryCode(Utils.getStrCCode());
//                        resend.setPhoneNumber(MOBILE);
//                        loginViewModel.smsotpresend(resend);
                        break;
                    case "EditEmail":
                        isOldEmail = getIntent().getStringExtra("IS_OLD_EMAIL");
                        oldEmail = getIntent().getStringExtra("OLD_EMAIL");
                        newEmail = getIntent().getStringExtra("NEW_EMAIL");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (isOldEmail.equals("true")) {
                            headerTV.setText("Please Verify your Current Email");
                            subHeaderTV.setText("We have sent you a 6-digit code to the registered email address: " + oldEmail);
                        } else {
                            headerTV.setText("Please Verify New Email");
                            subHeaderTV.setText("We have sent you a 6-digit code to the registered email address: " + newEmail);
//                            loginViewModel.emailotpresend(newEmail);
                        }
                        break;
                    case "EditPhone":
                        isOldPhone = getIntent().getStringExtra("IS_OLD_PHONE");
                        oldPhone = getIntent().getStringExtra("OLD_PHONE");
                        newPhone = getIntent().getStringExtra("NEW_PHONE");
                        oldPhoneMasked = getIntent().getStringExtra("OLD_PHONE_MASKED");
                        newPhoneMasked = getIntent().getStringExtra("NEW_PHONE_MASKED");

                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (isOldPhone.equals("true")) {
                            headerTV.setText("Please Verify your Current Phone Number");
                            subHeaderTV.setText("We have sent you a 6-digit code to the registered phone number " + oldPhoneMasked);
                        } else {
                            headerTV.setText("Please Verify your New Phone Number");
                            subHeaderTV.setText("We have sent you a 6-digit code to the registered phone number " + newPhoneMasked);
//                            loginViewModel.emailotpresend(newEmail);
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
                    if (resendCounter < 4) {
                        Utils.hideKeypad(OTPValidation.this, view);
                        if ((strScreen != null && !strScreen.equals("") && (strScreen.equals("ForgotPwd") || strScreen.equals("ForgotPin") || strScreen.equals("EditEmail"))) || (OTP_TYPE.equals("EMAIL"))) {
                            dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            if (strScreen.equals("EditEmail")) {
                                if (isOldEmail.equals("true")) {
                                    UpdateResendRequest updateResendRequest = new UpdateResendRequest();
                                    updateResendRequest.setEmail(true);
                                    updateResendRequest.setNew(false);
                                    updateResendRequest.setTrackerId(objMyApplication.getUpdateEmailResponse().getData().getTrackerId());
                                    loginViewModel.updateOtpResend(updateResendRequest);
//                                    loginViewModel.emailotpresend(oldEmail);
                                } else {
//                                    loginViewModel.emailotpresend(newEmail);
                                    UpdateResendRequest updateResendRequest = new UpdateResendRequest();
                                    updateResendRequest.setEmail(true);
                                    updateResendRequest.setNew(true);
                                    updateResendRequest.setTrackerId(objMyApplication.getUpdateEmailResponse().getData().getTrackerId());
                                    loginViewModel.updateOtpResend(updateResendRequest);
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
                        } else if (strScreen.equals("EditPhone")) {
                            dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
//                            SMSResend resend = new SMSResend();
//                            resend.setCountryCode(Utils.getStrCCode());
                            if (isOldPhone.equals("true")) {
                                UpdateResendRequest updateResendRequest = new UpdateResendRequest();
                                updateResendRequest.setEmail(false);
                                updateResendRequest.setNew(false);
                                updateResendRequest.setTrackerId(objMyApplication.getUpdatePhoneResponse().getData().getTrackerId());
                                loginViewModel.updateOtpResend(updateResendRequest);
//                                resend.setPhoneNumber(oldPhone);
                            } else {
                                UpdateResendRequest updateResendRequest = new UpdateResendRequest();
                                updateResendRequest.setEmail(false);
                                updateResendRequest.setNew(true);
                                updateResendRequest.setTrackerId(objMyApplication.getUpdatePhoneResponse().getData().getTrackerId());
                                loginViewModel.updateOtpResend(updateResendRequest);
//                                resend.setPhoneNumber(newPhone);
                            }
//                            loginViewModel.smsotpresend(resend);
                        }
                    } else {
                        if (strScreen.equals("SignUp")) {
                            layoutEntry.setVisibility(View.GONE);
                            layoutFailure.setVisibility(View.VISIBLE);
                        } else {
                            layoutEntry.setVisibility(View.VISIBLE);
                            layoutFailure.setVisibility(View.GONE);
                            Utils.displayAlert("Looks like we are having an issue with your OTP request, please retry again", OTPValidation.this, "", "");
                        }

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
//                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
//                                    dialog.setIndeterminate(false);
//                                    dialog.setMessage("Please wait...");
//                                    dialog.show();
                                    SmsRequest smsRequest = new SmsRequest();
                                    smsRequest.setEmail(EMAIL.trim());
                                    smsRequest.setOtp(charSequence.toString().trim());
                                    loginViewModel.emailotpValidate(smsRequest);
                                }
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("retEmail")) {
                                if (charSequence.length() == 6) {
                                    String strFirstName = "", strLastName = "";
                                    Utils.hideKeypad(OTPValidation.this);
//                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
//                                    dialog.setIndeterminate(false);
//                                    dialog.setMessage("Please wait...");
//                                    dialog.show();
                                    String phoneNumber = "";
                                    if (MOBILE.contains("(")) {
                                        phoneNumber = MOBILE.substring(1, 4) + MOBILE.substring(6, 9) + MOBILE.substring(10, MOBILE.length());
                                    } else {
                                        phoneNumber = MOBILE;
                                    }
                                    if (getIntent().getStringExtra("firstname") != null && !getIntent().getStringExtra("firstname").equals("")) {
                                        strFirstName = getIntent().getStringExtra("firstname");
                                    }
                                    if (getIntent().getStringExtra("lastname") != null && !getIntent().getStringExtra("lastname").equals("")) {
                                        strLastName = getIntent().getStringExtra("lastname");
                                    }
                                    RetrieveUsersRequest request = new RetrieveUsersRequest();
                                    request.setCountryCode(Utils.getStrCCode());
                                    request.setPhoneNumber(phoneNumber);
                                    request.setFirstName(strFirstName);
                                    request.setLastName(strLastName);
                                    loginViewModel.retrieveUsers(request, charSequence.toString().trim());
                                }
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("EditEmail")) {
                                if (charSequence.length() == 6) {
                                    Utils.hideKeypad(OTPValidation.this);
//                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
//                                    dialog.setIndeterminate(false);
//                                    dialog.setMessage("Please wait...");
//                                    dialog.show();
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
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("EditPhone")) {
                                if (charSequence.length() == 6) {
                                    Utils.hideKeypad(OTPValidation.this);
//                                    dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
//                                    dialog.setIndeterminate(false);
//                                    dialog.setMessage("Please wait...");
//                                    dialog.show();
                                    if (isOldPhone.equals("true")) {
                                        UpdatePhoneValidateRequest updatePhoneValidateRequest = new UpdatePhoneValidateRequest();
                                        updatePhoneValidateRequest.setCountryCode(Utils.getStrCCode());
                                        updatePhoneValidateRequest.setCurrentNumber(true);
                                        updatePhoneValidateRequest.setTrackerId(objMyApplication.getUpdatePhoneResponse().getData().getTrackerId());
                                        updatePhoneValidateRequest.setOtp(charSequence.toString().trim());
                                        updatePhoneValidateRequest.setPhoneNumber(oldPhone);
                                        loginViewModel.updatePhoneotpValidate(updatePhoneValidateRequest);
                                    } else {
                                        UpdatePhoneValidateRequest updatePhoneValidateRequest = new UpdatePhoneValidateRequest();
                                        updatePhoneValidateRequest.setCountryCode(Utils.getStrCCode());
                                        updatePhoneValidateRequest.setCurrentNumber(false);
                                        updatePhoneValidateRequest.setTrackerId(objMyApplication.getUpdatePhoneResponse().getData().getTrackerId());
                                        updatePhoneValidateRequest.setOtp(charSequence.toString().trim());
                                        updatePhoneValidateRequest.setPhoneNumber(newPhone);
                                        loginViewModel.updatePhoneotpValidate(updatePhoneValidateRequest);
                                    }
                                }
                            } else {
                                if (OTP_TYPE.equals("MOBILE")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
//                                        dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
//                                        dialog.setIndeterminate(false);
//                                        dialog.setMessage("Please wait...");
//                                        dialog.show();
                                        SmsRequest smsRequest = new SmsRequest();
                                        smsRequest.setEmail(EMAIL.trim());
                                        smsRequest.setOtp(charSequence.toString().trim());
                                        loginViewModel.smsotp(smsRequest);
                                    }
                                } else if (OTP_TYPE.equals("EMAIL")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
//                                        dialog = new ProgressDialog(OTPValidation.this, R.style.MyAlertDialogStyle);
//                                        dialog.setIndeterminate(false);
//                                        dialog.setMessage("Please wait...");
//                                        dialog.show();
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
                        .putExtra("screen", strScreen)
                        .putExtra("TYPE", "CHOOSE")
                        .putExtra(Utils.ACCOUNT_TYPE, mAccountType));
            });

//            layoutMain.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        Utils.hideKeypad(OTPValidation.this);
//                    }
//                    return false;
//                }
//            });

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
                try {
                    //dialog.dismiss();
                    if (emailResponse != null) {
                        Log.e("Email OTP Validate", new Gson().toJson(emailResponse));
                        if (emailResponse.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                        } else {
//                            objMyApplication.setUserId(emailResponse.getData().getUserId());
                            objMyApplication.setLoginUserId(emailResponse.getData().getUserId());
                            getStatesUrl(emailResponse.getData().getStateList().getUS());
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (strScreen != null && !strScreen.equals("")) {
                                            switch (strScreen) {
                                                case "ForgotPwd":
                                                    startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class)
                                                            .putExtra("screen", "ForgotPwd"));
                                                    break;
                                                case "ForgotPin":
                                                    startActivity(new Intent(OTPValidation.this, PINActivity.class).putExtra("TYPE", "CHOOSE")
                                                            .putExtra("screen", getIntent().getStringExtra("screen")));
                                                    finish();
                                                    break;
                                                case "SignUp":
                                                    if (OTP_TYPE.equals("EMAIL")) {
                                                        Utils.hideKeypad(OTPValidation.this, otpPV.getRootView());
                                                        Utils.setStrAuth(emailResponse.getData().getJwtToken());
                                                        secureAccountRL.setVisibility(View.VISIBLE);
                                                        layoutMain.setClickable(false);
                                                        layoutMain.setEnabled(false);
                                                        layoutType = "SECURE";
                                                        layoutEntry.setVisibility(View.GONE);
                                                        layoutFailure.setVisibility(View.GONE);
                                                        //saveFirstUser();
                                                    }
                                                    break;
                                            }
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getSmsotpLiveData().observe(this, new Observer<SMSValidate>() {
            @Override
            public void onChanged(SMSValidate smsValidate) {
                try {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
                    if (smsValidate != null) {
                        if (smsValidate.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                            if (strScreen != null && strScreen.equals("login")) {
                                if (resendCounter >= 5) {
                                    Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", OTPValidation.this, "Error", "");
                                }
                            } else {
                                if (smsValidate.getError().getErrorDescription().toLowerCase().contains("twilio") ||
                                        smsValidate.getError().getErrorDescription().toLowerCase().contains("resend")) {
                                    try {
                                        if (smsValidate.getError().getErrorDescription().equals("")) {
                                            Utils.displayAlert(smsValidate.getError().getFieldErrors().get(0), OTPValidation.this, "", smsValidate.getError().getFieldErrors().get(0));
                                        } else {
                                            Utils.displayAlert(smsValidate.getError().getErrorDescription(), OTPValidation.this, "", smsValidate.getError().getFieldErrors().get(0));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            if (strScreen != null && !strScreen.equals("")) {
                                switch (strScreen) {
                                    case "SignUp":
                                        if (OTP_TYPE.equals("MOBILE")) {
                                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                            shakeAnimateUpDown();
                                            resendCounter = 0;
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                                                .putExtra("screen", "SignUp")
                                                                .putExtra("OTP_TYPE", "EMAIL")
                                                                .putExtra("MOBILE", MOBILE)
                                                                .putExtra("EMAIL", EMAIL));
                                                        finish();
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            }, Utils.duration);
                                        }
                                        break;
                                    case "login":
                                        secureAccountRL.setVisibility(View.VISIBLE);
                                        layoutMain.setClickable(false);
                                        layoutMain.setEnabled(false);
                                        layoutType = "SECURE";
                                        layoutEntry.setVisibility(View.GONE);
                                        layoutFailure.setVisibility(View.GONE);
                                        break;
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loginViewModel.getEmailValidateResponseMutableLiveData().observe(this, new Observer<EmailValidateResponse>() {
            @Override
            public void onChanged(EmailValidateResponse emailValidateResponse) {
                try {
                    //dialog.dismiss();
                    if (emailValidateResponse != null) {
                        if (emailValidateResponse.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class).putExtra("code", emailValidateResponse.getData().getCode()));
                                        finish();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
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
                        resendTV.setVisibility(View.GONE);
                        newCodeTV.setVisibility(View.VISIBLE);
                        resendCounter++;
                        startTimer();
                        if (strScreen != null && strScreen.equals("login")) {
                            if (resendCounter >= 5) {
                                Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", OTPValidation.this, "Error", "");
                            }
                        }
                    } else {
                        try {
                            if (smsResponse.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(smsResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", smsResponse.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(smsResponse.getError().getErrorDescription(), OTPValidation.this, "", smsResponse.getError().getFieldErrors().get(0));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                try {
                    dialog.dismiss();
                    if (emailResponse != null) {
                        if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                            resendTV.setVisibility(View.GONE);
                            newCodeTV.setVisibility(View.VISIBLE);
                            resendCounter++;
                            startTimer();
                        } else {
                            try {
                                if (emailResponse.getError().getErrorDescription().equals("")) {
                                    Utils.displayAlert(emailResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                } else {
                                    Utils.displayAlert(emailResponse.getError().getErrorDescription(), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                        layoutMain.setClickable(false);
                        layoutMain.setEnabled(false);
                        layoutType = "SECURE";
                        layoutEntry.setVisibility(View.GONE);
                        layoutFailure.setVisibility(View.GONE);
                    } else {
                        Utils.displayAlert(initializeCustomerResponse.getError().getErrorDescription(), OTPValidation.this, "", initializeCustomerResponse.getError().getFieldErrors().get(0));
                    }
                }
            }
        });

        loginViewModel.getRetrieveUsersResponseMutableLiveData().observe(this, new Observer<RetrieveUsersResponse>() {
            @Override
            public void onChanged(RetrieveUsersResponse retrieveUsersResponse) {
                try {
                    //dialog.dismiss();
                    if (retrieveUsersResponse != null) {
                        if (!retrieveUsersResponse.getStatus().toLowerCase().equals("error")) {
                            objMyApplication.setObjRetUsers(retrieveUsersResponse);
                            startActivity(new Intent(OTPValidation.this, BindingLayoutActivity.class)
                                    .putExtra("screen", "retEmailfound"));
                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
//                            Utils.displayAlert(retrieveUsersResponse.getError().getErrorDescription(), OTPValidation.this, "");
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
                        Utils.displayAlert(apiError.getError().getErrorDescription(), OTPValidation.this, "", apiError.getError().getFieldErrors().get(0));
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), OTPValidation.this, "", apiError.getError().getFieldErrors().get(0));
                    }
                }
            }
        });

        loginViewModel.getUpdateEmailValidateResponse().observe(this, new Observer<UpdateEmailResponse>() {
            @Override
            public void onChanged(UpdateEmailResponse updateEmailResponse) {
                try {
                    //dialog.dismiss();
                    if (updateEmailResponse != null) {
                        if (updateEmailResponse.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            resendCounter = 0;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (getIntent().getStringExtra("IS_OLD_EMAIL").equalsIgnoreCase("true")) {
                                            startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                                    .putExtra("screen", "EditEmail")
                                                    .putExtra("OTP_TYPE", "OTP")
                                                    .putExtra("IS_OLD_EMAIL", "false")
                                                    .putExtra("OLD_EMAIL", oldEmail)
                                                    .putExtra("NEW_EMAIL", newEmail));
                                            finish();
                                        } else {
                                            startActivity(new Intent(OTPValidation.this, BindingLayoutActivity.class)
                                                    .putExtra("screen", "EditEmail"));
                                            finish();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getUpdatePhoneValidateResponse().observe(this, new Observer<UpdatePhoneResponse>() {
            @Override
            public void onChanged(UpdatePhoneResponse updatePhoneResponse) {
                try {
                    //dialog.dismiss();
                    if (updatePhoneResponse != null) {
                        if (updatePhoneResponse.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            resendCounter = 0;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (getIntent().getStringExtra("IS_OLD_PHONE").equalsIgnoreCase("true")) {
                                            startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                                    .putExtra("screen", "EditPhone")
                                                    .putExtra("OTP_TYPE", "OTP")
                                                    .putExtra("IS_OLD_PHONE", "false")
                                                    .putExtra("OLD_PHONE_MASKED", oldPhoneMasked)
                                                    .putExtra("NEW_PHONE_MASKED", newPhoneMasked)
                                                    .putExtra("OLD_PHONE", oldPhone)
                                                    .putExtra("NEW_PHONE", newPhone));
                                            finish();
                                        } else {
                                            try {
                                                if (EditPhoneActivity.editPhoneActivity != null) {
                                                    EditPhoneActivity.editPhoneActivity.finish();
                                                }
                                                finish();
                                                Utils.showCustomToast(UserDetailsActivity.userDetailsActivity, "Phone number updated", R.drawable.ic_phone, "EditPhone");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getUpdateResendOTPMutableLiveData().observe(this, new Observer<UpdateResendOTPResponse>() {
            @Override
            public void onChanged(UpdateResendOTPResponse emailResponse) {
                try {
                    dialog.dismiss();
                    if (emailResponse != null) {
                        if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                            resendTV.setVisibility(View.GONE);
                            newCodeTV.setVisibility(View.VISIBLE);
                            resendCounter++;
                            startTimer();
                        } else {
                            try {
                                if (emailResponse.getError().getErrorDescription().equals("")) {
                                    Utils.displayAlert(emailResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                } else {
                                    Utils.displayAlert(emailResponse.getError().getErrorDescription(), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void shakeAnimateLeftRight() {
        try {
            otpPV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        otpPV.setText("");
                        otpPV.requestFocus();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 2000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shakeAnimateUpDown() {
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
        otpPV.requestFocus();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!layoutType.equals("SECURE")) {
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(OTPValidation.this);
//                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
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
//                    Intent intent = new Intent(OTPValidation.this, OnboardActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
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

    private void SetDB() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, email TEXT);");
            }
        }
    }

    private void saveFirstUser() {
        try {
            mydatabase.execSQL("Delete from tblUserDetails");
            mydatabase.execSQL("INSERT INTO tblUserDetails(id,email) VALUES(null,'" + EMAIL.toLowerCase() + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getStatesUrl(String strCode) {
        try {
            byte[] valueDecoded = new byte[0];
            valueDecoded = Base64.decode(strCode.getBytes("UTF-8"), Base64.DEFAULT);
            objMyApplication.setStrStatesUrl(new String(valueDecoded));
            Log.e("States url", objMyApplication.getStrStatesUrl() + "   sdssd");
            try {
                new HttpGetRequest().execute("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(objMyApplication.getStrStatesUrl());
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(result, type);
            objMyApplication.setListStates(listStates);
//            Log.e("result", result);
        }
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
        Log.e("isKeyboardVisible", Utils.isKeyboardVisible + "");
    }

}
package com.coyni.mapp.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.model.coynipin.StepUpOTPResponse;
import com.coyni.mapp.model.register.OTPResendRequest;
import com.coyni.mapp.model.register.OTPValidateRequest;
import com.coyni.mapp.model.register.OTPValidateResponse;
import com.coyni.mapp.view.business.AcceptAgreementsActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.R;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.EmailRequest;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.forgotpassword.EmailValidateResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailValidateRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneResponse;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneValidateRequest;
import com.coyni.mapp.model.register.EmailResendResponse;
import com.coyni.mapp.model.register.EmailResponse;
import com.coyni.mapp.model.register.InitializeCustomerResponse;
import com.coyni.mapp.model.register.SMSResend;
import com.coyni.mapp.model.register.SMSResponse;
import com.coyni.mapp.model.register.SMSValidate;
import com.coyni.mapp.model.register.SmsRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersResponse;
import com.coyni.mapp.model.update_resend_otp.UpdateResendOTPResponse;
import com.coyni.mapp.model.update_resend_otp.UpdateResendRequest;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.otpview.PinView;
import com.coyni.mapp.viewmodel.LoginViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPValidation extends BaseActivity implements OnKeyboardVisibilityListener {
    TextView resendTV, newCodeTV, subHeaderTV, headerTV, contactUsTV;
    PinView otpPV;
    ImageView otpValidationCloseIV;
    int resendCounter = 0;
    private Vibrator vibrator;
    String OTP_TYPE = "", MOBILE = "", EMAIL = "", strScreen = "", maskedPhone = "",
            oldEmail = "", newEmail = "", isOldEmail = "", oldPhone = "", newPhone = "", oldPhoneMasked = "", newPhoneMasked = "", isOldPhone = "";
    LinearLayout layoutFailure, layoutMain, contactUsLL;
    RelativeLayout layoutEntry;
    CardView tryAgainCV;
    Dialog dialog;
    LoginViewModel loginViewModel;
    RelativeLayout secureAccountRL;
    CardView secureNextCV;
    MyApplication objMyApplication;
    //    SQLiteDatabase mydatabase;
    Cursor dsUserDetails;
    String strFirstUser = "";
    private int mAccountType = Utils.PERSONAL_ACCOUNT;
    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code

    String layoutType = "OTP"; //SECURE: if VISIBLITY ON FOR SECURE ACCOUNT SCREEN AFTER API CALL
    Long mLastClickTime = 0L;
    private static boolean isActivityVisible = false;
    private boolean isBackEnabled = true;

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
//            registerReceiver(smsVerificationReceiver, intentFilter);
            registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);

            layoutType = "OTP";
            OTP_TYPE = getIntent().getStringExtra("OTP_TYPE");
            MOBILE = getIntent().getStringExtra("MOBILE");
            EMAIL = getIntent().getStringExtra("EMAIL");
            strScreen = getIntent().getStringExtra("screen");
            if (getIntent() != null) {
                mAccountType = getIntent().getIntExtra(Utils.ACCOUNT_TYPE, Utils.PERSONAL_ACCOUNT);
            }
//            SetDB();
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
            contactUsLL = findViewById(R.id.contactUsLL);
            tryAgainCV = findViewById(R.id.tryAgainCV);
            secureAccountRL = findViewById(R.id.secureAccountRL);
            secureNextCV = findViewById(R.id.secureNextCV);
            contactUsTV = findViewById(R.id.contactUsTV);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            layoutEntry.setVisibility(View.VISIBLE);
            layoutFailure.setVisibility(View.GONE);

            resendTV.setPaintFlags(resendTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            otpPV.setAnimationEnable(true);
            objMyApplication = (MyApplication) getApplicationContext();
            if (EMAIL != null && !EMAIL.equals(""))
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
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            Utils.hideKeypad(OTPValidation.this, otpPV.getRootView());
                            secureAccountRL.setVisibility(View.VISIBLE);
                            layoutMain.setClickable(false);
                            layoutMain.setEnabled(false);
                            layoutType = "SECURE";
                            layoutEntry.setVisibility(View.GONE);
                            layoutFailure.setVisibility(View.GONE);
                        }
                        break;
                    case "login_SET_PIN":
                        maskedPhone = getIntent().getStringExtra("MASK_MOBILE");
                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
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
                            contactUsLL.setVisibility(View.VISIBLE);
                            headerTV.setText("Please Verify your Current Email");
                            subHeaderTV.setText("We have sent you a 6-digit code to the registered email address: " + oldEmail);
                        } else {
                            contactUsLL.setVisibility(View.GONE);
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
                        contactUsLL.setVisibility(View.VISIBLE);

                        otpValidationCloseIV.setImageResource(R.drawable.ic_back);
                        if (isOldPhone.equals("true")) {
                            contactUsLL.setVisibility(View.VISIBLE);
                            headerTV.setText("Please Verify your Current Phone Number");
                            subHeaderTV.setText("We have sent you a 6-digit code to the registered phone number " + oldPhoneMasked);
                        } else {
                            contactUsLL.setVisibility(View.GONE);
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
//                    if (resendCounter < 4) {
                    Utils.hideKeypad(OTPValidation.this, view);
                    if ((strScreen != null && !strScreen.equals("") && (strScreen.equals("ForgotPwd") || strScreen.equals("ForgotPin") || strScreen.equals("EditEmail"))) || (OTP_TYPE.equals("EMAIL"))) {
                        dialog = Utils.showProgressDialog(OTPValidation.this);
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
                        } else if (strScreen.equals("SignUp")) {
                            OTPResendRequest resend = new OTPResendRequest();
                            resend.setToken(objMyApplication.getStrRegisToken());
                            loginViewModel.regEmailOTPResend(resend);
                        } else {
                            EmailRequest emailRequest = new EmailRequest();
                            emailRequest.setEmail(EMAIL.trim());
                            loginViewModel.emailotpresend(emailRequest);
//                            loginViewModel.emailotpresend(EMAIL.trim());
                        }

                    } else if (OTP_TYPE.equals("MOBILE")) {
                        dialog = Utils.showProgressDialog(this);
                        if (strScreen.equals("SignUp")) {
                            OTPResendRequest resend = new OTPResendRequest();
                            resend.setToken(objMyApplication.getStrRegisToken());
                            loginViewModel.regPhoneOTPResend(resend);
                        } else {
                            SMSResend resend = new SMSResend();
                            resend.setCountryCode(Utils.getStrCCode());
                            resend.setPhoneNumber(MOBILE);
                            loginViewModel.smsotpresend(resend);
                        }
                    } else if (strScreen.equals("EditPhone")) {
                        dialog = Utils.showProgressDialog(this);
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
//                    } else {
//                        Utils.hideKeypad(OTPValidation.this, view);
//                        if (strScreen.equals("SignUp")) {
//                            layoutEntry.setVisibility(View.GONE);
//                            layoutFailure.setVisibility(View.VISIBLE);
//                            isBackEnabled = false;
//                        } else {
//                            layoutEntry.setVisibility(View.VISIBLE);
//                            layoutFailure.setVisibility(View.GONE);
//                            displayAlertNew("Looks like we are having an issue with your OTP request, please retry again", OTPValidation.this, "", strScreen);
//                        }
//
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            otpValidationCloseIV.setOnClickListener(view -> {
                if (isBackEnabled)
                    finish();
                if (Utils.isKeyboardVisible) {
                    Utils.hideKeypad(OTPValidation.this);
                }
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
//                            if (strScreen != null && !strScreen.equals("") && (strScreen.equals("ForgotPwd") || strScreen.equals("ForgotPin"))) {
                            if (strScreen != null && !strScreen.equals("") && strScreen.equals("ForgotPwd")) {
                                if (charSequence.length() == 6) {
                                    Utils.hideKeypad(OTPValidation.this);
                                    SmsRequest smsRequest = new SmsRequest();
                                    smsRequest.setEmail(EMAIL.trim());
                                    smsRequest.setOtp(charSequence.toString().trim());
                                    loginViewModel.emailotpValidate(smsRequest);
                                    isBackEnabled = false;
                                }
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("ForgotPin")) {
                                if (charSequence.length() == 6) {
                                    Utils.hideKeypad(OTPValidation.this);
                                    SmsRequest smsRequest = new SmsRequest();
                                    smsRequest.setEmail(EMAIL.trim());
                                    smsRequest.setOtp(charSequence.toString().trim());
                                    if (getIntent().getStringExtra("screenFrom") != null && getIntent().getStringExtra("screenFrom").equals("login")) {
                                        loginViewModel.stepUpEmailOTP(smsRequest);
                                    } else {
                                        loginViewModel.emailotpValidate(smsRequest);
                                    }

                                    isBackEnabled = false;
                                }
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("retEmail")) {
                                if (charSequence.length() == 6) {
                                    String strFirstName = "", strLastName = "";
                                    Utils.hideKeypad(OTPValidation.this);
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
                            } else if (strScreen != null && !strScreen.equals("") && strScreen.equals("login_SET_PIN")) {
                                if (OTP_TYPE.equals("MOBILE")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
                                        SmsRequest smsRequest = new SmsRequest();
                                        smsRequest.setEmail(EMAIL.trim());
                                        smsRequest.setOtp(charSequence.toString().trim());
//                                        loginViewModel.smsotpLoginValidate(smsRequest);
                                        loginViewModel.stepUpPhoneOTP(smsRequest);
                                    }
                                }
                            } else {
                                if (OTP_TYPE.equals("MOBILE")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
//                                        SmsRequest smsRequest = new SmsRequest();
//                                        smsRequest.setEmail(EMAIL.trim());
//                                        smsRequest.setOtp(charSequence.toString().trim());
//                                        smsRequest.setToken(objMyApplication.getStrRegisToken());
//                                        loginViewModel.smsotp(smsRequest);
                                        OTPValidateRequest OTPValidateRequest = new OTPValidateRequest();
                                        OTPValidateRequest.setOtp(charSequence.toString().trim());
                                        OTPValidateRequest.setToken(objMyApplication.getStrRegisToken());
                                        loginViewModel.validateRegisterMobileOTP(OTPValidateRequest);
                                    }
                                } else if (OTP_TYPE.equals("EMAIL")) {
                                    if (charSequence.length() == 6) {
                                        Utils.hideKeypad(OTPValidation.this);
//                                        SmsRequest smsRequest = new SmsRequest();
//                                        smsRequest.setEmail(EMAIL.trim());
//                                        smsRequest.setOtp(charSequence.toString().trim());
//                                        smsRequest.setToken(objMyApplication.getStrRegisToken());
//                                        loginViewModel.emailotp(smsRequest);

                                        OTPValidateRequest OTPValidateRequest = new OTPValidateRequest();
                                        OTPValidateRequest.setOtp(charSequence.toString().trim());
                                        OTPValidateRequest.setToken(objMyApplication.getStrRegisToken());
                                        loginViewModel.validateRegisterEmailOTP(OTPValidateRequest);
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
                    Utils.setStrAuth("");
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

            contactUsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(Utils.mondayURL));
//                    startActivity(i);
                    startActivity(new Intent(OTPValidation.this, GetHelpActivity.class));
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
                                try {
                                    resendTV.setVisibility(View.VISIBLE);
                                    newCodeTV.setVisibility(View.GONE);
                                    if (!Utils.isKeyboardVisible)
                                        Utils.shwForcedKeypad(OTPValidation.this);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
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

        loginViewModel.getRegEmailOTPValidateLiveData().observe(this, new Observer<OTPValidateResponse>() {
            @Override
            public void onChanged(OTPValidateResponse emailResponse) {
                try {
                    //dialog.dismiss();
                    if (emailResponse != null) {
                        Log.e("Email OTP Validate", new Gson().toJson(emailResponse));
                        if (emailResponse.getStatus().toLowerCase().equals("error")) {
                            try {
                                if (emailResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                    showAlert(emailResponse.getError().getErrorDescription());
                                } else {
                                    otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                    shakeAnimateLeftRight();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (strScreen != null && !strScreen.equals("")) {
                                            switch (strScreen) {
                                                case "SignUp":
                                                    if (OTP_TYPE.equals("EMAIL")) {
                                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                                        Utils.hideKeypad(OTPValidation.this, otpPV.getRootView());
//                                                        Utils.setStrAuth(emailResponse.getData().getJwtToken());
//                                                        secureAccountRL.setVisibility(View.VISIBLE);
//                                                        layoutMain.setClickable(false);
//                                                        layoutMain.setEnabled(false);
//                                                        layoutType = "SECURE";
//                                                        layoutEntry.setVisibility(View.GONE);
//                                                        layoutFailure.setVisibility(View.GONE);
                                                        objMyApplication.setStrRegisToken(emailResponse.getData().getToken());
                                                        startActivity(new Intent(OTPValidation.this, AcceptAgreementsActivity.class)
                                                                .putExtra(Utils.AGREEMENT_TYPE, Utils.cTOS)
                                                                .putExtra(Utils.ACT_TYPE, Utils.multiple));
                                                        finish();
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
                                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    if (smsValidate != null) {
                        if (smsValidate.getStatus().toLowerCase().equals("error")) {
                            otpPV.setLineColor(getResources().getColor(R.color.error_red));
                            shakeAnimateLeftRight();
                            if (strScreen != null && strScreen.equals("login_SET_PIN")) {
                                if (resendCounter >= 5) {
                                    Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", OTPValidation.this, "Error", "");
                                }
                            } else {
//                                if (smsValidate.getError().getErrorDescription().toLowerCase().contains("twilio") ||
//                                        smsValidate.getError().getErrorDescription().toLowerCase().contains("resend")) {
                                if (smsValidate.getError().getErrorDescription().toLowerCase().contains("twilio")) {
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
                                    case "login_SET_PIN":
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
//                                        startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class).putExtra("code", emailValidateResponse.getData().getCode()));
//                                        finish();
                                        if (strScreen != null && !strScreen.equals("")) {
                                            switch (strScreen) {
                                                case "ForgotPwd":
                                                    startActivity(new Intent(OTPValidation.this, CreatePasswordActivity.class)
                                                            .putExtra("code", emailValidateResponse.getData().getCode())
                                                            .putExtra("screen", "ForgotPwd"));
                                                    break;
                                                case "ForgotPin":
                                                    startActivity(new Intent(OTPValidation.this, PINActivity.class).putExtra("TYPE", "CHOOSE")
                                                            .putExtra("screen", getIntent().getStringExtra("screen"))
                                                            .putExtra("screenFrom", getIntent().getStringExtra("screenFrom")));
                                                    break;
                                            }
                                            finish();
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
                        if (strScreen != null && strScreen.equals("login_SET_PIN")) {
                            if (resendCounter >= 5) {
                                Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", OTPValidation.this, "Error", "");
                            }
                        }
                    } else {
                        try {
                            if (smsResponse.getError().getErrorDescription().equals("")) {
                                if (!strScreen.equals("retEmail"))
                                    Utils.displayAlert(smsResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", smsResponse.getError().getFieldErrors().get(0));
                                else
                                    displayAlertNew(smsResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", strScreen);
                            } else {
                                if (!strScreen.equals("retEmail")) {
                                    if (smsResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(smsResponse.getError().getErrorDescription());
                                    } else
                                        Utils.displayAlert(smsResponse.getError().getErrorDescription(), OTPValidation.this, "", smsResponse.getError().getFieldErrors().get(0));
                                } else {
                                    if (smsResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(smsResponse.getError().getErrorDescription());
                                    } else
                                        displayAlertNew(smsResponse.getError().getErrorDescription(), OTPValidation.this, "", strScreen);
                                }
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
                                    if (emailResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(emailResponse.getError().getErrorDescription());
                                    } else {
                                        Utils.displayAlert(emailResponse.getError().getErrorDescription(), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                    }
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
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    if (retrieveUsersResponse != null) {
                        if (!retrieveUsersResponse.getStatus().toLowerCase().equals("error")) {
                            objMyApplication.setObjRetUsers(retrieveUsersResponse);
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        startActivity(new Intent(OTPValidation.this, BindingLayoutActivity.class)
                                                .putExtra("screen", "retEmailfound"));
                                        finish();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
//                            startActivity(new Intent(OTPValidation.this, BindingLayoutActivity.class)
//                                    .putExtra("screen", "retEmailfound"));
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
                            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
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
                                                        .putExtra("EMAIL", oldEmail)
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
                            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                                if (getIntent().getStringExtra("IS_OLD_EMAIL").equalsIgnoreCase("true")) {
                                    startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                            .putExtra("screen", "EditEmail")
                                            .putExtra("OTP_TYPE", "OTP")
                                            .putExtra("IS_OLD_EMAIL", "false")
                                            .putExtra("OLD_EMAIL", oldEmail)
                                            .putExtra("EMAIL", oldEmail)
                                            .putExtra("NEW_EMAIL", newEmail));
                                    finish();
                                } else {

                                    startActivity(new Intent(OTPValidation.this, BindingLayoutActivity.class)
                                            .putExtra("screen", "EditEmail"));
                                    finish();

//                                    Utils.showCustomToast(OTPValidation.this, "Email updated", R.drawable.ic_check, "EMAIL");
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                finish();
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }
//                                    }, 2000);
                                }
                            }
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
                            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
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
                                                    if (BusinessUserDetailsPreviewActivity.businessUserDetailsPreviewActivity != null && objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                                                        BusinessUserDetailsPreviewActivity.businessUserDetailsPreviewActivity.finish();
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
                            } else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
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
                                                Utils.showCustomToast(OTPValidation.this, "Phone number updated", R.drawable.ic_phone, "EditPhone");
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            finish();
                                                            if (EditPhoneActivity.editPhoneActivity != null) {
                                                                EditPhoneActivity.editPhoneActivity.finish();
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }, 2000);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, Utils.duration);
                            }
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
                                    displayAlertNew(emailResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", strScreen);
                                } else {
                                    displayAlertNew(emailResponse.getError().getErrorDescription(), OTPValidation.this, "", strScreen);
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

        loginViewModel.getStepUpEmailOTPResponseMutableLiveData().observe(this, new Observer<StepUpOTPResponse>() {
            @Override
            public void onChanged(StepUpOTPResponse stepUpOTPResponse) {
                if (stepUpOTPResponse != null) {
                    if (!stepUpOTPResponse.getStatus().toLowerCase().equals("error")) {
                        otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                        shakeAnimateUpDown();
                        Utils.setStrAuth(stepUpOTPResponse.getData().getJwtToken());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (strScreen != null && !strScreen.equals("")) {
                                        switch (strScreen) {
                                            case "ForgotPin":
                                                startActivity(new Intent(OTPValidation.this, PINActivity.class).putExtra("TYPE", "CHOOSE")
                                                        .putExtra("screen", getIntent().getStringExtra("screen"))
                                                        .putExtra("screenFrom", getIntent().getStringExtra("screenFrom")));
                                                break;
                                        }
                                        finish();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, Utils.duration);
                    } else {
                        otpPV.setLineColor(getResources().getColor(R.color.error_red));
                        shakeAnimateLeftRight();
                    }
                }
            }
        });

        loginViewModel.getStepUpPhoneOTPResponseMutableLiveData().observe(this, new Observer<StepUpOTPResponse>() {
            @Override
            public void onChanged(StepUpOTPResponse stepUpOTPResponse) {
                if (stepUpOTPResponse != null) {
                    if (!stepUpOTPResponse.getStatus().toLowerCase().equals("error")) {
                        if (strScreen != null && !strScreen.equals("") && strScreen.equals("login_SET_PIN")) {
                            otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                            shakeAnimateUpDown();
                            Utils.setStrAuth(stepUpOTPResponse.getData().getJwtToken());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                        secureAccountRL.setVisibility(View.VISIBLE);
                                        layoutMain.setClickable(false);
                                        layoutMain.setEnabled(false);
                                        layoutType = "SECURE";
                                        layoutEntry.setVisibility(View.GONE);
                                        layoutFailure.setVisibility(View.GONE);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
                        }
                    } else {
                        otpPV.setLineColor(getResources().getColor(R.color.error_red));
                        shakeAnimateLeftRight();
                    }
                }
            }
        });

        loginViewModel.getRegMobileOTPLiveData().observe(this, new Observer<OTPValidateResponse>() {
            @Override
            public void onChanged(OTPValidateResponse smsValidate) {
                try {
                    if (smsValidate != null) {
                        if (smsValidate.getStatus().toLowerCase().equals("error")) {
                            if (strScreen != null && strScreen.equals("login_SET_PIN")) {
                                if (resendCounter >= 5) {
                                    Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", OTPValidation.this, "Error", "");
                                }
                            } else {
                                try {
                                    if (smsValidate.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(smsValidate.getError().getErrorDescription());
                                    } else {
                                        otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                        shakeAnimateLeftRight();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
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
                                                        if (smsValidate.getData().getToken() != null) {
                                                            objMyApplication.setStrRegisToken(smsValidate.getData().getToken());
                                                        }
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
                                    case "login_SET_PIN":
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        loginViewModel.getRegPhoneOTPResendLiveData().observe(this, new Observer<OTPValidateResponse>() {
            @Override
            public void onChanged(OTPValidateResponse smsResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (smsResponse != null) {
                    if (smsResponse.getStatus().toLowerCase().toString().equals("success")) {
                        resendTV.setVisibility(View.GONE);
                        newCodeTV.setVisibility(View.VISIBLE);
                        resendCounter++;
                        startTimer();
                        if (smsResponse.getData().getToken() != null) {
                            objMyApplication.setStrRegisToken(smsResponse.getData().getToken());
                        }
                        if (strScreen != null && strScreen.equals("login_SET_PIN")) {
                            if (resendCounter >= 5) {
                                Utils.displayAlert("You have exceeded maximum OTP verification attempts hence locking your account for 10 minutes. Try after 10 minutes to resend OTP.", OTPValidation.this, "Error", "");
                            }
                        }
                    } else {
                        try {
                            if (smsResponse.getError().getErrorDescription().equals("")) {
                                if (!strScreen.equals("retEmail"))
                                    Utils.displayAlert(smsResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", smsResponse.getError().getFieldErrors().get(0));
                                else
                                    displayAlertNew(smsResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", strScreen);
                            } else {
                                if (!strScreen.equals("retEmail")) {
                                    if (smsResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(smsResponse.getError().getErrorDescription());
                                    } else
                                        Utils.displayAlert(smsResponse.getError().getErrorDescription(), OTPValidation.this, "", smsResponse.getError().getFieldErrors().get(0));
                                } else {
                                    if (smsResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(smsResponse.getError().getErrorDescription());
                                    } else
                                        displayAlertNew(smsResponse.getError().getErrorDescription(), OTPValidation.this, "", strScreen);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        loginViewModel.getRegEmailOTPResendLiveData().observe(this, new Observer<OTPValidateResponse>() {
            @Override
            public void onChanged(OTPValidateResponse emailResponse) {
                try {
                    dialog.dismiss();
                    if (emailResponse != null) {
                        if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                            resendTV.setVisibility(View.GONE);
                            newCodeTV.setVisibility(View.VISIBLE);
                            resendCounter++;
                            startTimer();
                            if (emailResponse.getData().getToken() != null) {
                                objMyApplication.setStrRegisToken(emailResponse.getData().getToken());
                            }
                        } else {
                            try {
                                if (emailResponse.getError().getErrorDescription().equals("")) {
                                    Utils.displayAlert(emailResponse.getError().getFieldErrors().get(0), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                } else {
                                    if (emailResponse.getError().getErrorDescription().equals(getString(R.string.otp_retry_error))) {
                                        showAlert(emailResponse.getError().getErrorDescription());
                                    } else {
                                        Utils.displayAlert(emailResponse.getError().getErrorDescription(), OTPValidation.this, "", emailResponse.getError().getFieldErrors().get(0));
                                    }
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
                        if (isActivityVisible) {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                            isBackEnabled = true;
                        }
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
        isBackEnabled = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
        otpPV.requestFocus();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!layoutType.equals("SECURE")) {
                    if (!Utils.isKeyboardVisible)
//                        Utils.shwForcedKeypad(OTPValidation.this);
                        showSoftKeyboard(otpPV);
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
            if (isBackEnabled) {
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

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        isActivityVisible = false;
    }

    public static void displayAlertNew(String msg, final Context context, String headerText, String screen) {
        // custom dialog
        Dialog displayAlertDialog = new Dialog(context);
        displayAlertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        displayAlertDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        displayAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = displayAlertDialog.findViewById(R.id.tvHead);
        TextView message = displayAlertDialog.findViewById(R.id.tvMessage);
        CardView actionCV = displayAlertDialog.findViewById(R.id.cvAction);
        TextView actionText = displayAlertDialog.findViewById(R.id.tvAction);

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog.dismiss();
                try {
                    if (screen.equals("EditEmail")) {
                        EditEmailActivity.editEmailActivity.finish();
                        Activity activity = (Activity) context;
                        activity.finish();
                    } else if (screen.equals("EditPhone")) {
                        EditPhoneActivity.editPhoneActivity.finish();
                        Activity activity = (Activity) context;
                        activity.finish();
                    } else if (screen.equals("ForgotPwd") || screen.equals("retEmail")) {
                        Activity activity = (Activity) context;
                        activity.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        message.setText(msg);
        Window window = displayAlertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        displayAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        displayAlertDialog.setCanceledOnTouchOutside(true);
        displayAlertDialog.show();
    }

    public void showAlert(String message) {
        if (strScreen.equals("SignUp")) {
            layoutEntry.setVisibility(View.GONE);
            layoutFailure.setVisibility(View.VISIBLE);
            isBackEnabled = false;
        } else {
            Utils.displayAlert(message, OTPValidation.this, "", "");
        }
    }

}
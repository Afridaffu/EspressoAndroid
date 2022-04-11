package com.greenbox.coyni.view;


import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.buytoken.BuyTokenResponse;
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.StepUpResponse;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.paidorder.PaidOrderRequest;
import com.greenbox.coyni.model.paidorder.PaidOrderResp;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;

public class PINActivity extends BaseActivity implements View.OnClickListener {
    View chooseCircleOne, chooseCircleTwo, chooseCircleThree, chooseCircleFour, chooseCircleFive, chooseCircleSix;
    TextView keyZeroTV, keyOneTV, keyTwoTV, keyThreeTV, keyFourTV, keyFiveTV, keySixTV, keySevenTV, keyEightTV, keyNineTV;
    ImageView backActionIV, imgBack;
    String passcode = "", strChoose = "", strConfirm = "", TYPE, strScreen = "";
    TextView tvHead, tvForgot;
    CoyniViewModel coyniViewModel;
    ProgressDialog pDialog;
    LinearLayout circleOneLL, circleTwoLL, circleThreeLL, circleFourLL, circleFiveLL, circleSixLL, pinLL;
    MyApplication objMyApplication;
    SQLiteDatabase mydatabase;
    private int mAccountType = Utils.PERSONAL_ACCOUNT;
    Cursor dsDontRemind;
    Boolean isDontRemind = false;
    String resetPINValue = "ENTER";
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    Dialog prevDialog;
    private DatabaseHandler dbHandler;
    LoginViewModel loginViewModel;
    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_pin_keyboard);
            initializeComponents();
            mAccountType = getIntent().getIntExtra(Utils.ACCOUNT_TYPE, Utils.PERSONAL_ACCOUNT);
            TYPE = getIntent().getStringExtra("TYPE");
            switch (TYPE) {
                case "CHOOSE":
                    tvHead.setText("Choose your PIN");
                    tvForgot.setVisibility(View.GONE);
                    break;
                case "CONFIRM":
                    tvForgot.setVisibility(View.GONE);
                    break;
                case "ENTER":
                    tvHead.setText("Enter your PIN");
                    tvForgot.setVisibility(View.VISIBLE);
                    tvForgot.setText(Html.fromHtml("<u>Forgot PIN</u>"));
                    break;
            }
            strScreen = getIntent().getStringExtra("screen");
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            SetDontRemind();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try {
            switch (TYPE) {
                case "CHOOSE":
                    super.onBackPressed();
                    break;
                case "ENTER": {
                    super.onBackPressed();
                    break;
                }
                case "CONFIRM":
                    tvForgot.setVisibility(View.GONE);
                    TYPE = "CHOOSE";
                    tvHead.setText("Choose your PIN");
                    clearControls();
                    passcode = "";
                    if (getIntent().getStringExtra("AUTH_TYPE") != null && getIntent().getStringExtra("AUTH_TYPE").equals("TOUCH")) {
                        imgBack.setImageResource(R.drawable.ic_close);
                    } else{
                        imgBack.setImageResource(R.drawable.ic_back);
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeComponents() {
        try {
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            chooseCircleOne = (View) findViewById(R.id.chooseCircleOne);
            chooseCircleTwo = (View) findViewById(R.id.chooseCircleTwo);
            chooseCircleThree = (View) findViewById(R.id.chooseCircleThree);
            chooseCircleFour = (View) findViewById(R.id.chooseCircleFour);
            chooseCircleFive = (View) findViewById(R.id.chooseCircleFive);
            chooseCircleSix = (View) findViewById(R.id.chooseCircleSix);

            circleOneLL = findViewById(R.id.circleOneLL);
            circleTwoLL = findViewById(R.id.circleTwoLL);
            circleThreeLL = findViewById(R.id.circleThreeLL);
            circleFourLL = findViewById(R.id.circleFourLL);
            circleFiveLL = findViewById(R.id.circleFiveLL);
            circleSixLL = findViewById(R.id.circleSixLL);

            pinLL = findViewById(R.id.pinLL);

            keyZeroTV = (TextView) findViewById(R.id.keyZeroTV);
            keyOneTV = (TextView) findViewById(R.id.keyOneTV);
            keyTwoTV = (TextView) findViewById(R.id.keyTwoTV);
            keyThreeTV = (TextView) findViewById(R.id.keyThreeTV);
            keyFourTV = (TextView) findViewById(R.id.keyFourTV);
            keyFiveTV = (TextView) findViewById(R.id.keyFiveTV);
            keySixTV = (TextView) findViewById(R.id.keySixTV);
            keySevenTV = (TextView) findViewById(R.id.keySevenTV);
            keyEightTV = (TextView) findViewById(R.id.keyEightTV);
            keyNineTV = (TextView) findViewById(R.id.keyNineTV);
            tvHead = (TextView) findViewById(R.id.tvHead);
            tvForgot = (TextView) findViewById(R.id.tvForgot);
            backActionIV = (ImageView) findViewById(R.id.backActionIV);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            objMyApplication = (MyApplication) getApplicationContext();
            dbHandler = DatabaseHandler.getInstance(PINActivity.this);
            if (getIntent().getStringExtra("screen") != null && (getIntent().getStringExtra("screen").equals("login") ||
                    getIntent().getStringExtra("screen").equals("EditEmail") || getIntent().getStringExtra("screen").equals("EditPhone")
                    || getIntent().getStringExtra("screen").equals("EditAddress") || getIntent().getStringExtra("screen").equals("ResetPIN")
                    || getIntent().getStringExtra("screen").equals("Withdraw")
                    || getIntent().getStringExtra("screen").equals("Pay"))
                    || getIntent().getStringExtra("screen").equals("Notifications")
                    || getIntent().getStringExtra("screen").equals("Buy")
                    || getIntent().getStringExtra("screen").equals("Paid")) {
                imgBack.setImageResource(R.drawable.ic_close);
            } else {
                imgBack.setImageResource(R.drawable.ic_back);
            }
            keyZeroTV.setOnClickListener(this);
            keyOneTV.setOnClickListener(this);
            keyTwoTV.setOnClickListener(this);
            keyThreeTV.setOnClickListener(this);
            keyFourTV.setOnClickListener(this);
            keyFiveTV.setOnClickListener(this);
            keySixTV.setOnClickListener(this);
            keySevenTV.setOnClickListener(this);
            keyEightTV.setOnClickListener(this);
            keyNineTV.setOnClickListener(this);
            backActionIV.setOnClickListener(this);
            tvForgot.setOnClickListener(this);
            imgBack.setOnClickListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {

        coyniViewModel.getValidateResponseMutableLiveData().observe(this, new Observer<ValidateResponse>() {
            @Override
            public void onChanged(ValidateResponse validateResponse) {
                try {
                    //dialog.dismiss();
                    if (validateResponse != null) {
                        if (!validateResponse.getStatus().toLowerCase().equals("error")) {
                            if (validateResponse.getData().getRequestToken() != null && !validateResponse.getData().getRequestToken().equals("")) {
                                Utils.setStrToken(validateResponse.getData().getRequestToken());
                            }
                            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                                businessIdentityVerificationViewModel.getBusinessTracker();
                            }
                            shakeAnimateUpDown();//new

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String strScreen = "";
                                        if (getIntent().getStringExtra("screen") != null) {
                                            strScreen = getIntent().getStringExtra("screen");
                                        }
                                        switch (strScreen) {
                                            case "loginExpiry":
                                                Intent i = new Intent(PINActivity.this, CreatePasswordActivity.class);
                                                i.putExtra("screen", getIntent().getStringExtra("screen"));
                                                startActivity(i);
                                                finish();
                                                break;
                                            case "login":
                                                if (objMyApplication.getBiometric() && objMyApplication.getLocalBiometric()) {
                                                    launchDashboard();
                                                } else {
                                                    if (!isDontRemind) {
                                                        if (Utils.checkBiometric(PINActivity.this)) {
                                                            if (Utils.checkAuthentication(PINActivity.this)) {
                                                                if (Utils.isFingerPrint(PINActivity.this)) {
                                                                    startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                            .putExtra("ENABLE_TYPE", "TOUCH")
                                                                            .putExtra("screen", strScreen));
                                                                } else {
                                                                    startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                            .putExtra("ENABLE_TYPE", "FACE")
                                                                            .putExtra("screen", strScreen));
                                                                }
                                                            } else {
                                                                startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                        .putExtra("ENABLE_TYPE", "SUCCESS")
                                                                        .putExtra("screen", strScreen));
                                                            }
                                                        } else {
                                                            startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                    .putExtra("ENABLE_TYPE", "TOUCH")
                                                                    .putExtra("screen", strScreen));
                                                        }
                                                    } else {
                                                        launchDashboard();
                                                    }
                                                }
                                                break;
                                            case "EditEmail":
                                                Intent ee = new Intent(PINActivity.this, EditEmailActivity.class);
                                                startActivity(ee);
                                                finish();
                                                break;
                                            case "EditPhone":
                                                Intent ep = new Intent(PINActivity.this, EditPhoneActivity.class);
                                                ep.putExtra("OLD_PHONE", getIntent().getStringExtra("OLD_PHONE"));
                                                startActivity(ep);
                                                finish();
                                                break;
                                            case "EditAddress":
                                                Intent ea = new Intent(PINActivity.this, EditAddressActivity.class);
                                                startActivity(ea);
                                                finish();
                                                break;
                                            case "ResetPIN":
                                                imgBack.setImageResource(R.drawable.ic_back);
                                                Log.e("resetPin", resetPINValue);
                                                if (resetPINValue.equals("ENTER")) {
                                                    tvHead.setText("Choose your PIN");
                                                    tvForgot.setVisibility(View.GONE);
                                                    passcode = "";
                                                    resetPINValue = "CHOOSE";
                                                    clearPassCode();
                                                    TYPE = "CHOOSE";
                                                } else if (resetPINValue.equals("CHOOSE")) {
                                                    tvHead.setText("Confirm your PIN");
                                                    tvForgot.setVisibility(View.GONE);
                                                    passcode = "";
                                                    resetPINValue = "CONFIRM";
                                                    clearPassCode();
                                                    TYPE = "CHOOSE";
                                                }
                                                break;
                                            case "ChangePassword":
                                                Intent cp = new Intent(PINActivity.this, ConfirmPasswordActivity.class);
                                                startActivity(cp);
                                                finish();
                                                break;
                                            case "buy":
                                                buyToken();
                                                break;
                                            case "Withdraw":
                                                WithdrawMethod();
                                                break;
                                            case "Pay":
                                                payTransaction();
                                                break;
                                            case "Paid":
                                                paidTransaction();
                                                break;
                                            case "Notifications":
                                                Intent returnIntent = new Intent();
                                                setResult(235, returnIntent);
                                                finish();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
                        } else {
                            setErrorPIN();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        coyniViewModel.getRegisterPINResponseMutableLiveData().observe(this, new Observer<PINRegisterResponse>() {
            @Override
            public void onChanged(PINRegisterResponse pinRegisterResponse) {
                try {
                    //dialog.dismiss();
                    if (pinRegisterResponse != null) {
                        Log.e("PIN Response", new Gson().toJson(pinRegisterResponse));
                        if (!pinRegisterResponse.getStatus().toLowerCase().equals("error")) {
                            String strScreen = "";
                            if (getIntent().getStringExtra("screen") != null) {
                                strScreen = getIntent().getStringExtra("screen");
                            }
                            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ForgotPin")) {
                                Utils.showCustomToast(PINActivity.this, "PIN code has been updated", R.drawable.ic_custom_tick, "pin");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
//                                            launchDashboard();
                                            dbHandler.clearAllTables();
                                            Intent i = new Intent(PINActivity.this, OnboardActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }, 2000);

                            } else if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("ResetPIN")) {
//                                Utils.showCustomToast(PINActivity.this, "PIN code has been updated", R.drawable.ic_custom_tick, "pin");
                                Utils.showCustomToast(PINActivity.this, "PIN code has been updated", R.drawable.ic_custom_tick, "pin");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        try {
//                                            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
//                                                finish();
//                                            } else {
//                                                launchDashboard();
//                                            }
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }

                                        try {
                                            dbHandler.clearAllTables();
                                            Intent i = new Intent(PINActivity.this, OnboardActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }, 2000);

                            } else {
                                if (Utils.checkBiometric(PINActivity.this)) {
                                    if (Utils.checkAuthentication(PINActivity.this)) {
                                        if (Utils.isFingerPrint(PINActivity.this)) {
                                            startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                    .putExtra("ENABLE_TYPE", "TOUCH")
                                                    .putExtra("screen", strScreen));
                                        } else {
                                            startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                    .putExtra("ENABLE_TYPE", "FACE")
                                                    .putExtra("screen", strScreen));
                                        }
                                    } else {
                                        startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                .putExtra("ENABLE_TYPE", "SUCCESS")
                                                .putExtra("screen", strScreen));
                                    }
                                } else {
                                    startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                            .putExtra("ENABLE_TYPE", "TOUCH")
                                            .putExtra("screen", strScreen));
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getWithdrawResponseMutableLiveData().observe(this, new Observer<WithdrawResponse>() {
            @Override
            public void onChanged(WithdrawResponse withdrawResponse) {
                try {
                    if (withdrawResponse != null) {
                        Utils.setStrToken("");
                        objMyApplication.setWithdrawResponse(withdrawResponse);
                        if (withdrawResponse.getStatus().equalsIgnoreCase("success")) {
                            if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("giftcard")) {
                                startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                        .putExtra("status", "inprogress")
                                        .putExtra("subtype", getIntent().getStringExtra("subtype"))
                                        .putExtra("fee", GiftCardDetails.giftCardDetails.fee.toString()));
                            } else {
                                startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                        .putExtra("status", "inprogress")
                                        .putExtra("subtype", getIntent().getStringExtra("subtype")));
                            }
                            finish();
                        } else {
                            if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("giftcard")) {
                                startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                        .putExtra("status", "failed")
                                        .putExtra("subtype", getIntent().getStringExtra("subtype"))
                                        .putExtra("fee", GiftCardDetails.giftCardDetails.fee.toString()));
                            } else {
                                startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                        .putExtra("status", "failed")
                                        .putExtra("subtype", getIntent().getStringExtra("subtype")));
                            }
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        payViewModel.getPayRequestResponseMutableLiveData().observe(this, new Observer<PayRequestResponse>() {
            @Override
            public void onChanged(PayRequestResponse payRequestResponse) {
                try {
                    if (payRequestResponse != null) {
                        Utils.setStrToken("");
                        objMyApplication.setPayRequestResponse(payRequestResponse);
                        if (payRequestResponse.getStatus().toLowerCase().equals("success")) {
                            startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "success")
                                    .putExtra("subtype", "pay"));

                        } else {
                            startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "failed")
                                    .putExtra("subtype", "pay"));
                        }
                        finish();
                    } else {
                        Utils.displayAlert("something went wrong", PINActivity.this, "", "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        payViewModel.getPaidOrderRespMutableLiveData().observe(this, new Observer<PaidOrderResp>() {
            @Override
            public void onChanged(PaidOrderResp paidOrderResp) {
                if (paidOrderResp != null) {
                    objMyApplication.setPaidOrderResp(paidOrderResp);
                    Utils.setStrToken("");
                    if (paidOrderResp.getStatus().equalsIgnoreCase("success")) {
                        startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Success")
                                .putExtra("subtype", "paid"));

                    } else {
                        startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Failed")
                                .putExtra("subtype", "paid"));
                    }
                    finish();
                } else {
                    Utils.displayAlert("something went wrong", PINActivity.this, "", "");
                }
            }
        });

        businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
            @Override
            public void onChanged(BusinessTrackerResponse businessTrackerResponse) {

                if (businessTrackerResponse != null) {
                    if (businessTrackerResponse.getStatus().toLowerCase().toString().equals("success")) {
                        objMyApplication.setBusinessTrackerResponse(businessTrackerResponse);

                        Log.e("Tracker resp PIN", new Gson().toJson(objMyApplication.getBusinessTrackerResponse()));
                    }
                }
            }
        });

        coyniViewModel.getStepUpResponseMutableLiveData().observe(this, new Observer<StepUpResponse>() {
            @Override
            public void onChanged(StepUpResponse stepUpResponse) {
                try {
                    if (stepUpResponse != null) {
                        if (!stepUpResponse.getStatus().toLowerCase().equals("error")) {
                            Utils.setStrAuth(stepUpResponse.getData().getJwtToken());
                            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                                businessIdentityVerificationViewModel.getBusinessTracker();
                            }
                            shakeAnimateUpDown();//new

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String strScreen = "";
                                        if (getIntent().getStringExtra("screen") != null) {
                                            strScreen = getIntent().getStringExtra("screen");
                                        }
                                        switch (strScreen) {
                                            case "loginExpiry":
                                                Intent i = new Intent(PINActivity.this, CreatePasswordActivity.class);
                                                i.putExtra("screen", getIntent().getStringExtra("screen"));
                                                startActivity(i);
                                                finish();
                                                break;
                                            case "login":
                                                if (objMyApplication.getBiometric() && objMyApplication.getLocalBiometric()) {
                                                    launchDashboard();
                                                } else {
                                                    if (!isDontRemind) {
                                                        if (Utils.checkBiometric(PINActivity.this)) {
                                                            if (Utils.checkAuthentication(PINActivity.this)) {
                                                                if (Utils.isFingerPrint(PINActivity.this)) {
                                                                    startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                            .putExtra("ENABLE_TYPE", "TOUCH")
                                                                            .putExtra("screen", strScreen));
                                                                } else {
                                                                    startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                            .putExtra("ENABLE_TYPE", "FACE")
                                                                            .putExtra("screen", strScreen));
                                                                }
                                                            } else {
                                                                startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                        .putExtra("ENABLE_TYPE", "SUCCESS")
                                                                        .putExtra("screen", strScreen));
                                                            }
                                                        } else {
                                                            startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                                                    .putExtra("ENABLE_TYPE", "TOUCH")
                                                                    .putExtra("screen", strScreen));
                                                        }
                                                    } else {
                                                        launchDashboard();
                                                    }
                                                }
                                                break;
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, Utils.duration);
                        } else {
                            setErrorPIN();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getBuyTokResponseMutableLiveData().observe(this, new Observer<BuyTokenResponse>() {
            @Override
            public void onChanged(BuyTokenResponse buyTokenResponse) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                if (buyTokenResponse != null) {
                    Utils.setStrToken("");
                    objMyApplication.setBuyTokenResponse(buyTokenResponse);
                    if (buyTokenResponse.getStatus().equalsIgnoreCase("success")) {
                        startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "inprogress")
                                .putExtra("subtype", getIntent().getStringExtra("screen"))
                                .putExtra("cynValue", getIntent().getStringExtra("cynValue")));
                        finish();
                    } else {
                        startActivity(new Intent(PINActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "failed")
                                .putExtra("subtype", getIntent().getStringExtra("screen"))
                                .putExtra("cynValue", getIntent().getStringExtra("cynValue")));
                        finish();
                    }
                }
            }
        });

        buyTokenViewModel.getBuyTokenFailureMutableLiveData().observe(this, new Observer<BuyTokenResponse>() {
            @Override
            public void onChanged(BuyTokenResponse buyTokenResponse) {
                if (buyTokenResponse != null) {
                    buyTokenFailure(buyTokenResponse);
                }
            }
        });

        try {
            loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
                @Override
                public void onChanged(EmailResendResponse emailResponse) {
                    dismissDialog();
                    if (emailResponse != null) {
                        if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                            Intent i = new Intent(PINActivity.this, OTPValidation.class);
                            i.putExtra("OTP_TYPE", "EMAIL");
                            i.putExtra("EMAIL", Utils.getUserEmail(PINActivity.this));
                            i.putExtra("screen", "ForgotPin");
                            startActivity(i);
                        } else {
                            String message = getString(R.string.something_went_wrong);
                            if (emailResponse.getError().getFieldErrors().size() > 0) {
                                message = emailResponse.getError().getFieldErrors().get(0);
                            }
                            Utils.displayAlert(emailResponse.getError().getErrorDescription(), PINActivity.this, "", message);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyZeroTV:
                if (passcode.length() < 6) {
                    passcode += "0";
                    passNumber(passcode);
                }
                break;
            case R.id.keyOneTV:
                if (passcode.length() < 6) {
                    passcode += "1";
                    passNumber(passcode);
                }
                break;
            case R.id.keyTwoTV:
                if (passcode.length() < 6) {
                    passcode += "2";
                    passNumber(passcode);
                }
                break;
            case R.id.keyThreeTV:
                if (passcode.length() < 6) {
                    passcode += "3";
                    passNumber(passcode);
                }
                break;
            case R.id.keyFourTV:
                if (passcode.length() < 6) {
                    passcode += "4";
                    passNumber(passcode);
                }
                break;
            case R.id.keyFiveTV:
                if (passcode.length() < 6) {
                    passcode += "5";
                    passNumber(passcode);
                }
                break;
            case R.id.keySixTV:
                if (passcode.length() < 6) {
                    passcode += "6";
                    passNumber(passcode);
                }
                break;
            case R.id.keySevenTV:
                if (passcode.length() < 6) {
                    passcode += "7";
                    passNumber(passcode);
                }
                break;
            case R.id.keyEightTV:
                if (passcode.length() < 6) {
                    passcode += "8";
                    passNumber(passcode);
                }
                break;
            case R.id.keyNineTV:
                if (passcode.length() < 6) {
                    passcode += "9";
                    passNumber(passcode);
                }
                break;
            case R.id.backActionIV:
                if (!passcode.equals("")) {
                    passcode = passcode.substring(0, passcode.length() - 1);
                }
                passNumberClear(passcode);
                break;
            case R.id.imgBack:
                if (getIntent().getStringExtra("screen") != null && (getIntent().getStringExtra("screen").equals("login")
                        || getIntent().getStringExtra("screen").equals("loginExpiry")
                        || getIntent().getStringExtra("screen").equals("ChangePassword")
                        || getIntent().getStringExtra("screen").equals("EditEmail")
                        || getIntent().getStringExtra("screen").equals("EditPhone")
                        || getIntent().getStringExtra("screen").equals("EditAddress")
                        || getIntent().getStringExtra("screen").equals("Withdraw")
                        || getIntent().getStringExtra("screen").equals("Pay")
                        || getIntent().getStringExtra("screen").equals("Notifications")
                        || getIntent().getStringExtra("screen").equals("buy"))
                        || getIntent().getStringExtra("screen").equals("Paid")) {
                    onBackPressed();
                } else if (getIntent().getStringExtra("screen") != null &&
                        (getIntent().getStringExtra("screen").equals("ResetPIN"))) {
                    Log.e("resetPin", resetPINValue);
                    Log.e("Type", TYPE);
                    if (resetPINValue.equals("ENTER")) {
                        onBackPressed();
                    } else if (resetPINValue.equals("CHOOSE")) {
//                        tvForgot.setVisibility(View.VISIBLE);
//                        TYPE = "ENTER";
//                        tvHead.setText("Enter your PIN");
//                        clearControls();
//                        passcode = "";
//                        resetPINValue = "ENTER";
//                        imgBack.setImageResource(R.drawable.ic_close);
                        onBackPressed();
                    } else if (resetPINValue.equals("CONFIRM")) {
                        tvForgot.setVisibility(View.GONE);
                        TYPE = "CHOOSE";
                        tvHead.setText("Choose your PIN");
                        clearControls();
                        passcode = "";
                        if (getIntent().getStringExtra("AUTH_TYPE") != null && getIntent().getStringExtra("AUTH_TYPE").equals("TOUCH")) {
                            imgBack.setImageResource(R.drawable.ic_close);
                        } else{
                            imgBack.setImageResource(R.drawable.ic_back);
                        }
                        resetPINValue = "CHOOSE";
                    }
                } else {
                    if (TYPE.equals("CHOOSE")) {
                        onBackPressed();
                    } else {
                        tvForgot.setVisibility(View.GONE);
                        TYPE = "CHOOSE";
                        tvHead.setText("Choose your PIN");
                        clearControls();
                        passcode = "";
                    }
                }
                break;
            case R.id.tvForgot:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showProgressDialog();
                loginViewModel.emailotpresend(Utils.getUserEmail(PINActivity.this));
//                Intent i = new Intent(PINActivity.this, ForgotPasswordActivity.class);
//                i.putExtra("screen", "ForgotPin");
//                startActivity(i);
                break;

        }

    }

    private void passNumber(String number_list) {
        try {
            if (number_list.length() == 0) {
                clearPassCode();
            } else {
                switch (number_list.length()) {
                    case 1:
                        chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle);
                        break;
                    case 2:
                        chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle);
                        break;
                    case 3:
                        chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle);
                        break;
                    case 4:
                        chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle);
                        break;
                    case 5:
                        chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle);
                        break;
                    case 6:
                        chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle);

                        switch (TYPE) {
                            case "CHOOSE":
                                strChoose = passcode;
                                passcode = "";
                                TYPE = "CONFIRM";
                                tvHead.setText("Confirm your PIN");
                                clearPassCode();
                                resetPINValue = "CONFIRM";
                                if (getIntent().getStringExtra("AUTH_TYPE") != null && getIntent().getStringExtra("AUTH_TYPE").equals("TOUCH")) {
                                    imgBack.setImageResource(R.drawable.ic_back);
                                }
                                break;
                            case "CONFIRM":
                                strConfirm = passcode;
                                if (!strChoose.equals(strConfirm)) {
                                    setErrorPINMismatch(strChoose, strConfirm);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                clearControls();
                                                passcode = "";
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }, 2000);
                                } else {
                                    shakeAnimateUpDown();
                                    RegisterRequest registerRequest = new RegisterRequest();
                                    registerRequest.setPin(strChoose);
                                    coyniViewModel.registerCoyniPin(registerRequest);
                                }
                                break;
                            case "ENTER":
                                validatePIN();
                                break;
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void passNumberClear(String number_list) {
        try {
            switch (number_list.length()) {
                case 5:
                    //setSuccessPIN();
                    chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    circleSixLL.setBackgroundResource(R.drawable.ic_outline_circle);
                    break;
                case 4:
                    chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    circleFiveLL.setBackgroundResource(R.drawable.ic_outline_circle);
                    break;
                case 3:
                    chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    circleFourLL.setBackgroundResource(R.drawable.ic_outline_circle);
                    break;
                case 2:
                    chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    circleThreeLL.setBackgroundResource(R.drawable.ic_outline_circle);
                    break;
                case 1:
                    chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    circleTwoLL.setBackgroundResource(R.drawable.ic_outline_circle);
                    break;
                case 0:
                    chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    circleOneLL.setBackgroundResource(R.drawable.ic_outline_circle);
                    break;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearPassCode() {
        try {
            chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_white);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void validatePIN() {
        try {
            ValidateRequest request = new ValidateRequest();
            request.setPin(passcode);
            switch (strScreen.toLowerCase()) {
                case "changepassword":
                    request.setActionType(Utils.changeActionType);
                    break;
                case "withdraw":
                    request.setActionType(Utils.withdrawActionType);
                    break;
                case "pay":
                case "notifications": {
                    request.setActionType(Utils.sendActionType);
                }
                break;
                case "resetpin":
                    request.setActionType(Utils.pinActionType);
                    break;
                case "buy":
                    request.setActionType(Utils.buyActionType);
                    break;
                case "paid":
                    request.setActionType(Utils.paidActionType);
                    break;
            }
//            if (strScreen.toLowerCase().equals("changepassword")) {
//                request.setActionType(Utils.changeActionType);
//            }
            //Uncomment for stepup process
            if (getIntent().getStringExtra("screen") != null && ((getIntent().getStringExtra("screen").equals("login")) || (getIntent().getStringExtra("screen").equals("loginExpiry")))) {
                coyniViewModel.stepUpPin(request);
            } else {
                coyniViewModel.validateCoyniPin(request);
            }
//            coyniViewModel.validateCoyniPin(request);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setErrorPIN() {
        circleOneLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
        circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
        circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
        circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
        circleFiveLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
        circleSixLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));

        chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        shakeAnimateLeftRight();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    clearControls();
                    passcode = "";
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 2000);
    }

    public void setErrorPINMismatch(String strChoose, String strConfirm) {
        Log.e("char", strChoose.substring(0, 1));
        if (strChoose.substring(0, 1).equals(strConfirm.substring(0, 1))) {
            circleOneLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleOneLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
            chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(1, 2).equals(strConfirm.substring(1, 2))) {
            circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
            chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(2, 3).equals(strConfirm.substring(2, 3))) {
            circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
            chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(3, 4).equals(strConfirm.substring(3, 4))) {
            circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
            chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }


        if (strChoose.substring(4, 5).equals(strConfirm.substring(4, 5))) {
            circleFiveLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleFiveLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
            chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(5, 6).equals(strConfirm.substring(5, 6))) {
            circleSixLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleSixLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error));
            chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        shakeAnimateLeftRight();
    }

    public void shakeAnimateLeftRight() {
        pinLL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void shakeAnimateUpDown() {
        pinLL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_up_down));
    }

    private void clearControls() {
        try {
            circleOneLL.setBackgroundResource(R.drawable.ic_outline_circle);
            circleTwoLL.setBackgroundResource(R.drawable.ic_outline_circle);
            circleThreeLL.setBackgroundResource(R.drawable.ic_outline_circle);
            circleFourLL.setBackgroundResource(R.drawable.ic_outline_circle);
            circleFiveLL.setBackgroundResource(R.drawable.ic_outline_circle);
            circleSixLL.setBackgroundResource(R.drawable.ic_outline_circle);
            clearPassCode();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void toastTimer(Dialog dialog) {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dialog.dismiss();
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
        }.start();
    }

//    private void SetDontRemind() {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsDontRemind = mydatabase.rawQuery("Select * from tblDontRemind", null);
//            dsDontRemind.moveToFirst();
//            if (dsDontRemind.getCount() > 0) {
//                if (dsDontRemind.getString(1).equals("true")) {
//                    isDontRemind = true;
//                } else {
//                    isDontRemind = false;
//                }
//            }
//        } catch (Exception ex) {
//            if (ex.getMessage().toString().contains("no such table")) {
//                mydatabase.execSQL("DROP TABLE IF EXISTS tblDontRemind;");
//                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblDontRemind(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isDontRemind TEXT);");
//            }
//        }
//    }

    private void SetDontRemind() {
        String value = dbHandler.getTableDontRemind();
        isDontRemind = value != null && value.equals("true");

    }

    private void launchDashboard() {

        objMyApplication.launchDashboard(this, strScreen);

//        try {
//            Intent dashboardIntent = new Intent(PINActivity.this, DashboardActivity.class);
//            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
//                BusinessTrackerResponse btr = objMyApplication.getBusinessTrackerResponse();
//                if (btr != null && btr.getData().isCompanyInfo() && btr.getData().isDbaInfo() && btr.getData().isBeneficialOwners()
//                        && btr.getData().isIsbankAccount() && btr.getData().isAgreementSigned() && btr.getData().isApplicationSummary()) {
//                    dashboardIntent = new Intent(PINActivity.this, BusinessDashboardActivity.class);
//                } else {
//                    dashboardIntent = new Intent(PINActivity.this, BusinessRegistrationTrackerActivity.class);
//                    dashboardIntent.putExtra("FROM", strScreen);
//                }
//            }
//            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PINActivity.this.startActivity(dashboardIntent);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    private void WithdrawMethod() {
        try {
            WithdrawRequest request = objMyApplication.getWithdrawRequest();
            if (Utils.checkInternet(PINActivity.this)) {
                buyTokenViewModel.withdrawTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransaction() {
        try {
            if (Utils.checkInternet(PINActivity.this)) {
                payViewModel.sendTokens(objMyApplication.getTransferPayRequest());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void paidTransaction() {

        PaidOrderRequest request = new PaidOrderRequest();
        request.setRequestToken(Utils.getStrToken());
        request.setTokensAmount(Double.parseDouble(getIntent().getStringExtra(Utils.amount)));
        request.setRecipientWalletId(getIntent().getStringExtra(Utils.wallet));

        objMyApplication.setWithdrawAmount(Double.parseDouble(getIntent().getStringExtra(Utils.amount)));

        if (Utils.checkInternet(PINActivity.this)) {
            try {
                payViewModel.paidOrder(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buyToken() {
        try {
            if (Utils.checkInternet(PINActivity.this)) {
                buyTokenViewModel.buyTokens(objMyApplication.getBuyRequest());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyTokenFailure(BuyTokenResponse objData) {
        try {
            prevDialog = new Dialog(PINActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.buy_token_transaction_failed);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;


            TextView tvMessage = prevDialog.findViewById(R.id.tvMessage);
            CardView cvTryAgain = prevDialog.findViewById(R.id.cvTryAgain);

            tvMessage.setText("The transaction failed due to error code:\n" + objData.getError().getErrorCode() + " - " + objData.getError().getErrorDescription() + ". Please try again.");
            Window window = prevDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            prevDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            prevDialog.setCancelable(false);
            prevDialog.show();

            cvTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    finish();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
package com.greenbox.coyni.view;


import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.CertificatePinner;

public class PINActivity extends AppCompatActivity implements View.OnClickListener {
    View chooseCircleOne, chooseCircleTwo, chooseCircleThree, chooseCircleFour, chooseCircleFive, chooseCircleSix;
    TextView keyZeroTV, keyOneTV, keyTwoTV, keyThreeTV, keyFourTV, keyFiveTV, keySixTV, keySevenTV, keyEightTV, keyNineTV;
    ImageView backActionIV, imgBack;
    String passcode = "", strChoose = "", strConfirm = "", TYPE;
    TextView tvHead, tvForgot;
    CoyniViewModel coyniViewModel;
    ProgressDialog dialog;
    LinearLayout circleOneLL, circleTwoLL, circleThreeLL, circleFourLL, circleFiveLL, circleSixLL, pinLL;
    MyApplication objMyApplication;
    SQLiteDatabase mydatabase;
    Cursor dsDontRemind;
    Boolean isDontRemind = false;
    String resetPINValue = "ENTER";
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_pin_keyboard);
            initializeComponents();
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
            String strScreen = getIntent().getStringExtra("screen");
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
//        try {
//            clearControls();
//            passcode = "";
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
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
            if (getIntent().getStringExtra("screen") != null && (getIntent().getStringExtra("screen").equals("login") ||
                    getIntent().getStringExtra("screen").equals("EditEmail") || getIntent().getStringExtra("screen").equals("EditPhone")
                    || getIntent().getStringExtra("screen").equals("EditAddress") || getIntent().getStringExtra("screen").equals("ResetPIN")
                    || getIntent().getStringExtra("screen").equals("Withdraw")
                    || getIntent().getStringExtra("screen").equals("Pay"))
                    || getIntent().getStringExtra("screen").equals("Notifications")) {

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
                                                break;
                                            case "login":
                                                if (objMyApplication.getBiometric() && objMyApplication.getLocalBiometric()) {
                                                    Intent d = new Intent(PINActivity.this, DashboardActivity.class);
                                                    d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(d);
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
                                                        Intent d = new Intent(PINActivity.this, DashboardActivity.class);
                                                        d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(d);
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

                                            case "Withdraw":
                                                WithdrawMethod();
                                                break;
                                            case "Pay":
                                                payTransaction();
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
                                            Intent d = new Intent(PINActivity.this, DashboardActivity.class);
                                            d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            PINActivity.this.startActivity(d);
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
                                        try {
                                            Intent d = new Intent(PINActivity.this, DashboardActivity.class);
                                            d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            PINActivity.this.startActivity(d);
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
                        || getIntent().getStringExtra("screen").equals("Notifications"))) {
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
                        imgBack.setImageResource(R.drawable.ic_back);
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
                Intent i = new Intent(PINActivity.this, ForgotPasswordActivity.class);
                i.putExtra("screen", "ForgotPin");
                startActivity(i);
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
//                                    dialog = new ProgressDialog(PINActivity.this, R.style.MyAlertDialogStyle);
//                                    dialog.setIndeterminate(false);
//                                    dialog.setMessage("Please wait...");
//                                    dialog.getWindow().setGravity(Gravity.CENTER);
//                                    dialog.show();
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
//            dialog = new ProgressDialog(PINActivity.this, R.style.MyAlertDialogStyle);
//            dialog.setIndeterminate(false);
//            dialog.setMessage("Please wait...");
//            dialog.getWindow().setGravity(Gravity.CENTER);
//            dialog.show();
            ValidateRequest request = new ValidateRequest();
            request.setPin(passcode);
            coyniViewModel.validateCoyniPin(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setErrorPIN() {
        circleOneLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
        circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
        circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
        circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
        circleFiveLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
        circleSixLL.setBackground(getDrawable(R.drawable.ic_outline_circle));

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
            circleOneLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(1, 2).equals(strConfirm.substring(1, 2))) {
            circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(2, 3).equals(strConfirm.substring(2, 3))) {
            circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(3, 4).equals(strConfirm.substring(3, 4))) {
            circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }


        if (strChoose.substring(4, 5).equals(strConfirm.substring(4, 5))) {
            circleFiveLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleFiveLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_error);
        }

        if (strChoose.substring(5, 6).equals(strConfirm.substring(5, 6))) {
            circleSixLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
            chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle);
        } else {
            circleSixLL.setBackground(getDrawable(R.drawable.ic_outline_circle));
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

    private void SetDontRemind() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsDontRemind = mydatabase.rawQuery("Select * from tblDontRemind", null);
            dsDontRemind.moveToFirst();
            if (dsDontRemind.getCount() > 0) {
                if (dsDontRemind.getString(1).equals("true")) {
                    isDontRemind = true;
                } else {
                    isDontRemind = false;
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblDontRemind;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblDontRemind(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isDontRemind TEXT);");
            }
        }
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
}
package com.greenbox.coyni.view;


import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class PINActivity extends AppCompatActivity implements View.OnClickListener {
    View chooseCircleOne, chooseCircleTwo, chooseCircleThree, chooseCircleFour, chooseCircleFive, chooseCircleSix;
    TextView keyZeroTV, keyOneTV, keyTwoTV, keyThreeTV, keyFourTV, keyFiveTV, keySixTV, keySevenTV, keyEightTV, keyNineTV;
    ImageView backActionIV, imgBack;
    String passcode = "", strChoose = "", strConfirm = "", TYPE;
    TextView tvHead, tvForgot;
    CoyniViewModel coyniViewModel;
    ProgressDialog dialog;
    LinearLayout circleOneLL, circleTwoLL, circleThreeLL, circleFourLL, circleFiveLL, circleSixLL;

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
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                    clearPassCode();
                    passcode = "";
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeComponents() {
        try {
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
                dialog.dismiss();
                if (validateResponse != null) {
                    if (!validateResponse.getStatus().toLowerCase().equals("error")) {
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
                                Intent d = new Intent(PINActivity.this, DashboardActivity.class);
                                d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(d);
                                break;
                            case "EditEmail":
                                Intent ee = new Intent(PINActivity.this, EditEmailActivity.class);
                                startActivity(ee);
                                finish();
                                break;
                        }
                    } else {
                        Utils.displayAlert(validateResponse.getError().getErrorDescription(), PINActivity.this);
                        setErrorPIN();
                    }
                }
            }
        });

        coyniViewModel.getRegisterPINResponseMutableLiveData().observe(this, new Observer<PINRegisterResponse>() {
            @Override
            public void onChanged(PINRegisterResponse pinRegisterResponse) {
                dialog.dismiss();
                if (pinRegisterResponse != null) {
                    Log.e("PIN Response", new Gson().toJson(pinRegisterResponse));
                    if (!pinRegisterResponse.getStatus().toLowerCase().equals("error")) {

                        if (Utils.checkAuthentication(PINActivity.this)) {
                            if (Utils.isFingerPrint(PINActivity.this)) {
                                startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                        .putExtra("ENABLE_TYPE", "TOUCH"));
                            } else {
                                startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                        .putExtra("ENABLE_TYPE", "FACE"));
                            }
                        } else {
                            startActivity(new Intent(PINActivity.this, EnableAuthID.class)
                                    .putExtra("ENABLE_TYPE", "SUCCESS"));
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
//                            if (!fingerprintManager.isHardwareDetected()) {
//                                Log.e("Not support","Not support");
//                            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
//                                startActivity(new Intent(PINActivity.this, EnableAuthID.class)
//                                        .putExtra("ENABLE_TYPE","TOUCH"));
//                            } else {
//                                Log.e("Supports","Supports");
//                                startActivity(new Intent(PINActivity.this, EnableAuthID.class)
//                                        .putExtra("ENABLE_TYPE","TOUCH"));
//                            }
//                        }
                    }
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
                if (TYPE.equals("CHOOSE")) {
                    onBackPressed();
                } else {
                    tvForgot.setVisibility(View.GONE);
                    TYPE = "CHOOSE";
                    tvHead.setText("Choose your PIN");
                    clearPassCode();
                    passcode = "";
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
                                tvHead.setText("Confrim your PIN");
                                clearPassCode();
                                break;
                            case "CONFIRM":
                                strConfirm = passcode;
                                if (!strChoose.equals(strConfirm)) {
//                                    Toast.makeText(getApplication(), "PIN misMatch", Toast.LENGTH_LONG).show();
                                    setErrorPIN();
                                } else {

                                    dialog = new ProgressDialog(PINActivity.this, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.getWindow().setGravity(Gravity.CENTER);
                                    dialog.show();

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
                    setSuccessPIN();
                    chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    break;
                case 4:
                    chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    break;
                case 3:
                    chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    break;
                case 2:
                    chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    break;
                case 1:
                    chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_white);
                    break;
                case 0:
                    chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_white);
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
            dialog = new ProgressDialog(PINActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            ValidateRequest request = new ValidateRequest();
            request.setPin(passcode);
            coyniViewModel.validateCoyniPin(request);
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
    }

    public void setSuccessPIN() {
        circleOneLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleTwoLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleThreeLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleFourLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleFiveLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleSixLL.setBackgroundResource(R.drawable.ic_outline_circle);

        chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle);
        chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle);
        chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle);
        chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle);
        chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle);
        chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle);
    }
}
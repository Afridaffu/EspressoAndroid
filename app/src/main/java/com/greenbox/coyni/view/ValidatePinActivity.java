package com.greenbox.coyni.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class ValidatePinActivity extends BaseActivity implements View.OnClickListener {
    private View chooseCircleOne, chooseCircleTwo, chooseCircleThree, chooseCircleFour, chooseCircleFive, chooseCircleSix;
    private TextView keyZeroTV, keyOneTV, keyTwoTV, keyThreeTV, keyFourTV, keyFiveTV, keySixTV, keySevenTV, keyEightTV, keyNineTV;
    private ImageView backActionIV, imgBack;
    private String passcode = "";
    private TextView tvForgotPin;
    private LinearLayout circleOneLL, circleTwoLL, circleThreeLL, circleFourLL, circleFiveLL, circleSixLL, pinLL;
    private MyApplication objMyApplication;
    private String actionType;
    private CoyniViewModel coyniViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_validate_pin);

            actionType = getIntent().getStringExtra(Utils.ACTION_TYPE);
            initFields();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
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
                onBackPressed();
                break;
            case R.id.tvForgot:
                showForgotPasswordFlow();
                break;
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

    private void initFields() {
        try {
            chooseCircleOne = findViewById(R.id.chooseCircleOne);
            chooseCircleTwo = findViewById(R.id.chooseCircleTwo);
            chooseCircleThree = findViewById(R.id.chooseCircleThree);
            chooseCircleFour = findViewById(R.id.chooseCircleFour);
            chooseCircleFive = findViewById(R.id.chooseCircleFive);
            chooseCircleSix = findViewById(R.id.chooseCircleSix);

            circleOneLL = findViewById(R.id.circleOneLL);
            circleTwoLL = findViewById(R.id.circleTwoLL);
            circleThreeLL = findViewById(R.id.circleThreeLL);
            circleFourLL = findViewById(R.id.circleFourLL);
            circleFiveLL = findViewById(R.id.circleFiveLL);
            circleSixLL = findViewById(R.id.circleSixLL);

            pinLL = findViewById(R.id.pinLL);
            tvForgotPin = findViewById(R.id.tvForgot);

            keyZeroTV = findViewById(R.id.keyZeroTV);
            keyOneTV = findViewById(R.id.keyOneTV);
            keyTwoTV = findViewById(R.id.keyTwoTV);
            keyThreeTV = findViewById(R.id.keyThreeTV);
            keyFourTV = findViewById(R.id.keyFourTV);
            keyFiveTV = findViewById(R.id.keyFiveTV);
            keySixTV = findViewById(R.id.keySixTV);
            keySevenTV = findViewById(R.id.keySevenTV);
            keyEightTV = findViewById(R.id.keyEightTV);
            keyNineTV = findViewById(R.id.keyNineTV);
            backActionIV = findViewById(R.id.backActionIV);
            imgBack = findViewById(R.id.imgBack);
            objMyApplication = (MyApplication) getApplicationContext();

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
            imgBack.setOnClickListener(this);
            tvForgotPin.setOnClickListener(this);

            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showForgotPasswordFlow() {
        try {
            Intent i = new Intent(ValidatePinActivity.this, ForgotPasswordActivity.class);
            i.putExtra("email", objMyApplication.getStrEmail());
            i.putExtra("screen", "ForgotPin");
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setErrorPIN() {
        circleOneLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleTwoLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleThreeLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleFourLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleFiveLL.setBackgroundResource(R.drawable.ic_outline_circle);
        circleSixLL.setBackgroundResource(R.drawable.ic_outline_circle);

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

    private void initObserver() {

        coyniViewModel.getValidateResponseMutableLiveData().observe(this, new Observer<ValidateResponse>() {
            @Override
            public void onChanged(ValidateResponse validateResponse) {
                try {
                    if (validateResponse != null && validateResponse.getStatus() != null) {
                        if (validateResponse.getStatus().toLowerCase().equals("success")) {
                            shakeAnimateUpDown();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent in = new Intent();
                                    in.putExtra(Utils.ACTION_TYPE, actionType);
                                    in.putExtra(Utils.TRANSACTION_TOKEN, validateResponse.getData().getRequestToken());
                                    sendSuccessResult(in);
                                }
                            }, 200);
                        } else {
                            setErrorPIN();
                        }
                    } else {
                        setErrorPIN();
                    }
                } catch (Exception ex) {
                    setErrorPIN();
                    ex.printStackTrace();
                }
            }
        });
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
                        checkAndProceed();
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkAndProceed() {
        validatePIN();
    }

    public void shakeAnimateLeftRight() {
        pinLL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void shakeAnimateUpDown() {
        pinLL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_up_down));
    }

    private void validatePIN() {
        ValidateRequest request = new ValidateRequest();
        request.setPin(passcode);
        request.setActionType(actionType);
        coyniViewModel.validateCoyniPin(request);
    }

    private void sendSuccessResult(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}

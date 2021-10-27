package com.greenbox.coyni.view;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.greenbox.coyni.R;
import java.util.ArrayList;

public class PINActivity extends AppCompatActivity implements View.OnClickListener {
    View chooseCircleOne, chooseCircleTwo, chooseCircleThree, chooseCircleFour, chooseCircleFive, chooseCircleSix;
    TextView keyZeroTV, keyOneTV, keyTwoTV, keyThreeTV, keyFourTV, keyFiveTV, keySixTV, keySevenTV, keyEightTV, keyNineTV;
    ImageView backActionIV, imgBack;
    String passcode = "", strChoose = "", strConfirm = "", TYPE;
    TextView tvHead, tvForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            switch (TYPE) {
                case "CHOOSE":
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyZeroTV:
                passcode += "0";
                passNumber(passcode);
                break;
            case R.id.keyOneTV:
                passcode += "1";
                passNumber(passcode);
                break;
            case R.id.keyTwoTV:
                passcode += "2";
                passNumber(passcode);
                break;
            case R.id.keyThreeTV:
                passcode += "3";
                passNumber(passcode);
                break;
            case R.id.keyFourTV:
                passcode += "4";
                passNumber(passcode);
                break;
            case R.id.keyFiveTV:
                passcode += "5";
                passNumber(passcode);
                break;
            case R.id.keySixTV:
                passcode += "6";
                passNumber(passcode);
                break;
            case R.id.keySevenTV:
                passcode += "7";
                passNumber(passcode);
                break;
            case R.id.keyEightTV:
                passcode += "8";
                passNumber(passcode);
                break;
            case R.id.keyNineTV:
                passcode += "9";
                passNumber(passcode);
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
                                    Toast.makeText(getApplication(), "PIN misMatch", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplication(), "PIN match", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case "ENTER":
                                Toast.makeText(getApplication(), "Enter PIN value", Toast.LENGTH_LONG).show();
                                if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("loginExpiry")) {
                                    Intent i = new Intent(PINActivity.this, CreatePasswordActivity.class);
                                    i.putExtra("screen", getIntent().getStringExtra("screen"));
                                    startActivity(i);
                                }
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

    private void matchPasscode() {


    }

}
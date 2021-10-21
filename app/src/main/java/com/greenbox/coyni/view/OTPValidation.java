package com.greenbox.coyni.view;

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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.otpview.PinView;

public class OTPValidation extends AppCompatActivity {

    TextView resendTV,newCodeTV,subHeaderTV,headerTV;
    PinView otpPV;
    ImageView otpValidationCloseIV;
    int resendCounter = 0;
    private Vibrator vibrator;

    String OTP_TYPE = "";
    String MOBILE = "";
    String EMAIL = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_otpvalidation);

        OTP_TYPE = getIntent().getStringExtra("OTP_TYPE");
        MOBILE = getIntent().getStringExtra("MOBILE");
        EMAIL = getIntent().getStringExtra("EMAIL");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        resendTV = findViewById(R.id.resendTV);
        headerTV = findViewById(R.id.headerTV);
        subHeaderTV = findViewById(R.id.subHeaderTV);
        newCodeTV = findViewById(R.id.newCodeTV);
        otpPV = findViewById(R.id.otpPV);
        otpValidationCloseIV = findViewById(R.id.otpValidationCloseIV);

        resendTV.setPaintFlags(resendTV.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        otpPV.setAnimationEnable(true);

        if(OTP_TYPE.equals("MOBILE")){
            headerTV.setText("Please Verify your Phone Number");
            subHeaderTV.setText("We sent you a 6-digit code to the register phone number: "+MOBILE);
        }else if(OTP_TYPE.equals("EMAIL")){
            headerTV.setText("Please Verify your Email");
            subHeaderTV.setText("We sent you a 6-digit code to the register email address: "+EMAIL);
        }

        resendTV.setOnClickListener(view -> {

            if(resendCounter<5){
                resendTV.setVisibility(View.GONE);
                newCodeTV.setVisibility(View.VISIBLE);

                resendCounter++;
                startTimer();

            }else {
                startActivity(new Intent(OTPValidation.this, RetryCreateAccount.class));
                finish();
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
                    if(OTP_TYPE.equals("MOBILE")){
                        if(charSequence.length()==6 ){
                            if(charSequence.toString().equals("123456")){
                                otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                shakeAnimateUpDown();
                                startActivity(new Intent(OTPValidation.this, OTPValidation.class)
                                        .putExtra("OTP_TYPE","EMAIL")
                                        .putExtra("MOBILE",MOBILE)
                                        .putExtra("EMAIL",EMAIL)
                                );
                            }else{
                                otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                shakeAnimateLeftRight();
                            }
                        }
                    }else{
                        if(charSequence.length()==6 ){
                            if(charSequence.toString().equals("123456")){
                                otpPV.setLineColor(getResources().getColor(R.color.primary_color));
                                shakeAnimateUpDown();
                                startActivity(new Intent(OTPValidation.this, SecureAccount.class));
                            }else{
                                otpPV.setLineColor(getResources().getColor(R.color.error_red));
                                shakeAnimateLeftRight();
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    void startTimer(){
        new Thread(){
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

            };
        }.start();
    }

    public void shakeAnimateLeftRight(){
        vibrateAction();
        otpPV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void shakeAnimateUpDown(){
        vibrateAction();
        otpPV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_up_down));
    }

    public void vibrateAction(){

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(600);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
}
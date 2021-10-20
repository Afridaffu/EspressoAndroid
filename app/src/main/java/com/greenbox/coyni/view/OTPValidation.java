package com.greenbox.coyni.view;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.otpview.PinView;

public class OTPValidation extends AppCompatActivity {

    TextView resendTV,newCodeTV;
    PinView otpPV;


    int resendCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_otpvalidation);

        resendTV = findViewById(R.id.resendTV);
        newCodeTV = findViewById(R.id.newCodeTV);
        otpPV = findViewById(R.id.otpPV);

        resendTV.setPaintFlags(resendTV.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        otpPV.setAnimationEnable(true);


        resendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(resendCounter<5){
                    resendTV.setVisibility(View.GONE);
                    newCodeTV.setVisibility(View.VISIBLE);

                    resendCounter++;
                    startTimer();

                }else {
                    startActivity(new Intent(OTPValidation.this, RetryCreateAccount.class));
                    finish();
                }
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
}
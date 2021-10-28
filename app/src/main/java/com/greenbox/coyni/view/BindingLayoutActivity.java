package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class BindingLayoutActivity extends AppCompatActivity {
    String strScreen = "";
    LinearLayout llCoyniAct,lyClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_binding_layout);
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
                ControlMethod(strScreen);
            }
            initialization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            llCoyniAct = findViewById(R.id.llCoyniAct);
            lyClose = findViewById(R.id.lyClose);
            llCoyniAct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
            lyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "retEmailfound": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                }
                break;
                case "retEmailfail": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
package com.greenbox.coyni.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;

public class EnableAuthID extends AppCompatActivity {

    MaterialCardView enableFaceCV,enableTouchCV;
    TextView notNowFaceTV,notNowTouchTV ;
    RelativeLayout faceIDRL,touchIDRL,successRL;
    String enableType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_enable_auth_id);

            enableFaceCV = findViewById(R.id.enableFaceCV);
            notNowFaceTV = findViewById(R.id.notNowFaceTV);

            enableTouchCV = findViewById(R.id.enableTouchCV);
            notNowTouchTV = findViewById(R.id.notNowTouchTV);

            faceIDRL = findViewById(R.id.faceIDRL);
            touchIDRL = findViewById(R.id.touchIDRL);

            successRL = findViewById(R.id.successRL);

            enableType = getIntent().getStringExtra("ENABLE_TYPE");

            if(enableType.equals("TOUCH")){
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.VISIBLE);
                successRL.setVisibility(View.GONE);
            }else if(enableType.equals("FACE")){
                faceIDRL.setVisibility(View.VISIBLE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.GONE);
            }


            enableFaceCV.setOnClickListener(view -> {

            });

            notNowFaceTV.setOnClickListener(view -> {
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.VISIBLE);
            });

            enableTouchCV.setOnClickListener(view -> {

            });

            notNowTouchTV.setOnClickListener(view -> {
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.VISIBLE);
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class BusinessProfileActivity extends AppCompatActivity {

    private LinearLayout feesLL, teamLL,bpbackBtn,switchOffLL,switchOnLL;
    boolean isTogleBtn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_profile);
        feesLL = findViewById(R.id.feesLL);
        teamLL = findViewById(R.id.teamLL);
        bpbackBtn = findViewById(R.id.bpbackBtn);
        switchOnLL=findViewById(R.id.switchOn);
        switchOffLL=findViewById(R.id.switchOff);
        switchOffLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTogleBtn=true;
                switchOnLL.setVisibility(View.VISIBLE);
                switchOffLL.setVisibility(View.GONE);
            }
        });

        switchOnLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTogleBtn=false;
                switchOnLL.setVisibility(View.GONE);
                switchOffLL.setVisibility(View.VISIBLE);
            }
        });



        bpbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        teamLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileActivity.this,TeamActivity.class);
                startActivity(intent);
            }
        });
        feesLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileActivity.this,FeesActivity.class);
                startActivity(intent);
            }
        });
    }
}
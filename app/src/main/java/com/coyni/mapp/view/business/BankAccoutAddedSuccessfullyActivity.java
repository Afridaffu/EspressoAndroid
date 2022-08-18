package com.coyni.mapp.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.coyni.mapp.R;

public class BankAccoutAddedSuccessfullyActivity extends AppCompatActivity {

    private ImageView crossIV;
    private CardView doneCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_accout_added_successfully);
        crossIV = findViewById(R.id.crossIV);
        doneCV = findViewById(R.id.doneCV);
        initMethod();
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initMethod() {
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
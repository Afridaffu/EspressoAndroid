package com.greenbox.coyni.view;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;

public class SecureAccount extends AppCompatActivity {

    MaterialCardView secureNextCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_secure_account);

        secureNextCV = findViewById(R.id.secureNextCV);

        secureNextCV.setOnClickListener(view -> {

        });
    }
}
package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class SignupMaillingAddresActivity extends AppCompatActivity {
ImageView addbenif;
TextView textTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_mailling_addres);

        addbenif = findViewById(R.id.addbenifIV);
        textTV = findViewById(R.id.textTV);

        addbenif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupMaillingAddresActivity.this,AddBeneficialOwnerActivity.class);
                startActivity(intent);
                textTV.setVisibility(View.GONE);
            }
        });
    }
}
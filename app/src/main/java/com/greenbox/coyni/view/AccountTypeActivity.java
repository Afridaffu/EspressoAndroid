package com.greenbox.coyni.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;


public class AccountTypeActivity extends AppCompatActivity {

    LinearLayout personalAccontLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_account_type);

        personalAccontLL = findViewById(R.id.personalAccontLL);


        personalAccontLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountTypeActivity.this, CreateAccountActivity.class));
            }
        });
    }
}
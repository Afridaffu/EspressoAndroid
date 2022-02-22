package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BindingLayoutActivity;

public class BusinessAddNewAccountActivity extends AppCompatActivity {

    ImageView imageViewClose;
    LinearLayout llBusinessAccount,llPersonalAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.business_onboarding_open_new_account);

        llBusinessAccount = findViewById(R.id.businessAccontLL);
        llPersonalAccount = findViewById(R.id.personalAccontLL);
        imageViewClose = findViewById(R.id.imv_close);

        llBusinessAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessAddNewAccountActivity.this, BusinessAddNewBusinessAccountActivity.class));

            }
        });

        llPersonalAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessAddNewAccountActivity.this, BindingLayoutActivity.class));
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
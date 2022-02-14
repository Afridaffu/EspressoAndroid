package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;

public class BusinessCreateAccountsActivity extends AppCompatActivity {

    TextView userShortInfoTV,userNameTV,userBalanceTV;
    ImageView imgProfile,accountsCloseIV;
    LinearLayout llOpenAccount;
    MyApplication objMyApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_personal_accounts);

        llOpenAccount = findViewById(R.id.ll_open_account);
        userShortInfoTV = findViewById(R.id.tvUserInfo);
        imgProfile = findViewById(R.id.imgProfile);
        userNameTV = findViewById(R.id.userNameTV);
        userBalanceTV = findViewById(R.id.userBalanceTV);
        accountsCloseIV = findViewById(R.id.accountsCloseIV);

        accountsCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        llOpenAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessAddNewAccountActivity.class));
            }
        });

        userNameTV.setText("Jonathan Joestar");
        userBalanceTV.setText("19.99");
    }

}
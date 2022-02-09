package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class BusinessQuickActionActivity extends AppCompatActivity {

    LinearLayout scanLL, receivePaymentLL, merchantbuyTokenLL, widthdrawLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_quick_action);

        scanLL = findViewById(R.id.scanLL);
        receivePaymentLL = findViewById(R.id.receivePaymentLL);
        merchantbuyTokenLL = findViewById(R.id.merchantbuyTokenLL);
        widthdrawLL = findViewById(R.id.widthdrawLL);

        merchantbuyTokenLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessQuickActionActivity.this, SelectPaymentMethodActivity.class));
            }
        });
        widthdrawLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessQuickActionActivity.this, SelectWithdrawMethodActivity.class));
            }
        });
    }
}
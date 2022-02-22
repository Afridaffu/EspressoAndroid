package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.greenbox.coyni.R;

public class BusinessSalesOrderActivity extends AppCompatActivity {

    private ImageView refundIV, backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_sales_order);

        backIV = findViewById(R.id.BackIV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refundIV = findViewById(R.id.RefundIV);

        refundIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessSalesOrderActivity.this,RefundTransactionActivity.class);
                startActivity(intent);
            }
        });
    }
}
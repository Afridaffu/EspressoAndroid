package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.greenbox.coyni.R;

public class RefundTransactionActivity extends AppCompatActivity {

    private ImageView refundBackIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_transaction);

        refundBackIV = findViewById(R.id.RefundbackIV);
        refundBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
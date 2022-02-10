package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class SelectPaymentMethodActivity extends AppCompatActivity {
    private LinearLayout backpLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment_method);

        backpLL = findViewById(R.id.backpLL);

        backpLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
package com.coyni.mapp.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.coyni.mapp.R;

public class MerchantProfile extends AppCompatActivity {
LinearLayout openaccountLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_profile);
        openaccountLL = findViewById(R.id.openaccountLL);
        openaccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MerchantProfile.this, BusinessOnboardingOpenNewAccount.class);
                startActivity(intent);
            }
        });

    }
}

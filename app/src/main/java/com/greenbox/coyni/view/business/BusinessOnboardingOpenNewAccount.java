package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class BusinessOnboardingOpenNewAccount extends BaseActivity {
    LinearLayout businessAccountLL,personalaccountLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_onboarding_open_new_account);

        businessAccountLL = findViewById(R.id.businessAccontLL);
        businessAccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BusinessOnboardingOpenNewAccount.this,OpenNewAccount.class);
                startActivity(i);
            }
        });

        personalaccountLL = findViewById(R.id.personalAccontLL);
        personalaccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BusinessOnboardingOpenNewAccount.this,AddPersonalAccount.class);
                startActivity(i);
            }
        });
    }
}

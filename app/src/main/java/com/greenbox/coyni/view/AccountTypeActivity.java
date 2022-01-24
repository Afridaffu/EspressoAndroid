package com.greenbox.coyni.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.DashboardAddBenifitialOwnersActivity;


public class AccountTypeActivity extends AppCompatActivity {

    LinearLayout personalAccontLL, layoutClose, businessAccontLL;
    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_account_type);

            personalAccontLL = findViewById(R.id.personalAccontLL);
            businessAccontLL = findViewById(R.id.businessAccontLL);
            layoutClose = findViewById(R.id.layoutClose);
            personalAccontLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startCreateAccountActivity(Utils.PERSONAL_ACCOUNT);
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            businessAccontLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startCreateAccountActivity(Utils.BUSINESS_ACCOUNT);
                    try {
                        Intent i = new Intent(AccountTypeActivity.this, DashboardAddBenifitialOwnersActivity.class);
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void startCreateAccountActivity(int accountType) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent inCreateAccount = new Intent(AccountTypeActivity.this, CreateAccountActivity.class);
        inCreateAccount.putExtra(Utils.ACCOUNT_TYPE, accountType);
        startActivity(inCreateAccount);
        finish();
        overridePendingTransition(0, 0);
    }
}
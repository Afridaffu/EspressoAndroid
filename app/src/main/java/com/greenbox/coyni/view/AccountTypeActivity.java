package com.greenbox.coyni.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.GetstartedSuccessAcivity;


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
//                public void onClick(View v) {
//                    startCreateAccountActivity(Utils.BUSINESS_ACCOUNT);
//                }
                public void onClick(View v) {
                    Intent intent = new Intent(AccountTypeActivity.this, GetstartedSuccessAcivity.class);
                    startActivity(intent);
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

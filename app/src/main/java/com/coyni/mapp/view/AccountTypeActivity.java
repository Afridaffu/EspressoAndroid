package com.coyni.mapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;


public class AccountTypeActivity extends BaseActivity {

    LinearLayout personalAccontLL, layoutClose, businessAccontLL;
    Long mLastClickTime = 0L;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_account_type);

            objMyApplication = (MyApplication) getApplicationContext();
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
                    startCreateAccountActivity(Utils.BUSINESS_ACCOUNT);
//                    Toast.makeText(getApplication(), "Coming soon.", Toast.LENGTH_LONG).show();
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
        objMyApplication.setAccountType(accountType);
        Intent createIntent = new Intent(AccountTypeActivity.this, CreateAccountActivity.class);
        createIntent.putExtra(Utils.ACCOUNT_TYPE, accountType);
        startActivity(createIntent);
        finish();
        overridePendingTransition(0, 0);
    }
}

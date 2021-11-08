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


public class AccountTypeActivity extends AppCompatActivity {

    LinearLayout personalAccontLL;
    Long mLastClickTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_account_type);

            personalAccontLL = findViewById(R.id.personalAccontLL);
            personalAccontLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(AccountTypeActivity.this, CreateAccountActivity.class));
                    finish();

                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
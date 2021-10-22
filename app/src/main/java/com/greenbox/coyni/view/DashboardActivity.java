package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard);
            String url = BuildConfig.URL_PRODUCTION;
            String refererUrl = BuildConfig.Referer;
            if (url.equals("")) {
                Utils.setStrURL_PRODUCTION(url);
                Utils.setStrReferer(refererUrl);
            }
            Toast.makeText(this, "Url - " + url, Toast.LENGTH_LONG).show();
            TextView tvForgot = findViewById(R.id.tvForgot);
            tvForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DashboardActivity.this, ForgotPasswordActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
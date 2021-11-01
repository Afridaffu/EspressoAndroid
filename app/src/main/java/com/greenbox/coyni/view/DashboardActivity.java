package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout layoutProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            layoutProfile = findViewById(R.id.layoutProfile);
            layoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DashboardActivity.this, CustomerProfileActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {

    }
}
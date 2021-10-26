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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
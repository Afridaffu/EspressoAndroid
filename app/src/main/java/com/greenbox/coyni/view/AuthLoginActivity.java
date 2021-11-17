package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.greenbox.coyni.R;

public class AuthLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_auth_login);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
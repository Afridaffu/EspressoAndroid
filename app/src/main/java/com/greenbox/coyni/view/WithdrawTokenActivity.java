package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.greenbox.coyni.R;

public class WithdrawTokenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_withdraw_token);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
package com.coyni.mapp.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.coyni.mapp.model.check_out_transactions.CheckOutModel;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

public class IntentForwardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(intent);
    }

    private void handleIntent(Intent intent) {
        try {
            getIntentData(intent);
            Utils.setStrAuth("");
            intent.setClass(this, OnboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getIntentData(Intent intent) {
        if (intent == null) {
            return;
        }

        MyApplication objMyApplication = (MyApplication) getApplicationContext();
        Uri uri = intent.getData();
        if (uri != null && uri.isAbsolute()) {
            if (uri.getScheme().equals("coyni")
                    && uri.getHost().equalsIgnoreCase("paidOrder")) {
                if (uri.getQueryParameterNames().contains("encryptedToken")) {
                    CheckOutModel checkOutModel = new CheckOutModel();
                    checkOutModel.setCheckOutFlag(true);
                    checkOutModel.setEncryptedToken(uri.getQueryParameter("encryptedToken"));
                    objMyApplication.setCheckOutModel(checkOutModel);
                }
                if (uri.getQueryParameterNames().contains(Utils.SKIP_ENCRYPTION)) {
                    Utils.QA_SKIP_ENCRYPTION = true;
                }
            }
        }
    }
}

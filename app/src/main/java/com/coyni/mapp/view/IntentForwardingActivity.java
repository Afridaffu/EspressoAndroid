package com.coyni.mapp.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.coyni.mapp.model.check_out_transactions.CheckOutModel;
import com.coyni.mapp.utils.CheckOutConstants;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

import java.util.Set;

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
        CheckOutModel checkOutModel = new CheckOutModel();
        Uri uri = intent.getData();
        if (uri != null && uri.isAbsolute()) {
            Set<String> queryParams = uri.getQueryParameterNames();
            checkOutModel.setCheckOutFlag(true);
            for (String s : queryParams) {
                if (s.equalsIgnoreCase(CheckOutConstants.REQUEST_TOKEN)) {
                    checkOutModel.setEncryptedToken(uri.getQueryParameter(s));
                }
                if(s.equalsIgnoreCase(Utils.SKIP_ENCRYPTION)) {
                    Utils.QA_SKIP_ENCRYPTION = true;
                }
            }
            MyApplication objMyApplication = (MyApplication) getApplicationContext();
            objMyApplication.setCheckOutModel(checkOutModel);
        }
    }
}

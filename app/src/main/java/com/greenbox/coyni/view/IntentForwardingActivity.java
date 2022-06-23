package com.greenbox.coyni.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.utils.CheckOutConstants;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

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
        getIntentData(intent);
        intent.setClass(this, OnboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void getIntentData(Intent intent) {
        if(intent == null) {
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
            }
            MyApplication objMyApplication = (MyApplication) getApplicationContext();
            objMyApplication.setCheckOutModel(checkOutModel);
        }
    }
}

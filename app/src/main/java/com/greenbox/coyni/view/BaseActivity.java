package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.utils.CheckOutConstants;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.Set;

public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getName());
        Utils.launchedActivity = getClass();
        getIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.v(TAG, "onNewIntent called");
        getIntentData(intent);
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
                if (s.equalsIgnoreCase(CheckOutConstants.AMOUNT)) {
                    checkOutModel.setCheckOutAmount(uri.getQueryParameter(s));
                } else if (s.equalsIgnoreCase(CheckOutConstants.WALLET)) {
                    checkOutModel.setCheckOutWalletId(uri.getQueryParameter(s));
                }
            }
            MyApplication objMyApplication = (MyApplication) getApplicationContext();
            objMyApplication.setCheckOutModel(checkOutModel);
        }
    }

    public void showProgressDialog() {
        showProgressDialog("Please wait...");
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, false);
    }

    public void showProgressDialog(String message, boolean isCanceledOnTouchOutside) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ProgressDialog(BaseActivity.this, R.style.MyAlertDialogStyle);
        dialog.setIndeterminate(false);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}

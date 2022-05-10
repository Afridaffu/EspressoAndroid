package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.LogUtils;

public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getName());
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

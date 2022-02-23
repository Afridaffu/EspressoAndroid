package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.LogUtils;

public class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    public ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getName());
    }

    public void showProgressDialog() {
        dialog = new ProgressDialog(BaseActivity.this, R.style.MyAlertDialogStyle);
        dialog.setIndeterminate(false);
        dialog.setMessage("Please wait...");
        dialog.show();
    }

    public void dismissDialog() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }
}

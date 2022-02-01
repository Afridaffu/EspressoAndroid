package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.LogUtils;

public class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    public static BaseActivity baseActivity;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getName());
        baseActivity = this;
    }

    public void showProgressDialog() {
        dialog = new ProgressDialog(baseActivity, R.style.MyAlertDialogStyle);
        dialog.setIndeterminate(false);
        dialog.setMessage("Please wait...");
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}

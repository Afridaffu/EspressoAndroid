package com.greenbox.coyni.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.utils.LogUtils;

public class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    public static BaseActivity baseActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getName());
        baseActivity = this;
    }

    public void showProgressDialog() {

    }
}

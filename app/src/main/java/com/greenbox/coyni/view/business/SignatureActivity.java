package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.CustomSignatureView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignatureActivity extends BaseActivity {

    private CustomSignatureView mCustomSignatureView;
    private RelativeLayout mRlRotateMessageLayout;
    private LinearLayout mLlSignatureLayout;
    private String mSignatureFile = "signature.jpeg";
    private TextView mTvDone;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        initialization();
        handleOrientationChanges();

        mCustomSignatureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTvDone.setEnabled(true);
                mTvDone.setTextColor(getResources().getColor(R.color.primary_color));
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshOrientationChanges();
    }

    public void onDoneClicked(View view) {
        Intent in = new Intent();
        in.putExtra(Utils.DATA, saveSignature());
        setResult(RESULT_OK, in);
        finish();
    }

    public void onCloseClicked(View view) {
        mCustomSignatureView.clearSignature();
        finish();
    }

    public void onClearClicked(View view) {
        if (mCustomSignatureView != null) {
            mCustomSignatureView.clearSignature();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private int getAutoRotationSetting() {
        int setting = 0;
        try {
            setting = Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            LogUtils.v(TAG, "Auto rotate " + setting);
        } catch (Settings.SettingNotFoundException snfe) {
            LogUtils.e(TAG, snfe.getMessage());
        }
        return setting;
    }

    private void initialization() {
        mCustomSignatureView = findViewById(R.id.csv_signature_view);
        mRlRotateMessageLayout = findViewById(R.id.rl_rotate_message_layout);
        mTvDone = findViewById(R.id.tv_signature_done);
        mLlSignatureLayout = findViewById(R.id.ll_signature_layout);
        mTvDone.setEnabled(false);
    }

    private void handleOrientationChanges() {
        if (getAutoRotationSetting() == 0) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    refreshOrientationChanges();
                }
            };
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 2000);
        } else {
            refreshOrientationChanges();
        }
    }

    private void refreshOrientationChanges() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.v(TAG, "ORIENTATION_LANDSCAPE");
            mRlRotateMessageLayout.setVisibility(View.GONE);
            mLlSignatureLayout.setVisibility(View.VISIBLE);
            mCustomSignatureView.setEnabled(true);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtils.v(TAG, "ORIENTATION_PORTRAIT");
            mRlRotateMessageLayout.setVisibility(View.VISIBLE);
            mLlSignatureLayout.setVisibility(View.GONE);
            mCustomSignatureView.setEnabled(false);
        }
    }

    private String saveSignature() {
        String filePath = null;
        try {
            Bitmap bmp = mCustomSignatureView.getSignature();
            String destFolder = getCacheDir().getAbsolutePath();
            File f = new File(destFolder, mSignatureFile);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, out);
            filePath = f.getAbsolutePath();
            //filePath = compressImage(filePath);
            LogUtils.v(TAG, "file path " + filePath);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception " + e.getMessage());
        }
        return filePath;
    }

}

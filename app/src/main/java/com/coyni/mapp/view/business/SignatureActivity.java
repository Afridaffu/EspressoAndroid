package com.coyni.mapp.view.business;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.BitmapUtils;
import com.coyni.mapp.utils.CustomSignatureView;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;

public class SignatureActivity extends BaseActivity {

    private CustomSignatureView mCustomSignatureView;
    private RelativeLayout mRlRotateMessageLayout;
    private LinearLayout mLlSignatureLayout;
    private final String mSignatureFile = "signature.jpeg";
    private TextView mTvDone;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        initialization();
        refreshOrientationChanges();
        setOrientationListener();

        mCustomSignatureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setDoneButtonEnabled(true);
                return false;
            }
        });
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
        setDoneButtonEnabled(false);
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

    private void setDoneButtonEnabled(boolean isEnabled) {
        mTvDone.setEnabled(isEnabled);
        mTvDone.setTextColor(isEnabled ? getResources().getColor(R.color.primary_color) :
                getResources().getColor(R.color.light_gray));
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
            Bitmap bitmap = BitmapUtils.getScaledDownBitmap(bmp, 400, false);
            String destFolder = getCacheDir().getAbsolutePath();
            File f = new File(destFolder, mSignatureFile);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            filePath = f.getAbsolutePath();
            //filePath = compressImage(filePath);
            LogUtils.v(TAG, "file path " + filePath);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception " + e.getMessage());
        }
        return filePath;
    }

    private void setOrientationListener() {

        OrientationEventListener orientationEventListener =
                new OrientationEventListener(this) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        LogUtils.v(TAG, "orientation is " + orientation);
                        int epsilon = 10;
                        int leftLandscape = 90;
                        int rightLandscape = 270;
                        if (epsilonCheck(orientation, leftLandscape, epsilon) ||
                                epsilonCheck(orientation, rightLandscape, epsilon)) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        }
                    }

                    private boolean epsilonCheck(int a, int b, int epsilon) {
                        return a > b - epsilon && a < b + epsilon;
                    }
                };
        orientationEventListener.enable();
    }

}
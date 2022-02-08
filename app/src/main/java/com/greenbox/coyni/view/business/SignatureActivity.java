package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.CustomSignatureView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;

public class SignatureActivity extends BaseActivity {

    private CustomSignatureView mCustomSignatureView;
    private RelativeLayout mRlRotateMessageLayout;
    private LinearLayout mLlSignatureLayout;
    private String mSignatureFile = "signature.jpeg";
    private TextView mTvDone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        initialization();
        checkOrientation();

        mCustomSignatureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTvDone.setEnabled(true);
                mTvDone.setTextColor(getResources().getColor(R.color.primary_color));
                return false;
            }
        });
    }

    public void onDoneClicked(View view) {
        Intent in = new Intent();
        in.putExtra(Utils.DATA, saveSignature(mCustomSignatureView));
        setResult(RESULT_OK, in);
        finish();
    }

    public void onCloseClicked(View view) {
        mCustomSignatureView.clear();
        finish();
    }

    public void onClearClicked(View view) {
        if(mCustomSignatureView != null) {
            mCustomSignatureView.clear();
        }
    }

    private void initialization() {
        mCustomSignatureView = findViewById(R.id.csv_signature_view);
        mRlRotateMessageLayout = findViewById(R.id.rl_rotate_message_layout);
        mTvDone = findViewById(R.id.tv_signature_done);
        mLlSignatureLayout = findViewById(R.id.ll_signature_layout);
        mTvDone.setEnabled(false);
    }

    private void checkOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.v(TAG, "ORIENTATION_LANDSCAPE");
            mRlRotateMessageLayout.setVisibility(View.GONE);
            mLlSignatureLayout.setVisibility(View.VISIBLE);
            mCustomSignatureView.setEnabled(true);
        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtils.v(TAG, "ORIENTATION_PORTRAIT");
            mRlRotateMessageLayout.setVisibility(View.VISIBLE);
            mLlSignatureLayout.setVisibility(View.GONE);
            mCustomSignatureView.setEnabled(false);
        }
    }

    private String saveSignature(View v) {
        String filePath = null;
        try {
            Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);
            String destFolder = getCacheDir().getAbsolutePath();
            File f = new File(destFolder, mSignatureFile);
            if(f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, out);
            filePath = f.getAbsolutePath();
            LogUtils.v(TAG, "file path " + filePath);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception " + e.getMessage());
        }
        return filePath;
    }
}

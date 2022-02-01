package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.CustomSignatureView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;

public class SignatureActivity extends BaseActivity {

    private CustomSignatureView customSignatureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        checkOrientation();

        customSignatureView = findViewById(R.id.csv_signature_view);
    }

    public void onDoneClicked(View view) {
        Intent in = new Intent();
        in.putExtra("data", saveSignature(customSignatureView));
        setResult(RESULT_OK, in);
        finish();
    }

    public void onCloseClicked(View view) {
        customSignatureView.clear();
        finish();
    }

    public void onClearClicked(View view) {
        if(customSignatureView != null) {
            customSignatureView.clear();
        }
    }

    private void checkOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.v(TAG, "ORIENTATION_LANDSCAPE");
        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtils.v(TAG, "ORIENTATION_PORTRAIT");
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
            File f = new File(destFolder, "signature.jpeg");
            if(f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
            filePath = f.getAbsolutePath();
            LogUtils.v(TAG, "file path " + filePath);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception " + e.getMessage());
        }
        return filePath;
    }
}

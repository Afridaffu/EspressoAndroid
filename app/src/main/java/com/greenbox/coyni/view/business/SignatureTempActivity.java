package com.greenbox.coyni.view.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;

import java.io.File;

public class SignatureTempActivity extends BaseActivity {

    private ImageView mIVSignature;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_signature);

        mIVSignature = findViewById(R.id.iv_signature);
    }

    public void onGetSignatureCLicked(View view) {
        Intent inSignature = new Intent(SignatureTempActivity.this, SignatureActivity.class);
        activityResultLauncher.launch(inSignature);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        String filePath = data.getStringExtra("data");
                        File targetFile = new File(filePath);
                        if(targetFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
                            LogUtils.v(TAG, "file size " + myBitmap.getByteCount());
                            mIVSignature.setImageBitmap(myBitmap);
                        }
                    }
                }
            });
}

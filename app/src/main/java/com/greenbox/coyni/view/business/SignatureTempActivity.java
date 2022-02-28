package com.greenbox.coyni.view.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignatureTempActivity extends BaseActivity {

    private ImageView mIVSignature;
    List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    List<ProfilesResponse.Profiles> dbaAccountList = new ArrayList<>();
    List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    Map<String, ArrayList<ProfilesResponse.Profiles>> map = new HashMap<>();

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
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getSignature(result.getData());
                }
            });

    private void getSignature(Intent data) {
        if (data != null) {
            String filePath = data.getStringExtra(Utils.DATA);
            File targetFile = new File(filePath);
            if (targetFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 3;
                Bitmap myBitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
                LogUtils.v(TAG, myBitmap.getHeight() + " - " + myBitmap.getWidth() + " - file size " + myBitmap.getByteCount());
                mIVSignature.setImageBitmap(myBitmap);
            }
        }
    }


}

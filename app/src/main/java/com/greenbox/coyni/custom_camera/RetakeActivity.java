package com.greenbox.coyni.custom_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class RetakeActivity extends AppCompatActivity {

    ImageView croppedIV;
    LinearLayout retakeCloseIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_retake);
        croppedIV = findViewById(R.id.croppedIV);
        retakeCloseIV = findViewById(R.id.retakeCloseIV);

        retakeCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rotatePicture(getIntent().getIntExtra("rotation",0),CameraFragment.cameraByteData,croppedIV);
    }


    private void rotatePicture(int rotation,  byte[] data, ImageView photoImageView) {
        try {
//            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromByte(this, data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (rotation != 0) {
                Bitmap oldBitmap = bitmap;
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                bitmap = Bitmap.createBitmap(
                        oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false);
                try {
                    if (!oldBitmap.isRecycled()) {
                        oldBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

////        Matrix matrix = new Matrix();
////        matrix.postRotate((float) rotation);
////
////        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
////                matrix, false);
//
//            photoImageView.setImageBitmap(bitmap);

            ImageUtility.savePicture(this, bitmap,photoImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
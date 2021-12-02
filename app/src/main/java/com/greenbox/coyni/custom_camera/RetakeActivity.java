package com.greenbox.coyni.custom_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import com.greenbox.coyni.R;

public class RetakeActivity extends AppCompatActivity {

    ImageView croppedIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retake);
        croppedIV = findViewById(R.id.croppedIV);

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
package com.coyni.mapp.custom_camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coyni.mapp.R;
import com.coyni.mapp.fragments.BaseFragment;
import com.coyni.mapp.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RetakeFragment extends BaseFragment {

    private View currentView;
    private ImageView croppedIV;
    private LinearLayout retakeCloseIV, saveLL, retakeLL;
    private OnImageRetakeListener listener;
    private File mediaFile;

    public static RetakeFragment newInstance() {
        return new RetakeFragment();
    }

    public void setRetakeListener(OnImageRetakeListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        currentView = inflater.inflate(R.layout.fragment_retake, container, false);
        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        croppedIV = currentView.findViewById(R.id.croppedIV);
        retakeCloseIV = currentView.findViewById(R.id.retakeCloseIV);
        saveLL = currentView.findViewById(R.id.saveLL);
        retakeLL = currentView.findViewById(R.id.retakeLL);

        retakeCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onImageRetake();
                }
            }
        });

        retakeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onImageRetake();
                }
            }
        });

        saveLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onImageSaved(mediaFile);
                }
            }
        });
        Bundle b = getArguments();
        rotatePicture(b.getInt("rotation", 0), CameraHandlerActivity.cameraByteData, croppedIV);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (listener != null) {
            listener.onImageRetake();
        }
    }

    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView) {
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
            savePicture(getActivity(), bitmap, photoImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri savePicture(Context context, Bitmap bitmap, ImageView photoImageView) {

        Uri fileContentUri = null;
        try {
            int halvedRectangularHeight = (int) (CameraFragmentRefactor.globalImageHeight);
            int height = (int) (halvedRectangularHeight);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, CameraFragmentRefactor.globalImageWidth, (int) (height * 2));
            photoImageView.setImageBitmap(bitmap);
//            File mediaStorageDir = new File(
//                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "coyni");
//            if (!mediaStorageDir.exists()) {
//                if (!mediaStorageDir.mkdirs()) {
//                    return null;
//                }
//            }
            String tempDirPath = FileUtils.getCacheTempDirectory(getActivity());
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mediaFile = new File(tempDirPath + "IMG_" + timeStamp + ".jpg");
            // Saving the bitmap
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                FileOutputStream stream = new FileOutputStream(mediaFile);
                stream.write(out.toByteArray());
                stream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileContentUri;
    }
}

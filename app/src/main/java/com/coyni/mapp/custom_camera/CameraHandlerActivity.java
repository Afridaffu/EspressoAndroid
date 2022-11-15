package com.coyni.mapp.custom_camera;

import static com.coyni.mapp.custom_camera.CameraUtility.CAMERA_ACTION_SELECTOR.CAMERA_CROP;
import static com.coyni.mapp.custom_camera.CameraUtility.CAMERA_ACTION_SELECTOR.GALLERY_CROP;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.coyni.mapp.R;
import com.coyni.mapp.fragments.BaseFragment;
import com.coyni.mapp.utils.FileUtils;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.view.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraHandlerActivity extends BaseActivity implements OnPictureTakenListener, OnImageRetakeListener {

    public static byte[] cameraByteData;
    private BaseFragment currentFragment;
    //    private String filePath = "";
    private CameraUtility.CAMERA_ACTION_SELECTOR action;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_handler);

        launchFlowBasedOnAction();

    }

    private void launchFlowBasedOnAction() {
        if (getIntent() != null && getIntent().getSerializableExtra(CameraUtility.CAMERA_ACTION) != null) {
            action = (CameraUtility.CAMERA_ACTION_SELECTOR) getIntent().getSerializableExtra(CameraUtility.CAMERA_ACTION);
            switch (action) {
                case CAMERA_CROP:
                    openCameraForImage();
                    break;
                case CAMERA_RETAKE:
                    showCameraFragment();
                    break;
                case BROWSE:
                    browseFile();
                    break;
                case GALLERY:
                case GALLERY_CROP:
                    openGalleryForImage();
                    break;
            }
        } else {
            LogUtils.e(TAG, "No action available to perform");
        }
    }

    private void browseFile() {
        Intent pickIntent = new Intent();
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickIntent.setType("*/*");
        String[] extraMimeTypes = {"application/pdf", "image/*"};
        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Picture");
        browseFileLauncher.launch(chooserIntent);
    }

    public void openCameraForImage() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityLauncher.launch(i);
    }

    public void openGalleryForImage() {
        finish();
        Intent i = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        galleryActionLauncher.launch(i);
    }

    ActivityResultLauncher<Intent> cameraActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    setActionCropImage(data);
                } else {
                    sendCancelledResult();
                }
            });

    ActivityResultLauncher<Intent> galleryActionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    processGalleryImage(result.getData());
                } else {
                    sendCancelledResult();
                }
            });

    ActivityResultLauncher<Intent> browseFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        String filePath = FileUtils.getReadablePathFromUri(getApplicationContext(), result.getData().getData());
                        if(filePath == null || filePath.equals("")) {
                            sendCancelledResult();
                            return;
                        }
                        File mediaFile = new File(filePath);
                        if (mediaFile.exists()) {
                            sendResult(mediaFile.getAbsolutePath());
                        } else {
                            sendCancelledResult();
                        }
                    }
                } else {
                    sendCancelledResult();
                }
            });

    private void processGalleryImage(Intent data) {
        Uri selectedImage = null;
        if (data != null) {
            selectedImage = data.getData();
        }
        if (selectedImage == null) {
            LogUtils.e(TAG, "selectedImage is null");
            return;
        }
        Bitmap bitmap = null;
        try {
            bitmap = Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            String filePath = saveBitmapToFile(bitmap);
            File mediaFile = new File(filePath);
            if (action == GALLERY_CROP) {
                CropImage.activity(Uri.fromFile(mediaFile)).start(CameraHandlerActivity.this);
            } else {
                sendResult(mediaFile.getAbsolutePath());
            }
        }

    }

    private void setActionCropImage(Intent data) {
        try {
            Bitmap selectedImage;
            if (data != null) {
                selectedImage = (Bitmap) data.getExtras().get("data");
                String filePath = saveBitmapToFile(selectedImage);
//                Uri uri = getImageUri(selectedImage);
                if (action == GALLERY_CROP || action == CAMERA_CROP) {
                    CropImage.activity(Uri.fromFile(new File(filePath))).start(CameraHandlerActivity.this);
                } else {
                    sendResult(filePath);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String saveBitmapToFile(Bitmap inImage) {
        String tempFile = FileUtils.getCacheTempDirectory(CameraHandlerActivity.this);
        try {
            File file = new File(tempFile, "doc_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null) {
            currentFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null && result.getUri() != null) {
                sendResult(result.getUri().getPath());
            } else {
                sendCancelledResult();
            }
        }
    }

    private void showFragment(BaseFragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, "")
                .commit();
    }

    private void showCameraFragment() {
        try {
            CameraFragmentRefactor cameraFragment = CameraFragmentRefactor.newInstance();
            cameraFragment.setCameraCallback(this);
            showFragment(cameraFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRetakeFragment(int rotation) {
        try {
            RetakeFragment retakeFragment = RetakeFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("rotation", rotation);
            retakeFragment.setArguments(bundle);
            retakeFragment.setRetakeListener(this);
            showFragment(retakeFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPictureTakingCompleted(byte[] data, int rotation) {
        cameraByteData = data;
        showRetakeFragment(rotation);
    }

    @Override
    public void onPictureTakingCancelled() {
        sendCancelledResult();
    }

    @Override
    public void onImageRetake() {
        showCameraFragment();
    }

    @Override
    public void onImageSaved(File file) {
        LogUtils.e(TAG, "Selected filepath " + file.getAbsolutePath());
        sendResult(file.getAbsolutePath());
    }

    private void sendResult(String filePath) {
        Intent in = new Intent();
        in.putExtra(CameraUtility.TARGET_FILE, filePath);
        in.putExtra(CameraUtility.SELECTING_ID, getIntent().getStringExtra(CameraUtility.SELECTING_ID));
        setResult(RESULT_OK, in);
        finish();
    }

    private void sendCancelledResult() {
        setResult(RESULT_CANCELED);
        finish();
    }


}

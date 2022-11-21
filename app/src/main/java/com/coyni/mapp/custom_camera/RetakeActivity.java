package com.coyni.mapp.custom_camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.AdditionalActionUploadActivity;
import com.coyni.mapp.view.IdentityVerificationActivity;
import com.coyni.mapp.view.business.AddBeneficialOwnerActivity;
import com.coyni.mapp.view.business.AdditionalInformationRequiredActivity;
import com.coyni.mapp.view.business.BusinessAdditionalActionRequiredActivity;
import com.coyni.mapp.view.business.CompanyInformationActivity;
import com.coyni.mapp.view.business.DBAInfoAcivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RetakeActivity extends AppCompatActivity {

    ImageView croppedIV;
    LinearLayout retakeCloseIV, saveLL, retakeLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_retake);
        croppedIV = findViewById(R.id.croppedIV);
        retakeCloseIV = findViewById(R.id.retakeCloseIV);
        saveLL = findViewById(R.id.saveLL);
        retakeLL = findViewById(R.id.retakeLL);

        Log.e("From", getIntent().getStringExtra("FROM"));
        String from = getIntent().getStringExtra("FROM");

        retakeCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (from.equals("CI-AOI")) {
                    CompanyInformationActivity.aoiFile = null;
                } else if (from.equals("CI-EINLETTER")) {
                    CompanyInformationActivity.einLetterFile = null;
                } else if (from.equals("CI-W9")) {
                    CompanyInformationActivity.w9FormFile = null;
                } else if (from.equals("DBA_INFO")) {
                    DBAInfoAcivity.dbaFile = null;
                } else if (from.equals("IDVE")) {
                    IdentityVerificationActivity.identityFile = null;
                    IdentityVerificationActivity.isFileSelected = false;
                    IdentityVerificationActivity.enableNext();
                } else if (from.equals("ADD_BO")) {
                    AddBeneficialOwnerActivity.identityFile = null;
                    AddBeneficialOwnerActivity.isFileSelected = false;
                    AddBeneficialOwnerActivity.enableOrDisableNext();
                } else if (from.equals("BAARA")) {
                } else if (from.equals("AAR-securityCard")) {
                    AdditionalInformationRequiredActivity.securityFile = null;
                } else if (from.equals("AAR-actionReq2File")) {
                    AdditionalInformationRequiredActivity.actionReq2File = null;
                } else if (from.equals("ActRqrdDocs")) {
//                    AdditionalActionUploadActivity.mediaFile = null;
                }
                finish();
            }
        });

        saveLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CameraActivity.cameraActivity.finish();
                finish();

                if (from.equals("CI-AOI")) {
                    CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(5);
                } else if (from.equals("CI-EINLETTER")) {
                    CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(6);
                } else if (from.equals("CI-W9")) {
                    if (CompanyInformationActivity.companyInformationActivity.SSNTYPE.equals("SSN"))
                        CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(11);
                    else if (CompanyInformationActivity.companyInformationActivity.SSNTYPE.equals("EIN/TIN"))
                        CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(7);
                } else if (from.equals("DBA_INFO")) {
                    DBAInfoAcivity.dbaInfoAcivity.removeAndUploadAdditionalDoc(8);
                } else if (from.equals("IDVE")) {
                    IdentityVerificationActivity.enableNext();
                } else if (from.equals("ADD_BO")) {
                    AddBeneficialOwnerActivity.addBeneficialOwnerActivity.setDocSelected();
                    AddBeneficialOwnerActivity.enableOrDisableNext();
                }
//                else if (from.equals("BAARA")) {
//                    BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.saveFileFromCamera(ImageUtility.mediaFile);
//                    // BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.removeAndUploadAdditionalDoc(0);
//                }
                else if (from.equals("AAR-FBL")) {
                    //  BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.removeAndUploadAdditionalDoc(3);
                } else if (from.equals("AAR-securityCard")) {
                    AdditionalInformationRequiredActivity.additionalInformationRequiredActivity.removeAndUploadAdditionalDoc(0);
                } else if (from.equals("AAR-actionReq2File")) {
                    AdditionalInformationRequiredActivity.additionalInformationRequiredActivity.removeAndUploadAdditionalDoc(0);
                } else if (from.equals("ActRqrdDocs")) {
                    AdditionalActionUploadActivity.additionalActionUploadActivity.saveFileFromCamera(ImageUtility.mediaFile);
                }
            }
        });

        retakeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (from.equals("CI-AOI")) {
                    CompanyInformationActivity.aoiFile = null;
                } else if (from.equals("CI-EINLETTER")) {
                    CompanyInformationActivity.einLetterFile = null;
                } else if (from.equals("CI-W9")) {
                    CompanyInformationActivity.w9FormFile = null;
                } else if (from.equals("DBA_INFO")) {
                    DBAInfoAcivity.dbaFile = null;
                } else if (from.equals("IDVE")) {
                    IdentityVerificationActivity.identityFile = null;
                    IdentityVerificationActivity.isFileSelected = false;
                    IdentityVerificationActivity.enableNext();
                } else if (from.equals("ADD_BO")) {
                    AddBeneficialOwnerActivity.identityFile = null;
                    AddBeneficialOwnerActivity.isFileSelected = false;
                    AddBeneficialOwnerActivity.enableOrDisableNext();
                } else if (from.equals("BAARA")) {
                    //BusinessAdditionalActionRequiredActivity.adtionalSscFile = null;
                } else if (from.equals("AAR-SecFile")) {
                    BusinessAdditionalActionRequiredActivity.additional2fFle = null;
                } else if (from.equals("AAR-FBL")) {
                    BusinessAdditionalActionRequiredActivity.businessLicenceFile = null;
                } else if (from.equals("AAR-securityCard")) {
                    AdditionalInformationRequiredActivity.securityFile = null;
                } else if (from.equals("AAR-actionReq2File")) {
                    AdditionalInformationRequiredActivity.actionReq2File = null;
                } else if (from.equals("ActRqrdDocs")) {
//                    AdditionalActionUploadActivity.mediaFile = null;
                }
            }
        });

        rotatePicture(getIntent().getIntExtra("rotation", 0), CameraFragment.cameraByteData, croppedIV);
    }


    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView) {
        try {
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
            ImageUtility.savePicture(this, bitmap, photoImageView, getIntent().getStringExtra("FROM"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public static class ImageUtility {

        private static final double ASPECT_RATIO = 3.0 / 4.0;

        private static File mediaFile;

        public String convertBitmapToString(Bitmap bitmap) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
        }

        public byte[] convertBitmapStringToByteArray(String bitmapByteString) {
            return Base64.decode(bitmapByteString, Base64.DEFAULT);
        }

        public Bitmap rotatePicture(Context context, int rotation, byte[] data) {
            Bitmap bitmap = decodeSampledBitmapFromByte(context, data);

            if (rotation != 0) {
                Bitmap oldBitmap = bitmap;

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                bitmap = Bitmap.createBitmap(
                        oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
                );

                oldBitmap.recycle();
            }

            return bitmap;
        }

        public static Uri savePicture(Context context, Bitmap bitmap, ImageView photoImageView, String from) {

            Uri fileContentUri = null;
            try {
                int halvedRectangularHeight = (int) (CameraFragment.globalImageHeight);
                int height = (int) (halvedRectangularHeight);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, CameraFragment.globalImageWidth, (int) (height * 2));
                photoImageView.setImageBitmap(bitmap);
                File mediaStorageDir = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "coyni");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return null;
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
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
//             Mediascanner need to scan for the image saved
                Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                fileContentUri = Uri.fromFile(mediaFile);
                mediaScannerIntent.setData(fileContentUri);
                context.sendBroadcast(mediaScannerIntent);
                if (from.equals("IDVE")) {
                    if (Utils.isValidFileSize(mediaFile)) {
                        IdentityVerificationActivity.identityFile = mediaFile;
                        IdentityVerificationActivity.isFileSelected = true;
                    } else {
                        Utils.displayAlert(context.getString(R.string.allowed_file_size_error), (Activity) context, "coyni", "");
                    }
                } else if (from.equals("CI-AOI")) {
                    CompanyInformationActivity.aoiFile = mediaFile;
//                CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(5);
                } else if (from.equals("CI-EINLETTER")) {
                    CompanyInformationActivity.einLetterFile = mediaFile;
//                CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(6);
                } else if (from.equals("CI-W9")) {
                    CompanyInformationActivity.w9FormFile = mediaFile;
//                CompanyInformationActivity.companyInformationActivity.removeAndUploadAdditionalDoc(7);
                } else if (from.equals("DBA_INFO")) {
                    DBAInfoAcivity.dbaFile = mediaFile;
                    // DBAInfoAcivity.dbaInfoAcivity.removeAndUploadAdditionalDoc(8);
                } else if (from.equals("ADD_BO")) {
                    AddBeneficialOwnerActivity.identityFile = mediaFile;
//                DBAInfoAcivity.dbaInfoAcivity.removeAndUploadAdditionalDoc(8);
                } else if (from.equals("BAARA")) {
//                    BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.saveFileFromCamera(mediaFile);
                } else if (from.equals("AAR-SecFile")) {
                    BusinessAdditionalActionRequiredActivity.additional2fFle = mediaFile;
                } else if (from.equals("AAR-FBL")) {
                    BusinessAdditionalActionRequiredActivity.businessLicenceFile = mediaFile;
                } else if (from.equals("AAR-securityCard")) {
                    AdditionalInformationRequiredActivity.securityFile = mediaFile;
                } else if (from.equals("AAR-actionReq2File")) {
                    AdditionalInformationRequiredActivity.actionReq2File = mediaFile;
                } else if (from.equals("ActRqrdDocs")) {
//                    AdditionalActionUploadActivity.additionalActionUploadActivity.saveFileFromCamera(mediaFile);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return fileContentUri;
        }

        public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            options.inBitmap = BitmapFactory.decodeFile(path, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inScaled = true;
            options.inDensity = options.outWidth;
            options.inTargetDensity = reqWidth * options.inSampleSize;

            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inInputShareable = true;

            return BitmapFactory.decodeFile(path, options);
        }

        /**
         * Decode and sample down a bitmap from a byte stream
         */
        public Bitmap decodeSampledBitmapFromByte(Context context, byte[] bitmapBytes) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            int reqWidth, reqHeight;
            Point point = new Point();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                display.getSize(point);
                reqWidth = point.x;
                reqHeight = point.y;
            } else {
                reqWidth = display.getWidth();
                reqHeight = display.getHeight();
            }

            Log.e("req w and h", reqWidth + "  " + reqHeight);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            options.inBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Load & resize the image to be 1/inSampleSize dimensions
            // Use when you do not want to scale the image with a inSampleSize that is a power of 2
            options.inScaled = true;
            options.inDensity = options.outWidth;
            options.inTargetDensity = reqWidth * options.inSampleSize;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false; // If set to true, the decoder will return null (no bitmap), but the out... fields will still be set, allowing the caller to query the bitmap without having to allocate the memory for its pixels.
            options.inPurgeable = true;         // Tell to gc that whether it needs free memory, the Bitmap can be cleared
            options.inInputShareable = true;    // Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

//        Log.e("options", options.inBitmap.getWidth()+" "+options.inBitmap.getHeight());
//        Log.e("options", options.inSampleSize+" "+options.inDensity+" "+options.inTargetDensity);
            return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
        }

        /**
         * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
         * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
         * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
         * having a width and height equal to or larger than the requested width and height
         * <p>
         * The function rounds up the sample size to a power of 2 or multiple
         * of 8 because BitmapFactory only honors sample size this way.
         * For example, BitmapFactory downsamples an image by 2 even though the
         * request is 3. So we round up the sample size to avoid OOM.
         */
        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            int initialInSampleSize = computeInitialSampleSize(options, reqWidth, reqHeight);

            Log.e("initialInSampleSize", initialInSampleSize + "");
            int roundedInSampleSize;
            if (initialInSampleSize <= 8) {
                roundedInSampleSize = 1;
                while (roundedInSampleSize < initialInSampleSize) {
                    // Shift one bit to left
                    roundedInSampleSize <<= 1;
                }
            } else {
                roundedInSampleSize = (initialInSampleSize + 7) / 8 * 8;
            }
            Log.e("roundedInSampleSize", roundedInSampleSize + "");


            return roundedInSampleSize;
        }

        private int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final double height = options.outHeight;
            final double width = options.outWidth;

            final long maxNumOfPixels = reqWidth * reqHeight;
            final int minSideLength = Math.min(reqHeight, reqWidth);

            int lowerBound = (maxNumOfPixels < 0) ? 1 :
                    (int) Math.ceil(Math.sqrt(width * height / maxNumOfPixels));
            int upperBound = (minSideLength < 0) ? 128 :
                    (int) Math.min(Math.floor(width / minSideLength),
                            Math.floor(height / minSideLength));

            if (upperBound < lowerBound) {
                // return the larger one when there is no overlapping zone.
                return lowerBound;
            }

            if (maxNumOfPixels < 0 && minSideLength < 0) {
                return 1;
            } else if (minSideLength < 0) {
                return lowerBound;
            } else {
                return upperBound;
            }
        }

    }
}
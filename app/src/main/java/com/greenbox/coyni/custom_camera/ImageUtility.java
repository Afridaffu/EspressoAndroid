package com.greenbox.coyni.custom_camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.greenbox.coyni.view.IdentityVerificationActivity;
import com.greenbox.coyni.view.business.AddBeneficialOwnerActivity;
import com.greenbox.coyni.view.business.AdditionalInformationRequiredActivity;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;
import com.greenbox.coyni.view.business.CompanyInformationActivity;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtility {


    private static final double ASPECT_RATIO = 3.0 / 4.0;

    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    public static byte[] convertBitmapStringToByteArray(String bitmapByteString) {
        return Base64.decode(bitmapByteString, Base64.DEFAULT);
    }

    public static Bitmap rotatePicture(Context context, int rotation, byte[] data) {
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
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
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
                IdentityVerificationActivity.identityFile = mediaFile;
                IdentityVerificationActivity.isFileSelected = true;
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
            } else if(from.equals("BAARA")) {
                BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.saveFileFromCamera(mediaFile);
            }
            else if(from.equals("AAR-SecFile")) {
                BusinessAdditionalActionRequiredActivity.additional2fFle = mediaFile;
            }
            else if(from.equals("AAR-FBL")) {
                BusinessAdditionalActionRequiredActivity.businessLicenceFile = mediaFile;
            }
            else if(from.equals("AAR-securityCard")){
                AdditionalInformationRequiredActivity.securityFile = mediaFile;
            }
            else if(from.equals("AAR-actionReq2File")){
                AdditionalInformationRequiredActivity.actionReq2File = mediaFile;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileContentUri;
    }

    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
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
    public static Bitmap decodeSampledBitmapFromByte(Context context, byte[] bitmapBytes) {
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
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    private static int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

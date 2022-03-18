package com.greenbox.coyni.custom_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.IdentityVerificationActivity;
import com.greenbox.coyni.view.business.AddBeneficialOwnerActivity;
import com.greenbox.coyni.view.business.AdditionalInformationRequiredActivity;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;
import com.greenbox.coyni.view.business.CompanyInformationActivity;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

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
                }else if (from.equals("AAR-SSC")) {
                    BusinessAdditionalActionRequiredActivity.adtionalSscFile = null;
                }else if (from.equals("AAR-SecFile")){
                    BusinessAdditionalActionRequiredActivity.addtional2fFle = null;
                }else if (from.equals("AAR-FBL")){
                    BusinessAdditionalActionRequiredActivity.businessLincenseFile = null;
                }
                else if(from.equals("AAR-securityCard")){
                    AdditionalInformationRequiredActivity.securityFile = null;
                }
                else if(from.equals("AAR-actionReq2File")){
                    AdditionalInformationRequiredActivity.actionReq2File = null;
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
                    AddBeneficialOwnerActivity.addBeneficialOwnerActivity.removeAndUploadBODoc();
                    AddBeneficialOwnerActivity.enableOrDisableNext();
                } else if(from.equals("AAR-SSC")){
                    BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.removeAndUploadAdditionalDoc(1);
                }else if(from.equals("AAR-SecFile")) {
                    BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.removeAndUploadAdditionalDoc(2);
                }else if(from.equals("AAR-FBL")) {
                    BusinessAdditionalActionRequiredActivity.businessAdditionalActionRequired.removeAndUploadAdditionalDoc(3);
                }
                else if(from.equals("AAR-securityCard")){
                    AdditionalInformationRequiredActivity.additionalInformationRequiredActivity.removeAndUploadAdditionalDoc(0);
                }
                else if(from.equals("AAR-actionReq2File")){
                    AdditionalInformationRequiredActivity.additionalInformationRequiredActivity.removeAndUploadAdditionalDoc(0);
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
                }else if (from.equals("AAR-SSC")) {
                    BusinessAdditionalActionRequiredActivity.adtionalSscFile = null;
                }else if (from.equals("AAR-SecFile")){
                    BusinessAdditionalActionRequiredActivity.addtional2fFle = null;
                }else if (from.equals("AAR-FBL")){
                    BusinessAdditionalActionRequiredActivity.businessLincenseFile = null;
                }
                else if(from.equals("AAR-securityCard")){
                    AdditionalInformationRequiredActivity.securityFile = null;
                }
                else if(from.equals("AAR-actionReq2File")){
                    AdditionalInformationRequiredActivity.actionReq2File = null;
                }
            }
        });

        rotatePicture(getIntent().getIntExtra("rotation", 0), CameraFragment.cameraByteData, croppedIV);
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
            ImageUtility.savePicture(this, bitmap, photoImageView, getIntent().getStringExtra("FROM"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
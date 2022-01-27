package com.greenbox.coyni.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;

public class Business_UserDetailsListenersActivity extends AppCompatActivity {

    String authenticateType = "",phoneFormat="";
    TextView heading,title,value;
    CardView changeCV;
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    Long mLastClickTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_user_details_listeners);

        heading=findViewById(R.id.intentName);
        title=findViewById(R.id.titleTV);
        value=findViewById(R.id.contentTV);
        changeCV=findViewById(R.id.changeCV);

        findViewById(R.id.dialogCLoseLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails")&&getIntent().getStringExtra("title").equalsIgnoreCase("EMAIL")){
            heading.setText("Email");
            title.setText("Current Email");
            authenticateType=getIntent().getStringExtra("title");
            value.setText(getIntent().getStringExtra("value"));
            isTouchId=Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock=Boolean.parseBoolean(getIntent().getStringExtra("face"));
            changeCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    try {
                        if ((isFaceLock || isTouchId) && Utils.checkAuthentication(Business_UserDetailsListenersActivity.this)) {
                            if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(Business_UserDetailsListenersActivity.this)) || (isFaceLock))) {
                                Utils.checkAuthentication(Business_UserDetailsListenersActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                            } else {
                                startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("screen", "EditEmail"));
                            }
                        } else {
                            startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "EditEmail"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails")&&getIntent().getStringExtra("title").equalsIgnoreCase("ADDRESS")){
            heading.setText("Address");
            title.setText("Current Address");
            value.setText(getIntent().getStringExtra("value"));
            authenticateType=getIntent().getStringExtra("title");
            isTouchId=Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock=Boolean.parseBoolean(getIntent().getStringExtra("face"));

            changeCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    try {

                                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(Business_UserDetailsListenersActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(Business_UserDetailsListenersActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(Business_UserDetailsListenersActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "EditAddress"));
                        }
                    } else {
                        startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditAddress"));
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
         
        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails")&&getIntent().getStringExtra("title").equalsIgnoreCase("PHONE")) {
            heading.setText("Phone Number");
            title.setText("Current Phone Number");
            value.setText(getIntent().getStringExtra("value"));
            phoneFormat=getIntent().getStringExtra("value");
            authenticateType=getIntent().getStringExtra("title");
            isTouchId=Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock=Boolean.parseBoolean(getIntent().getStringExtra("face"));

                changeCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        try {
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(Business_UserDetailsListenersActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(Business_UserDetailsListenersActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(Business_UserDetailsListenersActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("OLD_PHONE", phoneFormat)
                                            .putExtra("screen", "EditPhone"));
                                }
                            } else {
                                startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("OLD_PHONE", phoneFormat)
                                        .putExtra("screen", "EditPhone"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
//            case 0:
//                if (resultCode == RESULT_OK && data != null) {
//                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                    Uri uri = getImageUri(this, selectedImage);
//                    CropImage.activity(uri).start(this);
//                        userProfileIV.setVisibility(View.VISIBLE);
//                        imageTextTV.setVisibility(View.GONE);
//                        userProfileIV.setImageBitmap(selectedImage);
//                        uploadImage();
//                }
//                break;
//            case 1:
//                if (resultCode == RESULT_OK && data != null) {
//                    try {
//                        Uri selectedImage = data.getData();
//                        if (selectedImage != null) {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
////                                userProfileIV.setImageBitmap(bitmap);
////                                userProfileIV.setVisibility(View.VISIBLE);
////                                imageTextTV.setVisibility(View.GONE);
////                                uploadImage();
//                            Uri uri = getImageUri(this, bitmap);
//                            CropImage.activity(uri).start(this);
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                break;

//            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                if (resultCode == RESULT_OK) {
//                    Uri resultUri = result.getUri();
//                    userProfileIV.setVisibility(View.VISIBLE);
//                    imageTextTV.setVisibility(View.GONE);
//                    userProfileIV.setImageURI(resultUri);
//                    uploadImage();
//
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Exception error = result.getError();
//                }
//            }
//            break;
            case 251: {
                if (resultCode == RESULT_OK) {
                    if (authenticateType.equals("EMAIL")) {
                        Intent ee = new Intent(Business_UserDetailsListenersActivity.this, EditEmailActivity.class);
                        startActivity(ee);
                    } else if (authenticateType.equals("ADDRESS")) {
                        Intent ea = new Intent(Business_UserDetailsListenersActivity.this, EditAddressActivity.class);
                        startActivity(ea);
                    } else if (authenticateType.equals("PHONE")) {
                        Intent ep = new Intent(Business_UserDetailsListenersActivity.this, EditPhoneActivity.class);
                        ep.putExtra("OLD_PHONE", phoneFormat);
                        startActivity(ep);

                    }
                } else {
                    if (authenticateType.equals("EMAIL")) {
                        startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditEmail"));
                    } else if (authenticateType.equals("ADDRESS")) {
                        startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditAddress"));
                    } else if (authenticateType.equals("PHONE")) {
                        startActivity(new Intent(Business_UserDetailsListenersActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("OLD_PHONE", phoneFormat)
                                .putExtra("screen", "EditPhone"));

                    }
                }
            }
            break;
        }
    }
}
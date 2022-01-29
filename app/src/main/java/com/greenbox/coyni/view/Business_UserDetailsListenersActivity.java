package com.greenbox.coyni.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

public class Business_UserDetailsListenersActivity extends AppCompatActivity {

    String authenticateType = "", phoneFormat = "";
    TextView heading, title, value;
    CardView changeCV;
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    DashboardViewModel dashboardViewModel;
    Long mLastClickTime = 0L;
    MyApplication myApplicationObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_user_details_listeners);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        heading = findViewById(R.id.intentName);
        title = findViewById(R.id.titleTV);
        value = findViewById(R.id.contentTV);
        changeCV = findViewById(R.id.changeCV);
        myApplicationObj = (MyApplication) getApplicationContext();
        initObservers();
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

        if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails") && getIntent().getStringExtra("title").equalsIgnoreCase("EMAIL")) {
            heading.setText("Email");
            title.setText("Current Email");
            authenticateType = getIntent().getStringExtra("title");
            value.setText(getIntent().getStringExtra("value"));
            isTouchId = Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock = Boolean.parseBoolean(getIntent().getStringExtra("face"));
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
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails") && getIntent().getStringExtra("title").equalsIgnoreCase("ADDRESS")) {
            heading.setText("Address");
            title.setText("Current Address");
            if (value.getText().toString().equals("")) {
                value.setText(getIntent().getStringExtra("value"));
            }
            authenticateType = getIntent().getStringExtra("title");
            isTouchId = Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock = Boolean.parseBoolean(getIntent().getStringExtra("face"));

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
//shiva
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails") && getIntent().getStringExtra("title").equalsIgnoreCase("PHONE")) {
            heading.setText("Phone Number");
            title.setText("Current Phone Number");
            value.setText(getIntent().getStringExtra("value"));
            phoneFormat = getIntent().getStringExtra("value");
            authenticateType = getIntent().getStringExtra("title");
            isTouchId = Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock = Boolean.parseBoolean(getIntent().getStringExtra("face"));

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

    private void initObservers() {
        dashboardViewModel.getProfileMutableLiveData().observe(Business_UserDetailsListenersActivity.this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                myApplicationObj.setMyProfile(profile);
                if (getIntent().getStringExtra("title").equalsIgnoreCase("ADDRESS")) {
                    String addressFormatted = "";
                    if (profile.getData().getAddressLine1() != null && !profile.getData().getAddressLine1().equals("")) {
                        addressFormatted = addressFormatted + profile.getData().getAddressLine1() + ", ";
                    }
                    if (profile.getData().getAddressLine2() != null && !profile.getData().getAddressLine2().equals("")) {
                        addressFormatted = addressFormatted + profile.getData().getAddressLine2() + ", ";
                    }
                    if (profile.getData().getCity() != null && !profile.getData().getCity().equals("")) {
                        addressFormatted = addressFormatted + profile.getData().getCity() + ", ";
                    }
                    if (profile.getData().getState() != null && !profile.getData().getState().equals("")) {
                        addressFormatted = addressFormatted + profile.getData().getState() + ", ";
                    }

                    if (profile.getData().getZipCode() != null && !profile.getData().getZipCode().equals("")) {
                        addressFormatted = addressFormatted + profile.getData().getZipCode() + ", ";
                    }

                    value.setText(addressFormatted.substring(0, addressFormatted.trim().length() - 1) + ".");
                }
                if (getIntent().getStringExtra("title").equalsIgnoreCase("EMAIL")){
                    value.setText(profile.getData().getEmail());
                }


//                    myApplicationObj.setMyProfile(profile);
//                    if (profile.getData().getImage() != null && !profile.getData().getImage().trim().equals("")) {
//                        userProfileIV.setVisibility(View.VISIBLE);
//                        imageTextTV.setVisibility(View.GONE);
//                        Glide.with(UserDetailsActivity.this)
//                                .load(profile.getData().getImage())
//                                .into(userProfileIV);
//                    } else {
//                        userProfileIV.setVisibility(View.GONE);
//                        imageTextTV.setVisibility(View.VISIBLE);
//                        String imageText ="";
//                        imageText = imageText+profile.getData().getFirstName().substring(0,1).toUpperCase()+
//                                profile.getData().getLastName().substring(0,1).toUpperCase();
//                        imageTextTV.setText(imageText);
//                    }

            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dashboardViewModel.meProfile();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
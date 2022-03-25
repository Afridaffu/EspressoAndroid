package com.greenbox.coyni.view.business;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DBAInfo.BusinessType;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessUserDetailsPreviewActivity;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.microblink.blinkcard.image.Image;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DBAInfoDetails extends BaseActivity {
    private TextView nameTV, emailTV, webSiteTV, phoneNumberTV, addressTV, businessType, dba_imageTextTV;
    private LinearLayout closeLL, webLL;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    DashboardViewModel dashboardViewModel;
    ImageView dba_userProfileIV, editProfileIV;
    private MyApplication objMyApplication;
    private List<BusinessType> responce;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    ProgressDialog dialog;
    Long mLastClickTime = 0L;
    private LinearLayout editEmail, editPhone;
    String emailID, phone_Number,bType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_dbainfo_details);
        try {
            initFields();
//            initData();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initFields() {
        try {
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            closeLL = findViewById(R.id.closeLL);
            nameTV = findViewById(R.id.nameTV);
            emailTV = findViewById(R.id.emailIDTV);
            webSiteTV = findViewById(R.id.websiteTV);
            webLL = findViewById(R.id.webIdLL);
            phoneNumberTV = findViewById(R.id.phoneNumberTV);
            addressTV = findViewById(R.id.addressTV);
            businessType = findViewById(R.id.businessTypeTV);
            dba_imageTextTV = findViewById(R.id.dba_imageTextTV);
            dba_userProfileIV = findViewById(R.id.dba_userProfileIV);
            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            responce = objMyApplication.getBusinessTypeResp().getData();
            editProfileIV = findViewById(R.id.dba_editProfileIV);
            editEmail = findViewById(R.id.editEmailDBALL);
            editPhone = findViewById(R.id.editPhoneNumDBALL);

            editProfileIV.setOnClickListener(view -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (checkAndRequestPermissions(this)) {
                        try {
                            showImagePickerDialog(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            editEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DBAInfoDetails.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "DBAInfo").putExtra("action", "EditEmailDBA").putExtra("value", emailID));
                }
            });

            editPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DBAInfoDetails.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "DBAInfo").putExtra("action", "EditPhoneDBA").putExtra("value", phone_Number));
                }
            });


            closeLL.setOnClickListener(v -> {
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            for (int i = 0; i < responce.size(); i++) {
                try {
                    if (bType.trim().equals(responce.get(i).getKey().toLowerCase().trim())) {
                        businessType.setText(responce.get(i).getValue());
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        try {
            DBAInfoResp dbaInfoResp = objMyApplication.getDbaInfoResp();
            if (dbaInfoResp.getStatus().equalsIgnoreCase("SUCCESS")) {
                if (dbaInfoResp.getData().getName() != null && dbaInfoResp.getData().getName().length() > 20) {
                    nameTV.setText(dbaInfoResp.getData().getName().substring(0, 20) + "...");
                } else {
                    nameTV.setText(dbaInfoResp.getData().getName());
                }
                nameTV.setOnClickListener(view -> {

                    try {
                        if (nameTV.getText().toString().contains("...") && dbaInfoResp.getData().getName().length() >= 21) {
                                nameTV.setText(objMyApplication.getDbaInfoResp().getData().getName());

                        } else if (dbaInfoResp.getData().getName().length() >= 21) {
                                nameTV.setText(dbaInfoResp.getData().getName().substring(0, 20) + "...");
                            } else {
                                nameTV.setText(dbaInfoResp.getData().getName());
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                if (dbaInfoResp.getData().getEmail() != null) {
                    emailTV.setText(dbaInfoResp.getData().getEmail());
                    try {
                        emailID = dbaInfoResp.getData().getEmail();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    emailTV.setText("");
                }
                if (dbaInfoResp.getData().getWebsite() != null && !dbaInfoResp.getData().getWebsite().equals("")) {
                    webLL.setVisibility(View.VISIBLE);
                    webSiteTV.setText("https://" + dbaInfoResp.getData().getWebsite());
                } else {
                    webLL.setVisibility(View.GONE);
                    webSiteTV.setText("");
                }
                if (dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber() != null && !dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().equals("") && dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().length() >= 10) {
                    String pnhNum = dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber();
                    String phone_number = "(" + pnhNum.substring(0, 3) + ") " + pnhNum.substring(3, 6) + "-" + pnhNum.substring(6, 10);
                    phoneNumberTV.setText(phone_number);
                    phone_Number = phone_number;
                } else {
                    phoneNumberTV.setText("");
                }

                String addressFormatted = "";
                if (dbaInfoResp.getData().getAddressLine1() != null && !dbaInfoResp.getData().getAddressLine1().equals("")) {
                    addressFormatted = addressFormatted + dbaInfoResp.getData().getAddressLine1() + ", ";
                }
                if (dbaInfoResp.getData().getAddressLine2() != null && !dbaInfoResp.getData().getAddressLine2().equals("")) {
                    addressFormatted = addressFormatted + dbaInfoResp.getData().getAddressLine2() + ", ";
                }
                if (dbaInfoResp.getData().getCity() != null && !dbaInfoResp.getData().getCity().equals("")) {
                    addressFormatted = addressFormatted + dbaInfoResp.getData().getCity() + ", ";
                }
                if (dbaInfoResp.getData().getState() != null && !dbaInfoResp.getData().getState().equals("")) {
                    addressFormatted = addressFormatted + dbaInfoResp.getData().getState() + ", ";
                }

                if (dbaInfoResp.getData().getZipCode() != null && !dbaInfoResp.getData().getZipCode().equals("")) {
                    addressFormatted = addressFormatted + dbaInfoResp.getData().getZipCode() + ", ";
                }

                if (addressFormatted.equals("")) {
                    addressFormatted = addressFormatted + "United States";
                    addressTV.setText(addressFormatted);
    //                        business_userAddreTV.setText(addressFormatted);
    //                        address=addressFormatted;
                } else {
                    addressTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
    //                        business_userAddreTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
    //                        address=addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".";
                }

                for (int i = 0; i < responce.size(); i++) {
                    try {
                        if (dbaInfoResp.getData().getBusinessType().toLowerCase().trim().equals(responce.get(i).getKey().toLowerCase().trim())) {
                            businessType.setText(responce.get(i).getValue());
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
    //            if (dbaInfoResp.getData().getBusinessType() != null) {
    //                businessType.setText(dbaInfoResp.getData().getBusinessType());
    //            }
    //            else {
    //                businessType.setText("");
    //            }


                try {
                    bindImage(objMyApplication.getMyProfile().getData().getImage(), dbaInfoResp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void showImagePickerDialog(Activity activity) {
        // custom dialog
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.image_picker_options_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = activity.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;


            LinearLayout chooseLL = dialog.findViewById(R.id.chooseLL);
            LinearLayout takePhotoLL = dialog.findViewById(R.id.takePhotoLL);

            chooseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(pickPhoto, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            takePhotoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(takePicture, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getGetDBAInfoResponse().observe(this, new Observer<DBAInfoResp>() {
                @Override
                public void onChanged(DBAInfoResp dbaInfoResp) {
                    dismissDialog();
                    if (dbaInfoResp.getStatus().equalsIgnoreCase("SUCCESS")) {

                        objMyApplication.setDbaInfoResp(dbaInfoResp);
                        if (dbaInfoResp.getData().getName() != null && dbaInfoResp.getData().getName().length() > 20) {
                            nameTV.setText(dbaInfoResp.getData().getName().substring(0, 20)+"...");
                        } else if (dbaInfoResp.getData().getName() != null){
                            nameTV.setText(dbaInfoResp.getData().getName());
                        }
                        if (dbaInfoResp.getData().getEmail() != null) {
                            emailTV.setText(dbaInfoResp.getData().getEmail());
                        } else {
                            emailTV.setText("");
                        }
                        if (dbaInfoResp.getData().getWebsite() != null) {
                            webSiteTV.setText("https://" + dbaInfoResp.getData().getWebsite());
                        } else {
                            webSiteTV.setText("");
                        }
//                        if (dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber() != null) {
//                            phoneNumberTV.setText("(" + dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().substring(0, 3) + ") " + dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().substring(3, 6) + "-" + dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().substring(6, 10));
//                        } else {
//                            phoneNumberTV.setText("");
//                        }

                        if (dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber() != null && !dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().equals("") && dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().length()>= 10) {
                            String pnhNum = dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber();
                            String phone_number = "(" + pnhNum.substring(0, 3) + ") " + pnhNum.substring(3, 6) + "-" + pnhNum.substring(6, 10);
                            phoneNumberTV.setText(phone_number);
                            phone_Number = phone_number;
                        } else {
                            phoneNumberTV.setText("");
                        }

                        String addressFormatted = "";
                        if (dbaInfoResp.getData().getAddressLine1() != null && !dbaInfoResp.getData().getAddressLine1().equals("")) {
                            addressFormatted = addressFormatted + dbaInfoResp.getData().getAddressLine1() + ", ";
                        }
                        if (dbaInfoResp.getData().getAddressLine2() != null && !dbaInfoResp.getData().getAddressLine2().equals("")) {
                            addressFormatted = addressFormatted + dbaInfoResp.getData().getAddressLine2() + ", ";
                        }
                        if (dbaInfoResp.getData().getCity() != null && !dbaInfoResp.getData().getCity().equals("")) {
                            addressFormatted = addressFormatted + dbaInfoResp.getData().getCity() + ", ";
                        }
                        if (dbaInfoResp.getData().getState() != null && !dbaInfoResp.getData().getState().equals("")) {
                            addressFormatted = addressFormatted + dbaInfoResp.getData().getState() + ", ";
                        }

                        if (dbaInfoResp.getData().getZipCode() != null && !dbaInfoResp.getData().getZipCode().equals("")) {
                            addressFormatted = addressFormatted + dbaInfoResp.getData().getZipCode() + ", ";
                        }

                        if (addressFormatted.equals("")) {
                            addressFormatted = addressFormatted + "United States";
                            addressTV.setText(addressFormatted);
                            //                        business_userAddreTV.setText(addressFormatted);
                            //                        address=addressFormatted;
                        } else {
                            addressTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
                            //                        business_userAddreTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
                            //                        address=addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".";
                        }


                        if (dbaInfoResp.getData().getBusinessType() != null && !dbaInfoResp.getData().getBusinessType().equals("")){
                            bType = dbaInfoResp.getData().getBusinessType();
                        }
                        for (int i = 0; i < responce.size(); i++) {
                            try {
                                if (dbaInfoResp.getData().getBusinessType().toLowerCase().trim().equals(responce.get(i).getKey().toLowerCase().trim())) {
                                    businessType.setText(responce.get(i).getValue());
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            bindImage(objMyApplication.getMyProfile().getData().getImage(), dbaInfoResp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Utils.displayAlert(dbaInfoResp.getError().getErrorDescription(), DBAInfoDetails.this, "", dbaInfoResp.getError().getFieldErrors().get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            });
            dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
                @Override
                public void onChanged(Profile profile) {
                    if (profile.getStatus().equalsIgnoreCase("SUCCESS")) {
                        try {
                            objMyApplication.setMyProfile(profile);
                            bindImage(profile.getData().getImage(), objMyApplication.getDbaInfoResp());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });


            dashboardViewModel.getImageResponseMutableLiveData().observe(this, new Observer<ImageResponse>() {
                @Override
                public void onChanged(ImageResponse imageResponse) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (imageResponse != null) {
                        if (imageResponse.getStatus().toLowerCase().equals("success")) {

                            try {
                                dba_userProfileIV.setVisibility(View.GONE);
                                dba_imageTextTV.setVisibility(View.VISIBLE);
                                String imageTextNew = "";
                                imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                                dba_imageTextTV.setText(imageTextNew);
                                dashboardViewModel.meProfile();
                                Utils.showCustomToast(DBAInfoDetails.this, imageResponse.getData().getMessage(), R.drawable.ic_custom_tick, "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Utils.displayAlert(imageResponse.getError().getErrorDescription(), DBAInfoDetails.this, "", imageResponse.getError().getFieldErrors().get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void bindImage(String imageString, DBAInfoResp dbaInfoResp) {
        try {
            dba_userProfileIV.setVisibility(View.GONE);
            dba_imageTextTV.setVisibility(View.VISIBLE);
//            business_userProfileIV.setVisibility(View.GONE);
//            business_imageTextTV.setVisibility(View.VISIBLE);
            String imageTextNew = "";
            imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            dba_imageTextTV.setText(imageTextNew);
//            business_imageTextTV.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                try {
                    dba_userProfileIV.setVisibility(View.VISIBLE);
                    dba_imageTextTV.setVisibility(View.GONE);
//                business_userProfileIV.setVisibility(View.VISIBLE);
//                business_imageTextTV.setVisibility(View.GONE);

                    Glide.with(this)
                            .load(imageString)
                            .placeholder(R.drawable.ic_profile_male_user)
                            .into(dba_userProfileIV);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                dba_userProfileIV.setVisibility(View.GONE);
                dba_imageTextTV.setVisibility(View.VISIBLE);

                String imageText = "";
                try {
                    imageText = imageText + dbaInfoResp.getData().getName().substring(0, 1).toUpperCase() +
                            dbaInfoResp.getData().getName().split(" ")[1].substring(0, 1).toUpperCase();
                    dba_imageTextTV.setText(imageText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            showProgressDialog();
            businessIdentityVerificationViewModel.getDBAInfo();
            dashboardViewModel.meProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        Uri uri = getImageUri(this, selectedImage);
                        CropImage.activity(uri).start(this);
                        //                        userProfileIV.setVisibility(View.VISIBLE);
                        //                        imageTextTV.setVisibility(View.GONE);
                        //                        userProfileIV.setImageBitmap(selectedImage);
                        //                        uploadImage();
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            Uri selectedImage = data.getData();
                            if (selectedImage != null) {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                //                                userProfileIV.setImageBitmap(bitmap);
                                //                                userProfileIV.setVisibility(View.VISIBLE);
                                //                                imageTextTV.setVisibility(View.GONE);
                                //                                uploadImage();
                                Uri uri = getImageUri(this, bitmap);
                                CropImage.activity(uri).start(this);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        dba_userProfileIV.setVisibility(View.VISIBLE);
                        dba_imageTextTV.setVisibility(View.GONE);
                        dba_userProfileIV.setImageURI(resultUri);
                        uploadImage();

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                    objMyApplication.getMyProfile().getData().getId() + "_profile" + System.currentTimeMillis(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.parse(path);
    }

    private void uploadImage() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) dba_userProfileIV.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            //Compress the image size
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
            byte[] imgInByte = stream1.toByteArray();
            //End

//            Uri tempUri = getImageUri(context, bitmap);
            Uri tempUri = getImageUri(DBAInfoDetails.this, scaled);

            File file = new File(getRealPathFromURI(tempUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            String extention = file.getName().substring(file.getName().lastIndexOf("."));

            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("image", userId + "_profile" + extention, requestFile);
            dialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.show();
            dashboardViewModel.updateProfile(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        try {
            if (getContentResolver() != null) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cursor.getString(idx);
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        try {
            int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.CAMERA);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(context, listPermissionsNeeded
                                .toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case REQUEST_ID_MULTIPLE_PERMISSIONS:

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Camera.", DBAInfoDetails.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", DBAInfoDetails.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", DBAInfoDetails.this, "", "");

                    } else {
//                        startActivity(new Intent(this, CameraActivity.class));
//                        chooseFilePopup(this, selectedDocType);
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(DBAInfoDetails.this);
                        showImagePickerDialog(this);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
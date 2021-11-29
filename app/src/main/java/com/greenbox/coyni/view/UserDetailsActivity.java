package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserDetailsActivity extends AppCompatActivity {

    ImageView editProfileIV, userProfileIV;
    TextView userAddressTV,userPhoneNumTV,userEmailIdTV,imageTextTV,userNameTV;
    MyApplication myApplicationObj;
    LinearLayout emailLL,phoneLL,addressLL,userDetailsCloseLL;
    public static UserDetailsActivity userDetailsActivity;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    String strFileName = "",phoneFormat = "",phoneNumber="";
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    boolean isProfile = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_user_details);
            initFields();
            getStates();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields(){
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            myApplicationObj = (MyApplication) getApplicationContext();
            userDetailsActivity = this;
            editProfileIV = findViewById(R.id.editProfileIV);
            userProfileIV = findViewById(R.id.userProfileIV);
            userAddressTV = findViewById(R.id.userAddressTV);
            userPhoneNumTV = findViewById(R.id.userPhoneNumTV);
            userEmailIdTV = findViewById(R.id.userEmailIdTV);
            imageTextTV = findViewById(R.id.imageTextTV);
            emailLL = findViewById(R.id.emailLL);
            userDetailsCloseLL = findViewById(R.id.userDetailsCloseLL);
            phoneLL = findViewById(R.id.phoneLL);
            addressLL = findViewById(R.id.addressLL);
            userNameTV = findViewById(R.id.userNameTV);

            editProfileIV.setOnClickListener(view -> {
                if (checkAndRequestPermissions(this)) {
                    chooseImage(this);
                }
            });

            emailLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                            .putExtra("TYPE","ENTER")
                            .putExtra("screen","EditEmail"));
                }
            });

            addressLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                            .putExtra("TYPE","ENTER")
                            .putExtra("screen","EditAddress"));
                }
            });

            phoneLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                            .putExtra("TYPE","ENTER")
                            .putExtra("OLD_PHONE",phoneFormat)
                            .putExtra("screen","EditPhone"));
                }
            });

            userDetailsCloseLL.setOnClickListener(view -> {
                finish();
            });

            if(myApplicationObj.getMyProfile().getData().getFirstName()!=null){
                Profile profile = myApplicationObj.getMyProfile();
                phoneNumber  = profile.getData().getPhoneNumber().split(" ")[1];
                phoneFormat = "("+phoneNumber.substring(0, 3)+") "+phoneNumber.substring(3, 6)+"-"+phoneNumber.substring(6, 10);
                bindImage(myApplicationObj.getMyProfile().getData().getImage());
                strFileName = myApplicationObj.getMyProfile().getData().getImage();
                userEmailIdTV.setText(profile.getData().getEmail());
                userNameTV.setText(profile.getData().getFirstName()+" "+profile.getData().getLastName());

                userPhoneNumTV.setText(phoneFormat);

                String addressFormatted = "";
                if(profile.getData().getAddressLine1()!=null && !profile.getData().getAddressLine1().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getAddressLine1()+", ";
                }
                if(profile.getData().getAddressLine2()!=null && !profile.getData().getAddressLine2().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getAddressLine2()+", ";
                }
                if(profile.getData().getCity()!=null && !profile.getData().getCity().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getCity()+", ";
                }
                if(profile.getData().getState()!=null && !profile.getData().getState().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getState()+", ";
                }

                addressFormatted = addressFormatted + "United States, ";

                if(profile.getData().getZipCode()!=null && !profile.getData().getZipCode().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getZipCode()+", ";
                }

                if(addressFormatted.trim().endsWith(",")){
                    userAddressTV.setText(addressFormatted.trim().substring(0,addressFormatted.trim().length()-1));
                }else{
                    userAddressTV.setText(addressFormatted);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers(){

        dashboardViewModel.getImageResponseMutableLiveData().observe(this, new Observer<ImageResponse>() {
            @Override
            public void onChanged(ImageResponse imageResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (imageResponse != null) {
                    if(imageResponse.getStatus().toLowerCase().equals("success")){
                        userProfileIV.setVisibility(View.GONE);
                        imageTextTV.setVisibility(View.VISIBLE);
                        String imageTextNew ="";
                        imageTextNew = imageTextNew+myApplicationObj.getMyProfile().getData().getFirstName().substring(0,1).toUpperCase()+
                                myApplicationObj.getMyProfile().getData().getLastName().substring(0,1).toUpperCase();
                        imageTextTV.setText(imageTextNew);
                        dashboardViewModel.meProfile();
                        Utils.displayAlert(imageResponse.getData().getMessage(),UserDetailsActivity.this,"");
                    }else{
                        Utils.displayAlert(imageResponse.getError().getErrorDescription(),UserDetailsActivity.this,"");
                    }

                }
            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError imageResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (imageResponse != null) {
                        Utils.displayAlert(imageResponse.getError().getErrorDescription(),UserDetailsActivity.this,"");
                }
            }
        });

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile != null) {
                    myApplicationObj.setMyProfile(profile);
                    if(myApplicationObj.getMyProfile().getData().getFirstName()!=null){
                        phoneNumber  = profile.getData().getPhoneNumber().split(" ")[1];
                        phoneFormat = "("+phoneNumber.substring(0, 3)+") "+phoneNumber.substring(3, 6)+"-"+phoneNumber.substring(6, 10);
                        bindImage(myApplicationObj.getMyProfile().getData().getImage());
                        strFileName = myApplicationObj.getMyProfile().getData().getImage();
                        userEmailIdTV.setText(profile.getData().getEmail());
                        userNameTV.setText(profile.getData().getFirstName()+" "+profile.getData().getLastName());

                        userPhoneNumTV.setText(phoneFormat);

                        String addressFormatted = "";
                        if(profile.getData().getAddressLine1()!=null && !profile.getData().getAddressLine1().equals("")){
                            addressFormatted = addressFormatted + profile.getData().getAddressLine1()+", ";
                        }
                        if(profile.getData().getAddressLine2()!=null && !profile.getData().getAddressLine2().equals("")){
                            addressFormatted = addressFormatted + profile.getData().getAddressLine2()+", ";
                        }
                        if(profile.getData().getCity()!=null && !profile.getData().getCity().equals("")){
                            addressFormatted = addressFormatted + profile.getData().getCity()+", ";
                        }
                        if(profile.getData().getState()!=null && !profile.getData().getState().equals("")){
                            addressFormatted = addressFormatted + profile.getData().getState()+", ";
                        }

                        addressFormatted = addressFormatted + "United States, ";

                        if(profile.getData().getZipCode()!=null && !profile.getData().getZipCode().equals("")){
                            addressFormatted = addressFormatted + profile.getData().getZipCode()+", ";
                        }

                        if(addressFormatted.trim().endsWith(",")){
                            userAddressTV.setText(addressFormatted.trim().substring(0,addressFormatted.trim().length()-1));
                        }else{
                            userAddressTV.setText(addressFormatted);
                        }

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
            }
        });

    }

    private void bindImage(String imageString) {
        try {
            userProfileIV.setVisibility(View.GONE);
            imageTextTV.setVisibility(View.VISIBLE);
            String imageTextNew ="";
            imageTextNew = imageTextNew+myApplicationObj.getMyProfile().getData().getFirstName().substring(0,1).toUpperCase()+
                    myApplicationObj.getMyProfile().getData().getLastName().substring(0,1).toUpperCase();
            imageTextTV.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                userProfileIV.setVisibility(View.VISIBLE);
                imageTextTV.setVisibility(View.GONE);
                Glide.with(this)
                        .load(imageString)
                        .into(userProfileIV);
            } else {
                userProfileIV.setVisibility(View.GONE);
                imageTextTV.setVisibility(View.VISIBLE);
                String imageText ="";
                imageText = imageText+myApplicationObj.getMyProfile().getData().getFirstName().substring(0,1).toUpperCase()+
                        myApplicationObj.getMyProfile().getData().getLastName().substring(0,1).toUpperCase();
                imageTextTV.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getStates() {
        String json = null;
        try {
            InputStream is = getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(json, type);
            myApplicationObj.setListStates(listStates);
            Log.e("list states", listStates.size()+"");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    private void chooseImage(Context context) {
        try {
            final CharSequence[] optionsMenu = {"Take Photo", "Choose Photo", "Delete Photo", "Cancel"}; // create a menuOption Array
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (optionsMenu[i].equals("Take Photo")) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    } else if (optionsMenu[i].equals("Choose Photo")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);

                    } else if (optionsMenu[i].equals("Delete Photo")) {
                        if (strFileName != null && !strFileName.trim().equals("")) {
                            Context context = new ContextThemeWrapper(UserDetailsActivity.this, R.style.Theme_Coyni);
                            new MaterialAlertDialogBuilder(context)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Do you want to delete the Profile image?")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        removeImage();
                                    })
                                    .setNegativeButton("Cancel", (dialog1, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        } else {
                            Utils.displayAlert("No Profile image found to remove", UserDetailsActivity.this,"");
                        }
                    } else if (optionsMenu[i].equals("Cancel")) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.setTitle("Add Profile Picture");
            builder.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void uploadImage() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) userProfileIV.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            //Compress the image size
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
            byte[] imgInByte = stream1.toByteArray();

            //End

//            Uri tempUri = getImageUri(context, bitmap);
            Uri tempUri = getImageUri(this, scaled);

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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                myApplicationObj.getMyProfile().getData().getId() + "_profile", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void removeImage() {
        try {
            String filename = "";
            int length = 0;
            if (strFileName != null && !strFileName.trim().equals("")) {
                length = strFileName.split("/").length;
                filename = strFileName.split("/")[length - 1];
                dialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                Log.e("filename",""+filename);
                dashboardViewModel.removeImage(filename);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Camera.", UserDetailsActivity.this,"");

                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Your Storage.",UserDetailsActivity.this,"");

                } else {
                    chooseImage(this);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        userProfileIV.setVisibility(View.VISIBLE);
                        imageTextTV.setVisibility(View.GONE);
                        userProfileIV.setImageBitmap(selectedImage);
                        uploadImage();
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            Uri selectedImage = data.getData();
                            if (selectedImage != null) {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                userProfileIV.setImageBitmap(bitmap);
                                userProfileIV.setVisibility(View.VISIBLE);
                                imageTextTV.setVisibility(View.GONE);
                                uploadImage();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dashboardViewModel.meProfile();
    }
}
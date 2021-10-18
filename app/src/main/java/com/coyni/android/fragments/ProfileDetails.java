package com.coyni.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.model.GlideApp;
import com.coyni.android.model.user.ImageRequest;
import com.coyni.android.model.user.ImageResponse;
import com.coyni.android.view.MainActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.coyni.android.model.user.User;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileDetails extends Fragment {
    View view;
    static Context context;
    ImageView imgCamera, imgBack;
    TextView tvUserName, tvUserMail, tvUserPhone, tvAvailableAddress, addOrupdateAddress, tvAddressWithoutEdit, tvAddressEdit;
    MyApplication objMyApplication;
    TextInputEditText etAddress1, etAddress2, etCity, etZipCode, etStateRegion;
    DashboardViewModel dashboardViewModel;
    ProgressDialog dialog;
    CardView cvAddAddress;
    CircularImageView imgProfile;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    int userId = 0;
    String strFileName = "";
    Boolean isProfile = false;

    public ProfileDetails() {
    }

    public static ProfileDetails newInstance(Context cxt) {
        ProfileDetails fragment = new ProfileDetails();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Camera.", getActivity());

                } else if (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Your Storage.", getActivity());

                } else {
                    chooseImage(context);
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
                        imgProfile.setImageBitmap(selectedImage);
                        uploadImage();
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            Uri selectedImage = data.getData();
                            if (selectedImage != null) {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                                imgProfile.setImageBitmap(bitmap);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_details, container, false);
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) context.getApplicationContext();
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserMail = view.findViewById(R.id.tvUserMail);
            tvUserPhone = view.findViewById(R.id.tvUserPhone);
            tvAvailableAddress = view.findViewById(R.id.tvAvailableAddress);
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            cvAddAddress = (CardView) view.findViewById(R.id.cvAddAddress);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            imgCamera = (ImageView) view.findViewById(R.id.imgCamera);
            imgProfile = (CircularImageView) view.findViewById(R.id.imgProfile);
            etAddress1 = (TextInputEditText) view.findViewById(R.id.etAddress1);
            etAddress2 = (TextInputEditText) view.findViewById(R.id.etAddress2);
            etCity = (TextInputEditText) view.findViewById(R.id.etCity);
            etZipCode = (TextInputEditText) view.findViewById(R.id.etZipCode);
            etStateRegion = (TextInputEditText) view.findViewById(R.id.etStateRegion);
            addOrupdateAddress = (TextView) view.findViewById(R.id.addOrupdateAddress);
            tvAddressEdit = (TextView) view.findViewById(R.id.tvAddressEdit);
            tvAddressWithoutEdit = (TextView) view.findViewById(R.id.tvAddressWithoutEdit);
//            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
//            dialog.setIndeterminate(false);
//            dialog.setMessage("Please wait...");
//            dialog.getWindow().setGravity(Gravity.CENTER);
//            dialog.show();
            dashboardViewModel.meProfile();
            updateAddressAfterAdding();
            initObservables();

            // Checking address availability from UserTracker
            if (objMyApplication.getUserTracker() != null && objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                cvAddAddress.setVisibility(View.GONE);
                tvAddressWithoutEdit.setVisibility(View.GONE);
                tvAddressEdit.setVisibility(View.VISIBLE);
            } else {
                cvAddAddress.setVisibility(View.VISIBLE);
                tvAvailableAddress.setVisibility(View.GONE);
                tvAddressWithoutEdit.setVisibility(View.VISIBLE);
                tvAddressEdit.setVisibility(View.GONE);
            }

            tvUserName.setText(Utils.capitalize(objMyApplication.getStrUser().toString()));
            tvUserMail.setText(objMyApplication.getStrEmail());
            tvUserPhone.setText("+" + objMyApplication.getStrPhoneNum().replace(" ", " - "));
            bindImage(objMyApplication.getStrProfileImg());
            strFileName = objMyApplication.getStrProfileImg();

            imgCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions(getActivity())) {
                        chooseImage(context);
                    }

                }
            });

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).loadProfile();
                }
            });
            cvAddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAddressFragment userAddressFragment = UserAddressFragment.newInstance(getActivity());
                    try {
                        openFragment(userAddressFragment, "userAddress");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            tvAddressEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAddressFragment userAddressFragment = UserAddressFragment.newInstance(getActivity());
                    try {
                        openFragment(userAddressFragment, "userAddress");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    private void updateAddressAfterAdding() {
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                try {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
                    if (user != null) {
                        if (user.getData().getAddressLine2() == null || user.getData().getAddressLine2().trim().equals("")) {
                            tvAvailableAddress.setText(user.getData().getAddressLine1() + ", " + user.getData().getCity() + ", "
                                    + user.getData().getState() + ", " + "United States" + ", " + user.getData().getZipCode());
                        } else {
                            tvAvailableAddress.setText(user.getData().getAddressLine1() + ", " + user.getData().getAddressLine2() + ", "
                                    + user.getData().getCity() + ", " + user.getData().getState()
                                    + ", " + "United States" + ", " + user.getData().getZipCode());
                        }
                        objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                        objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                        objMyApplication.setStrState(user.getData().getState());
                        objMyApplication.setStrCity(user.getData().getCity());
                        objMyApplication.setStrZipCode(user.getData().getZipCode());
                        objMyApplication.setIntUserId(user.getData().getId());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initObservables() {
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                try {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
                    if (user != null) {
                        objMyApplication.setStrUser(Utils.capitalize(user.getData().getFirstName() + " " + user.getData().getLastName()));
                        objMyApplication.setStrUserCode(user.getData().getFirstName().substring(0, 1).toUpperCase() + user.getData().getLastName().substring(0, 1).toUpperCase());
                        userId = user.getData().getId();
                        objMyApplication.setStrEmail(user.getData().getEmail().trim());
                        objMyApplication.setStrPhoneNum(user.getData().getPhoneNumber());
                        objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                        objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                        objMyApplication.setStrState(user.getData().getState());
                        objMyApplication.setStrCity(user.getData().getCity());
                        objMyApplication.setStrCountry(user.getData().getCountry());
                        objMyApplication.setStrZipCode(user.getData().getZipCode());
                        objMyApplication.setIntUserId(user.getData().getId());
                        objMyApplication.setStrProfileImg(user.getData().getImage());
                        if (!isProfile) {
                            bindImage(user.getData().getImage());
                        }
                        strFileName = user.getData().getImage();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dashboardViewModel.getImageResponseMutableLiveData().observe(getActivity(), new Observer<ImageResponse>() {
            @Override
            public void onChanged(ImageResponse imageResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (imageResponse != null) {
                    isProfile = true;
                    if (imageResponse.getData().toLowerCase().contains("delete")) {
                        imgProfile.setImageResource(R.drawable.ic_profile_male_user);
                    }
                    Context context = new ContextThemeWrapper(getActivity(), R.style.Theme_QuickCard);
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.app_name)
                            .setMessage(imageResponse.getData())
                            .setPositiveButton("OK", (dilog, which) -> {
                                dilog.dismiss();
                                dashboardViewModel.meProfile();
                            }).show();
                }
            }
        });
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                            Context context = new ContextThemeWrapper(getActivity(), R.style.Theme_QuickCard);
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
                            Utils.displayAlert("No Profile image found to remove", getActivity());
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

    private void uploadImage() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) imgProfile.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            //Compress the image size
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
            byte[] imgInByte = stream1.toByteArray();

            //End

//            Uri tempUri = getImageUri(context, bitmap);
            Uri tempUri = getImageUri(context, scaled);

            File file = new File(getRealPathFromURI(tempUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            String extention = file.getName().substring(file.getName().lastIndexOf("."));

            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("image", userId + "_profile" + extention, requestFile);
            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.show();
            dashboardViewModel.updateProfile(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindImage(String imageString) {
        try {
            if (imageString != null && !imageString.trim().equals("")) {
                GlideApp.with(context)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.ic_profile_male_user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, userId + "_profile", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
                dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                dashboardViewModel.removeImage(filename);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


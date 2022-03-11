package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;

import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.dialogs.BaseDialog;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserDetailsActivity extends AppCompatActivity implements OnKeyboardVisibilityListener, BusinessProfileRecyclerAdapter.OnSelectListner {

    ImageView editProfileIV, userProfileIV, mIvUserIcon;
    private TextView userAddressTV, userPhoneNumTV, userEmailIdTV, imageTextTV, userNameTV, defualtAccountDialogPersonalNameTV,
            mTvUserIconText;
    private TextInputLayout business_defaultAccTIL;
    private TextInputEditText business_defaultaccountET;
    private MyApplication myApplicationObj;
    private ExpandableListView brandsGV;

    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();

    LinearLayout emailLL, phoneLL, addressLL, userDetailsCloseLL, businessPersonalProfileAccount;
    @SuppressLint("StaticFieldLeak")
    public static UserDetailsActivity userDetailsActivity;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    String strFileName = "", phoneFormat = "", phoneNumber = "";
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    boolean isProfile = false;
    Long mLastClickTime = 0L;
    private String personalAccountExist;

    String emailId = "", address = "", phoneNo = "";

    //Business
    ImageView business_userProfileIV;
    TextView business_imageTextTV, business_userNameTV, business_emailIdTV, business_userPhneNoTV, business_userAddreTV;
    LinearLayout business_emailLL, business_PhoneNumLL, business_AddreLL;

    static SQLiteDatabase mydatabase;
    static Cursor dsPermanentToken, dsFacePin, dsTouchID;
    static String strToken = "";
    static String strDeviceID = "";
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;
    String authenticateType = "";
    private LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();
    private CustomerProfileViewModel customerProfileViewModel;
    private String accountTypeId = "";
    private int childid;
    private String SelectedDBAName;
    private BusinessProfileRecyclerAdapter listAdapter;
    private ImageView businessPersonalProfileTickIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_user_details);

            initFields();
            initObservers();
            dashboardViewModel.mePreferences();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            setKeyboardVisibilityListener(UserDetailsActivity.this);

            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

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

            //Business ID's
            business_userProfileIV = findViewById(R.id.b_userProfileIV);
            business_imageTextTV = findViewById(R.id.b_imageTextTV);
            business_userAddreTV = findViewById(R.id.b_userAddressTV);
            business_userNameTV = findViewById(R.id.b_userNameTV);
            business_emailIdTV = findViewById(R.id.b_userEmailIdTV);
            business_userPhneNoTV = findViewById(R.id.b_userPhoneNumTV);
            business_emailLL = findViewById(R.id.b_emailLL);
            business_PhoneNumLL = findViewById(R.id.b_phoneLL);
            business_AddreLL = findViewById(R.id.b_addressLL);
            business_defaultAccTIL = findViewById(R.id.b_accountTIL);
            business_defaultaccountET = findViewById(R.id.b_accountET);

            business_defaultAccTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//            isBiometric = Utils.checkBiometric(UserDetailsActivity.this);
            isBiometric = Utils.getIsBiometric();

            SetToken(myApplicationObj, this);
            SetFaceLock(myApplicationObj, this);
            SetTouchId(myApplicationObj, this);

            editProfileIV.setOnClickListener(view -> {
                if (checkAndRequestPermissions(this)) {
//                    chooseImage(this);
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showImagePickerDialog(this);
                }
            });

            emailLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
//                    startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
//                            .putExtra("TYPE", "ENTER")
//                            .putExtra("screen", "EditEmail"));
                    authenticateType = "EMAIL";

                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(UserDetailsActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(UserDetailsActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(UserDetailsActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "EditEmail"));
                        }
                    } else {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditEmail"));
                    }
                }
            });

            addressLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
//                    startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
//                            .putExtra("TYPE", "ENTER")
//                            .putExtra("screen", "EditAddress"));

                    authenticateType = "ADDRESS";

                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(UserDetailsActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(UserDetailsActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(UserDetailsActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "EditAddress"));
                        }
                    } else {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditAddress"));
                    }

                }
            });

            phoneLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
//                    startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
//                            .putExtra("TYPE", "ENTER")
//                            .putExtra("OLD_PHONE", phoneFormat)
//                            .putExtra("screen", "EditPhone"));

                    authenticateType = "PHONE";

                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(UserDetailsActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(UserDetailsActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(UserDetailsActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("OLD_PHONE", phoneFormat)
                                    .putExtra("screen", "EditPhone"));
                        }
                    } else {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("OLD_PHONE", phoneFormat)
                                .putExtra("screen", "EditPhone"));
                    }

                }
            });
            //Business setOnClick's
            business_emailLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                authenticateType = "EMAIL";
                String face = String.valueOf(isFaceLock);
                String touch = String.valueOf(isTouchId);

                startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", emailId).putExtra("touch", touch).putExtra("face", face));
            });

            business_AddreLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                authenticateType = "ADDRESS";
                String face = String.valueOf(isFaceLock);
                String touch = String.valueOf(isTouchId);

                startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", address).putExtra("touch", touch).putExtra("face", face));


            });

            business_PhoneNumLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                authenticateType = "PHONE";
                String face = String.valueOf(isFaceLock);
                String touch = String.valueOf(isTouchId);

                startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", phoneFormat).putExtra("touch", touch).putExtra("face", face));

            });

            userDetailsCloseLL.setOnClickListener(view -> finish());


            if (myApplicationObj.getMyProfile().getData().getFirstName() != null) {

                Profile profile = myApplicationObj.getMyProfile();

                phoneNumber = profile.getData().getPhoneNumber().split(" ")[1];
                phoneFormat = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
                bindImage(myApplicationObj.getMyProfile().getData().getImage());
                strFileName = myApplicationObj.getMyProfile().getData().getImage();
                userEmailIdTV.setText(profile.getData().getEmail());
                business_emailIdTV.setText(profile.getData().getEmail());
                userNameTV.setText(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                business_userNameTV.setText(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                userPhoneNumTV.setText(phoneFormat);
                business_userPhneNoTV.setText(phoneFormat);

                LogUtils.d("TAG","profiledata"+profile);

//                String fullname = Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
//
//                if (fullname.length() > 30) {
//                    business_defaultaccountET.setText(fullname.substring(0, 30).trim() + "...");
//                } else {
//                    business_defaultaccountET.setText(fullname);
//                }

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

                if (addressFormatted.equals("")) {
                    addressFormatted = addressFormatted + "United States";
                    userAddressTV.setText(addressFormatted);
                    business_userAddreTV.setText(addressFormatted);
                } else {
                    userAddressTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
                    business_userAddreTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
                }


            }

            if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                findViewById(R.id.businessUserDetailsLL).setVisibility(View.VISIBLE);
                findViewById(R.id.personalUserDetailsCV).setVisibility(View.GONE);
            } else if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                findViewById(R.id.businessUserDetailsLL).setVisibility(View.GONE);
                findViewById(R.id.personalUserDetailsCV).setVisibility(View.VISIBLE);
            }


            business_defaultaccountET.setOnClickListener(view -> {

                try {
                    final Dialog dialog = new Dialog(UserDetailsActivity.this);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.default_account_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    DisplayMetrics mertics = getApplicationContext().getResources().getDisplayMetrics();
//                    int width = mertics.widthPixels;

                    CardView doneButton = dialog.findViewById(R.id.default_DoneBtn);
                    brandsGV = dialog.findViewById(R.id.business_profile_accounts_expandable_list);
                    mIvUserIcon = dialog.findViewById(R.id.profile_img);
                    mTvUserIconText = dialog.findViewById(R.id.b_imageTextTV);
                    businessPersonalProfileAccount = dialog.findViewById(R.id.profileLL);
                    businessPersonalProfileTickIcon = dialog.findViewById(R.id.tickIcon);
                    defualtAccountDialogPersonalNameTV = dialog.findViewById(R.id.defualt_account_dialog_personal_name);

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (mertics.heightPixels * 0.75));

                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);

                    if(personalAccountList.get(0).isSelected()){
                        businessPersonalProfileTickIcon.setVisibility(View.VISIBLE);
                    } else {
                        businessPersonalProfileTickIcon.setVisibility(View.GONE);

                    }

                    brandsGV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                            businessPersonalProfileTickIcon.setVisibility(View.GONE);
                            BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
                            LogUtils.d("TAG", "GroupChildClick" + i + "....." + i1 + "....." + l);
                            childid = subSet.get(i).getSubsetName().get(i1).getId();
                            SelectedDBAName = subSet.get(i).getSubsetName().get(i1).getName();
                            for(int k=0;k<subSet.size();k++) {
                                for (int j = 0; j < subSet.get(k).getSubsetName().size(); j++) {
                                    if (subSet.get(k).getSubsetName().get(j).getId() == childid) {
                                        subSet.get(k).getSubsetName().get(j).setIsSelected(true);
                                    } else {
                                        subSet.get(k).getSubsetName().get(j).setIsSelected(false);
                                    }
                                }
                            }
                            LogUtils.d("TAG", "subSetChildClick" + subSet);
                            listAdapter.notifyDataSetChanged();
                            return true;
                        }
                    });

                    if (businessAccountList.size() != 0) {
                        brandsGV.setVisibility(View.VISIBLE);
                        LogUtils.d("TAG", "subSet" + subSet);

                        listAdapter = new BusinessProfileRecyclerAdapter(UserDetailsActivity.this, subSet, UserDetailsActivity.this);
                        brandsGV.setAdapter(listAdapter);
                    } else {
                        brandsGV.setVisibility(View.GONE);
                    }

                    if (personalAccountList.size() != 0) {
                        businessPersonalProfileAccount.setVisibility(View.VISIBLE);
                        personalAccountExist = "true";
                        String iconText = "";
                        if (personalAccountList.get(0).getCompanyName() != null
                        ) {
                            String firstName = personalAccountList.get(0).getCompanyName();
                            iconText = firstName.substring(0, 1).toUpperCase();
                            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();

                        }
                        if (personalAccountList.get(0).getImage() != null) {
                            mTvUserIconText.setVisibility(View.GONE);
                            mIvUserIcon.setVisibility(View.VISIBLE);
                            Glide.with(UserDetailsActivity.this)
                                    .load(personalAccountList.get(0).getImage())
                                    .placeholder(R.drawable.ic_profile_male_user)
                                    .into(mIvUserIcon);
                        } else {
                            mTvUserIconText.setVisibility(View.VISIBLE);
                            mIvUserIcon.setVisibility(View.GONE);
                            mTvUserIconText.setText(iconText);
                        }
                        defualtAccountDialogPersonalNameTV.setText(personalAccountList.get(0).getFullName());
                    } else {
                        personalAccountExist = "false";
                        businessPersonalProfileAccount.setVisibility(View.GONE);
                    }

                    businessPersonalProfileAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
                            businessPersonalProfileTickIcon.setVisibility(View.VISIBLE);
                            childid = personalAccountList.get(0).getId();
                            SelectedDBAName = personalAccountList.get(0).getFullName();
                            for(int k=0;k<subSet.size();k++) {
                                for (int j = 0; j < subSet.get(k).getSubsetName().size(); j++) {
                                    if (subSet.get(k).getSubsetName().get(j).getId() == childid) {
                                        subSet.get(k).getSubsetName().get(j).setIsSelected(true);
                                    } else {
                                        subSet.get(k).getSubsetName().get(j).setIsSelected(false);
                                    }
                                }
                            }
                            LogUtils.d("TAG", "subSetChildClick" + subSet);
                            listAdapter.notifyDataSetChanged();

                        }
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();


                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            LogUtils.d("profilesss", "childid" + childid);
                            UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
                            userPreferenceModel.setLocalCurrency(0);
                            userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
                            userPreferenceModel.setPreferredAccount(childid);
                            customerProfileViewModel.updatePreferences(userPreferenceModel);
                            dialog.dismiss();

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectedItem(int id) {
        LogUtils.d("profilesss", "profiles" + id);
        UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
        userPreferenceModel.setLocalCurrency(0);
        userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
        userPreferenceModel.setPreferredAccount(id);
        customerProfileViewModel.updatePreferences(userPreferenceModel);

    }

    public void initObservers() {

        dashboardViewModel.getImageResponseMutableLiveData().observe(this, new Observer<ImageResponse>() {
            @Override
            public void onChanged(ImageResponse imageResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (imageResponse != null) {
                    if (imageResponse.getStatus().toLowerCase().equals("success")) {
                        userProfileIV.setVisibility(View.GONE);
                        imageTextTV.setVisibility(View.VISIBLE);
                        String imageTextNew = "";
                        imageTextNew = imageTextNew + myApplicationObj.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                                myApplicationObj.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                        imageTextTV.setText(imageTextNew);
                        dashboardViewModel.meProfile();
                        Utils.showCustomToast(UserDetailsActivity.this, imageResponse.getData().getMessage(), R.drawable.ic_custom_tick, "");
                    } else {
                        Utils.displayAlert(imageResponse.getError().getErrorDescription(), UserDetailsActivity.this, "", imageResponse.getError().getFieldErrors().get(0));
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
                    Utils.displayAlert(imageResponse.getError().getErrorDescription(), UserDetailsActivity.this, "", imageResponse.getError().getFieldErrors().get(0));
                }
            }
        });


        dashboardViewModel.getPreferenceMutableLiveData().observe(this, new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences preferences) {

                try {
                    if (preferences != null) {
                        myApplicationObj.setTimezoneID(preferences.getData().getTimeZone());
                        if (preferences.getData().getTimeZone() == 0) {
                            myApplicationObj.setTempTimezone(getString(R.string.PST));
                            myApplicationObj.setTempTimezoneID(0);
                            myApplicationObj.setStrPreference("PST");
                        } else if (preferences.getData().getTimeZone() == 1) {
                            myApplicationObj.setTempTimezone(getString(R.string.MST));
                            myApplicationObj.setTempTimezoneID(1);
                            myApplicationObj.setStrPreference("America/Denver");
                        } else if (preferences.getData().getTimeZone() == 2) {
                            myApplicationObj.setTempTimezone(getString(R.string.CST));
                            myApplicationObj.setTempTimezoneID(2);
                            myApplicationObj.setStrPreference("CST");
                        } else if (preferences.getData().getTimeZone() == 3) {
                            myApplicationObj.setTempTimezone(getString(R.string.EST));
                            myApplicationObj.setTempTimezoneID(3);
                            myApplicationObj.setStrPreference("America/New_York");
                        } else if (preferences.getData().getTimeZone() == 4) {
                            myApplicationObj.setTempTimezone(getString(R.string.HST));
                            myApplicationObj.setTempTimezoneID(4);
                            myApplicationObj.setStrPreference("HST");
                        } else if (preferences.getData().getTimeZone() == 5) {
                            myApplicationObj.setTempTimezone(getString(R.string.AST));
                            myApplicationObj.setTempTimezoneID(5);
                            myApplicationObj.setStrPreference("AST");
                        }
                    }
                    accountTypeId = preferences.getData().getPreferredAccount();
                    dashboardViewModel.getProfiles();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile != null) {
                    myApplicationObj.setMyProfile(profile);
                    if (myApplicationObj.getMyProfile().getData().getFirstName() != null) {
                        phoneNumber = profile.getData().getPhoneNumber().split(" ")[1];
                        phoneFormat = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
                        bindImage(myApplicationObj.getMyProfile().getData().getImage());
                        strFileName = myApplicationObj.getMyProfile().getData().getImage();
                        userEmailIdTV.setText(profile.getData().getEmail());
                        business_emailIdTV.setText(profile.getData().getEmail());
                        emailId = profile.getData().getEmail();
                        userNameTV.setText(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                        business_userNameTV.setText(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                        userPhoneNumTV.setText(phoneFormat);
                        business_userPhneNoTV.setText(phoneFormat);
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

                        if (addressFormatted.equals("")) {
                            addressFormatted = addressFormatted + "United States";
                            userAddressTV.setText(addressFormatted);
                            business_userAddreTV.setText(addressFormatted);
                            address = addressFormatted;
                        } else {
                            userAddressTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
                            business_userAddreTV.setText(addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".");
                            address = addressFormatted.trim().substring(0, addressFormatted.trim().length() - 1) + ".";
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

        customerProfileViewModel.getUserPreferenceMutableLiveData().observe(this, new Observer<UserPreference>() {
            @Override
            public void onChanged(UserPreference userPreference) {
                if (userPreference != null) {
                    if (!userPreference.getStatus().toLowerCase().equals("success")) {
                        Utils.displayAlert(userPreference.getError().getErrorDescription(), UserDetailsActivity.this, "", userPreference.getError().getFieldErrors().get(0));
                    } else {
                        business_defaultaccountET.setText(SelectedDBAName);
                        myApplicationObj.setTimezoneID(myApplicationObj.getTempTimezoneID());
                        myApplicationObj.setTimezone(myApplicationObj.getTempTimezone());
                        if (myApplicationObj.getTempTimezoneID() == 0) {
                            myApplicationObj.setStrPreference("PST");
                        } else if (myApplicationObj.getTempTimezoneID() == 1) {
                            myApplicationObj.setStrPreference("America/Denver");
                        } else if (myApplicationObj.getTempTimezoneID() == 2) {
                            myApplicationObj.setStrPreference("CST");
                        } else if (myApplicationObj.getTempTimezoneID() == 3) {
                            myApplicationObj.setStrPreference("America/New_York");
                        } else if (myApplicationObj.getTempTimezoneID() == 4) {
                            myApplicationObj.setStrPreference("HST");
                        } else if (myApplicationObj.getTempTimezoneID() == 5) {
                            myApplicationObj.setStrPreference("AST");
                        }
                        // timeZoneET.setText(myApplicationObj.getTimezone());
                        Utils.showCustomToast(UserDetailsActivity.this, userPreference.getData().getMessage(), R.drawable.ic_custom_tick, "authid");

                    }
                }
            }
        });

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                if (profilesResponse != null) {
                    filterList = profilesResponse.getData();
                    for (ProfilesResponse.Profiles c : filterList) {
                        if (c.getAccountType().equals(Utils.BUSINESS)) {
                            businessAccountList.add(c);
                            addDetails(String.valueOf(c.getCompanyName()), c.getDbaName(), c.getImage(), c.getId());
                        } else {
                            personalAccountList.add(c);
                            for(int i=0;i<personalAccountList.size();i++){
                                if(personalAccountList.get(i).getId() == Integer.parseInt(accountTypeId)){
                                    personalAccountList.get(i).setSelected(true);
                                    business_defaultaccountET.setText(personalAccountList.get(i).getFullName());
                                } else {
                                    personalAccountList.get(i).setSelected(false);

                                }
                            }
                        }

                    }

                }
            }
        });

    }

    private int addDetails(String mainSet, String subSet, String image, int id) {

        LogUtils.d("ADDDETAILS", "adddetails" + mainSet + subSet + id + accountTypeId);
        int groupPosition = 0;
        BusinessAccountsListInfo headerInfo = this.mainSet.get(mainSet);

        if (headerInfo == null) {
            headerInfo = new BusinessAccountsListInfo();
            headerInfo.setName(mainSet);
            headerInfo.setMainImage(image);
            this.mainSet.put(mainSet, headerInfo);
            this.subSet.add(headerInfo);
        }

        ArrayList<BusinessAccountDbaInfo> subList = headerInfo.getSubsetName();
        int listSize = subList.size();
        listSize++;

        BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
        detailInfo.setName(subSet);
        detailInfo.setDbaImage(image);
        detailInfo.setId(id);

        if (detailInfo.getId() == Integer.parseInt(accountTypeId)) {
            detailInfo.setIsSelected(true);
            business_defaultaccountET.setText(subSet);

        } else {
            detailInfo.setIsSelected(false);
        }

        subList.add(detailInfo);

        headerInfo.setSubsetName(subList);
        groupPosition = this.subSet.indexOf(headerInfo);

        return groupPosition;
    }

    private void bindImage(String imageString) {
        try {
            userProfileIV.setVisibility(View.GONE);
            imageTextTV.setVisibility(View.VISIBLE);
            business_userProfileIV.setVisibility(View.GONE);
            business_imageTextTV.setVisibility(View.VISIBLE);
            String imageTextNew = "";
            imageTextNew = imageTextNew + myApplicationObj.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    myApplicationObj.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            imageTextTV.setText(imageTextNew);
            business_imageTextTV.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                userProfileIV.setVisibility(View.VISIBLE);
                imageTextTV.setVisibility(View.GONE);
                business_userProfileIV.setVisibility(View.VISIBLE);
                business_imageTextTV.setVisibility(View.GONE);

                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(userProfileIV);

                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(business_userProfileIV);
            } else {
                userProfileIV.setVisibility(View.GONE);
                imageTextTV.setVisibility(View.VISIBLE);

                business_userProfileIV.setVisibility(View.GONE);
                business_imageTextTV.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + myApplicationObj.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        myApplicationObj.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                imageTextTV.setText(imageText);
                business_imageTextTV.setText(imageText);
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
            Log.e("list states", listStates.size() + "");
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
                            Utils.displayAlert("No Profile image found to remove", UserDetailsActivity.this, "", "");
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
            Uri tempUri = getImageUri(UserDetailsActivity.this, scaled);

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
                myApplicationObj.getMyProfile().getData().getId() + "_profile" + System.currentTimeMillis(), null);
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
                Log.e("filename", "" + filename);
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
                    Utils.displayAlert("Requires Access to Camera.", UserDetailsActivity.this, "", "");

                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Your Storage.", UserDetailsActivity.this, "", "");

                } else {
//                    chooseImage(this);
                    showImagePickerDialog(this);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_CANCELED) {
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
                    userProfileIV.setVisibility(View.VISIBLE);
                    imageTextTV.setVisibility(View.GONE);
                    userProfileIV.setImageURI(resultUri);
                    uploadImage();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
            break;
            case 251: {
                if (resultCode == RESULT_OK) {
                    if (authenticateType.equals("EMAIL")) {
                        Intent ee = new Intent(UserDetailsActivity.this, EditEmailActivity.class);
                        startActivity(ee);
                    } else if (authenticateType.equals("ADDRESS")) {
                        Intent ea = new Intent(UserDetailsActivity.this, EditAddressActivity.class);
                        startActivity(ea);
                    } else if (authenticateType.equals("PHONE")) {
                        Intent ep = new Intent(UserDetailsActivity.this, EditPhoneActivity.class);
                        ep.putExtra("OLD_PHONE", phoneFormat);
                        startActivity(ep);

                    }
                } else {
                    if (authenticateType.equals("EMAIL")) {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditEmail"));
                    } else if (authenticateType.equals("ADDRESS")) {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditAddress"));
                    } else if (authenticateType.equals("PHONE")) {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("OLD_PHONE", phoneFormat)
                                .putExtra("screen", "EditPhone"));

                    }
                }
            }
            break;
        }
    }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        dashboardViewModel.meProfile();
        if (Utils.isKeyboardVisible) {
            Utils.hideKeypad(UserDetailsActivity.this);
        }
    }

    public static void showImagePickerDialog(Activity activity) {
        // custom dialog
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
                dialog.dismiss();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(pickPhoto, 1);
            }
        });

        takePhotoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(takePicture, 0);
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
    }

    public static void SetToken(MyApplication objMyApplication, Activity activity) {
        try {
            mydatabase = activity.openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsPermanentToken = mydatabase.rawQuery("Select * from tblPermanentToken", null);
            dsPermanentToken.moveToFirst();
            if (dsPermanentToken.getCount() > 0) {
                strToken = dsPermanentToken.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SetFaceLock(MyApplication objMyApplication, Activity activity) {
        try {
            isFaceLock = false;
            mydatabase = activity.openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFaceLock = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SetTouchId(MyApplication objMyApplication, Activity activity) {
        try {
            isTouchId = false;
            mydatabase = activity.openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsTouchID.moveToFirst();
            if (dsTouchID.getCount() > 0) {
                String value = dsTouchID.getString(1);
                if (value.equals("true")) {
                    isTouchId = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouchId = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
        Log.e("isKeyboardVisible", Utils.isKeyboardVisible + "");
    }

}
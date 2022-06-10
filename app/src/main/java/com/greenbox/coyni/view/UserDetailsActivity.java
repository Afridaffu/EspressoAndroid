package com.greenbox.coyni.view;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;

import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.AccountsData;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
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

public class UserDetailsActivity extends BaseActivity implements OnKeyboardVisibilityListener {

    private static Dialog bottomDialog;
    ImageView editProfileIV, userProfileIV, mIvUserIcon;
    private TextView userAddressTV, userPhoneNumTV, userEmailIdTV, imageTextTV, userNameTV, defualtAccountDialogPersonalNameTV,
            mTvUserIconText;
    private TextInputLayout business_defaultAccTIL;
    private TextInputEditText business_defaultaccountET;
    private MyApplication myApplicationObj;
    private DatabaseHandler dbHandler;
    private ExpandableListView profilesListView;
    private Long mLastClickTime = 0L;

    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();

    LinearLayout emailLL, phoneLL, addressLL, userDetailsCloseLL, businessPersonalProfileAccount;
    @SuppressLint("StaticFieldLeak")
    public static UserDetailsActivity userDetailsActivity;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    String strFileName = "", phoneFormat = "", phoneNumber = "";
    Dialog dialog;
    DashboardViewModel dashboardViewModel;
    boolean isProfile = false;
    private String personalAccountExist;

    String emailId = "", address = "", phoneNo = "";

    //Business
    ImageView business_userProfileIV;
    TextView business_imageTextTV, business_userNameTV, business_emailIdTV, business_userPhneNoTV, business_userAddreTV;
    LinearLayout business_emailLL, business_PhoneNumLL, business_AddreLL;

    static String strToken = "";
    static String strDeviceID = "";
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;
    String authenticateType = "";
    private LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();
    private CustomerProfileViewModel customerProfileViewModel;
    private int accountTypeId, preferredId = 0;
    //private int childid;
    private String selectedName;
    private BusinessProfileRecyclerAdapter profilesListAdapter;
    private ImageView businessPersonalProfileTickIcon;
    private String preferenceName;
    private String childName;
    private ProfilesResponse globalProfileResp;
    private CardView doneButton;

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
            showProgressDialog();
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
            dbHandler = DatabaseHandler.getInstance(UserDetailsActivity.this);
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

            business_defaultAccTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(UserDetailsActivity.this));
//            isBiometric = Utils.checkBiometric(UserDetailsActivity.this);
            try {
                isBiometric = Utils.getIsBiometric();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                setToken();
                setTouchId();
                setFaceLock();
            } catch (Exception e) {
                e.printStackTrace();
            }

            editProfileIV.setOnClickListener(view -> {

                if (checkAndRequestPermissions(this)) {
//                    chooseImage(this);
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    try {
                        showImagePickerDialog(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            emailLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    //New wireframe changes
                    try {
                        authenticateType = "EMAIL";
                        String face = String.valueOf(isFaceLock);
                        String touch = String.valueOf(isTouchId);

                        startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", emailId).putExtra("touch", touch).putExtra("face", face));
                    } catch (Exception e) {
                        e.printStackTrace();
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

                    try {
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
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    try {
                        authenticateType = "PHONE";
                        String face = String.valueOf(isFaceLock);
                        String touch = String.valueOf(isTouchId);

                        startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", phoneFormat).putExtra("touch", touch).putExtra("face", face));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            business_emailLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                try {
                    authenticateType = "EMAIL";
                    String face = String.valueOf(isFaceLock);
                    String touch = String.valueOf(isTouchId);

                    startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", emailId).putExtra("touch", touch).putExtra("face", face));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            business_AddreLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                try {
                    authenticateType = "ADDRESS";
                    String face = String.valueOf(isFaceLock);
                    String touch = String.valueOf(isTouchId);

                    startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", address).putExtra("touch", touch).putExtra("face", face));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            });

            business_PhoneNumLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                try {
                    authenticateType = "PHONE";
                    String face = String.valueOf(isFaceLock);
                    String touch = String.valueOf(isTouchId);

                    startActivity(new Intent(UserDetailsActivity.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "UserDetails").putExtra("title", authenticateType).putExtra("value", phoneFormat).putExtra("touch", touch).putExtra("face", face));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            userDetailsCloseLL.setOnClickListener(view -> finish());


            if (myApplicationObj.getMyProfile().getData().getFirstName() != null) {

                try {
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

                    LogUtils.d(TAG, "profiledata" + profile);

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
                } catch (Exception e) {
                    e.printStackTrace();
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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    final Dialog dialog = new Dialog(UserDetailsActivity.this);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.default_account_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    DisplayMetrics mertics = getApplicationContext().getResources().getDisplayMetrics();
//                    int width = mertics.widthPixels;

                    doneButton = dialog.findViewById(R.id.default_DoneBtn);
                    profilesListView = dialog.findViewById(R.id.business_profile_accounts_expandable_list);
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
                    LogUtils.d("TAG", "subSet" + subSet);
                    setProfilesAdapter();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    doneButton.setEnabled(false);
                    profilesListView.setAdapter(profilesListAdapter);
                    setInitialListViewHeight(profilesListView);
                    profilesListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            ImageView arrowImg = v.findViewById(R.id.arrowImg);
                            if (parent != null && parent.isGroupExpanded(groupPosition)) {
                                arrowImg.setImageResource(R.drawable.ic_chevron_down);
                            } else {
                                arrowImg.setImageResource(R.drawable.ic_chevron_up);
                            }
                            setListViewHeight(parent, groupPosition);
                            return false;
                        }
                    });

                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //LogUtils.d("profilesss", "childid" + childid);
                            UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
                            userPreferenceModel.setLocalCurrency(0);
                            userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
                            userPreferenceModel.setPreferredAccount(accountTypeId);
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

    private void setInitialListViewHeight(ExpandableListView listView) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void setProfilesAdapter() {

        boolean showDBA = false;
        AccountsData accountsData = new AccountsData(filterList);
        profilesListView.setVisibility(View.VISIBLE);
        profilesListAdapter = new BusinessProfileRecyclerAdapter(UserDetailsActivity.this, accountsData, preferredId, showDBA);

        profilesListAdapter.setOnItemClickListener(new BusinessProfileRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onGroupClicked(int position, String accountType, Integer id, String fullname) {
                LogUtils.v("PreferencesActivity", "account type: " + accountType + "  id: " + id + " fullname " + fullname);
                if (preferredId != id) {
                    doneButton.setEnabled(true);
                    doneButton.setCardBackgroundColor(getColor(R.color.primary_color));
                    accountTypeId = id;
                    selectedName = fullname;
                } else {
                    doneButton.setEnabled(false);
                    doneButton.setCardBackgroundColor(getColor(R.color.light_primary_color));
                }
            }

            @Override
            public void onChildClicked(ProfilesResponse.Profiles detailInfo) {
                LogUtils.v("PreferencesActivity", "account type " + detailInfo + "    id: " + detailInfo.getId() + "detailInfo" + detailInfo.getDbaName());
                //childid = detailInfo.getId();

                if (preferredId != detailInfo.getId()) {
                    doneButton.setEnabled(true);
                    doneButton.setCardBackgroundColor(getColor(R.color.primary_color));
                    accountTypeId = detailInfo.getId();
                    selectedName = detailInfo.getDbaName();
                } else {
                    doneButton.setEnabled(false);
                    doneButton.setCardBackgroundColor(getColor(R.color.light_primary_color));
                }

            }

            @Override
            public void onAddDbaClicked(String accountType, Integer id) {
                LogUtils.v("PreferencesActivity", "account type " + accountType + "    id: " + id);
                //accountTypeId = id;

            }
        });
        profilesListView.setAdapter(profilesListAdapter);

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
                        try {
                            userProfileIV.setVisibility(View.GONE);
                            imageTextTV.setVisibility(View.VISIBLE);
                            String imageTextNew = "";
                            imageTextNew = imageTextNew + myApplicationObj.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                                    myApplicationObj.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                            imageTextTV.setText(imageTextNew);
                            dashboardViewModel.meProfile();
                            Utils.showCustomToast(UserDetailsActivity.this, imageResponse.getData().getMessage(), R.drawable.ic_custom_tick, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                    dashboardViewModel.getProfiles();
                    if (preferences.getData().getPreferredAccount() != null && !preferences.getData().getPreferredAccount().trim().equals("")) {
                        accountTypeId = Integer.parseInt(preferences.getData().getPreferredAccount());
                        preferredId = accountTypeId;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                try {
                    dismissDialog();
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

                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                        LogUtils.d(TAG, "userPreference" + userPreference);
                        if (selectedName == null || selectedName.equals(""))
                            business_defaultaccountET.setText("[DBA Name]");
                        else if (selectedName.length() > 20)
                            business_defaultaccountET.setText((selectedName).substring(0, 20));
                        else
                            business_defaultaccountET.setText(selectedName);

                        preferredId = accountTypeId;
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
//                        Utils.showCustomToast(UserDetailsActivity.this, userPreference.getData().getMessage(), R.drawable.ic_custom_tick, "authid");
                        Utils.showCustomToast(UserDetailsActivity.this, getResources().getString(R.string.default_account_changed), R.drawable.ic_custom_tick, "authid");

                    }
                }
            }
        });

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                if (profilesResponse != null) {
                    filterList = profilesResponse.getData();
                    LogUtils.v(TAG, "getProfileRespMutableLiveData" + profilesResponse.getData());
                    LogUtils.v(TAG, "accountTypeId" + accountTypeId);
                    globalProfileResp = profilesResponse;
                    for (ProfilesResponse.Profiles c : filterList) {
                        if (c.getId() == accountTypeId) {
                            if(c.getAccountType().equals(Utils.PERSONAL)) {
                                selectedName = c.getFullName();
                            } else {
                                selectedName = c.getDbaName();
                            }
                            business_defaultaccountET.setText(selectedName);
                        }
                    }

                }
            }
        });

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

                if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                    utility.addImage(imageString, userProfileIV, R.drawable.ic_profile_male_user);
                } else {
                    DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                    utility.addImage(imageString, business_userProfileIV, R.drawable.ic_profile_male_user);
                }
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
            dialog = Utils.showProgressDialog(UserDetailsActivity.this);
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
                dialog = Utils.showProgressDialog(this);
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
//                    Utils.displayAlert("Requires Access to Camera.", UserDetailsActivity.this, "", "");
//                    displayAlertNew("Requires Access to Camera.", UserDetailsActivity.this, "");

                    Utils.showDialogPermission(UserDetailsActivity.this, getString(R.string.allow_access_header), getString(R.string.camera_permission_desc));

                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    displayAlertNew("Requires Access to Your Storage.", UserDetailsActivity.this, "");

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
        try {
            dashboardViewModel.meProfile();
            if (Utils.isKeyboardVisible) {
                Utils.hideKeypad(UserDetailsActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                myApplicationObj.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                myApplicationObj.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTouchId() {
        try {
            isTouchId = false;
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                isTouchId = true;
                myApplicationObj.setLocalBiometric(true);
            } else {
                isTouchId = false;
                myApplicationObj.setLocalBiometric(false);
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


    public static void displayAlertNew(String msg, final Context context, String headerText) {
        // custom dialog
        if (bottomDialog == null) {
            bottomDialog = new Dialog(context);
            bottomDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            bottomDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
            bottomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView header = bottomDialog.findViewById(R.id.tvHead);
            TextView message = bottomDialog.findViewById(R.id.tvMessage);
            CardView actionCV = bottomDialog.findViewById(R.id.cvAction);
            TextView actionText = bottomDialog.findViewById(R.id.tvAction);

            if (!headerText.equals("")) {
                header.setVisibility(View.VISIBLE);
                header.setText(headerText);
            }

            actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomDialog.dismiss();
                }
            });

            message.setText(msg);
            Window window = bottomDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            bottomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            bottomDialog.setCanceledOnTouchOutside(true);
            bottomDialog.show();

            bottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    bottomDialog = null;
                }
            });
        }
    }

}
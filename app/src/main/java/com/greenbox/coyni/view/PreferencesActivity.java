package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.AccountsData;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PreferencesActivity extends BaseActivity implements BusinessProfileRecyclerAdapter.OnSelectListner {

    private MyApplication myApplicationObj;
    ProgressDialog dialog;
    private DashboardViewModel dashboardViewModel;
    private boolean isProfile = false;
    private TextInputLayout timeZoneTIL, accountTIL, currencyTIL;
    private TextInputEditText timeZoneET, accountET, currencyET;
    private RelativeLayout timeZoneRL, accountRL;
    private LinearLayout preferencesCloseLL, defaultaccLL;
    private TextView timezonetext, mTvUserIconText, defualtAccountDialogPersonalNameTV;
    private ImageView accountDDIV;
    private View disableView;
    public static CustomerProfileViewModel customerProfileViewModel;
    private int timeZoneID = 0;
    public static PreferencesActivity preferencesActivity;
    private ExpandableListView profilesListView;
    private ImageView editProfileIV, userProfileIV, mIvUserIcon;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private int childid;
    private String SelectedDBAName = "";
    private BusinessProfileRecyclerAdapter profilesListAdapter;
    private ImageView businessPersonalProfileTickIcon;
    LinearLayout emailLL, phoneLL, addressLL, userDetailsCloseLL, businessPersonalProfileAccount;
    Long mLastClickTime = 0L;
    private LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();
    private String accountTypeId = "";
    private String personalAccountExist;
    ProfilesResponse globalProfileResp;
    private String childName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_preferences);
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {

        try {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            preferencesActivity = this;
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            myApplicationObj = (MyApplication) getApplicationContext();
            timeZoneTIL = findViewById(R.id.timeZoneTIL);
            timeZoneET = findViewById(R.id.timeZoneET);
            accountTIL = findViewById(R.id.accountTIL);
            accountRL = findViewById(R.id.accountRL);
            currencyTIL = findViewById(R.id.currencyTIL);
            accountET = findViewById(R.id.accountET);
            currencyET = findViewById(R.id.currencyET);
            timeZoneRL = findViewById(R.id.timezoneRL);
            accountDDIV = findViewById(R.id.accountDDICon);
            //disableView = findViewById(R.id.disableView);
            preferencesCloseLL = findViewById(R.id.preferencesCloseLL);
            timezonetext = findViewById(R.id.timezoneTextTV);
            defaultaccLL = findViewById(R.id.defaultaccLL);

            timeZoneTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            Utils.setUpperHintColor(currencyTIL, getColor(R.color.et_bg_gray));

            currencyTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            Utils.setUpperHintColor(currencyTIL, getColor(R.color.xdark_gray));

            accountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            Utils.setUpperHintColor(accountTIL, getColor(R.color.xdark_gray));

            timeZoneRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj, "PREFERENCES");
                }
            });

            accountET.setOnClickListener(view -> {
                try {
                    final Dialog dialog = new Dialog(PreferencesActivity.this);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.default_account_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    DisplayMetrics mertics = getApplicationContext().getResources().getDisplayMetrics();
//                    int width = mertics.widthPixels;

                    CardView doneButton = dialog.findViewById(R.id.default_DoneBtn);
                    profilesListView = dialog.findViewById(R.id.business_profile_accounts_expandable_list);
                    mIvUserIcon = dialog.findViewById(R.id.profile_img);
                    mTvUserIconText = dialog.findViewById(R.id.b_imageTextTV);
                    businessPersonalProfileAccount = dialog.findViewById(R.id.profileLL);
                    businessPersonalProfileTickIcon = dialog.findViewById(R.id.tickIcon);
                    defualtAccountDialogPersonalNameTV = dialog.findViewById(R.id.defualt_account_dialog_personal_name);

                    Window windowAccount = dialog.getWindow();
                    windowAccount.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (mertics.heightPixels * 0.75));
                    WindowManager.LayoutParams wlp = windowAccount.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    windowAccount.setAttributes(wlp);

                    setProfilesAdapter();

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

//            timeZoneTIL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj);
//                }
//            });

            timeZoneET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj, "PREFERENCES");
                }
            });

            preferencesCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            dialog = Utils.showProgressDialog(this);

            dashboardViewModel.mePreferences();

            //  dashboardViewModel.getProfiles();
            // Business Preferences

            if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                timezonetext.setVisibility(View.VISIBLE);
                defaultaccLL.setVisibility(View.GONE);
            }

            if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                timezonetext.setVisibility(View.GONE);
                defaultaccLL.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initObservers() {

        dashboardViewModel.getPreferenceMutableLiveData().observe(this, new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences preferences) {

                try {
                    if (preferences != null) {

                        timeZoneID = preferences.getData().getTimeZone();
                        myApplicationObj.setTimezoneID(timeZoneID);
                        if (preferences.getData().getTimeZone() == 0) {
                            timeZoneET.setText(getString(R.string.PST));
                            myApplicationObj.setTempTimezone(getString(R.string.PST));
                            myApplicationObj.setTempTimezoneID(0);
                            myApplicationObj.setStrPreference("PST");
                        } else if (preferences.getData().getTimeZone() == 1) {
                            timeZoneET.setText(getString(R.string.MST));
                            myApplicationObj.setTempTimezone(getString(R.string.MST));
                            myApplicationObj.setTempTimezoneID(1);
                            myApplicationObj.setStrPreference("America/Denver");
                        } else if (preferences.getData().getTimeZone() == 2) {
                            timeZoneET.setText(getString(R.string.CST));
                            myApplicationObj.setTempTimezone(getString(R.string.CST));
                            myApplicationObj.setTempTimezoneID(2);
                            myApplicationObj.setStrPreference("CST");
                        } else if (preferences.getData().getTimeZone() == 3) {
                            timeZoneET.setText(getString(R.string.EST));
                            myApplicationObj.setTempTimezone(getString(R.string.EST));
                            myApplicationObj.setTempTimezoneID(3);
                            myApplicationObj.setStrPreference("America/New_York");
                        } else if (preferences.getData().getTimeZone() == 4) {
                            timeZoneET.setText(getString(R.string.HST));
                            myApplicationObj.setTempTimezone(getString(R.string.HST));
                            myApplicationObj.setTempTimezoneID(4);
                            myApplicationObj.setStrPreference("HST");
                        } else if (preferences.getData().getTimeZone() == 5) {
                            timeZoneET.setText(getString(R.string.AST));
                            myApplicationObj.setTempTimezone(getString(R.string.AST));
                            myApplicationObj.setTempTimezoneID(5);
                            myApplicationObj.setStrPreference("AST");
                        }

                        accountTypeId = preferences.getData().getPreferredAccount();

                        dashboardViewModel.getProfiles();
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
                        Utils.displayAlert(userPreference.getError().getErrorDescription(), PreferencesActivity.this, "", userPreference.getError().getFieldErrors().get(0));
                    } else {
                        try {
                            if (SelectedDBAName.equals(""))
                                accountET.setText(Utils.capitalize(globalProfileResp.getData().get(0).getFullName()));
                            else
                                accountET.setText(SelectedDBAName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                        timeZoneET.setText(myApplicationObj.getTimezone());
                        Utils.showCustomToast(PreferencesActivity.this, userPreference.getData().getMessage(), R.drawable.ic_custom_tick, "authid");

                    }
                }
            }
        });

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                dialog.dismiss();
                if (profilesResponse != null) {
//                    if(profilesResponse.getData().get(0).getEntityName() != null) {
//                        accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getEntityName()));
//                    }
////                    accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getEntityName()));
                    accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getFullName()));
                    globalProfileResp = profilesResponse;

                    if (profilesResponse.getStatus().equals("SUCCESS")) {

                        filterList = profilesResponse.getData();

                    }
                }
            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (apiError == null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void setProfilesAdapter() {
        boolean showDBA = false;
        AccountsData accountsData = new AccountsData(filterList);
        profilesListView.setVisibility(View.VISIBLE);
        profilesListAdapter = new BusinessProfileRecyclerAdapter(PreferencesActivity.this, accountsData,myApplicationObj.getLoginUserId(),showDBA);

        profilesListAdapter.setOnItemClickListener(new BusinessProfileRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onGroupClicked(int position, String accountType, Integer id,String fullname) {
                LogUtils.v("PreferencesActivity", "account type " + accountType + "    id: " + id);
                childid = id;
                SelectedDBAName = fullname;

            }

            @Override
            public void onChildClicked(ProfilesResponse.Profiles detailInfo) {
                LogUtils.v("PreferencesActivity", "account type " + detailInfo + "    id: " + detailInfo.getId());
                childid = detailInfo.getId();
                SelectedDBAName = detailInfo.getDbaName();
            }

            @Override
            public void onAddDbaClicked(String accountType, Integer id) {
                LogUtils.v("PreferencesActivity", "account type " + accountType + "    id: " + id);
                childid = id;

            }
        });
        profilesListView.setAdapter(profilesListAdapter);

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
        } else {
            detailInfo.setIsSelected(false);
        }

        subList.add(detailInfo);

        headerInfo.setSubsetName(subList);
        groupPosition = this.subSet.indexOf(headerInfo);

        return groupPosition;
    }


    @Override
    public void selectedItem(int id) {

    }

}
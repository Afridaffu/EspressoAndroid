package com.coyni.mapp.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.adapters.BusinessProfileRecyclerAdapter;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.AccountsData;
import com.coyni.mapp.model.preferences.BaseProfile;
import com.coyni.mapp.model.preferences.Preferences;
import com.coyni.mapp.model.preferences.ProfilesResponse;
import com.coyni.mapp.model.preferences.UserPreference;
import com.coyni.mapp.model.profile.BusinessAccountsListInfo;
import com.coyni.mapp.model.users.UserPreferenceModel;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.UserData;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PreferencesActivity extends BaseActivity implements BusinessProfileRecyclerAdapter.OnSelectListner {

    private MyApplication myApplicationObj;
    Dialog dialog;
    private UserData mCurrentUserData;
    private DashboardViewModel dashboardViewModel;
    private boolean isProfile = false;
    private TextInputLayout timeZoneTIL, accountTIL, currencyTIL;
    private TextInputEditText timeZoneET, accountET, currencyET;
    private RelativeLayout timeZoneRL, accountRL;
    private LinearLayout preferencesCloseLL, defaultaccLL;
    private TextView timezonetext, mTvUserIconText, defualtAccountDialogPersonalNameTV;
    private ImageView accountDDIV;
    private View accountDisableView;
    private CustomerProfileViewModel customerProfileViewModel;
    private int timeZoneID = 0;
    public static PreferencesActivity preferencesActivity;
    private ExpandableListView profilesListView;
    private ImageView editProfileIV, userProfileIV, mIvUserIcon;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private String selectedName = "";
    private BusinessProfileRecyclerAdapter profilesListAdapter;
    private ImageView businessPersonalProfileTickIcon;
    LinearLayout emailLL, phoneLL, addressLL, userDetailsCloseLL, businessPersonalProfileAccount;
    Long mLastClickTime = 0L;
    private LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();
    private int accountTypeId, preferredId = 0;
    private String personalAccountExist;
    ProfilesResponse globalProfileResp;
    private String childName;
    private int userId;
    private CardView doneButton;
    private boolean isTimeZone = false;
    private AccountsData accountsData;


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

    public void callTimeZonePreferenceApi() {
        UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
        userPreferenceModel.setLocalCurrency(0);
        userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
        if (myApplicationObj.getAccountType() != Utils.SHARED_ACCOUNT) {
            userPreferenceModel.setPreferredAccount(preferredId);
        }
        isTimeZone = true;
//        if (myApplicationObj.getAccountType() != Utils.SHARED_ACCOUNT)
        customerProfileViewModel.updatePreferences(userPreferenceModel, myApplicationObj.getAccountType());
//        else
//            customerProfileViewModel.updateTimeZoneShared(userPreferenceModel);
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
            accountDisableView = findViewById(R.id.accountDisableView);
            //disableView = findViewById(R.id.disableView);
            preferencesCloseLL = findViewById(R.id.preferencesCloseLL);
            timezonetext = findViewById(R.id.timezoneTextTV);
            defaultaccLL = findViewById(R.id.defaultaccLL);


            mCurrentUserData = new UserData();
            userId = myApplicationObj.getLoginUserId();
            timeZoneTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            Utils.setUpperHintColor(currencyTIL, getColor(R.color.et_bg_gray));

            currencyTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            Utils.setUpperHintColor(currencyTIL, getColor(R.color.xdark_gray));

            accountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            Utils.setUpperHintColor(accountTIL, getColor(R.color.text_color));

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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    final Dialog dialog = new Dialog(PreferencesActivity.this);
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
                    doneButton.setEnabled(false);


                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            LogUtils.d("profilesss", "childid" + accountTypeId);
                            UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
                            userPreferenceModel.setLocalCurrency(0);
                            //userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
                            userPreferenceModel.setPreferredAccount(accountTypeId);
                            isTimeZone = false;
                            customerProfileViewModel.updateDefaultAccount(userPreferenceModel);
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

            dashboardViewModel.mePreferences(myApplicationObj);

            //  dashboardViewModel.getProfiles();
            // Business Preferences

            if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT
                    || myApplicationObj.getAccountType() == Utils.SHARED_ACCOUNT) {
                timezonetext.setVisibility(View.VISIBLE);
                defaultaccLL.setVisibility(View.GONE);
            } else if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
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
                        if (myApplicationObj.getAccountType() != Utils.SHARED_ACCOUNT)
                            dashboardViewModel.getProfiles();
                        else
                            dialog.dismiss();
                        if (preferences.getData().getPreferredAccount() != null && !preferences.getData().getPreferredAccount().trim().equals("")) {
                            accountTypeId = Integer.parseInt(preferences.getData().getPreferredAccount());
                            preferredId = accountTypeId;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        customerProfileViewModel.getUserPreferenceMutableLiveData().

                observe(this, new Observer<UserPreference>() {
                    @Override
                    public void onChanged(UserPreference userPreference) {
                        if (userPreference != null) {
                            if (!userPreference.getStatus().toLowerCase().equals("success")) {
                                Utils.displayAlert(userPreference.getError().getErrorDescription(), PreferencesActivity.this, "", userPreference.getError().getFieldErrors().get(0));
                            } else {
                                try {
                                    if (selectedName.equals(""))
                                        accountET.setText(Utils.capitalize(globalProfileResp.getData().get(0).getFullName()));
                                    else
                                        accountET.setText(selectedName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                                timeZoneET.setText(myApplicationObj.getTimezone());
                                Utils.showCustomToast(PreferencesActivity.this, isTimeZone ? getString(R.string.time_zone_changed) : getString(R.string.default_account_changed), R.drawable.ic_custom_tick, "authid");

                            }
                        }
                    }
                });

        dashboardViewModel.getProfileRespMutableLiveData().

                observe(this, new Observer<ProfilesResponse>() {
                    @Override
                    public void onChanged(ProfilesResponse profilesResponse) {
                        dialog.dismiss();
                        if (profilesResponse != null) {
                            accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getFullName()));
                            globalProfileResp = profilesResponse;
                            if (profilesResponse.getStatus().equals("SUCCESS")) {
                                filterList = profilesResponse.getData();
                                setDefaultAccountData();
                            }
                        }
                    }
                });

        dashboardViewModel.getApiErrorMutableLiveData().

                observe(this, new Observer<APIError>() {
                    @Override
                    public void onChanged(APIError apiError) {
                        if (apiError == null) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void setDefaultAccountData() {
        if (filterList == null) {
            return;
        }

        for (ProfilesResponse.Profiles c : filterList) {
            if (c.getId() == accountTypeId) {
                if (c.getAccountType().equals(Utils.PERSONAL)) {
                    selectedName = c.getFullName();
                } else {
                    selectedName = c.getDbaName();
                }
                accountET.setText(selectedName);
            }
        }

        accountsData = new AccountsData(filterList);

        accountsData.removeSharedAccounts();
        accountsData.removeDeclinedPersonalAccount();


        if (accountsData.getGroupData().size() > 1) {
            accountET.setEnabled(true);
            enableOrDisableAccount(false);
        } else if (accountsData.getGroupData().size() == 1) {
            BaseProfile groupProfile = accountsData.getGroupData().get(0);
            ArrayList<ProfilesResponse.Profiles> profilesList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(groupProfile.getId());
            if (groupProfile.getAccountType().equalsIgnoreCase(Utils.PERSONAL)
                    || profilesList == null || profilesList.size() <= 1) {
                accountET.setEnabled(false);
                enableOrDisableAccount(true);
            } else {
                accountET.setEnabled(true);
                enableOrDisableAccount(false);
            }
        } else {
            accountET.setEnabled(false);
            enableOrDisableAccount(true);
        }
    }

    private void setProfilesAdapter() {
        boolean showDBA = false;
        profilesListView.setVisibility(View.VISIBLE);
        profilesListAdapter = new BusinessProfileRecyclerAdapter(PreferencesActivity.this, accountsData, preferredId, showDBA);

        profilesListAdapter.setOnItemClickListener(new BusinessProfileRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onGroupClicked(int position, String accountType, Integer id, String fullname) {
                LogUtils.v("PreferencesActivity", "account type " + accountType + "    id: " + id);
//                childid = id;
//                selectedName = fullname;
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
                LogUtils.v("PreferencesActivity", "account type " + detailInfo + "    id: " + detailInfo.getId());
//                childid = detailInfo.getId();
//                selectedName = detailInfo.getDbaName();
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

//            @Override
//            public void onAddDbaClicked(String accountType, Integer id) {
//                LogUtils.v("PreferencesActivity", "account type " + accountType + "    id: " + id);
//                //childid = id;
//
//            }


            @Override
            public void onAddDbaClicked(ProfilesResponse.Profiles profiles, Integer id) {
                LogUtils.v("PreferencesActivity", "account type " + profiles.getAccountType() + "    id: " + id);
                //childid = id;
            }
        });

        profilesListView.setAdapter(profilesListAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setInitialListViewHeight(profilesListView);
            }
        }, 200);

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
    }

    @Override
    public void selectedItem(int id) {

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
        //params.height = (int) (height * 0.55);
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

    private void enableOrDisableAccount(boolean enable) {
        if (enable) {
            accountDDIV.setVisibility(View.GONE);
            accountDisableView.setVisibility(View.VISIBLE);
        } else {
            accountDDIV.setVisibility(View.VISIBLE);
            accountDisableView.setVisibility(View.GONE);
        }
    }

}
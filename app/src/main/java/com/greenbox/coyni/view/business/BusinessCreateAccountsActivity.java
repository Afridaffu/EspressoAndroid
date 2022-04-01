package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.model.AccountsData;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.businesswallet.WalletInfo;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BusinessCreateAccountsActivity extends BaseActivity {

    private TextView userShortInfoTV, defualtAccountDialogPersonalNameTV, userNameTV,
            userBalanceTV, mTvUserIconText;
    private ImageView imgProfile, accountsCloseIV, mIvUserIcon;
    private LinearLayout llOpenAccount, businessSharedProfileAccount;
    private MyApplication myApplication;
    private DashboardViewModel dashboardViewModel;
    private ExpandableListView profilesListView, sharedEL;
    private List<ProfilesResponse.Profiles> profilesList = new ArrayList<>();
    private final List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private final List<ProfilesResponse.Profiles> sharedAccountList = new ArrayList<>();
    private final List<ProfilesResponse.Profiles> dbaList = new ArrayList<>();
    private final List<ProfilesResponse.Profiles> dbaAccountList = new ArrayList<>();
    private final List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private ImageView businessPersonalProfileTickIcon;
    private LoginViewModel loginViewModel;
    private int childid;
    private String SelectedDBAName;
    private final String accountTypeId = "";
    private final LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private final LinkedHashMap<String, BusinessAccountsListInfo> mainSetShared = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private final ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();
    private final ArrayList<BusinessAccountsListInfo> subSetShared = new ArrayList<BusinessAccountsListInfo>();
    private BusinessProfileRecyclerAdapter profilesListAdapter;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_business_personal_accounts);

            initFields();
            showUserData();
            initObservers();

            accountsCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            llOpenAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessAddNewAccountActivity.class)
//                            .putExtra("PersonalAccount", personalAccountExist)
//                    );
                    startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessAddNewAccountActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardViewModel.getProfiles();

    }

    private void initFields() {
        try {
            llOpenAccount = findViewById(R.id.ll_open_account);
            userShortInfoTV = findViewById(R.id.tvUserInfo);
            imgProfile = findViewById(R.id.imgProfile);
            userNameTV = findViewById(R.id.userNameTV);
            userBalanceTV = findViewById(R.id.userBalanceTV);
            accountsCloseIV = findViewById(R.id.accountsCloseIV);
            profilesListView = findViewById(R.id.recyclerView);
            mIvUserIcon = findViewById(R.id.profile_img);
            mTvUserIconText = findViewById(R.id.b_imageTextTV);
            myApplication = (MyApplication) getApplicationContext();
            businessPersonalProfileTickIcon = findViewById(R.id.tickIcon);
            defualtAccountDialogPersonalNameTV = findViewById(R.id.defualt_account_dialog_personal_name);

        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        loginViewModel = new ViewModelProvider(BusinessCreateAccountsActivity.this).get(LoginViewModel.class);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

//        profilesListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
//                businessPersonalProfileTickIcon.setVisibility(View.GONE);
//                BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
//                LogUtils.d(TAG, "GroupChildClick" + i + "....." + i1 + "....." + l);
//                childid = subSet.get(i).getSubsetName().get(i1).getId();
//                SelectedDBAName = subSet.get(i).getSubsetName().get(i1).getName();
//
//                for(int k=0;k<subSet.size();k++) {
//                    for (int j = 0; j < subSet.get(k).getSubsetName().size(); j++) {
//                        if (subSet.get(k).getSubsetName().get(j).getId() == childid) {
//                            subSet.get(k).getSubsetName().get(j).setIsSelected(true);
//                        } else {
//                            subSet.get(k).getSubsetName().get(j).setIsSelected(false);
//                        }
//                    }
//                }
//                LogUtils.d(TAG, "subSetChildClick" + subSet);
//                LogUtils.d(TAG, "childid" + childid);
//                changeAccount(childid);
//                listAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });

//        businessPersonalProfileAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
//                businessPersonalProfileTickIcon.setVisibility(View.VISIBLE);
//                childid = personalAccountList.get(0).getId();
//                SelectedDBAName = personalAccountList.get(0).getFullName();
//                for (int k = 0; k < subSet.size(); k++) {
//                    for (int j = 0; j < subSet.get(k).getSubsetName().size(); j++) {
//                        if (subSet.get(k).getSubsetName().get(j).getId() == childid) {
//                            subSet.get(k).getSubsetName().get(j).setIsSelected(true);
//                        } else {
//                            subSet.get(k).getSubsetName().get(j).setIsSelected(false);
//                        }
//                    }
//                }
//                LogUtils.d(TAG, "subSetChildClick" + subSet);
//                changeAccount(childid);
//                profilesListAdapter.notifyDataSetChanged();
//
//            }
//        });
    }

    private void changeAccount(int childID) {

        loginViewModel.postChangeAccount(childID);

    }

    private void showUserData() {
        String iconText = "";
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getFirstName() != null) {
            String firstName = myApplication.getMyProfile().getData().getFirstName();
            iconText = firstName.substring(0, 1).toUpperCase();
            userName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            if (myApplication.getMyProfile().getData().getLastName() != null) {
                String lastName = myApplication.getMyProfile().getData().getLastName();
                iconText = iconText + lastName.substring(0, 1).toUpperCase();
                userName = userName + " ";
                userName = userName + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }

            userNameTV.setText(getResources().getString(R.string.dba_name, userName));

            if (userName != null && userName.length() > 20) {
                userNameTV.setText(userName.substring(0, 20));
            } else {
                userNameTV.setText(userName);
            }

            userNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userNameTV.getText().toString().contains("...")) {
                        if (userName.length() == 21 || userName.length() > 21) {
                            userNameTV.setText(userName.substring(0, 20));
                        } else {
                            userNameTV.setText(userName);
                        }
                    } else {
                        if (userName.length() == 21) {
                            userNameTV.setText(userName.substring(0, 20) + "...");
                        } else if (userName.length() > 22) {
                            userNameTV.setText(userName.substring(0, 22) + "...");
                        } else {
                            userNameTV.setText(userName);
                        }
                    }
                }
            });
        }
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getImage() != null) {
            userShortInfoTV.setVisibility(View.GONE);
            imgProfile.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(myApplication.getMyProfile().getData().getImage())
                    .placeholder(R.drawable.ic_profile_male_user)
                    .into(imgProfile);
        } else {
            userShortInfoTV.setVisibility(View.VISIBLE);
            imgProfile.setVisibility(View.GONE);
            userShortInfoTV.setText(iconText);
        }

//        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
//                && myApplication.getMyProfile().getData().getImage() != null) {
//            userShortInfoTV.setVisibility(View.GONE);
//            imgProfile.setVisibility(View.VISIBLE);
//            Glide.with(this)
//                    .load(myApplication.getMyProfile().getData().getImage())
//                    .placeholder(R.drawable.ic_profile_male_user)
//                    .into(imgProfile);
//        } else {
//            userShortInfoTV.setVisibility(View.VISIBLE);
//            imgProfile.setVisibility(View.GONE);
//            userShortInfoTV.setText(iconText);
//        }

        setUserBalance(myApplication.getWalletResponseData());

//        Double bal = myApplication.getGBTBalance();
//        String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
//        userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));

    }

    private void setUserBalance(WalletResponseData walletResponse) {
        try {
            String strAmount = "";
            if(walletResponse == null) {
                return;
            }
            List<WalletInfo> walletInfo = walletResponse.getWalletNames();
            LogUtils.d(TAG, "setUserBalance" + walletInfo.toString());
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
//                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
                    myApplication.setGbtWallet(walletInfo.get(i));
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getExchangeAmount()));
                    userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                    myApplication.setGBTBalance(walletInfo.get(i).getExchangeAmount());
//                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setProfilesAdapter() {

        AccountsData accountsData = new AccountsData(profilesList);
        profilesListView.setVisibility(View.VISIBLE);
        profilesListAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this,
                accountsData, myApplication.getLoginUserId());

        profilesListAdapter.setOnItemClickListener(new BusinessProfileRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onGroupClicked(int position, String accountType, Integer id) {
                LogUtils.v(TAG, "account type " + accountType + "    id: " + id);
                changeAccount(id);
            }

            @Override
            public void onChildClicked(String accountType, Integer id) {
                LogUtils.v(TAG, "account type " + accountType + "    id: " + id);
                changeAccount(id);

            }

            @Override
            public void onAddDbaClicked(String accountType, Integer id) {
                LogUtils.v(TAG, "account type " + accountType + "    id: " + id);
                changeAccount(id);

            }
        });
        profilesListView.setAdapter(profilesListAdapter);
        setInitialListViewHeight(profilesListView);
        profilesListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
    }

    public void initObservers() {

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                if (profilesResponse != null) {
                    profilesList = profilesResponse.getData();
                    setProfilesAdapter();

//                    for (ProfilesResponse.Profiles c : filterList) {
//                        if (c.getAccountType().equals(Utils.BUSINESS)) {
//                            businessAccountList.add(c);
//                            addDetails(String.valueOf(c.getCompanyName()), c.getDbaName(), c.getImage(), c.getId(), c.getAccountStatus());
//                        }
//                        else {
//                            personalAccountList.add(c);
//                            for(int i=0;i<personalAccountList.size();i++){
//                                    personalAccountList.get(i).setSelected(false);
//                            }
//                        }
//
//                    }
//
//                    for (ProfilesResponse.Profiles c : filterList) {
//                        if (c.getAccountType().equals(Utils.SHARED)) {
//                            businessAccountList.add(c);
//                            addDetails("Shared Account", c.getCompanyName(), c.getImage(), c.getId(), c.getAccountStatus());
//                        }
//                    }

//                    if (businessAccountList.size() != 0) {
//                        try {
//                            brandsGV.setVisibility(View.VISIBLE);
//                            LogUtils.d(TAG, "subSet" + subSet);
//                            listAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this, accountsData, BusinessCreateAccountsActivity.this);
//                            brandsGV.setAdapter(listAdapter);
//                            setListViewHeight(brandsGV, -1);
//                            brandsGV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//                                @Override
//                                public boolean onGroupClick(ExpandableListView parent, View v,
//                                                            int groupPosition, long id) {
//                                    setListViewHeight(parent, groupPosition);
//                                    return false;
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        brandsGV.setVisibility(View.GONE);
//                    }

//                    if (sharedAccountList.size() != 0) {
//                        try {
//                            businessSharedProfileAccount.setVisibility(View.GONE);
//                            LogUtils.d(TAG, "subSetShared" + subSetShared);
//                            listAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this, subSetShared, BusinessCreateAccountsActivity.this);
//                            sharedEL.setAdapter(listAdapter);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        businessSharedProfileAccount.setVisibility(View.GONE);
//                    }

//                    if (personalAccountList.size() != 0) {
//                        String iconText = "";
//                        if (personalAccountList.get(0).getCompanyName() != null
//                        ) {
//                            String firstName = personalAccountList.get(0).getCompanyName();
//                            iconText = firstName.substring(0, 1).toUpperCase();
//                            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
//
//                        }
//                        if (personalAccountList.get(0).getImage() != null) {
//                            mTvUserIconText.setVisibility(View.GONE);
//                            mIvUserIcon.setVisibility(View.VISIBLE);
//                            Glide.with(BusinessCreateAccountsActivity.this)
//                                    .load(personalAccountList.get(0).getImage())
//                                    .placeholder(R.drawable.ic_profile_male_user)
//                                    .into(mIvUserIcon);
//                        } else {
//                            mTvUserIconText.setVisibility(View.VISIBLE);
//                            mIvUserIcon.setVisibility(View.GONE);
//                            mTvUserIconText.setText(iconText);
//                        }
//                    }
                }
            }
        });

        businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
            @Override
            public void onChanged(BusinessTrackerResponse businessTrackerResponse) {
                if (businessTrackerResponse != null) {
                    if (businessTrackerResponse.getStatus().toLowerCase().equals("success")) {
                        myApplication.setBusinessTrackerResponse(businessTrackerResponse);

                    }
                }
            }
        });

        loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
            @Override
            public void onChanged(AddBusinessUserResponse btResp) {
                if (btResp != null) {
                    if (btResp.getStatus().toLowerCase().equals("success")) {

                        LogUtils.d(TAG, "btResp" + btResp.getData().getAccountType());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getAccountStatus());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getDbaOwnerId());

                        Utils.setStrAuth(btResp.getData().getJwtToken());
                        myApplication.setLoginUserId(btResp.getData().getUserId());
                        businessIdentityVerificationViewModel.getBusinessTracker();

                        if (btResp.getData().getAccountType() == Utils.BUSINESS_ACCOUNT || btResp.getData().getAccountType() == Utils.SHARED_ACCOUNT) {
                            // if (btResp.getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                            myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
                            Intent intent = new Intent(BusinessCreateAccountsActivity.this, BusinessDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
//                            } else {
//                                myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
//                                Intent intent = new Intent(BusinessCreateAccountsActivity.this, BusinessDashboardActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                            }


                        } else {
                            myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
                            Log.e(TAG, new Gson().toJson(myApplication.getBusinessTrackerResponse()));
                            Intent intent = new Intent(BusinessCreateAccountsActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }


                    } else {

                        Utils.displayAlert(btResp.getError().getErrorDescription(), BusinessCreateAccountsActivity.this, "", "");

                    }

                } else {
                    Utils.displayAlert(getString(R.string.something_went_wrong), BusinessCreateAccountsActivity.this, "", "");
                }
            }
        });

        try {
            identityVerificationViewModel.getBusinessAddDBAResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {
                    LogUtils.d(TAG, "addDBAresponse" + identityImageResponse);
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                        startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra("ADDBUSINESS", true)
                                .putExtra("ADDDBA", true));
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BusinessCreateAccountsActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
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
}
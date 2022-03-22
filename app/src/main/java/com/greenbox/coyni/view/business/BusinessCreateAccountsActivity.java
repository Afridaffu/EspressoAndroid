package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.businesswallet.WalletInfo;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BusinessCreateAccountsActivity extends BaseActivity implements BusinessProfileRecyclerAdapter.OnSelectListner {

    private TextView userShortInfoTV, defualtAccountDialogPersonalNameTV,userNameTV, userBalanceTV, businessPersonalAccountNameTv, mTvUserIconText;
    private ImageView imgProfile, accountsCloseIV, mIvUserIcon;
    private LinearLayout llOpenAccount, businessPersonalProfileAccount;
    private MyApplication myApplication;
    private DashboardViewModel dashboardViewModel;
    private ExpandableListView brandsGV;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> dbaList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> dbaAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private BusinessProfileRecyclerAdapter giftCardsAdapter;
    private String personalAccountExist;
    private ImageView businessPersonalProfileTickIcon;
    private LoginViewModel loginViewModel;
    private int childid;
    private String SelectedDBAName;
    private String accountTypeId = "";
    private LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();
    private BusinessProfileRecyclerAdapter listAdapter;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;


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
            brandsGV = findViewById(R.id.recyclerView);
            businessPersonalAccountNameTv = findViewById(R.id.business_personal_account_name);
            mIvUserIcon = findViewById(R.id.profile_img);
            mTvUserIconText = findViewById(R.id.b_imageTextTV);
            businessPersonalProfileAccount = findViewById(R.id.profileLL);
            myApplication = (MyApplication) getApplicationContext();
            businessPersonalProfileTickIcon = findViewById(R.id.tickIcon);
            defualtAccountDialogPersonalNameTV = findViewById(R.id.defualt_account_dialog_personal_name);

        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        loginViewModel = new ViewModelProvider(BusinessCreateAccountsActivity.this).get(LoginViewModel.class);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);


        brandsGV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                businessPersonalProfileTickIcon.setVisibility(View.GONE);
                BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
                LogUtils.d(TAG, "GroupChildClick" + i + "....." + i1 + "....." + l);
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
                LogUtils.d(TAG, "subSetChildClick" + subSet);
                LogUtils.d(TAG, "childid" + childid);
                changeAccount(childid);
                listAdapter.notifyDataSetChanged();
                return true;
            }
        });

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
                LogUtils.d(TAG, "subSetChildClick" + subSet);
                changeAccount(childid);
                listAdapter.notifyDataSetChanged();

            }
        });
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
            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            if (myApplication.getMyProfile().getData().getLastName() != null) {
                String lastName = myApplication.getMyProfile().getData().getFirstName();
                iconText = iconText + lastName.substring(0, 1).toUpperCase();
                username = username + " ";
                username = username + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }
//          userNameTV.setText(getResources().getString(R.string.dba_name, username));
            userNameTV.setText(username);
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

        setUserBalance(myApplication.getWalletResponseData());

//        Double bal = myApplication.getGBTBalance();
//        String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
//        userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));

    }

    private void setUserBalance(WalletResponseData walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getWalletNames();
            LogUtils.d(TAG,"setUserBalance"+walletInfo.toString());
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

    public void initObservers() {

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                if (profilesResponse != null) {
                    Map<String, ArrayList<ProfilesResponse.Profiles>> map = new HashMap<>();
                    filterList = profilesResponse.getData();
                    for (ProfilesResponse.Profiles c : filterList) {
                        if (c.getAccountType().equals(Utils.BUSINESS))  {
                            businessAccountList.add(c);
                            addDetails(String.valueOf(c.getCompanyName()), c.getDbaName(), c.getImage(), c.getId(),c.getAccountStatus());
                        } else {
                            personalAccountList.add(c);
                            for(int i=0;i<personalAccountList.size();i++){
//                                if(personalAccountList.get(i).getId() == Integer.parseInt(accountTypeId)){
//                                    personalAccountList.get(i).setSelected(true);
//                                } else {
                                    personalAccountList.get(i).setSelected(false);

                               // }
                            }
                        }

                    }
                    if (businessAccountList.size() != 0) {
                        try {
                            brandsGV.setVisibility(View.VISIBLE);
                            LogUtils.d(TAG, "subSet" + subSet);
                            listAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this, subSet, BusinessCreateAccountsActivity.this);
                            brandsGV.setAdapter(listAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                            Glide.with(BusinessCreateAccountsActivity.this)
                                    .load(personalAccountList.get(0).getImage())
                                    .placeholder(R.drawable.ic_profile_male_user)
                                    .into(mIvUserIcon);
                        } else {
                            mTvUserIconText.setVisibility(View.VISIBLE);
                            mIvUserIcon.setVisibility(View.GONE);
                            mTvUserIconText.setText(iconText);
                        }
                    } else {
                        personalAccountExist = "false";
                        businessPersonalProfileAccount.setVisibility(View.GONE);
                    }
                }
            }
        });

        businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
            @Override
            public void onChanged(BusinessTrackerResponse businessTrackerResponse) {
                if (businessTrackerResponse != null) {
                    if (businessTrackerResponse.getStatus().toLowerCase().toString().equals("success")) {
                        myApplication.setBusinessTrackerResponse(businessTrackerResponse);

                    }
                }
            }
        });

        loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
            @Override
            public void onChanged(AddBusinessUserResponse btResp) {

                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d(TAG, "btResp" + btResp.getData().getAccountType());
                            LogUtils.d(TAG, "btResp" + btResp.getData().getAccountStatus());
                            LogUtils.d(TAG, "btResp" + btResp.getData().getDbaOwnerId());

                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            businessIdentityVerificationViewModel.getBusinessTracker();

                            if (btResp.getData().getAccountType() == Utils.BUSINESS_ACCOUNT) {
                                if(btResp.getData().getAccountStatus().equalsIgnoreCase("active")){
                                     myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
                                    Intent intent = new Intent(BusinessCreateAccountsActivity.this, BusinessDashboardActivity.class);
                                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     startActivity(intent);
                                 } else {
                                    myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
                                    Log.e("Tracker resp", new Gson().toJson(myApplication.getBusinessTrackerResponse()));
                                    Intent intent = new Intent(BusinessCreateAccountsActivity.this, BusinessDashboardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                 }


                            } else {
                                Intent i = new Intent(BusinessCreateAccountsActivity.this, DashboardActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }

                        }
                    }

            }
        });
    }

    private int addDetails(String mainSet, String subSet, String image, int id , String accountStatus) {

        int groupPosition = 0;
        try {
            LogUtils.d("ADDDETAILS", "adddetails" + mainSet + subSet + id + accountTypeId);
            groupPosition = 0;
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
            detailInfo.setAccountSttaus(accountStatus);

//            if (detailInfo.getId() == Integer.parseInt(accountTypeId)) {
//                detailInfo.setIsSelected(true);
//                business_defaultaccountET.setText(subSet);
//
//            } else {
                detailInfo.setIsSelected(false);
           // }

            subList.add(detailInfo);
            headerInfo.setSubsetName(subList);
            groupPosition = this.subSet.indexOf(headerInfo);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return groupPosition;
    }

//    private int addDetails(String mainSet, String subSet) {
//        int groupPosition = 0;
//        BusinessAccountsListInfo headerInfo = this.mainSet.get(mainSet);
//        if (headerInfo == null) {
//            headerInfo = new BusinessAccountsListInfo();
//            headerInfo.setName(mainSet);
//            this.mainSet.put(mainSet, headerInfo);
//            this.subSet.add(headerInfo);
//        }
//        ArrayList<BusinessAccountDbaInfo> subList = headerInfo.getSubsetName();
//        int listSize = subList.size();
//        listSize++;
//        BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
//        detailInfo.setName(subSet);
//        subList.add(detailInfo);
//        headerInfo.setSubsetName(subList);
//        groupPosition = this.subSet.indexOf(headerInfo);
//        return groupPosition;
//    }

    @Override
    public void selectedItem(int id) {

        LogUtils.d("switching", "accounttttt" + id);
//        UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
//        userPreferenceModel.setLocalCurrency(0);
//        userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
//        userPreferenceModel.setPreferredAccount(childid);
//        customerProfileViewModel.updatePreferences(userPreferenceModel);
//        dialog.dismiss();

    }
}
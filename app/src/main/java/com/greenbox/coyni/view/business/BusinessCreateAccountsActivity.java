package com.greenbox.coyni.view.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LongSparseArray;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.adapters.GiftCardsRecyclerAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BindingLayoutActivity;
import com.greenbox.coyni.view.CreatePasswordActivity;
import com.greenbox.coyni.view.GiftCardActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.view.PreferencesActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BusinessCreateAccountsActivity extends BaseActivity {

    private TextView userShortInfoTV, userNameTV, userBalanceTV,businessPersonalAccountNameTv,mTvUserIconText;
    private ImageView imgProfile, accountsCloseIV,mIvUserIcon;
    private LinearLayout llOpenAccount,businessPersonalProfileAccount;
    private MyApplication myApplication;
    private DashboardViewModel dashboardViewModel;
    private ExpandableListView brandsGV;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> dbaAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private BusinessProfileRecyclerAdapter giftCardsAdapter;
    private String personalAccountExist;


    private LinkedHashMap<String, BusinessAccountsListInfo> mainSet = new LinkedHashMap<String, BusinessAccountsListInfo>();
    private ArrayList<BusinessAccountsListInfo> subSet = new ArrayList<BusinessAccountsListInfo>();


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

        } catch (Exception e) {
            e.printStackTrace();
        }
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
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
            userNameTV.setText(getResources().getString(R.string.dba_name, username));
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

        setUserBalance(myApplication.getWalletResponse());

//        Double bal = myApplication.getGBTBalance();
//        String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
//        userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));

    }

    private void setUserBalance(WalletResponse walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getData().getWalletInfo();
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
                        myApplication.setGbtWallet(walletInfo.get(i));
                        strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getExchangeAmount()));
                        userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                        myApplication.setGBTBalance(walletInfo.get(i).getExchangeAmount());
                    }
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

                    for(ProfilesResponse.Profiles c: filterList){
                        if(c.getAccountType().equals(Utils.BUSINESS)){
                                businessAccountList.add(c);

                                addDetails(String.valueOf(c.getId()),c.getDbaOwner());
                        } else {
                            personalAccountList.add(c);
                        }
                    }

//                    try {
//
//                        for(ProfilesResponse.Profiles c: filterList){
//                            if(c.getAccountType().equals(Utils.BUSINESS)){
//                                businessAccountList.add(c);
//                            } else {
//                                personalAccountList.add(c);
//                            }
//                        }
//
//
//                        for(ProfilesResponse.Profiles d: businessAccountList) {
//                            if(d.getDbaOwner() == null) {
//                                map.put(d.getId()+"", null);
//                            } else {
//
//                                ArrayList<ProfilesResponse.Profiles> list = map.get(d.getDbaOwner()+"");
//                                if(list == null) {
//                                    list = new ArrayList<>();
//                                }
//                                list.add(d);
//                                map.put(d.getDbaOwner()+"", list);
//                            }
//
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    LogUtils.v(TAG, map.toString());

                    if(businessAccountList.size()!=0) {
                        brandsGV.setVisibility(View.VISIBLE);
                        LogUtils.d(TAG,"subSet"+subSet);
                        BusinessProfileRecyclerAdapter listAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this, subSet);
                        brandsGV.setAdapter(listAdapter);
                    } else {
                        brandsGV.setVisibility(View.GONE);
                    }

                    if(personalAccountList.size()!=0) {
                        businessPersonalProfileAccount.setVisibility(View.VISIBLE);
                         personalAccountExist = "true";
                        String iconText = "";
                        if (personalAccountList.get(0).getCompanyName() != null
                                ) {
                            String firstName = personalAccountList.get(0).getCompanyName();
                            iconText = firstName.substring(0, 1).toUpperCase();
                            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();

                        }
                        if (personalAccountList.get(0).getImage()!= null) {
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
                        businessPersonalAccountNameTv.setText(personalAccountList.get(0).getFullName());
                    } else {
                        personalAccountExist = "false";
                        businessPersonalProfileAccount.setVisibility(View.GONE);
                    }
                }
            }
        });


    }
    private int addDetails(String mainSet, String subSet) {
        int groupPosition = 0;
        BusinessAccountsListInfo headerInfo = this.mainSet.get(mainSet);
        if (headerInfo == null) {
            headerInfo = new BusinessAccountsListInfo();
            headerInfo.setName(mainSet);
            this.mainSet.put(mainSet, headerInfo);
            this.subSet.add(headerInfo);
        }
        ArrayList<BusinessAccountDbaInfo> subList = headerInfo.getSubsetName();
        int listSize = subList.size();
        listSize++;
        BusinessAccountDbaInfo detailInfo = new BusinessAccountDbaInfo();
        detailInfo.setName(subSet);
        subList.add(detailInfo);
        headerInfo.setSubsetName(subList);
        groupPosition = this.subSet.indexOf(headerInfo);
        return groupPosition;
    }

}
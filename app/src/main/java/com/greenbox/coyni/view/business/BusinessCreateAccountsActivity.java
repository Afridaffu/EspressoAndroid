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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.adapters.GiftCardsRecyclerAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.GiftCardActivity;
import com.greenbox.coyni.view.PreferencesActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessCreateAccountsActivity extends BaseActivity {

    private TextView userShortInfoTV, userNameTV, userBalanceTV,businessPersonalAccountNameTv,mTvUserIconText;
    private ImageView imgProfile, accountsCloseIV,mIvUserIcon;
    private LinearLayout llOpenAccount,businessPersonalProfileAccount;
    private MyApplication myApplication;
    private DashboardViewModel dashboardViewModel;
    private ExpandableListView brandsGV;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private BusinessProfileRecyclerAdapter giftCardsAdapter;


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

        Log.d("getwallet", "getwallet" + myApplication.getWalletResponse());
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
        Double bal = myApplication.getGBTBalance();
        String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
        userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));

    }

    public void initObservers() {

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                if (profilesResponse != null) {
                    filterList = profilesResponse.getData();

                    for(ProfilesResponse.Profiles c: filterList){
                        if(c.getAccountType().equals(Utils.BUSINESS)){
                                businessAccountList.add(c);
                        } else {
                            personalAccountList.add(c);
                        }

                    }

                    if(businessAccountList.size()!=0) {
                        brandsGV.setVisibility(View.VISIBLE);
                        BusinessProfileRecyclerAdapter listAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this, businessAccountList);
                        brandsGV.setAdapter(listAdapter);
                    } else {
                        brandsGV.setVisibility(View.GONE);
                    }

                    if(personalAccountList.size()!=0) {
                        businessPersonalProfileAccount.setVisibility(View.VISIBLE);
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
                        businessPersonalAccountNameTv.setText(personalAccountList.get(0).getCompanyName());
                    } else {
                        businessPersonalProfileAccount.setVisibility(View.GONE);
                    }
                }
            }
        });


    }

}
package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.greenbox.coyni.utils.DisplayImageUtility;
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
import java.util.List;

public class BusinessCreateAccountsActivity extends BaseActivity {

    private TextView userShortInfoTV, defaultAccountDialogPersonalNameTV, userNameTV,
            userBalanceTV, mTvUserIconText;
    private ImageView imgProfile, accountsCloseIV, mIvUserIcon;
    private LinearLayout llOpenAccount;
    private MyApplication myApplication;
    private DashboardViewModel dashboardViewModel;
    private ExpandableListView profilesListView;
    private List<ProfilesResponse.Profiles> profilesList = new ArrayList<>();
    private ImageView businessPersonalProfileTickIcon;
    private LoginViewModel loginViewModel;
    private BusinessProfileRecyclerAdapter profilesListAdapter;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private String userName;
    private Long mLastClickTimeQA = 0L;

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
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    finish();
                }
            });

            llOpenAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessAddNewAccountActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            defaultAccountDialogPersonalNameTV = findViewById(R.id.defualt_account_dialog_personal_name);

        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        loginViewModel = new ViewModelProvider(BusinessCreateAccountsActivity.this).get(LoginViewModel.class);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

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

            DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
            utility.addImage(myApplication.getMyProfile().getData().getImage(), imgProfile, R.drawable.ic_profile_male_user);
        } else {
            userShortInfoTV.setVisibility(View.VISIBLE);
            imgProfile.setVisibility(View.GONE);
            userShortInfoTV.setText(iconText);
        }

//        setUserBalance(myApplication.getWalletResponseData());
        setUserBalance(myApplication.getCurrentUserData().getMerchantWalletResponse());

    }

    private void setUserBalance(WalletResponseData walletResponse) {
        try {
            String strAmount = "";
            if (walletResponse == null) {
                return;
            }
            List<WalletInfo> walletInfo = walletResponse.getWalletNames();
            LogUtils.d(TAG, "setUserBalance" + walletInfo.toString());
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
//                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
//                    myApplication.setGbtWallet(walletInfo.get(i));
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getAvailabilityToUse()));
                    userBalanceTV.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
//                    myApplication.setGBTBalance(walletInfo.get(i).getExchangeAmount());
//                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setProfilesAdapter() {
        boolean showDBA = true;
        AccountsData accountsData = new AccountsData(profilesList);
        profilesListView.setVisibility(View.VISIBLE);
        profilesListAdapter = new BusinessProfileRecyclerAdapter(BusinessCreateAccountsActivity.this,
                accountsData, myApplication.getLoginUserId(), showDBA);

        profilesListAdapter.setOnItemClickListener(new BusinessProfileRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onGroupClicked(int position, String accountType, Integer id, String fullname) {
                LogUtils.v(TAG, "account type " + accountType + "    id: " + id);
                changeAccount(id);
            }

            @Override
            public void onChildClicked(ProfilesResponse.Profiles detailInfo) {
                LogUtils.v("PreferencesActivity", "account type " + detailInfo + "    id: " + detailInfo.getId());
                changeAccount(detailInfo.getId());
            }

            @Override
            public void onAddDbaClicked(String accountType, Integer id) {
                LogUtils.v(TAG, "account type " + accountType + "    id: " + id);
                addDBA(id);
            }
        });
        profilesListView.setAdapter(profilesListAdapter);
        setInitialListViewHeight(profilesListView);
        profilesListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ImageView arrowImg = v.findViewById(R.id.arrowImg);
                if(parent != null && parent.isGroupExpanded(groupPosition)) {
                    arrowImg.setImageResource(R.drawable.ic_chevron_down);
                } else {
                    arrowImg.setImageResource(R.drawable.ic_chevron_up);
                }
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
    }

    public void addDBA(int companyId) {
        LogUtils.d(TAG, "addDBA" + companyId);
        if (companyId != 0) {
            identityVerificationViewModel.getPostAddDBABusiness(companyId);
        } else {

        }
    }

    public void initObservers() {

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                if (profilesResponse != null) {
                    profilesList = profilesResponse.getData();
                    setProfilesAdapter();
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
                            myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
                            Intent intent = new Intent(BusinessCreateAccountsActivity.this, BusinessDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
                    LogUtils.d(TAG, "AddBusinessUserResponse " + identityImageResponse);
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                        startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra(Utils.ADD_BUSINESS, true)
                                .putExtra(Utils.ADD_DBA, true));
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BusinessCreateAccountsActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getBusinessAddDBAResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {
                    LogUtils.d(TAG, "AddBusinessUserResponse " + identityImageResponse);
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                        startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra(Utils.ADD_BUSINESS, true)
                                .putExtra(Utils.ADD_DBA, true));
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
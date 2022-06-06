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
    private String userName = "", firstName = "", lastName = "";
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
                    openNewAccount();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            showProgressDialog();
            dashboardViewModel.getProfiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        showProgressDialog();
        loginViewModel.postChangeAccount(childID);
    }

    private void showUserData() {
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null &&
                myApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
            if (myApplication.getMyProfile().getData().getFirstName() != null) {
                firstName = myApplication.getMyProfile().getData().getFirstName();
//            iconText = firstName.substring(0, 1).toUpperCase();
                userName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            }
            if (myApplication.getMyProfile().getData().getLastName() != null) {
                lastName = myApplication.getMyProfile().getData().getLastName();
//                iconText = iconText + lastName.substring(0, 1).toUpperCase();
                userName = userName + " ";
                userName = userName + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }
            userNameTV.setText(getResources().getString(R.string.dba_name, userName));

            if (userName != null && userName.length() > 18) {
                userNameTV.setText(Utils.getCapsSentences(userName).substring(0, 18) + " ");
            } else {
                userNameTV.setText(Utils.getCapsSentences(userName));
            }
            if (firstName != null && !firstName.equals("") && lastName != null && !lastName.equals("")) {
                char first = firstName.charAt(0);
                char last = lastName.charAt(0);
                String imageName = String.valueOf(first).toUpperCase() + String.valueOf(last).toUpperCase();
                userShortInfoTV.setText(imageName);
                userShortInfoTV.setVisibility(View.VISIBLE);
                imgProfile.setVisibility(View.GONE);
            }
        } else if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null) {
            userName = myApplication.getMyProfile().getData().getDbaName();
            if (userName != null && userName.length() > 18) {
                userNameTV.setText(Utils.getCapsSentences(userName).substring(0, 18) + " ");
            } else if (userName != null) {
                userNameTV.setText(Utils.getCapsSentences(userName));
            }
            imgProfile.setVisibility(View.VISIBLE);
            if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                    && myApplication.getMyProfile().getData().getImage() != null) {
                userShortInfoTV.setVisibility(View.GONE);
                String imageUrl = myApplication.getMyProfile().getData().getImage().trim();
                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(imageUrl, imgProfile, R.drawable.acct_profile);
            } else {
                imgProfile.setImageResource(R.drawable.acct_profile);
            }
        }
        if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null) {
                if (myApplication.getMyProfile().getData().getFirstName() != null) {
                    firstName = myApplication.getMyProfile().getData().getFirstName();
                    userName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                }
                if (myApplication.getMyProfile().getData().getLastName() != null) {
                    lastName = myApplication.getMyProfile().getData().getLastName();
                    userName = userName + " ";
                    userName = userName + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                }
                if (userName != null && userName.length() > 21) {
                    userNameTV.setText( Utils.getCapsSentences(userName).substring(0, 21) + " ");
                } else {
                    userNameTV.setText(Utils.getCapsSentences(userName));
                }
                imgProfile.setVisibility(View.VISIBLE);
                if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                        && myApplication.getMyProfile().getData().getImage() != null) {
                    userShortInfoTV.setVisibility(View.GONE);
                    String imageUrl = myApplication.getMyProfile().getData().getImage().trim();
                    DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                    utility.addImage(imageUrl, imgProfile, R.drawable.ic_profile);
                } else {
                    userShortInfoTV.setVisibility(View.VISIBLE);
                    imgProfile.setVisibility(View.GONE);
                    String userName = firstName.substring(0, 1).toUpperCase() + lastName.substring(0,1).toUpperCase();
                    userShortInfoTV.setText(userName);
                }
            }
        }


        userNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameTV.getText().toString().contains("...")) {
                    if (userName.length() == 18 || userName.length() > 18) {
                        userNameTV.setText(Utils.getCapsSentences(userName).substring(0, 18));
                    } else {
                        userNameTV.setText(Utils.getCapsSentences(userName));
                    }
                } else {
                    if (userName.length() == 18) {
                        userNameTV.setText(Utils.getCapsSentences(userName).substring(0, 17) + "...");
                    } else if (userName.length() > 18) {
                        userNameTV.setText(Utils.getCapsSentences(userName).substring(0, 18) + "...");
                    } else {
                        userNameTV.setText(Utils.getCapsSentences(userName));
                    }
                }
            }
        });

//        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
//                && myApplication.getMyProfile().getData().getImage() != null) {
//            userShortInfoTV.setVisibility(View.GONE);
//            imgProfile.setVisibility(View.VISIBLE);
//
//            DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
//            utility.addImage(myApplication.getMyProfile().getData().getImage(), imgProfile, R.drawable.ic_profile_male_user);
//        } else {
//            userShortInfoTV.setVisibility(View.VISIBLE);
//            imgProfile.setVisibility(View.GONE);
////            userShortInfoTV.setText(iconText);
//        }

//        setUserBalance(myApplication.getWalletResponseData());
        if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT)
            setUserBalance(myApplication.getCurrentUserData().getTokenWalletResponse());
        else
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
            } else {
                userBalanceTV.setText("0.00");
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
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                changeAccount(id);
            }

            @Override
            public void onChildClicked(ProfilesResponse.Profiles detailInfo) {
                LogUtils.v("PreferencesActivity", "account type " + detailInfo + "    id: " + detailInfo.getId());
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                changeAccount(detailInfo.getId());
            }

            @Override
            public void onAddDbaClicked(String accountType, Integer id) {
//                LogUtils.v(TAG, "account type " + accountType + "    id: " + id);
//                addDBA(id);
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                openNewAccount();
            }
        });
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
    }

    public void addDBA(int companyId) {
        LogUtils.d(TAG, "addDBA" + companyId);
        if (companyId != 0) {
            showProgressDialog();
            identityVerificationViewModel.getPostAddDBABusiness(companyId);
        } else {

        }
    }

    public void initObservers() {

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                dismissDialog();
                if (profilesResponse != null) {
                    profilesList = profilesResponse.getData();
                    setProfilesAdapter();
                }
            }
        });

        loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
            @Override
            public void onChanged(AddBusinessUserResponse btResp) {
                dismissDialog();
                if (btResp != null) {
                    if (btResp.getStatus().toLowerCase().equals("success")) {

                        LogUtils.d(TAG, "btResp" + btResp.getData().getAccountType());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getAccountStatus());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getDbaOwnerId());
                        myApplication.clearUserData();
                        Utils.setStrAuth(btResp.getData().getJwtToken());
                        myApplication.setLoginUserId(btResp.getData().getUserId());
                        myApplication.setStrEmail(btResp.getData().getEmail());
                        myApplication.setAccountType(btResp.getData().getAccountType());
                        myApplication.setIsReserveEnabled(btResp.getData().isReserveEnabled());

                        if (btResp.getData().getAccountType() == Utils.BUSINESS_ACCOUNT || btResp.getData().getAccountType() == Utils.SHARED_ACCOUNT) {
                            myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
                            Intent intent = new Intent(BusinessCreateAccountsActivity.this, BusinessDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            myApplication.setDbaOwnerId(btResp.getData().getDbaOwnerId());
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
                    dismissDialog();
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

    public void openNewAccount(){
        Intent inNewAccount = new Intent(BusinessCreateAccountsActivity.this, BusinessAddNewAccountActivity.class);
        for (ProfilesResponse.Profiles profile : profilesList) {
            if (profile.getAccountType().equals(Utils.PERSONAL)) {
                inNewAccount.putExtra("PersonalAccount", "true");
                break;
            }
        }
        startActivity(inNewAccount);
    }
}
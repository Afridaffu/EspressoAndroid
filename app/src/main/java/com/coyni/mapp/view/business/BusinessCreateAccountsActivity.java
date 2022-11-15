package com.coyni.mapp.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.AddNewBusinessAccountDBAAdapter;
import com.coyni.mapp.adapters.BusinessProfileRecyclerAdapter;
import com.coyni.mapp.dialogs.OnAgreementsAPIListener;
import com.coyni.mapp.model.AccountsData;
import com.coyni.mapp.model.FilteredAgreements;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.businesswallet.WalletResponseData;
import com.coyni.mapp.model.preferences.BaseProfile;
import com.coyni.mapp.model.preferences.ProfilesResponse;
import com.coyni.mapp.model.profile.AddBusinessUserResponse;
import com.coyni.mapp.model.profile.Profile;
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.view.DashboardActivity;
import com.coyni.mapp.view.OnboardActivity;
import com.coyni.mapp.view.PINActivity;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.IdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

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

            setOnAgreementsAPIListener(new OnAgreementsAPIListener() {
                @Override
                public void onAgreementsAPIResponse(SignAgreementsResp signAgreementsResp, boolean isMerchantHide) {
                    dismissDialog();
                    if (isMerchantHide) {
                        FilteredAgreements filteredAgreements = Utils.getFilteredAgreements(signAgreementsResp.getData());
                        if (filteredAgreements.getAgreements().size() > 0) {
                            myApplication.setHasToSignAgreements(filteredAgreements.getAgreements());
                            Utils.launchAgreements(BusinessCreateAccountsActivity.this, isMerchantHide);
                        } else {
                            launchDasboardFromBase();
                        }
                    } else {
                        if (signAgreementsResp.getData().size() > 0) {
                            myApplication.setHasToSignAgreements(signAgreementsResp.getData());
                            Utils.launchAgreements(BusinessCreateAccountsActivity.this, isMerchantHide);
                        } else {
                            launchDasboardFromBase();
                        }
                    }
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
            dashboardViewModel.meProfile();
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

            if (userName != null && userName.length() > 20) {
                userNameTV.setText((userName).substring(0, 20) + " ");
            } else {
                userNameTV.setText((userName));
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
            if (userName != null && userName.length() > 20) {
                userNameTV.setText((userName).substring(0, 20) + " ");
            } else if (userName != null) {
                userNameTV.setText((userName));
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
                if (userName != null && userName.length() > 20) {
                    userNameTV.setText((userName).substring(0, 20) + " ");
                } else {
                    userNameTV.setText(userName);
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
                    String userName = firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
                    userShortInfoTV.setText(userName);
                }
            }
        }


        userNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameTV.getText().toString().contains("...")) {
                    if (userName.length() == 21 || userName.length() > 21) {
                        userNameTV.setText((userName).substring(0, 20));
                    } else {
                        userNameTV.setText(userName);
                    }
                } else {
                    if (userName.length() == 21) {
                        userNameTV.setText((userName).substring(0, 20) + "...");
                    } else if (userName.length() > 21) {
                        userNameTV.setText((userName).substring(0, 21) + "...");
                    } else {
                        userNameTV.setText(userName);
                    }
                }
            }
        });

        if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            setUserBalance(myApplication.getCurrentUserData().getTokenWalletResponse());
        } else {
            setUserBalance(myApplication.getCurrentUserData().getTokenWalletResponse());
        }
    }

    private void setUserBalance(WalletInfo walletResponse) {
        try {
            String strAmount = "";
            if (walletResponse == null) {
                userBalanceTV.setText("0.00");
                return;
            }
            LogUtils.d(TAG, "setUserBalance" + walletResponse.toString());
            strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletResponse.getAvailabilityToUse()));
            userBalanceTV.setText(strAmount);
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

//            @Override
//            public void onAddDbaClicked(String accountType, Integer id) {
//                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
//                    return;
//                }
//                mLastClickTimeQA = SystemClock.elapsedRealtime();
//                addDBA(id);
//            }


            @Override
//            public void onAddDbaClicked(ProfilesResponse.Profiles profiles, Integer id) {
            public void onAddDbaClicked(BaseProfile profiles, Integer id) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                displayDBAAlert(profiles, id);
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
//            showProgressDialog();
//            identityVerificationViewModel.getPostAddDBABusiness(companyId);
            Intent inAddDba = new Intent(BusinessCreateAccountsActivity.this, BusinessRegistrationTrackerActivity.class);
            inAddDba.putExtra(Utils.ADD_BUSINESS, true);
            inAddDba.putExtra(Utils.ADD_DBA, true);
            inAddDba.putExtra(Utils.NEW_DBA, true);
            inAddDba.putExtra(Utils.COMPANY_ID, companyId);
            startActivity(inAddDba);
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

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile.getStatus().equalsIgnoreCase("SUCCESS")) {
                    try {
                        myApplication.setMyProfile(profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        loginViewModel.postChangeAccountResponse().observe(this, new Observer<BiometricSignIn>() {
            @Override
            public void onChanged(BiometricSignIn btResp) {
                dismissDialog();
                if (btResp != null) {
                    if (btResp.getStatus().toLowerCase().equals("success")) {
                        if (btResp.getData() != null)
                            myApplication.setCompanyName(btResp.getData().getCompanyName());

                        myApplication.setAgreementSigned(btResp.getData().getTracker().isIsAgreementSigned());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getAccountType());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getAccountStatus());
                        LogUtils.d(TAG, "btResp" + btResp.getData().getDbaOwnerId());
                        myApplication.clearUserData();
                        if (btResp.getData() != null) {
                            myApplication.setBusinessUserID(String.valueOf(btResp.getData().getBusinessUserId()));
                            myApplication.setOwnerImage(btResp.getData().getImage());
                        }
                        Utils.setStrAuth(btResp.getData().getJwtToken());
                        myApplication.setLoginUserId(Integer.parseInt(String.valueOf(btResp.getData().getUserId())));
                        myApplication.setStrEmail(btResp.getData().getEmail());
                        myApplication.setAccountType(btResp.getData().getAccountType());
                        myApplication.setDbaOwnerId(Integer.parseInt(String.valueOf(btResp.getData().getDbaOwnerId())));
                        myApplication.setIsReserveEnabled(btResp.getData().isReserveEnabled());
                        myApplication.setIsLoggedIn(true);
                        myApplication.setLoginResponse(btResp);
                        if (!btResp.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())
                                && !btResp.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                                && !btResp.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
//                            !btResp.getData().getTracker().isIsAgreementSigned() &&
                            if (btResp.getData().getAccountType() == Utils.SHARED_ACCOUNT) {
                                if (btResp.getData().getOwnerDetails() != null && !btResp.getData().getOwnerDetails().getTracker().isIsAgreementSigned()) {
//                                    //-------------------------------------------------------------------
//                                    if (btResp.getData().getOwnerDetails().getBusinessTracker() == null || btResp.getData().getOwnerDetails().getBusinessTracker().isIsAgreementSigned())
//                                        callHasToSignAPI(false);
//                                    else if (!btResp.getData().getOwnerDetails().getBusinessTracker().isIsAgreementSigned()) {
//                                        callHasToSignAPI(true);
//                                    }
//                                    //-------------------------------------------------------------------

                                    //Comment Above section and uncomment below line to avoid merchant agreement for shared account
                                    callHasToSignAPI(true);
                                } else {
                                    launchDasboardFromBase();
                                }
                            } else {
                                if (!btResp.getData().getTracker().isIsAgreementSigned()) {
//                                    if (btResp.getData().getBusinessTracker() == null || btResp.getData().getBusinessTracker().isIsAgreementSigned())
//                                        callHasToSignAPI(false);
//                                    else if (!btResp.getData().getBusinessTracker().isIsAgreementSigned()) {
//                                        callHasToSignAPI(true);
//                                    }
                                    if (btResp.getData().getBusinessTracker() == null || !btResp.getData().getBusinessTracker().isIsAgreementSigned())
                                        callHasToSignAPI(true);
                                    else if (btResp.getData().getBusinessTracker().isIsAgreementSigned()) {
                                        callHasToSignAPI(false);
                                    }
                                } else {
                                    launchDasboardFromBase();
                                }
                            }
                        } else {
                            if (btResp.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                                myApplication.launchDeclinedActivity(BusinessCreateAccountsActivity.this);
                            } else {
                                launchDasboardFromBase();
                            }
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
//                        startActivity(new Intent(BusinessCreateAccountsActivity.this, BusinessRegistrationTrackerActivity.class)
//                                .putExtra(Utils.ADD_BUSINESS, true)
//                                .putExtra(Utils.ADD_DBA, true));
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

    public void openNewAccount() {
//        Intent inNewAccount = new Intent(BusinessCreateAccountsActivity.this, BusinessAddNewAccountActivity.class);
        Intent inNewAccount = new Intent(BusinessCreateAccountsActivity.this, AddNewAccountActivity.class);
        for (ProfilesResponse.Profiles profile : profilesList) {
            if (profile.getAccountType().equals(Utils.PERSONAL)) {
                inNewAccount.putExtra("PersonalAccount", "true");
                break;
            }
        }
        //showProgressDialog();
        startActivity(inNewAccount);
    }

    //    private void displayDBAAlert(ProfilesResponse.Profiles profiles, Integer id) {
    private void displayDBAAlert(BaseProfile profiles, Integer id) {
        try {
            // custom dialog
            final Dialog dialog = new Dialog(BusinessCreateAccountsActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_new_dba_account_alert_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView tvCompany = dialog.findViewById(R.id.tvCompany);
            TextView tvDBACount = dialog.findViewById(R.id.tvDBACount);
            CardView addDBACardView = dialog.findViewById(R.id.cvAction);

            tvCompany.setText(profiles.getCompanyName());
            AccountsData accountsData = new AccountsData(profilesList);
            ArrayList<ProfilesResponse.Profiles> dBAList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(profiles.getId());
            if (dBAList != null) {
                tvDBACount.setText("Total DBAs : " + dBAList.size());
            }

            addDBACardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDBA(id);
                    dialog.dismiss();

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
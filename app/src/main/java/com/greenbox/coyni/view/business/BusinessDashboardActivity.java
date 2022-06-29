package com.greenbox.coyni.view.business;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.UnderReviewErrorMsgDialog;
import com.greenbox.coyni.fragments.ActionRequiredFragment;
import com.greenbox.coyni.fragments.BaseFragment;
import com.greenbox.coyni.fragments.BusinessAccountFragment;
import com.greenbox.coyni.fragments.BusinessDashboardFragment;
import com.greenbox.coyni.fragments.GetStartedFragment;
import com.greenbox.coyni.fragments.UnderReviewFragment;
import com.greenbox.coyni.fragments.VerificationFailedFragment;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessReceivePaymentActivity;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.ScanActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.NotificationsViewModel;

import java.util.List;

//Business dashboard activity created
public class BusinessDashboardActivity extends BaseActivity {
    private BusinessDashboardViewModel businessDashboardViewModel;
    private CustomerProfileViewModel customerProfileViewModel;
    private NotificationsViewModel notificationsViewModel;
    private MyApplication objMyApplication;
    private Tabs selectedTab = Tabs.DASHBOARD;
    private ImageView mIvDashboard, mIvAccount, mIvTransactions, mIvProfile, mIvMenu, mIvUserIcon;
    private TextView mTvDashboard, mTvAccount, mTvTransactions, mTvProfile, countTV, mTvUserName, mTvUserIconText;
    private String userName = "", firstName = "", lastName = "";
    private CardView countCV;
    private RelativeLayout notificationsRL, mUserIconRelativeLayout;

    private enum Tabs {DASHBOARD, ACCOUNT, TRANSACTIONS, PROFILE}

    private DashboardViewModel mDashboardViewModel;
    private BaseFragment mCurrentFragment;
    Long mLastClickTimeQA = 0L;
    private boolean isTabsEnabled = false;
    int notificationCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_business_dashboard);
            initialization();
            initObserver();
            enableDisableTabView();
            removeFragment();
            showProgressDialog();
            mDashboardViewModel.meProfile();
            firebaseToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDashboardTabSelected(View view) {
        try {
            if (selectedTab != Tabs.DASHBOARD) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                selectedTab = Tabs.DASHBOARD;
                setSelectedTab(true, false, false, false);
                LogUtils.d(TAG, "onDashboardTabSelected");
                checkLoadFragment(objMyApplication.getMyProfile());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onAccountTabSelected(View view) {
        int selectedTextColor = getColor(R.color.primary_green);
        int unSelectedTextColor = getColor(R.color.light_gray);
        if (objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus()) ||
                objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())) {
            mTvAccount.setClickable(false);
            mTvAccount.setTextColor(isTabsEnabled ? selectedTextColor : unSelectedTextColor);
            mIvAccount.setImageResource(isTabsEnabled ? R.drawable.ic_account_active : R.drawable.ic_account_disabled);
        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            if (!isTabsEnabled) {
                UnderReviewErrorMsgDialog reviewErrorMsgDialog = new UnderReviewErrorMsgDialog(this);
                reviewErrorMsgDialog.show();
            } else {
                try {
                    if (selectedTab != Tabs.ACCOUNT) {
                        selectedTab = Tabs.ACCOUNT;
                        setSelectedTab(false, true, false, false);
                        LogUtils.d(TAG, "onAccountTabSelected");
                        pushFragment(new BusinessAccountFragment());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void onTransactionsTabSelected(View view) {
        int selectedTextColor = getColor(R.color.primary_green);
        int disabledColor = getColor(R.color.light_gray);
        if (objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus()) ||
                objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())) {
            mIvTransactions.setClickable(false);
            mTvTransactions.setTextColor(isTabsEnabled ? selectedTextColor : disabledColor);
            mIvTransactions.setImageResource(isTabsEnabled ? R.drawable.ic_transactions_active : R.drawable.ic_transactions_disabled);
        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            if (!isTabsEnabled) {
                UnderReviewErrorMsgDialog reviewErrorMsgDialog = new UnderReviewErrorMsgDialog(this);
                reviewErrorMsgDialog.show();
            } else {
                try {
                    if (selectedTab != Tabs.TRANSACTIONS) {
                        startActivity(new Intent(BusinessDashboardActivity.this, MerchantTransactionListActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void onProfileTabSelected(View view) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }

            mLastClickTimeQA = SystemClock.elapsedRealtime();
            Intent in = new Intent(BusinessDashboardActivity.this, BusinessProfileActivity.class);
            activityResultLauncher.launch(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onQuickMenuTabSelected(View view) {
        if (objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus()) ||
                objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())) {
            mIvMenu.setClickable(false);
            mIvMenu.setImageResource(isTabsEnabled ? R.drawable.quick_action_btn : R.drawable.quick_action_btn_disabled);
        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            if (!isTabsEnabled) {
                UnderReviewErrorMsgDialog reviewErrorMsgDialog = new UnderReviewErrorMsgDialog(this);
                reviewErrorMsgDialog.show();
            } else {
                try {
                    LogUtils.d(TAG, "onQuickMenuTabSelected");
                    Dialog dialog = new Dialog(BusinessDashboardActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                    dialog.setContentView(R.layout.activity_business_quick_action);
                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    WindowManager.LayoutParams wl = window.getAttributes();
                    wl.gravity = Gravity.BOTTOM;
                    wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wl);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    LinearLayout buyTokenLL = dialog.findViewById(R.id.buy_TokenLL);
                    LinearLayout widthdrawtoLL = dialog.findViewById(R.id.widthdrawtoLL);
                    LinearLayout receivePaymentLL = dialog.findViewById(R.id.receive_PaymentLL);
                    LinearLayout llScan = dialog.findViewById(R.id.llScan);

                    buyTokenLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            launchBuyTokens();
                        }
                    });
                    widthdrawtoLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(BusinessDashboardActivity.this, WithdrawPaymentMethodsActivity.class));
                        }
                    });
                    receivePaymentLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(BusinessDashboardActivity.this, BusinessReceivePaymentActivity.class));
                        }
                    });
                    llScan.setAlpha(0.5f);
                    llScan.setEnabled(false);
                    llScan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dialog.dismiss();
//                            startActivity(new Intent(BusinessDashboardActivity.this, ScanActivity.class));
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void launchBuyTokens() {
        try {
//            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
//                return;
//            }
//            mLastClickTimeQA = SystemClock.elapsedRealtime();
            Intent i = new Intent(BusinessDashboardActivity.this, SelectPaymentMethodActivity.class);
            i.putExtra("screen", "dashboard");
            i.putExtra("menuitem", "buy");
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setSelectedTab(boolean isDashboard, boolean isAccount, boolean isTransactions, boolean isProfile) {
        mIvDashboard.setImageResource(isDashboard ? R.drawable.ic_dashboard_active : R.drawable.ic_dashboard_inactive);
        mIvAccount.setImageResource(isAccount ? R.drawable.ic_account_active : R.drawable.ic_account_inactive);
        mIvTransactions.setImageResource(isTransactions ? R.drawable.ic_transactions_active : R.drawable.ic_transactions_inactive);
        mIvProfile.setImageResource(isProfile ? R.drawable.ic_profile_active : R.drawable.ic_profile_inactive);

        int selectedTextColor = getColor(R.color.primary_green);
        int unSelectedTextColor = getColor(R.color.dark_grey);
        mTvDashboard.setTextColor(isDashboard ? selectedTextColor : unSelectedTextColor);
        mTvAccount.setTextColor(isAccount ? selectedTextColor : unSelectedTextColor);
        mTvTransactions.setTextColor(isTransactions ? selectedTextColor : unSelectedTextColor);
        mTvProfile.setTextColor(isProfile ? selectedTextColor : unSelectedTextColor);
    }

    private void setEnabledTabs() {
        int disabledColor = getColor(R.color.cyn_color);
        int unSelectedTextColor = getColor(R.color.dark_grey);
        int selectedTextColor = getColor(R.color.primary_green);
        if (selectedTab == Tabs.ACCOUNT) {
            mTvAccount.setTextColor(isTabsEnabled ? selectedTextColor : unSelectedTextColor);
            mIvAccount.setImageResource(isTabsEnabled ? R.drawable.ic_account_active : R.drawable.ic_account_inactive);
        } else {
            mTvAccount.setClickable(false);
            mTvAccount.setTextColor(isTabsEnabled ? unSelectedTextColor : disabledColor);
            mIvAccount.setImageResource(isTabsEnabled ? R.drawable.ic_account_inactive : R.drawable.ic_account_disabled);
        }
        if (selectedTab == Tabs.TRANSACTIONS) {
            mTvTransactions.setTextColor(isTabsEnabled ? selectedTextColor : disabledColor);
            mIvTransactions.setImageResource(isTabsEnabled ? R.drawable.ic_transactions_active : R.drawable.ic_transactions_inactive);
        } else {
            mTvTransactions.setClickable(false);
            mTvTransactions.setTextColor(isTabsEnabled ? unSelectedTextColor : disabledColor);
            mIvTransactions.setImageResource(isTabsEnabled ? R.drawable.ic_transactions_inactive : R.drawable.ic_transactions_disabled);
        }
        mIvMenu.setImageResource(isTabsEnabled ? R.drawable.quick_action_btn : R.drawable.quick_action_btn_disabled);
    }

    private void pushFragment(BaseFragment fragment) {
        mCurrentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content_frame, fragment);
        transaction.commit();
    }

    private void removeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            transaction.remove(mCurrentFragment);
            transaction.commit();
            mCurrentFragment = null;
        }
    }

    private void initialization() {
        try {

            // fix for reloading dashboard on new DBA Creation
            try {
                BusinessRegistrationTrackerActivity.isAddBusinessCalled = false;
                BusinessRegistrationTrackerActivity.isAddDbaCalled = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            // fix for reloading dashboard on new DBA Creation

            mIvDashboard = findViewById(R.id.iv_dashboard_icon);
            mIvAccount = findViewById(R.id.iv_account_icon);
            mIvTransactions = findViewById(R.id.iv_transactions_icon);
            mIvProfile = findViewById(R.id.iv_profile_icon);
            mTvDashboard = findViewById(R.id.tv_dashboard_text);
            mTvAccount = findViewById(R.id.tv_account_text);
            mTvTransactions = findViewById(R.id.tv_transactions_text);
            mTvProfile = findViewById(R.id.tv_profile_text);
            mIvMenu = findViewById(R.id.iv_menu_tab);
            objMyApplication = (MyApplication) getApplicationContext();
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            mDashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

            mUserIconRelativeLayout = findViewById(R.id.rl_user_icon_layout);
            mIvUserIcon = findViewById(R.id.iv_user_icon);
            mTvUserName = findViewById(R.id.tv_user_name);
            mTvUserIconText = findViewById(R.id.tv_user_icon_text);
            notificationsRL = findViewById(R.id.notificationsRL);
            countTV = findViewById(R.id.countTV);
            countCV = findViewById(R.id.countCV);

            notificationsRL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                startActivity(new Intent(BusinessDashboardActivity.this, NotificationsActivity.class));
            });

            mUserIconRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchSwitchAccountPage();
                }
            });

            new FetchData(BusinessDashboardActivity.this).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableDisableTabView() {
        isTabsEnabled = false;
        if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                && objMyApplication.getMyProfile().getData().getAccountStatus() != null) {
            String accountStatus = objMyApplication.getMyProfile().getData().getAccountStatus();
            if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                isTabsEnabled = true;
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                isTabsEnabled = false;
            }
        }
        setEnabledTabs();
    }

    public void launchApplicationCancelledScreen() {
        Intent inCancelledApplication = new Intent(BusinessDashboardActivity.this, ApplicationCancelledActivity.class);
        inCancelledApplication.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityResultLauncher.launch(inCancelledApplication);
    }

    public void launchSwitchAccountPage() {
        if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
            return;
        }
        mLastClickTimeQA = SystemClock.elapsedRealtime();
        Intent in = new Intent(BusinessDashboardActivity.this, BusinessCreateAccountsActivity.class);
        startActivity(in);
    }

    public void startTracker(int dbaOwnerId) {
        Intent inTracker = new Intent(BusinessDashboardActivity.this, BusinessRegistrationTrackerActivity.class);
        if (dbaOwnerId != 0) {
            inTracker.putExtra(Utils.ADD_BUSINESS, true);
            inTracker.putExtra(Utils.ADD_DBA, true);
            inTracker.putExtra(Utils.IS_TRACKER, true);
        }
        startActivity(inTracker);
    }

    public void launchAdditionalActionPage() {
        Intent in = new Intent(BusinessDashboardActivity.this, BusinessAdditionalActionRequiredActivity.class);
        activityResultLauncher.launch(in);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    removeFragment();
                    showProgressDialog();
                    mDashboardViewModel.meProfile();
                }
            });

    private void initObserver() {
        businessDashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse paymentMethodsResponse) {
                if (paymentMethodsResponse != null) {
                    objMyApplication.setPaymentMethodsResponse(paymentMethodsResponse);
                }
            }
        });

        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    if (signOn != null) {
                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                            objMyApplication.setSignOnData(signOn.getData());
                            objMyApplication.setStrSignOnError("");
                        } else {
                            objMyApplication.setSignOnData(null);
                            objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mDashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                try {
                    dismissDialog();
                    if (profile != null) {
                        objMyApplication.setMyProfile(profile);
                        if (profile.getData() != null) {
                            if (objMyApplication.getAccountType() != Utils.SHARED_ACCOUNT) {
                                objMyApplication.setIsReserveEnabled(profile.getData().isReserveEnabled());
                            }
                            objMyApplication.setStrUserName(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                        }
                        enableDisableTabView();
                        checkLoadFragment(profile);
                        Utils.setUserEmail(BusinessDashboardActivity.this, profile.getData().getEmail());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mDashboardViewModel.getGetUserLatestTxns().observe(this, new Observer<LatestTxnResponse>() {
            @Override
            public void onChanged(LatestTxnResponse latestTxnResponse) {
                try {
                    if (latestTxnResponse != null) {
                        objMyApplication.setListLatestTxn(latestTxnResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            notificationsViewModel.getNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(Notifications notifications) {
//
                    if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                        notificationCount = 0;
                        for (int i = 0; i < notifications.getData().getItems().size(); i++) {
                            if (!notifications.getData().getItems().get(i).isRead()) {
                                notificationCount++;
                            }
                        }
                        notificationsViewModel.getReceivedNotifications();
                        Log.e("count notif", notificationCount + "");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notificationsViewModel.getReceivedNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(Notifications notifications) {

                    try {
                        if (notifications != null) {
                            if (notifications.getStatus().equalsIgnoreCase("success")) {
                                List<NotificationsDataItems> localData = notifications.getData().getItems();
                                for (int i = 0; i < localData.size(); i++) {
                                    if (localData.get(i).getStatus().equalsIgnoreCase("Requested") ||
                                            localData.get(i).getStatus().equalsIgnoreCase("Remind")) {
                                        notificationCount++;
                                    }
                                }

                                if (notificationCount > 0) {
                                    countCV.setVisibility(View.VISIBLE);
                                    countTV.setText(notificationCount + "");
                                } else {
                                    countCV.setVisibility(View.GONE);
                                }

                                Log.e("count total", notificationCount + "");
                            } else {
                                if (notificationCount > 0) {
                                    countCV.setVisibility(View.VISIBLE);
                                    countTV.setText(notificationCount + "");
                                } else {
                                    countCV.setVisibility(View.GONE);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLoadFragment(Profile profile) {
        if (profile == null || profile.getData() == null || profile.getData().getAccountStatus() == null) {
            return;
        }
        String accountStatus = profile.getData().getAccountStatus();
        if (isTabsEnabled && selectedTab == Tabs.ACCOUNT) {
            pushFragment(new BusinessAccountFragment());
        } else {
            if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
                pushFragment(new GetStartedFragment());
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                pushFragment(new UnderReviewFragment());
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())
                    || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ADDITIONAL_INFO_REQUIRED.getStatus())) {
                pushFragment(new ActionRequiredFragment());
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                    || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
                pushFragment(new VerificationFailedFragment());
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                pushFragment(new VerificationFailedFragment());
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                pushFragment(new BusinessDashboardFragment());
            }
        }

    }

    public void showUserData() {
        String iconText = "";
        if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                && objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
            if (objMyApplication.getMyProfile().getData().getFirstName() != null) {
                firstName = objMyApplication.getMyProfile().getData().getFirstName();
//            iconText = firstName.substring(0, 1).toUpperCase();
                userName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            }
            if (objMyApplication.getMyProfile().getData().getLastName() != null) {
                lastName = objMyApplication.getMyProfile().getData().getLastName();
//                iconText = iconText + lastName.substring(0, 1).toUpperCase();
                userName = userName + " ";
                userName = userName + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }
            mTvUserName.setText(getResources().getString(R.string.dba_name, userName));

            if (userName != null && userName.length() > 21) {
                mTvUserName.setText("Hi! " + Utils.capitalize(userName).substring(0, 21) + " ");
            } else {
                mTvUserName.setText("Hi! " + Utils.capitalize(userName));
            }
            if (firstName != null && !firstName.equals("") && lastName != null && !lastName.equals("")) {
                char first = firstName.charAt(0);
                char last = lastName.charAt(0);
                String imageName = String.valueOf(first).toUpperCase() + String.valueOf(last).toUpperCase();
                mTvUserIconText.setText(imageName);
                mTvUserIconText.setVisibility(View.VISIBLE);
                mIvUserIcon.setVisibility(View.GONE);
            }
        } else if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null) {
            if (objMyApplication.getMyProfile().getData().getDbaName() != null) {
                iconText = objMyApplication.getMyProfile().getData().getDbaName().substring(0, 1).toUpperCase();
            }
            userName = objMyApplication.getMyProfile().getData().getDbaName();
            if (userName != null && userName.length() > 20) {
                mTvUserName.setText("Hi! " + Utils.capitalize(userName).substring(0, 20) + " ");
            } else if (userName != null) {
                mTvUserName.setText("Hi! " + Utils.capitalize(userName));
            }
            mIvUserIcon.setVisibility(View.VISIBLE);
            if (objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                if (objMyApplication.getOwnerImage() != null && !objMyApplication.getOwnerImage().equals("")) {
                    mTvUserIconText.setVisibility(View.GONE);
                    String imageUrl = objMyApplication.getOwnerImage().trim();
                    DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                    utility.addImage(imageUrl, mIvUserIcon, R.drawable.acct_profile);
                } else {
                    mIvUserIcon.setImageResource(R.drawable.acct_profile);
                }
            } else {
                if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                        && objMyApplication.getMyProfile().getData().getImage() != null) {
                    mTvUserIconText.setVisibility(View.GONE);
                    String imageUrl = objMyApplication.getMyProfile().getData().getImage().trim();
                    DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                    utility.addImage(imageUrl, mIvUserIcon, R.drawable.acct_profile);
                } else {
                    mIvUserIcon.setImageResource(R.drawable.acct_profile);
                }
            }
        }

        mTvUserName.setOnClickListener(view -> {
            if (mTvUserName.getText().toString().contains("...")) {
                if (userName.length() == 21 || userName.length() > 21) {
                    mTvUserName.setText("Hi! " + Utils.capitalize(userName).substring(0, 20));
                } else {
                    mTvUserName.setText("Hi! " + Utils.capitalize(userName));
                }
            } else {
                if (userName.length() == 21) {
                    mTvUserName.setText("Hi! " + Utils.capitalize(userName).substring(0, 22) + "...");
                } else if (userName.length() > 22) {
                    mTvUserName.setText("Hi! " + Utils.capitalize(userName).substring(0, 22) + "...");
                } else {
                    mTvUserName.setText("Hi! " + Utils.capitalize(userName));
                }
            }
        });
        notificationsViewModel.getNotifications();
    }

    public class FetchData extends AsyncTask<Void, Void, Boolean> {

        public FetchData(Context context) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                customerProfileViewModel.meSignOn();
                businessDashboardViewModel.meBusinessPaymentMethods();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean list) {
            super.onPostExecute(list);

        }
    }

    private void firebaseToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();

                            // Log and toast
//                            String msg = getString(R.string.msg_token_fmt, token);
//                            Log.d("", msg);
                            //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


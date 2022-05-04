package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.UnderReviewErrorMsgDialog;
import com.greenbox.coyni.fragments.BaseFragment;
import com.greenbox.coyni.fragments.BusinessAccountFragment;
import com.greenbox.coyni.fragments.BusinessDashboardFragment;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletInfo;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessReceivePaymentActivity;
import com.greenbox.coyni.view.ScanActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;

//Business dashboard activity created
public class BusinessDashboardActivity extends BaseActivity {
    private BusinessDashboardViewModel businessDashboardViewModel;
    private CustomerProfileViewModel customerProfileViewModel;
    private MyApplication objMyApplication;
    private Tabs selectedTab = Tabs.DASHBOARD;
    private ImageView mIvDashboard, mIvAccount, mIvTransactions, mIvProfile, mIvMenu;
    private TextView mTvDashboard, mTvAccount, mTvTransactions, mTvProfile;
    private String userName = "";

    private enum Tabs {DASHBOARD, ACCOUNT, TRANSACTIONS, PROFILE}

    private DashboardViewModel mDashboardViewModel;
    private BaseFragment mCurrentFragment;
    Long mLastClickTimeQA = 0L;
    private boolean isTabsEnabled = false;

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
            pushFragment(new BusinessDashboardFragment());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDashboardViewModel.meProfile();
        } catch (Exception e) {
            e.printStackTrace();
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
                pushFragment(new BusinessDashboardFragment());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onAccountTabSelected(View view) {
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

    public void onTransactionsTabSelected(View view) {
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

    public void onProfileTabSelected(View view) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }

            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startActivity(new Intent(BusinessDashboardActivity.this, BusinessProfileActivity.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onQuickMenuTabSelected(View view) {
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
                llScan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        startActivity(new Intent(BusinessDashboardActivity.this, ScanActivity.class));
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void launchBuyTokens() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            Intent i = new Intent(BusinessDashboardActivity.this, SelectPaymentMethodActivity.class);
            i.putExtra("screen", "dashboard");
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setSelectedTab(boolean isDashboard, boolean isAccount, boolean isTransactions, boolean isProfile) {
        mIvDashboard.setImageResource(isDashboard ? R.drawable.ic_dashboard_active : R.drawable.ic_dashboard_inactive);
        mIvAccount.setImageResource(isAccount ? R.drawable.ic_account_active : R.drawable.ic_account_inactive);
        mIvTransactions.setImageResource(isTransactions ? R.drawable.ic_transactions_active : R.drawable.ic_transactions_inactive);
        mIvProfile.setImageResource(isProfile ? R.drawable.ic_profile_active : R.drawable.ic_profile);

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
            mTvAccount.setTextColor(isTabsEnabled ? selectedTextColor : disabledColor);
            mIvAccount.setImageResource(isTabsEnabled ? R.drawable.ic_account_active : R.drawable.ic_account_disabled);
        } else {
            mTvAccount.setTextColor(isTabsEnabled ? unSelectedTextColor : disabledColor);
            mIvAccount.setImageResource(isTabsEnabled ? R.drawable.ic_account_inactive : R.drawable.ic_account_disabled);
        }
        if (selectedTab == Tabs.TRANSACTIONS) {
            mTvTransactions.setTextColor(isTabsEnabled ? selectedTextColor : disabledColor);
            mIvTransactions.setImageResource(isTabsEnabled ? R.drawable.ic_transactions_active : R.drawable.ic_transactions_disabled);
        } else {
            mTvTransactions.setTextColor(isTabsEnabled ? unSelectedTextColor : disabledColor);
            mIvTransactions.setImageResource(isTabsEnabled ? R.drawable.ic_transactions_inactive : R.drawable.ic_transactions_disabled);
        }
//        mIvMenu.setImageResource(isTabsEnabled ? R.drawable.quick_action_btn : R.drawable.quick_action_btn_disabled);
    }

    private void pushFragment(BaseFragment fragment) {
        mCurrentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content_frame, fragment);
        transaction.commit();
    }

    private void initialization() {
        try {
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

            WalletRequest walletRequest = new WalletRequest();
            walletRequest.setWalletType(Utils.MERCHANT);
            walletRequest.setUserId(String.valueOf(objMyApplication.getLoginUserId()));
            businessDashboardViewModel.meMerchantWallet(walletRequest);

            walletRequest.setWalletType(Utils.TOKEN);
            businessDashboardViewModel.meMerchantWallet(walletRequest);

            walletRequest.setWalletType(Utils.RESERVE);
            businessDashboardViewModel.meMerchantWallet(walletRequest);

//            businessDashboardViewModel.meMerchantWallet(Utils.MERCHANT);
//            businessDashboardViewModel.meMerchantWallet(Utils.TOKEN);
//            businessDashboardViewModel.meMerchantWallet(Utils.RESERVE);

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
            }
        }
        setEnabledTabs();
    }

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
                    if (profile != null) {
                        objMyApplication.setMyProfile(profile);
                        objMyApplication.setStrUserName(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                        enableDisableTabView();
                        if (mCurrentFragment != null) {
                            mCurrentFragment.updateData();
                        }
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

        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                try {
                    if (businessWalletResponse != null) {
//                        objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                        if (businessWalletResponse.getData().getWalletNames() != null && businessWalletResponse.getData().getWalletNames().size() > 0) {
                            if (businessWalletResponse.getData().getWalletNames().get(0).getWalletType().equals(Utils.TOKEN_STR)) {
                                objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                                objMyApplication.setGBTBalance(businessWalletResponse.getData().getWalletNames().get(0).getExchangeAmount());
                            } else if (businessWalletResponse.getData().getWalletNames().get(0).getWalletType().equals(Utils.RESERVE_STR)) {
                                objMyApplication.setReserveBalance(businessWalletResponse.getData().getWalletNames().get(0).getExchangeAmount());
                            } else if (businessWalletResponse.getData().getWalletNames().get(0).getWalletType().equals(Utils.MERCHANT_STR)) {
//                                objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                                objMyApplication.setMerchantBalance(businessWalletResponse.getData().getWalletNames().get(0).getExchangeAmount());
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void showUserData(ImageView mIvUserIcon, TextView mTvUserName, TextView mTvUserIconText) {
        String iconText = "";
        if (objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())
                && objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null) {
            iconText = objMyApplication.getMyProfile().getData().getDbaName().substring(0, 1).toUpperCase();
            userName = objMyApplication.getMyProfile().getData().getDbaName();
            if (userName != null && userName.length() > 21) {
                mTvUserName.setText("Hi! " + userName.substring(0, 21) + " ");
            } else {
                mTvUserName.setText("Hi! " + userName);
            }
        } else if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                && objMyApplication.getMyProfile().getData().getFirstName() != null) {
            String firstName = objMyApplication.getMyProfile().getData().getFirstName();
            iconText = firstName.substring(0, 1).toUpperCase();
            userName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            if (objMyApplication.getMyProfile().getData().getLastName() != null) {
                String lastName = objMyApplication.getMyProfile().getData().getLastName();
                iconText = iconText + lastName.substring(0, 1).toUpperCase();
                userName = userName + " ";
                userName = userName + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }
            mTvUserName.setText(getResources().getString(R.string.dba_name, userName));

            if (userName != null && userName.length() > 21) {
                mTvUserName.setText("Hi! " + userName.substring(0, 21) + " ");
            } else {
                mTvUserName.setText("Hi! " + userName);
            }
        }
        if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                && objMyApplication.getMyProfile().getData().getImage() != null) {
            mTvUserIconText.setVisibility(View.GONE);
            mIvUserIcon.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(objMyApplication.getMyProfile().getData().getImage())
                    .placeholder(R.drawable.ic_profile_male_user)
                    .into(mIvUserIcon);
        } else {
            mTvUserIconText.setVisibility(View.VISIBLE);
            mIvUserIcon.setVisibility(View.GONE);
            mTvUserIconText.setText(iconText);
        }

        mTvUserName.setOnClickListener(view -> {
            if (mTvUserName.getText().toString().contains("...")) {
                if (userName.length() == 21 || userName.length() > 21) {
                    mTvUserName.setText("Hi! "+userName.substring(0, 20));
                } else {
                    mTvUserName.setText("Hi! "+userName);
                }
            } else {
                if (userName.length() == 21) {
                    mTvUserName.setText("Hi! "+userName.substring(0, 20) + "...");
                } else if (userName.length() > 22) {
                    mTvUserName.setText("Hi! "+userName.substring(0, 22) + "...");
                } else {
                    mTvUserName.setText("Hi! "+userName);
                }
            }
        });
    }

    public class FetchData extends AsyncTask<Void, Void, Boolean> {

        public FetchData(Context context) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                customerProfileViewModel.meSignOn();
                businessDashboardViewModel.meBusinessPaymentMethods();
                //mDashboardViewModel.getLatestTxns();
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
}

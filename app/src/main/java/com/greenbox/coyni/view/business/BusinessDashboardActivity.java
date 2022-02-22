package com.greenbox.coyni.view.business;

import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.BaseFragment;
import com.greenbox.coyni.fragments.BusinessAccountFragment;
import com.greenbox.coyni.fragments.BusinessDashboardFragment;
import com.greenbox.coyni.fragments.BusinessTransactionsFragment;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessReceivePaymentActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.ScanActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

//Business dashboard activity created
public class BusinessDashboardActivity extends BaseActivity {
    private BusinessDashboardViewModel businessDashboardViewModel;
    private CustomerProfileViewModel customerProfileViewModel;
    private MyApplication objMyApplication;
    private Tabs selectedTab = Tabs.DASHBOARD;
    private ImageView mIvDashboard, mIvAccount, mIvTransactions, mIvProfile;
    private TextView mTvDashboard, mTvAccount, mTvTransactions, mTvProfile;

    private enum Tabs {DASHBOARD, ACCOUNT, TRANSACTIONS, PROFILE}

    private DashboardViewModel mDashboardViewModel;
    private BaseFragment mCurrentFragment;
    Long mLastClickTimeQA = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_business_dashboard);
            initialization();
            initObserver();
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
//        pushFragment(new BusinessDashboardFragment());
    }

    public void onDashboardTabSelected(View view) {
        if (selectedTab != Tabs.DASHBOARD) {
            selectedTab = Tabs.DASHBOARD;
            setSelectedTab(true, false, false, false);
            LogUtils.d(TAG, "onDashboardTabSelected");
            pushFragment(new BusinessDashboardFragment());
        }
    }

    public void onAccountTabSelected(View view) {
        if (selectedTab != Tabs.ACCOUNT) {
            selectedTab = Tabs.ACCOUNT;
            setSelectedTab(false, true, false, false);
            LogUtils.d(TAG, "onAccountTabSelected");
            pushFragment(new BusinessAccountFragment());
        }
    }

    public void onTransactionsTabSelected(View view) {
        if (selectedTab != Tabs.TRANSACTIONS) {
            selectedTab = Tabs.TRANSACTIONS;
            setSelectedTab(false, false, true, false);
            LogUtils.d(TAG, "onTransactionsTabSelected");
            pushFragment(new BusinessTransactionsFragment());
        }
    }

    public void onProfileTabSelected(View view) {
//        if (selectedTab != Tabs.PROFILE) {
//            selectedTab = Tabs.PROFILE;
//            setSelectedTab(false, false, false, true);
//            LogUtils.d(TAG, "onProfileTabSelected");
//            pushFragment(new BusinessProfileFragment());
//        }
        startActivity(new Intent(BusinessDashboardActivity.this, BusinessProfileActivity.class));

    }

    public void onQuickMenuTabSelected(View view) {
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
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    Intent i = new Intent(BusinessDashboardActivity.this, SelectPaymentMethodActivity.class);
                    i.putExtra("screen", "dashboard");
                    startActivity(i);
                    //startActivity(new Intent(BusinessDashboardActivity.this, SelectPaymentMethodActivity.class));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        widthdrawtoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessDashboardActivity.this, SelectWithdrawMethodActivity.class));
            }
        });
        receivePaymentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessDashboardActivity.this, BusinessReceivePaymentActivity.class));
            }
        });
        llScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessDashboardActivity.this, ScanActivity.class));
            }
        });
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
            objMyApplication = (MyApplication) getApplicationContext();
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            mDashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            new FetchData(BusinessDashboardActivity.this).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse != null) {
                    objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                }
            }
        });

        mDashboardViewModel.getProfileMutableLiveData().
                observe(this, new Observer<Profile>() {
                    @Override
                    public void onChanged(Profile profile) {
                        try {
                            if (profile != null) {
                                objMyApplication.setMyProfile(profile);
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
                    if (latestTxnResponse !=null) {
                        objMyApplication.setListLatestTxn(latestTxnResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                businessDashboardViewModel.meMerchantWallet();
                mDashboardViewModel.getLatestTxns();
//                notificationsViewModel.getNotifications();
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

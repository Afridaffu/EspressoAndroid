package com.greenbox.coyni.view.business;

import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.BaseFragment;
import com.greenbox.coyni.fragments.BusinessAccountFragment;
import com.greenbox.coyni.fragments.BusinessDashboardFragment;
import com.greenbox.coyni.fragments.BusinessTransactionsFragment;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

public class BusinessDashboardActivity extends BaseActivity {
    BusinessDashboardViewModel businessDashboardViewModel;
    CustomerProfileViewModel customerProfileViewModel;
    MyApplication objMyApplication;
    private Tabs selectedTab = Tabs.DASHBOARD;

    enum Tabs {DASHBOARD, ACCOUNT, TRANSACTIONS, PROFILE}

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

    public void onDashboardTabSelected(View view) {
        if (selectedTab != Tabs.DASHBOARD) {
            selectedTab = Tabs.DASHBOARD;
            LogUtils.d(TAG, "onDashboardTabSelected");
            pushFragment(new BusinessDashboardFragment());
        }
    }

    public void onAccountTabSelected(View view) {
        if (selectedTab != Tabs.ACCOUNT) {
            selectedTab = Tabs.ACCOUNT;
            LogUtils.d(TAG, "onAccountTabSelected");
            pushFragment(new BusinessAccountFragment());
        }
    }

    public void onTransactionsTabSelected(View view) {
        if (selectedTab != Tabs.TRANSACTIONS) {
            selectedTab = Tabs.TRANSACTIONS;
            LogUtils.d(TAG, "onTransactionsTabSelected");
            pushFragment(new BusinessTransactionsFragment());
        }
    }

    public void onProfileTabSelected(View view) {
//        if (selectedTab != Tabs.PROFILE) {
//            selectedTab = Tabs.PROFILE;
//            LogUtils.d(TAG, "onProfileTabSelected");
//            pushFragment(new BusinessProfileFragment());
//        }
        startActivity(new Intent(BusinessDashboardActivity.this, BusinessProfileActivity.class));

    }

    public void onQuickMenuTabSelected(View view) {
        LogUtils.d(TAG, "onQuickMenuTabSelected");
    }

    private void pushFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content_frame, fragment);
        transaction.commit();
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
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

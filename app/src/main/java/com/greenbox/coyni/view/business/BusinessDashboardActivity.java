package com.greenbox.coyni.view.business;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.BaseFragment;
import com.greenbox.coyni.fragments.BusinessAccountFragment;
import com.greenbox.coyni.fragments.BusinessDashboardFragment;
import com.greenbox.coyni.fragments.BusinessProfileFragment;
import com.greenbox.coyni.fragments.BusinessTransactionsFragment;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;

public class BusinessDashboardActivity extends BaseActivity {

    private Tabs selectedTab = Tabs.DASHBOARD;

    enum Tabs {DASHBOARD, ACCOUNT, TRANSACTIONS, PROFILE}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        pushFragment(new BusinessDashboardFragment());
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
        if (selectedTab != Tabs.PROFILE) {
            selectedTab = Tabs.PROFILE;
            LogUtils.d(TAG, "onProfileTabSelected");
//            pushFragment(new BusinessProfileFragment());
            startActivity(new Intent(BusinessDashboardActivity.this, BusinessProfileActivity.class));
        }
    }

    public void onQuickMenuTabSelected(View view) {
        LogUtils.d(TAG, "onQuickMenuTabSelected");
    }

    private void pushFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content_frame, fragment);
        transaction.commit();
    }


}

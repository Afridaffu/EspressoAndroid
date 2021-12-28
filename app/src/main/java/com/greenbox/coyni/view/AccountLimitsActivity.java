package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;

public class AccountLimitsActivity extends AppCompatActivity {
    AccountLimitsViewModel accountLimitsViewModel;
    ProgressDialog dialog;
    int userType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_limits);

        accountLimitsViewModel = new ViewModelProvider(this).get(AccountLimitsViewModel.class);
        initObserver();
        accountLimitsViewModel.meAccountLimits(userType);


    }

    public void initObserver(){

        accountLimitsViewModel.getUserAccountLimitsMutableLiveData().observe(this, new Observer<AccountLimits>() {
            @Override
            public void onChanged(AccountLimits accountLimits) {





            }
        });
    }
}
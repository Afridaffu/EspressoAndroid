package com.greenbox.coyni.view.business;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.databinding.ActivityBusinessDashboardBinding;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class BusinessDashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityBusinessDashboardBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_business_dashboard);
        BusinessDashboardViewModel businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        binding.setLifecycleOwner(this);
       // binding.setBusinessDashboard(businessDashboardViewModel);
    }
}

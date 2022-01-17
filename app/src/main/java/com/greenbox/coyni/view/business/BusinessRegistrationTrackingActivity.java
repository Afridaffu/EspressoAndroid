package com.greenbox.coyni.view.business;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.databinding.ActivityBusinessRegistrationTrackingBinding;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessRegistrationTrackingViewModel;

public class BusinessRegistrationTrackingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityBusinessRegistrationTrackingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_business_registration_tracking);
        BusinessRegistrationTrackingViewModel businessRegistrationTrackingViewModel = new ViewModelProvider(this).get(BusinessRegistrationTrackingViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setBusinessRegistrationTrackingViewModel(businessRegistrationTrackingViewModel);

        //showProgressDialog("Please wait..");
    }
}

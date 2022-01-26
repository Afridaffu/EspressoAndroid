//package com.greenbox.coyni.view;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.annotation.Nullable;
//import androidx.databinding.DataBindingUtil;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.greenbox.coyni.R;
//import com.greenbox.coyni.databinding.ActivityCreateBusinessAccountBinding;
//import com.greenbox.coyni.viewmodel.CreateBusinessAccountViewModel;
//
//public class CreateBusinessAccountActivity extends BaseActivity {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        ActivityCreateBusinessAccountBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_create_business_account);
//        CreateBusinessAccountViewModel businessDashboardViewModel = new ViewModelProvider(this).get(CreateBusinessAccountViewModel.class);
//        binding.setLifecycleOwner(this);
//        binding.setBusinessDashboard(businessDashboardViewModel);
//    }
//
//}

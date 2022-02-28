package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BindingLayoutActivity;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

public class BusinessAddNewAccountActivity extends AppCompatActivity {

    private ImageView imageViewClose,ivPersonalProfileIcon;
    private TextView tvPersonalProfileName;
    private LinearLayout llBusinessAccount,llPersonalAccount;
    private String personalAccountSize;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private String personalAccountAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.business_onboarding_open_new_account);

       // personalAccountSize = getIntent().getStringExtra("PersonalAccountSize");

        llBusinessAccount = findViewById(R.id.businessAccontLL);
        llPersonalAccount = findViewById(R.id.personalAccontLL);
        ivPersonalProfileIcon = findViewById(R.id.iv_personal_profile_icon);
        tvPersonalProfileName = findViewById(R.id.tv_personal_profile_name);
        imageViewClose = findViewById(R.id.imv_close);

//        if (getIntent().getStringExtra("PersonalAccount") != null && !getIntent().getStringExtra("PersonalAccount").equals("")) {
//            LogUtils.d("PersonalAccount","PersonalAccount"+getIntent().getStringExtra("PersonalAccount"));
//            personalAccountAvailable = getIntent().getStringExtra("PersonalAccount");
//            if(personalAccountAvailable.equalsIgnoreCase("true")){
//                ivPersonalProfileIcon.setBackgroundResource(R.drawable.ic_user_inactive);
//                tvPersonalProfileName.setTextColor(getResources().getColor(R.color.trans_black));
//
//            } else {
//                ivPersonalProfileIcon.setBackgroundResource(R.drawable.ic_user);
//                tvPersonalProfileName.setTextColor(getResources().getColor(R.color.primary_black));
//
//            }
//        }

        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

        llBusinessAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessAddNewAccountActivity.this, BusinessAddNewBusinessAccountActivity.class));
            }
        });

        llPersonalAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessAddNewAccountActivity.this, BindingLayoutActivity.class));
                //identityVerificationViewModel.getPostAddCustomer();

            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
       // initObservers();

    }


}
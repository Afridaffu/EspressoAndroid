package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BindingLayoutActivity;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

public class BusinessAddNewAccountActivity extends AppCompatActivity {

    private ImageView imageViewClose, ivPersonalProfileIcon;
    private TextView tvPersonalProfileName;
    private LinearLayout llBusinessAccount, llPersonalAccount;
    private String personalAccountSize;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private String personalAccountAvailable;
    private Long mLastClickTimeQA = 0L;

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
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                startActivity(new Intent(BusinessAddNewAccountActivity.this, BusinessAddNewBusinessAccountActivity.class));
            }
        });

        llPersonalAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                startActivity(new Intent(BusinessAddNewAccountActivity.this, BindingLayoutActivity.class)
                        .putExtra("screen", "profileGetStarted"));
                //identityVerificationViewModel.getPostAddCustomer();

            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                finish();
            }
        });
        // initObservers();

    }


}
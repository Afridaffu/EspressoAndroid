package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    ImageView imageViewClose;
    LinearLayout llBusinessAccount,llPersonalAccount;
    private String personalAccountSize;
    private IdentityVerificationViewModel identityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.business_onboarding_open_new_account);

       // personalAccountSize = getIntent().getStringExtra("PersonalAccountSize");

        llBusinessAccount = findViewById(R.id.businessAccontLL);
        llPersonalAccount = findViewById(R.id.personalAccontLL);
        imageViewClose = findViewById(R.id.imv_close);

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
                identityVerificationViewModel.getPostAddCustomer();
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initObservers();

    }

    public void initObservers() {
        try {
            identityVerificationViewModel.getBusinessAddCustomer().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {

                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        LogUtils.d("AddBusinessUserResponse","AddBusinessUserResponse"+identityImageResponse);
                        LogUtils.d("Utils.getStrAuth();","Utils.getStrAuth();"+Utils.getStrAuth());
                        //Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                         startActivity(new Intent(BusinessAddNewAccountActivity.this, BindingLayoutActivity.class));

                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BusinessAddNewAccountActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
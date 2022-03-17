package com.greenbox.coyni.view.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.MerchantSettingsBeneficialOwnersAdapter;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantSettingsBeneficialOwnersActivity extends BaseActivity {

    private LinearLayout bpbackBtn;
    private RecyclerView beneficialOwnersRV;
    private List<BOResp.BeneficialOwner> beneficialOwnerList = new ArrayList<>();
    private TextView noDataTV;
    private MyApplication objMyApplication;
    private MerchantSettingsBeneficialOwnersAdapter.RecyclerClickListener beneficialClickListener;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_merchant_settings_benificial_owners);
        initFields();
        setOnClickListener();
        initObservers();

    }

    private void initFields() {
        objMyApplication = (MyApplication) getApplicationContext();
        dialog = Utils.showProgressDialog(MerchantSettingsBeneficialOwnersActivity.this);
        bpbackBtn = findViewById(R.id.backLL);
        noDataTV = findViewById(R.id.noDataTV);
        beneficialOwnersRV = findViewById(R.id.rvBo);
        bpbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getBeneficialOwners();

    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersResponse().observe(this, new Observer<BOResp>() {
                @Override
                public void onChanged(BOResp boResp) {
                    dialog.dismiss();
                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBeneficialOwnersResponse(boResp);
                            loadBeneficialOwners(boResp);
                        } else {
                            noDataTV.setVisibility(View.VISIBLE);
                            beneficialOwnersRV.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void loadBeneficialOwners(BOResp boResp) {

        if (objMyApplication.getBeneficialOwnersResponse().getData().size() > 0 && beneficialOwnerList != null) {
            noDataTV.setVisibility(View.GONE);
            beneficialOwnersRV.setVisibility(View.VISIBLE);
            beneficialOwnerList = boResp.getData();

            Collections.sort(beneficialOwnerList, Comparator.comparing(BOResp.BeneficialOwner::getOwnershipParcentage, Comparator.reverseOrder())
                    .thenComparing(BOResp.BeneficialOwner::getId, Comparator.reverseOrder()));
            MerchantSettingsBeneficialOwnersAdapter beneficialOwnersAdapter = new MerchantSettingsBeneficialOwnersAdapter(this, beneficialOwnerList, beneficialClickListener);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            beneficialOwnersRV.setLayoutManager(mLayoutManager);
            beneficialOwnersRV.setAdapter(beneficialOwnersAdapter);

        }
    }

    private void setOnClickListener() {
        try {

            beneficialClickListener = (view, position) -> {
                Context context = view.getContext();
                Intent intent = new Intent();
                intent = new Intent(context, BenificialOwnerDetails.class);
                intent.putExtra(Utils.boData, beneficialOwnerList.get(position));
                intent.putExtra(Utils.position, position);
                context.startActivity(intent);
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
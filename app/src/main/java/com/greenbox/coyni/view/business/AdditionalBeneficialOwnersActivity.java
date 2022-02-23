package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.BeneficialOwnersAdapter;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.BeneficialOwners.DeleteBOResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AgreementsActivity;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class AdditionalBeneficialOwnersActivity extends BaseActivity {

    ImageView backIV;
    RecyclerView beneficialOwnersRV;
    TextView percentageTV, notFoundTV;
    MyApplication objMyApplication;
    LinearLayout addNewBOLL;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_additional_benifitial_owners);

        initFields();
        initObservers();

    }

    private void initFields() {

        objMyApplication = (MyApplication) getApplicationContext();
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);

        backIV = findViewById(R.id.backIV);
        beneficialOwnersRV = findViewById(R.id.beneficialOwnersRV);
        percentageTV = findViewById(R.id.percentageTV);
        notFoundTV = findViewById(R.id.notFoundTV);
        addNewBOLL = findViewById(R.id.addNewBOLL);

        loadBeneficialOwners();

        backIV.setOnClickListener(v -> finish());

        addNewBOLL.setOnClickListener(v -> finish());
    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getDeleteBOesponse().observe(this, new Observer<DeleteBOResp>() {
                @Override
                public void onChanged(DeleteBOResp deleteBOResp) {

                    if (deleteBOResp != null) {
                        if (deleteBOResp.getStatus().toLowerCase().toString().equals("success")) {
                            businessIdentityVerificationViewModel.getBeneficialOwners();
                        } else {
                            Utils.displayAlert(deleteBOResp.getError().getErrorDescription(),
                                    AdditionalBeneficialOwnersActivity.this, "", deleteBOResp.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersResponse().observe(this, new Observer<BOResp>() {
                @Override
                public void onChanged(BOResp boResp) {

                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBeneficialOwnersResponse(boResp);
                            loadBeneficialOwners();
                        } else {
                            notFoundTV.setVisibility(View.VISIBLE);
                            beneficialOwnersRV.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseEditORDelete(int boID) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_delete_dialog);
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            LinearLayout viewEditLL = dialog.findViewById(R.id.viewEditLL);
            LinearLayout deleteLL = dialog.findViewById(R.id.deleteLL);

            viewEditLL.setOnClickListener(view -> {
                dialog.dismiss();
                startActivity(new Intent(this, AddBeneficialOwnerActivity.class).putExtra("FROM", "EDIT_BO"));
                finish();
            });

            deleteLL.setOnClickListener(view -> {
                dialog.dismiss();
                businessIdentityVerificationViewModel.deleteBeneficialOwner(boID);
            });

            dialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadBeneficialOwners() {

        if (objMyApplication.getBeneficialOwnersResponse().getData().size() > 0) {
            notFoundTV.setVisibility(View.GONE);
            beneficialOwnersRV.setVisibility(View.VISIBLE);

            BeneficialOwnersAdapter beneficialOwnersAdapter = new BeneficialOwnersAdapter(this, objMyApplication.getBeneficialOwnersResponse());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            beneficialOwnersRV.setLayoutManager(mLayoutManager);
            beneficialOwnersRV.setItemAnimator(new DefaultItemAnimator());
            beneficialOwnersRV.setAdapter(beneficialOwnersAdapter);
        } else {
            notFoundTV.setVisibility(View.VISIBLE);
            beneficialOwnersRV.setVisibility(View.GONE);
        }
    }
}
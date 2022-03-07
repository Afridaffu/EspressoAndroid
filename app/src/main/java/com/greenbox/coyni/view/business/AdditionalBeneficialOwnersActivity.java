package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.BeneficialOwnersAdapter;
import com.greenbox.coyni.model.BeneficialOwners.BOIdResp;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.BeneficialOwners.BOValidateResp;
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
    CardView validateCV;
    boolean isValidateEnabled = false;
    Long mLastClickTime = 0L;
    boolean hasDrafts = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_additional_benifitial_owners);

            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields() {

        try {
            objMyApplication = (MyApplication) getApplicationContext();
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);

            backIV = findViewById(R.id.backIV);
            beneficialOwnersRV = findViewById(R.id.beneficialOwnersRV);
            percentageTV = findViewById(R.id.percentageTV);
            notFoundTV = findViewById(R.id.notFoundTV);
            addNewBOLL = findViewById(R.id.addNewBOLL);
            validateCV = findViewById(R.id.validateCV);

//            loadBeneficialOwners();

            backIV.setOnClickListener(v -> finish());

            addNewBOLL.setOnClickListener(view -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (hasDrafts) {
                        Utils.displayAlert("Please complete draft beneficial owner information.", this, "", "");
                    } else {
                        try {
                            if (objMyApplication.getBeneficialOwnersResponse().getData().size() < 20) {
                                businessIdentityVerificationViewModel.postBeneficialOwnersID();
                            } else {
                                Utils.showCustomToast(this, "You are exceeded your benificial accounts max limit.", 0, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            businessIdentityVerificationViewModel.postBeneficialOwnersID();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            validateCV.setOnClickListener(view -> {
                if (isValidateEnabled) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    businessIdentityVerificationViewModel.validateBeneficialOwners();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            notFoundTV.setVisibility(View.GONE);
                            percentageTV.setVisibility(View.VISIBLE);
                            addNewBOLL.setVisibility(View.VISIBLE);
                            beneficialOwnersRV.setVisibility(View.GONE);
                            isValidateEnabled = false;
                            validateCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersIDResponse().observe(this, new Observer<BOIdResp>() {
                @Override
                public void onChanged(BOIdResp boIdResp) {

                    if (boIdResp != null) {
                        if (boIdResp.getStatus().toLowerCase().toString().equals("success")) {
                            startActivity(new Intent(AdditionalBeneficialOwnersActivity.this, AddBeneficialOwnerActivity.class)
                                    .putExtra("FROM", "ADD_BO")
                                    .putExtra("ID", boIdResp.getData().getId()));
                            finish();
                        } else {

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getValidateBOResponse().observe(this, new Observer<BOValidateResp>() {
                @Override
                public void onChanged(BOValidateResp boValidateResp) {

                    if (boValidateResp != null) {
                        if (boValidateResp.getStatus().toLowerCase().toString().equals("success")) {
                            finish();
                        } else {
                            Utils.displayAlert(boValidateResp.getError().getErrorDescription(),
                                    AdditionalBeneficialOwnersActivity.this, "", boValidateResp.getError().getFieldErrors().get(0));
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
                startActivity(new Intent(this, AddBeneficialOwnerActivity.class)
                        .putExtra("FROM", "EDIT_BO")
                        .putExtra("ID", boID));
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

            int totalPercentage = 0;

            for (int i = 0; i < objMyApplication.getBeneficialOwnersResponse().getData().size(); i++) {
                totalPercentage = totalPercentage + objMyApplication.getBeneficialOwnersResponse().getData().get(i).getOwnershipParcentage();
                BOResp.BeneficialOwner bo = objMyApplication.getBeneficialOwnersResponse().getData().get(i);

                try {
                    objMyApplication.getBeneficialOwnersResponse().getData().get(i).setDraft(bo.getFirstName().equals("") || bo.getLastName().equals("") || bo.getDob().equals("")
                            || bo.getOwnershipParcentage() <= 0 || bo.getAddressLine1().equals("")
                            || bo.getCity().equals("") || bo.getState().equals("") || bo.getZipCode().equals("")
                            || bo.getSsn().equals("") || bo.getRequiredDocuments().size() <= 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    objMyApplication.getBeneficialOwnersResponse().getData().get(i).setDraft(true);
                }

                if (objMyApplication.getBeneficialOwnersResponse().getData().get(i).isDraft())
                    hasDrafts = true;
            }

            BeneficialOwnersAdapter beneficialOwnersAdapter = new BeneficialOwnersAdapter(this, objMyApplication.getBeneficialOwnersResponse());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            beneficialOwnersRV.setLayoutManager(mLayoutManager);
            beneficialOwnersRV.setItemAnimator(new DefaultItemAnimator());
            beneficialOwnersRV.setAdapter(beneficialOwnersAdapter);


            if (totalPercentage >= Utils.boTargetPercentage && !hasDrafts) {
                percentageTV.setVisibility(View.GONE);
                isValidateEnabled = true;
                validateCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                percentageTV.setVisibility(View.VISIBLE);
                isValidateEnabled = false;
                validateCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }

            if (totalPercentage >= 100) {
                addNewBOLL.setVisibility(View.GONE);
                percentageTV.setVisibility(View.GONE);
            } else {
                addNewBOLL.setVisibility(View.VISIBLE);
                percentageTV.setVisibility(View.VISIBLE);
            }
        } else {
            addNewBOLL.setVisibility(View.VISIBLE);
            percentageTV.setVisibility(View.VISIBLE);

            isValidateEnabled = false;
            validateCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            businessIdentityVerificationViewModel.getBeneficialOwners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
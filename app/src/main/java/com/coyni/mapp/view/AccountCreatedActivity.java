package com.coyni.mapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;

import com.coyni.mapp.databinding.ActivityAccountCreatedBinding;
import com.coyni.mapp.dialogs.OnAgreementsAPIListener;
import com.coyni.mapp.model.FilteredAgreements;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

public class AccountCreatedActivity extends BaseActivity {

    private ActivityAccountCreatedBinding binding;
    private Long mLastClickTime = 0L;
    private MyApplication objMyApplication;
    private String strScreen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountCreatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        objMyApplication = (MyApplication) getApplicationContext();
        if (getIntent().getStringExtra("screen") != null) {
            strScreen = getIntent().getStringExtra("screen");
        }
        binding.tvAddCard.setOnClickListener(view -> {
            startActivity(new Intent(this, AddCardActivity.class)
                    .putExtra("card", "None")
                    .putExtra("screen", "signup"));
        });

        binding.tvSkip.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            launchDashboard();
        });

        setOnAgreementsAPIListener(new OnAgreementsAPIListener() {
            @Override
            public void onAgreementsAPIResponse(SignAgreementsResp signAgreementsResp, boolean isMerchantHide) {
                dismissDialog();
                if (isMerchantHide) {
                    FilteredAgreements filteredAgreements = Utils.getFilteredAgreements(signAgreementsResp.getData());
                    if (filteredAgreements.getAgreements().size() > 0) {
                        objMyApplication.setHasToSignAgreements(filteredAgreements.getAgreements());
                        Utils.launchAgreements(AccountCreatedActivity.this, isMerchantHide);
                    } else {
                        launchDasboardFromBase();
                    }
                } else {
                    if (signAgreementsResp.getData().size() > 0) {
                        objMyApplication.setHasToSignAgreements(signAgreementsResp.getData());
                        Utils.launchAgreements(AccountCreatedActivity.this, isMerchantHide);
                    } else {
                        launchDasboardFromBase();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private void launchDashboard() {
        try {
            if (objMyApplication.getInitializeResponse() != null && objMyApplication.getInitializeResponse().getData() != null
                    && !objMyApplication.getInitializeResponse().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())
                    && !objMyApplication.getInitializeResponse().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                    && !objMyApplication.getInitializeResponse().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {

                if (objMyApplication.getInitializeResponse().getData().getAccountType() == Utils.SHARED_ACCOUNT) {
                    if (objMyApplication.getInitializeResponse().getData().getOwnerDetails() != null && !objMyApplication.getInitializeResponse().getData().getOwnerDetails().getTracker().isIsAgreementSigned()) {
                        showProgressDialog();
                        callHasToSignAPI(true);
                    } else {
                        dashboard();
                    }
                } else {
                    if (!objMyApplication.getInitializeResponse().getData().getTracker().isIsAgreementSigned()) {
                        showProgressDialog();
//                        if (objMyApplication.getInitializeResponse().getData().getBusinessTracker() == null || objMyApplication.getInitializeResponse().getData().getBusinessTracker().isIsAgreementSigned())
//                            callHasToSignAPI(false);
//                        else if (!objMyApplication.getInitializeResponse().getData().getBusinessTracker().isIsAgreementSigned()) {
//                            callHasToSignAPI(true);
//                        }
                        if (objMyApplication.getInitializeResponse().getData().getBusinessTracker() == null || !objMyApplication.getInitializeResponse().getData().getBusinessTracker().isIsAgreementSigned())
                            callHasToSignAPI(true);
                        else if (objMyApplication.getInitializeResponse().getData().getBusinessTracker().isIsAgreementSigned()) {
                            callHasToSignAPI(false);
                        }
                    } else {
                        dashboard();
                    }
                }

            } else {
                dashboard();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dashboard() {
        if (objMyApplication.checkForDeclinedStatus()) {
            objMyApplication.setIsLoggedIn(true);
            objMyApplication.launchDeclinedActivity(this);
        } else {
            objMyApplication.setIsLoggedIn(true);
            objMyApplication.launchDashboard(this, strScreen);
        }
    }

}
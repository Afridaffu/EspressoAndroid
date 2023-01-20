package com.coyni.mapp.view.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.ActivityMerchatTrackerAgreementsBinding;
import com.coyni.mapp.model.Agreements;
import com.coyni.mapp.model.Item;
import com.coyni.mapp.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.util.Objects;

public class MerchantTrackerAgreementsActivity extends BaseActivity {

    private long mLastClickTime = 0;
    private ActivityMerchatTrackerAgreementsBinding binding;
    private boolean isPP = false, isTOS = false;
    private int agreementType = -1;
    private DashboardViewModel dashboardViewModel;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private String ppDate = "", tosDate = "";
    private MyApplication myApplication;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_merchat_tracker_agreements);

        myApplication = (MyApplication) getApplicationContext();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        showProgressDialog();
        dashboardViewModel.meAgreementsById();

        initObservers();
        enableDoneButton();

        binding.termsOfServiceLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTOS) showAgreement(Utils.mTOS);
            }
        });

        binding.privacyPolicyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPP) showAgreement(Utils.mPP);
            }
        });

        binding.ivBack.setOnTouchListener((view, motionEvent) -> {
            finish();
            return false;
        });

        binding.cvDone.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            // TODO Api call to mark agreements are done
            businessDashboardViewModel.updateSignedAgree();
        });
    }

    private void showAgreement(int agreementType) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        this.agreementType = agreementType;
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(MerchantTrackerAgreementsActivity.this, AcceptAgreementsActivity.class);
        intent.putExtra(Utils.AGREEMENT_TYPE, agreementType);
        intent.putExtra(Utils.ACT_TYPE, Utils.single);
        intent.putExtra(Utils.SCREEN, "Tracker");
        agreementVerifiedLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> agreementVerifiedLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                updateUI();
            }
        }
    });

    private void initObservers() {
        dashboardViewModel.getAgreementsMutableLiveData().observe(this, new Observer<Agreements>() {
            @Override
            public void onChanged(Agreements agreements) {
                try {
                    dismissDialog();
                    LogUtils.v(TAG, agreements.getStatus());
                    if (agreements.getStatus().contains(Utils.SUCCESS)) {
                        if (agreements.getData() != null && agreements.getData().getItems() != null && agreements.getData().getItems().size() > 0) {
                            for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                Item item = agreements.getData().getItems().get(i);
                                if (item.getSignatureType() == Utils.mTOS) {
                                    tosDate = item.getSignedOn();
                                    if (!Objects.equals(item.getSignature(), "")) {
                                        isTOS = true;
                                        binding.ivCheckTOS.setVisibility(View.VISIBLE);
                                        binding.tvStatusTOS.setText("Agreed On: " + myApplication.convertZoneDateTime(tosDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy") + ".");
                                    }
                                } else if (item.getSignatureType() == Utils.mPP) {
                                    ppDate = item.getSignedOn();
                                    if (!Objects.equals(item.getSignature(), "")) {
                                        isPP = true;
                                        binding.ivCheckPP.setVisibility(View.VISIBLE);
                                        binding.tvStatusPP.setText("Agreed On: " + myApplication.convertZoneDateTime(ppDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy") + ".");
                                    }
                                }


                            }
                        }
                        enableDoneButton();
                    } else {
                        Utils.displayAlert(agreements.getError().getErrorDescription(), MerchantTrackerAgreementsActivity.this, "", agreements.getError().getFieldErrors().get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getUpdateSignAgreementsResponseMutableLiveData().observe(this, new Observer<UpdateSignAgreementsResponse>() {
            @Override
            public void onChanged(UpdateSignAgreementsResponse response) {
                if (response.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    finish();
                } else {
                    Utils.displayAlert(response.getError().getErrorDescription(), MerchantTrackerAgreementsActivity.this, "", "");
                }
            }
        });
    }

    private void updateUI() {
        if (agreementType == Utils.mPP) {
            isPP = true;
            binding.ivCheckPP.setVisibility(View.VISIBLE);
            binding.tvStatusPP.setText("Agreed On: " + myApplication.convertZoneDateTime(ppDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy") + ".");
        } else {
            isTOS = true;
            binding.ivCheckTOS.setVisibility(View.VISIBLE);
            binding.tvStatusTOS.setText("Agreed On: " + myApplication.convertZoneDateTime(tosDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy") + ".");
        }

        enableDoneButton();
    }

    private void enableDoneButton() {
        if (isPP && isTOS) {
            binding.cvDone.setEnabled(true);
            binding.cvDone.setCardBackgroundColor(getColor(R.color.primary_green));
        } else {
            binding.cvDone.setEnabled(false);
            binding.cvDone.setCardBackgroundColor(getColor(R.color.inactive_color));
        }
    }
}
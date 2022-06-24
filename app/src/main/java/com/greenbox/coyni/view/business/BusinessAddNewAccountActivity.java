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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BindingLayoutActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessAddNewAccountActivity extends BaseActivity {

    private ImageView imageViewClose, ivPersonalProfileIcon;
    private TextView tvPersonalProfileName;
    private LinearLayout llBusinessAccount, llPersonalAccount;
    private String personalAccountSize;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private String personalAccountAvailable;
    private Long mLastClickTimeQA = 0L;
    private DashboardViewModel dashboardViewModel;
    private List<ProfilesResponse.Profiles> profilesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.business_onboarding_open_new_account);

//        personalAccountSize = getIntent().getStringExtra("PersonalAccountSize");
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        llBusinessAccount = findViewById(R.id.businessAccontLL);
        llPersonalAccount = findViewById(R.id.personalAccontLL);
        ivPersonalProfileIcon = findViewById(R.id.iv_personal_profile_icon);
        tvPersonalProfileName = findViewById(R.id.tv_personal_profile_name);
        imageViewClose = findViewById(R.id.imv_close);
        llPersonalAccount.setEnabled(false);
        ivPersonalProfileIcon.setImageResource(R.drawable.inactive_personal);
        tvPersonalProfileName.setTextColor(getResources().getColor(R.color.trans_black));

        initObservers();

//        if (getIntent().getStringExtra("PersonalAccount") != null && !getIntent().getStringExtra("PersonalAccount").equals("")) {
//            LogUtils.d("PersonalAccount", "PersonalAccount" + getIntent().getStringExtra("PersonalAccount"));
//            personalAccountAvailable = getIntent().getStringExtra("PersonalAccount");
//            if (personalAccountAvailable.equalsIgnoreCase("true")) {
//                llPersonalAccount.setEnabled(false);
//                ivPersonalProfileIcon.setImageResource(R.drawable.inactive_personal);
//                tvPersonalProfileName.setTextColor(getResources().getColor(R.color.trans_black));
//            } else {
//                llPersonalAccount.setEnabled(true);
//                ivPersonalProfileIcon.setImageResource(R.drawable.ic_user);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            showProgressDialog();
            dashboardViewModel.getProfiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {
        try {
            dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
                @Override
                public void onChanged(ProfilesResponse profilesResponse) {
                    dismissDialog();
                    if (profilesResponse != null) {
                        if (profilesResponse.getStatus().equals("SUCCESS")) {
                            profilesList = profilesResponse.getData();
                            profileList();
                        } else {
                            Utils.displayAlert(profilesResponse.getError().getErrorDescription(), BusinessAddNewAccountActivity.this, "", profilesResponse.getError().getFieldErrors().get(0));
                        }
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileList() {
        boolean isPersonalAccountFound = false;
        for (ProfilesResponse.Profiles c : profilesList) {
            if (c.getAccountType().equals(Utils.PERSONAL)) {
                isPersonalAccountFound = true;
                break;
            }
        }

        llPersonalAccount.setEnabled(!isPersonalAccountFound);
        ivPersonalProfileIcon.setImageResource(isPersonalAccountFound ? R.drawable.inactive_personal : R.drawable.ic_user);
        tvPersonalProfileName.setTextColor(getResources().getColor(isPersonalAccountFound ? R.color.trans_black : R.color.primary_black));
    }
}
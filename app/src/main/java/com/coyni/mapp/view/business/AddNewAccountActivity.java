package com.coyni.mapp.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.AddNewAccountsAdapter;
import com.coyni.mapp.databinding.ActivityAddNewAccountBinding;
import com.coyni.mapp.model.preferences.ProfilesResponse;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.view.BindingLayoutActivity;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddNewAccountActivity extends BaseActivity {
    private Long mLastClickTimeQA = 0L;
    private DashboardViewModel dashboardViewModel;
    private List<ProfilesResponse.Profiles> profilesList = new ArrayList<>();
    private ActivityAddNewAccountBinding binding;
    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_account);

            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

            initObservers();
            binding.llBusinessAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    startActivity(new Intent(AddNewAccountActivity.this, BusinessAddNewBusinessAccountActivity.class));
                }
            });

            binding.llBAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    startActivity(new Intent(AddNewAccountActivity.this, BusinessAddNewBusinessAccountActivity.class));
                }
            });

            binding.llPersonalAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    startActivity(new Intent(AddNewAccountActivity.this, BindingLayoutActivity.class)
                            .putExtra("screen", "profileGetStarted"));
                }
            });

            binding.imvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                        return;
                    }
                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                    finish();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                            Utils.displayAlert(profilesResponse.getError().getErrorDescription(), AddNewAccountActivity.this, "", profilesResponse.getError().getFieldErrors().get(0));
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
        binding.llPersonalAccount.setEnabled(!isPersonalAccountFound);
        if (isPersonalAccountFound) {
            binding.chooceAccTextTV.setVisibility(View.GONE);
            binding.llPersonalAccount.setVisibility(View.GONE);
            binding.llBusinessAccount.setVisibility(View.GONE);
            binding.tabs.setVisibility(View.GONE);
            binding.llBAccount.setVisibility(View.VISIBLE);
            setPagerAdapter(isPersonalAccountFound);
        } else {
            binding.chooceAccTextTV.setVisibility(View.VISIBLE);
            binding.llPersonalAccount.setVisibility(View.VISIBLE);
            binding.llBusinessAccount.setVisibility(View.VISIBLE);
            binding.llBAccount.setVisibility(View.GONE);
            binding.tabs.setVisibility(View.VISIBLE);
            setPagerAdapter(isPersonalAccountFound);
        }

    }

    private void setPagerAdapter(boolean isPersonalAccountFound) {
        try {
            AddNewAccountsAdapter autoScrollPagerAdapter = new AddNewAccountsAdapter(getSupportFragmentManager(), isPersonalAccountFound ? 1 : 2);
            binding.viewPager.setAdapter(autoScrollPagerAdapter);
            binding.tabs.setupWithViewPager(binding.viewPager);
            binding.viewPager.setStopScrollWhenTouch(true);
            binding.viewPager.setInterval(AUTO_SCROLL_THRESHOLD_IN_MILLI);

            // start auto scroll
            binding.viewPager.startAutoScroll();

            // enable recycling using true
            binding.viewPager.setCycle(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout layoutProfile;
    DashboardViewModel dashboardViewModel;
    TextView tvUserName;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            layoutProfile = findViewById(R.id.layoutProfile);
            tvUserName = findViewById(R.id.tvUserName);
            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            if (Utils.checkInternet(DashboardActivity.this)) {
                dashboardViewModel.meProfile();
            } else {
                Utils.displayAlert(getString(R.string.internet), DashboardActivity.this, "");
            }
            layoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DashboardActivity.this, CustomerProfileActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile != null) {
                    objMyApplication.setMyProfile(profile);
                    objMyApplication.setStrUserName(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                    String strName = Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
                    if (strName != null && strName.length() > 21) {
                        tvUserName.setText("Hi " + strName.substring(0, 21) + "...");
                    } else {
                        tvUserName.setText("Hi " + strName);
                    }
                }
            }
        });
    }
}
package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class PreferencesActivity extends AppCompatActivity {

    MyApplication myApplicationObj;
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    boolean isProfile = false;
    TextInputLayout timeZoneTIL, accountTIL;
    TextInputEditText timeZoneET, accountET;
    ConstraintLayout timeZoneCL;
    LinearLayout preferencesCloseLL;
    public static CustomerProfileViewModel customerProfileViewModel;
    int timeZoneID = 0;
    public static PreferencesActivity preferencesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferences);
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            preferencesActivity = this;
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            myApplicationObj = (MyApplication) getApplicationContext();
            timeZoneTIL = findViewById(R.id.timeZoneTIL);
            timeZoneET = findViewById(R.id.timeZoneET);
            accountTIL = findViewById(R.id.accountTIL);
            accountET = findViewById(R.id.accountET);
            timeZoneCL = findViewById(R.id.timeZoneCL);
            preferencesCloseLL = findViewById(R.id.preferencesCloseLL);

            timeZoneCL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj);
                }
            });

            timeZoneET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj);
                }
            });

            preferencesCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            dashboardViewModel.mePreferences();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {

        dashboardViewModel.getPreferenceMutableLiveData().observe(this, new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences preferences) {

                try {
                    if (preferences != null) {
                        timeZoneID = preferences.getData().getTimeZone();
                        myApplicationObj.setTimezoneID(timeZoneID);
                        if (preferences.getData().getTimeZone() == 0) {
                            timeZoneET.setText(getString(R.string.PST));
//                            myApplicationObj.setTimezone("PST");
                            myApplicationObj.setTimezone(getString(R.string.PST));
                        } else if (preferences.getData().getTimeZone() == 1) {
                            timeZoneET.setText(getString(R.string.MST));
//                            myApplicationObj.setTimezone("MST");
                            myApplicationObj.setTimezone(getString(R.string.MST));
                        } else if (preferences.getData().getTimeZone() == 2) {
                            timeZoneET.setText(getString(R.string.CST));
//                            myApplicationObj.setTimezone("CST");
                            myApplicationObj.setTimezone(getString(R.string.CST));
                        } else if (preferences.getData().getTimeZone() == 3) {
                            timeZoneET.setText(getString(R.string.EST));
//                            myApplicationObj.setTimezone("EST");
                            myApplicationObj.setTimezone(getString(R.string.EST));
                        } else if (preferences.getData().getTimeZone() == 4) {
                            timeZoneET.setText(getString(R.string.HST));
//                            myApplicationObj.setTimezone("HST");
                            myApplicationObj.setTimezone(getString(R.string.HST));
                        } else if (preferences.getData().getTimeZone() == 5) {
                            timeZoneET.setText(getString(R.string.AST));
//                            myApplicationObj.setTimezone("AST");
                            myApplicationObj.setTimezone(getString(R.string.AST));
                        }
                        accountET.setText(preferences.getData().getPreferredAccount());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        customerProfileViewModel.getUserPreferenceMutableLiveData().observe(this, new Observer<UserPreference>() {
            @Override
            public void onChanged(UserPreference userPreference) {
                if (userPreference != null) {
                    if (!userPreference.getStatus().toLowerCase().equals("success")) {
                        Utils.displayAlert(userPreference.getError().getErrorDescription(), PreferencesActivity.this, "");
                    }else{
                        myApplicationObj.setTimezoneID(myApplicationObj.getTempTimezoneID());
                        myApplicationObj.setTimezone(myApplicationObj.getTempTimezone());
                        timeZoneET.setText(myApplicationObj.getTimezone());
                    }
                }
            }
        });
    }


}
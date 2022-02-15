package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
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
    TextInputLayout timeZoneTIL, accountTIL, currencyTIL;
    TextInputEditText timeZoneET, accountET, currencyET;
    RelativeLayout timeZoneRL;
    LinearLayout preferencesCloseLL,defaultaccLL;
    TextView timezonetext;
    ImageView accountDDIV;
    View disableView;
    public static CustomerProfileViewModel customerProfileViewModel;
    int timeZoneID = 0;
    public static PreferencesActivity preferencesActivity;
    Long mLastClickTime = 0L;

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
            currencyTIL = findViewById(R.id.currencyTIL);
            accountET = findViewById(R.id.accountET);
            currencyET = findViewById(R.id.currencyET);
            timeZoneRL = findViewById(R.id.timezoneRL);
            accountDDIV = findViewById(R.id.accountDDICon);
            disableView = findViewById(R.id.disableView);
            preferencesCloseLL = findViewById(R.id.preferencesCloseLL);
            timezonetext = findViewById(R.id.timezoneTextTV);
            defaultaccLL = findViewById(R.id.defaultaccLL);


            currencyTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            Utils.setUpperHintColor(currencyTIL, getColor(R.color.xdark_gray));

            accountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            Utils.setUpperHintColor(accountTIL, getColor(R.color.xdark_gray));

            timeZoneRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj, "PREFERENCES");
                }
            });

//            timeZoneTIL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj);
//                }
//            });

            timeZoneET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET, myApplicationObj, "PREFERENCES");
                }
            });

            preferencesCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            dialog = Utils.showProgressDialog(this);

            dashboardViewModel.mePreferences();

            dashboardViewModel.getProfiles();


            // Business Preferences
            if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT){
                timezonetext.setVisibility(View.VISIBLE);
                defaultaccLL.setVisibility(View.GONE); }

            if(myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT){
                timezonetext.setVisibility(View.GONE);
                defaultaccLL.setVisibility(View.VISIBLE);
            }

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
                            myApplicationObj.setTempTimezone(getString(R.string.PST));
                            myApplicationObj.setTempTimezoneID(0);
                            myApplicationObj.setStrPreference("PST");
                        } else if (preferences.getData().getTimeZone() == 1) {
                            timeZoneET.setText(getString(R.string.MST));
                            myApplicationObj.setTempTimezone(getString(R.string.MST));
                            myApplicationObj.setTempTimezoneID(1);
                            myApplicationObj.setStrPreference("America/Denver");
                        } else if (preferences.getData().getTimeZone() == 2) {
                            timeZoneET.setText(getString(R.string.CST));
                            myApplicationObj.setTempTimezone(getString(R.string.CST));
                            myApplicationObj.setTempTimezoneID(2);
                            myApplicationObj.setStrPreference("CST");
                        } else if (preferences.getData().getTimeZone() == 3) {
                            timeZoneET.setText(getString(R.string.EST));
                            myApplicationObj.setTempTimezone(getString(R.string.EST));
                            myApplicationObj.setTempTimezoneID(3);
                            myApplicationObj.setStrPreference("America/New_York");
                        } else if (preferences.getData().getTimeZone() == 4) {
                            timeZoneET.setText(getString(R.string.HST));
                            myApplicationObj.setTempTimezone(getString(R.string.HST));
                            myApplicationObj.setTempTimezoneID(4);
                            myApplicationObj.setStrPreference("HST");
                        } else if (preferences.getData().getTimeZone() == 5) {
                            timeZoneET.setText(getString(R.string.AST));
                            myApplicationObj.setTempTimezone(getString(R.string.AST));
                            myApplicationObj.setTempTimezoneID(5);
                            myApplicationObj.setStrPreference("AST");
                        }

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
                        Utils.displayAlert(userPreference.getError().getErrorDescription(), PreferencesActivity.this, "", userPreference.getError().getFieldErrors().get(0));
                    } else {
                        myApplicationObj.setTimezoneID(myApplicationObj.getTempTimezoneID());
                        myApplicationObj.setTimezone(myApplicationObj.getTempTimezone());
                        if (myApplicationObj.getTempTimezoneID() == 0) {
                            myApplicationObj.setStrPreference("PST");
                        } else if (myApplicationObj.getTempTimezoneID() == 1) {
                            myApplicationObj.setStrPreference("America/Denver");
                        } else if (myApplicationObj.getTempTimezoneID() == 2) {
                            myApplicationObj.setStrPreference("CST");
                        } else if (myApplicationObj.getTempTimezoneID() == 3) {
                            myApplicationObj.setStrPreference("America/New_York");
                        } else if (myApplicationObj.getTempTimezoneID() == 4) {
                            myApplicationObj.setStrPreference("HST");
                        } else if (myApplicationObj.getTempTimezoneID() == 5) {
                            myApplicationObj.setStrPreference("AST");
                        }
                        timeZoneET.setText(myApplicationObj.getTimezone());
                        Utils.showCustomToast(PreferencesActivity.this, "Timezone has been updated", R.drawable.ic_custom_tick, "authid");

                    }
                }
            }
        });

        dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
            @Override
            public void onChanged(ProfilesResponse profilesResponse) {
                dialog.dismiss();
                if (profilesResponse != null) {
//                    if(profilesResponse.getData().get(0).getEntityName() != null) {
//                        accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getEntityName()));
//                    }
////                    accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getEntityName()));
                    accountET.setText(Utils.capitalize(profilesResponse.getData().get(0).getFullName()));

                    if (profilesResponse.getStatus().equals("SUCCESS")) {
                        if (profilesResponse.getData().size() > 1) {
                            disableView.setVisibility(View.GONE);
                            accountDDIV.setVisibility(View.VISIBLE);
                            accountET.setClickable(true);
                            accountET.setEnabled(true);
                            Utils.setUpperHintColor(accountTIL, getColor(R.color.black));

                        } else {
                            Log.e("else", "else");
                            accountDDIV.setVisibility(View.GONE);
                            disableView.setVisibility(View.VISIBLE);
                            accountET.setClickable(false);
                            accountET.setEnabled(false);
                            Log.e("else", "else");
                            Utils.setUpperHintColor(accountTIL, getColor(R.color.xdark_gray));
                        }

                    }
                }
            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (apiError == null) {
                    dialog.dismiss();
                }
            }
        });

    }


}
package com.coyni.mapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.model.bank.SignOn;
import com.coyni.mapp.model.logout.LogoutResponse;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

public class IdentityVerificationBindingLayoutActivity extends BaseActivity {
    String strScreen = "";
    LinearLayout successCloseIV, underReviewCloseIV;
    MyApplication objMyApplication;
    CardView ivSuccessCV, idveriCardViewExitBtn, idveriDoneBtn;
    TextView contactUSTV;
    Long mLastClickTime = 0L;
    private LoginViewModel loginViewModel;
    private DisplayImageUtility displayImageUtility;
    private DatabaseHandler dbHandler;
    CustomerProfileViewModel customerProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_iv_binding_layout);
            initialization();
            initObserver();
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
                ControlMethod(strScreen);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            dbHandler = DatabaseHandler.getInstance(IdentityVerificationBindingLayoutActivity.this);
            displayImageUtility = DisplayImageUtility.getInstance(this);
            successCloseIV = findViewById(R.id.successCloseIV);
            ivSuccessCV = findViewById(R.id.ivSuccessCV);

            contactUSTV = findViewById(R.id.contactUSTV);
            idveriCardViewExitBtn = findViewById(R.id.idveriCardViewExitBtn);

            underReviewCloseIV = findViewById(R.id.underReviewCloseIV);
            idveriDoneBtn = findViewById(R.id.idveriDoneBtn);

            objMyApplication = (MyApplication) getApplicationContext();

            //customerProfileViewModel.meSignOn();

            Log.d("objMyApplication", "objMyApplication" + objMyApplication.getAccountType());

            ivSuccessCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (objMyApplication.getAccountType() == 2) {
//                        Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, BusinessDashboardActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(i);
//                    } else {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
//                    }
                }
            });

            successCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

            contactUSTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(Utils.mondayURL));
//                    startActivity(i);
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(IdentityVerificationBindingLayoutActivity.this,GetHelpActivity.class));
                }
            });

            idveriCardViewExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        showProgressDialog();
                        loginViewModel.logout();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            underReviewCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

            idveriDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "SUCCESS": {
                    findViewById(R.id.ivSuccessLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.ivFailedLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivUnderReviewLayout).setVisibility(View.GONE);
                }
                break;
                case "FAILED": {
                    findViewById(R.id.ivSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivFailedLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.ivUnderReviewLayout).setVisibility(View.GONE);
                }
                break;
                case "UNDER_REVIEW": {
                    findViewById(R.id.ivSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivFailedLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivUnderReviewLayout).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initObserver() {
        try {
            loginViewModel.getLogoutLiveData().observe(this, new Observer<LogoutResponse>() {
                @Override
                public void onChanged(LogoutResponse logoutResponse) {
                    dismissDialog();
                    if (logoutResponse != null) {
                        if (logoutResponse.getStatus().toLowerCase().equals("success")) {
                            onLogoutSuccess();
                        } else {
                            if (!logoutResponse.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(logoutResponse.getError().getErrorDescription(), IdentityVerificationBindingLayoutActivity.this, "", "");
                            } else {
                                Utils.displayAlert(logoutResponse.getError().getFieldErrors().get(0), IdentityVerificationBindingLayoutActivity.this, "", "");
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
//            @Override
//            public void onChanged(SignOn signOn) {
//                try {
//                    if (signOn != null) {
//                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
//                            objMyApplication.setSignOnData(signOn.getData());
//                            objMyApplication.setStrSignOnError("");
//                        } else {
//                            objMyApplication.setSignOnData(null);
//                            objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
    }

    private void onLogoutSuccess() {
        try {
            objMyApplication.setStrRetrEmail("");
            objMyApplication.clearUserData();
            dropAllTables();
            displayImageUtility.clearCache();
            Utils.setStrAuth("");
            Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, OnboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dropAllTables() {
        try {
            dbHandler.clearAllTables();
            SharedPreferences prefs = getSharedPreferences("DeviceID", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
package com.greenbox.coyni.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.greenbox.coyni.adapters.RetEmailAdapter;
import com.greenbox.coyni.model.logout.LogoutResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessAddNewAccountActivity;
import com.greenbox.coyni.view.business.BusinessAddNewBusinessAccountActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.List;

public class BindingLayoutActivity extends BaseActivity {
    String strScreen = "";
    LinearLayout lyClose, verifyAccountCloseLL;
    TextView tvEmail;
    MyApplication objMyApplication;
    CardView reTryAgainBtn, btnChangePassCV, nextGetStartedCV;
    CardView editEmailLogoutCV;
    DatabaseHandler dbHandler;
    RetEmailAdapter retEmailAdapter;
    RecyclerView retEmailRV;
    TextView txvVerifyName, txvVerifyDescription;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private LoginViewModel loginViewModel;
    private Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_binding_layout);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            initialization();
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
            lyClose = findViewById(R.id.lyClose);
            tvEmail = findViewById(R.id.tvEmail);
            reTryAgainBtn = findViewById(R.id.reTryAgainBtn);
            btnChangePassCV = findViewById(R.id.btnCV);
            nextGetStartedCV = findViewById(R.id.nextGetStartedCV);
            editEmailLogoutCV = findViewById(R.id.editEmailLogoutCV);
            verifyAccountCloseLL = findViewById(R.id.verifyAccountCloseLL);
            retEmailRV = findViewById(R.id.retEmailRV);
            txvVerifyName = findViewById(R.id.txv_verify_name);
            dbHandler = DatabaseHandler.getInstance(BindingLayoutActivity.this);
            txvVerifyDescription = findViewById(R.id.txv_verify_description);

            objMyApplication = (MyApplication) getApplicationContext();

            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                txvVerifyName.setText("Add Personal Account");
                txvVerifyDescription.setText(" Please follow the instructions below to create personal account.");
            }
            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            List<RetUserResData> usersData;
            if (objMyApplication.getObjRetUsers() != null) {
                usersData = objMyApplication.getObjRetUsers().getData();
                if (usersData != null && usersData.size() > 0) {
                    retEmailAdapter = new RetEmailAdapter(usersData, this);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                    retEmailRV.setLayoutManager(mLayoutManager);
                    retEmailRV.setItemAnimator(new DefaultItemAnimator());
                    retEmailRV.setAdapter(retEmailAdapter);
//                    tvEmail.setText(usersData.get(0).getEmail().replaceAll("(?<=.{4}).(?=.*@)", "*"));
//                    objMyApplication.setStrRetrEmail(usersData.get(0).getEmail());
                }
            }
            btnChangePassCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    loginViewModel.logout();
                }
            });

            lyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            reTryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, RetrieveEmailActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            editEmailLogoutCV.setOnClickListener(view -> {
                try {
                    objMyApplication.setStrRetrEmail("");
                    dropAllTables();
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            nextGetStartedCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT
                            || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                        identityVerificationViewModel.getPostAddCustomer();
                    } else {
                        Intent i = new Intent(BindingLayoutActivity.this, IdentityVerificationActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });

            verifyAccountCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initObservers();

    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "retEmailfound": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.changePasswordLayout).setVisibility(View.GONE);
                    findViewById(R.id.verifyYourAccount).setVisibility(View.GONE);
                }
                break;
                case "retEmailfail": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.changePasswordLayout).setVisibility(View.GONE);
                    findViewById(R.id.verifyYourAccount).setVisibility(View.GONE);
                }
                break;
                case "EditEmail": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.changePasswordLayout).setVisibility(View.GONE);
                    findViewById(R.id.verifyYourAccount).setVisibility(View.GONE);
                    objMyApplication.setStrRetrEmail("");
                    dropAllTables();
                }
                break;
                case "ChangePassword": {
                    findViewById(R.id.changePasswordLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.verifyYourAccount).setVisibility(View.GONE);
                    dropAllTables();
                }
                break;
                case "profileGetStarted": {
                    findViewById(R.id.changePasswordLayout).setVisibility(View.GONE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.verifyYourAccount).setVisibility(View.VISIBLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void initObservers() {
        try {
            identityVerificationViewModel.getBusinessAddCustomer().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {

                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {

                        if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT
                                || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                            Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                            objMyApplication.setLoginUserId(identityImageResponse.getData().getUserId());
                            objMyApplication.setStrEmail(identityImageResponse.getData().getEmail());
                            if (identityImageResponse.getData().getAccountType() != 0) {
                                objMyApplication.setAccountType(identityImageResponse.getData().getAccountType());
                            } else {
                                objMyApplication.setAccountType(Utils.PERSONAL_ACCOUNT);
                            }
                            objMyApplication.setDbaOwnerId(identityImageResponse.getData().getDbaOwnerId());
                            objMyApplication.setIsReserveEnabled(identityImageResponse.getData().isReserveEnabled());
                            Intent i = new Intent(BindingLayoutActivity.this, IdentityVerificationActivity.class);
                            i.putExtra("ADDPERSONAL", "true");
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(BindingLayoutActivity.this, IdentityVerificationActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } else {

                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BindingLayoutActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                                Utils.displayAlert(logoutResponse.getError().getErrorDescription(), BindingLayoutActivity.this, "", "");
                            } else {
                                Utils.displayAlert(logoutResponse.getError().getFieldErrors().get(0), BindingLayoutActivity.this, "", "");
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLogoutSuccess() {
        objMyApplication.setStrRetrEmail("");
        dropAllTables();
        overridePendingTransition(0, 0);
        Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}
package com.greenbox.coyni.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessAddNewAccountActivity;
import com.greenbox.coyni.view.business.BusinessAddNewBusinessAccountActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.util.List;

public class BindingLayoutActivity extends AppCompatActivity {
    String strScreen = "";
    LinearLayout lyClose, verifyAccountCloseLL;
    TextView tvEmail;
    MyApplication objMyApplication;
    CardView reTryAgainBtn, btnChangePassCV, nextGetStartedCV;
    CardView editEmailLogoutCV;
    SQLiteDatabase mydatabase;
    RetEmailAdapter retEmailAdapter;
    RecyclerView retEmailRV;
    TextView txvVerifyName,txvVerifyDescription;
    private IdentityVerificationViewModel identityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_binding_layout);
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
            txvVerifyDescription = findViewById(R.id.txv_verify_description);

            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();

            if (objMyApplication.getAccountType() == 2) {
                txvVerifyName.setText("Add Personal Account");
                txvVerifyDescription.setText(" Please follow the instructions below to create personal account.");
                identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
           }

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
                    dropAllTables();
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            });

            lyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
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
                    if(objMyApplication.getAccountType()==2){
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
//            mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
//            mydatabase.execSQL("DROP TABLE IF EXISTS tblRemember;");
//            mydatabase.execSQL("DROP TABLE IF EXISTS tblThumbPinLock;");
//            mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
//            mydatabase.execSQL("DROP TABLE IF EXISTS tblPermanentToken;");
//            mydatabase.execSQL("DROP TABLE IF EXISTS tblDontRemind;");
            mydatabase.execSQL("Delete from tblUserDetails;");
            mydatabase.execSQL("Delete from tblRemember;");
            mydatabase.execSQL("Delete from tblThumbPinLock;");
            mydatabase.execSQL("Delete from tblFacePinLock;");
            mydatabase.execSQL("Delete from tblPermanentToken;");
            mydatabase.execSQL("Delete from tblDontRemind;");
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

                        if(objMyApplication.getAccountType()==2){
                            Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                            Intent i = new Intent(BindingLayoutActivity.this, IdentityVerificationActivity.class);
                            i.putExtra("ADDPERSONAL","true");
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
    }
}
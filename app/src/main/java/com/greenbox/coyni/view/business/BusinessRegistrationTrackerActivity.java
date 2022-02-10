package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.BaseActivity;

public class BusinessRegistrationTrackerActivity extends BaseActivity {
    TextView caStartTV, dbaStartTV, boStartTV, addBankStartTV, aggrementsStartTV;
    Dialog choose;
    LinearLayout caCompleteLL, caIncompleteLL, dbaCompleteLL, dbaIncompleteLL, boCompleteLL, boIncompleteLL, addBankCompleteLL,
            addBankIncompleteLL, aggrementsCompleteLL, aggrementsIncompleteLL;
    Long mLastClickTime = 0L;
    BusinessTrackerResponse businessTrackerResponse;
    MyApplication objMyApplication;
    ImageView businessTrackerCloseIV,bagIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_tracker_account);

        TextView dashboardTV = findViewById(R.id.dashboardTV);
        dashboardTV.setOnClickListener(view -> {
            startActivity(new Intent(BusinessRegistrationTrackerActivity.this, BusinessDashboardActivity.class));
        });
        initFields();

    }

    private void dbaBotmsheetPopUp(final Context context) {
        try {
            choose = new Dialog(context);
            choose.requestWindowFeature(Window.FEATURE_NO_TITLE);
            choose.setContentView(R.layout.activity_dbainfo_btm_sheet);
            choose.setCancelable(true);
            Window window = choose.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            choose.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            choose.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            choose.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAbasicInformationAcivity.class);
        startActivity(intent);

    }

    private void initFields() {

        objMyApplication = (MyApplication) getApplicationContext();
        businessTrackerResponse = objMyApplication.getBusinessTrackerResponse();
        caStartTV = findViewById(R.id.caStartTV);
        dbaStartTV = findViewById(R.id.dbaStartTV);
        boStartTV = findViewById(R.id.boStartTV);
        addBankStartTV = findViewById(R.id.addBankStartTV);
        aggrementsStartTV = findViewById(R.id.aggrementsStartTV);
        businessTrackerCloseIV = findViewById(R.id.businessTrackerCloseIV);

        caCompleteLL = findViewById(R.id.caCompleteLL);
        caIncompleteLL = findViewById(R.id.caIncompleteLL);

        dbaCompleteLL = findViewById(R.id.dbaCompleteLL);
        dbaIncompleteLL = findViewById(R.id.dbaIncompleteLL);

        boCompleteLL = findViewById(R.id.boCompleteLL);
        boIncompleteLL = findViewById(R.id.boIncompleteLL);

        addBankCompleteLL = findViewById(R.id.addBankCompleteLL);
        addBankIncompleteLL = findViewById(R.id.addBankIncompleteLL);

        aggrementsCompleteLL = findViewById(R.id.aggrementsCompleteLL);
        aggrementsIncompleteLL = findViewById(R.id.aggrementsIncompleteLL);

        bagIV = findViewById(R.id.bagIV);
        bagIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this,MerchantsAgrementActivity.class);
                startActivity(intent);
            }
        });

        if (businessTrackerResponse.getData().isCompanyInfo()) {
            dbaStartTV.setVisibility(View.VISIBLE);
            caCompleteLL.setVisibility(View.VISIBLE);
            caIncompleteLL.setVisibility(View.GONE);
        } else {
            dbaStartTV.setVisibility(View.GONE);
            caCompleteLL.setVisibility(View.GONE);
            caIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (businessTrackerResponse.getData().isDbaInfo()) {
            boStartTV.setVisibility(View.VISIBLE);
            dbaCompleteLL.setVisibility(View.VISIBLE);
            dbaIncompleteLL.setVisibility(View.GONE);
        } else {
            boStartTV.setVisibility(View.GONE);
            dbaCompleteLL.setVisibility(View.GONE);
            dbaIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (businessTrackerResponse.getData().isBeneficialOwners()) {
            addBankStartTV.setVisibility(View.VISIBLE);
            boCompleteLL.setVisibility(View.VISIBLE);
            boIncompleteLL.setVisibility(View.GONE);
        } else {
            addBankStartTV.setVisibility(View.GONE);
            boCompleteLL.setVisibility(View.GONE);
            boIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (businessTrackerResponse.getData().isIsbankAccount()) {
            aggrementsStartTV.setVisibility(View.VISIBLE);
            addBankCompleteLL.setVisibility(View.VISIBLE);
            addBankIncompleteLL.setVisibility(View.GONE);
        } else {
            aggrementsStartTV.setVisibility(View.GONE);
            addBankCompleteLL.setVisibility(View.GONE);
            addBankIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (businessTrackerResponse.getData().isAgreementSigned()) {
            aggrementsCompleteLL.setVisibility(View.VISIBLE);
            addBankIncompleteLL.setVisibility(View.GONE);
        } else {
            aggrementsCompleteLL.setVisibility(View.GONE);
            aggrementsIncompleteLL.setVisibility(View.VISIBLE);
        }


        businessTrackerCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        caIncompleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, CompanyInformationActivity.class);
                startActivity(intent);
            }
        });
        dbaIncompleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (businessTrackerResponse.getData().isCompanyInfo()) {
                    dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
                }
            }
        });
        boIncompleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (businessTrackerResponse.getData().isDbaInfo()) {
                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AddBeneficialOwnerActivity.class);
                    startActivity(intent);
                }
            }
        });
        addBankIncompleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (businessTrackerResponse.getData().isBeneficialOwners()) {
                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AccountHasCreatedSucessful.class);
                    startActivity(intent);
                }
            }
        });
        aggrementsIncompleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (businessTrackerResponse.getData().isIsbankAccount()) {
//                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, MerchantsAgrementActivity.class);
//                    startActivity(intent);
                }
            }
        });

    }
}
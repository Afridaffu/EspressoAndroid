package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class BusinessRegistrationTrackerActivity extends BaseActivity {
    TextView caStartTV, dbaStartTV, boStartTV, addBankStartTV, aggrementsStartTV, caTV, caIncompleteTV;
    Dialog choose;
    LinearLayout caCompleteLL, caIncompleteLL, dbaCompleteLL, dbaIncompleteLL, boCompleteLL, boIncompleteLL, addBankCompleteLL,
            addBankIncompleteLL, aggrementsCompleteLL, aggrementsIncompleteLL;
    Long mLastClickTime = 0L;
    BusinessTrackerResponse businessTrackerResponse;
    MyApplication objMyApplication;
    ImageView businessTrackerCloseIV, caInProgressIV,bagIV;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    CompanyInfoResp dbaInfoResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_business_tracker_account);

            TextView dashboardTV = findViewById(R.id.dashboardTV);
            dashboardTV.setOnClickListener(view -> {
                startActivity(new Intent(BusinessRegistrationTrackerActivity.this, BusinessDashboardActivity.class));
            });
            initFields();
            initObservers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

            LinearLayout diffLL = choose.findViewById(R.id.diffLL);
            LinearLayout sameLL = choose.findViewById(R.id.sameLL);

            diffLL.setOnClickListener(view -> {
                choose.dismiss();
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                intent.putExtra("TYPE", "DIFF");
                startActivity(intent);
            });

            sameLL.setOnClickListener(view -> {
                choose.dismiss();
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                intent.putExtra("TYPE", "SAME");
                startActivity(intent);
            });

            choose.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private void initFields() {
        try {
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            businessTrackerResponse = objMyApplication.getBusinessTrackerResponse();
            caStartTV = findViewById(R.id.caStartTV);
            dbaStartTV = findViewById(R.id.dbaStartTV);
            boStartTV = findViewById(R.id.boStartTV);
            addBankStartTV = findViewById(R.id.addBankStartTV);
            aggrementsStartTV = findViewById(R.id.aggrementsStartTV);
            businessTrackerCloseIV = findViewById(R.id.businessTrackerCloseIV);

        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        objMyApplication = (MyApplication) getApplicationContext();
        businessTrackerResponse = objMyApplication.getBusinessTrackerResponse();
        caStartTV = findViewById(R.id.caStartTV);
        dbaStartTV = findViewById(R.id.dbaStartTV);
        boStartTV = findViewById(R.id.boStartTV);
        addBankStartTV = findViewById(R.id.addBankStartTV);
        aggrementsStartTV = findViewById(R.id.aggrementsStartTV);
        businessTrackerCloseIV = findViewById(R.id.businessTrackerCloseIV);

            caCompleteLL = findViewById(R.id.caCompleteLL);
            caTV = findViewById(R.id.caTV);
            caIncompleteLL = findViewById(R.id.caIncompleteLL);
            caIncompleteTV = findViewById(R.id.caIncompleteTV);
            caInProgressIV = findViewById(R.id.caInProgressIV);

            dbaCompleteLL = findViewById(R.id.dbaCompleteLL);
            dbaIncompleteLL = findViewById(R.id.dbaIncompleteLL);

            boCompleteLL = findViewById(R.id.boCompleteLL);
            boIncompleteLL = findViewById(R.id.boIncompleteLL);

            addBankCompleteLL = findViewById(R.id.addBankCompleteLL);
            addBankIncompleteLL = findViewById(R.id.addBankIncompleteLL);

            aggrementsCompleteLL = findViewById(R.id.aggrementsCompleteLL);
            aggrementsIncompleteLL = findViewById(R.id.aggrementsIncompleteLL);
        aggrementsCompleteLL = findViewById(R.id.aggrementsCompleteLL);
        aggrementsIncompleteLL = findViewById(R.id.aggrementsIncompleteLL);
        bagIV = findViewById(R.id.bagIV);

            if (businessTrackerResponse != null && businessTrackerResponse.getData().isCompanyInfo()) {
                dbaStartTV.setVisibility(View.VISIBLE);
                caCompleteLL.setVisibility(View.VISIBLE);
                caIncompleteLL.setVisibility(View.GONE);
            } else {
                dbaStartTV.setVisibility(View.GONE);
                caCompleteLL.setVisibility(View.GONE);
                caIncompleteLL.setVisibility(View.VISIBLE);
            }

            if (businessTrackerResponse != null && businessTrackerResponse.getData().isDbaInfo()) {
                boStartTV.setVisibility(View.VISIBLE);
                dbaCompleteLL.setVisibility(View.VISIBLE);
                dbaIncompleteLL.setVisibility(View.GONE);
            } else {
                boStartTV.setVisibility(View.GONE);
                dbaCompleteLL.setVisibility(View.GONE);
                dbaIncompleteLL.setVisibility(View.VISIBLE);
            }

            if (businessTrackerResponse != null && businessTrackerResponse.getData().isBeneficialOwners()) {
                addBankStartTV.setVisibility(View.VISIBLE);
                boCompleteLL.setVisibility(View.VISIBLE);
                boIncompleteLL.setVisibility(View.GONE);
            } else {
                addBankStartTV.setVisibility(View.GONE);
                boCompleteLL.setVisibility(View.GONE);
                boIncompleteLL.setVisibility(View.VISIBLE);
            }

            if (businessTrackerResponse != null && businessTrackerResponse.getData().isIsbankAccount()) {
                aggrementsStartTV.setVisibility(View.VISIBLE);
                addBankCompleteLL.setVisibility(View.VISIBLE);
                addBankIncompleteLL.setVisibility(View.GONE);
            } else {
                aggrementsStartTV.setVisibility(View.GONE);
                addBankCompleteLL.setVisibility(View.GONE);
                addBankIncompleteLL.setVisibility(View.VISIBLE);
            }

            if (businessTrackerResponse != null && businessTrackerResponse.getData().isAgreementSigned()) {
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
                if (objMyApplication.getBusinessTrackerResponse().getData().isCompanyInfo()
                        && dbaInfoResponse != null && dbaInfoResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
                } else {
                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                    intent.putExtra("TYPE", "EXIST");
                    startActivity(intent);
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
                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, MerchantsAgrementActivity.class);
                    startActivity(intent);
                }
            }
        });

        bagIV = findViewById(R.id.bagIV);
        bagIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this,MerchantsAgrementActivity.class);
                startActivity(intent);
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
                        Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, MerchantsAgrementActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
                @Override
                public void onChanged(BusinessTrackerResponse btResp) {

                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTrackerResponse(btResp);
                            businessTrackerResponse = btResp;
                            reloadTrackerDashboard(btResp);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getGetDBAInfoResponse().observe(this, new Observer<CompanyInfoResp>() {
                @Override
                public void onChanged(CompanyInfoResp companyInfoResp) {
                    if (companyInfoResp != null) {
                        if (companyInfoResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                CompanyInfoResp.Data cir = companyInfoResp.getData();
                                dbaInfoResponse = companyInfoResp;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            dbaInfoResponse = companyInfoResp;
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getGetCompanyInfoResponse().observe(this, new Observer<CompanyInfoResp>() {
                @Override
                public void onChanged(CompanyInfoResp companyInfoResp) {
                    if (companyInfoResp != null) {
                        if (companyInfoResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                objMyApplication.setCompanyInfoResp(companyInfoResp);
                                CompanyInfoResp.Data cir = companyInfoResp.getData();
                                if (cir.getName() != null && !cir.getName().equals("")
                                        || cir.getEmail() != null && !cir.getEmail().equals("")
                                        || cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")
                                        || cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")
                                        || cir.getIdentificationType() != null && !cir.getIdentificationType().equals("")
                                        || cir.getSsnOrEin() != null && !cir.getSsnOrEin().equals("")
                                        || cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")
                                        || cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")
                                        || cir.getCity() != null && !cir.getCity().equals("")
                                        || cir.getState() != null && !cir.getState().equals("")
                                        || cir.getZipCode() != null && !cir.getZipCode().equals("")
                                        || cir.getRequiredDocumets().size() > 0) {

                                    caTV.setTextColor(getResources().getColor(R.color.primary_green));
                                    caIncompleteTV.setTextColor(getResources().getColor(R.color.primary_green));
                                    caIncompleteTV.setText("In Progress");
                                    caStartTV.setVisibility(GONE);
                                    caInProgressIV.setVisibility(VISIBLE);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            businessIdentityVerificationViewModel.getBusinessTracker();
            businessIdentityVerificationViewModel.getCompanyInfo();
            businessIdentityVerificationViewModel.getDBAInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadTrackerDashboard(BusinessTrackerResponse businessTrackerResponse) {

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
            aggrementsIncompleteLL.setVisibility(GONE);
            addBankIncompleteLL.setVisibility(View.GONE);
        } else {
            aggrementsCompleteLL.setVisibility(View.GONE);
            aggrementsIncompleteLL.setVisibility(View.VISIBLE);
        }

    }
}
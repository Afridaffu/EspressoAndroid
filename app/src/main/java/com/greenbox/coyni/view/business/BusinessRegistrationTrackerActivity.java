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

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOIdResp;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.IdentityVerificationActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class BusinessRegistrationTrackerActivity extends BaseActivity {
    private TextView caStartTV, dbaStartTV, boStartTV, addBankStartTV, aggrementsStartTV, caTV, caIncompleteTV, dbaTV, dbaIncompleteTV,
            boTV, boIncompleteTV, addBankTV, addBankIncompleteTV, aggrementsTV, aggrementsIncompleteTV, appFinishedTV, infoTV;
    private Dialog choose;
    private LinearLayout caCompleteLL, caIncompleteLL, dbaCompleteLL, dbaIncompleteLL, boCompleteLL, boIncompleteLL, addBankCompleteLL,
            addBankIncompleteLL, aggrementsCompleteLL, aggrementsIncompleteLL;
    private Long mLastClickTime = 0L;
    private BusinessTrackerResponse businessTrackerResponse;
    private MyApplication objMyApplication;
    private ImageView businessTrackerCloseIV, caInProgressIV, dbaInProgressIV, boInProgressIV, addBankInProgressIV, aggrementsInProgressIV;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private DBAInfoResp dbaInfoResponse;
    private String addBusiness = "false";
    private String addDBA = "false";
    private LoginViewModel loginViewModel;
    private String boAPICallFrom = "RESUME";
    private CardView mReviewCv;
    private boolean review = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_business_tracker_account);

            TextView dashboardTV = findViewById(R.id.dashboardTV);

            if (getIntent().getStringExtra("ADDBUSINESS") != null) {
                loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                addBusiness = getIntent().getStringExtra("ADDBUSINESS");
                LogUtils.d("addBusiness", "addBusiness" + addBusiness);
            }

            if (getIntent().getStringExtra("ADDDBA") != null) {
                loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                addDBA = getIntent().getStringExtra("ADDDBA");
                LogUtils.d("addDBA", "addDBA" + addDBA);

            }


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
                intent.putExtra("FROM", "TRACKER");
                intent.putExtra("TYPE", "DIFF");
                startActivity(intent);
            });

            sameLL.setOnClickListener(view -> {
                choose.dismiss();
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                intent.putExtra("FROM", "TRACKER");
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

            if (getIntent().getStringExtra("FROM").equalsIgnoreCase("login"))
                businessTrackerCloseIV.setVisibility(GONE);

            objMyApplication = (MyApplication) getApplicationContext();
            businessTrackerResponse = objMyApplication.getBusinessTrackerResponse();
            caStartTV = findViewById(R.id.caStartTV);
            dbaStartTV = findViewById(R.id.dbaStartTV);
            boStartTV = findViewById(R.id.boStartTV);
            addBankStartTV = findViewById(R.id.addBankStartTV);
            aggrementsStartTV = findViewById(R.id.aggrementsStartTV);

            infoTV = findViewById(R.id.infoTV);
            appFinishedTV = findViewById(R.id.appFinishedTV);

            caCompleteLL = findViewById(R.id.caCompleteLL);
            caTV = findViewById(R.id.caTV);
            caIncompleteLL = findViewById(R.id.caIncompleteLL);
            caIncompleteTV = findViewById(R.id.caIncompleteTV);
            caInProgressIV = findViewById(R.id.caInProgressIV);

            dbaCompleteLL = findViewById(R.id.dbaCompleteLL);
            dbaIncompleteLL = findViewById(R.id.dbaIncompleteLL);
            dbaTV = findViewById(R.id.dbaTV);
            dbaIncompleteTV = findViewById(R.id.dbaIncompleteTV);
            dbaInProgressIV = findViewById(R.id.dbaInProgressIV);

            boCompleteLL = findViewById(R.id.boCompleteLL);
            boIncompleteLL = findViewById(R.id.boIncompleteLL);
            boTV = findViewById(R.id.boTV);
            boIncompleteTV = findViewById(R.id.boIncompleteTV);
            boInProgressIV = findViewById(R.id.boInProgressIV);

            addBankCompleteLL = findViewById(R.id.addBankCompleteLL);
            addBankIncompleteLL = findViewById(R.id.addBankIncompleteLL);
            addBankTV = findViewById(R.id.addBankTV);
            addBankIncompleteTV = findViewById(R.id.addBankIncompleteTV);
            addBankInProgressIV = findViewById(R.id.addBankInProgressIV);

            aggrementsCompleteLL = findViewById(R.id.aggrementsCompleteLL);
            aggrementsIncompleteLL = findViewById(R.id.aggrementsIncompleteLL);
            aggrementsTV = findViewById(R.id.aggrementsTV);
            aggrementsIncompleteTV = findViewById(R.id.aggrementsIncompleteTV);
            aggrementsInProgressIV = findViewById(R.id.aggrementsInProgressIV);
            mReviewCv = findViewById(R.id.reviewCv);

            if (businessTrackerResponse != null) {
                reloadTrackerDashboard(businessTrackerResponse);
            }

            businessTrackerCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addBusiness.equalsIgnoreCase("true")) {
                        loginViewModel.postChangeAccount(objMyApplication.getLoginUserId());
                    } else {
                        finish();
                    }
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
                    intent.putExtra("FROM", "TRACKER");
                    startActivity(intent);
                }
            });

//            caCompleteLL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, CompanyInformationActivity.class);
//                    startActivity(intent);
//
//                }
//            });

            dbaIncompleteLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (objMyApplication.getBusinessTrackerResponse().getData().isCompanyInfo()) {
                        if (dbaInfoResponse != null && dbaInfoResponse.getData().getId() == 0) {
                            dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
                        } else if (dbaInfoResponse != null && dbaInfoResponse.getData().getId() != 0) {
                            Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                            intent.putExtra("FROM", "TRACKER");
                            intent.putExtra("TYPE", "EXIST");
                            startActivity(intent);
                        }
                    }
                }
            });

//            dbaCompleteLL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    if (objMyApplication.getBusinessTrackerResponse().getData().isCompanyInfo()) {
//
//                        dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
//
//                    }
//                }
//            });

            boIncompleteLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (businessTrackerResponse.getData().isDbaInfo()) {
                        boAPICallFrom = "INCOMPLETE";
                        businessIdentityVerificationViewModel.getBeneficialOwners();
                    }
                }
            });

            addBankIncompleteLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (businessTrackerResponse.getData().isBeneficialOwners()) {
                            Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AddBankAccount.class);
                            startActivity(intent);
                        }

//                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AddBankAccount.class);
//                    startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            mReviewCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (review) {
                        Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, ReviewApplicationActivity.class);
                        startActivity(intent);
                    }
                }
            });


//            boCompleteLL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    boAPICallFrom = "INCOMPLETE";
//                    businessIdentityVerificationViewModel.getBeneficialOwners();
//
//                }
//            });

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
            loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse btResp) {
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d("btResp", "btResp" + btResp);
                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            finish();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getGetDBAInfoResponse().observe(this, new Observer<DBAInfoResp>() {
                @Override
                public void onChanged(DBAInfoResp dbaInfoResp) {
                    if (dbaInfoResp != null) {
                        dbaInfoResponse = dbaInfoResp;
                        objMyApplication.setDbaInfoResp(dbaInfoResp);

                        DBAInfoResp.Data dia = dbaInfoResp.getData();
                        if (dia.getName() != null && !dia.getName().equals("")
                                || dia.getEmail() != null && !dia.getEmail().equals("")
                                || dia.getPhoneNumberDto() != null && dia.getPhoneNumberDto().getPhoneNumber() != null && !dia.getPhoneNumberDto().getPhoneNumber().equals("")
                                || dia.getBusinessType() != null && !dia.getBusinessType().equals("")
                                || dia.getWebsite() != null && !dia.getWebsite().equals("")
                                || dia.getMonthlyProcessingVolume() != null && !dia.getMonthlyProcessingVolume().equals("")
                                || dia.getHighTicket() != null && !dia.getHighTicket().equals("")
                                || dia.getAverageTicket() != null && !dia.getAverageTicket().equals("")
                                || dia.getAddressLine1() != null && !dia.getAddressLine1().equals("")
                                || dia.getAddressLine2() != null && !dia.getAddressLine2().equals("")
                                || dia.getCity() != null && !dia.getCity().equals("")
                                || dia.getState() != null && !dia.getState().equals("")
                                || dia.getZipCode() != null && !dia.getZipCode().equals("")
                                || dia.getRequiredDocuments() != null && dia.getRequiredDocuments().size() > 0) {

                            dbaTV.setTextColor(getResources().getColor(R.color.primary_green));
                            dbaIncompleteTV.setTextColor(getResources().getColor(R.color.primary_green));
                            dbaIncompleteTV.setText("In Progress");
                            dbaStartTV.setVisibility(GONE);
                            dbaInProgressIV.setVisibility(VISIBLE);

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
//                                businessIdentityVerificationViewModel.getDBAInfo();

                                objMyApplication.setCompanyInfoResp(companyInfoResp);
                                CompanyInfoResp.Data cir = companyInfoResp.getData();
                                if (cir.getName() != null && !cir.getName().equals("")
                                        || cir.getEmail() != null && !cir.getEmail().equals("")
                                        || cir.getPhoneNumberDto() != null && cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")
                                        || cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")
                                        || cir.getIdentificationType() != null && !cir.getIdentificationType().equals("")
                                        || cir.getSsnOrEin() != null && !cir.getSsnOrEin().equals("")
                                        || cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")
                                        || cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")
                                        || cir.getCity() != null && !cir.getCity().equals("")
                                        || cir.getState() != null && !cir.getState().equals("")
                                        || cir.getZipCode() != null && !cir.getZipCode().equals("")
                                        || cir.getRequiredDocumets() != null && cir.getRequiredDocumets().size() > 0) {

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

        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersResponse().observe(this, new Observer<BOResp>() {
                @Override
                public void onChanged(BOResp boResp) {

                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success") && boResp.getData().size() > 0) {
                            objMyApplication.setBeneficialOwnersResponse(boResp);
                            if (boAPICallFrom.equals("INCOMPLETE")) {
                                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AdditionalBeneficialOwnersActivity.class);
                                startActivity(intent);
                            } else {
                                boTV.setTextColor(getResources().getColor(R.color.primary_green));
                                boIncompleteTV.setTextColor(getResources().getColor(R.color.primary_green));
                                boIncompleteTV.setText("In Progress");
                                boStartTV.setVisibility(GONE);
                                boInProgressIV.setVisibility(VISIBLE);
                            }
                        } else {
                            if (boAPICallFrom.equals("INCOMPLETE"))
                                businessIdentityVerificationViewModel.postBeneficialOwnersID();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersIDResponse().observe(this, new Observer<BOIdResp>() {
                @Override
                public void onChanged(BOIdResp boIdResp) {

                    if (boIdResp != null) {
                        if (boIdResp.getStatus().toLowerCase().toString().equals("success")) {
                            startActivity(new Intent(BusinessRegistrationTrackerActivity.this, AddBeneficialOwnerActivity.class)
                                    .putExtra("FROM", "ADD_BO")
                                    .putExtra("ID", boIdResp.getData().getId()));
                        } else {
                            Utils.displayAlert(boIdResp.getError().getErrorDescription(), BusinessRegistrationTrackerActivity.this, "", boIdResp.getError().getFieldErrors().get(0));
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
//            businessIdentityVerificationViewModel.getCompanyInfo();
//            businessIdentityVerificationViewModel.getDBAInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadTrackerDashboard(BusinessTrackerResponse businessTrackerResponse) {

        businessIdentityVerificationViewModel.getCompanyInfo();
        businessIdentityVerificationViewModel.getDBAInfo();
        boAPICallFrom = "RESUME";
        businessIdentityVerificationViewModel.getBeneficialOwners();
        LogUtils.d("BusinessTrackerResponse", "BusinessTrackerResponse" + businessTrackerResponse);

        if (businessTrackerResponse.getData().isCompanyInfo()) {
            dbaInProgressIV.setVisibility(GONE);
            dbaStartTV.setVisibility(View.VISIBLE);
            dbaIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
            caCompleteLL.setVisibility(View.VISIBLE);
            caIncompleteLL.setVisibility(View.GONE);
        } else {
            dbaIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color));
            dbaStartTV.setVisibility(View.GONE);
            caCompleteLL.setVisibility(View.GONE);
            caIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (addDBA.equalsIgnoreCase("true")) {
            caIncompleteLL.setVisibility(View.GONE);
        }

        if (businessTrackerResponse.getData().isDbaInfo()) {
            boInProgressIV.setVisibility(GONE);
            boStartTV.setVisibility(View.VISIBLE);
            boIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
            dbaCompleteLL.setVisibility(View.VISIBLE);
            dbaIncompleteLL.setVisibility(View.GONE);
        } else {
            boStartTV.setVisibility(View.GONE);
            boIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color));
            dbaCompleteLL.setVisibility(View.GONE);
            dbaIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (businessTrackerResponse.getData().isBeneficialOwners()) {
            addBankInProgressIV.setVisibility(GONE);
            addBankStartTV.setVisibility(View.VISIBLE);
            addBankIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
            boCompleteLL.setVisibility(View.VISIBLE);
            boIncompleteLL.setVisibility(View.GONE);
        } else {
            addBankStartTV.setVisibility(View.GONE);
            addBankIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color));
            boCompleteLL.setVisibility(View.GONE);
            boIncompleteLL.setVisibility(View.VISIBLE);
        }

        if (businessTrackerResponse.getData().isIsbankAccount()) {
            aggrementsInProgressIV.setVisibility(GONE);
            aggrementsStartTV.setVisibility(View.VISIBLE);
            aggrementsIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
            addBankCompleteLL.setVisibility(View.VISIBLE);
            addBankIncompleteLL.setVisibility(View.GONE);
        } else {
            aggrementsStartTV.setVisibility(View.GONE);
            aggrementsIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color));
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

        if (businessTrackerResponse != null && businessTrackerResponse.getData().isCompanyInfo() && businessTrackerResponse.getData().isDbaInfo() && businessTrackerResponse.getData().isBeneficialOwners()
                && businessTrackerResponse.getData().isIsbankAccount() && businessTrackerResponse.getData().isAgreementSigned()) {
            review = true;
            mReviewCv.setVisibility(VISIBLE);
            appFinishedTV.setVisibility(VISIBLE);
            infoTV.setVisibility(GONE);

        }
    }

    @Override
    public void onBackPressed() {
        if (!getIntent().getStringExtra("FROM").equals("login"))
            super.onBackPressed();
    }
}
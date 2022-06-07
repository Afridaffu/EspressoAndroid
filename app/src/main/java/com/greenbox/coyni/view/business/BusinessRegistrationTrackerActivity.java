package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.BeneficialOwners.BOIdResp;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessRegistrationTrackerActivity extends BaseActivity implements OnKeyboardVisibilityListener {
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
    private boolean addBusiness = false, addDBA = false, new_DBA = false;
    public static boolean isAddBusinessCalled = false;
    public static boolean isAddDbaCalled = false;
    private String boAPICallFrom = "RESUME";
    private LoginViewModel loginViewModel;
    private CardView mReviewCv;
    private boolean review = false, isBOStart = false;
    private ImageView bagIV;
    private int dbaID = 0;
    private boolean isNewCompany = true;
    private boolean isTrackerCall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            setContentView(R.layout.activity_business_tracker_account);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            TextView dashboardTV = findViewById(R.id.dashboardTV);

            if (getIntent().getBooleanExtra(Utils.ADD_BUSINESS, false)) {
                addBusiness = getIntent().getBooleanExtra(Utils.ADD_BUSINESS, false);
            }

            if (getIntent().getBooleanExtra(Utils.ADD_DBA, false)) {
                addDBA = getIntent().getBooleanExtra(Utils.ADD_DBA, false);
            }
            if (getIntent().getBooleanExtra(Utils.NEW_DBA, false)) {
                new_DBA = getIntent().getBooleanExtra(Utils.NEW_DBA, false);
            }

            if (getIntent().getBooleanExtra(Utils.IS_TRACKER, false)) {
                isTrackerCall = getIntent().getBooleanExtra(Utils.IS_TRACKER, false);
            }

            if (getIntent().getIntExtra("dbaId", 0) != 0) {
                dbaID = getIntent().getIntExtra("dbaId", 0);
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
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            //businessTrackerResponse = objMyApplication.getBusinessTrackerResponse();
            caStartTV = findViewById(R.id.caStartTV);
            dbaStartTV = findViewById(R.id.dbaStartTV);
            boStartTV = findViewById(R.id.boStartTV);
            addBankStartTV = findViewById(R.id.addBankStartTV);
            aggrementsStartTV = findViewById(R.id.aggrementsStartTV);
            businessTrackerCloseIV = findViewById(R.id.businessTrackerCloseIV);
            bagIV = findViewById(R.id.bagIV);
            setKeyboardVisibilityListener(BusinessRegistrationTrackerActivity.this);

//            if (getIntent().getStringExtra("FROM").equalsIgnoreCase("login"))
//                businessTrackerCloseIV.setVisibility(GONE);

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

//            if (businessTrackerResponse != null) {
//                reloadTrackerDashboard(businessTrackerResponse);
//            }

            if (addDBA) {
                caIncompleteLL.setVisibility(GONE);
                dbaStartTV.setVisibility(VISIBLE);
            } else {
                caIncompleteLL.setVisibility(VISIBLE);
            }

            businessTrackerCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addBusiness) {
                        showProgressDialog();
                        loginViewModel.postChangeAccount(objMyApplication.getLoginUserId());
                    } else {
                        Intent dashboardIntent = new Intent(BusinessRegistrationTrackerActivity.this, BusinessDashboardActivity.class);
                        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(dashboardIntent);
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
                    if (addBusiness) {
                        intent.putExtra("isNew", isNewCompany);
                    }
                    startActivityForResult(intent, 1234);

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
                    try {
                        if (addDBA) {
                            Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                            intent.putExtra("FROM", "TRACKER");
                            intent.putExtra("TYPE", "EXIST");
                            intent.putExtra(Utils.ADD_DBA, addDBA);
                            intent.putExtra(Utils.NEW_DBA, new_DBA);
                            intent.putExtra(Utils.COMPANY_ID, getIntent().getIntExtra(Utils.COMPANY_ID, 0));
                            startActivity(intent);

                        } else if (objMyApplication.getBusinessTrackerResponse().getData().isCompanyInfo()) {
                            if (dbaInfoResponse != null && dbaInfoResponse.getData() != null && dbaInfoResponse.getData().getId() == 0) {
                                dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
                            } else if (dbaInfoResponse != null && dbaInfoResponse.getData() != null && dbaInfoResponse.getData().getId() != 0) {
                                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
                                intent.putExtra("FROM", "TRACKER");
                                intent.putExtra("TYPE", "EXIST");
                                startActivity(intent);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
//                        if (dbaInfoResponse != null && dbaInfoResponse.getData().getId() == 0) {
//                            dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
//                        } else if (dbaInfoResponse != null && dbaInfoResponse.getData().getId() != 0) {
//                            Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAInfoAcivity.class);
//                            intent.putExtra("FROM", "TRACKER");
//                            intent.putExtra("TYPE", "EXIST");
//                            startActivity(intent);
//                        }
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
                    LogUtils.d(TAG, "mReviewCv" + addDBA + ",,,,," + addBusiness);
                    if (review) {
                        if (addDBA) {
                            startActivity(new Intent(BusinessRegistrationTrackerActivity.this, ReviewApplicationActivity.class)
                                    .putExtra(Utils.ADD_BUSINESS, true)
                                    .putExtra(Utils.ADD_DBA, true));

                        } else if (addBusiness) {

                            startActivity(new Intent(BusinessRegistrationTrackerActivity.this, ReviewApplicationActivity.class)
                                    .putExtra(Utils.ADD_BUSINESS, true)
                                    .putExtra(Utils.ADD_DBA, false));

                        } else {
                            startActivity(new Intent(BusinessRegistrationTrackerActivity.this, ReviewApplicationActivity.class)
                                    .putExtra(Utils.ADD_BUSINESS, false)
                                    .putExtra(Utils.ADD_DBA, false));
                        }

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

            businessIdentityVerificationViewModel.getBusinessType();

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
                            isNewCompany = false;
                            objMyApplication.setBusinessTrackerResponse(btResp);
                            businessTrackerResponse = btResp;
                            businessIdentityVerificationViewModel.getCompanyInfo();
                            businessIdentityVerificationViewModel.getDBAInfo();
//                            if(!btResp.getData().isCompanyInfo() || !btResp.getData().isDbaInfo()) {
//                                businessIdentityVerificationViewModel.getCompanyInfo();
//                            } else if(btResp.getData().isCompanyInfo() || !btResp.getData().isDbaInfo()) {
//                                businessIdentityVerificationViewModel.getDBAInfo();
//                            }
                            reloadTrackerDashboard(btResp);

                        } else {
                            Utils.displayAlert(btResp.getError().getErrorDescription(), BusinessRegistrationTrackerActivity.this, "", btResp.getError().getFieldErrors().get(0));
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
                    dismissDialog();
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d("btResp", "btResp" + btResp);
                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            isAddBusinessCalled = false;
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
//                        else {
//                            dbaTV.setTextColor(getResources().getColor(R.color.primary_black));
//                            dbaIncompleteTV.setTextColor(getResources().getColor(R.color.primary_black));
//                            dbaIncompleteTV.setText("Incomplete");
//                            dbaStartTV.setVisibility(VISIBLE);
//                            dbaInProgressIV.setVisibility(GONE);
//                        }
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
                                        || cir.getIdentificationType() != null && !cir.getIdentificationType().equals("") && !cir.getIdentificationType().equals("0")
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

                                } else {
                                    caTV.setTextColor(getResources().getColor(R.color.primary_black));
                                    caIncompleteTV.setTextColor(getResources().getColor(R.color.dark_grey));
                                    caIncompleteTV.setText("Incomplete");
                                    caStartTV.setVisibility(VISIBLE);
                                    caInProgressIV.setVisibility(GONE);

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
                    dismissDialog();
                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success") && boResp.getData().size() > 0) {

                            BOResp finalBOResp = new BOResp();
                            List<BOResp.BeneficialOwner> finalBOList = new ArrayList();
                            List<BOResp.BeneficialOwner> noNameList = new ArrayList<>();

                            for (int i = 0; i < boResp.getData().size(); i++) {
                                BOResp.BeneficialOwner bo = boResp.getData().get(i);

                                try {
                                    if (bo.getFirstName().equals("") || bo.getFirstName() == null || bo.getLastName().equals("") || bo.getLastName() == null
                                            || bo.getOwnershipParcentage() <= 0) {
                                        noNameList.add(boResp.getData().get(i));
                                    } else {
                                        finalBOList.add(boResp.getData().get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    noNameList.add(boResp.getData().get(i));
                                }
                            }

                            finalBOResp.setData(finalBOList);

                            if (noNameList.size() > 0) {
                                for (int i = 0; i < noNameList.size(); i++) {
                                    businessIdentityVerificationViewModel.deleteBeneficialOwner(noNameList.get(i).getId());
                                }
                            }

                            if (finalBOResp.getData().size() > 0) {

                                objMyApplication.setBeneficialOwnersResponse(finalBOResp);
                                if (boAPICallFrom.equals("INCOMPLETE")) {
                                    Log.e("One", "One");
                                    Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AdditionalBeneficialOwnersActivity.class);
                                    startActivity(intent);
                                    Log.e("Two", "Two");
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
                                if (isBOStart) {
                                    boTV.setTextColor(getResources().getColor(R.color.primary_black));
                                    boIncompleteTV.setTextColor(getResources().getColor(R.color.dark_grey));
                                    boIncompleteTV.setText("Incomplete");
                                    boStartTV.setVisibility(VISIBLE);
                                    boInProgressIV.setVisibility(GONE);
                                }
                            }
                        } else {
                            if (boAPICallFrom.equals("INCOMPLETE"))
                                businessIdentityVerificationViewModel.postBeneficialOwnersID();
                            if (isBOStart) {
                                boTV.setTextColor(getResources().getColor(R.color.primary_black));
                                boIncompleteTV.setTextColor(getResources().getColor(R.color.dark_grey));
                                boIncompleteTV.setText("Incomplete");
                                boStartTV.setVisibility(VISIBLE);
                                boInProgressIV.setVisibility(GONE);
                            }
                        }
                    }

                    boIncompleteLL.setClickable(true);
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

        try {
            businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
                @Override
                public void onChanged(BusinessTypeResp businessTypeResp) {

                    if (businessTypeResp != null) {
                        if (businessTypeResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTypeResp(businessTypeResp);
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
//            if (Utils.isKeyboardVisible)
//                Utils.hideKeypad(this);
            if (!addBusiness || isAddBusinessCalled || isAddDbaCalled || isTrackerCall) {
                showProgressDialog();
                businessIdentityVerificationViewModel.getBusinessTracker();
            } else {
                //caIncompleteLL.set
                if (!addDBA) {
                    dbaIncompleteLL.setClickable(false);
                }
                if(addDBA) {
                    dbaIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
                }else{
                    dbaIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color));
                }
                addBankIncompleteLL.setClickable(false);
                boIncompleteLL.setClickable(false);
                aggrementsIncompleteLL.setClickable(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadTrackerDashboard(BusinessTrackerResponse businessTrackerResponse) {
        boAPICallFrom = "RESUME";
        boIncompleteLL.setClickable(false);
        businessIdentityVerificationViewModel.getBeneficialOwners();
        LogUtils.d("BusinessTrackerResponse", "BusinessTrackerResponse" + new Gson().toJson(businessTrackerResponse));

        if (businessTrackerResponse.getData().isCompanyInfo()) {
            dbaInProgressIV.setVisibility(GONE);
            dbaStartTV.setVisibility(View.VISIBLE);
            dbaIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
            caIncompleteLL.setVisibility(View.GONE);
            if (addDBA) {
                caCompleteLL.setVisibility(GONE);
            } else {
                caCompleteLL.setVisibility(VISIBLE);
            }
        } else {
            dbaStartTV.setVisibility(View.GONE);
            dbaIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color));
            caCompleteLL.setVisibility(View.GONE);
            caIncompleteLL.setVisibility(View.VISIBLE);
        }
        if (businessTrackerResponse.getData().isDbaInfo()) {
            boInProgressIV.setVisibility(GONE);
            boStartTV.setVisibility(View.VISIBLE);
            boIncompleteLL.setBackground(getResources().getDrawable(R.drawable.bg_white_color_primary_border));
            dbaCompleteLL.setVisibility(View.VISIBLE);
            dbaIncompleteLL.setVisibility(View.GONE);
            isBOStart = true;
        } else {
            isBOStart = false;
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
        LogUtils.d(TAG, "reviewbutton" + addDBA + businessTrackerResponse);

        if (businessTrackerResponse != null && businessTrackerResponse.getData().isCompanyInfo() && businessTrackerResponse.getData().isDbaInfo() && businessTrackerResponse.getData().isBeneficialOwners()
                && businessTrackerResponse.getData().isIsbankAccount() && businessTrackerResponse.getData().isAgreementSigned()) {
            review = true;
            mReviewCv.setVisibility(VISIBLE);
            appFinishedTV.setVisibility(VISIBLE);
            infoTV.setVisibility(GONE);
            bagIV.setImageDrawable(getResources().getDrawable(R.drawable.idve_completed));
        } else {
            if (addDBA) {
                if (businessTrackerResponse != null && businessTrackerResponse.getData().isDbaInfo() && businessTrackerResponse.getData().isBeneficialOwners()
                        && businessTrackerResponse.getData().isIsbankAccount() && businessTrackerResponse.getData().isAgreementSigned()) {
                    LogUtils.d(TAG, "iffff" + addDBA + businessTrackerResponse);
                    review = true;
                    mReviewCv.setVisibility(VISIBLE);
                    appFinishedTV.setVisibility(VISIBLE);
                    infoTV.setVisibility(GONE);
                    bagIV.setImageDrawable(getResources().getDrawable(R.drawable.idve_completed));

                } else {
                    LogUtils.d(TAG, "elseeeeee" + addDBA + businessTrackerResponse);
                    review = false;
                    mReviewCv.setVisibility(GONE);
                    appFinishedTV.setVisibility(GONE);
                    infoTV.setVisibility(VISIBLE);
                    bagIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_appl_inprogress));
                }
            } else {
                review = false;
                mReviewCv.setVisibility(GONE);
                appFinishedTV.setVisibility(GONE);
                infoTV.setVisibility(VISIBLE);
                bagIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_appl_inprogress));
            }
        }
    }

    @Override
    public void onBackPressed() {
//        if (!getIntent().getStringExtra("FROM").equals("login")) {
//            super.onBackPressed();
//        }
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
//            pageOneView.setVisibility(VISIBLE);
//            pageTwoView.setVisibility(VISIBLE);
        } else {
//            pageOneView.setVisibility(GONE);
//            pageTwoView.setVisibility(GONE);
            Utils.isKeyboardVisible = false;
        }
    }

}
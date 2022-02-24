package com.greenbox.coyni.view.business;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.greenbox.coyni.adapters.BankAccountsRecyclerAdapter;
import com.greenbox.coyni.adapters.BenificialOwnersRecyclerAdapter;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsData;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.model.bank.BankAccountsDataModel;
import com.greenbox.coyni.model.bank.BankItems;
import com.greenbox.coyni.model.bank.BanksResponseModel;

import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.submit.ApplicationSubmitRequest;
import com.greenbox.coyni.model.submit.RequiredDocumentsDb;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.ApplicationSubmissionViewModel;
import com.greenbox.coyni.viewmodel.BankAccountsViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewApplicationActivity extends AppCompatActivity {
    TextView edit1, edit2, edit3;
    CheckBox agreeCB;
    public boolean isNextEnabled = false, isagreed = false;
    public CardView submitCv;
    private TextView mCompanyNameTx, mBusinessEntityTx ,mEINTx, mEmailTx, mPhoneNumberTx, mAddressTx, mArticleDateTx, mEINDateTx, mW9DateTx;
    private TextView mDbNameTx,mBusinessTypeTx, mTimeZoneTx,mWebsiteTx,mMonthlyProcVolumeTx,mHighTicketTx,mAverageTicketTx,mCustomerServiceEmailTx,mCustomerServicePhoneTx,mDbAddressLineTx,mDbFillingDateTx;
    private TextView mPrivacyVno,mTermsVno,mMerchantsVno;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private BankAccountsViewModel bankAccountsViewModel;
    DashboardViewModel dashboardViewModel;
    private BankAccountsRecyclerAdapter accountsRecyclerAdapter;
    private List<BankItems> bankItems = new ArrayList<BankItems>();
    private BenificialOwnersRecyclerAdapter benificialOwnersRecyclerAdapter;
    private List<BOResp.BeneficialOwner> beneficialOwnerList=new ArrayList<>();
    private RecyclerView bankRecyclerView,boRecyclerView;
    private TextView noBanksTv;
    LinearLayout banksLL;
    DBAInfoResp dbaInfoResponse;
    ApplicationSubmissionViewModel applicationSubmissionViewModel;
    private int monthlyProcVolume=0;
    private List<Item> items=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_application);
        initFields();
        initObservers();
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2TV);
        edit3 = findViewById(R.id.edit3TV);
        agreeCB = findViewById(R.id.agreeCB);
        submitCv = findViewById(R.id.submitCV);
        noBanksTv=findViewById(R.id.noBanksTV);
        banksLL=findViewById(R.id.banksLL);
        bankRecyclerView=findViewById(R.id.banksRecycler);
        boRecyclerView=findViewById(R.id.boRecycler);

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, CompanyInformationActivity.class);
                startActivity(intent);
            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, DBAInfoAcivity.class);
                startActivity(intent);
            }
        });
        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, ReviewApplicationActivity.class);
                startActivity(intent);
            }
        });
        agreeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((agreeCB.isEnabled())) {
                    submitCv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    submitCv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        });
        submitCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveApplicationData();
                Intent intent = new Intent(ReviewApplicationActivity.this, ReviewApplicationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveApplicationData() {
        ApplicationSubmitRequest request = new ApplicationSubmitRequest();
        request.setCompanyName(mCompanyNameTx.getText().toString());
        request.setCompanyBusinessEntity(mBusinessEntityTx.getText().toString());
        //request.setDbTimezone(Utils.changeActionType);
        request.setCompanySsnOrEin(mEINTx.getText().toString());
        request.setCompanyEmail(mEmailTx.getText().toString());
        PhNoWithCountryCode phone = new PhNoWithCountryCode();
        phone.setCountryCode(Utils.strCCode);
        phone.setPhoneNumber(mPhoneNumberTx.getText().toString());
        request.setCompanyPhoneNumberDto(phone);
        ArrayList<ApplicationSubmitRequest.RequiredDocumets> companyRequiredDocumets = new ArrayList<>();
        companyRequiredDocumets.get(0).setUpdatedAt(mArticleDateTx.getText().toString());
        companyRequiredDocumets.get(1).setUpdatedAt(mEINDateTx.getText().toString());
        companyRequiredDocumets.get(2).setUpdatedAt(mW9DateTx.getText().toString());
        request.setCompanyRequiredDocumets(companyRequiredDocumets);
//        companyRequiredDocumets.set(0,)
//        companyRequiredDocumets.setUpdatedAt(mArticleDateTx.getText().toString());
//        companyRequiredDocumets.setUpdatedAt(mEINDateTx.getText().toString());
//        companyRequiredDocumets.setUpdatedAt(mW9DateTx.getText().toString());


        request.setDbName(mDbNameTx.getText().toString());
        request.setDbBusinessType(mBusinessTypeTx.getText().toString());
        request.setDbWebsite(mWebsiteTx.getText().toString());
        request.setDbMonthlyProcessingVolume(mMonthlyProcVolumeTx.getText().toString());
        request.setDbHighTicket(mHighTicketTx.getText().toString());
        request.setDbAverageTicket(mAverageTicketTx.getText().toString());
        request.setDbEmail(mCustomerServiceEmailTx.getText().toString());
        PhNoWithCountryCode phone1 = new PhNoWithCountryCode();
        phone1.setCountryCode(Utils.strCCode);
        phone1.setPhoneNumber(mCustomerServicePhoneTx.getText().toString());
        request.setDbPhoneNumberDto(phone1);
        request.setCompanyAddressLine1(mAddressTx.getText().toString());
        ArrayList<ApplicationSubmitRequest.RequiredDocumets> requiredDocumentsDbs=new ArrayList<>();
        requiredDocumentsDbs.get(0).setUpdatedAt(mDbFillingDateTx.getText().toString());
        request.setCompanyRequiredDocumets(requiredDocumentsDbs);

        //
        BankAccountsDataModel bankAccountsDataModel=new BankAccountsDataModel();
        bankAccountsDataModel.setItems(bankItems);
        request.setData(bankAccountsDataModel);
        //
        ApplicationSubmitRequest.BOResp boResp= new ApplicationSubmitRequest.BOResp();
        boResp.setData(beneficialOwnerList);
        request.setBoResp(boResp);
        //
        AgreementsData agreementsData=new AgreementsData();
        agreementsData.setItems(items);
        request.setData(agreementsData);
        applicationSubmissionViewModel.postApplicationData(request);
    }

    private void initFields() {
        mCompanyNameTx = (TextView) findViewById(R.id.companyNameTx);
        mBusinessEntityTx = (TextView) findViewById(R.id.businesEntityTx);
        mTimeZoneTx = (TextView) findViewById(R.id.timeZoneTx);
        mEINTx = (TextView) findViewById(R.id.einTx);
        mEmailTx = (TextView) findViewById(R.id.emailTx);
        mPhoneNumberTx = (TextView) findViewById(R.id.phoneNumberTx);
        mAddressTx = (TextView) findViewById(R.id.addressTx);
        mArticleDateTx = (TextView) findViewById(R.id.uploaded_articles_date);
        mEINDateTx = (TextView) findViewById(R.id.uploaded_ein_date);
        mW9DateTx = (TextView) findViewById(R.id.uploaded_w9_form_date);
        mDbNameTx = (TextView) findViewById(R.id.db_name);
        mBusinessTypeTx = (TextView) findViewById(R.id.business_type);
        mWebsiteTx = (TextView) findViewById(R.id.website);
        mMonthlyProcVolumeTx = (TextView) findViewById(R.id.monthly_process_volume);
        mHighTicketTx = (TextView) findViewById(R.id.high_ticket);
        mAverageTicketTx = (TextView) findViewById(R.id.average_ticket);
        mCustomerServiceEmailTx = (TextView) findViewById(R.id.customer_service_email);
        mCustomerServicePhoneTx = (TextView) findViewById(R.id.customer_service_phone);
        mDbAddressLineTx = (TextView) findViewById(R.id.db_address_line);
        mDbFillingDateTx = (TextView) findViewById(R.id.db_filling_date);
        mPrivacyVno=(TextView)findViewById(R.id.privacy_policy);
        mTermsVno=(TextView)findViewById(R.id.terms_of_service);
        mMerchantsVno=(TextView)findViewById(R.id.merchant_agreements);


        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getCompanyInfo();
        businessIdentityVerificationViewModel.getDBAInfo();
        businessIdentityVerificationViewModel.getBeneficialOwners();
        bankAccountsViewModel = new ViewModelProvider(this).get(BankAccountsViewModel.class);
        bankAccountsViewModel.getBankAccountsData();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.meAgreementsById();
       // applicationSubmissionViewModel = new ViewModelProvider(this).get(ApplicationSubmissionViewModel.class);


    }

    public void initObservers() {
        try {
            businessIdentityVerificationViewModel.getGetCompanyInfoResponse().observe(this, new Observer<CompanyInfoResp>() {
                @Override
                public void onChanged(CompanyInfoResp companyInfoResp) {
                    if (companyInfoResp != null) {
                        if (companyInfoResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                CompanyInfoResp.Data cir = companyInfoResp.getData();
                                if (cir.getName() != null && !cir.getName().equals("")) {
                                    mCompanyNameTx.setText(cir.getName());
                                }

                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    mEmailTx.setText(cir.getEmail());
                                }

                                if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
                                    mPhoneNumberTx.setText(cir.getPhoneNumberDto().getPhoneNumber());
                                }

                                if (cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")) {
                                    mBusinessEntityTx.setText(cir.getBusinessEntity());
                                }

                                if (cir.getSsnOrEin() != null && !cir.getSsnOrEin().equals("")) {
                                    mEINTx.setText(cir.getSsnOrEin());

                                }

                                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                                    mAddressTx.setText(cir.getAddressLine1());
                                }

                                if (cir.getRequiredDocumets().size() > 0) {
                                    for (int i = 0; i < cir.getRequiredDocumets().size(); i++) {
                                        if (cir.getRequiredDocumets().get(i).getIdentityId() == 5) {
                                            mArticleDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
                                        } else if (cir.getRequiredDocumets().get(i).getIdentityId() == 6) {
                                            mEINDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
                                        } else if (cir.getRequiredDocumets().get(i).getIdentityId() == 7) {
                                            mW9DateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
                                        }
                                    }
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
            bankAccountsViewModel.getBankAccountsMutableLiveData().observe(this, new Observer<BanksResponseModel>() {
                @Override
                public void onChanged(BanksResponseModel banksResponseModel) {
                    if (banksResponseModel != null) {
                        if (banksResponseModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                            if (banksResponseModel.getData().getItems().size() > 0) {
                                banksLL.setVisibility(View.VISIBLE);
                                noBanksTv.setVisibility(View.GONE);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewApplicationActivity.this);
                                accountsRecyclerAdapter = new BankAccountsRecyclerAdapter(ReviewApplicationActivity.this, banksResponseModel.getData().getItems());
                                bankItems = banksResponseModel.getData().getItems();
                                bankRecyclerView.setLayoutManager(layoutManager);
                                bankRecyclerView.setAdapter(accountsRecyclerAdapter);

                            } else {
                                banksLL.setVisibility(View.GONE);
                                noBanksTv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //Utils.displayAlert(banksResponseModel.getError().getErrorDescription(), ReviewApplicationActivity.this, "", banksResponseModel.getError().getFieldErrors().get(0));
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
                        if (dbaInfoResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                DBAInfoResp.Data cir = dbaInfoResp.getData();
                                dbaInfoResponse = dbaInfoResp;
                                if (cir.getName() != null && !cir.getName().equals("")) {
                                    mDbNameTx.setText(cir.getName());
                                }

                                if (cir.getBusinessType() != null && !cir.getBusinessType().equals("")) {
                                    mBusinessTypeTx.setText(cir.getBusinessType());
                                }
                                if (cir.getTimeZone() != 0) {
                                    mTimeZoneTx.setText(cir.getTimeZone());
                                }

                                if (cir.getWebsite() != null && !cir.getWebsite().equals("")) {
                                    mWebsiteTx.setText(cir.getWebsite().toString());
                                }

                                if (cir.getMonthlyProcessingVolume() != null && !cir.getMonthlyProcessingVolume().equals("")) {
                                   // monthlyProcVolume=cir.getMonthlyProcessingVolume();
                                    mMonthlyProcVolumeTx.setText(cir.getMonthlyProcessingVolume().toString());
                                }

                                if (cir.getHighTicket() != null && !cir.getHighTicket().equals("")) {
                                    mHighTicketTx.setText(cir.getHighTicket().toString());
                                }

                                if (cir.getAverageTicket() != null && !cir.getAverageTicket().equals("")) {
                                    mAverageTicketTx.setText(cir.getAverageTicket().toString());
                                }
                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    mCustomerServiceEmailTx.setText(cir.getEmail().toString());
                                }
                                if (cir.getPhoneNumberDto() != null && !cir.getPhoneNumberDto().equals("")) {
                                    mCustomerServicePhoneTx.setText(cir.getPhoneNumberDto().toString());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            dbaInfoResponse = dbaInfoResp;
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dashboardViewModel.getAgreementsMutableLiveData().observe(this, new Observer<Agreements>() {
                @Override
                public void onChanged(Agreements agreements) {
                    try {
                        Log.e("act", agreements.getStatus());
                        if (agreements.getStatus().contains("SUCCESS")) {
                            items=agreements.getData().getItems();
                            if (agreements.getData().getItems() != null && agreements.getData().getItems().size() > 0) {
                                for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                    if (agreements.getData().getItems().get(i).getSignatureType() == 0) {
                                        mTermsVno.setText(agreements.getData().getItems().get(i).getDocumentVersion());
                                    }
                                    if (agreements.getData().getItems().get(i).getSignatureType() == 1) {
                                        mPrivacyVno.setText(agreements.getData().getItems().get(i).getDocumentVersion());
                                    }
                                    if (agreements.getData().getItems().get(i).getSignatureType() == 5) {
                                        mMerchantsVno.setText(agreements.getData().getItems().get(i).getDocumentVersion());

                                    }
                                }
                               
                            }

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersResponse().observe(this, new Observer<BOResp>() {
                @Override
                public void onChanged(BOResp boResp) {
                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                List<BOResp.BeneficialOwner> boList=boResp.getData();
                                if(boList.size()>0) {
                                    banksLL.setVisibility(View.VISIBLE);
                                    noBanksTv.setVisibility(View.GONE);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewApplicationActivity.this);
                                    benificialOwnersRecyclerAdapter = new BenificialOwnersRecyclerAdapter(ReviewApplicationActivity.this,boList);
                                    beneficialOwnerList = boResp.getData();
                                    boRecyclerView.setLayoutManager(layoutManager);
                                    boRecyclerView.setAdapter(benificialOwnersRecyclerAdapter);
                                    List<BOResp.BeneficialOwner> beneficialOwner = boResp.getData();
                                    if (beneficialOwner.get(0).getFirstName() != null && !beneficialOwner.get(0).equals("")) {
                                      //  mCompanyNameTx.setText(cir.getName());
                                    }

//                                    if (cir.getEmail() != null && !cir.getEmail().equals("")) {
//                                        mEmailTx.setText(cir.getEmail());
//                                    }
//
//                                    if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
//                                        mPhoneNumberTx.setText(cir.getPhoneNumberDto().getPhoneNumber());
//                                    }
//
//                                    if (cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")) {
//                                        mBusinessEntityTx.setText(cir.getBusinessEntity());
//                                    }
//
//                                    if (cir.getSsnOrEin() != null && !cir.getSsnOrEin().equals("")) {
//                                        mEINTx.setText(cir.getSsnOrEin());
//
//                                    }
//
//                                    if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
//                                        mAddressTx.setText(cir.getAddressLine1());
//                                    }
//
//                                    if (cir.getRequiredDocumets().size() > 0) {
//                                        for (int i = 0; i < cir.getRequiredDocumets().size(); i++) {
//                                            if (cir.getRequiredDocumets().get(i).getIdentityId() == 5) {
//                                                mArticleDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
//                                            } else if (cir.getRequiredDocumets().get(i).getIdentityId() == 6) {
//                                                mEINDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
//                                            } else if (cir.getRequiredDocumets().get(i).getIdentityId() == 7) {
//                                                mW9DateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
//                                            }
//                                        }
//                                    }
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


}

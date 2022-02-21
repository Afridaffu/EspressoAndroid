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
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.model.bank.BankItems;
import com.greenbox.coyni.model.bank.BanksResponseModel;

import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.submit.ApplicationSubmitRequest;
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
    private RecyclerView bankRecyclerView;
    private TextView noBanksTv;
    LinearLayout banksLL;
    DBAInfoResp dbaInfoResponse;
    ApplicationSubmissionViewModel applicationSubmissionViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_application);
        initFields();
        getCompanyInfo();
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2TV);
        edit3 = findViewById(R.id.edit3TV);
        agreeCB = findViewById(R.id.agreeCB);
        submitCv = findViewById(R.id.submitCV);
        noBanksTv=findViewById(R.id.noBanksTV);
        banksLL=findViewById(R.id.banksLL);
        bankRecyclerView=findViewById(R.id.banksRecycler);


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
//        companyRequiredDocumets.set(0,)
//        companyRequiredDocumets.setUpdatedAt(mArticleDateTx.getText().toString());
//        companyRequiredDocumets.setUpdatedAt(mEINDateTx.getText().toString());
//        companyRequiredDocumets.setUpdatedAt(mW9DateTx.getText().toString());
        request.setCompanyRequiredDocumets(companyRequiredDocumets);
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
        bankAccountsViewModel = new ViewModelProvider(this).get(BankAccountsViewModel.class);
        bankAccountsViewModel.getBankAccountsData();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.meAgreementsById();
       // applicationSubmissionViewModel = new ViewModelProvider(this).get(ApplicationSubmissionViewModel.class);


    }

//    public void getCompanyInfo() {
//        try {
//            companyInfoViewModel.getCompanyInfoMutableLiveData().observe(this, new Observer<CompanyInfoResponse>() {
//                @Override
//                public void onChanged(CompanyInfoResponse companyInfoResponse) {
//                    if (companyInfoResponse != null) {
//                        if (companyInfoResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
//                            if (companyInfoResponse.getData() != null) {
//                                documentArrayList = companyInfoResponse.getData().getRequiredDocumets();
//                                mCompanyNameTx.setText("");
//                                mBusinessEntityTx.setText(companyInfoResponse.getData().getBusinessEntity());
//                                mTimeZoneTx.setText("");
//                                mEINTx.setText(companyInfoResponse.getData().getSsnOrEin());
//                                mEmailTx.setText(companyInfoResponse.getData().getEmail());
//                                mPhoneNumberTx.setText(companyInfoResponse.getData().getPhoneNumberDto().getPhoneNumber());
//                                mAddressTx.setText(companyInfoResponse.getData().getAddressLine1());
//                                for (int i = 0; i < documentArrayList.size(); i++) {
//                                    if (i == 0)
//                                        mArticleDateTx.setText(companyInfoResponse.getData().getRequiredDocumets().get(i).getUpdatedAt());
//                                    if (i == 1)
//                                        mEINDateTx.setText(companyInfoResponse.getData().getRequiredDocumets().get(i).getUpdatedAt());
//                                    if (i == 2)
//                                        mW9DateTx.setText(companyInfoResponse.getData().getRequiredDocumets().get(i).getUpdatedAt());
//
//                            }
//                        }
//                    } else {
//                        // Utils.displayAlert(companyInfoResponse.getError().getErrorDescription(), ReviewApplicationActivity.this, "", companyInfoResponse.getError().getFieldErrors().get(0));
//                    }
//                }
//            }
//        });
//    } catch(Exception e)
//    {
//        e.printStackTrace();
//    }
//}
    public void getCompanyInfo() {
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
                            if (agreements.getData().getItems() != null && agreements.getData().getItems().size() > 0) {
                                for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                    if (agreements.getData().getItems().get(i).getSignatureType() == 0) {
                                        mPrivacyVno.setText(agreements.getData().getItems().get(i).getDocumentVersion());

                                    }
                                    if (agreements.getData().getItems().get(i).getSignatureType() == 1) {
                                        mTermsVno.setText(agreements.getData().getItems().get(i).getDocumentVersion());

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

    }


}

package com.greenbox.coyni.view.business;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.greenbox.coyni.model.cards.business.BusinessCardResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.submit.ApplicationSubmitRequest;
import com.greenbox.coyni.model.submit.ApplicationSubmitResponseModel;
import com.greenbox.coyni.model.submit.RequiredDocumentsDb;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.ConfirmPasswordActivity;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.viewmodel.ApplicationSubmissionViewModel;
import com.greenbox.coyni.viewmodel.BankAccountsViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewApplicationActivity extends BaseActivity {
    private TextView edit1, edit2, edit3;
    private CheckBox agreeCB;
    private boolean isNextEnabled = false, isagreed = false;
    private CardView submitCv;
    private TextView mCompanyNameTx, mBusinessEntityTx, mEINTx, mEmailTx, mPhoneNumberTx, mAddressTx, mArticleDateTx, mEINDateTx, mW9DateTx;
    private TextView mDbNameTx, mBusinessTypeTx, mTimeZoneTx, mWebsiteTx, mMonthlyProcVolumeTx, mHighTicketTx, mAverageTicketTx, mCustomerServiceEmailTx, mCustomerServicePhoneTx, mDbAddressLineTx, mDbFillingDateTx;
    private TextView mPrivacyVno, mTermsVno, mMerchantsVno;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private BankAccountsViewModel bankAccountsViewModel;
    private DashboardViewModel dashboardViewModel;
    private BankAccountsRecyclerAdapter accountsRecyclerAdapter;
    private List<BankItems> bankItems = new ArrayList<BankItems>();
    private BenificialOwnersRecyclerAdapter benificialOwnersRecyclerAdapter;
    private List<BOResp.BeneficialOwner> beneficialOwnerList = new ArrayList<>();
    private RecyclerView bankRecyclerView, boRecyclerView;
    private TextView noBanksTv;
    private LinearLayout banksLL;
    private DBAInfoResp dbaInfoResponse;
    private ApplicationSubmissionViewModel applicationSubmissionViewModel;
    private int monthlyProcVolume = 0;
    private List<Item> items = new ArrayList<>();
    private ArrayList<CompanyInfoResp.RequiredDocumets> companyReqDocList = new ArrayList<>();
    private ArrayList<DBAInfoResp.RequiredDocumets> dbReqDocList = new ArrayList<>();
    private String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    private String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    private ImageView mPrivacyImg, mTermsImg, mAgreementsImg;
    private ProgressDialog progressDialog;
    private boolean isAgree = false;
    private LoginViewModel loginViewModel;
    private MyApplication objMyApplication;
    private String mCompanyName="", mBusinessEntity="", mEIN="", mEmail="", mPhoneNumber="", mAddress="", mArticleDate="", mEINDate="", mW9Date="";
    private String mDbName="", mBusinessType="", mTimeZone="", mWebsite="", mMonthlyProcVolume="", mHighTicket="", mAverageTicket="", mCustomerServiceEmail="", mCustomerServicePhone="", mDbAddressLine="", mDbFillingDate="";
    private boolean addbusiness=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_application);

        objMyApplication = (MyApplication) getApplicationContext();

        initFields();
        initObservers();

    }

    private void saveApplicationData() {
        mCompanyName=mCompanyNameTx.getText().toString().trim();
        mBusinessEntity=mBusinessEntityTx.getText().toString().trim();
        mEIN=mEINTx.getText().toString().trim();
        mEmail=mEmailTx.getText().toString().trim();
        mPhoneNumber=mPhoneNumberTx.getText().toString().trim();

        //
        mDbName=mDbNameTx.getText().toString().trim();
        mBusinessType=mBusinessTypeTx.getText().toString().trim();
        mWebsite=mWebsiteTx.getText().toString().trim();
        mTimeZone=mTimeZoneTx.getText().toString().trim();
        mMonthlyProcVolume=mMonthlyProcVolumeTx.getText().toString().trim();
        mHighTicket=mHighTicketTx.getText().toString().trim();
        mAverageTicket=mAverageTicketTx.getText().toString().trim();
        mCustomerServiceEmail=mCustomerServiceEmailTx.getText().toString().trim();
        mCustomerServicePhone=mCustomerServicePhoneTx.getText().toString().trim();
        mAddress=mAddressTx.getText().toString().trim();
        ApplicationSubmitRequest request = new ApplicationSubmitRequest();
        request.setCompanyName(mCompanyName);
        request.setCompanyBusinessEntity(mBusinessEntity);
        request.setCompanySsnOrEin(mEIN);
        request.setCompanyEmail(mEmail);
        PhNoWithCountryCode phone = new PhNoWithCountryCode();
        phone.setCountryCode(Utils.strCCode);
        phone.setPhoneNumber(mPhoneNumber);
        request.setCompanyPhoneNumberDto(phone);
        request.setCompanyRequiredDocumets(companyReqDocList);
//        companyRequiredDocumets.setUpdatedAt(mArticleDateTx.getText().toString());
//        companyRequiredDocumets.setUpdatedAt(mEINDateTx.getText().toString());
//        companyRequiredDocumets.setUpdatedAt(mW9DateTx.getText().toString());


        request.setDbName(mDbName);
        request.setDbBusinessType(mBusinessType);
        request.setDbWebsite(mWebsite);
        request.setDbTimezone(mTimeZone);
        request.setDbMonthlyProcessingVolume(mMonthlyProcVolume);
        request.setDbHighTicket(mHighTicket);
        request.setDbAverageTicket(mAverageTicket);
        request.setDbEmail(mCustomerServiceEmail);
        PhNoWithCountryCode phone1 = new PhNoWithCountryCode();
        phone1.setCountryCode(Utils.strCCode);
        phone1.setPhoneNumber(mCustomerServicePhone);
        request.setDbPhoneNumberDto(phone1);
        request.setCompanyAddressLine1(mAddress);
        request.setRequiredDocuments1(dbReqDocList);

        //
        BankAccountsDataModel bankAccountsDataModel = new BankAccountsDataModel();
        bankAccountsDataModel.setItems(bankItems);
        request.setData(bankAccountsDataModel);
        //
        ApplicationSubmitRequest.BOResp boResp = new ApplicationSubmitRequest.BOResp();
        boResp.setData(beneficialOwnerList);
        request.setBoResp(boResp);
        //
        AgreementsData agreementsData = new AgreementsData();
        agreementsData.setItems(items);
        request.setData(agreementsData);
        //
        request.setAgree(isAgree);
        applicationSubmissionViewModel.postApplicationData(request);
    }

    private void initFields() {
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2TV);
        edit3 = findViewById(R.id.edit3TV);
        agreeCB = findViewById(R.id.agreeCB);
        submitCv = findViewById(R.id.submitCV);
        noBanksTv = findViewById(R.id.noBanksTV);
        banksLL = findViewById(R.id.banksLL);
        bankRecyclerView = findViewById(R.id.banksRecycler);
        boRecyclerView = findViewById(R.id.boRecycler);

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
                Intent intent = new Intent(ReviewApplicationActivity.this, AdditionalBeneficialOwnersActivity.class);
                startActivity(intent);
            }
        });
        agreeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((agreeCB.isEnabled())) {
                    isAgree = true;
                    submitCv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    submitCv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        });

        submitCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = Utils.showProgressDialog(ReviewApplicationActivity.this);
                saveApplicationData();
                 if(addbusiness) {
                     loginViewModel = new ViewModelProvider(ReviewApplicationActivity.this).get(LoginViewModel.class);
                     loginViewModel.postChangeAccount(objMyApplication.getLoginUserId());
                 } else {

                     Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                     startActivity(intent);
                 }
            }
        });

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
        mPrivacyVno = (TextView) findViewById(R.id.privacy_policy);
        mTermsVno = (TextView) findViewById(R.id.terms_of_service);
        mMerchantsVno = (TextView) findViewById(R.id.merchant_agreements);
        mPrivacyImg = (ImageView) findViewById(R.id.privacy);
        mTermsImg = (ImageView) findViewById(R.id.terms);
        mAgreementsImg = (ImageView) findViewById(R.id.merchant_agreem);

        mPrivacyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyURL));
                startActivity(browserIntent);
            }
        });

        mTermsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tosURL));
                startActivity(browserIntent);
            }
        });

//        mAgreementsImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getCompanyInfo();
        businessIdentityVerificationViewModel.getDBAInfo();
        businessIdentityVerificationViewModel.getBeneficialOwners();
        bankAccountsViewModel = new ViewModelProvider(this).get(BankAccountsViewModel.class);
        bankAccountsViewModel.getBankAccountsData();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.meAgreementsById();
        applicationSubmissionViewModel = new ViewModelProvider(this).get(ApplicationSubmissionViewModel.class);


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
                                companyReqDocList = cir.getRequiredDocumets();
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
                                dbReqDocList = cir.getRequiredDocuments();
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
                            items = agreements.getData().getItems();
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
                    dialog.dismiss();
                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                List<BOResp.BeneficialOwner> boList = boResp.getData();
                                if (boList.size() > 0) {
                                    banksLL.setVisibility(View.VISIBLE);
                                    noBanksTv.setVisibility(View.GONE);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewApplicationActivity.this);
                                    benificialOwnersRecyclerAdapter = new BenificialOwnersRecyclerAdapter(ReviewApplicationActivity.this, boList);
                                    beneficialOwnerList = boResp.getData();
                                    boRecyclerView.setLayoutManager(layoutManager);
                                    boRecyclerView.setAdapter(benificialOwnersRecyclerAdapter);
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
            applicationSubmissionViewModel.getPostCompanyInfoResponse().observe(this, new Observer<ApplicationSubmitResponseModel>() {
                @Override
                public void onChanged(ApplicationSubmitResponseModel applicationSubmitResponseModel) {
                    progressDialog.dismiss();
                    if (applicationSubmitResponseModel != null && applicationSubmitResponseModel.getStatus().toString().toLowerCase().equals("success")) {
                        saveApplicationData();
                    } else {

                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse btResp) {
                    dialog.dismiss();
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d("btResp","btResp"+btResp);
                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            //finish();
                            Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                            startActivity(intent);

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

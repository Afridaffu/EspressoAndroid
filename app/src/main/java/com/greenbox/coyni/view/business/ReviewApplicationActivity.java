package com.greenbox.coyni.view.business;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AddNewBusinessAccountDBAAdapter;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.BankAccountsRecyclerAdapter;
import com.greenbox.coyni.adapters.BenificialOwnersRecyclerAdapter;
import com.greenbox.coyni.adapters.PastAgreeListAdapter;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.submit.ApplicationSubmitRequest;
import com.greenbox.coyni.model.submit.ApplicationSubmitResponseModel;
import com.greenbox.coyni.model.summary.Agreements;
import com.greenbox.coyni.model.summary.ApplicationSummaryModelResponse;
import com.greenbox.coyni.model.summary.Bankaccount;
import com.greenbox.coyni.model.summary.BeneficialOwnerInfo;
import com.greenbox.coyni.model.summary.CompanyInfo;
import com.greenbox.coyni.model.summary.DbaInfo;
import com.greenbox.coyni.model.summary.Item1;
import com.greenbox.coyni.model.summary.RequiredDocument;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AgreementsActivity;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.ApplicationSubmissionViewModel;
import com.greenbox.coyni.viewmodel.BankAccountsViewModel;
import com.greenbox.coyni.viewmodel.BusinessApplicationSummaryViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewApplicationActivity extends BaseActivity implements BenificialOwnersRecyclerAdapter.OnSelectListner  {
    private TextView edit1, edit2, edit3;
    private CheckBox agreeCB;
    private boolean isNextEnabled = false, isagreed = false;
    private CardView submitCv;
    private TextView mCompanyNameTx, mBusinessEntityTx, mEINTx, mEmailTx, mPhoneNumberTx, mAddressTx, mArticleDateTx, mEINDateTx, mW9DateTx;
    private TextView mDbNameTx, mBusinessTypeTx, mTimeZoneTx, mWebsiteTx, mMonthlyProcVolumeTx, mHighTicketTx, mAverageTicketTx, mCustomerServiceEmailTx, mCustomerServicePhoneTx, mDbAddressLineTx, mDbFillingDateTx;
    private TextView mPrivacyVno, mTermsVno, mMerchantsVno;
    private BankAccountsRecyclerAdapter accountsRecyclerAdapter;
    private List<com.greenbox.coyni.model.summary.Item> bankItems = new ArrayList<>();
    private BenificialOwnersRecyclerAdapter benificialOwnersRecyclerAdapter;
    private List<BeneficialOwnerInfo> beneficialOwnerList = new ArrayList<>();
    private RecyclerView bankRecyclerView, boRecyclerView;
    private TextView noBanksTv, noBoTV;
    private LinearLayout banksLL, boLL;
    private LinearLayout uploadArticlesLL, uploadEINLL, uploadW9LL, dbaFillingLL,llDBADocuments;
    private ApplicationSubmissionViewModel applicationSubmissionViewModel;
    private BusinessApplicationSummaryViewModel summaryViewModel;
    private int monthlyProcVolume = 0;
    private List<Item1> agreements = new ArrayList<>();
    private List<RequiredDocument> companyReqDocList = new ArrayList<>();
    private List<Object> dbReqDocList = new ArrayList<>();
    private String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    private String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    private ImageView mPrivacyImg, mTermsImg, mAgreementsImg;
    private LinearLayout llPrivacy, llTerms, llMerchant;
    private ProgressDialog progressDialog;
    private boolean isAgree = false;
    private LoginViewModel loginViewModel;
    private MyApplication objMyApplication;
    private String mCompanyName = "", mBusinessEntity = "", mEIN = "", mEmail = "", mPhoneNumber = "", mAddress = "", mArticleDate = "", mEINDate = "", mW9Date = "";
    private String mDbName = "", mBusinessType = "", mTimeZone = "", mWebsite = "", mMonthlyProcVolume = "", mHighTicket = "", mAverageTicket = "", mCustomerServiceEmail = "", mCustomerServicePhone = "", mDbAddressLine = "", mDbFillingDate = "";
    private boolean addbusiness = false;
    private BankAccountsViewModel bankAccountsViewModel;
    private DashboardViewModel dashboardViewModel;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_application);

        objMyApplication = (MyApplication) getApplicationContext();

        initFields();
        initObservers();

    }

    private void saveApplicationData() {

        mCompanyName = mCompanyNameTx.getText().toString().trim();
        mBusinessEntity = mBusinessEntityTx.getText().toString().trim();
        mEIN = mEINTx.getText().toString().trim();
        mEmail = mEmailTx.getText().toString().trim();
        mPhoneNumber = mPhoneNumberTx.getText().toString().trim();

        mDbName = mDbNameTx.getText().toString().trim();
        mBusinessType = mBusinessTypeTx.getText().toString().trim();
        mWebsite = mWebsiteTx.getText().toString().trim();
        mTimeZone = mTimeZoneTx.getText().toString().trim();
        mMonthlyProcVolume = mMonthlyProcVolumeTx.getText().toString().trim();
        mHighTicket = mHighTicketTx.getText().toString().trim();
        mAverageTicket = mAverageTicketTx.getText().toString().trim();
        mCustomerServiceEmail = mCustomerServiceEmailTx.getText().toString().trim();
        mCustomerServicePhone = mCustomerServicePhoneTx.getText().toString().trim();
        mAddress = mAddressTx.getText().toString().trim();

        ApplicationSubmitRequest request = new ApplicationSubmitRequest();
        request.setCompanyName(mCompanyName);
        request.setCompanyBusinessEntity(mBusinessEntity);
        request.setCompanySsnOrEin(mEIN);
        request.setCompanyEmail(mEmail);
        PhNoWithCountryCode phone = new PhNoWithCountryCode();
        phone.setCountryCode(Utils.strCCode);
        phone.setPhoneNumber(mPhoneNumber);
        request.setCompanyPhoneNumberDto(phone);
        request.setRequiredDocuments(companyReqDocList);


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
        Bankaccount bankAccountsDataModel = new Bankaccount();
        bankAccountsDataModel.setItems(bankItems);
        request.setData(bankAccountsDataModel);
        //

        request.setBeneficialOwnerInfo(beneficialOwnerList);
        //
        Agreements agreementsData = new Agreements();
        agreementsData.setItems(agreements);
        request.setData(agreementsData);
        //
        request.setAgree(isAgree);
        applicationSubmissionViewModel.postApplicationData(request);
    }

    private void initFields() {
        objMyApplication = (MyApplication) getApplicationContext();
        applicationSubmissionViewModel = new ViewModelProvider(this).get(ApplicationSubmissionViewModel.class);

        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2TV);
        edit3 = findViewById(R.id.edit3TV);
        agreeCB = findViewById(R.id.agreeCB);
        submitCv = findViewById(R.id.submitCV);
        noBanksTv = findViewById(R.id.noBanksTV);
        noBoTV = findViewById(R.id.noBOTV);
        banksLL = findViewById(R.id.banksLL);
        boLL = findViewById(R.id.boLL);
        bankRecyclerView = findViewById(R.id.banksRecycler);
        boRecyclerView = findViewById(R.id.boRecycler);
        uploadArticlesLL = findViewById(R.id.ll_upload_articles);
        uploadEINLL = findViewById(R.id.ll_upload_ein);
        uploadW9LL = findViewById(R.id.ll_upload_w9);
        dbaFillingLL = findViewById(R.id.ll_dba_filling);
        llDBADocuments = findViewById(R.id.llDBADocuments);

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
                if (addbusiness) {
                    loginViewModel = new ViewModelProvider(ReviewApplicationActivity.this).get(LoginViewModel.class);
                    loginViewModel.postChangeAccount(objMyApplication.getLoginUserId());
                } else {

                    Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                    startActivity(intent);
                }
                submitResponse();
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
        llPrivacy = (LinearLayout) findViewById(R.id.ll_privacy);
        llTerms = (LinearLayout) findViewById(R.id.ll_terms);
        llMerchant = (LinearLayout) findViewById(R.id.ll_merchant);

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

        mAgreementsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAgree = true;
            }
        });

        summaryViewModel = new ViewModelProvider(this).get(BusinessApplicationSummaryViewModel.class);
        summaryViewModel.getApplicationSummaryData();


    }

    public void initObservers() {
        try {
            summaryViewModel.getSummaryMutableLiveData().observe(this, new Observer<ApplicationSummaryModelResponse>() {
                @Override
                public void onChanged(ApplicationSummaryModelResponse summaryModelResponse) {
                    if (summaryModelResponse != null) {
                        if (summaryModelResponse.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                CompanyInfo cir = summaryModelResponse.getData().getCompanyInfo();
                                companyReqDocList = cir.getRequiredDocuments();
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

                                if (cir.getRequiredDocuments().size() > 0) {
                                    for (int i = 0; i < cir.getRequiredDocuments().size(); i++) {
                                        if (cir.getRequiredDocuments().get(i).getIdentityId() == 5) {
                                            uploadArticlesLL.setVisibility(View.VISIBLE);
                                            uploadArticlesLL.setTag(cir.getRequiredDocuments().get(i).getImgLink());
                                            uploadArticlesLL.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showFile((String) v.getTag());
                                                }
                                            });
                                            mArticleDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        } else if (cir.getRequiredDocuments().get(i).getIdentityId() == 6) {
                                            uploadEINLL.setVisibility(View.VISIBLE);
                                            uploadEINLL.setTag(cir.getRequiredDocuments().get(i).getImgLink());
                                            uploadEINLL.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showFile((String) v.getTag());
                                                }
                                            });
                                            mEINDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        } else if (cir.getRequiredDocuments().get(i).getIdentityId() == 7 || cir.getRequiredDocuments().get(i).getIdentityId() == 11) {
                                            uploadW9LL.setVisibility(View.VISIBLE);
                                            uploadW9LL.setTag(cir.getRequiredDocuments().get(i).getImgLink());
                                            uploadW9LL.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showFile((String) v.getTag());
                                                }
                                            });
                                            mW9DateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        }
                                    }
                                }
                                DbaInfo dbaInfo = summaryModelResponse.getData().getDbaInfo();
                                dbReqDocList = dbaInfo.getRequiredDocuments();
                                if (dbaInfo.getName() != null && !dbaInfo.getName().equals("")) {
                                    mDbNameTx.setText(dbaInfo.getName());
                                }

                                if (dbaInfo.getBusinessType() != null && !dbaInfo.getBusinessType().equals("")) {
                                    mBusinessTypeTx.setText(dbaInfo.getBusinessType());
                                }
                                if (dbaInfo.getTimeZone() != 0) {
                                    mTimeZoneTx.setText(dbaInfo.getTimeZone().toString());
                                }
                                if (dbaInfo.getWebsite() != null && !dbaInfo.getWebsite().equals("")) {
                                    mWebsiteTx.setText(dbaInfo.getWebsite().toString());
                                }

                                if (dbaInfo.getMonthlyProcessingVolume() != null && !dbaInfo.getMonthlyProcessingVolume().equals("")) {
                                    // monthlyProcVolume=cir.getMonthlyProcessingVolume();
                                    mMonthlyProcVolumeTx.setText(dbaInfo.getMonthlyProcessingVolume().toString());
                                }

                                if (dbaInfo.getHighTicket() != null && !dbaInfo.getHighTicket().equals("")) {
                                    mHighTicketTx.setText(dbaInfo.getHighTicket().toString());
                                }

                                if (dbaInfo.getAverageTicket() != null && !dbaInfo.getAverageTicket().equals("")) {
                                    mAverageTicketTx.setText(dbaInfo.getAverageTicket().toString());
                                }
                                if (dbaInfo.getEmail() != null && !dbaInfo.getEmail().equals("")) {
                                    mCustomerServiceEmailTx.setText(dbaInfo.getEmail().toString());
                                }
                                if (dbaInfo.getPhoneNumberDto() != null && !dbaInfo.getPhoneNumberDto().equals("")) {
                                    mCustomerServicePhoneTx.setText(dbaInfo.getPhoneNumberDto().toString());
                                }
                                if (dbaInfo.getAddressLine1() != null && !dbaInfo.getAddressLine1().equals("") || dbaInfo.getAddressLine2() != null && !dbaInfo.getAddressLine2().equals("")) {
                                    mDbAddressLineTx.setText(dbaInfo.getAddressLine1().toString() + dbaInfo.getAddressLine2().toString());
                                }
                                if (dbaInfo.getRequiredDocuments().size() > 0) {
                                    for (int i = 0; i < dbaInfo.getRequiredDocuments().size(); i++) {
                                        llDBADocuments.setVisibility(View.VISIBLE);
                                        mDbFillingDateTx.setText(Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        dbaFillingLL.setTag(cir.getRequiredDocuments().get(i).getImgLink());
                                        dbaFillingLL.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showFile((String) v.getTag());
                                            }
                                        });
                                    }
                                }
                                List<BeneficialOwnerInfo> boList = summaryModelResponse.getData().getBeneficialOwnerInfo();
                                Log.d("BOWData", boList.toString());
                                if (boList.size() > 0) {
                                    boLL.setVisibility(View.VISIBLE);
                                    noBoTV.setVisibility(View.GONE);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewApplicationActivity.this);
                                    benificialOwnersRecyclerAdapter = new BenificialOwnersRecyclerAdapter(ReviewApplicationActivity.this, boList,ReviewApplicationActivity.this);
                                    beneficialOwnerList = boList;
                                    boRecyclerView.setLayoutManager(layoutManager);
                                    boRecyclerView.setAdapter(benificialOwnersRecyclerAdapter);
                                } else {
                                    noBoTV.setVisibility(View.VISIBLE);
                                    boLL.setVisibility(View.GONE);
                                }
                                if (summaryModelResponse.getData().getBankaccount().getItems().size() > 0) {
                                    banksLL.setVisibility(View.VISIBLE);
                                    noBanksTv.setVisibility(View.GONE);
                                    bankItems = summaryModelResponse.getData().getBankaccount().getItems();
                                    Log.d("BankItems", bankItems.toString());
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewApplicationActivity.this);
                                    accountsRecyclerAdapter = new BankAccountsRecyclerAdapter(ReviewApplicationActivity.this, bankItems);

                                    bankRecyclerView.setLayoutManager(layoutManager);
                                    bankRecyclerView.setAdapter(accountsRecyclerAdapter);

                                } else {
                                    banksLL.setVisibility(View.GONE);
                                    noBanksTv.setVisibility(View.VISIBLE);
                                }
                                agreements = summaryModelResponse.getData().getAgreements().getItems();
                                Agreements agreements1 = summaryModelResponse.getData().getAgreements();

                                if (agreements != null && agreements1.getItems().size() > 0) {
                                    for (int i = 0; i < agreements1.getItems().size(); i++) {
                                        if (agreements1.getItems().get(i).getSignatureType() == 0) {
                                            mTermsVno.setText(agreements1.getItems().get(i).getDocumentVersion());
                                            llPrivacy.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dashboardViewModel.agreementsByType("0");
                                                }
                                            });
                                        }
                                        if (agreements1.getItems().get(i).getSignatureType() == 1) {
                                            mPrivacyVno.setText(agreements1.getItems().get(i).getDocumentVersion());
                                            llTerms.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dashboardViewModel.agreementsByType("1");

                                                }
                                            });
                                        }
                                        if (agreements1.getItems().get(i).getSignatureType() == 5) {
                                            mMerchantsVno.setText(agreements1.getItems().get(i).getDocumentVersion());
                                            llMerchant.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dashboardViewModel.agreementsByType("5");

                                                }
                                            });

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse btResp) {
                    dialog.dismiss();
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d("btResp", "btResp" + btResp);
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


        dashboardViewModel.getAgreementsPdfMutableLiveData().observe(this, new Observer<AgreementsPdf>() {
            @Override
            public void onChanged(AgreementsPdf agreementsPdf) {
                LogUtils.d("pdfff","pdf"+agreementsPdf);
                if (agreementsPdf.getStatus().equalsIgnoreCase("SUCCESS")) {
                        if(agreementsPdf.getData().getAgreementFileRefPath()!=null) {
                            showFile(agreementsPdf.getData().getAgreementFileRefPath());
                        }
                }
            }
        });
    }

    private void showFile(String fileUrl) {
        if(fileUrl != null && !fileUrl.trim().equalsIgnoreCase("")) {
            //Call the activity here
            Intent intent = new Intent(ReviewApplicationActivity.this, WebViewActivity.class);
            intent.putExtra("FILEURL",fileUrl);
            startActivity(intent);
        } else {
            LogUtils.v(TAG, "fileUrl is null or empty");
        }
    }

    public void submitResponse() {
        try {
            applicationSubmissionViewModel.getPostCompanyInfoResponse().observe(this, new Observer<ApplicationSubmitResponseModel>() {
                @Override
                public void onChanged(ApplicationSubmitResponseModel submissionViewModel) {
                    if (submissionViewModel != null) {
                        dialog.dismiss();
                        if (submissionViewModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                            Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                            startActivity(intent);
                        }
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void selectedItem(String file) {

        LogUtils.d("file","file"+file);

        showFile(file);

    }
}

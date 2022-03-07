package com.greenbox.coyni.view.business;


import static com.greenbox.coyni.utils.Utils.convertTwoDecimal;
import static com.greenbox.coyni.view.PreferencesActivity.customerProfileViewModel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BankAccountsRecyclerAdapter;
import com.greenbox.coyni.adapters.BenificialOwnersRecyclerAdapter;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
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
import com.greenbox.coyni.model.users.TimeZoneModel;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.WebViewActivity;
import com.greenbox.coyni.viewmodel.ApplicationSubmissionViewModel;
import com.greenbox.coyni.viewmodel.BankAccountsViewModel;
import com.greenbox.coyni.viewmodel.BusinessApplicationSummaryViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewApplicationActivity extends BaseActivity implements BenificialOwnersRecyclerAdapter.OnSelectListner , BankAccountsRecyclerAdapter.OnSelectListner {
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
    private LinearLayout banksLL, boLL, CloseLL;
    private LinearLayout uploadArticlesLL, uploadEINLL, uploadW9LL, dbaFillingLL, llDBADocuments;
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
    private String strScreen = "", strSignOn = "";
    private Boolean isBank = false;
    private Boolean isCPwdEye = false;
    SignOnData signOnData;
    private ImageView llEin;
    private PaymentMethodsViewModel paymentMethodsViewModel;
    private TextView tosTV, prTv;
    private CompanyInfo cir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_application);

        objMyApplication = (MyApplication) getApplicationContext();

        initFields();
        initObservers();

    }

    private void initFields() {
        objMyApplication = (MyApplication) getApplicationContext();


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
        llEin = findViewById(R.id.llEIN);
        CloseLL = findViewById(R.id.CloseLL);

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, CompanyInformationActivity.class);
                intent.putExtra("FROM", "EDIT");
                startActivity(intent);
            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, DBAInfoAcivity.class);
                intent.putExtra("FROM", "EDIT");
                intent.putExtra("TYPE", "EXIST");
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

        llEin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isCPwdEye) {
                        isCPwdEye = true;
                        llEin.setBackgroundResource(R.drawable.ic_eyeclose);
                        String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", ".");
                        String hifened = converted.substring(0, 2) + "-" + converted.substring(2);
                        mEINTx.setText(hifened);
                    } else {
                        isCPwdEye = false;
                        llEin.setBackgroundResource(R.drawable.ic_eyeopen);
                        mEINTx.setText(cir.getSsnOrEin().substring(0, 2) + "-" + cir.getSsnOrEin().substring(2));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        agreeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAgree = true;
                    submitCv.setEnabled(true);
                    submitCv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    isAgree = false;
                    submitCv.setEnabled(false);
                    submitCv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        });

        submitCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                if (addbusiness) {
                    loginViewModel = new ViewModelProvider(ReviewApplicationActivity.this).get(LoginViewModel.class);
                    loginViewModel.postChangeAccount(objMyApplication.getLoginUserId());
                } else {
                    applicationSubmissionViewModel.postApplicationData();
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
        tosTV = (TextView) findViewById(R.id.tosTV);
        prTv = (TextView) findViewById(R.id.privacyTV);
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
        tosTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboardViewModel.agreementsByType("0");

            }
        });
        prTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboardViewModel.agreementsByType("1");


            }
        });

        mTermsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tosURL));
                startActivity(browserIntent);
            }
        });

        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getBusinessType();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.meAgreementsById();

        applicationSubmissionViewModel = new ViewModelProvider(this).get(ApplicationSubmissionViewModel.class);
        paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);


        mAgreementsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAgree = true;
            }
        });

        summaryViewModel = new ViewModelProvider(this).get(BusinessApplicationSummaryViewModel.class);

        customerProfileViewModel = new ViewModelProvider(ReviewApplicationActivity.this).get(CustomerProfileViewModel.class);
        customerProfileViewModel.meSignOn();

        CloseLL.setOnClickListener(view -> finish());

    }

    public void deleteBankAPICall(int id) {
        paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
        paymentMethodsViewModel.deleteBanks(id);
    }

    public void showBankDeleteCOnfirmationDialog() {
        DialogAttributes dialogAttributes = new DialogAttributes(getString(R.string.bank_delete_title),
                getString(R.string.bankdeletemsg),
                getString(R.string.bank_delete_keep),getString(R.string.bank_delete_relink));
        CustomConfirmationDialog customConfirmationDialog = new CustomConfirmationDialog
                (ReviewApplicationActivity.this, dialogAttributes);

        customConfirmationDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                LogUtils.d("onclickkk","onclickkk"+action+value);
                if(action.equalsIgnoreCase(getString(R.string.bank_delete_relink))){
                    try {
                        dialog.dismiss();
                        if (objMyApplication.getStrSignOnError().equals("") && objMyApplication.getSignOnData() != null && objMyApplication.getSignOnData().getUrl() != null) {
                            isBank = true;
                            Intent i = new Intent(ReviewApplicationActivity.this, WebViewActivity.class);
                            i.putExtra("signon", objMyApplication.getSignOnData());
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(objMyApplication.getStrSignOnError(), ReviewApplicationActivity.this, "", "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });

        customConfirmationDialog.show();
    }

    public void initObservers() {

        paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {

                    Utils.showCustomToast(ReviewApplicationActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");

                }
            }
        });

        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (signOn != null) {
                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                            objMyApplication.setSignOnData(signOn.getData());
                            signOnData = signOn.getData();
                            objMyApplication.setStrSignOnError("");
                            strSignOn = "";
                            if (objMyApplication.getResolveUrl()) {
                                objMyApplication.callResolveFlow(ReviewApplicationActivity.this, strSignOn, signOnData);
                            }
                        } else {
                            if (signOn.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                                objMyApplication.setResolveUrl(true);
                                customerProfileViewModel.meSignOn();
                            } else {
                                objMyApplication.setSignOnData(null);
                                signOnData = null;
                                objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
                                strSignOn = signOn.getError().getErrorDescription();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            summaryViewModel.getSummaryMutableLiveData().observe(this, new Observer<ApplicationSummaryModelResponse>() {
                @Override
                public void onChanged(ApplicationSummaryModelResponse summaryModelResponse) {
                    dismissDialog();
                    if (summaryModelResponse != null) {
                        if (summaryModelResponse.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                cir = summaryModelResponse.getData().getCompanyInfo();
                                companyReqDocList = cir.getRequiredDocuments();
                                if (cir.getName() != null && !cir.getName().equals("")) {
                                    mCompanyNameTx.setText(cir.getName());
                                }

                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    mEmailTx.setText(cir.getEmail());
                                }

                                if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
                                    mPhoneNumberTx.setText(Utils.convertToUSFormatNew(cir.getPhoneNumberDto().getPhoneNumber()));
                                }

                                if (cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")) {
                                    mBusinessEntityTx.setText(cir.getBusinessEntity());
                                }

                                if (cir.getSsnOrEin() != null && !cir.getSsnOrEin().equals("")) {
                                    isCPwdEye = true;
                                    String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", ".");
                                    String hifened = converted.substring(0, 2) + "-" + converted.substring(2);
                                    //String mEintext = cir.getSsnOrEin().substring(0,2).replaceAll("\\w(?=\\w{2})", ".")+ "-"+ cir.getSsnOrEin().substring(2).replaceAll("\\w(?=\\w{2})", ".");
                                    mEINTx.setText(hifened);

                                }

                                StringBuilder sbCompany = new StringBuilder();
                                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                                    sbCompany.append(cir.getAddressLine1());
                                }
                                if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                                    sbCompany.append(",").append(cir.getAddressLine1());
                                }
                                if (cir.getCity() != null && !cir.getCity().equals("")) {
                                    sbCompany.append(",").append(cir.getCity());
                                }
                                if (cir.getState() != null && !cir.getState().equals("")) {
                                    sbCompany.append(",").append(cir.getState());
                                }
                                if (cir.getZipCode() != null && !cir.getZipCode().equals("")) {
                                    sbCompany.append(",").append(cir.getZipCode());
                                }
                                mAddressTx.setText(sbCompany.toString());


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
                                            mArticleDateTx.setText(getResources().getString(R.string.uploaded_on) + " " + Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        } else if (cir.getRequiredDocuments().get(i).getIdentityId() == 6) {
                                            uploadEINLL.setVisibility(View.VISIBLE);
                                            uploadEINLL.setTag(cir.getRequiredDocuments().get(i).getImgLink());
                                            uploadEINLL.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showFile((String) v.getTag());
                                                }
                                            });
                                            mEINDateTx.setText(getResources().getString(R.string.uploaded_on) + " " + Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        } else if (cir.getRequiredDocuments().get(i).getIdentityId() == 7 || cir.getRequiredDocuments().get(i).getIdentityId() == 11) {
                                            uploadW9LL.setVisibility(View.VISIBLE);
                                            uploadW9LL.setTag(cir.getRequiredDocuments().get(i).getImgLink());
                                            uploadW9LL.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showFile((String) v.getTag());
                                                }
                                            });
                                            mW9DateTx.setText(getResources().getString(R.string.uploaded_on) + " " + Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
                                        }
                                    }
                                }
                                DbaInfo dbaInfo = summaryModelResponse.getData().getDbaInfo();
                                dbReqDocList = dbaInfo.getRequiredDocuments();
                                if (dbaInfo.getName() != null && !dbaInfo.getName().equals("")) {
                                    mDbNameTx.setText(dbaInfo.getName());
                                }

                                if (dbaInfo.getBusinessType() != null && !dbaInfo.getBusinessType().equals("")) {
                                    mBusinessTypeTx.setText(Utils.getBusinessName(objMyApplication, dbaInfo.getBusinessType()));
                                    // Utils.getBusinessName(objMyApplication,dbaInfo.getBusinessType());
                                }
                                if (dbaInfo.getTimeZone() != null) {
                                    ArrayList<TimeZoneModel> arrZonesList = new ArrayList<>();
                                    LogUtils.d("TimeZoneModel", "TimeZoneModel" + arrZonesList);
                                    if (dbaInfo.getTimeZone().toString().equalsIgnoreCase("3")) {
                                        mTimeZoneTx.setText(R.string.EST);
                                    } else if (dbaInfo.getTimeZone().toString().equalsIgnoreCase("2")) {
                                        mTimeZoneTx.setText(R.string.CST);
                                    } else if (dbaInfo.getTimeZone().toString().equalsIgnoreCase("1")) {
                                        mTimeZoneTx.setText(R.string.MST);
                                    } else if (dbaInfo.getTimeZone().toString().equalsIgnoreCase("0")) {
                                        mTimeZoneTx.setText(R.string.PST);
                                    } else if (dbaInfo.getTimeZone().toString().equalsIgnoreCase("5")) {
                                        mTimeZoneTx.setText(R.string.AST);
                                    } else if (dbaInfo.getTimeZone().toString().equalsIgnoreCase("4")) {
                                        mTimeZoneTx.setText(R.string.HST);
                                    }
                                }
                                if (dbaInfo.getWebsite() != null && !dbaInfo.getWebsite().equals("")) {
                                    mWebsiteTx.setText(dbaInfo.getWebsite().toString());
                                } else {
                                    mWebsiteTx.setText("");

                                }

                                if (dbaInfo.getMonthlyProcessingVolume() != null && !dbaInfo.getMonthlyProcessingVolume().equals("")) {
                                    // monthlyProcVolume=cir.getMonthlyProcessingVolume();
                                    mMonthlyProcVolumeTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getMonthlyProcessingVolume().toString()));
                                }

                                if (dbaInfo.getHighTicket() != null && !dbaInfo.getHighTicket().equals("")) {
                                    mHighTicketTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getHighTicket().toString()));
                                }

                                if (dbaInfo.getAverageTicket() != null && !dbaInfo.getAverageTicket().equals("")) {
                                    mAverageTicketTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getAverageTicket().toString()));
                                }
                                if (dbaInfo.getEmail() != null && !dbaInfo.getEmail().equals("")) {
                                    mCustomerServiceEmailTx.setText(dbaInfo.getEmail().toString());
                                }
                                if (dbaInfo.getPhoneNumberDto() != null && !dbaInfo.getPhoneNumberDto().equals("")) {
                                    mCustomerServicePhoneTx.setText(Utils.convertToUSFormatNew(dbaInfo.getPhoneNumberDto().getPhoneNumber()));
                                }

                                StringBuilder sb = new StringBuilder();
                                if (dbaInfo.getAddressLine1() != null && !dbaInfo.getAddressLine1().equals("")) {
                                    sb.append(dbaInfo.getAddressLine1());
                                }
                                if (dbaInfo.getAddressLine2() != null && !dbaInfo.getAddressLine2().equals("")) {
                                    sb.append(",").append(dbaInfo.getAddressLine1());
                                }
                                if (dbaInfo.getCity() != null && !dbaInfo.getCity().equals("")) {
                                    sb.append(",").append(dbaInfo.getCity());
                                }
                                if (dbaInfo.getState() != null && !dbaInfo.getState().equals("")) {
                                    sb.append(",").append(dbaInfo.getState());
                                }
                                if (dbaInfo.getZipCode() != null && !dbaInfo.getZipCode().equals("")) {
                                    sb.append(",").append(dbaInfo.getZipCode());
                                }
                                mDbAddressLineTx.setText(sb.toString());

                                if (dbaInfo.getRequiredDocuments().size() > 0) {
                                    for (int i = 0; i < dbaInfo.getRequiredDocuments().size(); i++) {
                                        llDBADocuments.setVisibility(View.VISIBLE);
                                        mDbFillingDateTx.setText(getResources().getString(R.string.uploaded_on) + " " + Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(i).getUpdatedAt()));
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
                                    benificialOwnersRecyclerAdapter = new BenificialOwnersRecyclerAdapter(ReviewApplicationActivity.this, boList, ReviewApplicationActivity.this);
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

                                    accountsRecyclerAdapter = new BankAccountsRecyclerAdapter(ReviewApplicationActivity.this, bankItems,ReviewApplicationActivity.this);

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

        } catch (Exception ex) {
            ex.printStackTrace();
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
        try {
            applicationSubmissionViewModel.getPostCompanyInfoResponse().observe(this, new Observer<ApplicationSubmitResponseModel>() {
                @Override
                public void onChanged(ApplicationSubmitResponseModel submissionViewModel) {
                    if (submissionViewModel != null) {
                        dismissDialog();
                        if (submissionViewModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                            Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Utils.displayAlert(submissionViewModel.getError().getErrorDescription(), ReviewApplicationActivity.this, "", submissionViewModel.getError().getFieldErrors().get(0));
                        }
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardViewModel.getAgreementsPdfMutableLiveData().observe(this, new Observer<AgreementsPdf>() {
            @Override
            public void onChanged(AgreementsPdf agreementsPdf) {
                LogUtils.d("pdfff", "pdf" + agreementsPdf);
                if (agreementsPdf.getStatus().equalsIgnoreCase("SUCCESS")) {
                    if (agreementsPdf.getData().getAgreementFileRefPath() != null) {
                        showFile(agreementsPdf.getData().getAgreementFileRefPath());
                    }
                }
            }
        });

        try {
            businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
                @Override
                public void onChanged(BusinessTypeResp businessTypeResp) {

                    if (businessTypeResp != null) {
                        if (businessTypeResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTypeResp(businessTypeResp);
                            businessIdentityVerificationViewModel.getDBAInfo();
//                           loadCompanyInfo();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.trim().equalsIgnoreCase("")) {
            //Call the activity here
            Intent intent = new Intent(ReviewApplicationActivity.this, WebViewShowFileActivity.class);
            intent.putExtra("FILEURL", fileUrl);
            startActivity(intent);
        } else {
            LogUtils.v(TAG, "fileUrl is null or empty");
        }
    }

    @Override
    public void selectedItem(String file) {
        LogUtils.d("file", "file" + file);
        showFile(file);
    }

    @Override
    public void selectedBankItem(int id) {
        LogUtils.d("selectedBankItem", "selectedBankItem" + id);
         if(id==0){
             showBankDeleteCOnfirmationDialog();
         } else {
             deleteBankAPICall(id);
         }

    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            showProgressDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    summaryViewModel.getApplicationSummaryData();
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

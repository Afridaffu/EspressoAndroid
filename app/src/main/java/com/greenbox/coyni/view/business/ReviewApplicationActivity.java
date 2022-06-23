package com.greenbox.coyni.view.business;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.greenbox.coyni.utils.Utils.convertTwoDecimal;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.DownloadDocumentData;
import com.greenbox.coyni.model.profile.DownloadDocumentResponse;
import com.greenbox.coyni.model.profile.DownloadImageData;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.model.profile.DownloadUrlRequest;
import com.greenbox.coyni.model.submit.ApplicationSubmitResponseModel;
import com.greenbox.coyni.model.summary.Agreements;
import com.greenbox.coyni.model.summary.ApplicationSummaryModelResponse;
import com.greenbox.coyni.model.summary.BankAccount;
import com.greenbox.coyni.model.summary.BeneficialOwnerInfo;
import com.greenbox.coyni.model.summary.CompanyInfo;
import com.greenbox.coyni.model.summary.DbaInfo;
import com.greenbox.coyni.model.summary.Item1;
import com.greenbox.coyni.model.summary.RequiredDocument;
import com.greenbox.coyni.model.users.TimeZoneModel;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AgreementsActivity;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.DashboardActivity;
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

public class ReviewApplicationActivity extends BaseActivity implements BenificialOwnersRecyclerAdapter.OnSelectListner, BankAccountsRecyclerAdapter.OnSelectListener, OnKeyboardVisibilityListener {
    private TextView edit1, edit2, edit3;
    private CheckBox agreeCB;
    private boolean isNextEnabled = false, isagreed = false;
    private CardView submitCv;
    private TextView mCompanyNameTx, mBusinessEntityTx, mEINTx, mEmailTx, mPhoneNumberTx, mAddressTx, mArticleDateTx, mEINDateTx, mW9DateTx;
    private TextView mDbNameTx, mBusinessTypeTx, mTimeZoneTx, mWebsiteTx, mMonthlyProcVolumeTx, mHighTicketTx, mAverageTicketTx, mCustomerServiceEmailTx, mCustomerServicePhoneTx, mDbAddressLineTx, mDbFillingDateTx, mWebsiteHeadTX;
    private TextView mPrivacyVno, mTermsVno, mMerchantsVno;
    private BankAccountsRecyclerAdapter accountsRecyclerAdapter;
    private List<BankAccount> bankBankAccounts = new ArrayList<>();
    private BenificialOwnersRecyclerAdapter benificialOwnersRecyclerAdapter;
    private List<BeneficialOwnerInfo> beneficialOwnerList = new ArrayList<>();
    private RecyclerView bankRecyclerView, boRecyclerView;
    private TextView noBanksTv, noBoTV, ssnEinTV;
    private LinearLayout banksLL, boLL, CloseLL,companyEditLL,websiteLL;
    private LinearLayout uploadArticlesLL, uploadEINLL, uploadW9LL, dbaFillingLL, llDBADocuments;
    private ApplicationSubmissionViewModel applicationSubmissionViewModel;
    private BusinessApplicationSummaryViewModel summaryViewModel;
    private int monthlyProcVolume = 0;
    private List<Item1> agreements = new ArrayList<>();
    private List<RequiredDocument> companyReqDocList = new ArrayList<>();
    private List<RequiredDocument> dbReqDocList = new ArrayList<>();
    //    private String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
//    private String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    private ImageView mPrivacyImg, mTermsImg, mAgreementsImg;
    private LinearLayout llPrivacy, llTerms, llMerchant;
    private Dialog progressDialog;
    private boolean isAgree = false;
    private LoginViewModel loginViewModel;
    private MyApplication objMyApplication;
    private String mCompanyName = "", mBusinessEntity = "", mEIN = "", mEmail = "", mPhoneNumber = "", mAddress = "", mArticleDate = "", mEINDate = "", mW9Date = "";
    private String mDbName = "", mBusinessType = "", mTimeZone = "", mWebsite = "", mMonthlyProcVolume = "", mHighTicket = "", mAverageTicket = "", mCustomerServiceEmail = "", mCustomerServicePhone = "", mDbAddressLine = "", mDbFillingDate = "";
    private Boolean addBusiness = false;
    private Boolean addDBA = false;
    private BankAccountsViewModel bankAccountsViewModel;
    private DashboardViewModel dashboardViewModel;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private CustomerProfileViewModel customerProfileViewModel;
    private String strScreen = "", strSignOn = "";
    private Boolean isBank = false;
    private Boolean isCPwdEye = false;
    SignOnData signOnData;
    private ImageView llEin;
    private PaymentMethodsViewModel paymentMethodsViewModel;
    private TextView tosTV, prTv, spannableTV, httpHeader;
    private CompanyInfo cir;
    Long mLastClickTimeQA = 0L;
    Long mLastClickTime = 0L;
    private String selectedAgreement = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_review_application);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setKeyboardVisibilityListener(ReviewApplicationActivity.this);

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        objMyApplication = (MyApplication) getApplicationContext();

        if (getIntent().getBooleanExtra(Utils.ADD_BUSINESS, false)) {
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            addBusiness = getIntent().getBooleanExtra(Utils.ADD_BUSINESS, false);
        }
        if (getIntent().getBooleanExtra(Utils.ADD_DBA, false)) {
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            addDBA = getIntent().getBooleanExtra(Utils.ADD_DBA, false);
        }

        initFields();
        initObservers();
        companyEditLL.setVisibility(addDBA ? companyEditLL.GONE : companyEditLL.VISIBLE);
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
        ssnEinTV = findViewById(R.id.ssnEinTV);
        spannableTV = findViewById(R.id.spannableTV);
        httpHeader = findViewById(R.id.httpHeader);
        companyEditLL = findViewById(R.id.companyEditLL);
        websiteLL = findViewById(R.id.websiteLL);

        setSpannableText();

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
                        if (cir.getIdentificationType() == 11) {
                            String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", "•");
                            String hifened = converted.substring(0, 3) + " - " + converted.substring(3, 5) + " - " + converted.substring(5, converted.length());
                            mEINTx.setText(hifened);
                        } else {
                            String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", "•");
                            String hifened = converted.substring(0, 2) + " - " + converted.substring(2);
                            mEINTx.setText(hifened);
                        }
//                        String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", ".");
//                        String hifened = converted.substring(0, 2) + "-" + converted.substring(2);
//                        mEINTx.setText(hifened);
                    } else {
                        isCPwdEye = false;
                        if (cir.getIdentificationType() == 11) {
                            mEINTx.setText(cir.getSsnOrEin().substring(0, 3) + " - " + cir.getSsnOrEin().substring(3, 5) + " - " + cir.getSsnOrEin().substring(5, cir.getSsnOrEin().length()));

                        } else {
                            mEINTx.setText(cir.getSsnOrEin().substring(0, 2) + " - " + cir.getSsnOrEin().substring(2));

                        }
                        llEin.setBackgroundResource(R.drawable.ic_eyeopen);
                        // mEINTx.setText(cir.getSsnOrEin().substring(0, 2) + "-" + cir.getSsnOrEin().substring(2));
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
                if (isAgree) {
                    showProgressDialog();

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
        mWebsiteHeadTX = (TextView) findViewById(R.id.tvWebHead);
        mMonthlyProcVolumeTx = (TextView) findViewById(R.id.monthly_process_volume);
        mHighTicketTx = (TextView) findViewById(R.id.high_ticket);
        mAverageTicketTx = (TextView) findViewById(R.id.average_ticket);
        mCustomerServiceEmailTx = (TextView) findViewById(R.id.customer_service_email);
        mCustomerServicePhoneTx = (TextView) findViewById(R.id.customer_service_phone);
        mDbAddressLineTx = (TextView) findViewById(R.id.db_address_line);
        mDbFillingDateTx = (TextView) findViewById(R.id.db_filling_date);
        mPrivacyVno = (TextView) findViewById(R.id.privacy_policy);
        mTermsVno = (TextView) findViewById(R.id.terms_of_service);
//        tosTV = (TextView) findViewById(R.id.tosTV);
//        prTv = (TextView) findViewById(R.id.privacyTV);
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
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyURL));
//                startActivity(browserIntent);
            }
        });
//        tosTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                dashboardViewModel.agreementsByType("1");
//                showProgressDialog();
//                dashboardViewModel.agreementsByType(String.valueOf(Utils.mTOS));
//
//            }
//        });
//        prTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                dashboardViewModel.agreementsByType("0");
//                showProgressDialog();
//                dashboardViewModel.agreementsByType(String.valueOf(Utils.mPP));
//            }
//        });

        mTermsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tosURL));
//                startActivity(browserIntent);
            }
        });

        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getBusinessType();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.meAgreementsById();

        applicationSubmissionViewModel = new ViewModelProvider(this).get(ApplicationSubmissionViewModel.class);
        paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
        loginViewModel = new ViewModelProvider(ReviewApplicationActivity.this).get(LoginViewModel.class);

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
        showProgressDialog();
        paymentMethodsViewModel.deleteBanks(id);
    }

    public void showBankDeleteCOnfirmationDialog() {
        DialogAttributes dialogAttributes = new DialogAttributes(getString(R.string.bank_delete_title),
                getString(R.string.bankdeletemsg),
                getString(R.string.bank_delete_keep), getString(R.string.bank_delete_relink));
        CustomConfirmationDialog customConfirmationDialog = new CustomConfirmationDialog
                (ReviewApplicationActivity.this, dialogAttributes);

        customConfirmationDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                LogUtils.d(TAG, "onclickkk" + action + value);
                if (action.equalsIgnoreCase(getString(R.string.bank_delete_relink))) {
                    try {
                        dismissDialog();
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
                    dismissDialog();
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
                    //showProgressDialog();
                    summaryViewModel.getApplicationSummaryData();
                    Utils.showCustomToast(ReviewApplicationActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                }
            }
        });

        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    dismissDialog();
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

        summaryViewModel.getSummaryMutableLiveData().observe(this, new Observer<ApplicationSummaryModelResponse>() {
            @Override
            public void onChanged(ApplicationSummaryModelResponse summaryModelResponse) {
                dismissDialog();
                if (summaryModelResponse != null) {
                    if (summaryModelResponse.getStatus().toLowerCase().toString().equals("success")) {
                        try {
                            cir = summaryModelResponse.getData().getCompanyInfo();
                            companyReqDocList = cir.getRequiredDocuments();
                            if (cir.getName() != null) {
                                mCompanyNameTx.setText(cir.getName());
                            }

                            if (cir.getEmail() != null) {
                                mEmailTx.setText(cir.getEmail());
                            }

                            if (cir.getPhoneNumberDto().getPhoneNumber() != null) {
                                mPhoneNumberTx.setText(Utils.convertToUSFormatNew(cir.getPhoneNumberDto().getPhoneNumber()));
                            }

                            if (cir.getBusinessEntity() != null) {
                                mBusinessEntityTx.setText(cir.getBusinessEntity());
                            }

                            if (cir.getSsnOrEin() != null) {
                                if (!cir.getSsnOrEin().equals("")) {
                                    if (cir.getIdentificationType() == 11) {
                                        ssnEinTV.setText("SSN");
                                        isCPwdEye = true;
                                        String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", "•");
                                        String hifened = converted.substring(0, 3) + " - " + converted.substring(3, 5) + " - " + converted.substring(5, converted.length());
                                        mEINTx.setText(hifened);
                                    } else {
                                        ssnEinTV.setText("EIN/TIN");
                                        isCPwdEye = true;
                                        String converted = cir.getSsnOrEin().replaceAll("\\w(?=\\w{2})", "•");
                                        String hifened = converted.substring(0, 2) + " - " + converted.substring(2);
                                        mEINTx.setText(hifened);
                                    }
                                }
                            }

                            StringBuilder sbCompany = new StringBuilder();
                            if (cir.getAddressLine1() != null) {
                                sbCompany.append(cir.getAddressLine1());
                            }
                            if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                                sbCompany.append(", ").append(cir.getAddressLine2());
                            }
                            if (cir.getCity() != null) {
                                sbCompany.append(", ").append(cir.getCity());
                            }
                            if (cir.getState() != null) {
                                sbCompany.append(", ").append(cir.getState());
                            }
                            if (cir.getZipCode() != null) {
                                sbCompany.append(", ").append(cir.getZipCode());
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

                            if (cir.getIdentificationType() == 10) {
                                uploadArticlesLL.setVisibility(View.VISIBLE);
                                uploadEINLL.setVisibility(View.VISIBLE);
                                uploadW9LL.setVisibility(View.VISIBLE);
                            } else if (cir.getIdentificationType() == 11) {
                                uploadArticlesLL.setVisibility(GONE);
                                uploadEINLL.setVisibility(GONE);
                                uploadW9LL.setVisibility(View.VISIBLE);
                            }

                            DbaInfo dbaInfo = summaryModelResponse.getData().getDbaInfo();
                            dbReqDocList = dbaInfo.getRequiredDocuments();
                            if (dbaInfo.getName() != null) {
                                mDbNameTx.setText(dbaInfo.getName());
                            }

                            if (dbaInfo.getBusinessType() != null) {
                                mBusinessTypeTx.setText(Utils.getBusinessName(objMyApplication, dbaInfo.getBusinessType()));
                                // Utils.getBusinessName(objMyApplication,dbaInfo.getBusinessType());
                            }
                            if (dbaInfo.getTimeZone() != null) {
                                ArrayList<TimeZoneModel> arrZonesList = new ArrayList<>();
                                LogUtils.d(TAG, "TimeZoneModel" + arrZonesList);
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
                            if (dbaInfo.getIdentificationType() == 8) {
                                mWebsiteHeadTX.setText("Website (Optional)");
                                if(dbaInfo.getWebsite() == null || dbaInfo.getWebsite().equals("")){
                                    websiteLL.setVisibility(GONE);
                                }else {
                                    websiteLL.setVisibility(VISIBLE);
                                }
                            } else if (dbaInfo.getIdentificationType() == 9) {
                                mWebsiteHeadTX.setText("Website");
                                httpHeader.setVisibility(View.VISIBLE);
                                websiteLL.setVisibility(VISIBLE);
                            }
                            if (dbaInfo.getWebsite() != null ) {
                                mWebsiteTx.setText(dbaInfo.getWebsite());
                            }else{
                                websiteLL.setVisibility(GONE);
                            }

                            if (dbaInfo.getMonthlyProcessingVolume() != null) {
                                // monthlyProcVolume=cir.getMonthlyProcessingVolume();
                                if (Integer.parseInt(dbaInfo.getMonthlyProcessingVolume().toString()) != 0) {
                                    mMonthlyProcVolumeTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getMonthlyProcessingVolume().toString()));
                                }else{
                                    mMonthlyProcVolumeTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getMonthlyProcessingVolume().toString()));

                                }
                            }

                            if (dbaInfo.getHighTicket() != null) {
                                if (Integer.parseInt(dbaInfo.getHighTicket().toString()) != 0) {
                                    mHighTicketTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getHighTicket().toString()));
                                }else{
                                    mHighTicketTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getHighTicket().toString()));
                                }
                            }

                            if (dbaInfo.getAverageTicket() != null) {
                                if (Integer.parseInt(dbaInfo.getHighTicket().toString()) != 0){
                                    mAverageTicketTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getAverageTicket().toString()));
                            }else{
                                    mAverageTicketTx.setText(getResources().getString(R.string.dollor) + " " + convertTwoDecimal(dbaInfo.getAverageTicket().toString()));
                                }
                            }

                            if (dbaInfo.getEmail() != null) {
                                mCustomerServiceEmailTx.setText(dbaInfo.getEmail());
                            }

                            if (dbaInfo.getPhoneNumberDto() != null) {
                                if (!dbaInfo.getPhoneNumberDto().equals(""))
                                    mCustomerServicePhoneTx.setText(Utils.convertToUSFormatNew(dbaInfo.getPhoneNumberDto().getPhoneNumber()));
                            }

                            StringBuilder sb = new StringBuilder();
                            if (dbaInfo.getAddressLine1() != null && !dbaInfo.getAddressLine1().equals("")) {
                                sb.append(dbaInfo.getAddressLine1());
                            }
                            if (dbaInfo.getAddressLine2() != null && !dbaInfo.getAddressLine2().equals("")) {
                                sb.append(", ").append(dbaInfo.getAddressLine2());
                            }
                            if (dbaInfo.getCity() != null && !dbaInfo.getCity().equals("")) {
                                sb.append(", ").append(dbaInfo.getCity());
                            }
                            if (dbaInfo.getState() != null && !dbaInfo.getState().equals("")) {
                                sb.append(", ").append(dbaInfo.getState());
                            }
                            if (dbaInfo.getZipCode() != null && !dbaInfo.getZipCode().equals("")) {
                                sb.append(", ").append(dbaInfo.getZipCode());
                            }
                            mDbAddressLineTx.setText(sb.toString());

                            if (dbaInfo.getRequiredDocuments().size() > 0) {
                                for (int i = 0; i < dbaInfo.getRequiredDocuments().size(); i++) {
                                    llDBADocuments.setVisibility(View.VISIBLE);
                                    mDbFillingDateTx.setText(getResources().getString(R.string.uploaded_on) + " " + Utils.convertDocUploadedDate(dbaInfo.getRequiredDocuments().get(i).getUpdatedAt()));
                                    llDBADocuments.setTag(dbaInfo.getRequiredDocuments().get(i).getImgLink());
                                    llDBADocuments.setOnClickListener(new View.OnClickListener() {
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
                                boRecyclerView.setNestedScrollingEnabled(false);
                                boRecyclerView.setLayoutManager(layoutManager);
                                boRecyclerView.setAdapter(benificialOwnersRecyclerAdapter);
                            } else {
                                noBoTV.setVisibility(View.VISIBLE);
                                boLL.setVisibility(View.GONE);
                            }
                            if (summaryModelResponse.getData().getBankaccount().size() > 0) {
                                banksLL.setVisibility(View.VISIBLE);
                                noBanksTv.setVisibility(View.GONE);
                                bankBankAccounts = summaryModelResponse.getData().getBankaccount();
                                Log.d("BankItems", bankBankAccounts.toString());
                                LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewApplicationActivity.this);

                                accountsRecyclerAdapter = new BankAccountsRecyclerAdapter(ReviewApplicationActivity.this, bankBankAccounts, ReviewApplicationActivity.this);

                                bankRecyclerView.setNestedScrollingEnabled(false);
                                bankRecyclerView.setLayoutManager(layoutManager);
                                bankRecyclerView.setAdapter(accountsRecyclerAdapter);

                            } else {
                                banksLL.setVisibility(View.GONE);
                                noBanksTv.setVisibility(View.VISIBLE);
                            }
                            agreements = summaryModelResponse.getData().getAgreements().getItems();
                            Agreements agreements1 = summaryModelResponse.getData().getAgreements();
                            llPrivacy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                                        return;
                                    }
                                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                                    showProgressDialog();
                                    selectedAgreement = getString(R.string.gbx_pp);
                                    dashboardViewModel.getDocumentUrl(Utils.mPP);
                                }
                            });
                            llTerms.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                                        return;
                                    }
                                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                                    showProgressDialog();
                                    selectedAgreement = getString(R.string.gbx_tos);
                                    dashboardViewModel.getDocumentUrl(Utils.mTOS);
                                }
                            });
                            llMerchant.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                                        return;
                                    }
                                    mLastClickTimeQA = SystemClock.elapsedRealtime();
                                    showProgressDialog();
                                    selectedAgreement = getString(R.string.gbx_merchant);
                                    dashboardViewModel.getDocumentUrl(Utils.mAgmt);

                                }
                            });
                            if (agreements != null && agreements1.getItems().size() > 0) {
                                for (int i = 0; i < agreements1.getItems().size(); i++) {
                                    String doc = agreements1.getItems().get(i).getDocumentVersion();
                                    switch (agreements1.getItems().get(i).getSignatureType()) {
                                        case Utils.mPP:
                                            mPrivacyVno.setText(doc.substring(0, 1).toLowerCase() + doc.substring(1).trim());
                                            break;
                                        case Utils.mTOS:
                                            mTermsVno.setText(doc.substring(0, 1).toLowerCase() + doc.substring(1).trim());
                                            break;
                                        case Utils.mAgmt:
                                            mMerchantsVno.setText(doc.substring(0, 1).toLowerCase() + doc.substring(1).trim());
                                            break;
                                    }
//                                    if (agreements1.getItems().get(i).getSignatureType() == Utils.mPP) {
//                                        mPrivacyVno.setText(agreements1.getItems().get(i).getDocumentVersion());
//                                    }
//                                    if (agreements1.getItems().get(i).getSignatureType() == Utils.mTOS) {
//                                        mTermsVno.setText(agreements1.getItems().get(i).getDocumentVersion());
//                                    }
//                                    if (agreements1.getItems().get(i).getSignatureType() == Utils.mAgmt) {
//                                        mMerchantsVno.setText(agreements1.getItems().get(i).getDocumentVersion());
//                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
            @Override
            public void onChanged(AddBusinessUserResponse btResp) {
                try {
                    dismissDialog();
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d(TAG, "btResp" + btResp);
                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                                Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                                intent.putExtra("showGetStarted", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent i = new Intent(ReviewApplicationActivity.this, DashboardActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }

                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        applicationSubmissionViewModel.getPostApplicationSubmissionData().observe(this, new Observer<ApplicationSubmitResponseModel>() {
            @Override
            public void onChanged(ApplicationSubmitResponseModel submissionViewModel) {
                if (submissionViewModel != null) {
                    dismissDialog();
                    if (submissionViewModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                        objMyApplication.setSubmitResponseModel(submissionViewModel);
                        LogUtils.d(TAG, "applicationSubmissionViewModel" + addDBA + ",,,,," + addBusiness);
//                        if (addBusiness) {
//                            loginViewModel.postChangeAccount(objMyApplication.getLoginUserId());
//                        } else {
                            Intent intent = new Intent(ReviewApplicationActivity.this, BusinessDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
//                        }

                    } else {
                        Utils.displayAlert(submissionViewModel.getError().getErrorDescription(), ReviewApplicationActivity.this, "", submissionViewModel.getError().getFieldErrors().get(0));
                    }
                }
            }
        });

        dashboardViewModel.getDownloadDocumentResponse().observe(this, new Observer<DownloadDocumentResponse>() {
            @Override
            public void onChanged(DownloadDocumentResponse downloadDocumentResponse) {
                dismissDialog();
                if (downloadDocumentResponse != null && downloadDocumentResponse.getStatus() != null) {
                    if (downloadDocumentResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        DownloadDocumentData data = downloadDocumentResponse.getData();
                        if (data != null) {
                            if (data.getDownloadUrl() != null && !data.getDownloadUrl().equals("")) {
                                launchDocumentUrl(data.getDownloadUrl());
                            } else {
                                Utils.displayAlert(getString(R.string.unable_to_get_document), ReviewApplicationActivity.this, "", "");
                            }
                        }
                    } else {
                        Utils.displayAlert(downloadDocumentResponse.getError().getErrorDescription(), ReviewApplicationActivity.this, "", "");
                    }
                }
            }
        });

        businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
            @Override
            public void onChanged(BusinessTypeResp businessTypeResp) {
                if (businessTypeResp != null) {
                    if (businessTypeResp.getStatus().toLowerCase().toString().equals("success")) {
                        objMyApplication.setBusinessTypeResp(businessTypeResp);
                        businessIdentityVerificationViewModel.getDBAInfo();
                    }
                }
            }
        });

        dashboardViewModel.getDownloadUrlResponse().observe(this, new Observer<DownloadImageResponse>() {
            @Override
            public void onChanged(DownloadImageResponse downloadImageResponse) {
                dismissDialog();
                if (downloadImageResponse != null && downloadImageResponse.getStatus() != null) {
                    if (downloadImageResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        List<DownloadImageData> data = downloadImageResponse.getData();
                        if (data != null && data.size() > 0) {
                            if (data.get(0).getDownloadUrl() != null && !data.get(0).getDownloadUrl().equals("")) {
                                convertPdfUrl(data.get(0).getDownloadUrl());
                            } else {
                                Utils.displayAlert(getString(R.string.unable_to_get_document), ReviewApplicationActivity.this, "", "");
                            }
                        }
                    } else {
                        Utils.displayAlert(downloadImageResponse.getError().getErrorDescription(), ReviewApplicationActivity.this, "", "");
                    }
                }
            }
        });

    }

    private void showFile(String key) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            DownloadUrlRequest downloadUrlRequest = new DownloadUrlRequest();
            downloadUrlRequest.setKey(key);
            ArrayList<DownloadUrlRequest> urlList = new ArrayList<>();
            urlList.add(downloadUrlRequest);
            dashboardViewModel.getDownloadUrl(urlList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void convertPdfUrl(String fileUrl) {
        if (fileUrl != null && !fileUrl.trim().equalsIgnoreCase("")) {
            if (fileUrl.contains(".pdf")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(ReviewApplicationActivity.this, WebViewShowFileActivity.class);
                intent.putExtra("FILEURL", fileUrl);
                startActivity(intent);

            }


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
        LogUtils.d(TAG, "selectedBankItem" + id);
        if (id == 0) {
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
            }, 1500);
            //temporary API Call to proceed further,not required response
            summaryViewModel.fees();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setSpannableText() {

        SpannableString ss = new SpannableString("By clicking this box, I acknowledge I have read and agree to the Terms of Service & Privacy Policy ");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.e("Click", "click");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showProgressDialog();
                selectedAgreement = getString(R.string.gbx_tos);
                dashboardViewModel.getDocumentUrl(Utils.mTOS);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.e("Click", "click");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showProgressDialog();
                selectedAgreement = getString(R.string.gbx_pp);
                dashboardViewModel.getDocumentUrl(Utils.mPP);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        ss.setSpan(clickableSpan, 65, 81, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 84, 98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), 65, 81, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), 84, 98, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableTV.setText(ss);
        spannableTV.setMovementMethod(LinkMovementMethod.getInstance());
        spannableTV.setHighlightColor(Color.TRANSPARENT);
    }

    private void launchDocumentUrl(String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(url);
//        intent.setDataAndType(uri, "application/pdf");
//        startActivity(intent);

        startActivity(new Intent(ReviewApplicationActivity.this, PDFWebViewActivity.class)
                .putExtra("URL", url)
                .putExtra("NAME", selectedAgreement));

    }
}

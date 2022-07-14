package com.greenbox.coyni.view.business;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.PaymentMethodsAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.cards.CardDeleteResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AddCardActivity;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.EditCardActivity;
import com.greenbox.coyni.view.PaymentMethodsActivity;
import com.greenbox.coyni.view.WebViewActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.util.List;

public class BusinessPaymentMethodsActivity extends BaseActivity {
    MyApplication objMyApplication;
    PaymentMethodsResponse paymentMethodsResponse;
    BusinessDashboardViewModel businessDashboardViewModel;
    CustomerProfileViewModel customerProfileViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    String strSignOn = "", strCurrent = "";
    SignOnData signOnData;
    Dialog dialog, pDialog, extBankDialog;
    RecyclerView rvPaymentMethods;
    LinearLayout lyBPayClose, lyExternalClose, lyPayBack;
    TextView tvBankError, tvSignetError, tvDCardError, tvLearnMore, tvBankCount, tvSignetCount, tvDCardCount, tvMsg;
    TextView tvBankHead, tvBankMsg, tvSignetHead, tvSignetMsg, tvDCHead, tvDCardMsg, tvErrorHead, tvErrorMessage;
    RelativeLayout lyAddBank, layoutSignet, layoutDCard;
    CardView cvNext, cvAddPayment, cvTryAgain, cvDone;
    ImageView imgBankIcon, imgBankArrow, imgSignetLogo, imgSignetArrow, imgDCardLogo, imgDCardArrow, imgLogo;
    Long mLastClickTime = 0L;
    Boolean isBank = false, isPayments = false, isDeCredit = false, isBankSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_payment_methods);
        initialization();
        initObserver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode > 0) {
                switch (requestCode) {
                    case 1:
                        if (data == null) {
                            if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                                Utils.displayAlert("Bank integration has been cancelled", BusinessPaymentMethodsActivity.this, "", "");
                            } else {
                                if (extBankDialog != null) {
                                    extBankDialog.dismiss();
                                }
                                showProgressDialog();
                                customerProfileViewModel.meSyncAccount();
                            }
                        }
                        break;
                    case 2:
                        if (objMyApplication.getSignet()) {
                            objMyApplication.setSignet(false);
                            getPaymentMethods();
                        }
                        break;
                    case 3:
                        if (strCurrent.equals("debit")) {
                            if (!objMyApplication.getCardSave()) {
                                isDeCredit = true;
                                ControlMethod("addpayment");
                            } else {
                                objMyApplication.setCardSave(false);
                                ControlMethod("paymentMethods");
                                strCurrent = "paymentMethods";
                            }
                            getPaymentMethods();
                        }
                        break;
                    case 4:
                        if (data.getStringExtra("screen") != null && data.getStringExtra("screen").equals("editcard")) {
                            if (data.getStringExtra("action") != null && data.getStringExtra("action").equals("remove")) {
                                deleteBank(objMyApplication.getSelectedCard());
                            }
                        }
                        break;
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            super.onActivityResult(requestCode, resultCode, data);
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (!isBankSuccess) {
//            if (strCurrent.equals("addpay") || strCurrent.equals("addpayment")) {
                if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0 && (strCurrent.equals("addpay") || strCurrent.equals("addpayment"))) {
                    getPaymentMethods();
                } else if (strCurrent.equals("externalBank")) {
                    ControlMethod("addpayment");
                    strCurrent = "addpayment";
                } else if (!strCurrent.equals("firstError")) {
                    super.onBackPressed();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
//                dismissDialog();
            if (!isBankSuccess) {
                if (strCurrent.equals("firstError")) {
                    displayError();
                } else if (strCurrent.equals("addpay") || strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit") || strCurrent.equals("signet")) {
                    ControlMethod("addpayment");
                    addPayment();
                    strCurrent = "addpayment";
                } else {
                    if (!isPayments) {
                        getPaymentMethods();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            if (Utils.checkInternet(BusinessPaymentMethodsActivity.this)) {
                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                    customerProfileViewModel.meSignOn();
                } else {
                    strSignOn = objMyApplication.getStrSignOnError();
                    signOnData = objMyApplication.getSignOnData();
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), BusinessPaymentMethodsActivity.this, "", "");
            }
            if (paymentMethodsResponse.getData() != null && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                ControlMethod("paymentMethods");
                strCurrent = "paymentMethods";
            } else {
                ControlMethod("addpayment");
                strCurrent = "addpayment";
            }
            addPayment();
            paymentMethods();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {

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
                                objMyApplication.callResolveFlow(BusinessPaymentMethodsActivity.this, strSignOn, signOnData);
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

        customerProfileViewModel.getApiErrorMutableLiveData().observe(BusinessPaymentMethodsActivity.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dismissDialog();
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            customerProfileViewModel.meSignOn();
                        } else if (!isBank) {
                            if (!apiError.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BusinessPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BusinessPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else {
                            isBank = false;
                            if (apiError.getError().getErrorCode().equals(getString(R.string.bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("this payment method has already")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BusinessPaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
                            } else if (apiError.getError().getErrorCode().equals(getString(R.string.no_bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("no bank accounts found")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BusinessPaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
                            } else {
                                displayError();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getSyncAccountMutableLiveData().observe(BusinessPaymentMethodsActivity.this, new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                try {
                    dismissDialog();
                    if (syncAccount != null) {
                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
                            businessDashboardViewModel.meBusinessPaymentMethods();
                            displaySuccess();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse payMethodsResponse) {
                dismissDialog();
                if (payMethodsResponse != null) {
                    objMyApplication.setPaymentMethodsResponse(payMethodsResponse);
                    paymentMethodsResponse = payMethodsResponse;
                    if (isDeCredit) {
                        isDeCredit = false;
                        ControlMethod("addpayment");
                        strCurrent = "addpayment";
                        numberOfAccounts();
                    } else if (isPayments && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                        isPayments = false;
                        ControlMethod("paymentMethods");
                        strCurrent = "paymentMethods";
                        paymentMethods();
                    } else if (isPayments) {
                        isPayments = false;
                        ControlMethod("addpayment");
                        strCurrent = "addpayment";
                        addPayment();
                    }
                }
            }
        });

        paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                pDialog.dismiss();
                if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {
//                    Utils.showCustomToast(BusinessPaymentMethodsActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                    Utils.showCustomToast(BusinessPaymentMethodsActivity.this, bankDeleteResponseData.getData(), R.drawable.ic_custom_tick, "");
                    getPaymentMethods();
                }
            }
        });

        paymentMethodsViewModel.getCardDeleteResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                pDialog.dismiss();
                if (cardDeleteResponse.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(BusinessPaymentMethodsActivity.this, "Card has been removed.", R.drawable.ic_custom_tick, "");
                    getPaymentMethods();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dismissDialog();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), BusinessPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BusinessPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void addPayment() {
        try {
            lyBPayClose = findViewById(R.id.lyBPayClose);
            tvBankError = findViewById(R.id.tvBBankError);
            tvSignetError = findViewById(R.id.tvSignetError);
            tvDCardError = findViewById(R.id.tvBDCardError);
            tvBankCount = findViewById(R.id.tvBankCount);
            tvSignetCount = findViewById(R.id.tvSignetCount);
            tvDCardCount = findViewById(R.id.tvDCardCount);
            tvBankHead = findViewById(R.id.tvBankHead);
            tvBankMsg = findViewById(R.id.tvBankMsg);
            tvSignetHead = findViewById(R.id.tvSignetHead);
            tvSignetMsg = findViewById(R.id.tvSignetMsg);
            tvDCHead = findViewById(R.id.tvBDCHead);
            tvDCardMsg = findViewById(R.id.tvBDCardMsg);
            imgBankIcon = findViewById(R.id.imgBBankIcon);
            imgBankArrow = findViewById(R.id.imgBBankArrow);
            imgSignetLogo = findViewById(R.id.imgSignetLogo);
            imgSignetArrow = findViewById(R.id.imgSignetArrow);
            imgDCardLogo = findViewById(R.id.imgBDCardLogo);
            imgDCardArrow = findViewById(R.id.imgBDCardArrow);
            lyAddBank = findViewById(R.id.lyAddBank);
            lyExternalClose = findViewById(R.id.lyExternalClose);
            layoutSignet = findViewById(R.id.layoutSignet);
            layoutDCard = findViewById(R.id.layoutBDCard);
            cvNext = findViewById(R.id.cvNext);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            tvMsg = findViewById(R.id.tvMessage);
            imgLogo = findViewById(R.id.imgLogo);
            if (paymentMethodsResponse.getData() != null && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                tvMsg.setVisibility(View.GONE);
                imgLogo.setImageResource(R.drawable.ic_addpayment_method);
            } else {
                imgLogo.setImageResource(R.drawable.ic_addpayment_method2);
                tvMsg.setVisibility(View.VISIBLE);
            }

            lyBPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((strCurrent.equals("addpay") || strCurrent.equals("addpayment") || strCurrent.equals("debit") || strCurrent.equals("credit"))
                            && (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0)) {
                        ControlMethod("paymentMethods");
                        strCurrent = "paymentMethods";
                    } else {
                        strCurrent = "";
                        onBackPressed();
                    }
                }
            });

            lyAddBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (paymentMethodsResponse.getData().getBankCount() < paymentMethodsResponse.getData().getMaxBankAccountsAllowed()) {
                            ControlMethod("externalBank");
                            strCurrent = "externalBank";
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutSignet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (paymentMethodsResponse.getData().getSignetCount() < paymentMethodsResponse.getData().getMaxSignetAccountsAllowed()) {
                            strCurrent = "signet";
                            Intent i = new Intent(BusinessPaymentMethodsActivity.this, AddPaymentSignetActivity.class);
                            startActivityForResult(i, 2);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutDCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        if (paymentMethodsResponse.getData().getCreditCardCount() < paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                        if (paymentMethodsResponse.getData().getDebitCardCount() < paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                            strCurrent = "debit";
                            Intent i = new Intent(BusinessPaymentMethodsActivity.this, AddCardActivity.class);
                            i.putExtra("card", "debit");
                            startActivityForResult(i, 3);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lyExternalClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ControlMethod("addpayment");
                    strCurrent = "addpayment";
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                            isBank = true;
                            Intent i = new Intent(BusinessPaymentMethodsActivity.this, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, BusinessPaymentMethodsActivity.this, "", "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            tvLearnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.populateLearnMore(BusinessPaymentMethodsActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            numberOfAccounts();
        } catch (
                Exception ex) {
            ex.printStackTrace();
        }

    }

    private void paymentMethods() {
        try {
            rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
            lyPayBack = findViewById(R.id.lyPayBack);
            cvAddPayment = findViewById(R.id.cvAddPayment);
            numberOfAccounts();
            if (paymentMethodsResponse.getData() != null && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                bindPaymentMethods(paymentMethodsResponse.getData().getData());
            }
            lyPayBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strCurrent.equals("paymentMethods")) {
                        onBackPressed();
                    }
                }
            });
            cvAddPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberOfAccounts();
                    ControlMethod("addpayment");
                    strCurrent = "addpay";
                    addPayment();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindPaymentMethods(List<PaymentsList> listPayments) {
        PaymentMethodsAdapter paymentMethodsAdapter;
        try {
            if (listPayments != null && listPayments.size() > 0) {
                paymentMethodsAdapter = new PaymentMethodsAdapter(listPayments, BusinessPaymentMethodsActivity.this, "business");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(BusinessPaymentMethodsActivity.this);
                rvPaymentMethods.setLayoutManager(mLayoutManager);
                rvPaymentMethods.setItemAnimator(new DefaultItemAnimator());
                rvPaymentMethods.setAdapter(paymentMethodsAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void numberOfAccounts() {
        try {
            if (paymentMethodsResponse.getData() != null) {
                tvBankCount.setText("(" + paymentMethodsResponse.getData().getBankCount() + "/" + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + ")");
                tvSignetCount.setText("(" + paymentMethodsResponse.getData().getSignetCount() + "/" + paymentMethodsResponse.getData().getMaxSignetAccountsAllowed() + ")");
                tvDCardCount.setText("(" + paymentMethodsResponse.getData().getDebitCardCount() + "/" + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + ")");

                tvBankError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + " banks");
                tvDCardError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + " cards");
                tvSignetError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxSignetAccountsAllowed() + " accounts");
            }
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                if (paymentMethodsResponse.getData().getBankCount() >= paymentMethodsResponse.getData().getMaxBankAccountsAllowed()) {
                    tvBankError.setVisibility(View.VISIBLE);
                    tvBankHead.setTextColor(getColor(R.color.light_gray));
                    tvBankCount.setTextColor(getColor(R.color.light_gray));
                    tvBankMsg.setTextColor(getColor(R.color.light_gray));
                    imgBankArrow.setColorFilter(getColor(R.color.light_gray));
                    imgBankIcon.setImageResource(R.drawable.ic_bank_account_inactive);
                } else {
                    tvBankError.setVisibility(View.GONE);
                    tvBankHead.setTextColor(getColor(R.color.primary_black));
                    tvBankCount.setTextColor(getColor(R.color.dark_grey));
                    tvBankMsg.setTextColor(getColor(R.color.dark_grey));
                    imgBankArrow.setColorFilter(getColor(R.color.primary_black));
                    imgBankIcon.setImageResource(R.drawable.ic_bank_account_active);
                }
                if (paymentMethodsResponse.getData().getDebitCardCount() >= paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                    tvDCardError.setVisibility(View.VISIBLE);
                    tvDCHead.setTextColor(getColor(R.color.light_gray));
                    tvDCardCount.setTextColor(getColor(R.color.light_gray));
                    tvDCardMsg.setTextColor(getColor(R.color.light_gray));
                    imgDCardArrow.setColorFilter(getColor(R.color.light_gray));
                    imgDCardLogo.setImageResource(R.drawable.ic_credit_debit_card_inactive);
                } else {
                    tvDCardError.setVisibility(View.GONE);
                    tvDCHead.setTextColor(getColor(R.color.primary_black));
                    tvDCardCount.setTextColor(getColor(R.color.dark_grey));
                    tvDCardMsg.setTextColor(getColor(R.color.dark_grey));
                    imgDCardArrow.setColorFilter(getColor(R.color.primary_black));
                    imgDCardLogo.setImageResource(R.drawable.ic_credit_debit_card);
                }
                if (paymentMethodsResponse.getData().getSignetCount() >= paymentMethodsResponse.getData().getMaxSignetAccountsAllowed()) {
                    tvSignetError.setVisibility(View.VISIBLE);
                    tvSignetHead.setTextColor(getColor(R.color.light_gray));
                    tvSignetCount.setTextColor(getColor(R.color.light_gray));
                    tvSignetMsg.setTextColor(getColor(R.color.light_gray));
                    imgSignetArrow.setColorFilter(getColor(R.color.light_gray));
                    imgSignetLogo.setImageResource(R.drawable.ic_signetinactive);
                } else {
                    tvSignetError.setVisibility(View.GONE);
                    tvSignetHead.setTextColor(getColor(R.color.primary_black));
                    tvSignetCount.setTextColor(getColor(R.color.dark_grey));
//                    tvSignetHead.setTextColor(getColor(R.color.light_gray));
//                    tvSignetCount.setTextColor(getColor(R.color.light_gray));
                    tvSignetMsg.setTextColor(getColor(R.color.dark_grey));
                    imgSignetLogo.setImageResource(R.drawable.ic_signetactive);
//                    imgSignetLogo.setImageResource(R.drawable.ic_signetinactive);
                    imgSignetArrow.setColorFilter(getColor(R.color.primary_black));
//                    imgSignetArrow.setColorFilter(getColor(R.color.light_gray));

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPaymentMethods() {
        try {
            isPayments = true;
//            dialog = Utils.showProgressDialog(BusinessPaymentMethodsActivity.this);
            showProgressDialog();
            businessDashboardViewModel.meBusinessPaymentMethods();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayError() {
        try {
            ControlMethod("firstError");
            tvErrorHead = findViewById(R.id.tvErrorHead);
            tvErrorMessage = findViewById(R.id.tvErrorMessage);
            cvTryAgain = findViewById(R.id.cvTryAgain);
            tvErrorHead.setText(getString(R.string.bank_exhausthead));
            strCurrent = "firstError";
            tvErrorMessage.setText("There is an account limit of " + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + " total bank accounts, and it looks like you surpassed that number via the Fiserv bank account verification process. Please try again or remove one or more of your current bank account payment methods.");
            cvTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ControlMethod("externalBank");
                    strCurrent = "externalBank";
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displaySuccess() {
        try {
            isBankSuccess = true;
            ControlMethod("banksuccess");
            cvDone = findViewById(R.id.cvDone);
            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBankSuccess = false;
                    if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                        ControlMethod("paymentMethods");
                        strCurrent = "paymentMethods";
                        paymentMethods();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "addpayment": {
                    findViewById(R.id.addpayment).setVisibility(View.VISIBLE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                }
                break;
                case "paymentMethods": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.VISIBLE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                }
                break;
                case "externalBank": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                }
                break;
                case "firstError": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.VISIBLE);
                }
                break;
                case "banksuccess": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteBank(PaymentsList objPayment) {
        try {
            final Dialog dialog = new Dialog(BusinessPaymentMethodsActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.payment_remove);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            ImageView imgBankIcon = dialog.findViewById(R.id.imgBankIcon);
            TextView tvBankName = dialog.findViewById(R.id.tvBankName);
            TextView tvCardName = dialog.findViewById(R.id.tvCardName);
            TextView tvAccount = dialog.findViewById(R.id.tvAccount);
            TextView tvCardNumber = dialog.findViewById(R.id.tvCardNumber);
            TextView tvNo = dialog.findViewById(R.id.tvNo);
            TextView tvYes = dialog.findViewById(R.id.tvYes);
            LinearLayout layoutCard = dialog.findViewById(R.id.layoutCard);
            LinearLayout layoutBank = dialog.findViewById(R.id.layoutBank);
            if (objPayment != null) {
                if (objPayment.getPaymentMethod().toLowerCase().equals("bank")) {
                    layoutCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.VISIBLE);
                    imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                    tvBankName.setText(objPayment.getBankName());
                    if (objPayment.getAccountNumber() != null && objPayment.getAccountNumber().length() > 4) {
                        tvAccount.setText("**** " + objPayment.getAccountNumber().substring(objPayment.getAccountNumber().length() - 4));
                    } else {
                        tvAccount.setText(objPayment.getAccountNumber());
                    }
                } else if (objPayment.getPaymentMethod().toLowerCase().equals("signet")) {
                    layoutCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.VISIBLE);
                    imgBankIcon.setImageResource(R.drawable.ic_signetactive);
                    tvAccount.setVisibility(View.GONE);
                    if (objPayment.getAccountNumber() != null && objPayment.getAccountNumber().length() > 14) {
                        tvBankName.setText(objPayment.getAccountNumber().substring(0, 10) + "**** " + objPayment.getAccountNumber().substring(objPayment.getAccountNumber().length() - 4));
                    } else {
                        tvBankName.setText(objPayment.getAccountNumber());
                    }
                } else {
                    layoutCard.setVisibility(View.VISIBLE);
                    layoutBank.setVisibility(View.GONE);
                    tvCardNumber.setText("****" + objPayment.getLastFour());
                    switch (objPayment.getCardBrand().toUpperCase().replace(" ", "")) {
                        case "VISA":
                            tvCardName.setText(Utils.capitalize(objPayment.getCardBrand() + " " + objPayment.getCardType()));
                            imgBankIcon.setImageResource(R.drawable.ic_visaactive);
                            break;
                        case "MASTERCARD":
                            tvCardName.setText(Utils.capitalize(objPayment.getCardBrand() + " " + objPayment.getCardType()));
                            imgBankIcon.setImageResource(R.drawable.ic_masteractive);
                            break;
                        case "AMERICANEXPRESS":
                            tvCardName.setText("American Express Card");
                            imgBankIcon.setImageResource(R.drawable.ic_amexactive);
                            break;
                        case "DISCOVER":
                            tvCardName.setText("Discover Card");
                            imgBankIcon.setImageResource(R.drawable.ic_discoveractive);
                            break;
                    }
                }
            }

            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    pDialog = Utils.showProgressDialog(BusinessPaymentMethodsActivity.this);
                    if (objPayment.getPaymentMethod().toLowerCase().equals("bank") || objPayment.getPaymentMethod().toLowerCase().equals("signet")) {
                        paymentMethodsViewModel.deleteBanks(objPayment.getId());
                    } else {
                        paymentMethodsViewModel.deleteCards(objPayment.getId());
                    }
                }
            });
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void expiry(PaymentsList objPayment) {
        try {
            final Dialog dialog = new Dialog(BusinessPaymentMethodsActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.payment_expire);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvMessage = dialog.findViewById(R.id.tvMessage);
            TextView tvRemove = dialog.findViewById(R.id.tvRemove);
            TextView tvEdit = dialog.findViewById(R.id.tvEdit);
            if (objPayment != null) {
                if (objPayment.getPaymentMethod().toLowerCase().equals("bank")) {
                    tvMessage.setText("Seems like you have an issue with your bank account");
                    tvEdit.setText("Relink");
                    customerProfileViewModel.meSignOn();
                } else {
                    tvMessage.setText("Seems like you have an issue with your card");
                    tvEdit.setText("Edit");
                }

            }
            tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    deleteBank(objPayment);
                }
            });

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    try {
                        if (!objPayment.getPaymentMethod().toLowerCase().equals("bank")) {
                            Intent i = new Intent(BusinessPaymentMethodsActivity.this, EditCardActivity.class);
                            startActivity(i);
                        } else {
//                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
//                                isBank = true;
//                                //objMyApplication.setResolveUrl(true);
//                                Intent i = new Intent(BusinessPaymentMethodsActivity.this, WebViewActivity.class);
//                                i.putExtra("signon", signOnData);
//                                startActivityForResult(i, 1);
//                            } else {
//                                Utils.displayAlert(strSignOn, BusinessPaymentMethodsActivity.this, "", "");
//                            }
                            showExternalBank();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editCard() {
        try {
            Intent i = new Intent(BusinessPaymentMethodsActivity.this, EditCardActivity.class);
            startActivityForResult(i, 4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showExternalBank() {
        try {
            extBankDialog = new Dialog(BusinessPaymentMethodsActivity.this);
            extBankDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            extBankDialog.setContentView(R.layout.activity_add_external_bank_acc);
            extBankDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvBankHead = extBankDialog.findViewById(R.id.tvBankHead);
            TextView tvEBMessage = extBankDialog.findViewById(R.id.tvEBMessage);
            TextView tvLearn = extBankDialog.findViewById(R.id.tvLearnMore);
            CardView cNext = extBankDialog.findViewById(R.id.cvNext);
            LinearLayout lyClose = extBankDialog.findViewById(R.id.lyExternalClose);
            tvBankHead.setText("Link External Bank Account");
            tvEBMessage.setText("We will now redirect you to our trusted partner Fiserv.\nWho will help you verify your external bank account with us.\n\nAre you ready to begin the process?");
            tvLearn.setVisibility(View.GONE);

            Window window = extBankDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            extBankDialog.show();
            lyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        extBankDialog.dismiss();
                        if (strCurrent.equals("firstError")) {
                            ControlMethod("addpayment");
                            strCurrent = "addpayment";
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            cNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                            isBank = true;
                            Intent i = new Intent(BusinessPaymentMethodsActivity.this, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, BusinessPaymentMethodsActivity.this, "", "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            extBankDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        extBankDialog.dismiss();
                        if (strCurrent.equals("firstError")) {
                            ControlMethod("addpayment");
                            strCurrent = "addpayment";
                        }
                    }
                    return true;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
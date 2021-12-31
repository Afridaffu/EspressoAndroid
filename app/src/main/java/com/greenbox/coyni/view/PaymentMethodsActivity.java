package com.greenbox.coyni.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.util.List;

public class PaymentMethodsActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    PaymentMethodsResponse paymentMethodsResponse;
    LinearLayout lyAPayClose, lyExternalClose, lyPayBack;
    CardView cvNext, cvAddPayment;
    TextView tvBankError, tvDCardError, tvCCardError, tvExtBankHead, tvExtBankMsg, tvDCardHead, tvDCardMsg, tvCCardHead, tvCCardMsg;
    TextView tvErrorMessage, tvLearnMore, tvExtBHead, tvDCHead, tvCCHead, tvErrorHead, tvMessage;
    String strCurrent = "", strSignOn = "", strScreen = "";
    ImageView imgBankArrow, imgBankIcon, imgDCardLogo, imgDCardArrow, imgCCardLogo, imgCCardArrow, imgLogo;
    RecyclerView rvPaymentMethods;
    CustomerProfileViewModel customerProfileViewModel;
    DashboardViewModel dashboardViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    ProgressDialog dialog, pDialog;
    SignOnData signOnData;
    Boolean isBank = false, isPayments = false, isDeCredit = false;
    RelativeLayout layoutDCard, lyExternal, layoutCCard;
    CardView cvTryAgain, cvDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_payment_methods);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (strCurrent.equals("addpay")) {
                ControlMethod("paymentMethods");
                strCurrent = "paymentMethods";
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", PaymentMethodsActivity.this, "", "");
                } else {
                    dialog = Utils.showProgressDialog(this);
                    customerProfileViewModel.meSyncAccount();
                }
            } else if (requestCode == 2) {
                if (data.getStringExtra("screen") != null && data.getStringExtra("screen").equals("editcard")) {
                    if (data.getStringExtra("action") != null && data.getStringExtra("action").equals("remove")) {
                        deleteBank(PaymentMethodsActivity.this, objMyApplication.getSelectedCard());
                    }
                }
            } else if (requestCode == 3) {
                if (strCurrent.equals("debit") || strCurrent.equals("credit")) {
                    //ControlMethod("addpayment");
                    ControlMethod("paymentMethods");
                    strCurrent = "paymentMethods";
                    getPaymentMethods();
                    isDeCredit = true;
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            super.onActivityResult(requestCode, resultCode, data);
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
            }
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            if (Utils.checkInternet(PaymentMethodsActivity.this)) {
                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                    customerProfileViewModel.meSignOn();
                } else {
                    strSignOn = objMyApplication.getStrSignOnError();
                    signOnData = objMyApplication.getSignOnData();
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), PaymentMethodsActivity.this, "", "");
            }
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                ControlMethod("paymentMethods");
                strCurrent = "paymentMethods";
            } else {
                ControlMethod("addpayment");
                strCurrent = "addpayment";
            }
            addPayment();
            paymentMethods();
//            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("editcard")) {
//                if (getIntent().getStringExtra("action") != null && getIntent().getStringExtra("action").equals("remove")) {
//                    deleteBank(PaymentMethodsActivity.this, objMyApplication.getSelectedCard());
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (strCurrent.equals("addpay") || strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit")) {
                ControlMethod("addpayment");
//                strCurrent = "addpay";
                addPayment();
            } else {
                getPaymentMethods();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
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
                                objMyApplication.callResolveFlow(PaymentMethodsActivity.this,strSignOn,signOnData);
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

        customerProfileViewModel.getApiErrorMutableLiveData().observe(PaymentMethodsActivity.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            customerProfileViewModel.meSignOn();
                        } else if (!isBank) {
                            if (!apiError.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), PaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), PaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else {
                            isBank = false;
                            if (apiError.getError().getErrorCode().equals(getString(R.string.bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("this payment method has already")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), PaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
                            } else {
//                                String strError = "";
//                                if (!apiError.getError().getErrorDescription().equals("")) {
//                                    strError = apiError.getError().getErrorDescription();
//                                } else {
//                                    strError = apiError.getError().getFieldErrors().get(0);
//                                }
//                                displayError(strError);
                                displayError();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getSyncAccountMutableLiveData().observe(PaymentMethodsActivity.this, new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                try {
                    dialog.dismiss();
                    if (syncAccount != null) {
                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
                            dashboardViewModel.mePaymentMethods();
                            displaySuccess();
                        } else {
//                        if (Arrays.asList(bob).contains("silly")) {
//                            // true
//                        }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse payMethodsResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
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
                        numberOfAccounts();
                    }
                }
            }
        });

        paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                pDialog.dismiss();
                if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(PaymentMethodsActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                    getPaymentMethods();
                }
            }
        });

        paymentMethodsViewModel.getCardDeleteResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                pDialog.dismiss();
                if (cardDeleteResponse.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(PaymentMethodsActivity.this, "Card has been removed.", R.drawable.ic_custom_tick, "");
                    getPaymentMethods();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    pDialog.dismiss();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), PaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), PaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
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
            lyAPayClose = findViewById(R.id.lyAPayClose);
            tvBankError = findViewById(R.id.tvBankError);
            tvDCardError = findViewById(R.id.tvDCardError);
            tvCCardError = findViewById(R.id.tvCCardError);
            lyExternal = findViewById(R.id.lyAddExternal);
            lyExternalClose = findViewById(R.id.lyExternalClose);
            tvExtBHead = findViewById(R.id.tvExtBHead);
            tvExtBankHead = findViewById(R.id.tvExtBankHead);
            tvExtBankMsg = findViewById(R.id.tvExtBankMsg);
            imgBankArrow = findViewById(R.id.imgBankArrow);
            imgBankIcon = findViewById(R.id.imgBankIcon);
            imgDCardLogo = findViewById(R.id.imgDCardLogo);
            tvDCHead = findViewById(R.id.tvDCHead);
            tvDCardHead = findViewById(R.id.tvDCardHead);
            tvDCardMsg = findViewById(R.id.tvDCardMsg);
            imgDCardArrow = findViewById(R.id.imgDCardArrow);
            imgCCardLogo = findViewById(R.id.imgCCardLogo);
            imgCCardArrow = findViewById(R.id.imgCCardArrow);
            tvCCHead = findViewById(R.id.tvCCHead);
            tvCCardHead = findViewById(R.id.tvCCardHead);
            tvCCardMsg = findViewById(R.id.tvCCardMsg);
            layoutDCard = findViewById(R.id.layoutDCard);
            layoutCCard = findViewById(R.id.layoutCCard);
            cvNext = findViewById(R.id.cvNext);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            tvMessage = findViewById(R.id.tvMessage);
            imgLogo = findViewById(R.id.imgLogo);
            if (strScreen != null && strScreen.equals("dashboard")) {
                imgLogo.setVisibility(View.GONE);
                tvExtBHead.setText("Bank Account");
                tvMessage.setText("Choose a payment method");
                tvMessage.setVisibility(View.VISIBLE);
//            } else if (strScreen != null && strScreen.equals("quick_action")) {
            } else {
                imgLogo.setVisibility(View.VISIBLE);
                tvMessage.setVisibility(View.GONE);
                tvExtBHead.setText("External Bank Account");
                tvMessage.setText("There is no payment method currently \\nlinked to your account. Please follow one of \\nthe prompts below to link an account.");
            }
            lyAPayClose.setOnClickListener(new View.OnClickListener() {
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
            lyExternal.setOnClickListener(new View.OnClickListener() {
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

            layoutDCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (paymentMethodsResponse.getData().getDebitCardCount() < paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                            strCurrent = "debit";
                            Intent i = new Intent(PaymentMethodsActivity.this, AddCardActivity.class);
                            i.putExtra("card", "debit");
                            startActivityForResult(i, 3);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutCCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (paymentMethodsResponse.getData().getCreditCardCount() < paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                            strCurrent = "credit";
                            Intent i = new Intent(PaymentMethodsActivity.this, AddCardActivity.class);
                            i.putExtra("card", "credit");
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
                        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                            isBank = true;
                            Intent i = new Intent(PaymentMethodsActivity.this, WebViewActivity.class);
//                            Intent i = new Intent(PaymentMethodsActivity.this, WebViewActivity1.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, PaymentMethodsActivity.this, "", "");
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
                        Utils.populateLearnMore(PaymentMethodsActivity.this);
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
                }
            });
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
//            tvErrorMessage.setText(getString(R.string.bank_exhaust));
            tvErrorMessage.setText("There is an account limit of " + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + " total bank accounts, and it looks like you surpassed that number via the Fiserv bank account verification process. Please try again or remove one or more of your current bank account payment methods.");
            cvTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ControlMethod("addpayment");
//                    strCurrent = "addpay";
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
            ControlMethod("banksuccess");
            cvDone = findViewById(R.id.cvDone);
            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                }
                break;
                case "paymentMethods": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.VISIBLE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                }
                break;
                case "externalBank": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
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

    private void bindPaymentMethods(List<PaymentsList> listPayments) {
        PaymentMethodsAdapter paymentMethodsAdapter;
        try {
            if (listPayments != null && listPayments.size() > 0) {
                paymentMethodsAdapter = new PaymentMethodsAdapter(listPayments, PaymentMethodsActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(PaymentMethodsActivity.this);
                rvPaymentMethods.setLayoutManager(mLayoutManager);
                rvPaymentMethods.setItemAnimator(new DefaultItemAnimator());
                rvPaymentMethods.setAdapter(paymentMethodsAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void callResolveFlow() {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                Intent i = new Intent(PaymentMethodsActivity.this, WebViewActivity.class);
                i.putExtra("signon", signOnData);
                startActivityForResult(i, 1);
            } else {
                Utils.displayAlert(strSignOn, PaymentMethodsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPaymentMethods() {
        try {
            isPayments = true;
            dialog = Utils.showProgressDialog(this);
            dashboardViewModel.mePaymentMethods();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void numberOfAccounts() {
        try {
            if (paymentMethodsResponse.getData() != null) {
                tvExtBankHead.setText("(" + paymentMethodsResponse.getData().getBankCount() + "/" + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + ")");
                tvDCardHead.setText("(" + paymentMethodsResponse.getData().getDebitCardCount() + "/" + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + ")");
                tvCCardHead.setText("(" + paymentMethodsResponse.getData().getCreditCardCount() + "/" + paymentMethodsResponse.getData().getMaxCreditCardsAllowed() + ")");

                tvBankError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + " banks");
                tvDCardError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + " cards");
                tvCCardError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxCreditCardsAllowed() + " cards");
            }
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                if (paymentMethodsResponse.getData().getBankCount() >= paymentMethodsResponse.getData().getMaxBankAccountsAllowed()) {
                    tvBankError.setVisibility(View.VISIBLE);
                    tvExtBHead.setTextColor(getColor(R.color.light_gray));
                    tvExtBankHead.setTextColor(getColor(R.color.light_gray));
                    tvExtBankMsg.setTextColor(getColor(R.color.light_gray));
                    imgBankArrow.setColorFilter(getColor(R.color.light_gray));
                    imgBankIcon.setImageResource(R.drawable.ic_bank_account_inactive);
                } else {
                    tvBankError.setVisibility(View.GONE);
                    tvExtBHead.setTextColor(getColor(R.color.primary_black));
                    tvExtBankHead.setTextColor(getColor(R.color.dark_grey));
                    tvExtBankMsg.setTextColor(getColor(R.color.dark_grey));
                    imgBankArrow.clearColorFilter();
                    imgBankIcon.setImageResource(R.drawable.ic_bank_account_active);
                }
                if (paymentMethodsResponse.getData().getDebitCardCount() >= paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                    tvDCardError.setVisibility(View.VISIBLE);
                    tvDCHead.setTextColor(getColor(R.color.light_gray));
                    tvDCardHead.setTextColor(getColor(R.color.light_gray));
                    tvDCardMsg.setTextColor(getColor(R.color.light_gray));
                    imgDCardArrow.setColorFilter(getColor(R.color.light_gray));
                    imgDCardLogo.setImageResource(R.drawable.ic_credit_debit_card_inactive);
                } else {
                    tvDCardError.setVisibility(View.GONE);
                    tvDCHead.setTextColor(getColor(R.color.primary_black));
                    tvDCardHead.setTextColor(getColor(R.color.dark_grey));
                    tvDCardMsg.setTextColor(getColor(R.color.dark_grey));
                    imgDCardArrow.clearColorFilter();
                    imgDCardLogo.setImageResource(R.drawable.ic_credit_debit_card);
                }
                if (paymentMethodsResponse.getData().getCreditCardCount() >= paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                    tvCCardError.setVisibility(View.VISIBLE);
                    tvCCHead.setTextColor(getColor(R.color.light_gray));
                    tvCCardHead.setTextColor(getColor(R.color.light_gray));
                    tvCCardMsg.setTextColor(getColor(R.color.light_gray));
                    imgCCardArrow.setColorFilter(getColor(R.color.light_gray));
                    imgCCardLogo.setImageResource(R.drawable.ic_credit_debit_card_inactive);
                } else {
                    tvCCardError.setVisibility(View.GONE);
                    tvCCHead.setTextColor(getColor(R.color.primary_black));
                    tvCCardHead.setTextColor(getColor(R.color.dark_grey));
                    tvCCardMsg.setTextColor(getColor(R.color.dark_grey));
                    imgCCardArrow.clearColorFilter();
                    imgCCardLogo.setImageResource(R.drawable.ic_credit_debit_card);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteBank(Context context, PaymentsList objPayment) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.payment_remove);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
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
                    pDialog = Utils.showProgressDialog(context);
                    if (objPayment.getPaymentMethod().toLowerCase().equals("bank")) {
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

    public void expiry(Context context, PaymentsList objPayment) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.payment_expire);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
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
                    deleteBank(context, objPayment);
                }
            });

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    try {
                        if (!objPayment.getPaymentMethod().toLowerCase().equals("bank")) {
                            Intent i = new Intent(PaymentMethodsActivity.this, EditCardActivity.class);
                            startActivity(i);
                        } else {
                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                isBank = true;
                                objMyApplication.setResolveUrl(true);
                                Intent i = new Intent(PaymentMethodsActivity.this, WebViewActivity.class);
                                i.putExtra("signon", signOnData);
                                startActivityForResult(i, 1);
                            } else {
                                Utils.displayAlert(strSignOn, PaymentMethodsActivity.this, "", "");
                            }
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
            Intent i = new Intent(PaymentMethodsActivity.this, EditCardActivity.class);
            startActivityForResult(i, 2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
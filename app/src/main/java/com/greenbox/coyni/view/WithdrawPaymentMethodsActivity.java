package com.greenbox.coyni.view;

import static android.view.View.VISIBLE;

import static com.greenbox.coyni.view.TransactionListActivity.context;

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
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.greenbox.coyni.adapters.SelectedPaymentMethodsAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.cards.CardDeleteResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.AddPaymentSignetActivity;
import com.greenbox.coyni.view.business.SelectPaymentMethodActivity;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.util.ArrayList;
import java.util.List;

public class WithdrawPaymentMethodsActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomerProfileViewModel customerProfileViewModel;
    DashboardViewModel dashboardViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    WalletResponseData walletResponse;
    Double walletBalance = 0.0;
    Long mLastClickTime = 0L;
    Boolean isBank = false, isPayments = false, isDeCredit = false, isBankSuccess = false, isNoToken = false;
    List<PaymentsList> bankList;
    List<PaymentsList> cardList;
    List<PaymentsList> signetList;
    Dialog payDialog, addPayDialog;
    String strSignOn = "", strCurrent = "", strScreen = "", strOnPauseScreen = "";
    SignOnData signOnData;
    ProgressDialog dialog, pDialog;
    LinearLayout lyAPayClose, lyExternalClose, lyBPayClose;
    RelativeLayout layoutDCard, lyAddExternal, layoutCCard, lyAddBank, layoutSignet;
    TextView tvBankError, tvDCardError, tvCCardError, tvExtBankHead, tvExtBankMsg, tvDCardHead, tvDCardMsg, tvCCardHead, tvCCardMsg;
    TextView tvLearnMore, tvExtBHead, tvDCHead, tvCCHead, tvMessage;
    ImageView imgBankArrow, imgBankIcon, imgDCardLogo, imgDCardArrow, imgCCardLogo, imgCCardArrow, imgLogo;
    CardView cvNext, cvTryAgain, cvDone;
    public static WithdrawPaymentMethodsActivity withdrawPaymentMethodsActivity;
    TextView tvErrorHead, tvErrorMessage;
    TextView tvBankHead, tvBankCount, tvBankMsg, tvBBankError, tvSignetHead, tvSignetCount, tvSignetMsg, tvSignetError;
    ImageView imgBBankIcon, imgBBankArrow, imgSignetLogo, imgSignetArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_withdraw_payment_methods);
            withdrawPaymentMethodsActivity = this;
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", WithdrawPaymentMethodsActivity.this, "", "");
                } else {
                    dialog = Utils.showProgressDialog(this);
                    customerProfileViewModel.meSyncAccount();
                }
            } else if (requestCode == 3) {
                if (strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit")) {
                    if (cardList != null && cardList.size() > 0) {
                        isDeCredit = true;
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            ControlMethod("addpayment");
                        } else {
                            ControlMethod("addbpayment");
                        }
                    } else {
                        ControlMethod("withdrawmethod");
                        selectWithdrawMethod();
                        strScreen = "withdrawmethod";
                        strCurrent = "";
                    }
                    getPaymentMethods();
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
        if (!isBankSuccess) {
            if (strCurrent.equals("externalBank")) {
                if (bankList != null && bankList.size() > 0) {
                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                        ControlMethod("addpayment");
                        addPayment();
                        strCurrent = "addpayment";
                    } else {
                        ControlMethod("addbpayment");
                        addMerchantPayment();
                        strCurrent = "addbpayment";
                    }
                } else {
                    strCurrent = "";
                    ControlMethod("withdrawpay");
                    withdrawPaymentMethod("bank");
                    strScreen = "withdrawpay";
                }
//                strCurrent = "";
//                ControlMethod("withdrawpay");
//                withdrawPaymentMethod("bank");
//                strScreen = "withdrawpay";
            } else if (strCurrent.equals("debit")) {
                strCurrent = "";
                ControlMethod("withdrawpay");
                withdrawPaymentMethod("card");
                strScreen = "withdrawpay";
            } else if ((strScreen.equals("withdrawpay") && !strCurrent.equals("firstError")) || strCurrent.equals("addpayment") || strCurrent.equals("addbpayment")) {
                ControlMethod("withdrawmethod");
                selectWithdrawMethod();
                strScreen = "withdrawmethod";
                strCurrent = "";
            } else if (!strCurrent.equals("firstError")) {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        try {
            if (addPayDialog != null) {
                addPayDialog.dismiss();
            }
            if (strOnPauseScreen.equals("externalBank")) {
                ControlMethod("externalBank");
                strCurrent = "externalBank";
                strOnPauseScreen = "";
            } else if (strCurrent.equals("externalBank")) {
                ControlMethod("withdrawpay");
                withdrawPaymentMethod("bank");
                strScreen = "withdrawpay";
            } else if (strCurrent.equals("debit")) {
                ControlMethod("withdrawpay");
                withdrawPaymentMethod("card");
                strScreen = "withdrawpay";
            } else if (strScreen != null && strScreen.equals("banksuccess")) {
                //Added on 29-03-2022 - VT
            } else if (!isPayments) {
                getPaymentMethods();
            }
            super.onResume();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            walletResponse = objMyApplication.getCurrentUserData().getTokenWalletResponse();
            walletBalance = objMyApplication.getGBTBalance();
            if (paymentMethodsResponse != null) {
                getPayments(paymentMethodsResponse.getData().getData());
            }
            if (walletBalance != 0.0) {
                ControlMethod("withdrawmethod");
                selectWithdrawMethod();
            } else {
                isNoToken = true;
                ControlMethod("withdrawnotoken");
                bindWithdrawNoTokens();
            }
            if (Utils.checkInternet(WithdrawPaymentMethodsActivity.this)) {
                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                    customerProfileViewModel.meSignOn();
                } else {
                    strSignOn = objMyApplication.getStrSignOnError();
                    signOnData = objMyApplication.getSignOnData();
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), WithdrawPaymentMethodsActivity.this, "", "");
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
                                objMyApplication.callResolveFlow(WithdrawPaymentMethodsActivity.this, strSignOn, signOnData);
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

        customerProfileViewModel.getApiErrorMutableLiveData().observe(WithdrawPaymentMethodsActivity.this, new Observer<APIError>() {
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
                                Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), WithdrawPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else {
                            isBank = false;
                            if (apiError.getError().getErrorCode().equals(getString(R.string.bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("this payment method has already")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawPaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
                            } else if (apiError.getError().getErrorCode().equals(getString(R.string.no_bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("no bank accounts found")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawPaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
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

        customerProfileViewModel.getSyncAccountMutableLiveData().observe(WithdrawPaymentMethodsActivity.this, new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                try {
                    dialog.dismiss();
                    if (syncAccount != null) {
                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
                            dashboardViewModel.mePaymentMethods();
                            displaySuccess();
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
                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                        PaymentMethodsResponse objResponse = objMyApplication.filterPaymentMethods(payMethodsResponse);
                        objMyApplication.setPaymentMethodsResponse(objResponse);
                        paymentMethodsResponse = objResponse;
                    } else {
                        objMyApplication.setPaymentMethodsResponse(payMethodsResponse);
                        paymentMethodsResponse = payMethodsResponse;
                    }
                    getPayments(payMethodsResponse.getData().getData());
                    if (isDeCredit) {
                        isDeCredit = false;
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            ControlMethod("addpayment");
                            strCurrent = "addpayment";
                            numberOfAccounts();
                        } else {
                            ControlMethod("addbpayment");
                            strCurrent = "addbpayment";
                            numberOfMerchantAccounts();
                        }
                    } else if (isPayments && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                        isPayments = false;
                        if (isNoToken || objMyApplication.getGBTBalance() == 0) {
                            isNoToken = false;
                            ControlMethod("withdrawnotoken");
                            bindWithdrawNoTokens();
                        } else {
                            ControlMethod("withdrawmethod");
                            selectWithdrawMethod();
                            strScreen = "withdrawmethod";
                        }
                    } else if (isPayments && strCurrent.equals("debit")) {
                        isPayments = false;
                        ControlMethod("withdrawpay");
                        withdrawPaymentMethod("card");
                        strScreen = "withdrawpay";
                    }
                }
            }
        });

        paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                pDialog.dismiss();
                if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(WithdrawPaymentMethodsActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                    getPaymentMethods();
                }
            }
        });

        paymentMethodsViewModel.getCardDeleteResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                pDialog.dismiss();
                if (cardDeleteResponse.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(WithdrawPaymentMethodsActivity.this, "Card has been removed.", R.drawable.ic_custom_tick, "");
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
                            Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), WithdrawPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
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
            lyAddExternal = findViewById(R.id.lyAddExternal);
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
            imgCCardArrow = findViewById(R.id.imgCCardArrowC);
            tvCCHead = findViewById(R.id.tvCCHead);
            tvCCardHead = findViewById(R.id.tvCCardHead);
            tvCCardMsg = findViewById(R.id.tvCCardMsg);
            layoutDCard = findViewById(R.id.layoutDCard);
            layoutCCard = findViewById(R.id.layoutCCard);
            cvNext = findViewById(R.id.cvNext);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            tvMessage = findViewById(R.id.tvMessage);
            imgLogo = findViewById(R.id.imgLogo);
            lyAPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strCurrent.equals("addpay") || strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit")) {
                        ControlMethod("paymentMethods");
                        strCurrent = "paymentMethods";
                    } else {
                        onBackPressed();
                    }
                }
            });
            lyAddExternal.setOnClickListener(new View.OnClickListener() {
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
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddCardActivity.class);
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
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddCardActivity.class);
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
                    try {
                        ControlMethod("addpayment");
                        strCurrent = "addpayment";
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                            isBank = true;
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, WithdrawPaymentMethodsActivity.this, "", "");
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
                        Utils.populateLearnMore(WithdrawPaymentMethodsActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            numberOfAccounts();
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
                    imgBankArrow.setEnabled(false);
                    imgBankIcon.setImageResource(R.drawable.ic_bank_account_inactive);
                } else {
                    tvBankError.setVisibility(View.GONE);
                    tvExtBHead.setTextColor(getColor(R.color.primary_black));
                    tvExtBankHead.setTextColor(getColor(R.color.dark_grey));
                    tvExtBankMsg.setTextColor(getColor(R.color.dark_grey));
//                    imgBankArrow.clearColorFilter();
                    imgBankArrow.setColorFilter(getColor(R.color.primary_black));
                    imgBankArrow.setEnabled(true);
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
//                    imgDCardArrow.clearColorFilter();
                    imgDCardArrow.setColorFilter(getColor(R.color.primary_black));
                    imgDCardLogo.setImageResource(R.drawable.ic_credit_debit_card);
                }
                if (paymentMethodsResponse.getData().getCreditCardCount() >= paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                    tvCCardError.setVisibility(View.VISIBLE);
                    tvCCHead.setTextColor(getColor(R.color.light_gray));
                    tvCCardHead.setTextColor(getColor(R.color.light_gray));
                    tvCCardMsg.setTextColor(getColor(R.color.light_gray));
                    imgCCardArrow.setColorFilter(getColor(R.color.light_gray));
                    imgCCardArrow.setEnabled(false);
                    imgCCardLogo.setImageResource(R.drawable.ic_credit_debit_card_inactive);
                } else {
                    tvCCardError.setVisibility(View.GONE);
                    tvCCHead.setTextColor(getColor(R.color.primary_black));
                    tvCCardHead.setTextColor(getColor(R.color.dark_grey));
                    tvCCardMsg.setTextColor(getColor(R.color.dark_grey));
//                    imgCCardArrow.clearColorFilter();
                    imgCCardArrow.setColorFilter(getColor(R.color.primary_black));
                    imgCCardArrow.setEnabled(true);
                    imgCCardLogo.setImageResource(R.drawable.ic_credit_debit_card);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addMerchantPayment() {
        try {
            lyBPayClose = findViewById(R.id.lyBPayClose);
            tvBBankError = findViewById(R.id.tvBBankError);
            tvDCardError = findViewById(R.id.tvDCardError);
            tvSignetError = findViewById(R.id.tvSignetError);
            lyAddBank = findViewById(R.id.lyAddBank);
            lyExternalClose = findViewById(R.id.lyExternalClose);
            tvBankHead = findViewById(R.id.tvBankHead);
            tvBankCount = findViewById(R.id.tvBankCount);
            tvBankMsg = findViewById(R.id.tvBankMsg);
            imgBBankArrow = findViewById(R.id.imgBBankArrow);
            imgBBankIcon = findViewById(R.id.imgBBankIcon);
            imgDCardLogo = findViewById(R.id.imgDCardLogo);
            tvDCHead = findViewById(R.id.tvDCHead);
            tvDCardHead = findViewById(R.id.tvDCardHead);
            tvDCardMsg = findViewById(R.id.tvDCardMsg);
            imgDCardArrow = findViewById(R.id.imgDCardArrow);
            imgSignetLogo = findViewById(R.id.imgSignetLogo);
            imgSignetArrow = findViewById(R.id.imgSignetArrow);
            tvSignetHead = findViewById(R.id.tvSignetHead);
            tvSignetCount = findViewById(R.id.tvSignetCount);
            tvSignetMsg = findViewById(R.id.tvSignetMsg);
            layoutDCard = findViewById(R.id.layoutDCard);
            layoutSignet = findViewById(R.id.layoutSignet);
            cvNext = findViewById(R.id.cvNext);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            tvMessage = findViewById(R.id.tvMessage);
            imgLogo = findViewById(R.id.imgLogo);
            lyBPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strCurrent.equals("addpay") || strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit")) {
                        ControlMethod("paymentMethods");
                        strCurrent = "paymentMethods";
                    } else {
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

            layoutDCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (paymentMethodsResponse.getData().getDebitCardCount() < paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                            strCurrent = "debit";
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddCardActivity.class);
                            i.putExtra("card", "debit");
                            startActivityForResult(i, 3);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutSignet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (paymentMethodsResponse.getData().getCreditCardCount() < paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                            strCurrent = "credit";
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddCardActivity.class);
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
                    ControlMethod("addbpayment");
                    strCurrent = "addbpayment";
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
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, WithdrawPaymentMethodsActivity.this, "", "");
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
                        Utils.populateLearnMore(WithdrawPaymentMethodsActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            numberOfMerchantAccounts();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void numberOfMerchantAccounts() {
        try {
            if (paymentMethodsResponse.getData() != null) {
                tvBankCount.setText("(" + paymentMethodsResponse.getData().getBankCount() + "/" + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + ")");
                tvDCardHead.setText("(" + paymentMethodsResponse.getData().getDebitCardCount() + "/" + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + ")");
                tvSignetCount.setText("(" + paymentMethodsResponse.getData().getSignetCount() + "/" + paymentMethodsResponse.getData().getMaxSignetAccountsAllowed() + ")");

                tvBBankError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + " banks");
                tvDCardError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + " cards");
                tvSignetError.setText("This method has reached maximum " + paymentMethodsResponse.getData().getMaxCreditCardsAllowed() + " cards");
            }
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                if (paymentMethodsResponse.getData().getBankCount() >= paymentMethodsResponse.getData().getMaxBankAccountsAllowed()) {
                    tvBBankError.setVisibility(View.VISIBLE);
                    tvBankHead.setTextColor(getColor(R.color.light_gray));
                    tvBankCount.setTextColor(getColor(R.color.light_gray));
                    tvBankMsg.setTextColor(getColor(R.color.light_gray));
                    imgBBankArrow.setColorFilter(getColor(R.color.light_gray));
                    imgBBankIcon.setImageResource(R.drawable.ic_bank_account_inactive);
                } else {
                    tvBBankError.setVisibility(View.GONE);
                    tvBankHead.setTextColor(getColor(R.color.primary_black));
                    tvBankCount.setTextColor(getColor(R.color.dark_grey));
                    tvBankMsg.setTextColor(getColor(R.color.dark_grey));
//                    imgBBankArrow.clearColorFilter();
                    imgBBankArrow.setColorFilter(getColor(R.color.primary_black));
                    imgBBankIcon.setImageResource(R.drawable.ic_bank_account_active);
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
//                    imgDCardArrow.clearColorFilter();
                    imgDCardArrow.setColorFilter(getColor(R.color.primary_black));
                    imgDCardLogo.setImageResource(R.drawable.ic_credit_debit_card);
                }
                if (paymentMethodsResponse.getData().getCreditCardCount() >= paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                    tvSignetError.setVisibility(View.VISIBLE);
                    tvSignetHead.setTextColor(getColor(R.color.light_gray));
                    tvSignetCount.setTextColor(getColor(R.color.light_gray));
                    tvSignetMsg.setTextColor(getColor(R.color.light_gray));
                    imgSignetArrow.setColorFilter(getColor(R.color.light_gray));
                    imgSignetLogo.setImageResource(R.drawable.ic_credit_debit_card_inactive);
                } else {
                    tvSignetError.setVisibility(View.GONE);
                    tvSignetHead.setTextColor(getColor(R.color.primary_black));
                    tvSignetCount.setTextColor(getColor(R.color.dark_grey));
                    tvSignetMsg.setTextColor(getColor(R.color.dark_grey));
//                    imgSignetArrow.clearColorFilter();
                    imgSignetArrow.setColorFilter(getColor(R.color.primary_black));
                    imgSignetLogo.setImageResource(R.drawable.ic_credit_debit_card);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "withdrawmethod": {
                    findViewById(R.id.withdrawmethod).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                }
                break;
                case "withdrawnotoken": {
                    findViewById(R.id.withdrawnotoken).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                }
                break;
                case "addpayment": {
                    findViewById(R.id.addpayment).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                }
                break;
                case "addbpayment": {
                    findViewById(R.id.addbpayment).setVisibility(View.VISIBLE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                }
                break;
                case "withdrawpay": {
                    findViewById(R.id.withdrawpay).setVisibility(VISIBLE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                }
                break;
                case "externalBank": {
                    findViewById(R.id.externalBank).setVisibility(VISIBLE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                }
                break;
                case "firstError": {
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.VISIBLE);
                }
                break;
                case "banksuccess": {
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.addbpayment).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectWithdrawMethod() {
        try {
            LinearLayout lySelBack = findViewById(R.id.lySelBack);
            RelativeLayout lyExternal = findViewById(R.id.lyExternal);
            RelativeLayout lyInstantPay = findViewById(R.id.lyInstantPay);
            RelativeLayout lyGiftCard = findViewById(R.id.lyGiftCard);
            RelativeLayout lySignet = findViewById(R.id.lySignet);
            View viewSignet = findViewById(R.id.viewSignet);

            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                lySignet.setVisibility(VISIBLE);
                viewSignet.setVisibility(VISIBLE);
            } else {
                lySignet.setVisibility(View.GONE);
                viewSignet.setVisibility(View.GONE);
            }

            lySelBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    onBackPressed();
                    //Added on 29-03-2022 - VT
                    finish();
                }
            });

            lyExternal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (bankList != null && bankList.size() > 0) {
                            selectPayMethod(bankList);
                        } else {
                            ControlMethod("withdrawpay");
                            withdrawPaymentMethod("bank");
                            strScreen = "withdrawpay";
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lyInstantPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (cardList != null && cardList.size() > 0) {
                            selectPayMethod(cardList);
                        } else {
                            ControlMethod("withdrawpay");
                            withdrawPaymentMethod("card");
                            strScreen = "withdrawpay";
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lyGiftCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(WithdrawPaymentMethodsActivity.this, GiftCardActivity.class));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lySignet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (signetList != null && signetList.size() > 0) {
                            selectPayMethod(signetList);
                        } else {
//                            strCurrent = "signet";
//                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddPaymentSignetActivity.class);
//                            startActivityForResult(i, 4);
                            showAddPayment("signet");
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

    private void withdrawPaymentMethod(String strPay) {
        try {
            ImageView imgPayLogo = findViewById(R.id.imgPayLogo);
            ImageView imgPayment = findViewById(R.id.imgPayment);
            TextView tvPayHead = findViewById(R.id.tvPayHead);
            TextView tvPayMethod = findViewById(R.id.tvPayMethod);
            TextView tvPayMMessage = findViewById(R.id.tvPayMMessage);
            TextView tvCount = findViewById(R.id.tvCount);
            LinearLayout lyWPayClose = findViewById(R.id.lyWPayClose);
            LinearLayout lyPayClick = findViewById(R.id.lyPayClick);
            if (strPay.equals("bank")) {
                tvPayHead.setText("Add Bank Account");
                tvPayMethod.setText("External Bank Account");
                tvCount.setText("(0/2)");
                tvPayMMessage.setText("Can be used for making coyni purchases or withdrawing funds.");
                imgPayLogo.setImageResource(R.drawable.ic_add_bank);
                imgPayment.setImageResource(R.drawable.ic_bank_account_active);
            } else {
                tvPayHead.setText("Add Instant Pay");
                tvPayMethod.setText("Debit Card");
                tvCount.setText("(0/4)");
                tvPayMMessage.setText("Visa or Mastercard debit cards");
                imgPayLogo.setImageResource(R.drawable.ic_notokenavail);
                imgPayment.setImageResource(R.drawable.ic_credit_debit_card);
            }
            lyWPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ControlMethod("withdrawmethod");
                    selectWithdrawMethod();
                    strScreen = "withdrawmethod";
                }
            });
            lyPayClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (strPay.equals("bank")) {
                            ControlMethod("externalBank");
                            strCurrent = "externalBank";
                            strOnPauseScreen = "externalBank";
                            LinearLayout lyExternalClose = findViewById(R.id.lyExternalClose);
                            TextView tvLearnMore = findViewById(R.id.tvLearnMore);
                            cvNext = findViewById(R.id.cvNext);
                            cvNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                        isBank = true;
                                        Intent i = new Intent(WithdrawPaymentMethodsActivity.this, WebViewActivity.class);
                                        i.putExtra("signon", signOnData);
                                        startActivityForResult(i, 1);
                                    } else {
                                        Utils.displayAlert(strSignOn, WithdrawPaymentMethodsActivity.this, "", "");
                                    }
                                }
                            });
                            lyExternalClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    strCurrent = "";
                                    ControlMethod("withdrawpay");
                                    withdrawPaymentMethod("bank");
                                    strScreen = "withdrawpay";
                                }
                            });
                            tvLearnMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    Utils.populateLearnMore(WithdrawPaymentMethodsActivity.this);
                                }
                            });
                        } else {
                            strCurrent = "debit";
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddCardActivity.class);
                            i.putExtra("card", "debit");
                            startActivityForResult(i, 3);
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

    private void getPaymentMethods() {
        try {
            isPayments = true;
            dialog = Utils.showProgressDialog(this);
            dashboardViewModel.mePaymentMethods();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindWithdrawNoTokens() {
        try {
            LinearLayout lyNTClose = findViewById(R.id.lyNTClose);
            LinearLayout lyAddPay = findViewById(R.id.lyAddPay);
            ExpandableHeightRecyclerView rvPayMethods = findViewById(R.id.rvPayMethods);
            List<PaymentsList> listData = new ArrayList<>();
            if (paymentMethodsResponse != null) {
                listData = paymentMethodsResponse.getData().getData();
                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                    List<PaymentsList> filterData = new ArrayList<>();
                    for (int i = 0; i < listData.size(); i++) {
                        if (listData.get(i).getPaymentMethod().toLowerCase().equals("bank")) {
                            filterData.add(listData.get(i));
                        }
                    }
                    listData = new ArrayList<>();
                    listData = filterData;
                }
                if (listData != null && listData.size() > 0) {
                    rvPayMethods.setVisibility(VISIBLE);
                    SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(listData, WithdrawPaymentMethodsActivity.this, "withdraw");
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(WithdrawPaymentMethodsActivity.this);
                    rvPayMethods.setLayoutManager(mLayoutManager);
                    rvPayMethods.setNestedScrollingEnabled(false);
                    rvPayMethods.setExpanded(true);
                    rvPayMethods.setItemAnimator(new DefaultItemAnimator());
                    rvPayMethods.setAdapter(selectedPaymentMethodsAdapter);
                } else {
                    rvPayMethods.setVisibility(View.GONE);
                }
            }

            lyAddPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, BuyTokenPaymentMethodsActivity.class);
                            i.putExtra("screen", "withdraw");
                            startActivity(i);
                        } else {
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, SelectPaymentMethodActivity.class);
                            i.putExtra("screen", "withdraw");
                            startActivity(i);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            lyNTClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectPayMethod(List<PaymentsList> listPayments) {
        try {
            payDialog = new Dialog(WithdrawPaymentMethodsActivity.this);
            payDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            payDialog.setContentView(R.layout.choosepaymentmethod);
            payDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            ExpandableHeightRecyclerView rvSelPayMethods = payDialog.findViewById(R.id.rvSelPayMethods);
            LinearLayout lyAddPay = payDialog.findViewById(R.id.lyAddPay);
            TextView tvHead = payDialog.findViewById(R.id.tvHead);
            tvHead.setText("Withdraw Method");
            SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
            if (listPayments != null && listPayments.size() > 0) {
                selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(listPayments, WithdrawPaymentMethodsActivity.this, "withdrawtoken");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(WithdrawPaymentMethodsActivity.this);
                rvSelPayMethods.setLayoutManager(mLayoutManager);
                rvSelPayMethods.setNestedScrollingEnabled(false);
                rvSelPayMethods.setExpanded(true);
                rvSelPayMethods.setItemAnimator(new DefaultItemAnimator());
                rvSelPayMethods.setAdapter(selectedPaymentMethodsAdapter);
            }

            Window window = payDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            payDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            payDialog.setCanceledOnTouchOutside(true);
            payDialog.show();
            lyAddPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        payDialog.dismiss();
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            ControlMethod("addpayment");
                            addPayment();
                            strCurrent = "addpayment";
                        } else {
                            ControlMethod("addbpayment");
                            addMerchantPayment();
                            strCurrent = "addbpayment";
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

    public void expiry() {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            final Dialog dialog = new Dialog(WithdrawPaymentMethodsActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.payment_expire);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;
            PaymentsList objPayment = objMyApplication.getSelectedCard();
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (!objPayment.getPaymentMethod().toLowerCase().equals("bank")) {
                            Intent i = new Intent(WithdrawPaymentMethodsActivity.this, EditCardActivity.class);
                            startActivity(i);
                        } else {
                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                isBank = true;
                                objMyApplication.setResolveUrl(true);
                                Intent i = new Intent(WithdrawPaymentMethodsActivity.this, WebViewActivity.class);
                                i.putExtra("signon", signOnData);
                                startActivityForResult(i, 1);
                            } else {
                                Utils.displayAlert(strSignOn, WithdrawPaymentMethodsActivity.this, "", "");
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

    public void deleteBank(PaymentsList objPayment) {
        try {
            final Dialog dialog = new Dialog(WithdrawPaymentMethodsActivity.this);
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
                    pDialog = Utils.showProgressDialog(WithdrawPaymentMethodsActivity.this);
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

    public void bindSelectedCard(String strscreen) {
        try {
            if (strscreen.equals("withdrawtoken")) {
                closePaymentMethods();
                Intent i = new Intent(WithdrawPaymentMethodsActivity.this, WithdrawTokenActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(WithdrawPaymentMethodsActivity.this, BuyTokenActivity.class);
                i.putExtra("notoken", strscreen);
                startActivity(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindSelectedBank(String strscreen) {
        try {
            if (strscreen.equals("withdrawtoken")) {
                closePaymentMethods();
                Intent i = new Intent(WithdrawPaymentMethodsActivity.this, WithdrawTokenActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(WithdrawPaymentMethodsActivity.this, BuyTokenActivity.class);
                startActivity(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closePaymentMethods() {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPayments(List<PaymentsList> allPayments) {
        try {
            if (allPayments != null && allPayments.size() > 0) {
                bankList = new ArrayList<>();
                cardList = new ArrayList<>();
                signetList = new ArrayList<>();
                for (int i = 0; i < allPayments.size(); i++) {
                    if (allPayments.get(i).getPaymentMethod() != null) {
                        switch (allPayments.get(i).getPaymentMethod().toLowerCase()) {
                            case "bank":
                                bankList.add(allPayments.get(i));
                                break;
                            case "debit":
                                cardList.add(allPayments.get(i));
                                break;
                            case "signet":
                                signetList.add(allPayments.get(i));
                                break;
                        }
                    }
                }
            } else {
                isPayments = false;
            }
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

            //Added 29-03-2022 - VT
            strCurrent = strScreen = "banksuccess";
            //Added 29-03-2022 - VT

            cvDone = findViewById(R.id.cvDone);
            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBankSuccess = false;
//                    //Added 29-03-2022 - VT
//                    strCurrent = "externalBank";
//                    strScreen = "withdrawpay";
//                    //Added 29-03-2022 - VT
                    strCurrent = "";
                    if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                        ControlMethod("withdrawmethod");
                        selectWithdrawMethod();
                        strScreen = "withdrawmethod";
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showAddPayment(String strType) {
        try {
            addPayDialog = new Dialog(WithdrawPaymentMethodsActivity.this);
            addPayDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            addPayDialog.setContentView(R.layout.withdraw_pay_method);
            addPayDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvHead = addPayDialog.findViewById(R.id.tvPayHead);
            TextView tvPayHead = addPayDialog.findViewById(R.id.tvPayMethod);
            TextView tvCnt = addPayDialog.findViewById(R.id.tvCount);
            TextView tvMessage = addPayDialog.findViewById(R.id.tvPayMMessage);
            LinearLayout lyClose = addPayDialog.findViewById(R.id.lyWPayClose);
            LinearLayout layoutPayClick = addPayDialog.findViewById(R.id.lyPayClick);
            ImageView imgLogo = addPayDialog.findViewById(R.id.imgPayLogo);
            ImageView imgPaymnt = addPayDialog.findViewById(R.id.imgPayment);
            if (strType.equals("signet")) {
                tvHead.setText("Add Signet Account");
                tvPayHead.setText("Signet Account");
                tvCnt.setVisibility(View.GONE);
                tvMessage.setText("Can be used for making coyni purchases or withdrawing funds.");
                imgLogo.setImageResource(R.drawable.ic_add_signet);
                imgPaymnt.setImageResource(R.drawable.ic_signetactive);
            }
            Window window = addPayDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            addPayDialog.show();
            lyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPayDialog.dismiss();
                }
            });

            layoutPayClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Intent i = new Intent(WithdrawPaymentMethodsActivity.this, AddPaymentSignetActivity.class);
                        startActivityForResult(i, 4);
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
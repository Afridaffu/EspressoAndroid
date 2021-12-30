package com.greenbox.coyni.view;

import static android.view.View.VISIBLE;

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
import android.content.Intent;
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

import com.google.android.material.textfield.TextInputEditText;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.SelectedPaymentMethodsAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.cards.CardDeleteResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
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
    WalletResponse walletResponse;
    Double walletBalance = 0.0;
    Long mLastClickTime = 0L;
    Boolean isBank = false, isPayments = false, isDeCredit = false;
    List<PaymentsList> bankList;
    List<PaymentsList> cardList;
    Dialog payDialog;
    String strSignOn = "", strCurrent = "", strScreen = "";
    SignOnData signOnData;
    ProgressDialog dialog, pDialog;
    LinearLayout lyAPayClose, lyExternalClose;
    RelativeLayout layoutDCard, lyExternal, layoutCCard;
    TextView tvBankError, tvDCardError, tvCCardError, tvExtBankHead, tvExtBankMsg, tvDCardHead, tvDCardMsg, tvCCardHead, tvCCardMsg;
    TextView tvLearnMore, tvExtBHead, tvDCHead, tvCCHead, tvMessage;
    ImageView imgBankArrow, imgBankIcon, imgDCardLogo, imgDCardArrow, imgCCardLogo, imgCCardArrow, imgLogo;
    CardView cvNext;
    public static WithdrawPaymentMethodsActivity withdrawPaymentMethodsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
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
                    ControlMethod("withdrawmethod");
                    selectWithdrawMethod();
                    strScreen = "withdrawmethod";
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
        if (strCurrent.equals("externalBank")) {
            strCurrent = "";
            ControlMethod("withdrawpay");
            withdrawPaymentMethod("bank");
            strScreen = "withdrawpay";
        } else if (strScreen.equals("withdrawpay")) {
            ControlMethod("withdrawmethod");
            selectWithdrawMethod();
            strScreen = "withdrawmethod";
        } else {
            super.onBackPressed();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            walletResponse = objMyApplication.getWalletResponse();
            walletBalance = objMyApplication.getGBTBalance();
            if (paymentMethodsResponse != null) {
//                List<PaymentsList> allPayments = paymentMethodsResponse.getData().getData();
//                if (allPayments != null && allPayments.size() > 0) {
//                    bankList = new ArrayList<>();
//                    cardList = new ArrayList<>();
//                    for (int i = 0; i < allPayments.size(); i++) {
//                        if (allPayments.get(i).getPaymentMethod() != null) {
//                            if (allPayments.get(i).getPaymentMethod().toLowerCase().equals("bank")) {
//                                bankList.add(allPayments.get(i));
//                            } else if (allPayments.get(i).getPaymentMethod().toLowerCase().equals("debit")) {
//                                cardList.add(allPayments.get(i));
//                            }
//                        }
//                    }
//                }
                getPayments(paymentMethodsResponse.getData().getData());
            }
            if (walletBalance != 0.0) {
                ControlMethod("withdrawmethod");
                selectWithdrawMethod();
            } else {
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
                                callResolveFlow();
                            }
                        } else {
                            objMyApplication.setSignOnData(null);
                            signOnData = null;
                            objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
                            strSignOn = signOn.getError().getErrorDescription();
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
                    getPayments(payMethodsResponse.getData().getData());
                    if (isDeCredit) {
                        isDeCredit = false;
                        ControlMethod("addpayment");
                        strCurrent = "addpayment";
                        numberOfAccounts();
                    } else if (isPayments && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                        isPayments = false;
                        ControlMethod("paymentMethods");
                        strCurrent = "paymentMethods";
//                        paymentMethods();
                    } else if (isPayments && strCurrent.equals("debit")) {
                        ControlMethod("withdrawpay");
                        withdrawPaymentMethod("card");
                        strScreen = "withdrawpay";
                    }

//                    else if (isPayments) {
//                        isPayments = false;
//                        isDeCredit = false;
//                        ControlMethod("addpayment");
//                        strCurrent = "addpayment";
//                        numberOfAccounts();
//                    }
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
            lyExternal = findViewById(R.id.lyExternal);
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

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "withdrawmethod": {
                    findViewById(R.id.withdrawmethod).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                }
                break;
                case "withdrawnotoken": {
                    findViewById(R.id.withdrawnotoken).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);

                }
                break;
                case "addpayment": {
                    findViewById(R.id.addpayment).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                }
                break;
                case "withdrawpay": {
                    findViewById(R.id.withdrawpay).setVisibility(VISIBLE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                }
                break;
                case "externalBank": {
                    findViewById(R.id.externalBank).setVisibility(VISIBLE);
                    findViewById(R.id.withdrawpay).setVisibility(View.GONE);
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.withdrawmethod).setVisibility(View.GONE);
                    findViewById(R.id.withdrawnotoken).setVisibility(View.GONE);
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
            lySelBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
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
            LinearLayout lyWPayClose = findViewById(R.id.lyWPayClose);
            LinearLayout lyPayClick = findViewById(R.id.lyPayClick);
            if (strPay.equals("bank")) {
                tvPayHead.setText("Add Bank Account");
                tvPayMethod.setText("External Bank Account");
                tvPayMMessage.setText("Can be used for making Coyni purchases or withdrawing funds.");
                imgPayLogo.setImageResource(R.drawable.ic_add_bank);
                imgPayment.setImageResource(R.drawable.ic_bank_account_active);
            } else {
                tvPayHead.setText("Add Instant Pay");
                tvPayMethod.setText("Debit Card");
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
                            LinearLayout lyExternalClose = findViewById(R.id.lyExternalClose);
                            TextView tvLearnMore = findViewById(R.id.tvLearnMore);
                            cvNext = findViewById(R.id.cvNext);
                            cvNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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
            RecyclerView rvPayMethods = findViewById(R.id.rvPayMethods);
            List<PaymentsList> listData = new ArrayList<>();
            if (paymentMethodsResponse != null) {
                listData = paymentMethodsResponse.getData().getData();
                if (listData != null && listData.size() > 0) {
                    rvPayMethods.setVisibility(VISIBLE);
                    SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(listData, WithdrawPaymentMethodsActivity.this, "withdraw");
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(WithdrawPaymentMethodsActivity.this);
                    rvPayMethods.setLayoutManager(mLayoutManager);
                    rvPayMethods.setItemAnimator(new DefaultItemAnimator());
                    rvPayMethods.setAdapter(selectedPaymentMethodsAdapter);
                } else {
                    rvPayMethods.setVisibility(View.GONE);
                }
            }

            lyAddPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(WithdrawPaymentMethodsActivity.this, BuyTokenPaymentMethodsActivity.class);
                    i.putExtra("screen", "withdraw");
                    startActivity(i);
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

    private void callResolveFlow() {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
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

    private void selectPayMethod(List<PaymentsList> listPayments) {
        try {
            payDialog = new Dialog(WithdrawPaymentMethodsActivity.this);
            payDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            payDialog.setContentView(R.layout.choosepaymentmethod);
            payDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            RecyclerView rvSelPayMethods = payDialog.findViewById(R.id.rvSelPayMethods);
            LinearLayout lyAddPay = payDialog.findViewById(R.id.lyAddPay);
            TextView tvHead = payDialog.findViewById(R.id.tvHead);
            tvHead.setText("Withdraw Method");
            SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
            if (listPayments != null && listPayments.size() > 0) {
                selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(listPayments, WithdrawPaymentMethodsActivity.this, "withdrawtoken");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(WithdrawPaymentMethodsActivity.this);
                rvSelPayMethods.setLayoutManager(mLayoutManager);
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
                        ControlMethod("addpayment");
                        addPayment();
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
                for (int i = 0; i < allPayments.size(); i++) {
                    if (allPayments.get(i).getPaymentMethod() != null) {
                        if (allPayments.get(i).getPaymentMethod().toLowerCase().equals("bank")) {
                            bankList.add(allPayments.get(i));
                        } else if (allPayments.get(i).getPaymentMethod().toLowerCase().equals("debit")) {
                            cardList.add(allPayments.get(i));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
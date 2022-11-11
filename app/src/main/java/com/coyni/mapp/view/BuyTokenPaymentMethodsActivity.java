package com.coyni.mapp.view;

import static android.view.View.VISIBLE;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.mapp.adapters.SelectedPaymentMethodsAdapter;
import com.coyni.mapp.view.business.AddManualBankAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.mapp.R;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.bank.BankDeleteResponseData;
import com.coyni.mapp.model.bank.SignOnData;
import com.coyni.mapp.model.cards.CardDeleteResponse;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.paymentmethods.PaymentsList;
import com.coyni.mapp.utils.CheckOutConstants;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.keyboards.CustomKeyboard;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.PaymentMethodsViewModel;

import java.util.List;

public class BuyTokenPaymentMethodsActivity extends BaseActivity {
    MyApplication objMyApplication;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomerProfileViewModel customerProfileViewModel;
    DashboardViewModel dashboardViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    LinearLayout lyAPayClose, lyExternalClose, lySelBack, lyAddPay;
    RelativeLayout layoutDCard, lyExternal, layoutCCard;
    String strCurrent = "", strSignOn = "", strScreen = "", strOnPauseScreen = "";
    SignOnData signOnData;
    TextView tvBankError, tvDCardError, tvCCardError, tvExtBankHead, tvExtBankMsg, tvDCardHead, tvDCardMsg, tvCCardHead, tvCCardMsg;
    TextView tvErrorMessage, tvLearnMore, tvExtBHead, tvDCHead, tvCCHead, tvErrorHead, tvMessage;
    ImageView imgBankArrow, imgBankIcon, imgDCardLogo, imgDCardArrow, imgCCardLogo, imgCCardArrow, imgLogo;
    CardView cvNext, cvTryAgain, cvDone;
    Boolean isBank = false, isPayments = false, isDeCredit = false, isBankSuccess = false, isPaymentAdded = false;
    TextInputEditText etCVV;
    RecyclerView rvSelPayMethods;
    public static BuyTokenPaymentMethodsActivity buyTokenPaymentMethodsActivity;
    Long mLastClickTime = 0L;
    Dialog cvvDialog, extBankDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_buy_token_payment_methods);
            buyTokenPaymentMethodsActivity = this;
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isBankSuccess) {
            if (objMyApplication.getStrScreen() == null || objMyApplication.getStrScreen().equals("")) {
//                if (!strScreen.equals("withdraw") && !strScreen.equals("buytoken") && (strCurrent.equals("addpay") || strCurrent.equals("debit") || strCurrent.equals("credit") || strCurrent.equals("addpayment"))) {
//                    ControlMethod("paymentMethods");
//                    strCurrent = "paymentMethods";
//                } else if (strCurrent.equals("externalBank")) {
//                    ControlMethod("addpayment");
//                    strCurrent = "addpayment";
//                } else if (!strCurrent.equals("firstError")) {
//                    super.onBackPressed();
//                }
                if (strCurrent.equals("externalBank")) {
                    ControlMethod("addpayment");
                    strCurrent = "addpayment";
                } else if (strCurrent.equals("paymentMethods")) {
                    //Why is empty , so i have added on backpressed
                    super.onBackPressed();
                } else if (!strScreen.equals("dashboard") && !strScreen.equals("withdraw") && !strScreen.equals("buytoken") && (strCurrent.equals("addpay") || strCurrent.equals("debit") || strCurrent.equals("credit") || strCurrent.equals("addpayment"))) {
                    ControlMethod("paymentMethods");
                    strCurrent = "paymentMethods";
                } else if ((!strCurrent.equals("firstError")) || (strScreen.equals("dashboard") && strCurrent.equals("addpayment"))) {
                    super.onBackPressed();
                }
            } else {
                if (!strScreen.equals("payRequest") && (strCurrent.equals("debit") || strCurrent.equals("credit"))) {
                    ControlMethod("addpayment");
                    strCurrent = "addpayment";
                } else {
                    objMyApplication.setStrScreen("");
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (strOnPauseScreen.equals("externalBank")) {
                ControlMethod("externalBank");
                strCurrent = "externalBank";
                strOnPauseScreen = "";
            } else if (strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit") || strScreen.equals("withdraw") || strScreen.equals("buytoken")) {
                ControlMethod("addpayment");
            } else if (strScreen != null && strScreen.equals("banksuccess")) {
                //Added on 29-03-2022 - VT
            } else if (strScreen != null && !strScreen.equals("addpay")) {
                if (!isPayments) {
                    getPaymentMethods();
                }
            } else if (strScreen != null && strScreen.equals("addpay")) { //Added for Buy token navigation change while adding payment method
                paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
                ControlMethod("paymentMethods");
                strCurrent = "paymentMethods";
                paymentMethods();
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
                    Utils.displayAlert("Bank integration has been cancelled", BuyTokenPaymentMethodsActivity.this, "", "");
                } else {
                    if (extBankDialog != null) {
                        extBankDialog.dismiss();
                    }
                }
            } else if (requestCode == 3) {
                if (objMyApplication.getStrScreen() == null || objMyApplication.getStrScreen().equals("")) {
//                    if (strScreen.equals("withdraw") || strScreen.equals("buytoken")) {
//                        onBackPressed();
//                    } else
                    if (strCurrent.equals("externalBank") || strCurrent.equals("debit") || strCurrent.equals("credit")) {
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
                } else {
                    onBackPressed();
                }
            } else if (requestCode == 4) {
//                if (strScreen.equals("withdraw") || strScreen.equals("buytoken") || strScreen.equals("payRequest")) {
//                    onBackPressed();
//                } else
                if (!objMyApplication.getBankSave()) {
                    isDeCredit = true;
                    ControlMethod("addpayment");
                } else {
                    objMyApplication.setBankSave(false);
                    ControlMethod("paymentMethods");
                    strCurrent = "paymentMethods";
                }
                getPaymentMethods();
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
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
//            if (Utils.checkInternet(BuyTokenPaymentMethodsActivity.this)) {
//                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
//                    customerProfileViewModel.meSignOn();
//                } else {
//                    strSignOn = objMyApplication.getStrSignOnError();
//                    signOnData = objMyApplication.getSignOnData();
//                }
//            } else {
//                Utils.displayAlert(getString(R.string.internet), BuyTokenPaymentMethodsActivity.this, "", "");
//            }

            if (getIntent().getStringExtra("screen") != null) {
                strScreen = getIntent().getStringExtra("screen");
            }
            if (strScreen != null && !strScreen.equals("addpay") && !strScreen.equals("withdraw") && !strScreen.equals("buytoken") && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                ControlMethod("paymentMethods");
                strCurrent = "paymentMethods";
            } else {
                ControlMethod("addpayment");
                if (strScreen != null && !strScreen.equals("addpay")) {
                    strCurrent = "addpayment";
                }
            }
            addPayment();
            paymentMethods();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
//        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
//            @Override
//            public void onChanged(SignOn signOn) {
//                try {
////                    if (dialog != null) {
////                        dialog.dismiss();
////                    }
//                    dismissDialog();
//                    if (signOn != null) {
//                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
//                            objMyApplication.setSignOnData(signOn.getData());
//                            signOnData = signOn.getData();
//                            objMyApplication.setStrSignOnError("");
//                            strSignOn = "";
//                            if (objMyApplication.getResolveUrl()) {
//                                objMyApplication.callResolveFlow(BuyTokenPaymentMethodsActivity.this, strSignOn, signOnData);
//                            }
//                        } else {
//                            if (signOn.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
//                                Log.e("setResolveUrl", "228 setResolveUrl");
//                                objMyApplication.setResolveUrl(true);
//                                //customerProfileViewModel.meSignOn();
//                            } else {
//                                objMyApplication.setSignOnData(null);
//                                signOnData = null;
//                                objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
//                                strSignOn = signOn.getError().getErrorDescription();
//                            }
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });

        customerProfileViewModel.getApiErrorMutableLiveData().observe(BuyTokenPaymentMethodsActivity.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
//                    dialog.dismiss();
                    dismissDialog();
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            Log.e("setResolveUrl", "252 setResolveUrl");
                            objMyApplication.setResolveUrl(true);
                            //customerProfileViewModel.meSignOn();
                        } else if (!isBank) {
                            if (!apiError.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BuyTokenPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else {
                            isBank = false;
                            if (apiError.getError().getErrorCode().equals(getString(R.string.bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("this payment method has already")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenPaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
                            } else if (apiError.getError().getErrorCode().equals(getString(R.string.no_bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("no bank accounts found")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenPaymentMethodsActivity.this, "Error", apiError.getError().getFieldErrors().get(0));
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

//        customerProfileViewModel.getSyncAccountMutableLiveData().observe(BuyTokenPaymentMethodsActivity.this, new Observer<SyncAccount>() {
//            @Override
//            public void onChanged(SyncAccount syncAccount) {
//                try {
////                    dialog.dismiss();
//                    dismissDialog();
//                    if (syncAccount != null) {
//                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
//
//                            if (objMyApplication.getSelectedCard() != null) {
//                                //Added on 30-03-2022 - VT
//                                if (objMyApplication.getSelectedCard().getPaymentMethod().toLowerCase().equals("bank")) {
//                                    objMyApplication.getSelectedCard().setRelink(false);
//                                } else {
//                                    objMyApplication.getSelectedCard().setExpired(false);
//                                }
//                            }
//                            //Added on 30-03-2022 - VT
//                            dashboardViewModel.mePaymentMethods();
//                            displaySuccess();
//                        } else {
////                        if (Arrays.asList(bob).contains("silly")) {
////                            // true
////                        }
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });

        dashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse payMethodsResponse) {
                dismissDialog();
                if (payMethodsResponse != null) {
                    PaymentMethodsResponse objResponse;
                    if (strScreen.equalsIgnoreCase(CheckOutConstants.ScreenCheckOut)) {
                        objResponse = objMyApplication.filterCheckPaymentMethods(payMethodsResponse);
                    } else {
                        objResponse = objMyApplication.filterPaymentMethods(payMethodsResponse);
                    }
                    objMyApplication.setPaymentMethodsResponse(objResponse);
                    paymentMethodsResponse = objResponse;
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
                        isDeCredit = false;
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
                try {
//                pDialog.dismiss();
                    dismissDialog();
                    if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {
                        Utils.showCustomToast(BuyTokenPaymentMethodsActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                        getPaymentMethods();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getCardDeleteResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                try {
//                pDialog.dismiss();
                    dismissDialog();
                    if (cardDeleteResponse.getStatus().toLowerCase().equals("success")) {
                        Utils.showCustomToast(BuyTokenPaymentMethodsActivity.this, "Card has been removed.", R.drawable.ic_custom_tick, "");
                        getPaymentMethods();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
//                    pDialog.dismiss();
                    dismissDialog();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BuyTokenPaymentMethodsActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void callResolveFlow() {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, WebViewActivity.class);
                i.putExtra("signon", signOnData);
                startActivityForResult(i, 1);
            } else {
                Utils.displayAlert(strSignOn, BuyTokenPaymentMethodsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPaymentMethods() {
        try {
            isPayments = true;
//            dialog = Utils.showProgressDialog(this);
            showProgressDialog();
            dashboardViewModel.mePaymentMethods();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            if (strScreen != null && (strScreen.equals("dashboard") || strScreen.equals("payRequest"))) {
                imgLogo.setVisibility(View.VISIBLE);
                imgLogo.setImageResource(R.drawable.ic_addpayment_method2);
                tvMessage.setVisibility(VISIBLE);
                tvExtBHead.setText("External Bank Account");
                tvMessage.setText("There is no payment method currently linked to your account. Please follow one of the prompts below to link an account.");
            } else {
                imgLogo.setVisibility(View.GONE);
                tvExtBHead.setText("Bank Account");
                tvMessage.setText("Choose a payment method");
                tvMessage.setVisibility(VISIBLE);
            }
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
                        if (objMyApplication.getFeatureControlGlobal().getPayBank() != null && objMyApplication.getFeatureControlByUser() != null
                                && objMyApplication.getFeatureControlGlobal().getPayBank() && objMyApplication.getFeatureControlByUser().getPayBank()) {
                            if (paymentMethodsResponse.getData().getBankCount() < paymentMethodsResponse.getData().getMaxBankAccountsAllowed()) {
                                //showExternalBank();
                                Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, AddManualBankAccount.class);
                                i.putExtra("From", "pay");
                                i.putExtra("screen", strScreen);
                                startActivityForResult(i, 4);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.errormsg), BuyTokenPaymentMethodsActivity.this, "", "");
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
                        if (objMyApplication.getFeatureControlGlobal().getPayDebit() != null && objMyApplication.getFeatureControlByUser() != null
                                && objMyApplication.getFeatureControlGlobal().getPayDebit() && objMyApplication.getFeatureControlByUser().getPayDebit()) {
                            if (paymentMethodsResponse.getData().getDebitCardCount() < paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                                strCurrent = "debit";
                                Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, AddCardActivity.class);
                                i.putExtra("card", "debit");
                                i.putExtra("screen", strScreen);
                                startActivityForResult(i, 3);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.errormsg), BuyTokenPaymentMethodsActivity.this, "", "");
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
                        if (objMyApplication.getFeatureControlGlobal().getPayCredit() != null && objMyApplication.getFeatureControlByUser() != null
                                && objMyApplication.getFeatureControlGlobal().getPayCredit() && objMyApplication.getFeatureControlByUser().getPayCredit()) {
                            if (paymentMethodsResponse.getData().getCreditCardCount() < paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                                strCurrent = "credit";
                                Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, AddCardActivity.class);
                                i.putExtra("card", "credit");
                                i.putExtra("screen", strScreen);
                                startActivityForResult(i, 3);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.errormsg), BuyTokenPaymentMethodsActivity.this, "", "");
                        }
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
//            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
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
//                    imgBankArrow.clearColorFilter();
                imgBankArrow.setColorFilter(getColor(R.color.primary_black));
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
                imgCCardLogo.setImageResource(R.drawable.ic_credit_debit_card_inactive);
            } else {
                tvCCardError.setVisibility(View.GONE);
                tvCCHead.setTextColor(getColor(R.color.primary_black));
                tvCCardHead.setTextColor(getColor(R.color.dark_grey));
                tvCCardMsg.setTextColor(getColor(R.color.dark_grey));
//                    imgCCardArrow.clearColorFilter();
                imgCCardArrow.setColorFilter(getColor(R.color.primary_black));
                imgCCardLogo.setImageResource(R.drawable.ic_credit_debit_card);

            }
//            }
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

    private void paymentMethods() {
        try {
            rvSelPayMethods = findViewById(R.id.rvSelPayMethods);
            lySelBack = findViewById(R.id.lySelBack);
            lyAddPay = findViewById(R.id.lyAddPay);
            numberOfAccounts();
            if (paymentMethodsResponse.getData() != null && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                bindPaymentMethods(paymentMethodsResponse.getData().getData());
            }
            lySelBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strCurrent.equals("paymentMethods")) {
                        onBackPressed();
                    }
                }
            });
            lyAddPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberOfAccounts();
                    ControlMethod("addpayment");
                    if (!strScreen.equals(CheckOutConstants.ScreenCheckOut)) {
                        strScreen = "addpay";
                    }
                    strCurrent = "addpay";
                    addPayment();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindPaymentMethods(List<PaymentsList> listPayments) {
        SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
        try {
            if (listPayments != null && listPayments.size() > 0) {
                selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(listPayments, BuyTokenPaymentMethodsActivity.this, "selectpay");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(BuyTokenPaymentMethodsActivity.this);
                rvSelPayMethods.setLayoutManager(mLayoutManager);
                rvSelPayMethods.setItemAnimator(new DefaultItemAnimator());
                rvSelPayMethods.setAdapter(selectedPaymentMethodsAdapter);
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
//                    ControlMethod("externalBank");
//                    strCurrent = "externalBank";
                    showExternalBank();
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
                    //Added 29-03-2022 - VT
                    strCurrent = "externalBank";
                    strScreen = "withdraw";
                    //Added 29-03-2022 - VT
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

    public void displayCVV() {
        try {
            cvvDialog = new Dialog(BuyTokenPaymentMethodsActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.cvvlayout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            etCVV = (TextInputEditText) cvvDialog.findViewById(R.id.etCVV);
            CustomKeyboard ctKey;
            ctKey = cvvDialog.findViewById(R.id.ckb);
            ctKey.setKeyAction("OK", this);
            ctKey.setScreenName("cvv");
            InputConnection ic = etCVV.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            etCVV.setShowSoftInputOnFocus(false);
            etCVV.requestFocus();

            etCVV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(BuyTokenPaymentMethodsActivity.this, v);
                }
            });
            etCVV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Utils.hideSoftKeypad(BuyTokenPaymentMethodsActivity.this, view);
                }
            });
            etCVV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 2) {
                        ctKey.enableButton();
                    } else {
                        ctKey.disableButton();
                    }
                }
            });

            Window window = cvvDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            cvvDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            cvvDialog.setCanceledOnTouchOutside(true);
            cvvDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void expiry() {
        try {
            final Dialog dialog = new Dialog(BuyTokenPaymentMethodsActivity.this);
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
                    //customerProfileViewModel.meSignOn();
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
                            Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, EditCardActivity.class);
                            startActivity(i);
                        } else {
                            openBankFiserv();
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

    public void openBankFiserv() {
        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
            isBank = true;
            Log.e("setResolveUrl", "862 setResolveUrl");
            //objMyApplication.setResolveUrl(true);
            Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, WebViewActivity.class);
            i.putExtra("signon", signOnData);
            startActivityForResult(i, 1);
        } else {
            Utils.displayAlert(strSignOn, BuyTokenPaymentMethodsActivity.this, "", "");
        }
    }

    public void deleteBank(PaymentsList objPayment) {
        try {
            final Dialog dialog = new Dialog(BuyTokenPaymentMethodsActivity.this);
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
//                    pDialog = Utils.showProgressDialog(BuyTokenPaymentMethodsActivity.this);
                    showProgressDialog();
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

    public void okClick() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            etCVV = (TextInputEditText) cvvDialog.findViewById(R.id.etCVV);
            if (!etCVV.getText().toString().trim().equals("")) {
                cvvDialog.dismiss();
                Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, BuyTokenActivity.class);
                i.putExtra("cvv", etCVV.getText().toString().trim());
                startActivity(i);
            }
//            else {
//                Utils.displayAlert("Please enter CVV", BuyTokenPaymentMethodsActivity.this, "", "");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindSelectedBank() {
        try {
            Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, BuyTokenActivity.class);
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showExternalBank() {
        try {
            extBankDialog = new Dialog(BuyTokenPaymentMethodsActivity.this);
            extBankDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            extBankDialog.setContentView(R.layout.activity_add_external_bank_acc);
            extBankDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView tvLearn = extBankDialog.findViewById(R.id.tvLearnMore);
            CardView cNext = extBankDialog.findViewById(R.id.cvNext);
            LinearLayout lyClose = extBankDialog.findViewById(R.id.lyExternalClose);
            tvLearn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    try {
                        Utils.populateLearnMore(BuyTokenPaymentMethodsActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


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
//                            ControlMethod("addpayment");
//                            strCurrent = "addpayment";

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
                            Intent i = new Intent(BuyTokenPaymentMethodsActivity.this, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, BuyTokenPaymentMethodsActivity.this, "", "");
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
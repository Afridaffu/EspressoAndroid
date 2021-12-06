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
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import org.json.JSONObject;

import java.util.List;

public class PaymentMethodsActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    PaymentMethodsResponse paymentMethodsResponse;
    LinearLayout lyAPayClose, lyExternalClose, lyPayBack;
    CardView cvNext, cvAddPayment;
    TextView tvBankError, tvDCardError, tvCCardError, tvExtBankHead, tvExtBankMsg, tvDCardHead, tvDCardMsg, tvCCardHead, tvCCardMsg;
    TextView tvErrorMessage, tvLearnMore, tvExtBHead, tvDCHead, tvCCHead, tvErrorHead;
    String strCurrent = "", strSignOn = "";
    ImageView imgBankArrow, imgBankIcon, imgDCardLogo, imgDCardArrow, imgCCardLogo, imgCCardArrow;
    RecyclerView rvPaymentMethods;
    CustomerProfileViewModel customerProfileViewModel;
    DashboardViewModel dashboardViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    ProgressDialog dialog, pDialog;
    SignOnData signOnData;
    Boolean isBank = false, isPayments = false;
    RelativeLayout layoutDCard, lyExternal;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", PaymentMethodsActivity.this, "");
                } else {
                    dialog = Utils.showProgressDialog(this);
                    customerProfileViewModel.meSyncAccount();
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
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            if (Utils.checkInternet(PaymentMethodsActivity.this)) {
                dialog = Utils.showProgressDialog(this);
                customerProfileViewModel.meSignOn();
            } else {
                Utils.displayAlert(getString(R.string.internet), PaymentMethodsActivity.this, "");
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    dialog.dismiss();
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
                                Utils.displayAlert(apiError.getError().getErrorDescription(), PaymentMethodsActivity.this, "");
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), PaymentMethodsActivity.this, "");
                            }
                        } else {
                            isBank = false;
                            if (apiError.getError().getErrorCode().equals(getString(R.string.bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("this payment method has already")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), PaymentMethodsActivity.this, "Error");
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
                            //Utils.displayAlert("You added a new Bank Account to your profile!", PaymentMethodsActivity.this, "");
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
                    if (isPayments && paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
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
                            Utils.displayAlert(apiError.getError().getErrorDescription(), PaymentMethodsActivity.this, "");
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), PaymentMethodsActivity.this, "");
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
            cvNext = findViewById(R.id.cvNext);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            lyAPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strCurrent.equals("addpay")) {
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
                        Intent i = new Intent(PaymentMethodsActivity.this, AddCardActivity.class);
                        startActivity(i);
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
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, PaymentMethodsActivity.this, "");
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
//                        FiservLearnMore_BottomSheet learnMore_bottomSheet = FiservLearnMore_BottomSheet.newInstance();
//                        learnMore_bottomSheet.show(getSupportFragmentManager(), learnMore_bottomSheet.getTag());
                        Utils.populateLearnMore(PaymentMethodsActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            numberOfAccounts();
//            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
//                if (paymentMethodsResponse.getData().getBankCount() >= paymentMethodsResponse.getData().getMaxBankAccountsAllowed()) {
//                    tvBankError.setVisibility(View.VISIBLE);
//                    tvExtBHead.setTextColor(getColor(R.color.light_gray));
//                    tvExtBankHead.setTextColor(getColor(R.color.light_gray));
//                    tvExtBankMsg.setTextColor(getColor(R.color.light_gray));
//                    imgBankArrow.setColorFilter(getColor(R.color.light_gray));
//                    imgBankIcon.setColorFilter(getColor(R.color.light_gray));
//                } else {
//                    tvBankError.setVisibility(View.GONE);
//                }
//                if (paymentMethodsResponse.getData().getDebitCardCount() >= paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
//                    tvDCardError.setVisibility(View.VISIBLE);
//                    tvDCHead.setTextColor(getColor(R.color.light_gray));
//                    tvDCardHead.setTextColor(getColor(R.color.light_gray));
//                    tvDCardMsg.setTextColor(getColor(R.color.light_gray));
//                    imgDCardArrow.setColorFilter(getColor(R.color.light_gray));
//                    imgDCardLogo.setColorFilter(getColor(R.color.light_gray));
//                } else {
//                    tvDCardError.setVisibility(View.GONE);
//                }
//                if (paymentMethodsResponse.getData().getCreditCardCount() >= paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
//                    tvCCardError.setVisibility(View.VISIBLE);
//                    tvCCHead.setTextColor(getColor(R.color.light_gray));
//                    tvCCardHead.setTextColor(getColor(R.color.light_gray));
//                    tvCCardMsg.setTextColor(getColor(R.color.light_gray));
//                    imgCCardArrow.setColorFilter(getColor(R.color.light_gray));
//                    imgCCardLogo.setColorFilter(getColor(R.color.light_gray));
//                } else {
//                    tvCCardError.setVisibility(View.GONE);
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void paymentMethods() {
        try {
            rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
            lyPayBack = findViewById(R.id.lyPayBack);
            cvAddPayment = findViewById(R.id.cvAddPayment);
//            if (paymentMethodsResponse.getData() != null) {
//                tvExtBankHead.setText("(" + paymentMethodsResponse.getData().getBankCount() + "/" + paymentMethodsResponse.getData().getMaxBankAccountsAllowed() + ")");
//                tvDCardHead.setText("(" + paymentMethodsResponse.getData().getDebitCardCount() + "/" + paymentMethodsResponse.getData().getMaxDebitCardsAllowed() + ")");
//                tvCCardHead.setText("(" + paymentMethodsResponse.getData().getCreditCardCount() + "/" + paymentMethodsResponse.getData().getMaxCreditCardsAllowed() + ")");
//                bindPaymentMethods(paymentMethodsResponse.getData().getData());
//            }
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
                Utils.displayAlert(strSignOn, PaymentMethodsActivity.this, "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareCards() {
        try {
            JSONObject object = new JSONObject();
            object.put("addressLine1", "address 1");
            object.put("addressLine2", "address 2");
            object.put("cardNumber", "4587726823648736");
            object.put("city", "city");
            object.put("country", "country");
            object.put("cvc", "123");
            object.put("defaultForAllWithDrawals", true);
            object.put("expiryDate", "11/25");
            object.put("name", "Krishna");
            object.put("state", "state");
            object.put("zipCode", "5467362");
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
                    imgBankIcon.setColorFilter(getColor(R.color.light_gray));
                } else {
                    tvBankError.setVisibility(View.GONE);
                    tvExtBHead.setTextColor(getColor(R.color.primary_black));
                    tvExtBankHead.setTextColor(getColor(R.color.dark_grey));
                    tvExtBankMsg.setTextColor(getColor(R.color.dark_grey));
                    imgBankArrow.clearColorFilter();
                    imgBankIcon.clearColorFilter();
                }
                if (paymentMethodsResponse.getData().getDebitCardCount() >= paymentMethodsResponse.getData().getMaxDebitCardsAllowed()) {
                    tvDCardError.setVisibility(View.VISIBLE);
                    tvDCHead.setTextColor(getColor(R.color.light_gray));
                    tvDCardHead.setTextColor(getColor(R.color.light_gray));
                    tvDCardMsg.setTextColor(getColor(R.color.light_gray));
                    imgDCardArrow.setColorFilter(getColor(R.color.light_gray));
                    imgDCardLogo.setColorFilter(getColor(R.color.light_gray));
                } else {
                    tvDCardError.setVisibility(View.GONE);
                    tvDCHead.setTextColor(getColor(R.color.primary_black));
                    tvDCardHead.setTextColor(getColor(R.color.dark_grey));
                    tvDCardMsg.setTextColor(getColor(R.color.dark_grey));
                    imgDCardArrow.clearColorFilter();
                    imgDCardLogo.clearColorFilter();
                }
                if (paymentMethodsResponse.getData().getCreditCardCount() >= paymentMethodsResponse.getData().getMaxCreditCardsAllowed()) {
                    tvCCardError.setVisibility(View.VISIBLE);
                    tvCCHead.setTextColor(getColor(R.color.light_gray));
                    tvCCardHead.setTextColor(getColor(R.color.light_gray));
                    tvCCardMsg.setTextColor(getColor(R.color.light_gray));
                    imgCCardArrow.setColorFilter(getColor(R.color.light_gray));
                    imgCCardLogo.setColorFilter(getColor(R.color.light_gray));
                } else {
                    tvCCardError.setVisibility(View.GONE);
                    tvCCHead.setTextColor(getColor(R.color.primary_black));
                    tvCCardHead.setTextColor(getColor(R.color.dark_grey));
                    tvCCardMsg.setTextColor(getColor(R.color.dark_grey));
                    imgCCardArrow.clearColorFilter();
                    imgCCardLogo.clearColorFilter();
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
            dialog.setContentView(R.layout.activity_removing_btm_sheet);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvBankName = dialog.findViewById(R.id.tvBankName);
            TextView tvAccount = dialog.findViewById(R.id.tvAccount);
            TextView tvNo = dialog.findViewById(R.id.tvNo);
            TextView tvYes = dialog.findViewById(R.id.tvYes);
            if (objPayment != null) {
                tvBankName.setText(objPayment.getBankName());
                if (objPayment.getAccountNumber() != null && objPayment.getAccountNumber().length() > 4) {
                    tvAccount.setText("**** " + objPayment.getAccountNumber().substring(objPayment.getAccountNumber().length() - 4));
                } else {
                    tvAccount.setText(objPayment.getAccountNumber());
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
                    paymentMethodsViewModel.deleteBanks(String.valueOf(objPayment.getId()));
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

}
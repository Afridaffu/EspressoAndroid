package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.SelectedPaymentMethodsAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.buytoken.BuyTokenRequest;
import com.greenbox.coyni.model.buytoken.BuyTokenResponse;
import com.greenbox.coyni.model.buytoken.BuyTokenResponseData;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.model.transactionlimit.LimitResponseData;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;


public class BuyTokenActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    PaymentsList selectedCard, objSelected, prevSelectedCard;
    ImageView imgBankIcon, imgArrow, imgConvert;
    TextView tvLimit, tvPayHead, tvAccNumber, tvCurrency, tvBankName, tvBAccNumber, tvError, tvCYN;
    RelativeLayout lyPayMethod, lyMainLayout;
    LinearLayout lyCDetails, lyBuyClose, lyBDetails;
    EditText etAmount;
    CustomKeyboard ctKey;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomerProfileViewModel customerProfileViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    DashboardViewModel dashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    Dialog payDialog, prevDialog, cvvDialog;
    TransactionLimitResponse objResponse;
    ProgressDialog pDialog;
    String strLimit = "", strType = "", strBankId = "", strCardId = "", strCvv = "", strSubType = "", strSignOn = "";
    Double maxValue = 0.0, dget = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, usdValidation = 0.0, cynValidation = 0.0;
    SignOnData signOnData;
    float fontSize, dollarFont;
    Boolean isUSD = false, isCYN = false, isBank = false;
    public static BuyTokenActivity buyTokenActivity;
    TextInputEditText etCVV;
    Long mLastClickTime = 0L;
    boolean isBuyTokenAPICalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_buy_token);
            buyTokenActivity = this;
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        dashboardViewModel.mePaymentMethods();
        super.onResume();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == etAmount.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    etAmount.setHint("");
                    if (tvCYN.getVisibility() == View.VISIBLE) {
                        isCYN = true;
                        isUSD = false;
                        convertCYNValue();
                    } else {
                        isCYN = false;
                        isUSD = true;
                        convertUSDValue();
                    }
                    if (editable.length() > 8) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() > 5) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else {
                        etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
                        tvCurrency.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, dollarFont));
                    }

                    if (validation()) {
                        ctKey.enableButton();
                    } else {
                        ctKey.disableButton();
                    }
                } else if (editable.toString().equals(".")) {
                    etAmount.setText("");
                    ctKey.disableButton();
                } else if (editable.length() == 0) {
                    etAmount.setHint("0.00");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    usdValidation = 0.0;
                    ctKey.disableButton();
                    tvError.setVisibility(View.INVISIBLE);
                    ctKey.clearData();
                    setDefaultLength();
                } else {
                    etAmount.setText("");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    usdValidation = 0.0;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", BuyTokenActivity.this, "", "");
                } else {
                    pDialog = Utils.showProgressDialog(this);
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
            selectedCard = objMyApplication.getSelectedCard();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            imgBankIcon = findViewById(R.id.imgBankIcon);
            imgArrow = findViewById(R.id.imgArrow);
            imgConvert = findViewById(R.id.imgConvert);
            tvLimit = findViewById(R.id.tvLimit);
            tvPayHead = findViewById(R.id.tvPayHead);
            tvBankName = findViewById(R.id.tvBankName);
            tvAccNumber = findViewById(R.id.tvAccNumber);
            tvBAccNumber = findViewById(R.id.tvBAccNumber);
            tvError = findViewById(R.id.tvError);
            tvCYN = findViewById(R.id.tvCYN);
            tvCurrency = findViewById(R.id.tvCurrency);
            lyPayMethod = findViewById(R.id.lyPayMethod);
            lyMainLayout = findViewById(R.id.lyMainLayout);
            lyBuyClose = findViewById(R.id.lyBuyClose);
            etAmount = findViewById(R.id.etAmount);
            lyCDetails = findViewById(R.id.lyCDetails);
            lyBDetails = findViewById(R.id.lyBDetails);
            ctKey = (CustomKeyboard) findViewById(R.id.ckb);
            ctKey.setKeyAction("Buy\nToken",this);
            ctKey.setScreenName("buy");
            InputConnection ic = etAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            fontSize = etAmount.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            etAmount.requestFocus();
            etAmount.setShowSoftInputOnFocus(false);
//            etAmount.setEnabled(false);
            if (getIntent().getStringExtra("cvv") != null && !getIntent().getStringExtra("cvv").equals("")) {
                strCvv = getIntent().getStringExtra("cvv");
            }
            bindPayMethod(selectedCard);
            etAmount.addTextChangedListener(this);
            etAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(BuyTokenActivity.this, v);
                }
            });

            etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        Utils.hideSoftKeypad(BuyTokenActivity.this, view);
                        if (!b) {
                            if (!etAmount.getText().toString().equals("")) {
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                                etAmount.setFilters(FilterArray);
                                USFormat(etAmount);
                            }
                        } else {
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                            etAmount.setFilters(FilterArray);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lyMainLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    try {
                        if (etAmount.hasFocus()) {
                            etAmount.clearFocus();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            });

            lyPayMethod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        selectPayMethod();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lyBuyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            imgConvert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (etAmount.getText().toString().trim().length() > 0 && Double.parseDouble(etAmount.getText().toString().replace(",", "")) != 0) {
                            if (tvCYN.getVisibility() == View.GONE) {
                                tvCYN.setVisibility(View.VISIBLE);
                                tvCurrency.setVisibility(View.INVISIBLE);
                                etAmount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                                convertUSDtoCYN();
                                if (tvError.getVisibility() == View.VISIBLE) {
                                    if (tvError.getText().toString().trim().contains("Minimum Amount")) {
                                        tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                                    } else {
                                        if (strLimit.equals("daily")) {
                                            tvError.setText("Amount entered exceeds your daily limit");
                                        } else if (strLimit.equals("week")) {
                                            tvError.setText("Amount entered exceeds your weekly limit");
                                        }
                                    }
                                }
                            } else {
                                tvCYN.setVisibility(View.GONE);
                                tvCurrency.setVisibility(View.VISIBLE);
                                convertCYNtoUSD();
                                etAmount.setGravity(Gravity.CENTER_VERTICAL);
                                if (tvError.getVisibility() == View.VISIBLE) {
                                    if (tvError.getText().toString().trim().contains("Minimum Amount")) {
                                        tvError.setText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD");
                                    } else {
                                        if (strLimit.equals("daily")) {
                                            tvError.setText("Amount entered exceeds your daily limit");
                                        } else if (strLimit.equals("week")) {
                                            tvError.setText("Amount entered exceeds your weekly limit");
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!etAmount.getText().toString().equals("")) {
                                etAmount.setText("0.00");
                                ctKey.setText("0.00");
                                etAmount.setSelection(etAmount.getText().length());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            calculateFee("10");
            strSignOn = objMyApplication.getStrSignOnError();
            signOnData = objMyApplication.getSignOnData();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    try {
                        objResponse = transactionLimitResponse;
                        setDailyWeekLimit(transactionLimitResponse.getData());
                        if (etAmount.getText().toString().trim().length() > 0) {
                            if (validation()) {
                                ctKey.enableButton();
                            } else {
                                ctKey.disableButton();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        buyTokenViewModel.getTransferFeeResponseMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
                if (transferFeeResponse != null) {
                    objMyApplication.setTransferFeeResponse(transferFeeResponse);
                    feeInAmount = transferFeeResponse.getData().getFeeInAmount();
                    feeInPercentage = transferFeeResponse.getData().getFeeInPercentage();
                    if (!etAmount.getText().toString().equals("") && !etAmount.getText().toString().equals("0")) {
                        Double pay = Double.parseDouble(etAmount.getText().toString().replace(",", ""));
                        pfee = transferFeeResponse.getData().getFee();
                        dget = pay - pfee;
                        buyTokenPreview();
                    }
                }
            }
        });

        buyTokenViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BuyTokenActivity.this, "", "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getBuyTokResponseMutableLiveData().observe(this, new Observer<BuyTokenResponse>() {
            @Override
            public void onChanged(BuyTokenResponse buyTokenResponse) {
                if (prevDialog != null) {
                    prevDialog.dismiss();
                }
                if (buyTokenResponse != null) {
                    if (buyTokenResponse.getStatus().trim().toLowerCase().equals("success")) {
                        buyTokenInProgress(buyTokenResponse.getData());
                    }
                }
            }
        });

        buyTokenViewModel.getBuyTokenFailureMutableLiveData().observe(this, new Observer<BuyTokenResponse>() {
            @Override
            public void onChanged(BuyTokenResponse buyTokenResponse) {
                if (buyTokenResponse != null) {
                    buyTokenFailure(buyTokenResponse);
                }
            }
        });

        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    if (signOn != null) {
                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                            objMyApplication.setSignOnData(signOn.getData());
                            signOnData = signOn.getData();
                            objMyApplication.setStrSignOnError("");
                            strSignOn = "";
                            if (objMyApplication.getResolveUrl()) {
                                objMyApplication.callResolveFlow(BuyTokenActivity.this, strSignOn, signOnData);
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

        customerProfileViewModel.getSyncAccountMutableLiveData().observe(this, new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                try {
                    pDialog.dismiss();
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
                if (payMethodsResponse != null) {
//                    objMyApplication.setPaymentMethodsResponse(payMethodsResponse);
//                    paymentMethodsResponse = payMethodsResponse;
                    PaymentMethodsResponse objResponse = objMyApplication.filterPaymentMethods(payMethodsResponse);
                    objMyApplication.setPaymentMethodsResponse(objResponse);
                    paymentMethodsResponse = objResponse;
                    if (objMyApplication.getSelectedCard() != null) {
                        selectedCard = objMyApplication.getSelectedCard();
                        bindPayMethod(selectedCard);
                    }
                }
            }
        });
    }

    public void bindPayMethod(PaymentsList objData) {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            selectedCard = objData;
            TransactionLimitRequest obj = new TransactionLimitRequest();
            obj.setTransactionType(Integer.parseInt(Utils.addType));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                strType = "bank";
                strBankId = String.valueOf(objData.getId());
                strSubType = Utils.bankType;
                obj.setTransactionSubType(Integer.parseInt(Utils.bankType));
                lyBDetails.setVisibility(View.VISIBLE);
                lyCDetails.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, lyBDetails.getId());
                imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                if (objData.getBankName().length() > 15) {
                    tvBankName.setText(objData.getBankName().substring(0, 15) + "...");
                } else {
                    tvBankName.setText(objData.getBankName());
                }
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    tvBAccNumber.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    tvBAccNumber.setText(objData.getAccountNumber());
                }
            } else {
                strCardId = String.valueOf(objData.getId());
                if (objData.getCardType().toLowerCase().equals("debit")) {
                    strType = "debit";
                    strSubType = Utils.debitType;
                    obj.setTransactionSubType(Integer.parseInt(Utils.debitType));
                } else if (objData.getCardType().toLowerCase().equals("credit")) {
                    strType = "credit";
                    strSubType = Utils.creditType;
                    obj.setTransactionSubType(Integer.parseInt(Utils.creditType));
                }
                params.addRule(RelativeLayout.BELOW, lyCDetails.getId());
                lyBDetails.setVisibility(View.GONE);
                lyCDetails.setVisibility(View.VISIBLE);
                tvAccNumber.setText("****" + objData.getLastFour());
                switch (objData.getCardBrand().toUpperCase().replace(" ", "")) {
                    case "VISA":
                        tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                        imgBankIcon.setImageResource(R.drawable.ic_visaactive);
                        break;
                    case "MASTERCARD":
                        tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                        imgBankIcon.setImageResource(R.drawable.ic_masteractive);
                        break;
                    case "AMERICANEXPRESS":
                        tvPayHead.setText("American Express Card");
                        imgBankIcon.setImageResource(R.drawable.ic_amexactive);
                        break;
                    case "DISCOVER":
                        tvPayHead.setText("Discover Card");
                        imgBankIcon.setImageResource(R.drawable.ic_discoveractive);
                        break;
                }
            }
            params.addRule(RelativeLayout.RIGHT_OF, imgBankIcon.getId());
            params.addRule(RelativeLayout.LEFT_OF, imgArrow.getId());
            params.setMargins(Utils.convertPxtoDP(15), Utils.convertPxtoDP(5), 0, 0);
            tvLimit.setLayoutParams(params);
            if (Utils.checkInternet(BuyTokenActivity.this)) {
                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
            } else {
                Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectPayMethod() {
        try {
            payDialog = new Dialog(BuyTokenActivity.this);
            payDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            payDialog.setContentView(R.layout.choosepaymentmethod);
            payDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            RecyclerView rvSelPayMethods = payDialog.findViewById(R.id.rvSelPayMethods);
            LinearLayout lyAddPay = payDialog.findViewById(R.id.lyAddPay);
            SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
            prevSelectedCard = objMyApplication.getSelectedCard();
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(paymentMethodsResponse.getData().getData(), BuyTokenActivity.this, "buytoken");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(BuyTokenActivity.this);
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
                        Intent i = new Intent(BuyTokenActivity.this, BuyTokenPaymentMethodsActivity.class);
                        i.putExtra("screen", "buytoken");
                        startActivityForResult(i, 3);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
            if (objLimit.getTokenLimitFlag()) {
                tvLimit.setVisibility(View.VISIBLE);
                Double week = 0.0, daily = 0.0;
                String strCurrency = "", strAmount = "";
                if (objLimit.getWeeklyAccountLimit() != null && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("NA") && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("unlimited")) {
                    week = Double.parseDouble(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Double.parseDouble(objLimit.getDailyAccountLimit());
                }
                strCurrency = " " + getString(R.string.currency);
                if ((week == 0 || week < 0) && daily > 0) {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if ((daily == 0 || daily < 0) && week > 0) {
                    strLimit = "week";
                    maxValue = week;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(week));
                    tvLimit.setText("Your weekly limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    tvLimit.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + strCurrency);
                    strLimit = "unlimited";
                } else {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                }
            } else {
                tvLimit.setVisibility(View.GONE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateFee(String strAmount) {
        try {
            TransferFeeRequest request = new TransferFeeRequest();
            request.setTokens(strAmount.trim().replace(",", ""));
            request.setTxnType(Utils.addType);
            if (strType.toLowerCase().equals("debit")) {
                request.setTxnSubType(Utils.debitType);
            } else if (strType.toLowerCase().equals("credit")) {
                request.setTxnSubType(Utils.creditType);
            } else if (strType.toLowerCase().equals("bank")) {
                request.setTxnSubType(Utils.bankType);
            } else if (strType.toLowerCase().equals("instant")) {
                request.setTxnSubType(Utils.instantType);
            }
            if (Utils.checkInternet(BuyTokenActivity.this)) {
                buyTokenViewModel.transferFee(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            cynValidation = Double.parseDouble(objResponse.getData().getMinimumLimit());
            String strPay = "";
            if (tvCYN.getVisibility() == View.VISIBLE) {
                strPay = String.valueOf(cynValue);
            } else {
                strPay = String.valueOf(usdValue);
//                usdValidation = (cynValidation + (cynValidation * (feeInPercentage / 100))) + feeInAmount;
            }
            usdValidation = (cynValidation + (cynValidation * (feeInPercentage / 100))) + feeInAmount;
//            if ((Double.parseDouble(strPay.replace(",", "")) < cynValidation) || Double.parseDouble(strPay.replace(",", "")) < usdValidation) {
//                if (tvCYN.getVisibility() == View.VISIBLE) {
//                    tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
//                } else {
//                    tvError.setText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD");
//                }
//                tvError.setVisibility(View.VISIBLE);
//                return value = false;
//            }
            if (tvCYN.getVisibility() == View.VISIBLE && Double.parseDouble(strPay.replace(",", "")) < cynValidation) {
                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                tvError.setVisibility(View.VISIBLE);
                return value = false;
            } else if (tvCYN.getVisibility() == View.GONE && Double.parseDouble(strPay.replace(",", "")) < usdValidation) {
                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD");
                tvError.setVisibility(View.VISIBLE);
                return value = false;
            } else if (objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(strPay.replace(",", "")) > maxValue) {
                if (strLimit.equals("daily")) {
                    tvError.setText("Amount entered exceeds your daily limit");
                } else if (strLimit.equals("week")) {
                    tvError.setText("Amount entered exceeds your weekly limit");
                }
                tvError.setVisibility(View.VISIBLE);
                return value = false;
            } else {
                tvError.setVisibility(View.INVISIBLE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public void buyTokenPreview() {
        try {
            prevDialog = new Dialog(BuyTokenActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.buy_token_order_review);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;
            isBuyTokenAPICalled = false;
            TextView tvGet = prevDialog.findViewById(R.id.tvGet);
            TextView tvBankName = prevDialog.findViewById(R.id.tvBankName);
            TextView tvAccount = prevDialog.findViewById(R.id.tvAccount);
            TextView tvPayMethod = prevDialog.findViewById(R.id.tvPayMethod);
            TextView tvPurchaseAmt = prevDialog.findViewById(R.id.tvPurchaseAmt);
            TextView tvProcessingFee = prevDialog.findViewById(R.id.tvProcessingFee);
            TextView tvTotal = prevDialog.findViewById(R.id.tvTotal);
            LinearLayout layoutBank = prevDialog.findViewById(R.id.layoutBank);
            LinearLayout layoutCard = prevDialog.findViewById(R.id.layoutCard);
            ImageView imgCardType = prevDialog.findViewById(R.id.imgCardType);
            MotionLayout slideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);
            TextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);

            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(pfee));
            tvGet.setText(Utils.USNumberFormat(cynValue));
            tvPurchaseAmt.setText(Utils.USNumberFormat(cynValue) + " USD");
            tvProcessingFee.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " USD");
            total = cynValue + Double.parseDouble(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " USD");
            if (selectedCard.getPaymentMethod().toLowerCase().equals("bank")) {
                layoutBank.setVisibility(View.VISIBLE);
                layoutCard.setVisibility(View.GONE);
                tvBankName.setText(selectedCard.getBankName());
                if (selectedCard.getAccountNumber() != null && selectedCard.getAccountNumber().length() > 4) {
                    tvAccount.setText("**** " + selectedCard.getAccountNumber().substring(selectedCard.getAccountNumber().length() - 4));
                } else {
                    tvAccount.setText(selectedCard.getAccountNumber());
                }
            } else {
                layoutBank.setVisibility(View.GONE);
                layoutCard.setVisibility(View.VISIBLE);
                tvPayMethod.setText("****" + selectedCard.getLastFour());
                switch (selectedCard.getCardBrand().toUpperCase().replace(" ", "")) {
                    case "VISA":
                        imgCardType.setImageResource(R.drawable.ic_visa1);
                        break;
                    case "MASTERCARD":
                        imgCardType.setImageResource(R.drawable.ic_master);
                        break;
                    case "AMERICANEXPRESS":
                        imgCardType.setImageResource(R.drawable.ic_amex);
                        break;
                    case "DISCOVER":
                        imgCardType.setImageResource(R.drawable.ic_discover);
                        break;
                }
            }

            slideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                    if (progress > Utils.slidePercentage) {
                        im_lock_.setAlpha(1.0f);
                        motionLayout.setTransition(R.id.middle, R.id.end);
                        motionLayout.transitionToState(motionLayout.getEndState());
                        slideToConfirm.setInteractionEnabled(false);
                        tv_lable.setText("Verifying");
                        if (!isBuyTokenAPICalled)
                            buyToken();
                    }
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
//                    if (currentId == motionLayout.getEndState()) {
//                        slideToConfirm.setInteractionEnabled(false);
//                        tv_lable.setText("Verifying");
//                        buyToken();
//                    }
                }

                @Override
                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

                }
            });

            Window window = prevDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            prevDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            prevDialog.setCanceledOnTouchOutside(true);
            prevDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(BuyTokenActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(BuyTokenActivity.this);
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void convertUSDValue() {
        try {
            if (isUSD) {
                isUSD = false;
                usdValue = Double.parseDouble(etAmount.getText().toString().trim().replace(",", ""));
                cynValue = ((usdValue - feeInAmount) * 100) / (100 + feeInPercentage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertCYNValue() {
        try {
            if (isCYN) {
                isCYN = false;
                cynValue = Double.parseDouble(etAmount.getText().toString().trim().replace(",", ""));
                usdValue = (cynValue + (cynValue * (feeInPercentage / 100))) + feeInAmount;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertUSDtoCYN() {
        try {
            convertUSDValue();
            if (cynValue != 0.0 || usdValue != 0.0) {
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                etAmount.setFilters(FilterArray);
                etAmount.removeTextChangedListener(BuyTokenActivity.this);
//                etAmount.setText(String.valueOf(cynValue));
                etAmount.setText(Utils.convertBigDecimalUSDC(String.valueOf(cynValue)));
                etAmount.addTextChangedListener(BuyTokenActivity.this);
                USFormat(etAmount);
                etAmount.setSelection(etAmount.getText().length());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertCYNtoUSD() {
        try {
            convertCYNValue();
            if (usdValue != 0.0) {
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                etAmount.setFilters(FilterArray);
                etAmount.removeTextChangedListener(BuyTokenActivity.this);
//                etAmount.setText(String.valueOf(usdValue));
                etAmount.setText(Utils.convertBigDecimalUSDC(String.valueOf(usdValue)));
                etAmount.addTextChangedListener(BuyTokenActivity.this);
                USFormat(etAmount);
                etAmount.setSelection(etAmount.getText().length());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buyTokenClick() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (tvCYN.getVisibility() == View.VISIBLE) {
                convertUSDtoCYN();
            } else {
                convertCYNtoUSD();
            }
            calculateFee(Utils.USNumberFormat(cynValue));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyToken() {
        try {
            isBuyTokenAPICalled = true;
            BuyTokenRequest request = new BuyTokenRequest();
            request.setBankId(strBankId);
            request.setCardId(strCardId);
            request.setCvc(strCvv);
            request.setTokens(Utils.convertBigDecimalUSDC(String.valueOf(total)));
            request.setTxnSubType(strSubType);
            if (Utils.checkInternet(BuyTokenActivity.this)) {
                buyTokenViewModel.buyTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyTokenInProgress(BuyTokenResponseData objData) {
        try {
            prevDialog = new Dialog(BuyTokenActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.buy_token_transaction_in_progress);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvAmount = prevDialog.findViewById(R.id.tvAmount);
            TextView tvMessage = prevDialog.findViewById(R.id.tvMessage);
            TextView tvReferenceID = prevDialog.findViewById(R.id.tvReferenceID);
            TextView tvBalance = prevDialog.findViewById(R.id.tvBalance);
            TextView tvLearnMore = prevDialog.findViewById(R.id.tvLearnMore);
            TextView tvHeading = prevDialog.findViewById(R.id.tvHeading);
            LinearLayout layoutReference = prevDialog.findViewById(R.id.layoutReference);
            ImageView imgLogo = prevDialog.findViewById(R.id.imgLogo);
            CardView cvDone = prevDialog.findViewById(R.id.cvDone);
            if (objData.getGbxTransactionId().length() > 10) {
                tvReferenceID.setText(objData.getGbxTransactionId().substring(0, 10) + "...");
            } else {
                tvReferenceID.setText(objData.getGbxTransactionId());
            }
            if (objData.getType().toLowerCase().contains("bank")) {
                tvHeading.setText("Transaction Pending");
                imgLogo.setImageResource(R.drawable.ic_hourglass_pending_icon);
            } else {
                tvHeading.setText("Transaction In Progress");
                imgLogo.setImageResource(R.drawable.ic_in_progress_icon);
            }
            Double bal = cynValue + objMyApplication.getGBTBalance();
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
            tvAmount.setText(Utils.USNumberFormat(cynValue));
            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as " + objData.getDescriptorName() + ".");
            Window window = prevDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            prevDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            prevDialog.setCancelable(false);
            prevDialog.show();

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(BuyTokenActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            layoutReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getGbxTransactionId(), BuyTokenActivity.this);
                }
            });
            tvLearnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateLearnMore(BuyTokenActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void displayCVV(PaymentsList objData) {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            objSelected = objData;
            cvvDialog = new Dialog(BuyTokenActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.cvvlayout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView cvvErrorTV = cvvDialog.findViewById(R.id.cvvErrorTV);
            etCVV = cvvDialog.findViewById(R.id.etCVV);
            TextInputLayout etlCVV = cvvDialog.findViewById(R.id.etlCVV);
            LinearLayout cvvErrorLL = cvvDialog.findViewById(R.id.cvvErrorLL);
            CustomKeyboard ctKey;
            ctKey = cvvDialog.findViewById(R.id.ckb);
            ctKey.setKeyAction("OK",this);
            ctKey.setScreenName("buycvv");
            InputConnection ic = etCVV.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            etCVV.setShowSoftInputOnFocus(false);
//            etCVV.setEnabled(false);
            etCVV.requestFocus();

            etCVV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(BuyTokenActivity.this, v);
                }
            });
            etCVV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Utils.hideSoftKeypad(BuyTokenActivity.this, view);
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

//            cvvDialog.setCanceledOnTouchOutside(true);
            cvvDialog.show();
            cvvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (dialogInterface != null) {
                        if (prevSelectedCard != null) {
                            objMyApplication.setSelectedCard(prevSelectedCard);
                            selectedCard = prevSelectedCard;
                        }

                    }
                }
            });
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
            if (!etCVV.getText().toString().trim().equals("")) {
                prevSelectedCard = null;
                cvvDialog.dismiss();
                strCvv = etCVV.getText().toString().trim();
//                etAmount.setText("");
//                ctKey.clearData();
                bindPayMethod(objSelected);
            } else {
                Utils.displayAlert("Please enter CVV", BuyTokenActivity.this, "", "");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindSelectedBank(PaymentsList objData) {
        try {
            objSelected = objData;
            prevSelectedCard = null;
            strCvv = "";
//            etAmount.setText("");
//            ctKey.clearData();
            bindPayMethod(objSelected);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void expiry() {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            final Dialog dialog = new Dialog(BuyTokenActivity.this);
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
                            Intent i = new Intent(BuyTokenActivity.this, EditCardActivity.class);
                            i.putExtra("screen", "buy");
                            startActivity(i);
                        } else {
                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                isBank = true;
                                objMyApplication.setResolveUrl(true);
                                Intent i = new Intent(BuyTokenActivity.this, WebViewActivity.class);
                                i.putExtra("signon", signOnData);
                                startActivityForResult(i, 1);
                            } else {
                                Utils.displayAlert(strSignOn, BuyTokenActivity.this, "", "");
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
            final Dialog dialog = new Dialog(BuyTokenActivity.this);
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
                    pDialog = Utils.showProgressDialog(BuyTokenActivity.this);
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

    private void buyTokenFailure(BuyTokenResponse objData) {
        try {
            prevDialog.dismiss();
            prevDialog = new Dialog(BuyTokenActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.buy_token_transaction_failed);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;


            TextView tvMessage = prevDialog.findViewById(R.id.tvMessage);
            CardView cvTryAgain = prevDialog.findViewById(R.id.cvTryAgain);

            tvMessage.setText("The transaction failed due to error code:\n" + objData.getError().getErrorCode() + " - " + objData.getError().getErrorDescription() + ". Please try again.");
            Window window = prevDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            prevDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            prevDialog.setCancelable(false);
            prevDialog.show();

            cvTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prevDialog.dismiss();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if (editable.length() > 12) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            } else {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
                tvCurrency.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, dollarFont));
            }
            etAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            etAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
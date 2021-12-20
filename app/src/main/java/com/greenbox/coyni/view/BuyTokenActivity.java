package com.greenbox.coyni.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.SelectedPaymentMethodsAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.buytoken.BuyTokenRequest;
import com.greenbox.coyni.model.buytoken.BuyTokenResponse;
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
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

public class BuyTokenActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    PaymentsList selectedCard;
    ImageView imgBankIcon, imgArrow, imgConvert;
    TextView tvLimit, tvPayHead, tvAccNumber, tvCurrency, tvBankName, tvBAccNumber, tvError, tvCYN;
    RelativeLayout lyPayMethod, lyBDetails;
    LinearLayout lyCDetails, lyBuyClose;
    EditText etAmount;
    CustomKeyboard ctKey;
    PaymentMethodsResponse paymentMethodsResponse;
    BuyTokenViewModel buyTokenViewModel;
    Dialog payDialog, prevDialog;
    TransactionLimitResponse objResponse;
    String strLimit = "", strType = "", strBankId = "", strCardId = "", strCvv = "", strSubType = "";
    Double maxValue = 0.0, dget = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0;
    float fontSize;
    Boolean isUSD = false, isCYN = false;
    public static BuyTokenActivity buyTokenActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_buy_token);
            buyTokenActivity = this;
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                    ctKey.enableButton();
                    if (tvCYN.getVisibility() == View.VISIBLE) {
                        isCYN = true;
                        isUSD = false;
                    } else {
                        isCYN = false;
                        isUSD = true;
                    }
                    if (editable.length() > 7) {
                        etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, 55));
                    } else if (editable.length() > 5) {
                        etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, 60));
                    } else {
                        etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
                    }
                } else if (editable.toString().equals(".")) {
                    etAmount.setText("");
                    ctKey.disableButton();
                } else if (editable.length() == 0) {
                    ctKey.disableButton();
                } else {
                    etAmount.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            selectedCard = objMyApplication.getSelectedCard();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
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
            lyBuyClose = findViewById(R.id.lyBuyClose);
            etAmount = findViewById(R.id.etAmount);
            lyCDetails = findViewById(R.id.lyCDetails);
            lyBDetails = findViewById(R.id.lyBDetails);
            ctKey = (CustomKeyboard) findViewById(R.id.ckb);
            ctKey.setKeyAction("Buy\nToken");
            ctKey.setScreenName("buy");
            InputConnection ic = etAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            fontSize = etAmount.getTextSize();
            etAmount.setShowSoftInputOnFocus(false);
//            etAmount.setEnabled(false);
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

//            etAmount.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    try {
//                        if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
//                            ctKey.enableButton();
//                            calculateFee();
//                            if (editable.length() > 7) {
//                                etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, 45));
//                            } else if (editable.length() > 5) {
//                                etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, 50));
//                            } else {
//                                etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
//                            }
//                        } else if (editable.toString().equals(".")) {
//                            etAmount.setText("");
//                            ctKey.disableButton();
//                        } else if (editable.length() == 0) {
//                            ctKey.disableButton();
//                        } else {
//                            etAmount.setText("");
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });

            lyPayMethod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPayMethod();
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
                        if (etAmount.getText().toString().trim().length() > 0) {
                            if (tvCYN.getVisibility() == View.GONE) {
                                tvCYN.setVisibility(View.VISIBLE);
                                tvCurrency.setVisibility(View.GONE);
                                convertUSDtoCYN();
                            } else {
                                tvCYN.setVisibility(View.GONE);
                                tvCurrency.setVisibility(View.VISIBLE);
                                convertCYNtoUSD();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            calculateFee("10");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;
                    setDailyWeekLimit(transactionLimitResponse.getData());
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
                if (buyTokenResponse != null) {

                }
            }
        });

        buyTokenViewModel.getBuyTokenFailureMutableLiveData().observe(this, new Observer<BuyTokenResponse>() {
            @Override
            public void onChanged(BuyTokenResponse buyTokenResponse) {
                if (buyTokenResponse != null) {

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
                tvBankName.setText(objData.getBankName());
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
            params.setMargins(Utils.convertPxtoDP(15), Utils.convertPxtoDP(15), 0, 0);
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
            SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
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

    public void buyTokenPreview() {
        try {
            prevDialog = new Dialog(BuyTokenActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.buy_token_order_review);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

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

                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                    if (currentId == motionLayout.getEndState()) {
                        slideToConfirm.setInteractionEnabled(false);
                        tv_lable.setText("Verifying");
                        buyToken();
                    }
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
            //etAmount.setSelection(etAmount.getText().length() - 3);
            etAmount.addTextChangedListener(BuyTokenActivity.this);
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void convertUSDtoCYN() {
        try {
            if (isUSD) {
                isUSD = false;
                usdValue = Double.parseDouble(etAmount.getText().toString().trim().replace(",", ""));
                cynValue = ((usdValue - feeInAmount) * 100) / (100 + feeInPercentage);
            }
            etAmount.removeTextChangedListener(BuyTokenActivity.this);
            etAmount.setText(String.valueOf(cynValue));
            etAmount.addTextChangedListener(BuyTokenActivity.this);
            USFormat(etAmount);
            etAmount.setSelection(etAmount.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertCYNtoUSD() {
        try {
            if (isCYN) {
                isCYN = false;
                cynValue = Double.parseDouble(etAmount.getText().toString().trim().replace(",", ""));
                usdValue = (cynValue + (cynValue * (feeInPercentage / 100))) + feeInAmount;
            }
            etAmount.removeTextChangedListener(BuyTokenActivity.this);
            etAmount.setText(String.valueOf(usdValue));
            etAmount.addTextChangedListener(BuyTokenActivity.this);
            USFormat(etAmount);
            etAmount.setSelection(etAmount.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buyTokenClick() {
        try {
            convertUSDtoCYN();
            convertCYNtoUSD();
            calculateFee(Utils.USNumberFormat(cynValue));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyToken() {
        try {
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

}
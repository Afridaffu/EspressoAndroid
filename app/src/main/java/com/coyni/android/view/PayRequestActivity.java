package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.model.APIError;
import com.coyni.android.model.templates.TemplateRequest;
import com.coyni.android.model.templates.TemplateResponse;
import com.coyni.android.model.transactions.LimitResponseData;
import com.coyni.android.model.transactions.TransactionLimitRequest;
import com.coyni.android.model.transactions.TransactionLimitResponse;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.userdetails.UserDetails;
import com.coyni.android.model.userrequest.UserRequest;
import com.coyni.android.model.userrequest.UserRequestResponse;
import com.coyni.android.model.wallet.WalletInfo;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.PayViewModel;
import com.coyni.android.viewmodel.SendViewModel;
import com.github.jinatonic.confetti.CommonConfetti;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.coyni.android.viewmodel.BuyViewModel;


public class PayRequestActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    PayViewModel payViewModel;
    BuyViewModel buyViewModel;
    SendViewModel sendViewModel;
    ProgressDialog dialog;
    String strWalletId = "", strTAmount = "", strLimit = "";
    TextInputEditText etMessage;
    EditText etAmount;
    Boolean isMax = false;
    TextView tvBalance, tvPay, tvRequest, tvExchangeValue, tvLimit, tvAmount, tvPFee, tvCharCount;
    ImageView imgMax;
    Double balance, maxValue = 0.0, feeValue = 0.0;
    RelativeLayout layoutProgress, layoutHead, layoutBack;
    CardView cvUserInfo;
    TransactionLimitResponse objResponse;
    int requestedToUserId = 0;
    WalletInfo cynWallet;
    LinearLayout layoutPay;
    float fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_request);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        try {
            layoutHead.setVisibility(View.VISIBLE);
            cvUserInfo.setVisibility(View.VISIBLE);
            layoutProgress.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            Utils.statusBar(PayRequestActivity.this);
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(PayRequestActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(PayRequestActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(PayRequestActivity.this, this, false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == etAmount.getEditableText()) {
            try {
                if (s.length() > 0 && !s.toString().equals(".") && !s.toString().equals(".00")) {
                    calculateFee();
                    if (s.length() > 7) {
                        etAmount.setTextSize(pixelsToSp(PayRequestActivity.this, 45));
                    } else if (s.length() > 5) {
                        etAmount.setTextSize(pixelsToSp(PayRequestActivity.this, 50));
                    } else {
                        etAmount.setTextSize(pixelsToSp(PayRequestActivity.this, fontSize));
                    }
                } else if (s.toString().equals(".")) {
                    etAmount.setText("");
                } else if (s.length() == 0) {
                    tvExchangeValue.setText("0.00 USD");
                    enableButtons(false);
                } else {
                    etAmount.removeTextChangedListener(PayRequestActivity.this);
                    etAmount.setText("");
                    tvExchangeValue.setText("0.00 USD");
                    enableButtons(false);
                    etAmount.addTextChangedListener(PayRequestActivity.this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (s == etMessage.getEditableText()) {
            if (s.length() > 0) {
                tvCharCount.setText(s.length() + "/160");
                enableButtons(true);
            } else {
                tvCharCount.setText("0/160");
                enableButtons(false);
            }
        }
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(PayRequestActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strWalletId = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayRequestActivity.this)) {
                    dialog = new ProgressDialog(PayRequestActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    payViewModel.getUserDetails(strWalletId);
                } else {
                    Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this);
                }
            }
            balance = objMyApplication.getGBTBalance();
            cynWallet = objMyApplication.getGbtWallet();
            layoutBack = findViewById(R.id.layoutBack);
            layoutHead = findViewById(R.id.layoutHead);
            layoutProgress = findViewById(R.id.layoutProgress);
            cvUserInfo = findViewById(R.id.cvUserInfo);
            tvAmount = findViewById(R.id.tvAmount);
            tvLimit = findViewById(R.id.tvLimit);
            tvExchangeValue = findViewById(R.id.tvExchangeValue);
            tvBalance = findViewById(R.id.tvBalance);
            tvPay = findViewById(R.id.tvPay);
            tvRequest = findViewById(R.id.tvRequest);
            tvPFee = findViewById(R.id.tvPFee);
            tvCharCount = findViewById(R.id.tvCharCount);
            etAmount = findViewById(R.id.etAmount);
            etMessage = findViewById(R.id.etMessage);
            imgMax = findViewById(R.id.imgMax);
            layoutPay = findViewById(R.id.layoutPay);
            etAmount.requestFocus();
            String strAmount = "";
            if (balance != null) {
                strAmount = Utils.convertBigDecimalUSDC(String.valueOf(balance));
                tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            }
            enableButtons(false);
            fontSize = etAmount.getTextSize();
            etAmount.addTextChangedListener(this);
            etMessage.addTextChangedListener(this);
            etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
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

            etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && etMessage.getText().toString().length() == 1) {
                        etMessage.setText("");
                        if (!etAmount.getText().toString().trim().equals("")) {
                            enableButtons(true);
                        } else {
                            enableButtons(false);
                        }
                    }
                }
            });

            tvPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(PayRequestActivity.this, v);
                        if (Utils.checkInternet(PayRequestActivity.this)) {
                            if (validation()) {
                                Intent i = new Intent(PayRequestActivity.this, PayRequestPreviewActivity.class);
                                i.putExtra("pay", etAmount.getText().toString());
                                i.putExtra("msg", etMessage.getText().toString().trim());
                                i.putExtra("walletId", strWalletId);
                                startActivity(i);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            tvRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(PayRequestActivity.this, v);
                        if (Utils.checkInternet(PayRequestActivity.this)) {
                            if (requestValidation()) {
//                                strTAmount = USFormat(etAmount) + " " + getString(R.string.currency);
                                strTAmount = USFormat(etAmount);
                                dialog = new ProgressDialog(PayRequestActivity.this, R.style.MyAlertDialogStyle);
                                dialog.setIndeterminate(false);
                                dialog.setMessage("Please wait...");
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.show();
                                TemplateRequest request = new TemplateRequest();
                                request.setBody1(objMyApplication.getStrUser());
                                request.setBody2(strTAmount);
                                request.setBody3(cynWallet.getWalletId());
                                payViewModel.getTemplate(Utils.requestId, request);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            imgMax.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        isMax = true;
                        etAmount.setText(tvBalance.getText().toString().trim());
                        enableButtons(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutBack.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        if (!etAmount.getText().toString().equals("")) {
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                            etAmount.setFilters(FilterArray);
                            USFormat(etAmount);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            });


            if (Utils.checkInternet(PayRequestActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.payType));
                obj.setTransactionSubType(Integer.parseInt(Utils.paySubType));
                buyViewModel.transactionLimits(obj);
            } else {
                Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        payViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {
                dialog.dismiss();
                if (userDetails != null) {
                    bindUserInfo(userDetails);
                }
            }
        });

        payViewModel.getTemplateResponseMutableLiveData().observe(this, new Observer<TemplateResponse>() {
            @Override
            public void onChanged(TemplateResponse templateResponse) {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (templateResponse != null) {
                        if (Utils.checkInternet(PayRequestActivity.this)) {
                            UserRequest request = new UserRequest();
                            request.setAmount(Double.parseDouble(etAmount.getText().toString().replace(",", "")));
                            request.setContent(templateResponse.getData().getInviteBody());
                            request.setPortalType(Utils.portal);
                            request.setRemarks(etMessage.getText().toString().trim());
                            request.setRequestedToUserId(requestedToUserId);
                            request.setRequesterWalletId(cynWallet.getWalletId());
                            request.setRequestType(Utils.request);
                            request.setSubject(Utils.requestSub);
                            payViewModel.userRequests(request);
                        } else {
                            Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getLimitMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;
                    setDailyWeekLimit(objResponse.getData());
                }
            }
        });

        payViewModel.getUserRequestResponseMutableLiveData().observe(this, new Observer<UserRequestResponse>() {
            @Override
            public void onChanged(UserRequestResponse userRequestResponse) {
                dialog.dismiss();
                if (userRequestResponse != null && userRequestResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                    statusBar("FFFFFF");
                    String strValue = "<font  face = \"Open Sans\" size = \"18\" color='#35BAB6'><b>" + strTAmount + "</b></font>";
                    String msg = "Your request for " + strValue + " has been<br>sent Successfully";
                    tvAmount.setText(Html.fromHtml(msg));
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            animation();
                        }
                    });
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                    layoutHead.setVisibility(View.GONE);
                    cvUserInfo.setVisibility(View.GONE);
                    layoutProgress.setVisibility(View.VISIBLE);
                    LinearLayout layoutDone = findViewById(R.id.layoutDone);
                    layoutDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(PayRequestActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    });
                }
            }
        });

        sendViewModel.getTransferFeeMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
                try {
                    if (transferFeeResponse != null) {
                        String strMsg = Utils.feeCalculation(transferFeeResponse);
                        if (!strMsg.equals("")) {
                            tvPFee.setText(strMsg);
                            tvPFee.setVisibility(View.VISIBLE);
                        } else {
                            tvPFee.setVisibility(View.GONE);
                        }
                        if (!etAmount.getText().toString().equals("") && !etAmount.getText().toString().equals("0")) {
                            objMyApplication.setTransferFeeResponse(transferFeeResponse);
                            Double dget = 0.0;
                            Double pay = Double.parseDouble(etAmount.getText().toString().replace(",", ""));
                            Double fee = transferFeeResponse.getData().getFee();
                            feeValue = fee;
                            dget = pay - fee;
                            if (isMax) {
                                isMax = false;
                                etAmount.removeTextChangedListener(PayRequestActivity.this);
                                etAmount.setText(Utils.convertBigDecimalUSDC(String.valueOf(dget)));
                                etAmount.addTextChangedListener(PayRequestActivity.this);
                                calculateFee();
                                USFormat(etAmount);
                            }
//                            USFormat(etAmount);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        payViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    String strMsg = "";
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        strMsg = apiError.getError().getErrorDescription();
                    } else {
                        strMsg = apiError.getError().getFieldErrors().get(0);
                    }
                    if (strMsg.toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                        objMyApplication.displayAlert(PayRequestActivity.this, getString(R.string.session));
                    } else {
                        Context context = new ContextThemeWrapper(PayRequestActivity.this, R.style.Theme_QuickCard);
                        new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.app_name)
                                .setMessage(strMsg)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                                        onBackPressed();
                                    }
                                }).show();

                    }
                }
            }
        });

        payViewModel.getErrorMutableLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dialog.dismiss();
                if (s != null && !s.equals("")) {
                    Context context = new ContextThemeWrapper(PayRequestActivity.this, R.style.Theme_QuickCard);
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.app_name)
                            .setMessage(s)
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                                    onBackPressed();
                                }
                            }).show();
                }
            }
        });

    }

    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, tvNameHead;
            tvName = findViewById(R.id.tvName);
            tvNameHead = findViewById(R.id.tvNameHead);
            requestedToUserId = userDetails.getData().getUserId();
            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            tvNameHead.setText(userDetails.getData().getFirstName().substring(0, 1).toUpperCase() + userDetails.getData().getLastName().substring(0, 1).toUpperCase());
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
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    strAmount = Utils.convertBigDecimalUSDC(objLimit.getDailyAccountLimit());
                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + " USD");
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

    private Boolean requestValidation() {
        Boolean value = true;
        try {
            String strPay = etAmount.getText().toString().trim().replace("\"", "");
            if (strPay.equals("") || Double.parseDouble(strPay.replace(",", "")) == 0.0) {
                Utils.displayAlert("Please enter Amount", PayRequestActivity.this);
                return value = false;
            } else if (etMessage.getText().toString().trim().equals("")) {
                etMessage.requestFocus();
                Utils.displayAlert("Please enter Message to Recipient.", PayRequestActivity.this);
                return value = false;
            } else if (strWalletId.equals(cynWallet.getWalletId())) {
                Utils.displayAlert("Tokens can not request to your own wallet", PayRequestActivity.this);
                return value = false;
            } else if (requestedToUserId == 0) {
                Utils.displayAlert("Wallet data not found.", PayRequestActivity.this);
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            String strPay = etAmount.getText().toString().trim().replace("\"", "");
            String strMinLimit = Utils.USNumberFormat(objResponse.getData().getMinimumLimit()) + " " + getString(R.string.currency);
            if (strPay.equals("") || Double.parseDouble(strPay.replace(",", "")) == 0.0) {
                etAmount.requestFocus();
                Utils.displayAlert("You Pay is required", PayRequestActivity.this);
                return value = false;
            } else if (Double.parseDouble(strPay.replace(",", "")) < objResponse.getData().getMinimumLimit()) {
                etAmount.requestFocus();
                Utils.displayAlert("Amount should be more than minimum limit " + strMinLimit, PayRequestActivity.this);
                return value = false;
            } else if (objResponse.getData().getTokenLimitFlag() && Double.parseDouble(strPay.replace(",", "")) > maxValue) {
                etAmount.requestFocus();
                if (strLimit.equals("daily")) {
                    Utils.displayAlert("Amount exceeds daily limit", PayRequestActivity.this);
                } else if (strLimit.equals("week")) {
                    Utils.displayAlert("Amount exceeds weekly limit", PayRequestActivity.this);
                }
                return value = false;
            } else if (!strPay.equals("") && Double.parseDouble(strPay.replace(",", "")) < 0.0) {
                Utils.displayAlert("Deposit amount cannot be negative.", PayRequestActivity.this);
                return value = false;
            } else if (etMessage.getText().toString().trim().equals("")) {
                etMessage.requestFocus();
                Utils.displayAlert("Please enter Message to Recipient.", PayRequestActivity.this);
                return value = false;
            } else if (Double.parseDouble(strPay.replace(",", "")) > objMyApplication.getGBTBalance()) {
                Utils.displayAlert(getString(R.string.walletBalValidation), PayRequestActivity.this);
                return value = false;
            } else if (!objResponse.getData().getTokenLimitFlag() && Double.parseDouble(strPay) > objMyApplication.getGBTBalance()) {
                Utils.displayAlert(getString(R.string.walletBalValidation), PayRequestActivity.this);
                return value = false;
            } else if (strWalletId.equals(cynWallet.getWalletId())) {
                Utils.displayAlert("Tokens can not be transferred to your own wallet", PayRequestActivity.this);
                return value = false;
            } else if (requestedToUserId == 0) {
                Utils.displayAlert("Wallet data not found.", PayRequestActivity.this);
                return value = false;
            } else {
                TransferFeeResponse transferFeeResponse = objMyApplication.getTransferFeeResponse();
                Double total, yPay;
                yPay = Double.parseDouble(strPay.replace(",", ""));
                total = yPay + transferFeeResponse.getData().getFee();
                if (total > objMyApplication.getGBTBalance()) {
                    Utils.displayAlert("Insufficient balance for this transaction.", PayRequestActivity.this);
                    return value = false;
                } else if (objResponse.getData().getTokenLimitFlag() && total > maxValue) {
                    Utils.displayAlert("Insufficient balance for this transaction.", PayRequestActivity.this);
                    return value = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    private void animation() {
        try {
            ViewGroup container;
            container = findViewById(R.id.layoutProgress);
            CommonConfetti.rainingConfetti(container, new int[]{Color.YELLOW, Color.GREEN, Color.MAGENTA})
                    .oneShot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(PayRequestActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.setSelection(etAmount.getText().length() - 3);
            etAmount.addTextChangedListener(PayRequestActivity.this);
            tvExchangeValue.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)) + " USD");
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            enableButtons(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void statusBar(String color) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(color));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateFee() {
        try {
            if (!etAmount.getText().toString().trim().equals("") && !etAmount.getText().toString().trim().equals("0")) {
                TransferFeeRequest request = new TransferFeeRequest();
                request.setTokens(etAmount.getText().toString().trim().replace(",", ""));
                request.setTxnType(Utils.payType);
                request.setTxnSubType(Utils.paySubType);
                if (Utils.checkInternet(PayRequestActivity.this)) {
                    sendViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableButtons(Boolean value) {
        try {
            if (value && !etAmount.getText().toString().trim().equals("") && !etMessage.getText().toString().trim().equals("")) {
                layoutPay.setBackgroundColor(getResources().getColor(R.color.btnback));
                tvPay.setTextColor(getResources().getColor(R.color.headcolor));
                tvRequest.setTextColor(getResources().getColor(R.color.headcolor));
            } else {
                layoutPay.setBackgroundColor(getResources().getColor(R.color.btndisable));
                tvPay.setTextColor(getResources().getColor(R.color.withdrawopt));
                tvRequest.setTextColor(getResources().getColor(R.color.withdrawopt));
            }
            tvPay.setClickable(value);
            tvRequest.setClickable(value);
            tvPay.setEnabled(value);
            tvRequest.setEnabled(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }
}
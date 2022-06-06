package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.PayToMerchantWithAmountDialog;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletInfo;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.model.paidorder.PaidOrderRequest;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.templates.TemplateRequest;
import com.greenbox.coyni.model.transactionlimit.LimitResponseData;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.CheckOutConstants;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.GiftCardBindingLayoutActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;

import java.util.ArrayList;
import java.util.List;

public class PayToMerchantActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    EditText payET;
    Dialog cvvDialog, prevDialog;
    private DatabaseHandler dbHandler;
    DashboardViewModel dashboardViewModel;
    BusinessDashboardViewModel businessDashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    CoyniViewModel coyniViewModel;
    LinearLayout lyPayClose, lyBalance;
    ImageView imgProfile, imgConvert;
    TextView profileTitle, tvName, accAddress, tvCurrency, coyniTV, availBal, requestTV, payTV, addNoteTV,tvError;
    TransactionLimitResponse objResponse;
    float fontSize, dollarFont;
    WalletInfo cynWallet;
    Boolean isFaceLock = false, isTouchId = false;
    String strAmount = "", strWalletId = "", strLimit = "", strUserName = "", recipientAddress = "", strToken = "";
    Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    Long mLastClickTime = 0L;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;
    private static final int FOR_RESULT = 235;
    boolean isAuthenticationCalled = false, isPayClickable = false, isReqClickable = false, isPayClick = false;
    Dialog pDialog;
    int requestedToUserId = 0;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomKeyboard cKey;
    public static PayToMerchantActivity payToMerchantActivity;
    UserDetails details;
    boolean value;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    TextView merchantType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_to_merchant);
            initialization();
            initObservers();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (start == 0 && after == 0) {
            payET.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, fontSize));
            tvCurrency.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, dollarFont));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null) {

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == payET.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    payET.setHint("");
                    convertUSDValue();
                    if (editable.length() > 8) {
                        payET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() > 5) {
                        payET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else {
                        payET.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, fontSize));
                        tvCurrency.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, dollarFont));
                    }
                    if (Double.parseDouble(editable.toString().replace(",", "")) > 0) {
                        disableButtons(false);
                    } else {
                        disableButtons(true);
                    }
                    if (validation()){
                        disableButtons(false);
                    }
                    else {
                        disableButtons(true);
                    }
                    //payRequestET.setSelection(payRequestET.getText().length());
                } else if (editable.toString().equals(".")) {
                    payET.setText("");
                    disableButtons(true);
                } else if (editable.length() == 0) {
                    payET.setHint("0.00");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
                    cKey.clearData();
                } else {
                    payET.setText("");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
                    cKey.clearData();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
            case 235: {
                try {
                    //payTransaction();
                    pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
                    BiometricTokenRequest request = new BiometricTokenRequest();
                    request.setDeviceId(Utils.getDeviceID());
//                    request.setMobileToken(strToken);
                    request.setMobileToken(objMyApplication.getStrMobileToken());
                    request.setActionType(Utils.paidActionType);
                    coyniViewModel.biometricToken(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 0:
                try {
                    startActivity(new Intent(PayToMerchantActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "Paid")
                            .putExtra(Utils.wallet, recipientAddress)
                            .putExtra(Utils.amount, payET.getText().toString().replace(",", "").trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
//        try {
//            if (cvvDialog != null && addNoteET.hasFocus()) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        addNoteET.requestFocus();
//                        Utils.openKeyPad(PayToMerchantActivity.this, addNoteET);
//                    }
//                }, 100);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        super.onResume();
    }


    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            payET = findViewById(R.id.merchantAmountET);
            lyPayClose = findViewById(R.id.payToMerchantClose);
            tvCurrency = findViewById(R.id.tvCurrency);
            coyniTV = findViewById(R.id.tvCYN);
            tvError = findViewById(R.id.tvError);
            availBal = findViewById(R.id.tvAvailableBal);
            lyBalance = findViewById(R.id.lybalance);
            cKey = (CustomKeyboard) findViewById(R.id.ckb);
            imgConvert = findViewById(R.id.imageConvertIV);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            dbHandler = DatabaseHandler.getInstance(PayToMerchantActivity.this);

            fontSize = payET.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            payET.requestFocus();
            payET.setShowSoftInputOnFocus(false);
            payToMerchantActivity = this;
            merchantType = findViewById(R.id.merchantTypeTV);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);

            InputConnection ic = payET.onCreateInputConnection(new EditorInfo());
            cKey.setInputConnection(ic);
            cKey.setScreenName("payToMerch");
            payET.setOnClickListener(v -> Utils.hideSoftKeypad(PayToMerchantActivity.this, v));
            payET.addTextChangedListener(this);

            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strWalletId = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayToMerchantActivity.this)) {
                    dashboardViewModel.getUserDetail(strWalletId);
                } else {
                    Utils.displayAlert(getString(R.string.internet), PayToMerchantActivity.this, "", "");
                }
            }

            calculateFee("10");
            transactionLimitCall();
            walletAPICall();
            onClickListeners();
            setFaceLock();
            setTouchId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void walletAPICall() {

        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletType(Utils.TOKEN);
        walletRequest.setUserId(String.valueOf(objMyApplication.getLoginUserId()));

        businessDashboardViewModel.meMerchantWallet(walletRequest);
    }

    private void transactionLimitCall() {
        if (Utils.checkInternet(PayToMerchantActivity.this)) {
            TransactionLimitRequest obj = new TransactionLimitRequest();
            obj.setTransactionType(Utils.saleOrder);
            obj.setTransactionSubType(Utils.saleOrderToken);
            pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
            } else {
                buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
            }
        }
    }

    private void intents() {

        if (getIntent().getStringExtra(CheckOutConstants.CheckOutAmount) != null && !getIntent().getStringExtra(CheckOutConstants.CheckOutAmount).equalsIgnoreCase("")) {
            String amount = getIntent().getStringExtra(CheckOutConstants.CheckOutAmount);
            payET.setText(Utils.convertTwoDecimal(amount));
            USFormat(payET);
            if (payValidation()) {
                pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
                isPayClick = true;
                cynValue = Double.parseDouble(payET.getText().toString().trim().replace(",", ""));
                calculateFee(Utils.USNumberFormat(cynValue));
            }
        }
    }

    private void onClickListeners() {
        payET.setOnFocusChangeListener((view, b) -> {
            try {
                Utils.hideSoftKeypad(PayToMerchantActivity.this, view);
                if (!b) {
                    if (!payET.getText().toString().equals("")) {
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                        payET.setFilters(FilterArray);
                        USFormat(payET);
                    }
                } else {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                    payET.setFilters(FilterArray);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        lyPayClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        imgConvert.setOnClickListener(view -> {
            try {
                if (payET.getText().toString().trim().length() > 0) {
                    convertDecimal();
                    if (coyniTV.getVisibility() == View.GONE) {
                        coyniTV.setVisibility(View.VISIBLE);
                        tvCurrency.setVisibility(View.INVISIBLE);
                        payET.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                    } else {
                        coyniTV.setVisibility(View.GONE);
                        tvCurrency.setVisibility(View.VISIBLE);
                        payET.setGravity(Gravity.CENTER_VERTICAL);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void initObservers() {
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, userDetails -> {
            if (userDetails != null && userDetails.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                bindUserInfo(userDetails);
                details = userDetails;
                paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
                businessIdentityVerificationViewModel.getBusinessType();

            } else {
                if (userDetails.getError() != null && userDetails.getError().getErrorDescription() != null) {
                    Utils.displayAlert(userDetails.getError().getErrorDescription(), this, "Oops", "");
                }
            }
        });

        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, transactionLimitResponse -> {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            if (transactionLimitResponse != null && transactionLimitResponse.getData() != null) {
                objResponse = transactionLimitResponse;
                setDailyWeekLimit(objResponse.getData());
            }
        });

        buyTokenViewModel.getTransferFeeResponseMutableLiveData().observe(this, transferFeeResponse -> {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            if (transferFeeResponse != null) {
                objMyApplication.setTransferFeeResponse(transferFeeResponse);
                feeInAmount = transferFeeResponse.getData().getFeeInAmount();
                feeInPercentage = transferFeeResponse.getData().getFeeInPercentage();
                pfee = transferFeeResponse.getData().getFee();
                if (!payET.getText().toString().equals("") && !payET.getText().toString().equals("0") && Double.parseDouble(payET.getText().toString()) > 0) {
                    if (isPayClick) {
                        isPayClick = false;
                        LogUtils.e("payRequestET", payET.getText().toString());
                        payPreview();
                    }
                } else {
                    disableButtons(true);
                }
            }
        });

        payViewModel.getPaidOrderRespMutableLiveData().observe(this, paidOrderResp -> {
            try {
                if (paidOrderResp != null) {
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
//                    Utils.setStrToken("");
                    objMyApplication.clearStrToken();
                    objMyApplication.setPaidOrderResp(paidOrderResp);
                    if (paidOrderResp.getStatus().equalsIgnoreCase("success")) {
                        startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Success")
                                .putExtra("subtype", "paid"));

                    } else {
                        startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Failed")
                                .putExtra("subtype", "paid"));
                    }
                } else {
                    Utils.displayAlert("something went wrong", PayToMerchantActivity.this, "", "");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

//        payViewModel.getPayRequestResponseMutableLiveData().observe(this, payRequestResponse -> {
//            try {
//                if (payRequestResponse != null) {
//                    if (pDialog != null) {
//                        pDialog.dismiss();
//                    }
//                    Utils.setStrToken("");
//                    objMyApplication.setPayRequestResponse(payRequestResponse);
//                    if (payRequestResponse.getStatus().toLowerCase().equals("success")) {
//                        startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
//                                .putExtra("status", "success")
//                                .putExtra("subtype", "pay"));
//
//                    } else {
//                        startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
//                                .putExtra("status", "failed")
//                                .putExtra("subtype", "pay"));
//                    }
//                } else {
//                    Utils.displayAlert("something went wrong", PayToMerchantActivity.this, "", "");
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });

//        payViewModel.getTemplateResponseMutableLiveData().observe(this, templateResponse -> {
//            if (templateResponse != null) {
//                if (Utils.checkInternet(PayToMerchantActivity.this)) {
//                    UserRequest request = new UserRequest();
//                    request.setAmount(Double.parseDouble(payET.getText().toString().replace(",", "")));
//                    request.setContent(templateResponse.getData().getInviteBody());
//                    request.setPortalType(Utils.portal);
//                    request.setRemarks(addNoteTV.getText().toString().trim());
//                    request.setRequestedToUserId(requestedToUserId);
//                    request.setRequesterWalletId(cynWallet.getWalletId());
//                    request.setRequestType(Utils.request);
//                    request.setSubject(Utils.requestSub);
//                    payViewModel.userRequests(request);
//                } else {
//                    Utils.displayAlert(getString(R.string.internet), PayToMerchantActivity.this, "", "");
//                }
//            }
//        });

//        payViewModel.getUserRequestResponseMutableLiveData().observe(this, userRequestResponse -> {
//            if (prevDialog != null) {
//                prevDialog.dismiss();
//            }
//            if (userRequestResponse.getStatus().equalsIgnoreCase("success")) {
//                startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
//                        .putExtra("status", "success")
//                        .putExtra("subtype", "request"));
//
//            } else {
//                startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
//                        .putExtra("status", "failed")
//                        .putExtra("subtype", "request"));
//            }
//        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, biometricTokenResponse -> {
            if (biometricTokenResponse != null) {
                if (biometricTokenResponse.getStatus().equalsIgnoreCase("success")) {
                    if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
//                        Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        objMyApplication.setStrToken(biometricTokenResponse.getData().getRequestToken());
                    }
                    payTransaction();
                }
            }
        });

        try {
            businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onChanged(BusinessTypeResp businessTypeResp) {
                    if (businessTypeResp != null && businessTypeResp.getStatus().equalsIgnoreCase("SUCCESS")) {
                        for (int i = 0; i < businessTypeResp.getData().size(); i++) {
                            try {
                                if (details.getData() != null && details.getData().getBusinessType() != null && details.getData().getBusinessType().toLowerCase().trim().equals(businessTypeResp.getData().get(i).getKey().toLowerCase().trim())) {
                                    String bType = businessTypeResp.getData().get(i).getValue();
                                    if (bType.length() > 11) {
                                        merchantType.setText("(" + bType.substring(0, 10) + "..." + ")");
                                    } else {
                                        merchantType.setText("(" + bType + ")");
                                    }
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse != null) {
                    try {
                        if (businessWalletResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (businessWalletResponse.getData() != null && businessWalletResponse.getData().getWalletNames() != null) {
                                List<WalletInfo> walletData = businessWalletResponse.getData().getWalletNames();
                                if (walletData.get(0).getWalletId()!= null) {
                                    avaBal = walletData.get(0).getAvailabilityToUse();
                                    availBal.setText(Utils.USNumberFormat(avaBal));
                                    cynWallet = walletData.get(0);

                                    intents();
                                }
                            }
                        }
                        else {
                            try {
                                Utils.displayAlert(businessWalletResponse.getError().getErrorDescription(),PayToMerchantActivity.this,"Oops","");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, userName, userWalletAddre;
            ImageView userProfile;
            tvName = findViewById(R.id.merchantyNameTV);
            userName = findViewById(R.id.userProfileTextTV);
            userProfile = findViewById(R.id.userProfileIV);
            userWalletAddre = findViewById(R.id.accountAddress);

            if (userDetails.getData() != null) {
                requestedToUserId = userDetails.getData().getUserId();
            }
            if (userDetails.getData() != null && userDetails.getData().getFullName() != null) {
                if (userDetails.getData().getFullName().length() > 20) {
                    tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
                } else {
                    tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
                }
                strUserName = Utils.capitalize(userDetails.getData().getFullName());
            }
//            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            String imageTextNew = "";
            if (userDetails.getData().getFirstName() != null && userDetails.getData().getLastName() != null) {
                imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                        userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            }
            userName.setText(imageTextNew);
            if (userDetails.getData().getWalletId() != null) {
                if (userDetails.getData().getWalletId().length() > Integer.parseInt(getString(R.string.waddress_length))) {
                    userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                } else {
                    userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
                }
            }
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(userDetails.getData().getImage(), userProfile, R.drawable.ic_profilelogo);
            } else {
                userProfile.setVisibility(View.GONE);
                userName.setVisibility(View.VISIBLE);
            }
            recipientAddress = "";
            recipientAddress = userDetails.getData().getWalletId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean payValidation() {
        try {
            String strPay = payET.getText().toString().trim().replace("\"", "");

            if (cynValue > avaBal) {
                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
                value = false;
            } else {
                value = true;
            }
//            else if (Double.parseDouble(strPay.replace(",", "")) > avaBal) {
//                Utils.displayAlert("Amount entered exceeds available balance", PayToMerchantActivity.this, "", "");
//                value = false;
//            }

//            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
//                                    isPayClick = true;
//                                    pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
//                                    cynValue = Double.parseDouble(payRequestET.getText().toString().trim().replace(",", ""));
//                                    calculateFee(Utils.USNumberFormat(cynValue));
//                                } else {
//                                    objMyApplication.setStrScreen("payRequest");
//                                    Intent i = new Intent(PayToMerchantActivity.this, BuyTokenPaymentMethodsActivity.class);
//                                    i.putExtra("screen", "payRequest");
//                                    startActivity(i);
//                                }
//            else if (objResponse != null && objResponse.getData() != null && objResponse.getData().getTransactionLimit() != null && cynValue > Double.parseDouble(objResponse.getData().getTransactionLimit())) {
//                Utils.displayAlert("Amount entered exceeds transaction limit.", PayToMerchantActivity.this, "Oops!", "");
//                value = false;
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private boolean validation() {
        boolean value = true;
        try {
            String strPay = payET.getText().toString().trim().replace("\"", "");
            if (Double.parseDouble(strPay.replace(",", "")) == 0.0) {
                //Utils.displayAlert("Amount should be greater than zero.", PayRequestActivity.this, "Oops!", "");
                tvError.setText("Amount should be greater than zero.");
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if ((Double.parseDouble(strPay.replace(",", "")) < Double.parseDouble(objResponse.getData().getMinimumLimit()))) {
                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(Double.parseDouble(objResponse.getData().getMinimumLimit())) + " CYN");
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
//            } else if (cynValue > Double.parseDouble(objResponse.getData().getTransactionLimit())) {
            } else if (Double.parseDouble(strPay.replace(",", "")) > Double.parseDouble(objResponse.getData().getTransactionLimit())) {
                tvError.setText("Amount entered exceeds transaction limit.");
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else {
                tvError.setVisibility(View.GONE);
                lyBalance.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private Boolean requestValidation() {
        Boolean value = true;
        try {
            String strPay = payET.getText().toString().trim().replace("\"", "");
            if ((Double.parseDouble(strPay.replace(",", "")) > Double.parseDouble(getString(R.string.payrequestMaxAmt)))) {
                value = false;
                Utils.displayAlert("You can request up to " + Utils.USNumberFormat(Double.parseDouble(getString(R.string.payrequestMaxAmt))) + " CYN", PayToMerchantActivity.this, "Oops!", "");
            }
//            else if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
//                value = false;
//                Utils.displayAlert("You can only Pay", PayToMerchantActivity.this, "Oops!", "");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void disableButtons(Boolean value) {
        try {
            if (value) {
//                payRequestLL.setBackgroundResource(R.drawable.payrequest_bgcolor);
                cKey.disableButton();
                isPayClickable = false;
                isReqClickable = false;
            } else {
//                payRequestLL.setBackgroundResource(R.drawable.payrequest_activebg);
                cKey.enableButton();
                isPayClickable = true;
                isReqClickable = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if (editable.length() > 12) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            } else {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                payET.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, fontSize));
                tvCurrency.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, dollarFont));
            }
            payET.setFilters(FilterArray);
            payET.setSelection(payET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            payET.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateFee(String strAmount) {
        try {
            TransferFeeRequest request = new TransferFeeRequest();
            request.setTokens(strAmount.trim().replace(",", ""));
            if (Utils.checkInternet(PayToMerchantActivity.this)) {
                request.setTxnType(String.valueOf(Utils.saleOrder));
                request.setTxnSubType(Utils.tokenType);
                buyTokenViewModel.transferFee(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransaction() {
        try {
            PaidOrderRequest request = new PaidOrderRequest();
            request.setTokensAmount(Double.parseDouble(payET.getText().toString().trim().replace(",", "").trim()));
            request.setRecipientWalletId(recipientAddress);
//            request.setRequestToken(Utils.getStrToken());
            request.setRequestToken(objMyApplication.getStrToken());
            objMyApplication.setPaidOrderRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
            if (Utils.checkInternet(PayToMerchantActivity.this)) {
                payViewModel.paidOrder(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransactionRequest() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(payET.getText().toString().trim().replace(",", ""));
            request.setRemarks(addNoteTV.getText().toString().trim());
            request.setRecipientWalletId(recipientAddress);
            objMyApplication.setTransferPayRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void requestTransaction() {
        try {
            TemplateRequest request = new TemplateRequest();
            request.setBody1(objMyApplication.getStrUserName());
            request.setBody2(Utils.convertBigDecimalUSDC(payET.getText().toString().trim().replace(",", "")));
            request.setBody3(recipientAddress);
            payViewModel.getTemplate(Utils.requestId, request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertUSDValue() {
        try {
            usdValue = Double.parseDouble(payET.getText().toString().trim().replace(",", ""));
            cynValue = (usdValue + (usdValue * (feeInPercentage / 100))) + feeInAmount;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(PayToMerchantActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(PayToMerchantActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
                if (objLimit.getTransactionLimit() != null && !objLimit.getTransactionLimit().equalsIgnoreCase("NA") && !objLimit.getTransactionLimit().equalsIgnoreCase("unlimited")) {
                    maxValue = Double.parseDouble(objLimit.getTransactionLimit());
                }
                if (maxValue > 0) {
                    if (objLimit.getLimitType().equalsIgnoreCase("daily")) {
                        strLimit = "daily";
                    } else if (objLimit.getLimitType().equalsIgnoreCase("weekly")) {
                        strLimit = "week";
                    } else if (objLimit.getLimitType().equalsIgnoreCase("unlimited")) {
                        strLimit = "unlimited";
                    } else {
                        strLimit = "daily";
                    } }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayAlert(String msg, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(PayToMerchantActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = dialog.findViewById(R.id.tvHead);
        TextView message = dialog.findViewById(R.id.tvMessage);
        CardView actionCV = dialog.findViewById(R.id.cvAction);
        TextView actionText = dialog.findViewById(R.id.tvAction);
        actionText.setText("Buy Token");

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                objMyApplication.setStrScreen("payRequest");
                Intent i = new Intent(PayToMerchantActivity.this, BuyTokenPaymentMethodsActivity.class);
                i.putExtra("screen", "payRequest");
                startActivity(i);
                //finish();
            }
        });

        message.setText(msg);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        if (objMyApplication.getCheckOutModel()!= null && objMyApplication.getCheckOutModel().isCheckOutFlag()) {
            dialog.setCanceledOnTouchOutside(false);
        } else {
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
    }

    private void displayComments() {
        try {
            cvvDialog = new Dialog(PayToMerchantActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.add_note_layout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

//            addNoteET = cvvDialog.findViewById(R.id.addNoteET);
            CardView doneBtn = cvvDialog.findViewById(R.id.doneBtn);
            TextInputLayout addNoteTIL = cvvDialog.findViewById(R.id.etlMessage);
            LinearLayout cancelBtn = cvvDialog.findViewById(R.id.cancelBtn);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    addNoteET.requestFocus();
//                    Utils.openKeyPad(PayToMerchantActivity.this, addNoteET);
//
//                }
//            }, 100);
//            addNoteET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    if (charSequence.length() == 0) {
//                        addNoteTIL.setCounterEnabled(false);
//                    } else {
//                        addNoteTIL.setCounterEnabled(true);
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    try {
//                        String str = addNoteET.getText().toString();
//                        if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
//                            addNoteET.setText("");
//                            addNoteET.setSelection(addNoteET.getText().length());
//                        } else if (str.length() > 0 && str.contains(".")) {
//                            addNoteET.setText(addNoteET.getText().toString().replaceAll("\\.", ""));
//                            addNoteET.setSelection(addNoteET.getText().length());
//                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
//                            addNoteET.setText("");
//                            addNoteET.setSelection(addNoteET.getText().length());
//                        }
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//            if (!addNoteTV.getText().toString().trim().equals("")) {
//                addNoteET.setText(addNoteTV.getText().toString().trim());
//                addNoteET.setSelection(addNoteET.getText().toString().trim().length());
//            }
            Window window = cvvDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            cvvDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            cvvDialog.show();
            cancelBtn.setOnClickListener(view -> {
                cvvDialog.dismiss();
                Utils.hideKeypad(PayToMerchantActivity.this);
            });
            doneBtn.setOnClickListener(view -> {
                try {
//                        addNoteTV.setText(addNoteET.getText().toString().trim());
                    cvvDialog.dismiss();
                    Utils.hideKeypad(PayToMerchantActivity.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertDecimal() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            payET.setFilters(FilterArray);
            USFormat(payET);
            payET.setSelection(payET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payPreview() {
        try {
            value = true;
            showPayToMerchantWithAmountDialog(String.valueOf(cynValue), details, value, avaBal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void payAmountClick() {
        if (isPayClickable) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            convertDecimal();

            if (payValidation()) {
                isPayClick = true;
                pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
                cynValue = Double.parseDouble(payET.getText().toString().trim().replace(",", ""));
                calculateFee(Utils.USNumberFormat(cynValue));
            }
//                                if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
//                                    isPayClick = true;
//                                    pDialog = Utils.showProgressDialog(PayRequestActivity.this);
//                                    cynValue = Double.parseDouble(payRequestET.getText().toString().trim().replace(",", ""));
//                                    calculateFee(Utils.USNumberFormat(cynValue));
//                                } else {
//                                    objMyApplication.setStrScreen("payRequest");
//                                    Intent i = new Intent(PayRequestActivity.this, BuyTokenPaymentMethodsActivity.class);
//                                    i.putExtra("screen", "payRequest");
//                                    startActivity(i);
//                                }
        }
    }


    private void showPayToMerchantWithAmountDialog(String amount, UserDetails userDetails, boolean isPayToMerchantActivity, Double balance) {
        PayToMerchantWithAmountDialog payToMerchantWithAmountDialog = new PayToMerchantWithAmountDialog(PayToMerchantActivity.this, amount, userDetails, isPayToMerchantActivity, balance, "");
        payToMerchantWithAmountDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase("payTransaction")) {
                    if (!isAuthenticationCalled) {
                        isAuthenticationCalled = true;
                        if ((isFaceLock || isTouchId) && Utils.checkAuthentication(PayToMerchantActivity.this)) {
                            if (objMyApplication.getBiometric() && ((isTouchId && Utils.isFingerPrint(PayToMerchantActivity.this)) || (isFaceLock))) {
                                Utils.checkAuthentication(PayToMerchantActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                            } else {
//                                payTransaction();
                                startActivity(new Intent(PayToMerchantActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("screen", "Paid")
                                        .putExtra(Utils.wallet, recipientAddress)
                                        .putExtra(Utils.amount, payET.getText().toString().replace(",", "").trim()));

                            }
                        } else {
//                            payTransaction();
                            startActivity(new Intent(PayToMerchantActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "Paid")
                                    .putExtra(Utils.wallet, recipientAddress)
                                    .putExtra(Utils.amount, payET.getText().toString().replace(",", "").trim()));
                        }
                    }
                    LogUtils.v("Scan", "onDialog Clicked " + action);
                }
            }
        });

        if (objMyApplication.getCheckOutModel() != null && objMyApplication.getCheckOutModel().isCheckOutFlag()) {
            payToMerchantWithAmountDialog.setCanceledOnTouchOutside(false);
        }

        payToMerchantWithAmountDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isAuthenticationCalled = false;
                if (objMyApplication.getCheckOutModel() != null && objMyApplication.getCheckOutModel().isCheckOutFlag()) {
                    onBackPressed();
                }
            }
        });
        payToMerchantWithAmountDialog.show();
    }

//        private void setFaceLock() {
//        try {
//            isFaceLock = false;
//            String value = dbHandler.getFacePinLock();
//            if (value != null && value.equals("true")) {
//                isFaceLock = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isFaceLock = false;
//                objMyApplication.setLocalBiometric(false);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void setTouchId() {
//        try {
//            isTouchId = false;
//            String value = dbHandler.getThumbPinLock();
//            if (value != null && value.equals("true")) {
//                isTouchId = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isTouchId = false;
//                objMyApplication.setLocalBiometric(false);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setTouchId() {
        try {
            isTouchId = false;
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                isTouchId = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (objMyApplication.getCheckOutModel() != null && objMyApplication.getCheckOutModel().isCheckOutFlag()) {
            objMyApplication.setCheckOutModel(null);
        }
        super.onBackPressed();
    }
}
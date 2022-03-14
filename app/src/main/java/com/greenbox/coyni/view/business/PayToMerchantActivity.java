package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.businesswallet.WalletInfo;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.templates.TemplateRequest;
import com.greenbox.coyni.model.templates.TemplateResponse;
import com.greenbox.coyni.model.transactionlimit.LimitResponseData;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.GiftCardBindingLayoutActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;

public class PayToMerchantActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    EditText payET;
    Dialog cvvDialog, prevDialog;
    private DatabaseHandler dbHandler;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID, dsPermanentToken;
    DashboardViewModel dashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    CoyniViewModel coyniViewModel;
    LinearLayout lyPayClose, lyBalance;
    ImageView imgProfile, imgConvert;
    TextView profileTitle, tvName, accAddress, tvCurrency, coyniTV, availBal, requestTV, payTV, addNoteTV;
    TransactionLimitResponse objResponse;
    float fontSize, dollarFont;
    WalletInfo cynWallet;
    Boolean isFaceLock = false, isTouchId = false;
    String strAmount = "", strWalletId = "", strLimit = "", strUserName = "", recipientAddress = "", strToken = "";
    Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    Long mLastClickTime = 0L;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private static int FOR_RESULT = 235;
    boolean isAuthenticationCalled = false, isPayClickable = false, isReqClickable = false, isPayClick = false;
    ProgressDialog pDialog;
    int requestedToUserId = 0;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomKeyboard cKey;

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
                    request.setActionType(Utils.sendActionType);
                    coyniViewModel.biometricToken(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 0:
                startActivity(new Intent(PayToMerchantActivity.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("screen", "Pay"));
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

//    public void SetFaceLock() {
//        try {
//            isFaceLock = false;
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
//            dsFacePin.moveToFirst();
//            if (dsFacePin.getCount() > 0) {
//                String value = dsFacePin.getString(1);
//                if (value.equals("true")) {
//                    isFaceLock = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isFaceLock = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void SetTouchId() {
//        try {
//            isTouchId = false;
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
//            dsTouchID.moveToFirst();
//            if (dsTouchID.getCount() > 0) {
//                String value = dsTouchID.getString(1);
//                if (value.equals("true")) {
//                    isTouchId = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isTouchId = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

//    private void SetToken() {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsPermanentToken = mydatabase.rawQuery("Select * from tblPermanentToken", null);
//            dsPermanentToken.moveToFirst();
//            if (dsPermanentToken.getCount() > 0) {
//                strToken = dsPermanentToken.getString(1);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


    public void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

    public void setFaceLock() {
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

    public void setTouchId() {
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

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            payET = findViewById(R.id.merchantAmountET);
            lyPayClose = findViewById(R.id.payToMerchantClose);
//            imgProfile = findViewById(R.id.userProfileIV);
//            profileTitle = findViewById(R.id.userProfileTextTV);
//            tvName = findViewById(R.id.merchantyNameTV);
//            accAddress = findViewById(R.id.accountAddress);
            tvCurrency = findViewById(R.id.tvCurrency);
            coyniTV = findViewById(R.id.tvCYN);
            availBal = findViewById(R.id.tvAvailableBal);
            imgConvert = findViewById(R.id.imageConvertIV);
//            lyBalance = findViewById(R.id.lyBalance);
//            payRequestLL = findViewById(R.id.payRequestLL);
//            addNoteClickLL = findViewById(R.id.addNoteClickLL);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            dbHandler = DatabaseHandler.getInstance(PayToMerchantActivity.this);
            fontSize = payET.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            availBal.setText(Utils.USNumberFormat(objMyApplication.getGBTBalance()));
            avaBal = objMyApplication.getGBTBalance();
            cynWallet = objMyApplication.getGbtWallet();
            payET.requestFocus();
            payET.setShowSoftInputOnFocus(false);
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strWalletId = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayToMerchantActivity.this)) {
                    dashboardViewModel.getUserDetail(strWalletId);
                } else {
                    Utils.displayAlert(getString(R.string.internet), PayToMerchantActivity.this, "", "");
                }
            }

            payET.addTextChangedListener(this);
            if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                payET.setText(getIntent().getStringExtra("amount"));
                USFormat(payET);
                payET.setEnabled(false);
            } else {
                //enableButtons();
                cKey = (CustomKeyboard) findViewById(R.id.ckb);
                InputConnection ic = payET.onCreateInputConnection(new EditorInfo());
                cKey.setInputConnection(ic);
            }
            payET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(PayToMerchantActivity.this, v);
                }
            });

            payET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
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
                }
            });

            lyPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


//            addNoteClickLL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    displayComments();
//                }
//            });

            imgConvert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (payET.getText().toString().trim().length() > 0) {
                            convertDecimal();
                            if (coyniTV.getVisibility() == View.GONE) {
                                coyniTV.setVisibility(View.VISIBLE);
                                tvCurrency.setVisibility(View.INVISIBLE);
                                payET.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                            } else {
                                coyniTV.setVisibility(View.GONE);
                                tvCurrency.setVisibility(View.VISIBLE);
                                payET.setGravity(Gravity.CENTER_VERTICAL);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
//            SetToken();
            setFaceLock();
            setTouchId();
//            enableButtons();
            calculateFee("10");
            if (Utils.checkInternet(PayToMerchantActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.payType));
                obj.setTransactionSubType(Integer.parseInt(Utils.paySubType));
//                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                } else {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObservers() {
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {
                if (userDetails != null) {
                    bindUserInfo(userDetails);
                }
            }
        });

        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;
                    setDailyWeekLimit(objResponse.getData());
                }
            }
        });

        buyTokenViewModel.getTransferFeeResponseMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
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
                            Log.e("payRequestET", payET.getText().toString());
                            payPreview();
                        }
                    } else {
                        disableButtons(true);
                    }
                }
            }
        });

        payViewModel.getPayRequestResponseMutableLiveData().observe(this, new Observer<PayRequestResponse>() {
            @Override
            public void onChanged(PayRequestResponse payRequestResponse) {
                try {
                    if (payRequestResponse != null) {
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                        Utils.setStrToken("");
                        objMyApplication.setPayRequestResponse(payRequestResponse);
                        if (payRequestResponse.getStatus().toLowerCase().equals("success")) {
                            startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "success")
                                    .putExtra("subtype", "pay"));

                        } else {
                            startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "failed")
                                    .putExtra("subtype", "pay"));
                        }
                    } else {
                        Utils.displayAlert("something went wrong", PayToMerchantActivity.this, "", "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        payViewModel.getTemplateResponseMutableLiveData().observe(this, new Observer<TemplateResponse>() {
            @Override
            public void onChanged(TemplateResponse templateResponse) {
                if (templateResponse != null) {
                    if (templateResponse != null) {
                        if (Utils.checkInternet(PayToMerchantActivity.this)) {
                            UserRequest request = new UserRequest();
                            request.setAmount(Double.parseDouble(payET.getText().toString().replace(",", "")));
                            request.setContent(templateResponse.getData().getInviteBody());
                            request.setPortalType(Utils.portal);
                            request.setRemarks(addNoteTV.getText().toString().trim());
                            request.setRequestedToUserId(requestedToUserId);
                            request.setRequesterWalletId(cynWallet.getWalletId());
                            request.setRequestType(Utils.request);
                            request.setSubject(Utils.requestSub);
                            payViewModel.userRequests(request);
                        } else {
                            Utils.displayAlert(getString(R.string.internet), PayToMerchantActivity.this, "", "");
                        }
                    }
                }
            }
        });

        payViewModel.getUserRequestResponseMutableLiveData().observe(this, new Observer<UserRequestResponse>() {
            @Override
            public void onChanged(UserRequestResponse userRequestResponse) {
                if (prevDialog != null) {
                    prevDialog.dismiss();
                }
                if (userRequestResponse.getStatus().toLowerCase().equals("success")) {
                    startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
                            .putExtra("status", "success")
                            .putExtra("subtype", "request"));

                } else {
                    startActivity(new Intent(PayToMerchantActivity.this, GiftCardBindingLayoutActivity.class)
                            .putExtra("status", "failed")
                            .putExtra("subtype", "request"));
                }
            }
        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                if (biometricTokenResponse != null) {
                    if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                        if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        }
                        payTransaction();
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

            requestedToUserId = userDetails.getData().getUserId();
            if (userDetails.getData().getFullName().length() > 20) {
                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
            } else {
                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            }
//            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            strUserName = Utils.capitalize(userDetails.getData().getFullName());
            String imageTextNew = "";
            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            userName.setText(imageTextNew);
            if (userDetails.getData().getWalletId().length() > Integer.parseInt(getString(R.string.waddress_length))) {
                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            } else {
                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
            }
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                Glide.with(PayToMerchantActivity.this)
                        .load(userDetails.getData().getImage())
                        .placeholder(R.drawable.ic_profilelogo)
                        .into(userProfile);
            } else {
                userProfile.setVisibility(View.GONE);
                userName.setVisibility(View.VISIBLE);
            }
            recipientAddress = "";
            recipientAddress = userDetails.getData().getWalletId().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean payValidation() {
        Boolean value = true;
        try {
            //cynValidation = Double.parseDouble(objResponse.getData().getMinimumLimit());
            String strPay = payET.getText().toString().trim().replace("\"", "");
//            if ((Double.parseDouble(strPay.replace(",", "")) < cynValidation)) {
//                Utils.displayAlert("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN", PayToMerchantActivity.this, "", "");
//                return value = false;
//            } else if (objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(strPay.replace(",", "")) > maxValue) {
//                if (strLimit.equals("daily")) {
//                    tvError.setText("Amount entered exceeds your daily limit");
//                } else if (strLimit.equals("week")) {
//                    tvError.setText("Amount entered exceeds your weekly limit");
//                }
//                tvError.setVisibility(View.VISIBLE);
//                lyBalance.setVisibility(View.GONE);
//                return value = false;
//            } else if (Double.parseDouble(strPay.replace(",", "")) > avaBal) {
//                Utils.displayAlert("Amount entered exceeds available balance", PayToMerchantActivity.this, "", "");
//                return value = false;
//            } else if (cynValue > avaBal) {
//                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
//                return value = false;
//            }
//            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() == 0) {
//                objMyApplication.setStrScreen("payRequest");
//                Intent i = new Intent(PayToMerchantActivity.this, BuyTokenPaymentMethodsActivity.class);
//                i.putExtra("screen", "payRequest");
//                startActivity(i);
//                value = false;
//            } else
            if (cynValue > avaBal) {
                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
                value = false;
            } else if (cynValue > Double.parseDouble(objResponse.getData().getTransactionLimit())) {
                Utils.displayAlert("Amount entered exceeds transaction limit.", PayToMerchantActivity.this, "Oops!", "");
                value = false;
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
                isPayClickable = false;
                isReqClickable = false;
            } else {
//                payRequestLL.setBackgroundResource(R.drawable.payrequest_activebg);
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
            request.setTxnType(Utils.payType);
            request.setTxnSubType(Utils.paySubType);
            if (Utils.checkInternet(PayToMerchantActivity.this)) {
                buyTokenViewModel.transferFee(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransaction() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(payET.getText().toString().trim().replace(",", ""));
            request.setRemarks(addNoteTV.getText().toString().trim());
            request.setRecipientWalletId(recipientAddress);
            objMyApplication.setTransferPayRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
            if (Utils.checkInternet(PayToMerchantActivity.this)) {
                payViewModel.sendTokens(request);
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
            if (objLimit.getTokenLimitFlag()) {
                Double week = 0.0, daily = 0.0;
                if (objLimit.getWeeklyAccountLimit() != null && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("NA") && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("unlimited")) {
                    week = Double.parseDouble(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Double.parseDouble(objLimit.getDailyAccountLimit());
                }
                if ((week == 0 || week < 0) && daily > 0) {
                    strLimit = "daily";
                    maxValue = daily;
                } else if ((daily == 0 || daily < 0) && week > 0) {
                    strLimit = "week";
                    maxValue = week;
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    strLimit = "unlimited";
                } else {
                    strLimit = "daily";
                    maxValue = daily;
                }
            }

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

        dialog.setCanceledOnTouchOutside(true);
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
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvvDialog.dismiss();
                    Utils.hideKeypad(PayToMerchantActivity.this);
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        addNoteTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        Utils.hideKeypad(PayToMerchantActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
            prevDialog = new Dialog(PayToMerchantActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.pay_order_preview);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView amountPayTV = prevDialog.findViewById(R.id.amountPayTV);
            TextView userNamePayTV = prevDialog.findViewById(R.id.userNamePayTV);
            TextView tvProcessingFee = prevDialog.findViewById(R.id.tvProcessingFee);
            TextView recipAddreTV = prevDialog.findViewById(R.id.recipAddreTV);
            TextView tvTotal = prevDialog.findViewById(R.id.tvTotal);
            TextView messageNoteTV = prevDialog.findViewById(R.id.messageNoteTV);
            LinearLayout copyRecipientLL = prevDialog.findViewById(R.id.copyRecipientLL);
            LinearLayout lyMessage = prevDialog.findViewById(R.id.lyMessage);
            MotionLayout slideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);
            TextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            userNamePayTV.setText(strUserName);
            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(pfee));
            if (recipientAddress.length() > 13) {
                recipAddreTV.setText(recipientAddress.substring(0, 13) + "...");
            } else {
                recipAddreTV.setText(recipientAddress);
            }
            String enteredAmount = Utils.convertBigDecimalUSDC(payET.getText().toString().replace(",", ""));
            amountPayTV.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
            tvProcessingFee.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " " + getString(R.string.currency));
            total = cynValue + Double.parseDouble(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));

            isAuthenticationCalled = false;
            if (!addNoteTV.getText().toString().trim().equals("")) {
                lyMessage.setVisibility(View.VISIBLE);
                messageNoteTV.setText(addNoteTV.getText().toString());
            } else {
                lyMessage.setVisibility(View.INVISIBLE);
            }
            payTransactionRequest();
            copyRecipientLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(recipientAddress, PayToMerchantActivity.this);
                }
            });
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

                        prevDialog.dismiss();
                        if (!isAuthenticationCalled) {
                            isAuthenticationCalled = true;
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(PayToMerchantActivity.this)) {
                                if (objMyApplication.getBiometric() && ((isTouchId && Utils.isFingerPrint(PayToMerchantActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(PayToMerchantActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    startActivity(new Intent(PayToMerchantActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("screen", "Pay"));
                                }
                            } else {
                                startActivity(new Intent(PayToMerchantActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("screen", "Pay"));
                            }
                        }
                    }
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {

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
}
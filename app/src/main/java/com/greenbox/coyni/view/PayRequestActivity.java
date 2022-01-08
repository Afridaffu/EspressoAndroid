package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.templates.TemplateRequest;
import com.greenbox.coyni.model.templates.TemplateResponse;
import com.greenbox.coyni.model.transactionlimit.LimitResponseData;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;

public class PayRequestActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    MyApplication objMyApplication;
    private TextView keyOne, keyTwo, keyThree, keyFour, keyFive, keySix, keySeven, keyEight, keyNine, keyZero, keyDot, keyActionText, keyPay, keyRquest;
    private ImageView keyBack;
    EditText payRequestET;
    Dialog cvvDialog, prevDialog;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID;
    DashboardViewModel dashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    LinearLayout lyPayClose, lyBalance, payRequestLL, addNoteClickLL;
    ImageView imgProfile, imgConvert;
    TextView profileTitle, tvName, accAddress, tvCurrency, coyniTV, availBal, requestTV, payTV, addNoteTV;
    TransactionLimitResponse objResponse;
    float fontSize, dollarFont;
    WalletInfo cynWallet;
    Boolean isFaceLock = false, isTouchId = false;
    String strAmount = "", strWalletId = "", strLimit = "", strUserName = "", recipientAddress = "";
    Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    Long mLastClickTime = 0L;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private static int FOR_RESULT = 235;
    boolean isAuthenticationCalled = false, isPayClickable = false, isReqClickable = false, isPayClick = false;
    ProgressDialog pDialog;
    int requestedToUserId = 0;
    PaymentMethodsResponse paymentMethodsResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_request);
            initialization();
            initObservers();
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
        if (editable == payRequestET.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    payRequestET.setHint("");
                    convertUSDValue();
                    if (editable.length() > 8) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() > 5) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else {
                        payRequestET.setTextSize(Utils.pixelsToSp(PayRequestActivity.this, fontSize));
                        tvCurrency.setTextSize(Utils.pixelsToSp(PayRequestActivity.this, dollarFont));
                    }
                    if (Double.parseDouble(editable.toString()) > 0) {
                        disableButtons(false);
                    }
                    payRequestET.setSelection(payRequestET.getText().length());
                } else if (editable.toString().equals(".")) {
                    payRequestET.setText("");
                    disableButtons(true);
                } else if (editable.length() == 0) {
                    payRequestET.setHint("0.00");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
                } else {
                    payRequestET.setText("");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
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
                payTransaction();
            }
            break;
            case 0:
                startActivity(new Intent(PayRequestActivity.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("screen", "Pay"));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.keyZeroTV:
                    strAmount += 0;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyOneTV:
                    strAmount += 1;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyTwoTV:
                    strAmount += 2;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyThreeTV:
                    strAmount += 3;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyFourTV:
                    strAmount += 4;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyFiveTV:
                    strAmount += 5;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keySixTV:
                    strAmount += 6;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keySevenTV:
                    strAmount += 7;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyEightTV:
                    strAmount += 8;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyNineTV:
                    strAmount += 9;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyDotTV:
                    if (!strAmount.equals("") && !strAmount.contains(".")) {
                        strAmount += ".";
                        payRequestET.setText(strAmount);
                    }
                    break;
                case R.id.backActionIV:
                    if (strAmount.length() > 0) {
                        strAmount = strAmount.substring(0, strAmount.length() - 1);
                        payRequestET.setText(strAmount);
                    }
                    break;
                case R.id.tvPay:
                    try {
                        if (isPayClickable) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            convertDecimal();
                            if (payValidation()) {
                                if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                                    isPayClick = true;
                                    pDialog = Utils.showProgressDialog(PayRequestActivity.this);
                                    cynValue = Double.parseDouble(payRequestET.getText().toString().trim().replace(",", ""));
                                    calculateFee(Utils.USNumberFormat(cynValue));
                                } else {
                                    Intent i = new Intent(PayRequestActivity.this, BuyTokenPaymentMethodsActivity.class);
                                    i.putExtra("screen", "payRequest");
                                    startActivity(i);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case R.id.tvRequest:
                    try {
                        if (isReqClickable) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            convertDecimal();
                            if (requestValidation()) {
                                requestPreview();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetFaceLock() {
        try {
            isFaceLock = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFaceLock = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetTouchId() {
        try {
            isTouchId = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsTouchID.moveToFirst();
            if (dsTouchID.getCount() > 0) {
                String value = dsTouchID.getString(1);
                if (value.equals("true")) {
                    isTouchId = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouchId = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            payRequestET = findViewById(R.id.payrequestET);
            lyPayClose = findViewById(R.id.lyPayClose);
            imgProfile = findViewById(R.id.imgProfile);
            profileTitle = findViewById(R.id.profileTitle);
            tvName = findViewById(R.id.tvName);
            accAddress = findViewById(R.id.accAddress);
            tvCurrency = findViewById(R.id.tvCurrency);
            coyniTV = findViewById(R.id.coyniTV);
            availBal = findViewById(R.id.availBal);
            requestTV = findViewById(R.id.tvRequest);
            payTV = findViewById(R.id.tvPay);
            addNoteTV = findViewById(R.id.addNoteTV);
            imgConvert = findViewById(R.id.imgConvert);
            lyBalance = findViewById(R.id.lyBalance);
            payRequestLL = findViewById(R.id.payRequestLL);
            addNoteClickLL = findViewById(R.id.addNoteClickLL);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            fontSize = payRequestET.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            availBal.setText(Utils.USNumberFormat(objMyApplication.getGBTBalance()));
            avaBal = objMyApplication.getGBTBalance();
            cynWallet = objMyApplication.getGbtWallet();
            payRequestET.requestFocus();
            payRequestET.setShowSoftInputOnFocus(false);
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strWalletId = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayRequestActivity.this)) {
                    dashboardViewModel.getUserDetail(strWalletId);
                } else {
                    Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this, "", "");
                }
            }

            payRequestET.addTextChangedListener(this);
            if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                payRequestET.setText(getIntent().getStringExtra("amount"));
                USFormat(payRequestET);
                payRequestET.setEnabled(false);
            } else {
                enableButtons();
            }
            payRequestET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(PayRequestActivity.this, v);
                }
            });

            payRequestET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        Utils.hideSoftKeypad(PayRequestActivity.this, view);
                        if (!b) {
                            if (!payRequestET.getText().toString().equals("")) {
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                                payRequestET.setFilters(FilterArray);
                                USFormat(payRequestET);
                            }
                        } else {
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                            payRequestET.setFilters(FilterArray);
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

            payTV.setOnClickListener(this);
            requestTV.setOnClickListener(this);

            addNoteClickLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    displayComments();
                }
            });

            imgConvert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (payRequestET.getText().toString().trim().length() > 0) {
                            convertDecimal();
                            if (coyniTV.getVisibility() == View.GONE) {
                                coyniTV.setVisibility(View.VISIBLE);
                                tvCurrency.setVisibility(View.INVISIBLE);
                                payRequestET.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                            } else {
                                coyniTV.setVisibility(View.GONE);
                                tvCurrency.setVisibility(View.VISIBLE);
                                payRequestET.setGravity(Gravity.CENTER_VERTICAL);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            SetFaceLock();
            SetTouchId();
//            enableButtons();
            calculateFee("10");
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
                    if (!payRequestET.getText().toString().equals("") && !payRequestET.getText().toString().equals("0")) {
                        if (isPayClick) {
                            isPayClick = false;
                            payPreview();
                        }
                    }
                }
            }
        });

        payViewModel.getPayRequestResponseMutableLiveData().observe(this, new Observer<PayRequestResponse>() {
            @Override
            public void onChanged(PayRequestResponse payRequestResponse) {
                try {
                    if (payRequestResponse != null) {
                        objMyApplication.setPayRequestResponse(payRequestResponse);
                        if (payRequestResponse.getStatus().toLowerCase().equals("success")) {
                            startActivity(new Intent(PayRequestActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "success")
                                    .putExtra("subtype", "pay"));

                        } else {
                            startActivity(new Intent(PayRequestActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "failed")
                                    .putExtra("subtype", "pay"));
                        }
                    } else {
                        Utils.displayAlert("something went wrong", PayRequestActivity.this, "", "");
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
                        if (Utils.checkInternet(PayRequestActivity.this)) {
                            UserRequest request = new UserRequest();
                            request.setAmount(Double.parseDouble(payRequestET.getText().toString().replace(",", "")));
                            request.setContent(templateResponse.getData().getInviteBody());
                            request.setPortalType(Utils.portal);
                            request.setRemarks(addNoteTV.getText().toString().trim());
                            request.setRequestedToUserId(requestedToUserId);
                            request.setRequesterWalletId(cynWallet.getWalletId());
                            request.setRequestType(Utils.request);
                            request.setSubject(Utils.requestSub);
                            payViewModel.userRequests(request);
                        } else {
                            Utils.displayAlert(getString(R.string.internet), PayRequestActivity.this, "", "");
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
                    startActivity(new Intent(PayRequestActivity.this, GiftCardBindingLayoutActivity.class)
                            .putExtra("status", "success")
                            .putExtra("subtype", "request"));

                } else {
                    startActivity(new Intent(PayRequestActivity.this, GiftCardBindingLayoutActivity.class)
                            .putExtra("status", "failed")
                            .putExtra("subtype", "request"));
                }
            }
        });
    }

    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, userName, userWalletAddre;
            ImageView userProfile;
            tvName = findViewById(R.id.tvName);
            userName = findViewById(R.id.profileTitle);
            userProfile = findViewById(R.id.imgProfile);
            userWalletAddre = findViewById(R.id.accAddress);
            requestedToUserId = userDetails.getData().getUserId();
            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            strUserName = Utils.capitalize(userDetails.getData().getFullName());
            String imageTextNew = "";
            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            userName.setText(imageTextNew);
            userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                Glide.with(PayRequestActivity.this)
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
            String strPay = payRequestET.getText().toString().trim().replace("\"", "");
//            if ((Double.parseDouble(strPay.replace(",", "")) < cynValidation)) {
//                Utils.displayAlert("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN", PayRequestActivity.this, "", "");
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
//                Utils.displayAlert("Amount entered exceeds available balance", PayRequestActivity.this, "", "");
//                return value = false;
//            } else if (cynValue > avaBal) {
//                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
//                return value = false;
//            }
            if (cynValue > avaBal) {
                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
                value = false;
            }
//            else if (Double.parseDouble(strPay.replace(",", "")) > avaBal) {
//                Utils.displayAlert("Amount entered exceeds available balance", PayRequestActivity.this, "", "");
//                value = false;
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private Boolean requestValidation() {
        Boolean value = true;
        try {
            String strPay = payRequestET.getText().toString().trim().replace("\"", "");
            if ((Double.parseDouble(strPay.replace(",", "")) > Double.parseDouble(getString(R.string.payrequestMaxAmt)))) {
                value = false;
                Utils.displayAlert("You can request up to " + Utils.USNumberFormat(Double.parseDouble(getString(R.string.payrequestMaxAmt))) + " CYN", PayRequestActivity.this, "Oops!", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void disableButtons(Boolean value) {
        try {
            if (value) {
                payRequestLL.setBackgroundResource(R.drawable.payrequest_bgcolor);
                isPayClickable = false;
                isReqClickable = false;
            } else {
                payRequestLL.setBackgroundResource(R.drawable.payrequest_activebg);
                isPayClickable = true;
                isReqClickable = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableButtons() {
        try {
            keyOne = findViewById(R.id.keyOneTV);
            keyOne.setOnClickListener(this);

            keyTwo = findViewById(R.id.keyTwoTV);
            keyTwo.setOnClickListener(this);

            keyThree = findViewById(R.id.keyThreeTV);
            keyThree.setOnClickListener(this);

            keyFour = findViewById(R.id.keyFourTV);
            keyFour.setOnClickListener(this);

            keyFive = findViewById(R.id.keyFiveTV);
            keyFive.setOnClickListener(this);

            keySix = findViewById(R.id.keySixTV);
            keySix.setOnClickListener(this);

            keySeven = findViewById(R.id.keySevenTV);
            keySeven.setOnClickListener(this);

            keyEight = findViewById(R.id.keyEightTV);
            keyEight.setOnClickListener(this);

            keyNine = findViewById(R.id.keyNineTV);
            keyNine.setOnClickListener(this);

            keyZero = findViewById(R.id.keyZeroTV);
            keyZero.setOnClickListener(this);

            keyDot = findViewById(R.id.keyDotTV);
            keyDot.setOnClickListener(this);

            keyPay = findViewById(R.id.tvPay);
            keyPay.setOnClickListener(this);

            keyRquest = findViewById(R.id.tvRequest);
            keyRquest.setOnClickListener(this);
            keyBack = findViewById(R.id.backActionIV);
            keyBack.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            } else {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                payRequestET.setTextSize(Utils.pixelsToSp(PayRequestActivity.this, fontSize));
                tvCurrency.setTextSize(Utils.pixelsToSp(PayRequestActivity.this, dollarFont));
            }
            payRequestET.setFilters(FilterArray);
            payRequestET.setSelection(payRequestET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            payRequestET.setFilters(FilterArray);
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
            if (Utils.checkInternet(PayRequestActivity.this)) {
                buyTokenViewModel.transferFee(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransaction() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(payRequestET.getText().toString().trim().replace(",", ""));
            request.setRemarks(addNoteTV.getText().toString().trim());
            request.setRecipientWalletId(recipientAddress);
            objMyApplication.setTransferPayRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
            if (Utils.checkInternet(PayRequestActivity.this)) {
                payViewModel.sendTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransactionRequest() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(payRequestET.getText().toString().trim().replace(",", ""));
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
            request.setBody2(Utils.convertBigDecimalUSDC(payRequestET.getText().toString().trim().replace(",", "")));
            request.setBody3(recipientAddress);
            payViewModel.getTemplate(Utils.requestId, request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertUSDValue() {
        try {
            usdValue = Double.parseDouble(payRequestET.getText().toString().trim().replace(",", ""));
            cynValue = (usdValue + (usdValue * (feeInPercentage / 100))) + feeInAmount;
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
            etAmount.addTextChangedListener(PayRequestActivity.this);
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
        final Dialog dialog = new Dialog(PayRequestActivity.this);
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
                Intent i = new Intent(PayRequestActivity.this, BuyTokenPaymentMethodsActivity.class);
                i.putExtra("screen", "payRequest");
                startActivity(i);
                finish();
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
            cvvDialog = new Dialog(PayRequestActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.add_note_layout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            EditText addNoteET = cvvDialog.findViewById(R.id.addNoteET);
            CardView doneBtn = cvvDialog.findViewById(R.id.doneBtn);
            TextInputLayout addNoteTIL = cvvDialog.findViewById(R.id.etlMessage);
            LinearLayout cancelBtn = cvvDialog.findViewById(R.id.cancelBtn);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addNoteET.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(addNoteET, InputMethodManager.SHOW_IMPLICIT);

                }
            }, 100);
            addNoteET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 0) {
                        addNoteTIL.setCounterEnabled(false);
                    } else {
                        addNoteTIL.setCounterEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = addNoteET.getText().toString();
                        if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                            addNoteET.setText("");
                            addNoteET.setSelection(addNoteET.getText().length());
                        } else if (str.length() > 0 && str.contains(".")) {
                            addNoteET.setText(addNoteET.getText().toString().replaceAll("\\.", ""));
                            addNoteET.setSelection(addNoteET.getText().length());
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            addNoteET.setText("");
                            addNoteET.setSelection(addNoteET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            if (!addNoteTV.getText().toString().trim().equals("")) {
                addNoteET.setText(addNoteTV.getText().toString().trim());
                addNoteET.setSelection(addNoteET.getText().toString().trim().length());
            }
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
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        addNoteTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
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
            payRequestET.setFilters(FilterArray);
            USFormat(payRequestET);
            payRequestET.setSelection(payRequestET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payPreview() {
        try {
            prevDialog = new Dialog(PayRequestActivity.this);
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
            recipAddreTV.setText(recipientAddress.substring(0, 10) + "...");
            String enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString().replace(",", ""));
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
                    Utils.copyText(recipientAddress, PayRequestActivity.this);
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
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(PayRequestActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(PayRequestActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(PayRequestActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    startActivity(new Intent(PayRequestActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("screen", "Pay"));
                                }
                            } else {
                                startActivity(new Intent(PayRequestActivity.this, PINActivity.class)
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

    private void requestPreview() {
        try {
            prevDialog = new Dialog(PayRequestActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.pay_order_preview);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvHeading = prevDialog.findViewById(R.id.tvHeading);
            TextView amountPayTV = prevDialog.findViewById(R.id.amountPayTV);
            TextView userNamePayTV = prevDialog.findViewById(R.id.userNamePayTV);
            LinearLayout lyProcessing = prevDialog.findViewById(R.id.lyProcessing);
            LinearLayout lyTotal = prevDialog.findViewById(R.id.lyTotal);
            TextView recipAddreTV = prevDialog.findViewById(R.id.recipAddreTV);
            TextView messageNoteTV = prevDialog.findViewById(R.id.messageNoteTV);
            LinearLayout copyRecipientLL = prevDialog.findViewById(R.id.copyRecipientLL);
            LinearLayout lyMessage = prevDialog.findViewById(R.id.lyMessage);
            MotionLayout slideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);
            TextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            userNamePayTV.setText(strUserName);
            tvHeading.setText("Requesting");

            recipAddreTV.setText(recipientAddress.substring(0, 10) + "...");
            String enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString().replace(",", ""));
            amountPayTV.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
            lyProcessing.setVisibility(View.GONE);
            lyTotal.setVisibility(View.GONE);
            isAuthenticationCalled = false;
            if (!addNoteTV.getText().toString().trim().equals("")) {
                lyMessage.setVisibility(View.VISIBLE);
                messageNoteTV.setText(addNoteTV.getText().toString());
            } else {
                lyMessage.setVisibility(View.INVISIBLE);
            }
            copyRecipientLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(recipientAddress, PayRequestActivity.this);
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

                        if (!isAuthenticationCalled) {
                            isAuthenticationCalled = true;
                            requestTransaction();
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
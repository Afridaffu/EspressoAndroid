package com.coyni.mapp.view;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.biometric.BiometricTokenRequest;
import com.coyni.mapp.model.biometric.BiometricTokenResponse;
import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.payrequest.PayRequestResponse;
import com.coyni.mapp.model.payrequest.TransferPayRequest;
import com.coyni.mapp.model.templates.TemplateRequest;
import com.coyni.mapp.model.templates.TemplateResponse;
import com.coyni.mapp.model.transactionlimit.LimitResponseData;
import com.coyni.mapp.model.transactionlimit.TransactionLimitRequest;
import com.coyni.mapp.model.transactionlimit.TransactionLimitResponse;
import com.coyni.mapp.model.transferfee.TransferFeeRequest;
import com.coyni.mapp.model.transferfee.TransferFeeResponse;
import com.coyni.mapp.model.userrequest.UserRequest;
import com.coyni.mapp.model.userrequest.UserRequestResponse;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.utils.CustomeTextView.AnimatedGradientTextView;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MatomoConstants;
import com.coyni.mapp.utils.MatomoUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.keyboards.PayRequestCustomKeyboard;
import com.coyni.mapp.viewmodel.BuyTokenViewModel;
import com.coyni.mapp.viewmodel.CoyniViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.PayViewModel;

public class PayRequestActivity extends BaseActivity implements View.OnClickListener, TextWatcher, OnKeyboardVisibilityListener {
    MyApplication objMyApplication;
    private TextView keyOne, keyTwo, keyThree, keyFour, keyFive, keySix, keySeven, keyEight, keyNine, keyZero, keyDot, keyActionText, keyPay, keyRquest;
    private ImageView keyBack;
    EditText payRequestET, addNoteET;
    Dialog cvvDialog, prevDialog;
    DashboardViewModel dashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    CoyniViewModel coyniViewModel;
    DatabaseHandler dbHandler;
    LinearLayout lyPayClose, lyBalance, payRequestLL, addNoteClickLL;
    ImageView imgProfile, imgConvert;
    TextView profileTitle, tvName, accAddress, tvCurrency, coyniTV, availBal, requestTV, payTV, addNoteTV, tvError;
    TransactionLimitResponse objResponse;
    float fontSize, dollarFont;
    WalletInfo cynWallet;
    Boolean isFaceLock = false, isTouchId = false;
    String strAmount = "", strWalletId = "", strLimit = "", strUserName = "", recipientAddress = "", strToken = "", strCImage = "";
    Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    Long mLastClickTime = 0L;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private static int FOR_RESULT = 235;
    boolean isAuthenticationCalled = false, isPayClickable = false, isReqClickable = false, isPayClick = false;
    Dialog pDialog;
    int requestedToUserId = 0;
    PaymentMethodsResponse paymentMethodsResponse;
    PayRequestCustomKeyboard cKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_pay_request);
            MatomoUtility.getInstance().trackScreen(MatomoConstants.CUSTOMER_PAY_REQUEST_SCREEN);
            initialization();
            initObservers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (start == 0 && after == 0) {
            payRequestET.setTextSize(Utils.pixelsToSp(PayRequestActivity.this, fontSize));
            tvCurrency.setTextSize(Utils.pixelsToSp(PayRequestActivity.this, dollarFont));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null) {

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == payRequestET.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    payRequestET.setHint("");
                    convertUSDValue();
                    if (editable.length() == 5 || editable.length() == 6) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 6, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 12);
                        tvCurrency.setLayoutParams(params1);

                        //tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() == 7 || editable.length() == 8) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 0, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 10);
                        tvCurrency.setLayoutParams(params1);

                        //tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() >= 9) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 6, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 10);
                        tvCurrency.setLayoutParams(params1);

                    } else if (editable.length() <= 4) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 13, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 25);
                        tvCurrency.setLayoutParams(params1);

                    }

                    if (validation()) {
                        disableButtons(false);
                    } else {
                        disableButtons(true);
                    }
                    payRequestET.setSelection(payRequestET.getText().length());
                    payRequestET.setTextDirection(View.TEXT_DIRECTION_LTR);
                } else if (editable.toString().equals(".")) {
                    payRequestET.setText("");
                    disableButtons(true);
                } else if (editable.length() == 0) {
                    payRequestET.setHint("0.00");
                    payRequestET.setTextDirection(View.TEXT_DIRECTION_RTL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    payRequestET.setLayoutParams(lp);
                    LogUtils.d(TAG, "lengthhh zero");
                    LinearLayout.LayoutParams lpImageConvert = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    lpImageConvert.setMargins(15, 38, 0, 0);
                    imgConvert.setLayoutParams(lpImageConvert);
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
                    cKey.clearData();
                    tvError.setVisibility(View.GONE);
                    lyBalance.setVisibility(View.VISIBLE);
                } else {
                    payRequestET.setText("");
                    LogUtils.d(TAG, "lengthhh zeroo");
                    LinearLayout.LayoutParams lpImageConvert = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    lpImageConvert.setMargins(15, 13, 0, 0);
                    imgConvert.setLayoutParams(lpImageConvert);
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
                    pDialog = Utils.showProgressDialog(PayRequestActivity.this);
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
                startActivity(new Intent(PayRequestActivity.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("screen", "Pay"));
                break;
        }
    }

    @Override
    protected void onResume() {
        try {
            if (cvvDialog != null && cvvDialog.isShowing() && addNoteET.hasFocus()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addNoteET.requestFocus();
                        Utils.openKeyPad(PayRequestActivity.this, addNoteET);
                    }
                }, 100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
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
                            if (objMyApplication.getFeatureControlGlobal().getPay() != null && objMyApplication.getFeatureControlGlobal().getPay()
                                    && objMyApplication.getFeatureControlByUser().getPay() != null && objMyApplication.getFeatureControlByUser().getPay()) {
                                MatomoUtility.getInstance().trackEvent(MatomoConstants.CUSTOMER_PAY, MatomoConstants.CUSTOMER_PAY_CLICKED);
                                convertDecimal();
                                if (payValidation()) {
                                    isPayClick = true;
                                    pDialog = Utils.showProgressDialog(PayRequestActivity.this);
                                    cynValue = Utils.doubleParsing(payRequestET.getText().toString().trim().replace(",", ""));
                                    calculateFee(Utils.USNumberFormat(cynValue));
                                }
                            } else {
                                Utils.displayAlert(getString(R.string.errormsg), PayRequestActivity.this, "", "");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case R.id.tvRequest:
                    try {
                        if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                            Utils.displayAlert("You can only Pay", PayRequestActivity.this, "", "");
                        } else {
                            if (isReqClickable) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                if (objMyApplication.getFeatureControlGlobal().getRequest() != null && objMyApplication.getFeatureControlGlobal().getRequest()
                                        && objMyApplication.getFeatureControlByUser().getRequest() != null && objMyApplication.getFeatureControlByUser().getRequest()) {
                                    convertDecimal();
                                    if (requestValidation()) {
                                        if (Utils.doubleParsing(payRequestET.getText().toString().replace(",", "")) > 0) {
                                            MatomoUtility.getInstance().trackEvent(MatomoConstants.CUSTOMER_REQUEST, MatomoConstants.CUSTOMER_REQUEST_CLICKED);
                                            requestPreview();
                                        } else {
                                            disableButtons(true);
                                        }

                                    }
                                } else {
                                    Utils.displayAlert(getString(R.string.errormsg), PayRequestActivity.this, "", "");
                                }
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

    private void initialization() {
        try {
            setKeyboardVisibilityListener(PayRequestActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            payRequestET = findViewById(R.id.payrequestET);
            lyPayClose = findViewById(R.id.lyPayClose);
            imgProfile = findViewById(R.id.imgProfile);
            profileTitle = findViewById(R.id.profileTitle);
            tvName = findViewById(R.id.tvName);
            accAddress = findViewById(R.id.accAddress);
            tvCurrency = findViewById(R.id.tvCurrency);
            tvError = findViewById(R.id.tvError);
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
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            dbHandler = DatabaseHandler.getInstance(PayRequestActivity.this);
            fontSize = payRequestET.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            availBal.setText(Utils.USNumberFormat(objMyApplication.getGBTBalance()));
            avaBal = objMyApplication.getGBTBalance();
            cynWallet = objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0);
            payRequestET.requestFocus();
            payRequestET.setShowSoftInputOnFocus(false);
            // payRequestET.setMovementMethod(null);

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
                cKey = (PayRequestCustomKeyboard) findViewById(R.id.payReqCK);
                InputConnection ic = payRequestET.onCreateInputConnection(new EditorInfo());
                cKey.setInputConnection(ic);
            }

            if (getIntent().getStringExtra("image") != null && !getIntent().getStringExtra("image").equals("")) {
                strCImage = getIntent().getStringExtra("image");
            }

            payRequestET.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void sendAccessibilityEvent(View host, int eventType) {
                    super.sendAccessibilityEvent(host, eventType);
                    if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                        payRequestET.setSelection(payRequestET.getText().toString().length());
                    }
                }
            });

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
                            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
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
                            if (validation()) {
                                disableButtons(false);
                            } else {
                                disableButtons(true);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            objMyApplication.initializeDBHandler(PayRequestActivity.this);
            isFaceLock = objMyApplication.setFaceLock();
            isTouchId = objMyApplication.setTouchId();
            if (isFaceLock || isTouchId) {
                objMyApplication.setLocalBiometric(true);
            } else {
                objMyApplication.setLocalBiometric(false);
            }

            calculateFee("10");
            if (Utils.checkInternet(PayRequestActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.payType));
                obj.setTransactionSubType(Integer.parseInt(Utils.paySubType));
                pDialog = Utils.showProgressDialog(PayRequestActivity.this);
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
                    if (userDetails.getStatus().toLowerCase().equals("success")) {
                        bindUserInfo(userDetails);
                    } else {
                        if (!userDetails.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(userDetails.getError().getErrorDescription(), PayRequestActivity.this, "", userDetails.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(userDetails.getError().getFieldErrors().get(0), PayRequestActivity.this, "", "");
                        }
                    }
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
                try {
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
                    if (transferFeeResponse != null) {
                        objMyApplication.setTransferFeeResponse(transferFeeResponse);
                        feeInAmount = transferFeeResponse.getData().getFeeInAmount();
                        feeInPercentage = transferFeeResponse.getData().getFeeInPercentage();
                        pfee = transferFeeResponse.getData().getFee();
                        if (!payRequestET.getText().toString().equals("") && !payRequestET.getText().toString().equals("0") && Utils.doubleParsing(payRequestET.getText().toString()) > 0) {
                            if (isPayClick) {
                                isPayClick = false;
                                Log.e("payRequestET", payRequestET.getText().toString());
                                payPreview();
                            }
                        } else {
                            disableButtons(true);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
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
//                        Utils.setStrToken("");
                        objMyApplication.clearStrToken();
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
                            request.setAmount(Utils.doubleParsing(payRequestET.getText().toString().replace(",", "")));
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

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                if (biometricTokenResponse != null) {
                    if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                        if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
//                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                            objMyApplication.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        }
                        payTransaction();
                    }
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
//            if (userDetails.getData().getFullName().length() > 20) {
//                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
//            } else {
//                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
//            }

            String strPhContact = "", strEcoSysName = "", strPhone = "", strName = "", strHead = "";
            if (userDetails.getData().getWalletId() != null && userDetails.getData().getFirstName() != null && !userDetails.getData().getFirstName().equals("") && userDetails.getData().getLastName() != null && !userDetails.getData().getLastName().equals("")) {
                String fullName = userDetails.getData().getFirstName() + " " + userDetails.getData().getLastName();
                if (fullName.length() > 20) {
                    strEcoSysName = fullName.substring(0, 20) + "...";
                } else {
                    strEcoSysName = fullName;
                }
                strHead = Utils.capitalize(userDetails.getData().getFirstName().substring(0, 1))
                        + Utils.capitalize(userDetails.getData().getLastName().substring(0, 1));
            } else if (userDetails.getData().getFullName() != null && !userDetails.getData().getFullName().equals("")) {
                if (userDetails.getData().getFullName().length() > 20) {
                    strEcoSysName = userDetails.getData().getFullName().substring(0, 20) + "...";
                } else {
                    strEcoSysName = userDetails.getData().getFullName();
                }
            } else {
                strEcoSysName = "";
            }
            if (getIntent().getStringExtra("phone") != null && !getIntent().getStringExtra("phone").equals("")) {
                strPhone = getIntent().getStringExtra("phone").replace("(1)", "");
            } else {
                strPhone = "";
            }
            if (!strPhone.equals("") && objMyApplication.getObjPhContacts().containsKey(strPhone)) {
                strName = objMyApplication.getObjPhContacts().get(strPhone).getFirstName() + " " + objMyApplication.getObjPhContacts().get(strPhone).getLastName();
//                if (objMyApplication.getObjPhContacts().get(strPhone).getUserName().length() > 24) {
//                    strPhContact = objMyApplication.getObjPhContacts().get(strPhone).getUserName().substring(0, 24) + "...";
//                } else {
//                    strPhContact = objMyApplication.getObjPhContacts().get(strPhone).getUserName();
//                }
                if (strName.length() > 24) {
                    strPhContact = strName.substring(0, 24) + "...";
                } else {
                    strPhContact = strName;
                }
            } else {
                strPhContact = "";
            }
            if (!strPhContact.equals("") && !strEcoSysName.equals("")) {
                tvName.setText(Utils.capitalize(strPhContact));
                userWalletAddre.setText("@" + Utils.capitalize(strEcoSysName));
                if (!strHead.equals(""))
                    userName.setText(strHead);
                else
                    userName.setText(objMyApplication.setNameHead(strEcoSysName));
            } else if (strPhContact.equals("") && !strEcoSysName.equals("")) {
                tvName.setText(Utils.capitalize(strEcoSysName));
                userWalletAddre.setVisibility(View.GONE);
                if (!strHead.equals(""))
                    userName.setText(strHead);
                else
                    userName.setText(objMyApplication.setNameHead(strEcoSysName));
            } else if (!strPhContact.equals("") && strEcoSysName.equals("")) {
                tvName.setText(Utils.capitalize(strPhContact));
                userWalletAddre.setVisibility(View.GONE);
                userName.setText(objMyApplication.setNameHead(strPhContact));
            }


            strUserName = Utils.capitalize(userDetails.getData().getFullName());
//            String imageTextNew = "";
//            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
//                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
//            userName.setText(imageTextNew);
//            if (userDetails.getData().getWalletId().length() > Integer.parseInt(getString(R.string.waddress_length))) {
//                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
//            } else {
//                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
//            }
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
//            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
//                userProfile.setVisibility(View.VISIBLE);
//                userName.setVisibility(View.GONE);
//                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
//                utility.addImage(userDetails.getData().getImage(), userProfile, R.drawable.ic_profilelogo);
//            } else {
//                userProfile.setVisibility(View.GONE);
//                userName.setVisibility(View.VISIBLE);
//            }
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(userDetails.getData().getImage(), userProfile, R.drawable.ic_profilelogo);
            } else if (strCImage.startsWith("content:")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                userProfile.setImageBitmap(objMyApplication.convertImageURIToBitMap(strCImage));
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

    private Boolean validation() {
        Boolean value = true;
        try {
            String strPay = payRequestET.getText().toString().trim().replace("\"", "");
            if (Utils.doubleParsing(strPay.replace(",", "")) == 0.0) {
                tvError.setText("Amount should be greater than zero.");
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if ((Utils.doubleParsing(strPay.replace(",", "")) < Utils.doubleParsing(objResponse.getData().getMinimumLimit()))) {
//                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(Utils.doubleParsing(objResponse.getData().getMinimumLimit())) + " CYN");
                Utils.setErrorSpannableText("Minimum Amount is " + Utils.USNumberFormat(Utils.doubleParsing(objResponse.getData().getMinimumLimit())) + " CYN", PayRequestActivity.this, tvError, 17);

                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if (!strLimit.equals("") && !strLimit.equals("unlimited") && !strLimit.equals("no limit") && Utils.doubleParsing(strPay.replace(",", "")) > Utils.doubleParsing(objResponse.getData().getTransactionLimit())) {
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

    private Boolean payValidation() {
        Boolean value = true;
        try {
            cynValue = Utils.doubleParsing(payRequestET.getText().toString().trim().replace(",", ""));
            if (cynValue > avaBal) {
                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
                value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private Boolean requestValidation() {
        Boolean value = true;
        try {
            changeUSFormat();
            String strPay = payRequestET.getText().toString().trim().replace("\"", "");
            if (strPay.equals("")) {
                Utils.displayAlert("Please enter Amount", PayRequestActivity.this, "Oops!", "");
                return value = false;
            } else if (Utils.doubleParsing(strPay.replace(",", "")) == 0.0) {
                Utils.displayAlert("Amount should be greater than zero.", PayRequestActivity.this, "Oops!", "");
                return value = false;
//            } else if (cynValue > Utils.doubleParsing(objResponse.getData().getTransactionLimit())) {
            } else if (Utils.doubleParsing(strPay.replace(",", "")) > Utils.doubleParsing(objResponse.getData().getTransactionLimit())) {
                Utils.displayAlert("You can request up to " + Utils.USNumberFormat(Utils.doubleParsing(objResponse.getData().getTransactionLimit())) + " CYN", PayRequestActivity.this, "Oops!", "");
                value = false;
            } else if ((Utils.doubleParsing(strPay.replace(",", "")) > Utils.doubleParsing(getString(R.string.payrequestMaxAmt)))) {
                value = false;
                Utils.displayAlert("You can request up to " + Utils.USNumberFormat(Utils.doubleParsing(getString(R.string.payrequestMaxAmt))) + " CYN", PayRequestActivity.this, "Oops!", "");
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
            if (editable.length() == 5 || editable.length() == 6) {
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 6, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 12);
                tvCurrency.setLayoutParams(params1);


            } else if (editable.length() == 7 || editable.length() == 8) {
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 0, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 10);
                tvCurrency.setLayoutParams(params1);

            } else if (editable.length() >= 9) {
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 6, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 10);
                tvCurrency.setLayoutParams(params1);

            } else if (editable.length() <= 4) {
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 13, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 25);
                tvCurrency.setLayoutParams(params1);
            }
            payRequestET.setSelection(payRequestET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
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
            request.setSourceWalletId(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getWalletId());
            objMyApplication.setTransferPayRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
            if (Utils.checkInternet(PayRequestActivity.this)) {
                payViewModel.sendTokens(request, objMyApplication.getStrToken());
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
            request.setSourceWalletId(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getWalletId());
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
            request.setBody2(Utils.convertBigDecimalUSD(payRequestET.getText().toString().trim().replace(",", "")));
            request.setBody3(recipientAddress);
            payViewModel.getTemplate(Utils.requestId, request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertUSDValue() {
        try {
            usdValue = Utils.doubleParsing(payRequestET.getText().toString().trim().replace(",", ""));
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
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            cKey.setEnteredText(etAmount.getText().toString());
            etAmount.addTextChangedListener(PayRequestActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
//                Double week = 0.0, daily = 0.0;
            if (objLimit.getTransactionLimit() != null && !objLimit.getTransactionLimit().toLowerCase().equals("NA") && !objLimit.getTransactionLimit().toLowerCase().equals("unlimited")) {
                maxValue = Utils.doubleParsing(objLimit.getTransactionLimit());
            }
//                if (objLimit.getTransactionLimit() != null && !objLimit.getTransactionLimit().toLowerCase().equals("NA") && !objLimit.getTransactionLimit().toLowerCase().equals("unlimited")) {
//                    daily = Utils.doubleParsing(objLimit.getTransactionLimit());
//                }
            if (maxValue > 0) {
                if (objLimit.getLimitType().equalsIgnoreCase("daily")) {
                    strLimit = "daily";
                } else if (objLimit.getLimitType().equalsIgnoreCase("weekly")) {
                    strLimit = "week";
                } else if (objLimit.getLimitType().equalsIgnoreCase("unlimited") || objLimit.getLimitType().equalsIgnoreCase("no limit")) {
//                    strLimit = "unlimited";
                    strLimit = objLimit.getLimitType().toLowerCase();
                } else {
                    strLimit = "daily";
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
                objMyApplication.setStrScreen("payRequest");
                Intent i = new Intent(PayRequestActivity.this, BuyTokenPaymentMethodsActivity.class);
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
            cvvDialog = new Dialog(PayRequestActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.add_note_layout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            addNoteET = cvvDialog.findViewById(R.id.addNoteET);
            CardView doneBtn = cvvDialog.findViewById(R.id.doneBtn);
            TextInputLayout addNoteTIL = cvvDialog.findViewById(R.id.etlMessage);
            LinearLayout cancelBtn = cvvDialog.findViewById(R.id.cancelBtn);
            addNoteET.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(PayRequestActivity.this);
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
                        //doneBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    } else {
                        addNoteTIL.setCounterEnabled(true);
                        //doneBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
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

            cvvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (!cvvDialog.isShowing()) {
                        if (Utils.isKeyboardVisible) {
                            Utils.hideKeypad(PayRequestActivity.this);
                        }
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvvDialog.dismiss();
                    Utils.isKeyboardVisible = false;
                    Utils.hideKeypad(PayRequestActivity.this);
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        addNoteTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        Utils.isKeyboardVisible = false;
                        Utils.hideKeypad(PayRequestActivity.this);
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
            AnimatedGradientTextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = prevDialog.findViewById(R.id.tv_lable_verify);

            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            userNamePayTV.setText(strUserName);
            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(pfee));
            if (recipientAddress.length() > 13) {
                recipAddreTV.setText(recipientAddress.substring(0, 13) + "...");
            } else {
                recipAddreTV.setText(recipientAddress);
            }
            String enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString().replace(",", ""));
            amountPayTV.setText(Utils.USNumberFormat(Utils.doubleParsing(enteredAmount)));
            tvProcessingFee.setText(Utils.USNumberFormat(Utils.doubleParsing(strPFee)) + " " + getString(R.string.currency));
            total = cynValue + Utils.doubleParsing(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));

            isAuthenticationCalled = false;
            if (!addNoteTV.getText().toString().trim().equals("")) {
                lyMessage.setVisibility(View.VISIBLE);
                messageNoteTV.setText("\"" + addNoteTV.getText().toString() + "\"");
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
//                        tv_lable.setText("Verifying");
//                        tv_lable.setVisibility(View.GONE);
//                        tv_lable_verify.setVisibility(View.VISIBLE);
                        prevDialog.dismiss();
                        if (!isAuthenticationCalled) {
                            tv_lable.setText("Verifying");
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
            AnimatedGradientTextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = prevDialog.findViewById(R.id.tv_lable_verify);

            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            if (strUserName.length() > 20) {
                userNamePayTV.setText(strUserName.substring(0, 20) + "...");
            } else {
                userNamePayTV.setText(strUserName);
            }
            tvHeading.setText("Requesting");

            if (recipientAddress.length() > 13) {
                recipAddreTV.setText(recipientAddress.substring(0, 13) + "...");
            } else {
                recipAddreTV.setText(recipientAddress);
            }
            String enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString().replace(",", ""));
            amountPayTV.setText(Utils.USNumberFormat(Utils.doubleParsing(enteredAmount)));
            lyProcessing.setVisibility(View.GONE);
            lyTotal.setVisibility(View.GONE);
            isAuthenticationCalled = false;
            if (!addNoteTV.getText().toString().trim().equals("")) {
                lyMessage.setVisibility(View.VISIBLE);
                messageNoteTV.setText("\"" + addNoteTV.getText().toString() + "\"");
            } else {
                lyMessage.setVisibility(View.INVISIBLE);
            }
            copyRecipientLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.copyText(recipientAddress, PayRequestActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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

                        if (!isAuthenticationCalled) {
                            isAuthenticationCalled = true;
                            requestTransaction();

//                            tv_lable.setVisibility(View.GONE);
                            tv_lable.setText("Verifying");
//                            tv_lable_verify.setVisibility(View.VISIBLE);
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

    private void changeUSFormat() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            payRequestET.setFilters(FilterArray);
            USFormat(payRequestET);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
        Log.e("isKeyboardVisible", Utils.isKeyboardVisible + "");
    }

}
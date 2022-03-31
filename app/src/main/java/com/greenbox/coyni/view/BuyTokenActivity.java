package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
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
import com.greenbox.coyni.utils.CustomeTextView.AnimatedGradientTextView;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.SelectPaymentMethodActivity;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
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
    DatabaseHandler dbHandler;
    EditText etAmount;
    CustomKeyboard ctKey;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomerProfileViewModel customerProfileViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    DashboardViewModel dashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    CoyniViewModel coyniViewModel;
    Dialog payDialog, prevDialog, cvvDialog;
    TransactionLimitResponse objResponse;
    ProgressDialog pDialog;
    String strLimit = "", strType = "", strBankId = "", strCardId = "", strCvv = "", strSubType = "", strSignOn = "", strToken = "";
    Double maxValue = 0.0, dget = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, usdValidation = 0.0, cynValidation = 0.0;
    SignOnData signOnData;
    float fontSize, dollarFont;
    Boolean isUSD = false, isCYN = false, isBank = false, isFaceLock = false, isTouchId = false, isBuyTokenAPICalled = false, isButtonClick = false;
    public static BuyTokenActivity buyTokenActivity;
    TextInputEditText etCVV;
    Long mLastClickTime = 0L;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID, dsPermanentToken;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;

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
        if (start == 0 && after == 0) {
            etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
            tvCurrency.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, dollarFont));
        }
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

                    if (editable.length() == 5 || editable.length() == 6) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 6, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 12);
                        tvCurrency.setLayoutParams(params1);

                        //tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() == 7 || editable.length() == 8) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 0, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 10);
                        tvCurrency.setLayoutParams(params1);

                        //tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() >= 9) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 6, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 10);
                        tvCurrency.setLayoutParams(params1);
                    }  else if (editable.length() <= 4) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(15, 13, 0, 0);
                        imgConvert.setLayoutParams(params);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 25);
                        tvCurrency.setLayoutParams(params1);
                    }

//                    if (editable.length() > 8) {
//                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
//                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
//                    } else if (editable.length() > 5) {
//                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
//                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
//                    } else {
//                        etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
//                        tvCurrency.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, dollarFont));
//                    }

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
            super.onActivityResult(requestCode, resultCode, data);
//            switch (resultCode) {
//                case 1:
//                    if (data == null) {
//                        if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
//                            Utils.displayAlert("Bank integration has been cancelled", BuyTokenActivity.this, "", "");
//                        } else {
//                            pDialog = Utils.showProgressDialog(this);
//                            customerProfileViewModel.meSyncAccount();
//                        }
//                    }
//                    break;
//                case RESULT_OK:
//                case 235: {
//                    try {
//                        //buyToken();
//                        pDialog = Utils.showProgressDialog(BuyTokenActivity.this);
//                        BiometricTokenRequest request = new BiometricTokenRequest();
//                        request.setDeviceId(Utils.getDeviceID());
////                        request.setMobileToken(strToken);
//                        request.setMobileToken(objMyApplication.getStrMobileToken());
//                        request.setActionType(Utils.buyActionType);
//                        coyniViewModel.biometricToken(request);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                break;
//                case 0:
//                    if (requestCode != 3 && requestCode != 1) {
//                        startActivity(new Intent(BuyTokenActivity.this, PINActivity.class)
//                                .putExtra("TYPE", "ENTER")
//                                .putExtra("screen", "buy")
//                                .putExtra("cynValue", String.valueOf(cynValue)));
//                    }
//                    break;
//            }

            switch (resultCode) {
                case RESULT_OK:
                    try {
                        if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                            pDialog = Utils.showProgressDialog(BuyTokenActivity.this);
                            BiometricTokenRequest request = new BiometricTokenRequest();
                            request.setDeviceId(Utils.getDeviceID());
                            request.setMobileToken(objMyApplication.getStrMobileToken());
                            request.setActionType(Utils.buyActionType);
                            coyniViewModel.biometricToken(request);
                        } else if (data == null && requestCode == 1) {
                            if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                                Utils.displayAlert("Bank integration has been cancelled", BuyTokenActivity.this, "", "");
                            } else {
                                pDialog = Utils.showProgressDialog(this);
                                customerProfileViewModel.meSyncAccount();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 0:
                    if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                        startActivity(new Intent(BuyTokenActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "buy")
                                .putExtra("cynValue", String.valueOf(cynValue)));
                    }
                    break;
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
            dbHandler = DatabaseHandler.getInstance(BuyTokenActivity.this);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
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
            ctKey.setKeyAction("Buy\nToken", this);
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
//            SetFaceLock();
//            SetTouchId();
            setFaceLock();
            setTouchId();
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
                            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
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
                        rollbackSelectedCard();
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
//                                        tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                                        setSpannableText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN",BuyTokenActivity.this,tvError,17);
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
//                                        tvError.setText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD");
                                        setSpannableText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD",BuyTokenActivity.this,tvError,17);

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
            pDialog = Utils.showProgressDialog(this);
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
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
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
                } else {
                    if (!transactionLimitResponse.getError().getErrorDescription().equals("")) {
                        Utils.displayAlert(transactionLimitResponse.getError().getErrorDescription(), BuyTokenActivity.this, "", transactionLimitResponse.getError().getFieldErrors().get(0));
                    } else {
                        Utils.displayAlert(transactionLimitResponse.getError().getFieldErrors().get(0), BuyTokenActivity.this, "", "");
                    }
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
                        if (isButtonClick && !etAmount.getText().toString().equals("") && !etAmount.getText().toString().equals("0")) {
                            isButtonClick = false;
                            Double pay = Double.parseDouble(etAmount.getText().toString().replace(",", ""));
                            pfee = transferFeeResponse.getData().getFee();
                            dget = pay - pfee;
                            buyTokenPreview();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
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
                pDialog.dismiss();
                if (buyTokenResponse != null) {
                    if (buyTokenResponse.getStatus().trim().toLowerCase().equals("success")) {
                        buyTokenInProgress(buyTokenResponse.getData());
                    } else {
                        if (!buyTokenResponse.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(buyTokenResponse.getError().getErrorDescription(), BuyTokenActivity.this, "", buyTokenResponse.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(buyTokenResponse.getError().getFieldErrors().get(0), BuyTokenActivity.this, "", "");
                        }
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
                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                        PaymentMethodsResponse objResponse = objMyApplication.filterPaymentMethods(payMethodsResponse);
                        objMyApplication.setPaymentMethodsResponse(objResponse);
                        paymentMethodsResponse = objResponse;
                    } else {
                        objMyApplication.setPaymentMethodsResponse(payMethodsResponse);
                        paymentMethodsResponse = payMethodsResponse;
                    }
                    if (objMyApplication.getSelectedCard() != null) {
//                        selectedCard = objMyApplication.getSelectedCard();
                        bindPayMethod(rollbackSelectedCard());
                    }
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
                        buyToken();
                    } else {
                        if (!biometricTokenResponse.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(biometricTokenResponse.getError().getErrorDescription(), BuyTokenActivity.this, "", biometricTokenResponse.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(biometricTokenResponse.getError().getFieldErrors().get(0), BuyTokenActivity.this, "", "");
                        }
                    }
                }
            }
        });
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
                //buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                } else {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                }
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
                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                    paymentMethodsResponse = objMyApplication.businessPaymentMethods(paymentMethodsResponse);
                }
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
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            Intent i = new Intent(BuyTokenActivity.this, BuyTokenPaymentMethodsActivity.class);
                            i.putExtra("screen", "buytoken");
                            startActivityForResult(i, 3);
                        } else {
                            Intent i = new Intent(BuyTokenActivity.this, SelectPaymentMethodActivity.class);
                            i.putExtra("screen", "buytoken");
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
//                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                setSpannableText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN",BuyTokenActivity.this,tvError,17);

                tvError.setVisibility(View.VISIBLE);
                return value = false;
            } else if (tvCYN.getVisibility() == View.GONE && Double.parseDouble(strPay.replace(",", "")) < usdValidation) {
//                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD");
                setSpannableText("Minimum Amount is " + Utils.USNumberFormat(usdValidation) + " USD",BuyTokenActivity.this,tvError,17);

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
            AnimatedGradientTextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = prevDialog.findViewById(R.id.tv_lable_verify);
            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);

            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(pfee));
            tvGet.setText(Utils.USNumberFormat(cynValue));
            tvPurchaseAmt.setText(Utils.USNumberFormat(cynValue) + " USD");
            tvProcessingFee.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " USD");
            total = cynValue + Double.parseDouble(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " USD");
            prepareBuyRequest();
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
//                        tv_lable.setVisibility(View.GONE);
//                        tv_lable.setText("Verifying");
//                        tv_lable_verify.setVisibility(View.VISIBLE);
                        if (!isBuyTokenAPICalled) {
                            //buyToken();
                            tv_lable.setText("Verifying");
                            isBuyTokenAPICalled = true;
                            prevDialog.dismiss();
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BuyTokenActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(BuyTokenActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(BuyTokenActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    startActivity(new Intent(BuyTokenActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("cynValue", String.valueOf(cynValue))
                                            .putExtra("screen", "buy"));
                                }
                            } else {
                                startActivity(new Intent(BuyTokenActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("cynValue", String.valueOf(cynValue))
                                        .putExtra("screen", "buy"));
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
            isButtonClick = true;
            calculateFee(Utils.USNumberFormat(cynValue));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareBuyRequest() {
        try {
            BuyTokenRequest request = new BuyTokenRequest();
            request.setBankId(strBankId);
            request.setCardId(strCardId);
            request.setCvc(strCvv);
            request.setTokens(Utils.convertBigDecimalUSDC(String.valueOf(total)));
            request.setTxnSubType(strSubType);
            objMyApplication.setBuyRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyToken() {
        try {
//            isBuyTokenAPICalled = true;
//            BuyTokenRequest request = new BuyTokenRequest();
//            request.setBankId(strBankId);
//            request.setCardId(strCardId);
//            request.setCvc(strCvv);
//            request.setTokens(Utils.convertBigDecimalUSDC(String.valueOf(total)));
//            request.setTxnSubType(strSubType);
            if (Utils.checkInternet(BuyTokenActivity.this)) {
                buyTokenViewModel.buyTokens(objMyApplication.getBuyRequest());
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

            TextView tvAmount = prevDialog.findViewById(R.id.tvBAmount);
            TextView tvMessage = prevDialog.findViewById(R.id.tvBMessage);
            TextView tvReferenceID = prevDialog.findViewById(R.id.tvBReferenceID);
            TextView tvBalance = prevDialog.findViewById(R.id.tvBBalance);
            TextView tvLearnMore = prevDialog.findViewById(R.id.tvBLearnMore);
            TextView tvHeading = prevDialog.findViewById(R.id.tvBHeading);
            LinearLayout layoutReference = prevDialog.findViewById(R.id.layoutBReference);
            ImageView imgLogo = prevDialog.findViewById(R.id.imgBLogo);
            ImageView imgRefCopy = prevDialog.findViewById(R.id.imgBRefCopy);
            CardView cvDone = prevDialog.findViewById(R.id.cvBDone);
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
//                    Intent i = new Intent(BuyTokenActivity.this, DashboardActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(i);
                    try {
                        Intent i;
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            i = new Intent(BuyTokenActivity.this, DashboardActivity.class);
                        } else {
                            i = new Intent(BuyTokenActivity.this, BusinessDashboardActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            tvReferenceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getGbxTransactionId(), BuyTokenActivity.this);
                }
            });

            imgRefCopy.setOnClickListener(new View.OnClickListener() {
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
            ctKey.setKeyAction("OK", this);
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
//                        rollbackSelectedCard();
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

            if (editable.length() == 5 || editable.length() == 6) {
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 6, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 12);
                tvCurrency.setLayoutParams(params1);

            } else if (editable.length() == 7 || editable.length() == 8) {
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 0, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 10);
                tvCurrency.setLayoutParams(params1);

            } else if (editable.length() >= 9) {
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 6, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 10);
                tvCurrency.setLayoutParams(params1);

            } else if (editable.length() <= 4) {
                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 13, 0, 0);
                imgConvert.setLayoutParams(params);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 25);
                tvCurrency.setLayoutParams(params1);

            }

//            if (editable.length() > 12) {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
//                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
//                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
//            } else if (editable.length() > 8) {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
//                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
//                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
//            } else if (editable.length() > 5) {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
//                etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
//                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
//            } else {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
//                etAmount.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, fontSize));
//                tvCurrency.setTextSize(Utils.pixelsToSp(BuyTokenActivity.this, dollarFont));
//            }
            etAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
            etAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public PaymentsList rollbackSelectedCard() {
        if (objMyApplication.getSelectedCard().getPaymentMethod().toLowerCase().equals("bank")) {
            if (objMyApplication.getSelectedCard().getRelink()) {
                selectedCard = objMyApplication.getPrevSelectedCard();
                objMyApplication.setSelectedCard(selectedCard);
            } else {
                selectedCard = objMyApplication.getSelectedCard();
            }
        } else {
            if (objMyApplication.getSelectedCard().getExpired()) {
                selectedCard = objMyApplication.getPrevSelectedCard();
                objMyApplication.setSelectedCard(selectedCard);
            } else {
                selectedCard = objMyApplication.getSelectedCard();
            }
        }

        return selectedCard;
    }

    public static void setSpannableText(String text, Context context, TextView spannableTV, int start) {

        SpannableString ss = new SpannableString(text);

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ss.setSpan(new RelativeSizeSpan(1f), start, ss.length(), 0);
        ss.setSpan(bss, start, ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(context.getColor(R.color.error_red)), start, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableTV.setText(ss);
        spannableTV.setMovementMethod(LinkMovementMethod.getInstance());
        spannableTV.setHighlightColor(Color.TRANSPARENT);
    }
    private void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

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
}
package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.model.transactionlimit.LimitResponseData;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.model.withdraw.WithdrawResponseData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.business.SelectPaymentMethodActivity;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.util.ArrayList;
import java.util.List;

public class WithdrawTokenActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    PaymentsList selectedCard, prevSelectedCard;
    ImageView imgBankIcon, imgArrow, imgConvert;
    TextView tvLimit, tvPayHead, tvAccNumber, tvCurrency, tvBankName, tvBAccNumber, tvError, tvCYN, etRemarks, tvAvailableBal;
    RelativeLayout lyPayMethod;
    LinearLayout lyCDetails, lyWithdrawClose, lyBDetails, lyBalance;
    EditText etAmount, addNoteET;
    CustomKeyboard ctKey;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomerProfileViewModel customerProfileViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    BuyTokenViewModel buyTokenViewModel;
    DashboardViewModel dashboardViewModel;
    CoyniViewModel coyniViewModel;
    Dialog payDialog, prevDialog, cvvDialog;
    TransactionLimitResponse objResponse;
    ProgressDialog pDialog;
    String strLimit = "", strType = "", strBankId = "", strCardId = "", strSubType = "", strSignOn = "";
    Double maxValue = 0.0, dget = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    SignOnData signOnData;
    float fontSize, dollarFont;
    public static WithdrawTokenActivity withdrawTokenActivity;
    Long mLastClickTime = 0L, bankId, cardId;
    Boolean isUSD = false, isCYN = false, isBank = false;
    Boolean isFaceLock = false, isTouchId = false;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private static int FOR_RESULT = 235;
    boolean isAuthenticationCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_withdraw_token);
            withdrawTokenActivity = this;
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            dashboardViewModel.mePaymentMethods();
            if (cvvDialog != null && addNoteET.hasFocus()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addNoteET.requestFocus();
                        Utils.openKeyPad(WithdrawTokenActivity.this, addNoteET);

                    }
                }, 100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                if (data == null) {
                    if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                        Utils.displayAlert("Bank integration has been cancelled", WithdrawTokenActivity.this, "", "");
                    } else {
                        pDialog = Utils.showProgressDialog(this);
                        customerProfileViewModel.meSyncAccount();
                    }
                }
                break;
            case RESULT_OK:
            case 235: {
                try {
                    //withdrawToken();
                    pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
                    BiometricTokenRequest request = new BiometricTokenRequest();
                    request.setDeviceId(Utils.getDeviceID());
                    request.setMobileToken(objMyApplication.getStrMobileToken());
                    request.setActionType(Utils.withdrawActionType);
                    coyniViewModel.biometricToken(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 0:
                startActivity(new Intent(WithdrawTokenActivity.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase())
                        .putExtra("screen", "Withdraw"));
                break;
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
                    etAmount.setHint("");
                    isCYN = false;
                    isUSD = true;
                    convertUSDValue();

                    if (editable.length() > 8) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() > 5) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else {
                        etAmount.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, fontSize));
                        tvCurrency.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, dollarFont));
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
                    lyBalance.setVisibility(View.VISIBLE);
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    ctKey.disableButton();
                    tvError.setVisibility(View.GONE);
                    ctKey.clearData();
                    setDefaultLength();
                } else {
                    etAmount.setText("");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
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
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            imgBankIcon = findViewById(R.id.imgBankIcon);
            imgArrow = findViewById(R.id.imgArrow);
            imgConvert = findViewById(R.id.imgConvert);
            etRemarks = findViewById(R.id.etRemarks);
            tvLimit = findViewById(R.id.tvLimit);
            tvPayHead = findViewById(R.id.tvPayHead);
            tvBankName = findViewById(R.id.tvBankName);
            tvAccNumber = findViewById(R.id.tvAccNumber);
            tvBAccNumber = findViewById(R.id.tvBAccNumber);
            tvError = findViewById(R.id.tvError);
            tvCYN = findViewById(R.id.tvCYN);
            tvCurrency = findViewById(R.id.tvCurrency);
            lyPayMethod = findViewById(R.id.lyPayMethod);
            lyWithdrawClose = findViewById(R.id.lyWithdrawClose);
            etAmount = findViewById(R.id.etAmount);
            lyCDetails = findViewById(R.id.lyCDetails);
            lyBDetails = findViewById(R.id.lyBDetails);
            lyBalance = findViewById(R.id.lyBalance);
            tvAvailableBal = findViewById(R.id.tvAvailableBal);
            ctKey = (CustomKeyboard) findViewById(R.id.ckb);
            ctKey.setKeyAction("Withdraw", this);
            ctKey.setScreenName("withdraw");
            InputConnection ic = etAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            fontSize = etAmount.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            etAmount.requestFocus();
            etAmount.setShowSoftInputOnFocus(false);
            avaBal = objMyApplication.getGBTBalance();
            tvAvailableBal.setText(Utils.USNumberFormat(objMyApplication.getGBTBalance()));
            SetFaceLock();
            SetTouchId();
            pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
            bindPayMethod(selectedCard);
            etAmount.addTextChangedListener(this);
            etAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(WithdrawTokenActivity.this, v);
                }
            });

            etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        Utils.hideSoftKeypad(WithdrawTokenActivity.this, view);
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

            lyWithdrawClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            etRemarks.setOnClickListener(new View.OnClickListener() {
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
                    enableButton();
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
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;
                    setDailyWeekLimit(transactionLimitResponse.getData());
                    if (etAmount.getText().toString().trim().length() > 0) {
                        if (validation()) {
                            ctKey.enableButton();
                        } else {
                            ctKey.disableButton();
                        }
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
                    if (!etAmount.getText().toString().equals("") && !etAmount.getText().toString().equals("0") && Double.parseDouble(etAmount.getText().toString()) > 0) {
                        Double pay = Double.parseDouble(etAmount.getText().toString().replace(",", ""));
                        pfee = transferFeeResponse.getData().getFee();
                        dget = pay - pfee;
                        withdrawTokenPreview();
                        ctKey.enableButton();
                    } else {
                        ctKey.disableButton();
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
                            Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawTokenActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), WithdrawTokenActivity.this, "", "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getWithdrawResponseMutableLiveData().observe(this, new Observer<WithdrawResponse>() {
            @Override
            public void onChanged(WithdrawResponse withdrawResponse) {
                if (prevDialog != null) {
                    prevDialog.dismiss();
                }
                pDialog.dismiss();
                Utils.setStrToken("");
                if (withdrawResponse != null) {
                    objMyApplication.setWithdrawResponse(withdrawResponse);
                    if (withdrawResponse.getStatus().trim().toLowerCase().equals("success")) {
                        //withdrawTokenInProgress(withdrawResponse.getData());
                        startActivity(new Intent(WithdrawTokenActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "inprogress")
                                .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase()));
                    } else {
                        startActivity(new Intent(WithdrawTokenActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "failed")
                                .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase()));
                        finish();
                    }
                } else {
                    Utils.displayAlert("something went wrong", WithdrawTokenActivity.this, "", "");
                }
            }
        });

        buyTokenViewModel.getWithdrawFailureResponseMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError withdrawResponse) {
                Utils.setStrToken("");
                if (withdrawResponse != null) {
//                    withdrawTokenFailure(withdrawResponse);
                    startActivity(new Intent(WithdrawTokenActivity.this, GiftCardBindingLayoutActivity.class)
                            .putExtra("status", "failed")
                            .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase()));
                    finish();
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
                                objMyApplication.callResolveFlow(WithdrawTokenActivity.this, strSignOn, signOnData);
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

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                if (biometricTokenResponse != null) {
                    if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                        if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        }
                        withdrawToken();
                    }
                }
            }
        });
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

    private void selectPayMethod() {
        try {
            payDialog = new Dialog(WithdrawTokenActivity.this);
            payDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            payDialog.setContentView(R.layout.choosepaymentmethod);
            payDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            RecyclerView rvSelPayMethods = payDialog.findViewById(R.id.rvSelPayMethods);
            LinearLayout lyAddPay = payDialog.findViewById(R.id.lyAddPay);
            SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
            prevSelectedCard = objMyApplication.getSelectedCard();
            List<PaymentsList> listPayments = new ArrayList<>();
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                for (int i = 0; i < paymentMethodsResponse.getData().getData().size(); i++) {
                    if (!paymentMethodsResponse.getData().getData().get(i).getPaymentMethod().toLowerCase().equals("credit")) {
                        listPayments.add(paymentMethodsResponse.getData().getData().get(i));
                    }
                }
                if (listPayments != null && listPayments.size() > 0) {
                    selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(listPayments, WithdrawTokenActivity.this, "wdrawtoken");
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(WithdrawTokenActivity.this);
                    rvSelPayMethods.setLayoutManager(mLayoutManager);
                    rvSelPayMethods.setItemAnimator(new DefaultItemAnimator());
                    rvSelPayMethods.setAdapter(selectedPaymentMethodsAdapter);
                }
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
                        try {
                            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                                Intent i = new Intent(WithdrawTokenActivity.this, BuyTokenPaymentMethodsActivity.class);
                                i.putExtra("screen", "withdraw");
                                startActivity(i);
                            } else {
                                Intent i = new Intent(WithdrawTokenActivity.this, SelectPaymentMethodActivity.class);
                                i.putExtra("screen", "withdraw");
                                startActivity(i);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
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
            request.setTxnType(Utils.withdrawType);
            if (strType.toLowerCase().equals("debit")) {
                request.setTxnSubType(Utils.instantType);
            } else if (strType.toLowerCase().equals("bank")) {
                request.setTxnSubType(Utils.bankType);
            }
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                buyTokenViewModel.transferFee(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindPayMethod(PaymentsList objData) {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            selectedCard = objData;
            TransactionLimitRequest obj = new TransactionLimitRequest();
            obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
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
                    strSubType = Utils.instantType;
                    obj.setTransactionSubType(Integer.parseInt(Utils.instantType));
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
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
//                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                } else {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindSelectedBank(PaymentsList objData) {
        try {
            prevSelectedCard = null;
            bindPayMethod(objData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            cynValidation = Double.parseDouble(objResponse.getData().getMinimumLimit());
            String strPay = Utils.convertBigDecimalUSDC((etAmount.getText().toString().trim().replace("\"", "")));
            if ((Double.parseDouble(strPay.replace(",", "")) < cynValidation)) {
                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if (Double.parseDouble(strPay.replace(",", "")) <= 0) {
                value = false;
            } else if (objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(strPay.replace(",", "")) > maxValue) {
                if (strLimit.equals("daily")) {
                    tvError.setText("Amount entered exceeds your daily limit");
                } else if (strLimit.equals("week")) {
                    tvError.setText("Amount entered exceeds your weekly limit");
                }
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if (Double.parseDouble(strPay.replace(",", "")) > avaBal) {
                tvError.setText("Amount entered exceeds available balance");
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if (cynValue > avaBal) {
                tvError.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");
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

    public void withdrawTokenPreview() {
        try {
            prevDialog = new Dialog(WithdrawTokenActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.buy_token_order_review);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvGet = prevDialog.findViewById(R.id.tvGet);
            TextView tvBankName = prevDialog.findViewById(R.id.tvBankName);
            TextView tvAccount = prevDialog.findViewById(R.id.tvAccount);
            TextView tvCYN = prevDialog.findViewById(R.id.tvCYN);
            TextView tvPaymentHead = prevDialog.findViewById(R.id.tvPaymentHead);
            TextView tvPurchaseHead = prevDialog.findViewById(R.id.tvPurchaseHead);
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
            tvPaymentHead.setText("Withdraw to");
            tvPurchaseHead.setText("Withdraw Amount");
            tvCYN.setVisibility(View.GONE);
            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(pfee));
            tvGet.setText("$ " + Utils.USNumberFormat(cynValue));
            tvPurchaseAmt.setText(Utils.USNumberFormat(cynValue) + " " + getString(R.string.currency));
            tvProcessingFee.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " " + getString(R.string.currency));
            total = cynValue + Double.parseDouble(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
            isAuthenticationCalled = false;
            createWithdrawRequest();

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

                        if (!isAuthenticationCalled) {
                            isAuthenticationCalled = true;
                            prevDialog.dismiss();
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(WithdrawTokenActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(WithdrawTokenActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(WithdrawTokenActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    startActivity(new Intent(WithdrawTokenActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase())
                                            .putExtra("screen", "Withdraw"));
                                }
                            } else {
                                startActivity(new Intent(WithdrawTokenActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase())
                                        .putExtra("screen", "Withdraw"));
                            }
                        }

                    }

                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
//                    if (currentId == motionLayout.getEndState()) {
//                        try {
//                            slideToConfirm.setInteractionEnabled(false);
//                            tv_lable.setText("Verifying");
//                            //withdrawToken();
//                            prevDialog.dismiss();
//                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(WithdrawTokenActivity.this)) {
//                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(WithdrawTokenActivity.this)) || (isFaceLock))) {
//                                    Utils.checkAuthentication(WithdrawTokenActivity.this, CODE_AUTHENTICATION_VERIFICATION);
//                                } else {
//                                    startActivityForResult(new Intent(WithdrawTokenActivity.this, PINActivity.class)
//                                            .putExtra("TYPE", "ENTER")
//                                            .putExtra("screen", "GiftCard"), FOR_RESULT);
//                                }
//                            } else {
//                                startActivityForResult(new Intent(WithdrawTokenActivity.this, PINActivity.class)
//                                        .putExtra("TYPE", "ENTER")
//                                        .putExtra("screen", "GiftCard"), FOR_RESULT);
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
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

    public void expiry() {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            final Dialog dialog = new Dialog(WithdrawTokenActivity.this);
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
                            Intent i = new Intent(WithdrawTokenActivity.this, EditCardActivity.class);
                            i.putExtra("screen", "withdraw");
                            startActivity(i);
                        } else {
                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                isBank = true;
                                objMyApplication.setResolveUrl(true);
                                Intent i = new Intent(WithdrawTokenActivity.this, WebViewActivity.class);
                                i.putExtra("signon", signOnData);
                                startActivityForResult(i, 1);
                            } else {
                                Utils.displayAlert(strSignOn, WithdrawTokenActivity.this, "", "");
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
            final Dialog dialog = new Dialog(WithdrawTokenActivity.this);
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
                    pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
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

    public void bindSelectedCard(PaymentsList objData) {
        try {
            prevSelectedCard = null;
            bindPayMethod(objData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(WithdrawTokenActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(WithdrawTokenActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
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
                etAmount.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, fontSize));
                tvCurrency.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, dollarFont));
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

    private void withdrawToken() {
        try {
            pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                buyTokenViewModel.withdrawTokens(objMyApplication.getWithdrawRequest());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayComments() {
        try {
            cvvDialog = new Dialog(WithdrawTokenActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.add_note_layout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            addNoteET = cvvDialog.findViewById(R.id.addNoteET);
            CardView doneBtn = cvvDialog.findViewById(R.id.doneBtn);
            TextInputLayout addNoteTIL = cvvDialog.findViewById(R.id.etlMessage);
            LinearLayout cancelBtn = cvvDialog.findViewById(R.id.cancelBtn);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addNoteET.requestFocus();
                    Utils.openKeyPad(WithdrawTokenActivity.this, addNoteET);
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
            if (!etRemarks.getText().toString().trim().equals("")) {
                addNoteET.setText(etRemarks.getText().toString().trim());
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
                    Utils.hideKeypad(WithdrawTokenActivity.this);
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        etRemarks.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        Utils.hideKeypad(WithdrawTokenActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void withdrawTokenClick() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            cynValue = Double.parseDouble(etAmount.getText().toString().trim());
            mLastClickTime = SystemClock.elapsedRealtime();
            convertUSDtoCYN();
            calculateFee(Utils.USNumberFormat(cynValue));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertUSDValue() {
        try {
            if (isUSD) {
                isUSD = false;
                usdValue = Double.parseDouble(etAmount.getText().toString().trim().replace(",", ""));
                cynValue = (usdValue + (usdValue * (feeInPercentage / 100))) + feeInAmount;
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
                etAmount.removeTextChangedListener(WithdrawTokenActivity.this);
//                etAmount.setText(String.valueOf(cynValue));
                etAmount.setText(Utils.convertBigDecimalUSDC(String.valueOf(cynValue)));
                etAmount.addTextChangedListener(WithdrawTokenActivity.this);
                USFormat(etAmount);
                etAmount.setSelection(etAmount.getText().length());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertDecimal() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            etAmount.setFilters(FilterArray);
            USFormat(etAmount);
            etAmount.setSelection(etAmount.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawTokenInProgress(WithdrawResponseData objData) {
        try {
            prevDialog = new Dialog(WithdrawTokenActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.withdraw_token_trans_inprogress);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvAmount = prevDialog.findViewById(R.id.tvAmount);
            TextView tvMessage = prevDialog.findViewById(R.id.tvWDMessage);
            TextView tvReferenceID = prevDialog.findViewById(R.id.tvReferenceID);
            TextView tvBalance = prevDialog.findViewById(R.id.tvBalance);
//            TextView tvLearnMore = prevDialog.findViewById(R.id.tvLearnMore);
            TextView tvHeading = prevDialog.findViewById(R.id.tvHeading);
            TextView tvDescription = prevDialog.findViewById(R.id.tvDescription);
            LinearLayout layoutReference = prevDialog.findViewById(R.id.layoutReference);
            ImageView imgLogo = prevDialog.findViewById(R.id.imgLogo);
            CardView cvDone = prevDialog.findViewById(R.id.cvDone);
            if (objData.getGbxTransactionId().length() > 10) {
                tvReferenceID.setText(objData.getGbxTransactionId().substring(0, 10) + "...");
            } else {
                tvReferenceID.setText(objData.getGbxTransactionId());
            }
            String strMessage = "";
            if (!strBankId.equals("")) {
                strMessage = "We are processing  your request, please allow a 3-5 business days for your coyni bank withdrawal to be reflected in your bank account. Learn More";
            }
            if (!strCardId.equals("")) {
                strMessage = "We are processing your request, please allow a few minutes for your coyni instant withdrawal to be reflected in your bank account. Learn More";
            }
            SpannableString ss = new SpannableString(strMessage);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.populateLearnMore(WithdrawTokenActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#00a6a2"));
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#00a6a2")), strMessage.indexOf("Learn More"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new UnderlineSpan(), strMessage.indexOf("Learn More"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(clickableSpan, strMessage.indexOf("Learn More"), strMessage.length(), 0);

            tvDescription.setText(ss);

            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            tvHeading.setText("Transaction in Progress");
            imgLogo.setImageResource(R.drawable.ic_in_progress_icon);
            Double bal = cynValue + objMyApplication.getGBTBalance();
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
            tvAmount.setText("$ " + Utils.USNumberFormat(cynValue));
//            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as " + objData.getDescriptorName() + ".");
            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as Coyni.");
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
                    Intent i = new Intent(WithdrawTokenActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            layoutReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getGbxTransactionId(), WithdrawTokenActivity.this);
                }
            });
//            tvLearnMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Utils.populateLearnMore(WithdrawTokenActivity.this);
//                }
//            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawTokenFailure(APIError objData) {
        try {
            prevDialog = new Dialog(WithdrawTokenActivity.this);
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
                    onBackPressed();
                    finish();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private WithdrawRequest createWithdrawRequest() {
        WithdrawRequest request = new WithdrawRequest();
        if (!strBankId.equals("")) {
            bankId = Long.parseLong(strBankId);
        }
        if (!strCardId.equals("")) {
            cardId = Long.parseLong(strCardId);
        }
        request.setBankId(bankId);
        request.setCardId(cardId);
        request.setGiftCardWithDrawInfo(null);
        request.setTokens(cynValue);
        request.setRemarks(etRemarks.getText().toString().trim());
        request.setWithdrawType(strSubType);
        objMyApplication.setWithdrawRequest(request);
        objMyApplication.setWithdrawAmount(cynValue);

        return request;
    }

    private void enableButton() {
        try {
            if (etAmount.getText().toString().trim().length() > 0) {
                convertDecimal();
                if (validation()) {
                    ctKey.enableButton();
                } else {
                    ctKey.disableButton();
                }
                if (tvCYN.getVisibility() == View.GONE) {
                    tvCYN.setVisibility(View.VISIBLE);
                    tvCurrency.setVisibility(View.INVISIBLE);
                    etAmount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    if (tvError.getVisibility() == View.VISIBLE) {
                        lyBalance.setVisibility(View.GONE);
                        if (tvError.getText().toString().trim().contains("Minimum Amount")) {
                            tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                        } else if (tvError.getText().toString().trim().equals("Amount entered exceeds available balance")) {
                            tvError.setText("Amount entered exceeds available balance");
                        } else if (tvError.getText().toString().trim().contains("Insufficient funds")) {
                            tvError.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");
                        } else {
                            if (strLimit.equals("daily")) {
                                tvError.setText("Amount entered exceeds your daily limit");
                            } else if (strLimit.equals("week")) {
                                tvError.setText("Amount entered exceeds your weekly limit");
                            }
                        }
                    } else {
                        lyBalance.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvCYN.setVisibility(View.GONE);
                    tvCurrency.setVisibility(View.VISIBLE);
                    etAmount.setGravity(Gravity.CENTER_VERTICAL);
                    if (tvError.getVisibility() == View.VISIBLE) {
                        lyBalance.setVisibility(View.GONE);
                        if (tvError.getText().toString().trim().contains("Minimum Amount")) {
                            tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                        } else if (tvError.getText().toString().trim().equals("Amount entered exceeds available balance")) {
                            tvError.setText("Amount entered exceeds available balance");
                        } else if (tvError.getText().toString().trim().contains("Insufficient funds")) {
                            tvError.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");
                        } else {
                            if (strLimit.equals("daily")) {
                                tvError.setText("Amount entered exceeds your daily limit");
                            } else if (strLimit.equals("week")) {
                                tvError.setText("Amount entered exceeds your weekly limit");
                            }
                        }
                    } else {
                        lyBalance.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
package com.coyni.mapp.view;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.mapp.model.bank.BankDeleteResponseData;
import com.coyni.mapp.model.cards.CardDeleteResponse;
import com.coyni.mapp.view.business.RefundTransactionActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.adapters.SelectedPaymentMethodsAdapter;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.bank.SignOn;
import com.coyni.mapp.model.bank.SignOnData;
import com.coyni.mapp.model.bank.SyncAccount;
import com.coyni.mapp.model.biometric.BiometricTokenRequest;
import com.coyni.mapp.model.biometric.BiometricTokenResponse;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.paymentmethods.PaymentsList;
import com.coyni.mapp.model.transactionlimit.LimitResponseData;
import com.coyni.mapp.model.transactionlimit.TransactionLimitRequest;
import com.coyni.mapp.model.transactionlimit.TransactionLimitResponse;
import com.coyni.mapp.model.transferfee.TransferFeeRequest;
import com.coyni.mapp.model.transferfee.TransferFeeResponse;
import com.coyni.mapp.model.withdraw.WithdrawRequest;
import com.coyni.mapp.model.withdraw.WithdrawResponse;
import com.coyni.mapp.model.withdraw.WithdrawResponseData;
import com.coyni.mapp.utils.CustomeTextView.AnimatedGradientTextView;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.keyboards.CustomKeyboard;
import com.coyni.mapp.view.business.BusinessDashboardActivity;
import com.coyni.mapp.view.business.SelectPaymentMethodActivity;
import com.coyni.mapp.viewmodel.BuyTokenViewModel;
import com.coyni.mapp.viewmodel.CoyniViewModel;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.PaymentMethodsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WithdrawTokenActivity extends BaseActivity implements TextWatcher, OnKeyboardVisibilityListener {
    MyApplication objMyApplication;
    PaymentsList selectedCard, prevSelectedCard;
    ImageView imgBankIcon, imgArrow, imgConvert;
    TextView tvLimit, tvPayHead, tvAccNumber, tvCurrency, tvBankName, tvBAccNumber, tvError, tvCYN, etRemarks, tvAvailableBal, tvFeePer;
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
    DatabaseHandler dbHandler;
    Dialog payDialog, prevDialog, cvvDialog;
    TransactionLimitResponse objResponse;
    Dialog pDialog;
    String strLimit = "", strType = "", strBankId = "", strCardId = "", strSubType = "", strSignOn = "", signetWalletId = "", strPayment = "";
    Double maxValue = 0.0, dget = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    SignOnData signOnData;
    float fontSize, dollarFont;
    public static WithdrawTokenActivity withdrawTokenActivity;
    Long mLastClickTime = 0L, bankId, cardId;
    Boolean isUSD = false, isCYN = false, isBank = false, isButtonClick = false, isMinimumError = false;
    Boolean isFaceLock = false, isTouchId = false, isPayment = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    boolean isAuthenticationCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_withdraw_token);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(WithdrawTokenActivity.this);
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
        if (requestCode == 3) {
            if (objMyApplication.getCardSave()) {
                objMyApplication.setCardSave(false);
                isPayment = true;
            } else if (objMyApplication.getSignet()) {
                objMyApplication.setSignet(false);
                isPayment = true;
            } else if (objMyApplication.getBankSave()) {
                objMyApplication.setBankSave(false);
                isPayment = true;
            }
        } else {
            switch (resultCode) {
                case RESULT_OK:
                    try {
                        if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                            pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
                            BiometricTokenRequest request = new BiometricTokenRequest();
                            request.setDeviceId(Utils.getDeviceID());
                            request.setMobileToken(objMyApplication.getStrMobileToken());
                            request.setActionType(Utils.withdrawActionType);
                            coyniViewModel.biometricToken(request);
                        } else if (data == null && requestCode == 1) {
                            if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                                Utils.displayAlert("Bank integration has been cancelled", WithdrawTokenActivity.this, "", "");
                            } else {
//                                pDialog = Utils.showProgressDialog(this);
//                                customerProfileViewModel.meSyncAccount();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 0:
                    if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                        startActivity(new Intent(WithdrawTokenActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase())
                                .putExtra("screen", "Withdraw"));
                    }
                    break;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        if (start == 0 && after == 0) {
            etAmount.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, fontSize));
            tvCurrency.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, dollarFont));
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
                    isCYN = false;
                    isUSD = true;
                    convertUSDValue();

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

                    if (validation()) {
                        ctKey.enableButton();
                    } else {
                        ctKey.disableButton();
                    }
                    etAmount.setSelection(etAmount.getText().length());
                    etAmount.setTextDirection(View.TEXT_DIRECTION_LTR);
                } else if (editable.toString().equals(".")) {
                    etAmount.setText("");
                    ctKey.disableButton();
                } else if (editable.length() == 0) {
                    etAmount.setHint("0.00");
                    etAmount.setTextDirection(View.TEXT_DIRECTION_RTL);
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
            setKeyboardVisibilityListener(this);
            objMyApplication = (MyApplication) getApplicationContext();
            selectedCard = objMyApplication.getSelectedCard();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            dbHandler = DatabaseHandler.getInstance(WithdrawTokenActivity.this);
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
            tvFeePer = findViewById(R.id.tvFeePer);
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
            objMyApplication.initializeDBHandler(WithdrawTokenActivity.this);
            isFaceLock = objMyApplication.setFaceLock();
            isTouchId = objMyApplication.setTouchId();
            if (isFaceLock || isTouchId) {
                objMyApplication.setLocalBiometric(true);
            } else {
                objMyApplication.setLocalBiometric(false);
            }
            bindPayMethod(selectedCard);
            etAmount.addTextChangedListener(this);

            etAmount.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void sendAccessibilityEvent(View host, int eventType) {
                    super.sendAccessibilityEvent(host, eventType);
                    if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                        etAmount.setSelection(etAmount.getText().toString().length());
                    }
                }
            });

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
                            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
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
                        rollbackSelectedCard();
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
            pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
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
                try {
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
                    if (transferFeeResponse != null) {
                        objMyApplication.setTransferFeeResponse(transferFeeResponse);
                        feeInAmount = transferFeeResponse.getData().getFeeInAmount();
                        feeInPercentage = transferFeeResponse.getData().getFeeInPercentage();
                        if (isButtonClick && !etAmount.getText().toString().equals("") && !etAmount.getText().toString().equals("0") && Utils.doubleParsing(etAmount.getText().toString().replace(",", "")) > 0) {
                            isButtonClick = false;
                            Double pay = Utils.doubleParsing(etAmount.getText().toString().replace(",", ""));
                            pfee = transferFeeResponse.getData().getFee();
                            dget = pay - pfee;
                            withdrawTokenPreview();
                            ctKey.enableButton();
                        } else {
                            ctKey.disableButton();
                        }

                        String feeString = "Fees: ";

                        if (feeInAmount != 0 && feeInPercentage != 0)
                            feeString = feeString + "$" + Utils.convertTwoDecimalPoints(feeInAmount) + " + " + Utils.convertTwoDecimalPoints(feeInPercentage) + "%";

                        else if (feeInAmount != 0 && feeInPercentage == 0)
                            feeString = feeString + "$" + Utils.convertTwoDecimalPoints(feeInAmount);

                        else if (feeInAmount == 0 && feeInPercentage != 0)
                            feeString = feeString + Utils.convertTwoDecimalPoints(feeInPercentage) + "%";

                        if (!feeString.equals("Fees: ")) {
                            tvFeePer.setVisibility(View.VISIBLE);
                            tvFeePer.setText(feeString);
                        } else
                            tvFeePer.setVisibility(View.GONE);

                        if (!etAmount.getText().toString().equals("") && !etAmount.getText().toString().equals("0") && Utils.doubleParsing(etAmount.getText().toString().replace(",", "")) > 0) {
                            isUSD = true;
                            convertUSDValue();
                            if (validation()) {
                                ctKey.enableButton();
                            } else {
                                ctKey.disableButton();
                            }
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
//                Utils.setStrToken("");
                objMyApplication.clearStrToken();
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
//                Utils.setStrToken("");
                objMyApplication.clearStrToken();
                if (withdrawResponse != null) {
//                    withdrawTokenFailure(withdrawResponse);
                    startActivity(new Intent(WithdrawTokenActivity.this, GiftCardBindingLayoutActivity.class)
                            .putExtra("status", "failed")
                            .putExtra("subtype", selectedCard.getPaymentMethod().toLowerCase()));
                    finish();
                }
            }
        });

//        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
//            @Override
//            public void onChanged(SignOn signOn) {
//                try {
//                    if (signOn != null) {
//                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
//                            objMyApplication.setSignOnData(signOn.getData());
//                            signOnData = signOn.getData();
//                            objMyApplication.setStrSignOnError("");
//                            strSignOn = "";
//                            if (objMyApplication.getResolveUrl()) {
//                                objMyApplication.callResolveFlow(WithdrawTokenActivity.this, strSignOn, signOnData);
//                            }
//                        } else {
//                            if (signOn.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
//                                objMyApplication.setResolveUrl(true);
//                                customerProfileViewModel.meSignOn();
//                            } else {
//                                objMyApplication.setSignOnData(null);
//                                signOnData = null;
//                                objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
//                                strSignOn = signOn.getError().getErrorDescription();
//                            }
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//
//        customerProfileViewModel.getSyncAccountMutableLiveData().observe(this, new Observer<SyncAccount>() {
//            @Override
//            public void onChanged(SyncAccount syncAccount) {
//                try {
//                    pDialog.dismiss();
//                    if (syncAccount != null) {
//                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
//                            dashboardViewModel.mePaymentMethods();
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });

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
//                    if (objMyApplication.getSelectedCard() != null) {
                    PaymentsList objData = paymentMethodsResponse.getData().getData().get(0);
                    if (!isPayment && objMyApplication.getSelectedCard() != null) {
//                        selectedCard = objMyApplication.getSelectedCard();
//                        bindPayMethod(selectedCard);
                        bindPayMethod(rollbackSelectedCard());
                    } else {
                        isPayment = false;
                        if (!objData.getPaymentMethod().toLowerCase().equals("credit")) {
//                            objMyApplication.setSelectedCard(objData);
//                            bindPayMethod(objData);
                            bindPayMethod(previousSelectedCard(objData));
                        } else {
                            bindPayMethod(rollbackSelectedCard());
                        }
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
//                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                            objMyApplication.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        }
                        withdrawToken();
                    } else {
                        if (!biometricTokenResponse.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(biometricTokenResponse.getError().getErrorDescription(), WithdrawTokenActivity.this, "", biometricTokenResponse.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(biometricTokenResponse.getError().getFieldErrors().get(0), WithdrawTokenActivity.this, "", "");
                        }
                    }
                }
            }
        });

        paymentMethodsViewModel.getCardDeleteResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                try {
                    pDialog.dismiss();
                    if (cardDeleteResponse.getStatus().toLowerCase().equals("success")) {
                        Utils.showCustomToast(WithdrawTokenActivity.this, "Card has been removed.", R.drawable.ic_custom_tick, "");
                        dashboardViewModel.mePaymentMethods();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                try {
                    pDialog.dismiss();
                    if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {
                        if (strPayment.equals("bank")) {
                            Utils.showCustomToast(WithdrawTokenActivity.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                        } else {
                            Utils.showCustomToast(WithdrawTokenActivity.this, "Signet has been removed.", R.drawable.ic_custom_tick, "");
                        }
                        dashboardViewModel.mePaymentMethods();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
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
            String strPaymentMethod = "";
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                for (int i = 0; i < paymentMethodsResponse.getData().getData().size(); i++) {
//                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
//                        if (!paymentMethodsResponse.getData().getData().get(i).getPaymentMethod().toLowerCase().equals("credit")) {
//                            listPayments.add(paymentMethodsResponse.getData().getData().get(i));
//                        }
//                    } else {
//                        strPaymentMethod = paymentMethodsResponse.getData().getData().get(i).getPaymentMethod();
//                        if (strPaymentMethod != null && (strPaymentMethod.toLowerCase().equals("bank") || strPaymentMethod.toLowerCase().equals("signet"))) {
//                            listPayments.add(paymentMethodsResponse.getData().getData().get(i));
//                        }
//                    }
                    strPaymentMethod = paymentMethodsResponse.getData().getData().get(i).getPaymentMethod();
                    if (strPaymentMethod != null && !strPaymentMethod.toLowerCase().equals("credit")) {
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
                                startActivityForResult(i, 3);
                            } else {
                                Intent i = new Intent(WithdrawTokenActivity.this, SelectPaymentMethodActivity.class);
                                i.putExtra("screen", "withdraw");
                                i.putExtra("subtype", "add");
                                startActivityForResult(i, 3);
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
            tvLimit.setVisibility(View.VISIBLE);
            String strCurrency = "", strAmount = "";
            if (objLimit.getTransactionLimit() != null && !objLimit.getTransactionLimit().toLowerCase().equals("NA") && !objLimit.getTransactionLimit().toLowerCase().equals("unlimited")) {
                maxValue = Utils.doubleParsing(objLimit.getTransactionLimit());
            }
            strCurrency = " " + getString(R.string.currency);
//                if ((week == 0 || week < 0) && daily > 0) {
//                    strLimit = "daily";
//                    maxValue = daily;
//                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
//                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
//                } else if ((daily == 0 || daily < 0) && week > 0) {
//                    strLimit = "week";
//                    maxValue = week;
//                    strAmount = Utils.convertBigDecimalUSD(String.valueOf(week));
//                    tvLimit.setText("Your weekly limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
//                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
//                    tvLimit.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + strCurrency);
//                    strLimit = "unlimited";
//                } else {
//                    strLimit = "daily";
//                    maxValue = daily;
//                    strAmount = Utils.convertBigDecimalUSD(String.valueOf(daily));
//                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
//                }
            if (objLimit.getLimitType().toLowerCase().equals("daily")) {
                strLimit = "daily";
                strAmount = Utils.convertBigDecimalUSD(String.valueOf(maxValue));
                tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
            } else if (objLimit.getLimitType().toLowerCase().equals("weekly")) {
                strLimit = "week";
                strAmount = Utils.convertBigDecimalUSD(String.valueOf(maxValue));
                tvLimit.setText("Your weekly limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
            } else if (objLimit.getLimitType().toLowerCase().equals("per transaction")) {
                strLimit = "per transaction";
                strAmount = Utils.convertBigDecimalUSD(String.valueOf(maxValue));
                tvLimit.setText("Your per transaction limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
            }
//            else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
//                tvLimit.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + strCurrency);
//                strLimit = "unlimited";
//            }
            else {
//                strLimit = "";
                strLimit = objLimit.getLimitType().toLowerCase();
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
            } else if (strType.toLowerCase().equals("signet")) {
                request.setTxnSubType(Utils.signetType);
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
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (objData.getPaymentMethod().toLowerCase().equals("bank") || objData.getPaymentMethod().toLowerCase().equals("signet")) {
                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                    strType = "bank";
                    strSubType = Utils.bankType;
                    signetWalletId = "";
                    imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                } else {
                    strType = "signet";
                    strSubType = Utils.signetType;
                    signetWalletId = objData.getAccountNumber();
                    imgBankIcon.setImageResource(R.drawable.ic_signetactive);
                }
                strBankId = String.valueOf(objData.getId());
//                obj.setTransactionSubType(Integer.parseInt(Utils.bankType));
                obj.setTransactionSubType(Integer.parseInt(strSubType));
                lyBDetails.setVisibility(View.VISIBLE);
                lyCDetails.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, lyBDetails.getId());
//                imgBankIcon.setImageResource(R.drawable.ic_bankactive);
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
            params1.addRule(RelativeLayout.BELOW, tvLimit.getId());
            params1.addRule(RelativeLayout.RIGHT_OF, imgBankIcon.getId());
            params1.addRule(RelativeLayout.LEFT_OF, imgArrow.getId());
            params1.setMargins(Utils.convertPxtoDP(15), Utils.convertPxtoDP(5), 0, 0);
            tvFeePer.setLayoutParams(params1);
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                } else {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this, "", "");
            }
            calculateFee("10");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindSelectedBank(PaymentsList objData) {
        try {
            strCardId = "0";
            prevSelectedCard = null;
            bindPayMethod(objData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        isMinimumError = false;
        try {
            cynValidation = Utils.doubleParsing(objResponse.getData().getMinimumLimit());
            String strPay = Utils.convertBigDecimalUSD((etAmount.getText().toString().trim().replace("\"", "")).replace(",", ""));
            if ((Utils.doubleParsing(strPay.replace(",", "")) < cynValidation)) {
//                tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                Utils.setErrorSpannableText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN", WithdrawTokenActivity.this, tvError, 17);
                isMinimumError = true;
                tvError.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                value = false;
            } else if (Utils.doubleParsing(strPay.replace(",", "")) <= 0) {
                value = false;
            } else if (!strLimit.equals("") && !strLimit.equals("unlimited") && !strLimit.equals("no limit") && Utils.doubleParsing(strPay.replace(",", "")) > maxValue) {
//                if (strLimit.equals("daily")) {
//                    tvError.setText("Amount entered exceeds your daily limit");
//                } else if (strLimit.equals("week")) {
//                    tvError.setText("Amount entered exceeds your weekly limit");
//                }
//                tvError.setVisibility(View.VISIBLE);
//                lyBalance.setVisibility(View.GONE);
//                value = false;
                if (!objResponse.getData().getLimitType().toLowerCase(Locale.ROOT).equals("per transaction")) {
                    if (objResponse.getData().getLimitType().toLowerCase(Locale.ROOT).equals("daily")) {
                        tvError.setText("Amount entered exceeds your daily limit");
                    } else if (objResponse.getData().getLimitType().toLowerCase(Locale.ROOT).equals("weekly")) {
                        tvError.setText("Amount entered exceeds your weekly limit");
                    }
                    tvError.setVisibility(View.VISIBLE);
                    lyBalance.setVisibility(View.GONE);
                    isMinimumError = false;
                    value = false;
                } else if (objResponse.getData().getLimitType().toLowerCase(Locale.ROOT).equals("per transaction")) {
                    if (Utils.doubleParsing(strPay.replace(",", "")) > avaBal) {
                        tvError.setText("Amount entered exceeds available balance");
                    } else {
                        tvError.setText("Amount entered exceeds your per transaction limit");
                    }
                    tvError.setVisibility(View.VISIBLE);
                    lyBalance.setVisibility(View.GONE);
                    isMinimumError = false;
                    value = false;
                } else {
                    tvError.setVisibility(View.GONE);
                    lyBalance.setVisibility(View.VISIBLE);
                    value = true;
                }
            } else if (Utils.doubleParsing(strPay.replace(",", "")) > avaBal) {
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
            AnimatedGradientTextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = prevDialog.findViewById(R.id.tv_lable_verify);

            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            tvPaymentHead.setText("Withdraw to");
            tvPurchaseHead.setText("Withdraw Amount");
            tvCYN.setVisibility(View.GONE);
            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSD(String.valueOf(pfee));
            tvGet.setText("$ " + Utils.USNumberFormat(cynValue));
            tvPurchaseAmt.setText(Utils.USNumberFormat(cynValue) + " " + getString(R.string.currency));
            tvProcessingFee.setText(Utils.USNumberFormat(Utils.doubleParsing(strPFee)) + " " + getString(R.string.currency));
            total = cynValue + Utils.doubleParsing(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
            isAuthenticationCalled = false;
            createWithdrawRequest();

            if (selectedCard.getPaymentMethod().toLowerCase().equals("bank") || selectedCard.getPaymentMethod().toLowerCase().equals("signet")) {
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
//                        tv_lable.setText("Verifying");
//                        tv_lable.setVisibility(View.GONE);
//                        tv_lable_verify.setVisibility(View.VISIBLE);
                        if (!isAuthenticationCalled) {
                            tv_lable.setText("Verifying");
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
                    //customerProfileViewModel.meSignOn();
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
//                                objMyApplication.setResolveUrl(true);
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
                } else if (objPayment.getPaymentMethod().toLowerCase().equals("signet")) {
                    if (payDialog != null && payDialog.isShowing()) {
                        payDialog.dismiss();
                    }
                    layoutCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.VISIBLE);
                    imgBankIcon.setImageResource(R.drawable.ic_signetactive);
                    tvAccount.setVisibility(View.GONE);
                    if (objPayment.getAccountNumber() != null && objPayment.getAccountNumber().length() > 14) {
                        tvBankName.setText(objPayment.getAccountNumber().substring(0, 10) + "**** " + objPayment.getAccountNumber().substring(objPayment.getAccountNumber().length() - 4));
                    } else {
                        tvBankName.setText(objPayment.getAccountNumber());
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
                    if (objPayment.getPaymentMethod().toLowerCase().equals("bank") || objPayment.getPaymentMethod().toLowerCase().equals("signet")) {
                        paymentMethodsViewModel.deleteBanks(objPayment.getId());
                        strPayment = objPayment.getPaymentMethod().toLowerCase();
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
            strBankId = "0";
            prevSelectedCard = null;
            bindPayMethod(objData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSD(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(WithdrawTokenActivity.this);
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            ctKey.setEnteredText(etAmount.getText().toString());
            etAmount.addTextChangedListener(WithdrawTokenActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
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
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
//                etAmount.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, fontSize));
//                tvCurrency.setTextSize(Utils.pixelsToSp(WithdrawTokenActivity.this, dollarFont));
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

    private void withdrawToken() {
        try {
            pDialog = Utils.showProgressDialog(WithdrawTokenActivity.this);
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                buyTokenViewModel.withdrawTokens(objMyApplication.getWithdrawRequest(), objMyApplication.getStrToken());
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
            addNoteET.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(WithdrawTokenActivity.this);
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
            cvvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    cvvDialog = null;
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(WithdrawTokenActivity.this);
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
            cynValue = Utils.doubleParsing(etAmount.getText().toString().trim().replace(",", ""));
            mLastClickTime = SystemClock.elapsedRealtime();
            isButtonClick = true;
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
//                usdValue = Utils.doubleParsing(etAmount.getText().toString().trim().replace(",", ""));
                usdValue = Utils.doubleParsing(Utils.convertBigDecimalUSD(etAmount.getText().toString().trim().replace(",", "")));
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
                etAmount.setText(Utils.convertBigDecimalUSD(String.valueOf(cynValue)));
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
//                strMessage = "We are processing  your request, please allow a 3-5 business days for your coyni bank withdrawal to be reflected in your bank account. Learn More";
                strMessage = "We are processing  your request, please allow a 3-5 business days for your coyni bank withdrawal to be reflected in your bank account.";
            }
            if (!strCardId.equals("")) {
//                strMessage = "We are processing your request, please allow a few minutes for your coyni instant withdrawal to be reflected in your bank account. Learn More";
                strMessage = "We are processing your request, please allow a few minutes for your coyni instant withdrawal to be reflected in your bank account.";
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
//            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#00a6a2")), strMessage.indexOf("Learn More"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(new UnderlineSpan(), strMessage.indexOf("Learn More"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(clickableSpan, strMessage.length() - 10, strMessage.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvDescription.setText(ss);

            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            tvHeading.setText("Transaction in Progress");
            imgLogo.setImageResource(R.drawable.ic_in_progress_icon);
            Double bal = cynValue + objMyApplication.getGBTBalance();
            String strBal = Utils.convertBigDecimalUSD(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Utils.doubleParsing(strBal)) + " " + getString(R.string.currency));
            tvAmount.setText("$ " + Utils.USNumberFormat(cynValue));
//            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nbank statement as " + objData.getDescriptorName().toLowerCase() + ".");
            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nbank statement.");
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
//                    Intent i = new Intent(WithdrawTokenActivity.this, DashboardActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(i);
                    try {
                        Intent i;
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            i = new Intent(WithdrawTokenActivity.this, DashboardActivity.class);
                        } else {
                            i = new Intent(WithdrawTokenActivity.this, BusinessDashboardActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
        if (!signetWalletId.equals("")) {
            request.setSignetWalletId(signetWalletId);
        }
        objMyApplication.setWithdrawRequest(request);
        objMyApplication.setWithdrawAmount(cynValue);

        return request;
    }

    private void enableButton() {
        try {
            isMinimumError = false;
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
//                            tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                            isMinimumError = true;
                            Utils.setErrorSpannableText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN", WithdrawTokenActivity.this, tvError, 17);
                        } else if (tvError.getText().toString().trim().equals("Amount entered exceeds available balance")) {
                            tvError.setText("Amount entered exceeds available balance");
                        } else if (tvError.getText().toString().trim().contains("Insufficient funds")) {
                            tvError.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");
                        } else {
                            if (strLimit.equals("daily")) {
                                tvError.setText("Amount entered exceeds your daily limit");
                            } else if (strLimit.equals("week")) {
                                tvError.setText("Amount entered exceeds your weekly limit");
                            } else if (strLimit.equalsIgnoreCase("per transaction")) {
                                tvError.setText("Amount entered exceeds your per transaction limit");
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
//                            tvError.setText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN");
                            Utils.setErrorSpannableText("Minimum Amount is " + Utils.USNumberFormat(cynValidation) + " CYN", WithdrawTokenActivity.this, tvError, 17);
                        } else if (tvError.getText().toString().trim().equals("Amount entered exceeds available balance")) {
                            tvError.setText("Amount entered exceeds available balance");
                        } else if (tvError.getText().toString().trim().contains("Insufficient funds")) {
                            tvError.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");
                        } else {
                            if (strLimit.equals("daily")) {
                                tvError.setText("Amount entered exceeds your daily limit");
                            } else if (strLimit.equals("week")) {
                                tvError.setText("Amount entered exceeds your weekly limit");
                            } else if (strLimit.equalsIgnoreCase("per transaction")) {
                                tvError.setText("Amount entered exceeds your per transaction limit");
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
        Utils.isKeyboardVisible = visible;
    }

    public PaymentsList rollbackSelectedCard() {
        if (objMyApplication.getSelectedCard().getPaymentMethod().toLowerCase().equals("bank") || objMyApplication.getSelectedCard().getPaymentMethod().toLowerCase().equals("signet")) {
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

    public PaymentsList previousSelectedCard(PaymentsList objData) {
        try {
            switch (objData.getPaymentMethod().toLowerCase()) {
                case "bank":
                    if (objMyApplication.getFeatureControlGlobal() != null && objMyApplication.getFeatureControlGlobal().getWithBank() != null && objMyApplication.getFeatureControlByUser() != null
                            && (objMyApplication.getFeatureControlGlobal().getWithBank() && objMyApplication.getFeatureControlByUser().getWithBank())) {
                        selectedCard = objData;
                        objMyApplication.setSelectedCard(selectedCard);
                    }
                    break;
                case "signet":
                    if (objMyApplication.getFeatureControlGlobal() != null && objMyApplication.getFeatureControlGlobal().getWithSignet() != null && objMyApplication.getFeatureControlByUser() != null
                            && (objMyApplication.getFeatureControlGlobal().getWithSignet() && objMyApplication.getFeatureControlByUser().getWithSignet())) {
                        selectedCard = objData;
                        objMyApplication.setSelectedCard(selectedCard);
                    }
                    break;
                case "debit":
                    if (objMyApplication.getFeatureControlGlobal() != null && objMyApplication.getFeatureControlGlobal().getWithInstant() != null && objMyApplication.getFeatureControlByUser() != null
                            && (objMyApplication.getFeatureControlGlobal().getWithInstant() && objMyApplication.getFeatureControlByUser().getWithInstant())) {
                        selectedCard = objData;
                        objMyApplication.setSelectedCard(selectedCard);
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return selectedCard;
    }

}
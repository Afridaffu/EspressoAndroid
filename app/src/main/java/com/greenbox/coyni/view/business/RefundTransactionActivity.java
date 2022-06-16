package com.greenbox.coyni.view.business;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.util.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.RefundInsufficeintTokenDialog;
import com.greenbox.coyni.dialogs.RefundInsufficientMerchnatDialog;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.paidorder.PaidOrderRequest;
import com.greenbox.coyni.model.transaction.RefundDataResponce;
import com.greenbox.coyni.model.transaction.RefundReferenceRequest;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.utils.CustomeTextView.AnimatedGradientTextView;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenActivity;
import com.greenbox.coyni.view.ForgotPasswordActivity;
import com.greenbox.coyni.view.LoginActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.view.PayRequestActivity;
import com.greenbox.coyni.view.ValidatePinActivity;
import com.greenbox.coyni.view.WithdrawTokenActivity;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class RefundTransactionActivity extends BaseActivity implements TextWatcher, OnKeyboardVisibilityListener {
    MyApplication objMyApplication;
    private ImageView refundBackIV;
    private TextView etremarksTV, fullamounttv, halfamounttv;
    public EditText refundET, addNoteET;
    private TextView refundcurrencyTV, tvcynTV;
    private LinearLayout remarksll,lyPRClose
            ;
    private DatabaseHandler dbHandler;
    private CardView fullamount, halfamount;
    private CustomKeyboard cKey;
    private Long mLastClickTime = 0L, bankId, cardId;
    private float fontSize, dollarFont;
    private String token, biorefund;
    private String strToken = "";
    private DashboardViewModel dashboardViewModel;
    private Dialog pDialog, cvvDialog, refundDialog, insuffDialog, insuffTokenDialog;
    private Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    private Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    private boolean isamount = false, isremarks = false, isrefundClick = false;
    Boolean isFaceLock = false, isTouchId = false;
    public boolean isfullamount = false, ishalfamount = false, isrefundClickable = false;
    public static RefundTransactionActivity refundTransactionActivity;
    private TransactionData transactionData;
    private String refundamount = "", etvalue = "", refundreason = "", gbxid = "", recipientAddress = "", strUserName = "", walletbalance = "", hamount = "", saleorderID = "";
    private int wallettype;
    private boolean isRefundProcessCalled = false, insufficientTokenBalance = false, insufficientMerchantBalance = false;
    private double value, value1, Value, etValue, processingFee;
    private int enteramout, textamount;
    private static final String ACTION = "RefundPreviewDialog";
    private static final String ACTIONN = "insuffintmerchantbalancedialog ";
    private CoyniViewModel coyniViewModel;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_refund_transaction);
            transactionData = (TransactionData) getIntent().getSerializableExtra(Utils.SELECTED_MERCHANT_TRANSACTION);
            gbxid = getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID);
            dbHandler = DatabaseHandler.getInstance(RefundTransactionActivity.this);
            initialization();
            initobservers();
            lyPRClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    if (Utils.isKeyboardVisible) {
                        Utils.hideKeypad(RefundTransactionActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            if (cvvDialog != null && addNoteET.hasFocus()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addNoteET.requestFocus();
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(RefundTransactionActivity.this);
                    }
                }, 100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    private void initialization() {
        setKeyboardVisibilityListener(this);
        etremarksTV = findViewById(R.id.eTremarks);
        refundBackIV = findViewById(R.id.RefundbackIV);
        lyPRClose = findViewById(R.id.lyPRClose);
        refundET = findViewById(R.id.refundAmountET);
        refundcurrencyTV = findViewById(R.id.refundCurrencyTV);
        tvcynTV = findViewById(R.id.tvCYN);
        remarksll = findViewById(R.id.remarksLL);
        fullamount = findViewById(R.id.FullamountSOT);
        halfamount = findViewById(R.id.HalfamountSOT);
        fullamounttv = findViewById(R.id.FullamountTV);
        halfamounttv = findViewById(R.id.halfamountTV);
        refundTransactionActivity = this;
//        setFaceLock();
//        setTouchId();
        objMyApplication = (MyApplication) getApplicationContext();
        objMyApplication.initializeDBHandler(RefundTransactionActivity.this);
        isFaceLock = objMyApplication.setFaceLock();
        isTouchId = objMyApplication.setTouchId();
        if (isFaceLock || isTouchId) {
            objMyApplication.setLocalBiometric(true);
        } else {
            objMyApplication.setLocalBiometric(false);
        }
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
        refundET.requestFocus();
        refundET.setSelection(refundET.getText().length());
        refundET.setShowSoftInputOnFocus(false);
        refundET.setSelected(false);
        refundET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Utils.isKeyboardVisible)
//                    Utils.hideKeypad(RefundTransactionActivity.this);
            }
        });
        refundET.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public void sendAccessibilityEvent(View host, int eventType) {
                super.sendAccessibilityEvent(host, eventType);
                if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                    refundET.setSelection(refundET.getText().toString().length());
                }
            }
        });
        refundET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.hideKeypad(RefundTransactionActivity.this);
                if (!hasFocus) {
                    if (!refundET.getText().toString().equals("")) {
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                        refundET.setFilters(FilterArray);
                        USFormat(refundET);

                    }
                } else {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlengthValue)));
                    refundET.setFilters(FilterArray);

                }
            }
        });


        if (transactionData.getGrossAmount() != null && !transactionData.getGrossAmount().equals("")) {
            value = Double.parseDouble(Utils.convertTwoDecimal(transactionData.getGrossAmount().replace("CYN", "").trim()));
            refundcurrencyTV.setText("" + Utils.convertTwoDecimal(transactionData.getGrossAmount().replace("CYN", "").trim()));

        }


        refundET.addTextChangedListener(this);
        if (getIntent().getStringExtra(Utils.amount) != null && !getIntent().getStringExtra(Utils.amount).equals("")) {
            refundET.setText(getIntent().getStringExtra(Utils.amount));
            USFormat(refundET);
//            refundET.setEnabled(true);
        }
        cKey = (CustomKeyboard) findViewById(R.id.ckbrefund);
        InputConnection ic = refundET.onCreateInputConnection(new EditorInfo());
        cKey.setInputConnection(ic);
        cKey.setKeyAction("Refund", RefundTransactionActivity.this);
        cKey.setScreenName("refundables");

        etremarksTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayComments();
                    }

                }, 700);
            }
        });

        fullamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (transactionData.getGrossAmount() != null && !transactionData.getGrossAmount().equals("")) {

                        value1 = Double.parseDouble(Utils.convertTwoDecimal(transactionData.getGrossAmount().replace("CYN", "").trim()));
                        refundET.setText("" + Utils.convertTwoDecimal(transactionData.getGrossAmount().replace("CYN", "").trim()));
                        refundET.setSelection(refundET.getText().length());
                        fullamount.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        fullamounttv.setTextColor(getResources().getColor(R.color.white));
                        halfamounttv.setTextColor(getResources().getColor(R.color.primary_green));
                        halfamount.setCardBackgroundColor(getResources().getColor(R.color.slidebtn_bg));
                        isfullamount = true;
                        ishalfamount = false;
                        cKey.setEnteredText(refundET.getText().toString().trim());
                    } else {
                        ishalfamount = false;
                        isfullamount = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        halfamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (transactionData.getGrossAmount() != null && !transactionData.getGrossAmount().equals("")) {
                        Value = Double.parseDouble(transactionData.getGrossAmount().replace("CYN", "").trim());
                        Value = Value / 2;
                        refundET.setText("" + Utils.convertTwoDecimal(String.valueOf(Value)));
                        refundET.setSelection(refundET.getText().length());
                        halfamount.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        halfamounttv.setTextColor(getResources().getColor(R.color.white));
                        fullamounttv.setTextColor(getResources().getColor(R.color.primary_green));
                        fullamount.setCardBackgroundColor(getResources().getColor(R.color.slidebtn_bg));
                        ishalfamount = true;
                        isfullamount = false;
                        cKey.setEnteredText(refundET.getText().toString().trim());
                    } else {
                        ishalfamount = false;
                        isfullamount = false;
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            switch (resultCode) {
                case RESULT_OK:
                    try {
                        showProgressDialog();
                        BiometricTokenRequest request = new BiometricTokenRequest();
                        request.setDeviceId(Utils.getDeviceID());
                        request.setMobileToken(objMyApplication.getStrMobileToken());
                        request.setActionType(Utils.refundActionType);
                        coyniViewModel.biometricToken(request);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case RESULT_CANCELED:
                    launchPinActivity();
                    break;
            }
        }
    }

    private void refundAPI(RefundReferenceRequest refundrefrequest) {
        dashboardViewModel.refundDetails(refundrefrequest);
    }

    private void refundProcessAPI(RefundReferenceRequest request) {
        dashboardViewModel.refundprocessDetails(request);
    }


    public RefundReferenceRequest prepareReq() {
        RefundReferenceRequest refundrefrequest = new RefundReferenceRequest();
        try {
            refundamount = refundET.getText().toString().trim();
            refundreason = etremarksTV.getText().toString().trim();

            refundrefrequest.setAmount(Double.parseDouble(refundamount));
            refundrefrequest.setRemarks(refundreason);
            refundrefrequest.setGbxTransactionId(gbxid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return refundrefrequest;
    }

    public RefundReferenceRequest refundTransaction() {
        RefundReferenceRequest request = new RefundReferenceRequest();
        try {
            request.setAmount(Double.parseDouble(refundamount));
            request.setRemarks(refundreason);
            request.setGbxTransactionId(gbxid);
            request.setWalletType(wallettype);
            request.setRequestToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }


    public void setRefundAmountClick() {
        if (isrefundClickable) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            convertDecimal();
            {
                isrefundClick = true;
                showProgressDialog();
                refundAPI(prepareReq());

            }
        }

    }

    private void initobservers() {

        dashboardViewModel.getRefundDetailsMutableLiveData().observe(this, new Observer<RefundDataResponce>() {
            @Override
            public void onChanged(RefundDataResponce refundDataResponce) {
                dismissDialog();
                try {
                    if (refundDataResponce != null) {
                        if (refundDataResponce.getStatus().equalsIgnoreCase(Utils.Success)) {
                            refundInfo(refundDataResponce);
                        } else {
                            if (refundDataResponce.getError().getErrorDescription() != null && !refundDataResponce.getError().getErrorDescription().equalsIgnoreCase("")) {
                                Utils.displayAlert(refundDataResponce.getError().getErrorDescription(), RefundTransactionActivity.this, "", refundDataResponce.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(refundDataResponce.getError().getFieldErrors().get(0), RefundTransactionActivity.this, "", "");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, biometricTokenResponse -> {
            if (biometricTokenResponse != null) {
                if (biometricTokenResponse.getStatus().equalsIgnoreCase("success")) {
                    if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
//                        Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        token = biometricTokenResponse.getData().getRequestToken();
                        showProgressDialog();
                        refundProcessAPI(refundTransaction());
                    }
                }
            }
        });

        dashboardViewModel.getRefundProcessMutableLiveData().observe(this, new Observer<RefundDataResponce>() {
            @Override
            public void onChanged(RefundDataResponce refundDataResponce) {
                dismissDialog();
                try {
                    if (refundDataResponce != null) {
                        if (refundDataResponce.getData() != null) {
                            if (refundDataResponce.getData().getReferenceId() != null && !refundDataResponce.getData().getReferenceId().equals("")) {
                                dismissDialog();
                                Intent i = new Intent(RefundTransactionActivity.this, RefundTransactionSuccessActivity.class);
                                i.putExtra(Utils.amount, refundET.getText().toString());
                                i.putExtra(Utils.gbxTransID, refundDataResponce.getData().getReferenceId());
                                startActivity(i);
                            } else {
                                Intent i = new Intent(RefundTransactionActivity.this, RefundTransactionFailed.class);
                                startActivity(i);
                            }
                        } else {
                            if (refundDataResponce.getError().getErrorDescription() != null && !refundDataResponce.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(refundDataResponce.getError().getErrorDescription(), RefundTransactionActivity.this, "", refundDataResponce.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(refundDataResponce.getError().getFieldErrors().get(0), RefundTransactionActivity.this, "", "");
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void refundInfo(RefundDataResponce refundDataResponce) {
        try {
            if (refundDataResponce.getData().getReferenceId() != null && !refundDataResponce.getData().getReferenceId().equals("")) {
                recipientAddress = refundDataResponce.getData().getReferenceId();
            }
            if (refundDataResponce.getData().getWalletBalance() != null && !refundDataResponce.getData().getWalletBalance().equals("")) {
                walletbalance = refundDataResponce.getData().getWalletBalance();
            }
            if (refundDataResponce.getData().getProcessingFee() != null) {
                processingFee = refundDataResponce.getData().getProcessingFee();
            }
            if (refundDataResponce.getData().getWalletType() != null) {
                wallettype = refundDataResponce.getData().getWalletType();
            }
            if (refundDataResponce.getData().getInsufficientMerchantBalance() != null) {
                insufficientMerchantBalance = refundDataResponce.getData().getInsufficientMerchantBalance();
            }
            if (refundDataResponce.getData().getInsufficientTokenBalance() != null) {
                insufficientTokenBalance = refundDataResponce.getData().getInsufficientTokenBalance();
            }
            if (!insufficientMerchantBalance && !insufficientTokenBalance) {
                refundPreview();
                enableRefund();
            } else if (insufficientMerchantBalance && !insufficientTokenBalance) {
                insufficientMerchantBalancedialog();
                enableRefund();

            } else {
                insufficientTokenBalancedialog();
                enableRefund();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insufficientTokenBalancedialog() {

        try {
            RefundInsufficientMerchnatDialog refundInsufficientMerchnatDialog = new RefundInsufficientMerchnatDialog(RefundTransactionActivity.this);
            refundInsufficientMerchnatDialog.show();
            refundInsufficientMerchnatDialog.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClicked(String action, Object value) {
                    if (action.equalsIgnoreCase(ACTIONN)) {
                        Intent i = new Intent(RefundTransactionActivity.this, SelectPaymentMethodActivity.class);
                        startActivity(i);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insufficientMerchantBalancedialog() {

        try {
            RefundInsufficeintTokenDialog refunInsufficeintTokenDialog = new RefundInsufficeintTokenDialog(RefundTransactionActivity.this, objMyApplication.getGBTBalance());
            refunInsufficeintTokenDialog.show();
            refunInsufficeintTokenDialog.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClicked(String action, Object value) {
                    if (action.equalsIgnoreCase(ACTION)) {
                        refundPreview();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void refundPreview() {
        try {
            refundDialog = new Dialog(RefundTransactionActivity.this);
            refundDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            refundDialog.setContentView(R.layout.refund_preview_dialog);
            refundDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView amountPayTV = refundDialog.findViewById(R.id.amountPayTV);
            TextView userefundTV = refundDialog.findViewById(R.id.userRefundTV);
            TextView tvProcessingFee = refundDialog.findViewById(R.id.tvProcessingFee);
            TextView recipaddreTV = refundDialog.findViewById(R.id.recipAddreTV);
            TextView tvTotal = refundDialog.findViewById(R.id.tvTotal);
            TextView messageNoteTV = refundDialog.findViewById(R.id.messageNoteTV);
            LinearLayout copyRecipientLL = refundDialog.findViewById(R.id.copyRecipientLL);
            LinearLayout refundPreviewLL = refundDialog.findViewById(R.id.refundpreviewLL);
            LinearLayout lyMessage = refundDialog.findViewById(R.id.lyMessage);
            MotionLayout slideToConfirm = refundDialog.findViewById(R.id.slideToConfirmm);
            AnimatedGradientTextView tv_lable = refundDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = refundDialog.findViewById(R.id.tv_lable_verify);

            CardView im_lock_ = refundDialog.findViewById(R.id.im_lock_);
            userefundTV.setText(strUserName);
            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(processingFee));
            String enteredAmount = Utils.convertBigDecimalUSDC(refundET.getText().toString().replace(",", ""));
            amountPayTV.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
            tvProcessingFee.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " " + getString(R.string.currency));
            total = cynValue + Double.parseDouble(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
            if (gbxid != null && !gbxid.equals("")) {
                if (gbxid.length() > 10) {
                    recipaddreTV.setText(gbxid.substring(0, 10) + "...");
                } else {
                    recipaddreTV.setText(gbxid);
                }
            }
            isRefundProcessCalled = false;
            if (!etremarksTV.getText().toString().trim().equals("")) {
                lyMessage.setVisibility(View.VISIBLE);
                messageNoteTV.setText("\"" + etremarksTV.getText().toString() + "\"");
            } else {
//                lyMessage.setVisibility(View.INVISIBLE);
            }

            copyRecipientLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(gbxid, RefundTransactionActivity.this);
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
                        if (!isRefundProcessCalled) {
                            refundDialog.dismiss();
                            tv_lable.setText(Utils.Verifying);
                            isRefundProcessCalled = true;

                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(RefundTransactionActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(RefundTransactionActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(RefundTransactionActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                }
                            } else {
                                launchPinActivity();
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

            Window window = refundDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            refundDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            refundDialog.setCanceledOnTouchOutside(true);
            refundDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void launchPinActivity() {
        Intent refundPin = new Intent(RefundTransactionActivity.this, ValidatePinActivity.class);
        refundPin.putExtra(Utils.ACTION_TYPE, Utils.refundActionType);
        pinActivityResultLauncher.launch(refundPin);
    }

    ActivityResultLauncher<Intent> pinActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Call API Here
                    LogUtils.v(TAG, "RESULT_OK" + result);
                    token = result.getData().getStringExtra(Utils.TRANSACTION_TOKEN);
                    showProgressDialog();
                    refundProcessAPI(refundTransaction());
                }
            });


    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];

            if (editable.length() > 12) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            } else if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                cKey.disableButton();
            } else {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
//                refundET.setTextSize(Utils.pixelsToSp(RefundTransactionActivity.this, fontSize));
//                refundcurrencyTV.setTextSize(Utils.pixelsToSp(RefundTransactionActivity.this, dollarFont));
            }
            refundET.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (start == 0 && after == 0) {
//            refundET.setTextSize(Utils.pixelsToSp(RefundTransactionActivity.this, fontSize));
//            refundcurrencyTV.setTextSize(Utils.pixelsToSp(RefundTransactionActivity.this, dollarFont));
        }

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().length() == 0) {


        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == refundET.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".")
                        && !editable.toString().equals(".00")) {
                    refundET.setHint("");
                    convertUSDValue();
                    if (editable.length() == 5 || editable.length() == 6) {
                        refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
                        tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        cKey.disableButton();

                    } else if (editable.length() == 7 || editable.length() == 8) {
                        refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                        tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        cKey.disableButton();

                    } else if (editable.length() >= 9) {
                        refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                        tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        cKey.disableButton();
                    } else if (editable.length() <= 4) {
                        refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53);
                        tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        cKey.disableButton();
                    }
                } else if (editable.toString().equals(".")) {
                    refundET.setText("");
                } else if (editable.length() == 0) {
                    refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
                    refundET.setHint("0.00");
                    tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    refundcurrencyTV.setVisibility(View.VISIBLE);
                    cKey.disableButton();
                    cKey.clearData();
                } else {
                    refundET.setText("");
                    LogUtils.d(TAG, "lengthhh zeroo");
                    refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
                    cKey.disableButton();
                    cKey.clearData();
//                    clearAmountCards();
                }
                enableRefund();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void enableRefund() {
        try {
            etValue = Double.parseDouble(Utils.convertBigDecimal(refundET.getText().toString()));

            if (etValue > 0 && etValue <= value
//                    && !etremarksTV.getText().toString().equals("")
            ) {
                cKey.enableButton();
                isrefundClickable = true;
            } else {
                cKey.disableButton();
                isrefundClickable = false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void convertUSDValue() {
        try {
            usdValue = Double.parseDouble(refundET.getText().toString().trim().replace(",", ""));
            cynValue = (usdValue + (usdValue * (feeInPercentage / 100))) + feeInAmount;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertDecimal() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            refundET.setFilters(FilterArray);
            USFormat(refundET);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            refundET.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertTwoDecimal(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(RefundTransactionActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(RefundTransactionActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
            cKey.setEnteredText(refundET.getText().toString().trim());
//            refundET.clearFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void displayComments() {
        try {

            cvvDialog = new Dialog(RefundTransactionActivity.this);
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
                        Utils.shwForcedKeypad(RefundTransactionActivity.this);
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
//                        cKey.disableButton();
//                        doneBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    } else {
                        addNoteTIL.setCounterEnabled(true);
//                        doneBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
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
            if (!etremarksTV.getText().toString().trim().equals("")) {
                addNoteET.setText(etremarksTV.getText().toString().trim());
                addNoteET.setSelection(addNoteET.getText().toString().trim().length());
            }
            Window window = cvvDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            cvvDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//            cvvDialog.setCanceledOnTouchOutside(true);
            cvvDialog.show();
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvvDialog.dismiss();
                    if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(RefundTransactionActivity.this);
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        etremarksTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(RefundTransactionActivity.this);
                        enableRefund();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


//            cvvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (Utils.isKeyboardVisible)
//                                Utils.hideKeypad(RefundTransactionActivity.this);
//                            cvvDialog.dismiss();
//                        }
//                    }, 600);
//                }
//            });
            cvvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    cvvDialog = null;
                    if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(RefundTransactionActivity.this);

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void clearAmountCards() {
        try {
            fullamount.setCardBackgroundColor(getResources().getColor(R.color.slidebtn_bg));
            fullamounttv.setTextColor(getResources().getColor(R.color.primary_green));
            halfamount.setCardBackgroundColor(getResources().getColor(R.color.slidebtn_bg));
            halfamounttv.setTextColor(getResources().getColor(R.color.primary_green));

            isfullamount = false;
            ishalfamount = false;
//            refundET.setText("");
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setKeyboardVisibilityListener(
            final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
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

    public void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

    //    private void setFaceLock() {
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
////                objMyApplication.setLocalBiometric(false);
//                if (!isFaceLock) {
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
}
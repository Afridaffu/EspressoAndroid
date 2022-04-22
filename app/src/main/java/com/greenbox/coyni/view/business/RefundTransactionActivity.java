package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.RefundInsufficeintTokenDialog;
import com.greenbox.coyni.dialogs.RefundInsufficientMerchnatDialog;
import com.greenbox.coyni.model.transaction.RefundDataResponce;
import com.greenbox.coyni.model.transaction.RefundReferenceRequest;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.utils.CustomeTextView.AnimatedGradientTextView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class RefundTransactionActivity extends BaseActivity implements TextWatcher {
    MyApplication objMyApplication;
    private ImageView refundBackIV;
    private TextView etremarksTV, fullamounttv, halfamounttv;
    public EditText refundET, addNoteET;
    private TextView refundcurrencyTV, tvcynTV;
    private LinearLayout remarksll;
    private CardView fullamount, halfamount;
    private CustomKeyboard cKey;
    private Long mLastClickTime = 0L, bankId, cardId;
    private float fontSize, dollarFont;
    private DashboardViewModel dashboardViewModel;
    private Dialog pDialog, cvvDialog, refundDialog, insuffDialog, insuffTokenDialog;
    private Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    private Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    private boolean isamount = false, isremarks = false, isrefundClick = false;

    public boolean isfullamount = false, ishalfamount = false, isrefundClickable = false;
    public static RefundTransactionActivity refundTransactionActivity;
    private TransactionData transactionData;
    private String refundamount = "", etvalue = "", refundreason = "", gbxid = "", recipientAddress = "", strUserName = "", walletbalance = "", hamount = "";
    private int processingFee, wallettype;
    private boolean isRefundProcessCalled = false, insufficientTokenBalance = false, insufficientMerchantBalance = false;
    private double value, value1, Value, etValue;
    private int enteramout, textamount;
    private static final String ACTION = "RefundPreviewDialog";
    private static final String ACTIONN = "insuffintmerchantbalancedialog ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_refund_transaction);
            transactionData = (TransactionData) getIntent().getSerializableExtra(Utils.SELECTED_MERCHANT_TRANSACTION);
            gbxid = getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID);

            initialization();
            initobservers();
            refundBackIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialization() {

        etremarksTV = findViewById(R.id.eTremarks);
        refundBackIV = findViewById(R.id.RefundbackIV);
        refundET = findViewById(R.id.refundAmountET);
        refundcurrencyTV = findViewById(R.id.refundCurrencyTV);
        tvcynTV = findViewById(R.id.tvCYN);
        remarksll = findViewById(R.id.remarksLL);
        fullamount = findViewById(R.id.FullamountSOT);
        halfamount = findViewById(R.id.HalfamountSOT);
        fullamounttv = findViewById(R.id.FullamountTV);
        halfamounttv = findViewById(R.id.halfamountTV);
        refundTransactionActivity = this;

        objMyApplication = (MyApplication) getApplicationContext();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        refundET.requestFocus();
        refundET.setShowSoftInputOnFocus(false);
        refundET.setSelected(false);
        refundET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeypad(RefundTransactionActivity.this, v);
            }
        });
        refundET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.hideSoftKeypad(RefundTransactionActivity.this, v);
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
        } else {
            cKey = (CustomKeyboard) findViewById(R.id.ckbrefund);
            InputConnection ic = refundET.onCreateInputConnection(new EditorInfo());
            cKey.setInputConnection(ic);
            cKey.setKeyAction("Refund", RefundTransactionActivity.this);
            cKey.setScreenName("refundables");
        }

        remarksll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                displayComments();
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
//                        Value = Double.parseDouble(Utils.convertTwoDecimal(transactionData.getGrossAmount().replace("CYN", "").trim())) / 2;
                        hamount = String.valueOf(value1/2);
                        Value = Double.parseDouble(Utils.convertTwoDecimal(hamount));
                        refundET.setText("" + Value);
                        refundET.setSelection(refundET.getText().length());
                        halfamount.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                        halfamounttv.setTextColor(getResources().getColor(R.color.white));
                        fullamounttv.setTextColor(getResources().getColor(R.color.primary_green));
                        fullamount.setCardBackgroundColor(getResources().getColor(R.color.slidebtn_bg));
                        ishalfamount = true;
                        isfullamount = false;
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


    private void refundAPI(RefundReferenceRequest refundrefrequest) {
        dashboardViewModel.refundDetails(refundrefrequest);
    }

    private void refundProcessAPI(RefundReferenceRequest refundrefrequest) {
        dashboardViewModel.refundprocessDetails(refundrefrequest);
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
        try {
            dashboardViewModel.getRefundDetailsMutableLiveData().observe(this, new Observer<RefundDataResponce>() {
                @Override
                public void onChanged(RefundDataResponce refundDataResponce) {
                    dismissDialog();
                    if (refundDataResponce != null) {
                        if (refundDataResponce.getStatus().equalsIgnoreCase(Utils.Success)) {
                            refundInfo(refundDataResponce);
                        } else {
                            if (!refundDataResponce.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(refundDataResponce.getError().getErrorDescription(), RefundTransactionActivity.this, "", refundDataResponce.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(refundDataResponce.getError().getFieldErrors().get(0), RefundTransactionActivity.this, "", "");
                            }
                        }
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dashboardViewModel.getRefundProcessMutableLiveData().observe(this, new Observer<RefundDataResponce>() {
                @Override
                public void onChanged(RefundDataResponce refundDataResponce) {
                    dismissDialog();
                    if (refundDataResponce != null) {
                        if (refundDataResponce.getStatus().equalsIgnoreCase(Utils.Success)) {
                            if (refundDataResponce.getData() != null) {
                                if (refundDataResponce.getData().getReferenceId() != null && !refundDataResponce.getData().getReferenceId().equals("")) {
                                    Intent i = new Intent(RefundTransactionActivity.this, RefundTransactionSuccessActivity.class);
                                    i.putExtra(Utils.amount, refundET.getText().toString());
                                    i.putExtra(Utils.gbxTransID, refundDataResponce.getData().getReferenceId());
                                    startActivity(i);
                                }
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

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            insufficientMerchantBalance = refundDataResponce.getData().getInsufficientMerchantBalance();
            insufficientTokenBalance = refundDataResponce.getData().getInsufficientTokenBalance();
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
//                        i.putExtra("screen", "refund");
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
            recipaddreTV.setText(recipientAddress);
            isRefundProcessCalled = false;
            if (!etremarksTV.getText().toString().trim().equals("")) {
                lyMessage.setVisibility(View.VISIBLE);
                messageNoteTV.setText(etremarksTV.getText().toString());
            } else {
                lyMessage.setVisibility(View.INVISIBLE);
            }

            copyRecipientLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(recipientAddress, RefundTransactionActivity.this);
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
                            refundProcessAPI(refundTransaction());
                            //tv_lable.setText(Utils.Verifying);
//                            finish();
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
                    tvcynTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    refundcurrencyTV.setVisibility(View.VISIBLE);
                    cKey.disableButton();
                    cKey.clearData();
                } else {
                    refundET.setText("");
                    LogUtils.d(TAG, "lengthhh zeroo");
                    refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70);
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

            if (etValue > value && etValue >= value1) {
                cKey.disableButton();
                isrefundClickable = false;
            } else if (etValue > 0 && etValue <= value && !etremarksTV.getText().toString().equals("")) {
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


            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvvDialog.dismiss();
                    Utils.hideKeypad(RefundTransactionActivity.this);
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        etremarksTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        enableRefund();
                        Utils.hideKeypad(RefundTransactionActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            addNoteET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() >= 1) {
                        addNoteTIL.setCounterEnabled(true);
                    } else {
                        cKey.disableButton();
                        addNoteTIL.setCounterEnabled(false);
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

            cvvDialog.show();

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
            refundET.setText("");
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
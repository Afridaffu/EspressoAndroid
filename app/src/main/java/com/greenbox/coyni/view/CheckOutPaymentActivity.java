package com.greenbox.coyni.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoResponse;
import com.greenbox.coyni.model.check_out_transactions.OrderPayRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderPayResponse;
import com.greenbox.coyni.model.check_out_transactions.ScanQRRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.utils.CheckOutConstants;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.PayToMerchantActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CheckOutViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class CheckOutPaymentActivity extends AppCompatActivity {

    private BusinessDashboardViewModel businessDashboardViewModel;
    private BuyTokenViewModel buyTokenViewModel;
    private CheckOutViewModel checkOutViewModel;
    private MyApplication myApplication;
    private TextView mTokenBalance, mUserName, errorText,
            tvCurrency, tv_lable_verify, tv_lable;
    //    private LinearLayout lyBalance;
    private TextView mAmount;
    private double availableBalance;
    private MotionLayout slideToConfirm;
    private CoyniViewModel coyniViewModel;
    private DatabaseHandler dbHandler;
    private CardView im_lock_;
    private Dialog pDialog;
    private boolean isAuthenticationCalled = false, isFaceLock = false, isTouchId = false;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;
    private String requestToken;
    private ImageView merchantImage;
    private LinearLayout closeButton;
    private String actionTypeYes = "YES";
    private String actionTypeNo = "No";
    private Long mLastClickTime = 0L;
    private double transactionLimit = 0.0, minimumLimit = 0.0, userAmount = 0.0;
    private float fontSize, dollarFont;
    private static Dialog displayAlertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_payment);
        initFields();
        listeners();
        walletAPICall();
        initObservers();
    }


    private void initFields() {
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        checkOutViewModel = new ViewModelProvider(this).get(CheckOutViewModel.class);
        buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
        myApplication = (MyApplication) getApplicationContext();
        dbHandler = DatabaseHandler.getInstance(CheckOutPaymentActivity.this);
        coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
        mTokenBalance = findViewById(R.id.tvBalance);
//        lyBalance = findViewById(R.id.available_balance_tv);
        mUserName = findViewById(R.id.checkout_user_name_tv);
        tvCurrency = findViewById(R.id.tvCurrency);
        mAmount = findViewById(R.id.checkout_amount);
        tv_lable_verify = findViewById(R.id.tv_lable_verify);
        fontSize = mAmount.getTextSize();
        dollarFont = tvCurrency.getTextSize();
        errorText = findViewById(R.id.error_tv);
        merchantImage = findViewById(R.id.merchant_profile_iv);
        slideToConfirm = findViewById(R.id.slide_to_confirm_ml);
        closeButton = findViewById(R.id.payToMerchantClose);
        tv_lable = findViewById(R.id.tv_lable);
        im_lock_ = findViewById(R.id.im_lock_);
        if (myApplication.getCheckOutModel() != null && myApplication.getCheckOutModel().isCheckOutFlag()) {
            if (myApplication.getCheckOutModel().getEncryptedToken() != null && !myApplication.getCheckOutModel().getEncryptedToken().equals("")) {
                requestToken = myApplication.getCheckOutModel().getEncryptedToken();
//                checkOutViewModel.scanQRCode(requestToken);
                orderInfoAPICall(requestToken);
            }
        }
//        slideToConfirm.setInteractionEnabled(false);
//        setFaceLock();
//        setTouchId();
        myApplication.initializeDBHandler(CheckOutPaymentActivity.this);
        isFaceLock = myApplication.setFaceLock();
        isTouchId = myApplication.setTouchId();
        if (isFaceLock || isTouchId) {
            myApplication.setLocalBiometric(true);
        } else {
            myApplication.setLocalBiometric(false);
        }
    }

    private void orderInfoAPICall(String requestToken) {
        OrderInfoRequest request = new OrderInfoRequest();
        request.setEncryptedToken(requestToken);
        checkOutViewModel.getOrderInfo(request);
    }

    private void launchPinActivity() {
        Intent refundPin = new Intent(CheckOutPaymentActivity.this, ValidatePinActivity.class);
        refundPin.putExtra(Utils.ACTION_TYPE, Utils.paidActionType);
        pinActivityResultLauncher.launch(refundPin);
    }

    ActivityResultLauncher<Intent> pinActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Call API Here
                    String token = result.getData().getStringExtra(Utils.TRANSACTION_TOKEN);
                    checkOutPayAPICall(token);
                }
            });


    private void listeners() {

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        slideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                LogUtils.v("TAG", "onTransitionStarted");
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                LogUtils.v("TAG", progress + " progress percent " + Utils.slidePercentage);
                try {
                    if (progress > Utils.slidePercentage) {
                        im_lock_.setAlpha(1.0f);
                        motionLayout.setTransition(R.id.middle, R.id.end);
                        motionLayout.transitionToState(motionLayout.getEndState());
//                        slideToConfirm.setInteractionEnabled(false);
//                        if (myApplication.getCheckOutModel().isCheckOutFlag()){
//                            myApplication.setCheckOutModel(null);
//                        }
                        if (!isAuthenticationCalled) {
                            tv_lable.setText("Verifying");
                            isAuthenticationCalled = true;
                            if (payValidation()) {
                                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(CheckOutPaymentActivity.this)) {
//                                    if (myApplication.getBiometric() && ((isTouchId && Utils.isFingerPrint(CheckOutPaymentActivity.this)) || (isFaceLock))) {
                                    if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(CheckOutPaymentActivity.this)) || (isFaceLock))) {
                                        Utils.checkAuthentication(CheckOutPaymentActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                    } else {
                                        launchPinActivity();
                                    }
                                } else {
                                    launchPinActivity();
                                }
                            }
//                            checkOutPayAPICall("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                LogUtils.v("TAG", "onTransitionCompleted");
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                LogUtils.v("TAG", "onTransitionTrigger");
            }
        });

        mAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i == 0 && i2 == 0) {
                    mAmount.setTextSize(Utils.pixelsToSp(CheckOutPaymentActivity.this, fontSize));
                    tvCurrency.setTextSize(Utils.pixelsToSp(CheckOutPaymentActivity.this, dollarFont));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                        mAmount.setHint("");
//                            convertUSDValue();
                        if (editable.length() > 8) {
                            mAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                            tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                        } else if (editable.length() > 5) {
                            mAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                            tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                        } else {
                            mAmount.setTextSize(Utils.pixelsToSp(CheckOutPaymentActivity.this, fontSize));
                            tvCurrency.setTextSize(Utils.pixelsToSp(CheckOutPaymentActivity.this, dollarFont));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


        });

    }


    private void initObservers() {
        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse != null) {
                    if (businessWalletResponse.getStatus() != null && businessWalletResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (businessWalletResponse.getData() != null && businessWalletResponse.getData().getWalletNames() != null) {
                            availableBalance = businessWalletResponse.getData().getWalletNames().get(0).getAvailabilityToUse();
                            mTokenBalance.setText("Available: " + Utils.convertTwoDecimal(String.valueOf(availableBalance)) + "CYN");
                        }
                    }
                }
            }
        });
        checkOutViewModel.getOrderInfoResponseMutableLiveData().observe(this, new Observer<OrderInfoResponse>() {
            @Override
            public void onChanged(OrderInfoResponse orderInfoResponse) {
                if (orderInfoResponse != null) {
                    if (orderInfoResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (orderInfoResponse.getData() != null && orderInfoResponse.getData().isCheckoutUser()) {
                            initUserData(orderInfoResponse);
                            TransactionLimitAPICall();
                            if (orderInfoResponse.getData().getOrderId() != null) {
                                ScanQRRequest request = new ScanQRRequest();
                                request.setOrderId(orderInfoResponse.getData().getOrderId());
                                checkOutViewModel.scanQRCode(request);
                            }
                        }
                    } else {
                        slideToConfirm.setInteractionEnabled(false);
                        Utils.displayAlertNew(orderInfoResponse.getError().getErrorDescription(), CheckOutPaymentActivity.this, "Coyni");
                    }
                }
            }
        });
        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    if (transactionLimitResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (transactionLimitResponse.getData() != null) {
                            if (transactionLimitResponse.getData().getTransactionLimit() != null && transactionLimitResponse.getData().getMinimumLimit() != null) {
                                transactionLimit = Double.parseDouble(transactionLimitResponse.getData().getTransactionLimit());
                                minimumLimit = Double.parseDouble(transactionLimitResponse.getData().getMinimumLimit());
                            }
//                            validation(transactionLimitResponse);
                        }
                    } else {
                        slideToConfirm.setInteractionEnabled(false);
                        Utils.displayAlert(transactionLimitResponse.getError().getErrorDescription(), CheckOutPaymentActivity.this, "Oops", transactionLimitResponse.getError().getFieldErrors().get(0));
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
                            //myApplication.setStrToken(biometricTokenResponse.getData().getRequestToken());
                            checkOutPayAPICall(biometricTokenResponse.getData().getRequestToken());
                        }

                    }
                }
            }
        });

        checkOutViewModel.getOrderPayResponseMutableLiveData().observe(this, new Observer<OrderPayResponse>() {
            @Override
            public void onChanged(OrderPayResponse orderPayResponse) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (orderPayResponse != null) {
                    myApplication.setOrderPayResponse(orderPayResponse);
                    myApplication.clearStrToken();
                    if (orderPayResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        startActivity(new Intent(CheckOutPaymentActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Success")
                                .putExtra("subtype", CheckOutConstants.CheckOut));

                    } else {
                        startActivity(new Intent(CheckOutPaymentActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Failed")
                                .putExtra("subtype", CheckOutConstants.CheckOut));
                    }
                    finish();
                } else {
                    Utils.displayAlert("something went wrong", CheckOutPaymentActivity.this, "", "");
                }
            }
        });

    }

    private void checkOutPayAPICall(String requestToken) {
        if (pDialog == null || !pDialog.isShowing()) {
            pDialog = Utils.showProgressDialog(CheckOutPaymentActivity.this);
        }
        if (myApplication.getCheckOutModel() != null && myApplication.getCheckOutModel().isCheckOutFlag()) {
            OrderPayRequest request = new OrderPayRequest();
            request.setAmount(mAmount.getText().toString().replace(",", "").trim());
            request.setEncryptedToken(myApplication.getCheckOutModel().getEncryptedToken());
            //TODO set Request token here
            myApplication.setWithdrawAmount(Double.parseDouble(mAmount.getText().toString().replace(",", "").trim()));
            if (request.getAmount() != null && request.getEncryptedToken() != null) {
                checkOutViewModel.orderPay(request);
            }
        }
    }

    private void validation(TransactionLimitResponse transactionLimitResponse) {

        try {
            if (transactionLimitResponse != null) {
                String strPay = mAmount.getText().toString().trim().replace("\"", "");
                if (Double.parseDouble(strPay.replace(",", "")) == 0.0) {
                    Utils.displayAlert("Amount should be greater than zero.", CheckOutPaymentActivity.this, "Oops!", "");
//                    errorText.setText("Amount should be greater than zero.");
//                    errorText.setVisibility(View.VISIBLE);
//                    slideToConfirm.setInteractionEnabled(false);
//                    lyBalance.setVisibility(View.GONE);
                } else if ((Double.parseDouble(strPay.replace(",", "")) < Double.parseDouble(transactionLimitResponse.getData().getMinimumLimit()))) {
                    Utils.displayAlertNew("Minimum Amount is " + Utils.USNumberFormat(Double.parseDouble(transactionLimitResponse.getData().getMinimumLimit())) + " CYN", CheckOutPaymentActivity.this, "Oops!");
//                    errorText.setVisibility(View.VISIBLE);
//                    lyBalance.setVisibility(View.GONE);
//                    slideToConfirm.setInteractionEnabled(false);
                    //            } else if (cynValue > Double.parseDouble(objResponse.getData().getTransactionLimit())) {
                } else if (Double.parseDouble(strPay.replace(",", "")) > Double.parseDouble(transactionLimitResponse.getData().getTransactionLimit())) {
//                    errorText.setText("Amount entered exceeds transaction limit.");
//                    errorText.setVisibility(View.VISIBLE);
//                    lyBalance.setVisibility(View.GONE);
                    Utils.displayAlertNew("Amount entered exceeds transaction limit.", CheckOutPaymentActivity.this, "Oops!");
//                    slideToConfirm.setInteractionEnabled(false);
                }
//                else {
//                    errorText.setVisibility(View.GONE);
//                    lyBalance.setVisibility(View.VISIBLE);
//                    slideToConfirm.setInteractionEnabled(true);
//                    slideToConfirm.setBackground(getDrawable(R.drawable.shape_round_rectable_green));
//                    tv_lable.setTextColor(getColor(R.color.white));
//                }
            } else {
                slideToConfirm.setInteractionEnabled(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void TransactionLimitAPICall() {
        TransactionLimitRequest request = new TransactionLimitRequest();
        request.setTransactionType(Utils.saleOrder);
        request.setTransactionSubType(Utils.token);
        if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            buyTokenViewModel.transactionLimits(request, Utils.userTypeCust);
        } else {
            buyTokenViewModel.transactionLimits(request, Utils.userTypeBusiness);
        }
    }

    private void initUserData(OrderInfoResponse orderInfoResponse) {
        if (orderInfoResponse.getData().getMerchantName() != null) {
            mUserName.setText(orderInfoResponse.getData().getMerchantName());
        } else {
            mUserName.setText(R.string.dba_name_text);
        }
        if (orderInfoResponse.getData().getAmount() != null) {
            mAmount.setText(Utils.convertTwoDecimal(orderInfoResponse.getData().getAmount()));
            userAmount = Double.parseDouble(orderInfoResponse.getData().getAmount());
        }

        if (orderInfoResponse.getData().getMerchantLogo() != null) {
            DisplayImageUtility utility = DisplayImageUtility.getInstance(CheckOutPaymentActivity.this);
            utility.addImage(orderInfoResponse.getData().getMerchantLogo(), merchantImage, R.drawable.acct_profile);
        }

    }

    private void walletAPICall() {

        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletType(Utils.TOKEN);

        if (Utils.checkInternet(CheckOutPaymentActivity.this)) {
            businessDashboardViewModel.meMerchantWallet(walletRequest);
        } else {
            Utils.displayAlert(getString(R.string.internet), CheckOutPaymentActivity.this, "", "");
        }
    }

    @Override
    public void onBackPressed() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        getAlertDialog();
    }

    private void getAlertDialog() {

        DialogAttributes dialogAttributes = new DialogAttributes(getString(R.string.alert),
                getString(R.string.checkout_cancel_message), getString(R.string.yes),
                getString(R.string.no));
        CustomConfirmationDialog customConfirmationDialog = new CustomConfirmationDialog
                (this, dialogAttributes);

        customConfirmationDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(getString(R.string.yes))) {
                    if (myApplication.getCheckOutModel() != null && myApplication.getCheckOutModel().isCheckOutFlag()) {
                        myApplication.setCheckOutModel(null);
                    }
                    CheckOutPaymentActivity.super.onBackPressed();
                } else if (action.equalsIgnoreCase(getString(R.string.no))) {
                    customConfirmationDialog.dismiss();
                }
            }
        });
        customConfirmationDialog.show();
    }


//    private void setFaceLock() {
//        try {
//            isFaceLock = false;
//            String value = dbHandler.getFacePinLock();
//            if (value != null && value.equals("true")) {
//                isFaceLock = true;
//                myApplication.setLocalBiometric(true);
//            } else {
//                isFaceLock = false;
//                myApplication.setLocalBiometric(false);
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
//                myApplication.setLocalBiometric(true);
//            } else {
//                isTouchId = false;
////                myApplication.setLocalBiometric(false);
//                if (!isFaceLock) {
//                    myApplication.setLocalBiometric(false);
//                }
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            switch (resultCode) {
                case RESULT_OK: {
                    try {
                        pDialog = Utils.showProgressDialog(CheckOutPaymentActivity.this);
                        BiometricTokenRequest request = new BiometricTokenRequest();
                        request.setDeviceId(Utils.getDeviceID());
                        request.setMobileToken(myApplication.getStrMobileToken());
                        request.setActionType(Utils.paidActionType);
                        coyniViewModel.biometricToken(request);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
                case 0:
                    launchPinActivity();
                    break;
            }
        }
    }

    private boolean payValidation() {
        String strPay = mAmount.getText().toString().trim().replace("\"", "");
        boolean value = false;

        if (userAmount > availableBalance) {
            if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
            } else if (myApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                displayMerchantAlert(getString(R.string.buy_token_message), CheckOutPaymentActivity.this, "coyni");
            }
        } else if (Double.parseDouble(strPay.replace(",", "")) == 0.0) {
            displayAlertNew("Amount should be greater than zero.", CheckOutPaymentActivity.this, "Oops!");
        } else if ((Double.parseDouble(strPay.replace(",", "")) < minimumLimit)) {
            displayAlertNew("Minimum Amount is " + Utils.USNumberFormat(minimumLimit) + " CYN", CheckOutPaymentActivity.this, "Oops!");
        } else if (Double.parseDouble(strPay.replace(",", "")) > transactionLimit) {
            displayAlertNew("Amount exceeds transaction limit.", CheckOutPaymentActivity.this, "Oops!");
        } else {
            value = true;
        }
        return value;
    }

    private void displayAlert(String msg, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(CheckOutPaymentActivity.this);
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
                myApplication.setStrScreen(CheckOutConstants.FlowCheckOut);
                myApplication.getCheckOutModel().setCheckOutFlag(false);
                Intent i = new Intent(CheckOutPaymentActivity.this, BuyTokenPaymentMethodsActivity.class);
                i.putExtra("screen", CheckOutConstants.ScreenCheckOut);
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

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                changeSlideState();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (slideToConfirm.isInteractionEnabled()) {
            isAuthenticationCalled = false;
            changeSlideState();
        }
    }

    private void changeSlideState() {
        try {
            slideToConfirm.setInteractionEnabled(true);
            slideToConfirm.setTransition(R.id.start, R.id.start);
            tv_lable.setText("Slide to Confirm");
            tv_lable.setVisibility(View.VISIBLE);
            tv_lable_verify.setVisibility(View.GONE);
            slideToConfirm.setProgress(0);
            im_lock_.setAlpha(1.0f);
            isAuthenticationCalled = false;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayAlertNew(String msg, final Context context, String headerText) {
        // custom dialog
        displayAlertDialog = new Dialog(context);
        displayAlertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        displayAlertDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        displayAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = displayAlertDialog.findViewById(R.id.tvHead);
        TextView message = displayAlertDialog.findViewById(R.id.tvMessage);
        CardView actionCV = displayAlertDialog.findViewById(R.id.cvAction);
        TextView actionText = displayAlertDialog.findViewById(R.id.tvAction);

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }
        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog.dismiss();
//                changeSlideState();
            }
        });

        displayAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                changeSlideState();
            }
        });

        message.setText(msg);
        Window window = displayAlertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        displayAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        displayAlertDialog.setCanceledOnTouchOutside(true);
        displayAlertDialog.show();
    }

    private void displayMerchantAlert(String msg, final Context context, String headerText) {
        // custom dialog
        displayAlertDialog = new Dialog(context);
        displayAlertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        displayAlertDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        displayAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = displayAlertDialog.findViewById(R.id.tvHead);
        TextView message = displayAlertDialog.findViewById(R.id.tvMessage);
        CardView actionCV = displayAlertDialog.findViewById(R.id.cvAction);
        TextView actionText = displayAlertDialog.findViewById(R.id.tvAction);

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }
        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog.dismiss();
//                changeSlideState();
            }
        });

        displayAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
//                changeSlideState();
                myApplication.setCheckOutModel(null);
                finish();
            }
        });

        message.setText(msg);
        Window window = displayAlertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        displayAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        displayAlertDialog.setCanceledOnTouchOutside(true);
        displayAlertDialog.show();
    }


}
package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.CheckOutDialog;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoResponse;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.utils.CustomeTextView.AnimatedGradientTextView;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
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
    private TextView mTokenBalance,mUserName,errorText;
    private LinearLayout lyBalance;
    private EditText mAmount;
    private double availableBalance;
    private MotionLayout slideToConfirm;
    private CoyniViewModel coyniViewModel;
    private DatabaseHandler dbHandler;
    private TextView tv_lable;
    private CardView im_lock_;
    private Dialog pDialog;
    private EditText payET;
    private boolean isAuthenticationCalled = false, isFaceLock = false, isTouchId = false;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;
    private String requestToken;
    private ImageView merchantImage;
    private LinearLayout closeButton;
    private String actionTypeYes = "YES";
    private String actionTypeNo = "No";
    private double transactionLimit = 0.0,minimumLimit = 0.0,userAmount = 0.0;

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
        mTokenBalance = findViewById(R.id.available_token_balance_tv);
        payET = findViewById(R.id.merchantAmountET);
        lyBalance = findViewById(R.id.available_balance_tv);
        mUserName = findViewById(R.id.checkout_user_name_tv);
        mAmount = findViewById(R.id.checkout_amount);
        errorText = findViewById(R.id.error_tv);
        merchantImage = findViewById(R.id.merchant_profile_iv);
        slideToConfirm = findViewById(R.id.slide_to_confirm_ml);
        closeButton = findViewById(R.id.payToMerchantClose);
        tv_lable = findViewById(R.id.tv_lable);
        im_lock_ = findViewById(R.id.im_lock_);
        if (myApplication.getCheckOutModel() != null && myApplication.getCheckOutModel().isCheckOutFlag()) {
            if (myApplication.getCheckOutModel().getEncryptedToken() != null && !myApplication.getCheckOutModel().getEncryptedToken().equals("")) {
                requestToken = myApplication.getCheckOutModel().getEncryptedToken();
                orderInfoAPICall(requestToken);
            }
        }
        setFaceLock();
        setTouchId();
    }

    private void orderInfoAPICall(String requestToken) {
        OrderInfoRequest request = new OrderInfoRequest();
        request.setEncryptedToken(requestToken);
        checkOutViewModel.getOrderInfo(request);
    }


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
                        slideToConfirm.setInteractionEnabled(false);
                        tv_lable.setText("Verifying");
                        if (!isAuthenticationCalled) {
                            isAuthenticationCalled = true;
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(CheckOutPaymentActivity.this)) {
                                if (myApplication.getBiometric() && ((isTouchId && Utils.isFingerPrint(CheckOutPaymentActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(CheckOutPaymentActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
//                                payTransaction();
                                    startActivity(new Intent(CheckOutPaymentActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("screen", "Paid")
//                                            .putExtra(Utils.wallet, recipientAddress)
                                            .putExtra(Utils.amount, payET.getText().toString().replace(",", "").trim()));

                                }
                            } else {
//                            payTransaction();
                                startActivity(new Intent(CheckOutPaymentActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("screen", "Paid")
//                                        .putExtra(Utils.wallet, recipientAddress)
                                        .putExtra(Utils.amount, payET.getText().toString().replace(",", "").trim()));
                            }
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

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                            mTokenBalance.setText(Utils.convertTwoDecimal(String.valueOf(availableBalance)));
                        }
                    }
                }
            }
        });
        checkOutViewModel.getOrderInfoResponseMutableLiveData().observe(this, new Observer<OrderInfoResponse>() {
            @Override
            public void onChanged(OrderInfoResponse orderInfoResponse) {
                if (orderInfoResponse != null) {
                    if (orderInfoResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)){
                        if (orderInfoResponse.getData()!= null && orderInfoResponse.getData().isCheckoutUser()){
                            if (orderInfoResponse.getData().getMerchantLogo()!= null){
                                initUserData(orderInfoResponse);
                                TransactionLimitAPICall();

                            }

                        }
                    }
                }
            }
        });
        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse!= null){
                    if (transactionLimitResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)){
                        if (transactionLimitResponse.getData()!= null) {
                            if(transactionLimitResponse.getData().getTransactionLimit()!= null && transactionLimitResponse.getData().getMinimumLimit()!=null){
                                transactionLimit = Double.parseDouble(transactionLimitResponse.getData().getTransactionLimit());
                                minimumLimit = Double.parseDouble(transactionLimitResponse.getData().getMinimumLimit());
                            }
                            validation(transactionLimitResponse);
                        }
                    }
                    else {
                        Utils.displayAlert(transactionLimitResponse.getError().getErrorDescription(),CheckOutPaymentActivity.this,"Oops",transactionLimitResponse.getError().getFieldErrors().get(0));
                    }
                }
            }
        });
    }

    private void validation(TransactionLimitResponse transactionLimitResponse) {

        try {
            String strPay = mAmount.getText().toString().trim().replace("\"", "");
            if (Double.parseDouble(strPay.replace(",", "")) == 0.0) {
                //Utils.displayAlert("Amount should be greater than zero.", PayRequestActivity.this, "Oops!", "");
                errorText.setText("Amount should be greater than zero.");
                errorText.setVisibility(View.VISIBLE);
                slideToConfirm.setInteractionEnabled(false);
                lyBalance.setVisibility(View.GONE);
            } else if ((Double.parseDouble(strPay.replace(",", "")) < Double.parseDouble(transactionLimitResponse.getData().getMinimumLimit()))) {
                errorText.setText("Minimum Amount is " + Utils.USNumberFormat(Double.parseDouble(transactionLimitResponse.getData().getMinimumLimit())) + " CYN");
                errorText.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                slideToConfirm.setInteractionEnabled(false);
//            } else if (cynValue > Double.parseDouble(objResponse.getData().getTransactionLimit())) {
            } else if (Double.parseDouble(strPay.replace(",", "")) > Double.parseDouble(transactionLimitResponse.getData().getTransactionLimit())) {
                errorText.setText("Amount entered exceeds transaction limit.");
                errorText.setVisibility(View.VISIBLE);
                lyBalance.setVisibility(View.GONE);
                slideToConfirm.setInteractionEnabled(false);
            } else {
                errorText.setVisibility(View.GONE);
                lyBalance.setVisibility(View.VISIBLE);
                slideToConfirm.setInteractionEnabled(true);
                slideToConfirm.setBackground(getDrawable(R.drawable.shape_round_rectable_green));
                tv_lable.setTextColor(getColor(R.color.white));
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
            buyTokenViewModel.transactionLimits(request,Utils.userTypeCust);
        }
        else {
            buyTokenViewModel.transactionLimits(request,Utils.userTypeBusiness);
        }
    }

    private void initUserData(OrderInfoResponse orderInfoResponse) {
        DisplayImageUtility utility = DisplayImageUtility.getInstance(CheckOutPaymentActivity.this);
        utility.addImage(orderInfoResponse.getData().getMerchantLogo(), merchantImage, R.drawable.ic_case);
        if (orderInfoResponse.getData().getMerchantName()!= null){
            mUserName.setText(orderInfoResponse.getData().getMerchantName());
        }
        else {
            mUserName.setText(R.string.dba_name_text);
        }
        if (orderInfoResponse.getData().getAmount()!= null ){
            mAmount.setText(Utils.convertTwoDecimal(orderInfoResponse.getData().getAmount()));
            userAmount = Double.parseDouble(orderInfoResponse.getData().getAmount());
        }
    }

    private void walletAPICall() {

        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletType(Utils.TOKEN);
        walletRequest.setUserId(String.valueOf(myApplication.getLoginUserId()));

        if (Utils.checkInternet(CheckOutPaymentActivity.this)) {
            businessDashboardViewModel.meMerchantWallet(walletRequest);
        } else {
            Utils.displayAlert(getString(R.string.internet), CheckOutPaymentActivity.this, "", "");
        }
    }

    @Override
    public void onBackPressed() {
        getAlertDialog();
    }

    private void getAlertDialog() {
//        CheckOutDialog checkOutDialog = new CheckOutDialog(this);
//        checkOutDialog.setOnDialogClickListener(new OnDialogClickListener() {
//            @Override
//            public void onDialogClicked(String action, Object value) {
//                if (action.equalsIgnoreCase(actionTypeYes)){
//                    if (myApplication.getCheckOutModel() != null && myApplication.getCheckOutModel().isCheckOutFlag()) {
//                        myApplication.setCheckOutModel(null);
//                    }
//                    CheckOutPaymentActivity.super.onBackPressed();
//                }else if (action.equalsIgnoreCase(actionTypeNo)){
//                    checkOutDialog.dismiss();
//                }
//            }
//        });
//
//        checkOutDialog.show();

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
                }
                else if (action.equalsIgnoreCase(getString(R.string.no))){
                    customConfirmationDialog.dismiss();
                }
            }
        });
        customConfirmationDialog.show();
    }


    private void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                myApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                myApplication.setLocalBiometric(false);
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
                myApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                myApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK: {
                try {
                    //payTransaction();
                    pDialog = Utils.showProgressDialog(CheckOutPaymentActivity.this);
                    BiometricTokenRequest request = new BiometricTokenRequest();
                    request.setDeviceId(Utils.getDeviceID());
//                    request.setMobileToken(strToken);
                    request.setMobileToken(myApplication.getStrMobileToken());
                    request.setActionType(Utils.paidActionType);
                    coyniViewModel.biometricToken(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 0:
                try {
                    startActivity(new Intent(CheckOutPaymentActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "Paid")
//                            .putExtra(Utils.wallet, recipientAddress)
                            .putExtra(Utils.amount, payET.getText().toString().replace(",", "").trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
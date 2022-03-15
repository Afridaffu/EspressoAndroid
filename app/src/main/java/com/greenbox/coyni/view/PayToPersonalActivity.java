package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.templates.TemplateResponse;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;

public class PayToPersonalActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    LinearLayout lyPayClose;
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    DashboardViewModel dashboardViewModel;
    CoyniViewModel coyniViewModel;
    ProgressDialog pDialog;
    Dialog prevDialog;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID;
    Boolean isFaceLock = false, isTouchId = false, isCancel = false;
    Double pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0, total = 0.0, cynValue = 0.0, avaBal = 0.0;
    String strUserName = "", strAddress = "";
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    Boolean isAuthenticationCalled = false, isPayCalled = false;
    TextView tvCurrency, tvAmount, tvCYN, tvLable, tvBalance;
    MotionLayout paySlideToConfirm;
    CardView cvLock, im_lock;
    float fontSize, dollarFont;
    ImageView imgConvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_to_personal);
            initialization();
            initObservers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
            case 235: {
                try {
//                    payTransaction();
                    pDialog = Utils.showProgressDialog(PayToPersonalActivity.this);
                    BiometricTokenRequest request = new BiometricTokenRequest();
                    request.setDeviceId(Utils.getDeviceID());
                    request.setMobileToken(objMyApplication.getStrMobileToken());
                    request.setActionType(Utils.sendActionType);
                    coyniViewModel.biometricToken(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 0:
                try {
                    isCancel = true;
                    if (prevDialog != null) {
                        prevDialog.dismiss();
                    }
                    startActivity(new Intent(PayToPersonalActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "Pay"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
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
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            lyPayClose = findViewById(R.id.lyPayClose);
            tvCurrency = findViewById(R.id.tvCurrency);
            tvAmount = findViewById(R.id.tvAmount);
            tvCYN = findViewById(R.id.tvCYN);
            tvLable = findViewById(R.id.tvLable);
            tvBalance = findViewById(R.id.tvBalance);
            paySlideToConfirm = findViewById(R.id.paySlideToConfirm);
            imgConvert = findViewById(R.id.imgConvert);
            isPayCalled = false;
            cvLock = findViewById(R.id.im_lock_);
            im_lock = findViewById(R.id.im_lock);
            tvBalance.setText("Available: " + Utils.USNumberFormat(objMyApplication.getGBTBalance()) + "CYN");
            avaBal = objMyApplication.getGBTBalance();
            fontSize = tvAmount.getTextSize();
            dollarFont = tvCurrency.getTextSize();
            imgConvert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (tvAmount.getText().toString().trim().length() > 0) {
                            if (tvCYN.getVisibility() == View.GONE) {
                                tvCYN.setVisibility(View.VISIBLE);
                                tvCurrency.setVisibility(View.GONE);
                                tvAmount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                            } else {
                                tvCYN.setVisibility(View.GONE);
                                tvCurrency.setVisibility(View.VISIBLE);
                                tvAmount.setGravity(Gravity.CENTER_VERTICAL);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            paySlideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                    try {
                        if (progress > Utils.slidePercentage) {
                            cvLock.setAlpha(1.0f);
                            motionLayout.setTransition(R.id.middle, R.id.end);
                            motionLayout.transitionToState(motionLayout.getEndState());
                            paySlideToConfirm.setInteractionEnabled(false);
                            tvLable.setText("Verifying");

                            if (!isPayCalled) {
                                isPayCalled = true;
                                if (payValidation()) {
                                    pDialog = Utils.showProgressDialog(PayToPersonalActivity.this);
                                    cynValue = Double.parseDouble(tvAmount.getText().toString().trim().replace(",", ""));
                                    calculateFee(Utils.USNumberFormat(cynValue));
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {

                }

                @Override
                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

                }
            });

            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strAddress = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayToPersonalActivity.this)) {
                    dashboardViewModel.getUserDetail(strAddress);
                } else {
                    Utils.displayAlert(getString(R.string.internet), PayToPersonalActivity.this, "", "");
                }
            }
            if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                tvAmount.setText(getIntent().getStringExtra("amount"));
                USFormat(tvAmount);
                cynValue = Double.parseDouble(tvAmount.getText().toString().trim().replace(",", ""));
            }
            lyPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            SetFaceLock();
            SetTouchId();
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
                    payPreview();
                }
            }
        });

        payViewModel.getPayRequestResponseMutableLiveData().observe(this, new Observer<PayRequestResponse>() {
            @Override
            public void onChanged(PayRequestResponse payRequestResponse) {
                try {
                    if (payRequestResponse != null) {
                        Utils.setStrToken("");
                        objMyApplication.setPayRequestResponse(payRequestResponse);
                        if (payRequestResponse.getStatus().toLowerCase().equals("success")) {
                            startActivity(new Intent(PayToPersonalActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "success")
                                    .putExtra("subtype", "pay"));

                        } else {
                            startActivity(new Intent(PayToPersonalActivity.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "failed")
                                    .putExtra("subtype", "pay"));
                        }
                    } else {
                        Utils.displayAlert("something went wrong", PayToPersonalActivity.this, "", "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, tvWAddress, tvTitle;
            ImageView userProfile;
            tvName = findViewById(R.id.tvName);
            tvTitle = findViewById(R.id.tvTitle);
            tvWAddress = findViewById(R.id.tvWAddress);
            userProfile = findViewById(R.id.imgProfile);
            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            strUserName = Utils.capitalize(userDetails.getData().getFullName());
            String imageTextNew = "";
            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            tvTitle.setText(imageTextNew);
            tvWAddress.setText("Account Address " + userDetails.getData().getWalletId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            tvTitle.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.GONE);
                Glide.with(PayToPersonalActivity.this)
                        .load(userDetails.getData().getImage())
                        .placeholder(R.drawable.ic_profilelogo)
                        .into(userProfile);
            } else {
                userProfile.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
            }
            strAddress = "";
            strAddress = userDetails.getData().getWalletId().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payPreview() {
        try {
            prevDialog = new Dialog(PayToPersonalActivity.this);
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
            LinearLayout copyRecipientLL = prevDialog.findViewById(R.id.copyRecipientLL);
            LinearLayout lyMessage = prevDialog.findViewById(R.id.lyMessage);
            MotionLayout slideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);
            TextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            userNamePayTV.setText(strUserName);
            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(pfee));
            if (strAddress.length() > 13) {
                recipAddreTV.setText(strAddress.substring(0, 13) + "...");
            } else {
                recipAddreTV.setText(strAddress);
            }
            String enteredAmount = Utils.convertBigDecimalUSDC(tvAmount.getText().toString().replace(",", ""));
            amountPayTV.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
            tvProcessingFee.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " " + getString(R.string.currency));
            total = cynValue + Double.parseDouble(strPFee);
            tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));

            isAuthenticationCalled = false;
            lyMessage.setVisibility(View.INVISIBLE);

            payTransactionRequest();
            copyRecipientLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(strAddress, PayToPersonalActivity.this);
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

//                        prevDialog.dismiss();
                        if (!isAuthenticationCalled) {
                            isAuthenticationCalled = true;
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(PayToPersonalActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(PayToPersonalActivity.this)) || (isFaceLock))) {
                                    Utils.checkAuthentication(PayToPersonalActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    isCancel = true;
                                    prevDialog.dismiss();
                                    startActivity(new Intent(PayToPersonalActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("screen", "Pay"));
                                }
                            } else {
                                isCancel = true;
                                prevDialog.dismiss();
                                startActivity(new Intent(PayToPersonalActivity.this, PINActivity.class)
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
            prevDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (!isCancel) {
                        changeSlideState();
                    }
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

    private void calculateFee(String strAmount) {
        try {
            TransferFeeRequest request = new TransferFeeRequest();
            request.setTokens(strAmount.trim().replace(",", ""));
            request.setTxnType(Utils.payType);
            request.setTxnSubType(Utils.paySubType);
            if (Utils.checkInternet(PayToPersonalActivity.this)) {
                buyTokenViewModel.transferFee(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransactionRequest() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(tvAmount.getText().toString().trim().replace(",", ""));
            request.setRemarks("");
            request.setRecipientWalletId(strAddress);
            objMyApplication.setTransferPayRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(TextView etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            changeTextSize(strReturn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private Boolean payValidation() {
        Boolean value = true;
        try {
            if (cynValue > avaBal) {
                displayAlert("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
                value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void displayAlert(String msg, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(PayToPersonalActivity.this);
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
                Intent i = new Intent(PayToPersonalActivity.this, BuyTokenPaymentMethodsActivity.class);
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

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void payTransaction() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(tvAmount.getText().toString().trim().replace(",", ""));
            request.setRemarks("");
            request.setRecipientWalletId(strAddress);
            objMyApplication.setTransferPayRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
            if (Utils.checkInternet(PayToPersonalActivity.this)) {
                payViewModel.sendTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void changeSlideState() {
        try {
            paySlideToConfirm.setInteractionEnabled(true);
            paySlideToConfirm.setTransition(R.id.start, R.id.start);
            tvLable.setText("Slide to Confirm");
            paySlideToConfirm.setProgress(0);
            im_lock.setAlpha(1.0f);
            isPayCalled = false;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if(editable.length()==5 || editable.length()==6){
                tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
            } else if(editable.length()==7 || editable.length()==8){
                tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);

            }else if(editable.length()==9){
                tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            }

//            if (editable.length() > 12) {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
//                tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
//                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
//            } else if (editable.length() > 8) {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
//                tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
//                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
//            } else if (editable.length() > 5) {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
////                tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
////                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
//                tvAmount.setTextSize(Utils.pixelsToSp(PayToPersonalActivity.this, fontSize));
//                tvCurrency.setTextSize(Utils.pixelsToSp(PayToPersonalActivity.this, dollarFont));
//            } else {
//                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
//                tvAmount.setTextSize(Utils.pixelsToSp(PayToPersonalActivity.this, fontSize));
//                tvCurrency.setTextSize(Utils.pixelsToSp(PayToPersonalActivity.this, dollarFont));
//            }
            tvAmount.setFilters(FilterArray);
            tvAmount.setText(Utils.USNumberFormat(Double.parseDouble(editable.replace(",", ""))));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            isCancel = false;
            changeSlideState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
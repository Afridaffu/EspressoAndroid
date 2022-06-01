package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.utils.CustomeTextView.AnimatedGradientTextView;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.PayToMerchantActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class CheckOutPaymentActivity extends AppCompatActivity {

    private BusinessDashboardViewModel businessDashboardViewModel;
    private MyApplication myApplication;
    private TextView mTokenBalance;
    private double availableBalance;
    private MotionLayout slideToConfirm;
    private CoyniViewModel coyniViewModel;
    private DatabaseHandler dbHandler;
    private TextView tv_lable;
    private CardView im_lock_;
    private Dialog pDialog;
    private EditText payET;
    private boolean isAuthenticationCalled = false,isFaceLock = false,isTouchId = false;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;

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
        myApplication = (MyApplication) getApplicationContext();
        dbHandler = DatabaseHandler.getInstance(CheckOutPaymentActivity.this);
        coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
        mTokenBalance = findViewById(R.id.available_token_balance_tv);
        payET = findViewById(R.id.merchantAmountET);
        slideToConfirm = findViewById(R.id.slide_to_confirm_ml);
        tv_lable = findViewById(R.id.tv_lable);
        im_lock_ = findViewById(R.id.im_lock_);
        setFaceLock();
        setTouchId();
    }


    private void listeners() {

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
        if (myApplication.getCheckOutModel()!= null && myApplication.getCheckOutModel().isCheckOutFlag()){
            myApplication.setCheckOutModel(null);
        }
        super.onBackPressed();
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
package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.utils.CheckOutConstants;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.PayToMerchantActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    private ProgressDialog dialog;
    private MyApplication myApplication;
    private boolean isLoginFlow = false;
    private boolean launchedCheckoutFlow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getName());
        Utils.launchedActivity = getClass();
        myApplication = (MyApplication) getApplicationContext();
        //getIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.v(TAG, "onNewIntent called");
        //getIntentData(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLaunchCheckoutFlow() && !isLoginFlow && !launchedCheckoutFlow) {
            LogUtils.v(TAG, "Launching the checkout flow");
            launchCheckout();
        }
    }

    public void setLoginFlow(boolean isLoginFlow) {
        this.isLoginFlow = isLoginFlow;
    }

    public void showProgressDialog() {
        showProgressDialog("Please wait...");
    }

    private boolean isLaunchCheckoutFlow() {
        CheckOutModel checkOutModel = myApplication.getCheckOutModel();
        return checkOutModel != null && checkOutModel.isCheckOutFlag();
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, false);
    }

    public void showProgressDialog(String message, boolean isCanceledOnTouchOutside) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ProgressDialog(BaseActivity.this, R.style.MyAlertDialogStyle);
        dialog.setIndeterminate(false);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void launchCheckout() {
        Handler handler = new Handler();
        if (myApplication.getCheckOutModel() != null) {
            CheckOutModel checkOutModel = myApplication.getCheckOutModel();
            if (checkOutModel.isCheckOutFlag() && checkOutModel.getCheckOutWalletId() != null) {
                if (myApplication.getLoginResponse().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
                            try {
                                dismissDialog();
                                launchedCheckoutFlow = true;
                                startActivity(new Intent(BaseActivity.this, PayToMerchantActivity.class)
                                        .putExtra(CheckOutConstants.WALLET_ID, checkOutModel.getCheckOutWalletId())
                                        .putExtra(CheckOutConstants.CheckOutAmount, checkOutModel.getCheckOutAmount()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                        }
//                    }, 100);
                } else {
                    dismissDialog();
                    myApplication.setCheckOutModel(new CheckOutModel());
                    Utils.displayAlertNew("Please use active user account to make payments", BaseActivity.this, "coyni");
                }
            }
        }
    }

}

package com.coyni.mapp.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.coyni.mapp.R;
import com.coyni.mapp.model.check_out_transactions.CheckOutModel;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    private Dialog dialog;
    private MyApplication myApplication;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

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
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLaunchCheckoutFlow() && myApplication.isLoggedIn()) {
            LogUtils.v(TAG, "Launching the checkout flow");
            launchCheckout();
        }
        createReceiver();
        LogUtils.e("BNR Test", "BNR registerReceiver");
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        if (mReceiver != null) {
            LogUtils.e("BNR Test", "BNR unregisterReceiver");
            unregisterReceiver(mReceiver);
        }
        mReceiver = null;
        super.onPause();
    }

    public void onNotificationUpdate() {

    }

    public void showProgressDialog() {
        showProgressDialog("Loading");
    }

    private boolean isLaunchCheckoutFlow() {
        CheckOutModel checkOutModel = myApplication.getCheckOutModel();
        return checkOutModel != null && checkOutModel.isCheckOutFlag();
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, true, this);
    }

    public void showProgressDialog(String message, boolean isCanceledOnTouchOutside, BaseActivity baseActivity) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        dialog = new Dialog(baseActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loader);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView loaderMsg = dialog.findViewById(R.id.loaderText);
        loaderMsg.setText(message);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void handleCheckoutFlow(String accountStatus, int accountType) {
        if (accountType == Utils.PERSONAL_ACCOUNT
                && accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
            launchCheckoutFlow();
        } else if (accountType == Utils.BUSINESS_ACCOUNT) {
            myApplication.setCheckOutModel(new CheckOutModel());
            Utils.displayAlertNew(getString(R.string.merchant_shared_message), BaseActivity.this, "coyni");
        } else {
            myApplication.setCheckOutModel(new CheckOutModel());
            Utils.displayAlertNew(getString(R.string.please_use_active_account), BaseActivity.this, "coyni");
        }
    }

    private void launchCheckout() {
        if (myApplication.getCheckOutModel() != null) {
            CheckOutModel checkOutModel = myApplication.getCheckOutModel();
            if (checkOutModel.isCheckOutFlag()) {
                dismissDialog();

                if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                        && myApplication.getMyProfile().getData().getAccountStatus() != null) {
                    handleCheckoutFlow(myApplication.getMyProfile().getData().getAccountStatus(), myApplication.getMyProfile().getData().getAccountType());
                } else if (myApplication.getLoginResponse() != null && myApplication.getLoginResponse().getData() != null
                        && myApplication.getLoginResponse().getData().getAccountStatus() != null) {
                    handleCheckoutFlow(myApplication.getLoginResponse().getData().getAccountStatus(), myApplication.getLoginResponse().getData().getAccountType());
                }


//                if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
//                        && myApplication.getMyProfile().getData().getAccountStatus() != null) {
//                    if (myApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
//                        launchCheckoutFlow(checkOutModel);
//                    } else {
//                        myApplication.setCheckOutModel(new CheckOutModel());
//                        Utils.displayAlertNew(getString(R.string.please_use_active_account), BaseActivity.this, "coyni");
//                    }
//                } else if (myApplication.getLoginResponse().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
//                    launchCheckoutFlow(checkOutModel);
//                } else {
//                    myApplication.setCheckOutModel(new CheckOutModel());
//                    Utils.displayAlertNew(getString(R.string.please_use_active_account), BaseActivity.this, "coyni");
//                }
            }
        }
    }

    private void launchCheckoutFlow() {
        try {
            dismissDialog();
            startActivity(new Intent(BaseActivity.this, CheckOutPaymentActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createReceiver() {
        mIntentFilter = new IntentFilter(Utils.NOTIFICATION_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onNotificationUpdate();
            }
        };
    }

}
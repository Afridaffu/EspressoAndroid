package com.coyni.mapp.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

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
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        if (mReceiver != null) {
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
        if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
            launchCheckoutFlow();
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

    public void startWebSocket() {
        try {
            String serverUrl = myApplication.getWebSocketUrlResponse().getWebsocketUrl();
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
            Request request = new Request.Builder().url(serverUrl).build();
            EchoWebSocketListener listener = new EchoWebSocketListener();
            WebSocket webSocket = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int CLOSE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("authorization", Utils.getStrAuth());
                webSocket.send(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String message) {
            Log.e("Receive Message: ", message);
            try {
                JSONObject obj = new JSONObject(message);
                if (obj.getString("eventType").equals("TXN_STATUS") && obj.getString("txnStatus").toLowerCase().equals("completed")) {
                    sendBroadcast(new Intent().setAction(Utils.NOTIFICATION_ACTION));
                    webSocket.close(CLOSE_STATUS, null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.e("Receive Bytes : ", bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(CLOSE_STATUS, null);
            Log.e("Closing Socket : ", code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
            try {
                webSocket.close(CLOSE_STATUS, null);
                Log.e("Error : ", throwable.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}

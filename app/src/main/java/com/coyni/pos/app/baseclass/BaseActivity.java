package com.coyni.pos.app.baseclass;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.pos.app.R;
import com.coyni.pos.app.model.appupdate.AppUpdateResp;
import com.coyni.pos.app.utils.LogUtils;
import com.coyni.pos.app.utils.MyApplication;
import com.coyni.pos.app.utils.Utils;
import com.vt.kotlinexamples.retrofit_network.viewmodel.CommonViewModel;

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
    CommonViewModel commonViewModel;

    public Boolean isBaseBiometric = false, isAccess = false, isMerchantHide = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            LogUtils.Companion.d(TAG, getClass().getName());
            myApplication = (MyApplication) getApplicationContext();
            commonViewModel = CommonViewModel.Companion.getInstance(this);

//            runOnUiThread(() -> {
            commonViewModel.getAppUpdateRespMutableLiveData().observe(this, new Observer<AppUpdateResp>() {
                @Override
                public void onChanged(AppUpdateResp appUpdateResp) {
                    try {
                        if (appUpdateResp == null) {
                            return;
                        }
                        if (appUpdateResp.getData() != null) {
                            String version = getPackageManager().getPackageInfo(BaseActivity.this.getPackageName(), 0).versionName;
                            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                            int versionName = Integer.parseInt(version.replace(".", ""));

                        }
//                        else if (appUpdateResp.getError() != null && appUpdateResp.getError().getErrorCode().equals(getString(R.string.accessrestrictederrorcode))) {
//                            showAccessRestricted();
//                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showProgressDialog() {
        showProgressDialog("Loading");
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


}

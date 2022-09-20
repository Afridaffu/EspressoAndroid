package com.coyni.mapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    private Context activity;
    private Dialog dialog;

    public JavaScriptInterface(Context activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void showLoader() {
        if (dialog == null)
            dialog = Utils.showProgressDialog(activity);
    }

    @JavascriptInterface
    public void dismissLoader() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
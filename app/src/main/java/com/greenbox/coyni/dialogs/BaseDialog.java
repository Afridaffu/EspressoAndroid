package com.greenbox.coyni.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.greenbox.coyni.R;

public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.Theme_Dialog);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultProperties();
    }

    private void setDefaultProperties() {
        Window window = this.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setBackgroundDrawableResource(R.color.mb_transparent);
            window.setAttributes(wlp);
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            setCanceledOnTouchOutside(true);
        }
    }
}

package com.greenbox.coyni.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.greenbox.coyni.R;

public class CheckOutDialog extends Dialog {
    private OnDialogClickListener listener;
    TextView notNowBtn, settingsBtn, headerText, descriptionText;
    private String actionTypeYes = "YES";
    private String actionTypeNo = "No";

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public OnDialogClickListener getOnDialogClickListener() {
        return listener;
    }

    public CheckOutDialog(@NonNull Context context) {
        super(context);
    }

    public CheckOutDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permission);
        setDefaultProperties();
    }

    private void setDefaultProperties() {
        Window window = this.getWindow();
//        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        notNowBtn = findViewById(R.id.not_now_tv);
        settingsBtn = findViewById(R.id.settings_tv);
        headerText = findViewById(R.id.headerTextTV);
        descriptionText = findViewById(R.id.descriptonTV);

        settingsBtn.setText("Yes");
        headerText.setText("Coyni Alert");
        descriptionText.setText("Do you want to Cancel the Payment And Redirect to Coyni Dashboard.");
        notNowBtn.setText("No");

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(actionTypeYes, "");
                }
            }
        });

        notNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(actionTypeNo, "");
                }
            }
        });

    }
}

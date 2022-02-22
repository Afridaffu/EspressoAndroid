package com.greenbox.coyni.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DialogAttributes;

public class CustomConfirmationDialog extends Dialog {

    private DialogAttributes dialogAttributes;
    private TextView mTvTitle, mTvMessage, mTvPositiveBtn, mTvNegativeBtn;
    private OnDialogButtonClickListener onButtonClickLister;

    public CustomConfirmationDialog(Context context, DialogAttributes dialogAttributes) {
        super(context, R.style.Theme_Dialog);
        this.dialogAttributes = dialogAttributes;
        setDefaultProperties();
    }

    public void setOnButtonClickLister(OnDialogButtonClickListener onButtonClickLister) {
        this.onButtonClickLister = onButtonClickLister;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_confirmation_dialog);

        initialization();

        mTvTitle.setText(dialogAttributes.getTitle());
        mTvMessage.setText(dialogAttributes.getMessage());
        mTvPositiveBtn.setText(dialogAttributes.getPositiveBtn());
        mTvNegativeBtn.setText(dialogAttributes.getNegativeBtn());
    }

    private void initialization() {
        mTvTitle = findViewById(R.id.tv_dialog_title);
        mTvMessage = findViewById(R.id.tv_dialog_message);
        mTvPositiveBtn = findViewById(R.id.tv_positive_button);
        mTvNegativeBtn = findViewById(R.id.tv_negative_button);

        mTvPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onButtonClickLister != null) {
                    onButtonClickLister.onPositiveButtonClicked();
                }
                dismiss();
            }
        });

        mTvNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onButtonClickLister != null) {
                    onButtonClickLister.onNegativeButtonClicked();
                }
                dismiss();
            }
        });
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

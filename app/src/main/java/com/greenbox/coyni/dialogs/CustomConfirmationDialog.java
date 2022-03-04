package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.utils.OnDialogButtonClickListener;

public class CustomConfirmationDialog extends BaseDialog {

    private DialogAttributes dialogAttributes;
    private TextView mTvTitle, mTvMessage, mTvPositiveBtn, mTvNegativeBtn;
    private OnDialogButtonClickListener onButtonClickLister;

    public CustomConfirmationDialog(Context context, DialogAttributes dialogAttributes) {
        super(context);
        this.dialogAttributes = dialogAttributes;
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
                if (onButtonClickLister != null) {
                    onButtonClickLister.onPositiveButtonClicked();
                }
                dismiss();
            }
        });

        mTvNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonClickLister != null) {
                    onButtonClickLister.onNegativeButtonClicked();
                }
                dismiss();
            }
        });
    }


}
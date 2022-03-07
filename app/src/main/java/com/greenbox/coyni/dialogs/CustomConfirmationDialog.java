package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DialogAttributes;

public class CustomConfirmationDialog extends BaseDialog {

    private DialogAttributes dialogAttributes;
    private TextView mTvTitle, mTvMessage, mTvPositiveBtn, mTvNegativeBtn;

    public CustomConfirmationDialog(Context context, DialogAttributes dialogAttributes) {
        super(context);
        this.dialogAttributes = dialogAttributes;
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
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(dialogAttributes.getPositiveBtn(), "");
                }
                dismiss();
            }
        });

        mTvNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(dialogAttributes.getNegativeBtn(), "");
                }
                dismiss();
            }
        });
    }


}

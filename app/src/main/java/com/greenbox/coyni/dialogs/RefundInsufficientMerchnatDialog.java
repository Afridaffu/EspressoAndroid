package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class RefundInsufficientMerchnatDialog extends BaseDialog {


    public RefundInsufficientMerchnatDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insufficient_funds_dialog);

    }
}

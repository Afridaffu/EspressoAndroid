package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import com.greenbox.coyni.R;

public class PayToMerchantWithAmountDialog extends BaseDialog {

    public PayToMerchantWithAmountDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_merchant_with_amount);

    }
}

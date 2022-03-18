package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import com.greenbox.coyni.R;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog{

    public PayoutTransactionsDetailsFiltersDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payout_transactions_filter);
    }
}

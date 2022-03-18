package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog{

    LinearLayout datePickLL;
    ImageView datePickIV;
    public CardView applyFilterBtnCV;
    EditText filterdatePickET;

    public PayoutTransactionsDetailsFiltersDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payout_transactions_filter);

        datePickLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        filterdatePickET = findViewById(R.id.filterdatePickET);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);

    }
}

package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog {
    LinearLayout datePickLL;
    ImageView datePickIV;
    public CardView applyFilterBtnCV;
    EditText filterdatePickET;
    private String dateSelected = null;

    public PayoutTransactionsDetailsFiltersDialog(Context context, String dateSelected) {
        super(context);
        this.dateSelected = dateSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payout_transactions_filter);

        datePickLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        filterdatePickET = findViewById(R.id.filterdatePickET);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);

        if(dateSelected != null && !dateSelected.equals("")) {
            filterdatePickET.setText(dateSelected);
        }
        datePickLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnDialogClickListener().onDialogClicked("Date_PICK_SELECTED", null);
                dismiss();
            }
        });
        applyFilterBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnDialogClickListener().onDialogClicked("Date_SELECTED", dateSelected);
                dismiss();
            }
        });

    }
}

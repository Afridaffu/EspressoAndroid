package com.greenbox.coyni.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.TransactionListActivity;

import org.w3c.dom.Text;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog {
    LinearLayout datePickLL;
    TextView resetFilterTV;
    ImageView datePickIV;
    public CardView applyFilterBtnCV;
    EditText filterdatePickET;
    private String dateSelected = null;
    public boolean isfilterdatePickET = false,isapplyEnabled= false;
    public static PayoutTransactionsDetailsFiltersDialog payoutTransactionsDetailsFiltersDialog;

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
        resetFilterTV = findViewById(R.id.resetFiltersTV);

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
        resetFilterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterdatePickET.setText("");
            }
        });

    }
}

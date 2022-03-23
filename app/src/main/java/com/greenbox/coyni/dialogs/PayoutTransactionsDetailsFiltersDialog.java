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
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.TransactionListActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutSearchActivity;

import org.w3c.dom.Text;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog {
    LinearLayout datePickLL;
    TextView resetFilterTV;
    ImageView datePickIV;
    public CardView applyFilterBtnCV;
    EditText filterDatePickET;
    private Context context;
    public boolean isfilterdatePickET = false,isapplyEnabled= false;
//    public static PayoutTransactionsDetailsFiltersDialog payoutTransactionsDetailsFiltersDialog;
//    public static DateRangePickerDialog dateRangePickerDialog;

    public PayoutTransactionsDetailsFiltersDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payout_transactions_filter);

        datePickLL = findViewById(R.id.payoutDateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        filterDatePickET = findViewById(R.id.filterdatePickET);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        resetFilterTV = findViewById(R.id.resetFiltersTV);

        datePickLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
//                getOnDialogClickListener().onDialogClicked("Date_PICK_SELECTED", null);
//                dismiss();
            }
        });
        applyFilterBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
//                getOnDialogClickListener().onDialogClicked("Date_SELECTED", dateSelected);
            }
        });
        resetFilterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDatePickET.setText("");
            }
        });
    }
    private void showCalendarDialog() {
        DateRangePickerDialog dialog = new DateRangePickerDialog(context);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if(action.equals("Done")) {
                    filterDatePickET.setText("");
//                    showFiltersPopup();
//                    getOnDialogClickListener().onDialogClicked("Filter_applied", null);
//                    dismiss();
                }
            }
        });
        dialog.show();
    }

}

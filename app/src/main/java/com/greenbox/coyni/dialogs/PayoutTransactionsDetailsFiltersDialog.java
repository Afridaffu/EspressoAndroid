package com.greenbox.coyni.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListData;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionListActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutSearchActivity;
import com.greenbox.coyni.view.business.EditEmail;
import com.greenbox.coyni.view.business.PayoutDetailsTransactionList;

import org.w3c.dom.Text;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog {
    private LinearLayout datePickLL;
    private TextView resetFilterTV;
    private ImageView datePickIV;
    public CardView applyFilterBtnCV;
    private EditText filterDatePickET;
    private Context context;
    private MyApplication objMyApplication;
    private Long mLastClickTime = 0L, mLastClickTimeFilters = 0L;

    private Boolean isFilters = false;

    private String tempStrSelectedDate = "";
    public boolean isfilterdatePickET = false, isapplyEnabled = false;

    private RangeDates rangeDates;
    private ReserveFilter filter;
    //    public static PayoutTransactionsDetailsFiltersDialog payoutTransactionsDetailsFiltersDialog;
    public static DateRangePickerDialog dateRangePickerDialog;

    public PayoutTransactionsDetailsFiltersDialog(Context context, ReserveFilter filter) {
        super(context);
        this.context = context;
        this.filter = filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payout_transactions_filter);
        objMyApplication = (MyApplication) context.getApplicationContext();

        datePickLL = findViewById(R.id.payoutDateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        filterDatePickET = findViewById(R.id.filterdatePickET);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        resetFilterTV = findViewById(R.id.resetFiltersTV);

        if (filter == null) {
            filter = new ReserveFilter();
        }

        datePickLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendarDialog();
            }
        });
        filterDatePickET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendarDialog();
            }
        });
        applyFilterBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!filter.isFilterApplied) {
                    dismiss();
                    Toast.makeText(context, "plese select fromdate and todate", Toast.LENGTH_SHORT).show();
                } else {
                    if (getOnDialogClickListener() != null) {
                        getOnDialogClickListener().onDialogClicked("ApplyFilter", filter);
                        dismiss();
                    }
                }
//                getOnDialogClickListener().onDialogClicked("Date_SELECTED", strSelectedDate);
//                if (rangeDates == null) {
//                    Toast.makeText(context, "plese select fromdate and todate", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    getOnDialogClickListener().onDialogClicked("dates", rangeDates);
//                    dismiss();
//                }
            }
        });
        resetFilterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDatePickET.setText("");
                filter.isFilterApplied = false;
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked("ResetFilter", filter);
                }
            }
        });
    }

    private void showCalendarDialog() {
        DateRangePickerDialog dialog = new DateRangePickerDialog(context);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equals(Utils.datePicker)) {
                    rangeDates = (RangeDates) value;
                    filter.isFilterApplied = true;
                    filter.setUpdatedFromDate(rangeDates.getUpdatedFromDate());
                    filter.setUpdatedToDate(rangeDates.getUpdatedToDate());
                    filterDatePickET.setText(rangeDates.getFullDate());
                }
            }
        });
        dialog.show();
    }

}

package com.greenbox.coyni.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayoutTransactionsDetailsFiltersDialog extends BaseDialog {
    private LinearLayout datePickLL;
    private TextView resetFilterTV;
    private ImageView datePickIV;
    public CardView applyFilterBtnCV;
    private EditText filterDatePickET;
    private Context context;
    private MyApplication objMyApplication;
    private Long mLastClickTime = 0L, mLastClickTimeFilters = 0L;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private  String storedSelectDate = "",tempStrSelectedDate = "",strFromdate = "",strTodate = "",strSelectedDate = "",strended = "",strupdated = "",strF = "",strT = "";


    private Boolean isFilters = false;

    public boolean isfilterdatePickET = false, isapplyEnabled = false;
    private String displayFormat = "MM-dd-yyyy";
    private SimpleDateFormat displayFormatter;
    private java.util.Date startDateD = null;
    private Date endDateD = null;
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
        displayFormatter = new SimpleDateFormat(displayFormat);

        objMyApplication = (MyApplication) context.getApplicationContext();

        datePickLL = findViewById(R.id.payoutDateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        filterDatePickET = findViewById(R.id.filterdatePickET);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        resetFilterTV = findViewById(R.id.resetFiltersTV);

        if (filter != null && filter.getUpdatedFromDate() != null && filter.getUpdatedToDate() != null) {
             strF = filter.getUpdatedFromDate();
             strT = filter.getUpdatedToDate();


            String formatToDisplay = "MMM dd, yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatToDisplay);
            try {
                startDateD = displayFormatter.parse(strF);
                endDateD = displayFormatter.parse(strT);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            strupdated = simpleDateFormat.format(startDateD);
            strended = simpleDateFormat.format(endDateD);

            strSelectedDate = simpleDateFormat.format(startDateD) + "  " + simpleDateFormat.format(endDateD);
            filterDatePickET.setText(strSelectedDate);
            strSelectedDate = strupdated + " - " + strended;
            if (strSelectedDate != null) {
                filterDatePickET.setText(strSelectedDate);
            }else {
                filterDatePickET.setText("");
            }

            SimpleDateFormat rangeFormat = new SimpleDateFormat(DateRangePickerDialog.displayFormat);
            rangeDates = new RangeDates();
            rangeDates.setUpdatedFromDate(rangeFormat.format(startDateD));
            rangeDates.setUpdatedToDate(rangeFormat.format(endDateD));

        }
        else {
            filterDatePickET.setText("");
            filterDatePickET.clearFocus();
            strF = "";
            strT = "";
            strSelectedDate = "";

        }

        if (filter.isFilterApplied) {

        } else {
            rangeDates = new RangeDates();
            rangeDates.setUpdatedFromDate("");
            rangeDates.setUpdatedToDate("");
            filterDatePickET.setText("");
            filterDatePickET.clearFocus();
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
                } else {
                    if (getOnDialogClickListener() != null) {
//                        filterDatePickET.setText(storedSelectDate);
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
                filterDatePickET.clearFocus();
                filter.isFilterApplied = false;
                strSelectedDate = "";
                tempStrSelectedDate = "";
                filter.isFilterApplied = false;
                strF = "";
                strT = "";
                tempStrSelectedDate = "";
                rangeDates = new RangeDates();
                rangeDates.setUpdatedToDate("");
                rangeDates.setUpdatedFromDate("");

                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked("ResetFilter", filter);
                }
            }
        });
    }

    private void showCalendarDialog() {
        DateRangePickerDialog dialog = new DateRangePickerDialog( context,rangeDates);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equals(Utils.datePicker)) {
                    rangeDates = (RangeDates) value;
                    filter.isFilterApplied = true;
                    filter.setUpdatedFromDate(rangeDates.getUpdatedFromDate());
                    filter.setUpdatedToDate(rangeDates.getUpdatedToDate());
                    tempStrSelectedDate = rangeDates.getFullDate();
                    filterDatePickET.setText(tempStrSelectedDate);

                }
            }
        });
        dialog.show();
    }

}

package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReserveReleasesFilterDialog extends BaseDialog {

    private Context context;
    private Chip openC, releasedC, onHoldC, canceledC;
    private CardView applyFilter;
    private TextView resetFilter;
    private EditText dateRange;
    private LinearLayout dateClick;
    private RangeDates rangeDates;
    private MyApplication objMyApplication;
    private DateRangePickerDialog dateRangePickerDialog;
    private ReserveFilter filter;
    private Long mLastClickTime = 0L, mLastClickTimeFilters = 0L;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String storedSelectDate = "", tempStrSelectedDate = "", strFromDate = "", strToDate = "", strSelectedDate = "", strupdated = "", strended = "", strupdateddate = "", strtoupdateddate = "", strF = "", strT = "";
    private String displayFormat = "MM-dd-yyyy";
    private SimpleDateFormat displayFormatter;
    private Date startDateD = null;
    private Date endDateD = null;


    public ReserveReleasesFilterDialog(Context context, ReserveFilter filter1) {
        super(context);
        this.context = context;
        this.filter = filter1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reserve_filter);
        displayFormatter = new SimpleDateFormat(displayFormat);
        initFields();

    }

    private void initFields() {
        objMyApplication = (MyApplication) context.getApplicationContext();

        openC = findViewById(R.id.OpenC);
        releasedC = findViewById(R.id.releasedC);
        onHoldC = findViewById(R.id.onHoldC);
        canceledC = findViewById(R.id.canceledC);
        applyFilter = findViewById(R.id.applyFilterBtnCV);
        resetFilter = findViewById(R.id.resetFiltersTV);
        dateClick = findViewById(R.id.dateRangePickerLL);
        dateRange = findViewById(R.id.datePickET);


        try {
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
                strSelectedDate = strupdated + " - " + strended;
                if (strSelectedDate != null) {
                    dateRange.setText(strSelectedDate);
                } else {
                    dateRange.setText("");
                }

                SimpleDateFormat rangeFormat = new SimpleDateFormat(DateRangePickerDialog.displayFormat);
                rangeDates = new RangeDates();
                rangeDates.setUpdatedFromDate(rangeFormat.format(startDateD));
                rangeDates.setUpdatedToDate(rangeFormat.format(endDateD));

                //rangeDates.setFullDate(strSelectedDate);
            } else {
                dateRange.setText("");
                dateRange.clearFocus();
                strF = "";
                strT = "";
                strSelectedDate = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (filter.isFilterApplied) {
            openC.setChecked(filter.isOpen());
            onHoldC.setChecked(filter.isOnHold());
            releasedC.setChecked(filter.isReleased());
            canceledC.setChecked(filter.isCancelled());
        } else {
            rangeDates = new RangeDates();
            rangeDates.setUpdatedFromDate("");
            rangeDates.setUpdatedToDate("");
            dateRange.setText("");
            dateRange.clearFocus();
        }

        openC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter.isFilterApplied = true;
                filter.setOpen(isChecked);
            }
        });
        releasedC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter.isFilterApplied = true;
                filter.setReleased(isChecked);
            }
        });
        onHoldC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter.isFilterApplied = true;
                filter.setOnHold(isChecked);
            }
        });
        canceledC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter.isFilterApplied = true;
                filter.setCancelled(isChecked);
            }
        });


        dateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendarDialog();
            }
        });

        dateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendarDialog();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!filter.isFilterApplied) {
                    dismiss();
                    dateRange.setText("");
                    //Toast.makeText(context, "plese select fromdate and todate", Toast.LENGTH_SHORT).show();
                } else {
                    if (getOnDialogClickListener() != null) {
                        getOnDialogClickListener().onDialogClicked("ApplyFilter", filter);
                        dismiss();
                    }
                }
            }
        });

        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //LogUtils.d("Dialogg", "resetFilter" + transactionStatus.size());
                    openC.setChecked(false);
                    onHoldC.setChecked(false);
                    canceledC.setChecked(false);
                    releasedC.setChecked(false);
                    strSelectedDate = "";
                    filter.isFilterApplied = false;
                    strF = "";
                    strT = "";
                    dateRange.setText("");
                    strFromDate = "";
                    strToDate = "";
                    tempStrSelectedDate = "";
                    rangeDates = new RangeDates();
                    rangeDates.setUpdatedToDate("");
                    rangeDates.setUpdatedFromDate("");
                    if (getOnDialogClickListener() != null) {
                        getOnDialogClickListener().onDialogClicked("ResetFilter", filter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showCalendarDialog() {
        dateRangePickerDialog = new DateRangePickerDialog(context, rangeDates);
        dateRangePickerDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(Utils.datePicker)) {
                    filter.isFilterApplied = true;
                    rangeDates = (RangeDates) value;
                    strFromDate = rangeDates.getUpdatedFromDate();
                    strToDate = rangeDates.getUpdatedToDate();
                    filter.setUpdatedFromDate(strFromDate);
                    filter.setUpdatedToDate(strToDate);
                    tempStrSelectedDate = rangeDates.getFullDate();
                    dateRange.setText(tempStrSelectedDate);

                }
            }
        });

        dateRangePickerDialog.show();
    }

}


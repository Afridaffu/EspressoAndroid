package com.greenbox.coyni.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.ReserveReleasesActivity;

import java.util.ArrayList;

public class ReserveReleasesFilterDialog extends BaseDialog {

    private Context context;
    private boolean isFilters = false;
    private Chip openC, releasedC, onHoldC, canceledC;
    private CardView applyFilter;
    private TextView resetFilter, dateRange;
    private LinearLayout dateClick;
    private RangeDates rangeDates;
    private DateRangePickerDialog dateRangePickerDialog;
    private ReserveFilter filter;

    public ReserveReleasesFilterDialog(Context context, ReserveFilter filter) {
        super(context);
        this.context = context;
        this.filter = filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reserve_filter);

        if(filter == null) {
            filter = new ReserveFilter();
        }

        initFields();
    }

    private void initFields() {

        openC = findViewById(R.id.OpenC);
        releasedC = findViewById(R.id.releasedC);
        onHoldC = findViewById(R.id.onHoldC);
        canceledC = findViewById(R.id.canceledC);
        applyFilter = findViewById(R.id.applyFilterBtnCV);
        resetFilter = findViewById(R.id.resetFiltersTV);
        dateClick = findViewById(R.id.dateRangePickerLL);
        dateRange = findViewById(R.id.datePickET);

        if(filter.isFilterApplied) {
            openC.setChecked(filter.isOpen());
            onHoldC.setChecked(filter.isOnHold());
            releasedC.setChecked(filter.isReleased());
            canceledC.setChecked(filter.isCancelled());
            //Show dates
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
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendarDialog();
            }
        });

        dateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendarDialog();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtils.d("TAG","applyFilter"+filter.isFilterApplied);
                LogUtils.d("TAG","applyFilterdateRange"+dateRange);

                if (!filter.isFilterApplied) {
                    dismiss();
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
                //LogUtils.d("Dialogg", "resetFilter" + transactionStatus.size());
                openC.setChecked(false);
                onHoldC.setChecked(false);
                canceledC.setChecked(false);
                releasedC.setChecked(false);
                dateRange.setText("");
                filter.isFilterApplied = false;
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked("ResetFilter", filter);
                }
            }
        });

    }
    private void showCalendarDialog() {

        dateRangePickerDialog = new DateRangePickerDialog(context);
        dateRangePickerDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if(action.equalsIgnoreCase(Utils.datePicker)) {
                    filter.isFilterApplied = true;
                    rangeDates = (RangeDates) value;
                    dateRange.setText(rangeDates.getFullDate());
                }

            }
        });

        dateRangePickerDialog.show();
    }

}

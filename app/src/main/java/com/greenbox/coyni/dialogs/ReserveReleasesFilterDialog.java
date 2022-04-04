package com.greenbox.coyni.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.ReserveReleasesActivity;

import java.util.ArrayList;

public class ReserveReleasesFilterDialog extends BaseDialog {

    private  Activity activity;
    Context context;
    private ArrayList<String> transactionStatus = new ArrayList<>();
    private boolean isFilters = false;
    private Chip openC, releasedC, onHoldC, canceledC;
    private CardView applyFilter;
    private TextView resetFilter, dateRange;
    private LinearLayout dateClick;
    private RangeDates rangeDates;
    private DateRangePickerDialog dateRangePickerDialog;


    public ReserveReleasesFilterDialog(Context context) {
        super(context);
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reserve_filter);
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

        openC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionStatus.add(Utils.open);
                } else {
                    transactionStatus.remove(Utils.open);
                }
            }
        });
        releasedC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionStatus.add(Utils.released);
                } else {
                    transactionStatus.remove(Utils.released);
                }
            }
        });
        onHoldC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionStatus.add(Utils.onhold);
                } else {
                    transactionStatus.remove(Utils.onhold);
                }
            }
        });
        canceledC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionStatus.add(Utils.canceled);
                } else {
                    transactionStatus.remove(Utils.canceled);
                }
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

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFilters = false;

                if (transactionStatus.size() > 0  && dateRange != null) {
                    isFilters = true;

                    // transactionListRequest.settransactionStatus(transactionStatus);
                }

                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked("ApplyFilter", "");
                }
                dismiss();
            }
        });

        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("Dialogg", "resetFilter" + transactionStatus.size());
                //transactionStatus.clear();
                isFilters = false;
                openC.setChecked(false);
                onHoldC.setChecked(false);
                canceledC.setChecked(false);
                releasedC.setChecked(false);
                dateRange.setText("");
            }
        });

    }
    private void showCalendarDialog() {

        dateRangePickerDialog = new DateRangePickerDialog(context);

        dateRangePickerDialog.show();

        dateRangePickerDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if(action.equalsIgnoreCase(Utils.datePicker)) {
                    rangeDates = (RangeDates) value;
                    dateRange.setText(rangeDates.getFullDate());
                }

            }
        });
    }

}

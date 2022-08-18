package com.coyni.mapp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.coyni.mapp.R;

public class ProcessingVolumeDialog extends BaseDialog {

    private TextView tvToday, tvYesterday, tvMonthToDate, tvLastMonth, tvCustomDate;

    public ProcessingVolumeDialog(Context context) {
        super(context);
    }

    private String todayValue = "Today";
    private String yesterdayValue = "Yesterday";
    private String monthDate = "Month to Date";
    private String lastMonthDate = "Last Month";
    private String customDate = "Custom Date Range";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.processing_volume_dialog);

        initFields();

        tvToday.setOnClickListener(v -> setSelectedValue(todayValue));

        tvYesterday.setOnClickListener(v -> setSelectedValue(yesterdayValue));

        tvMonthToDate.setOnClickListener(v -> setSelectedValue(monthDate));

        tvLastMonth.setOnClickListener(v -> setSelectedValue(lastMonthDate));

        tvCustomDate.setOnClickListener(v -> setSelectedValue(customDate));
    }

    private void initFields() {
        tvToday = findViewById(R.id.tv_today);
        tvYesterday = findViewById(R.id.tv_yesterday);
        tvMonthToDate = findViewById(R.id.tv_month_to_date);
        tvLastMonth = findViewById(R.id.tv_last_month);
        tvCustomDate = findViewById(R.id.tv_custom_date);
    }

    private void setSelectedValue(String action) {
        if (getOnDialogClickListener() != null) {
            getOnDialogClickListener().onDialogClicked(action, null);
        }
        dismiss();
    }
}

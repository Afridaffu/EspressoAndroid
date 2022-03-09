package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.greenbox.coyni.R;

public class ProcessingVolumeDialog extends BaseDialog {

    private TextView tvToday, tvYesterday, tvMonthToDate, tvLastMonth, tvCustomDate;

    public ProcessingVolumeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.processing_volume_dialog);

        initFields();

        tvToday.setOnClickListener(v -> setSelectedValue(((TextView) v).getText().toString()));

        tvYesterday.setOnClickListener(v -> setSelectedValue(((TextView) v).getText().toString()));

        tvMonthToDate.setOnClickListener(v -> setSelectedValue(((TextView) v).getText().toString()));

        tvLastMonth.setOnClickListener(v -> setSelectedValue(((TextView) v).getText().toString()));

        tvCustomDate.setOnClickListener(v -> setSelectedValue(((TextView) v).getText().toString()));
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

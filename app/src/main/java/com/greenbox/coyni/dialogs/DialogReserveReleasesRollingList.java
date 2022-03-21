package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;

public class DialogReserveReleasesRollingList extends BaseDialog {

    public DialogReserveReleasesRollingList(@NonNull Context context) {
        super(context);
    }

    public DialogReserveReleasesRollingList(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_filter);
        initFields();
    }

    private void initFields() {
        Chip OpenC = findViewById(R.id.OpenC);
        Chip releasedC = findViewById(R.id.releasedC);
        Chip onHoldC = findViewById(R.id.onHoldC);
        Chip canceledC = findViewById(R.id.canceledC);
    }
}

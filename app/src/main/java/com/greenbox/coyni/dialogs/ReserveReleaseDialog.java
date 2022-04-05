package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.greenbox.coyni.R;

public class ReserveReleaseDialog extends BaseDialog {

    private LinearLayout rolling, manual;
    private Context context;
    private ImageView rollingIV,manualIV;
    private static final String reserveRolling = "Rolling";
    private static final String reserveManual = "Manual";

    public ReserveReleaseDialog(@NonNull Context context) {
        super(context);
    }

    public ReserveReleaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reserve_release);

        rolling = findViewById(R.id.llRolling);
        manual = findViewById(R.id.llManual);
        rollingIV = findViewById(R.id.rollingIV);
        manualIV = findViewById(R.id.manualIV);

        rolling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollingIV.setVisibility(View.VISIBLE);
                manualIV.setVisibility(View.GONE);
                getOnDialogClickListener().onDialogClicked(reserveRolling, null);
            }
        });
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualIV.setVisibility(View.VISIBLE);
                rollingIV.setVisibility(View.GONE);
                getOnDialogClickListener().onDialogClicked(reserveManual, null);
            }
        });

    }
}

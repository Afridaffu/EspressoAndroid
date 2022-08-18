package com.coyni.mapp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.coyni.mapp.R;

public class ReserveReleaseDialog extends BaseDialog {

    private LinearLayout rolling, manual;
    private Context context;
    private ImageView rollingIV,manualIV;
    private static final String reserveRolling = "Rolling";
    private static final String reserveManual = "Manual";
    private boolean isRolling = true;

    public ReserveReleaseDialog(@NonNull Context context, boolean isRolling) {
        super(context);
        this.isRolling = isRolling;
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

        if(isRolling) {
            rollingIV.setVisibility(View.VISIBLE);
            manualIV.setVisibility(View.GONE);
        } else {
            rollingIV.setVisibility(View.GONE);
            manualIV.setVisibility(View.VISIBLE);
        }

        rolling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRolling) {
                    rollingIV.setVisibility(View.VISIBLE);
                    manualIV.setVisibility(View.GONE);
                    getOnDialogClickListener().onDialogClicked(reserveRolling, null);
                }
            }
        });
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRolling) {
                    manualIV.setVisibility(View.VISIBLE);
                    rollingIV.setVisibility(View.GONE);
                    getOnDialogClickListener().onDialogClicked(reserveManual, null);
                }
            }
        });

    }
}

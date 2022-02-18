package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;

public class ReserveReleasesActivity extends AppCompatActivity {

    ImageView ivFilterIcon;
    LinearLayout closeBtnIV, rollingLL;
    boolean isFilter = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        closeBtnIV = findViewById(R.id.closeBtnIV);
        ivFilterIcon = findViewById(R.id.ivFilterIcon);
        rollingLL = findViewById(R.id.rollingLL);

        closeBtnIV.setOnClickListener(view -> onBackPressed());
        ivFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersPopup();
            }
        });
        rollingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ReserveReleasesActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.reserve_manual_dialog);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.gravity = Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                LinearLayout llRolling = dialog.findViewById(R.id.llRolling);
                LinearLayout llManualLL = dialog.findViewById(R.id.llManual);

                llRolling.setOnClickListener(view ->
                        startActivity(new Intent(ReserveReleasesActivity.this, ReserveReleasesActivity.class))
                );
                llManualLL.setOnClickListener(view ->
                        startActivity(new Intent(ReserveReleasesActivity.this, ManualReleasesActivity.class)));

            }
        });

    }

    private void showFiltersPopup() {
        Dialog dialog = new Dialog(ReserveReleasesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
        dialog.setContentView(R.layout.activity_reserve_filter);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.gravity = Gravity.BOTTOM;
        wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wl);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Chip OpenC = dialog.findViewById(R.id.OpenC);
        Chip releasedC = dialog.findViewById(R.id.releasedC);
        Chip onHoldC = dialog.findViewById(R.id.onHoldC);
        Chip canceledC = dialog.findViewById(R.id.canceledC);
    }
}
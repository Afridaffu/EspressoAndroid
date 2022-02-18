package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class ManualReleasesActivity extends AppCompatActivity {

    private LinearLayout llx, manualReleasesLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_releases);

        llx = findViewById(R.id.llx);
        manualReleasesLL= findViewById(R.id.manualReleasesLL);

        llx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManualReleasesActivity.this, BusinessDashboardActivity.class));
            }
        });
        manualReleasesLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ManualReleasesActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.reserve_manual_dialog);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl=window.getAttributes();
                wl.gravity= Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                LinearLayout llRolling = dialog.findViewById(R.id.llRolling);
                LinearLayout llManualLL = dialog.findViewById(R.id.llManual);

                llRolling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ManualReleasesActivity.this, ReserveReleasesActivity.class));
                    }
                });
                llManualLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ManualReleasesActivity.this, ManualReleasesActivity.class));
                    }
                });

            }
        });
    }
}
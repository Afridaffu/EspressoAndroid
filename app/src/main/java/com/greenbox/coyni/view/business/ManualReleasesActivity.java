package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.ManualDialog;
import com.greenbox.coyni.view.BaseActivity;

public class ManualReleasesActivity extends BaseActivity {

    private LinearLayout llx, manualReleasesLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_releases);

        llx = findViewById(R.id.llx);
        manualReleasesLL = findViewById(R.id.manualReleasesLL);

        llx.setOnClickListener(v -> startActivity(new Intent(ManualReleasesActivity.this, BusinessDashboardActivity.class)));
        manualReleasesLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualDialog dialog = new ManualDialog(ManualReleasesActivity.this);
                dialog.show();
                LinearLayout llRolling = dialog.findViewById(R.id.llRolling);
                LinearLayout llManualLL = dialog.findViewById(R.id.llManual);

                llRolling.setOnClickListener(view -> startActivity(new Intent(ManualReleasesActivity.this, ReserveReleasesActivity.class)));
                llManualLL.setOnClickListener(view -> dialog.dismiss());

            }
        });
    }
}
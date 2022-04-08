package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.view.BaseActivity;

public class BusinessApplicationApprovedActivity extends BaseActivity {

    private TextView businessApprovedDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_application_approved);

        businessApprovedDecline = findViewById(R.id.cardDeclined);

        businessApprovedDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeclineDialog();
            }
        });

    }

    private void showDeclineDialog() {
        DialogAttributes attributes = new DialogAttributes(getString(R.string.decline_reserve_rules),
                getString(R.string.decline_reserve_rules_message), getString(R.string.yes),
                getString(R.string.no_go_back));
        CustomConfirmationDialog dialog = new CustomConfirmationDialog(BusinessApplicationApprovedActivity.this, attributes);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(getString(R.string.yes))) {

                }
            }
        });

    }
}
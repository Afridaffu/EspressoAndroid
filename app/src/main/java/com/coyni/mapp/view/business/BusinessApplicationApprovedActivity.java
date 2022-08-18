package com.coyni.mapp.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.dialogs.CustomConfirmationDialog;
import com.coyni.mapp.dialogs.OnDialogClickListener;
import com.coyni.mapp.model.DialogAttributes;
import com.coyni.mapp.view.BaseActivity;

public class BusinessApplicationApprovedActivity extends BaseActivity {

    private TextView businessApprovedDecline;
    private ImageView i_iconIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_application_approved);
//        ApplicationApprovedDialog dialog = new ApplicationApprovedDialog(BusinessApplicationApprovedActivity.this, null);

        businessApprovedDecline = findViewById(R.id.cardDeclined);
        i_iconIV = findViewById(R.id.i_iconIV);

        i_iconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.show();
            }
        });

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
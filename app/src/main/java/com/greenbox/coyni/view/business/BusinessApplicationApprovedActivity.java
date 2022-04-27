package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.AddCommentsDialog;
import com.greenbox.coyni.dialogs.ApplicationApprovedDialog;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;

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
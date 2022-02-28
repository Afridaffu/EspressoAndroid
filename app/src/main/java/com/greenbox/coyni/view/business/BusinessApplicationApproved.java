package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class BusinessApplicationApproved extends AppCompatActivity {

    private TextView businessApprovedDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_application_approved);

        businessApprovedDecline = findViewById(R.id.business_approved_decline);

        businessApprovedDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.displayAlertDecline("Are you sure you want to decline this \\nreserve rule and cancel your merchant \\napplication? You will need to complete \\nanother application to apply again.",
                        BusinessApplicationApproved.this, getResources().getString(R.string.decline_reserve_rules), "");

            }
        });

    }
}
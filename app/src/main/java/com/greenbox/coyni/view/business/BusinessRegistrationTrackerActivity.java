package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class BusinessRegistrationTrackerActivity extends AppCompatActivity {
TextView start,start2,start3,start4,start5;
Dialog choose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_tracker_account);

        start = findViewById(R.id.startTV);
        start2 = findViewById(R.id.startTV2);
        start3= findViewById(R.id.startTV3);
        start4 = findViewById(R.id.startTV4);
        start5 = findViewById(R.id.startTV5);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, CompanyInformationActivity.class);
                startActivity(intent);
            }
        });
        start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbaBotmsheetPopUp(BusinessRegistrationTrackerActivity.this);
            }
        });


        start3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AddBenificialOwnerActivity.class);
                startActivity(intent);
            }
        });
        start4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, AccountHasCreatedSucessful.class);
                startActivity(intent);
            }
        });
        start5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, MerchantsAgrementActivity.class);
                startActivity(intent);
               }
        });

    }

    private void dbaBotmsheetPopUp(final Context context) {
        try {
            choose = new Dialog(context);
            choose.requestWindowFeature(Window.FEATURE_NO_TITLE);
            choose.setContentView(R.layout.activity_dbainfo_btm_sheet);
            choose.setCancelable(true);
            Window window = choose.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            choose.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            choose.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            choose.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Intent intent = new Intent(BusinessRegistrationTrackerActivity.this, DBAbasicInformationAcivity.class);
        startActivity(intent);

    }
}
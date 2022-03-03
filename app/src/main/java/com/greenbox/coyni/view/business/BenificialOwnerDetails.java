package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

public class BenificialOwnerDetails extends BaseActivity {
    private ProgressBar mProgress;
    private TextView mPercentage, mMailingAddress, mSSN, mName, mDob;
    private String firstName = "", lastName = "", address = "", dob = "", ssn = "";
    private int percentage = 0, position = 0;
    private LinearLayout bpbackBtn, primaryLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benificial_owner_details);
        Bundle bundle = getIntent().getExtras();
        firstName = bundle.getString(Utils.boFirstName, firstName);
        lastName = bundle.getString(Utils.boLastName, lastName);
        percentage = bundle.getInt(String.valueOf(Utils.boOwnershipPercentage), percentage);
        address = bundle.getString(Utils.boAddress, address);
        dob = bundle.getString(Utils.boDob, dob);
        ssn = bundle.getString(Utils.boSSN, ssn);
        position = bundle.getInt(String.valueOf(Utils.position), position);
        initFields();
    }

    private void initFields() {
        bpbackBtn = findViewById(R.id.backLL);
        mProgress = findViewById(R.id.progress_bar);
        mPercentage = findViewById(R.id.percentage);
        mMailingAddress = findViewById(R.id.address_bo);
        mSSN = findViewById(R.id.ssn_bo);
        mName = findViewById(R.id.owner_name);
        mDob = findViewById(R.id.dob_bo);
        primaryLL = findViewById(R.id.primaryLL);
        if (position != 0) {
            primaryLL.setVisibility(View.GONE);
        } else {
            primaryLL.setVisibility(View.VISIBLE);
        }
        bpbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (firstName != null || lastName != null) {
            mName.setText(firstName + "" + lastName);
        }

        mProgress.setProgress(percentage);
        mPercentage.setText(percentage + getResources().getString(R.string.percentage));
        if (dob != null) {
            mDob.setText(dob);
        }
        if (address != null) {
            mMailingAddress.setText(address);
        }
        if (ssn != null) {
            mSSN.setText(ssn);
        }

    }
}
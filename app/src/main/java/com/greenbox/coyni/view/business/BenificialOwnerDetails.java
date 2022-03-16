package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

public class BenificialOwnerDetails extends BaseActivity {
    private ProgressBar mProgress;
    private TextView mPercentage, mMailingAddress, mSSN, mName, mDob;
    private String name = "", address = "", dob = "", ssn = "", address1 = "", city = "", state = "", country = "", zipcode = "";
    private int percentage = 0, position = 0;
    private LinearLayout bpbackBtn, primaryLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benificial_owner_details);
        BOResp.BeneficialOwner beneficialOwner = (BOResp.BeneficialOwner) getIntent().getSerializableExtra(Utils.boName);
       // position = bundle.getInt(Utils.position, position);
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
        if (name != null && !name.equals("")) {
            mName.setText(name);
        }

        mProgress.setProgress(percentage);
        mPercentage.setText(percentage + getResources().getString(R.string.percentage));
        if (dob != null && !dob.equals("")) {
            mDob.setText(Utils.convertTxnDatebusiness(dob));
        }
        if (address != null && !address.equals("")) {
            mMailingAddress.setText(address);
        }
        if (address1 != null && !address1.equals("")) {
            mMailingAddress.append(", " + address1);
        }
        if (city != null && !city.equals("")) {
            mMailingAddress.append(", " + city);
        }
        if (state != null && !state.equals("")) {
            mMailingAddress.append(", " + state);
        }
        if (country != null && !country.equals("")) {
            mMailingAddress.append(", " + country);
        }
        if (zipcode != null && !zipcode.equals("")) {
            mMailingAddress.append(", " + zipcode);
        }
        String converted = ssn.replaceAll("\\w(?=\\w{2})", ".");
        String hifened = converted.substring(0, 3) + "-" + converted.substring(3, 5) + "-" + converted.substring(5, converted.length());
        if (ssn != null && !ssn.equals("")) {
            mSSN.setText(hifened);
        }

    }
}
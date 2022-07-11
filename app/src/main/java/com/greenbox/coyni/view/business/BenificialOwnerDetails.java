package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

public class BenificialOwnerDetails extends BaseActivity {
    private ProgressBar mProgress;
    private TextView mPercentage, mMailingAddress, mSSN, mName, mDob;
    private String firstname = "", lastname = "", ssn = "";
    private int position = 0;
    private LinearLayout bpbackBtn, primaryLL;
    private BOResp.BeneficialOwner beneficialOwner;
    private MyApplication myApplication;
    public String state = "", convert = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_benificial_owner_details);
        beneficialOwner = (BOResp.BeneficialOwner) getIntent().getSerializableExtra(Utils.boData);
        position = getIntent().getIntExtra(Utils.position, position);
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
        myApplication = (MyApplication) getApplicationContext();

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
        if (beneficialOwner.getFirstName() != null && !beneficialOwner.getFirstName().equals("")) {
            firstname = beneficialOwner.getFirstName();
        }
        if (beneficialOwner.getLastName() != null && !beneficialOwner.getLastName().equals("")) {
            lastname = beneficialOwner.getLastName();
        }
        mName.setText(firstname + " " + lastname);
        mProgress.setProgress(beneficialOwner.getOwnershipParcentage());
        mPercentage.setText(beneficialOwner.getOwnershipParcentage() + getResources().getString(R.string.percentage));
        if (beneficialOwner.getDob() != null && !beneficialOwner.getDob().equals("")) {
            mDob.setText(Utils.convertTxnDatebusiness(beneficialOwner.getDob()));
        }
        if (beneficialOwner.getAddressLine1() != null && !beneficialOwner.getAddressLine1().equals("")) {
            mMailingAddress.setText(beneficialOwner.getAddressLine1());
        }
        if (beneficialOwner.getAddressLine2() != null && !beneficialOwner.getAddressLine2().equals("")) {
            mMailingAddress.append(", " + beneficialOwner.getAddressLine2());
        }
        if (beneficialOwner.getCity() != null && !beneficialOwner.getCity().equals("")) {
            mMailingAddress.append(", " + beneficialOwner.getCity());
        }
//        if (beneficialOwner.getState() != null && !beneficialOwner.getState().equals("")) {
//            mMailingAddress.append(", " + beneficialOwner.getState());
//        }
        if (beneficialOwner.getState() != null && !beneficialOwner.getState().equals("")) {
            state = beneficialOwner.getState().toLowerCase();
            String stateCode = Utils.getStateCode(state, myApplication.getListStates());
            if (stateCode != null && !stateCode.equals("")) {
                mMailingAddress.append(", " + stateCode);
            }
        }
        if (beneficialOwner.getCountry() != null && !beneficialOwner.getCountry().equals("")) {
            mMailingAddress.append(", " + beneficialOwner.getCountry());
        }
        if (beneficialOwner.getZipCode() != null && !beneficialOwner.getZipCode().equals("")) {
            mMailingAddress.append(", " + beneficialOwner.getZipCode() + ".");
        }
        if (beneficialOwner.getSsn() != null && !beneficialOwner.getSsn().equals("")) {
            convert = beneficialOwner.getSsn().replaceAll("\\-", "");
            String converted = convert.replaceAll("\\w(?=\\w{2})", "•");
            String hifened = converted.substring(0, 3) + " - " + converted.substring(3, 5) + " - " + converted.substring(5);
            mSSN.setText(hifened);
        }

    }
}
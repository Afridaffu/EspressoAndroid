package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

public class ChangeEmail extends BaseActivity {
    private TextView emailTx, titleTx, currentTitleTx;
    private String companyEmail = "", companyPhone = "",  companyCountryCode = "";
    private int changeEmail = 0, companyId = 0;
    private CardView mChange;
    private LinearLayout closeLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        companyEmail = bundle.getString(Utils.companyEmail, companyEmail);
        companyPhone = bundle.getString(Utils.companyNumber, companyPhone);
        changeEmail = bundle.getInt(Utils.changeEdit, changeEmail);
        companyCountryCode = bundle.getString(Utils.comCountryCode, companyCountryCode);
        companyId = bundle.getInt(String.valueOf(Utils.companyId), companyId);
        setContentView(R.layout.activity_change_email);
        initFields();

    }

    private void initFields() {
        closeLL = findViewById(R.id.bpCloseLL);
        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        emailTx = (TextView) findViewById(R.id.company_email);
        titleTx = (TextView) findViewById(R.id.title);
        currentTitleTx = (TextView) findViewById(R.id.currentTitle);
        mChange = (CardView) findViewById(R.id.nextCV);
        if (changeEmail == 1) {
            emailTx.setText(companyEmail);
            titleTx.setText("Email");
            currentTitleTx.setText("Current Email");
        } else {
            emailTx.setText(companyPhone);
            titleTx.setText("Phone Number");
            currentTitleTx.setText("Current Phone Number");
        }

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeEmail.this, EditEmail.class);
                intent.putExtra(Utils.companyEmail, companyEmail);
                intent.putExtra(Utils.companyNumber, companyPhone);
                intent.putExtra(Utils.comCountryCode, companyCountryCode);
                intent.putExtra(String.valueOf(Utils.companyId), companyId);
                intent.putExtra(Utils.changeEdit, changeEmail);
                startActivity(intent);
            }
        });
    }

}
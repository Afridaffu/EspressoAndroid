package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class ChangeEmail extends BaseActivity {
    private TextView emailTx, titleTx, currentTitleTx;
    private String companyEmail = "", companyPhone = "";
    private int changeEmail = 0;
    private CardView mChange;
    private LinearLayout closeLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        companyEmail = bundle.getString("CompanyEmail");
        companyPhone = bundle.getString("CompanyPhone");
        changeEmail = bundle.getInt("ChangeEmail");
        setContentView(R.layout.activity_change_email);
        initFields();

    }

    private void initFields() {
        closeLL = findViewById(R.id.bpCloseLL);
        closeLL.setOnClickListener(v -> finish());
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
                intent.putExtra("CompanyEmail", companyEmail);
                intent.putExtra("CompanyPhone", companyPhone);
                intent.putExtra("ChangeEmail", changeEmail);
                startActivity(intent);
            }
        });
    }

}
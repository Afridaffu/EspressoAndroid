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
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class ChangeEmail extends AppCompatActivity {
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private TextView emailTx,titleTx,currentTitleTx;
    private String companyEmail="",companyPhone="";
    int changeEmail=0;
    private CardView mChange;
    LinearLayout closeLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = getSharedPreferences("", 0);
        mEditor = mSharedPref.edit();
        Bundle bundle=getIntent().getExtras();
        companyEmail=bundle.getString("CompanyEmail");
        companyPhone = bundle.getString("CompanyPhone");
        changeEmail=bundle.getInt("ChangeEmail");
        setContentView(R.layout.activity_change_email);
        closeLL = findViewById(R.id.bpCloseLL);
        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeEmail.this, CompanyInfoDetails.class);

                startActivity(intent);
            }
        });
        initFields();
        initObserver();

    }

    private void initFields() {
        emailTx = (TextView) findViewById(R.id.company_email);
        titleTx=(TextView)findViewById(R.id.title);
        currentTitleTx=(TextView)findViewById(R.id.currentTitle);
        mChange = (CardView) findViewById(R.id.nextCV);
        if (changeEmail == 1) {
            emailTx.setText(companyEmail);
            titleTx.setText("Email");
            currentTitleTx.setText("Current Email");
        }
        else{
            emailTx.setText(companyPhone);
            titleTx.setText("Phone Number");
            currentTitleTx.setText("Current Phone Number");
        }

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChangeEmail.this, EditEmail.class);
                intent.putExtra("CompanyEmail",companyEmail);
                intent.putExtra("CompanyPhone",companyPhone);
                intent.putExtra("ChangeEmail",changeEmail);
                startActivity(intent);
               // companyInfoAPICall(prepareRequest());
            }
        });
    }
    private void initObserver() {

    }
    public void companyInfoAPICall(CompanyInfoRequest companyInfoRequest) {
        businessIdentityVerificationViewModel.patchCompanyInfo(companyInfoRequest);
    }
    public CompanyInfoRequest prepareRequest() {
        CompanyInfoRequest companyInfoRequest = new CompanyInfoRequest();

        try {
               if(changeEmail==1){
                   companyInfoRequest.setEmail(emailTx.getText().toString());
               }
               else{
                   PhNoWithCountryCode phone = new PhNoWithCountryCode();
                   phone.setCountryCode(Utils.strCCode);
                   phone.setPhoneNumber(emailTx.getText().toString());
                   companyInfoRequest.setPhoneNumberDto(phone);
               }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyInfoRequest;
    }
}
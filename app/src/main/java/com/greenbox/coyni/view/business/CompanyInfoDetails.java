package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class CompanyInfoDetails extends BaseActivity {
    LinearLayout closeLL,emailLL,phoneLL;
    TextView mEmailTx,mPhoneNumberTx,mAddressTx;
    private String companyEmail="",companyPhone="";
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_details);
        initFields();
        initObservers();

    }


    private void initFields() {
        closeLL = findViewById(R.id.bpCloseLL);
        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyInfoDetails.this, BusinessProfileActivity.class);

                startActivity(intent);
            }
        });
        mEmailTx=(TextView) findViewById(R.id.emailTx);
        mPhoneNumberTx=(TextView) findViewById(R.id.phoneNumberTx);
        mAddressTx=(TextView) findViewById(R.id.addressTx);
        emailLL=(LinearLayout)findViewById(R.id.emailLL);
        phoneLL=(LinearLayout)findViewById(R.id.phoneLL);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getCompanyInfo();

        emailLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyInfoDetails.this, ChangeEmail.class);
                intent.putExtra("CompanyEmail",companyEmail);
                intent.putExtra("CompanyPhone",companyPhone);
                intent.putExtra("ChangeEmail",1);
                startActivity(intent);
            }
        });
        phoneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyInfoDetails.this, ChangeEmail.class);
                intent.putExtra("CompanyEmail",companyEmail);
                intent.putExtra("CompanyPhone",companyPhone);
                intent.putExtra("ChangeEmail",2);
                startActivity(intent);
            }
        });
    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getGetCompanyInfoResponse().observe(this, new Observer<CompanyInfoResp>() {
                @Override
                public void onChanged(CompanyInfoResp companyInfoResp) {
                    if (companyInfoResp != null) {
                        if (companyInfoResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                CompanyInfoResp.Data cir = companyInfoResp.getData();

                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    mEmailTx.setText(cir.getEmail());
                                    companyEmail=cir.getEmail();
                                }

                                if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
                                    mPhoneNumberTx.setText(cir.getPhoneNumberDto().getPhoneNumber());
                                    companyPhone=cir.getPhoneNumberDto().getPhoneNumber();
                                }

                                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                                    mAddressTx.setText(cir.getAddressLine1());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
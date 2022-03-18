package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessUserDetailsPreviewActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class CompanyInfoDetails extends BaseActivity {
    private LinearLayout closeLL, emailLL, phoneLL;
    private TextView mEmailTx, mPhoneNumberTx, mAddressTx, nameTX, mBusinessEntity;
    private String companyEmail = "", companyPhone = "", companyCountryCode = "";
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private int companyId = 0;
    private String strName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_details);
        initFields();
        initObservers();

    }


    private void initFields() {
        closeLL = findViewById(R.id.bpCloseLL);
        mEmailTx = (TextView) findViewById(R.id.emailTx);
        mPhoneNumberTx = (TextView) findViewById(R.id.phoneNumberTx);
        mAddressTx = (TextView) findViewById(R.id.addressTx);
        nameTX = (TextView) findViewById(R.id.name_id);
        mBusinessEntity = (TextView) findViewById(R.id.business_entity);
        emailLL = (LinearLayout) findViewById(R.id.emailLL);
        phoneLL = (LinearLayout) findViewById(R.id.phoneLL);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessIdentityVerificationViewModel.getCompanyInfo();

        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        emailLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyInfoDetails.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "CompanyInfo").putExtra("action", "EditEmailCompany").putExtra("value", companyEmail));
            }
        });

        phoneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyInfoDetails.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "CompanyInfo").putExtra("action", "EditPhoneCompany").putExtra("value", companyPhone));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        businessIdentityVerificationViewModel.getCompanyInfo();
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

                                if (cir.getName() != null && !cir.getName().equals("")) {
                                    strName = cir.getName();
                                    if (strName != null && strName.length() > 20) {
                                        nameTX.setText(strName.substring(0, 20) + "...");
                                    } else {
                                        nameTX.setText(strName);
                                    }
                                    nameTX.setOnClickListener(view -> {

                                        if (nameTX.getText().toString().contains("...") && strName.length() >= 21) {
                                            nameTX.setText(strName);

                                        } else {
                                            nameTX.setText(strName.substring(0, 20) + "...");
                                        }
                                    });
                                }
                                if (cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")) {
                                    mBusinessEntity.setText(cir.getBusinessEntity());
                                }

                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    mEmailTx.setText(cir.getEmail());
                                    companyEmail = cir.getEmail();
                                }

                                if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
                                    mPhoneNumberTx.setText("(" + cir.getPhoneNumberDto().getPhoneNumber().substring(0, 3) + ") " + cir.getPhoneNumberDto().getPhoneNumber().substring(3, 6) + "-" + cir.getPhoneNumberDto().getPhoneNumber().substring(6, 10));
                                    companyPhone = mPhoneNumberTx.getText().toString();
                                }

                                if (cir.getPhoneNumberDto().getCountryCode() != null && !cir.getPhoneNumberDto().getCountryCode().equals("")) {
                                    companyCountryCode = cir.getPhoneNumberDto().getCountryCode();
                                }
                                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                                    mAddressTx.setText(cir.getAddressLine1() + cir.getAddressLine2());
                                }
                                if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                                    mAddressTx.append(", " + cir.getAddressLine2());
                                }
                                if (cir.getCity() != null && !cir.getCity().equals("")) {
                                    mAddressTx.append(", " + cir.getCity());
                                }
                                if (cir.getState() != null && !cir.getState().equals("")) {
                                    mAddressTx.append(", " + cir.getState());
                                }
                                if (cir.getCountry() != null && !cir.getCountry().equals("")) {
                                    mAddressTx.append(", " + cir.getCountry());
                                }
                                if (cir.getZipCode() != null && !cir.getZipCode().equals("")) {
                                    mAddressTx.append(", " + cir.getZipCode());
                                }

                                companyId = cir.getId();

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
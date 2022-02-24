package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoUpdateResp;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class EditEmail extends AppCompatActivity {

    TextView mEditTitle;
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    private String companyEmail,companyPhone;
    int changeEmail=0;
    CardView mSave;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    LinearLayout closeLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email2);
        closeLL = findViewById(R.id.bpCloseLL);
        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditEmail.this, ChangeEmail.class);
                startActivity(intent);
            }
        });
        Bundle bundle=getIntent().getExtras();
        companyEmail=bundle.getString("CompanyEmail");
        companyPhone = bundle.getString("CompanyPhone");
        changeEmail=bundle.getInt("ChangeEmail");
        initFields();
        intObservers();
    }

    private void initFields() {
        mEditTitle=(TextView) findViewById(R.id.editTitle);
        textInputLayout=(TextInputLayout) findViewById(R.id.textInputLayout);
        textInputEditText=(TextInputEditText) findViewById(R.id.companyEmailET);
        mSave=(CardView) findViewById(R.id.saveCh);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessIdentityVerificationViewModel.postCompanyInfo(prepareRequest());
                if(changeEmail==1) {
                    Utils.showCustomToast(EditEmail.this, "Email Updated.", R.drawable.ic_custom_tick, "");
                    finish();
                }
                else{
                    Utils.showCustomToast(EditEmail.this, "Phone Number Updated.", R.drawable.ic_custom_tick, "");
                    finish();
                }
            }
        });
        if (changeEmail==1) {
            mEditTitle.setText("Edit Email");
            textInputLayout.setHint("Enter New Email");
            textInputEditText.setText(companyEmail);
        }
        else{
            mEditTitle.setText("Edit Phone Number");
            textInputLayout.setHint("Enter New Phone Number");
            textInputEditText.setText(companyPhone);
        }
    }
    public CompanyInfoRequest prepareRequest() {
        CompanyInfoRequest companyInfoRequest = new CompanyInfoRequest();

        try {
            if(changeEmail==1) {
                companyInfoRequest.setEmail(textInputEditText.getText().toString());
            }
            else {
                PhNoWithCountryCode phone = new PhNoWithCountryCode();
                phone.setCountryCode(Utils.strCCode);
                phone.setPhoneNumber(textInputEditText.getText().toString());
                companyInfoRequest.setEmail(textInputEditText.getText().toString());
                companyInfoRequest.setPhoneNumberDto(phone);
            }
            //Address
        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyInfoRequest;
    }

    private void intObservers() {

        try {
            businessIdentityVerificationViewModel.getPostCompanyInfoResponse().observe(this, new Observer<CompanyInfoUpdateResp>() {
                @Override
                public void onChanged(CompanyInfoUpdateResp companyInfoResponse) {

                    if (companyInfoResponse != null) {
                        if (companyInfoResponse.getStatus().toLowerCase().toString().equals("success")) {
                            finish();
                        } else {
                            Utils.displayAlert(companyInfoResponse.getError().getErrorDescription(),
                                    EditEmail.this, "", companyInfoResponse.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
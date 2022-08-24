package com.coyni.mapp.view.business;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoUpdateResp;
import com.coyni.mapp.model.CompanyInfo.ContactInfoRequest;
import com.coyni.mapp.model.register.PhNoWithCountryCode;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;

public class EditEmail extends BaseActivity {

    private TextView mEditTitle;
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    private String companyEmail = "", companyPhone = "", companyCountryCode = "";
    private int changeEmail = 0, companyId = 0;
    private CardView mSave;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private LinearLayout closeLL;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email2);
        Bundle bundle = getIntent().getExtras();
        companyEmail = bundle.getString(Utils.companyEmail, companyEmail);
        companyPhone = bundle.getString(Utils.companyNumber, companyPhone);
        changeEmail = bundle.getInt(Utils.changeEdit, changeEmail);
        companyCountryCode = bundle.getString(Utils.comCountryCode, companyCountryCode);
        companyId = bundle.getInt(String.valueOf(Utils.companyId), companyId);
        initFields();
        intObservers();
    }

    private void initFields() {
        closeLL = findViewById(R.id.bpCloseLL);
        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mEditTitle = (TextView) findViewById(R.id.editTitle);
        textInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);
        textInputEditText = (TextInputEditText) findViewById(R.id.companyEmailET);
        if (!Utils.isKeyboardVisible)
            Utils.shwForcedKeypad(EditEmail.this);
        mSave = (CardView) findViewById(R.id.saveCh);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = Utils.showProgressDialog(EditEmail.this);
                companyInfoUpdateAPICall(prepareRequest());
            }
        });
        if (changeEmail == 1) {
            mEditTitle.setText("Edit Email");
            textInputLayout.setHint("Enter New Email");
            textInputEditText.setText(companyEmail);
        } else {
            mEditTitle.setText("Edit Phone Number");
            textInputLayout.setHint("Enter New Phone Number");
            textInputEditText.setText(companyPhone);
        }
    }

    public void companyInfoUpdateAPICall(ContactInfoRequest contactInfoRequest) {
        businessIdentityVerificationViewModel.updateCompanyInfo(contactInfoRequest);
    }

    public ContactInfoRequest prepareRequest() {
        ContactInfoRequest contactInfoRequest = new ContactInfoRequest();
        progressDialog.dismiss();
        try {
            if (changeEmail == 1) {
                String email = textInputEditText.getText().toString();
                contactInfoRequest.setEmail(email);
                PhNoWithCountryCode phone = new PhNoWithCountryCode();
                phone.setCountryCode(companyCountryCode);
                phone.setPhoneNumber(companyPhone);
                contactInfoRequest.setPhoneNumberDto(phone);

            } else {
                String phoneNumber = textInputEditText.getText().toString();
                PhNoWithCountryCode phone = new PhNoWithCountryCode();
                phone.setCountryCode(companyCountryCode);
                phone.setPhoneNumber(phoneNumber);
                contactInfoRequest.setPhoneNumberDto(phone);
                contactInfoRequest.setEmail(companyEmail);
            }
            contactInfoRequest.setId(companyId);
            //Address
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactInfoRequest;
    }

    private void intObservers() {

        try {
            businessIdentityVerificationViewModel.getContactInfoUpdateResponse().observe(this, new Observer<CompanyInfoUpdateResp>() {
                @Override
                public void onChanged(CompanyInfoUpdateResp companyInfoResponse) {

                    if (companyInfoResponse != null) {
                        if (companyInfoResponse.getStatus().toLowerCase().toString().equals("success")) {
                            if (changeEmail == 1) {
                                Utils.showCustomToast(EditEmail.this, "Email Updated.", R.drawable.ic_custom_tick, "");
                                finish();
                            } else {
                                Utils.showCustomToast(EditEmail.this, "Phone Number Updated.", R.drawable.ic_custom_tick, "");
                                finish();
                            }
                        } else {
                            Utils.displayAlert(companyInfoResponse.getError().getErrorDescription(),
                                    EditEmail.this, "", companyInfoResponse.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Toast.makeText(EditEmail.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
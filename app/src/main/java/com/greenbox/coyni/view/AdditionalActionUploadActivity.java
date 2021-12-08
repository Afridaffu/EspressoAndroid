package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.identity_verification.AddressObj;
import com.greenbox.coyni.model.identity_verification.GetIdentityResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.PhotoIDEntityObject;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.santalu.maskara.widget.MaskEditText;

public class AdditionalActionUploadActivity extends AppCompatActivity {
    MaskEditText ssnET;
    CardView idveriDone;
    boolean isssn = false, isSubmitEnabled = false;
    LinearLayout ssnCloseLL, ssnErrorLL;
    TextView ssnErrorTV;
    IdentityVerificationViewModel identityVerificationViewModel;
    GetIdentityResponse IDVEResponse;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_action_upload);
        ssnCloseLL = findViewById(R.id.ssnCloseLL);
        ssnET = findViewById(R.id.ssnET);
        ssnErrorLL = findViewById(R.id.ssnErrorLL);
        ssnErrorTV = findViewById(R.id.ssnErrorTV);

        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

        identityVerificationViewModel.getIdentityData();

        ssnCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdditionalActionUploadActivity.this, DashboardActivity.class));
            }
        });

        ssnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 11) {
                    isssn = true;
                    isSubmitEnabled = true;
                } else {
                    isssn = false;
                    isSubmitEnabled = false;
                }
                enableORdiableNext();

            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        idveriDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSubmitEnabled) {
                    dialog = Utils.showProgressDialog(AdditionalActionUploadActivity.this);
                    IdentityAddressRequest identityAddressRequest = new IdentityAddressRequest();
                    identityAddressRequest.setFirstName(IDVEResponse.getData().getFirstName());
                    identityAddressRequest.setLastName(IDVEResponse.getData().getLastName());
                    identityAddressRequest.setPhoneNumber(IDVEResponse.getData().getPhoneNumber());
                    identityAddressRequest.setEmail(IDVEResponse.getData().getEmail());
                    identityAddressRequest.setDateOfBirth(IDVEResponse.getData().getDateOfBirth());
                    //Unmask SSN
                    identityAddressRequest.setSsn(ssnET.getText().toString().trim());

                    AddressObj addressObj = new AddressObj();
                    addressObj.setAddressLine1(IDVEResponse.getData().getUseraddress().getAddressLine1());
                    addressObj.setAddressLine2(IDVEResponse.getData().getUseraddress().getAddressLine2());
                    addressObj.setAddressType(IDVEResponse.getData().getUseraddress().getAddressType());
                    addressObj.setCity(IDVEResponse.getData().getUseraddress().getCity());
                    addressObj.setState(IDVEResponse.getData().getUseraddress().getState());
                    addressObj.setCountry("us");
                    addressObj.setZipCode(IDVEResponse.getData().getUseraddress().getZipCode());

                    PhotoIDEntityObject photoIDEntityObject = new PhotoIDEntityObject();
                    photoIDEntityObject.setNumber(ssnET.getText().toString().trim());
                    photoIDEntityObject.setType(IDVEResponse.getData().getPhotoIDEntityObject().getType());
                    photoIDEntityObject.setIssuer(IDVEResponse.getData().getPhotoIDEntityObject().getIssuer());

                    identityAddressRequest.setAddressObj(addressObj);
                    identityAddressRequest.setPhotoIDEntityObject(photoIDEntityObject);

                    identityVerificationViewModel.uploadIdentityAddress(identityAddressRequest);

                }
            }
        });

        identityVerificationViewModel.getGetIdentity().observe(this, new Observer<GetIdentityResponse>() {
            @Override
            public void onChanged(GetIdentityResponse identityResponse) {
                if (identityResponse != null && identityResponse.getStatus().equalsIgnoreCase("success")) {
                    IDVEResponse = identityResponse;
                }
            }
        });


        try {
            identityVerificationViewModel.getUploadIdentityAddressResponse().observe(this, new Observer<IdentityAddressResponse>() {
                @Override
                public void onChanged(IdentityAddressResponse identityAddressResponse) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (identityAddressResponse.getStatus().equalsIgnoreCase("success")) {
                        String respCode = identityAddressResponse.getData().getGiactResponseName();
                        if (respCode.equalsIgnoreCase("ND02") || respCode.equalsIgnoreCase("CA11")
                                || respCode.equalsIgnoreCase("CI11") || respCode.equalsIgnoreCase("CA24")
                                || respCode.equalsIgnoreCase("CI24")) {
                            //Success
                            startActivity(new Intent(AdditionalActionUploadActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "SUCCESS"));
                            finish();
                        } else if (respCode.equalsIgnoreCase("CA22") || respCode.equalsIgnoreCase("CI22")) {
                            //SSN Error
                            startActivity(new Intent(AdditionalActionUploadActivity.this, AdditionalActionUploadActivity.class));
                            finish();
                        } else if (respCode.equalsIgnoreCase("CA25") || respCode.equalsIgnoreCase("CI25")
                                || respCode.equalsIgnoreCase("CA21") || respCode.equalsIgnoreCase("CI21")
                                || respCode.equalsIgnoreCase("CA01") || respCode.equalsIgnoreCase("CI01")
                                || respCode.equalsIgnoreCase("CA30") || respCode.equalsIgnoreCase("CI30")
                                || respCode.equalsIgnoreCase("CA23") || respCode.equalsIgnoreCase("CI23")) {
                            //Under Review
                            startActivity(new Intent(AdditionalActionUploadActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "UNDER_REVIEW"));
                            finish();

                        } else {
                            //Failed
                            startActivity(new Intent(AdditionalActionUploadActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "FAILED"));
                            finish();

                        }
                    } else {
                        Utils.displayAlert(identityAddressResponse.getError().getErrorDescription(), AdditionalActionUploadActivity.this, "");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableORdiableNext() {
        if (isssn) {
            idveriDone.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
        } else {
            idveriDone.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }
}
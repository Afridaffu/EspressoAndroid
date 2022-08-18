package com.coyni.mapp.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.model.identity_verification.AddressObj;
import com.coyni.mapp.model.identity_verification.GetIdentityResponse;
import com.coyni.mapp.model.identity_verification.IdentityAddressRequest;
import com.coyni.mapp.model.identity_verification.IdentityAddressResponse;
import com.coyni.mapp.model.identity_verification.PhotoIDEntityObject;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.outline_et.SSNBOEditText;
import com.coyni.mapp.viewmodel.IdentityVerificationViewModel;

public class IdVeAdditionalActionActivity extends AppCompatActivity {
    //    TextInputEditText ssnET;
    TextInputLayout ssnaacTIL;
    CardView idveridoneBtn;
    public boolean isssn = false, isSubmitEnabled = false;
    public LinearLayout ssnCloseLL, ssnErrorLL;
    public TextView ssnErrorTV;
    IdentityVerificationViewModel identityVerificationViewModel;
    GetIdentityResponse IDVEResponse;
    Dialog dialog;
    private Long mLastClickTime = 0L;
    SSNBOEditText ssnET;
    public ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_ve_additional_action);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ssnCloseLL = findViewById(R.id.ssnCloseLL);
//        ssnET = findViewById(R.id.ssnET);
        ssnErrorLL = findViewById(R.id.ssnErrorLL);
        ssnErrorTV = findViewById(R.id.ssnErrorTV);
        idveridoneBtn = findViewById(R.id.idveridoneBtn);
        ssnaacTIL = findViewById(R.id.ssnTIL);
        scrollView = findViewById(R.id.scrollView);

        ssnET = findViewById(R.id.ssnOutLineBoxET);
        ssnET.setFrom("IDVE_SSN", this);


        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

        identityVerificationViewModel.getIdentityData();

        ssnCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra("from").equals("IDVE")) {
                    Intent i = new Intent(IdVeAdditionalActionActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    finish();
                }

            }
        });

//        ssnET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (ssnET.getText().toString().length() == 0) {
////                    ssnErrorLL.setVisibility(VISIBLE);
////                    ssnErrorTV.setText("Field Required");
//                    isssn = false;
//                } else if (ssnET.getText().toString().length() > 0 && ssnET.getText().toString().length() < 9) {
////                    ssnErrorLL.setVisibility(VISIBLE);
////                    ssnErrorTV.setText("Please enter a valid SSN");
//                    isssn = true;
//                } else if (ssnET.getText().toString().length() == 9) {
////                    ssnErrorLL.setVisibility(GONE);
//                    isssn = true;
//                }
//                ssnErrorLL.setVisibility(GONE);
//                enableORdiableNext();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        idveridoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (isSubmitEnabled) {
                    String SSN = ssnET.getUnMasked();

                    if (SSN.length() == 0) {
                        ssnErrorLL.setVisibility(VISIBLE);
                        ssnErrorTV.setText("Field Required");
                    } else if (SSN.length() > 0 && SSN.length() < 9) {
                        ssnErrorLL.setVisibility(VISIBLE);
                        ssnErrorTV.setText("Please enter a valid SSN");
                    } else if (SSN.length() == 9) {
                        ssnErrorLL.setVisibility(GONE);

                        dialog = Utils.showProgressDialog(IdVeAdditionalActionActivity.this);
                        IdentityAddressRequest identityAddressRequest = new IdentityAddressRequest();
                        try {
                            identityAddressRequest.setFirstName(IDVEResponse.getData().getFirstName());
                            identityAddressRequest.setLastName(IDVEResponse.getData().getLastName());
                            identityAddressRequest.setPhoneNumber(IDVEResponse.getData().getPhoneNumber());
                            identityAddressRequest.setEmail(IDVEResponse.getData().getEmail());
                            identityAddressRequest.setDateOfBirth(IDVEResponse.getData().getDateOfBirth());
                            //Unmask SSN
//                    String SSN = ssnET.getText().toString().substring(0,2)+ssnET.getText().toString().substring(4,5)+ssnET.getText().toString().substring(7,10);

                            identityAddressRequest.setSsn(SSN);

                            AddressObj addressObj = new AddressObj();
                            addressObj.setAddressLine1(IDVEResponse.getData().getUseraddress().getAddressLine1());
                            addressObj.setAddressLine2(IDVEResponse.getData().getUseraddress().getAddressLine2());
                            addressObj.setAddressType(IDVEResponse.getData().getUseraddress().getAddressType());
                            addressObj.setCity(IDVEResponse.getData().getUseraddress().getCity());
//                        addressObj.setState(IDVEResponse.getData().getUseraddress().getState());
                            addressObj.setState(IDVEResponse.getData().getUseraddress().getStateCode());
                            try {
                                addressObj.setStateCode(IDVEResponse.getData().getUseraddress().getStateCode());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            addressObj.setCountry("us");
                            addressObj.setZipCode(IDVEResponse.getData().getUseraddress().getZipCode());

                            PhotoIDEntityObject photoIDEntityObject = new PhotoIDEntityObject();
                            photoIDEntityObject.setNumber(SSN);
                            try {
                                photoIDEntityObject.setType(IDVEResponse.getData().getPhotoIDEntityObject().getType());
                                photoIDEntityObject.setIssuer(IDVEResponse.getData().getPhotoIDEntityObject().getIssuer());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            identityAddressRequest.setAddressObj(addressObj);
                            identityAddressRequest.setPhotoIDEntityObject(photoIDEntityObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        identityVerificationViewModel.uploadIdentityAddressPatch(identityAddressRequest);
                    }

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

        enableORdiableNext();

        try {
            identityVerificationViewModel.getUploadIdentityAddressPatchResponse().observe(this, new Observer<IdentityAddressResponse>() {
                @Override
                public void onChanged(IdentityAddressResponse identityAddressResponse) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (identityAddressResponse.getStatus().equalsIgnoreCase("success")) {
                        //Success
//                        startActivity(new Intent(IdVeAdditionalActionActivity.this, IdentityVerificationBindingLayoutActivity.class)
//                                .putExtra("screen", "SUCCESS"));
//                        finish();
                        String respCode = identityAddressResponse.getData().getGiactResponseName();
                        if (respCode.equalsIgnoreCase("ND02") || respCode.equalsIgnoreCase("CA11")
                                || respCode.equalsIgnoreCase("CI11") || respCode.equalsIgnoreCase("CA24")
                                || respCode.equalsIgnoreCase("CI24")) {
                            //Success
                            startActivity(new Intent(IdVeAdditionalActionActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "SUCCESS"));
                        } else if (respCode.equalsIgnoreCase("CA22") || respCode.equalsIgnoreCase("CI22")) {
                            //SSN Error
                            if (identityAddressResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus()) ||
                                    identityAddressResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                                //Failed
                                startActivity(new Intent(IdVeAdditionalActionActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "FAILED"));
                            }
                        } else if (respCode.equalsIgnoreCase("CA25") || respCode.equalsIgnoreCase("CI25")
                                || respCode.equalsIgnoreCase("CA21") || respCode.equalsIgnoreCase("CI21")
                                || respCode.equalsIgnoreCase("CA01") || respCode.equalsIgnoreCase("CI01")
                                || respCode.equalsIgnoreCase("CA30") || respCode.equalsIgnoreCase("CI30")
                                || respCode.equalsIgnoreCase("CA23") || respCode.equalsIgnoreCase("CI23")) {
                            //Under Review
                            startActivity(new Intent(IdVeAdditionalActionActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "UNDER_REVIEW"));
                        } else {
                            //Failed
                            startActivity(new Intent(IdVeAdditionalActionActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "FAILED"));
                        }
                    } else {
                        Utils.displayAlert(identityAddressResponse.getError().getErrorDescription(), IdVeAdditionalActionActivity.this, "", identityAddressResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableORdiableNext() {
        if (isssn) {
            isSubmitEnabled = true;
            idveridoneBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
        } else {
            isSubmitEnabled = false;
            idveridoneBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }

}
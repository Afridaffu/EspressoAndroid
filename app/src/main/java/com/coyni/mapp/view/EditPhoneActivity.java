package com.coyni.mapp.view;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoUpdateResp;
import com.coyni.mapp.model.CompanyInfo.ContactInfoRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneResponse;
import com.coyni.mapp.model.register.PhNoWithCountryCode;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.outline_et.OutLineBoxPhoneUpdateET;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;

import java.util.Objects;

public class EditPhoneActivity extends BaseActivity {

    OutLineBoxPhoneUpdateET currentPhoneET, newPhoneET, b_newPhoneET;
    MyApplication myApplicationObj;
    NestedScrollView editPhoneSV;
    public boolean isSaveEnabled = false, isCurrentPhone = true, isNewPhone = false;
    public LinearLayout currentPhoneErrorLL, newPhoneErrorLL, editPhoneCloseLL, b_newPhoneErrorLL, b_editPhoneCloseLL;
    public TextView currentPhoneErrorTV, newPhoneErrorTV, b_newPhoneErrorTV;
    public CardView savePhoneCV;
    Long mLastClickTime = 0L;
    Dialog dialog;
    CustomerProfileViewModel customerProfileViewModel;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    public static EditPhoneActivity editPhoneActivity;
    String currentPhoneNumber, newPhoneNumber;
    TextView contactUsTV, b_contactUsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_edit_phone);
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);

//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            myApplicationObj = (MyApplication) getApplicationContext();
            editPhoneActivity = this;
            currentPhoneET = findViewById(R.id.currentPhoneET);
            currentPhoneET.setHint("Current Phone Number");
            currentPhoneET.disable();
            newPhoneET = findViewById(R.id.newPhoneET);
            newPhoneET.setHint("Phone Number");
            editPhoneSV = findViewById(R.id.editPhoneSV);
            currentPhoneErrorLL = findViewById(R.id.currentPhoneErrorLL);
            newPhoneErrorLL = findViewById(R.id.newPhoneErrorLL);
            currentPhoneErrorTV = findViewById(R.id.currentPhoneErrorTV);
            newPhoneErrorTV = findViewById(R.id.newPhoneErrorTV);
            savePhoneCV = findViewById(R.id.savePhoneCV);
            editPhoneCloseLL = findViewById(R.id.editPhoneCloseLL);
            contactUsTV = findViewById(R.id.contactUsTV);

            //Business..
            b_newPhoneET = findViewById(R.id.b_newPhoneET);
            b_newPhoneErrorLL = findViewById(R.id.b_newPhoneErrorLL);
            b_newPhoneErrorTV = findViewById(R.id.b_newPhoneErrorTV);
            b_editPhoneCloseLL = findViewById(R.id.b_editPhoneCloseLL);
            b_contactUsTV = findViewById(R.id.b_contactUsTV);

            findViewById(R.id.editPhoneSV).setVisibility(View.GONE);
            findViewById(R.id.business_topLL).setVisibility(View.VISIBLE);

            if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                b_newPhoneET.setHint("New Phone Number");
            } else if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT || myApplicationObj.getAccountType() == Utils.SHARED_ACCOUNT) {
                currentPhoneET.setVisibility(View.GONE);
                b_newPhoneET.setHint("Phone Number");

            }

            currentPhoneET.setText(getIntent().getStringExtra("OLD_PHONE"));
//            currentPhoneET.setHint("Current Phone Number");

            savePhoneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSaveEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        dialog = Utils.showProgressDialog(EditPhoneActivity.this);
                        if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equalsIgnoreCase("DBAChangePhone")) {
                            try {
                                ContactInfoRequest contactInfoRequest = new ContactInfoRequest();
                                contactInfoRequest.setEmail(Objects.requireNonNull(myApplicationObj.getDbaInfoResp().getData().getEmail()));
                                contactInfoRequest.setId(myApplicationObj.getDbaInfoResp().getData().getId());
                                PhNoWithCountryCode phNoWithCountryCode = new PhNoWithCountryCode();
                                phNoWithCountryCode.setCountryCode(myApplicationObj.getDbaInfoResp().getData().getPhoneNumberDto().getCountryCode());
                                newPhoneNumber = b_newPhoneET.getText().toString().substring(1, 4) + b_newPhoneET.getText().toString().substring(6, 9) + b_newPhoneET.getText().toString().substring(10, b_newPhoneET.getText().length());
                                phNoWithCountryCode.setPhoneNumber(newPhoneNumber);
                                contactInfoRequest.setPhoneNumberDto(phNoWithCountryCode);
                                currentPhoneNumber = currentPhoneET.getText().toString().substring(1, 4) + currentPhoneET.getText().toString().substring(6, 9) + currentPhoneET.getText().toString().substring(10, currentPhoneET.getText().length());
                                if (currentPhoneNumber.equalsIgnoreCase(newPhoneNumber)) {
                                    dialog.dismiss();
                                    Utils.displayAlertNew("Please enter a new phone number ", EditPhoneActivity.this, "coyni");
                                } else {
                                    businessIdentityVerificationViewModel.updateCompanyInfo(contactInfoRequest);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equalsIgnoreCase("CompanyChangePhone")) {
                            try {

                                ContactInfoRequest contactInfoRequest = new ContactInfoRequest();
                                contactInfoRequest.setEmail(Objects.requireNonNull(myApplicationObj.getCompanyInfoResp().getData().getEmail()));
                                PhNoWithCountryCode phNoWithCountryCode = new PhNoWithCountryCode();
                                phNoWithCountryCode.setCountryCode(myApplicationObj.getCompanyInfoResp().getData().getPhoneNumberDto().getCountryCode());
                                newPhoneNumber = b_newPhoneET.getText().toString().substring(1, 4) + b_newPhoneET.getText().toString().substring(6, 9) + b_newPhoneET.getText().toString().substring(10, b_newPhoneET.getText().length());
                                phNoWithCountryCode.setPhoneNumber(newPhoneNumber);
                                contactInfoRequest.setPhoneNumberDto(phNoWithCountryCode);
                                contactInfoRequest.setId(myApplicationObj.getCompanyInfoResp().getData().getId());
                                currentPhoneNumber = currentPhoneET.getText().toString().substring(1, 4) + currentPhoneET.getText().toString().substring(6, 9) + currentPhoneET.getText().toString().substring(10, currentPhoneET.getText().length());
                                if (currentPhoneNumber.equalsIgnoreCase(newPhoneNumber)) {
                                    try {
                                        dialog.dismiss();
                                        Utils.displayAlertNew("Please enter a new phone number ", EditPhoneActivity.this, "coyni");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        businessIdentityVerificationViewModel.updateCompanyInfo(contactInfoRequest);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else  {
                            try {
                                callSendPhoneOTPAPI();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.e("isSaveEnabled", isSaveEnabled + "");
                    }
                }
            });

            editPhoneCloseLL.setOnClickListener(view -> {
                finish();
            });
            b_editPhoneCloseLL.setOnClickListener(view -> {
                finish();
            });

            contactUsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        Utils.hideKeypad(EditPhoneActivity.this);
                        Utils.hideSoftKeyboard(EditPhoneActivity.this);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(Utils.mondayURL));
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            b_contactUsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        Utils.hideKeypad(EditPhoneActivity.this);
                        Utils.hideSoftKeyboard(EditPhoneActivity.this);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(Utils.mondayURL));
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            b_editPhoneCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    Utils.hideKeypad(EditPhoneActivity.this);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSendPhoneOTPAPI() {
        try {
            currentPhoneNumber = currentPhoneET.getText().toString().substring(1, 4) + currentPhoneET.getText().toString().substring(6, 9) + currentPhoneET.getText().toString().substring(10, currentPhoneET.getText().length());
            newPhoneNumber = b_newPhoneET.getText().toString().substring(1, 4) + b_newPhoneET.getText().toString().substring(6, 9) + b_newPhoneET.getText().toString().substring(10, b_newPhoneET.getText().length());
            UpdatePhoneRequest updatePhoneRequest = new UpdatePhoneRequest();
            updatePhoneRequest.setCurrentPhoneNumber(currentPhoneNumber);
            updatePhoneRequest.setCurrentcountryCode(Utils.getStrCCode());
            updatePhoneRequest.setNewPhoneNumber(newPhoneNumber);
            updatePhoneRequest.setNewcountryCode(Utils.getStrCCode());
            customerProfileViewModel.updatePhoneSendOTP(updatePhoneRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableOrDisableSave() {

        try {
            Log.e("all fields", isCurrentPhone + " " + isNewPhone);
            if (isCurrentPhone && isNewPhone) {
                isSaveEnabled = true;
                savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isSaveEnabled = false;
                savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initObservers() {

        customerProfileViewModel.getUpdatePhoneSendOTPResponse().observe(this, new Observer<UpdatePhoneResponse>() {
            @Override
            public void onChanged(UpdatePhoneResponse updatePhoneResponse) {
                try {
                    dialog.dismiss();
                    if (updatePhoneResponse != null && updatePhoneResponse.getStatus().toLowerCase().equals("success")) {
                        myApplicationObj.setUpdatePhoneResponse(updatePhoneResponse);
                        Utils.hideKeypad(EditPhoneActivity.this);
                        startActivity(new Intent(EditPhoneActivity.this, OTPValidation.class)
                                .putExtra("screen", "EditPhone")
                                .putExtra("OTP_TYPE", "OTP")
                                .putExtra("IS_OLD_PHONE", "true")
                                .putExtra("OLD_PHONE_MASKED", currentPhoneET.getText().toString().trim())
                                .putExtra("NEW_PHONE_MASKED", b_newPhoneET.getText().toString().trim())
                                .putExtra("OLD_PHONE", currentPhoneNumber)
                                .putExtra("MOBILE", currentPhoneNumber)
                                .putExtra("NEW_PHONE", newPhoneNumber));
                    } else {
                        Utils.hideSoftKeyboard(EditPhoneActivity.this);
                        Utils.displayAlert(updatePhoneResponse.getError().getErrorDescription(), EditPhoneActivity.this, "", updatePhoneResponse.getError().getFieldErrors().get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        businessIdentityVerificationViewModel.getContactInfoUpdateResponse().observe(this
                , new Observer<CompanyInfoUpdateResp>() {
                    @Override
                    public void onChanged(CompanyInfoUpdateResp companyInfoUpdateResp) {
                        dialog.dismiss();
                        try {
                            if (companyInfoUpdateResp != null && companyInfoUpdateResp.getStatus().equalsIgnoreCase("SUCCESS")) {
                                finish();
                            } else {
                                Toast.makeText(EditPhoneActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            b_newPhoneET.setFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        newPhoneET.clearFocus();
    }
}
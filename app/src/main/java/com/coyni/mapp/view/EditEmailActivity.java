package com.coyni.mapp.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoUpdateResp;
import com.coyni.mapp.model.CompanyInfo.ContactInfoRequest;
import com.coyni.mapp.model.EmailRequest;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailRequest;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailResponse;
import com.coyni.mapp.model.register.EmailExistsResponse;
import com.coyni.mapp.model.register.PhNoWithCountryCode;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

import java.util.Objects;

public class EditEmailActivity extends BaseActivity {

    TextInputEditText currentEmailET, newEmailET, b_newEmailET;
    TextInputLayout currentEmailTIL, newEmailTIL, b_newEmailTIL;
    MyApplication myApplicationObj;
    NestedScrollView editEmailSV;
    public boolean isSaveEnabled = false, isCurrentEmail = true, isNewEmail = false;
    LinearLayout currentEmailErrorLL, newEmailErrorLL, b_newEmailErrorLL;
    TextView currentEmailErrorTV, newEmailErrorTV, contactUsTV, b_newEmailErrorTV;
    CardView saveEmailCV;
    Long mLastClickTime = 0L;
    Dialog dialog;
    CustomerProfileViewModel customerProfileViewModel;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    LoginViewModel loginViewModel;
    LinearLayout b_editEmailCloseLL;
    private String currentEmail;
    public static EditEmailActivity editEmailActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_edit_email);
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            initFields();
            initObservers();
            textWatchers();
            focusWatchers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            editEmailActivity = this;
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            myApplicationObj = (MyApplication) getApplicationContext();
//            editEmailCloseLL = findViewById(R.id.editEmailCloseLL);
            currentEmailET = findViewById(R.id.currentEmailET);
//            newEmailET = findViewById(R.id.newEmailET);
            currentEmailTIL = findViewById(R.id.currentEmailTIL);
//            newEmailTIL = findViewById(R.id.newEmailTIL);
//            editEmailSV = findViewById(R.id.editEmailSV);
//            currentEmailErrorLL = findViewById(R.id.currentEmailErrorLL);
//            newEmailErrorLL = findViewById(R.id.newEmailErrorLL);
//            currentEmailErrorTV = findViewById(R.id.currentEmailErrorTV);
//            newEmailErrorTV = findViewById(R.id.newEmailErrorTV);
            saveEmailCV = findViewById(R.id.saveEmailCV);
//            contactUsTV = findViewById(R.id.contactUsTV);

            //Business..
            b_editEmailCloseLL = findViewById(R.id.b_editEmailCloseLL);
            b_newEmailET = findViewById(R.id.b_newEmailET);
            b_newEmailTIL = findViewById(R.id.b_newEmailTIL);
            b_newEmailErrorLL = findViewById(R.id.b_newEmailErrorLL);
            b_newEmailErrorTV = findViewById(R.id.b_newEmailErrorTV);


            currentEmailET.setText(myApplicationObj.getMyProfile().getData().getEmail());

//            newEmailET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
            b_newEmailET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});

            currentEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));


//            editEmailSV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Utils.hideKeypad(EditEmailActivity.this);
//                    currentEmailET.clearFocus();
//                    newEmailET.currentEmailTILclearFocus();
//                }
//            });


            if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT || myApplicationObj.getAccountType() == Utils.SHARED_ACCOUNT) {
                currentEmailTIL.setVisibility(GONE);
            } else {
                currentEmailTIL.setVisibility(VISIBLE);
            }

            saveEmailCV.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    if (isSaveEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();


                        dialog = Utils.showProgressDialog(EditEmailActivity.this);

                        if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equalsIgnoreCase("DBAChangeEmail")) {
                            callChangeDbaEmailAPI();
                        } else if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equalsIgnoreCase("CompanyChangeEmail")) {
                            try {
                                ContactInfoRequest contactInfoRequest = new ContactInfoRequest();
                                contactInfoRequest.setEmail(Objects.requireNonNull(b_newEmailET.getText()).toString());
                                PhNoWithCountryCode phNoWithCountryCode = new PhNoWithCountryCode();
                                phNoWithCountryCode.setCountryCode(myApplicationObj.getCompanyInfoResp().getData().getPhoneNumberDto().getCountryCode());
                                phNoWithCountryCode.setPhoneNumber(myApplicationObj.getCompanyInfoResp().getData().getPhoneNumberDto().getPhoneNumber());
                                contactInfoRequest.setPhoneNumberDto(phNoWithCountryCode);
                                contactInfoRequest.setId(myApplicationObj.getCompanyInfoResp().getData().getId());
                                if (getIntent().getStringExtra("currentEmail") != null && !getIntent().getStringExtra("currentEmail").equals("")) {
                                    currentEmail = getIntent().getStringExtra("currentEmail");
                                    if (currentEmail.equalsIgnoreCase(b_newEmailET.getText().toString())) {
                                        dialog.dismiss();
//                    b_newEmailErrorLL.setVisibility(VISIBLE);
//                    b_newEmailErrorTV.setText("Please enter a new email ");
                                        Utils.displayAlertNew("Please enter a new email ", EditEmailActivity.this, "coyni");
                                    } else {
                                        b_newEmailErrorTV.setText("");
                                        b_newEmailErrorLL.setVisibility(GONE);
                                        businessIdentityVerificationViewModel.updateCompanyInfo(contactInfoRequest);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else  {
                            callSendEmailOTPAPI();
                        }

                    } else {
                        Log.e("isSaveEnabled", isSaveEnabled + "");
                    }
                }
            });
//
//            saveEmailCV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (isSaveEnabled) {
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//
//
//                        callSendEmailOTPAPI();
//
//                    } else {
//                        Log.e("isSaveEnabled", isSaveEnabled + "");
//                    }
//                }
//            });
//            editEmailCloseLL.setOnClickListener(view -> {
//                finish();
//            });

            b_editEmailCloseLL.setOnClickListener(view -> {
                finish();
            });

//            contactUsTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
////                        Utils.hideKeypad(EditEmailActivity.this);
//                        Utils.hideSoftKeyboard(EditEmailActivity.this);
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(Utils.mondayURL));
//                        startActivity(i);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void textWatchers() {

        try {
            String beforeText = "";
            b_newEmailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.e("beforeTextChanged", charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                        b_newEmailErrorLL.setVisibility(GONE);
                        b_newEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        newEmailTIL.setHintTextColor(colorState);
                        Utils.setUpperHintColor(b_newEmailTIL, getResources().getColor(R.color.primary_green));
                        isNewEmail = true;
                    } else if (b_newEmailET.getText().toString().trim().length() == 0) {
//                        b_newEmailErrorLL.setVisibility(VISIBLE);
//                        b_newEmailErrorTV.setText("Field Required");
                        isNewEmail = false;
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
                        isNewEmail = true;
                    } else {
                        isNewEmail = false;
                    }
                    enableOrDisableSave();

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = b_newEmailET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            b_newEmailET.setText(b_newEmailET.getText().toString().replaceAll(" ", ""));
                            b_newEmailET.setSelection(b_newEmailET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusWatchers() {

        try {
            b_newEmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (b_newEmailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(b_newEmailET.getText().toString().trim())) {
                            b_newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(EditEmailActivity.this));
                            Utils.setUpperHintColor(b_newEmailTIL, getColor(R.color.error_red));
                            b_newEmailErrorLL.setVisibility(VISIBLE);
                            b_newEmailErrorTV.setText("Please enter a valid Email");
                        } else if (b_newEmailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(b_newEmailET.getText().toString().trim())) {
                            b_newEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(EditEmailActivity.this));
                            Utils.setUpperHintColor(b_newEmailTIL, getColor(R.color.primary_black));
                            b_newEmailErrorLL.setVisibility(GONE);
                            EmailRequest emailRequest = new EmailRequest();
                            emailRequest.setEmail(b_newEmailET.getText().toString().trim());
//                            loginViewModel.validateEmail(b_newEmailET.getText().toString().trim());
                            loginViewModel.validateEmail(emailRequest);
                        } else {
                            b_newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(EditEmailActivity.this));
                            Utils.setUpperHintColor(b_newEmailTIL, getColor(R.color.error_red));
                            b_newEmailErrorLL.setVisibility(VISIBLE);
                            b_newEmailErrorTV.setText("Field Required");
                        }
                    } else {
                        b_newEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(b_newEmailTIL, getColor(R.color.primary_green));
//                        b_newEmailErrorTV.setText("Field Required");

//                        currentEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                        Utils.setUpperHintColor(currentEmailTIL, getColor(R.color.primary_black));

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void callSendEmailOTPAPI() {
        try {

            UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
            updateEmailRequest.setExistingEmail(currentEmailET.getText().toString().trim());
            updateEmailRequest.setNewEmail(b_newEmailET.getText().toString().trim());
            customerProfileViewModel.updateEmailSendOTP(updateEmailRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableOrDisableSave() {

        try {
            Log.e("all fields", isCurrentEmail + " " + isNewEmail);
            if (isCurrentEmail && isNewEmail) {
                isSaveEnabled = true;
                saveEmailCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isSaveEnabled = false;
                saveEmailCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initObservers() {

        customerProfileViewModel.getUpdateEmailSendOTPResponse().observe(this, new Observer<UpdateEmailResponse>() {
            @Override
            public void onChanged(UpdateEmailResponse updateEmailResponse) {
                try {
                    dialog.dismiss();
                    if (updateEmailResponse != null && updateEmailResponse.getStatus().toLowerCase().equals("success")) {
                        myApplicationObj.setUpdateEmailResponse(updateEmailResponse);
                        Utils.hideKeypad(EditEmailActivity.this);
                        if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            startActivity(new Intent(EditEmailActivity.this, OTPValidation.class)
                                    .putExtra("screen", "EditEmail")
                                    .putExtra("OTP_TYPE", "OTP")
                                    .putExtra("IS_OLD_EMAIL", "true")
                                    .putExtra("OLD_EMAIL", currentEmailET.getText().toString().trim())
                                    .putExtra("EMAIL", currentEmailET.getText().toString().trim())
                                    .putExtra("NEW_EMAIL", b_newEmailET.getText().toString().trim())
                            );
                        }
                        if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT || myApplicationObj.getAccountType() == Utils.SHARED_ACCOUNT) {
                            startActivity(new Intent(EditEmailActivity.this, OTPValidation.class)
                                    .putExtra("screen", "EditEmail")
                                    .putExtra("OTP_TYPE", "OTP")
                                    .putExtra("IS_OLD_EMAIL", "true")
                                    .putExtra("OLD_EMAIL", currentEmailET.getText().toString().trim())
                                    .putExtra("EMAIL", currentEmailET.getText().toString().trim())
                                    .putExtra("NEW_EMAIL", b_newEmailET.getText().toString().trim()));
                        }
                    } else {
                        if (updateEmailResponse.getError().getErrorDescription().equals("")) {
                            try {
                                Utils.hideSoftKeyboard(EditEmailActivity.this);
                                if (updateEmailResponse.getError().getFieldErrors().get(0).contains(":")) {
                                    int counter = 0;
                                    for (int i = 0; i < updateEmailResponse.getError().getFieldErrors().get(0).length(); i++) {
                                        if (String.valueOf(updateEmailResponse.getError().getFieldErrors().get(0).charAt(i)).equals(":")) {
                                            counter = i;
                                            break;
                                        }
                                    }
                                    Utils.displayAlert(updateEmailResponse.getError().getFieldErrors().get(0).substring(counter + 1), EditEmailActivity.this, "", updateEmailResponse.getError().getFieldErrors().get(0));

                                } else {
                                    Utils.displayAlert(updateEmailResponse.getError().getFieldErrors().get(0), EditEmailActivity.this, "", updateEmailResponse.getError().getFieldErrors().get(0));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.hideSoftKeyboard(EditEmailActivity.this);
                            Utils.displayAlert(updateEmailResponse.getError().getErrorDescription(), EditEmailActivity.this, "", updateEmailResponse.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loginViewModel.getEmailExistsResponseMutableLiveData().observe(this, new Observer<EmailExistsResponse>() {
            @Override
            public void onChanged(EmailExistsResponse emailExistsResponse) {
                try {
                    if (emailExistsResponse != null) {
                        if (myApplicationObj.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            if (!emailExistsResponse.getStatus().toLowerCase().equals("error")) {
                                newEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(EditEmailActivity.this));
                                Utils.setUpperHintColor(newEmailTIL, getColor(R.color.primary_black));
                                newEmailErrorLL.setVisibility(GONE);
                            } else {
                                newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(EditEmailActivity.this));
                                Utils.setUpperHintColor(newEmailTIL, getColor(R.color.error_red));
                                newEmailErrorLL.setVisibility(VISIBLE);
                                newEmailErrorTV.setText(emailExistsResponse.getError().getErrorDescription());
                            }
                        }
                        if (myApplicationObj.getAccountType() == Utils.BUSINESS_ACCOUNT || myApplicationObj.getAccountType() == Utils.SHARED_ACCOUNT) {
                            if (!emailExistsResponse.getStatus().toLowerCase().equals("error")) {
                                b_newEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(EditEmailActivity.this));
                                Utils.setUpperHintColor(b_newEmailTIL, getColor(R.color.primary_black));
                                b_newEmailErrorLL.setVisibility(GONE);
                            } else {
                                b_newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(EditEmailActivity.this));
                                Utils.setUpperHintColor(b_newEmailTIL, getColor(R.color.error_red));
                                b_newEmailErrorLL.setVisibility(VISIBLE);
                                b_newEmailErrorTV.setText(emailExistsResponse.getError().getErrorDescription());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
//                        if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
////                            objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
//                        } else {
                        Utils.hideSoftKeyboard(EditEmailActivity.this);
                        Utils.displayAlert(apiError.getError().getErrorDescription(), EditEmailActivity.this, "", apiError.getError().getFieldErrors().get(0));
//                        }
                    } else {
                        Utils.hideSoftKeyboard(EditEmailActivity.this);
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), EditEmailActivity.this, "", apiError.getError().getFieldErrors().get(0));
                    }
                }
            }
        });

//        businessIdentityVerificationViewModel.getUpdateBasicDBAInfoResponse().observe(this, new Observer<DBAInfoUpdateResp>() {
//            @Override
//            public void onChanged(DBAInfoUpdateResp dbaInfoUpdateResp) {
//                dialog.dismiss();
//                try {
//                    if (dbaInfoUpdateResp !=null && dbaInfoUpdateResp.getStatus().equalsIgnoreCase("SUCCESS")){
//                        Utils.showCustomToast(EditEmailActivity.this, "Email updated", R.drawable.ic_check, "EMAIL");
//                        new Handler().postDelayed(() -> {
//                            try {
//                                finish();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }, 2000);
//
//                    }
//                    else {
//                        Toast.makeText(EditEmailActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        businessIdentityVerificationViewModel.getContactInfoUpdateResponse().observe(this, new Observer<CompanyInfoUpdateResp>() {
            @Override
            public void onChanged(CompanyInfoUpdateResp companyInfoUpdateResp) {
                dialog.dismiss();
                try {
                    if (companyInfoUpdateResp != null && companyInfoUpdateResp.getStatus().equalsIgnoreCase("SUCCESS")) {
                        finish();
                    } else {
                        Toast.makeText(EditEmailActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void callChangeDbaEmailAPI() {
        try {
            ContactInfoRequest contactInfoRequest = new ContactInfoRequest();
            contactInfoRequest.setEmail(Objects.requireNonNull(b_newEmailET.getText()).toString());
            contactInfoRequest.setId(myApplicationObj.getDbaInfoResp().getData().getId());
            PhNoWithCountryCode phNoWithCountryCode = new PhNoWithCountryCode();
            phNoWithCountryCode.setCountryCode(myApplicationObj.getDbaInfoResp().getData().getPhoneNumberDto().getCountryCode());
            phNoWithCountryCode.setPhoneNumber(myApplicationObj.getDbaInfoResp().getData().getPhoneNumberDto().getPhoneNumber());
            contactInfoRequest.setPhoneNumberDto(phNoWithCountryCode);
            if (getIntent().getStringExtra("currentEmail") != null && !getIntent().getStringExtra("currentEmail").equals("")) {
                currentEmail = getIntent().getStringExtra("currentEmail");
                if (currentEmail.equalsIgnoreCase(b_newEmailET.getText().toString())) {
                    dialog.dismiss();
//                    b_newEmailErrorLL.setVisibility(VISIBLE);
//                    b_newEmailErrorTV.setText("Please enter a new email ");
                    Utils.displayAlertNew("Please enter a new email ", EditEmailActivity.this, "coyni");
                } else {
                    b_newEmailErrorTV.setText("");
                    b_newEmailErrorLL.setVisibility(GONE);
                    businessIdentityVerificationViewModel.updateCompanyInfo(contactInfoRequest);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            b_newEmailET.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class RetrieveEmailActivity extends AppCompatActivity implements TextWatcher {
    OutLineBoxPhoneNumberEditText phoneNumberET;
    CardView nextBtn;
    TextInputEditText firstName, lastName;
    TextInputLayout firstTIL, lastTIL;
    LinearLayout layoutClose, layoutMain;
    LoginViewModel loginViewModel;
    Dialog dialog;
    public static RetrieveEmailActivity retrieveEmailActivity;
    String phoneNumber = "";
    public LinearLayout phoneErrorLL, firstNameErrorLL, lastNameErrorLL;
    public TextView phoneErrorTV, firstNameErrorTV, lastNameErrorTV;
    private long mLastClickTime = 0;
    Boolean isDel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_retrieve_email);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            phoneNumberET.requestFocus();
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (after < count) {
            isDel = true;
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        if (charSequence == firstName.getEditableText()) {
            if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                firstNameErrorLL.setVisibility(GONE);
                Utils.setUpperHintColor(firstTIL, getResources().getColor(R.color.primary_black));
            } else if (firstName.getText().toString().trim().length() == 0) {
                firstNameErrorLL.setVisibility(VISIBLE);
                firstNameErrorTV.setText("Field Required");
            }
            enableButton();
        } else if (charSequence == lastName.getEditableText()) {
            if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                lastNameErrorLL.setVisibility(GONE);
                Utils.setUpperHintColor(lastTIL, getResources().getColor(R.color.primary_black));
            } else if (lastName.getText().toString().trim().length() == 0) {
                lastNameErrorLL.setVisibility(VISIBLE);
                lastNameErrorTV.setText("Field Required");
            }
            enableButton();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == firstName.getEditableText()) {
            try {
                String str = firstName.getText().toString();
                if (str.length() > 0 && str.substring(0).equals(" ")) {
                    firstName.setText("");
                    firstName.setSelection(firstName.getText().length());
                } else if (str.length() > 0 && str.contains(".")) {
                    firstName.setText(firstName.getText().toString().replaceAll("\\.", ""));
                    firstName.setSelection(firstName.getText().length());
                } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                    firstName.setText("");
                    firstName.setSelection(firstName.getText().length());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (s == lastName.getEditableText()) {
            try {
                String str = lastName.getText().toString();
                if (str.length() > 0 && str.substring(0).equals(" ")) {
                    lastName.setText("");
                    lastName.setSelection(lastName.getText().length());
                } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                    lastName.setText(lastName.getText().toString().replaceAll(".", ""));
                    lastName.setSelection(lastName.getText().length());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initialization() {
        try {
            retrieveEmailActivity = this;
            phoneNumberET = findViewById(R.id.rePhoneNumber);
            nextBtn = findViewById(R.id.reCardViewNextBtn);
            firstName = findViewById(R.id.reFirstNameET);
            lastName = findViewById(R.id.reLastNameET);
            layoutClose = findViewById(R.id.layoutClose);
            firstTIL = findViewById(R.id.reFirstNameTIL);
            lastTIL = findViewById(R.id.reLastNameTIL);
            layoutMain = findViewById(R.id.layoutMain);

            firstName.setFilters(new InputFilter[]{Utils.acceptonlyAlphabetValuesnotNumbersMethod()});
            firstName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            lastName.setFilters(new InputFilter[]{Utils.acceptonlyAlphabetValuesnotNumbersMethod()});
            lastName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

            phoneErrorLL = findViewById(R.id.phoneErrorLL);
            firstNameErrorLL = findViewById(R.id.firstNameErrorLL);
            lastNameErrorLL = findViewById(R.id.lastNameErrorLL);
            firstNameErrorTV = findViewById(R.id.firstNameErrorTV);
            lastNameErrorTV = findViewById(R.id.lastNameErrorTV);
            phoneErrorTV = findViewById(R.id.phoneErrorTV);

            phoneNumberET.setFrom("Retrieve");
            nextBtn.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (validation()) {
                            if (Utils.checkInternet(RetrieveEmailActivity.this)) {
                                retrieveEmail();
                            } else {
                                Utils.displayAlert(getString(R.string.internet), RetrieveEmailActivity.this, "", "");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            firstName.addTextChangedListener(this);
            lastName.addTextChangedListener(this);

            firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        firstName.setHint("");
                        if (firstName.getText().toString().trim().length() > 1) {
                            firstNameErrorLL.setVisibility(GONE);
                            firstTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(firstTIL, getColor(R.color.primary_black));
                        } else if (firstName.getText().toString().trim().length() == 1) {
                            firstTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(firstTIL, getColor(R.color.error_red));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            firstTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(firstTIL, getColor(R.color.light_gray));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Field Required");
                        }
                    } else {
                        firstNameErrorLL.setVisibility(GONE);
                        firstTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstTIL, getColor(R.color.primary_green));
                        firstName.setHint("First Name");
                    }
                }
            });

            lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        lastName.setHint("");
                        if (lastName.getText().toString().trim().length() > 1) {
                            lastNameErrorLL.setVisibility(GONE);
                            lastTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(lastTIL, getColor(R.color.primary_black));
                        } else if (lastName.getText().toString().trim().length() == 1) {
                            lastTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(lastTIL, getColor(R.color.error_red));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            lastTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(lastTIL, getColor(R.color.light_gray));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Field Required");
                        }
                    } else {
                        lastNameErrorLL.setVisibility(GONE);
                        lastTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastTIL, getColor(R.color.primary_green));
                        lastName.setHint("Last Name");
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getRetrieveEmailResponseMutableLiveData().observe(this, new Observer<RetrieveEmailResponse>() {
            @Override
            public void onChanged(RetrieveEmailResponse retrieveEmailResponse) {
                if (retrieveEmailResponse != null) {
                    if (!retrieveEmailResponse.getStatus().toLowerCase().equals("error")) {
//                        SMSResend resend = new SMSResend();
//                        resend.setCountryCode(Utils.getStrCCode());
//                        resend.setPhoneNumber(phoneNumber);
//                        loginViewModel.smsotpresend(resend);
                        Intent i = new Intent(RetrieveEmailActivity.this, OTPValidation.class);
                        i.putExtra("OTP_TYPE", "MOBILE");
                        i.putExtra("MOBILE", phoneNumber);
                        i.putExtra("firstname", firstName.getText().toString().trim());
                        i.putExtra("lastname", lastName.getText().toString().trim());
                        i.putExtra("MASK_MOBILE", phoneNumberET.getText().toString());
                        i.putExtra("screen", "retEmail");
                        startActivity(i);
                    } else {
                        dialog.dismiss();
                        if (retrieveEmailResponse.getData() != null) {
                            if (!retrieveEmailResponse.getData().getMessage().equals("")) {
//                                Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
//                                emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());

                                Utils.emailPasswordIncorrectDialog("", RetrieveEmailActivity.this, "");
                            }
                        } else {
                            Utils.displayAlert(retrieveEmailResponse.getError().getErrorDescription(), RetrieveEmailActivity.this, "", "");
                        }
                    }
                }
            }
        });

        loginViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (apiError.getError().getErrorCode().equals("218020") || apiError.getError().getErrorDescription().contains("issue with your OTP")) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), RetrieveEmailActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), RetrieveEmailActivity.this, "", "");
                        }
                    } else {
                        displayNoAccount();
                    }
                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (phoneNumberET.getText().toString().trim().equals("")) {
                phoneNumberET.setErrorOutlineBox();
                return value = false;
            } else if (!phoneNumberET.getText().toString().trim().equals("") && phoneNumberET.getText().toString().length() < 14) {
                phoneNumberET.setErrorOutlineBox();
                return value = false;
            } else if (firstName.getText().toString().trim().equals("")) {
                firstTIL.setErrorEnabled(true);
                firstTIL.setError(" ");
                return value = false;
            } else if (lastName.getText().toString().trim().equals("")) {
                lastTIL.setErrorEnabled(true);
                lastTIL.setError(" ");
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void retrieveEmail() {
        try {
            dialog = new Dialog(RetrieveEmailActivity.this);
            dialog.setContentView(R.layout.retrieve_email_processing_layout);
            dialog.getWindow().setBackgroundDrawableResource(R.color.white);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();
            phoneNumber = phoneNumberET.getText().toString().substring(1, 4) + phoneNumberET.getText().toString().substring(6, 9) + phoneNumberET.getText().toString().substring(10, phoneNumberET.getText().length());
            RetrieveEmailRequest request = new RetrieveEmailRequest();
            request.setFirstName(firstName.getText().toString().trim());
            request.setLastName(lastName.getText().toString().trim());
            request.setCountryCode(Utils.getStrCCode());
            request.setPhoneNumber(phoneNumber.trim());
            loginViewModel.retrieveEmail(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayNoAccount() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
            Intent i = new Intent(RetrieveEmailActivity.this, BindingLayoutActivity.class);
            i.putExtra("screen", "retEmailfail");
            startActivity(i);
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableButton() {
        try {
            if (!phoneNumberET.getText().toString().trim().equals("") && phoneNumberET.getText().toString().length() == 14
                    && firstName.getText().length() >= 2 && lastName.getText().length() >= 2) {
                nextBtn.setEnabled(true);
                nextBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                nextBtn.setEnabled(false);
                nextBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CreateAccountActivity.focusedID = 0;
    }
}
package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class RetrieveEmailActivity extends AppCompatActivity {
    OutLineBoxPhoneNumberEditText phoneNumberET;
    MaterialCardView nextBtn;
    TextInputEditText firstName, lastName;
    TextInputLayout firstTIL, lastTIL;
    LinearLayout layoutClose, layoutMain;
    LoginViewModel loginViewModel;
    Dialog dialog;
    public static RetrieveEmailActivity retrieveEmailActivity;
    String phoneNumber = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_retrieve_email);
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
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
            phoneNumberET.setFrom("Retrieve");
            nextBtn.setEnabled(false);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            phoneNumberET.requestFocus();
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validation()) {
                        if (Utils.checkInternet(RetrieveEmailActivity.this)) {
                            retrieveEmail();
                        } else {
                            Utils.displayAlert(getString(R.string.internet), RetrieveEmailActivity.this);
                        }
                    }
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            firstName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0) {
                            firstTIL.setErrorEnabled(false);
                            firstTIL.setError("");
                            enableButton();
                        } else if (s.length() == 0) {
                            firstTIL.setErrorEnabled(true);
                            firstTIL.setError(" ");
                            nextBtn.setEnabled(false);
                            nextBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lastName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0) {
                            lastTIL.setErrorEnabled(false);
                            lastTIL.setError("");
                            enableButton();
                        } else if (s.length() == 0) {
                            lastTIL.setErrorEnabled(true);
                            lastTIL.setError(" ");
                            nextBtn.setEnabled(false);
                            nextBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (firstName.getText().toString().trim().equals("")) {
                            firstTIL.setErrorEnabled(true);
                            firstTIL.setError(" ");
                            nextBtn.setEnabled(false);
                            nextBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    }
                }
            });

            lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (lastName.getText().toString().trim().equals("")) {
                            lastTIL.setErrorEnabled(true);
                            lastTIL.setError(" ");
                            nextBtn.setEnabled(false);
                            nextBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    }
                }
            });

            layoutMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Utils.hideKeypad(RetrieveEmailActivity.this);
                    }
                    return false;
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
                        SMSResend resend = new SMSResend();
                        resend.setCountryCode(Utils.getStrCCode());
                        resend.setPhoneNumber(phoneNumber);
                        loginViewModel.smsotpresend(resend);
                        Intent i = new Intent(RetrieveEmailActivity.this, OTPValidation.class);
                        i.putExtra("OTP_TYPE", "MOBILE");
                        i.putExtra("MOBILE", phoneNumber);
                        i.putExtra("MASK_MOBILE", phoneNumberET.getText().toString());
                        i.putExtra("screen", "retEmail");
                        startActivity(i);
                    } else {
                        dialog.dismiss();
                        if (retrieveEmailResponse.getData() != null) {
                            if (!retrieveEmailResponse.getData().getMessage().equals("")) {
                                Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                                emailpass_incorrect.show(getSupportFragmentManager(), emailpass_incorrect.getTag());
                            }
                        } else {
                            Utils.displayAlert(retrieveEmailResponse.getError().getErrorDescription(), RetrieveEmailActivity.this);
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
//                    if (apiError.getError().getErrorCode().equals("111069")) {
//                        displayNoAccount();
//                    } else if (!apiError.getError().getErrorDescription().equals("")) {
//                        Utils.displayAlert(apiError.getError().getErrorDescription(), RetrieveEmailActivity.this);
//                    } else {
//                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), RetrieveEmailActivity.this);
//                    }
                    displayNoAccount();
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
            dialog = new Dialog(RetrieveEmailActivity.this);
            dialog.setContentView(R.layout.retrieve_email_tryagain_layout);
            dialog.getWindow().setBackgroundDrawableResource(R.color.white);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();
            CardView reTryAgainBtn = dialog.findViewById(R.id.reTryAgainBtn);
            reTryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    phoneNumberET.setText("");
                    firstName.setText("");
                    lastName.setText("");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableButton() {
        try {
            if (!phoneNumberET.getText().toString().trim().equals("") && phoneNumberET.getText().toString().length() == 14
                    && !firstName.getText().toString().trim().equals("") && !lastName.getText().toString().trim().equals("")) {
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

}
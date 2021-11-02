package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.login.LoginRequest;
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
    ImageView imgClose;
    LoginViewModel loginViewModel;
    Dialog dialog;


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

    private void initialization() {
        try {
            phoneNumberET = findViewById(R.id.rePhoneNumber);
            nextBtn = findViewById(R.id.reCardViewNextBtn);
            firstName = findViewById(R.id.reFirstNameET);
            lastName = findViewById(R.id.reLastNameET);
            imgClose = findViewById(R.id.imgClose);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
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

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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
                        Intent i = new Intent(RetrieveEmailActivity.this, OTPValidation.class);
                        i.putExtra("OTP_TYPE", "MOBILE");
                        i.putExtra("MOBILE", phoneNumberET.getText());
                        i.putExtra("MASK_MOBILE", phoneNumberET.getText());
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
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        Utils.displayAlert(apiError.getError().getErrorDescription(), RetrieveEmailActivity.this);
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), RetrieveEmailActivity.this);
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

    public void startTimer(Dialog dialog) {
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(5000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(RetrieveEmailActivity.this, OTPValidation.class);
                                    i.putExtra("OTP_TYPE", "MOBILE");
                                    i.putExtra("MOBILE", phoneNumberET.getText());
                                    i.putExtra("MASK_MOBILE", phoneNumberET.getText());
                                    i.putExtra("screen", "retEmail");
                                    startActivity(i);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void retrieveEmail() {
        try {
            dialog = new Dialog(RetrieveEmailActivity.this);
            dialog.setContentView(R.layout.retrieve_email_processing_layout);
            dialog.getWindow().setBackgroundDrawableResource(R.color.white);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();
            String phoneNumber = phoneNumberET.getText().toString().substring(1, 4) + phoneNumberET.getText().toString().substring(6, 9) + phoneNumberET.getText().toString().substring(10, phoneNumberET.getText().length());
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

}
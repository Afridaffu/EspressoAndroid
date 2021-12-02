package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.register.EmailExistsResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

public class EditEmailActivity extends AppCompatActivity {

    TextInputEditText currentEmailET, newEmailET;
    TextInputLayout currentEmailTIL, newEmailTIL;
    MyApplication myApplicationObj;
    NestedScrollView editEmailSV;
    public boolean isSaveEnabled = false, isCurrentEmail = true, isNewEmail = false;
    LinearLayout currentEmailErrorLL, newEmailErrorLL;
    TextView currentEmailErrorTV, newEmailErrorTV, contactUsTV;
    CardView saveEmailCV;
    Long mLastClickTime = 0L;
    ProgressDialog dialog;
    CustomerProfileViewModel customerProfileViewModel;
    LoginViewModel loginViewModel;
    LinearLayout editEmailCloseLL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_email);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
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
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            myApplicationObj = (MyApplication) getApplicationContext();
            editEmailCloseLL = findViewById(R.id.editEmailCloseLL);
            currentEmailET = findViewById(R.id.currentEmailET);
            newEmailET = findViewById(R.id.newEmailET);
            currentEmailTIL = findViewById(R.id.currentEmailTIL);
            newEmailTIL = findViewById(R.id.newEmailTIL);
            editEmailSV = findViewById(R.id.editEmailSV);
            currentEmailErrorLL = findViewById(R.id.currentEmailErrorLL);
            newEmailErrorLL = findViewById(R.id.newEmailErrorLL);
            currentEmailErrorTV = findViewById(R.id.currentEmailErrorTV);
            newEmailErrorTV = findViewById(R.id.newEmailErrorTV);
            saveEmailCV = findViewById(R.id.saveEmailCV);
            contactUsTV = findViewById(R.id.contactUsTV);

            currentEmailET.setText(myApplicationObj.getMyProfile().getData().getEmail());

            newEmailET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});


//            editEmailSV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Utils.hideKeypad(EditEmailActivity.this);
//                    currentEmailET.clearFocus();
//                    newEmailET.clearFocus();
//                }
//            });


            saveEmailCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSaveEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        dialog = new ProgressDialog(EditEmailActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        callSendEmailOTPAPI();

                    } else {
                        Log.e("isSaveEnabled", isSaveEnabled + "");
                    }
                }
            });

            editEmailCloseLL.setOnClickListener(view -> {
                finish();
            });

            contactUsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        Utils.hideKeypad(EditEmailActivity.this);
                        Utils.hideSoftKeyboard(EditEmailActivity.this);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(Utils.mondayURL));
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void textWatchers() {
        try {
            currentEmailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 0 && Utils.isValidEmail(charSequence.toString().trim())) {
                        currentEmailErrorLL.setVisibility(GONE);
                        currentEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        currentEmailTIL.setHintTextColor(colorState);
                        Utils.setUpperHintColor(currentEmailTIL, getResources().getColor(R.color.primary_green));

                        isCurrentEmail = true;
                    } else if (currentEmailET.getText().toString().trim().length() == 0) {
                        currentEmailErrorLL.setVisibility(VISIBLE);
                        currentEmailErrorTV.setText("Field Required");
                        isCurrentEmail = false;
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim())) {
                        isCurrentEmail = true;
                    } else {
                        isCurrentEmail = false;
                    }
                    enableOrDisableSave();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = currentEmailET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            currentEmailET.setText(currentEmailET.getText().toString().replaceAll(" ", ""));
                            currentEmailET.setSelection(currentEmailET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            newEmailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 0 && Utils.isValidEmail(charSequence.toString().trim())) {
                        newEmailErrorLL.setVisibility(GONE);
                        newEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        newEmailTIL.setHintTextColor(colorState);
                        Utils.setUpperHintColor(newEmailTIL, getResources().getColor(R.color.primary_green));
                        isNewEmail = true;
                    } else if (newEmailET.getText().toString().trim().length() == 0) {
                        newEmailErrorLL.setVisibility(VISIBLE);
                        newEmailErrorTV.setText("Field Required");
                        isNewEmail = false;
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim())) {
                        isNewEmail = true;
                    } else {
                        isNewEmail = false;
                    }
                    enableOrDisableSave();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = newEmailET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            newEmailET.setText(newEmailET.getText().toString().replaceAll(" ", ""));
                            newEmailET.setSelection(newEmailET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusWatchers() {

        try {
            newEmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (newEmailET.getText().toString().trim().length() > 0 && !Utils.isValidEmail(newEmailET.getText().toString().trim())) {
                            newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(newEmailTIL, getColor(R.color.error_red));
                            newEmailErrorLL.setVisibility(VISIBLE);
                            newEmailErrorTV.setText("Invalid Email");
                        } else if (newEmailET.getText().toString().trim().length() > 0 && Utils.isValidEmail(newEmailET.getText().toString().trim())) {
                            newEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(newEmailTIL, getColor(R.color.primary_black));
                            newEmailErrorLL.setVisibility(GONE);
                            loginViewModel.validateEmail(newEmailET.getText().toString().trim());
                        } else {
                            newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(newEmailTIL, getColor(R.color.error_red));
                            newEmailErrorLL.setVisibility(VISIBLE);
                            newEmailErrorTV.setText("Field Required");
                        }
                    } else {
                        newEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(newEmailTIL, getColor(R.color.primary_green));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            currentEmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (currentEmailET.getText().toString().trim().length() > 0 && !Utils.isValidEmail(currentEmailET.getText().toString().trim())) {
                            currentEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(currentEmailTIL, getColor(R.color.error_red));
                            currentEmailErrorLL.setVisibility(VISIBLE);
                            currentEmailErrorTV.setText("Invalid Email");
                        } else if (currentEmailET.getText().toString().trim().length() > 0 && Utils.isValidEmail(currentEmailET.getText().toString().trim())) {
                            currentEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(currentEmailTIL, getColor(R.color.primary_black));
                            currentEmailErrorLL.setVisibility(GONE);
                            //                            loginViewModel.validateEmail(currentEmailET.getText().toString().trim());
                        } else {
                            currentEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(currentEmailTIL, getColor(R.color.error_red));
                            currentEmailErrorLL.setVisibility(VISIBLE);
                            currentEmailErrorTV.setText("Field Required");
                        }
                    } else {
                        currentEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(currentEmailTIL, getColor(R.color.primary_green));
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
            updateEmailRequest.setNewEmail(newEmailET.getText().toString().trim());
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
                        startActivity(new Intent(EditEmailActivity.this, OTPValidation.class)
                                .putExtra("screen", "EditEmail")
                                .putExtra("OTP_TYPE", "OTP")
                                .putExtra("IS_OLD_EMAIL", "true")
                                .putExtra("OLD_EMAIL", currentEmailET.getText().toString().trim())
                                .putExtra("NEW_EMAIL", newEmailET.getText().toString().trim())
                        );
                    } else {
                        if (updateEmailResponse.getError().getErrorDescription().equals("")) {
                            try {
                                Utils.hideSoftKeyboard(EditEmailActivity.this);
                                if(updateEmailResponse.getError().getFieldErrors().get(0).contains(":")){
                                    int counter = 0;
                                    for(int i =0 ; i < updateEmailResponse.getError().getFieldErrors().get(0).length();i++ ){
                                        if(String.valueOf(updateEmailResponse.getError().getFieldErrors().get(0).charAt(i)).equals(":")){
                                            counter = i;
                                            break;
                                        }
                                    }
                                    Utils.displayAlert(updateEmailResponse.getError().getFieldErrors().get(0).substring(counter+1), EditEmailActivity.this,"");

                                }else{
                                    Utils.displayAlert(updateEmailResponse.getError().getFieldErrors().get(0), EditEmailActivity.this,"");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.hideSoftKeyboard(EditEmailActivity.this);
                            Utils.displayAlert(updateEmailResponse.getError().getErrorDescription(), EditEmailActivity.this, "");
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
                        if (!emailExistsResponse.getStatus().toLowerCase().equals("error")) {
                            newEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(newEmailTIL, getColor(R.color.primary_black));
                            newEmailErrorLL.setVisibility(GONE);
                        } else {
                            newEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(newEmailTIL, getColor(R.color.error_red));
                            newEmailErrorLL.setVisibility(VISIBLE);
                            newEmailErrorTV.setText(emailExistsResponse.getError().getErrorDescription());
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
                            Utils.displayAlert(apiError.getError().getErrorDescription(), EditEmailActivity.this,"");
//                        }
                    } else {
                        Utils.hideSoftKeyboard(EditEmailActivity.this);
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), EditEmailActivity.this,"");
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            newEmailET.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
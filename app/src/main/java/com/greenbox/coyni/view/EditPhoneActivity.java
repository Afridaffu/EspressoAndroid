package com.greenbox.coyni.view;

import static android.view.View.GONE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

public class EditPhoneActivity extends AppCompatActivity {

    TextInputEditText currentEmailET, newEmailET;
    TextInputLayout currentEmailTIL, newEmailTIL;
    MyApplication myApplicationObj;
    NestedScrollView editEmailSV;
    public boolean isCurrentEmailError = false, isNewEmailError = false,
            isSaveEnabled = false, isCurrentEmail = true, isNewEmail = false;
    LinearLayout currentEmailErrorLL,newEmailErrorLL;
    TextView currentEmailErrorTV,newEmailErrorTV;

    int[][] errorState, state;
    int[] errorColor, color;
    ColorStateList errorColorState, colorState;
    MaterialCardView saveEmailCV;
    Long mLastClickTime = 0L;
    ProgressDialog dialog;
    CustomerProfileViewModel customerProfileViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_phone);
            initFields();

            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields(){
        try {
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

            errorState = new int[][]{new int[]{android.R.attr.state_enabled}};
            errorColor = new int[]{getResources().getColor(R.color.error_red)};
            errorColorState = new ColorStateList(errorState, errorColor);

            state = new int[][]{new int[]{android.R.attr.state_enabled}};
            color = new int[]{getResources().getColor(R.color.primary_green)};
            colorState = new ColorStateList(state, color);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            myApplicationObj = (MyApplication) getApplicationContext();
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

            currentEmailET.setText(myApplicationObj.getMyProfile().getData().getEmail());


            editEmailSV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideKeypad(EditPhoneActivity.this);
                    currentEmailET.clearFocus();
                    newEmailET.clearFocus();
                }
            });


            currentEmailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        isCurrentEmailError = false;
                        currentEmailErrorLL.setVisibility(GONE);
                        currentEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        currentEmailTIL.setHintTextColor(colorState);
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

                }
            });

            currentEmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!currentEmailET.getText().toString().trim().equals("")){
                        if (Utils.isValidEmail(currentEmailET.getText().toString().trim())) {
                            isCurrentEmailError = false;
                            currentEmailErrorLL.setVisibility(GONE);
                        } else {
                            isCurrentEmailError = true;
                            currentEmailErrorLL.setVisibility(View.VISIBLE);
                            currentEmailErrorTV.setText("Field Required");
                        }
                    }
                }
            });

            newEmailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        isNewEmailError = false;
                        newEmailErrorLL.setVisibility(GONE);
                        newEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        newEmailTIL.setHintTextColor(colorState);
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim())) {
                        Log.e("valid","valid");
                        isNewEmail = true;
                    } else {
                        Log.e("valid","not valid");
                        isNewEmail = false;
                    }
                    enableOrDisableSave();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            newEmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!newEmailET.getText().toString().trim().equals("")){
                        if (Utils.isValidEmail(newEmailET.getText().toString().trim())) {
                            isNewEmailError = false;
                            newEmailErrorLL.setVisibility(GONE);
                        } else {
                            isNewEmailError = true;
                            newEmailErrorLL.setVisibility(View.VISIBLE);
                            newEmailErrorTV.setText("Field Required");
                        }
                    }
                }
            });

            saveEmailCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSaveEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        dialog = new ProgressDialog(EditPhoneActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        callSendEmailOTPAPI();

                    }else{
                        Log.e("isSaveEnabled", isSaveEnabled+"");
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
            Log.e("all fields", isCurrentEmail+" "+isNewEmail);
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

    public void initObservers(){

        customerProfileViewModel.getUpdateEmailSendOTPResponse().observe(this, new Observer<UpdateEmailResponse>() {
            @Override
            public void onChanged(UpdateEmailResponse updateEmailResponse) {
                dialog.dismiss();
                if(updateEmailResponse!= null && updateEmailResponse.getStatus().toLowerCase().equals("success")){
                    myApplicationObj.setUpdateEmailResponse(updateEmailResponse);
                    startActivity(new Intent(EditPhoneActivity.this, OTPValidation.class)
                            .putExtra("screen", "EditEmail")
                            .putExtra("IS_OLD_EMAIL","true")
                            .putExtra("OLD_EMAIL", currentEmailET.getText().toString().trim())
                            .putExtra("NEW_EMAIL", newEmailET.getText().toString().trim())
                    );
                }else {
                    Utils.displayAlert(updateEmailResponse.getData().getMessage(), EditPhoneActivity.this);
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
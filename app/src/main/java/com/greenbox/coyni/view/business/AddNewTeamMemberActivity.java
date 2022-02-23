package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.Objects;

public class AddNewTeamMemberActivity extends BaseActivity {

    private LinearLayout backBtnLL, fNameErrorLL, lNameErrorLL, emailIdErrorLL, phoneNoErrorLL;
    private TextInputEditText fNameET, lNameET, emailIdET;
    private TextInputLayout fNameTIL, lNameTIL, emailIdTIL;
    private TextView fNameErrorTV, lNameErrorTV, emailIdErrorTV, phoneNoErrorTV;
    private OutLineBoxPhoneNumberEditText phoneNoET;
    private CardView cvSend;
    private boolean isFName = false, isLName = false, isEmailId = false;

    LoginViewModel loginViewModel;
    public static int focusedID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_new_team_member);

            initialization();
            initObserver();
            focusWatchers();
            textWatchers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialization() {

        try {
            backBtnLL = findViewById(R.id.backBtnLL);
            fNameET = findViewById(R.id.fNameET);
            lNameET = findViewById(R.id.lNameET);
            emailIdET = findViewById(R.id.emailIdET);
            phoneNoET = findViewById(R.id.phoneNoET);
            fNameTIL = findViewById(R.id.fNameTIL);
            lNameTIL = findViewById(R.id.lNameTIL);
            emailIdTIL = findViewById(R.id.emailIdTIL);
            fNameErrorLL = findViewById(R.id.fNameLL);
            lNameErrorLL = findViewById(R.id.lNameLL);
            emailIdErrorLL = findViewById(R.id.emailIdLL);
            phoneNoErrorLL = findViewById(R.id.phoneNoLL);
            fNameErrorTV = findViewById(R.id.fNameTV);
            lNameErrorTV = findViewById(R.id.lNameTV);
            emailIdErrorTV = findViewById(R.id.emailIdTV);
            phoneNoErrorTV = findViewById(R.id.phoneNoTV);
            cvSend = findViewById(R.id.cvSend);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            backBtnLL.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initObserver() {
        loginViewModel.getEmailExistsResponseMutableLiveData().observe(this, emailExistsResponse -> {
            if (emailExistsResponse.getError() != null){
                emailIdErrorLL.setVisibility(VISIBLE);
                emailIdErrorTV.setText(emailExistsResponse.getError().getErrorDescription().split("or")[0]+".");
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void focusWatchers() {
        try {
            fNameET.setOnFocusChangeListener((view, b) -> {
                if (!b) {
                    fNameET.setHint("");
                    if (Objects.requireNonNull(fNameET.getText()).toString().trim().length() > 1) {
                        fNameErrorLL.setVisibility(GONE);
                        fNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(fNameTIL, getColor(R.color.primary_black));

                    } else if (fNameET.getText().toString().trim().length() == 1) {
                        fNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(fNameTIL, getColor(R.color.error_red));
                        fNameErrorLL.setVisibility(VISIBLE);
                        fNameErrorTV.setText("Minimum 2 Characters Required");
                    } else {
                        fNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(fNameTIL, getColor(R.color.light_gray));
                        fNameErrorLL.setVisibility(VISIBLE);
                        fNameErrorTV.setText("Field Required");
                    }
                } else {
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(AddNewTeamMemberActivity.this);
                    fNameErrorLL.setVisibility(GONE);
                    focusedID = fNameET.getId();
                    fNameET.setHint("First Name");
                    fNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(fNameTIL, getColor(R.color.primary_green));
                }
            });

            lNameET.setOnFocusChangeListener((view, b) -> {
                if (!b) {
                    lNameET.setHint("");
                    if (lNameET.getText().toString().trim().length() > 1) {
                        lNameErrorLL.setVisibility(GONE);
                        lNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(lNameTIL, getColor(R.color.primary_black));

                    } else if (lNameET.getText().toString().trim().length() == 1) {
                        lNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(lNameTIL, getColor(R.color.error_red));
                        lNameErrorLL.setVisibility(VISIBLE);
                        lNameErrorTV.setText("Minimum 2 Characters Required");
                    } else {
                        lNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(lNameTIL, getColor(R.color.light_gray));
                        lNameErrorLL.setVisibility(VISIBLE);
                        lNameErrorTV.setText("Field Required");
                    }
                } else {

                    lNameErrorLL.setVisibility(GONE);
                    focusedID = lNameET.getId();
                    lNameET.setHint("Last Name");
                    lNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(lNameTIL, getColor(R.color.primary_green));
                }
            });

            emailIdET.setOnFocusChangeListener((view, b) -> {
                if (!b) {
                    emailIdET.setHint("");
                    if (Objects.requireNonNull(emailIdET.getText()).toString().trim().length() > 5 && !Utils.isValidEmail(emailIdET.getText().toString().trim())) {
                        emailIdTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(emailIdTIL, getColor(R.color.error_red));
                        emailIdErrorLL.setVisibility(VISIBLE);
                        emailIdErrorTV.setText("Please Enter a valid Email");
                    } else if (emailIdET.getText().toString().trim().length() > 5 && Utils.isValidEmail(emailIdET.getText().toString().trim())) {
                        emailIdTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(emailIdTIL, getColor(R.color.primary_black));
                        emailIdErrorLL.setVisibility(GONE);
                        loginViewModel.validateEmail(emailIdET.getText().toString().trim());
                    } else if (emailIdET.getText().toString().trim().length() > 0 && emailIdET.getText().toString().trim().length() <= 5) {
                        emailIdTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(emailIdTIL, getColor(R.color.error_red));
                        emailIdErrorLL.setVisibility(VISIBLE);
                        emailIdErrorTV.setText("Field Required");
                    } else {
                        emailIdTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(emailIdTIL, getColor(R.color.light_gray));
                        emailIdErrorLL.setVisibility(VISIBLE);
                        emailIdErrorTV.setText("Field Required");
                    }
                } else {
                    emailIdErrorLL.setVisibility(GONE);
                    focusedID = emailIdET.getId();
                    emailIdET.setHint("Email");
                    emailIdTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(emailIdTIL, getColor(R.color.primary_green));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void textWatchers() {

        try {
            fNameET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isFName = true;
                        fNameErrorLL.setVisibility(GONE);
                        fNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(fNameTIL, getResources().getColor(R.color.primary_green));
                    } else {
                        isFName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String str = fNameET.getText().toString();
                        if (str.length() > 0 && str.trim().length() == 0) {
                            fNameET.setText("");
                            fNameET.setSelection(fNameET.getText().length());
                        } else if (str.length() > 0 && str.contains(".")) {
                            fNameET.setText(fNameET.getText().toString().replaceAll("\\.", ""));
                            fNameET.setSelection(fNameET.getText().length());
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            fNameET.setText("");
                            fNameET.setSelection(fNameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lNameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isLName = true;
                        lNameErrorLL.setVisibility(GONE);
                        lNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lNameTIL, getResources().getColor(R.color.primary_green));
                    } else {
                        isLName = false;
                    }

                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = Objects.requireNonNull(lNameET.getText()).toString();
                        if (str.length() > 0 && str.trim().length() == 0) {
                            lNameET.setText(lNameET.getText().toString().replaceAll(" ", ""));
                            lNameET.setSelection(lNameET.getText().length());
                        } else if (str.length() > 0 && str.endsWith(".")) {
                            lNameET.setText(lNameET.getText().toString().replaceAll("\\.", ""));
                            lNameET.setSelection(lNameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            emailIdET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                        isEmailId = false;
                        emailIdErrorLL.setVisibility(GONE);
                        emailIdTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(emailIdTIL, getResources().getColor(R.color.primary_green));

                    }

                    isEmailId = Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5;
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = Objects.requireNonNull(emailIdET.getText()).toString();
                        if ((str.length() > 0 && str.trim().length() == 0) || (str.length() > 0 && str.contains(" "))) {
                            emailIdET.setText(emailIdET.getText().toString().replaceAll(" ", ""));
                            emailIdET.setSelection(emailIdET.getText().length());
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

    private void enableOrDisableNext() {
        if (isFName && isLName && isEmailId) {
            cvSend.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
        } else {
            cvSend.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }
}
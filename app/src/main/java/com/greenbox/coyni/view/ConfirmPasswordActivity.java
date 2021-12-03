package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.login.PasswordRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.regex.Pattern;

public class ConfirmPasswordActivity extends AppCompatActivity {
    TextInputEditText currentPassET;
    TextInputLayout currentTIL;
    CardView saveBtn;
    boolean btnEnabled = false;
    ImageView backBtn;
    String oldPassword;
    Boolean isCPwdEye = false;
    private Pattern strong;
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*!?]).{8,})";
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    MyApplication objMyApplication;
    LinearLayout layoutPwdError;
    TextView tvPwdError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        try {
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            currentPassET = findViewById(R.id.currentPassET);
            currentTIL = findViewById(R.id.currentPassTIL);
            saveBtn = findViewById(R.id.saveBtnCV);
            backBtn = findViewById(R.id.cpConfirmBackIV);
            layoutPwdError = findViewById(R.id.layoutPwdError);
            tvPwdError = findViewById(R.id.tvPwdError);
            strong = Pattern.compile(STRONG_PATTERN);

            currentTIL.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isCPwdEye) {
                            isCPwdEye = true;
                            currentTIL.setEndIconDrawable(R.drawable.ic_eyeopen);
                            currentPassET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isCPwdEye = false;
                            currentTIL.setEndIconDrawable(R.drawable.ic_eyeclose);
                            currentPassET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        if (currentPassET.getText().length() > 0) {
                            currentPassET.setSelection(currentPassET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            currentPassET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() >= 8 && strong.matcher(currentPassET.getText().toString().trim()).matches()) {
                        btnEnabled = true;
                        oldPassword = currentPassET.getText().toString().trim();
                        saveBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    } else {
                        btnEnabled = false;
                        saveBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnEnabled) {
                        dialog = Utils.showProgressDialog(ConfirmPasswordActivity.this);
                        PasswordRequest passwordRequest = new PasswordRequest();
                        passwordRequest.setUsername(objMyApplication.getStrEmail());
                        passwordRequest.setPassword(currentPassET.getText().toString());
                        loginViewModel.authenticatePassword(passwordRequest);
//                        startActivity(new Intent(ConfirmPasswordActivity.this, CreatePasswordActivity.class)
//                                .putExtra("screen", "ConfirmPassword")
//                                .putExtra("oldpassword", oldPassword)
//                        );
//                        clearField();
//                        Log.e("oldPass", "" + oldPassword);
                    }

                }
            });

            loginViewModel.getAuthenticatePasswordResponse().observe(this, new Observer<LoginResponse>() {
                @Override
                public void onChanged(LoginResponse loginResponse) {
                    dialog.dismiss();
                    if (loginResponse != null) {
                        if (loginResponse.getStatus().trim().toLowerCase().equals("success")) {
                            layoutPwdError.setVisibility(View.GONE);
                            startActivity(new Intent(ConfirmPasswordActivity.this, CreatePasswordActivity.class)
                                    .putExtra("screen", "ConfirmPassword")
                                    .putExtra("oldpassword", oldPassword)
                            );
                            clearField();

                        } else {
                            if(loginResponse.getError().getErrorDescription().contains("Invalid credentials")){
                                layoutPwdError.setVisibility(View.VISIBLE);
                                tvPwdError.setText("Password Not Matched");
                            }else{
                                layoutPwdError.setVisibility(View.GONE);
                                Utils.displayAlert(loginResponse.getError().getErrorDescription(),ConfirmPasswordActivity.this,"");
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void clearField() {
        currentPassET.setText("");
    }
}
package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.CutCopyPasteEditText;
import com.greenbox.coyni.utils.EmojiExcludeFilter;
import com.greenbox.coyni.utils.Singleton;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxEditText;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

public class CreateAccountActivity extends AppCompatActivity {

    OutLineBoxPhoneNumberEditText phoneNumberET;
    TextInputEditText firstNameET, lastNameET, emailET, passwordET, confirmPasswordET;
    TextInputLayout firstNameTIL, lastNameTIL, emailTIL, passwordTIL, confPasswordTIL;
    public LinearLayout emailErrorLL, phoneErrorLL, firstNameErrorLL, lastNameErrorLL, passwordErrorLL, confPassErrorLL;
    public TextView emailErrorTV, phoneErrorTV, firstNameErrorTV, lastNameErrorTV, passwordErrorTV, confPassErrorTV;
    TextView passwordInfoTV, spannableText;
    public boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false,
            isPassword = false, isConfirmPassword = false, isNextEnabled = false;
    public String passwordString = "";
    public MaterialCardView nextCV;
    Long mLastClickTime = 0L;

    private LinearLayout stregnthViewLL;
    private View stregnthOne, stregnthTwo, stregnthThree;
    private Pattern strong, medium;
    LinearLayout layoutClose;
    ImageView createAccountCloseIV;

    //    !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~]).{8,})";

    private static final String MEDIUM_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~]).{6,})";

    public static CreateAccountActivity createAccountActivity;

    ProgressDialog dialog;
    LoginViewModel loginViewModel;
    String phoneNumber;

    String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";

    int[][] errorState, state;
    int[] errorColor, color;
    ColorStateList errorColorState, colorState;

    boolean isEmailError = false, isPhoneError = false;

    RelativeLayout mainRL;
    ScrollView mainSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_account);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            initFields();
            intiObserver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstNameET.requestFocus();
    }

    public void initFields() {
        try {
            createAccountActivity = this;
            errorState = new int[][]{new int[]{android.R.attr.state_focused}};
            errorColor = new int[]{getResources().getColor(R.color.error_red)};
            errorColorState = new ColorStateList(errorState, errorColor);

            state = new int[][]{new int[]{android.R.attr.state_enabled}};
            color = new int[]{getResources().getColor(R.color.primary_green)};
            colorState = new ColorStateList(state, color);

            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            emailErrorLL = findViewById(R.id.emailErrorLL);
            phoneErrorLL = findViewById(R.id.phoneErrorLL);
            firstNameErrorLL = findViewById(R.id.firstNameErrorLL);
            lastNameErrorLL = findViewById(R.id.lastNameErrorLL);
            passwordErrorLL = findViewById(R.id.passwordErrorLL);
            confPassErrorLL = findViewById(R.id.confPassErrorLL);
            firstNameErrorTV = findViewById(R.id.firstNameErrorTV);
            lastNameErrorTV = findViewById(R.id.lastNameErrorTV);
            emailErrorTV = findViewById(R.id.emailErrorTV);
            phoneErrorTV = findViewById(R.id.phoneErrorTV);
            passwordErrorTV = findViewById(R.id.passwordErrorTV);
            confPassErrorTV = findViewById(R.id.confPassErrorTV);

            passwordInfoTV = findViewById(R.id.passwordInfoTV);
            spannableText = findViewById(R.id.spannableTV);
            nextCV = findViewById(R.id.nextCV);
            layoutClose = findViewById(R.id.layoutClose);
            createAccountCloseIV = findViewById(R.id.createAccountCloseIV);

            firstNameET = findViewById(R.id.firstNameET);
            firstNameTIL = findViewById(R.id.firstNameTIL);
            lastNameTIL = findViewById(R.id.lastNameTIL);
            emailTIL = findViewById(R.id.emailTIL);
            passwordTIL = findViewById(R.id.passwordTIL);
            confPasswordTIL = findViewById(R.id.confPasswordTIL);

            lastNameET = findViewById(R.id.lastNameET);
            emailET = findViewById(R.id.emailET);
            phoneNumberET = findViewById(R.id.phoneNumberET);
            phoneNumberET.setFrom("CREATE_ACCOUNT");

            passwordET = findViewById(R.id.passwordET);
            passwordInfoTV = findViewById(R.id.passwordInfoTV);
            confirmPasswordET = findViewById(R.id.confirmPasswordET);

            stregnthViewLL = findViewById(R.id.stregnthViewLL);
            stregnthOne = findViewById(R.id.stregnthOne);
            stregnthTwo = findViewById(R.id.stregnthTwo);
            stregnthThree = findViewById(R.id.stregnthThree);
            mainRL = findViewById(R.id.mainRL);
            mainSV = findViewById(R.id.mainSV);

            strong = Pattern.compile(STRONG_PATTERN);
            medium = Pattern.compile(MEDIUM_PATTERN);
            firstNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            firstNameET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

            lastNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            lastNameET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

            emailET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});

            passwordET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            confirmPasswordET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});

            textWatchers();
            focusWatchers();
//            firstNameET.requestFocus();

            setSpannableText();

            nextCV.setOnClickListener(view -> {
                if (isNextEnabled) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    dialog = new ProgressDialog(CreateAccountActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    phoneNumber = phoneNumberET.getText().toString().substring(1, 4) + phoneNumberET.getText().toString().substring(6, 9) + phoneNumberET.getText().toString().substring(10, phoneNumberET.getText().length());

                    callRegisterAPI();

                } else {
//                    phoneNumber = phoneNumberET.getText().toString().substring(1, 4) + phoneNumberET.getText().toString().substring(6, 9) + phoneNumberET.getText().toString().substring(10, phoneNumberET.getText().length());
//                    Intent i = new Intent(CreateAccountActivity.this, OTPValidation.class);
//                    i.putExtra("screen", "SignUp");
//                    i.putExtra("OTP_TYPE", "MOBILE");
//                    i.putExtra("MOBILE", phoneNumber);
//                    i.putExtra("MASK_MOBILE",phoneNumberET.getText());
//                    i.putExtra("EMAIL", emailET.getText().toString().trim());
//                    startActivity(i);
                }
            });

            createAccountCloseIV.setOnClickListener(view -> {
                onBackPressed();
            });

//            mainRL.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    Utils.hideKeypad(CreateAccountActivity.this);
//                    return false;
//                }
//            });
//            mainSV.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                        Utils.hideKeypad(CreateAccountActivity.this);
//                    }
//                    return false;
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void intiObserver() {

        loginViewModel.getCustRegisResponseMutableLiveData().observe(this, new Observer<CustRegisterResponse>() {
            @Override
            public void onChanged(CustRegisterResponse custRegisterResponse) {
                try {
                    dialog.dismiss();
                    if (custRegisterResponse != null) {
                        try {
                            Intent i = new Intent(CreateAccountActivity.this, OTPValidation.class);
                            if (!custRegisterResponse.getData().isSmsVerified() && !custRegisterResponse.getData().isEmailVerified()) {
                                i.putExtra("screen", "SignUp");
                                i.putExtra("OTP_TYPE", "MOBILE");
                                i.putExtra("MOBILE", phoneNumber);
                                i.putExtra("MASK_MOBILE", phoneNumberET.getText());
                                i.putExtra("EMAIL", emailET.getText().toString().trim());
                            } else if (custRegisterResponse.getData().isSmsVerified() && !custRegisterResponse.getData().isEmailVerified()) {
                                i.putExtra("screen", "SignUp");
                                i.putExtra("OTP_TYPE", "EMAIL");
                                i.putExtra("MOBILE", phoneNumber);
                                i.putExtra("MASK_MOBILE", phoneNumberET.getText());
                                i.putExtra("EMAIL", emailET.getText().toString().trim());
                            } else if (custRegisterResponse.getData().isSmsVerified() && custRegisterResponse.getData().isEmailVerified()) {
                                i.putExtra("screen", "SignUp");
                                i.putExtra("OTP_TYPE", "SECURE");
                                i.putExtra("MOBILE", phoneNumber);
                                i.putExtra("MASK_MOBILE", phoneNumberET.getText());
                                i.putExtra("EMAIL", emailET.getText().toString().trim());
                            }
                            startActivity(i);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    Log.e("Error Message", s);
                    if (s.toString().toLowerCase().contains("entered email already exist in the system")) {
                        isEmailError = true;
                        emailTIL.requestFocus();
                        emailErrorLL.setVisibility(VISIBLE);
                        phoneErrorLL.setVisibility(GONE);
                    } else if (s.toString().toLowerCase().contains("entered phone number already exist in the system")) {
                        isPhoneError = true;
                        phoneNumberET.setErrorOutlineBox();
                        phoneErrorLL.setVisibility(VISIBLE);
                        phoneErrorTV.setText("User already exists. Try a new phone number or log in.");
                        emailErrorLL.setVisibility(GONE);
                    } else if (s.toString().toLowerCase().contains("entered phone number and email already exist in the system")) {
                        isPhoneError = true;
                        phoneNumberET.setErrorOutlineBox();
                        phoneErrorLL.setVisibility(VISIBLE);

                        isEmailError = true;
                        emailTIL.requestFocus();
                        emailErrorLL.setVisibility(VISIBLE);
                        emailErrorTV.setText("User already exists. Try a new email or log in.");
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.error_red));
                        emailTIL.setHintTextColor(errorColorState);

                    } else {
                        Log.e("Error message", s);
                        isEmailError = false;
                        isPhoneError = false;
                        phoneErrorLL.setVisibility(GONE);
                        emailErrorLL.setVisibility(GONE);
                        Utils.displayAlert(s, CreateAccountActivity.this);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void textWatchers() {

        try {
            firstNameET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 31) {
                        isFirstName = true;
                        firstNameErrorLL.setVisibility(GONE);
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        firstNameTIL.setHintTextColor(colorState);
                    } else {
                        isFirstName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String str = firstNameET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ")) {
                            firstNameET.setText(firstNameET.getText().toString().replaceAll(" ", ""));
                            firstNameET.setSelection(firstNameET.getText().length());
                        }else if(str.length() > 0 && str.substring(str.length()-1).equals(".")){
                            firstNameET.setText(firstNameET.getText().toString().replaceAll(".", ""));
                            firstNameET.setSelection(firstNameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lastNameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        isLastName = true;
                        lastNameErrorLL.setVisibility(GONE);
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        lastNameTIL.setHintTextColor(colorState);
                    } else {
                        isLastName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = lastNameET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ")) {
                            lastNameET.setText(lastNameET.getText().toString().replaceAll(" ", ""));
                            lastNameET.setSelection(lastNameET.getText().length());
                        }else if(str.length() > 0 && str.substring(str.length()-1).equals(".")){
                            lastNameET.setText(lastNameET.getText().toString().replaceAll(".", ""));
                            lastNameET.setSelection(lastNameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            emailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 0 && Utils.isValidEmail(charSequence.toString().trim())) {
                        isEmailError = false;
                        emailErrorLL.setVisibility(GONE);
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        emailTIL.setHintTextColor(colorState);
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim())) {
                        isEmail = true;
                    } else {
                        isEmail = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = emailET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            emailET.setText(emailET.getText().toString().replaceAll(" ", ""));
                            emailET.setSelection(emailET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            passwordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (strong.matcher(charSequence).matches()) {
                        stregnthOne.setVisibility(VISIBLE);
                        stregnthTwo.setVisibility(VISIBLE);
                        stregnthThree.setVisibility(VISIBLE);
                        stregnthOne.setBackgroundColor(getResources().getColor(R.color.primary_color));
                        stregnthTwo.setBackgroundColor(getResources().getColor(R.color.primary_color));
                        stregnthThree.setBackgroundColor(getResources().getColor(R.color.primary_color));
                        passwordInfoTV.setVisibility(GONE);
                    } else if (medium.matcher(charSequence).matches()) {
                        stregnthOne.setVisibility(VISIBLE);
                        stregnthTwo.setVisibility(VISIBLE);
                        stregnthThree.setVisibility(INVISIBLE);
                        stregnthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                        stregnthTwo.setBackgroundColor(getResources().getColor(R.color.error_red));
                        passwordInfoTV.setVisibility(VISIBLE);
                        passwordInfoTV.setTextColor(getResources().getColor(R.color.error_red));

                    } else {
                        stregnthOne.setVisibility(VISIBLE);
                        stregnthTwo.setVisibility(INVISIBLE);
                        stregnthThree.setVisibility(INVISIBLE);
                        stregnthOne.setBackgroundColor(getResources().getColor(R.color.error_red));
                        passwordInfoTV.setVisibility(VISIBLE);
                        passwordInfoTV.setTextColor(getResources().getColor(R.color.error_red));
                    }

                    if (charSequence.toString().trim().length() > 7 && strong.matcher(charSequence.toString().trim()).matches()) {
                        isPassword = true;
                    } else {
                        isPassword = false;
                    }
                    if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                        isConfirmPassword = true;

                        passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                        passwordTIL.setHint("Password");
                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                        confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        confPasswordTIL.setHint("Confirm Password");
                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                    } else {
                        isConfirmPassword = false;

                        if(confirmPasswordET.getText().toString().trim().length() > 0){
                            passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            passwordTIL.setHint("Password doesn’t match");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            confPasswordTIL.setHint("Password doesn’t match");
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                        }
                    }
                    enableOrDisableNext();

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            passwordET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            passwordET.setText(s.toString().trim());
                            passwordET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confirmPasswordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (passwordET.getText().toString().trim().length() > 7 && strong.matcher(passwordET.getText().toString().trim()).matches()) {
                        isPassword = true;
                    } else {
                        isPassword = false;
                    }

                    if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                        isConfirmPassword = true;

                        passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        passwordTIL.setHint("Password");
                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                        confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                        confPasswordTIL.setHint("Confirm Password");
                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));

                    } else {
                        isConfirmPassword = false;

                        passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        passwordTIL.setHint("Password doesn’t match");
                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));

                        confPasswordTIL.setHint("Password doesn’t match");

                        if (passwordET.getText().toString().trim().length() == confirmPasswordET.getText().toString().trim().length()) {
                            confirmPasswordET.clearFocus();
                        }

                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            confirmPasswordET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            confirmPasswordET.setText(s.toString().trim());
                            confirmPasswordET.setSelection(s.toString().trim().length());
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
            firstNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (firstNameET.getText().toString().trim().length() > 0) {
                            firstNameErrorLL.setVisibility(GONE);
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_black));
                        } else {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.error_red));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Field Required");
                        }
                    } else {
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_green));
                    }
                }
            });

            lastNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (lastNameET.getText().toString().trim().length() > 0) {
                            lastNameErrorLL.setVisibility(GONE);
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_black));
                        } else {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.error_red));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Field Required");
                        }
                    } else {
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_green));
                    }
                }
            });

            passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        stregnthViewLL.setVisibility(VISIBLE);
                        passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
//                        /////
//                        if(passwordET.getText().toString().trim().equals(passwordET.getText().toString().trim())){
//                            passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
//                            passwordTIL.setHint("Password");
//
//                        }else{
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            passwordTIL.setHint("Password doesn’t match");
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
//
//                            passwordTIL.setHint("Password doesn’t match");
//                        }
//                        /////
                    } else {
                        stregnthViewLL.setVisibility(GONE);
                        if (strong.matcher(passwordET.getText().toString()).matches()) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                        } else {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                        }
                    }
                }
            });

            confirmPasswordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
//                        /////
//                        if(passwordET.getText().toString().trim().length() > 7 && strong.matcher(passwordET.getText().toString().trim()).matches()){
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                            passwordTIL.setHint("Password");
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
//                        }else{
//                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            passwordTIL.setHint("Password doesn’t match");
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
//                        }
//                        /////

                        confPasswordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                    } else {
                        if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                        } else {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                        }
                    }

                }
            });

            emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (emailET.getText().toString().trim().length() > 0 && !Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Invalid Email");
                        } else if (emailET.getText().toString().trim().length() > 0 && Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_black));
                            emailErrorLL.setVisibility(GONE);
                        } else {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Field Required");
                        }
                    } else {
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_green));
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void enableOrDisableNext() {

        try {
            if (isFirstName && isLastName && isEmail && isPhoneNumber && isPassword && isConfirmPassword) {
                isNextEnabled = true;
                nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isFirstName + " " + isLastName + " " +
                        isEmail + " " + isPhoneNumber + " " +
                        isPassword + " " + isConfirmPassword);
            } else {

                Log.e("All boolean", isFirstName + " " + isLastName + " " +
                        isEmail + " " + isPhoneNumber + " " +
                        isPassword + " " + isConfirmPassword);

                isNextEnabled = false;
                nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static InputFilter acceptonlyAlphabetValuesnotNumbersMethod() {
        return new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                boolean isCheck = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) {
                        sb.append(c);
                    } else {
                        isCheck = false;
                    }
                }
                if (isCheck)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString spannableString = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, spannableString, 0);
                        return spannableString;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
                Matcher match = pattern.matcher(String.valueOf(c));
                return match.matches();
            }
        };
    }

    private void callRegisterAPI() {
        try {

            PhNoWithCountryCode phone = new PhNoWithCountryCode();
            phone.setCountryCode(Utils.strCCode);
            phone.setPhoneNumber(phoneNumber);

            CustRegisRequest regisRequest = new CustRegisRequest();
            regisRequest.setFirstName(firstNameET.getText().toString().trim());
            regisRequest.setLastName(lastNameET.getText().toString().trim());
            regisRequest.setPhoneNumberWithCountryCode(phone);
            regisRequest.setEmail(emailET.getText().toString().trim());
            regisRequest.setCreatePassword(passwordET.getText().toString().trim());
            regisRequest.setConfirmPassword(passwordET.getText().toString().trim());
            regisRequest.setAccountType(Utils.PERSONAL_ACCOUNT);
            regisRequest.setParentAccount(0);
            regisRequest.setEntityName(firstNameET.getText().toString().trim() + " " + lastNameET.getText().toString().trim());
            if (Singleton.getCustRegisterResponse().getData().getUserId().equals("")) {
                loginViewModel.customerRegistration(regisRequest, "POST");
            } else {
                regisRequest.setUserId(Singleton.getCustRegisterResponse().getData().getUserId() + "");
                loginViewModel.customerRegistration(regisRequest, "PATCH");
            }
            Log.e("Regsiter Object", new Gson().toJson(regisRequest));
            Singleton.setCustRegisRequest(regisRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Singleton.setCustRegisterResponse(new CustRegisterResponse());
    }

    //    static String enccriptData(String txt)
//    {
//        String encoded = "";
//        byte[] encrypted = null;
//        try {
//            byte[] publicBytes = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            PublicKey pubKey = keyFactory.generatePublic(keySpec);
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//            encrypted = cipher.doFinal(txt.getBytes());
//            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        return encoded;
//    }

    public void setSpannableText() {

        SpannableString ss = new SpannableString("By clicking next, you agree to Term of Service & Privacy Policy");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.e("Click", "click");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(tosURL), "application/pdf");
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.e("Click", "click");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(privacyURL), "application/pdf");
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 31, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 49, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), 31, 46, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), 49, 63, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableText.setText(ss);
        spannableText.setMovementMethod(LinkMovementMethod.getInstance());
        spannableText.setHighlightColor(Color.TRANSPARENT);
    }

//    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
//        if (event.action == MotionEvent.ACTION_DOWN) {
//            val v = currentFocus
//            if (v is EditText) {
//                val outRect = Rect()
//                v.getGlobalVisibleRect(outRect)
//                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                    Log.d("focus", "touchevent")
//                    v.clearFocus()
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(v.windowToken, 0)
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        if(ev.getAction() == MotionEvent.ACTION_DOWN){
//            View v = getCurrentFocus();
////            if (v.getClass().getName().toString().contains("EditText") ||
////                    v.getClass().getName().equalsIgnoreCase("EditText")) {
////                Log.e("touch", "Edittext");
////                v.requestFocus();
////            } else{
////                Log.e("touch", "not Edittext");
////                Utils.hideKeypad(CreateAccountActivity.this);
////            }
//
//            try {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
//                    Log.e("focus", "touchevent");
//                    v.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE) ;
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }else{
//                    Log.e("focus", "not touchevent");
//                    v.requestFocus();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
}
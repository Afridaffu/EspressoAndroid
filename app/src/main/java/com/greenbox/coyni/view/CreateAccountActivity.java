package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.greenbox.coyni.utils.Singleton;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxEditText;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.viewmodel.LoginViewModel;

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
    TextInputLayout firstNameTIL, emailTIL;
    public LinearLayout emailErrorLL, phoneErrorLL;
    TextView passwordInfoTV, privacyTV, tosTV;
    ImageView createAccountCloseIV;
    public boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false,
            isPassword = false, isConfirmPassword = false, isNextEnabled = false;
    public String passwordString = "";
    public MaterialCardView nextCV;
    Long mLastClickTime = 0L;

    private LinearLayout stregnthViewLL;
    private View stregnthOne, stregnthTwo, stregnthThree;
    private Pattern strong, medium;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_create_account);

            initFields();
            intiObserver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            createAccountActivity = this;

            errorState = new int[][]{new int[]{android.R.attr.state_enabled}};
            errorColor = new int[]{getResources().getColor(R.color.error_red)};
            errorColorState = new ColorStateList(errorState, errorColor);

            state = new int[][]{new int[]{android.R.attr.state_enabled}};
            color = new int[]{getResources().getColor(R.color.primary_green)};
            colorState = new ColorStateList(state, color);

            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            emailErrorLL = findViewById(R.id.emailErrorLL);
            phoneErrorLL = findViewById(R.id.phoneErrorLL);
            passwordInfoTV = findViewById(R.id.passwordInfoTV);
            privacyTV = findViewById(R.id.privacyTV);
            tosTV = findViewById(R.id.tosTV);
            nextCV = findViewById(R.id.nextCV);
            createAccountCloseIV = findViewById(R.id.createAccountCloseIV);

            firstNameET = findViewById(R.id.firstNameET);
            firstNameTIL = findViewById(R.id.firstNameTIL);
            emailTIL = findViewById(R.id.emailTIL);

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

            strong = Pattern.compile(STRONG_PATTERN);
            medium = Pattern.compile(MEDIUM_PATTERN);

            firstNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            lastNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            textWatchers();


            tosTV.setPaintFlags(tosTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            privacyTV.setPaintFlags(privacyTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tosTV.setOnClickListener(view -> {
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
            });

            privacyTV.setOnClickListener(view -> {
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
            });

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
                finish();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void intiObserver() {

        loginViewModel.getCustRegisResponseMutableLiveData().observe(this, new Observer<CustRegisterResponse>() {
            @Override
            public void onChanged(CustRegisterResponse custRegisterResponse) {
                dialog.dismiss();
                if (custRegisterResponse != null) {
                    try {
                        Intent i = new Intent(CreateAccountActivity.this, OTPValidation.class);

                        if (!custRegisterResponse.getData().isSms_verified() && !custRegisterResponse.getData().isEmail_verified()) {
                            i.putExtra("screen", "SignUp");
                            i.putExtra("OTP_TYPE", "MOBILE");
                            i.putExtra("MOBILE", phoneNumber);
                            i.putExtra("MASK_MOBILE", phoneNumberET.getText());
                            i.putExtra("EMAIL", emailET.getText().toString().trim());
                        } else if (custRegisterResponse.getData().isSms_verified() && !custRegisterResponse.getData().isEmail_verified()) {
                            i.putExtra("screen", "SignUp");
                            i.putExtra("OTP_TYPE", "EMAIL");
                            i.putExtra("MOBILE", phoneNumber);
                            i.putExtra("MASK_MOBILE", phoneNumberET.getText());
                            i.putExtra("EMAIL", emailET.getText().toString().trim());
                        } else if (custRegisterResponse.getData().isSms_verified() && custRegisterResponse.getData().isEmail_verified()) {
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
            }
        });

        loginViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
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
                    emailErrorLL.setVisibility(GONE);
                } else if (s.toString().toLowerCase().contains("entered phone number and email already exist in the system")) {
                    isPhoneError = true;
                    phoneNumberET.setErrorOutlineBox();
                    phoneErrorLL.setVisibility(VISIBLE);

                    isEmailError = true;
                    emailTIL.requestFocus();
                    emailErrorLL.setVisibility(VISIBLE);
                    emailTIL.setBoxStrokeColor(getResources().getColor(R.color.error_red));
                    emailTIL.setHintTextColor(errorColorState);

                } else {
                    isEmailError = false;
                    isPhoneError = false;
                    phoneErrorLL.setVisibility(GONE);
                    emailErrorLL.setVisibility(GONE);
                    Utils.displayAlert(s, CreateAccountActivity.this);
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
                    if (charSequence.toString().trim().length() > 0) {
                        isFirstName = true;
                    } else {
                        isFirstName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {

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
                    } else {
                        isLastName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            emailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 0) {
                        isEmailError = false;
                        emailErrorLL.setVisibility(GONE);
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        emailTIL.setHintTextColor(colorState);
                    }
                    if (isValidEmail(charSequence.toString().trim())) {
                        isEmail = true;
                    } else {
                        isEmail = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
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
                    enableOrDisableNext();


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            confirmPasswordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                        isConfirmPassword = true;
                    } else {
                        isConfirmPassword = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        stregnthViewLL.setVisibility(VISIBLE);
                    } else {
                        stregnthViewLL.setVisibility(GONE);
                    }
                }
            });

            emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (isEmailError) {
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.error_red));
                        emailTIL.setHintTextColor(errorColorState);
                    } else {
                        if (b) {
                            emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            emailTIL.setHintTextColor(colorState);
                        }
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

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
}
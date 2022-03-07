package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
import com.greenbox.coyni.model.register.EmailExistsResponse;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Singleton;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.view.business.CompanyInformationActivity;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    OutLineBoxPhoneNumberEditText phoneNumberET;
    TextInputEditText firstNameET, lastNameET, emailET, passwordET, confirmPasswordET;
    TextInputLayout firstNameTIL, lastNameTIL, emailTIL, passwordTIL, confPasswordTIL;
    public LinearLayout emailErrorLL, phoneErrorLL, firstNameErrorLL, lastNameErrorLL, passwordErrorLL, confPassErrorLL;
    public TextView emailErrorTV, phoneErrorTV, firstNameErrorTV, lastNameErrorTV, passwordErrorTV, confPassErrorTV;
    TextView passwordInfoTV, spannableText, privacyPolicyTV, tosTV;
    public boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false,
            isPassword = false, isConfirmPassword = false, isNextEnabled = false;
    public String passwordString = "";
    public CardView nextCV;
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

    boolean isEmailError = false, isPhoneError = false, isPwdEye = false, isCPwdEye = false, isAgreed = false;
    RelativeLayout mainRL;
    ScrollView mainSV;
    public static int focusedID = 0;
    CheckBox agreeCB;

    MyApplication objMyApplication;

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
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPwdEye = false;
        isCPwdEye = false;

        if (firstNameET.getId() == focusedID) {
            firstNameET.requestFocus();
        } else if (lastNameET.getId() == focusedID) {
            lastNameET.requestFocus();
        } else if (emailET.getId() == focusedID) {
            emailET.requestFocus();
        } else if (confirmPasswordET.getId() == focusedID) {
            confirmPasswordET.requestFocus();
        } else if (passwordET.getId() == focusedID) {
            passwordET.requestFocus();
        } else if (phoneNumberET.getETID() == focusedID) {
            phoneNumberET.requestETFocus();
        } else {
            firstNameET.requestFocus();
        }

        Log.e("ID", "" + focusedID);
    }

    public void initFields() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            setKeyboardVisibilityListener(this);
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
            privacyPolicyTV = findViewById(R.id.privacyTV);
            tosTV = findViewById(R.id.tosTV);
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
            agreeCB = findViewById(R.id.agreeCB);

            strong = Pattern.compile(STRONG_PATTERN);
            medium = Pattern.compile(MEDIUM_PATTERN);
            firstNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            firstNameET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
//            firstNameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


            lastNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            lastNameET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

            emailET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});

            passwordET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            confirmPasswordET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});


            lastNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            firstNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));


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

            createAccountCloseIV.setOnClickListener(view -> onBackPressed());

            passwordTIL.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isPwdEye) {
                            isPwdEye = true;
                            passwordTIL.setEndIconDrawable(R.drawable.ic_eyeopen);
                            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isPwdEye = false;
                            passwordTIL.setEndIconDrawable(R.drawable.ic_eyeclose);
                            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        if (passwordET.getText().length() > 0) {
                            passwordET.setSelection(passwordET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confPasswordTIL.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isCPwdEye) {
                            isCPwdEye = true;
                            confPasswordTIL.setEndIconDrawable(R.drawable.ic_eyeopen);
                            confirmPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isCPwdEye = false;
                            confPasswordTIL.setEndIconDrawable(R.drawable.ic_eyeclose);
                            confirmPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        if (confirmPasswordET.getText().length() > 0) {
                            confirmPasswordET.setSelection(confirmPasswordET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            agreeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isAgreed = b;
                    enableOrDisableNext();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initObservers() {

        loginViewModel.getCustRegisResponseMutableLiveData().observe(this, new Observer<CustRegisterResponse>() {
            @Override
            public void onChanged(CustRegisterResponse custRegisterResponse) {
                try {
                    dialog.dismiss();
                    if (custRegisterResponse != null) {
                        if (custRegisterResponse.getStatus().toLowerCase().equals("success")) {
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
                        } else {
                            Utils.displayAlert(custRegisterResponse.getError().getErrorDescription(), CreateAccountActivity.this, "", custRegisterResponse.getError().getFieldErrors().get(0));
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
                        Utils.displayAlert(s, CreateAccountActivity.this, "", "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getEmailExistsResponseMutableLiveData().observe(this, new Observer<EmailExistsResponse>() {
            @Override
            public void onChanged(EmailExistsResponse emailExistsResponse) {
                if (emailExistsResponse != null) {
                    if (!emailExistsResponse.getStatus().toLowerCase().equals("error")) {
                        emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_black));
                        emailErrorLL.setVisibility(GONE);
                        isEmail = true;
                        enableOrDisableNext();
                    } else {
                        emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                        emailErrorLL.setVisibility(VISIBLE);
                        emailErrorTV.setText(emailExistsResponse.getError().getErrorDescription());
                        isEmail = false;
                        enableOrDisableNext();
                    }
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
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isFirstName = true;
                        firstNameErrorLL.setVisibility(GONE);
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstNameTIL, getResources().getColor(R.color.primary_green));
//                        String str = firstNameET.getText().toString();
//                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
//                            firstNameET.setText(firstNameET.getText().toString().replaceAll(" ", ""));
//                            firstNameET.setSelection(firstNameET.getText().length());
//                        }
                    } else {
                        isFirstName = false;
                    }

//                    else if (firstNameET.getText().toString().trim().length() == 0) {
//                        firstNameErrorLL.setVisibility(VISIBLE);
//                        firstNameErrorTV.setText("Field Required");
//                    } else {
//                        isFirstName = false;
//                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String str = firstNameET.getText().toString();
                        if (str.length() > 0 && str.toString().trim().length() == 0) {
                            firstNameET.setText("");
                            firstNameET.setSelection(firstNameET.getText().length());
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            firstNameET.setText(str.trim());
                        } else if (str.length() > 0 && str.contains(".")) {
                            firstNameET.setText(firstNameET.getText().toString().replaceAll("\\.", ""));
                            firstNameET.setSelection(firstNameET.getText().length());
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            firstNameET.setText("");
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

                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isLastName = true;
                        lastNameErrorLL.setVisibility(GONE);
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastNameTIL, getResources().getColor(R.color.primary_green));
//                        String str = lastNameET.getText().toString();
//                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
//                            lastNameET.setText(lastNameET.getText().toString().replaceAll(" ", ""));
//                            lastNameET.setSelection(lastNameET.getText().length());
//                        }
                    } else {
                        isLastName = false;
                    }

//                    else if (lastNameET.getText().toString().trim().length() == 0) {
//                        lastNameErrorLL.setVisibility(VISIBLE);
//                        lastNameErrorTV.setText("Field Required");
//                    } else {
//                        isLastName = false;
//                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = lastNameET.getText().toString();
                        if (str.length() > 0 && str.toString().trim().length() == 0) {
                            lastNameET.setText(lastNameET.getText().toString().replaceAll(" ", ""));
                            lastNameET.setSelection(lastNameET.getText().length());
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            lastNameET.setText(str.trim());
                        } else if (str.length() > 0 && str.contains(".")) {
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

                    if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                        isEmailError = false;
                        emailErrorLL.setVisibility(GONE);
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(emailTIL, getResources().getColor(R.color.primary_green));

                    }
//                    else if (emailET.getText().toString().trim().length() == 0) {
//                        emailErrorLL.setVisibility(VISIBLE);
//                        emailErrorTV.setText("Field Required");
//                    }
                    if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
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
                        if ((str.length() > 0 && str.toString().trim().length() == 0) || (str.length() > 0 && str.contains(" "))) {
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

                    if (getCurrentFocus().getId() == passwordET.getId()) {

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
                            stregnthOne.setBackgroundColor(getResources().getColor(R.color.orange));
                            stregnthTwo.setBackgroundColor(getResources().getColor(R.color.orange));
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
                        if (passwordET.getText().toString().length() == 0) {
                            passwordTIL.setHint("Password");
                            confPasswordTIL.setHint("Confirm Password");

                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            if (confirmPasswordET.getText().toString().trim().length() == 0)
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));
                            else
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                        } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            isConfirmPassword = true;

                            passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            passwordTIL.setHint("Password");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            confPasswordTIL.setHint("Confirm Password");
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));

                        } else {
                            isConfirmPassword = false;

                            if (confirmPasswordET.getText().toString().trim().length() > 0) {
                                passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));

                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                confPasswordTIL.setHint("Password doesn’t match");
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                            } else if (confirmPasswordET.getText().toString().trim().length() == 0) {
                                confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                confPasswordTIL.setHint("Confirm Password");
                                Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));

                                passwordTIL.setHint("Password");

                                if (confPassErrorLL.getVisibility() == VISIBLE) {
                                    confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                }
                            }
                        }
                        enableOrDisableNext();
                    }
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

                    if (getCurrentFocus().getId() == confirmPasswordET.getId()) {

                        if (confirmPasswordET.getText().toString().length() == 0) {
                            passwordTIL.setHint("Password");
                            confPasswordTIL.setHint("Confirm Password");
                            if (passwordET.getText().toString().trim().length() > 0) {
                                if (strong.matcher(passwordET.getText().toString().trim()).matches()) {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                    Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                                } else {
                                    passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                }
                            }
                        } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim()) &&
                                strong.matcher(passwordET.getText().toString().trim()).matches()) {
                            isConfirmPassword = true;

                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            passwordTIL.setHint("Password");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));

                            confPasswordTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            confPasswordTIL.setHint("Confirm Password");
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));

                        } else {
                            isConfirmPassword = false;
                            confPasswordTIL.setHint("Password doesn’t match");
                        }
                        enableOrDisableNext();
                    }

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

            firstNameET.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
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
                        firstNameET.setHint("");
                        if (firstNameET.getText().toString().trim().length() > 1) {
                            firstNameErrorLL.setVisibility(GONE);
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_black));

                        } else if (firstNameET.getText().toString().trim().length() == 1) {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.error_red));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.light_gray));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CreateAccountActivity.this);
                        firstNameErrorLL.setVisibility(GONE);
                        focusedID = firstNameET.getId();
                        firstNameET.setHint("First Name");
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_green));
//                        firstNameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                    }
                }
            });

            lastNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        lastNameET.setHint("");
                        if (lastNameET.getText().toString().trim().length() > 1) {
                            lastNameErrorLL.setVisibility(GONE);
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_black));

                        } else if (lastNameET.getText().toString().trim().length() == 1) {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.error_red));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.light_gray));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CreateAccountActivity.this);
                        lastNameErrorLL.setVisibility(GONE);
                        focusedID = lastNameET.getId();
                        lastNameET.setHint("Last Name");
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_green));
//                        lastNameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                    }
                }
            });

            passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CreateAccountActivity.this);
                        if (passwordET.getText().toString().trim().length() == 0 || !strong.matcher(passwordET.getText().toString().trim()).matches()) {
                            passwordET.setHint("Password");
//                            stregnthViewLL.setVisibility(VISIBLE);
//                            passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
//                            passwordTIL.setHint("Password");
//                            focusedID = passwordET.getId();
                            passwordInfoTV.setVisibility(VISIBLE);
                            passwordInfoTV.setTextColor(getResources().getColor(R.color.error_red));
                            passwordErrorLL.setVisibility(GONE);
                        }
                        stregnthViewLL.setVisibility(VISIBLE);
                        passwordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_green));
                        passwordTIL.setHint("Password");
                        focusedID = passwordET.getId();
                    } else {
                        stregnthViewLL.setVisibility(GONE);
                        if (passwordET.getText().toString().trim().length() == 0) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            passwordTIL.setHint("Password");
                            passwordET.setHint("");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.light_gray));
//                            passwordInfoTV.setVisibility(VISIBLE);
//                            passwordInfoTV.setTextColor(getResources().getColor(R.color.error_red));
                            passwordInfoTV.setVisibility(GONE);
                            passwordErrorLL.setVisibility(VISIBLE);
                            passwordErrorTV.setText("Field Required");
                        } else if (!strong.matcher(passwordET.getText().toString().trim()).matches()) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            passwordTIL.setHint("Password");
                            passwordET.setHint("");
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.error_red));
                            passwordInfoTV.setVisibility(VISIBLE);
                            passwordInfoTV.setTextColor(getResources().getColor(R.color.error_red));
                        } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                        } else if (passwordET.getText().toString().length() > 0 && confirmPasswordET.getText().toString().length() > 0 && !passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            //passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                            confPasswordTIL.setHint("Password doesn’t match");
                        } else {
                            passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(passwordTIL, getColor(R.color.primary_black));
                        }
                    }
                }
            });

            confirmPasswordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CreateAccountActivity.this);
                        confirmPasswordET.setHint("Confirm Password");
                        confPasswordTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_green));
                        confPasswordTIL.setHint("Confirm Password");
                        focusedID = confirmPasswordET.getId();
                        confPassErrorLL.setVisibility(GONE);
                    } else {
                        confirmPasswordET.setHint("");
                        if (confirmPasswordET.getText().toString().trim().length() == 0) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.light_gray));
                            confPassErrorLL.setVisibility(VISIBLE);
                            confPassErrorTV.setText("Field Required");
                        } else if (passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                        } else if (passwordET.getText().toString().length() > 0 && confirmPasswordET.getText().toString().length() > 0 && !passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim())) {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.error_red));
                            confPasswordTIL.setHint("Password doesn’t match");
                        } else {
                            confPasswordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confPasswordTIL, getColor(R.color.primary_black));
                        }
                    }

                }
            });

            emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        emailET.setHint("");
                        if (emailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Please Enter a valid Email");
                        } else if (emailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_black));
                            emailErrorLL.setVisibility(GONE);
                            loginViewModel.validateEmail(emailET.getText().toString().trim());
                        } else if (emailET.getText().toString().trim().length() > 0 && emailET.getText().toString().trim().length() <= 5) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Please Enter a valid Email");
                        } else {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.light_gray));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CreateAccountActivity.this);
                        emailErrorLL.setVisibility(GONE);
                        focusedID = emailET.getId();
                        emailET.setHint("Email");
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
            if (isFirstName && isLastName && isEmail && isPhoneNumber && isPassword && isConfirmPassword && isAgreed) {
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
            regisRequest.setAccountType(objMyApplication.getAccountType());
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
        focusedID = 0;
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

        SpannableString ss = new SpannableString("By clicking next, you agree to Terms of Service & ");
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

        privacyPolicyTV.setText("Privacy Policy");
        privacyPolicyTV.setPaintFlags(privacyPolicyTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tosTV.setPaintFlags(tosTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        privacyPolicyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(CreateAccountActivity.this);

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
        });

        tosTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(CreateAccountActivity.this);

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
        });

        ss.setSpan(clickableSpan, 31, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(clickableSpan2, 49, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), 31, 47, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), 49, 63, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


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

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Utils.isKeyboardVisible = visible;
    }

}
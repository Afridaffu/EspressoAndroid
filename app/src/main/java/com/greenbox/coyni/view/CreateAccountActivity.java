package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.outline_et.OutLineBoxEditText;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity  {

    OutLineBoxPhoneNumberEditText phoneNumberET;
    TextInputEditText firstNameET, lastNameET, emailET, passwordET, confirmPasswordET;
    TextInputLayout firstNameTIL;
    LinearLayout emailErrorLL;
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

    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})";

    private static final String MEDIUM_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,})";

    public  static CreateAccountActivity createAccountActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_create_account);
            createAccountActivity = this;
            initFields();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void initFields() {
        try{

            emailErrorLL = findViewById(R.id.emailErrorLL);
            passwordInfoTV = findViewById(R.id.passwordInfoTV);
            privacyTV = findViewById(R.id.privacyTV);
            tosTV = findViewById(R.id.tosTV);
            nextCV = findViewById(R.id.nextCV);
            createAccountCloseIV = findViewById(R.id.createAccountCloseIV);

            firstNameET = findViewById(R.id.firstNameET);
            firstNameTIL = findViewById(R.id.firstNameTIL);

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

            });

            privacyTV.setOnClickListener(view -> {

            });

            nextCV.setOnClickListener(view -> {
                if (isNextEnabled) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(CreateAccountActivity.this, OTPValidation.class)
                            .putExtra("OTP_TYPE", "MOBILE")
                            .putExtra("MOBILE", phoneNumberET.getText())
                            .putExtra("EMAIL", emailET.getText().toString().trim()));

                } else {
                    startActivity(new Intent(CreateAccountActivity.this, OTPValidation.class)
                            .putExtra("OTP_TYPE", "EMAIL")
                            .putExtra("MOBILE", phoneNumberET.getText())
                            .putExtra("EMAIL", emailET.getText().toString().trim()));
                }

//            Log.e("Field1", firstNameET.getText());
//            Log.e("Field2", lastNameET.getText());
//            Log.e("Field3", emailET.getText());
//            Log.e("Field4", phoneNumberET.getText());
//            Log.e("Field5", passwordET.getText());
//            Log.e("Field6", confirmPasswordET.getText());
            });

            createAccountCloseIV.setOnClickListener(view -> {
                finish();
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void textWatchers(){

        try {
            firstNameET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().trim().length()>0){
                        isFirstName = true;
                    }else{
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
                    if(charSequence.toString().trim().length()>0){
                        isLastName = true;
                    }else{
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(isValidEmail(charSequence.toString().trim())){
                        isEmail = true;
                    }else{
                        isEmail = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) { }
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

                    if(charSequence.toString().trim().length()>7 && strong.matcher(charSequence.toString().trim()).matches()){
                        isPassword = true;
                    }else{
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

                    if(passwordET.getText().toString().trim().equals(confirmPasswordET.getText().toString().trim()) ){
                        isConfirmPassword = true;
                    }else{
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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void enableOrDisableNext(){

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
        }catch (Exception e){
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

}
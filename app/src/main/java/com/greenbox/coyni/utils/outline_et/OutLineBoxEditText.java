package com.greenbox.coyni.utils.outline_et;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.view.CreateAccountActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutLineBoxEditText extends ConstraintLayout {
    private TextView hintName, passwordInfoTV;
    private LinearLayout hintHolder, stregnthViewLL;
    private EditText outLineEditText;
    private TextInputLayout outLineTIL;
    private boolean showPassowrd = false;
    private View stregnthOne, stregnthTwo, stregnthThree;
    private Pattern strong, medium;
    private Matcher matcher;
    private String fieldType = "";
    private Activity activity;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,12})";

    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})";

    private static final String MEDIUM_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,})";

    public OutLineBoxEditText(Context context) {
        this(context, null, 0);
    }

    public OutLineBoxEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutLineBoxEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.outlinebox_layout, this, true);
        hintName = findViewById(R.id.hintTV);
        hintHolder = findViewById(R.id.hintdHolderLL);
        outLineEditText = findViewById(R.id.outLineEditText);
        outLineTIL = findViewById(R.id.outLineTIL);
        stregnthViewLL = findViewById(R.id.stregnthViewLL);
        stregnthOne = findViewById(R.id.stregnthOne);
        stregnthTwo = findViewById(R.id.stregnthTwo);
        stregnthThree = findViewById(R.id.stregnthThree);

        strong = Pattern.compile(STRONG_PATTERN);
        medium = Pattern.compile(MEDIUM_PATTERN);

        outLineEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (showPassowrd) {
                    stregnthViewLL.setVisibility(VISIBLE);
                } else {
                    stregnthViewLL.setVisibility(GONE);
                }

                if (b) {
                    hintName.setTextColor(getResources().getColor(R.color.primary_color));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                } else {
                    hintName.setTextColor(getResources().getColor(R.color.primary_black));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                    stregnthViewLL.setVisibility(GONE);
                }
            }
        });

        outLineEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (showPassowrd) {
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
                }

                if (fieldType.equalsIgnoreCase("First Name")) {
                    ((CreateAccountActivity)context).isFirstName = outLineEditText.getText().toString().trim().length() > 2;
                } else if (fieldType.equalsIgnoreCase("Last Name")) {
                    ((CreateAccountActivity)context).isLastName = outLineEditText.getText().toString().trim().length() > 0;
                } else if (fieldType.equalsIgnoreCase("Email")) {
                    ((CreateAccountActivity)context).isEmail = isValidEmail(outLineEditText.getText().toString().trim());
                } else if (fieldType.equalsIgnoreCase("Phone Number")) {
                    Log.e("Phone Number", outLineEditText.getText().toString().trim().length()+"");
                    ((CreateAccountActivity)context).isPhoneNumber = outLineEditText.getText().toString().trim().length() > 9;
                } else if (fieldType.equalsIgnoreCase("Password")) {
                    if (outLineEditText.getText().toString().trim().length() > 7 && strong.matcher(charSequence).matches()) {
                        ((CreateAccountActivity)context).isPassword = true;
                        ((CreateAccountActivity)context).passwordString = outLineEditText.getText().toString().trim();
                    } else {
                        ((CreateAccountActivity)context).isPassword = false;
                    }
                } else if (fieldType.equalsIgnoreCase("Confirm Password")) {
                    if(!((CreateAccountActivity)context).passwordString.equals(""))
                        ((CreateAccountActivity)context).isConfirmPassword = outLineEditText.getText().toString().equals(((CreateAccountActivity)context).passwordString);
                }

                if (((CreateAccountActivity)context).isFirstName && ((CreateAccountActivity)context).isLastName &&
                        ((CreateAccountActivity)context).isEmail && ((CreateAccountActivity)context).isPhoneNumber &&
                        ((CreateAccountActivity)context).isPassword && ((CreateAccountActivity)context).isConfirmPassword) {
                    ((CreateAccountActivity)context).isNextEnabled = true;
                    ((CreateAccountActivity)context).nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    Log.e("All boolean", ((CreateAccountActivity)context).isFirstName + " " + ((CreateAccountActivity)context).isLastName + " " +
                            ((CreateAccountActivity)context).isEmail + " " + ((CreateAccountActivity)context).isPhoneNumber + " " +
                            ((CreateAccountActivity)context).isPassword + " " + ((CreateAccountActivity)context).isConfirmPassword);
                } else {

                    Log.e("All boolean", ((CreateAccountActivity)context).isFirstName + " " + ((CreateAccountActivity)context).isLastName + " " +
                            ((CreateAccountActivity)context).isEmail + " " + ((CreateAccountActivity)context).isPhoneNumber + " " +
                            ((CreateAccountActivity)context).isPassword + " " + ((CreateAccountActivity)context).isConfirmPassword);

                    ((CreateAccountActivity)context).isNextEnabled = false;
                    ((CreateAccountActivity)context).nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void setErrorOutlineBox() {
        hintName.setTextColor(getResources().getColor(R.color.error_red));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
    }

    public void setField(String hintOutlineText, String hintText, String type, boolean showPassowrdStrength, Activity activity1) {
        activity = activity1;
        hintName.setText(hintOutlineText);
        outLineEditText.setHint(hintText);

        fieldType = hintOutlineText;

        if (type.equals("text")) {
            outLineEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (type.equals("textEmailAddress")) {
            outLineEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        showPassowrd = showPassowrdStrength;

    }

    public void setDigits() {
        outLineEditText.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
    }

    public void setPasswordType(boolean isPasswordToggleEnabled, TextView passwordInfoText) {
        passwordInfoTV = passwordInfoText;
        outLineEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        if (isPasswordToggleEnabled) {
            outLineTIL.setPasswordVisibilityToggleEnabled(true);
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

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void setText(String text) {
        outLineEditText.setText(text);
    }

    public String getText() {
        return outLineEditText.getText().toString().trim();
    }
}
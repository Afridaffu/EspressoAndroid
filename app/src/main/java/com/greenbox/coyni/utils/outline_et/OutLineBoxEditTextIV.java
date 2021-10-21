package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutLineBoxEditTextIV extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private EditText outLineEditText;
    private TextInputLayout outLineTIL;
    private boolean showPassowrd = false;
    public OutLineBoxEditTextIV(Context context){
        this(context,null,0);
    }

    public OutLineBoxEditTextIV(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public OutLineBoxEditTextIV(Context context, AttributeSet attrs, int defStyleattr){
        super(context,attrs,defStyleattr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attributeSet){
        LayoutInflater.from(context).inflate(R.layout.outlineboxiv_layout,this,true);
        hintName=findViewById(R.id.hintTV);
        hintHolder=findViewById(R.id.hintdHolderLL);
        outLineEditText=findViewById(R.id.outLineEditText);
        outLineTIL=findViewById(R.id.outLineTIL);
        outLineEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    outLineEditText.setTextColor(getResources().getColor(R.color.primary_black));
                    hintName.setTextColor(getResources().getColor(R.color.primary_black));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));

                }else{
                    hintName.setTextColor(getResources().getColor(R.color.primary_black));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));

                }
            }
        });
    }

    public void setField(String hintOutlineText, String hintText, String type, boolean showPassowrdStrength){
        hintName.setText(hintOutlineText);
        outLineEditText.setHint(hintText);
        if(type.equals("text")){
            outLineEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        }else if(type.equals("textEmailAddress")){
            outLineEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        else if(type.equals("number")){
            outLineEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else if(type.equals("phone")){
            outLineEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        else if(type.equals("none"))
        {
            outLineEditText.setInputType(InputType.TYPE_NULL);
        }

        showPassowrd = showPassowrdStrength;

    }
    public void backgroundColor(){
    outLineEditText.setEnabled(false);

     }

    public void setDigits(){
        outLineEditText.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
    }


    public void setInputLength(){
        int maxLenght=4;
        outLineEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLenght)});
    }
    public void setInputLengthPwd(){
        int maxLenght=12;
        outLineEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLenght)});
    }
    public void setPasswordType(boolean isPasswordToggleEnabled){
        outLineEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        if(isPasswordToggleEnabled)
            outLineTIL.setPasswordVisibilityToggleEnabled(true);

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
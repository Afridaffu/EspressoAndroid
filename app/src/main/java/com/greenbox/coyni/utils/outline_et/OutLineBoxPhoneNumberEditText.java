package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.CreateAccountActivity;
import com.santalu.maskara.widget.MaskEditText;

public class OutLineBoxPhoneNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText pnET;
    private CreateAccountActivity createAccountAct;

    public OutLineBoxPhoneNumberEditText(Context context){
        this(context,null,0);

    }

    public OutLineBoxPhoneNumberEditText(Context context, AttributeSet attrs){
        this(context,attrs,0);

    }

    public OutLineBoxPhoneNumberEditText(Context context, AttributeSet attrs, int defStyleattr){
        super(context,attrs,defStyleattr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attributeSet){
        LayoutInflater.from(context).inflate(R.layout.outlinebox_phone_number_layout,this,true);
        hintName=findViewById(R.id.hintTV);
        hintHolder=findViewById(R.id.hintdHolderLL);
        pnET=findViewById(R.id.pnET);
        pnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    hintName.setTextColor(getResources().getColor(R.color.primary_color));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                }else{
                    hintName.setTextColor(getResources().getColor(R.color.primary_black));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                }
            }
        });


        pnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("Phone Number", pnET.getText().toString().trim().length()+"");
                createAccountAct.isPhoneNumber = pnET.getText().toString().trim().length() > 13;

                if (createAccountAct.isFirstName && createAccountAct.isLastName &&
                        createAccountAct.isEmail && createAccountAct.isPhoneNumber &&
                        createAccountAct.isPassword && createAccountAct.isConfirmPassword) {
                    createAccountAct.isNextEnabled = true;
                    createAccountAct.nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    Log.e("All boolean", createAccountAct.isFirstName + " " + createAccountAct.isLastName + " " +
                            createAccountAct.isEmail + " " + createAccountAct.isPhoneNumber + " " +
                            createAccountAct.isPassword + " " + createAccountAct.isConfirmPassword);
                } else {

                    Log.e("All boolean", createAccountAct.isFirstName + " " + createAccountAct.isLastName + " " +
                            createAccountAct.isEmail + " " + createAccountAct.isPhoneNumber + " " +
                            createAccountAct.isPassword + " " + createAccountAct.isConfirmPassword);

                    createAccountAct.isNextEnabled = false;
                    createAccountAct.nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
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

    public void setField(String hintOutlineText, String hintText, String type, CreateAccountActivity createAccountActivity){
        createAccountAct = createAccountActivity;
        hintName.setText(hintOutlineText);
        pnET.setHint(hintText);
    }

    public void setText(String text) {
        pnET.setText(text);
    }

    public String getText() {
        return pnET.getText().toString().trim();
    }


}
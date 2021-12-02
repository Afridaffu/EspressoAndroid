package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.CreateAccountActivity;
import com.greenbox.coyni.view.RetrieveEmailActivity;

public class CardNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private EditText cnET;
    boolean isPhoneError = false;

    public String FROM = "";

    public CardNumberEditText(Context context) {
        this(context, null, 0);
    }

    public CardNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardNumberEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.card_number_layout, this, true);
        hintName = findViewById(R.id.hintTV);
        hintHolder = findViewById(R.id.hintdHolderLL);
        cnET = findViewById(R.id.pnET);
        cnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {

                    if (isPhoneError) {
                        if (b) {
                            CreateAccountActivity.focusedID = cnET.getId();
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    } else {
                        if (b) {
                            CreateAccountActivity.focusedID = cnET.getId();
                            hintName.setTextColor(getResources().getColor(R.color.primary_color));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }

                    }
                    if (FROM.equals("Retrieve") && !b) {
                        if ((cnET.getText().length() > 0 && cnET.getText().length() < 14)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            RetrieveEmailActivity rea = RetrieveEmailActivity.retrieveEmailActivity;
                            rea.phoneErrorLL.setVisibility(VISIBLE);
                            rea.phoneErrorTV.setText("Invalid Phone Number");
                        } else if ((cnET.getText().length() == 0)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            RetrieveEmailActivity rea = RetrieveEmailActivity.retrieveEmailActivity;
                            rea.phoneErrorLL.setVisibility(VISIBLE);
                            rea.phoneErrorTV.setText("Field Required");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    }else if (FROM.equals("CREATE_ACCOUNT") && !b) {
                        if ((cnET.getText().length() > 0 && cnET.getText().length() < 14)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            CreateAccountActivity caa = CreateAccountActivity.createAccountActivity;
                            caa.phoneErrorLL.setVisibility(VISIBLE);
                            caa.phoneErrorTV.setText("Invalid Phone Number");
                        } else if ((cnET.getText().length() == 0)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            CreateAccountActivity caa = CreateAccountActivity.createAccountActivity;
                            caa.phoneErrorLL.setVisibility(VISIBLE);
                            caa.phoneErrorTV.setText("Field Required");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (FROM.equals("CREATE_ACCOUNT")) {
                    CreateAccountActivity createAccountAct = CreateAccountActivity.createAccountActivity;
                    if (charSequence.length() == 14) {
                        isPhoneError = false;
//                        hintName.setTextColor(getResources().getColor(R.color.primary_green));
//                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                        createAccountAct.phoneErrorLL.setVisibility(GONE);
                    }

                    createAccountAct.isPhoneNumber = cnET.getText().toString().trim().length() > 13;

                    if (createAccountAct.isFirstName && createAccountAct.isLastName &&
                            createAccountAct.isEmail && createAccountAct.isPhoneNumber &&
                            createAccountAct.isPassword && createAccountAct.isConfirmPassword) {
                        createAccountAct.isNextEnabled = true;
                        createAccountAct.nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    } else {
                        createAccountAct.isNextEnabled = false;
                        createAccountAct.nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                    if ((cnET.getText().length() == 0)) {
                        CreateAccountActivity caa = CreateAccountActivity.createAccountActivity;
                        caa.phoneErrorLL.setVisibility(VISIBLE);
                        caa.phoneErrorTV.setText("Field Required");
                    }
                }else if (FROM.equals("Retrieve")) {
                    RetrieveEmailActivity rea = RetrieveEmailActivity.retrieveEmailActivity;
                    if (charSequence.length() == 14) {
                        isPhoneError = false;
                        rea.phoneErrorLL.setVisibility(GONE);
                    }

                    if ((cnET.getText().length() == 0)) {
                        rea.phoneErrorLL.setVisibility(VISIBLE);
                        rea.phoneErrorTV.setText("Field Required");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (FROM.equals("Retrieve")) {
                        RetrieveEmailActivity retrieveEmailActivity = RetrieveEmailActivity.retrieveEmailActivity;
                        retrieveEmailActivity.enableButton();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void setErrorOutlineBox() {
        isPhoneError = true;
        hintName.setTextColor(getResources().getColor(R.color.error_red));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
    }


    public void setFrom(String fromm) {
        FROM = fromm;
    }


    public void setText(String text) {
        cnET.setText(text);
    }

    public String getText() {
        return cnET.getText().toString().trim();
    }

    public int getETID() {
        return cnET.getId();
    }

    public void requestETFocus(){
        cnET.requestFocus();
    }
}
package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MaskEditText.widget.MaskEditText;
import com.greenbox.coyni.view.CreateAccountActivity;
import com.greenbox.coyni.view.RetrieveEmailActivity;

public class

OutLineBoxPhoneNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText pnET;
    boolean isPhoneError = false;

    public String FROM = "";

    public OutLineBoxPhoneNumberEditText(Context context) {
        this(context, null, 0);
    }

    public OutLineBoxPhoneNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutLineBoxPhoneNumberEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.outlinebox_phone_number_layout, this, true);
        hintName = findViewById(R.id.hintTV);
        hintHolder = findViewById(R.id.hintdHolderLL);
        pnET = findViewById(R.id.pnET);
        pnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (isPhoneError) {
                        if (b) {
                            hintName.setVisibility(VISIBLE);
                            CreateAccountActivity.focusedID = pnET.getId();
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                        } else {
                            if (pnET.getText().toString().length() > 0)
                                hintName.setVisibility(VISIBLE);
                            else
                                hintName.setVisibility(GONE);
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    } else {
                        if (b) {
                            hintName.setVisibility(VISIBLE);
                            CreateAccountActivity.focusedID = pnET.getId();
                            hintName.setTextColor(getResources().getColor(R.color.primary_color));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                        } else {
                            if (pnET.getText().toString().length() > 0)
                                hintName.setVisibility(VISIBLE);
                            else
                                hintName.setVisibility(GONE);
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }

                    }

                    if (b) {
                        if (FROM.equals("Retrieve")) {
                            RetrieveEmailActivity rea = RetrieveEmailActivity.retrieveEmailActivity;
                            rea.phoneErrorLL.setVisibility(GONE);
                        } else if (FROM.equals("CREATE_ACCOUNT")) {
                            CreateAccountActivity caa = CreateAccountActivity.createAccountActivity;
                            caa.phoneErrorLL.setVisibility(GONE);
                        }
                    }
//                    if (FROM.equals("Retrieve") && !b) {
                    if (FROM.equals("Retrieve")) {
                        RetrieveEmailActivity rea = RetrieveEmailActivity.retrieveEmailActivity;
                        if (!b) {
                            if ((pnET.getText().length() > 0 && pnET.getText().length() < 14)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                rea.phoneErrorLL.setVisibility(VISIBLE);
//                                rea.phoneErrorTV.setText("Invalid Phone Number");
                                rea.phoneErrorTV.setText("Please enter a valid Phone Number");
                            } else if ((pnET.getText().length() == 0)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                rea.phoneErrorLL.setVisibility(VISIBLE);
                                rea.phoneErrorTV.setText("Field Required");
                            } else {
                                hintName.setTextColor(getResources().getColor(R.color.primary_black));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            }
                        } else {
                            rea.phoneErrorLL.setVisibility(GONE);
                        }
//                    } else if (FROM.equals("CREATE_ACCOUNT") && !b) {
                    } else if (FROM.equals("CREATE_ACCOUNT")) {
                        CreateAccountActivity caa = CreateAccountActivity.createAccountActivity;
                        if (!b) {
                            if ((pnET.getText().length() > 0 && pnET.getText().length() < 14)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                caa.phoneErrorLL.setVisibility(VISIBLE);
//                                caa.phoneErrorTV.setText("Invalid Phone Number");
                                caa.phoneErrorTV.setText("Please enter a valid Phone Number");
                            } else if ((pnET.getText().length() == 0)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                caa.phoneErrorLL.setVisibility(VISIBLE);
                                caa.phoneErrorTV.setText("Field Required");
                            } else {
                                hintName.setTextColor(getResources().getColor(R.color.primary_black));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            }
                        } else {
                            caa.phoneErrorLL.setVisibility(GONE);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        pnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (FROM.equals("CREATE_ACCOUNT")) {
                    CreateAccountActivity createAccountAct = CreateAccountActivity.createAccountActivity;
                    if (charSequence.length() == 14) {
                        isPhoneError = false;
                        createAccountAct.phoneErrorLL.setVisibility(GONE);
                    }

                    createAccountAct.isPhoneNumber = pnET.getText().toString().trim().length() > 13;

                    if (createAccountAct.isFirstName && createAccountAct.isLastName &&
                            createAccountAct.isEmail && createAccountAct.isPhoneNumber &&
                            createAccountAct.isPassword && createAccountAct.isConfirmPassword) {
                        createAccountAct.isNextEnabled = true;
                        createAccountAct.nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    } else {
                        createAccountAct.isNextEnabled = false;
                        createAccountAct.nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

//                    if ((pnET.getText().length() == 0)) {
//                        CreateAccountActivity caa = CreateAccountActivity.createAccountActivity;
//                        caa.phoneErrorLL.setVisibility(VISIBLE);
//                        caa.phoneErrorTV.setText("Field Required");
//                    }
                } else if (FROM.equals("Retrieve")) {
                    RetrieveEmailActivity rea = RetrieveEmailActivity.retrieveEmailActivity;
                    if (charSequence.length() == 14) {
                        isPhoneError = false;
                        rea.phoneErrorLL.setVisibility(GONE);
                    }

//                    if ((pnET.getText().length() == 0)) {
//                        rea.phoneErrorLL.setVisibility(VISIBLE);
//                        rea.phoneErrorTV.setText("Field Required");
//                    }
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
        pnET.setText(text);
    }

    public String getText() {
        return pnET.getText().toString().trim();
    }
    public String getUnmaskedText() {
        return pnET.getUnMasked();
    }

    public int getETID() {
        return pnET.getId();
    }

    public void requestETFocus() {
        pnET.requestFocus();
    }

}
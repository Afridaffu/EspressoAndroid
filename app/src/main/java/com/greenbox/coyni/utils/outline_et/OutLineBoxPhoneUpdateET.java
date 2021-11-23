package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.CreateAccountActivity;
import com.greenbox.coyni.view.EditPhoneActivity;
import com.greenbox.coyni.view.RetrieveEmailActivity;
import com.santalu.maskara.widget.MaskEditText;

public class OutLineBoxPhoneUpdateET extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText pnET;
    boolean isPhoneError = false;

    public String FROM = "";

    public OutLineBoxPhoneUpdateET(Context context) {
        this(context, null, 0);
    }

    public OutLineBoxPhoneUpdateET(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutLineBoxPhoneUpdateET(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.outlinebox_phone_number_update_layout, this, true);
        hintName = findViewById(R.id.hintTV);
        hintHolder = findViewById(R.id.hintdHolderLL);
        pnET = findViewById(R.id.pnET);
        pnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (isPhoneError) {
                        if (b) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    } else {
                        if (b) {
                            hintName.setTextColor(getResources().getColor(R.color.primary_color));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }

                    }
                    if (!b) {
                        EditPhoneActivity epa = EditPhoneActivity.editPhoneActivity;
                        if (hintName.getText().toString().contains("Current")) {
                            if ((pnET.getText().length() > 0 && pnET.getText().length() < 14)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                epa.currentPhoneErrorLL.setVisibility(VISIBLE);
                                epa.currentPhoneErrorTV.setText("Invalid Phone Number");
                            } else if ((pnET.getText().length() == 0)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                epa.currentPhoneErrorLL.setVisibility(VISIBLE);
                                epa.currentPhoneErrorTV.setText("Field Required");
                            } else {
                                epa.currentPhoneErrorLL.setVisibility(GONE);
                                hintName.setTextColor(getResources().getColor(R.color.primary_black));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            }
                        } else {
                            if ((pnET.getText().length() > 0 && pnET.getText().length() < 14)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                epa.newPhoneErrorLL.setVisibility(VISIBLE);
                                epa.newPhoneErrorTV.setText("Invalid Phone Number");
                            } else if ((pnET.getText().length() == 0)) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                epa.newPhoneErrorLL.setVisibility(VISIBLE);
                                epa.newPhoneErrorTV.setText("Field Required");
                            } else {
                                epa.newPhoneErrorLL.setVisibility(GONE);
                                hintName.setTextColor(getResources().getColor(R.color.primary_black));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            }
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

                EditPhoneActivity epa = EditPhoneActivity.editPhoneActivity;

                if (hintName.getText().toString().contains("Current")) {

                    epa.isCurrentPhone = pnET.getText().toString().trim().length() > 13;

                    if (epa.isCurrentPhone &&
                            epa.isNewPhone) {
                        epa.isSaveEnabled = true;
                        epa.savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    } else {
                        epa.isSaveEnabled = false;
                        epa.savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                    if ((pnET.getText().length() == 0)) {
                        epa.currentPhoneErrorLL.setVisibility(VISIBLE);
                        epa.currentPhoneErrorTV.setText("Field Required");
                    }else if(pnET.getText().length() > 0){
                        epa.currentPhoneErrorLL.setVisibility(GONE);
                    }

                } else {

                    epa.isNewPhone = pnET.getText().toString().trim().length() > 13;

                    if (epa.isCurrentPhone &&
                            epa.isNewPhone) {
                        epa.isSaveEnabled = true;
                        epa.savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    } else {
                        epa.isSaveEnabled = false;
                        epa.savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                    if ((pnET.getText().length() == 0)) {
                        epa.newPhoneErrorLL.setVisibility(VISIBLE);
                        epa.newPhoneErrorTV.setText("Field Required");
                    }else if(pnET.getText().length() > 0){
                        epa.newPhoneErrorLL.setVisibility(GONE);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

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

    public void setHint(String hint) {
        pnET.setHint(hint);
        hintName.setText(hint);

        if (hint.contains("Current")){
            pnET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }else{
            pnET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }
    public void setFocus() {
        pnET.requestFocus();
    }

    public void disable(){
        pnET.setFocusable(false);
        pnET.setClickable(false);
        pnET.setEnabled(false);
    }
}
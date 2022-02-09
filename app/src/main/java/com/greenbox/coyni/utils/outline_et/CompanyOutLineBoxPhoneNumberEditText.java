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
import com.greenbox.coyni.view.business.CompanyInformationActivity;
import com.greenbox.coyni.view.business.DBAbasicInformationAcivity;

public class CompanyOutLineBoxPhoneNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText phnET;
    boolean isPhoneError1 = false;
    private Context mContext;
    public String FROM = "";

    public CompanyOutLineBoxPhoneNumberEditText(Context context) {
        this(context, null, 0);
    }

    public CompanyOutLineBoxPhoneNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompanyOutLineBoxPhoneNumberEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.company_outlinebox_phone_number_layout, this, true);
        hintName = findViewById(R.id.companyhintTV);
        hintHolder = findViewById(R.id.companyhintdHolderLL);
        phnET = findViewById(R.id.pnET);

        phnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {

                    if (isPhoneError1) {
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
                    if (FROM.equals("DbaInfo") && !b) {
                        DBAbasicInformationAcivity dba = (DBAbasicInformationAcivity) mContext;
                        if ((phnET.getText().length() > 0 && phnET.getText().length() < 14)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            dba.customerphonenumLL.setVisibility(VISIBLE);
                            dba.customernumTV.setText("Invalid Phone Number");
                        } else if ((phnET.getText().length() == 0)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            dba.customerphonenumLL.setVisibility(VISIBLE);
                            dba.customernumTV.setText("Field Required");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    } else if (FROM.equals("Company_Information") && !b) {
                        CompanyInformationActivity comp = (CompanyInformationActivity) mContext;
                        if ((phnET.getText().length() > 0 && phnET.getText().length() < 14)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.compphoneNumberErrorLL.setVisibility(VISIBLE);
                            comp.compphonenumberTV.setText("Invalid Phone Number");
                        } else if ((phnET.getText().length() == 0)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.compphoneNumberErrorLL.setVisibility(VISIBLE);
                            comp.compphonenumberTV.setText("Field Required");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        phnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (FROM.equals("Company_Information")) {
                    CompanyInformationActivity comAct = (CompanyInformationActivity) mContext;
                    if (charSequence.length() == 14) {
                        isPhoneError1 = false;
                        comAct.compphoneNumberErrorLL.setVisibility(GONE);
                    }

                    comAct.iscompPhoneNumber = phnET.getText().toString().trim().length() > 13;

                    if (comAct.iscompanyName && comAct.iscompanyEmail && comAct.iscompPhoneNumber) {
                        comAct.isBasicNextEnabled = true;
                        comAct.basicNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    } else {
                        comAct.isBasicNextEnabled = false;
                        comAct.basicNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                    if ((phnET.getText().length() == 0)) {
                        comAct.compphoneNumberErrorLL.setVisibility(VISIBLE);
                        comAct.compphonenumberTV.setText("Field Required");
                    }
                } else if (FROM.equals("DbaInfo")) {
                    DBAbasicInformationAcivity dba = (DBAbasicInformationAcivity) mContext;
                    if (charSequence.length() == 14) {
                        isPhoneError1 = false;
                        dba.customerphonenumLL.setVisibility(GONE);
                    }
                    dba.iscustPhoneNumber = phnET.getText().toString().trim().length() > 13;

                    if (dba.isdbaName && dba.isdbaEmail && dba.iscustPhoneNumber) {
                        dba.isNextEnabled = true;
                        dba.dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    } else {
                        dba.isNextEnabled = false;
                        dba.dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }
                    if ((phnET.getText().length() == 0)) {

                        dba.customerphonenumLL.setVisibility(VISIBLE);
                        dba.customernumTV.setText("Field Required");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public void setErrorOutlineBox() {
        isPhoneError1 = true;
        hintName.setTextColor(getResources().getColor(R.color.error_red));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
    }


    public void setFrom(String fromm, Context context) {
        FROM = fromm;
        mContext = context;
    }


    public void setText(String text) {
        phnET.setText(text);
    }

    public void setHintText(String text) {
        hintName.setText(text);
    }

    public String getText() {
        return phnET.getText().toString().trim();
    }

    public int getETID() {
        return phnET.getId();
    }

    public void requestETFocus() {
        phnET.requestFocus();
    }

    public String getUnmaskedText() {
        return phnET.getUnMasked();
    }


}
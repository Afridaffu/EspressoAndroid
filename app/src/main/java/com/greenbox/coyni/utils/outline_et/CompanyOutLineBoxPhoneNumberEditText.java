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
import com.greenbox.coyni.view.business.DBAInfoAcivity;

public class CompanyOutLineBoxPhoneNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText phnET;
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
        phnET = findViewById(R.id.companypnET);

        phnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {

                    if (b) {
                        if (FROM.equals("DbaInfo")) {
                            DBAInfoAcivity dba = (DBAInfoAcivity) mContext;
                            dba.customerphonenumLL.setVisibility(GONE);
                        } else if (FROM.equals("Company_Information")) {
                            CompanyInformationActivity cia = (CompanyInformationActivity) mContext;
                            cia.compphoneNumberErrorLL.setVisibility(GONE);
                        }
                        hintName.setVisibility(VISIBLE);
                        hintName.setTextColor(getResources().getColor(R.color.primary_color));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    } else {
                        if (phnET.getText().toString().length() > 0)
                            hintName.setVisibility(VISIBLE);
                        else
                            hintName.setVisibility(GONE);
                        hintName.setTextColor(getResources().getColor(R.color.primary_black));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                    }

                    if (FROM.equals("DbaInfo") && !b) {
                        DBAInfoAcivity dba = (DBAInfoAcivity) mContext;
                        if ((phnET.getText().length() > 0 && phnET.getText().length() < 14)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            dba.customerphonenumLL.setVisibility(VISIBLE);
                            dba.customernumTV.setText("Please enter a valid Phone Number");
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
                            comp.compphonenumberTV.setText("Please enter a valid Phone Number");
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

                if (charSequence.length() > 0)
                    hintName.setVisibility(VISIBLE);

                if (FROM.equals("Company_Information")) {
                    CompanyInformationActivity comAct = (CompanyInformationActivity) mContext;
                    if (charSequence.length() == 14)
                        comAct.compphoneNumberErrorLL.setVisibility(GONE);

                    comAct.iscompPhoneNumber = phnET.getText().toString().trim().length() > 13;

                    comAct.enableOrDisableNext();
                } else if (FROM.equals("DbaInfo")) {
                    DBAInfoAcivity dba = (DBAInfoAcivity) mContext;
                    if (charSequence.length() == 14)
                        dba.customerphonenumLL.setVisibility(GONE);
                    dba.iscustPhoneNumber = phnET.getText().toString().trim().length() > 13;
                    dba.enableOrDisableNext();
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

    public void setFrom(String fromm, Context context) {
        FROM = fromm;
        mContext = context;
    }

    public void setText(String text) {
        phnET.setText(text);
    }

    public void setHintText(String text) {
        hintName.setText(text);
        if (text.equals("Customer Service Phone Number"))
//            phnET.setHint("Customer Service Number");
            phnET.setHint("");
        else
            phnET.setHint(text);
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

    public void setSelection() {
        phnET.setSelection(phnET.getText().toString().length());
    }


}
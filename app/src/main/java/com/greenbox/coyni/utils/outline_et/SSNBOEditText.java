package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MaskEditText.widget.MaskEditText;
import com.greenbox.coyni.view.business.AddBeneficialOwnerActivity;
import com.greenbox.coyni.view.business.CompanyInformationActivity;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSNBOEditText extends ConstraintLayout {
    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText ssnET;
    private Context mContext;
    public String FROM = "", hintString = "";
    private ImageView viewSSNIV;
    private Boolean isCPwdEye = true;

    public SSNBOEditText(Context context) {
        this(context, null, 0);
    }

    public SSNBOEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SSNBOEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.outline_bo_ssn, this, true);
        hintHolder = findViewById(R.id.ssnhintdHolderLL);
        ssnET = findViewById(R.id.ssnET);
        hintName = findViewById(R.id.ssnhintTV);
        viewSSNIV = findViewById(R.id.viewSSNIV);

        ssnET.setOnFocusChangeListener((view, b) -> {
            try {
                AddBeneficialOwnerActivity aboa = (AddBeneficialOwnerActivity) mContext;
                if (b) {
                    ssnET.setHint("•••-••-••••");
                    hintName.setVisibility(VISIBLE);
                    hintName.setTextColor(getResources().getColor(R.color.primary_color));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    aboa.ssnLL.setVisibility(GONE);

                } else {
                    ssnET.setHint("SSN");
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                    if ((ssnET.getText().length() > 0 && ssnET.getText().length() < 11)) {
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                        hintName.setVisibility(VISIBLE);
                        aboa.ssnLL.setVisibility(VISIBLE);
                        aboa.ssnTV.setText("Please enter a valid SSN");
                        hintName.setTextColor(getResources().getColor(R.color.error_red));
                    } else if ((ssnET.getText().length() == 0)) {
                        hintName.setTextColor(getResources().getColor(R.color.error_red));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                        aboa.ssnLL.setVisibility(VISIBLE);
                        aboa.ssnTV.setText("Field Required");
                        hintName.setVisibility(GONE);
                    } else {
                        hintName.setTextColor(getResources().getColor(R.color.primary_black));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        aboa.ssnLL.setVisibility(GONE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ssnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                AddBeneficialOwnerActivity comAct = (AddBeneficialOwnerActivity) mContext;
                if (charSequence.length() == 11) {
                    comAct.ssnLL.setVisibility(GONE);
                    comAct.isssn = true;
                } else if (charSequence.length() > 0 && charSequence.length() < 11) {
                    comAct.isssn = false;
                } else if ((ssnET.getText().length() == 0)) {
                    comAct.isssn = false;
                }
                comAct.enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        viewSSNIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isCPwdEye) {
                        isCPwdEye = true;
                        viewSSNIV.setBackgroundResource(R.drawable.ic_eyeclose);
                        ssnET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        isCPwdEye = false;
                        viewSSNIV.setBackgroundResource(R.drawable.ic_eyeopen);
                        ssnET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }

                    if (ssnET.getText().length() > 0) {
                        ssnET.setSelection(ssnET.getText().toString().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void setFrom(String fromm, Context context) {
        FROM = fromm;
        mContext = context;
    }

    public void setSelection() {
        ssnET.setSelection(ssnET.getText().toString().length());
    }

    public void setText(String text) {
        hintName.setVisibility(VISIBLE);
        ssnET.setText(text);
        ssnET.setSelection(text.length());
    }

    public String getText() {
        return ssnET.getText().toString().trim();
    }

    public String getUnMasked() {
        return ssnET.getUnMasked();
    }

    public int getETID() {
        return ssnET.getId();
    }


}
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

public class SSNOutlineBoxNumberEditText extends ConstraintLayout {
    private TextView ssnType;
    private LinearLayout hintHolder;
    private MaskEditText ssnET;
    private Context mContext;
    public String FROM = "";

    public SSNOutlineBoxNumberEditText(Context context) {
        this(context, null, 0);
    }

    public SSNOutlineBoxNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SSNOutlineBoxNumberEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.activity_ssnoutline_box_number_edit_text, this, true);
        hintHolder = findViewById(R.id.ssnhintdHolderLL);
        ssnType = findViewById(R.id.ssnType);
        ssnET = findViewById(R.id.ssnET);

        ssnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    CompanyInformationActivity comp = (CompanyInformationActivity) mContext;
                    if (b) {
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    } else {
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        if ((ssnET.getText().length() > 0 && ssnET.getText().length() < 9)) {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.ssnErrorLL.setVisibility(VISIBLE);
                            comp.ssnErrorTV.setText("Enter a Valid " + getSSNTypeText());
                        } else if ((ssnET.getText().length() == 0)) {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.ssnErrorLL.setVisibility(VISIBLE);
                            comp.ssnErrorTV.setText("Field Required");
                        } else {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            comp.ssnErrorLL.setVisibility(GONE);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        ssnET.setOnTouchListener((view, motionEvent) -> {
            CompanyInformationActivity comp = (CompanyInformationActivity) mContext;
            comp.basicInfoSL.scrollTo(comp.ssnET.getLeft(), comp.ssnET.getBottom());
            return false;
        });

        ssnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CompanyInformationActivity comAct = (CompanyInformationActivity) mContext;
                if (charSequence.length() == 9) {
                    comAct.ssnErrorLL.setVisibility(GONE);
                    comAct.isSSN = true;
                } else if (charSequence.length() > 0 && charSequence.length() < 9) {
                    comAct.isSSN = false;
                } else if ((ssnET.getText().length() == 0)) {
                    comAct.isSSN = false;
                    comAct.ssnErrorLL.setVisibility(VISIBLE);
                    comAct.ssnErrorTV.setText("Field Required");
                }
                comAct.enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void setFrom(String fromm, Context context) {
        FROM = fromm;
        mContext = context;
    }

    public void setText(String text) {
        ssnET.setText(text);
    }

    public String getText() {
        return ssnET.getText().toString().trim();
    }

    public int getETID() {
        return ssnET.getId();
    }

    public void requestETFocus() {
        ssnET.requestFocus();
    }

    public void setSSNTypeText(String text) {
        ssnType.setText(text);
    }

    public String getSSNTypeText() {
        return ssnType.getText().toString();
    }

    public String getUnmaskedText() {
        return ssnET.getUnMasked();
    }
}
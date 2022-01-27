package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MaskEditText.widget.MaskEditText;

public class SSNOutlineBoxNumberEditText extends ConstraintLayout {
    private TextView hintName;
    private LinearLayout hintHolder;
    private MaskEditText ssnET;
    boolean isBusinError = false;

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
        ssnET = findViewById(R.id.ssnET);
        ssnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setFrom(String fromm) {
        FROM = fromm;
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




}
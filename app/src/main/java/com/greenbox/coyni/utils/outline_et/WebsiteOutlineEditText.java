package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;

public class WebsiteOutlineEditText extends ConstraintLayout {
    private TextView websitehintName;
    private LinearLayout websitehintHolder;
    private EditText websiteET;

    public String FROM = "";

    public WebsiteOutlineEditText(Context context) {
        this(context, null, 0);
    }

    public WebsiteOutlineEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebsiteOutlineEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.activity_website, this, true);
        websitehintHolder = findViewById(R.id.websitehintdHolderLL);
        websiteET = findViewById(R.id.websiteET);

        websiteET.setOnFocusChangeListener(new OnFocusChangeListener() {
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
        websiteET.setText(text);
    }

    public String getText() {
        return websiteET.getText().toString().trim();
    }

    public int getETID() {
        return websiteET.getId();
    }

    public void requestETFocus() {
        websiteET.requestFocus();
    }




}
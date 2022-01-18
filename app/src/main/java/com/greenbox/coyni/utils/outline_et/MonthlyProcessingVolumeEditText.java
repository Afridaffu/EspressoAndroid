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

public class MonthlyProcessingVolumeEditText extends ConstraintLayout {

    private TextView volumehintName;
    private LinearLayout hintHolder;
    private EditText volumeET;
    public String FROM = "";

    public MonthlyProcessingVolumeEditText(Context context) {
        this(context, null, 0);
    }

    public MonthlyProcessingVolumeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthlyProcessingVolumeEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.monthly_processing_volume_layout, this, true);
        hintHolder = findViewById(R.id.volhintdHolderLL);
        volumeET = findViewById(R.id.volET);

        volumeET.setOnFocusChangeListener(new OnFocusChangeListener() {
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
        volumeET.setText(text);
    }

    public String getText() {
        return volumeET.getText().toString().trim();
    }

    public int getETID() {
        return volumeET.getId();
    }

    public void requestETFocus() {
        volumeET.requestFocus();
    }


}
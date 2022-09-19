package com.coyni.mapp.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coyni.mapp.R;
import com.coyni.mapp.view.business.DBAInfoAcivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteOutlineEditText extends ConstraintLayout {
    private TextView hintName, websiteErrorTV;
    private LinearLayout hintHolder, websiteErrorLL;
    private EditText websiteET;
    private Context mContext;
    public String FROM = "", hintString = "";

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
        hintHolder = findViewById(R.id.websitehintdHolderLL);
        websiteET = findViewById(R.id.websiteET);
        hintName = findViewById(R.id.websitehintTV);
        websiteErrorLL = findViewById(R.id.websiteErrorLL);
        websiteErrorTV = findViewById(R.id.websiteErrorTV);

        websiteET.setOnFocusChangeListener((view, b) -> {
            try {
                if (b) {
//                    websiteET.setHint(hintString);
                    websiteET.setHint("www.example.com");
                    hintName.setVisibility(VISIBLE);
                    hintName.setTextColor(getResources().getColor(R.color.primary_color));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    websiteErrorLL.setVisibility(GONE);
                } else {
                    websiteET.setHint(hintString);
                    if (websiteET.getText().toString().length() > 0)
                        hintName.setVisibility(VISIBLE);
                    else
                        hintName.setVisibility(GONE);

                    if (websiteET.getText().toString().trim().length() > 0 && !isValidUrl(websiteET.getText().toString().trim())) {
                        websiteET.setHint("");
//                        if (hintString.equals("Website")) {
                        if (hintString.equals("Website (Optional)")) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            websiteErrorLL.setVisibility(VISIBLE);
                            websiteErrorTV.setText("Please enter a valid Website");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    } else if ((websiteET.getText().length() == 0)) {
                        websiteET.setHint(hintString);
                        if (hintString.equals("Website")) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            websiteErrorLL.setVisibility(VISIBLE);
                            websiteErrorTV.setText("Field Required");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    } else {
                        websiteET.setHint("");
                        hintName.setTextColor(getResources().getColor(R.color.primary_black));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        websiteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().length() > 0)
                    hintName.setVisibility(VISIBLE);

                if (FROM.equals("DBA_INFO")) {
                    DBAInfoAcivity dia = (DBAInfoAcivity) mContext;
//                    if (isValidUrl(charSequence.toString())) {
                    if (isValidUrl(charSequence.toString()) || charSequence.toString().equals("")) {
                        dia.isWebsite = true;
                        websiteErrorLL.setVisibility(GONE);
                    } else {
//                        if (hintString.equals("Website"))
//                            dia.isWebsite = false;
//                        else
//                            dia.isWebsite = true;
                        dia.isWebsite = false;
                    }
                    dia.enableOrDisableNext();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = websiteET.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        websiteET.setText("");
                        websiteET.setSelection(websiteET.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        websiteET.setText(str.trim());
                    } else if (str.length() > 0 && str.contains(" ")) {
                        websiteET.setText(websiteET.getText().toString().replaceAll(" ", ""));
                        websiteET.setSelection(websiteET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

//        websiteET.setOnTouchListener((view, motionEvent) -> {
//            if(FROM.equals("DBA_INFO")){
//                DBAInfoAcivity dia = (DBAInfoAcivity) mContext;
//                dia.pageOneView.setVisibility(VISIBLE);
//                dia.dbaBasicSL.scrollTo(dia.websiteOET.getLeft(), dia.websiteOET.getBottom());
//                websiteET.requestFocus();
//                websiteET.setSelection(websiteET.getText().toString().length());
//            }
//            return false;
//        });
    }

    public void setFrom(String fromm, Context context) {
        FROM = fromm;
        mContext = context;
    }

    public void setSelection() {
        websiteET.setSelection(websiteET.getText().toString().length());
    }

    public void setHint(String text) {
        hintName.setText(text);
        hintString = text;
//        if (!text.equals("Website") && !websiteET.hasFocus()) {
        if (!text.equals("Website (Optional)") && !websiteET.hasFocus()) {
            websiteET.setHint(text);
            websiteErrorLL.setVisibility(GONE);
            hintName.setTextColor(getResources().getColor(R.color.primary_black));
            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
//        } else if (text.equals("Website") && !websiteET.hasFocus()) {
        } else if (text.equals("Website (Optional)") && !websiteET.hasFocus()) {
            websiteET.setHint(text);
            if (websiteET.getText().toString().trim().length() > 0 && !isValidUrl(websiteET.getText().toString().trim())) {
                hintName.setTextColor(getResources().getColor(R.color.error_red));
                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                websiteErrorLL.setVisibility(VISIBLE);
                websiteErrorTV.setText("Please enter a valid Website");
            }
        }
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

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

}
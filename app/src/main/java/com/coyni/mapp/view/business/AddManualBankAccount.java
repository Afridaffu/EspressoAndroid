package com.coyni.mapp.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.CustomTypefaceSpan;

public class AddManualBankAccount extends AppCompatActivity {

    private TextView descriptionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual_bank_account);

        initfields();

    }

    private void initfields() {

        descriptionTV = findViewById(R.id.descriptionTV);
    }

    public void setSpannableText() {
        SpannableString ss = new SpannableString(descriptionTV.getText().toString());

        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(ss);
        SS.setSpan(new CustomTypefaceSpan("", font), ss.length() - 16, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        SS.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), ss.length() - 16, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SS.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), ss.length() - 16, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        descriptionTV.setText(SS);
        descriptionTV.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionTV.setHighlightColor(Color.TRANSPARENT);
    }

}
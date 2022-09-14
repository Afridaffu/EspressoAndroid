package com.coyni.mapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.ActivityGetHelpBinding;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.LogUtils;

public class GetHelpActivity extends BaseActivity {
    private TextView supportTv,supportDescClick;
    private ImageView ivBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
        initFields();
    }

    private void initFields() {
        supportTv = findViewById(R.id.text_support_tv);
        supportDescClick = findViewById(R.id.support_text_click);
        ivBackButton = findViewById(R.id.IVBack);
        spannableText();
        ivBackButton.setOnClickListener(view -> finish());
    }

    private void spannableText() {
        final int startValue = 41,endValue = 71;
        SpannableString spannableString = new SpannableString(getString(R.string.support_text));
        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_regular.ttf");
//        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.balance)), startValue, endValue, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setTypeface(font);
                ds.setColor(getColor(R.color.balance));
                ds.setUnderlineText(false);
            }
        }, startValue, endValue, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        supportTv.setText(spannableString);

        final int start =37,end=55;
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        SpannableString spannableString1 = new SpannableString(getString(R.string.get_help_description));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    //intent.putExtra(Intent.EXTRA_EMAIL, "shivas@ideyalabs.com");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ravim@ideyalabs.com"});
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "Test Subject");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setTypeface(font1);
                ds.setColor(getColor(R.color.primary_color));
            }
        };
        spannableString1.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        supportDescClick.setText(spannableString1);
        supportDescClick.setMovementMethod(LinkMovementMethod.getInstance());
        supportDescClick.setHighlightColor(Color.TRANSPARENT);
    }
}
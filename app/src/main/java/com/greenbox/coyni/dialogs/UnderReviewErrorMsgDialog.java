package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class UnderReviewErrorMsgDialog extends BaseDialog {

    private CardView okCV;
    private Context context;
    private TextView tvMessage;
    Long mLastClickTime = 0L;


    public UnderReviewErrorMsgDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public UnderReviewErrorMsgDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.under_review_error_msg_dialog);

        okCV = findViewById(R.id.okCV);
        okCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvMessage = findViewById(R.id.tvMessage2);

        String strMessage = context.getString(R.string.please_contact_the_coyni_customer_support);
        SpannableString ss = new SpannableString(strMessage);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(Utils.mondayURL));
                    context.startActivity(i);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
//                ds.setColor(Color.parseColor("#00a6a2"));
                ds.setUnderlineText(true);
            }
        };

        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#00a6a2")), strMessage.indexOf("customer support."), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), strMessage.indexOf("customer support."), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, strMessage.length() - 17, strMessage.length(), 0);
        tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        tvMessage.setText(ss);

    }
}

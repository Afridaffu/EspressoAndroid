package com.coyni.mapp.dialogs;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.CustomTypefaceSpan;

public class ApplicationApprovedDialog extends BaseDialog{
    private Context context;
    private ImageView closeIV;
    private TextView manualReleaseTv, rollingReleasesTv;


    public ApplicationApprovedDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_learn_more);
        closeIV = findViewById(R.id.closeIv);

        rollingReleasesTv = findViewById(R.id.tv_rolling_reserves);
        manualReleaseTv = findViewById(R.id.tv_manual_releases);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/opensans_bold.ttf");

        SpannableStringBuilder rolling = new SpannableStringBuilder(context.getString(R.string.rolling_reserves_with_a_rolling_reserve));
        rolling.setSpan(new CustomTypefaceSpan("", font), 0, 17, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        rollingReleasesTv.setText(rolling);

        SpannableStringBuilder manual = new SpannableStringBuilder(context.getString(R.string.manually_reserves));
        manual.setSpan(new CustomTypefaceSpan("", font), 0, 18, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        manualReleaseTv.setText(manual);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}

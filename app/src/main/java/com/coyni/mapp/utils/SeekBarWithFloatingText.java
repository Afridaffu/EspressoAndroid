package com.coyni.mapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.merchant_activity.Earning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeekBarWithFloatingText extends RelativeLayout {

    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvFloatingText, tv_floating_text_currency;
    private LinearLayout tvFloatingTextLL;
    private SeekBar seekBar;
    private View thumbView;
    private String floatingText;
    private List<Earning> userData;
    String defaultValue = " 0.00";
    private double totalAmountCumulate = 0.0;
    private ArrayList<Integer> keys = new ArrayList<>();


    public SeekBarWithFloatingText(Context context) {
        super(context);
        initView();
    }

    public SeekBarWithFloatingText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SeekBarWithFloatingText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setEnabled(boolean enabled) {
        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !enabled;
            }
        });
    }

    public void setProgressWithText(int progress, List<Earning> userData) {

        this.userData = userData;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                totalAmountCumulate = 0.0;
                seekBar.setProgress(progress);
                setTextPos(progress);
            }
        }, 1000);
    }

    private void initView() {
        inflate(getContext(), R.layout.seekbar_with_floating_text, this);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        thumbView = inflater.inflate(R.layout.seek_bar_thumb, null);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvFloatingText = findViewById(R.id.tv_floating_text);
        tvFloatingTextLL = findViewById(R.id.tv_floating_text_LL);
        tv_floating_text_currency = findViewById(R.id.tv_floating_text_currency);
        seekBar = findViewById(R.id.seekbar);
        seekBar.setThumb(getThumb());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTextPos(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setTextPos(int progress) {
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int width = seekBar.getWidth();
        int valueInPixels24 = (int) getResources().getDimension(R.dimen._24sdp);
        int valueInPixels10 = (int) getResources().getDimension(R.dimen._7sdp);
        int valueInPixels5 = (int) getResources().getDimension(R.dimen._7sdp);
        int calculatedWidth = (width / 24);
        String text = "";

        List<Earning> earningList = userData;
        if (progress < 12) {
            if (progress < 10) {
                if (progress == 0) {
                    text = "12:00am";
                } else {
                    text = "0" + progress + ":00am";
                }
            } else {
                text = progress + ":00am";
            }
//            text = totalAmount.toLowerCase();
            calculatedWidth = calculatedWidth + valueInPixels10 * progress;
        } else if (progress == 12) {
            text = progress + ":00pm";
//            text = totalAmount.toLowerCase();
            calculatedWidth = calculatedWidth + valueInPixels10 * progress;
        } else {
//            if (progress > 12 && progress < 20) {
            calculatedWidth = calculatedWidth + valueInPixels10 * progress;
//            }
//            else {
//                calculatedWidth = calculatedWidth + valueInPixels10 * progress;
////                calculatedWidth = valueInPixels5 * 20 + 20 - calculatedWidth;  // for fix the Position
//            }
            if (progress != 24) {
                text = (progress - 12) + ":00pm";
            } else {
                text = "11:59pm";
            }
//            text = totalAmount.toLowerCase();
        }
        if (calculatedWidth < width) {
            layoutParams.setMargins(calculatedWidth, 0, 0, 0);
            tvFloatingTextLL.setLayoutParams(layoutParams);
        }
        tvFloatingText.setText(text + defaultValue);
        if (earningList != null) {
//            for (int position = 0; position < earningList.size(); position++) {
//                if (progress == earningList.get(position).getKey()) {
//                    tvFloatingText.setText(text + " " + Utils.convertTwoDecimal(String.valueOf(earningList.get(position).getAmount())) + " CYN");
//                    break;
//                } else {
//                    tvFloatingText.setText(text + defaultValue);
//                }
//
//            }
            keys = new ArrayList<>();
            totalAmountCumulate = 0.0;
            for (int i = 0; i < earningList.size(); i++) {
                keys.add(earningList.get(i).getKey());
            }
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i) <= progress) {
                    totalAmountCumulate = totalAmountCumulate + earningList.get(i).getAmount();
                }
            }

        }
        if (totalAmountCumulate != 0) {
            tvFloatingText.setText(text + " " + Utils.convertTwoDecimal(String.valueOf(totalAmountCumulate)));
            tv_floating_text_currency.setText(" CYN");
        } else {
            tvFloatingText.setText(text + defaultValue);
            tv_floating_text_currency.setText(" CYN");
        }

    }

    public Drawable getThumb() {
        thumbView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

}

package com.greenbox.coyni.utils;

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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.merchant_activity.Earning;

import java.util.List;
import java.util.Vector;

public class SeekBarWithFloatingText extends RelativeLayout {

    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvFloatingText;
    private SeekBar seekBar;
    private View thumbView;
    private String floatingText;
    private List<Earning> userData;
    String defaultValue = " 0.00 CYN";
    private double totalAmountCumulate = 0.0;


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
            }
            else {
                text = "11:59pm";
            }
//            text = totalAmount.toLowerCase();
        }
        if (calculatedWidth < width) {
            layoutParams.setMargins(calculatedWidth, 0, 0, 0);
            tvFloatingText.setLayoutParams(layoutParams);
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

            for (int position = 0; position < earningList.size(); position++) {
               totalAmountCumulate = totalAmountCumulate + earningList.get(progress).getAmount();
                if (progress == earningList.get(position).getKey()) {

                    tvFloatingText.setText(text + " " + Utils.convertTwoDecimal(String.valueOf(totalAmountCumulate)) + " CYN");
                } else {
                    tvFloatingText.setText(text + defaultValue);
                }

            }

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

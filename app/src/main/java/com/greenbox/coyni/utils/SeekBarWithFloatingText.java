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

public class SeekBarWithFloatingText extends RelativeLayout {

    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvFloatingText;
    private SeekBar seekBar;
    private View thumbView;
    private String floatingText;
    private String minutesText;

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

    public void setProgressWithText(int progress, String time, String floating) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                floatingText = floating;
                minutesText = time;
                seekBar.setProgress(progress);
            }
        }, 100);
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
        int calculatedWidth = (width / 24);
        String text = "";
        if (progress < 12) {
//            text = minutesText + "am";
            text = minutesText.toLowerCase();
            calculatedWidth = calculatedWidth + 20 * progress;
        } else if (progress == 12) {
//            text = minutesText + "pm";
            text = minutesText.toLowerCase();
            calculatedWidth = calculatedWidth + 20 * progress;
        } else {
            if (progress > 12 && progress < 22) {
                calculatedWidth = calculatedWidth + 20 * progress;
            }
            else {
                calculatedWidth = 20*22 +20 -calculatedWidth;  // for fix the Position
            }
//            text = (progress - 12) + minutesText.substring(2) + "pm";
            text = minutesText.toLowerCase();
        }
        if (calculatedWidth < width) {
            layoutParams.setMargins(calculatedWidth, 0, 0, 0);
            tvFloatingText.setLayoutParams(layoutParams);
        }
        tvFloatingText.setText(text + " " + floatingText + " CYN");
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

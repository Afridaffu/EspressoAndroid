package com.greenbox.coyni.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AutoScrollPagerAdapter;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;

public class OnboardActivity extends AppCompatActivity {

    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 3000;

    LinearLayout getStarted;

    Long mLastClickTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_onboard);

        getStarted = findViewById(R.id.getStartedLL);

        AutoScrollPagerAdapter autoScrollPagerAdapter =
                new AutoScrollPagerAdapter(getSupportFragmentManager());
        AutoScrollViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(autoScrollPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        // start auto scroll
//        viewPager.startAutoScroll();
        // set auto scroll time in mili
        viewPager.setInterval(AUTO_SCROLL_THRESHOLD_IN_MILLI);
        // enable recycling using true
        viewPager.setCycle(true);

        getStarted.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivity(new Intent(OnboardActivity.this, AccountTypeActivity.class));
        });

        Log.e("Density", getDeviceDensity(this));
    }

    public static String getDeviceDensity(Context context){
        String deviceDensity = "";
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_TV:
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_260:
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_340:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
            case DisplayMetrics.DENSITY_440:
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
        }
        return deviceDensity;
    }
}
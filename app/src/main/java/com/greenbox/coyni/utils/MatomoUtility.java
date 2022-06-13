package com.greenbox.coyni.utils;

import android.app.Activity;
import android.content.Context;

import com.greenbox.coyni.BuildConfig;

import org.matomo.sdk.Matomo;
import org.matomo.sdk.Tracker;
import org.matomo.sdk.TrackerBuilder;
import org.matomo.sdk.extra.TrackHelper;

public class MatomoUtility {

    private Tracker tracker;
    private Context context;
    private static MatomoUtility instance;
    private static String API_URL = BuildConfig.MATOMO_SERVER + "/matomo.php";
    private static int SITE_ID = 1;

    private MatomoUtility() {
        context = MyApplication.getContext();
        tracker = TrackerBuilder.createDefault(API_URL, SITE_ID)
                .build(Matomo.getInstance(context));
    }

    public static synchronized MatomoUtility getInstance() {
        if (instance == null) {
            instance = new MatomoUtility();
        }
        return instance;
    }

    public void trackAppInstall() {
        if(BuildConfig.ANALYTICS_ENABLED) {
            TrackHelper.track().download().with(tracker);
        }
    }

    public void trackScreen(String screen) {
        if(BuildConfig.ANALYTICS_ENABLED) {
            TrackHelper.track().screen(screen).with(tracker);
        }
    }

    public void trackScreen(String screen, String title) {
        if(BuildConfig.ANALYTICS_ENABLED) {
            TrackHelper.track().screen(screen).title(title).with(tracker);
        }
    }

    public void trackActivity(Activity screen) {
        if(BuildConfig.ANALYTICS_ENABLED) {
            TrackHelper.track().screen(screen).with(tracker);
        }
    }

    public void trackEvent(String category, String action) {
        if(BuildConfig.ANALYTICS_ENABLED) {
            TrackHelper.track().event(category, action).with(tracker);
        }
    }
}

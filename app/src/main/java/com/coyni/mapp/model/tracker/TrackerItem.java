package com.coyni.mapp.model.tracker;

public class TrackerItem {
    private int featureIcon;
    private String featureName;
    private String featureSatus;
    private boolean isClickable = true;

    public int getFeatureIcon() {
        return featureIcon;
    }

    public void setFeatureIcon(int featureIcon) {
        this.featureIcon = featureIcon;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureSatus() {
        return featureSatus;
    }

    public void setFeatureSatus(String featureSatus) {
        this.featureSatus = featureSatus;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }
}

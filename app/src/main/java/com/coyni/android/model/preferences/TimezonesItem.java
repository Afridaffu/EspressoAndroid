package com.coyni.android.model.preferences;

public class TimezonesItem {
    private String algorithmName;

    public TimezonesItem(String countryName)
    {
        algorithmName = countryName;
    }

    public String getAlgorithmName()
    {
        return algorithmName;
    }
}

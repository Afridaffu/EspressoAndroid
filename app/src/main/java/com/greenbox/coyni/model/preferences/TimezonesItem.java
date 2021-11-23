package com.greenbox.coyni.model.preferences;

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

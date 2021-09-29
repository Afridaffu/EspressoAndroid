package com.coyni.android.model.user;

public class UserPreferenceModel {
    int localCurrency;
    int timezone;

    public int getLocalCurrency() {
        return localCurrency;
    }

    public void setLocalCurrency(int localCurrency) {
        this.localCurrency = localCurrency;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }
}

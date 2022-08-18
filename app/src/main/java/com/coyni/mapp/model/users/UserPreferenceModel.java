package com.coyni.mapp.model.users;

public class UserPreferenceModel {
    int localCurrency;
    int timezone;
    int preferredAccount;

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

    public int getPreferredAccount() {
        return preferredAccount;
    }

    public void setPreferredAccount(int preferredAccount) {
        this.preferredAccount = preferredAccount;
    }
}

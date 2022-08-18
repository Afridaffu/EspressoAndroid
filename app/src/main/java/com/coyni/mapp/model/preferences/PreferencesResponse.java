package com.coyni.mapp.model.preferences;


public class PreferencesResponse {
    private int userId;
    private int timeZone;
    private int localCurrency;
    private String message;
    private String preferredAccount;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public int getLocalCurrency() {
        return localCurrency;
    }

    public void setLocalCurrency(int localCurrency) {
        this.localCurrency = localCurrency;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPreferredAccount() {
        return preferredAccount;
    }

    public void setPreferredAccount(String preferredAccount) {
        this.preferredAccount = preferredAccount;
    }
}

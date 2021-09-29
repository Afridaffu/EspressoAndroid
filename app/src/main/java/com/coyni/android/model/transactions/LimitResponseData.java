package com.coyni.android.model.transactions;

public class LimitResponseData {
    private String dailyAccountLimit;
    private String weeklyAccountLimit;
    private double minimumLimit;
    private Boolean tokenLimitFlag;

    public String getDailyAccountLimit() {
        return dailyAccountLimit;
    }

    public void setDailyAccountLimit(String dailyAccountLimit) {
        this.dailyAccountLimit = dailyAccountLimit;
    }

    public String getWeeklyAccountLimit() {
        return weeklyAccountLimit;
    }

    public void setWeeklyAccountLimit(String weeklyAccountLimit) {
        this.weeklyAccountLimit = weeklyAccountLimit;
    }

    public double getMinimumLimit() {
        return minimumLimit;
    }

    public void setMinimumLimit(double minimumLimit) {
        this.minimumLimit = minimumLimit;
    }

    public Boolean getTokenLimitFlag() {
        return tokenLimitFlag;
    }

    public void setTokenLimitFlag(Boolean tokenLimitFlag) {
        this.tokenLimitFlag = tokenLimitFlag;
    }
}

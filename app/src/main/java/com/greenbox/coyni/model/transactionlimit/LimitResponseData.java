package com.greenbox.coyni.model.transactionlimit;

public class LimitResponseData {
    private String dailyAccountLimit;
    private String weeklyAccountLimit;
    private String minimumLimit;
    private String pertxnLimit;
    private Boolean tokenLimitFlag;
    private String limitType;
    private String transactionLimit;

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

    public String getMinimumLimit() {
        return minimumLimit;
    }

    public void setMinimumLimit(String minimumLimit) {
        this.minimumLimit = minimumLimit;
    }

    public String getPertxnLimit() {
        return pertxnLimit;
    }

    public void setPertxnLimit(String pertxnLimit) {
        this.pertxnLimit = pertxnLimit;
    }

    public Boolean getTokenLimitFlag() {
        return tokenLimitFlag;
    }

    public void setTokenLimitFlag(Boolean tokenLimitFlag) {
        this.tokenLimitFlag = tokenLimitFlag;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(String transactionLimit) {
        this.transactionLimit = transactionLimit;
    }
}

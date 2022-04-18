package com.greenbox.coyni.model.business_activity;

import java.io.Serializable;

public class BusinessActivityData implements Serializable {

    private String transactionType;
    private String transactionSubType;
    private String totalAmount;
    private long count;
    private int percentage;
    private String fee;

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public long getCount() {
        return count;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getFee() {
        return fee;
    }
}

package com.greenbox.coyni.model.business_activity;

import java.io.Serializable;

public class BusinessActivityData implements Serializable {

    private String transactionType;
    private String transactionSubType;
    private String totalAmount;
    private int count;
    private int percentage;
    private String fee;
    private String highTicket;

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public int getCount() {
        return count;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setTransactionSubType(String transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getFee() {
        return fee;
    }

    public String getHighTicket() {
        return highTicket;
    }

    public void setHighTicket(String highTicket) {
        this.highTicket = highTicket;
    }
}

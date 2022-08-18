package com.coyni.mapp.model.paidorder;

public class PaidResponseData {
    private String gbxTransactionId;
    private double fee;

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}

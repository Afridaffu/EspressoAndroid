package com.coyni.android.model.sendtransfer;

public class ResponseData {
    private String gbxTransactionId;
    private String blockTransactionId;
    private double tokens;
    private double fee;

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public String getBlockTransactionId() {
        return blockTransactionId;
    }

    public void setBlockTransactionId(String blockTransactionId) {
        this.blockTransactionId = blockTransactionId;
    }

    public double getTokens() {
        return tokens;
    }

    public void setTokens(double tokens) {
        this.tokens = tokens;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}

package com.coyni.android.model.buytoken;

public class BuyTokenResponseData {
    private String gbxTransactionId;
    private String blockTransactionId;
    private String tokens;
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

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}

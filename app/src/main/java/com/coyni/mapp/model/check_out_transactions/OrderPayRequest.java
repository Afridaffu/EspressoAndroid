package com.coyni.mapp.model.check_out_transactions;

public class OrderPayRequest {
    private String amount;
    private String encryptedToken;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEncryptedToken() {
        return encryptedToken;
    }

    public void setEncryptedToken(String encryptedToken) {
        this.encryptedToken = encryptedToken;
    }
}



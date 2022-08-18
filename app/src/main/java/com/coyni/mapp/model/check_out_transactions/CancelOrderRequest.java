package com.coyni.mapp.model.check_out_transactions;

public class CancelOrderRequest {

    private String encryptedToken;

    public String getEncryptedToken() {
        return encryptedToken;
    }

    public void setEncryptedToken(String encryptedToken) {
        this.encryptedToken = encryptedToken;
    }
}

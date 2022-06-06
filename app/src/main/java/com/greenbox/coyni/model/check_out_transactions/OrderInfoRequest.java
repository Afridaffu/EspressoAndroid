package com.greenbox.coyni.model.check_out_transactions;

public class OrderInfoRequest {
    private String encryptedToken;

    public String getEncryptedToken() {
        return encryptedToken;
    }

    public void setEncryptedToken(String encryptedToken) {
        this.encryptedToken = encryptedToken;
    }
}

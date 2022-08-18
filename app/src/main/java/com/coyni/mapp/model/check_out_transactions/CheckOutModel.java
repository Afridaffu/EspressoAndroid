package com.coyni.mapp.model.check_out_transactions;

public class CheckOutModel {

    private boolean isCheckOutFlag;
    private String encryptedToken;


    public String getEncryptedToken() {
        return encryptedToken;
    }

    public void setEncryptedToken(String encryptedToken) {
        this.encryptedToken = encryptedToken;
    }

    public boolean isCheckOutFlag() {
        return isCheckOutFlag;
    }

    public void setCheckOutFlag(boolean checkOutFlag) {
        isCheckOutFlag = checkOutFlag;
    }

}

package com.greenbox.coyni.utils.encryption;

public class EncryptRequest {
    private String encryptData;
    private byte[] encryptKey;
    public String getEncryptData() {
        return encryptData;
    }
    public void setEncryptData(String encryptData) {
        this.encryptData = encryptData;
    }
    public byte[] getEncryptKey() {
        return encryptKey;
    }
    public void setEncryptKey(byte[] encryptKey) {
        this.encryptKey = encryptKey;
    }

}


package com.greenbox.coyni.model.preauth;

public class PreAuthRequest {
    private String key;
    private String payload;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}


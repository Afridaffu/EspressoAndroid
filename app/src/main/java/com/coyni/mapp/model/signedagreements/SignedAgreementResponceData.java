package com.coyni.mapp.model.signedagreements;

import com.google.gson.annotations.SerializedName;

public class SignedAgreementResponceData {

    @SerializedName("id")
    private int id;
    @SerializedName("signature")
    private String signature;
    @SerializedName("message")
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

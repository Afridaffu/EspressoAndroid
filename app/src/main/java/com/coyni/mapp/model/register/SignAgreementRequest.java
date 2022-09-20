package com.coyni.mapp.model.register;

public class SignAgreementRequest {
    private int agreementType;
    private String token;

    public int getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(int agreementType) {
        this.agreementType = agreementType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

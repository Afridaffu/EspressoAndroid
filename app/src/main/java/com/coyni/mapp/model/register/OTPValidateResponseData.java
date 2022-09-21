package com.coyni.mapp.model.register;

public class OTPValidateResponseData {
    private String email;
    private String phoneNumber;
    private String countryCode;
    private Boolean isTosAgreed;
    private Boolean isPpAgreed;
    private Boolean isSmsVerified;
    private Boolean isEmailVerified;
    private String message;
    private String token;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getTosAgreed() {
        return isTosAgreed;
    }

    public void setTosAgreed(Boolean tosAgreed) {
        isTosAgreed = tosAgreed;
    }

    public Boolean getPpAgreed() {
        return isPpAgreed;
    }

    public void setPpAgreed(Boolean ppAgreed) {
        isPpAgreed = ppAgreed;
    }

    public Boolean getSmsVerified() {
        return isSmsVerified;
    }

    public void setSmsVerified(Boolean smsVerified) {
        isSmsVerified = smsVerified;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

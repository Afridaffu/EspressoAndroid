package com.greenbox.coyni.model.register;

public class EmailResponseData {
    private String userId;
    private String email_status;
    private String emailOtp;
    private String message;
    private Boolean smsVerified;
    private Boolean emailVerified;
    private String authenticationeCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail_status() {
        return email_status;
    }

    public void setEmail_status(String email_status) {
        this.email_status = email_status;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSmsVerified() {
        return smsVerified;
    }

    public void setSmsVerified(Boolean smsVerified) {
        this.smsVerified = smsVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getAuthenticationeCode() {
        return authenticationeCode;
    }

    public void setAuthenticationeCode(String authenticationeCode) {
        this.authenticationeCode = authenticationeCode;
    }
}


package com.greenbox.coyni.model.register;

public class EmailResponseData {
//    private String userId;
//    private String emailStatus;
//    private String emailOtp;
//    private String message;
//    private Boolean smsVerified;
//    private Boolean emailVerified;
//    private String code;
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getEmail_status() {
//        return emailStatus;
//    }
//
//    public void setEmail_status(String emailStatus) {
//        this.emailStatus = emailStatus;
//    }
//
//    public String getEmailOtp() {
//        return emailOtp;
//    }
//
//    public void setEmailOtp(String emailOtp) {
//        this.emailOtp = emailOtp;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public Boolean getSmsVerified() {
//        return smsVerified;
//    }
//
//    public void setSmsVerified(Boolean smsVerified) {
//        this.smsVerified = smsVerified;
//    }
//
//    public Boolean getEmailVerified() {
//        return emailVerified;
//    }
//
//    public void setEmailVerified(Boolean emailVerified) {
//        this.emailVerified = emailVerified;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void getCode(String authenticationeCode) {
//        this.code = authenticationeCode;
//    }

    private int userId;
    private String message;
    private String jwtToken;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}


package com.greenbox.coyni.model.register;

public class SMSValidateData {
    private String phoneNumber;
    private String verificationStatus;
    private String message;
    private String emailOtp;
    private int sendEmailCodeAttempts;
    private Boolean smsVerified;
    private Boolean emailVerified;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public int getSendEmailCodeAttempts() {
        return sendEmailCodeAttempts;
    }

    public void setSendEmailCodeAttempts(int sendEmailCodeAttempts) {
        this.sendEmailCodeAttempts = sendEmailCodeAttempts;
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
}

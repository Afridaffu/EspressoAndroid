package com.coyni.mapp.model.register;

public class SMSResponseData {
    private String phoneNumber;
    private int smsOtpAttempts;
    private String smsOtp;
    private String message;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSmsOtpAttempts() {
        return smsOtpAttempts;
    }

    public void setSmsOtpAttempts(int smsOtpAttempts) {
        this.smsOtpAttempts = smsOtpAttempts;
    }

    public String getSmsOtp() {
        return smsOtp;
    }

    public void setSmsOtp(String smsOtp) {
        this.smsOtp = smsOtp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


package com.coyni.android.model.register;

public class CustomerData {
    private String registrationStatus;
    private String activationLink;
    private String phoneNumber;
    private String smsOtp;
    private String email;
    private String emailOtp;
    private Integer userId;
    private String name;
    private Integer sendSmsCodeAttempts;
    private Integer sendEmailCodeAttempts;

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getActivationLink() {
        return activationLink;
    }

    public void setActivationLink(String activationLink) {
        this.activationLink = activationLink;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSmsOtp() {
        return smsOtp;
    }

    public void setSmsOtp(String smsOtp) {
        this.smsOtp = smsOtp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSendSmsCodeAttempts() {
        return sendSmsCodeAttempts;
    }

    public void setSendSmsCodeAttempts(Integer sendSmsCodeAttempts) {
        this.sendSmsCodeAttempts = sendSmsCodeAttempts;
    }

    public Integer getSendEmailCodeAttempts() {
        return sendEmailCodeAttempts;
    }

    public void setSendEmailCodeAttempts(Integer sendEmailCodeAttempts) {
        this.sendEmailCodeAttempts = sendEmailCodeAttempts;
    }
}

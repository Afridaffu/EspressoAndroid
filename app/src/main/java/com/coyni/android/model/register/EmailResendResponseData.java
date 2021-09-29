package com.coyni.android.model.register;

public class EmailResendResponseData {
    private String email;
    private int emailOtpAttempts;
    private String emailOtp;
    private String message;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEmailOtpAttempts() {
        return emailOtpAttempts;
    }

    public void setEmailOtpAttempts(int emailOtpAttempts) {
        this.emailOtpAttempts = emailOtpAttempts;
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
}

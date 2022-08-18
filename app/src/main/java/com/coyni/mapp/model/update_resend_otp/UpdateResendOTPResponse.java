package com.coyni.mapp.model.update_resend_otp;

import com.coyni.mapp.model.Error;

public class UpdateResendOTPResponse {

    private String status;
    private String timestamp;
    private Dataa data;
    private Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Dataa getData() {
        return data;
    }

    public void setData(Dataa data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Dataa{
        private String phoneNumber;
        private String email;
        private String smsOtpAttempts;
        private String message;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSmsOtpAttempts() {
            return smsOtpAttempts;
        }

        public void setSmsOtpAttempts(String smsOtpAttempts) {
            this.smsOtpAttempts = smsOtpAttempts;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}


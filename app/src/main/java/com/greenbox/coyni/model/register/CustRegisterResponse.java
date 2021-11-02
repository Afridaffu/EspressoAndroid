package com.greenbox.coyni.model.register;

import com.greenbox.coyni.model.Error;

public class CustRegisterResponse {

    private String status;
    private String timestamp;
    private Data data = new Data();
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Data {
        private String accountStatus;
        private String userId = "";
        private String name;
        private String phoneNumber;
        private String email;
        private String smsOtp;
        private String sendSmsCodeAttempts;
        private boolean sms_verified;
        private boolean email_verified;

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

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

        public String getSmsOtp() {
            return smsOtp;
        }

        public void setSmsOtp(String smsOtp) {
            this.smsOtp = smsOtp;
        }

        public String getSendSmsCodeAttempts() {
            return sendSmsCodeAttempts;
        }

        public void setSendSmsCodeAttempts(String sendSmsCodeAttempts) {
            this.sendSmsCodeAttempts = sendSmsCodeAttempts;
        }

        public boolean isSms_verified() {
            return sms_verified;
        }

        public void setSms_verified(boolean sms_verified) {
            this.sms_verified = sms_verified;
        }

        public boolean isEmail_verified() {
            return email_verified;
        }

        public void setEmail_verified(boolean email_verified) {
            this.email_verified = email_verified;
        }
    }

}

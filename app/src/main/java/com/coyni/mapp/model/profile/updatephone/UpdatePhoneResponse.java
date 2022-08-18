package com.coyni.mapp.model.profile.updatephone;

import com.coyni.mapp.model.Error;

public class UpdatePhoneResponse {
    private String status;
    private String timestamp;
    private Data data;
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

    public class Data{
        private String currentphoneNumber;
        private String smsOtpAttempts;
        private String currentPhoneSmsOtp;
        private String trackerId;
        private String message;

        public String getTrackerId() {
            return trackerId;
        }

        public void setTrackerId(String trackerId) {
            this.trackerId = trackerId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSmsOtpAttempts() {
            return smsOtpAttempts;
        }

        public void setSmsOtpAttempts(String smsOtpAttempts) {
            this.smsOtpAttempts = smsOtpAttempts;
        }

        public String getCurrentphoneNumber() {
            return currentphoneNumber;
        }

        public void setCurrentphoneNumber(String currentphoneNumber) {
            this.currentphoneNumber = currentphoneNumber;
        }

        public String getCurrentPhoneSmsOtp() {
            return currentPhoneSmsOtp;
        }

        public void setCurrentPhoneSmsOtp(String currentPhoneSmsOtp) {
            this.currentPhoneSmsOtp = currentPhoneSmsOtp;
        }
    }
}

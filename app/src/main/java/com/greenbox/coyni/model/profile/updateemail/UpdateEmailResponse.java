package com.greenbox.coyni.model.profile.updateemail;

import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.profile.ProfileData;

public class UpdateEmailResponse {
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
        private String email;
        private String emailOtpAttempts;
        private String emailOtp;
        private String trackerId;
        private String message;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmailOtpAttempts() {
            return emailOtpAttempts;
        }

        public void setEmailOtpAttempts(String emailOtpAttempts) {
            this.emailOtpAttempts = emailOtpAttempts;
        }

        public String getEmailOtp() {
            return emailOtp;
        }

        public void setEmailOtp(String emailOtp) {
            this.emailOtp = emailOtp;
        }

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
    }
}

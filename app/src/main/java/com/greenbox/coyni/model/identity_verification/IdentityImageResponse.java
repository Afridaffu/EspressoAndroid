package com.greenbox.coyni.model.identity_verification;


import com.greenbox.coyni.model.Error;

public class IdentityImageResponse {
    private String status;
    private String timestamp;
    private Daata data;
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

    public Daata getData() {
        return data;
    }

    public void setData(Daata data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Daata {
        private String identityUrl;
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getIdentityUrl() {
            return identityUrl;
        }

        public void setIdentityUrl(String identityUrl) {
            this.identityUrl = identityUrl;
        }
    }
}


package com.coyni.mapp.model.identity_verification;


import com.coyni.mapp.model.Error;

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
        private String id;
        private String documentSize;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDocumentSize() {
            return documentSize;
        }

        public void setDocumentSize(String documentSize) {
            this.documentSize = documentSize;
        }
    }
}


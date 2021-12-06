package com.greenbox.coyni.model.identity_verification;


import com.greenbox.coyni.model.Error;

public class IdentityAddressResponse {
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

    public class Daata{
        private String giactResponseCode;
        private String giactResponseName;
        private String giactResponseDescription;

        public String getGiactResponseCode() {
            return giactResponseCode;
        }

        public void setGiactResponseCode(String giactResponseCode) {
            this.giactResponseCode = giactResponseCode;
        }

        public String getGiactResponseName() {
            return giactResponseName;
        }

        public void setGiactResponseName(String giactResponseName) {
            this.giactResponseName = giactResponseName;
        }

        public String getGiactResponseDescription() {
            return giactResponseDescription;
        }

        public void setGiactResponseDescription(String giactResponseDescription) {
            this.giactResponseDescription = giactResponseDescription;
        }
    }
}


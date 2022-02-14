package com.greenbox.coyni.model.biometric;

import com.greenbox.coyni.model.Error;

public class BiometricTokenResponse {
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

    public class Data {
        public String requestToken;

        public String getRequestToken() {
            return requestToken;
        }

        public void setRequestToken(String requestToken) {
            this.requestToken = requestToken;
        }
    }
}

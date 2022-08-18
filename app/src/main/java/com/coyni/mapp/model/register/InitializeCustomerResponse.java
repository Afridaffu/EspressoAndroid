package com.coyni.mapp.model.register;

import com.coyni.mapp.model.Error;

public class InitializeCustomerResponse {
    private String status;
    private String timestamp;
    private InitializeCustomerResponseData data;
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

    public InitializeCustomerResponseData getData() {
        return data;
    }

    public void setData(InitializeCustomerResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class InitializeCustomerResponseData{
        public int userId;
        public String message;
        public String jwtToken;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public void setJwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
        }
    }

}


package com.greenbox.coyni.model.register;

import com.greenbox.coyni.model.Error;

public class EmailResendResponse {
    private String status;
    private String timestamp;
    private EmailResendResponseData data;
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

    public EmailResendResponseData getData() {
        return data;
    }

    public void setData(EmailResendResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


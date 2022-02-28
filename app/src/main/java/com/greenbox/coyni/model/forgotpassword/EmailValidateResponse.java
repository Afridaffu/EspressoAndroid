package com.greenbox.coyni.model.forgotpassword;

import com.greenbox.coyni.model.Error;

public class EmailValidateResponse {
    private String status;
    private String timestamp;
    private EmailValidateResponseData data;
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

    public EmailValidateResponseData getData() {
        return data;
    }

    public void setData(EmailValidateResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


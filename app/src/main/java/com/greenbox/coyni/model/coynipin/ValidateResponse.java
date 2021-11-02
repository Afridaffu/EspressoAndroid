package com.greenbox.coyni.model.coynipin;

import com.greenbox.coyni.model.Error;

public class ValidateResponse {
    private String status;
    private String timestamp;
    private ValidateResponseData data;
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

    public ValidateResponseData getData() {
        return data;
    }

    public void setData(ValidateResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

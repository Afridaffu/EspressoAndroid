package com.coyni.mapp.model.register;

import com.coyni.mapp.model.Error;

public class OTPValidateResponse {
    private String status;
    private String timestamp;
    private OTPValidateResponseData data;
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

    public OTPValidateResponseData getData() {
        return data;
    }

    public void setData(OTPValidateResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

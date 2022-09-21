package com.coyni.mapp.model.register;

import com.coyni.mapp.model.Error;

public class SignAgreementResponse {
    private String status;
    private String timestamp;
    private SignAgreementResponseData data;
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

    public SignAgreementResponseData getData() {
        return data;
    }

    public void setData(SignAgreementResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

package com.coyni.mapp.model.register;

import com.coyni.mapp.model.Error;

public class SMSValidate {
    private String status;
    private String timestamp;
    private SMSValidateData data;
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

    public SMSValidateData getData() {
        return data;
    }

    public void setData(SMSValidateData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

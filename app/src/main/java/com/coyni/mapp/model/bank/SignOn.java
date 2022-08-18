package com.coyni.mapp.model.bank;


import com.coyni.mapp.model.Error;

public class SignOn {
    private String status;
    private String timestamp;
    private SignOnData data;
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

    public SignOnData getData() {
        return data;
    }

    public void setData(SignOnData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


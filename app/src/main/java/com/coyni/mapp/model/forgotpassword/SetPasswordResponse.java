package com.coyni.mapp.model.forgotpassword;

public class SetPasswordResponse {
    private String status;
    private String timestamp;
    private SetPasswordRespData data;
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

    public SetPasswordRespData getData() {
        return data;
    }

    public void setData(SetPasswordRespData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


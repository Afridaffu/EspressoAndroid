package com.coyni.android.model.login;

import com.coyni.android.model.Error;

public class ChangePasswordResponse {
    private String status;
    private String timestamp;
    private ChangePasswordData data;
    private Error error;

    public ChangePasswordData getData() {
        return data;
    }

    public void setData(ChangePasswordData data) {
        this.data = data;
    }

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


    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

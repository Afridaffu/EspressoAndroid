package com.coyni.android.model.register;

import com.coyni.android.model.Error;

public class ForgotPassword {
    private String status;
    private String timestamp;
    private ForgotPasswordResponse data;
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

    public ForgotPasswordResponse getData() {
        return data;
    }

    public void setData(ForgotPasswordResponse data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

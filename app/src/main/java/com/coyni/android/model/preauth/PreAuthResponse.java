package com.coyni.android.model.preauth;

import com.coyni.android.model.Error;

public class PreAuthResponse {
    private String status;
    private String timestamp;
    private PreAuthData data;
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

    public PreAuthData getData() {
        return data;
    }

    public void setData(PreAuthData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

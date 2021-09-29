package com.coyni.android.model.user;

import com.coyni.android.model.Error;

public class PublicKeyResponse {
    private String status;
    private String timestamp;
    private PublicKeyResponseData data;
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

    public PublicKeyResponseData getData() {
        return data;
    }

    public void setData(PublicKeyResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

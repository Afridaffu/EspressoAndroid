package com.coyni.android.model.signet;

import com.coyni.android.model.Error;

public class SignetResponse {
    private String status;
    private String timestamp;
    private SignetResponseData data;
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

    public SignetResponseData getData() {
        return data;
    }

    public void setData(SignetResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

package com.coyni.android.model.kycchecks;

import com.coyni.android.model.Error;

public class KYC_ChecksResponse {
    private String status;
    private String timestamp;
    private KYC_ChecksResponseData data;
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

    public KYC_ChecksResponseData getData() {
        return data;
    }

    public void setData(KYC_ChecksResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

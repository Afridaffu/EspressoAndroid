package com.coyni.android.model.sentrequests;

import com.coyni.android.model.Error;

public class SentRequests {
    private String status;
    private String timestamp;
    private SentRequestsData data;
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

    public SentRequestsData getData() {
        return data;
    }

    public void setData(SentRequestsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

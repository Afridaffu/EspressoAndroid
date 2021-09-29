package com.coyni.android.model.receiverequests;

import com.coyni.android.model.Error;

public class ReceiveRequests {
    private String status;
    private String timestamp;
    private ReceiveRequestsData data;
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

    public ReceiveRequestsData getData() {
        return data;
    }

    public void setData(ReceiveRequestsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

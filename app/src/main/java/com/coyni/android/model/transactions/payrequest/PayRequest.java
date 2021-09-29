package com.coyni.android.model.transactions.payrequest;

import com.coyni.android.model.Error;

public class PayRequest {
    private String status;
    private String timestamp;
    private PayRequestData data;
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

    public PayRequestData getData() {
        return data;
    }

    public void setData(PayRequestData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

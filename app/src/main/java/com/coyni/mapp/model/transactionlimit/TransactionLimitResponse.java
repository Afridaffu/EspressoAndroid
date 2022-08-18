package com.coyni.mapp.model.transactionlimit;

import com.coyni.mapp.model.Error;

public class TransactionLimitResponse {
    private String status;
    private String timestamp;
    private LimitResponseData data;
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

    public LimitResponseData getData() {
        return data;
    }

    public void setData(LimitResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

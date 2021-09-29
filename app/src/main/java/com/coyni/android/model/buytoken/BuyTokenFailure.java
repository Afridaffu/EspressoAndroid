package com.coyni.android.model.buytoken;

import com.coyni.android.model.Error;

public class BuyTokenFailure {
    private String status;
    private String timestamp;
    private BuyTokenFailureData data;
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

    public BuyTokenFailureData getData() {
        return data;
    }

    public void setData(BuyTokenFailureData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

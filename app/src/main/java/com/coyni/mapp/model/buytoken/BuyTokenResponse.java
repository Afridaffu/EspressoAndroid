package com.coyni.mapp.model.buytoken;

import com.coyni.mapp.model.Error;

public class BuyTokenResponse {
    private String status;
    private String timestamp;
    private BuyTokenResponseData data;
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

    public BuyTokenResponseData getData() {
        return data;
    }

    public void setData(BuyTokenResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

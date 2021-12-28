package com.greenbox.coyni.model.withdraw;

import com.greenbox.coyni.model.Error;

public class WithdrawResponse {
    private String status;
    private String timestamp;
    private WithdrawResponseData data;
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

    public WithdrawResponseData getData() {
        return data;
    }

    public void setData(WithdrawResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


package com.coyni.android.model.shuftipro;

import com.coyni.android.model.Error;

public class ShuftiProResponse {
    private String status;
    private String timestamp;
    private ShuftiProResponseData data;
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

    public ShuftiProResponseData getData() {
        return data;
    }

    public void setData(ShuftiProResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

package com.coyni.android.model.exchangerate;

import com.coyni.android.model.Error;

public class ExchangeRate {
    private String status;
    private String timestamp;
    private ExchangeRateData data;
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

    public ExchangeRateData getData() {
        return data;
    }

    public void setData(ExchangeRateData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

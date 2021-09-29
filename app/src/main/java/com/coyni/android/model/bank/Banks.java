package com.coyni.android.model.bank;

import com.coyni.android.model.Error;

public class Banks {
    private String status;
    private String timestamp;
    private BanksData data;
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

    public BanksData getData() {
        return data;
    }

    public void setData(BanksData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

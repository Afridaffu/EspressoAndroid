package com.greenbox.coyni.model.wallet;

public class WalletResponse {
    private String status;
    private String timestamp;
    private WalletData data;
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

    public WalletData getData() {
        return data;
    }

    public void setData(WalletData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


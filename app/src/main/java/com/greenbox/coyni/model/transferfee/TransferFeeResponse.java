package com.greenbox.coyni.model.transferfee;

public class TransferFeeResponse {
    private String status;
    private String timestamp;
    private TransferFeeData data;
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

    public TransferFeeData getData() {
        return data;
    }

    public void setData(TransferFeeData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}


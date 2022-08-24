package com.coyni.mapp.model.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coyni.mapp.model.Error;

public class TransactionDetails {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public TransactionData getData() {
        return data;
    }

    @SerializedName("data")
    @Expose
    private TransactionData data;
    @SerializedName("error")
    @Expose
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


    public void setData(TransactionData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
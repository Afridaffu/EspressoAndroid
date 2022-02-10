package com.greenbox.coyni.model.fee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.Error;

public class Fees {

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("timestamp")
    @Expose
    public String timestamp;
    @SerializedName("data")
    @Expose
    public FeeData data;
    @SerializedName("error")
    @Expose
    public Error error;

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

    public FeeData getData() {
        return data;
    }

    public void setData(FeeData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

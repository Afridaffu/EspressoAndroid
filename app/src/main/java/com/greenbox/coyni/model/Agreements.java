package com.greenbox.coyni.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Agreements {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("data")
    @Expose
    private AgreementsData data;
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

    public AgreementsData getData() {
        return data;
    }

    public void setData(AgreementsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
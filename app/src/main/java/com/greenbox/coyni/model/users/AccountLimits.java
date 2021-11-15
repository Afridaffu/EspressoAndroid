package com.greenbox.coyni.model.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountLimits {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("data")
    @Expose
    private AccountLimitsData data;
    @SerializedName("error")
    @Expose
    private Object error;

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

    public AccountLimitsData getData() {
        return data;
    }

    public void setData(AccountLimitsData data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
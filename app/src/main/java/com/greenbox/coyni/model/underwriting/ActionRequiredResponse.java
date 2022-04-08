package com.greenbox.coyni.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.transaction.TransactionData;

public class ActionRequiredResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("data")
    @Expose
    private ActionRequiredDataResponse data;

    @SerializedName("error")
    @Expose
    private Error error;

    public ActionRequiredDataResponse getData() {
        return data;
    }

    public void setData(ActionRequiredDataResponse data) {
        this.data = data;
    }

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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ActionRequiredResponse{" +
                "status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}


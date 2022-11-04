package com.coyni.mapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SignAgreementsResp {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("data")
    @Expose
    private List<SignAgreementData> data = new ArrayList<>();

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

    public List<SignAgreementData> getData() {
        return data;
    }

    public void setData(List<SignAgreementData> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
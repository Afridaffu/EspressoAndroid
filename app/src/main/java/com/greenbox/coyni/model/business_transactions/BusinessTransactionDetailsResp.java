package com.greenbox.coyni.model.business_transactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.transaction.TransactionData;

public class BusinessTransactionDetailsResp{
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("data")
    @Expose
    private BusinessTransactionDetailsData data;
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

    public BusinessTransactionDetailsData getData() {
        return data;
    }

    public void setData(BusinessTransactionDetailsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }


}

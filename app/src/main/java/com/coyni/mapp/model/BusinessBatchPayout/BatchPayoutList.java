package com.coyni.mapp.model.BusinessBatchPayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coyni.mapp.model.Error;
import com.coyni.mapp.model.transaction.TransactionListData;

public class BatchPayoutList {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("data")
    @Expose
    private TransactionListData data;
    @SerializedName("error")
    @Expose
    private Error error;
}

package com.coyni.mapp.model.BusinessBatchPayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coyni.mapp.model.BaseResponse;

public class BatchPayoutListResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private BatchPayoutListData data;

    public BatchPayoutListData getData() {
        return data;
    }

    public void setData(BatchPayoutListData data) {
        this.data = data;
    }
}

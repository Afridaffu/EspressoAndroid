package com.greenbox.coyni.model.BusinessBatchPayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.BaseResponse;
import com.greenbox.coyni.model.Error;

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

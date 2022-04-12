package com.greenbox.coyni.model.BatchNow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.BaseResponse;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutIdDetailsData;

public class BatchNowResponse extends BaseResponse {

    private BatchNowData data;

    public BatchNowData getData() {
        return data;
    }

    public void setData(BatchNowData data) {
        this.data = data;
    }
}

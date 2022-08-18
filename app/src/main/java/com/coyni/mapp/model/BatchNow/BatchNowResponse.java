package com.coyni.mapp.model.BatchNow;

import com.coyni.mapp.model.BaseResponse;

public class BatchNowResponse extends BaseResponse {

    private BatchNowData data;

    public BatchNowData getData() {
        return data;
    }

    public void setData(BatchNowData data) {
        this.data = data;
    }
}

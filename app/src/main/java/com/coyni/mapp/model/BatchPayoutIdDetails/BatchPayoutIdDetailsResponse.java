package com.coyni.mapp.model.BatchPayoutIdDetails;

import com.coyni.mapp.model.BaseResponse;

public class BatchPayoutIdDetailsResponse extends BaseResponse {

    private BatchPayoutIdDetailsData data;

    public BatchPayoutIdDetailsData getData() {
        return data;
    }

    public void setData(BatchPayoutIdDetailsData data) {
        this.data = data;
    }

}

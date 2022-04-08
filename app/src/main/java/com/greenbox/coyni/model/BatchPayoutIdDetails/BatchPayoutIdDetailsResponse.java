package com.greenbox.coyni.model.BatchPayoutIdDetails;

import com.greenbox.coyni.model.BaseResponse;

public class BatchPayoutIdDetailsResponse extends BaseResponse {

    private BatchPayoutIdDetailsData data;

    public BatchPayoutIdDetailsData getData() {
        return data;
    }

    public void setData(BatchPayoutIdDetailsData data) {
        this.data = data;
    }

}

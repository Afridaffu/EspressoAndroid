package com.coyni.mapp.model.business_id_verification;

import com.coyni.mapp.model.BaseResponse;

public class CancelApplicationResponse extends BaseResponse {

    private ApplicationCancelledData data;

    public ApplicationCancelledData getData() {
        return data;
    }

    public void setData(ApplicationCancelledData data) {
        this.data = data;
    }
}

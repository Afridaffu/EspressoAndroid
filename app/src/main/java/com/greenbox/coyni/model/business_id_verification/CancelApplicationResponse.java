package com.greenbox.coyni.model.business_id_verification;

import com.greenbox.coyni.model.BaseResponse;

public class CancelApplicationResponse extends BaseResponse {

    private ApplicationCancelledData data;

    public ApplicationCancelledData getData() {
        return data;
    }

    public void setData(ApplicationCancelledData data) {
        this.data = data;
    }
}

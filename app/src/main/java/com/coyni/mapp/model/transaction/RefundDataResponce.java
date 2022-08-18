package com.coyni.mapp.model.transaction;

import com.coyni.mapp.model.BaseResponse;

public class RefundDataResponce extends BaseResponse {

    private RefundData data;

    public RefundData getData() {
        return data;
    }

    public void setData(RefundData data) {
        this.data = data;
    }

}

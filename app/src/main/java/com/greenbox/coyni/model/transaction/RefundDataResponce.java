package com.greenbox.coyni.model.transaction;

import com.greenbox.coyni.model.BaseResponse;

public class RefundDataResponce extends BaseResponse {

    private RefundData data;

    public RefundData getData() {
        return data;
    }

    public void setData(RefundData data) {
        this.data = data;
    }

}

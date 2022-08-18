package com.coyni.mapp.model.paidorder;

import com.coyni.mapp.model.BaseResponse;

public class PaidOrderResp extends BaseResponse {

    private PaidResponseData data;


    public PaidResponseData getPaidResponseData() {
        return data;
    }

    public void setPaidResponseData(PaidResponseData paidResponseData) {
        this.data = paidResponseData;
    }
}

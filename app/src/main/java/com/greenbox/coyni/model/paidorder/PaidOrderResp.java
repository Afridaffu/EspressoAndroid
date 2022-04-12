package com.greenbox.coyni.model.paidorder;

import com.greenbox.coyni.model.BaseResponse;

public class PaidOrderResp extends BaseResponse {

    private PaidResponseData data;


    public PaidResponseData getPaidResponseData() {
        return data;
    }

    public void setPaidResponseData(PaidResponseData paidResponseData) {
        this.data = paidResponseData;
    }
}

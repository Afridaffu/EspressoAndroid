package com.greenbox.coyni.model.paidorder;

import com.greenbox.coyni.model.BaseResponse;

public class PaidOrderResp extends BaseResponse {

    private PaidResponseData paidResponseData;


    public PaidResponseData getPaidResponseData() {
        return paidResponseData;
    }

    public void setPaidResponseData(PaidResponseData paidResponseData) {
        this.paidResponseData = paidResponseData;
    }
}

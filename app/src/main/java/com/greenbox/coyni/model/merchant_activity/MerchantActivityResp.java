package com.greenbox.coyni.model.merchant_activity;

import com.greenbox.coyni.model.BaseResponse;

public class MerchantActivityResp extends BaseResponse {

    private MerchantActivityData data;

    public MerchantActivityData getData() {
        return data;
    }

    public void setData(MerchantActivityData data) {
        this.data = data;
    }
}

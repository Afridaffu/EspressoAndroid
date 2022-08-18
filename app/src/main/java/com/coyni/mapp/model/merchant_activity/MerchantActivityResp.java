package com.coyni.mapp.model.merchant_activity;

import com.coyni.mapp.model.BaseResponse;

public class MerchantActivityResp extends BaseResponse {

    private MerchantActivityData data;

    public MerchantActivityData getData() {
        return data;
    }

    public void setData(MerchantActivityData data) {
        this.data = data;
    }
}

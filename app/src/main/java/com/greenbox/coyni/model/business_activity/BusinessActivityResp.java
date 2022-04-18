package com.greenbox.coyni.model.business_activity;

import com.greenbox.coyni.model.BaseResponse;

public class BusinessActivityResp extends BaseResponse {

    private BusinessActivityData data;

    public BusinessActivityData getData() {
        return data;
    }

    public void setData(BusinessActivityData data) {
        this.data = data;
    }

}

package com.coyni.mapp.model.business_activity;

import com.coyni.mapp.model.BaseResponse;

import java.util.List;

public class BusinessActivityResp extends BaseResponse {

    private List<BusinessActivityData> data;

    public List<BusinessActivityData> getData() {
        return data;
    }

    public void setData(List<BusinessActivityData> data) {
        this.data = data;
    }

}

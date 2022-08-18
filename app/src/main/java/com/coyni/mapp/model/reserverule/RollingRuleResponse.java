package com.coyni.mapp.model.reserverule;

import com.coyni.mapp.model.BaseResponse;

public class RollingRuleResponse extends BaseResponse {

    private RollingData data;

    public RollingData getData() {
        return data;
    }

    public void setData(RollingData data) {
        this.data = data;
    }

}
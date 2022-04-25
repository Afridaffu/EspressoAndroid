package com.greenbox.coyni.model.reserverule;

import com.greenbox.coyni.model.BaseResponse;

public class RollingRuleResponse extends BaseResponse {

    private RollingData data;

    public RollingData getData() {
        return data;
    }

    public void setData(RollingData data) {
        this.data = data;
    }

}
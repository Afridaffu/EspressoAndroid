package com.greenbox.coyni.model.reserverolling;

import com.greenbox.coyni.model.BaseResponse;

public class RollingListResponse extends BaseResponse {

    private RollingData data;

    public RollingData getData() {
        return data;
    }

    public void setData(RollingData data) {
        this.data = data;
    }

}
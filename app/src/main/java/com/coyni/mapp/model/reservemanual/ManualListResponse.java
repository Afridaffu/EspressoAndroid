package com.coyni.mapp.model.reservemanual;

import com.coyni.mapp.model.BaseResponse;

public class ManualListResponse extends BaseResponse {

    private ManualData data;

    public ManualData getData() {
        return data;
    }

    public void setData(ManualData data) {
        this.data = data;
    }
}
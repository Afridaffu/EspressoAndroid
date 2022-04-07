package com.greenbox.coyni.model.reservemanual;

import com.greenbox.coyni.model.BaseResponse;

public class ManualListResponse extends BaseResponse {

    private ManualData data;

    public ManualData getData() {
        return data;
    }

    public void setData(ManualData data) {
        this.data = data;
    }
}
package com.coyni.mapp.model.activtity_log;

import com.coyni.mapp.model.BaseResponse;

import java.util.List;

public class ActivityLogResp extends BaseResponse {

    private List<AcitivityData> data;

    public List<AcitivityData> getData() {
        return data;
    }

    public void setData(List<AcitivityData> data) {
        this.data = data;
    }
}

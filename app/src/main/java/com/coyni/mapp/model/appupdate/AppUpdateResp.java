package com.coyni.mapp.model.appupdate;

import com.coyni.mapp.model.BaseResponse;

public class AppUpdateResp extends BaseResponse {
    private AppUpdateData data;

    public AppUpdateData getData() {
        return data;
    }

    public void setData(AppUpdateData data) {
        this.data = data;
    }

}

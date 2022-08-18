package com.coyni.mapp.model.deviceintialize;

import com.coyni.mapp.model.BaseResponse;

public class DeviceInitializeResponse extends BaseResponse {

    private DeviceInitializeData data;

    public DeviceInitializeData getData() {
        return data;
    }

    public void setData(DeviceInitializeData data) {
        this.data = data;
    }
}



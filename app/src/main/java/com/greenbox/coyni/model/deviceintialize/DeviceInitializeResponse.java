package com.greenbox.coyni.model.deviceintialize;

import com.greenbox.coyni.model.BaseResponse;

public class DeviceInitializeResponse extends BaseResponse {

    private DeviceInitializeData data;

    public DeviceInitializeData getData() {
        return data;
    }

    public void setData(DeviceInitializeData data) {
        this.data = data;
    }
}



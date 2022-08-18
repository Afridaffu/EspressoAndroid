package com.coyni.mapp.model.logout;

import com.coyni.mapp.model.BaseResponse;

public class LogoutResponse extends BaseResponse {

    private LogoutData data;

    public LogoutData getData() {
        return data;
    }

    public void setData(LogoutData data) {
        this.data = data;
    }
}

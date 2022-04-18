package com.greenbox.coyni.model.logout;

import com.greenbox.coyni.model.BaseResponse;

public class LogoutResponse extends BaseResponse {

    private LogoutData data;

    public LogoutData getData() {
        return data;
    }

    public void setData(LogoutData data) {
        this.data = data;
    }
}

package com.coyni.mapp.model.signin;


import com.coyni.mapp.model.BaseResponse;

public class InitializeResponse extends BaseResponse {

    private InitializeResponseData data;

    public InitializeResponseData getData() {
        return data;
    }

    public void setData(InitializeResponseData data) {
        this.data = data;
    }
}

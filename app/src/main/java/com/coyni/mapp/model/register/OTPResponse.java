package com.coyni.mapp.model.register;

import com.coyni.mapp.model.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private OTPResponseData data;

    public OTPResponseData getData() {
        return data;
    }

    public void setData(OTPResponseData data) {
        this.data = data;
    }
}

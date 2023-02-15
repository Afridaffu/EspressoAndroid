package com.coyni.pos.app.model.login;

import com.coyni.pos.app.model.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BiometricSignIn extends BaseResponse {

    @SerializedName("data")
    @Expose
    private BiometricSignInData data;

    public BiometricSignInData getData() {
        return data;
    }

    public void setData(BiometricSignInData data) {
        this.data = data;
    }


}
package com.coyni.mapp.model.bank;

import com.coyni.mapp.model.BaseResponse;

public class ManualBankResponse extends BaseResponse {
    private ManualBankResponseData data;

    public ManualBankResponseData getData() {
        return data;
    }

    public void setData(ManualBankResponseData data) {
        this.data = data;
    }
}

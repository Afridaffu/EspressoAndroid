package com.coyni.mapp.model.coynipin;

import com.coyni.mapp.model.BaseResponse;

public class StepUpOTPResponse extends BaseResponse {
    private StepUpOTPResponseData data;

    public StepUpOTPResponseData getData() {
        return data;
    }

    public void setData(StepUpOTPResponseData data) {
        this.data = data;
    }
}

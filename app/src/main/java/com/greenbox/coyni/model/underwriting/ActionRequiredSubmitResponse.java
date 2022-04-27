package com.greenbox.coyni.model.underwriting;

import com.greenbox.coyni.model.BaseResponse;

public class ActionRequiredSubmitResponse extends BaseResponse {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ActionRequiredResponse{" +
                "status='" + getStatus() + '\'' +
                ", timestamp='" + getTimestamp() + '\'' +
                ", data=" + data +
                ", error=" + getError() +
                '}';
    }
}

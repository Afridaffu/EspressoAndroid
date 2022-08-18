package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coyni.mapp.model.BaseResponse;

public class ActionRequiredResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ActionRequiredDataResponse data;

    public ActionRequiredDataResponse getData() {
        return data;
    }

    public void setData(ActionRequiredDataResponse data) {
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


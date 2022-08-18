package com.coyni.mapp.model.DashboardReserveList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coyni.mapp.model.BaseResponse;

public class ReserveListResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ReserveListData data;

    public ReserveListData getData() {
        return data;
    }

    public void setData(ReserveListData data) {
        this.data = data;
    }
}

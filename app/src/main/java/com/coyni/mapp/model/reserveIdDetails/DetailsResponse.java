package com.coyni.mapp.model.reserveIdDetails;

import com.coyni.mapp.model.BaseResponse;

public class DetailsResponse extends BaseResponse {

    public DetailsData data;

    public DetailsData getData() {
        return data;
    }

    public void setData(DetailsData data) {
        this.data = data;
    }
}
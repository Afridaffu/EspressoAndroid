package com.greenbox.coyni.model.reserveIdDetails;

import com.greenbox.coyni.model.BaseResponse;

public class DetailsResponse extends BaseResponse {

    public DetailsData data;

    public DetailsData getData() {
        return data;
    }

    public void setData(DetailsData data) {
        this.data = data;
    }
}
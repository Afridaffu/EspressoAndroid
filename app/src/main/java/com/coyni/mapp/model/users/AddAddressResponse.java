package com.coyni.mapp.model.users;

import com.coyni.mapp.model.BaseResponse;

public class AddAddressResponse extends BaseResponse {
    private AddAddressResponseData data;

    public AddAddressResponseData getData() {
        return data;
    }

    public void setData(AddAddressResponseData data) {
        this.data = data;
    }
}

package com.greenbox.coyni.model.check_out_transactions;

import com.greenbox.coyni.model.BaseResponse;

public class OrderPayResponse extends BaseResponse {

    private OrderPayData data;

    public OrderPayData getData() {
        return data;
    }

    public void setData(OrderPayData data) {
        this.data = data;
    }
}

package com.greenbox.coyni.model.check_out_transactions;

import com.greenbox.coyni.model.BaseResponse;

public class OrderInfoResponse extends BaseResponse {
    private OrderInfoData data;

    public OrderInfoData getData() {
        return data;
    }

    public void setData(OrderInfoData data) {
        this.data = data;
    }
}

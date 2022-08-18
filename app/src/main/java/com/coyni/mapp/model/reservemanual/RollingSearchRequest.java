package com.coyni.mapp.model.reservemanual;

import com.coyni.mapp.model.SearchKeyRequest;

public class RollingSearchRequest extends SearchKeyRequest {

    private int payoutType;

    public int getPayoutType() {
        return payoutType;
    }

    public void setPayoutType(int payoutType) {
        this.payoutType = payoutType;
    }
}

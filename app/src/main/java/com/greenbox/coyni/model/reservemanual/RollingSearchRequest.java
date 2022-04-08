package com.greenbox.coyni.model.reservemanual;

import com.greenbox.coyni.model.SearchKeyRequest;

public class RollingSearchRequest extends SearchKeyRequest {

    private int payoutType;

    public int getPayoutType() {
        return payoutType;
    }

    public void setPayoutType(int payoutType) {
        this.payoutType = payoutType;
    }
}

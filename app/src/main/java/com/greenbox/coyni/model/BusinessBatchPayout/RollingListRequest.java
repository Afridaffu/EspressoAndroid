package com.greenbox.coyni.model.BusinessBatchPayout;

import java.util.ArrayList;

public class RollingListRequest {

    private int payoutType;

    private ArrayList<Integer> status = new ArrayList<>();

    public ArrayList<Integer> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<Integer> status) {
        this.status = status;
    }

    public int getPayoutType() {
        return payoutType;
    }

    public void setPayoutType(int payoutType) {
        this.payoutType = payoutType;
    }
}

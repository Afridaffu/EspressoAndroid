package com.coyni.mapp.model.BatchNow;

import java.util.ArrayList;

public class BatchNowRequest {

    private int payoutType;
    private ArrayList<Integer> status;

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

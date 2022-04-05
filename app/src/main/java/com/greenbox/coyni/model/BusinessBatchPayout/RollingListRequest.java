package com.greenbox.coyni.model.BusinessBatchPayout;

import java.util.ArrayList;

public class RollingListRequest {

    private ArrayList<Integer> status = new ArrayList<>();

    public ArrayList<Integer> getStatus() {
        return status;
    }

    public void setRollingStatus(ArrayList<Integer> status) {
        this.status = status;
    }
}

package com.greenbox.coyni.model.merchant_activity;

public class MerchantActivityRequest {

    private String duration;
    private String endDate;
    private String startDate;
    private String userId;


    public void setDuration(String duration) {
        this.duration = duration;
    }


    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }
}

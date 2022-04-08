package com.greenbox.coyni.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReseveRuleData {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("merchantId")
    @Expose
    private int merchantId;

    @SerializedName("dbaName")
    @Expose
    private String dbaName;

    @SerializedName("reserveAmount")
    @Expose
    private Double reserveAmount;

    @SerializedName("reservePeriod")
    @Expose
    private int reservePeriod;

    @SerializedName("reserveReason")
    @Expose
    private String reserveReason;

    @SerializedName("isAccepted")
    @Expose
    private boolean isAccepted;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("reserveType")
    @Expose
    private String reserveType;

    @SerializedName("monthlyProcessingVolume")
    @Expose
    private String monthlyProcessingVolume;

     @SerializedName("highTicket")
    @Expose
    private String highTicket;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public Double getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(Double reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public int getReservePeriod() {
        return reservePeriod;
    }

    public void setReservePeriod(int reservePeriod) {
        this.reservePeriod = reservePeriod;
    }

    public String getReserveReason() {
        return reserveReason;
    }

    public void setReserveReason(String reserveReason) {
        this.reserveReason = reserveReason;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReserveType() {
        return reserveType;
    }

    public void setReserveType(String reserveType) {
        this.reserveType = reserveType;
    }

    public String getMonthlyProcessingVolume() {
        return monthlyProcessingVolume;
    }

    public void setMonthlyProcessingVolume(String monthlyProcessingVolume) {
        this.monthlyProcessingVolume = monthlyProcessingVolume;
    }

    public String getHighTicket() {
        return highTicket;
    }

    public void setHighTicket(String highTicket) {
        this.highTicket = highTicket;
    }
}

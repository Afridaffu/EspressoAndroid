package com.greenbox.coyni.model.reserverolling;

public class RollingItem {

    private String batchId;

    private String status;

    private String createdAt;

    private String totalTransactionsCount;

    private String totalFee;

    private String totalAmount;

    private String sentTo;

    private String reserve;

    private String reserveId;

    private String scheduledRelease;

    private int reserveAmount;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTotalTransactionsCount() {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount(String totalTransactionsCount) {
        this.totalTransactionsCount = totalTransactionsCount;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public String getScheduledRelease() {
        return scheduledRelease;
    }

    public void setScheduledRelease(String scheduledRelease) {
        this.scheduledRelease = scheduledRelease;
    }

    public int getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(int reserveAmount) {
        this.reserveAmount = reserveAmount;
    }
}

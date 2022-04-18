package com.greenbox.coyni.model.BusinessBatchPayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.ListItem;
import com.greenbox.coyni.model.transaction.TransactionListPosted;

import java.io.Serializable;
import java.util.List;

public class BatchPayoutListItems implements Serializable, Comparable<BatchPayoutListItems> {
    @SerializedName("batchId")
    @Expose
    private String batchId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("totalTransactionsCount")
    @Expose
    private String totalTransactionsCount;
    @SerializedName("totalFee")
    @Expose
    private String totalFee;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;
    @SerializedName("sentTo")
    @Expose
    private String sentTo;
    @SerializedName("reserve")
    @Expose
    private String scheduledRelease;

    private String reserveAmount;

    private String processType;

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getScheduledRelease() {
        return scheduledRelease;
    }

    public void setScheduledRelease(String scheduledRelease) {
        this.scheduledRelease = scheduledRelease;
    }

    public String getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(String reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public BatchPayoutListItems(List<BatchPayoutListItems> items) {
    }
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

    @Override
    public int compareTo(BatchPayoutListItems batchPayoutListItems) {
        return createdAt.compareTo(batchPayoutListItems.createdAt);
//        return getTotalAmount().compareTo(batchPayoutListItems.totalAmount);
    }
}

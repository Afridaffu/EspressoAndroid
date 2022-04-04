package com.greenbox.coyni.model.BusinessBatchPayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.ListItem;
import com.greenbox.coyni.model.transaction.TransactionListPosted;

import java.io.Serializable;
import java.util.List;

public class BatchPayoutListItems extends ListItem implements Serializable, Comparable<BatchPayoutListItems> {
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
    private String reserve;

    public BatchPayoutListItems(List<BatchPayoutListItems> items) {
    }
    private int type = ListItem.TYPE_GENERAL;

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


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compareTo(BatchPayoutListItems batchPayoutListItems) {
        return createdAt.compareTo(batchPayoutListItems.createdAt);
//        return getTotalAmount().compareTo(batchPayoutListItems.totalAmount);
    }
}

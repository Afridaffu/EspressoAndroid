package com.coyni.mapp.model.buytoken;

public class BuyTokenResponseData {
    private String gbxTransactionId;
    private Double fee;
    private String status;
    private String message;
    private String description;
    private String type;
    private int descriptorId;
    private String descriptorName;
    private String lastFour;
    private String firstSix;

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDescriptorId() {
        return descriptorId;
    }

    public void setDescriptorId(int descriptorId) {
        this.descriptorId = descriptorId;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public String getFirstSix() {
        return firstSix;
    }

    public void setFirstSix(String firstSix) {
        this.firstSix = firstSix;
    }
}

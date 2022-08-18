package com.coyni.mapp.model.transferfee;

public class TransferFeeData {
    private double fee;
    private double feeInAmount;
    private double feeInPercentage;

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getFeeInAmount() {
        return feeInAmount;
    }

    public void setFeeInAmount(double feeInAmount) {
        this.feeInAmount = feeInAmount;
    }

    public double getFeeInPercentage() {
        return feeInPercentage;
    }

    public void setFeeInPercentage(double feeInPercentage) {
        this.feeInPercentage = feeInPercentage;
    }
}


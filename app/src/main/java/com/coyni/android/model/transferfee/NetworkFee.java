package com.coyni.android.model.transferfee;

public class NetworkFee {
    private double networkFee;
    private double processingFeeNetworkFee;
    private int gasPrice;
    private int gasLimit;
    private int processingFeeGasPrice;
    private int processingFeeGasLimit;

    public double getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(double networkFee) {
        this.networkFee = networkFee;
    }

    public double getProcessingFeeNetworkFee() {
        return processingFeeNetworkFee;
    }

    public void setProcessingFeeNetworkFee(double processingFeeNetworkFee) {
        this.processingFeeNetworkFee = processingFeeNetworkFee;
    }

    public int getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }

    public int getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(int gasLimit) {
        this.gasLimit = gasLimit;
    }

    public int getProcessingFeeGasPrice() {
        return processingFeeGasPrice;
    }

    public void setProcessingFeeGasPrice(int processingFeeGasPrice) {
        this.processingFeeGasPrice = processingFeeGasPrice;
    }

    public int getProcessingFeeGasLimit() {
        return processingFeeGasLimit;
    }

    public void setProcessingFeeGasLimit(int processingFeeGasLimit) {
        this.processingFeeGasLimit = processingFeeGasLimit;
    }
}

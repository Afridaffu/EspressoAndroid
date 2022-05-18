package com.greenbox.coyni.model.check_out_transactions;

public class CheckOutModel {

    private boolean isCheckOutFlag;
    private String checkOutWalletId;
    private String checkOutAmount;

    public boolean isCheckOutFlag() {
        return isCheckOutFlag;
    }

    public void setCheckOutFlag(boolean checkOutFlag) {
        isCheckOutFlag = checkOutFlag;
    }

    public String getCheckOutWalletId() {
        return checkOutWalletId;
    }

    public void setCheckOutWalletId(String checkOutWalletId) {
        this.checkOutWalletId = checkOutWalletId;
    }

    public String getCheckOutAmount() {
        return checkOutAmount;
    }

    public void setCheckOutAmount(String checkOutAmount) {
        this.checkOutAmount = checkOutAmount;
    }
}

package com.coyni.mapp.model.businesswallet;

public class WalletRequest {
    private String walletType;
    private String userId;

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

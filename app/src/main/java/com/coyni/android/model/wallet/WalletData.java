package com.coyni.android.model.wallet;

import java.util.List;

public class WalletData {
    private Double totalWalletsBalance;
    private List<WalletInfo> walletInfo;

    public Double getTotalWalletsBalance() {
        return totalWalletsBalance;
    }

    public void setTotalWalletsBalance(Double totalWalletsBalance) {
        this.totalWalletsBalance = totalWalletsBalance;
    }

    public List<WalletInfo> getWalletInfo() {
        return walletInfo;
    }

    public void setWalletInfo(List<WalletInfo> walletInfo) {
        this.walletInfo = walletInfo;
    }
}

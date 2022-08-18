package com.coyni.mapp.model.businesswallet;

import java.util.List;

public class WalletResponseData {
    private List<WalletInfo> walletInfo;

    public List<WalletInfo> getWalletNames() {
        return walletInfo;
    }

    public void setWalletNames(List<WalletInfo> walletNames) {
        this.walletInfo = walletNames;
    }
}

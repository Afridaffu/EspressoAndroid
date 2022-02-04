package com.greenbox.coyni.model.businesswallet;

import java.util.List;

public class WalletResponseData {
    private List<WalletName> walletNames;

    public List<WalletName> getWalletNames() {
        return walletNames;
    }

    public void setWalletNames(List<WalletName> walletNames) {
        this.walletNames = walletNames;
    }
}

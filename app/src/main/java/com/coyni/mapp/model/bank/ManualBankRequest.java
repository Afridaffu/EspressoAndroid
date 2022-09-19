package com.coyni.mapp.model.bank;

public class ManualBankRequest {
    private String accountName;
    private String accountNumber;
    private Boolean giactReq;
    private String routingNumber;
    private int bankId;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getGiactReq() {
        return giactReq;
    }

    public void setGiactReq(Boolean giactReq) {
        this.giactReq = giactReq;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }
}


package com.coyni.mapp.model.bank;

public class ManualBankResponseData {
    private String bankName;
    private String id;
    private String message;
    private String routingNumber;
    private String accountReponseCodeDescription;
    private String accountName;
    private Boolean isGiactFail;
    private String accountResponseCodeName;
    private String accountNumber;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        bankName = bankName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getAccountReponseCodeDescription() {
        return accountReponseCodeDescription;
    }

    public void setAccountReponseCodeDescription(String accountReponseCodeDescription) {
        this.accountReponseCodeDescription = accountReponseCodeDescription;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Boolean getGiactFail() {
        return isGiactFail;
    }

    public void setGiactFail(Boolean giactFail) {
        isGiactFail = giactFail;
    }

    public String getAccountResponseCodeName() {
        return accountResponseCodeName;
    }

    public void setAccountResponseCodeName(String accountResponseCodeName) {
        this.accountResponseCodeName = accountResponseCodeName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}

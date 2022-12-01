package com.coyni.mapp.model.actionRqrd;

import com.coyni.mapp.model.underwriting.AdditionalDocumentData;
import com.coyni.mapp.model.underwriting.InformationChangeData;
import com.coyni.mapp.model.underwriting.ReseveRuleData;
import com.coyni.mapp.model.underwriting.WebsiteChangeData;

import java.util.List;

public class BankRequest {
    private String accountName;
    private String accountNumber;
    private int bankId;
    private boolean giactReq;
    private String routingNumber;

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

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public boolean isGiactReq() {
        return giactReq;
    }

    public void setGiactReq(boolean giactReq) {
        this.giactReq = giactReq;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }
}

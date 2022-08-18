package com.coyni.mapp.model.summary;


import java.util.List;

public class Data {

    private CompanyInfo companyInfo;

    private DbaInfo dbaInfo;

    private List<BeneficialOwnerInfo> beneficialOwnerInfo = null;

    private List<BankAccount> bankaccount;

    private Agreements agreements;

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }

    public DbaInfo getDbaInfo() {
        return dbaInfo;
    }

    public void setDbaInfo(DbaInfo dbaInfo) {
        this.dbaInfo = dbaInfo;
    }

    public List<BeneficialOwnerInfo> getBeneficialOwnerInfo() {
        return beneficialOwnerInfo;
    }

    public void setBeneficialOwnerInfo(List<BeneficialOwnerInfo> beneficialOwnerInfo) {
        this.beneficialOwnerInfo = beneficialOwnerInfo;
    }

    public List<BankAccount> getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(List<BankAccount> bankaccount) {
        this.bankaccount = bankaccount;
    }

    public Agreements getAgreements() {
        return agreements;
    }

    public void setAgreements(Agreements agreements) {
        this.agreements = agreements;
    }

}
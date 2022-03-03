package com.greenbox.coyni.model.submit;

import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.summary.Agreements;
import com.greenbox.coyni.model.summary.Bankaccount;
import com.greenbox.coyni.model.summary.BeneficialOwnerInfo;
import com.greenbox.coyni.model.summary.RequiredDocument;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSubmitRequest {


    //CompanyInfo
    private String companyAddressLine1 = "";
    private String companyAddressLine2 = "";

    private String companyName = "";
    private String companyEmail = "";
    private PhNoWithCountryCode companyPhoneNumberDto = new PhNoWithCountryCode();
    private String companyBusinessEntity = "";
    private String companySsnOrEin = "";
    private int companyIdentificationType = 0; // Here Identification Type is SSN(11) or EIN/TIN(10)


    private List<RequiredDocument> requiredDocuments;
//DbInfo

    private String dbAddressLine1 = "";
    private String dbAddressLine2 = "";

    private String dbName = "";
    private String dbEmail = "";
    private PhNoWithCountryCode dbPhoneNumberDto = new PhNoWithCountryCode();
    private String dbBusinessType = "";

    private List<Object> requiredDocuments1 = new ArrayList<>();


    public List<Object> getRequiredDocuments1() {
        return requiredDocuments1;
    }

    public void setRequiredDocuments1(List<Object> requiredDocuments1) {
        this.requiredDocuments1 = requiredDocuments1;
    }


    public String getDbIdentificationType() {
        return dbIdentificationType;
    }

    public void setDbIdentificationType(String dbIdentificationType) {
        this.dbIdentificationType = dbIdentificationType;
    }

    public String getDbAverageTicket() {
        return dbAverageTicket;
    }

    public void setDbAverageTicket(String dbAverageTicket) {
        this.dbAverageTicket = dbAverageTicket;
    }

    public String getDbHighTicket() {
        return dbHighTicket;
    }

    public void setDbHighTicket(String dbHighTicket) {
        this.dbHighTicket = dbHighTicket;
    }

    public String getDbMonthlyProcessingVolume() {
        return dbMonthlyProcessingVolume;
    }

    public void setDbMonthlyProcessingVolume(String dbMonthlyProcessingVolume) {
        this.dbMonthlyProcessingVolume = dbMonthlyProcessingVolume;
    }

    private String dbIdentificationType; // Here Identification Type is ECOMMERCE (9) or RETAIL(8)
    private String dbAverageTicket;
    private String dbHighTicket;
    private String dbMonthlyProcessingVolume;
    private boolean dbCopyCompanyInfo = false;
    private String dbTimezone = "";
    private String dbWebsite = "";

//Bank Info


    private Bankaccount bankAccountsDataModel;

    public Bankaccount getData() {
        return bankAccountsDataModel;
    }

    public void setData(Bankaccount data) {
        this.bankAccountsDataModel = data;
    }


    //Benificial


    private List<BeneficialOwnerInfo> beneficialOwnerInfo;

    public List<BeneficialOwnerInfo> getBeneficialOwnerInfo() {
        return beneficialOwnerInfo;
    }

    public void setBeneficialOwnerInfo(List<BeneficialOwnerInfo> beneficialOwnerInfo) {
        this.beneficialOwnerInfo = beneficialOwnerInfo;
    }


    //Agreements
    private Agreements data;

    public void setData(Agreements data) {
        this.data = data;
    }

    public boolean isAgree() {
        return isAgree;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }

    //
    private boolean isAgree;

    public String getCompanyAddressLine1() {
        return companyAddressLine1;
    }

    public void setCompanyAddressLine1(String companyAddressLine1) {
        this.companyAddressLine1 = companyAddressLine1;
    }

    public String getCompanyAddressLine2() {
        return companyAddressLine2;
    }

    public void setCompanyAddressLine2(String companyAddressLine2) {
        this.companyAddressLine2 = companyAddressLine2;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public PhNoWithCountryCode getCompanyPhoneNumberDto() {
        return companyPhoneNumberDto;
    }

    public void setCompanyPhoneNumberDto(PhNoWithCountryCode companyPhoneNumberDto) {
        this.companyPhoneNumberDto = companyPhoneNumberDto;
    }

    public String getCompanyBusinessEntity() {
        return companyBusinessEntity;
    }

    public void setCompanyBusinessEntity(String companyBusinessEntity) {
        this.companyBusinessEntity = companyBusinessEntity;
    }

    public String getCompanySsnOrEin() {
        return companySsnOrEin;
    }

    public void setCompanySsnOrEin(String companySsnOrEin) {
        this.companySsnOrEin = companySsnOrEin;
    }

    public int getCompanyIdentificationType() {
        return companyIdentificationType;
    }

    public void setCompanyIdentificationType(int companyIdentificationType) {
        this.companyIdentificationType = companyIdentificationType;
    }


    public List<RequiredDocument> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<RequiredDocument> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    //

    public String getDbAddressLine1() {
        return dbAddressLine1;
    }

    public void setDbAddressLine1(String dbAddressLine1) {
        this.dbAddressLine1 = dbAddressLine1;
    }

    public String getDbAddressLine2() {
        return dbAddressLine2;
    }

    public void setDbAddressLine2(String dbAddressLine2) {
        this.dbAddressLine2 = dbAddressLine2;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    public String getDbEmail() {
        return dbEmail;
    }

    public void setDbEmail(String dbEmail) {
        this.dbEmail = dbEmail;
    }

    public PhNoWithCountryCode getDbPhoneNumberDto() {
        return dbPhoneNumberDto;
    }

    public void setDbPhoneNumberDto(PhNoWithCountryCode dbPhoneNumberDto) {
        this.dbPhoneNumberDto = dbPhoneNumberDto;
    }

    public String getDbBusinessType() {
        return dbBusinessType;
    }

    public void setDbBusinessType(String dbBusinessType) {
        this.dbBusinessType = dbBusinessType;
    }


    public boolean isDbCopyCompanyInfo() {
        return dbCopyCompanyInfo;
    }

    public void setDbCopyCompanyInfo(boolean dbCopyCompanyInfo) {
        this.dbCopyCompanyInfo = dbCopyCompanyInfo;
    }

    public String getDbTimezone() {
        return dbTimezone;
    }

    public void setDbTimezone(String dbTimezone) {
        this.dbTimezone = dbTimezone;
    }

    public String getDbWebsite() {
        return dbWebsite;
    }

    public void setDbWebsite(String dbWebsite) {
        this.dbWebsite = dbWebsite;
    }


    public static class BOResp {
        private String status;
        private String timestamp;
        private List<com.greenbox.coyni.model.BeneficialOwners.BOResp.BeneficialOwner> data;
        private Error error;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public List<com.greenbox.coyni.model.BeneficialOwners.BOResp.BeneficialOwner> getData() {
            return data;
        }

        public void setData(List<com.greenbox.coyni.model.BeneficialOwners.BOResp.BeneficialOwner> data) {
            this.data = data;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

    }
}

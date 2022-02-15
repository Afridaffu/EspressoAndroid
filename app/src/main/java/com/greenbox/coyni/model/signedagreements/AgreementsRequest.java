package com.greenbox.coyni.model.signedagreements;

public class AgreementsRequest {
    private int agreementType;
    private String identityFile;

    public int getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(int agreementType) {
        this.agreementType = agreementType;
    }

    public String getIdentityFile() {
        return identityFile;
    }

    public void setIdentityFile(String identityFile) {
        this.identityFile = identityFile;
    }
}

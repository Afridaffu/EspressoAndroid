package com.coyni.mapp.model;

import java.util.ArrayList;
import java.util.List;

public class FilteredAgreements {
    private List<SignAgreementData> agreements = new ArrayList<>();
    private boolean isMerchantAgreement = false;

    public List<SignAgreementData> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<SignAgreementData> agreements) {
        this.agreements = agreements;
    }

    public boolean isMerchantAgreement() {
        return isMerchantAgreement;
    }

    public void setMerchantAgreement(boolean merchantAgreement) {
        isMerchantAgreement = merchantAgreement;
    }
}


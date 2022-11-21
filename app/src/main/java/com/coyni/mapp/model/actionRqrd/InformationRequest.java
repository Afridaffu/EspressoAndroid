package com.coyni.mapp.model.actionRqrd;

import java.util.List;

import retrofit2.http.Body;

public class InformationRequest {
    private List<Integer> websiteUpdates;
    private List<ProposalRequest> proposals;
    private BankRequest bankRequest;
    private Boolean reserveRuleAccepted;

    public List<Integer> getWebsiteUpdates() {
        return websiteUpdates;
    }

    public void setWebsiteUpdates(List<Integer> websiteUpdates) {
        this.websiteUpdates = websiteUpdates;
    }

    public BankRequest getBankRequest() {
        return bankRequest;
    }

    public void setBankRequest(BankRequest bankRequest) {
        this.bankRequest = bankRequest;
    }

    public boolean isReserveRuleAccepted() {
        return reserveRuleAccepted;
    }

    public void setReserveRuleAccepted(boolean reserveRuleAccepted) {
        this.reserveRuleAccepted = reserveRuleAccepted;
    }

    public List<ProposalRequest> getProposals() {
        return proposals;
    }

    public void setProposals(List<ProposalRequest> proposals) {
        this.proposals = proposals;
    }
}

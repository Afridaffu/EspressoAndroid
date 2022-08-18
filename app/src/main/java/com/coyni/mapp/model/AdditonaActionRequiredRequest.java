package com.coyni.mapp.model;

import com.coyni.mapp.model.underwriting.ProposalsSubmitRequestData;

import java.util.ArrayList;

public class AdditonaActionRequiredRequest {

    public ArrayList<Integer> documentIdList;

    public ArrayList<Integer> websiteUpdates;

    public ArrayList<ProposalsSubmitRequestData> proposals;

    public boolean reserveRuleAccepted;

    public ArrayList<Integer> getDocumentIdList() {
        return documentIdList;
    }

    public void setDocumentIdList(ArrayList<Integer> documentIdList) {
        this.documentIdList = documentIdList;
    }

    public ArrayList<Integer> getWebsiteUpdates() {
        return websiteUpdates;
    }

    public void setWebsiteUpdates(ArrayList<Integer> websiteUpdates) {
        this.websiteUpdates = websiteUpdates;
    }

    public ArrayList<ProposalsSubmitRequestData> getProposals() {
        return proposals;
    }

    public void setProposals(ArrayList<ProposalsSubmitRequestData> proposals) {
        this.proposals = proposals;
    }

    public boolean isReserveRuleAccepted() {
        return reserveRuleAccepted;
    }

    public void setReserveRuleAccepted(boolean reserveRuleAccepted) {
        this.reserveRuleAccepted = reserveRuleAccepted;
    }

    @Override
    public String toString() {
        return "AdditonaActionRequiredRequest{" +
                "documentIdList=" + documentIdList +
                ", websiteUpdates=" + websiteUpdates +
                ", proposals=" + proposals +
                ", reserveRuleAccepted=" + reserveRuleAccepted +
                '}';
    }
}

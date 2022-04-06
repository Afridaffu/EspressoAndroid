package com.greenbox.coyni.model;

import com.greenbox.coyni.model.preferences.BaseProfile;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.underwriting.ProposalsData;
import com.greenbox.coyni.model.underwriting.ProposalsSubmitRequestData;
import com.greenbox.coyni.utils.ProfileComparator;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

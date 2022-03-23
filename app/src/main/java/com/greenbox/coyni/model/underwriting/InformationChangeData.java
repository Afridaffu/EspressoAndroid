package com.greenbox.coyni.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InformationChangeData {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("iteration")
    @Expose
    private int iteration;

    @SerializedName("isCustomerAcknowledged")
    @Expose
    private Boolean isCustomerAcknowledged;

    @SerializedName("customerAcknowledgementTime")
    @Expose
    private String customerAcknowledgementTime;

    @SerializedName("proposals")
    @Expose
    private List<ProposalsData> proposals;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public Boolean getCustomerAcknowledged() {
        return isCustomerAcknowledged;
    }

    public void setCustomerAcknowledged(Boolean customerAcknowledged) {
        isCustomerAcknowledged = customerAcknowledged;
    }

    public String getCustomerAcknowledgementTime() {
        return customerAcknowledgementTime;
    }

    public void setCustomerAcknowledgementTime(String customerAcknowledgementTime) {
        this.customerAcknowledgementTime = customerAcknowledgementTime;
    }

    public List<ProposalsData> getProposals() {
        return proposals;
    }

    public void setProposals(List<ProposalsData> proposals) {
        this.proposals = proposals;
    }
}

package com.greenbox.coyni.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;

import java.util.List;

public class ActionRequiredDataResponse {

    @SerializedName("iteration")
    @Expose
    private int status;
    @SerializedName("additionalDocument")
    @Expose
    private List<AdditionalDocumentData> additionalDocument;

    @SerializedName("websiteChange")
    @Expose
    private List<WebsiteChangeData> websiteChange;

    @SerializedName("informationChange")
    @Expose
    private List<InformationChangeData> informationChange;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<AdditionalDocumentData> getAdditionalDocument() {
        return additionalDocument;
    }

    public void setAdditionalDocument(List<AdditionalDocumentData> additionalDocument) {
        this.additionalDocument = additionalDocument;
    }

    public List<WebsiteChangeData> getWebsiteChange() {
        return websiteChange;
    }

    public void setWebsiteChange(List<WebsiteChangeData> websiteChange) {
        this.websiteChange = websiteChange;
    }

    public List<InformationChangeData> getInformationChange() {
        return informationChange;
    }

    public void setInformationChange(List<InformationChangeData> informationChange) {
        this.informationChange = informationChange;
    }

    @Override
    public String toString() {

        return "ActionRequiredDataResponse{" +
                "status=" + status +
                ", additionalDocument=" + additionalDocument +
                ", websiteChange=" + websiteChange +
                ", informationChange=" + informationChange +
                '}';
    }
}


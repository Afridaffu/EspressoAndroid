package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("reserveRule")
    @Expose
    private ReseveRuleData reserveRule;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("note")
    @Expose
    private String note;

    public ReseveRuleData getReserveRule() {
        return reserveRule;
    }

    public void setReserveRule(ReseveRuleData reserveRule) {
        this.reserveRule = reserveRule;
    }

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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {

        return "ActionRequiredDataResponse{" +
                "status=" + status +
                ", additionalDocument=" + additionalDocument +
                ", websiteChange=" + websiteChange +
                ", informationChange=" + informationChange +
                ", reserverule =" + reserveRule +
                ", message =" + message +
                ", note =" + note +
                '}';
    }
}


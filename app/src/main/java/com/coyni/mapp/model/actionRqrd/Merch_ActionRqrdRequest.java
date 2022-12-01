package com.coyni.mapp.model.actionRqrd;

import com.coyni.mapp.model.underwriting.AdditionalDocumentData;
import com.coyni.mapp.model.underwriting.InformationChangeData;
import com.coyni.mapp.model.underwriting.ReseveRuleData;
import com.coyni.mapp.model.underwriting.WebsiteChangeData;

import java.util.List;

public class Merch_ActionRqrdRequest {
    private List<AdditionalDocumentData> additionalDocument;
    private int iteration;
    private String message;
    private String note;
    private List<WebsiteChangeData> websiteChange;
    private List<InformationChangeData> informationChange;
    private ReseveRuleData reserveRule;


    public List<AdditionalDocumentData> getAdditionalDocument() {
        return additionalDocument;
    }

    public void setAdditionalDocument(List<AdditionalDocumentData> additionalDocument) {
        this.additionalDocument = additionalDocument;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public ReseveRuleData getReserveRule() {
        return reserveRule;
    }

    public void setReserveRule(ReseveRuleData reserveRule) {
        this.reserveRule = reserveRule;
    }
}

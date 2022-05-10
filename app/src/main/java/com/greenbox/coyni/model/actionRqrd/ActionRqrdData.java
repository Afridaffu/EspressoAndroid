package com.greenbox.coyni.model.actionRqrd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.bank.BankItem;
import com.greenbox.coyni.model.underwriting.AdditionalDocumentData;
import com.greenbox.coyni.model.underwriting.InformationChangeData;
import com.greenbox.coyni.model.underwriting.ReseveRuleData;
import com.greenbox.coyni.model.underwriting.WebsiteChangeData;

import java.util.List;

public class ActionRqrdData {
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

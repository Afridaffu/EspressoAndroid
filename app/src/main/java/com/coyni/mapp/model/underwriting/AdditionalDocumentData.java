package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdditionalDocumentData {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("iteration")
    @Expose
    private int iteration;

    @SerializedName("documentId")
    @Expose
    private int documentId;

    @SerializedName("documentName")
    @Expose
    private String documentName;

    @SerializedName("documentUrl")
    @Expose
    private String documentUrl;

    @SerializedName("documentSize")
    @Expose
    private String documentSize;

    @SerializedName("isAccepted")
    @Expose
    private String isAccepted;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(String documentSize) {
        this.documentSize = documentSize;
    }

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }
}

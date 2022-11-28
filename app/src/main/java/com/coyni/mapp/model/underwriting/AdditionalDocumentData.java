package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

    @SerializedName("uploadDocs")
    @Expose
    private List<DocumentData> uploadDocs = new ArrayList<>();

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

    public List<DocumentData> getUploadDocs() {
        return uploadDocs;
    }

    public void setUploadDocs(List<DocumentData> uploadDocs) {
        this.uploadDocs = uploadDocs;
    }

    public class DocumentData {
        @SerializedName("docId")
        @Expose
        private int docId;

        @SerializedName("documentSize")
        @Expose
        private int documentSize;

        @SerializedName("fileName")
        @Expose
        private String fileName;

        @SerializedName("documentUrl")
        @Expose
        private String documentUrl;

        @SerializedName("uploadDate")
        @Expose
        private String uploadDate;

        public int getDocId() {
            return docId;
        }

        public void setDocId(int docId) {
            this.docId = docId;
        }

        public int getDocumentSize() {
            return documentSize;
        }

        public void setDocumentSize(int documentSize) {
            this.documentSize = documentSize;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDocumentUrl() {
            return documentUrl;
        }

        public void setDocumentUrl(String documentUrl) {
            this.documentUrl = documentUrl;
        }

        public String getUploadDate() {
            return uploadDate;
        }

        public void setUploadDate(String uploadDate) {
            this.uploadDate = uploadDate;
        }
    }
}

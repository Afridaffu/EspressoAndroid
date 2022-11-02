package com.coyni.mapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateSignAgreementsResp {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("data")
    @Expose
    private UpdatedSignData data;

    @SerializedName("error")
    @Expose
    private Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UpdatedSignData getData() {
        return data;
    }

    public void setData(UpdatedSignData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class UpdatedSignData {
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("signature")
        @Expose
        private String signature;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("agreementType")
        @Expose
        private int agreementType;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getAgreementType() {
            return agreementType;
        }

        public void setAgreementType(int agreementType) {
            this.agreementType = agreementType;
        }
    }

}
package com.coyni.mapp.model.BeneficialOwners;

import com.coyni.mapp.model.Error;

public class BOPatchResp {
    private String status;
    private String timestamp;
    private Data data;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Data {
        private String fullName;
        private String ownership;
        private String totalOwnership;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getOwnership() {
            return ownership;
        }

        public void setOwnership(String ownership) {
            this.ownership = ownership;
        }

        public String getTotalOwnership() {
            return totalOwnership;
        }

        public void setTotalOwnership(String totalOwnership) {
            this.totalOwnership = totalOwnership;
        }
    }
}


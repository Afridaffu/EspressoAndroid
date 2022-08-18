package com.coyni.mapp.model.DBAInfo;

import com.coyni.mapp.model.Error;

import java.util.List;

public class BusinessTypeResp {
    private String status;
    private String timestamp;
    private List<BusinessType> data;
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

    public List<BusinessType> getData() {
        return data;
    }

    public void setData(List<BusinessType> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }


    public class RequiredDocumets {
        private String imgSize;
        private String imgName;
        private String imgLink;
        private String updatedAt;
        private int identityId;

        public String getImgSize() {
            return imgSize;
        }

        public void setImgSize(String imgSize) {
            this.imgSize = imgSize;
        }

        public String getImgName() {
            return imgName;
        }

        public void setImgName(String imgName) {
            this.imgName = imgName;
        }

        public String getImgLink() {
            return imgLink;
        }

        public void setImgLink(String imgLink) {
            this.imgLink = imgLink;
        }

        public int getIdentityId() {
            return identityId;
        }

        public void setIdentityId(int identityId) {
            this.identityId = identityId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}


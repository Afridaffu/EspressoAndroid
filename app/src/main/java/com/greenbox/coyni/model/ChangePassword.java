package com.greenbox.coyni.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePassword {

        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("data")
        @Expose
        private ChangePasswordData data;
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

        public ChangePasswordData getData() {
            return data;
        }

        public void setData(ChangePasswordData data) {
            this.data = data;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

}

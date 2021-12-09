package com.greenbox.coyni.model.transaction;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.Error;

import java.io.Serializable;

public class TransactionList  {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("data")
        @Expose
        private TransactionListData data;
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

        public TransactionListData getData() {
            return data;
        }

        public void setData(TransactionListData data) {
            this.data = data;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

    }

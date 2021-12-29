package com.greenbox.coyni.model.identity_verification;


import com.greenbox.coyni.model.Error;

import java.util.List;

public class LatestTxnResponse {
    private String status;
    private String timestamp;
    private List<Daata> data;
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

    public List<Daata> getData() {
        return data;
    }

    public void setData(List<Daata> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Daata {
        private String createdAt;
        private String txnTypeDn;
        private String txnSubTypeDn;
        private String txnStatusDn;
        private String amount;
        private String txnDescription;
        private String walletBalance;
        private String transactionId;
        private String gbxTransactionId;
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getTxnTypeDn() {
            return txnTypeDn;
        }

        public void setTxnTypeDn(String txnTypeDn) {
            this.txnTypeDn = txnTypeDn;
        }

        public String getTxnSubTypeDn() {
            return txnSubTypeDn;
        }

        public void setTxnSubTypeDn(String txnSubTypeDn) {
            this.txnSubTypeDn = txnSubTypeDn;
        }

        public String getTxnStatusDn() {
            return txnStatusDn;
        }

        public void setTxnStatusDn(String txnStatusDn) {
            this.txnStatusDn = txnStatusDn;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTxnDescription() {
            return txnDescription;
        }

        public void setTxnDescription(String txnDescription) {
            this.txnDescription = txnDescription;
        }

        public String getWalletBalance() {
            return walletBalance;
        }

        public void setWalletBalance(String walletBalance) {
            this.walletBalance = walletBalance;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getGbxTransactionId() {
            return gbxTransactionId;
        }

        public void setGbxTransactionId(String gbxTransactionId) {
            this.gbxTransactionId = gbxTransactionId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

}


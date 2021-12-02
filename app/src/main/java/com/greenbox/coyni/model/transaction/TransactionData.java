package com.greenbox.coyni.model.transaction;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class TransactionData {

        @SerializedName("youPay")
        @Expose
        private String youPay;
        @SerializedName("cardHolderName")
        @Expose
        private String cardHolderName;
        @SerializedName("transactionSubtype")
        @Expose
        private String transactionSubtype;
        @SerializedName("processingFee")
        @Expose
        private String processingFee;
        @SerializedName("referenceId")
        @Expose
        private String referenceId;
        @SerializedName("transactionType")
        @Expose
        private String transactionType;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("exchangeRate")
        @Expose
        private String exchangeRate;
        @SerializedName("youGet")
        @Expose
        private String youGet;
        @SerializedName("accountBalance")
        @Expose
        private String accountBalance;
        @SerializedName("cardBrand")
        @Expose
        private String cardBrand;
        @SerializedName("cardNumber")
        @Expose
        private String cardNumber;
        @SerializedName("descriptorName")
        @Expose
        private String descriptorName;
        @SerializedName("status")
        @Expose
        private String status;

        public String getYouPay() {
            return youPay;
        }

        public void setYouPay(String youPay) {
            this.youPay = youPay;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getTransactionSubtype() {
            return transactionSubtype;
        }

        public void setTransactionSubtype(String transactionSubtype) {
            this.transactionSubtype = transactionSubtype;
        }

        public String getProcessingFee() {
            return processingFee;
        }

        public void setProcessingFee(String processingFee) {
            this.processingFee = processingFee;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getExchangeRate() {
            return exchangeRate;
        }

        public void setExchangeRate(String exchangeRate) {
            this.exchangeRate = exchangeRate;
        }

        public String getYouGet() {
            return youGet;
        }

        public void setYouGet(String youGet) {
            this.youGet = youGet;
        }

        public String getAccountBalance() {
            return accountBalance;
        }

        public void setAccountBalance(String accountBalance) {
            this.accountBalance = accountBalance;
        }

        public String getCardBrand() {
            return cardBrand;
        }

        public void setCardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getDescriptorName() {
            return descriptorName;
        }

        public void setDescriptorName(String descriptorName) {
            this.descriptorName = descriptorName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

}

package com.coyni.mapp.model.transaction;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class TransactionListItems {

        @SerializedName("pendingTransactions")
        @Expose
        private List<TransactionListPending> pendingTransactions;
        @SerializedName("pendingTransactionsCount")
        @Expose
        private int pendingTransactionsCount;
        @SerializedName("postedTransactions")
        @Expose
        private List<TransactionListPosted> postedTransactions;
        @SerializedName("postedTransactionsCount")
        @Expose
        private int postedTransactionsCount;

        public List<TransactionListPending> getPendingTransactions() {
            return pendingTransactions;
        }

        public void setPendingTransactions(List<TransactionListPending> pendingTransactions) {
            this.pendingTransactions = pendingTransactions;
        }

        public int getPendingTransactionsCount() {
            return pendingTransactionsCount;
        }

        public void setPendingTransactionsCount(int pendingTransactionsCount) {
            this.pendingTransactionsCount = pendingTransactionsCount;
        }

        public List<TransactionListPosted> getPostedTransactions() {
            return postedTransactions;
        }

        public void setPostedTransactions(List<TransactionListPosted> postedTransactions) {
            this.postedTransactions = postedTransactions;
        }

        public int getPostedTransactionsCount() {
            return postedTransactionsCount;
        }

        public void setPostedTransactionsCount(int postedTransactionsCount) {
            this.postedTransactionsCount = postedTransactionsCount;
        }

    }

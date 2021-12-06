package com.greenbox.coyni.model.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionListData {

    @SerializedName("items")
    @Expose
    private TransactionListItems items;
    @SerializedName("currentPageNo")
    @Expose
    private int currentPageNo;
    @SerializedName("pageSize")
    @Expose
    private int pageSize;
    @SerializedName("totalItems")
    @Expose
    private int totalItems;
    @SerializedName("totalPages")
    @Expose
    private int totalPages;

    public TransactionListItems getItems() {
        return items;
    }

    public void setItems(TransactionListItems items) {
        this.items = items;
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}

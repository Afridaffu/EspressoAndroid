package com.coyni.mapp.model.BusinessBatchPayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BatchPayoutListData {

    @SerializedName("items")
    @Expose
    private List<BatchPayoutListItems> items;
    @SerializedName("currentPageNo")
    @Expose
    private String currentPageNo;
    @SerializedName("pageSize")
    @Expose
    private String pageSize;
    @SerializedName("totalItems")
    @Expose
    private int totalItems;
    @SerializedName("totalPages")
    @Expose
    private int totalPages;

    public List<BatchPayoutListItems> getItems() {
        return items;
    }

    public void setItems(List<BatchPayoutListItems> items) {
        this.items = items;
    }

    public String getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(String currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
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

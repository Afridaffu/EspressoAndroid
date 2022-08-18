package com.coyni.mapp.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgreementsData {
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("currentPageNo")
    @Expose
    private String currentPageNo;
    @SerializedName("pageSize")
    @Expose
    private String pageSize;
    @SerializedName("totalItems")
    @Expose
    private String totalItems;
    @SerializedName("totalPages")
    @Expose
    private String totalPages;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
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

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

}

package com.greenbox.coyni.model.bank;

import java.util.List;

public class BankAccountsDataModel {


    private List<BankItems> items = null;

    private Integer currentPageNo;

    private Integer pageSize;

    private Integer totalItems;

    private Integer totalPages;

    public List<BankItems> getItems() {
        return items;
    }

    public void setItems(List<BankItems> items) {
        this.items = items;
    }

    public Integer getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(Integer currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}
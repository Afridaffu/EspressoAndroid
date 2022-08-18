package com.coyni.mapp.model.notification;

import java.util.List;

public class NotificationsData {
    private List<NotificationsDataItems> items;
    private int currentPageNo;
    private int pageSize;
    private int totalItems;
    private int totalPages;

    public List<NotificationsDataItems> getItems() {
        return items;
    }

    public void setItems(List<NotificationsDataItems> items) {
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

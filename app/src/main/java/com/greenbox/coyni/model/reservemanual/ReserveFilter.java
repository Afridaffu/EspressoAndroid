package com.greenbox.coyni.model.reservemanual;

import java.io.Serializable;

public class ReserveFilter implements Serializable {

    public boolean isFilterApplied = false;
    private  boolean isOpen = false, isOnHold = false, isReleased =false, isCancelled = false, isFailed = false, isDate = false;
    private String updatedFromDate;
    private String updatedToDate;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOnHold() {
        return isOnHold;
    }

    public void setOnHold(boolean onHold) {
        isOnHold = onHold;
    }

    public boolean isReleased() {
        return isReleased;
    }

    public void setReleased(boolean released) {
        isReleased = released;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public String getUpdatedFromDate() {
        return updatedFromDate;
    }

    public void setUpdatedFromDate(String updatedFromDate) {
        this.updatedFromDate = updatedFromDate;
    }

    public String getUpdatedToDate() {
        return updatedToDate;
    }

    public void setUpdatedToDate(String updatedToDate) {
        this.updatedToDate = updatedToDate;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }
}

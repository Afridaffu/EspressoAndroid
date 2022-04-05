package com.greenbox.coyni.model.reservemanual;

import java.io.Serializable;

public class ReserveFilter implements Serializable {

    public boolean isFilterApplied = false;
    private  boolean isOpen = false, isOnHold = false, isReleased =false, isCancelled = false;

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
}

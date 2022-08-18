package com.coyni.mapp.model.users;

public class TimeZoneModel {

    private String timezone;

    private int timezoneID;

    private boolean isSelected = false;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getTimezoneID() {
        return timezoneID;
    }

    public void setTimezoneID(int timezoneID) {
        this.timezoneID = timezoneID;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}


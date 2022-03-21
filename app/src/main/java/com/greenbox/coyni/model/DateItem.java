package com.greenbox.coyni.model;

import java.util.Objects;

public class DateItem extends ListItem {

    private int type = ListItem.TYPE_GROUP;

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        DateItem dateItem = (DateItem) obj;
        return Objects.equals(date, dateItem.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, date);
    }
}

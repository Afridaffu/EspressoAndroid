package com.coyni.mapp.model;

public class States {
    private String name;
    private String code;
    private boolean isSelected = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsocode() {
        return code;
    }

    public void setIsocode(String isocode) {
        this.code = isocode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

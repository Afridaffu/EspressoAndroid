package com.greenbox.coyni.model.profile;

import java.util.ArrayList;

public class BusinessAccountDbaInfo {

    private String name = "";
    private String dbaImage = "";
    private int id ;
    private boolean isSelected ;

    // Getter , setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDbaImage() {
        return dbaImage;
    }

    public void setDbaImage(String dbaImage) {
        this.dbaImage = dbaImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    @Override
    public String toString() {
        return "BusinessAccountDbaInfo{" +
                "name='" + name + '\'' +
                "image='" + dbaImage + '\'' +
                "id='" + id + '\'' +
                '}';
    }
}

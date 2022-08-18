package com.coyni.mapp.model.profile;

public class BusinessSharedAccountDbaInfo {

    private String name = "";
    private String dbaImage = "";
    private String accountSttaus = "";
    private int id ;
    private boolean isSelected ;


    public String getAccountSttaus() {
        return accountSttaus;
    }

    public void setAccountSttaus(String accountSttaus) {
        this.accountSttaus = accountSttaus;
    }

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
                ", dbaImage='" + dbaImage + '\'' +
                ", id=" + id +
                ", isSelected=" + isSelected +
                '}';
    }
}

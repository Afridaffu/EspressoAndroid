package com.greenbox.coyni.model.profile;

import com.greenbox.coyni.model.Error;

import java.util.ArrayList;

public class BusinessAccountsListInfo {

    private String mainSetName;
    private String mainImage;
    private int id ;

    private ArrayList<BusinessAccountDbaInfo> list = new ArrayList<BusinessAccountDbaInfo>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return mainSetName;
    }

    public void setName(String mainSetName) {
        this.mainSetName = mainSetName;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public ArrayList<BusinessAccountDbaInfo> getSubsetName() {
        return list;
    }

    public void setSubsetName(ArrayList<BusinessAccountDbaInfo> subSetName) {
        this.list = subSetName;
    }

    @Override
    public String toString() {
        return "BusinessAccountsListInfo{" +
                "mainSetName='" + mainSetName + '\'' +
                ", list=" + list +
                '}';
    }
}

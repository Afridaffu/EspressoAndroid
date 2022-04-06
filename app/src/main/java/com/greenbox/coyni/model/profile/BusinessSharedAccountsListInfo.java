package com.greenbox.coyni.model.profile;

import java.util.ArrayList;

public class BusinessSharedAccountsListInfo {

    private String mainSetName;
    private String mainImage;
    private int id ;

    private ArrayList<BusinessSharedAccountDbaInfo> list = new ArrayList<BusinessSharedAccountDbaInfo>();

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

    public ArrayList<BusinessSharedAccountDbaInfo> getSubsetName() {
        return list;
    }

    public void setSubsetName(ArrayList<BusinessSharedAccountDbaInfo> subSetName) {
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
package com.greenbox.coyni.model.profile;

import com.greenbox.coyni.model.Error;

import java.util.ArrayList;

public class BusinessAccountsListInfo {

    private String mainSetName;
    private ArrayList<BusinessAccountDbaInfo> list = new ArrayList<BusinessAccountDbaInfo>();

    public String getName() {
        return mainSetName;
    }

    public void setName(String mainSetName) {
        this.mainSetName = mainSetName;
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
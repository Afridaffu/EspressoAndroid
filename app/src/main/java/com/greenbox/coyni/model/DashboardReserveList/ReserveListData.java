package com.greenbox.coyni.model.DashboardReserveList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReserveListData {

    @SerializedName("responseList")
    @Expose
    private ArrayList<ReserveListItems> responseList = new ArrayList<>();
    @SerializedName("nextReserveReleaseAmount")
    @Expose
    private String nextReserveReleaseAmount;
    private String nextReserveReleaseDate;

    public ArrayList<ReserveListItems> getResponseList() {
        return responseList;
    }

    public void setResponseList(ArrayList<ReserveListItems> responseList) {
        this.responseList = responseList;
    }

    public String getNextReserveReleaseDate() {
        return nextReserveReleaseDate;
    }

    public void setNextReserveReleaseDate(String nextReserveReleaseDate) {
        this.nextReserveReleaseDate = nextReserveReleaseDate;
    }

    public String getNextReserveReleaseAmount() {
        return nextReserveReleaseAmount;
    }

    public void setNextReserveReleaseAmount(String nextReserveReleaseAmount) {
        this.nextReserveReleaseAmount = nextReserveReleaseAmount;
    }
}

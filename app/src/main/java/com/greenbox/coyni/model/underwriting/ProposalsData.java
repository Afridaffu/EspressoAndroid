package com.greenbox.coyni.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProposalsData {

    @SerializedName("dbId")
    @Expose
    private int dbId;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("properties")
    @Expose
    private List<ProposalsPropertiesData> properties;

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<ProposalsPropertiesData> getProperties() {
        return properties;
    }

    public void setProperties(List<ProposalsPropertiesData> properties) {
        this.properties = properties;
    }
}

package com.coyni.mapp.model.actionRqrd;

import java.util.List;

public class ProposalRequest {
    private String displayName;
    private String firstName;
    private String lastName;
    private String type;
    private int dbId;
    private List<PropertyRequest> properties;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public List<PropertyRequest> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyRequest> properties) {
        this.properties = properties;
    }
}

package com.coyni.mapp.model.featurecontrols;

import java.util.List;

public class FeatureData {
    private String portalName;
    private List<PermissionResponseList> permissionResponseList;
    private Boolean master;

    public String getPortalName() {
        return portalName;
    }

    public void setPortalName(String portalName) {
        this.portalName = portalName;
    }

    public List<PermissionResponseList> getPermissionResponseList() {
        return permissionResponseList;
    }

    public void setPermissionResponseList(List<PermissionResponseList> permissionResponseList) {
        this.permissionResponseList = permissionResponseList;
    }

    public Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }
}

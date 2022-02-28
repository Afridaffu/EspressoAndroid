package com.greenbox.coyni.model.profile;

import java.util.ArrayList;

public class BusinessAccountDbaInfo {

    private String name = "";

    // Getter , setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BusinessAccountDbaInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}

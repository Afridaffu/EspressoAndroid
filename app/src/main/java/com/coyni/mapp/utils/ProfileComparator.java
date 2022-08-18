package com.coyni.mapp.utils;

import com.coyni.mapp.model.preferences.BaseProfile;

import java.util.Comparator;

public class ProfileComparator implements Comparator<BaseProfile> {

    @Override
    public int compare(BaseProfile profile1, BaseProfile profile2) {
        return profile1.getAccountType().compareTo(profile2.getAccountType());
    }
}

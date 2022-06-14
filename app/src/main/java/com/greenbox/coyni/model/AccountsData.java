package com.greenbox.coyni.model;

import com.greenbox.coyni.model.preferences.BaseProfile;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.ProfileComparator;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AccountsData {

    HashMap<Integer, ArrayList<ProfilesResponse.Profiles>> accountsMap;
    ArrayList<BaseProfile> groupData;
    List<ProfilesResponse.Profiles> profilesList;

    public AccountsData(List<ProfilesResponse.Profiles> profilesList) {
        accountsMap = new HashMap<>();
        groupData = new ArrayList<>();
        this.profilesList = profilesList;
        processData();
    }

    public HashMap getData() {
        return accountsMap;
    }

    public ArrayList<BaseProfile> getGroupData() {
        Collections.sort(groupData, new ProfileComparator());
        return groupData;
    }

    public void removeSharedAccounts() {
        if(groupData == null || groupData.size() == 0) {
            return;
        }
        for(int count = 0; count < groupData.size(); count++) {
            BaseProfile profile = groupData.get(count);
            if(profile.getAccountType().equals(Utils.SHARED)) {
                groupData.remove(count);
                count--;
            }
        }
    }

    private void processData() {
        if (profilesList == null) {
            return;
        }
        for (ProfilesResponse.Profiles profile : profilesList) {
            if (profile.getAccountType().equals(Utils.BUSINESS)) {
                if (profile.getDbaOwner() == null) {
                    if (accountsMap.get(profile.getId()) == null) {
                        accountsMap.put(profile.getId(), new ArrayList<ProfilesResponse.Profiles>());
                    }
                    if (!groupData.contains(profile)) {
                        groupData.add(profile);
                    }
                    ArrayList<ProfilesResponse.Profiles> dbaList = accountsMap.get(profile.getId());
                    dbaList.add(profile);
                } else {
                    if (accountsMap.get(Integer.valueOf(profile.getDbaOwner())) == null) {
                        accountsMap.put(Integer.parseInt(profile.getDbaOwner()), new ArrayList<ProfilesResponse.Profiles>());
                        //groupData.add(profile);
                    }
                    ArrayList<ProfilesResponse.Profiles> dbaList = accountsMap.get(Integer.valueOf(profile.getDbaOwner()));
                    dbaList.add(profile);
                }
            } else if (profile.getAccountType().equals(Utils.SHARED)) {
                //ProfilesResponse.Profiles sharedProfile = new ProfilesResponse.Profiles();
                BaseProfile baseProfile = new BaseProfile();
                baseProfile.setId(0000);
                baseProfile.setAccountType(Utils.SHARED);
                if (!groupData.contains(baseProfile)) {
                    groupData.add(baseProfile);
                }
                if (accountsMap.get(0000) == null) {
                    accountsMap.put(0000, new ArrayList<ProfilesResponse.Profiles>());
                }
                ArrayList<ProfilesResponse.Profiles> sharedList = accountsMap.get(0000);
                sharedList.add(profile);

            } else if (profile.getAccountType().equals(Utils.PERSONAL)) {
                if (!groupData.contains(profile)) {
                    groupData.add(profile);
                }
            }

        }
    }
}

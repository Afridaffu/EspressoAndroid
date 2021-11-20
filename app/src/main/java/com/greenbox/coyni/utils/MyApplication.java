package com.greenbox.coyni.utils;

import android.app.Application;

import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsData;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.cards.CardsDataItem;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.users.AccountLimitsData;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    static String strEncryptedPublicKey;
    List<CardsDataItem> listCards = new ArrayList<>();
    List<Agreements> agreementsList;
    AccountLimitsData objAcc;
    RetrieveUsersResponse objRetUsers = new RetrieveUsersResponse();
    String strUserName = "", strRetrEmail = "",listAgree="";
    Profile myProfile = new Profile();
    UpdateEmailResponse updateEmailResponse = new UpdateEmailResponse();
    List<States> listStates = new ArrayList<>();
    Boolean isBiometric = false;
    public AccountLimitsData getObjAcc() {
        return objAcc;
    }

    public void setObjAcc(AccountLimitsData objAcc) {
        this.objAcc = objAcc;
    }

    public List<Agreements> getAgreementsList() {
        return agreementsList;
    }

    public void setAgreementsList(List<Agreements> agreementsList) {
        this.agreementsList = agreementsList;
    }

    public String getListAgree() {
        return listAgree;
    }

    public void setListAgree(String listAgree) {
        this.listAgree = listAgree;
    }

    public List<CardsDataItem> getListCards() {
        return listCards;
    }

    public void setListCards(List<CardsDataItem> listCards) {
        this.listCards = listCards;
    }

    private String fromWhichFragment;


    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }
    public RetrieveUsersResponse getObjRetUsers() {
        return objRetUsers;
    }

    public void setObjRetUsers(RetrieveUsersResponse objRetUsers) {
        this.objRetUsers = objRetUsers;
    }

    public String getStrRetrEmail() {
        return strRetrEmail;
    }

    public void setStrRetrEmail(String strRetrEmail) {
        this.strRetrEmail = strRetrEmail;
    }


    public String getFromWhichFragment() {
        return fromWhichFragment;
    }

    public void setFromWhichFragment(String fromWhichFragment) {
        this.fromWhichFragment = fromWhichFragment;
    }

    public Profile getMyProfile() {
        return myProfile;
    }

    public void setMyProfile(Profile myProfile) {
        this.myProfile = myProfile;
    }

    public UpdateEmailResponse getUpdateEmailResponse() {
        return updateEmailResponse;
    }

    public void setUpdateEmailResponse(UpdateEmailResponse updateEmailResponse) {
        this.updateEmailResponse = updateEmailResponse;
    }

    public List<States> getListStates() {
        return listStates;
    }

    public void setListStates(List<States> listStates) {
        this.listStates = listStates;
    }

    public Boolean getBiometric() {
        return isBiometric;
    }

    public void setBiometric(Boolean biometric) {
        isBiometric = biometric;
    }
}

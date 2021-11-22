package com.greenbox.coyni.utils;

import android.app.Application;

import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.cards.CardsDataItem;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    static String strEncryptedPublicKey;
    List<CardsDataItem> listCards = new ArrayList<>();
    RetrieveUsersResponse objRetUsers = new RetrieveUsersResponse();
    String strUserName = "", strRetrEmail = "";
    Profile myProfile = new Profile();
    UpdateEmailResponse updateEmailResponse = new UpdateEmailResponse();
    UpdatePhoneResponse updatePhoneResponse = new UpdatePhoneResponse();
    List<States> listStates = new ArrayList<>();
    //isBiometric - OS level on/off;  isLocalBiometric - LocalDB value
    Boolean isBiometric = false, isLocalBiometric = false;

    //Account Limits
    private double tokenWithdrawalBankDayLimit;
    private double tokenWithdrawalBankWeekLimit;

    private double tokenWithdrawalInstantpayDayLimit;
    private double tokenWithdrawalInstantpayWeekLimit;

    private double tokenWithdrawalGiftcardDayLimit;
    private double tokenWithdrawalGiftcardWeekLimit;

    private double tokenSendDayLimit;
    private double tokenSendWeekLimit;

    private double tokenBuyBankDayLimit;
    private double tokenBuyBankWeeekLimit;

    private double tokenBuyCardDayLimit;
    private double tokenBuyCardWeekLimit;

    private double tokenWithdrawalSignetDayLimit;
    private double tokenWithdrawalSignetWeekLimit;

    int intUserId;
    String strCity, strPhoneNum, strState, strCountry, strAddressLine1, strAddressLine2, strZipCode, strProfileImg;

    public int getIntUserId() {
        return intUserId;
    }

    public void setIntUserId(int intUserId) {
        this.intUserId = intUserId;
    }

    public String getStrCity() {
        return strCity;
    }

    public void setStrCity(String strCity) {
        this.strCity = strCity;
    }

    public String getStrPhoneNum() {
        return strPhoneNum;
    }

    public void setStrPhoneNum(String strPhoneNum) {
        this.strPhoneNum = strPhoneNum;
    }

    public String getStrState() {
        return strState;
    }

    public void setStrState(String strState) {
        this.strState = strState;
    }

    public String getStrCountry() {
        return strCountry;
    }

    public void setStrCountry(String strCountry) {
        this.strCountry = strCountry;
    }

    public String getStrAddressLine1() {
        return strAddressLine1;
    }

    public void setStrAddressLine1(String strAddressLine1) {
        this.strAddressLine1 = strAddressLine1;
    }

    public String getStrAddressLine2() {
        return strAddressLine2;
    }

    public void setStrAddressLine2(String strAddressLine2) {
        this.strAddressLine2 = strAddressLine2;
    }

    public String getStrZipCode() {
        return strZipCode;
    }

    public void setStrZipCode(String strZipCode) {
        this.strZipCode = strZipCode;
    }

    public String getStrProfileImg() {
        return strProfileImg;
    }

    public void setStrProfileImg(String strProfileImg) {
        this.strProfileImg = strProfileImg;
    }

    public double getTokenWithdrawalBankDayLimit() {
        return tokenWithdrawalBankDayLimit;
    }

    public void setTokenWithdrawalBankDayLimit(double tokenWithdrawalBankDayLimit) {
        this.tokenWithdrawalBankDayLimit = tokenWithdrawalBankDayLimit;
    }

    public double getTokenWithdrawalBankWeekLimit() {
        return tokenWithdrawalBankWeekLimit;
    }

    public void setTokenWithdrawalBankWeekLimit(double tokenWithdrawalBankWeekLimit) {
        this.tokenWithdrawalBankWeekLimit = tokenWithdrawalBankWeekLimit;
    }

    public double getTokenWithdrawalInstantpayDayLimit() {
        return tokenWithdrawalInstantpayDayLimit;
    }

    public void setTokenWithdrawalInstantpayDayLimit(double tokenWithdrawalInstantpayDayLimit) {
        this.tokenWithdrawalInstantpayDayLimit = tokenWithdrawalInstantpayDayLimit;
    }

    public double getTokenWithdrawalInstantpayWeekLimit() {
        return tokenWithdrawalInstantpayWeekLimit;
    }

    public void setTokenWithdrawalInstantpayWeekLimit(double tokenWithdrawalInstantpayWeekLimit) {
        this.tokenWithdrawalInstantpayWeekLimit = tokenWithdrawalInstantpayWeekLimit;
    }

    public double getTokenWithdrawalGiftcardDayLimit() {
        return tokenWithdrawalGiftcardDayLimit;
    }

    public void setTokenWithdrawalGiftcardDayLimit(double tokenWithdrawalGiftcardDayLimit) {
        this.tokenWithdrawalGiftcardDayLimit = tokenWithdrawalGiftcardDayLimit;
    }

    public double getTokenWithdrawalGiftcardWeekLimit() {
        return tokenWithdrawalGiftcardWeekLimit;
    }

    public void setTokenWithdrawalGiftcardWeekLimit(double tokenWithdrawalGiftcardWeekLimit) {
        this.tokenWithdrawalGiftcardWeekLimit = tokenWithdrawalGiftcardWeekLimit;
    }

    public double getTokenSendDayLimit() {
        return tokenSendDayLimit;
    }

    public void setTokenSendDayLimit(double tokenSendDayLimit) {
        this.tokenSendDayLimit = tokenSendDayLimit;
    }

    public double getTokenSendWeekLimit() {
        return tokenSendWeekLimit;
    }

    public void setTokenSendWeekLimit(double tokenSendWeekLimit) {
        this.tokenSendWeekLimit = tokenSendWeekLimit;
    }

    public double getTokenBuyBankDayLimit() {
        return tokenBuyBankDayLimit;
    }

    public void setTokenBuyBankDayLimit(double tokenBuyBankDayLimit) {
        this.tokenBuyBankDayLimit = tokenBuyBankDayLimit;
    }

    public double getTokenBuyBankWeeekLimit() {
        return tokenBuyBankWeeekLimit;
    }

    public void setTokenBuyBankWeeekLimit(double tokenBuyBankWeeekLimit) {
        this.tokenBuyBankWeeekLimit = tokenBuyBankWeeekLimit;
    }

    public double getTokenBuyCardDayLimit() {
        return tokenBuyCardDayLimit;
    }

    public void setTokenBuyCardDayLimit(double tokenBuyCardDayLimit) {
        this.tokenBuyCardDayLimit = tokenBuyCardDayLimit;
    }

    public double getTokenBuyCardWeekLimit() {
        return tokenBuyCardWeekLimit;
    }

    public void setTokenBuyCardWeekLimit(double tokenBuyCardWeekLimit) {
        this.tokenBuyCardWeekLimit = tokenBuyCardWeekLimit;
    }

    public double getTokenWithdrawalSignetDayLimit() {
        return tokenWithdrawalSignetDayLimit;
    }

    public void setTokenWithdrawalSignetDayLimit(double tokenWithdrawalSignetDayLimit) {
        this.tokenWithdrawalSignetDayLimit = tokenWithdrawalSignetDayLimit;
    }

    public double getTokenWithdrawalSignetWeekLimit() {
        return tokenWithdrawalSignetWeekLimit;
    }

    public void setTokenWithdrawalSignetWeekLimit(double tokenWithdrawalSignetWeekLimit) {
        this.tokenWithdrawalSignetWeekLimit = tokenWithdrawalSignetWeekLimit;
    }

    public static String getStrEncryptedPublicKey() {
        return strEncryptedPublicKey;
    }

    public void setStrEncryptedPublicKey(String strEncryptedPublicKey) {
        this.strEncryptedPublicKey = strEncryptedPublicKey;
    }

    public List<CardsDataItem> getListCards() {
        return listCards;
    }

    public void setListCards(List<CardsDataItem> listCards) {
        this.listCards = listCards;
    }

    public RetrieveUsersResponse getObjRetUsers() {
        return objRetUsers;
    }

    public void setObjRetUsers(RetrieveUsersResponse objRetUsers) {
        this.objRetUsers = objRetUsers;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    public String getStrRetrEmail() {
        return strRetrEmail;
    }

    public void setStrRetrEmail(String strRetrEmail) {
        this.strRetrEmail = strRetrEmail;
    }

    private String fromWhichFragment;

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

    public Boolean getLocalBiometric() {
        return isLocalBiometric;
    }

    public void setLocalBiometric(Boolean localBiometric) {
        isLocalBiometric = localBiometric;
    }

    public UpdatePhoneResponse getUpdatePhoneResponse() {
        return updatePhoneResponse;
    }

    public void setUpdatePhoneResponse(UpdatePhoneResponse updatePhoneResponse) {
        this.updatePhoneResponse = updatePhoneResponse;
    }

}

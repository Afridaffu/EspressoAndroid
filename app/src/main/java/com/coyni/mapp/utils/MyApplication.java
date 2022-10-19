package com.coyni.mapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.fee.Fees;
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.coyni.mapp.model.summary.BankAccount;
import com.coyni.mapp.model.websocket.WebSocketUrlResponseData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.model.BusinessBatchPayout.BatchPayoutListItems;
import com.coyni.mapp.model.check_out_transactions.CheckOutModel;
import com.coyni.mapp.model.featurecontrols.FeatureControlByUser;
import com.coyni.mapp.view.IdentityVerificationBindingLayoutActivity;
import com.coyni.mapp.model.check_out_transactions.OrderPayResponse;
import com.coyni.mapp.view.business.VerificationFailedActivity;
import com.coyni.mapp.model.AgreementsPdf;
import com.coyni.mapp.model.BeneficialOwners.BOResp;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoResp;
import com.coyni.mapp.model.DBAInfo.BusinessTypeResp;
import com.coyni.mapp.model.DBAInfo.DBAInfoResp;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.bank.SignOnData;
import com.coyni.mapp.model.business_id_verification.BusinessTrackerResponse;
import com.coyni.mapp.model.businesswallet.WalletResponseData;
import com.coyni.mapp.model.buytoken.BuyTokenRequest;
import com.coyni.mapp.model.buytoken.BuyTokenResponse;
import com.coyni.mapp.model.giftcard.BrandsResponse;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.paidorder.PaidOrderRequest;
import com.coyni.mapp.model.paidorder.PaidOrderResp;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.paymentmethods.PaymentsList;
import com.coyni.mapp.model.payrequest.PayRequestResponse;
import com.coyni.mapp.model.payrequest.TransferPayRequest;
import com.coyni.mapp.model.profile.Profile;
import com.coyni.mapp.model.profile.TrackerResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailResponse;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneResponse;
import com.coyni.mapp.model.reguser.Contacts;
import com.coyni.mapp.model.reguser.RegisteredUsersRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersResponse;
import com.coyni.mapp.model.submit.ApplicationSubmitResponseModel;
import com.coyni.mapp.model.transaction.TransactionList;
import com.coyni.mapp.model.transaction.TransactionListRequest;
import com.coyni.mapp.model.transferfee.TransferFeeResponse;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.model.withdraw.WithdrawRequest;
import com.coyni.mapp.model.withdraw.WithdrawResponse;
import com.coyni.mapp.view.DashboardActivity;
import com.coyni.mapp.view.business.BusinessDashboardActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyApplication extends Application {

    private UserData mCurrentUserData;
    private static Context context;
    private CheckOutModel checkOutModel;
    private List<States> listStates = new ArrayList<>();
    private String strMobileToken = "";
    private DatabaseHandler dbHandler;
    private String totalBuyAmountWithFee;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mCurrentUserData = new UserData();
    }

    public CheckOutModel getCheckOutModel() {
        return checkOutModel;
    }

    public void setCheckOutModel(CheckOutModel checkOutModel) {
        this.checkOutModel = checkOutModel;
    }

    public String getTotalBuyAmountWithFee() {
        return totalBuyAmountWithFee;
    }

    public void setTotalBuyAmountWithFee(String totalBuyAmountWithFee) {
        this.totalBuyAmountWithFee = totalBuyAmountWithFee;
    }

    public static Context getContext() {
        return context;
    }

    public UserData getCurrentUserData() {
        return mCurrentUserData;
    }

    public SignOnData getObjSignOnData() {
        return mCurrentUserData.getObjSignOnData();
    }

    public void setObjSignOnData(SignOnData objSignOnData) {
        mCurrentUserData.setObjSignOnData(objSignOnData);
    }

    public void setPaidOrderRequest(PaidOrderRequest paidOrderRequest) {
        mCurrentUserData.setPaidOrderRequest(paidOrderRequest);
    }

    public PaidOrderRequest getPaidOrderRequest() {
        return mCurrentUserData.getPaidOrderRequest();
    }

    public String getStrToken() {
        return mCurrentUserData.getStrToken();
    }

    public void setStrToken(String strToken) {
        mCurrentUserData.setStrToken(strToken);
    }

    public PaidOrderResp getPaidOrderResp() {
        return mCurrentUserData.getPaidOrderResp();
    }

    public void setPaidOrderResp(PaidOrderResp paidOrderResp) {
        mCurrentUserData.setPaidOrderResp(paidOrderResp);
    }

    public void setOrderPayResponse(OrderPayResponse orderPayResponse) {
        mCurrentUserData.setOrderPayResponse(orderPayResponse);
    }

    public OrderPayResponse getOrderPayResponse() {
        return mCurrentUserData.getOrderPayResponse();
    }

//    public Double getMerchantBalance() {
//        return mCurrentUserData.getMerchantBalance();
//    }
//
//    public void setMerchantBalance(Double merchantBalance) {
//        mCurrentUserData.setMerchantBalance(merchantBalance);
//    }

    public Integer getDbaOwnerId() {
        return mCurrentUserData.getDbaOwnerId();
    }

    public void setDbaOwnerId(int dbaOwnerId) {
        mCurrentUserData.setDbaOwnerId(dbaOwnerId);
    }

    public boolean isReserveEnabled() {
        return mCurrentUserData.isReserveEnabled();
    }

    public void setIsReserveEnabled(boolean isReserveEnabled) {
        mCurrentUserData.setIsReserveEnabled(isReserveEnabled);
    }

    public LatestTxnResponse getListLatestTxn() {
        return mCurrentUserData.getListLatestTxn();
    }

    public void setListLatestTxn(LatestTxnResponse listLatestTxn) {
        mCurrentUserData.setListLatestTxn(listLatestTxn);
    }

    public UserDetails getUserDetails() {
        return mCurrentUserData.getUserDetails();
    }

    public void setUserDetails(UserDetails userDetails) {
        mCurrentUserData.setUserDetails(userDetails);
    }

    public TransactionList getTransactionList() {
        return mCurrentUserData.getTransactionList();
    }

    public void setTransactionList(TransactionList transactionList) {
        mCurrentUserData.setTransactionList(transactionList);
    }

    public AgreementsPdf getAgreementsPdf() {
        return mCurrentUserData.getAgreementsPdf();
    }

    public void setAgreementsPdf(AgreementsPdf agreementsPdf) {
        mCurrentUserData.setAgreementsPdf(agreementsPdf);
    }

    public String getStrUserName() {
        return mCurrentUserData.getStrUserName();
    }

    public void setStrUserName(String strUserName) {
        mCurrentUserData.setStrUserName(strUserName);
    }

    public RetrieveUsersResponse getObjRetUsers() {
        return mCurrentUserData.getObjRetUsers();
    }

    public void setObjRetUsers(RetrieveUsersResponse objRetUsers) {
        mCurrentUserData.setObjRetUsers(objRetUsers);
    }

    public String getStrRetrEmail() {
        return mCurrentUserData.getStrRetrEmail();
    }

    public void setStrRetrEmail(String strRetrEmail) {
        mCurrentUserData.setStrRetrEmail(strRetrEmail);
    }

    public Profile getMyProfile() {
        return mCurrentUserData.getMyProfile();
    }

    public void setMyProfile(Profile myProfile) {
        mCurrentUserData.setMyProfile(myProfile);
    }

    public UpdateEmailResponse getUpdateEmailResponse() {
        return mCurrentUserData.getUpdateEmailResponse();
    }

    public void setUpdateEmailResponse(UpdateEmailResponse updateEmailResponse) {
        mCurrentUserData.setUpdateEmailResponse(updateEmailResponse);
    }

    public List<States> getListStates() {
        return listStates;
    }

    public void setListStates(List<States> listStates) {
        this.listStates = listStates;
    }

    public Boolean getBiometric() {
        return mCurrentUserData.getBiometric();
    }

    public void setBiometric(Boolean biometric) {
        mCurrentUserData.setBiometric(biometric);
    }

    public Boolean getLocalBiometric() {
        return mCurrentUserData.getLocalBiometric();
    }

    public void setLocalBiometric(Boolean localBiometric) {
        mCurrentUserData.setLocalBiometric(localBiometric);
    }

    public PaymentMethodsResponse getPaymentMethodsResponse() {
        return mCurrentUserData.getPaymentMethodsResponse();
    }

    public void setPaymentMethodsResponse(PaymentMethodsResponse paymentMethodsResponse) {
        mCurrentUserData.setPaymentMethodsResponse(paymentMethodsResponse);
    }

    public UpdatePhoneResponse getUpdatePhoneResponse() {
        return mCurrentUserData.getUpdatePhoneResponse();
    }

    public void setUpdatePhoneResponse(UpdatePhoneResponse updatePhoneResponse) {
        mCurrentUserData.setUpdatePhoneResponse(updatePhoneResponse);
    }

    public String getTimezone() {
        return mCurrentUserData.getTimezone();
    }

    public void setTimezone(String timezone) {
        mCurrentUserData.setTimezone(timezone);
    }

    public String getStrEmail() {
        return mCurrentUserData.getStrEmail();
    }

    public void setStrEmail(String strEmail) {
        mCurrentUserData.setStrEmail(strEmail);
    }

    public int getTimezoneID() {
        return mCurrentUserData.getTimezoneID();
    }

    public void setTimezoneID(int timezoneID) {
        mCurrentUserData.setTimezoneID(timezoneID);
    }

    public String getTempTimezone() {
        return mCurrentUserData.getTempTimezone();
    }

    public void setTempTimezone(String tempTimezone) {
        mCurrentUserData.setTempTimezone(tempTimezone);
    }

    public int getTempTimezoneID() {
        return mCurrentUserData.getTempTimezoneID();
    }

    public void setTempTimezoneID(int tempTimezoneID) {
        mCurrentUserData.setTempTimezoneID(tempTimezoneID);
    }

    public String getStrSignOnError() {
        return mCurrentUserData.getStrSignOnError();
    }

    public void setStrSignOnError(String strSignOnError) {
        mCurrentUserData.setStrSignOnError(strSignOnError);
    }

    public SignOnData getSignOnData() {
        return mCurrentUserData.getObjSignOnData();
    }

    public void setSignOnData(SignOnData objSignOnData) {
        mCurrentUserData.setObjSignOnData(objSignOnData);
    }

    public Boolean getResolveUrl() {
        return mCurrentUserData.getResolveUrl();
    }

    public void setResolveUrl(Boolean resolveUrl) {
        mCurrentUserData.setResolveUrl(resolveUrl);
    }

    public String getStrFiservError() {
        return mCurrentUserData.getStrFiservError();
    }

    public void setStrFiservError(String strFiservError) {
        mCurrentUserData.setStrFiservError(strFiservError);
    }

    public int getLoginUserId() {
        return mCurrentUserData.getLoginUserId();
    }

    public void setLoginUserId(int logUserId) {
        mCurrentUserData.setLoginUserId(logUserId);
    }

    public int getOldLoginUserId() {
        return mCurrentUserData.getOldLoginUserID();
    }

    public void setOldLoginUserId(int logUserId) {
        mCurrentUserData.setOldLoginUserID(logUserId);
    }

    public BiometricSignIn getLoginResponse() {
        return mCurrentUserData.getLoginResponse();
    }

    public void setLoginResponse(BiometricSignIn loginResponse) {
        mCurrentUserData.setLoginResponse(loginResponse);
    }

    public TrackerResponse getTrackerResponse() {
        return mCurrentUserData.getTrackerResponse();
    }

    public void setTrackerResponse(TrackerResponse trackerResponse) {
        mCurrentUserData.setTrackerResponse(trackerResponse);
    }

    public String getStrPreference() {
        return mCurrentUserData.getStrPreference();
    }

    public void setStrPreference(String strPreference) {
        mCurrentUserData.setStrPreference(strPreference);
    }

//    public WalletInfo getGbtWallet() {
//        return mCurrentUserData.getGbtWallet();
//    }
//
//    public void setGbtWallet(WalletInfo gbtWallet) {
//        mCurrentUserData.setGbtWallet(gbtWallet);
//    }

    public Double getGBTBalance() {
        return mCurrentUserData.getTokenGBTBalance();
    }


    public void setGBTBalance(Double gBTBalance, String walletType) {
        if (walletType.equals(Utils.TOKEN_STR)) {
            mCurrentUserData.setTokenGBTBalance(gBTBalance);
        } else if (walletType.equals(Utils.MERCHANT_STR)) {
            mCurrentUserData.setMerchnatGBTBalance(gBTBalance);
        } else if (walletType.equals(Utils.RESERVE_STR)) {
            mCurrentUserData.setReserveGBTBalance(gBTBalance);
        }
    }

//    public Double getReserveBalance() {
//        return mCurrentUserData.getReserveBalance();
//    }
//
//    public void setReserveBalance(Double reserveBalance) {
//        mCurrentUserData.setReserveBalance(reserveBalance);
//    }

    public PaymentsList getSelectedCard() {
        return mCurrentUserData.getSelectedCard();
    }

    public void setSelectedCard(PaymentsList selectedCard) {
        mCurrentUserData.setSelectedCard(selectedCard);
    }

    public PaymentsList getPrevSelectedCard() {
        return mCurrentUserData.getPrevSelectedCard();
    }

    public void setPrevSelectedCard(PaymentsList selectedCard) {
        mCurrentUserData.setPrevSelectedCard(selectedCard);
    }

    public String getStrStatesUrl() {
        return mCurrentUserData.getStrStatesUrl();
    }

    public void setStrStatesUrl(String strStatesUrl) {
        mCurrentUserData.setStrStatesUrl(strStatesUrl);
    }

    public String getRsaPublicKey() {
        return mCurrentUserData.getRsaPublicKey();
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        mCurrentUserData.setRsaPublicKey(rsaPublicKey);
    }

    public TransferFeeResponse getTransferFeeResponse() {
        return mCurrentUserData.getTransferFeeResponse();
    }

    public void setTransferFeeResponse(TransferFeeResponse transferFeeResponse) {
        mCurrentUserData.setTransferFeeResponse(transferFeeResponse);
    }

    public BrandsResponse getSelectedBrandResponse() {
        return mCurrentUserData.getSelectedBrandResponse();
    }

    public void setSelectedBrandResponse(BrandsResponse selectedBrandResponse) {
        mCurrentUserData.setSelectedBrandResponse(selectedBrandResponse);
    }

    public WithdrawRequest getWithdrawRequest() {
        return mCurrentUserData.getWithdrawRequest();
    }

    public void setWithdrawRequest(WithdrawRequest gcWithdrawRequest) {
        mCurrentUserData.setWithdrawRequest(gcWithdrawRequest);
    }

    public BuyTokenRequest getBuyRequest() {
        return mCurrentUserData.getBuyRequest();
    }

    public void setBuyRequest(BuyTokenRequest buyRequest) {
        mCurrentUserData.setBuyRequest(buyRequest);
    }

    public WithdrawResponse getWithdrawResponse() {
        return mCurrentUserData.getWithdrawResponse();
    }

    public void setWithdrawResponse(WithdrawResponse withdrawResponse) {
        mCurrentUserData.setWithdrawResponse(withdrawResponse);
    }

    public Boolean getContactPermission() {
        return mCurrentUserData.getContactPermission();
    }

    public void setContactPermission(Boolean contactPermission) {
        mCurrentUserData.setContactPermission(contactPermission);
    }

    public List<Contacts> getListContacts() {
        return mCurrentUserData.getListContacts();
    }

    public void setListContacts(List<Contacts> listContacts) {
        mCurrentUserData.setListContacts(listContacts);
    }

    public String getStrInvite() {
        return mCurrentUserData.getStrInvite();
    }

    public void setStrInvite(String strInvite) {
        mCurrentUserData.setStrInvite(strInvite);
    }

    public void initializeTransactionSearch() {
        mCurrentUserData.initializeTransactionSearch();
    }

    public TransactionListRequest getTransactionListSearch() {
        return mCurrentUserData.getTransactionListSearch();
    }

    public void setTransactionListSearch(TransactionListRequest transactionListSearch) {
        mCurrentUserData.setTransactionListSearch(transactionListSearch);
    }

    public BusinessTrackerResponse getBusinessTrackerResponse() {
        return mCurrentUserData.getBusinessTrackerResponse();
    }

    public void setBusinessTrackerResponse(BusinessTrackerResponse businessTrackerResponse) {
        mCurrentUserData.setBusinessTrackerResponse(businessTrackerResponse);
    }

    public BusinessTypeResp getBusinessTypeResp() {
        return mCurrentUserData.getBusinessTypeResp();
    }

    public void setBusinessTypeResp(BusinessTypeResp businessTypeResp) {
        mCurrentUserData.setBusinessTypeResp(businessTypeResp);
    }

    public CompanyInfoResp getCompanyInfoResp() {
        return mCurrentUserData.getCompanyInfoResp();
    }

    public void setCompanyInfoResp(CompanyInfoResp companyInfoResp) {
        mCurrentUserData.setCompanyInfoResp(companyInfoResp);
    }

    public DBAInfoResp getDbaInfoResp() {
        return mCurrentUserData.getDbaInfoResp();
    }

    public void setDbaInfoResp(DBAInfoResp dbaInfoResp) {
        mCurrentUserData.setDbaInfoResp(dbaInfoResp);
    }

    public BOResp getBeneficialOwnersResponse() {
        return mCurrentUserData.getBeneficialOwnersResponse();
    }

    public void setBeneficialOwnersResponse(BOResp beneficialOwnersResponse) {
        mCurrentUserData.setBeneficialOwnersResponse(beneficialOwnersResponse);
    }

    public String getStrMobileToken() {
//        return mCurrentUserData.getStrMobileToken();
        return strMobileToken;
    }

    public void setStrMobileToken(String strMobileToken) {
//        mCurrentUserData.setStrMobileToken(strMobileToken);
        this.strMobileToken = strMobileToken;
    }

    public BuyTokenResponse getBuyTokenResponse() {
        return mCurrentUserData.getBuyTokenResponse();
    }

    public void setBuyTokenResponse(BuyTokenResponse buyTokenResponse) {
        mCurrentUserData.setBuyTokenResponse(buyTokenResponse);
    }

    public HashMap<String, RegisteredUsersRequest> getObjPhContacts() {
        return mCurrentUserData.getObjPhContacts();
    }

    public void setObjPhContacts(HashMap<String, RegisteredUsersRequest> objPhContacts) {
        mCurrentUserData.setObjPhContacts(objPhContacts);
    }

    public ApplicationSubmitResponseModel getSubmitResponseModel() {
        return mCurrentUserData.getSubmitResponseModel();
    }

    public void setSubmitResponseModel(ApplicationSubmitResponseModel submitResponseModel) {
        mCurrentUserData.setSubmitResponseModel(submitResponseModel);
    }

    public String getStrRegisToken() {
        return mCurrentUserData.getStrRegisToken();
    }

    public void setStrRegisToken(String strRegisToken) {
        mCurrentUserData.setStrRegisToken(strRegisToken);
    }

    public String getSelectedButTokenType() {
        return mCurrentUserData.getSelectedButTokenType();
    }

    public void setSelectedButTokenType(String selectedButTokenType) {
        mCurrentUserData.setSelectedButTokenType(selectedButTokenType);
    }

    public TransferPayRequest getTransferPayRequest() {
        return mCurrentUserData.getTransferPayRequest();
    }

    public void setTransferPayRequest(TransferPayRequest transferPayRequest) {
        mCurrentUserData.setTransferPayRequest(transferPayRequest);
    }

    public Double getWithdrawAmount() {
        return mCurrentUserData.getWithdrawAmount();
    }

    public void setWithdrawAmount(Double withdrawAmount) {
        mCurrentUserData.setWithdrawAmount(withdrawAmount);
    }

    public PayRequestResponse getPayRequestResponse() {
        return mCurrentUserData.getPayRequestResponse();
    }

    public void setPayRequestResponse(PayRequestResponse payRequestResponse) {
        mCurrentUserData.setPayRequestResponse(payRequestResponse);
    }

    public String getStrScreen() {
        return mCurrentUserData.getStrScreen();
    }

    public void setStrScreen(String strScreen) {
        mCurrentUserData.setStrScreen(strScreen);
    }

    public Boolean getCardSave() {
        return mCurrentUserData.getCardSave();
    }

    public void setCardSave(Boolean cardSave) {
        mCurrentUserData.setCardSave(cardSave);
    }

    public Boolean getBankSave() {
        return mCurrentUserData.getBankSave();
    }

    public void setBankSave(Boolean bankSave) {
        mCurrentUserData.setBankSave(bankSave);
    }

    public int getAccountType() {
        return mCurrentUserData.getAccountType();
    }

    public Boolean getCogent() {
        return mCurrentUserData.getCogent();
    }

    public void setCogent(Boolean Cogent) {
        mCurrentUserData.setCogent(Cogent);
    }

    public Boolean getSignet() {
        return mCurrentUserData.getSignet();
    }

    public void setSignet(Boolean Signet) {
        mCurrentUserData.setSignet(Signet);
    }


    public void setAccountType(int accountType) {
        mCurrentUserData.setAccountType(accountType);
    }

    public void setWalletResponseData(WalletResponseData walletResponseData) {

        List<WalletInfo> walletInfoList = walletResponseData.getWalletNames();
        for (WalletInfo walletInfo : walletInfoList) {
            if (walletInfo.getWalletType() == null) {
                continue;
            }
            switch (walletInfo.getWalletType()) {
                case Utils.TOKEN_STR:
                    mCurrentUserData.setTokenWalletResponse(walletInfo);
                    break;
                case Utils.MERCHANT_STR:
                    mCurrentUserData.setMerchantWalletResponse(walletInfo);
                    break;
                case Utils.RESERVE_STR:
                    mCurrentUserData.setReserveWalletResponse(walletInfo);
                    break;
            }
        }
    }

    public Boolean isDeviceID() {
        Boolean value = false;
        SharedPreferences prefs = getSharedPreferences("DeviceID", MODE_PRIVATE);
        value = prefs.getBoolean("isDevice", false);
        if (value) {
            Utils.setDeviceID(prefs.getString("deviceId", ""));
        }
        return value;
    }

    public void getStates() {
        String json = null;
        try {
            InputStream is = getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(json, type);
            setListStates(listStates);
            Log.e("list states", listStates.size() + "");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void launchDeclinedActivity(Context context) {
        if (getAccountType() == Utils.BUSINESS_ACCOUNT || getAccountType() == Utils.SHARED_ACCOUNT) {
            Intent declinedIntent = new Intent(context, VerificationFailedActivity.class);
            declinedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(declinedIntent);
        } else if (getAccountType() == Utils.PERSONAL_ACCOUNT) {
            Intent intent = new Intent(context, IdentityVerificationBindingLayoutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("screen", "FAILED");
            startActivity(intent);
        }
    }

    public boolean checkForDeclinedStatus() {

        BiometricSignIn loginResponse = getLoginResponse();
        if (loginResponse != null && loginResponse.getStatus() != null
                && loginResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)
                && loginResponse.getData() != null
                && loginResponse.getData().getAccountStatus() != null) {
            if (getAccountType() == Utils.BUSINESS_ACCOUNT || getAccountType() == Utils.SHARED_ACCOUNT) {
                return loginResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus());
            } else if (getAccountType() == Utils.PERSONAL_ACCOUNT) {
                return loginResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus()) ||
                        loginResponse.getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus());
            }
        }
        return false;
    }

    public void launchDashboard(Context context, String fromScreen) {
        try {
            Intent dashboardIntent = new Intent(context, DashboardActivity.class);
            if (getAccountType() == Utils.BUSINESS_ACCOUNT || getAccountType() == Utils.SHARED_ACCOUNT) {
                dashboardIntent = new Intent(context, BusinessDashboardActivity.class);
            }
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dashboardIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*
     * Moving all these methods to Utils to decrease load on Application class
     *
     */

    public Bitmap convertImageURIToBitMap(String encodedString) {
        return Utils.convertImageURIToBitMap(getBaseContext(), encodedString);
    }

    public int monthsBetweenDates(Date startDate, Date endDate) {
        return Utils.monthsBetweenDates(startDate, endDate);
    }

    public PaymentMethodsResponse filterPaymentMethods(PaymentMethodsResponse objResponse) {
        return Utils.filterPaymentMethods(objResponse);
    }

    public Date getDate(String date) {
        return Utils.getDate(date);
    }

    public PaymentMethodsResponse businessPaymentMethods(PaymentMethodsResponse objResponse, String strScreen) {
        return Utils.businessPaymentMethods(getAccountType(), objResponse, strScreen);
    }

    public String setNameHead(String strName) {
        return Utils.setNameHead(strName);
    }

    public String transactionDate(String date) {
        return Utils.transactionDate(date, getStrPreference());
    }

    public String transactionTime(String date) {
        return Utils.transactionTime(date, getStrPreference());
    }

    public String reserveDate(String date) {
        return Utils.reserveDate(date, getStrPreference());
    }

    public String convertZoneDateTime(String date, String format, String requiredFormat) {
        return Utils.convertZoneDateTime(date, format, requiredFormat, getStrPreference());
    }

    public String exportDate(String date) {
        return Utils.exportDate(date, getStrPreference());
    }

    public String convertZoneLatestTxn(String date) {
        return Utils.convertZoneLatestTxn(date, getStrPreference());
    }

    public String convertZoneLatestTxndate(String date) {
        return Utils.convertZoneLatestTxndate(date, getStrPreference());
    }

    public String convertZoneReservedOn(String date) {
        return Utils.convertZoneReservedOn(date, getStrPreference());
    }

    public String convertNewZoneDate(String date) {
        return Utils.convertNewZoneDate(date, getStrPreference());
    }

    public String convertPayoutDateTimeZone(String date) throws ParseException {
        return Utils.convertPayoutDateTimeZone(date, getStrPreference());
    }

    public String convertZoneDateLastYear(String date) {
        return Utils.convertZoneDateLastYear(date, getStrPreference());
    }

    public void callResolveFlow(Activity activity, String strSignOn, SignOnData signOnData) {
        Utils.callResolveFlow(activity, strSignOn, signOnData);
    }

    public String convertNotificationTime(String date) {
        return Utils.convertNotificationTime(date, getStrPreference());
    }

    public void clearUserData() {
        mCurrentUserData = new UserData();
    }

    public void clearStrToken() {
        mCurrentUserData.setStrToken("");
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        mCurrentUserData.setIsLoggedIn(isLoggedIn);
    }

    public boolean isLoggedIn() {
        return mCurrentUserData.isLoggedIn();
    }

    public void initializeDBHandler(Context context) {
        dbHandler = DatabaseHandler.getInstance(context);
    }

    public boolean setTouchId() {
        boolean isTouchId = false;
        try {
            String value = dbHandler.getThumbPinLock();
            isTouchId = value != null && value.equals("true");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isTouchId;
    }

    public boolean setFaceLock() {
        boolean isFaceLock = false;
        try {
            String value = dbHandler.getFacePinLock();
            isFaceLock = value != null && value.equals("true");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isFaceLock;
    }

    public PaymentMethodsResponse filterCheckPaymentMethods(PaymentMethodsResponse objResponse) {
        PaymentMethodsResponse payMethodsResponse = objResponse;
        List<PaymentsList> listData = new ArrayList<>();
        try {
            if (objResponse != null && objResponse.getData() != null && objResponse.getData().getData() != null && objResponse.getData().getData().size() > 0) {
                for (int i = 0; i < objResponse.getData().getData().size(); i++) {
                    if (!objResponse.getData().getData().get(i).getPaymentMethod().equalsIgnoreCase("bank") && !objResponse.getData().getData().get(i).getPaymentMethod().equalsIgnoreCase("Cogent")) {
                        listData.add(objResponse.getData().getData().get(i));
                    }
                }
                payMethodsResponse.getData().setData(listData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return payMethodsResponse;
    }

    public String getBusinessUserID() {
        return mCurrentUserData.getBusinessUserID();
    }

    public void setBusinessUserID(String id) {
        mCurrentUserData.setBusinessUserID(id);
    }

    public String getOwnerImage() {
        return mCurrentUserData.getOwnerImage();
    }

    public void setOwnerImage(String ownerImage) {
        mCurrentUserData.setOwnerImage(ownerImage);
    }

    public List<BatchPayoutListItems> getBatchPayList() {
        return mCurrentUserData.getBatchPayList();
    }

    public void setBatchPayList(List<BatchPayoutListItems> batchPayList) {
        mCurrentUserData.setBatchPayList(batchPayList);
    }

    public FeatureControlByUser getFeatureControlByUser() {
        return mCurrentUserData.getFeatureControlByUser();
    }

    public void setFeatureControlByUser(FeatureControlByUser featureControlByUser) {
        mCurrentUserData.setFeatureControlByUser(featureControlByUser);
    }

    public FeatureControlByUser getFeatureControlGlobal() {
        return mCurrentUserData.getFeatureControlGlobal();
    }

    public void setFeatureControlGlobal(FeatureControlByUser featureControlGlobal) {
        mCurrentUserData.setFeatureControlGlobal(featureControlGlobal);
    }

    public Fees getFees() {
        return mCurrentUserData.getFees();
    }

    public void setFees(Fees fees) {
        mCurrentUserData.setFees(fees);
    }

    public WebSocketUrlResponseData getWebSocketUrlResponse() {
        return mCurrentUserData.getWebSocketUrlResponse();
    }

    public void setWebSocketUrlResponse(WebSocketUrlResponseData webSocketUrlResponse) {
        mCurrentUserData.setWebSocketUrlResponse(webSocketUrlResponse);

//        //Save isCogentEnabled and isSignetEnabled keys
//        if (webSocketUrlResponse.getIsCogentEnabled().equalsIgnoreCase("true"))
//            mCurrentUserData.setCogentEnabled(true);
//        else if (webSocketUrlResponse.getIsCogentEnabled().equalsIgnoreCase("false"))
//            mCurrentUserData.setCogentEnabled(false);
//
//        if (webSocketUrlResponse.getIsSignetEnabled().equalsIgnoreCase("true"))
//            mCurrentUserData.setSignetEnabled(true);
//        else if (webSocketUrlResponse.getIsSignetEnabled().equalsIgnoreCase("false"))
//            mCurrentUserData.setSignetEnabled(false);
    }

    public boolean isCogentEnabled() {
        return mCurrentUserData.isCogentEnabled();
    }

    public boolean isSignetEnabled() {
        return mCurrentUserData.isSignetEnabled();
    }

    public String getCompanyName() {
        return mCurrentUserData.getCompanyName();
    }

    public void setCompanyName(String companyName) {
        mCurrentUserData.setCompanyName(companyName);
    }

    public BankAccount getBankAccount() {
        return mCurrentUserData.getBankAccount();
    }

    public void setBankAccount(BankAccount bankAccount) {
        mCurrentUserData.setBankAccount(bankAccount);
    }

    public Boolean buyFeatureCtrlEnabled(PaymentsList objData) {
        Boolean isValue = false;
        try {
            switch (objData.getPaymentMethod().toLowerCase()) {
                case "bank":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getBuyBank() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getBuyBank() && getFeatureControlByUser().getBuyBank())) {
                        isValue = true;
                    }
                    break;
                case "signet":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getBuySignet() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getBuySignet() && getFeatureControlByUser().getBuySignet())) {
                        isValue = true;
                    }
                    break;
                case "debit":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getBuyDebit() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getBuyDebit() && getFeatureControlByUser().getBuyDebit())) {
                        isValue = true;
                    }
                    break;
                case "credit":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getBuyCredit() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getBuyCredit() && getFeatureControlByUser().getBuyCredit())) {
                        isValue = true;
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isValue;
    }

    public Boolean withFeatureCtrlEnabled(PaymentsList objData) {
        Boolean isValue = false;
        try {
            switch (objData.getPaymentMethod().toLowerCase()) {
                case "bank":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getWithBank() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getWithBank() && getFeatureControlByUser().getWithBank())) {
                        isValue = true;
                    }
                    break;
                case "signet":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getWithSignet() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getWithSignet() && getFeatureControlByUser().getWithSignet())) {
                        isValue = true;
                    }
                    break;
                case "debit":
                    if (getFeatureControlGlobal() != null && getFeatureControlGlobal().getWithInstant() != null && getFeatureControlByUser() != null
                            && (getFeatureControlGlobal().getWithInstant() && getFeatureControlByUser().getWithInstant())) {
                        isValue = true;
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isValue;
    }

}

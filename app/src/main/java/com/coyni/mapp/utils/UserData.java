package com.coyni.mapp.utils;

import com.coyni.mapp.model.AgreementsPdf;
import com.coyni.mapp.model.BeneficialOwners.BOResp;
import com.coyni.mapp.model.BusinessBatchPayout.BatchPayoutListItems;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoResp;
import com.coyni.mapp.model.DBAInfo.BusinessTypeResp;
import com.coyni.mapp.model.DBAInfo.DBAInfoResp;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.bank.ManualBankRequest;
import com.coyni.mapp.model.bank.SignOnData;
import com.coyni.mapp.model.business_id_verification.BusinessTrackerResponse;
import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.businesswallet.WalletResponseData;
import com.coyni.mapp.model.buytoken.BuyTokenRequest;
import com.coyni.mapp.model.buytoken.BuyTokenResponse;
import com.coyni.mapp.model.check_out_transactions.OrderPayResponse;
import com.coyni.mapp.model.featurecontrols.FeatureControlByUser;
import com.coyni.mapp.model.fee.Fees;
import com.coyni.mapp.model.giftcard.BrandsResponse;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.merchant_activity.Earning;
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
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.coyni.mapp.model.submit.ApplicationSubmitResponseModel;
import com.coyni.mapp.model.summary.BankAccount;
import com.coyni.mapp.model.transaction.TransactionList;
import com.coyni.mapp.model.transaction.TransactionListRequest;
import com.coyni.mapp.model.transferfee.TransferFeeResponse;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.model.websocket.WebSocketUrlResponseData;
import com.coyni.mapp.model.withdraw.WithdrawRequest;
import com.coyni.mapp.model.withdraw.WithdrawResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserData {

    private AgreementsPdf agreementsPdf;
    private RetrieveUsersResponse objRetUsers = new RetrieveUsersResponse();
    private String strUserName = "", strRetrEmail = "", strEmail = "", strSignOnError = "", strFiservError = "", strPreference = "CST", strInvite = "", strScreen = "";
    private Profile myProfile = new Profile();
    private Fees fees = new Fees();
    private UpdateEmailResponse updateEmailResponse = new UpdateEmailResponse();
    private UpdatePhoneResponse updatePhoneResponse = new UpdatePhoneResponse();
    private UserDetails userDetails;
    private List<States> listStates = new ArrayList<>();
    private LatestTxnResponse listLatestTxn;
    //isBiometric - OS level on/off;  isLocalBiometric - LocalDB value
    private Boolean isBiometric = false, isLocalBiometric = false, isResolveUrl = false, isContactPermission = true, isCardSave = false, isSignet = false, isBankSave = false;
    private PaymentMethodsResponse paymentMethodsResponse;
    //    WalletResponse walletResponse;
    private String timezone = "", tempTimezone = "Pacific (PST)", strStatesUrl = "", rsaPublicKey = "", strMobileToken = "", strRegisToken = "";
    private int timezoneID = 0, tempTimezoneID = 0, loginUserId, oldLoginUserID, accountType, dbaOwnerId = 0;
    private BiometricSignIn loginResponse;
    private TransactionList transactionList;
    private PaymentsList selectedCard, prevSelectedCard;
    private TransferFeeResponse transferFeeResponse;
    private BrandsResponse selectedBrandResponse;
    private WithdrawRequest withdrawRequest;
    private WithdrawResponse withdrawResponse;
    private BuyTokenResponse buyTokenResponse;
    private PayRequestResponse payRequestResponse;
    private TransferPayRequest transferPayRequest;
    private PaidOrderRequest paidOrderRequest;
    private PaidOrderResp paidOrderResp;
    private OrderPayResponse orderPayResponse;
    private List<Contacts> listContacts = new ArrayList<>();
    private TransactionListRequest transactionListSearch = new TransactionListRequest();
    private Double withdrawAmount;
    private BusinessTrackerResponse businessTrackerResponse;
    private WalletResponseData walletResponseData, tokenWalletResponse, merchantWalletResponse, reserveWalletResponse;
    private BusinessTypeResp businessTypeResp;
    private CompanyInfoResp companyInfoResp;
    private DBAInfoResp dbaInfoResp;
    private BuyTokenRequest buyRequest;
    private BOResp beneficialOwnersResponse;
    private HashMap<String, RegisteredUsersRequest> objPhContacts = new HashMap<>();
    private ApplicationSubmitResponseModel submitResponseModel;
    private Double merchantBalance = 0.0;
    private WalletInfo gbtWallet;
    private Double tokenGBTBalance = 0.0, merchnatGBTBalance = 0.0, reserveGBTBalance = 0.0;
    private Double reserveBalance = 0.0;
    private SignOnData objSignOnData = new SignOnData();
    private TrackerResponse trackerResponse = new TrackerResponse();
    private String selectedButTokenType = "";
    private List<Earning> earningList;
    boolean isReserveEnabled = false;
    private String strToken;
    private boolean isLoggedIn = false;
    private String businessUserID = "";
    private String ownerImage = "";
    private List<BatchPayoutListItems> batchPayList = new ArrayList<>();
    private FeatureControlByUser featureControlByUser;
    private FeatureControlByUser featureControlGlobal;
    private WebSocketUrlResponseData webSocketUrlResponse;
    private BankAccount bankAccount;

    public OrderPayResponse getOrderPayResponse() {
        return orderPayResponse;
    }

    public void setOrderPayResponse(OrderPayResponse orderPayResponse) {
        this.orderPayResponse = orderPayResponse;
    }

    public String getStrToken() {
        return strToken;
    }

    public void setStrToken(String strToken) {
        this.strToken = strToken;
    }

    public List<Earning> getEarningList() {
        return earningList;
    }

    public void setEarningList(List<Earning> earningList) {
        this.earningList = earningList;
    }

    public AgreementsPdf getAgreementsPdf() {
        return agreementsPdf;
    }

    public void setAgreementsPdf(AgreementsPdf agreementsPdf) {
        this.agreementsPdf = agreementsPdf;
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

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrSignOnError() {
        return strSignOnError;
    }

    public void setStrSignOnError(String strSignOnError) {
        this.strSignOnError = strSignOnError;
    }

    public String getStrFiservError() {
        return strFiservError;
    }

    public void setStrFiservError(String strFiservError) {
        this.strFiservError = strFiservError;
    }

    public String getStrPreference() {
        return strPreference;
    }

    public void setStrPreference(String strPreference) {
        this.strPreference = strPreference;
    }

    public String getStrInvite() {
        return strInvite;
    }

    public void setStrInvite(String strInvite) {
        this.strInvite = strInvite;
    }

    public String getStrScreen() {
        return strScreen;
    }

    public void setStrScreen(String strScreen) {
        this.strScreen = strScreen;
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

    public UpdatePhoneResponse getUpdatePhoneResponse() {
        return updatePhoneResponse;
    }

    public void setUpdatePhoneResponse(UpdatePhoneResponse updatePhoneResponse) {
        this.updatePhoneResponse = updatePhoneResponse;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public LatestTxnResponse getListLatestTxn() {
        return listLatestTxn;
    }

    public void setListLatestTxn(LatestTxnResponse listLatestTxn) {
        this.listLatestTxn = listLatestTxn;
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

    public Boolean getResolveUrl() {
        return isResolveUrl;
    }

    public void setResolveUrl(Boolean resolveUrl) {
        isResolveUrl = resolveUrl;
    }

    public Boolean getContactPermission() {
        return isContactPermission;
    }

    public void setContactPermission(Boolean contactPermission) {
        isContactPermission = contactPermission;
    }

    public Boolean getCardSave() {
        return isCardSave;
    }

    public void setCardSave(Boolean cardSave) {
        isCardSave = cardSave;
    }

    public Boolean getSignet() {
        return isSignet;
    }

    public void setSignet(Boolean signet) {
        isSignet = signet;
    }

    public Boolean getBankSave() {
        return isBankSave;
    }

    public void setBankSave(Boolean bankSave) {
        isBankSave = bankSave;
    }

    public PaymentMethodsResponse getPaymentMethodsResponse() {
        return paymentMethodsResponse;
    }

    public void setPaymentMethodsResponse(PaymentMethodsResponse paymentMethodsResponse) {
        this.paymentMethodsResponse = paymentMethodsResponse;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTempTimezone() {
        return tempTimezone;
    }

    public void setTempTimezone(String tempTimezone) {
        this.tempTimezone = tempTimezone;
    }

    public String getStrStatesUrl() {
        return strStatesUrl;
    }

    public void setStrStatesUrl(String strStatesUrl) {
        this.strStatesUrl = strStatesUrl;
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getStrMobileToken() {
        return strMobileToken;
    }

    public void setStrMobileToken(String strMobileToken) {
        this.strMobileToken = strMobileToken;
    }

    public String getStrRegisToken() {
        return strRegisToken;
    }

    public void setStrRegisToken(String strRegisToken) {
        this.strRegisToken = strRegisToken;
    }

    public int getTimezoneID() {
        return timezoneID;
    }

    public void setTimezoneID(int timezoneID) {
        this.timezoneID = timezoneID;
    }

    public int getTempTimezoneID() {
        return tempTimezoneID;
    }

    public void setTempTimezoneID(int tempTimezoneID) {
        this.tempTimezoneID = tempTimezoneID;
    }

    public int getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(int loginUserId) {
        this.loginUserId = loginUserId;
    }

    public int getOldLoginUserID() {
        return oldLoginUserID;
    }

    public void setOldLoginUserID(int oldLoginUserID) {
        this.oldLoginUserID = oldLoginUserID;
    }

    public BiometricSignIn getLoginResponse() {
        return loginResponse;
    }

    public void setLoginResponse(BiometricSignIn response) {
        this.loginResponse = response;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getDbaOwnerId() {
        return dbaOwnerId;
    }

    public void setDbaOwnerId(int dbaOwnerId) {
        this.dbaOwnerId = dbaOwnerId;
    }

    public TransactionList getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(TransactionList transactionList) {
        this.transactionList = transactionList;
    }

    public PaymentsList getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(PaymentsList selectedCard) {
        this.selectedCard = selectedCard;
    }

    public PaymentsList getPrevSelectedCard() {
        return prevSelectedCard;
    }

    public void setPrevSelectedCard(PaymentsList prevSelectedCard) {
        this.prevSelectedCard = prevSelectedCard;
    }

    public TransferFeeResponse getTransferFeeResponse() {
        return transferFeeResponse;
    }

    public void setTransferFeeResponse(TransferFeeResponse transferFeeResponse) {
        this.transferFeeResponse = transferFeeResponse;
    }

    public BrandsResponse getSelectedBrandResponse() {
        return selectedBrandResponse;
    }

    public void setSelectedBrandResponse(BrandsResponse selectedBrandResponse) {
        this.selectedBrandResponse = selectedBrandResponse;
    }

    public WithdrawRequest getWithdrawRequest() {
        return withdrawRequest;
    }

    public void setWithdrawRequest(WithdrawRequest withdrawRequest) {
        this.withdrawRequest = withdrawRequest;
    }

    public WithdrawResponse getWithdrawResponse() {
        return withdrawResponse;
    }

    public void setWithdrawResponse(WithdrawResponse withdrawResponse) {
        this.withdrawResponse = withdrawResponse;
    }

    public BuyTokenResponse getBuyTokenResponse() {
        return buyTokenResponse;
    }

    public void setBuyTokenResponse(BuyTokenResponse buyTokenResponse) {
        this.buyTokenResponse = buyTokenResponse;
    }

    public PayRequestResponse getPayRequestResponse() {
        return payRequestResponse;
    }

    public void setPayRequestResponse(PayRequestResponse payRequestResponse) {
        this.payRequestResponse = payRequestResponse;
    }

    public TransferPayRequest getTransferPayRequest() {
        return transferPayRequest;
    }

    public void setTransferPayRequest(TransferPayRequest transferPayRequest) {
        this.transferPayRequest = transferPayRequest;
    }

    public PaidOrderRequest getPaidOrderRequest() {
        return paidOrderRequest;
    }

    public void setPaidOrderRequest(PaidOrderRequest paidOrderRequest) {
        this.paidOrderRequest = paidOrderRequest;
    }

    public PaidOrderResp getPaidOrderResp() {
        return paidOrderResp;
    }

    public void setPaidOrderResp(PaidOrderResp paidOrderResp) {
        this.paidOrderResp = paidOrderResp;
    }

    public List<Contacts> getListContacts() {
        return listContacts;
    }

    public void setListContacts(List<Contacts> listContacts) {
        this.listContacts = listContacts;
    }

    public void initializeTransactionSearch() {
        transactionListSearch = new TransactionListRequest();
    }

    public TransactionListRequest getTransactionListSearch() {
        return transactionListSearch;
    }

    public void setTransactionListSearch(TransactionListRequest transactionListSearch) {
        this.transactionListSearch = transactionListSearch;
    }

    public Double getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(Double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public BusinessTrackerResponse getBusinessTrackerResponse() {
        return businessTrackerResponse;
    }

    public void setBusinessTrackerResponse(BusinessTrackerResponse businessTrackerResponse) {
        this.businessTrackerResponse = businessTrackerResponse;
    }

    public WalletResponseData getWalletResponseData() {
        return walletResponseData;
    }

    public void setWalletResponseData(WalletResponseData walletResponseData) {
        this.walletResponseData = walletResponseData;
    }

    public BusinessTypeResp getBusinessTypeResp() {
        return businessTypeResp;
    }

    public void setBusinessTypeResp(BusinessTypeResp businessTypeResp) {
        this.businessTypeResp = businessTypeResp;
    }

    public CompanyInfoResp getCompanyInfoResp() {
        return companyInfoResp;
    }

    public void setCompanyInfoResp(CompanyInfoResp companyInfoResp) {
        this.companyInfoResp = companyInfoResp;
    }

    public DBAInfoResp getDbaInfoResp() {
        return dbaInfoResp;
    }

    public void setDbaInfoResp(DBAInfoResp dbaInfoResp) {
        this.dbaInfoResp = dbaInfoResp;
    }

    public BuyTokenRequest getBuyRequest() {
        return buyRequest;
    }

    public void setBuyRequest(BuyTokenRequest buyRequest) {
        this.buyRequest = buyRequest;
    }

    public BOResp getBeneficialOwnersResponse() {
        return beneficialOwnersResponse;
    }

    public void setBeneficialOwnersResponse(BOResp beneficialOwnersResponse) {
        this.beneficialOwnersResponse = beneficialOwnersResponse;
    }

    public HashMap<String, RegisteredUsersRequest> getObjPhContacts() {
        return objPhContacts;
    }

    public void setObjPhContacts(HashMap<String, RegisteredUsersRequest> objPhContacts) {
        this.objPhContacts = objPhContacts;
    }

    public ApplicationSubmitResponseModel getSubmitResponseModel() {
        return submitResponseModel;
    }

    public void setSubmitResponseModel(ApplicationSubmitResponseModel submitResponseModel) {
        this.submitResponseModel = submitResponseModel;
    }

    public Double getMerchantBalance() {
        return merchantBalance;
    }

    public void setMerchantBalance(Double merchantBalance) {
        this.merchantBalance = merchantBalance;
    }

    public WalletInfo getGbtWallet() {
        return gbtWallet;
    }

    public void setGbtWallet(WalletInfo gbtWallet) {
        this.gbtWallet = gbtWallet;
    }

//    public Double getGBTBalance() {
//        return GBTBalance;
//    }
//
//    public void setGBTBalance(Double GBTBalance) {
//        this.GBTBalance = GBTBalance;
//    }

    public Double getReserveBalance() {
        return reserveBalance;
    }

    public void setReserveBalance(Double reserveBalance) {
        this.reserveBalance = reserveBalance;
    }

    public SignOnData getObjSignOnData() {
        return objSignOnData;
    }

    public void setObjSignOnData(SignOnData objSignOnData) {
        this.objSignOnData = objSignOnData;
    }

    public TrackerResponse getTrackerResponse() {
        return trackerResponse;
    }

    public void setTrackerResponse(TrackerResponse trackerResponse) {
        this.trackerResponse = trackerResponse;
    }

    public String getSelectedButTokenType() {
        return selectedButTokenType;
    }

    public void setSelectedButTokenType(String selectedButTokenType) {
        this.selectedButTokenType = selectedButTokenType;
    }

    public WalletResponseData getTokenWalletResponse() {
        return tokenWalletResponse;
    }

    public void setTokenWalletResponse(WalletResponseData tokenWalletResponse) {
        this.tokenWalletResponse = tokenWalletResponse;
    }

    public WalletResponseData getMerchantWalletResponse() {
        return merchantWalletResponse;
    }

    public void setMerchantWalletResponse(WalletResponseData merchantWalletResponse) {
        this.merchantWalletResponse = merchantWalletResponse;
    }

    public WalletResponseData getReserveWalletResponse() {
        return reserveWalletResponse;
    }

    public void setReserveWalletResponse(WalletResponseData reserveWalletResponse) {
        this.reserveWalletResponse = reserveWalletResponse;
    }

    public Double getTokenGBTBalance() {
        return tokenGBTBalance;
    }

    public boolean isReserveEnabled() {
        return isReserveEnabled;
    }

    public void setTokenGBTBalance(Double tokenGBTBalance) {
        this.tokenGBTBalance = tokenGBTBalance;
    }

    public Double getMerchnatGBTBalance() {
        return merchnatGBTBalance;
    }

    public void setMerchnatGBTBalance(Double merchnatGBTBalance) {
        this.merchnatGBTBalance = merchnatGBTBalance;
    }

    public Double getReserveGBTBalance() {
        return reserveGBTBalance;
    }

    public void setReserveGBTBalance(Double reserveGBTBalance) {
        this.reserveGBTBalance = reserveGBTBalance;
    }

    public void setIsReserveEnabled(boolean isReserveEnabled) {
        this.isReserveEnabled = isReserveEnabled;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getBusinessUserID() {
        return businessUserID;
    }

    public void setBusinessUserID(String businessUserID) {
        this.businessUserID = businessUserID;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }

    public List<BatchPayoutListItems> getBatchPayList() {
        return batchPayList;
    }

    public void setBatchPayList(List<BatchPayoutListItems> batchPayList) {
        this.batchPayList = batchPayList;
    }

    public FeatureControlByUser getFeatureControlByUser() {
        return featureControlByUser;
    }

    public void setFeatureControlByUser(FeatureControlByUser featureControlByUser) {
        this.featureControlByUser = featureControlByUser;
    }

    public FeatureControlByUser getFeatureControlGlobal() {
        return featureControlGlobal;
    }

    public void setFeatureControlGlobal(FeatureControlByUser featureControlGlobal) {
        this.featureControlGlobal = featureControlGlobal;
    }

    public Fees getFees() {
        return fees;
    }

    public void setFees(Fees fees) {
        this.fees = fees;
    }

    public WebSocketUrlResponseData getWebSocketUrlResponse() {
        return webSocketUrlResponse;
    }

    public void setWebSocketUrlResponse(WebSocketUrlResponseData webSocketUrlResponse) {
        this.webSocketUrlResponse = webSocketUrlResponse;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}

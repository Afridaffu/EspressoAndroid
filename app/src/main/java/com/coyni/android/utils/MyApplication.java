package com.coyni.android.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.coyni.android.R;
import com.coyni.android.model.APIError;
import com.coyni.android.model.States;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.bank.SignOnData;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.model.contacts.Contacts;
import com.coyni.android.model.export.ExportColumns;
import com.coyni.android.model.notification.NotificationsDataItems;
import com.coyni.android.model.transactions.TokenTransactions;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.user.Agreements;
import com.coyni.android.model.user.AgreementsData;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.wallet.WalletInfo;
import com.coyni.android.model.wallet.WalletResponse;
import com.coyni.android.view.LoginActivity;
import com.coyni.android.view.MainActivity;
import com.coyni.android.view.SplashActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;

public class MyApplication extends Application {
    String strEmail, strLang, strCode, strUser = "", strUserCode, strInvite = "", strLEmail = "", strLPwd = "";
    WalletResponse walletResponse;
    UserTracker userTracker;
    WalletInfo gbtWallet;
    WalletInfo assetInfo;
    TransferFeeResponse transferFeeResponse;
    CardsDataItem selectedCard;
    BanksDataItem selectedBank, editSignet;
    Double GBTBalance = 0.0;
    int statusId = -1, typeId = -1, dateId = 0;
    List<String> selectedListColumns = new ArrayList<>();
    Map<String, String> filtersMap = new HashMap<>();
    ExportColumns exportColumns;
    String strCvv = "", strSignOnError = "", strSignet = "", strFiservError = "", strPreference = "";
    List<BanksDataItem> listBanks = new ArrayList<>();
    List<BanksDataItem> signetBanks = new ArrayList<>();
    SignOnData objSignOnData = new SignOnData();
    List<String> listSelNotifications = new ArrayList<>();
    Boolean isResolveUrl = false, isCoyniPin = false, isNotiAvailable = false, isReqAvailable = false;
    List<States> listStates = new ArrayList<>();
    static String strEncryptedPublicKey;
    List<CardsDataItem> listCards = new ArrayList<>();
    List<NotificationsDataItems> notificationsDataItems = new ArrayList<>();
    Dialog popupInActive, popupSession;
    DashboardViewModel dashboardViewModel;
    Handler handler = new Handler();
    Handler appHandler = new Handler();
    Runnable myRunnable, appRunnable;
    List<Contacts> listContacts = new ArrayList<>();
    SQLiteDatabase mydatabase;
    Cursor dsFacePin;
    Boolean isFaceLock = false, isLogout = false, isLoginBack = false, isToken = false, isTrackerComplete = false;
    TokenTransactions tokenTransactions;

    //Nishanth
    private int id;
    private int signatureType;
    private int refId;
    private String userId;
    private String signature;
    private String signedOn;
    private String ipAddress;
    Agreements agreements;


    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrLang() {
        return strLang;
    }

    public void setStrLang(String strLang) {
        this.strLang = strLang;
    }

    public String getStrCode() {
        return strCode;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public String getStrUser() {
        return strUser;
    }

    public void setStrUser(String strUser) {
        this.strUser = strUser;
    }

    public String getStrUserCode() {
        return strUserCode;
    }

    public void setStrUserCode(String strUserCode) {
        this.strUserCode = strUserCode;
    }

    public WalletResponse getWalletResponse() {
        return walletResponse;
    }

    public void setWalletResponse(WalletResponse walletResponse) {
        this.walletResponse = walletResponse;
    }

    public UserTracker getUserTracker() {
        return userTracker;
    }

    public void setUserTracker(UserTracker userTracker) {
        this.userTracker = userTracker;
    }

    public WalletInfo getGbtWallet() {
        return gbtWallet;
    }

    public void setGbtWallet(WalletInfo gbtWallet) {
        this.gbtWallet = gbtWallet;
    }

    public WalletInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(WalletInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public TransferFeeResponse getTransferFeeResponse() {
        return transferFeeResponse;
    }

    public void setTransferFeeResponse(TransferFeeResponse transferFeeResponse) {
        this.transferFeeResponse = transferFeeResponse;
    }

    public CardsDataItem getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(CardsDataItem selectedCard) {
        this.selectedCard = selectedCard;
    }

    public Double getGBTBalance() {
        return GBTBalance;
    }

    public void setGBTBalance(Double GBTBalance) {
        this.GBTBalance = GBTBalance;
    }

    public BanksDataItem getSelectedBank() {
        return selectedBank;
    }

    public void setSelectedBank(BanksDataItem selectedBank) {
        this.selectedBank = selectedBank;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getDateId() {
        return dateId;
    }

    public void setDateId(int dateId) {
        this.dateId = dateId;
    }

    public List<String> getSelectedListColumns() {
        return selectedListColumns;
    }

    public void setSelectedListColumns(List<String> selectedListColumns) {
        this.selectedListColumns = selectedListColumns;
    }

    public void initializeListColumns() {
        this.selectedListColumns = new ArrayList<>();
    }

    public Map<String, String> getFiltersMap() {
        return filtersMap;
    }

    public void setFiltersMap(Map<String, String> filtersMap) {
        this.filtersMap = filtersMap;
    }

    public ExportColumns getExportColumns() {
        return exportColumns;
    }

    public void setExportColumns(ExportColumns exportColumns) {
        this.exportColumns = exportColumns;
    }

    public String getStrInvite() {
        return strInvite;
    }

    public void setStrInvite(String strInvite) {
        this.strInvite = strInvite;
    }

    public String getStrCvv() {
        return strCvv;
    }

    public void setStrCvv(String strCvv) {
        this.strCvv = strCvv;
    }


    public List<BanksDataItem> getListBanks() {
        return listBanks;
    }

    public void setListBanks(List<BanksDataItem> listBanks) {
        this.listBanks = listBanks;
    }

    public List<BanksDataItem> getSignetBanks() {
        return signetBanks;
    }

    public void setSignetBanks(List<BanksDataItem> signetBanks) {
        this.signetBanks = signetBanks;
    }

    public SignOnData getSignOnData() {
        return objSignOnData;
    }

    public void setSignOnData(SignOnData objSignOnData) {
        this.objSignOnData = objSignOnData;
    }

    public String getStrSignOnError() {
        return strSignOnError;
    }

    public void setStrSignOnError(String strSignOnError) {
        this.strSignOnError = strSignOnError;
    }

    public BanksDataItem getEditSignet() {
        return editSignet;
    }

    public void setEditSignet(BanksDataItem editSignet) {
        this.editSignet = editSignet;
    }

    public String getStrSignet() {
        return strSignet;
    }

    public void setStrSignet(String strSignet) {
        this.strSignet = strSignet;
    }

    public List<String> getListSelNotifications() {
        return listSelNotifications;
    }

    public void setListSelNotifications(List<String> listSelNotifications) {
        this.listSelNotifications = listSelNotifications;
    }

    public Boolean getResolveUrl() {
        return isResolveUrl;
    }

    public void setResolveUrl(Boolean resolveUrl) {
        isResolveUrl = resolveUrl;
    }

    public String getStrFiservError() {
        return strFiservError;
    }

    public void setStrFiservError(String strFiservError) {
        this.strFiservError = strFiservError;
    }

    public List<States> getListStates() {
        return listStates;
    }

    public void setListStates(List<States> listStates) {
        this.listStates = listStates;
    }

    public Boolean getCoyniPin() {
        return isCoyniPin;
    }

    public void setCoyniPin(Boolean coyniPin) {
        isCoyniPin = coyniPin;
    }

    public String getStrPreference() {
        return strPreference;
    }

    public void setStrPreference(String strPreference) {
        this.strPreference = strPreference;
    }

    public List<NotificationsDataItems> getNotificationsDataItems() {
        return notificationsDataItems;
    }

    public void setNotificationsDataItems(List<NotificationsDataItems> notificationsDataItems) {
        this.notificationsDataItems = notificationsDataItems;
    }

    public Boolean getNotiAvailable() {
        return isNotiAvailable;
    }

    public void setNotiAvailable(Boolean notiAvailable) {
        isNotiAvailable = notiAvailable;
    }

    public Boolean getReqAvailable() {
        return isReqAvailable;
    }

    public void setReqAvailable(Boolean reqAvailable) {
        isReqAvailable = reqAvailable;
    }

    public Handler getHandler() {
        return handler;
    }

    public Runnable getMyRunnable() {
        return myRunnable;
    }

    public Handler getAppHandler() {
        return appHandler;
    }

    public Runnable getAppRunnable() {
        return appRunnable;
    }

    public Boolean getLogout() {
        return isLogout;
    }

    public void setLogout(Boolean logout) {
        isLogout = logout;
    }

    public String getStrLEmail() {
        return strLEmail;
    }

    public void setStrLEmail(String strLEmail) {
        this.strLEmail = strLEmail;
    }

    public String getStrLPwd() {
        return strLPwd;
    }

    public void setStrLPwd(String strLPwd) {
        this.strLPwd = strLPwd;
    }

    public List<Contacts> getListContacts() {
        return listContacts;
    }

    public void setListContacts(List<Contacts> listContacts) {
        this.listContacts = listContacts;
    }

    public Boolean getLoginBack() {
        return isLoginBack;
    }

    public void setLoginBack(Boolean loginBack) {
        isLoginBack = loginBack;
    }

    public Boolean getToken() {
        return isToken;
    }

    public void setToken(Boolean token) {
        isToken = token;
    }

    public Boolean getTrackerComplete() {
        return isTrackerComplete;
    }

    public void setTrackerComplete(Boolean trackerComplete) {
        isTrackerComplete = trackerComplete;
    }

    public TokenTransactions getTokenTransactions() {
        return tokenTransactions;
    }

    public void setTokenTransactions(TokenTransactions tokenTransactions) {
        this.tokenTransactions = tokenTransactions;
    }

    public String transactionDate(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public String compareTransactionDate(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public String transactionTime(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public String convertZoneDate(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public String exportDate(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SS")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));

                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
                zonedTime = zonedTime.withZoneSameInstant(ZoneOffset.UTC);
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    //Nishanth changes
    public Agreements getAgreements() {
        return agreements;
    }

    public void setAgreements(Agreements agreements) {
        this.agreements = agreements;
    }

    AgreementsData agreementsData;

    public int getAgreementsId() {
        return id;
    }

    public void setAgreementsId(int id) {
        this.id = id;
    }

    public int getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(int signatureType) {
        this.signatureType = signatureType;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public String getAgreementsUserId() {
        return userId;
    }

    public void setAgreementsUserId(String userId) {
        this.userId = userId;
    }

    public String getAgreementsSignature() {
        return signature;
    }

    public void setAgreementsSignature(String signature) {
        this.signature = signature;
    }

    public String getAgreementsSignedOn() {
        return signedOn;
    }

    public void setAgreementsSignedOn(String signedOn) {
        this.signedOn = signedOn;
    }

    public String getAgreementsIpAddress() {
        return ipAddress;
    }

    public void setAgreementsIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    int cryptoBuyMinimumLimit;

    public int getCryptoBuyMinimumLimit() {
        return cryptoBuyMinimumLimit;
    }

    public void setCryptoBuyMinimumLimit(int cryptoBuyMinimumLimit) {
        this.cryptoBuyMinimumLimit = cryptoBuyMinimumLimit;
    }

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

    private String fromWhichFragment;

    public String getFromWhichFragment() {
        return fromWhichFragment;
    }

    public void setFromWhichFragment(String fromWhichFragment) {
        this.fromWhichFragment = fromWhichFragment;
    }

    //End

    public void logOut(Activity activity) {
        try {
            SetLock();
            setStrUser("");
            setUserTracker(null);
//            Intent i = new Intent(activity, LoginActivity.class);
            Intent i = new Intent(activity, SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("logout", "Yes");
            startActivity(i);
            activity.finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void logOutApp(Activity activity) {
        try {
            SetLock();
            setStrUser("");
            setUserTracker(null);
            Intent i = new Intent(activity, SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            activity.finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetLock() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                } else {
                    isFaceLock = false;
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            }
        }
    }

    public void displayAlert(Activity activity, String msg) {
        try {
            TextView tvOK;
            popupSession = new Dialog(activity, R.style.DialogTheme);
            popupSession.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupSession.setContentView(R.layout.sessionexpirepopup);
            Window window = popupSession.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupSession.getWindow().setAttributes(lp);
            popupSession.show();
            tvOK = (TextView) popupSession.findViewById(R.id.tvOK);

            tvOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupSession.dismiss();
                    logOut(activity);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public APIError getErrorResponse(ResponseBody error) {
        APIError errorResponse = new APIError();
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<APIError>() {
            }.getType();
            errorResponse = gson.fromJson(error.charStream().toString(), type);
            if (errorResponse == null) {
                errorResponse = gson.fromJson(error.string(), type);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return errorResponse;
    }

    public String currentTime() {
        String strTime = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("HH:mm");
            strTime = spf.format(new Date());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strTime;
    }

    public Calendar getCalendar(String strTime) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            cal.setTime(sdf.parse(strTime));// all done
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cal;
    }

    public String calculateTime(String strTime, Activity activity, ViewModelStoreOwner storeOwner) {
        String time = "";
        try {
            if (!strTime.equals("")) {
                String strCurTime = currentTime();
                Calendar start = getCalendar(strTime);
                Calendar current = getCalendar(strCurTime);
                if (current != null && start != null) {
                    long seconds = (current.getTimeInMillis() - start.getTimeInMillis());
                    long minutes = TimeUnit.MILLISECONDS
                            .toMinutes(seconds);
                    if (minutes > Integer.parseInt(activity.getString(R.string.buttonaction)) && minutes < Integer.parseInt(activity.getString(R.string.inactivemax))) {
                        //showPopup(activity, storeOwner);
                    }
                }
            }
            time = "";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return time;
    }

    public void showPopup(Activity activity, ViewModelStoreOwner storeOwner, Boolean isPause) {
        try {
            TextView tvContinue, tvSignoff;
            popupInActive = new Dialog(activity, R.style.DialogTheme);
            popupInActive.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupInActive.setContentView(R.layout.userinactivepopup);
            Window window = popupInActive.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupInActive.getWindow().setAttributes(lp);
            popupInActive.setCanceledOnTouchOutside(false);
            popupInActive.show();
            tvContinue = (TextView) popupInActive.findViewById(R.id.tvContinue);
            tvSignoff = (TextView) popupInActive.findViewById(R.id.tvSignoff);
            dashboardViewModel = new ViewModelProvider(storeOwner).get(DashboardViewModel.class);
            tvContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.removeCallbacks(myRunnable);
                    popupInActive.dismiss();
                    dashboardViewModel.meProfile();
                    userInactive(activity, storeOwner, isPause);
                }
            });
            tvSignoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.removeCallbacks(myRunnable);
                    logOut(activity);
                }
            });
            handler.removeCallbacks(myRunnable);
            myRunnable = new Runnable() {
                public void run() {
                    logOut(activity);
                }
            };
            handler.postDelayed(myRunnable, Integer.parseInt(activity.getString(R.string.buttonaction)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void userInactive(Activity activity, ViewModelStoreOwner storeOwner, Boolean isPause) {
        try {
            dashboardViewModel = new ViewModelProvider(storeOwner).get(DashboardViewModel.class);
            dashboardViewModel.meProfile();
            long time = Integer.parseInt(activity.getString(R.string.inactivemax)) * 60000;
            myRunnable = new Runnable() {
                public void run() {
                    if (!isPause) {
                        showPopup(activity, storeOwner, isPause);
                    } else {
                        handler.removeCallbacks(myRunnable);
                        logOut(activity);
                    }
                }
            };
            handler.postDelayed(myRunnable, time);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void appMinimized(Activity activity) {
        try {
            long time = Integer.parseInt(activity.getString(R.string.appminimized)) * 60000;
            appRunnable = new Runnable() {
                public void run() {
                    appHandler.removeCallbacks(appRunnable);
                    logOut(activity);
                }
            };
            appHandler.postDelayed(appRunnable, time);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

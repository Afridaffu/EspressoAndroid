package com.greenbox.coyni.utils;

import android.app.Application;
import android.os.Build;

import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsData;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.cards.CardsDataItem;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.model.users.AccountLimitsData;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MyApplication extends Application {
    static String strEncryptedPublicKey, strUser = "", strUserCode;
    List<CardsDataItem> listCards = new ArrayList<>();
    List<Agreements> agreementsList;
    AccountLimitsData objAcc;
    AgreementsPdf agreementsPdf;
    RetrieveUsersResponse objRetUsers = new RetrieveUsersResponse();
    String strUserName = "", strRetrEmail = "", strEmail = "", strSignOnError = "", strFiservError = "", strPreference = "";
    Profile myProfile = new Profile();
    UpdateEmailResponse updateEmailResponse = new UpdateEmailResponse();
    UpdatePhoneResponse updatePhoneResponse = new UpdatePhoneResponse();
    List<States> listStates = new ArrayList<>();
    //isBiometric - OS level on/off;  isLocalBiometric - LocalDB value
    Boolean isBiometric = false, isLocalBiometric = false, isResolveUrl = false;
    PaymentMethodsResponse paymentMethodsResponse;
    WalletResponse walletResponse;
    TransactionList transactionList;

    String timezone = "";
    int timezoneID = 0;
    String tempTimezone = "";
    int tempTimezoneID = 0;

    public TransactionList getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(TransactionList transactionList) {
        this.transactionList = transactionList;
    }

    int userId;
    SignOnData objSignOnData = new SignOnData();
    TrackerResponse trackerResponse = new TrackerResponse();

    public AgreementsPdf getAgreementsPdf() {
        return agreementsPdf;
    }

    public void setAgreementsPdf(AgreementsPdf agreementsPdf) {
        this.agreementsPdf = agreementsPdf;
    }

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

    public PaymentMethodsResponse getPaymentMethodsResponse() {
        return paymentMethodsResponse;
    }

    public void setPaymentMethodsResponse(PaymentMethodsResponse paymentMethodsResponse) {
        this.paymentMethodsResponse = paymentMethodsResponse;
    }

    public UpdatePhoneResponse getUpdatePhoneResponse() {
        return updatePhoneResponse;
    }

    public void setUpdatePhoneResponse(UpdatePhoneResponse updatePhoneResponse) {
        this.updatePhoneResponse = updatePhoneResponse;
    }

    public WalletResponse getWalletResponse() {
        return walletResponse;
    }

    public void setWalletResponse(WalletResponse walletResponse) {
        this.walletResponse = walletResponse;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public int getTimezoneID() {
        return timezoneID;
    }

    public void setTimezoneID(int timezoneID) {
        this.timezoneID = timezoneID;
    }

    public String getTempTimezone() {
        return tempTimezone;
    }

    public void setTempTimezone(String tempTimezone) {
        this.tempTimezone = tempTimezone;
    }

    public int getTempTimezoneID() {
        return tempTimezoneID;
    }

    public void setTempTimezoneID(int tempTimezoneID) {
        this.tempTimezoneID = tempTimezoneID;
    }

    public String getStrSignOnError() {
        return strSignOnError;
    }

    public void setStrSignOnError(String strSignOnError) {
        this.strSignOnError = strSignOnError;
    }

    public SignOnData getSignOnData() {
        return objSignOnData;
    }

    public void setSignOnData(SignOnData objSignOnData) {
        this.objSignOnData = objSignOnData;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public TrackerResponse getTrackerResponse() {
        return trackerResponse;
    }

    public void setTrackerResponse(TrackerResponse trackerResponse) {
        this.trackerResponse = trackerResponse;
    }

    public String getStrPreference() {
        return strPreference;
    }

    public void setStrPreference(String strPreference) {
        this.strPreference = strPreference;
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
}

package com.greenbox.coyni.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutIdDetailsData;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutIdDetailsResponse;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.businesswallet.WalletInfo;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.buytoken.BuyTokenRequest;
import com.greenbox.coyni.model.buytoken.BuyTokenResponse;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.paidorder.PaidOrderRequest;
import com.greenbox.coyni.model.paidorder.PaidOrderResp;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.reguser.Contacts;
import com.greenbox.coyni.model.reguser.RegisteredUsersRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.submit.ApplicationSubmitResponseModel;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.transaction.TransactionList;
//import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.WebViewActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.view.business.ReviewApplicationActivity;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    AgreementsPdf agreementsPdf;
    RetrieveUsersResponse objRetUsers = new RetrieveUsersResponse();
    String strUserName = "", strRetrEmail = "", strEmail = "", strSignOnError = "", strFiservError = "", strPreference = "PST", strInvite = "", strScreen = "";
    Profile myProfile = new Profile();
    UpdateEmailResponse updateEmailResponse = new UpdateEmailResponse();
    UpdatePhoneResponse updatePhoneResponse = new UpdatePhoneResponse();
    UserDetails userDetails;
    List<States> listStates = new ArrayList<>();
    LatestTxnResponse listLatestTxn;
    //isBiometric - OS level on/off;  isLocalBiometric - LocalDB value
    Boolean isBiometric = false, isLocalBiometric = false, isResolveUrl = false, isContactPermission = true, isCardSave = false, isSignet = false;
    PaymentMethodsResponse paymentMethodsResponse;
    //    WalletResponse walletResponse;
    String timezone = "", tempTimezone = "Pacific (PST)", strStatesUrl = "", rsaPublicKey = "", strMobileToken = "", strRegisToken = "";
    int timezoneID = 0, tempTimezoneID = 0, loginUserId, accountType, dbaOwnerId = 0;
    TransactionList transactionList;
    PaymentsList selectedCard, prevSelectedCard;
    TransferFeeResponse transferFeeResponse;
    BrandsResponse selectedBrandResponse;
    WithdrawRequest withdrawRequest;
    WithdrawResponse withdrawResponse;
    BuyTokenResponse buyTokenResponse;
    PayRequestResponse payRequestResponse;
    TransferPayRequest transferPayRequest;
    PaidOrderRequest paidOrderRequest;
    PaidOrderResp paidOrderResp;
    List<Contacts> listContacts = new ArrayList<>();
    TransactionListRequest transactionListSearch = new TransactionListRequest();
    Double withdrawAmount;
    BusinessTrackerResponse businessTrackerResponse;
    WalletResponseData walletResponseData;
    BusinessTypeResp businessTypeResp;
    CompanyInfoResp companyInfoResp;
    DBAInfoResp dbaInfoResp;
    BuyTokenRequest buyRequest;
    BOResp beneficialOwnersResponse;
    HashMap<String, RegisteredUsersRequest> objPhContacts = new HashMap<>();
    ApplicationSubmitResponseModel submitResponseModel;
    Double merchantBalance = 0.0;

    public SignOnData getObjSignOnData() {
        return objSignOnData;
    }

    String selectedButTokenType = "";

    public void setObjSignOnData(SignOnData objSignOnData) {
        this.objSignOnData = objSignOnData;
    }

    public void setPaidOrderRequest(PaidOrderRequest paidOrderRequest) {
        this.paidOrderRequest = paidOrderRequest;
    }

    public PaidOrderRequest getPaidOrderRequest() {
        return paidOrderRequest;
    }

    public PaidOrderResp getPaidOrderResp() {
        return paidOrderResp;
    }

    public void setPaidOrderResp(PaidOrderResp paidOrderResp) {
        this.paidOrderResp = paidOrderResp;
    }

    public Double getMerchantBalance() {
        return merchantBalance;
    }

    public void setMerchantBalance(Double merchantBalance) {
        this.merchantBalance = merchantBalance;
    }

    public int getDbaOwnerId() {
        return dbaOwnerId;
    }

    public void setDbaOwnerId(int dbaOwnerId) {
        this.dbaOwnerId = dbaOwnerId;
    }

    public LatestTxnResponse getListLatestTxn() {
        return listLatestTxn;
    }

    public void setListLatestTxn(LatestTxnResponse listLatestTxn) {
        this.listLatestTxn = listLatestTxn;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public TransactionList getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(TransactionList transactionList) {
        this.transactionList = transactionList;
    }

    SignOnData objSignOnData = new SignOnData();
    TrackerResponse trackerResponse = new TrackerResponse();

    public AgreementsPdf getAgreementsPdf() {
        return agreementsPdf;
    }

    WalletInfo gbtWallet;
    Double GBTBalance = 0.0;
    Double reserveBalance = 0.0;

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

//    public WalletResponse getWalletResponse() {
//        return walletResponse;
//    }

//    public void setWalletResponse(WalletResponse walletResponse) {
//        this.walletResponse = walletResponse;
//    }

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

    public int getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(int logUserId) {
        this.loginUserId = logUserId;
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

    public WalletInfo getGbtWallet() {
        return gbtWallet;
    }

    public void setGbtWallet(WalletInfo gbtWallet) {
        this.gbtWallet = gbtWallet;
    }

    public Double getGBTBalance() {
        return GBTBalance;
    }

    public void setGBTBalance(Double GBTBalance) {
        this.GBTBalance = GBTBalance;
    }

    public Double getReserveBalance() {
        return reserveBalance;
    }

    public void setReserveBalance(Double reserveBalance) {
        this.reserveBalance = reserveBalance;
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

    public String reserveDate(String date) {
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
                spf = new SimpleDateFormat("MM/dd/yyyy");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public String payoutDetailsDate(String date) {
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
                spf = new SimpleDateFormat("MM/dd/yyyy @ hh:mma");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }


    public String convertZoneDateTime(String date, String format, String requiredFormat) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern(format)
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(requiredFormat);
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat(format);
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat(requiredFormat);
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
        if (date.length() == 22) {
            date = date + "0";
        }
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));

                Log.e("getStrPreference", getStrPreference());
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
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

    public String convertZoneLatestTxn(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public String convertNewZoneDate(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd");
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

    public String convertZoneDateLastYear(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
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

    public PaymentsList getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(PaymentsList selectedCard) {
        this.selectedCard = selectedCard;
    }

    public PaymentsList getPrevSelectedCard() {
        return prevSelectedCard;
    }

    public void setPrevSelectedCard(PaymentsList selectedCard) {
        this.prevSelectedCard = selectedCard;
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

    public void setWithdrawRequest(WithdrawRequest gcWithdrawRequest) {
        this.withdrawRequest = gcWithdrawRequest;
    }

    public BuyTokenRequest getBuyRequest() {
        return buyRequest;
    }

    public void setBuyRequest(BuyTokenRequest buyRequest) {
        this.buyRequest = buyRequest;
    }

    public WithdrawResponse getWithdrawResponse() {
        return withdrawResponse;
    }

    public void setWithdrawResponse(WithdrawResponse withdrawResponse) {
        this.withdrawResponse = withdrawResponse;
    }

    public Boolean getContactPermission() {
        return isContactPermission;
    }

    public void setContactPermission(Boolean contactPermission) {
        isContactPermission = contactPermission;
    }

    public List<Contacts> getListContacts() {
        return listContacts;
    }

    public void setListContacts(List<Contacts> listContacts) {
        this.listContacts = listContacts;
    }

    public String getStrInvite() {
        return strInvite;
    }

    public void setStrInvite(String strInvite) {
        this.strInvite = strInvite;
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

    public void callResolveFlow(Activity activity, String strSignOn, SignOnData signOnData) {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                Intent i = new Intent(activity, WebViewActivity.class);
                i.putExtra("signon", signOnData);
                activity.startActivityForResult(i, 1);
            } else {
                Utils.displayAlert(strSignOn, activity, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String convertNotificationTime(String date) {
        String strDate = "";
        String timeAgo = "";
        try {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
            strDate = zonedTime.format(DATE_TIME_FORMATTER);

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date past = format.parse(strDate);

            Date now = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            String nowString = formatter.format(now);

            DateTimeFormatter dtfNow = new DateTimeFormatterBuilder().appendPattern("EEE MMM dd HH:mm:ss zzzz yyyy")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTimeNow = ZonedDateTime.parse(nowString, dtfNow);
            DateTimeFormatter DATE_TIME_FORMATTER_NOW = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            zonedTime = zonedTimeNow.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
            nowString = zonedTime.format(DATE_TIME_FORMATTER_NOW);

            SimpleDateFormat formatNow = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            now = formatNow.parse(nowString);
            Log.e("now", now + "");

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            int weeks = (int) days / 7;
            int months = (int) weeks / 4;
            int years = (int) months / 4;

            if (seconds < 60) {
                timeAgo = seconds + "s ago";
            } else if (minutes < 60) {
//                System.out.println(minutes + " minutes ago");
                timeAgo = minutes + "m ago";
            } else if (hours < 24) {
//                System.out.println(hours + " hours ago");
                timeAgo = hours + "h ago";
            } else if (days < 7) {
//                System.out.println(days + " days ago");
                timeAgo = days + "d ago";
            } else if (weeks < 4) {
                timeAgo = weeks + "w ago";
//                System.out.println(days + " weeks ago");
            } else if (months < 12) {
                if (months > 1)
                    timeAgo = months + "months ago";
                else
                    timeAgo = months + "month ago";
//                System.out.println(days + " weeks ago");
            } else {
                timeAgo = years + "y ago";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timeAgo;
    }

    public TransferPayRequest getTransferPayRequest() {
        return transferPayRequest;
    }

    public void setTransferPayRequest(TransferPayRequest transferPayRequest) {
        this.transferPayRequest = transferPayRequest;
    }

    public Double getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(Double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public PayRequestResponse getPayRequestResponse() {
        return payRequestResponse;
    }

    public void setPayRequestResponse(PayRequestResponse payRequestResponse) {
        this.payRequestResponse = payRequestResponse;
    }

    public String getStrScreen() {
        return strScreen;
    }

    public void setStrScreen(String strScreen) {
        this.strScreen = strScreen;
    }

    public Boolean getCardSave() {
        return isCardSave;
    }

    public void setCardSave(Boolean cardSave) {
        isCardSave = cardSave;
    }

    public int getAccountType() {
        return accountType;
    }

    public Boolean getSignet() {
        return isSignet;
    }

    public void setSignet(Boolean signet) {
        isSignet = signet;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public WalletResponseData getWalletResponseData() {
        return walletResponseData;
    }

    public void setWalletResponseData(WalletResponseData walletResponseData) {
        this.walletResponseData = walletResponseData;
    }

    public Bitmap convertImageURIToBitMap(String encodedString) {
        try {
            Bitmap bitmap = MediaStore.Images.Media
                    .getBitmap(getContentResolver(),
                            Uri.parse(encodedString));
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public int monthsBetweenDates(Date startDate, Date endDate) {
        int monthsBetween = 0;
        try {
            Calendar start = Calendar.getInstance();
            start.setTime(startDate);

            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            int dateDiff = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);

            if (dateDiff < 0) {
                int borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH);
                dateDiff = (end.get(Calendar.DAY_OF_MONTH) + borrrow) - start.get(Calendar.DAY_OF_MONTH);
                monthsBetween--;

                if (dateDiff > 0) {
                    monthsBetween++;
                }
            } else {
                monthsBetween++;
            }
            monthsBetween += end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
            monthsBetween += (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return monthsBetween;
    }

    public PaymentMethodsResponse filterPaymentMethods(PaymentMethodsResponse objResponse) {
        PaymentMethodsResponse payMethodsResponse = objResponse;
        List<PaymentsList> listData = new ArrayList<>();
        try {
            if (objResponse != null && objResponse.getData() != null && objResponse.getData().getData() != null && objResponse.getData().getData().size() > 0) {
                for (int i = 0; i < objResponse.getData().getData().size(); i++) {
                    if (!objResponse.getData().getData().get(i).getPaymentMethod().toLowerCase().equals("signet")) {
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

    public BusinessTrackerResponse getBusinessTrackerResponse() {
        return businessTrackerResponse;
    }

    public void setBusinessTrackerResponse(BusinessTrackerResponse businessTrackerResponse) {
        this.businessTrackerResponse = businessTrackerResponse;
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

    public BOResp getBeneficialOwnersResponse() {
        return beneficialOwnersResponse;
    }

    public void setBeneficialOwnersResponse(BOResp beneficialOwnersResponse) {
        this.beneficialOwnersResponse = beneficialOwnersResponse;
    }

    public String getStrMobileToken() {
        return strMobileToken;
    }

    public void setStrMobileToken(String strMobileToken) {
        this.strMobileToken = strMobileToken;
    }

    public BuyTokenResponse getBuyTokenResponse() {
        return buyTokenResponse;
    }

    public void setBuyTokenResponse(BuyTokenResponse buyTokenResponse) {
        this.buyTokenResponse = buyTokenResponse;
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

    public String getStrRegisToken() {
        return strRegisToken;
    }

    public void setStrRegisToken(String strRegisToken) {
        this.strRegisToken = strRegisToken;
    }

    public Date getDate(String date) {
        Date dtExpiry = null;
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
            dtExpiry = spf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dtExpiry;
    }

    public PaymentMethodsResponse businessPaymentMethods(PaymentMethodsResponse objResponse) {
        try {
            if (getAccountType() == Utils.BUSINESS_ACCOUNT) {
                PaymentMethodsResponse objData = objResponse;
                List<PaymentsList> listPayments = objData.getData().getData();
                List<PaymentsList> listBusPayments = new ArrayList<>();
                if (listPayments != null && listPayments.size() > 0) {
                    for (int i = 0; i < listPayments.size(); i++) {
//                        if (listPayments.get(i).getPaymentMethod() != null
//                                && (listPayments.get(i).getPaymentMethod().toLowerCase().equals("bank") || listPayments.get(i).getPaymentMethod().toLowerCase().equals("signet"))) {
                        if (listPayments.get(i).getPaymentMethod() != null && (listPayments.get(i).getPaymentMethod().toLowerCase().equals("bank"))) {
                            listBusPayments.add(listPayments.get(i));
                        }
                    }
                }
                objResponse.getData().setData(listBusPayments);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objResponse;
    }

    public void launchDashboard(Context context, String fromScreen) {
        try {
            Intent dashboardIntent = new Intent(context, DashboardActivity.class);
            if (getAccountType() == Utils.BUSINESS_ACCOUNT) {
                BusinessTrackerResponse btr = getBusinessTrackerResponse();
                if (btr != null && btr.getData().isCompanyInfo() && btr.getData().isDbaInfo() && btr.getData().isBeneficialOwners()
                        && btr.getData().isIsbankAccount() && btr.getData().isAgreementSigned() && btr.getData().isApplicationSummary()) {

                    if (btr.getData().isProfileVerified()) {
                        dashboardIntent = new Intent(context, BusinessDashboardActivity.class);
                    }
//                    else if (btr.getData().isApplicationSummary() && !btr.getData().isProfileVerified()) {
//                        dashboardIntent = new Intent(context, ReviewApplicationActivity.class);
//                    }
                    else {
                        dashboardIntent = new Intent(context, BusinessRegistrationTrackerActivity.class);
                        dashboardIntent.putExtra("FROM", fromScreen);
                    }

                } else {
                    dashboardIntent = new Intent(context, BusinessRegistrationTrackerActivity.class);
                    dashboardIntent.putExtra("FROM", fromScreen);
                }
            }
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dashboardIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public String setNameHead(String strName) {
        String strNameHead = "";
        try {
            if (strName.contains(" ")) {
                if (!strName.split(" ")[0].equals("")) {
                    if (strName.split(" ").length > 2) {
                        if (!strName.split(" ")[1].equals("")) {
                            strNameHead = strName.split(" ")[0].substring(0, 1).toUpperCase() + strName.split(" ")[1].substring(0, 1).toUpperCase();
                        } else {
                            strNameHead = strName.split(" ")[0].substring(0, 1).toUpperCase() + strName.split(" ")[2].substring(0, 1).toUpperCase();
                        }
                    } else {
                        strNameHead = strName.split(" ")[0].substring(0, 1).toUpperCase() + strName.split(" ")[1].substring(0, 1).toUpperCase();
                    }
                } else {
                    strNameHead = strName.split(" ")[0].toUpperCase() + strName.split(" ")[1].substring(0, 1).toUpperCase();
                }
            } else {
                strNameHead = strName.substring(0, 1).toUpperCase();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strNameHead;
    }

    public String getSelectedButTokenType() {
        return selectedButTokenType;
    }

    public void setSelectedButTokenType(String selectedButTokenType) {
        this.selectedButTokenType = selectedButTokenType;
    }
}

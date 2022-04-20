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
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
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
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.WebViewActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
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
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private UserData mCurrentUserData;

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentUserData = new UserData();
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

    public PaidOrderResp getPaidOrderResp() {
        return mCurrentUserData.getPaidOrderResp();
    }

    public void setPaidOrderResp(PaidOrderResp paidOrderResp) {
        mCurrentUserData.setPaidOrderResp(paidOrderResp);
    }

    public Double getMerchantBalance() {
        return mCurrentUserData.getMerchantBalance();
    }

    public void setMerchantBalance(Double merchantBalance) {
        mCurrentUserData.setMerchantBalance(merchantBalance);
    }

    public int getDbaOwnerId() {
        return mCurrentUserData.getDbaOwnerId();
    }

    public void setDbaOwnerId(int dbaOwnerId) {
        mCurrentUserData.setDbaOwnerId(dbaOwnerId);
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
        return mCurrentUserData.getListStates();
    }

    public void setListStates(List<States> listStates) {
        mCurrentUserData.setListStates(listStates);
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

//    public WalletResponse getWalletResponse() {
//        return walletResponse;
//    }

//    public void setWalletResponse(WalletResponse walletResponse) {
//        this.walletResponse = walletResponse;
//    }

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

    public WalletInfo getGbtWallet() {
        return mCurrentUserData.getGbtWallet();
    }

    public void setGbtWallet(WalletInfo gbtWallet) {
        mCurrentUserData.setGbtWallet(gbtWallet);
    }

    public Double getGBTBalance() {
        return mCurrentUserData.getGBTBalance();
    }

    public void setGBTBalance(Double gBTBalance) {
        mCurrentUserData.setGBTBalance(gBTBalance);
    }

    public Double getReserveBalance() {
        return mCurrentUserData.getReserveBalance();
    }

    public void setReserveBalance(Double reserveBalance) {
        mCurrentUserData.setReserveBalance(reserveBalance);
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

    public String convertZoneReservedOn(String date) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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

    public String convertPayoutDateTimeZone(String date) throws ParseException {
        String strDate = "";

//        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date newDate = spf.parse(date);
//        spf = new SimpleDateFormat("MM/dd/yyyy hh:mma");
//        strDate = spf.format(newDate);

        if (Build.VERSION.SDK_INT >= 26) {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.S")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mma");
            zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(getStrPreference(), ZoneId.SHORT_IDS));
            strDate = zonedTime.format(DATE_TIME_FORMATTER);
        } else {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            spf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy hh:mma");
            spf.setTimeZone(TimeZone.getTimeZone(getStrPreference()));
            strDate = spf.format(newDate);
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

    public int getAccountType() {
        return mCurrentUserData.getAccountType();
    }

    public Boolean getSignet() {
        return mCurrentUserData.getSignet();
    }

    public void setSignet(Boolean signet) {
        mCurrentUserData.setSignet(signet);
    }

    public void setAccountType(int accountType) {
        mCurrentUserData.setAccountType(accountType);
    }

    public WalletResponseData getWalletResponseData() {
        return mCurrentUserData.getWalletResponseData();
    }

    public void setWalletResponseData(WalletResponseData walletResponseData) {
        mCurrentUserData.setWalletResponseData(walletResponseData);
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
        return mCurrentUserData.getStrMobileToken();
    }

    public void setStrMobileToken(String strMobileToken) {
        mCurrentUserData.setStrMobileToken(strMobileToken);
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
        return mCurrentUserData.getSelectedButTokenType();
    }

    public void setSelectedButTokenType(String selectedButTokenType) {
        mCurrentUserData.setSelectedButTokenType(selectedButTokenType);
    }

}

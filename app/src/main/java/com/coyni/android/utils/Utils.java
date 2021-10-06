package com.coyni.android.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.coyni.android.R;
import com.coyni.android.model.Countries;
import com.coyni.android.model.Status;
import com.coyni.android.model.transferfee.TransferFeeResponse;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.KEYGUARD_SERVICE;

public class Utils {
    public static String strLang = "en-US";
    public static String strCode = "12345";
    public static String strAuth;
    public static final String transInProgress = "inprogress";
    public static final String transPending = "pending";
    public static final String transCompleted = "completed";
    public static final String transFailed = "failed";
    public static final String walletCategory = "1";
    public static final String ethWallet = "1";
    public static final String btcWallet = "2";
    public static final String usdcWallet = "0";
    public static final String gbtWallet = "3";
    public static final String gbtExchange = "gbt";
    public static final String usdExchange = "usdc";
    public static final String btcExchange = "btc";
    public static final String sendType = "0";
    public static final String addType = "2";
    public static final String withdrawType = "3";
    public static final String debitType = "3";
    public static final String creditType = "2";
    public static final String bankType = "0";
    public static final String instantType = "1";
    public static final String giftcardType = "6";
    public static final String signetType = "7";
    public static final String payType = "12";
    public static final String paySubType = "8";
    public static final String requestType = "12";
    public static final String requestSubType = "9";
    public static final String eventTypeId = "0";
    public static final String eventSubTypeId = "301";
    public static final String portal = "Customer";
    public static final String request = "Request";
    public static final String requested = "Requested";
    public static final String remind = "Remind";
    public static final String decline = "Declined";
    public static final String accept = "Accept";
    public static final String complete = "Completed";
    public static final String cancel = "Cancelled";
    public static final String requestSub = "REQUEST TOKEN";
    public static final String requestReminder = "REQUEST REMINDER";
    public static final String requestDecline = "REQUEST DECLINED";
    public static final String requestAccepted = "REQUEST ACCEPTED";
    public static final String shuftiClientID = "rMkMIVXRT7QW9wXN3Nl0NG7nCNEIfj0m62rhFHvffq8x2wpXL91606260437";
    public static final String shuftiSecretKey = "$2y$10$Gwc2htz30dl1Iv/k4hxPzuWyBYHNJ0eSyw467/.jz/X8ApeqWfqlC";
    public static final int inviteId = 74;
    public static final int requestId = 75;
    public static final int remindId = 76;
    public static final int declineId = 67;
    public static final int acceptedId = 60;
    public static final int pageSize = 100;

    public static String getStrLang() {
        return strLang;
    }

    public static void setStrLang(String strLang) {
        Utils.strLang = strLang;
    }

    public static String getStrCode() {
        return strCode;
    }

    public static void setStrCode(String strCode) {
        Utils.strCode = strCode;
    }

    public static String getStrAuth() {
        return strAuth;
    }

    public static void setStrAuth(String strAuth) {
        Utils.strAuth = strAuth;
    }

    public static void statusBar(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#35BAB6"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hideKeypad(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String convertBigDecimal(String amount) {
        String strValue = "";
        BigDecimal value;
        try {
            value = new BigDecimal(amount);
            strValue = String.valueOf(value.setScale(6, BigDecimal.ROUND_HALF_EVEN));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public static Boolean checkInternet(Context context) {
        Boolean value = false;
        try {
            ConnectivityManager ConnectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() == true) {
                value = true;
            } else {
                value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public static String convertDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MMM dd, yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void displayAlert(String msg, Activity activity) {
        Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    public static void displayCloseAlert(String msg, Activity activity) {
        Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setTitle(R.string.app_name);
        builder.setMessage(msg);
        AlertDialog dialog = builder.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, Integer.parseInt(context.getString(R.string.closealert)));
    }

    public static void copyAlert(String msg, Activity activity) {
        Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
        TextView showText = new TextView(activity);
        showText.setText(msg);
        showText.setTextIsSelectable(true);
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.app_name)
                .setView(showText)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    public static String convertBigDecimalUSDC(String amount) {
        String strValue = "";
        BigDecimal value;
        try {
            value = new BigDecimal(amount);
            strValue = String.valueOf(value.setScale(2, BigDecimal.ROUND_HALF_EVEN));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static String changeFormat(int value) {
        String strValue = "";
        try {
            if (value < 10) {
                strValue = "0" + value;
            } else {
                strValue = String.valueOf(value);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public static List<Status> getStatus() {
        List<Status> listStatus = new ArrayList<>();
        try {
            Status obj = new Status();
            obj.setStatusId(2);
            obj.setStatusName("Completed");
            listStatus.add(obj);
            obj = new Status();
            obj.setStatusId(3);
            obj.setStatusName("Failed");
            listStatus.add(obj);
            obj = new Status();
            obj.setStatusId(0);
            obj.setStatusName("In Progress");
            listStatus.add(obj);
            obj = new Status();
            obj.setStatusId(1);
            obj.setStatusName("Pending");
            listStatus.add(obj);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listStatus;
    }

    public static List<Status> getTypes() {
        List<Status> listStatus = new ArrayList<>();
        try {
            Status obj = new Status();
            obj.setStatusId(12);
            obj.setStatusName("Pay / Request");
            listStatus.add(obj);
//            obj = new Status();
//            obj.setStatusId(4);
//            obj.setStatusName("Request");
//            listStatus.add(obj);
            obj = new Status();
            obj.setStatusId(2);
            obj.setStatusName("Buy Token");
            listStatus.add(obj);
            obj = new Status();
            obj.setStatusId(3);
            obj.setStatusName("Withdrawal's");
            listStatus.add(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listStatus;
    }

    public static List<Status> getDateRange() {
        List<Status> listDates = new ArrayList<>();
        try {
            Status obj = new Status();
            obj.setStatusId(0);
            obj.setStatusName("Today's");
            listDates.add(obj);
            obj = new Status();
            obj.setStatusId(1);
            obj.setStatusName("Last Seven Days");
            listDates.add(obj);
            obj = new Status();
            obj.setStatusId(2);
            obj.setStatusName("Current Month");
            listDates.add(obj);
            obj = new Status();
            obj.setStatusId(3);
            obj.setStatusName("Previous Month");
            listDates.add(obj);
            obj = new Status();
            obj.setStatusId(4);
            obj.setStatusName("Custom");
            listDates.add(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listDates;
    }

    public static Date convertStringToDate(String date) {
        Date strDate = null;
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strDate = spf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String getCurrentDate(String strDay) {
        String strFrom = "", strTo = "";
        try {
            int year = 0, day = 0, month = 0;
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);
            month = (cal.get(Calendar.MONTH) + 1);
            day = cal.get(Calendar.DAY_OF_MONTH);
            if (strDay.equals("today")) {
                strFrom = year + "-" + changeFormat(month) + "-" + changeFormat(day) + " 00:00:00.00";
            } else if (strDay.equals("current")) {
                strFrom = year + "-" + changeFormat(month) + "-" + changeFormat(1) + " 00:00:00.00";
            } else if (strDay.equals("previous")) {
                cal.add(Calendar.MONTH, -1);
                year = cal.get(Calendar.YEAR);
                month = (cal.get(Calendar.MONTH) + 1);
                strFrom = year + "-" + changeFormat(month) + "-" + changeFormat(1) + " 00:00:00.00";
                day = cal.getActualMaximum(Calendar.DATE);
                strTo = year + "-" + changeFormat(month) + "-" + changeFormat(day) + " 23:59:59.00";
                strFrom = strFrom + ";" + strTo;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                String str = sdf.format(cal.getTime());
//                strFrom = year + "-" + Utils.changeFormat(month) + "-" + Utils.changeFormat(day) + " " + str;
                strFrom = year + "-" + changeFormat(month) + "-" + changeFormat(day) + " 23:59:59.00";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strFrom;
    }

    public static String getFromDate(int days) {
        String strDate = "";
        try {
            int year = 0, day = 0, month = 0;
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -days);
            year = cal.get(Calendar.YEAR);
            month = (cal.get(Calendar.MONTH) + 1);
            day = cal.get(Calendar.DAY_OF_MONTH);
            strDate = year + "-" + changeFormat(month) + "-" + changeFormat(day) + " 00:00:00.00";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String USNumberFormat(double number) {
        String strFormat = "";
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            strFormat = format.format(number).replace("$", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strFormat;
    }

    public static String transactionDate(String date) {
        String strDate = "", strAllDates = "";
        try {
            SimpleDateFormat spf1, spf2, spf3, spf4, spf5;
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//            spf1 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//            spf1.setTimeZone(TimeZone.getTimeZone("UTC"));
//            spf2 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//            spf2.setTimeZone(TimeZone.getTimeZone("UTC - 7"));
//            spf3 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//            spf3.setTimeZone(TimeZone.getTimeZone("MST"));
//            spf4 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//            spf4.setTimeZone(TimeZone.getTimeZone("CST"));
//            spf5 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
//            spf5.setTimeZone(TimeZone.getTimeZone("EST"));
            strDate = spf.format(newDate);
//            strAllDates = "UTC - " + spf1.format(newDate) + "; PST - " + spf2.format(newDate) + "; MST - " + spf3.format(newDate)
//                    + "; CST - " + spf4.format(newDate) + "; EST - " + spf5.format(newDate);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String transactionTime(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("hh:mm aa");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void copyText(String strText, Context context) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Wallet Address", strText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Countries> getCountries() {
        List<Countries> listCountries = new ArrayList<>();
        try {
            Countries objCountry;
            Locale[] locales = Locale.getAvailableLocales();
            ArrayList<String> countries = new ArrayList<String>();
            for (Locale locale : locales) {
                String country = locale.getDisplayCountry();
                if (country.trim().length() > 0 && !countries.contains(country)) {
                    objCountry = new Countries();
                    objCountry.setName(country);
                    countries.add(country);
                    objCountry.setIsocode(getCountryCode(country));
                    listCountries.add(objCountry);
                }
            }
            Collections.sort(countries);
            for (String country : countries) {
                System.out.println(country);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listCountries;
    }

    public static String getCountryCode(String countryName) {
        Map<String, String> countryMap = new HashMap<>();
        try {
            // Get all country codes in a string array.
            String[] isoCountryCodes = Locale.getISOCountries();
            Locale locale;
            String name;

            // Iterate through all country codes:
            for (String code : isoCountryCodes) {
                // Create a locale using each country code
                locale = new Locale("", code);
                // Get country name for each code.
                name = locale.getDisplayCountry();
                // Map all country names and codes in key - value pairs.
                countryMap.put(name, code);
            }

            // Return the country code for the given country name using the map.
            // Here you will need some validation or better yet
            // a list of countries to give to user to choose from.
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return countryMap.get(countryName); // "NL" for Netherlands.
    }

    public static int convertPxtoDP(int value) {
        int dpvalue = 0;
        dpvalue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
        return dpvalue;
    }

    public static String feeCalculation(TransferFeeResponse transferFeeResponse) {
        String strMsg = "";
        try {
            Double feeInAmt = 0.0, feePercent = 0.0;
            feePercent = transferFeeResponse.getData().getFeeInPercentage();
            feeInAmt = transferFeeResponse.getData().getFeeInAmount();
            if ((feePercent != 0.0 || feePercent != 0) && (feeInAmt != 0.0 || feeInAmt != 0)) {
                strMsg = "Processing fee " + convertBigDecimalUSDC(String.valueOf(feePercent)) + "% + $" + convertBigDecimalUSDC(String.valueOf(feeInAmt)) + " is applicable for\nthis transaction.";
            } else if ((feePercent != 0.0 || feePercent != 0) && (feeInAmt == 0.0 || feeInAmt == 0)) {
                strMsg = "Processing fee " + convertBigDecimalUSDC(String.valueOf(feePercent)) + "% is applicable for\nthis transaction.";
            } else if ((feePercent == 0.0 || feePercent == 0) && (feeInAmt != 0.0 || feeInAmt != 0)) {
                strMsg = "Processing fee $" + convertBigDecimalUSDC(String.valueOf(feeInAmt)) + " is applicable for\nthis transaction.";
            } else {
                strMsg = "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strMsg;
    }

    public static String OnlyDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static int numberOfDays(String strDate) {
        int daysDiff = 0;
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = spf.parse(strDate);
            Date curDate = new Date();
            int fromDay = Integer.parseInt(String.valueOf(DateFormat.format("dd", curDate)));
            int toDay = Integer.parseInt(String.valueOf(DateFormat.format("dd", date)));
            int fromMon = Integer.parseInt(String.valueOf(DateFormat.format("MM", curDate)));
            int toMon = Integer.parseInt(String.valueOf(DateFormat.format("MM", date)));
            if (fromMon == toMon) {
                daysDiff = fromDay - toDay;
            } else {
                long diff = curDate.getTime() - date.getTime();
                daysDiff = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return daysDiff;
    }

    public static void checkAuthentication(Activity context, int CODE_AUTHENTICATION_VERIFICATION) {
        try {
            KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Intent i = km.createConfirmDeviceCredentialIntent("Authentication required", "password");
                    context.startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
                }
            } else
                displayAlert("You enabled the Security permission in Coyni App. Please enable the Security settings in device for making the transactions.", context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Boolean checkAuthentication(Activity context) {
        Boolean value = false;
        try {
            KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                value = true;
            } else {
                value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public static void displayLongCloseAlert(String msg, Activity activity) {
        Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setTitle(R.string.app_name);
        builder.setMessage(msg);
        AlertDialog dialog = builder.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, Integer.parseInt(context.getString(R.string.closealert1)));
    }

}

package com.greenbox.coyni.utils;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessTypeListAdapter;
import com.greenbox.coyni.adapters.CustomerTimeZonesAdapter;
import com.greenbox.coyni.adapters.StatesListAdapter;
import com.greenbox.coyni.model.DBAInfo.BusinessType;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.users.TimeZoneModel;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.view.EnableAuthID;
import com.greenbox.coyni.view.LoginActivity;
import com.greenbox.coyni.view.OnboardActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.view.PreferencesActivity;
import com.greenbox.coyni.view.business.CompanyInformationActivity;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static int PERSONAL_ACCOUNT = 1, BUSINESS_ACCOUNT = 2, SHARED_ACCOUNT = 3;
    public static String PERSONAL = "Personal", BUSINESS = "Business", SHARED = "";
    public static final String TOKEN = "TOKEN", MERCHANT = "MERCHANT";

    //public static enum BUSINESS_ACCOUNT_STATUS {Unverified};
    public static enum BUSINESS_ACCOUNT_STATUS {
        UNDER_REVIEW("Under Review"),
        ACTIVE("Active"),
        DEACTIVE("DeActivated"),
        ACTION_REQUIRED("Action Required"),
        REGISTRATION_CANCELED("Canceled"),
        UNVERIFIED("Unverified"),
        INFO_SHARED("Info Shared"),
        EXPIRED("Expired"),
        PENDING("Pending"),
        TERMINATED("Terminated"),
        DECLINED("Declined");

        private String status;

        BUSINESS_ACCOUNT_STATUS(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    public static String strLang = "en-US";
    public static String strCode = "12345";
    public static String strDesc = "abcd";
    public static String strCCode = "";
    public static String strAuth;
    public static String strToken = "";
    public static String appVersion;
    public static String strReferer;
    public static String strURL_PRODUCTION;
    public static Boolean isFaceEnabled;
    public static Boolean isTouchEnabled;
    public static Boolean isBiometric = false;
    public static final String transInProgress = "inprogress";
    public static final String transPending = "pending";
    public static final String transCompleted = "completed";
    public static final String transOpen = "open";
    public static final String transFailed = "failed";
    public static final String transCancelled = "cancelled";
    public static final String transinprogress = "in progress";
    public static final String walletCategory = "1";
    public static final String addType = "2";
    public static final String withdrawType = "3";
    public static final String debitType = "3";
    public static final String creditType = "2";
    public static final String bankType = "0";
    public static final String instantType = "1";
    public static final String giftcardType = "6";
    public static final String signetType = "7";
    public static final String payType = "12";
    public static final String businessType = "19";
    public static final String tokenType = "11";
    public static final String merchantType = "7";
    public static final String transferType = "10";
    public static final String paySubType = "8";
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
    public static final String SUCCESS = "SUCCESS";
    public static final String gbxTxnIdType = "gbxTxnIdType";
    public static final String txnType = "txnType";
    public static final String txnSubType = "txnSubType";
    public static final String refundtxntype = "refund";
    public static final String saleOrdertxntype = "sale order";
    public static final String monthlyServiceFeetxntype = "monthly service fee";
    public static final String businessPayouttxntype = "business payout";
    public static final String merchantPayouttxntype = "merchant payout";
    public static final String tokensub = "token";
    public static final String transfersub = "transfer";
    public static final String Success = "Success";
    //    controll methods in merchanttransactiondetails
    public static final String refundCM = "refundCM";
    public static final String saleorderCM = "saleorderCM";
    public static final String monthlyservicefeeCM = "monthlyservicefeeCM";
    public static final String businesspayoutCM = "businesspayout";
    public static final String merchantpayoutCM = "merchantpayoutCM";
    public static final int inviteId = 74;
    public static final int requestId = 75;
    public static final int remindId = 76;
    public static final int declineId = 67;
    public static final int acceptedId = 60;
    public static final int pageSize = 25;
    public static String deviceID = "";
    public static Long mLastClickTime = 0L;
    public static final int duration = 1000;
    //    public static final int userTypeCust = 0;
    public static final int userTypeCust = 1;
    public static final int userTypeBusiness = 2;
    public static final String DATA = "data";
    public static int[][] errorState, state;
    public static int[] errorColor, color;
    public static ColorStateList errorColorState, colorState;
    public static String tempStateCode = "";
    public static String tempStateName = "";
    public static String[] for_Apptoved = {"1111", "2222", "3333", "5555", "7777", "8888", "9999", "GP01", "RT00", "RT03", "RT05", "ND00"};
    public static String[] for_Declined = {"RT01", "RT02"};
    public static String[] for_Error = {"GN05", "GS01", "GS02", "GS03", "GS04", "RT04"};
    public static String mondayURL = "https://monday.com/";
    //    public static String blinkCardKey = "sRwAAAASY29tLmdyZWVuYm94LmNveW5ppOyhw0QQR91SZ4Z+snkD6Sg0i3pdsBePQmRcpamT/Ss440879LzJVQJPWxAfslvVBaD7a11tGNrPOa59hRSx/Wr2JvEEZnMft6MClh2FHjehVH4TvbUH4Q5J8t9Fl59vCYSiHWl7wqEaSYJxkA5wI6VGC0+PVgcojfn3zlz04mza0I2zHWOHbIvl2z4WUw3lDmiV729HggfZJYSleNctEmFHscHKdTBIlJ2uhQm1uA==";
//    public static String blinkCardKey = "sRwAAAASY29tLmdyZWVuYm94LmNveW5ppOyhw0QQR91SZ4Z+skkD6XebOu1kYPMIy3HJXuIErNxvYkSdOTdpwY0Pn49l1koS9o2CfCe9Fa01YifNtCgXc37XRU5Di4z/Sspcjs9qrHOS0RFiGtmr5BaQcKjpuy/r5ukCVuNMHEK++HZYlahdIqFxGLjSll50XTn3j+YZFvIMd7CcXCmx9UP+zkdtcr5ib3+AyVdC/w5JKAMVRuNpN6PEGC02woYfYHB/uJmBZw==";
//    public static String blinkCardKey = "sRwAAAASY29tLmdyZWVuYm94LmNveW5ppOyhw0QQR91SZ4Z+smkG6e3GUWZyfYVqd79iVH7r6ZTsAAIna1uwZpVc86X6m0Bzjc8ut7V9K68Yzb4Khc1TVrsMgFpVsfwwBpFIqWAKSmjf+rMxdY7uHkkJfitfKGg8NOO1Cu2L+Qx4epOpghSkMw/c9Q7ORSIacDj0moI1AoPyVTzSehuNWGx17g3iQXD7E4HznTwkU2H0I9Zst23aWvZbiBQGVCZ9ChQvM3at2A==";
    public static String blinkCardKey = "sRwAAAASY29tLmdyZWVuYm94LmNveW5ppOyhw0QQR91SZ4Z+slEG6YrzB7khwY4q3StoNVUaCKdKVX2LzHaHhDz0MHkzRYMij/AYdyN6dsbX0w2EdCaLdY/wnQFmstU00LwMMm0LH2kvrwC14uwYF+DWsnakPr6ZXO+zR6B76tN53aS47Aa2P2qwbBt2pG/XHNNMcdJ0TbXxsZlZm8/o3F4t6yEqFppMuqaK0wfmfj95CAdT2S1SamGUHQeFONGL6hzGY38NbA==";

    public static final int payRequest = 12;
    public static final int buyTokens = 2;
    public static final int saleOrder = 10;
    public static final int monthlyservicefee = 17;
    public static final int withdraw = 3;
    public static final int refund = 9;
    public static final int accountTransfer = 0; //Not available
    public static final int paidInvoice = 15;
    public static final int businessPayout = 19;

    public static final int sent = 8;
    public static final int token = 11;
    public static final int received = 9;
    public static final int bankAccount = 0;
    public static final int creditCard = 2;
    public static final int debitCard = 3;
    public static final int signet = 7;
    public static final int instantPay = 1;
    public static final int giftCard = 6;
    public static final int saleOrderToken = 11; //need to confirm
    public static final int failedWithdraw = 11; // need to confirm
    public static final int cancelledWithdraw = 18;
    public static final int transfer = 10;

    public static final int pending = 1;
    public static final int completed = 2;
    public static final int cancelled = 4;//Not available
    public static final int inProgress = 0;
    public static final int failed = 3;

    //Merchant Transaction Filter Type values

    public static final int mRefund = 9;
    public static final int merchantPayout = 7;
    public static final int monthlyServiceFee = 17;

    public static final int mCompleted = 0;
    public static final int msRefund = 1;
    public static final int mPartialRefund = 2;
    public static final String SELECTED_MERCHANT_TRANSACTION = "Selected_Merchant_transaction";
    public static final float slidePercentage = 0.3f;

    public static boolean isKeyboardVisible = false;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static final String ACCOUNT_TYPE = "account_type";
    public static final String changeActionType = "CHANGE";
    public static final String withdrawActionType = "WITHDRAW";
    public static final String buyActionType = "BUY";
    public static final String sendActionType = "SEND";
    public static final String pinActionType = "COYNIPIN";

    public static final int boTargetPercentage = 51;

    public static final String teamFirstName = "TeamMemberFirstName";
    public static final String teamLastName = "TeamMemberLastName";

    public static final String teamEmailAddress = "TeamEmailAddress";
    public static final String teamPhoneNumber = "TeamPhoneNumber";
    public static final String teamMemberId = "TeamMemberId";
    public static final String teamStatus = "Status";

    public static final String boData = "BOData";

    public static final String companyName = "CompanyName";
    public static final String companyEmail = "CompanyEmail";
    public static final String companyNumber = "CompanyNumber";
    public static final String changeEdit = "ChangeEdit";
    public static final String comCountryCode = "CompanyCountryCode";
    public static final int companyId = 0;

    public static final String active = "Active";
    public static final String inActive = "Inactive";
    public static final String teammemberpending = "Pending";
    public static final String resendInvitation = "Resend Invitation";
    public static final String canceled = "Canceled";
    public static final String expired = "Expired";

    public static final String applyFilter = "apply";
    public static final String datePicker = "DatePicker";


    public static final String position = "Position";
    public static final int cPP = 0;
    public static final int cTOS = 1;
    public static final int mPP = 8;
    public static final int mTOS = 7;
    public static final int mAgmt = 5;


    public static String getStrLang() {
        return strLang;
    }

    public static String getStrCode() {
        return strCode;
    }

    public static String getStrDesc() {
        return strDesc;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static void setAppVersion(String appVersion) {
        Utils.appVersion = appVersion;
    }

    public static String getStrAuth() {
        return strAuth;
    }

    public static void setStrAuth(String strAuth) {
        Utils.strAuth = strAuth;
    }

    public static String getStrToken() {
        return strToken;
    }

    public static void setStrToken(String strToken) {
        Utils.strToken = strToken;
    }

    public static String getStrReferer() {
        return strReferer;
    }

    public static void setStrReferer(String strReferer) {
        Utils.strReferer = strReferer;
    }

    public static String getStrCCode() {
        return strCCode;
    }

    public static void setStrCCode(String strCCode) {
        Utils.strCCode = strCCode;
    }

    public static String getStrURL_PRODUCTION() {
        return strURL_PRODUCTION;
    }

    public static void setStrURL_PRODUCTION(String strURL_PRODUCTION) {
        Utils.strURL_PRODUCTION = strURL_PRODUCTION;
    }

    public static Boolean getIsFaceEnabled() {
        return isFaceEnabled;
    }

    public static void setIsFaceEnabled(Boolean isFaceEnabled) {
        Utils.isFaceEnabled = isFaceEnabled;
    }

    public static Boolean getIsTouchEnabled() {
        return isTouchEnabled;
    }

    public static void setIsTouchEnabled(Boolean isTouchEnabled) {
        Utils.isTouchEnabled = isTouchEnabled;
    }

    public static Boolean getIsBiometric() {
        return isBiometric;
    }

    public static void setIsBiometric(Boolean isBiometric) {
        Utils.isBiometric = isBiometric;
    }

    public static String getDeviceID() {
        return deviceID;
    }

    public static void setDeviceID(String dID) {
        Utils.deviceID = dID;
    }

    public static void statusBar(Activity activity, String strColor) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(strColor));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hideKeypad(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void shwForcedKeypad(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeypad(Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
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
            spf = new SimpleDateFormat("dd/MM/yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertDocUploadedDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("dd/MM/yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertDocUploadedDateAPITime(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("dd/MM/yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void displayAlert(String msg, Activity activity, String header, String fieldError) {

        if (!msg.equals("")) {
            if (msg.equals("Access token expired")) {
                Intent i = new Intent(activity, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
            } else {
                displayAlertNew(msg, activity, header);
            }
        } else {
            displayAlertNew(fieldError, activity, header);
        }
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

    public static void checkAuthentication(Activity context, int CODE_AUTHENTICATION_VERIFICATION) {
        try {
            KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Intent i = km.createConfirmDeviceCredentialIntent("Authentication required", "password");
                    context.startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
                }
            } else
                displayAlert("You enabled the Security permission in Coyni App. Please enable the Security settings in device for making the transactions.", context, "", "");
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

    public static Boolean isFingerPrint(Activity context) {
        Boolean value = false;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
                if (!fingerprintManager.isHardwareDetected()) {
                    value = false;
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    value = false;
                } else {
                    value = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public static void showCustomToast(final Context context, String text, int imageID, String strScreen) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_toast);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        TextView textTV = dialog.findViewById(R.id.toastTV);
        ImageView imageIV = dialog.findViewById(R.id.toastIV);
        textTV.setText(text);
        if (imageID == 0) {
            imageIV.setVisibility(View.GONE);
        } else {
            try {
                imageIV.setVisibility(View.VISIBLE);
                imageIV.setImageResource(imageID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        if (strScreen.equals("pin")) {
            new PINActivity().toastTimer(dialog);
        } else {
            new EnableAuthID().toastTimer(dialog);
        }

    }

    public static String formatDate(String date) {
        if (date.length() == 22) {
            date = date + "0";
        }
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            strDate = spf.format(newDate);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static boolean disabledMultiClick() {
        boolean action = false;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            action = false;
            mLastClickTime = 0L;
        } else {
            action = true;
            mLastClickTime = SystemClock.elapsedRealtime();
        }

        return action;
    }

    public static String convertToUSFormat(String strPhone) {
        String strNumber = "";
        strNumber = "(" + strPhone.substring(0, 3) + ") " + strPhone.substring(3, 6) + "-" + strPhone.substring(6, strPhone.length());
        return strNumber;
    }

    public static String convertToUSFormatNew(String strPhone) {
        String strNumber = "";
        strNumber = strPhone.substring(0, 3) + "-" + strPhone.substring(3, 6) + "-" + strPhone.substring(6, strPhone.length());
        return strNumber;
    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static ColorStateList getErrorColorState(Context context) {
        errorState = new int[][]{new int[]{-android.R.attr.state_focused}, new int[]{android.R.attr.state_focused}};
        errorColor = new int[]{ContextCompat.getColor(context, R.color.error_red), ContextCompat.getColor(context, R.color.error_red)};
        errorColorState = new ColorStateList(errorState, errorColor);
        return errorColorState;
    }

    public static ColorStateList getNormalColorState(Context context) {
        state = new int[][]{new int[]{-android.R.attr.state_focused}, new int[]{android.R.attr.state_focused}};
        color = new int[]{ContextCompat.getColor(context, R.color.light_gray), ContextCompat.getColor(context, R.color.light_gray)};
        colorState = new ColorStateList(state, color);
        return colorState;
    }

    public static ColorStateList getFocusedColorState(Context context) {
        state = new int[][]{new int[]{-android.R.attr.state_focused}, new int[]{android.R.attr.state_focused}};
        color = new int[]{ContextCompat.getColor(context, R.color.primary_green), ContextCompat.getColor(context, R.color.primary_green)};
        colorState = new ColorStateList(state, color);
        return colorState;
    }

    /**
     * @deprecated use {@link #getErrorColorState(Context)} instead.
     */
    @Deprecated
    public static ColorStateList getErrorColorState() {
        errorState = new int[][]{new int[]{-android.R.attr.state_focused}, new int[]{android.R.attr.state_focused}};
        errorColor = new int[]{OnboardActivity.onboardActivity.getResources().getColor(R.color.error_red), OnboardActivity.onboardActivity.getResources().getColor(R.color.error_red)};
        errorColorState = new ColorStateList(errorState, errorColor);

        return errorColorState;
    }

    /**
     * @deprecated use {@link #getNormalColorState(Context)} instead.
     */
    @Deprecated
    public static ColorStateList getNormalColorState() {
        state = new int[][]{new int[]{-android.R.attr.state_focused}, new int[]{android.R.attr.state_focused}};
        color = new int[]{OnboardActivity.onboardActivity.getResources().getColor(R.color.light_gray), OnboardActivity.onboardActivity.getResources().getColor(R.color.light_gray)};
        colorState = new ColorStateList(state, color);
        return colorState;
    }

    public static void setUpperHintColor(TextInputLayout til, int color) {
        try {
            Field field = til.getClass().getDeclaredField("defaultHintTextColor");
            field.setAccessible(true);
            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    color
            };
            ColorStateList myList = new ColorStateList(states, colors);
            field.set(til, myList);

            Method method = til.getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(til, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().equals("")) {
            return false;
        }
        try {
            new JSONObject(jsonString);
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void displayAlertNew(String msg, final Context context, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = dialog.findViewById(R.id.tvHead);
        TextView message = dialog.findViewById(R.id.tvMessage);
        CardView actionCV = dialog.findViewById(R.id.cvAction);
        TextView actionText = dialog.findViewById(R.id.tvAction);

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        message.setText(msg);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static InputFilter acceptonlyAlphabetValuesnotNumbersMethod() {
        return new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                boolean isCheck = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) {
                        sb.append(c);
                    } else {
                        isCheck = false;
                    }
                }
                if (isCheck)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString spannableString = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, spannableString, 0);
                        return spannableString;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
                Matcher match = pattern.matcher(String.valueOf(c));
                return match.matches();
            }
        };
    }

    public static Boolean checkBiometric(Context context) {
        Boolean isBiometric = false;
        try {
            BiometricManager biometricManager = BiometricManager.from(context);
            switch (biometricManager.canAuthenticate()) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.e("BIOMETRIC_STRONG", "App can authenticate using biometrics.");
                    isBiometric = true;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.e("BIOMETRIC_STRONG", "No biometric features available on this device.");
                    isBiometric = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.e("BIOMETRIC_STRONG", "Biometric features are currently unavailable.");
                    isBiometric = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    // Prompts the user to create credentials that your app accepts.
                    isBiometric = false;
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isBiometric;
    }

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        dialog.setIndeterminate(false);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    public static void populateTimeZones(Context context, EditText editText, MyApplication myApplicationObj, String from) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timezones_bottom_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        CardView actionCV = dialog.findViewById(R.id.cvAction);
        TextView actionText = dialog.findViewById(R.id.tvAction);
        RecyclerView timezonesRV = dialog.findViewById(R.id.timezonesRV);


        if (from.equals("DBA_INFO"))
            actionCV.setVisibility(View.GONE);
        try {
            ArrayList<TimeZoneModel> arrZonesList = new ArrayList<>();
            TimeZoneModel tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.EST));
            tzm.setTimezoneID(3);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.CST));
            tzm.setTimezoneID(2);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.MST));
            tzm.setTimezoneID(1);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.PST));
            tzm.setTimezoneID(0);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.AST));
            tzm.setTimezoneID(5);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.HST));
            tzm.setTimezoneID(4);
            arrZonesList.add(tzm);

            if (from.equals("PREFERENCES")) {
                for (int i = 0; i < arrZonesList.size(); i++) {
                    if (myApplicationObj.getTimezoneID() == arrZonesList.get(i).getTimezoneID()) {
                        arrZonesList.get(i).setSelected(true);
                    }
                }
            }

            CustomerTimeZonesAdapter customerTimeZonesAdapter = new CustomerTimeZonesAdapter(arrZonesList, context, from, dialog, editText);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            timezonesRV.setLayoutManager(mLayoutManager);
            timezonesRV.setItemAnimator(new DefaultItemAnimator());
            timezonesRV.setAdapter(customerTimeZonesAdapter);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    if (from.equals("PREFERENCES")) {

                        UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
                        userPreferenceModel.setLocalCurrency(0);
                        userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
                        userPreferenceModel.setPreferredAccount(myApplicationObj.getMyProfile().getData().getId());

                        PreferencesActivity preferencesActivity = (PreferencesActivity) context;
                        preferencesActivity.customerProfileViewModel.updatePreferences(userPreferenceModel);

                    } else if (from.equals("COMPANY_INFO")) {
                        myApplicationObj.setTimezone(myApplicationObj.getTempTimezone());
                        myApplicationObj.setTimezoneID(myApplicationObj.getTempTimezoneID());
                        editText.setText(myApplicationObj.getTimezone());
                        CompanyInformationActivity companyInformationActivity = (CompanyInformationActivity) context;
                        companyInformationActivity.isTimeZone = true;
                        companyInformationActivity.enableOrDisableNext();
                    } else if (from.equals("DBA_INFO")) {
                        myApplicationObj.setTimezone(myApplicationObj.getTempTimezone());
                        myApplicationObj.setTimezoneID(myApplicationObj.getTempTimezoneID());
                        editText.setText(myApplicationObj.getTimezone());
                        DBAInfoAcivity dbaInfoAcivity = (DBAInfoAcivity) context;
                        dbaInfoAcivity.isTimeZone = true;
                        dbaInfoAcivity.enableOrDisableNext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static void generateUUID(Context context) {
        try {
            String uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = context.getSharedPreferences("DeviceID", MODE_PRIVATE).edit();
            editor.putString("deviceId", uuid);
            editor.putBoolean("isDevice", true);
            editor.apply();
            Utils.setDeviceID(uuid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setUserEmail(Context context, String email) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("DeviceID", MODE_PRIVATE).edit();
            editor.putString("userEmail", email);
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DeviceID", MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", "");
    }

    public static void populateStates(Context context, EditText editText, MyApplication myApplicationObj) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.states_bottom_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;
//            List<States> statesFromAssets = getStates(context);

            Log.e("editext", editText.getText().toString());
            CardView actionCV = dialog.findViewById(R.id.cvAction);
            TextView actionText = dialog.findViewById(R.id.tvAction);
            RecyclerView statesRV = dialog.findViewById(R.id.statesRV);
            EditText searchET = dialog.findViewById(R.id.searchET);
            TextView notFoundTV = dialog.findViewById(R.id.notFoundTV);
            StatesListAdapter statesListAdapter = new StatesListAdapter(null, context, "EditAddress");

            List<States> listStates = myApplicationObj.getListStates();

//            if(listStates.size()==0 ){
//                listStates = statesFromAssets;
//            }

            tempStateName = "";
            tempStateCode = "";

            for (int i = 0; i < listStates.size(); i++) {
                if (editText.getText().toString().toLowerCase().trim().equals(listStates.get(i).getName().toLowerCase())) {
                    listStates.get(i).setSelected(true);
                    Utils.tempStateName = listStates.get(i).getName();
                    Utils.tempStateCode = listStates.get(i).getIsocode();
                } else {
                    listStates.get(i).setSelected(false);
                }
            }
            if (listStates.size() > 0) {
                statesRV.setVisibility(View.VISIBLE);
                notFoundTV.setVisibility(View.GONE);
                statesListAdapter = new StatesListAdapter(listStates, context, "EditAddress");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                statesRV.setLayoutManager(mLayoutManager);
                statesRV.setItemAnimator(new DefaultItemAnimator());
                statesRV.setAdapter(statesListAdapter);
            } else {
                statesRV.setVisibility(View.GONE);
                notFoundTV.setVisibility(View.VISIBLE);
            }

            StatesListAdapter finalStatesListAdapter = statesListAdapter;
            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String search_key = s.toString();
                        List<States> filterList = new ArrayList<>();
                        int sIndex = 0;
                        if (listStates.size() > 0) {
                            for (int i = 0; i < listStates.size(); i++) {
                                sIndex = listStates.get(i).getName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (sIndex == 0) {
                                    filterList.add(listStates.get(i));
                                }
                            }
                            if (filterList.size() > 0) {
                                statesRV.setVisibility(View.VISIBLE);
                                notFoundTV.setVisibility(View.GONE);
                                for (int i = 0; i < filterList.size(); i++) {
                                    if (editText.getText().toString().toLowerCase().trim().equals(filterList.get(i).getName().toLowerCase())) {
                                        filterList.get(i).setSelected(true);
                                    } else {
                                        filterList.get(i).setSelected(false);
                                    }
                                }
                                finalStatesListAdapter.updateList(filterList);
                            } else {
                                statesRV.setVisibility(View.GONE);
                                notFoundTV.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (!tempStateName.equals("")) {
                        editText.setText(tempStateName);
                    }
                }
            });

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (mertics.heightPixels * 0.80));

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (editText.getText().toString().equals("")) {
                        tempStateName = "";
                        tempStateCode = "";
                    }
                }
            });
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void populateLearnMore(Context context) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_leran_more);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            LinearLayout layoutClose = dialog.findViewById(R.id.layoutClose);
            TextView bankTV = dialog.findViewById(R.id.bankTV);
            TextView instantTV = dialog.findViewById(R.id.instantTV);
            TextView giftCardTV = dialog.findViewById(R.id.giftCardTV);

            setSpannableText("Bank Accounts: All Bank account transactions typically take up to 3 business days to process.", context, bankTV, 14);
            setSpannableText("Instant Pay: Withdrawing money to an Instant Pay bank account uses your debit card to facilitate the process. This typically takes about 30 seconds to process, but can take up to 30 minutes depending on your bank. Most, but not all banks support Instant Pay.", context, instantTV, 12);
            setSpannableText("Gift Cards: Purchasing a gift card with your coyni tokens typically takes about 1 minute to process the request and to send out the email that contains the gift card.", context, giftCardTV, 11);

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (mertics.heightPixels * 0.80));

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hideSoftKeypad(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int convertPxtoDP(int value) {
        int dpvalue = 0;
        dpvalue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
        return dpvalue;
    }

    public static String convertTwoDecimal(String strAmount) {
        String strValue = "", strAmt = "";
        try {
            if (strAmount.contains(" ")) {
                strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + strAmount.split(" ")[1];
            } else {
                strAmt = Utils.convertBigDecimalUSDC(strAmount);
//                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + mContext.getString(R.string.currency);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt));
            }
            Log.e("str", strValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public static String convertTxnDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertTxnDatebusiness(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MMMM dd, yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertTwoDecimalPoints(Double value) {
        return df.format(value);
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

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static String newDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static List<States> getStates(Context context) {
        String json = null;
        List<States> listStates = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            listStates = gson.fromJson(json, type);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listStates;
    }

    public static void openKeyPad(Context context, View view) {
//        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void emailPasswordIncorrectDialog(String msg, final Context context, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_login__em_pa_incorrect__bottom_sheet);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        CardView cvEmailOK = dialog.findViewById(R.id.cvEmailOK);

        cvEmailOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static String getBusinessName(MyApplication myApplicationObj, String key) {
        LogUtils.d("key", "key" + key);
        LogUtils.d("myApplicationObj", "myApplicationObj" + myApplicationObj);
        if (myApplicationObj.getBusinessTypeResp() != null && myApplicationObj.getBusinessTypeResp().getData() != null) {
            List<BusinessType> listBT = myApplicationObj.getBusinessTypeResp().getData();
            for (BusinessType businessType : listBT) {
                if (businessType.getKey().equalsIgnoreCase(key)) {
                    return businessType.getValue();
                }
            }
        }
        return key;
    }

    public static void populateBusinessTypes(Context context, EditText editText, MyApplication myApplicationObj, String from) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.business_types_bottom_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            Log.e("editext", editText.getText().toString());
            RecyclerView bTypesRV = dialog.findViewById(R.id.bTypesRV);
            EditText searchET = dialog.findViewById(R.id.searchET);
            TextView notFoundTV = dialog.findViewById(R.id.notFoundTV);
            BusinessTypeListAdapter businessTypeListAdapter = new BusinessTypeListAdapter(null, context, editText, dialog, from);

            List<BusinessType> listBT = new ArrayList<>();
            if (myApplicationObj.getBusinessTypeResp() != null && myApplicationObj.getBusinessTypeResp().getData() != null) {
                listBT = myApplicationObj.getBusinessTypeResp().getData();
            }

//            for (int i = 0; i < listBT.size(); i++) {
//                if (editText.getText().toString().toLowerCase().trim().equals(listBT.get(i).getValue().toLowerCase())) {
//                    listBT.get(i).setSelected(true);
//                } else {
//                    listBT.get(i).setSelected(false);
//                }
//            }
            if (listBT.size() > 0) {
                bTypesRV.setVisibility(View.VISIBLE);
                notFoundTV.setVisibility(View.GONE);
                businessTypeListAdapter = new BusinessTypeListAdapter(listBT, context, editText, dialog, from);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                bTypesRV.setLayoutManager(mLayoutManager);
                bTypesRV.setItemAnimator(new DefaultItemAnimator());
                bTypesRV.setAdapter(businessTypeListAdapter);
            } else {
                bTypesRV.setVisibility(View.GONE);
                notFoundTV.setVisibility(View.VISIBLE);
            }

            BusinessTypeListAdapter finalBTListAdapter = businessTypeListAdapter;
            Collections.sort(listBT);
            List<BusinessType> finalListBT = listBT;
            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String search_key = s.toString();
                        List<BusinessType> filterList = new ArrayList<>();
                        int sIndex = 0;
                        if (finalListBT.size() > 0) {
                            for (int i = 0; i < finalListBT.size(); i++) {
                                sIndex = finalListBT.get(i).getValue().toLowerCase().indexOf(search_key.toLowerCase());
                                if (sIndex == 0) {
                                    filterList.add(finalListBT.get(i));
                                }
                            }
                            if (filterList.size() > 0) {
                                bTypesRV.setVisibility(View.VISIBLE);
                                notFoundTV.setVisibility(View.GONE);
                                for (int i = 0; i < filterList.size(); i++) {
                                    if (editText.getText().toString().toLowerCase().trim().equals(filterList.get(i).getValue().toLowerCase())) {
                                        filterList.get(i).setSelected(true);
                                    } else {
                                        filterList.get(i).setSelected(false);
                                    }
                                }
                                finalBTListAdapter.updateList(filterList);
                            } else {
                                bTypesRV.setVisibility(View.GONE);
                                notFoundTV.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (mertics.heightPixels * 0.80));

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void setCustomSelectionActionModeCallback(TextInputEditText editText) {
        editText.setLongClickable(false);
        editText.setTextIsSelectable(false);
        editText.setClickable(false);
        editText.setPressed(false);
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
    }


    public static String convertPayoutDate(String date) {
        String strDate = "";
        try {

            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy @ hh:mma");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertUTCtoPST(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TimeZone gmtTime = TimeZone.getTimeZone("GMT");
            spf.setTimeZone(gmtTime);
            Date newDate = spf.parse(date);
            SimpleDateFormat sp = new SimpleDateFormat("MM/dd/yyyy @ hh:mma");
            TimeZone pstTime = TimeZone.getTimeZone("PST");
            sp.setTimeZone(pstTime);
            strDate = sp.format(newDate);
            return strDate;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void setSpannableText(String text, Context context, TextView spannableTV, int end) {

        SpannableString ss = new SpannableString(text);

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ss.setSpan(new RelativeSizeSpan(1f), 0, end, 0);
        ss.setSpan(bss, 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(context.getColor(R.color.primary_black)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableTV.setText(ss);
        spannableTV.setMovementMethod(LinkMovementMethod.getInstance());
        spannableTV.setHighlightColor(Color.TRANSPARENT);
    }


    public static String addNxtDay(String date) {
        String strDate = "";
        try {

            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();

            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy @ ");

            String todayAsString = dateFormat.format(today);
            String tomorrowAsString = dateFormat.format(tomorrow);

            return tomorrowAsString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }
}

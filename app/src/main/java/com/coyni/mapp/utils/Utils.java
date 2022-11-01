package com.coyni.mapp.utils;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;


import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.R;
import com.coyni.mapp.adapters.BusinessTypeListAdapter;
import com.coyni.mapp.adapters.CustomerTimeZonesAdapter;
import com.coyni.mapp.adapters.StatesListAdapter;
import com.coyni.mapp.model.DBAInfo.BusinessType;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.bank.SignOnData;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.paymentmethods.PaymentsList;
import com.coyni.mapp.model.tracker.TrackerItem;
import com.coyni.mapp.model.users.TimeZoneModel;
import com.coyni.mapp.view.EnableAuthID;
import com.coyni.mapp.view.LoginActivity;
import com.coyni.mapp.view.OnboardActivity;
import com.coyni.mapp.view.PINActivity;
import com.coyni.mapp.view.PreferencesActivity;
import com.coyni.mapp.view.WebViewActivity;
import com.coyni.mapp.view.business.CompanyInformationActivity;
import com.coyni.mapp.view.business.DBAInfoAcivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean QA_SKIP_ENCRYPTION = false;
    public static final String URI_COYNI = "coyni";
    public static final String URI_PAID_ORDER = "paidOrder";
    public static final String URI_ENCRYPTED_TOKEN = "encryptedToken";
    public static final String SKIP_ENCRYPTION = "skip_encryption";

    public static final String COMPANY_ID = "companyId";
    public static final String IS_TRACKER = "is_Tracker";
    public static int PERSONAL_ACCOUNT = 1, BUSINESS_ACCOUNT = 2, SHARED_ACCOUNT = 3;
    public static String PERSONAL = "Personal", BUSINESS = "Business", SHARED = "Shared";
    public static final String TOKEN = "0", MERCHANT = "1", RESERVE = "2";
    public static final String TOKEN_STR = "TOKEN", MERCHANT_STR = "MERCHANT", RESERVE_STR = "RESERVE";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;

    public static enum BUSINESS_ACCOUNT_STATUS {
        UNDER_REVIEW("Under Review"),
        ACTIVE("Active"),
        DEACTIVE("DeActivated"),
        ACTION_REQUIRED("Action Required"),
        ADDITIONAL_INFO_REQUIRED("Additional Info Required"),
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


    public static enum STRING_PREFERENCE {
        PST(0, "PST"),
        MST(1, "America/Denver"),
        CST(2, "CST"),
        EST(3, "America/New_York"),
        HST(4, "HST"),
        AST(5, "AST"),
        SST(6, "US/Samoa");

        int zoneID;
        String strPreference;

        STRING_PREFERENCE(int zoneID, String strPreference) {
            this.zoneID = zoneID;
            this.strPreference = strPreference;
        }

        public int getZoneID() {
            return zoneID;
        }

        public String getStrPreference() {
            return strPreference;
        }

    }

    public enum ROLLING_LIST_STATUS {

        OPEN(6, "Open"),
        ON_HOLD(7, "On Hold"),
        RELEASED(8, "Released"),
        CANCELED(10, "Canceled"),
        FAILED(9, "Failed");

        private int statusType;
        private String status;

        ROLLING_LIST_STATUS(int statusType, String status) {
            this.status = status;
            this.statusType = statusType;
        }

        public String getStatus() {
            return status;
        }

        public int getStatusType() {
            return statusType;
        }
    }

    public static String strLang = "en-US";
    public static String strCode = "12345";
    public static String strDesc = "abcd";
    public static String strCCode = "";
    public static String strAuth;
    public static String appVersion;
    public static String strReferer;
    public static String strURL_PRODUCTION;
    public static Boolean isFaceEnabled;
    public static Boolean isTouchEnabled;
    public static Boolean isBiometric = false;
    public static final String COMMENT_ACTION = "comment_action";
    public static final String transInProgress = "inprogress";
    public static final String SELECTED_BATCH_PAYOUT = "selectedBatchPayout";
    public static final String transPending = "pending";
    public static final String transCompleted = "completed";
    public static final String transOpen = "open";
    public static final String transFailed = "failed";
    public static final String partialrefund = "partial refund";
    public static final String partial_refund = "partialrefund";
    public static final String refundd = "refunded";
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
    public static final String CogentType = "14";
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
    public static final String Verifying = "Verifying";
    public static final String gbxTransID = "gbxTransID";
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
    public static final String Swiped = "Swiped";
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
    public static String helpURL = "https://forms.monday.com/forms/embed/574096202882f845863fd5a5c2b1d61d?r=use1";
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
    public static final int refunded = 7;
    public static final int partialRefund = 8;
    public static final int accountTransfer = 0; //Not available
    public static final int paidInvoice = 15;
    public static final int businessPayout = 19;

    public static final int sent = 8;
    public static final int token = 11;
    public static final int received = 9;
    public static final int bankAccount = 0;
    public static final int creditCard = 2;
    public static final int debitCard = 3;
    public static final int Cogent = 14;
    public static final int Signet = 7;
    public static final int instantPay = 1;
    public static final int giftCard = 6;
    public static final int saleOrderToken = 11; //need to confirm
    public static final int failedWithdraw = 11; // need to confirm
    public static final int cancelledWithdraw = 18;
    public static final int transfer = 10;

    public static final int pending = 1;
    public static final int completed = 2;
    public static final int cancelled = 5;
    public static final int inProgress = 0;
    public static final int failed = 3;
    public static final int paid = 5;
    public static final int payoutInProgress = 4;
    public static final int payoutFailed = 9;

    public static final int reserveRelease = 16;
    public static final int batchNow = 19;
    public static final int open = 1;
    public static final int onHold = 7;
    public static final int released = 8;
    public static final int reserve_failed = 9;

    //Merchant Transaction Filter Type values

    public static final int mRefund = 9;
    public static final int merchantPayout = 19;
    public static final int monthlyServiceFee = 17;

    public static final int mCompleted = 0;
    public static final int msRefund = 1;
    public static final int mPartialRefund = 2;
    public static final String SELECTED_MERCHANT_TRANSACTION = "Selected_Merchant_transaction";
    public static final String SELECTED_MERCHANT_TRANSACTION_GBX_ID = "Selected_Merchant_transaction_gbx_ID";
    public static final String SELECTED_MERCHANT_TRANSACTION_TXN_TYPE = "Selected_Merchant_transaction_txn_type";
    public static final String SELECTED_MERCHANT_TRANSACTION_TXN_SUB_TYPE = "Selected_Merchant_transaction_txn_sub_type";
    public static final float slidePercentage = 0.3f;
    public static final float slidePercentagehalf = 0.5f;

    public static boolean isKeyboardVisible = false;
    public static boolean isSettingsBtnClicked = false;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static final String ACTION_TYPE = "action_type";
    public static final String TRANSACTION_TOKEN = "Transaction_token";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String changeActionType = "CHANGE";
    public static final String withdrawActionType = "WITHDRAW";
    public static final String buyActionType = "BUY";
    public static final String sendActionType = "SEND";
    public static final String pinActionType = "COYNIPIN";
    public static final String paidActionType = "PAIDORDER";
    public static final String refundActionType = "REFUND";
    public static final String batchnowActionType = "MERCHANT_PAYOUT";

    public static final String MERCHANT_TRANSACTION_PARTIAL_REFUND = "Partial Refund";
    public static final String MERCHANT_TRANSACTION_COMPLETED = "Completed";
    public static final String MERCHANT_TRANSACTION_FAILED = "Failed";

    public static final String OPEN = "open";
    public static final String PAID = "paid";
    public static final String INPROGRESS = "In Progress";
    public static final String CLOSED = "closed";
    public static final String RELEASED = "released";
    public static final String ONHOLD = "on hold";
    public static final String FAILED = "Failed";
    public static final String CANCELED = "canceled";
    public static String processType = "M";

    public static final String SaleOrder = "Sale Order";
    public static final String Refund = "Refund";
    public static final String MerchantPayout = "Merchant Payout";
    public static final String MonthlyServiceFee = "Monthly Service Fee";

    public static final String ADD_BUSINESS = "ADDBUSINESS";
    public static final String ADD_DBA = "ADD_DBA";
    public static final String IS_FIRST_DBA = "is_first_dba";
    public static final String NEW_DBA = "NEW DBA";
    public static final String ACCESS_TOKEN_EXPIRED = "Access token expired";
    public static final String TIME_EXCEEDED = "User inactive time exceeded";
    public static final String RE_CREATE = "User activation time expired, kindly re-create the process.";

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
    public static final String DeActivated = "DeActivated";
    public static final String teammemberpending = "Pending";
    public static final String resendInvitation = "Resend Invitation";
    public static final String actionRequired = "Action Required";
    public static final String underReview = "Under Review";
    public static final String unVerified = "Unverified";
    public static final String canceled = "Canceled";
    public static final String expired = "Expired";

    public static final String wallet = "wallet";
    public static final String amount = "amount";
    public static final String sentt = "sent";

    public static final String applyFilter = "apply";
    public static final String resetFilter = "resetFilter";
    public static final String datePicker = "DatePicker";
    public static final String SelectStoredDate = "SelectStoredDate";
    public static final String selectfromdate = "selectfromdate";
    public static final String selecttodate = "selecttodate";

    public static final String position = "Position";
    public static final int cPP = 1;
    public static final int cTOS = 0;
    public static final int mPP = 1;
    public static final int mTOS = 0;
    public static final int mAgmt = 2;
    public static final int ACTIVE_AGREEMENT = 0;
    public static final int SCHEDULED_AGREEMENT = 3;

    public static Class<?> launchedActivity = OnboardActivity.class;
    public static final String NOTIFICATION_ACTION = "com.coyni.notification_received";

    //Cards
    public static final String MASTERCARD = "MASTERCARD";
    public static final String VISA = "VISA";
    public static final String AMERICANEXPRESS = "AMERICAN EXPRESS";
    public static final String DISCOVER = "DISCOVER";

    //Display Alert double popup check
    public static Dialog displayAlertDialog = null;

    public static boolean isDeploymentPopup = false;

    public static final String buyBankEnable = "token account.buy tokens.external bank account";
    public static final String buyDebitEnable = "token account.buy tokens.debit card";
    public static final String buyCreditEnable = "token account.buy token.credit card";
    public static final String buyCogentEnable = "token account.buy token.cogent account";
    public static final String buySignetEnable = "token account.buy token.signet account";
    public static final String withBankEnable = "token account.withdrawals.external bank account";
    public static final String withInstantEnable = "token account.withdrawals.instant pay";
    public static final String withGiftEnable = "token account.withdrawals.gift card";
    public static final String withCogentEnable = "token account.withdrawals.cogent account";
    public static final String withSignetEnable = "token account.withdrawals.signet account";
    public static final String saleOrderEnable = "token account.sale orders.token";
    public static final String payEnable = "token account.pay/request.pay";
    public static final String requestEnable = "token account.pay/request.request";
    public static final String payBankEnable = "token account.payment methods.external bank account";
    public static final String payDebitEnable = "token account.payment methods.debit card";
    public static final String payCreditEnable = "token account.payment methods.credit card";
    public static final String payCogentEnable = "token account.payment methods.cogent account";
    public static final String paySignetEnable = "token account.payment methods.signet account";
    public static final String allControlsEnable = "token account.all controls";

    public static final String AGREEMENT_TYPE = "AGREE_TYPE";
    public static final String ACT_TYPE = "ACT_TYPE";
    public static final String DOC_URL = "DOC_URL";
    public static final String DOC_NAME = "DOC_NAME";
    public static final String single = "Single";
    public static final String multiple = "Multiple";
    public static final String verifyActionType = "Verify";

    public static final long centuryTimeInMillis = 3155760000000L;

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
        Activity activity = (Activity) context;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void shwForcedKeypad(Context context) {
//        Activity  activity = (Activity) context;
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeypadWithHandler(Context context) {
        new Handler().postDelayed(() -> {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100);

    }

    public static void shwForcedKeypadWithHandler(Context context) {
        new Handler().postDelayed(() -> {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100);

    }

    public static void hideKeypad(Context context) {
        try {
            Activity activity = (Activity) context;
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    activity.getCurrentFocus().getWindowToken(), 0);
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

    public static String getCurrentDate() {
        try {
            SimpleDateFormat spf = new SimpleDateFormat("MM/dd/yyyy");
            return spf.format(new Date());
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getDocUpdatedDate() {
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
            return spf.format(new Date());
        } catch (Exception ex) {
            return null;
        }
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
            } else if (msg.equalsIgnoreCase(RE_CREATE)) {
                activity.finish();
            } else {
                if (displayAlertDialog != null) {
                    displayAlertDialog.dismiss();
                    displayAlertNew(msg, activity, header);
                } else {
                    displayAlertNew(msg, activity, header);
                }
            }
        } else {
            if (displayAlertDialog != null) {
                displayAlertDialog.dismiss();
                displayAlertNew(fieldError, activity, header);
            } else {
                displayAlertNew(fieldError, activity, header);
            }
        }
    }

    public static String convertToWithoutDecimal(String amount) {
        String strValue = "";
        try {
            int amt = Integer.parseInt(amount);
            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
            strValue = format.format(amt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public static String convertBigDecimalUSDC(String amount) {
        String strValue = "";
        try {
            Double amt = Utils.doubleParsing(amount.replaceAll(",", ""));
            NumberFormat format = DecimalFormat.getCurrencyInstance(Locale.US);
            format.setMaximumFractionDigits(2);
//            strValue = format.format(amt).replaceAll("$", "").replaceAll("USD", "").replace("CYN", "");
            strValue = format.format(amt).replace("$", "").replace("USD", "").replace("CYN", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return strValue;
    }

    public static String convertBigDecimalUSD(String amount) {
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

    public static void checkAuthentication(Fragment fragment, int CODE_AUTHENTICATION_VERIFICATION) {
        try {
            KeyguardManager km = (KeyguardManager) fragment.getContext().getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Intent i = km.createConfirmDeviceCredentialIntent("Authentication required", "password");
                    fragment.startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
                }
            } else
                displayAlert("You enabled the Security permission in coyni App. Please enable the Security settings in device for making the transactions.", fragment.getActivity(), "", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                displayAlert("You enabled the Security permission in coyni App. Please enable the Security settings in device for making the transactions.", context, "", "");
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

    public static void showDialogPermission(final Context context, String header, String description) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_permission);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView notNowBtn, settingsBtn, headerText, descriptionText;

        notNowBtn = dialog.findViewById(R.id.not_now_tv);
        settingsBtn = dialog.findViewById(R.id.settings_tv);
        headerText = dialog.findViewById(R.id.headerTextTV);
        descriptionText = dialog.findViewById(R.id.descriptonTV);

        if (header != null) {
            headerText.setText(header);
        }
        if (description != null) {
            descriptionText.setText(description);
        }

        notNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isSettingsBtnClicked = true;
                context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null)));
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();


    }

    public static void showDialogCheckOut(final Context context, String header, String description) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_permission);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView notNowBtn, settingsBtn, headerText, descriptionText;

        notNowBtn = dialog.findViewById(R.id.not_now_tv);
        settingsBtn = dialog.findViewById(R.id.settings_tv);
        headerText = dialog.findViewById(R.id.headerTextTV);
        descriptionText = dialog.findViewById(R.id.descriptonTV);

        settingsBtn.setText("Yes");
        notNowBtn.setText("No");

        if (header != null) {
            headerText.setText(header);
        }
        if (description != null) {
            descriptionText.setText(description);
        }

        notNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isSettingsBtnClicked = true;
                context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null)));
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    public static String formatDate(String date) {
        if (date.length() == 22) {
            date = date + "0";
        }
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strDate = spf.format(newDate);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String payoutDate(String date) {
        if (date.length() == 22) {
            date = date + "0";
        }
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("MM-dd-yyyy");
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

    public static void setUpperHintColor(TextInputLayout til, int color) {
        try {
            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    color
            };
            ColorStateList myList = new ColorStateList(states, colors);
            til.setDefaultHintTextColor(myList);
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
        displayAlertDialog = new Dialog(context);
        displayAlertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        displayAlertDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        displayAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = displayAlertDialog.findViewById(R.id.tvHead);
        TextView message = displayAlertDialog.findViewById(R.id.tvMessage);
        CardView actionCV = displayAlertDialog.findViewById(R.id.cvAction);
        TextView actionText = displayAlertDialog.findViewById(R.id.tvAction);

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog.dismiss();
            }
        });

        if (msg.equals("")) {
            message.setText(context.getString(R.string.please_try_after));
        } else
            message.setText(msg);
        Window window = displayAlertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        displayAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        displayAlertDialog.setCanceledOnTouchOutside(true);
        displayAlertDialog.show();
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

    public static Dialog showProgressDialog(Context context) {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loader);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.setCanceledOnTouchOutside(true);
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

            tzm = new TimeZoneModel();
            tzm.setTimezone(context.getString(R.string.SST));
            tzm.setTimezoneID(6);
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
                        PreferencesActivity preferencesActivity = (PreferencesActivity) context;
                        preferencesActivity.callTimeZonePreferenceApi();
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

    public static String getStateCode(String state, List<States> listStates) {

        if (state.trim().length() == 2) {
            return state.toUpperCase();
        } else {
            if (listStates == null) {
                return null;
            }
            for (int i = 0; i < listStates.size(); i++) {
                if (state.equalsIgnoreCase(listStates.get(i).getName().toLowerCase())) {
                    return listStates.get(i).getIsocode();
                }
            }
        }
        return null;
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
                String[] split = strAmount.split(" ");
                //strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
                strValue = Utils.convertBigDecimalUSDC(split[0]) + " " + split[1];
            } else {
                strValue = Utils.convertBigDecimalUSDC(strAmount);
//                strValue = Utils.USNumberFormat(Utils.doubleParsing(strAmt)) + " " + mContext.getString(R.string.currency);
//                strValue = Utils.USNumberFormat(Utils.doubleParsing(strAmt));
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

    public static String convertEffectiveDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public static String dateFormat(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("yyyy-MM-dd");
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
            spf = new SimpleDateFormat("MM/dd/yyyy hh:mma");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertMerchantDate(String date) {
        String strDate = "";
        try {

            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy hh:mma");
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

    public static String getCapsSentences(String Name) {
        if (Name == null) {
            return "";
        }
        String[] splits = Name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord.substring(0, 1).toUpperCase()
                    + eachWord.substring(1);
            sb.append(cap);
        }
        return sb.toString();
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

    public static void setSpannableStringAtEnd(String text, Context context, int start, TextView textView) {

        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.primary_black)),
                start, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), start, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(spannable);
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

    public static Bitmap convertImageURIToBitMap(Context context, String encodedString) {
        try {
            Bitmap bitmap = MediaStore.Images.Media
                    .getBitmap(context.getContentResolver(),
                            Uri.parse(encodedString));
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static int monthsBetweenDates(Date startDate, Date endDate) {
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

    public static PaymentMethodsResponse filterPaymentMethods(PaymentMethodsResponse objResponse) {
        PaymentMethodsResponse payMethodsResponse = objResponse;
        List<PaymentsList> listData = new ArrayList<>();
        try {
            if (objResponse != null && objResponse.getData() != null && objResponse.getData().getData() != null && objResponse.getData().getData().size() > 0) {
                for (int i = 0; i < objResponse.getData().getData().size(); i++) {
                    if (!objResponse.getData().getData().get(i).getPaymentMethod().toLowerCase().equals("Cogent")) {
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

    public static Date getDate(String date) {
        Date dtExpiry = null;
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
            dtExpiry = spf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dtExpiry;
    }

    public static Date simpleDate(String date) {
        Date dtExpiry = null;
        try {
            SimpleDateFormat spf = new SimpleDateFormat("MM/dd/yyyy");
            dtExpiry = spf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dtExpiry;
    }

    public static PaymentMethodsResponse businessPaymentMethods(int accountType, PaymentMethodsResponse objResponse, String strScreen) {
        try {
            if (accountType == Utils.BUSINESS_ACCOUNT || accountType == Utils.SHARED_ACCOUNT) {
                PaymentMethodsResponse objData = objResponse;
                List<PaymentsList> listPayments = objData.getData().getData();
                List<PaymentsList> listBusPayments = new ArrayList<>();
                if (listPayments != null && listPayments.size() > 0) {
                    for (int i = 0; i < listPayments.size(); i++) {
//                        if (listPayments.get(i).getPaymentMethod() != null
//                                && (listPayments.get(i).getPaymentMethod().toLowerCase().equals("bank") || listPayments.get(i).getPaymentMethod().toLowerCase().equals("Cogent"))) {
//                        if (listPayments.get(i).getPaymentMethod() != null && (listPayments.get(i).getPaymentMethod().toLowerCase().equals("bank"))) {
                        if (strScreen.equals("")) {
                            if (listPayments.get(i).getPaymentMethod() != null && (!listPayments.get(i).getPaymentMethod().toLowerCase().equals("credit"))) {
                                listBusPayments.add(listPayments.get(i));
                            }
                        } else {
//                            if (listPayments.get(i).getPaymentMethod() != null && (listPayments.get(i).getPaymentMethod().toLowerCase().equals("bank")
//                                    || listPayments.get(i).getPaymentMethod().toLowerCase().equalsIgnoreCase("Cogent")) || listPayments.get(i).getPaymentMethod().toLowerCase().equalsIgnoreCase("Signet")) {
                            if (listPayments.get(i).getPaymentMethod() != null && (listPayments.get(i).getPaymentMethod().toLowerCase().equals("bank"))) {
                                listBusPayments.add(listPayments.get(i));
                            }
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

    public static String setNameHead(String strName) {
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
                        if (strName.split(" ").length > 1) {
                            strNameHead = strName.split(" ")[0].substring(0, 1).toUpperCase() + strName.split(" ")[1].substring(0, 1).toUpperCase();
                        } else {
                            strNameHead = strName.split(" ")[0].substring(0, 1).toUpperCase();
                        }
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

    public static String transactionDate(String date, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String transactionTime(String date, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String reserveDate(String date, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertPreferenceZoneToUtcDateTime(String date, String format, String requiredFormat, String zoneId) {
        String strDate = "";
        try {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern(format)
//                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
            ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(requiredFormat);
            zonedTime = zonedTime.withZoneSameInstant(ZoneOffset.UTC);
            strDate = zonedTime.format(DATE_TIME_FORMATTER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertPrefZoneTimeFromPST(String date, String format, String requiredFormat, String zoneId) {
        String strDate = "";
        try {
            Log.e("date", date);
            Log.e("format", format);
            Log.e("requiredFormat", requiredFormat);
            Log.e("zoneId", zoneId);
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern(format)
//                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneId.of("PST", ZoneId.SHORT_IDS));
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(requiredFormat);
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertZoneDateTime(String date, String format, String requiredFormat, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern(format)
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(requiredFormat);
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat(format);
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat(requiredFormat);
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }


    public static String exportDate(String date, String zoneId) {
        if (date.length() == 22) {
            date = date + "0";
        }
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneId.of(zoneId, ZoneId.SHORT_IDS));

                Log.e("getStrPreference", zoneId);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                zonedTime = zonedTime.withZoneSameInstant(ZoneOffset.UTC);
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertZoneLatestTxn(String date, String zoneId) {
        String strDate = "";
        if (date != null && date.contains(".")) {
            date = date.split("\\.")[0];
        }
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertZoneLatestTxndate(String date, String zoneId) {
        String strDate = "";
        if (date != null && date.contains(".")) {
            date = date.split("\\.")[0];
        }
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }


    public static String convertZoneReservedOn(String date, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = null;
                if (date.length() == 23) {
                    dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
                            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                            .toFormatter()
                            .withZone(ZoneOffset.UTC);
                } else {
                    dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SS")
                            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                            .toFormatter()
                            .withZone(ZoneOffset.UTC);
                }
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = null;
                if (date.length() == 23) {
                    spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                } else {
                    spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

                }
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertNewZoneDate(String date, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String convertPayoutDateTimeZone(String date, String zoneId) throws ParseException {
        String strDate = "";

        if (Build.VERSION.SDK_INT >= 26) {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.S")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mma");
            zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
            strDate = zonedTime.format(DATE_TIME_FORMATTER);
        } else {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            spf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MM/dd/yyyy hh:mma");
            spf.setTimeZone(TimeZone.getTimeZone(zoneId));
            strDate = spf.format(newDate);
        }
        return strDate;
    }

    public static String convertZoneDateLastYear(String date, String zoneId) {
        String strDate = "";
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC);
                ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
                strDate = zonedTime.format(DATE_TIME_FORMATTER);
            } else {
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                spf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newDate = spf.parse(date);
                spf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                spf.setTimeZone(TimeZone.getTimeZone(zoneId));
                strDate = spf.format(newDate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void callResolveFlow(Activity activity, String strSignOn, SignOnData signOnData) {
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

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null && phoneNumber.equals("")) {
            return "";
        }
        if (phoneNumber.contains("(")) {
            return phoneNumber;
        }

        if (phoneNumber.length() != 10) {
            return phoneNumber;
        }
        return "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
    }

    public static String convertNotificationTime(String date, String zoneId) {
        String strDate = "";
        String timeAgo = "";
        try {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
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
            zonedTime = zonedTimeNow.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS));
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

    private String capitizeString(String name) {
        String captilizedString = "";
        if (!name.trim().equals("")) {
            captilizedString = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return captilizedString;
    }


    //Dynamic Tracker - VT

    public static List<TrackerItem> trackerList = new ArrayList<>();

    public static void prepareDefaultTrackerData() {
        trackerList = new ArrayList<>();
        //Company Info
        TrackerItem trackerItem1 = new TrackerItem();
        trackerItem1.setFeatureIcon(R.drawable.ic_company_info_business);
        trackerItem1.setFeatureName("Company Information");
        trackerItem1.setFeatureSatus(BUSINESS_TRACKER_STATUS.INCOMPLETE.getStatus());
        trackerList.add(trackerItem1);

        //DBA Info
        TrackerItem trackerItem2 = new TrackerItem();
        trackerItem2.setFeatureIcon(R.drawable.ic_dba);
        trackerItem2.setFeatureName("DBA Information");
        trackerItem2.setFeatureSatus(BUSINESS_TRACKER_STATUS.INCOMPLETE.getStatus());
        trackerList.add(trackerItem2);

        //BO Info
        TrackerItem trackerItem3 = new TrackerItem();
        trackerItem3.setFeatureIcon(R.drawable.ic_bo);
        trackerItem3.setFeatureName("Beneficial Owner");
        trackerItem3.setFeatureSatus(BUSINESS_TRACKER_STATUS.INCOMPLETE.getStatus());
        trackerList.add(trackerItem3);

        //Add bank Info
        TrackerItem trackerItem4 = new TrackerItem();
        trackerItem4.setFeatureIcon(R.drawable.ic_add_payment_business);
        trackerItem4.setFeatureName("Add Bank Account");
        trackerItem4.setFeatureSatus(BUSINESS_TRACKER_STATUS.INCOMPLETE.getStatus());
        trackerList.add(trackerItem4);

        //Agreements Info
        TrackerItem trackerItem5 = new TrackerItem();
        trackerItem5.setFeatureIcon(R.drawable.ic_agreements);
        trackerItem5.setFeatureName("Merchant's Agreement");
        trackerItem5.setFeatureSatus(BUSINESS_TRACKER_STATUS.INCOMPLETE.getStatus());
        trackerList.add(trackerItem5);
    }

    public static List<TrackerItem> getBusinessTrackerData() {
        return trackerList;
    }

    public static void setBusinessTrackerData(List<TrackerItem> data) {
        trackerList = data;
    }

    public static enum BUSINESS_TRACKER_FEATURE {
        COMPANY_INFO(0),
        DBA_INFO(1),
        BENEFICIAL_OWNERS(2),
        ADD_BANK(3),
        AGREEMENTS(4);

        private int feature;

        BUSINESS_TRACKER_FEATURE(int feature) {
            this.feature = feature;
        }

        public int getFeature() {
            return feature;
        }
    }

    public static enum BUSINESS_TRACKER_STATUS {
        INCOMPLETE("Incomplete"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed");

        private String status;

        BUSINESS_TRACKER_STATUS(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    //Dynamic Tracker - VT

    public static void deploymentErrorDialog() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (!isDeploymentPopup) {
                    Activity activity = OnboardActivity.onboardActivity;
                    Dialog dialog = new Dialog(activity);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    DisplayMetrics mertics = activity.getResources().getDisplayMetrics();
                    int width = mertics.widthPixels;

                    TextView message = dialog.findViewById(R.id.tvMessage);
                    CardView actionCV = dialog.findViewById(R.id.cvAction);

                    actionCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            isDeploymentPopup = false;
                        }
                    });

                    message.setText("Looks like we are having an issue, please try after sometime.");
                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);

                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    dialog.setCanceledOnTouchOutside(true);
                    try {
                        dialog.show();
                        isDeploymentPopup = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public static void setStateFromList(String state, EditText editText, MyApplication myApplication) {
        if (state.length() == 2) {
            List<States> listStates = myApplication.getListStates();
            for (int i = 0; i < listStates.size(); i++) {
                if (state.trim().equals(listStates.get(i).getIsocode())) {
                    editText.setText(listStates.get(i).getName());
                    break;
                }
            }
        } else {
            editText.setText(state);
        }
    }

    public static Double doubleParsing(String value) {
        return Double.parseDouble(value.replaceAll(",", "").replaceAll("$", "").replaceAll("USD", ""));
    }

    public static String newFormatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null && phoneNumber.equals("")) {
            return "";
        }
        if (phoneNumber.contains("(")) {
            return phoneNumber;
        }

        if (phoneNumber.length() != 10) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
    }

    public static void setTextSize(TextView tv1, String text, float textSize) {
        if (tv1 == null) {
            return;
        }
        if (text != null && text.length() > 0) {
            float maxTextSize = textSize;
            if (text.length() > 6) {
                maxTextSize = maxTextSize / 2 - (text.length() / 2);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, maxTextSize);
            }
            tv1.setText(text);
        }
    }

    public static void setErrorSpannableText(String text, Context context, TextView spannableTV, int start) {

        SpannableString ss = new SpannableString(text);

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ss.setSpan(new RelativeSizeSpan(1f), start, ss.length(), 0);
        ss.setSpan(bss, start, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(context.getColor(R.color.error_red)), start, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableTV.setText(ss);
        spannableTV.setMovementMethod(LinkMovementMethod.getInstance());
        spannableTV.setHighlightColor(Color.TRANSPARENT);
    }

    //Request for permissions
    public static boolean checkAndRequestStoragePermission(final Activity context) {
        try {
            int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(context, listPermissionsNeeded
                                .toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public static void showUpdateDialog(Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.app_name)
                .setMessage(context.getString(R.string.appUpdate))
                .setCancelable(false)
                .setPositiveButton("Update", (dialog, which) -> {
                    dialog.dismiss();
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("market://details?id=com.coyni.mapp"));
                    viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(viewIntent);
                }).show();
    }

    public static boolean isValidFileSize(File file) {
        int maxFileSize = 10 * 1000 * 1000;
        Long l = file.length();
        String fileSize = l.toString();
        int finalFileSize = Integer.parseInt(fileSize);
        if (finalFileSize > maxFileSize)
            return false;
        else
            return true;
    }
}

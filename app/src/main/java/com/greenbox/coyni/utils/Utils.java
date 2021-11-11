package com.greenbox.coyni.utils;

import static android.content.Context.KEYGUARD_SERVICE;

import static android.content.Context.FINGERPRINT_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.greenbox.coyni.R;
import com.greenbox.coyni.view.OnboardActivity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static int PERSONAL_ACCOUNT = 1, BUSINESS_ACCOUNT = 2, SHARED_ACCOUNT = 3;
    public static String strLang = "en-US";
    public static String strCode = "12345";
    public static String strCCode = "";
    public static String strAuth;
    public static String strReferer;
    public static String strURL_PRODUCTION;
    public static Boolean isFaceEnabled;
    public static Boolean isTouchEnabled;
    public static final String transInProgress = "inprogress";
    public static final String transPending = "pending";
    public static final String transCompleted = "completed";
    public static final String transFailed = "failed";
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
    public static final int inviteId = 74;
    public static final int requestId = 75;
    public static final int remindId = 76;
    public static final int declineId = 67;
    public static final int acceptedId = 60;
    public static final int pageSize = 25;
    public static String deviceID = "";
    public static Long mLastClickTime = 0L;

    public static String getStrLang() {
        return strLang;
    }

    public static String getStrCode() {
        return strCode;
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

    public static void hideKeypad(Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            spf = new SimpleDateFormat("MMM dd, yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void displayAlert(String msg, Activity activity) {
        Context context = new ContextThemeWrapper(activity, R.style.Theme_Coyni);
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setCancelable(false)
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

    public static void showCustomToast(final Context context, String text, int imageID) {
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
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView textTV = dialog.findViewById(R.id.toastTV);
        ImageView imageIV = dialog.findViewById(R.id.toastIV);
        textTV.setText(text);
        imageIV.setImageResource(imageID);

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        new OnboardActivity().toastTimer(dialog);

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

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

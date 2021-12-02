package com.greenbox.coyni.utils;

import static android.content.Context.KEYGUARD_SERVICE;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.biometric.BiometricManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.CustomerTimeZonesAdapter;
import com.greenbox.coyni.adapters.StatesListAdapter;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.users.TimeZoneModel;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.view.EnableAuthID;
import com.greenbox.coyni.view.OnboardActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.view.PreferencesActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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
    public static final int duration = 1000;

    public static int[][] errorState, state;
    public static int[] errorColor, color;
    public static ColorStateList errorColorState, colorState;
    public static String tempState = "";
    public static String mondayURL = "https://monday.com/";


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
            spf = new SimpleDateFormat("MMM dd, yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static void displayAlert(String msg, Activity activity, String header) {
//        Context context = new ContextThemeWrapper(activity, R.style.Theme_Coyni);
//        new MaterialAlertDialogBuilder(context)
//                .setTitle(R.string.app_name)
//                .setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK", (dialog, which) -> {
//                    dialog.dismiss();
//                }).show();

        displayAlertNew(msg, activity, header);
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
                displayAlert("You enabled the Security permission in Coyni App. Please enable the Security settings in device for making the transactions.", context, "");
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
        imageIV.setImageResource(imageID);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        if (strScreen.equals("pin")) {
            new PINActivity().toastTimer(dialog);
        } else {
            new EnableAuthID().toastTimer(dialog);
        }

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

    public static ColorStateList getErrorColorState() {
        errorState = new int[][]{new int[]{-android.R.attr.state_focused}, new int[]{android.R.attr.state_focused}};
        errorColor = new int[]{OnboardActivity.onboardActivity.getResources().getColor(R.color.error_red), OnboardActivity.onboardActivity.getResources().getColor(R.color.error_red)};
        errorColorState = new ColorStateList(errorState, errorColor);

        return errorColorState;
    }

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
            BiometricManager biometricManager = BiometricManager.from(context.getApplicationContext());
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
        dialog.show();
        return dialog;
    }

    public static void populateTimeZones(PreferencesActivity preferenceActivity, EditText editText, MyApplication myApplicationObj) {
        // custom dialog
        final Dialog dialog = new Dialog(preferenceActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timezones_bottom_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = preferenceActivity.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        CardView actionCV = dialog.findViewById(R.id.cvAction);
        TextView actionText = dialog.findViewById(R.id.tvAction);
        RecyclerView timezonesRV = dialog.findViewById(R.id.timezonesRV);

        try {
            ArrayList<TimeZoneModel> arrZonesList = new ArrayList<>();
            TimeZoneModel tzm = new TimeZoneModel();
            tzm.setTimezone(preferenceActivity.getString(R.string.EST));
            tzm.setTimezoneID(3);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(preferenceActivity.getString(R.string.CST));
            tzm.setTimezoneID(2);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(preferenceActivity.getString(R.string.MST));
            tzm.setTimezoneID(1);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(preferenceActivity.getString(R.string.PST));
            tzm.setTimezoneID(0);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(preferenceActivity.getString(R.string.AST));
            tzm.setTimezoneID(5);
            arrZonesList.add(tzm);

            tzm = new TimeZoneModel();
            tzm.setTimezone(preferenceActivity.getString(R.string.HST));
            tzm.setTimezoneID(4);
            arrZonesList.add(tzm);

            for (int i = 0; i < arrZonesList.size() - 1; i++) {
                if (myApplicationObj.getTimezoneID() == arrZonesList.get(i).getTimezoneID()) {
                    arrZonesList.get(i).setSelected(true);
                }
            }

            CustomerTimeZonesAdapter customerTimeZonesAdapter = new CustomerTimeZonesAdapter(arrZonesList, preferenceActivity);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(preferenceActivity);
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

                UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
                userPreferenceModel.setLocalCurrency(0);
                userPreferenceModel.setTimezone(myApplicationObj.getTempTimezoneID());
                userPreferenceModel.setPreferredAccount(myApplicationObj.getMyProfile().getData().getId());
                preferenceActivity.customerProfileViewModel.updatePreferences(userPreferenceModel);
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

    public static void setUserEmail(Context context,String email) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("DeviceID", MODE_PRIVATE).edit();
            editor.putString("userEmail", email);
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getUserEmail(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("DeviceID", MODE_PRIVATE);
        return sharedPreferences.getString("userEmail","");
    }

    public static void populateStates(Context context, EditText editText, MyApplication myApplicationObj) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.states_bottom_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            Log.e("editext", editText.getText().toString());
            CardView actionCV = dialog.findViewById(R.id.cvAction);
            TextView actionText = dialog.findViewById(R.id.tvAction);
            RecyclerView statesRV = dialog.findViewById(R.id.statesRV);
            EditText searchET = dialog.findViewById(R.id.searchET);
            TextView notFoundTV = dialog.findViewById(R.id.notFoundTV);
            StatesListAdapter statesListAdapter = new StatesListAdapter(null, context, "EditAddress");

            List<States> listStates = myApplicationObj.getListStates();

            for (int i = 0; i < listStates.size() - 1; i++) {
                if (editText.getText().toString().trim().equals(listStates.get(i).getIsocode())) {
                    listStates.get(i).setSelected(true);
                }else{
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
            }else{
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
                                finalStatesListAdapter.updateList(filterList);
                            }else{
                                statesRV.setVisibility(View.GONE);
                                notFoundTV.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    editText.setText(tempState);
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


}

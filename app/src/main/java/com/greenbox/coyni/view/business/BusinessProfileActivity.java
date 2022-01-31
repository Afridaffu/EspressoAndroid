package com.greenbox.coyni.view.business;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.Business_ReceivePaymentActivity;
import com.greenbox.coyni.view.Business_UserDetailsListenersActivity;
import com.greenbox.coyni.view.ConfirmPasswordActivity;
import com.greenbox.coyni.view.CustomerProfileActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class BusinessProfileActivity extends AppCompatActivity {

    private LinearLayout feesLL, teamLL,bpbackBtn,switchOffLL,switchOnLL;
    boolean isTogleBtn=false;
    private Long mLastClickTime = 0L;
    CardView business_userProfileCV,statusDot;
    DashboardViewModel dashboardViewModel;
    MyApplication myApplication;
    Boolean isSwitchEnabled = false;
    CoyniViewModel coyniViewModel;
    public static SQLiteDatabase mydatabase;
    ImageView profileImage;
    TextView profileText,account_status,account_id,userFullname,b_tvBMSetting;

    Dialog enablePopup;
    Cursor cursor;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    static String strToken = "";
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    static Cursor dsPermanentToken, dsFacePin, dsTouchID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_profile);
       initFields();
       initObservers();
    }
    

    private void initFields() {
        try {
            feesLL = findViewById(R.id.feesLL);
            teamLL = findViewById(R.id.teamLL);
            bpbackBtn = findViewById(R.id.b_backBtn);
            switchOnLL=findViewById(R.id.switchOn);
            switchOffLL=findViewById(R.id.switchOff);
            profileImage=findViewById(R.id.b_profileIV);
            profileText=findViewById(R.id.b_imageTextTV);
            account_status=findViewById(R.id.b_tvACStatus);
            statusDot=findViewById(R.id.b_statusDotCV);
            account_id=findViewById(R.id.b_accountIDTV);
            userFullname=findViewById(R.id.b_nameTV);
            b_tvBMSetting=findViewById(R.id.b_tvBMSetting);
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            myApplication=(MyApplication) getApplicationContext();
            dashboardViewModel=new ViewModelProvider(this).get(DashboardViewModel.class);
            business_userProfileCV=findViewById(R.id.business_userProfileCV);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);


            switchOffLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTogleBtn=true;
                    switchOnLL.setVisibility(View.VISIBLE);
                    switchOffLL.setVisibility(View.GONE);
                }
            });

            switchOnLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTogleBtn=false;
                    switchOnLL.setVisibility(View.GONE);
                    switchOffLL.setVisibility(View.VISIBLE);
                }
            });


            bpbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            teamLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this,TeamActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            feesLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this,FeesActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.business_UserDetailsLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        startActivity(new Intent(BusinessProfileActivity.this, UserDetailsActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            findViewById(R.id.b_cpChangePassword).setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
//                Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
//                        .putExtra("TYPE", "ENTER")
//                        .putExtra("screen", "ChangePassword");
//                startActivity(i);

                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BusinessProfileActivity.this)) {
                    if (isBiometric && ((isTouchId && Utils.isFingerPrint(BusinessProfileActivity.this)) || (isFaceLock))) {
                        Utils.checkAuthentication(BusinessProfileActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                    } else {
                        Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "ChangePassword");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ChangePassword");
                    startActivity(i);
                }
            });


            business_userProfileCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        startActivity(new Intent(BusinessProfileActivity.this, Business_ReceivePaymentActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            if (myApplication.getMyProfile().getData().getAccountStatus() != null) {
                try {
                    if (myApplication.getMyProfile().getData().getAccountStatus().equals("Active")) {
                        account_status.setTextColor(getResources().getColor(R.color.active_green));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.active_green));
                    } else if (myApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                        account_status.setTextColor(getResources().getColor(R.color.orange));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.orange));
                    } else if (myApplication.getMyProfile().getData().getAccountStatus().equals("Under Review")) {
                        account_status.setTextColor(getResources().getColor(R.color.under_review_blue));
                        statusDot.setCardBackgroundColor(getResources().getColor(R.color.under_review_blue));
                    } else {
                    }
                    //                    if (myApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                    //                        cardviewYourAccount.setVisibility(View.VISIBLE);
                    //                    } else {
                    //                        cardviewYourAccount.setVisibility(View.GONE);
                    //                    }
                    account_status.setText(myApplication.getMyProfile().getData().getAccountStatus());
                    account_id.setText("Account ID M-" + myApplication.getMyProfile().getData().getId());
                    String fullname=Utils.capitalize(myApplication.getMyProfile().getData().getFirstName() + " " + myApplication.getMyProfile().getData().getLastName());
                    userFullname.setText(fullname);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                account_status.setText("");
            }
            bindImage(myApplication.getMyProfile().getData().getImage());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void initObservers() {
        dashboardViewModel.getProfileMutableLiveData().

                observe(this, new Observer<Profile>() {
                    @Override
                    public void onChanged(Profile profile) {
                        try {
                            if (profile != null) {
                                myApplication.setMyProfile(profile);
                                bindImage(profile.getData().getImage());

                                if (profile.getData().getAccountStatus() != null) {
                                    try {
                                        if (profile.getData().getAccountStatus().equals("Active")) {
                                            account_status.setTextColor(getResources().getColor(R.color.active_green));
                                            statusDot.setCardBackgroundColor(getResources().getColor(R.color.active_green));
                                        } else if (profile.getData().getAccountStatus().equals("Unverified")) {
                                            account_status.setTextColor(getResources().getColor(R.color.orange));
                                            statusDot.setCardBackgroundColor(getResources().getColor(R.color.orange));
                                        } else if (profile.getData().getAccountStatus().equals("Under Review")) {
                                            account_status.setTextColor(getResources().getColor(R.color.under_review_blue));
                                            statusDot.setCardBackgroundColor(getResources().getColor(R.color.under_review_blue));
                                        } else {
                                        }
                                        //                    if (myApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                                        //                        cardviewYourAccount.setVisibility(View.VISIBLE);
                                        //                    } else {
                                        //                        cardviewYourAccount.setVisibility(View.GONE);
                                        //                    }
                                        account_status.setText(profile.getData().getAccountStatus());
                                        account_id.setText("Account ID M-" + profile.getData().getId());
                                        String fullname=Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
                                        userFullname.setText(fullname);
                                    } catch (Resources.NotFoundException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    account_status.setText("");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dashboardViewModel.meProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void bindImage(String imageString) {
        try {
            profileImage.setVisibility(View.GONE);
            profileText.setVisibility(View.VISIBLE);
            String imageTextNew = "";
            imageTextNew = imageTextNew + myApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    myApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            profileText.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                profileImage.setVisibility(View.VISIBLE);
                profileText.setVisibility(View.GONE);
                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(profileImage);
            } else {
                profileImage.setVisibility(View.GONE);
                profileText.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + myApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        myApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                profileText.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
//            if (enablePopup != null) {
//                enablePopup.dismiss();
//            }
//            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE && resultCode == RESULT_OK) {
////                dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
////                dialog.setIndeterminate(false);
////                dialog.setMessage("Please wait...");
////                dialog.show();
//                BiometricRequest biometricRequest = new BiometricRequest();
//                biometricRequest.setBiometricEnabled(true);
//                biometricRequest.setDeviceId(Utils.getDeviceID());
//                coyniViewModel.saveBiometric(biometricRequest);
//            } else
                if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                if (resultCode == RESULT_OK) {
                    Intent cp = new Intent(BusinessProfileActivity.this, ConfirmPasswordActivity.class);
                    startActivity(cp);
                } else {
                    Intent i = new Intent(BusinessProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ChangePassword");
                    startActivity(i);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dropAllTables() {
        try {
            enableBiometric(false);
            mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblRemember;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblThumbPinLock;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblPermanentToken;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblDontRemind;");
            SharedPreferences prefs = getSharedPreferences("DeviceID", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableBiometric(Boolean value) {
        try {
            BiometricRequest biometricRequest = new BiometricRequest();
            biometricRequest.setBiometricEnabled(value);
            biometricRequest.setDeviceId(Utils.getDeviceID());
            coyniViewModel.saveBiometric(biometricRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void isSwitchEnable() {
        try {
            if (!isSwitchEnabled) {
//                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                if (b_tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        enablePopup = showFaceTouchEnabledDialog(this, "TOUCH");
                    } else {
//                        dialog = Utils.showProgressDialog(this);
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                } else {

                    if (Utils.checkBiometric(BusinessProfileActivity.this)) {
                        if (!Utils.isFingerPrint(BusinessProfileActivity.this)) {
//                            dialog = Utils.showProgressDialog(this);
                            BiometricRequest biometricRequest = new BiometricRequest();
                            biometricRequest.setBiometricEnabled(true);
                            biometricRequest.setDeviceId(Utils.getDeviceID());
                            coyniViewModel.saveBiometric(biometricRequest);
                        }
                    } else {
                        enablePopup = showFaceTouchEnabledDialog(this, "FACE");
                    }
                }
            } else {
//                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                if (b_tvBMSetting.getText().toString().toLowerCase().contains("face")) {
//                    dialog = Utils.showProgressDialog(this);
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                } else {
//                    dialog = Utils.showProgressDialog(this);
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveThumb(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblThumbPinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("Delete from tblThumbPinLock");
            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                Log.e("Thumb", cursor.getString(1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFace(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("Delete from tblFacePinLock");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
            cursor = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                Log.e("Face", cursor.getString(1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveToken(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
            mydatabase.execSQL("Delete from tblPermanentToken");
            mydatabase.execSQL("INSERT INTO tblPermanentToken(id,perToken) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Dialog showFaceTouchEnabledDialog(final Context context, String type) {
        // custom dialog
        final Dialog dDialog = new Dialog(context);
        dDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dDialog.setContentView(R.layout.enable_disable_face_touch_dialog);
        dDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        CardView enableCV = dDialog.findViewById(R.id.enableCV);
        TextView tvEnable = dDialog.findViewById(R.id.tvEnable);
        TextView tvHead = dDialog.findViewById(R.id.tvHead);
        TextView tvMessage = dDialog.findViewById(R.id.tvMessage);
        LinearLayout notNowLL = dDialog.findViewById(R.id.notNowLL);

        if (type.equals("FACE")) {
            tvHead.setText(context.getString(R.string.set_up_face_id));
            tvEnable.setText(context.getString(R.string.set_up_face_id));
            tvMessage.setText(context.getString(R.string.enable_face_message));
        } else {
            tvHead.setText(context.getString(R.string.set_up_touch_id));
            tvEnable.setText(context.getString(R.string.set_up_touch_id));
            tvMessage.setText(context.getString(R.string.enable_touch_message));
        }

        notNowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dDialog.dismiss();
            }
        });

        enableCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("TOUCH")) {
                    FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG);
                        startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                    } else {
//                        dialog = Utils.showProgressDialog(context);
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                } else {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            }
        });

        Window window = dDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dDialog.setCanceledOnTouchOutside(true);
        dDialog.show();

        return dDialog;
    }



    private boolean getLocalBiometricEnabled() {
        boolean isFace = false;
        boolean isTouch = false;
        boolean isBiometric = false;
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            cursor = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String value = cursor.getString(1);
                if (value.equals("true")) {
                    isFace = true;
                    myApplication.setLocalBiometric(true);
                } else {
                    isFace = false;
                    myApplication.setLocalBiometric(false);
                }
            }

            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String value = cursor.getString(1);
                if (value.equals("true")) {
                    isTouch = true;
                    myApplication.setLocalBiometric(true);
                } else {
                    isTouch = false;
                    myApplication.setLocalBiometric(false);
                }
            }

            isBiometric = isFace || isTouch;


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isBiometric;
    }

    public boolean isTouchEnabled() {
        boolean touch = false;
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String value = cursor.getString(1);
                if (value.equals("true")) {
                    touch = true;
                } else {
                    touch = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return touch;
    }

    public boolean isFaceEnabled() {
        boolean face = false;
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            cursor = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String value = cursor.getString(1);
                if (value.equals("true")) {
                    face = true;
                } else {
                    face = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return face;
    }
    public static void SetToken(MyApplication objMyApplication, Activity activity) {
        try {
            mydatabase = activity.openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsPermanentToken = mydatabase.rawQuery("Select * from tblPermanentToken", null);
            dsPermanentToken.moveToFirst();
            if (dsPermanentToken.getCount() > 0) {
                strToken = dsPermanentToken.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SetFaceLock(MyApplication objMyApplication, Activity activity) {
        try {
            isFaceLock = false;
            mydatabase = activity.openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFaceLock = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SetTouchId(MyApplication objMyApplication, Activity activity) {
        try {
            isTouchId = false;
            mydatabase = activity.openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsTouchID.moveToFirst();
            if (dsTouchID.getCount() > 0) {
                String value = dsTouchID.getString(1);
                if (value.equals("true")) {
                    isTouchId = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouchId = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
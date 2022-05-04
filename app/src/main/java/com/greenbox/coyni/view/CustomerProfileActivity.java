package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.logout.LogoutResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfileActivity extends BaseActivity {
    ImageView imgQRCode, profileIV;
    LinearLayout cpbackBtn;
    //    ProgressDialog dialog;
    TextView customerNameTV, tvACStatus, tvBMSetting, cpAccountIDTV, imageTextTV, tvVersion;
    MyApplication objMyApplication;
    CardView cvLogout;
    ConstraintLayout userProfile;
    LinearLayout cpUserDetailsLL, cpPaymentMethodsLL, cpResetPin, cpAccountLimitsLL, cpAgreementsLL, cpChangePasswordLL, switchOff, switchOn, cpPreferencesLL;
    Long mLastClickTime = 0L, mLastClickTimeShare = 0L;
    public static SQLiteDatabase mydatabase;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    CoyniViewModel coyniViewModel;
    Boolean isSwitchEnabled = false;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    Cursor cursor;
    DashboardViewModel dashboardViewModel;
    CardView cardviewYourAccount, statusDotCV;
    Dialog enablePopup;
    Dialog qrDialog;
    String strWallet = "";
    private DatabaseHandler dbHandler;
    private LoginViewModel loginViewModel;
    static String strToken = "";
    static String strDeviceID = "";
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251, CODE_AUTHENTICATION = 512;
    private final int CODE_AUTHENTICATION_VERIFICATION_RESET_PIN = 252;
    String authenticateType = "";
    boolean isLoggedOut = false;
    CustomerProfileViewModel customerProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_customer_profile);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            imgQRCode = findViewById(R.id.imgQRCode);
            cpbackBtn = findViewById(R.id.cpbackBtn);
            imageTextTV = findViewById(R.id.imageTextTV);
            profileIV = findViewById(R.id.profileIV);
            customerNameTV = findViewById(R.id.customerNameTV);
            cpAccountIDTV = findViewById(R.id.cpAccountIDTV);
            cvLogout = findViewById(R.id.cvLogout);
            cpUserDetailsLL = findViewById(R.id.cpUserDetailsLL);
            cpPreferencesLL = findViewById(R.id.cpPreferencesLL);
            switchOff = findViewById(R.id.switchOff);
            switchOn = findViewById(R.id.switchOn);
            cpPaymentMethodsLL = findViewById(R.id.cpPaymentMethodsLL);
            tvACStatus = findViewById(R.id.tvACStatus);
            cpResetPin = findViewById(R.id.cpResetPin);
            cpAccountLimitsLL = findViewById(R.id.cpAccountLimitsLL);
            cpAgreementsLL = findViewById(R.id.cpAgreementsLL);
            switchOff = findViewById(R.id.switchOff);
            switchOn = findViewById(R.id.switchOn);
            cpChangePasswordLL = findViewById(R.id.cpChangePassword);
            tvBMSetting = findViewById(R.id.tvBMSetting);
            userProfile = findViewById(R.id.linearLayout);
            cardviewYourAccount = findViewById(R.id.cardviewYourAccount);
            statusDotCV = findViewById(R.id.statusDotCV);
            tvVersion = findViewById(R.id.tvVersion);
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            dbHandler = DatabaseHandler.getInstance(CustomerProfileActivity.this);

            isBiometric = Utils.getIsBiometric();
            setToken();
            setFaceLock();
            setTouchId();
            bindImage(objMyApplication.getMyProfile().getData().getImage());

            if (objMyApplication.getMyProfile().getData().getAccountStatus() != null) {
                try {
                    if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Active")) {
                        tvACStatus.setTextColor(getResources().getColor(R.color.active_green));
                        statusDotCV.setCardBackgroundColor(getResources().getColor(R.color.active_green));
                    } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                        tvACStatus.setTextColor(getResources().getColor(R.color.orange));
                        statusDotCV.setCardBackgroundColor(getResources().getColor(R.color.orange));
                    } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Under Review")) {
                        tvACStatus.setTextColor(getResources().getColor(R.color.under_review_blue));
                        statusDotCV.setCardBackgroundColor(getResources().getColor(R.color.under_review_blue));
                    } else {
                    }
                    if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                        cardviewYourAccount.setVisibility(View.VISIBLE);
                    } else {
                        cardviewYourAccount.setVisibility(View.GONE);
                    }
                    tvACStatus.setText(objMyApplication.getMyProfile().getData().getAccountStatus());
                    cpAccountIDTV.setText("Account ID M-" + objMyApplication.getMyProfile().getData().getId());
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                tvACStatus.setText("");
            }

            try {
                if (objMyApplication.getStrUserName().length() > 20) {
                    customerNameTV.setText(objMyApplication.getStrUserName().substring(0, 20));
                } else {
                    customerNameTV.setText(objMyApplication.getStrUserName());
                }

                dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
                customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                    tvBMSetting.setText(getString(R.string.security_touchid));
                } else {
                    tvBMSetting.setText(getString(R.string.security_faceid));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            imgQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    displayQRCode();
                }
            });

            userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    displayQRCode();
                }
            });

            cvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        showProgressDialog();
                        loginViewModel.logout();
//                        isLoggedOut = true;
//                        objMyApplication.setStrRetrEmail("");
//                        dropAllTables();
//                        Intent i = new Intent(CustomerProfileActivity.this, OnboardActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cpChangePasswordLL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
//                Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
//                        .putExtra("TYPE", "ENTER")
//                        .putExtra("screen", "ChangePassword");
//                startActivity(i);

                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(CustomerProfileActivity.this)) {
                    if (isBiometric && ((isTouchId && Utils.isFingerPrint(CustomerProfileActivity.this)) || (isFaceLock))) {
                        Utils.checkAuthentication(CustomerProfileActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                    } else {
                        Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "ChangePassword");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ChangePassword");
                    startActivity(i);
                }
            });

            switchOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if (Utils.checkAuthentication(CustomerProfileActivity.this)) {
                        if (isBiometric && ((Utils.isFingerPrint(CustomerProfileActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(CustomerProfileActivity.this, CODE_AUTHENTICATION);
                        } else {
                            if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                                enablePopup = showFaceTouchEnabledDialog(CustomerProfileActivity.this, "TOUCH");
                            } else {
                                enablePopup = showFaceTouchEnabledDialog(CustomerProfileActivity.this, "FACE");
                            }
                        }
                    } else {
                        if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                            enablePopup = showFaceTouchEnabledDialog(CustomerProfileActivity.this, "TOUCH");
                        } else {
                            enablePopup = showFaceTouchEnabledDialog(CustomerProfileActivity.this, "FACE");
                        }
                    }
                }
            });

            switchOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    isSwitchEnable();
                }
            });

            cpAgreementsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(CustomerProfileActivity.this, AgreementsActivity.class));
                }
            });

            cpAccountLimitsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(CustomerProfileActivity.this, AccountLimitsActivity.class));
                }
            });

            cpUserDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            startActivity(new Intent(CustomerProfileActivity.this, UserDetailsActivity.class));
                        } else {
                            Utils.showCustomToast(CustomerProfileActivity.this, "Please complete your Identity Verification process.", 0, "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cpPaymentMethodsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            startActivity(new Intent(CustomerProfileActivity.this, PaymentMethodsActivity.class));
                        } else {
                            Utils.showCustomToast(CustomerProfileActivity.this, "Please complete your Identity Verification process.", 0, "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cpResetPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(CustomerProfileActivity.this)) {
                        if (isBiometric && ((isTouchId && Utils.isFingerPrint(CustomerProfileActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(CustomerProfileActivity.this, CODE_AUTHENTICATION_VERIFICATION_RESET_PIN);
                        } else {
                            startActivity(new Intent(CustomerProfileActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "ResetPIN"));
                        }
                    } else {
                        startActivity(new Intent(CustomerProfileActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "ResetPIN"));
                    }

                }
            });

            cpPreferencesLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(CustomerProfileActivity.this, PreferencesActivity.class));
                }
            });

            if (getLocalBiometricEnabled()) {
                isSwitchEnabled = true;
                switchOff.setVisibility(View.GONE);
                switchOn.setVisibility(View.VISIBLE);
            } else {
                isSwitchEnabled = false;
                switchOff.setVisibility(View.VISIBLE);
                switchOn.setVisibility(View.GONE);
            }

            cpbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            if (Utils.checkInternet(CustomerProfileActivity.this)) {
                dashboardViewModel.mePaymentMethods();
            } else {
                Utils.displayAlert(getString(R.string.internet), CustomerProfileActivity.this, "", "");
            }

            customerNameTV.setOnClickListener(view -> {

                if (customerNameTV.getText().toString().contains("...")) {
                    if (objMyApplication.getStrUserName().length() == 21 || objMyApplication.getStrUserName().length() > 21) {
                        customerNameTV.setText(objMyApplication.getStrUserName().substring(0, 20));
                    } else {
                        customerNameTV.setText(objMyApplication.getStrUserName());
                    }
                } else {
                    if (objMyApplication.getStrUserName().length() == 21) {
                        customerNameTV.setText(objMyApplication.getStrUserName().substring(0, 20) + "...");
                    } else if (objMyApplication.getStrUserName().length() > 21) {
                        customerNameTV.setText(objMyApplication.getStrUserName().substring(0, 20) + "...");
                    } else {
                        customerNameTV.setText(objMyApplication.getStrUserName());
                    }
                }
            });

            cardviewYourAccount.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(CustomerProfileActivity.this, BindingLayoutActivity.class);
                i.putExtra("screen", "profileGetStarted");
                startActivity(i);
            });

            tvVersion.setText("Version " + Utils.getAppVersion().replace("Android : ", ""));
            tvVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String strEndPoint = "";
                        strEndPoint = "End Point Url - " + Utils.getStrURL_PRODUCTION();
                        Utils.displayAlert(strEndPoint, CustomerProfileActivity.this, "API Details", "");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            //customerProfileViewModel.meSyncAccount();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onLogoutSuccess() {
        isLoggedOut = true;
        objMyApplication.setStrRetrEmail("");
        dropAllTables();
        Intent i = new Intent(CustomerProfileActivity.this, OnboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void displayQRCode() {
        try {
            ImageView imgClose, copyRecipientAddress;
            ImageView meQrCode, shareImage, imgProfile;
            TextView userFullName, userInfo, walletAddress;
            // saved to album
            TextView tvSaveUserName, saveProfileTitle, saveToAlbumTV;
            ImageView savedImageView;
            CircleImageView saveProfileIV;
            LinearLayout saveToAlbumLL;


            qrDialog = new Dialog(CustomerProfileActivity.this, R.style.DialogTheme);
            qrDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            qrDialog.setContentView(R.layout.profileqrcode);
            Window window = qrDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            qrDialog.getWindow().setAttributes(lp);
            qrDialog.show();
            imgClose = qrDialog.findViewById(R.id.imgClose);
            meQrCode = qrDialog.findViewById(R.id.idIVQrcode);
            userFullName = qrDialog.findViewById(R.id.tvName);
            userInfo = qrDialog.findViewById(R.id.tvUserInfo);
            imgProfile = qrDialog.findViewById(R.id.imgProfile);
            walletAddress = qrDialog.findViewById(R.id.tvWalletAddress);
            shareImage = qrDialog.findViewById(R.id.imgShare);
            //init Saved to Album Layout
            savedImageView = qrDialog.findViewById(R.id.qrImageIV);
            tvSaveUserName = qrDialog.findViewById(R.id.tvNameSave);
            saveProfileIV = qrDialog.findViewById(R.id.saveprofileIV);
            saveProfileTitle = qrDialog.findViewById(R.id.saveprofileTitle);
            saveToAlbumTV = qrDialog.findViewById(R.id.savetoAlbumTV);
            saveToAlbumLL = qrDialog.findViewById(R.id.saveToAlbumLL);

            String savedStrName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());

            if (savedStrName != null && savedStrName.length() > 22) {
                tvSaveUserName.setText(savedStrName.substring(0, 22) + "...");
            } else {
                tvSaveUserName.setText(savedStrName);
            }

            try {
                saveProfileIV.setVisibility(View.GONE);
                saveProfileTitle.setVisibility(View.VISIBLE);
                String imageString = objMyApplication.getMyProfile().getData().getImage();
                String imageTextNew = "";
                imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                saveProfileTitle.setText(imageTextNew);

                if (imageString != null && !imageString.trim().equals("")) {
                    saveProfileIV.setVisibility(View.VISIBLE);
                    saveProfileTitle.setVisibility(View.GONE);
                    Glide.with(this)
                            .load(imageString)
                            .placeholder(R.drawable.ic_profile_male_user)
                            .into(saveProfileIV);
                } else {
                    saveProfileIV.setVisibility(View.GONE);
                    saveProfileTitle.setVisibility(View.VISIBLE);
                    String imageText = "";
                    imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                            objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                    saveProfileTitle.setText(imageText);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }


            WalletResponseData walletResponse = objMyApplication.getCurrentUserData().getTokenWalletResponse();

            try {
                imgProfile.setVisibility(View.GONE);
                userInfo.setVisibility(View.VISIBLE);
                String imageString = objMyApplication.getMyProfile().getData().getImage();
                String imageTextNew = "";
                imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                userInfo.setText(imageTextNew);

                if (imageString != null && !imageString.trim().equals("")) {
                    imgProfile.setVisibility(View.VISIBLE);
                    userInfo.setVisibility(View.GONE);
                    Glide.with(this)
                            .load(imageString)
                            .placeholder(R.drawable.ic_profile_male_user)
                            .into(imgProfile);
                } else {
                    imgProfile.setVisibility(View.GONE);
                    userInfo.setVisibility(View.VISIBLE);
                    String imageText = "";
                    imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                            objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                    userInfo.setText(imageText);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (walletResponse != null && walletResponse.getWalletNames().size() > 0) {
                strWallet = walletResponse.getWalletNames().get(0).getWalletId();
                generateQRCode(strWallet);
                meQrCode.setImageBitmap(bitmap);
                savedImageView.setImageBitmap(bitmap);
                walletAddress.setText(walletResponse.getWalletNames().get(0).getWalletId().substring(0, 16) + "...");
            }
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrDialog.dismiss();
                }
            });

            String strUserName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() + "" + objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase());
            String strName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());
            userInfo.setText(strUserName.toUpperCase(Locale.US));
//            if (strName != null && strName.length() > 21) {
//                userFullName.setText(strName.substring(0, 22) + "...");
//            } else {
//                userFullName.setText(strName);
//            }

            if (strName != null) {
                userFullName.setText(strName);
            }

            shareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        if (SystemClock.elapsedRealtime() - mLastClickTimeShare < 2000) {
                            return;
                        }
                        mLastClickTimeShare = SystemClock.elapsedRealtime();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, strWallet);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            copyRecipientAddress = qrDialog.findViewById(R.id.imgCopy);
            copyRecipientAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ClipboardManager myClipboard;
                        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                        ClipData myClip;
                        String text = objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getWalletId();
                        myClip = ClipData.newPlainText("text", text);
                        myClipboard.setPrimaryClip(myClip);
//                    showToast();
                        Utils.showCustomToast(CustomerProfileActivity.this, "Your address has successfully copied to clipboard.", R.drawable.ic_custom_tick, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            saveToAlbumTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        saveToAlbumLL.setDrawingCacheEnabled(true);
                        saveToAlbumLL.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                        saveToAlbumLL.layout(0, 0, saveToAlbumLL.getMeasuredWidth(), saveToAlbumLL.getMeasuredHeight());

                        saveToAlbumLL.buildDrawingCache(true);
                        Bitmap b = Bitmap.createBitmap(saveToAlbumLL.getDrawingCache());
                        saveToAlbumLL.setDrawingCacheEnabled(false);


//            Bitmap b=Bitmap.createBitmap(imageSaveAlbumLL.getWidth(),imageSaveAlbumLL.getHeight(),Bitmap.Config.ARGB_8888);
//            Canvas canvas=new Canvas(b);
//            imageSaveAlbumLL.draw(canvas);
//            savedImageView.setImageBitmap(b);


                        MediaStore.Images.Media.insertImage(getContentResolver(), b, "Coyni-Qr", "this is QR");

                        Utils.showCustomToast(CustomerProfileActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dropAllTables() {
        try {
//            enableBiometric(false);

            dbHandler.clearAllTables();

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
                if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        enablePopup = showFaceTouchEnabledDialog(this, "TOUCH");
                    } else {
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                } else {
                    if (Utils.checkBiometric(CustomerProfileActivity.this)) {
                        if (!Utils.isFingerPrint(CustomerProfileActivity.this)) {
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
                if (tvBMSetting.getText().toString().toLowerCase().contains("face")) {
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                } else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (enablePopup != null) {
                enablePopup.dismiss();
            }
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE) {
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            } else if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                if (resultCode == RESULT_OK) {
                    showProgressDialog();
                    BiometricTokenRequest request = new BiometricTokenRequest();
                    request.setDeviceId(Utils.getDeviceID());
//                    request.setMobileToken(strToken);
                    request.setMobileToken(objMyApplication.getStrMobileToken());
                    request.setActionType(Utils.changeActionType);
                    coyniViewModel.biometricToken(request);
//                    Intent cp = new Intent(CustomerProfileActivity.this, ConfirmPasswordActivity.class);
//                    startActivity(cp);
                } else {
                    Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ChangePassword");
                    startActivity(i);
                }
            } else if (requestCode == CODE_AUTHENTICATION_VERIFICATION_RESET_PIN) {
                if (resultCode == RESULT_OK) {
                    Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "CHOOSE")
                            .putExtra("screen", "ResetPIN")
                            .putExtra("AUTH_TYPE", "TOUCH");
                    startActivity(i);
                } else {
                    Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ResetPIN")
                            .putExtra("AUTH_TYPE", "PIN");
                    startActivity(i);
                }
            } else if (requestCode == CODE_AUTHENTICATION) {
                if (resultCode == RESULT_OK) {
                    isSwitchEnable();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void saveThumb(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblThumbPinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
//            mydatabase.execSQL("Delete from tblThumbPinLock");
//            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
//            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                Log.e("Thumb", cursor.getString(1));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void saveFace(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
//            mydatabase.execSQL("Delete from tblFacePinLock");
//            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
//            cursor = mydatabase.rawQuery("Select * from tblFacePinLock", null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                Log.e("Face", cursor.getString(1));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void saveToken(String value) {
//        try {
//            objMyApplication.setStrMobileToken(value);
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
//            mydatabase.execSQL("Delete from tblPermanentToken");
//            mydatabase.execSQL("INSERT INTO tblPermanentToken(id,perToken) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void saveThumb(String value) {
        dbHandler.clearThumbPinLockTable();
        dbHandler.insertThumbPinLock(value);
    }

    private void saveFace(String value) {
        dbHandler.clearFacePinLockTable();
        dbHandler.insertFacePinLock(value);
    }

    private void saveToken(String value) {
        objMyApplication.setStrMobileToken(value);
        dbHandler.clearPermanentTokenTable();
        dbHandler.insertPermanentToken(value);
    }

    public void initObserver() {
        coyniViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricResponse>() {
            @Override
            public void onChanged(BiometricResponse biometricResponse) {
                try {
                    if (enablePopup != null) {
                        enablePopup.dismiss();
                    }
//                    dialog.dismiss();
                    if (biometricResponse != null) {
                        saveToken(biometricResponse.getData().getToken());
//                        Utils.generateUUID(CustomerProfileActivity.this);
                        if (!objMyApplication.isDeviceID()) {
                            Utils.generateUUID(CustomerProfileActivity.this);
                        }
                        if (!isSwitchEnabled) {
                            if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
//                                saveFace("false");
//                                saveThumb("true");
                                if (!isLoggedOut) {
                                    saveFace("false");
                                    saveThumb("true");
                                    Utils.showCustomToast(CustomerProfileActivity.this, "Touch ID has been turned on", R.drawable.ic_touch_id, "authid");
                                }
                            } else {
//                                saveFace("true");
//                                saveThumb("false");
                                if (!isLoggedOut) {
                                    saveFace("true");
                                    saveThumb("false");
                                    Utils.showCustomToast(CustomerProfileActivity.this, "Face ID has been turned on", R.drawable.ic_faceid, "authid");
                                }
                            }

                            isSwitchEnabled = true;
                            switchOn.setVisibility(View.VISIBLE);
                            switchOff.setVisibility(View.GONE);
                            objMyApplication.setBiometric(true);
                        } else {
                            if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                                if (!isLoggedOut)
                                    Utils.showCustomToast(CustomerProfileActivity.this, "Touch ID has been turned off", R.drawable.ic_touch_id, "authid");
                            } else {
                                if (!isLoggedOut)
                                    Utils.showCustomToast(CustomerProfileActivity.this, "Face ID has been turned off", R.drawable.ic_faceid, "authid");
                            }
                            objMyApplication.setBiometric(false);
                            if (!isLoggedOut) {
                                saveFace("false");
                                saveThumb("false");
                            }
                            isSwitchEnabled = false;
                            switchOn.setVisibility(View.GONE);
                            switchOff.setVisibility(View.VISIBLE);
                            Log.e("isFace1", isFaceEnabled() + "");
                            Log.e("isTouch1", isTouchEnabled() + "");
                        }

                        setToken();
                        setFaceLock();
                        setTouchId();

                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse paymentMethodsResponse) {
                if (paymentMethodsResponse != null) {
                    PaymentMethodsResponse objResponse = objMyApplication.filterPaymentMethods(paymentMethodsResponse);
                    objMyApplication.setPaymentMethodsResponse(objResponse);
                }
            }
        });

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile != null) {
                    objMyApplication.setMyProfile(profile);
                    bindImage(objMyApplication.getMyProfile().getData().getImage());
                }
            }
        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                dismissDialog();
                if (biometricTokenResponse != null) {
                    if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                        if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        }
                        Intent cp = new Intent(CustomerProfileActivity.this, ConfirmPasswordActivity.class);
                        startActivity(cp);
                    }
                }
            }
        });

        loginViewModel.getLogoutLiveData().observe(this, new Observer<LogoutResponse>() {
            @Override
            public void onChanged(LogoutResponse logoutResponse) {
                dismissDialog();
                if (logoutResponse != null) {
                    if (logoutResponse.getStatus().toLowerCase().equals("success")) {
                        onLogoutSuccess();
                    } else {
                        if (!logoutResponse.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(logoutResponse.getError().getErrorDescription(), CustomerProfileActivity.this, "", "");
                        } else {
                            Utils.displayAlert(logoutResponse.getError().getFieldErrors().get(0), CustomerProfileActivity.this, "", "");
                        }
                    }
                }
            }
        });
    }

//    private boolean getLocalBiometricEnabled() {
//        boolean isFace = false;
//        boolean isTouch = false;
//        boolean isBiometric = false;
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            cursor = mydatabase.rawQuery("Select * from tblFacePinLock", null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                String value = cursor.getString(1);
//                if (value.equals("true")) {
//                    isFace = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isFace = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                String value = cursor.getString(1);
//                if (value.equals("true")) {
//                    isTouch = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isTouch = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//
//            isBiometric = isFace || isTouch;
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return isBiometric;
//    }
//
//    public boolean isTouchEnabled() {
//        boolean touch = false;
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                String value = cursor.getString(1);
//                if (value.equals("true")) {
//                    touch = true;
//                } else {
//                    touch = false;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return touch;
//    }
//
//    public boolean isFaceEnabled() {
//        boolean face = false;
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            cursor = mydatabase.rawQuery("Select * from tblFacePinLock", null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                String value = cursor.getString(1);
//                if (value.equals("true")) {
//                    face = true;
//                } else {
//                    face = false;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return face;
//    }


    private boolean getLocalBiometricEnabled() {
        boolean isFace = false;
        boolean isTouch = false;
        boolean isBiometric = false;
        String value = dbHandler.getFacePinLock();

        if (value != null && value.equals("true")) {
            isFace = true;
            objMyApplication.setLocalBiometric(true);
        } else {
            objMyApplication.setLocalBiometric(false);
        }

        String valueTouch = dbHandler.getThumbPinLock();
        if (valueTouch != null && valueTouch.equals("true")) {
            isTouch = true;
            objMyApplication.setLocalBiometric(true);
        } else {
            objMyApplication.setLocalBiometric(false);
        }

        isBiometric = isFace || isTouch;


        return isBiometric;
    }

    public boolean isTouchEnabled() {
        boolean touch = false;
        String value = dbHandler.getThumbPinLock();
        if (value.equals("true")) {
            touch = true;
        }
        return touch;
    }

    public boolean isFaceEnabled() {
        boolean face = false;
        String value = dbHandler.getFacePinLock();
        if (value.equals("true")) {
            face = true;
        }
        return face;
    }


    private void bindImage(String imageString) {
        try {
            profileIV.setVisibility(View.GONE);
            imageTextTV.setVisibility(View.VISIBLE);
            String imageTextNew = "";
            imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            imageTextTV.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                profileIV.setVisibility(View.VISIBLE);
//                userProfile.setBackground(getResources().getDrawable(R.drawable.corecircle));
                imageTextTV.setVisibility(View.GONE);
                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(profileIV);
            } else {
                profileIV.setVisibility(View.GONE);
                imageTextTV.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                imageTextTV.setText(imageText);
            }

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

    private void generateQRCode(String wallet) {
        try {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
//            qrgEncoder = new QRGEncoder(wallet, null, QRGContents.Type.TEXT, dimen);
            qrgEncoder = new QRGEncoder(wallet, null, QRGContents.Type.TEXT, 600);
//            qrgEncoder = new NewQRGEncoder(wallet, null, QRGContents.Type.TEXT, 1024);
            // getting our qrcode in the form of bitmap.
//            bitmap = qrgEncoder.encodeAsBitmap();
            bitmap = Bitmap.createBitmap(qrgEncoder.encodeAsBitmap(), 50, 50, 500, 500);
//            bitmap  = Utils.trimLeave5Percent(bitmap, R.color.white);
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
//            qrDialog.idIVQrcode.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_recipientaddress, (ViewGroup) findViewById(R.id.toastRootLL));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            dashboardViewModel.meProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTouchId() {
        try {
            isTouchId = false;
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                isTouchId = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
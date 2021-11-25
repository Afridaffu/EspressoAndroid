package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import com.bumptech.glide.Glide;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdSetupBottomSheet;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class CustomerProfileActivity extends AppCompatActivity {
    ImageView imgQRCode, profileIV;
    LinearLayout cpbackBtn;
    ProgressDialog dialog;
    TextView customerNameTV, tvACStatus, tvBMSetting, cpAccountIDTV, imageTextTV;
    MyApplication objMyApplication;
    CardView cvLogout;
    LinearLayout cpUserDetailsLL, cpPaymentMethodsLL, cpResetPin, cpAccountLimitsLL, cpAgreementsLL, cpChangePasswordLL, switchOff, switchOn, cpPreferencesLL;
    Long mLastClickTime = 0L;
    SQLiteDatabase mydatabase;
    CoyniViewModel coyniViewModel;
    Boolean isSwitchEnabled = false;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    Cursor cursor;
    DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
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
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);

            customerNameTV.setText(objMyApplication.getStrUserName());

            bindImage(objMyApplication.getMyProfile().getData().getImage());

            if (objMyApplication.getMyProfile().getData().getAccountStatus() != null) {
                tvACStatus.setText(objMyApplication.getMyProfile().getData().getAccountStatus());
                cpAccountIDTV.setText("Account ID " + objMyApplication.getMyProfile().getData().getId());
            } else {
                tvACStatus.setText("");
            }
            if (objMyApplication.getStrUserName().length() > 21) {
                customerNameTV.setText(objMyApplication.getStrUserName().substring(0, 21) + "...");
            } else {
                customerNameTV.setText(objMyApplication.getStrUserName());
            }

            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            viewFaceBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FaceIdSetupBottomSheet faceIdSetupBottomSheet = new FaceIdSetupBottomSheet();
                    faceIdSetupBottomSheet.show(getSupportFragmentManager(), faceIdSetupBottomSheet.getTag());
                }
            });
            if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                tvBMSetting.setText(getString(R.string.security_touchid));
            } else {
                tvBMSetting.setText(getString(R.string.security_faceid));
            }

            imgQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    displayQRCode();
                }
            });

            cvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dropAllTables();
                        Intent i = new Intent(CustomerProfileActivity.this, OnboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finishAffinity();
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
                Intent i=new Intent(CustomerProfileActivity.this,PINActivity.class)
                        .putExtra("TYPE","ENTER")
                        .putExtra("screen","ChangePassword");
                startActivity(i);

            });

            switchOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSwitchEnable();
                }
            });

            switchOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSwitchEnable();
                }
            });

            cpAgreementsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CustomerProfileActivity.this, AgreementsActivity.class));
                }
            });

            cpAccountLimitsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CustomerProfileActivity.this, AccountLimitsActivity.class));
                }
            });

            cpUserDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        startActivity(new Intent(CustomerProfileActivity.this, UserDetailsActivity.class));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cpPaymentMethodsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

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
                    startActivity(new Intent(CustomerProfileActivity.this, PINActivity.class)
                            .putExtra("TYPE", "ENTER")
                            .putExtra("screen", "ResetPIN"));
                }
            });
            customerNameTV.setText(objMyApplication.getStrUserName());


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
                Utils.displayAlert(getString(R.string.internet), CustomerProfileActivity.this, "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


//    private void displayQRCode() {
//        try {
//            ImageView imgClose;
//            dialog = new Dialog(CustomerProfileActivity.this, R.style.DialogTheme);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.profileqrcode);
//            Window window = dialog.getWindow();
//            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            window.setGravity(Gravity.CENTER);
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            dialog.getWindow().setAttributes(lp);
//            dialog.show();
//            imgClose = dialog.findViewById(R.id.imgClose);
//            imgClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void dropAllTables() {
        try {
            enableBiometric(false);
            mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblRemember;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblThumbPinLock;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblPermanentToken;");
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
                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG);
                        startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                    } else {
                        dialog = Utils.showProgressDialog(this);
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                } else {
                    dialog = Utils.showProgressDialog(this);
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            } else {
                if (Utils.getIsTouchEnabled() || (!Utils.getIsTouchEnabled() && !Utils.getIsFaceEnabled())) {
                    dialog = Utils.showProgressDialog(this);
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                } else {
                    dialog = Utils.showProgressDialog(this);
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
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE && resultCode == RESULT_OK) {
                dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.show();
                BiometricRequest biometricRequest = new BiometricRequest();
                biometricRequest.setBiometricEnabled(true);
                biometricRequest.setDeviceId(Utils.getDeviceID());
                coyniViewModel.saveBiometric(biometricRequest);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void initObserver() {
        coyniViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricResponse>() {
            @Override
            public void onChanged(BiometricResponse biometricResponse) {
                try {
                    dialog.dismiss();
                    if (biometricResponse != null) {
                        saveToken(biometricResponse.getData().getToken());
                        Utils.generateUUID(CustomerProfileActivity.this);
                        if (!isSwitchEnabled) {
                            if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
                                saveFace("false");
                                saveThumb("true");
//                                Utils.showCustomToast(CustomerProfileActivity.this, "Touch ID has been turned on", R.drawable.ic_faceid, "authid");
                            } else {
                                saveFace("true");
                                saveThumb("false");
//                                Utils.showCustomToast(CustomerProfileActivity.this, "Face ID has been turned on", R.drawable.ic_touch_id, "authid");
                            }

                            isSwitchEnabled = true;
                            switchOn.setVisibility(View.VISIBLE);
                            switchOff.setVisibility(View.GONE);
                            objMyApplication.setBiometric(true);
                        } else {
//                            if (tvBMSetting.getText().toString().toLowerCase().contains("touch")) {
//                                Utils.showCustomToast(CustomerProfileActivity.this, "Touch ID has been turned off", R.drawable.ic_faceid, "authid");
//                            } else {
//                                Utils.showCustomToast(CustomerProfileActivity.this, "Face ID has been turned off", R.drawable.ic_touch_id, "authid");
//                            }
                            objMyApplication.setBiometric(false);
                            saveFace("false");
                            saveThumb("false");
                            isSwitchEnabled = false;
                            switchOn.setVisibility(View.GONE);
                            switchOff.setVisibility(View.VISIBLE);
                            Log.e("isFace1", isFaceEnabled() + "");
                            Log.e("isTouch1", isTouchEnabled() + "");
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse paymentMethodsResponse) {
                if (paymentMethodsResponse != null) {
                    objMyApplication.setPaymentMethodsResponse(paymentMethodsResponse);
                }
            }
        });
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
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFace = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }

            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            cursor = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String value = cursor.getString(1);
                if (value.equals("true")) {
                    isTouch = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouch = false;
                    objMyApplication.setLocalBiometric(false);
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
                imageTextTV.setVisibility(View.GONE);
                Glide.with(this)
                        .load(imageString)
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

}
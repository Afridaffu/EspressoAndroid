package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdSetupBottomSheet;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class CustomerProfileActivity extends AppCompatActivity {
    View viewFaceBottom;
    ImageView imgQRCode;
    ProgressDialog dialog;
    TextView customerNameTV, tvACStatus;
    MyApplication objMyApplication;
    CardView cvLogout;
    LinearLayout cpUserDetailsLL, cpPaymentMethodsLL, cpResetPin, cpAccountLimitsLL, cpAgreementsLL, cpChangePasswordLL, switchOff, switchOn, cpPreferencesLL;
    Long mLastClickTime = 0L;
    SQLiteDatabase mydatabase;
    CoyniViewModel coyniViewModel;
    Boolean isSwitchEnabled = false;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;

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
            viewFaceBottom = findViewById(R.id.viewSetupFaceBottom);
            imgQRCode = findViewById(R.id.imgQRCode);
            customerNameTV = findViewById(R.id.customerNameTV);
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
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);

            viewFaceBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FaceIdSetupBottomSheet faceIdSetupBottomSheet = new FaceIdSetupBottomSheet();
                    faceIdSetupBottomSheet.show(getSupportFragmentManager(), faceIdSetupBottomSheet.getTag());
                }
            });

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
                Intent i = new Intent(CustomerProfileActivity.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("screen", "ChangePassword");
                startActivity(i);

            });

            switchOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSwitchEnabled = true;
                    switchOff.setVisibility(View.GONE);
                    switchOn.setVisibility(View.VISIBLE);
                    isSwitchEnable();
                }
            });
            switchOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSwitchEnabled = false;
                    switchOn.setVisibility(View.GONE);
                    switchOff.setVisibility(View.VISIBLE);
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

            customerNameTV.setText(objMyApplication.getStrUserName());
            if (objMyApplication.getMyProfile().getData().getAccountStatus() != null) {
                tvACStatus.setText(objMyApplication.getMyProfile().getData().getAccountStatus());
            } else {
                tvACStatus.setText("");
            }
            if (objMyApplication.getStrUserName().length() > 21) {
                customerNameTV.setText(objMyApplication.getStrUserName().substring(0, 21) + "...");
            } else {
                customerNameTV.setText(objMyApplication.getStrUserName());
            }

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
            if (isSwitchEnabled) {
                if (Utils.getIsTouchEnabled()) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.isHardwareDetected()) {
                        Log.e("Not support", "Not support");
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG);
                        startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                    } else {
                        dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        BiometricRequest biometricRequest = new BiometricRequest();
                        biometricRequest.setBiometricEnabled(true);
                        biometricRequest.setDeviceId(Utils.getDeviceID());
                        coyniViewModel.saveBiometric(biometricRequest);
                    }
                } else if (Utils.getIsFaceEnabled()) {
                    dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(true);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                }
            } else {
                if (Utils.getIsTouchEnabled()) {
                    dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    BiometricRequest biometricRequest = new BiometricRequest();
                    biometricRequest.setBiometricEnabled(false);
                    biometricRequest.setDeviceId(Utils.getDeviceID());
                    coyniViewModel.saveBiometric(biometricRequest);
                } else if (Utils.getIsFaceEnabled()) {
                    dialog = new ProgressDialog(CustomerProfileActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
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
            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFace(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveToken(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
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
                        if (Utils.getIsFaceEnabled()) {
                            saveFace("true");
                            saveThumb("false");
                            Utils.showCustomToast(CustomerProfileActivity.this, "Face ID has been turned on", R.drawable.ic_faceid, "authid");

                        } else if (Utils.getIsTouchEnabled()) {
                            saveFace("false");
                            saveThumb("true");
                            Utils.showCustomToast(CustomerProfileActivity.this, "Touch ID has been turned on", R.drawable.ic_touch_id, "authid");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
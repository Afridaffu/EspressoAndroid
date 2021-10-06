package com.coyni.android.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coyni.android.model.APIError;
import com.coyni.android.model.contacts.Contacts;
import com.coyni.android.model.login.Login;
import com.coyni.android.model.login.LoginRequest;
import com.coyni.android.model.register.EmailResendResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText etEmail, etPassword;
    MyApplication objMyApplication;
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    Dialog popupPwdExpiry;
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails, dsFacePin;
    TextInputLayout etlPassword;
    LinearLayout layoutFace;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    String strEmail = "", strPwd = "", strMsg = "";
    Boolean isFace = false, isFaceLock = false, isThumb = false, isAllowed = false, isFaceLogin = false;
    ImageView imgFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (Utils.checkInternet(LoginActivity.this)) {
                        if (dsUserDetails.getCount() > 0) {
                            strEmail = dsUserDetails.getString(1);
                            strPwd = dsUserDetails.getString(2);
                            isFaceLogin = true;
                            login();
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.internet), LoginActivity.this);
                    }
                }
            });
        } else {
            //Utils.displayAlert("Failure: Unable to verify user's identity", LoginActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        objMyApplication.setLoginBack(true);
    }

    private void initialization() {
        try {
            TextView tvForgot;
            CardView cvLogin;
            LinearLayout layoutSignUp;
            ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
            etEmail = (TextInputEditText) findViewById(R.id.etEmail);
            etPassword = (TextInputEditText) findViewById(R.id.etPassword);
            etlPassword = (TextInputLayout) findViewById(R.id.etlPassword);
            tvForgot = (TextView) findViewById(R.id.tvForgot);
            cvLogin = (CardView) findViewById(R.id.cvLogin);
            layoutSignUp = (LinearLayout) findViewById(R.id.layoutSignUp);
            layoutFace = (LinearLayout) findViewById(R.id.layoutFace);
            imgFace = (ImageView) findViewById(R.id.imgFace);
            objMyApplication = (MyApplication) getApplicationContext();
            Utils.statusBar(LoginActivity.this);
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            cvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(LoginActivity.this, v);
                        if (validation()) {
                            if (dsUserDetails.getCount() == 0) {
                                strEmail = "";
                                strPwd = "";
                            }
                            if (compareCredentials()) {
                                strEmail = etEmail.getText().toString().trim().toLowerCase();
                                strPwd = etPassword.getText().toString().trim();
                                objMyApplication.setStrLEmail(strEmail.toLowerCase());
                                objMyApplication.setStrLPwd(strPwd);
                                if (!isFace && Utils.checkAuthentication(LoginActivity.this)) {
                                    if (!isFaceLock) {
                                        Context context = new ContextThemeWrapper(LoginActivity.this, R.style.Theme_QuickCard);
                                        new MaterialAlertDialogBuilder(context)
                                                .setTitle(R.string.app_name)
                                                .setMessage(strMsg)
                                                .setPositiveButton("YES", (dialog, which) -> {
                                                    if (isThumb) {
                                                        imgFace.setImageResource(R.drawable.ic_thumb_active);
                                                    } else {
                                                        imgFace.setImageResource(R.drawable.ic_face);
                                                    }
                                                    dialog.dismiss();
//                                            saveCredentials();
                                                    saveIsLock("true");
                                                    login();
                                                })
                                                .setNegativeButton("NO", (dialog1, which) -> {
                                                    dialog1.dismiss();
                                                    saveIsLock("false");
                                                    login();
                                                }).show();
                                    } else {
                                        login();
                                    }
                                } else {
                                    login();
                                }
                            } else {
                                Utils.displayAlert("Invalid user credentials", LoginActivity.this);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
                    startActivity(i);
                }
            });

            tvForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    i.putExtra("email", strEmail);
                    startActivity(i);
                }
            });

            layoutFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!compareCredentials()) {
                    if (!isFaceLock) {
//                        if (isThumb) {
//                            imgFace.setImageResource(R.drawable.ic_thumb_active);
//                        } else {
//                            imgFace.setImageResource(R.drawable.ic_face);
//                        }
                        isFace = true;
                        Context context = new ContextThemeWrapper(LoginActivity.this, R.style.Theme_QuickCard);
                        new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.app_name)
                                .setMessage(strMsg)
                                .setPositiveButton("YES", (dialog, which) -> {
                                    if (isThumb) {
                                        imgFace.setImageResource(R.drawable.ic_thumb_active);
                                    } else {
                                        imgFace.setImageResource(R.drawable.ic_face);
                                    }
                                    dialog.dismiss();
                                    isAllowed = true;
                                    saveIsLock("true");
                                })
                                .setNegativeButton("NO", (dialog1, which) -> {
                                    dialog1.dismiss();
                                    isAllowed = false;
                                    saveIsLock("false");
                                }).show();
                    } else {
                        Utils.checkAuthentication(LoginActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                    }
                }
            });
            enableIcon();
            SetDB();
            SetLock();
            //getContacts();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getLoginLiveData().observe(this, new Observer<Login>() {
            @Override
            public void onChanged(Login login) {
                try {
                    dialog.dismiss();
                    if (login != null) {
                        if (!login.getStatus().toLowerCase().equals("error")) {
                            if (login.getData().getPasswordExpired()) {
                                showPwdExpiredPopup();
                            } else {
                                Utils.setStrAuth(login.getData().getJwtToken());
                                if (login.getData().getCoyniPin() != null) {
                                    objMyApplication.setCoyniPin(login.getData().getCoyniPin());
                                } else {
                                    objMyApplication.setCoyniPin(false);
                                }
                                saveCredentials();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } else {
                            if (login.getData() != null) {
                                if (!isFaceLogin) {
                                    Utils.displayAlert(login.getData().getMessage(), LoginActivity.this);
                                } else {
                                    mydatabase.execSQL("Delete from tblFacePinLock");
                                    SetLock();
                                    imgFace.setImageResource(R.drawable.ic_thumb_inactive);
                                    Utils.displayAlert(getString(R.string.custmsg), LoginActivity.this);
                                }
                            } else {
                                Utils.displayAlert(login.getError().getErrorDescription(), LoginActivity.this);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(LoginActivity.this, getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), LoginActivity.this);
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), LoginActivity.this);
                    }
                }
            }
        });
    }

    private void initObserverResensEmaiOtp() {
        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                dialog.dismiss();
                popupPwdExpiry.dismiss();
                if (emailResponse != null) {
                    Intent i = new Intent(LoginActivity.this, EmailOtpActivity.class);
                    i.putExtra("From", "passwordExpiredPopup");
                    i.putExtra("email", etEmail.getText().toString().toLowerCase());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etEmail.getText().toString().trim().equals("")) {
                Utils.displayAlert("Please enter Email", LoginActivity.this);
                etEmail.requestFocus();
                return value = false;
            } else if (etEmail.getText().toString().trim().startsWith(" ")) {
                Utils.displayAlert("Please enter valid Email", LoginActivity.this);
                etEmail.requestFocus();
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                Utils.displayAlert("Please enter valid Email", LoginActivity.this);
                etEmail.requestFocus();
                return value = false;
            } else if (etPassword.getText().toString().equals("")) {
                Utils.displayAlert("Please enter Password", LoginActivity.this);
                etPassword.requestFocus();
                return value = false;
            } else if (etPassword.getText().toString().startsWith(" ")) {
                Utils.displayAlert("Please enter valid Password", LoginActivity.this);
                etPassword.requestFocus();
                return value = false;
            } else if (etPassword.getText().toString().trim().equals("")) {
                Utils.displayAlert("Please enter valid Password", LoginActivity.this);
                etPassword.requestFocus();
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showPwdExpiredPopup() {
        try {
            CardView cvContinue;
            popupPwdExpiry = new Dialog(LoginActivity.this, R.style.DialogTheme);
            popupPwdExpiry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupPwdExpiry.setContentView(R.layout.password_expiry_popup);
            Window window = popupPwdExpiry.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupPwdExpiry.getWindow().setAttributes(lp);
            popupPwdExpiry.show();

            cvContinue = (CardView) popupPwdExpiry.findViewById(R.id.cvContinue);

            cvContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(LoginActivity.this, v);
                        dialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        loginViewModel.emailotpresend(etEmail.getText().toString().toLowerCase());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    initObserverResensEmaiOtp();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetDB() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
            dsUserDetails.moveToFirst();
            if (dsUserDetails.getCount() > 0 && Utils.checkAuthentication(LoginActivity.this)) {
//                strOldEmail = dsUserDetails.getString(1);
//                strOldPwd = dsUserDetails.getString(2);
                strEmail = dsUserDetails.getString(1);
                strPwd = dsUserDetails.getString(2);
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
            }
        }
    }

    private void SetLock() {
        try {
            isFaceLock = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                    if (isThumb) {
                        imgFace.setImageResource(R.drawable.ic_thumb_active);
                    } else {
                        imgFace.setImageResource(R.drawable.ic_face);
                    }
                } else {
                    isFaceLock = false;
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            }
        }
    }

    private void login() {
        try {
            dialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(strEmail);
            loginRequest.setPassword(strPwd);
            loginViewModel.login(loginRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveCredentials() {
        try {
            if (strEmail.equals("") && strPwd.equals("")) {
                strEmail = etEmail.getText().toString().trim().toLowerCase();
                strPwd = etPassword.getText().toString().trim();
            }
            mydatabase.execSQL("Delete from tblUserDetails");
            mydatabase.execSQL("INSERT INTO tblUserDetails(id,username,password) VALUES(null,'" + strEmail.toLowerCase() + "','" + strPwd + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveIsLock(String value) {
        try {
            mydatabase.execSQL("Delete from tblFacePinLock");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getContacts() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                new FetchContacts(LoginActivity.this).execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<Contacts> getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        List<Contacts> listContacts = new ArrayList<>();
        Contacts objContact;
        try {
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    objContact = new Contacts();
                    List<String> lstNumbers = new ArrayList<>();
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    objContact.setId(id);
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    objContact.setName(name);
                    nameList.add(name);
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!lstNumbers.contains(phoneNo.replace(" ", ""))) {
                                lstNumbers.add(phoneNo.replace(" ", ""));
                            }
                        }
                        objContact.setNumber(lstNumbers);
                        listContacts.add(objContact);
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listContacts;
    }

    public class FetchContacts extends AsyncTask<Void, Void, List> {

        public FetchContacts(Context context) {
        }

        @Override
        protected List doInBackground(Void... params) {
            return getAllContacts();
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            objMyApplication.setListContacts(list);
        }
    }

    private boolean isBiometricCompatibleDevice() {
        switch (getBiometricManager().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                isThumb = true;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                isThumb = false;
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                isThumb = false;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                isThumb = false;
                break;
        }
        return true;

    }

    private BiometricManager getBiometricManager() {
        return BiometricManager.from(this);
    }

    private Boolean isFingerPrint() {
        Boolean value = false;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                if (!fingerprintManager.isHardwareDetected()) {
                    // Device doesn't support fingerprint authentication
                    Log.e("MY_APP_TAG", "Device doesn't support fingerprint authentication.");
                    isThumb = false;
                    value = false;
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // User hasn't enrolled any fingerprints to authenticate with
                    Log.e("MY_APP_TAG", "User hasn't enrolled any fingerprints to authenticate with.");
                    isThumb = false;
                    value = false;
                } else {
                    // Everything is ready for fingerprint authentication
                    Log.e("MY_APP_TAG", "User hasn't enrolled any fingerprints to authenticate with.");
                    isThumb = true;
                    value = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void enableIcon() {
        try {
            if (Utils.checkAuthentication(LoginActivity.this)) {
                etlPassword.setPasswordVisibilityToggleEnabled(false);
                layoutFace.setVisibility(View.VISIBLE);
            } else {
                etlPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                layoutFace.setVisibility(View.GONE);
            }
            if (isFingerPrint()) {
                imgFace.setImageResource(R.drawable.ic_thumb_inactive);
                strMsg = "Do you want to register with Thumb/Pin.";
            } else {
                strMsg = "Do you want to register with FaceID/Pin.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean compareCredentials() {
        Boolean value = true;
        try {
            if (!strEmail.equals("") && !strPwd.equals("")) {
                if (!etEmail.getText().toString().trim().toLowerCase().equals("") && !etEmail.getText().toString().trim().toLowerCase().equals(strEmail.trim().toLowerCase())) {
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

}
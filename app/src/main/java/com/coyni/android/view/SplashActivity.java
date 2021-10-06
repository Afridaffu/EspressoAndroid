package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coyni.android.R;
import com.coyni.android.model.APIError;
import com.coyni.android.model.login.Login;
import com.coyni.android.model.login.LoginRequest;
import com.coyni.android.model.register.EmailResendResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.Executor;

import androidx.biometric.BiometricPrompt;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences settings;
    String MyPREFERENCES = "MyPrefs";
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails, dsFacePin;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    String strEmail = "", strPwd = "";
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    MyApplication objMyApplication;
    Dialog popupPwdExpiry;
    Boolean isFaceLock = false;
    LinearLayout layoutGetStart, layoutSignIn, layoutSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            initialization();
            initObserver();
            SetLock();
            SetDB();
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
                    if (Utils.checkInternet(SplashActivity.this)) {
                        if (!strEmail.equals("") && !strPwd.equals("")) {
                            if (dsUserDetails.getCount() > 0) {
                                strEmail = dsUserDetails.getString(1);
                                strPwd = dsUserDetails.getString(2);
                            }
                            login();
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.internet), SplashActivity.this);
                    }
                }
            });
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (objMyApplication.getLoginBack()) {
            layoutGetStart.setVisibility(View.VISIBLE);
        }
    }

    private void initialization() {
        try {
            CardView cvSignIn, cvGetStarted;
            layoutGetStart = (LinearLayout) findViewById(R.id.layoutGetStart);
            layoutSignIn = (LinearLayout) findViewById(R.id.layoutSignIn);
            layoutSignUp = (LinearLayout) findViewById(R.id.layoutSignUp);
            cvGetStarted = (CardView) findViewById(R.id.cvGetStarted);
            cvSignIn = (CardView) findViewById(R.id.cvSignIn);
            TextView tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            settings = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            objMyApplication = (MyApplication) getApplicationContext();
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
//            if (!settings.getBoolean("isLogin", false)) {
//                layoutGetStart.setVisibility(View.VISIBLE);
//                layoutSignIn.setVisibility(View.GONE);
//            } else {
//                layoutGetStart.setVisibility(View.GONE);
//                layoutSignIn.setVisibility(View.VISIBLE);
//            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            cvGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SplashActivity.this, CreateAccountActivity.class);
                    startActivity(i);
                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (isFaceLock && Utils.checkAuthentication(SplashActivity.this)) {
                            Utils.checkAuthentication(SplashActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });

            layoutSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SplashActivity.this, CreateAccountActivity.class);
                    startActivity(i);
                }
            });


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
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } else {
                            if (login.getData() != null) {
                                if (login.getData().getPasswordFailedAttempts() > 0) {
//                                Utils.displayAlert(login.getData().getMessage(), SplashActivity.this);
                                    Context context = new ContextThemeWrapper(SplashActivity.this, R.style.Theme_QuickCard);
                                    new MaterialAlertDialogBuilder(context)
                                            .setTitle(R.string.app_name)
                                            .setMessage(getString(R.string.custmsg))
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                dialog.dismiss();
                                                mydatabase.execSQL("Delete from tblFacePinLock");
                                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                            }).show();
                                }
                            } else {
                                Utils.displayAlert(login.getError().getErrorDescription(), SplashActivity.this);
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
                            objMyApplication.displayAlert(SplashActivity.this, getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), SplashActivity.this);
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), SplashActivity.this);
                    }
                }
            }
        });

        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                dialog.dismiss();
                popupPwdExpiry.dismiss();
                if (emailResponse != null) {
                    Intent i = new Intent(SplashActivity.this, EmailOtpActivity.class);
                    i.putExtra("From", "passwordExpiredPopup");
                    i.putExtra("email", strEmail);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });
    }

    private void SetDB() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
            dsUserDetails.moveToFirst();
            if (dsUserDetails.getCount() > 0 && Utils.checkAuthentication(SplashActivity.this)) {
                strEmail = dsUserDetails.getString(1);
                strPwd = dsUserDetails.getString(2);

                if (isFaceLock && Utils.checkAuthentication(SplashActivity.this) && !objMyApplication.getLogout() && getIntent().getStringExtra("logout") == null) {
                    layoutGetStart.setVisibility(View.GONE);
                    Utils.checkAuthentication(SplashActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                }
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
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
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
            dialog = new ProgressDialog(SplashActivity.this, R.style.MyAlertDialogStyle);
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

    private void showPwdExpiredPopup() {
        try {
            CardView cvContinue;
            popupPwdExpiry = new Dialog(SplashActivity.this, R.style.DialogTheme);
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
                        Utils.hideKeypad(SplashActivity.this, v);
                        dialog = new ProgressDialog(SplashActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        loginViewModel.emailotpresend(strEmail);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
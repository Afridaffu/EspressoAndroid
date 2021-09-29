package com.coyni.android.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.model.register.SetPassword;
import com.coyni.android.model.register.SetPasswordResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class CreatePasswordActivity extends AppCompatActivity {
    LoginViewModel loginViewModel;
    MyApplication objMyApplication;
    CardView cvProceed, cvPwdCriteria;
    TextInputEditText etPassword, etCPassword;
    String strCode = "", strNewPwd = "";
    ProgressDialog dialog;
    LinearLayout layoutEntry, llPasswordCheck;
    RelativeLayout layoutSuccess;
    ImageView imgUpper, imgNumber, imgLower, imgSpecial, imgNoOfCharacters;
    TextView tvUpper, tvNumber, tvLower, tvSpecial, tvNoOfCharacters, tvProceed, tvHead, tvHeading;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=."
                    + "*[A-Z])(?=.*\\d)"
                    + "(?=.*[-+_!@#$%^&*., ?]).+$");

    private static final Pattern UPPERCASE =
            Pattern.compile("^(?=.*[A-Z]).+$");
    private static final Pattern LOWERCASE =
            Pattern.compile("^(?=.*[a-z]).+$");
    private static final Pattern NUMBER =
            Pattern.compile("^(?=.*\\d).+$");
    private static final Pattern SPECIAL =
            Pattern.compile("^(?=.*[-+_!@#$%^&*., ?]).+$");
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_password);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        try {
            layoutSuccess.setVisibility(View.GONE);
            layoutEntry.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            objMyApplication = (MyApplication) getApplicationContext();
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            cvProceed = (CardView) findViewById(R.id.cvProceed);
            tvProceed = (TextView) findViewById(R.id.tvProceed);
            tvHead = (TextView) findViewById(R.id.tvHead);
            tvHeading = (TextView) findViewById(R.id.tvHeading);
            cvPwdCriteria = (CardView) findViewById(R.id.cvPwdCriteria);
            etPassword = (TextInputEditText) findViewById(R.id.etPassword);
            etCPassword = (TextInputEditText) findViewById(R.id.etCPassword);
            imgUpper = (ImageView) findViewById(R.id.imgUpper);
            imgNumber = (ImageView) findViewById(R.id.imgNumber);
            imgLower = (ImageView) findViewById(R.id.imgLower);
            imgSpecial = (ImageView) findViewById(R.id.imgSpecial);
            imgNoOfCharacters = (ImageView) findViewById(R.id.imgNoOfCharacters);
            tvUpper = (TextView) findViewById(R.id.tvUpper);
            tvNumber = (TextView) findViewById(R.id.tvNumber);
            tvLower = (TextView) findViewById(R.id.tvLower);
            tvSpecial = (TextView) findViewById(R.id.tvSpecial);
            tvNoOfCharacters = (TextView) findViewById(R.id.tvNoOfCharacters);
            layoutEntry = (LinearLayout) findViewById(R.id.layoutEntry);
            llPasswordCheck = (LinearLayout) findViewById(R.id.llPasswordCheck);
            layoutSuccess = (RelativeLayout) findViewById(R.id.layoutSuccess);
            Utils.statusBar(CreatePasswordActivity.this);
            if (getIntent().getStringExtra("code") != null && !getIntent().getStringExtra("code").equals("")) {
                strCode = getIntent().getStringExtra("code");
            }
            if (getIntent().getStringExtra("From").equals("forgotPwd") || getIntent().getStringExtra("From").equals("passwordExpiredPopup")) {
                tvProceed.setText("Reset Password");
                tvHead.setVisibility(View.GONE);
                tvHeading.setText("Create new password");
            }
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        cvPwdCriteria.setVisibility(View.VISIBLE);
                        passwordCreteria(s.toString());
                    } else {
                        cvPwdCriteria.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            cvProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validation()) {
                        Utils.hideKeypad(CreatePasswordActivity.this, v);
                        dialog = new ProgressDialog(CreatePasswordActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        strNewPwd = etPassword.getText().toString().trim();
                        SetPassword setPassword = new SetPassword();
                        setPassword.setCode(strCode);
                        setPassword.setPassword(etPassword.getText().toString().trim());
                        loginViewModel.setPassword(setPassword);
                    }
                }
            });
            SetDB();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getSetpwdLiveData().observe(this, new Observer<SetPasswordResponse>() {
            @Override
            public void onChanged(SetPasswordResponse login) {
                dialog.dismiss();
                if (login != null) {
                    if (login.getStatus().toLowerCase().equals("success")) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setHomeButtonEnabled(false);
                        layoutSuccess.setVisibility(View.VISIBLE);
                        layoutEntry.setVisibility(View.GONE);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String strEmail = "";
                                    dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
                                    dsUserDetails.moveToFirst();
                                    if (dsUserDetails.getCount() > 0) {
                                        strEmail = dsUserDetails.getString(1);
                                        mydatabase.execSQL("Delete from tblUserDetails");
                                        mydatabase.execSQL("INSERT INTO tblUserDetails(id,username,password) VALUES(null,'" + strEmail + "','" + strNewPwd + "')");
                                    }
                                } catch (Exception ex) {
                                    if (ex.getMessage().toString().contains("no such table")) {
                                        mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                                        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
                                    }
                                }
                                Intent i = new Intent(CreatePasswordActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        }, 1000);
                    }
                }
            }
        });
        loginViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Utils.displayAlert(s, CreatePasswordActivity.this);
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etPassword.getText().toString().equals("")) {
                Utils.displayAlert("Please enter Password", CreatePasswordActivity.this);
                return value = false;
            } else if (!PASSWORD_PATTERN.matcher(etPassword.getText().toString()).matches()) {
                Utils.displayAlert("Password must match following requirements", CreatePasswordActivity.this);
                return value = false;
            } else if (etPassword.getText().toString().indexOf(' ') != -1) {
                Utils.displayAlert("Spaces not allowed in Password", CreatePasswordActivity.this);
                return value = false;
            } else if (etPassword.getText().toString().length() < 8) {
                Utils.displayAlert("Please enter valid Password", CreatePasswordActivity.this);
                return value = false;
            } else if (etCPassword.getText().toString().equals("")) {
                Utils.displayAlert("Please enter Confirm Password", CreatePasswordActivity.this);
                return value = false;
            } else if (etCPassword.getText().toString().indexOf(' ') != -1) {
                Utils.displayAlert("Spaces not allowed in Confirm Password", CreatePasswordActivity.this);
                return value = false;
            } else if (!etPassword.getText().toString().equals(etCPassword.getText().toString())) {
                llPasswordCheck.setVisibility(View.VISIBLE);
                return value = false;
            }
            llPasswordCheck.setVisibility(View.GONE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void passwordCreteria(String password) {
        try {
            Boolean isUpper = false, isLower = false, isNum = false, isSpec = false, isNumCharacters = false;
            if (UPPERCASE.matcher(password).matches()) {
                imgUpper.setVisibility(View.VISIBLE);
                imgUpper.setImageResource(R.drawable.ic_tick);
                tvUpper.setTextColor(Color.GREEN);
                isUpper = true;
            } else {
                imgUpper.setVisibility(View.VISIBLE);
                imgUpper.setImageResource(R.drawable.ic_close);
                tvUpper.setTextColor(Color.RED);
                isUpper = false;
            }
            if (LOWERCASE.matcher(password).matches()) {
                imgLower.setVisibility(View.VISIBLE);
                imgLower.setImageResource(R.drawable.ic_tick);
                tvLower.setTextColor(Color.GREEN);
                isLower = true;
            } else {
                imgLower.setVisibility(View.VISIBLE);
                imgLower.setImageResource(R.drawable.ic_close);
                tvLower.setTextColor(Color.RED);
                isLower = false;
            }
            if (NUMBER.matcher(password).matches()) {
                imgNumber.setVisibility(View.VISIBLE);
                imgNumber.setImageResource(R.drawable.ic_tick);
                tvNumber.setTextColor(Color.GREEN);
                isNum = true;
            } else {
                imgNumber.setVisibility(View.VISIBLE);
                imgNumber.setImageResource(R.drawable.ic_close);
                tvNumber.setTextColor(Color.RED);
                isNum = false;
            }
            if (SPECIAL.matcher(password).matches()) {
                imgSpecial.setVisibility(View.VISIBLE);
                imgSpecial.setImageResource(R.drawable.ic_tick);
                tvSpecial.setTextColor(Color.GREEN);
                isSpec = true;
            } else {
                imgSpecial.setVisibility(View.VISIBLE);
                imgSpecial.setImageResource(R.drawable.ic_close);
                tvSpecial.setTextColor(Color.RED);
                isSpec = false;
            }
            if (password.length() >= 8 && password.length() <= 20) {
                imgNoOfCharacters.setVisibility(View.VISIBLE);
                imgNoOfCharacters.setImageResource(R.drawable.ic_tick);
                tvNoOfCharacters.setTextColor(Color.GREEN);
                isNumCharacters = true;
            } else {
                imgNoOfCharacters.setVisibility(View.VISIBLE);
                imgNoOfCharacters.setImageResource(R.drawable.ic_close);
                tvNoOfCharacters.setTextColor(Color.RED);
                isNumCharacters = false;
            }

            if (isUpper && isLower && isNum && isSpec && isNumCharacters) {
                cvPwdCriteria.setVisibility(View.GONE);
            } else {
                cvPwdCriteria.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, fragment, tag).addToBackStack(tag);
            ;
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetDB() {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
            dsUserDetails.moveToFirst();
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
            }
        }
    }

}
package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.model.register.EmailResendResponse;
import com.coyni.android.model.register.EmailResponse;
import com.coyni.android.model.register.SmsRequest;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EmailOtpActivity extends AppCompatActivity {
    CardView cvVerify;
    TextView tvVerify, tvMessage, tvInvalid, tvtimer, tvResend, tvFirstChar, tvLastChar;
    DilatingDotsProgressBar progressBar;
    LinearLayout layoutEntry;
    RelativeLayout layoutSuccess;
    String strMail = "", strcode = "";
    MyApplication objMyApplication;
    LoginViewModel loginViewModel;
    ProgressDialog dialog;
    CountDownTimer waitTimer;
    EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private EditText[] editTexts;
    View viewResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_email_otp);
            initialization();
            initObserver();
            otptimer();
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
            etOtp1 = (EditText) findViewById(R.id.etOtp1);
            etOtp2 = (EditText) findViewById(R.id.etOtp2);
            etOtp3 = (EditText) findViewById(R.id.etOtp3);
            etOtp4 = (EditText) findViewById(R.id.etOtp4);
            etOtp5 = (EditText) findViewById(R.id.etOtp5);
            etOtp6 = (EditText) findViewById(R.id.etOtp6);
            editTexts = new EditText[]{etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6};

            cvVerify = (CardView) findViewById(R.id.cvVerify);
            tvInvalid = (TextView) findViewById(R.id.tvInvalid);
            tvVerify = (TextView) findViewById(R.id.tvVerify);
            tvMessage = (TextView) findViewById(R.id.tvMessage2);
            tvtimer = (TextView) findViewById(R.id.tvtimer);
            tvResend = (TextView) findViewById(R.id.tvResend);
            tvFirstChar = (TextView) findViewById(R.id.tvFirstChar);
            tvLastChar = (TextView) findViewById(R.id.tvLastChar);
            viewResend = (View) findViewById(R.id.viewResend);
            layoutEntry = (LinearLayout) findViewById(R.id.layoutEntry);
            layoutSuccess = (RelativeLayout) findViewById(R.id.layoutSuccess);
            progressBar = (DilatingDotsProgressBar) findViewById(R.id.progress);
            cvVerify.setEnabled(false);
            Utils.statusBar(EmailOtpActivity.this);
            if (getIntent().getStringExtra("From").equals("forgotPwd") || getIntent().getStringExtra("From").equals("passwordExpiredPopup")) {
                strMail = getIntent().getStringExtra("email").trim();
            } else {
                strMail = objMyApplication.getStrEmail().trim();
            }

            String text = strMail.split("@")[1];
            char first = strMail.charAt(0);
            String strLast = strMail.split("@")[0];
            char last = strLast.charAt(strLast.length() - 1);
            tvFirstChar.setText("" + first);
            tvLastChar.setText("" + last);
            tvMessage.setText("@" + text);

            etOtp1.addTextChangedListener(new EmailOtpActivity.PinTextWatcher(0));
            etOtp2.addTextChangedListener(new EmailOtpActivity.PinTextWatcher(1));
            etOtp3.addTextChangedListener(new EmailOtpActivity.PinTextWatcher(2));
            etOtp4.addTextChangedListener(new EmailOtpActivity.PinTextWatcher(3));
            etOtp5.addTextChangedListener(new EmailOtpActivity.PinTextWatcher(4));
            etOtp6.addTextChangedListener(new EmailOtpActivity.PinTextWatcher(5));

            etOtp1.setOnKeyListener(new EmailOtpActivity.PinOnKeyListener(0));
            etOtp2.setOnKeyListener(new EmailOtpActivity.PinOnKeyListener(1));
            etOtp3.setOnKeyListener(new EmailOtpActivity.PinOnKeyListener(2));
            etOtp4.setOnKeyListener(new EmailOtpActivity.PinOnKeyListener(3));
            etOtp5.setOnKeyListener(new EmailOtpActivity.PinOnKeyListener(4));
            etOtp6.setOnKeyListener(new EmailOtpActivity.PinOnKeyListener(5));
            cvVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(EmailOtpActivity.this, v);
                        progressBar.show();
                        tvVerify.setVisibility(View.GONE);
                        tvInvalid.setVisibility(View.GONE);
                        SmsRequest smsRequest = new SmsRequest();
                        smsRequest.setEmail(strMail);
                        smsRequest.setOtp(etOtp1.getText().toString() + etOtp2.getText().toString() + etOtp3.getText().toString() + etOtp4.getText().toString() +
                                etOtp5.getText().toString() + etOtp6.getText().toString());
                        loginViewModel.emailotp(smsRequest);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            tvResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvResend.setVisibility(View.VISIBLE);
                    viewResend.setVisibility(View.VISIBLE);
                    try {
                        if (waitTimer != null) {
                            waitTimer.cancel();
                            waitTimer = null;
                        }
                        otptimer();
                        Utils.hideKeypad(EmailOtpActivity.this, v);
                        dialog = new ProgressDialog(EmailOtpActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        loginViewModel.emailotpresend(strMail);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getEmailotpLiveData().observe(this, new Observer<EmailResponse>() {
            @Override
            public void onChanged(EmailResponse login) {
                progressBar.hide();
                tvVerify.setVisibility(View.VISIBLE);
                if (login != null) {
                    if (login.getStatus().toLowerCase().equals("error")) {
                        etOtp1.setText("");
                        etOtp2.setText("");
                        etOtp3.setText("");
                        etOtp4.setText("");
                        etOtp5.setText("");
                        etOtp6.setText("");
                        etOtp1.requestFocus();
                        etOtp1.getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.deleteback), PorterDuff.Mode.SRC_ATOP);
                        etOtp2.getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.deleteback), PorterDuff.Mode.SRC_ATOP);
                        etOtp3.getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.deleteback), PorterDuff.Mode.SRC_ATOP);
                        etOtp4.getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.deleteback), PorterDuff.Mode.SRC_ATOP);
                        etOtp5.getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.deleteback), PorterDuff.Mode.SRC_ATOP);
                        etOtp6.getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.deleteback), PorterDuff.Mode.SRC_ATOP);
                        tvInvalid.setVisibility(View.VISIBLE);
                    } else {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setHomeButtonEnabled(false);
                        strcode = login.getData().getCode();
                        layoutSuccess.setVisibility(View.VISIBLE);
                        layoutEntry.setVisibility(View.GONE);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Intent i = new Intent(EmailOtpActivity.this, CreatePasswordActivity.class);
                                    i.putExtra("code", strcode);
                                    i.putExtra("From", getIntent().getStringExtra("From"));
                                    startActivity(i);
                                    etOtp1.setText("");
                                    etOtp2.setText("");
                                    etOtp3.setText("");
                                    etOtp4.setText("");
                                    etOtp5.setText("");
                                    etOtp6.setText("");
                                    etOtp1.requestFocus();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, 1000);
                    }
                }
            }
        });
        loginViewModel.getEmailresendMutableLiveData().observe(this, new Observer<EmailResendResponse>() {
            @Override
            public void onChanged(EmailResendResponse emailResponse) {
                dialog.dismiss();
                if (emailResponse != null) {
//                    resendFailedAttempts = emailResponse.getData().getEmailOtpAttempts();
                    if (emailResponse.getStatus().toLowerCase().toString().equals("success")) {
                        Utils.displayAlert(emailResponse.getData().getMessage().toString(), EmailOtpActivity.this);
                    } else {
                        Utils.displayAlert(emailResponse.getError().getErrorDescription().toString(), EmailOtpActivity.this);
                    }
                }
            }
        });
    }

    private void otptimer() {
        try {
            waitTimer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    tvtimer.setText(f.format(min) + ":" + f.format(sec));
                }

                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    tvtimer.setText("00:00");
                }
            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);
            editTexts[currentIndex].getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.status), PorterDuff.Mode.SRC_ATOP);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast) {
                editTexts[currentIndex + 1].requestFocus();
            } else {
                editTexts[currentIndex].requestFocus();
            }

            if (isAllEditTextsFilled()) { // isLast is optional

                cvVerify.setCardBackgroundColor(getResources().getColor(R.color.btnback));
                tvVerify.setTextColor(getResources().getColor(R.color.headcolor));
                cvVerify.setEnabled(true);
//                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            } else {
                tvInvalid.setVisibility(View.GONE);
                cvVerify.setCardBackgroundColor(Color.parseColor("#F2F2F2"));
                tvVerify.setTextColor(Color.parseColor("#D5D5D5"));
                cvVerify.setEnabled(false);
            }
        }

        private void moveToPrevious() {
            if (!isFirst) {
                editTexts[currentIndex - 1].requestFocus();
                editTexts[currentIndex].getBackground().setColorFilter(ContextCompat.getColor(EmailOtpActivity.this, R.color.unselectmenu), PorterDuff.Mode.SRC_ATOP);
            }
            if (isAllEditTextsFilled()) { // isLast is optional

                cvVerify.setCardBackgroundColor(getResources().getColor(R.color.btnback));
                tvVerify.setTextColor(getResources().getColor(R.color.headcolor));
                cvVerify.setEnabled(true);
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            } else {
                tvInvalid.setVisibility(View.GONE);
                cvVerify.setCardBackgroundColor(Color.parseColor("#F2F2F2"));
                tvVerify.setTextColor(Color.parseColor("#D5D5D5"));
                cvVerify.setEnabled(false);
            }
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        /*@Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }*/

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }
    }
}
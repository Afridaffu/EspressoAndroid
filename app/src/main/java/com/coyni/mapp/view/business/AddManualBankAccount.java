package com.coyni.mapp.view.business;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.dialogs.ManualAccountNumbersFullPage;
import com.coyni.mapp.model.bank.ManualBankRequest;
import com.coyni.mapp.model.bank.ManualBankResponse;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.view.PaymentMethodsActivity;
import com.coyni.mapp.view.WebViewActivity;
import com.coyni.mapp.viewmodel.PaymentMethodsViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddManualBankAccount extends BaseActivity {
    private TextView descriptionTV, nameOnBankErrorTV, routingNumberErrorTV, confirmRoutingNumberErrorTV, checkAccNumberErrorTV, confirmAccNumberErrorTV, headingTV;
    private TextInputLayout nameOnBankTIL, routingNumberTIL, confirmRoutingNumberTIL, checkAccNumberTIL, confirmAccNumberTIL;
    private TextInputEditText nameOnBankET, routingNumberET, confirmRoutingNumberET, checkAccNumberET, confirmAccNumberET;
    private LinearLayout nameOnBankErrorLL, routingNumberErrorLL, confirmRoutingNumberErrorLL, checkAccNumberErrorLL, confirmAccNumberErrorLL;
    private ImageView endIconIV, endIcon2IV, backLL;
    public boolean isName = false, isRoutNum = false, isConfRoutNum = false, isAcNum = false, isConfirmAc = false, isAddEnabled = false;
    private Long mLastClickTime = 0L;
    private MyApplication objMyApplication;
    private CardView addCV;
    PaymentMethodsViewModel paymentMethodsViewModel;
    String strScreen = "", strName = "";
    RelativeLayout lyAddBank, layoutLoader;
    Dialog bankStatusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_manual_bank_account);
            initfields();
            initObserver();
            textWatchers();
            focusWatchers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initfields() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            setSpannableText();
            lyAddBank = findViewById(R.id.lyAddBank);
            layoutLoader = findViewById(R.id.layoutLoader);
            backLL = findViewById(R.id.backLL);
            nameOnBankErrorTV = findViewById(R.id.nameOnBankErrorTV);
            nameOnBankTIL = findViewById(R.id.nameOnBankTIL);
            nameOnBankET = findViewById(R.id.nameOnBankET);
            nameOnBankErrorLL = findViewById(R.id.nameOnBankErrorLL);
            routingNumberTIL = findViewById(R.id.routingNumberTIL);
            routingNumberET = findViewById(R.id.routingNumberET);
            routingNumberErrorLL = findViewById(R.id.routingNumberErrorLL);
            routingNumberErrorTV = findViewById(R.id.routingNumberErrorTV);
            confirmRoutingNumberTIL = findViewById(R.id.confirmRoutingNumberTIL);
            confirmRoutingNumberET = findViewById(R.id.confirmRoutingNumberET);
            confirmRoutingNumberErrorLL = findViewById(R.id.confirmRoutingNumberErrorLL);
            confirmRoutingNumberErrorTV = findViewById(R.id.confirmRoutingNumberErrorTV);
            checkAccNumberTIL = findViewById(R.id.checkAccNumberTIL);
            checkAccNumberET = findViewById(R.id.checkAccNumberET);
            checkAccNumberErrorLL = findViewById(R.id.checkAccNumberErrorLL);
            checkAccNumberErrorTV = findViewById(R.id.checkAccNumberErrorTV);
            confirmAccNumberTIL = findViewById(R.id.confirmAccNumberTIL);
            confirmAccNumberET = findViewById(R.id.confirmAccNumberET);
            confirmAccNumberErrorLL = findViewById(R.id.confirmAccNumberErrorLL);
            confirmAccNumberErrorTV = findViewById(R.id.confirmAccNumberErrorTV);
            addCV = findViewById(R.id.addCV);
            endIconIV = findViewById(R.id.endIcon1IV);
            endIcon2IV = findViewById(R.id.endIcon2IV);
            headingTV = findViewById(R.id.headingTV);

            nameOnBankTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));


            if (getIntent().getStringExtra("From").equalsIgnoreCase("pay")) {
                headingTV.setText(R.string.add_bank_account);
                strScreen = getIntent().getStringExtra("From");
            } else if (getIntent().getStringExtra("FROM").equalsIgnoreCase("Resubmit") ||
                    getIntent().getStringExtra("FROM").equalsIgnoreCase("Edit")) {
                headingTV.setText(R.string.resubmit);
                strScreen = "";
            }
//            else if (getIntent().getStringExtra("FROM").equalsIgnoreCase("Edit")) {
//                headingTV.setText(R.string.resubmit);
//                strScreen = "";
//            }

            backLL.setOnClickListener(view -> onBackPressed());

            addCV.setOnClickListener(view -> {
                try {
                    if (isAddEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(AddManualBankAccount.this);
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (strScreen.equals("pay")) {
                            lyAddBank.setVisibility(View.GONE);
                            layoutLoader.setVisibility(View.VISIBLE);
                        }
                        ManualBankRequest request = new ManualBankRequest();
                        request.setAccountName(nameOnBankET.getText().toString());
                        request.setRoutingNumber(routingNumberET.getText().toString());
                        request.setAccountNumber(checkAccNumberET.getText().toString());
                        request.setGiactReq(true);
                        paymentMethodsViewModel.saveManualBank(request);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        paymentMethodsViewModel.getManualBankResponseMutableLiveData().observe(this, new Observer<ManualBankResponse>() {
            @Override
            public void onChanged(ManualBankResponse manualBankResponse) {
                if (manualBankResponse != null) {
                    if (manualBankResponse.getStatus().toLowerCase().equals("success")) {
                        if (strScreen.equals("pay")) {
                            layoutLoader.setVisibility(View.GONE);
                            showSuccessFailure(manualBankResponse);
                        }
                    }
                }
            }
        });
    }

    private void textWatchers() {
        try {
            nameOnBankET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isName = true;
                        nameOnBankErrorLL.setVisibility(View.GONE);
                    } else {
                        isName = false;
                    }
                    if (nameOnBankET.getText().toString().contains("  ")) {
                        nameOnBankET.setText(nameOnBankET.getText().toString().replace("  ", " "));
                        nameOnBankET.setSelection(nameOnBankET.getText().length());
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() > 0 && s.toString().trim().length() == 0) {
                        nameOnBankET.setText("");
                        nameOnBankET.setSelection(nameOnBankET.getText().length());
                    } else if (s.length() > 0 && String.valueOf(s.charAt(0)).equals(" ")) {
                        nameOnBankET.setText(s.toString().trim());
                    } else if (s.length() > 0 && s.toString().contains(".")) {
                        nameOnBankET.setText(nameOnBankET.getText().toString().replaceAll("\\.", ""));
                        nameOnBankET.setSelection(nameOnBankET.getText().length());
                    } else if (s.toString().length() > 0 && s.toString().contains("http") || s.toString().length() > 0 && s.toString().contains("https")) {
                        nameOnBankET.setText("");
                        nameOnBankET.setSelection(nameOnBankET.getText().length());
                    }
                }
            });

            routingNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSeq, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (confirmRoutingNumberET.getText().toString().length() > 0 &&
                                !routingNumberET.getText().toString().equalsIgnoreCase(confirmRoutingNumberET.getText().toString())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.error_red));
                            confirmRoutingNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmRoutingNumberErrorTV.setText("Routing number does not match");
                            endIconIV.setVisibility(View.GONE);
                        } else if (confirmRoutingNumberET.getText().toString().length() > 0 && routingNumberET.getText().toString().length() < 9
                                && routingNumberET.getText().toString().trim().equalsIgnoreCase(confirmRoutingNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            endIconIV.setVisibility(View.GONE);
                            confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                        } else if (routingNumberET.getText().toString().length() == 9 && confirmRoutingNumberET.getText().toString().length() == 9 &&
                                routingNumberET.getText().toString().equalsIgnoreCase(confirmRoutingNumberET.getText().toString())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                            endIconIV.setVisibility(View.VISIBLE);
                        }
                        if (charSeq.toString().length() == 9) {
                            isRoutNum = true;
                        } else {
                            isRoutNum = false;
                        }
                        enableOrDisableNext();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            confirmRoutingNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSeq, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (routingNumberET.getText().toString().equalsIgnoreCase(confirmRoutingNumberET.getText().toString()) && charSeq.toString().length() == 9) {
                            endIconIV.setVisibility(View.VISIBLE);
                            isConfRoutNum = true;
                        } else {
                            isConfRoutNum = false;
                            endIconIV.setVisibility(View.GONE);
                        }
                        enableOrDisableNext();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            checkAccNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSeq, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (confirmAccNumberET.getText().toString().length() > 0 &&
                                !checkAccNumberET.getText().toString().equalsIgnoreCase(confirmAccNumberET.getText().toString())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
                            confirmAccNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmAccNumberErrorTV.setText("Account number does not match");
                            endIcon2IV.setVisibility(View.GONE);
                        } else if (confirmAccNumberET.getText().toString().length() > 0 && checkAccNumberET.getText().toString().length() < 17
                                && checkAccNumberET.getText().toString().trim().equalsIgnoreCase(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            endIcon2IV.setVisibility(View.GONE);
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                        } else if (checkAccNumberET.getText().toString().length() == 17 && confirmAccNumberET.getText().toString().length() == 17 &&
                                checkAccNumberET.getText().toString().equalsIgnoreCase(confirmAccNumberET.getText().toString())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                            endIcon2IV.setVisibility(View.VISIBLE);
                        }
                        if (charSeq.toString().length() == 17) {
                            isAcNum = true;
                        } else {
                            isAcNum = false;
                        }
                        enableOrDisableNext();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            confirmAccNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSeq, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (checkAccNumberET.getText().toString().trim().equalsIgnoreCase(confirmAccNumberET.getText().toString().trim()) && charSeq.toString().length() == 17) {

                            isConfirmAc = true;
                            endIcon2IV.setVisibility(View.VISIBLE);

                        } else {

                            isConfirmAc = false;
                            endIcon2IV.setVisibility(View.GONE);

                        }
                        enableOrDisableNext();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusWatchers() {
        try {
            nameOnBankET.setOnFocusChangeListener((view, b) -> {
                if (!b) {

                    if (nameOnBankET.getText().length() > 0 && !nameOnBankET.getText().toString().substring(0, 1).equals(" ")) {
                        nameOnBankET.setText(nameOnBankET.getText().toString().substring(0, 1).toUpperCase() + nameOnBankET.getText().toString().substring(1));
                    }
                    nameOnBankET.setHint("");
                    if (nameOnBankET.getText().toString().trim().length() > 1) {
                        nameOnBankErrorLL.setVisibility(View.GONE);
                        nameOnBankTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.primary_black));

                    } else if (nameOnBankET.getText().toString().trim().length() == 1) {
                        nameOnBankTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.error_red));
                        nameOnBankErrorLL.setVisibility(View.VISIBLE);
                        nameOnBankErrorTV.setText("Minimum 2 Characters Required");
                    } else if (nameOnBankET.getText().toString().trim().length() <= 0) {
                        nameOnBankTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.light_gray));
                        nameOnBankErrorLL.setVisibility(View.VISIBLE);
                        nameOnBankErrorTV.setText("Field Required");
                    }
                } else {
                    nameOnBankErrorLL.setVisibility(View.GONE);
                    nameOnBankTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.primary_green));
                    nameOnBankET.setSelection(nameOnBankET.getText().toString().trim().length());
                }
            });

            routingNumberET.setOnFocusChangeListener((view, b) -> {
                if (b) {
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(AddManualBankAccount.this);
                    routingNumberTIL.setHint("Routing Number");
                    routingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
                    routingNumberErrorLL.setVisibility(View.GONE);
                } else {
                    routingNumberET.setHint("");
                    if (routingNumberET.getText().toString() != null && routingNumberET.getText().toString().length() == 9) {
                        routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_black));
                        routingNumberErrorLL.setVisibility(View.GONE);
                    } else {
                        routingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.error_red));
                        routingNumberErrorLL.setVisibility(View.VISIBLE);
                        if (routingNumberET.getText().toString().length() > 0 && routingNumberET.getText().toString().length() < 9) {
                            routingNumberErrorTV.setText("Please enter a valid 9-digit routing number");
                        } else if (routingNumberET.getText().toString().length() == 0) {
                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.light_gray));
                            routingNumberErrorTV.setText("Field Required");
                        }
                    }
                }
            });

            confirmRoutingNumberET.setOnFocusChangeListener((view, b) -> {
                if (b) {
                    confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                    confirmRoutingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_green));
                    confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                } else {
                    if (confirmRoutingNumberET.getText().length() > 0) {
                        if (!confirmRoutingNumberET.getText().toString().trim().equalsIgnoreCase(routingNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.error_red));
                            confirmRoutingNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmRoutingNumberErrorTV.setText("Routing number does not match");
                        }  else if (confirmRoutingNumberET.getText().toString().trim().equalsIgnoreCase(routingNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                        }

                    } else if (confirmRoutingNumberET.getText().length() == 0) {
                        confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.light_gray));
                        confirmRoutingNumberErrorLL.setVisibility(View.VISIBLE);
                        confirmRoutingNumberErrorTV.setText("Field Required");
                    }
                }
            });

            checkAccNumberET.setOnFocusChangeListener((view, b) -> {
                if (b) {
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(AddManualBankAccount.this);
                    checkAccNumberTIL.setHint("Checking Account Number");
                    checkAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                    checkAccNumberErrorLL.setVisibility(View.GONE);
                } else {
                    checkAccNumberET.setHint("");
                    if (checkAccNumberET.getText().toString() != null && checkAccNumberET.getText().toString().length() == 17) {
                        checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_black));
                        checkAccNumberErrorLL.setVisibility(View.GONE);
                    } else {
                        checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.error_red));
                        checkAccNumberErrorLL.setVisibility(View.VISIBLE);
                        if (checkAccNumberET.getText().toString().length() > 0 && checkAccNumberET.getText().toString().length() < 17) {
                            checkAccNumberErrorTV.setText("Please enter a valid account number");
                        } else if (checkAccNumberET.getText().toString().length() == 0) {
                            Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.light_gray));
                            checkAccNumberErrorTV.setText("Field Required");
                        }
                    }
                }
            });

            confirmAccNumberET.setOnFocusChangeListener((view, b) -> {
                if (b) {
                    confirmAccNumberET.setHint("Confirm Account Number");
                    confirmAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_green));
                    confirmAccNumberErrorLL.setVisibility(View.GONE);
                } else {
                    if (confirmAccNumberET.getText().length() > 0) {
                        if (!confirmAccNumberET.getText().toString().trim().equalsIgnoreCase(checkAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
                            confirmAccNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmAccNumberErrorTV.setText("Account number does not match");
                        }  else if (confirmAccNumberET.getText().toString().trim().equalsIgnoreCase(checkAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                        }

                    } else if (confirmAccNumberET.getText().length() == 0) {
                        confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.light_gray));
                        confirmAccNumberErrorLL.setVisibility(View.VISIBLE);
                        confirmAccNumberErrorTV.setText("Field Required");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableOrDisableNext() {
        try {
            if (isName && isRoutNum && isConfRoutNum && isAcNum && isConfirmAc) {
                isAddEnabled = true;
                addCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isAddEnabled = false;
                addCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSpannableText() {
        try {
            descriptionTV = findViewById(R.id.descriptionTV);

            String strMessage = getResources().getString(R.string.please_provide_your_bank_account_information_find_your_account_numbers);
            SpannableString ss = new SpannableString(strMessage);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        ManualAccountNumbersFullPage showImgDialog = new ManualAccountNumbersFullPage(AddManualBankAccount.this);
                        showImgDialog.show();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                }
            };
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#00a6a2")), strMessage.indexOf("Find your account numbers"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new UnderlineSpan(), strMessage.indexOf("Find your account numbers"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(clickableSpan, strMessage.length() - 24, strMessage.length() - 1, 0);
            descriptionTV.setMovementMethod(LinkMovementMethod.getInstance());
            descriptionTV.setHighlightColor(Color.TRANSPARENT);

            descriptionTV.setText(ss);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showSuccessFailure(ManualBankResponse manualBankResponse) {
        try {
            bankStatusDialog = new Dialog(AddManualBankAccount.this);
            bankStatusDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            bankStatusDialog.setContentView(R.layout.manual_bank_verfication_sucess_failed);
            bankStatusDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            ImageView imageIV = bankStatusDialog.findViewById(R.id.imageIV);
            TextView headerTV = bankStatusDialog.findViewById(R.id.headerTV);
            TextView statusTV = bankStatusDialog.findViewById(R.id.statusTV);
            TextView errorDescriptnTV = bankStatusDialog.findViewById(R.id.errorDescriptnTV);
            TextView nameOnBankTV = bankStatusDialog.findViewById(R.id.nameOnBankTV);
            TextView bankNameTV = bankStatusDialog.findViewById(R.id.bankNameTV);
            TextView routingNumTV = bankStatusDialog.findViewById(R.id.routingNumTV);
            TextView accNumTV = bankStatusDialog.findViewById(R.id.accNumTV);
            TextView doneTV = bankStatusDialog.findViewById(R.id.doneTV);
            CardView validateCV = bankStatusDialog.findViewById(R.id.validateCV);

            nameOnBankTV.setText(manualBankResponse.getData().getAccountName());
            routingNumTV.setText(manualBankResponse.getData().getRoutingNumber());
            accNumTV.setText(manualBankResponse.getData().getAccountNumber());
            Window window = bankStatusDialog.getWindow();
            if (!manualBankResponse.getData().getGiactFail()) {
                imageIV.setImageResource(R.drawable.ic_success);
                headerTV.setText(getString(R.string.bank_account_added));
                bankNameTV.setText(manualBankResponse.getData().getBankName());
                doneTV.setText(getString(R.string.done));
                errorDescriptnTV.setVisibility(View.GONE);
                statusTV.setText("Approved");
                statusTV.setTextColor(getColor(R.color.active_green));
                statusTV.setBackgroundResource(R.drawable.bank_status_bg);
            } else {
                strName = manualBankResponse.getData().getAccountName();
                imageIV.setImageResource(R.drawable.ic_failure);
                headerTV.setText(getString(R.string.bank_account_failed));
                bankNameTV.setText("--");
                doneTV.setText(getString(R.string.try_again));
                errorDescriptnTV.setVisibility(View.VISIBLE);
                statusTV.setText("Declined");
                statusTV.setTextColor(getColor(R.color.error));
                statusTV.setBackgroundResource(R.drawable.bank_status_decline_bg);
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            bankStatusDialog.setCanceledOnTouchOutside(false);
            bankStatusDialog.setCancelable(false);
            bankStatusDialog.show();

            validateCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (!manualBankResponse.getData().getGiactFail()) {
                            objMyApplication.setBankSave(true);
                            Intent i = new Intent();
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
//                            bankStatusDialog.dismiss();
//                            lyAddBank.setVisibility(View.VISIBLE);
//                            layoutLoader.setVisibility(View.GONE);
//                            routingNumberET.setText("");
//                            confirmRoutingNumberET.setText("");
//                            checkAccNumberET.setText("");
//                            confirmAccNumberET.setText("");
                            Intent i = new Intent(AddManualBankAccount.this, AddManualBankAccount.class);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(i);
                            overridePendingTransition(0, 0);
                        }

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
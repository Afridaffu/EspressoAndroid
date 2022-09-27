package com.coyni.mapp.view.business;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.bank.ManualBankRequest;
import com.coyni.mapp.model.bank.ManualBankResponse;
import com.coyni.mapp.model.summary.BankAccount;
import com.coyni.mapp.utils.EmojiFilter;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.PaymentMethodsViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddManualBankAccount extends BaseActivity implements OnKeyboardVisibilityListener {
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
    String strScreen = "";
    RelativeLayout lyAddBank, layoutLoader;
    Dialog bankStatusDialog;
    private String convert = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_manual_bank_account);
            initFields();
            initObserver();
            textWatchers();
            focusWatchers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initFields() {
        try {
            setKeyboardVisibilityListener(this);
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            setSpannableText();
            lyAddBank = findViewById(R.id.lyAddBank);
            layoutLoader = findViewById(R.id.layoutLoader);
            backLL = findViewById(R.id.backLL);
            nameOnBankErrorTV = findViewById(R.id.nameOnBankErrorTV);
            nameOnBankTIL = findViewById(R.id.nameOnBankTIL);
            nameOnBankET = findViewById(R.id.nameOnBankET);
            InputFilter[] filters = {new InputFilter.LengthFilter(61), EmojiFilter.getFilter()};
            nameOnBankET.setFilters(filters);

            nameOnBankErrorLL = findViewById(R.id.nameOnBankErrorLL);
            routingNumberTIL = findViewById(R.id.routingNumberTIL);
            routingNumberET = findViewById(R.id.routingNumberET);
            blockCopy(routingNumberET);

            routingNumberErrorLL = findViewById(R.id.routingNumberErrorLL);
            routingNumberErrorTV = findViewById(R.id.routingNumberErrorTV);
            confirmRoutingNumberTIL = findViewById(R.id.confirmRoutingNumberTIL);
            confirmRoutingNumberET = findViewById(R.id.confirmRoutingNumberET);
            blockCopy(confirmRoutingNumberET);

            confirmRoutingNumberErrorLL = findViewById(R.id.confirmRoutingNumberErrorLL);
            confirmRoutingNumberErrorTV = findViewById(R.id.confirmRoutingNumberErrorTV);
            checkAccNumberTIL = findViewById(R.id.checkAccNumberTIL);
            checkAccNumberET = findViewById(R.id.checkAccNumberET);
            blockCopy(checkAccNumberET);

            checkAccNumberErrorLL = findViewById(R.id.checkAccNumberErrorLL);
            checkAccNumberErrorTV = findViewById(R.id.checkAccNumberErrorTV);
            confirmAccNumberTIL = findViewById(R.id.confirmAccNumberTIL);
            confirmAccNumberET = findViewById(R.id.confirmAccNumberET);
            blockCopy(confirmAccNumberET);

            confirmAccNumberErrorLL = findViewById(R.id.confirmAccNumberErrorLL);
            confirmAccNumberErrorTV = findViewById(R.id.confirmAccNumberErrorTV);
            addCV = findViewById(R.id.addCV);
            endIconIV = findViewById(R.id.endIcon1IV);
            endIcon2IV = findViewById(R.id.endIcon2IV);
            headingTV = findViewById(R.id.headingTV);

            checkAccNumberET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmAccNumberET.setTransformationMethod(PasswordTransformationMethod.getInstance());

            nameOnBankTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

            if (getIntent().getStringExtra("From") != null && (getIntent().getStringExtra("From").equalsIgnoreCase("pay")
                    || getIntent().getStringExtra("From").equalsIgnoreCase("signUp")
                    || getIntent().getStringExtra("From").equalsIgnoreCase("REVIEW"))
                    || getIntent().getStringExtra("From").equalsIgnoreCase("Edit")) {
                headingTV.setText(R.string.add_bank_account);
                strScreen = getIntent().getStringExtra("From");
            } else if (getIntent().getStringExtra("From") != null && getIntent().getStringExtra("From").equalsIgnoreCase("Resubmit")) {
                headingTV.setText(R.string.resubmit);
                strScreen = getIntent().getStringExtra("From");
            }
            if (getIntent().getSerializableExtra("bankObject") != null) {
                BankAccount objBank = (BankAccount) getIntent().getSerializableExtra("bankObject");
                nameOnBankET.setText(objBank.getAccountName());
                routingNumberET.setText(objBank.getRoutingNumber());
                confirmRoutingNumberET.setText(objBank.getRoutingNumber());
                checkAccNumberET.setText(objBank.getAccountNumber());
                confirmAccNumberET.setText(objBank.getAccountNumber());
                Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.primary_black));
                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_black));
                Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                endIconIV.setVisibility(View.VISIBLE);
                endIcon2IV.setVisibility(View.VISIBLE);
                isName = true;
                isRoutNum = true;
                isConfRoutNum = true;
                isAcNum = true;
                isConfirmAc = true;
                enableOrDisableNext();
            } else {
                Utils.setUpperHintColor(nameOnBankTIL, getResources().getColor(R.color.primary_black));
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    nameOnBankET.setText(objMyApplication.getStrUserName());
                } else {
                    if (objMyApplication.getCompanyInfoResp() != null && objMyApplication.getCompanyInfoResp().getData() != null &&
                            objMyApplication.getCompanyInfoResp().getData().getName() != null) {
                        nameOnBankET.setText(objMyApplication.getCompanyInfoResp().getData().getName());
                    } else if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                            && objMyApplication.getMyProfile().getData().getCompanyName() != null &&
                            !objMyApplication.getMyProfile().getData().getCompanyName().equals("")) {
                        nameOnBankET.setText(objMyApplication.getMyProfile().getData().getCompanyName());
                    }
                }
                isName = true;
            }

            backLL.setOnClickListener(view -> {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(AddManualBankAccount.this);
                onBackPressed();
            });

            addCV.setOnClickListener(view -> {
                try {
                    if (isAddEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(AddManualBankAccount.this);
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (!strScreen.equals("Resubmit") && !strScreen.equals("Edit")) {
                            if (strScreen.equals("pay")) {
                                lyAddBank.setVisibility(View.GONE);
                                layoutLoader.setVisibility(View.VISIBLE);
                            }
                            ManualBankRequest request = new ManualBankRequest();
                            request.setAccountName(nameOnBankET.getText().toString());
                            request.setRoutingNumber(routingNumberET.getText().toString());
                            request.setAccountNumber(checkAccNumberET.getText().toString());
                            if (strScreen.equals("signUp") || strScreen.equals("REVIEW")) {
                                request.setGiactReq(false);
                            } else {
                                request.setGiactReq(true);
                            }
                            if (getIntent().getStringExtra("screen") != null) {
                                request.setFromTxnScreen(getIntent().getStringExtra("screen"));
                            } else {
                                request.setFromTxnScreen("");
                            }
                            paymentMethodsViewModel.saveManualBank(request);
                        } else {
                            BankAccount objBank = new BankAccount();
                            objBank.setAccountName(nameOnBankET.getText().toString());
                            objBank.setRoutingNumber(routingNumberET.getText().toString());
                            objBank.setAccountNumber(checkAccNumberET.getText().toString());
                            objMyApplication.setBankAccount(objBank);
                            Intent i = new Intent();
                            setResult(RESULT_OK, i);
                            finish();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
                        }
                    }
                    if (strScreen.equals("pay")) {
                        showSuccessFailure(manualBankResponse);
                    } else if (strScreen.equals("signUp") || strScreen.equals("REVIEW")) {
                        onBackPressed();
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
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 62) {
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 - i1 > 1) {
                        routingNumberET.setText(charSequence);
                        routingNumberET.setSelection(charSequence.toString().length());
                    }
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
                                routingNumberET.getText().toString().trim().equalsIgnoreCase(confirmRoutingNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                            endIconIV.setVisibility(View.VISIBLE);
                        }
                        if (charSeq.toString().length() == 9 && routingNumberET.getText().toString().trim().equalsIgnoreCase(confirmRoutingNumberET.getText().toString().trim())) {
                            isRoutNum = true;
                            isConfRoutNum = true;

                        } else {
                            isRoutNum = false;
                            isConfRoutNum = false;
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 - i1 > 1) {
                        confirmRoutingNumberET.setText(charSequence);
                        confirmRoutingNumberET.setSelection(charSequence.toString().length());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (routingNumberET.getText().toString().equalsIgnoreCase(confirmRoutingNumberET.getText().toString()) && charSeq.toString().length() == 9) {
                            endIconIV.setVisibility(View.VISIBLE);
                            isConfRoutNum = true;
                            isRoutNum = true;

                        } else {
                            isConfRoutNum = false;
                            isRoutNum = false;
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
                    if (i2 - i1 > 1) {
                        checkAccNumberET.setText(charSeq);
                        checkAccNumberET.setSelection(charSeq.toString().length());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (confirmAccNumberET.getText().toString().length() > 0 &&
                                !checkAccNumberET.getText().toString().trim().equalsIgnoreCase(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
                            confirmAccNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmAccNumberErrorTV.setText("Account number does not match");
                            endIcon2IV.setVisibility(View.GONE);
                        } else if (checkAccNumberET.getText().toString().length() <= 3 &&
                                checkAccNumberET.getText().toString().trim().equalsIgnoreCase(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            endIcon2IV.setVisibility(View.GONE);
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                        } else if (checkAccNumberET.getText().toString().trim().equalsIgnoreCase(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                            endIcon2IV.setVisibility(View.VISIBLE);
                        }
                        if (charSeq.toString().length() > 3 && checkAccNumberET.getText().toString().trim().equalsIgnoreCase(confirmAccNumberET.getText().toString().trim())) {
                            isAcNum = true;
                            isConfirmAc = true;
                        } else {
                            isAcNum = false;
                            isConfirmAc = false;
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
                    if (i2 - i1 > 1) {
                        confirmAccNumberET.setText(charSeq);
                        confirmAccNumberET.setSelection(charSeq.toString().length());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSeq, int i, int i1, int i2) {
                    if (charSeq != null && !charSeq.toString().equals("")) {
                        if (charSeq.toString().length() > 3 && confirmAccNumberET.getText().toString().trim().equalsIgnoreCase(checkAccNumberET.getText().toString().trim())) {
                            isConfirmAc = true;
                            isAcNum = true;
                            endIcon2IV.setVisibility(View.VISIBLE);

                        } else {
                            isConfirmAc = false;
                            isAcNum = false;
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

//                    if (nameOnBankET.getText().length() > 0 && !nameOnBankET.getText().toString().substring(0, 1).equals(" ")) {
//                        nameOnBankET.setText(nameOnBankET.getText().toString().substring(0, 1).toUpperCase() + nameOnBankET.getText().toString().substring(1));
//                    }
                    nameOnBankET.setHint("");
                    if (nameOnBankET.getText().toString().trim().length() > 0) {
                        nameOnBankErrorLL.setVisibility(View.GONE);
                        nameOnBankTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.primary_black));

                    }
//                    else if (nameOnBankET.getText().toString().trim().length() == 1) {
//                        nameOnBankTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                        Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.error_red));
//                        nameOnBankErrorLL.setVisibility(View.VISIBLE);
//                        nameOnBankErrorTV.setText("Minimum 2 Characters Required");
//                    }
                    else if (nameOnBankET.getText().toString().trim().length() <= 0) {
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
                    if (routingNumberET.getText() != null && routingNumberET.getText().length() > 1) {
                        routingNumberET.setSelection(routingNumberET.getText().length());
                    }
                    routingNumberTIL.setHint("Routing Number");
                    routingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
                    routingNumberErrorLL.setVisibility(View.GONE);
                    routingNumberET.setSelection(routingNumberET.getText().toString().trim().length());
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
                    if (confirmRoutingNumberET.getText() != null && confirmRoutingNumberET.getText().length() > 1) {
                        confirmRoutingNumberET.setSelection(confirmRoutingNumberET.getText().length());
                    }
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
                        } else if (confirmRoutingNumberET.getText().toString().trim().equalsIgnoreCase(routingNumberET.getText().toString().trim())) {
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
                    if (checkAccNumberET.getText() != null & checkAccNumberET.getText().length() > 1) {
                        checkAccNumberET.setSelection(checkAccNumberET.getText().length());
                    }
                    checkAccNumberTIL.setHint("Checking Account Number");
                    checkAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                    checkAccNumberErrorLL.setVisibility(View.GONE);
                } else {
                    checkAccNumberET.setHint("");
                    if (checkAccNumberET.getText().toString() != null && checkAccNumberET.getText().toString().length() > 3) {
                        checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_black));
                        checkAccNumberErrorLL.setVisibility(View.GONE);
                    } else {
                        checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.error_red));
                        checkAccNumberErrorLL.setVisibility(View.VISIBLE);
                        if (checkAccNumberET.getText().toString().length() > 0 && checkAccNumberET.getText().toString().length() < 4) {
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
                    if (confirmAccNumberET.getText() != null & confirmAccNumberET.getText().length() > 1) {
                        confirmAccNumberET.setSelection(confirmAccNumberET.getText().length());
                    }
                    confirmAccNumberTIL.setHint("Confirm Account Number");
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
                        } else if (confirmAccNumberET.getText().toString().trim().equalsIgnoreCase(checkAccNumberET.getText().toString().trim())) {
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
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(AddManualBankAccount.this);
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

            Window window = bankStatusDialog.getWindow();
            if (manualBankResponse.getData() != null && !manualBankResponse.getData().getGiactFail()) {
                nameOnBankTV.setText(manualBankResponse.getData().getAccountName());
                routingNumTV.setText(manualBankResponse.getData().getRoutingNumber());
                if (manualBankResponse.getData().getAccountNumber() != null && manualBankResponse.getData().getAccountNumber().length() > 4) {
//                    accNumTV.setText("**** " + manualBankResponse.getData().getAccountNumber().substring(manualBankResponse.getData().getAccountNumber().length() - 4));
                    convert = manualBankResponse.getData().getAccountNumber().replaceAll("", "");
                    String converted = convert.replaceAll("\\w(?=\\w{4})", "•");
                    accNumTV.setText(converted);
                } else {
                    accNumTV.setText(manualBankResponse.getData().getAccountNumber());
                }
                imageIV.setImageResource(R.drawable.ic_success_icon);
                headerTV.setText(getString(R.string.bank_account_added));
                bankNameTV.setText(manualBankResponse.getData().getBankName());
                doneTV.setText(getString(R.string.done));
                errorDescriptnTV.setVisibility(View.GONE);
                statusTV.setText("Approved");
                statusTV.setTextColor(getColor(R.color.active_green));
                statusTV.setBackgroundResource(R.drawable.bank_status_bg);
            } else {
                imageIV.setImageResource(R.drawable.ic_failed);
                headerTV.setText(getString(R.string.bank_account_failed));
                bankNameTV.setText("- -");
                doneTV.setText(getString(R.string.try_again));
                errorDescriptnTV.setVisibility(View.VISIBLE);
                statusTV.setText("Declined");
                statusTV.setTextColor(getColor(R.color.error));
                statusTV.setBackgroundResource(R.drawable.bank_status_decline_bg);
                if (manualBankResponse.getData() != null && manualBankResponse.getStatus().toLowerCase().equals("success")) {
                    nameOnBankTV.setText(manualBankResponse.getData().getAccountName());
                    routingNumTV.setText(manualBankResponse.getData().getRoutingNumber());
                    if (manualBankResponse.getData().getAccountNumber() != null &&
                            manualBankResponse.getData().getAccountNumber().length() > 4) {
//                        accNumTV.setText("**** " + manualBankResponse.getData().getAccountNumber().substring(manualBankResponse.getData().getAccountNumber().length() - 4));
                        convert = manualBankResponse.getData().getAccountNumber().replaceAll("", "");
                        String converted = convert.replaceAll("\\w(?=\\w{4})", "•");
                        accNumTV.setText(converted);

                    } else {
                        accNumTV.setText(manualBankResponse.getData().getAccountNumber());
                    }
                    errorDescriptnTV.setText("Bank verification failed due to error code: " + manualBankResponse.getData().getAccountResponseCodeName() + " - " + manualBankResponse.getData().getAccountReponseCodeDescription() + " Please try again.");
                } else {
                    nameOnBankTV.setText(nameOnBankET.getText().toString());
                    routingNumTV.setText(routingNumberET.getText().toString());
                    accNumTV.setText(checkAccNumberET.getText().toString());
                    if (checkAccNumberET.getText().toString() != null && checkAccNumberET.getText().toString().length() > 4) {
                        //accNumTV.setText("**** " + checkAccNumberET.getText().toString().substring(checkAccNumberET.getText().toString().length() - 4));
                        convert = checkAccNumberET.getText().toString().replaceAll("", "");
                        String converted = convert.replaceAll("\\w(?=\\w{4})", "•");
                        accNumTV.setText(converted);
                    } else {
                        accNumTV.setText(checkAccNumberET.getText().toString());
                    }
                    errorDescriptnTV.setText("Bank verification failed due to error code: " + manualBankResponse.getError().getErrorCode() + " - " + manualBankResponse.getError().getErrorDescription() + " Please try again.");
                }
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
                        if (manualBankResponse.getData() != null && !manualBankResponse.getData().getGiactFail()) {
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
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
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

    @Override
    protected void onResume() {
        super.onResume();
        routingNumberET.requestFocus();
        if (!Utils.isKeyboardVisible)
            Utils.shwForcedKeypad(this);
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Utils.isKeyboardVisible = visible;
    }

    private void blockCopy(EditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }
}
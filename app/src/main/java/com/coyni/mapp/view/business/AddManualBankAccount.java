package com.coyni.mapp.view.business;


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
import android.view.View;
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
import com.coyni.mapp.view.AddCardActivity;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.PaymentMethodsViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddManualBankAccount extends BaseActivity {

    private TextView descriptionTV, nameOnBankErrorTV, routingNumberErrorTV, confirmRoutingNumberErrorTV, checkAccNumberErrorTV, confirmAccNumberErrorTV;
    private TextInputLayout nameOnBankTIL, routingNumberTIL, confirmRoutingNumberTIL, checkAccNumberTIL, confirmAccNumberTIL;
    private TextInputEditText nameOnBankET, routingNumberET, confirmRoutingNumberET, checkAccNumberET, confirmAccNumberET;
    private LinearLayout nameOnBankErrorLL, routingNumberErrorLL, confirmRoutingNumberErrorLL, checkAccNumberErrorLL, confirmAccNumberErrorLL;
    private ImageView endIconIV, endIcon2IV, backLL;
    public boolean isName = false, isRoutNum = false, isConfRoutNum = false, isaccountNum = false, isConfirm = false, isAddEnabled = false;
    public static int focusedID = 0;
    private Long mLastClickTime = 0L;
    private MyApplication objMyApplication;
    private CardView addCV;
    PaymentMethodsViewModel paymentMethodsViewModel;
    String strScreen = "";
    RelativeLayout lyAddBank, layoutLoader;

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
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
            }
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

            backLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            addCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                            paymentMethodsViewModel.saveManualBank(request);
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
                        nameOnBankTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(nameOnBankTIL, getResources().getColor(R.color.primary_green));
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
                public void afterTextChanged(Editable editable) {

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
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence != null && charSequence.length() == 9) {
                            isRoutNum = true;
                        }
                        isRoutNum = false;
                        if (routingNumberET.getText().toString().trim().length() == 9) {
                            isRoutNum = true;
                            routingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            routingNumberTIL.setHint("Routing Number");
                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));

                        } else {
                            if (confirmRoutingNumberET.getText().toString().trim().length() > 0) {
                                routingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
                                confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                confirmRoutingNumberTIL.setHint("Routing Number doesn’t match");
                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.error_red));
                                endIconIV.setVisibility(View.GONE);
                            } else if (confirmRoutingNumberET.getText().toString().trim().length() == 0) {
                                confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.light_gray));
                                routingNumberTIL.setHint("Routing Number");
                                if (confirmRoutingNumberErrorLL.getVisibility() == View.VISIBLE) {
                                    confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                }
                            }
                        }
                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            routingNumberET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            routingNumberET.setText(s.toString().trim());
                            routingNumberET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (i2 > 2) {
                            if (charSequence != null && charSequence.length() == 9) {
                                isConfRoutNum = true;
                            }
                            isConfRoutNum = false;
                        }
                        if (charSequence.toString().trim().length() > 0) {
                            isConfRoutNum = true;
                            confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            confirmRoutingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getResources().getColor(R.color.primary_green));
                        } else if (charSequence.toString().trim().length() == 9 &&
                                routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
                            isConfRoutNum = true;
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            routingNumberTIL.setHint("Routing Number");
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_green));
                            endIconIV.setVisibility(View.VISIBLE);
                        } else if (charSequence.toString().trim().length() > 0) {
                            isConfRoutNum = true;
                            confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            confirmRoutingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            endIconIV.setVisibility(View.GONE);
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getResources().getColor(R.color.primary_green));
                        } else {
                            isConfRoutNum = false;
                        }
                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            confirmRoutingNumberET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            confirmRoutingNumberET.setText(s.toString().trim());
                            confirmRoutingNumberET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


//            routingNumberET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    if (i2 - i1 > 1) {
//                        routingNumberET.setText(charSequence);
//                        routingNumberET.setSelection(charSequence.toString().length());
//                    }
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    if (getCurrentFocus().getId() == routingNumberET.getId()) {
//
//                        if (routingNumberET.getText().toString().length() == 9) {
//                            routingNumberTIL.setHint("Routing Number");
//                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
//
////                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
//                            if (confirmRoutingNumberET.getText().toString().trim().length() == 0)
//                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.light_gray));
//                            else {
//                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
//                                confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
//                            }
//
//                        } else if (routingNumberET.getText().toString().trim().length() > 0 &&
//                                routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
//                            isConfRoutNum = true;
//                            routingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
//                            routingNumberTIL.setHint("Routing Number");
//                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
//                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
//                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
//                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
//
//                        } else {
//                            isConfRoutNum = false;
//
//                            if (confirmRoutingNumberET.getText().toString().trim().length() > 0) {
//                                routingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
//                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
//                                confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                                confirmRoutingNumberTIL.setHint("Routing Number doesn’t match");
//                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.error_red));
//                            } else if (confirmRoutingNumberET.getText().toString().trim().length() == 0) {
//                                confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
//                                confirmRoutingNumberTIL.setHint("Confirm Routing Number");
//                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.light_gray));
//
//                                routingNumberTIL.setHint("Routing Number");
//
//                                if (confirmAccNumberErrorLL.getVisibility() == VISIBLE) {
//                                    confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                                }
//                            }
//                        }
//                        enableOrDisableNext();
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try {
//                        if (s.length() > 0 && s.toString().trim().length() == 0) {
//                            routingNumberET.setText("");
//                        } else if (s.length() > 0 && s.toString().contains(" ")) {
//                            routingNumberET.setText(s.toString().trim());
//                            routingNumberET.setSelection(s.toString().trim().length());
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//
//            confirmRoutingNumberET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    if (i2 - i1 > 1) {
//                        confirmRoutingNumberET.setText(charSequence);
//                        confirmRoutingNumberET.setSelection(charSequence.toString().length());
//                    }
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    if (getCurrentFocus().getId() == confirmRoutingNumberET.getId()) {
//
//                        if (confirmRoutingNumberET.getText().toString().length() == 0) {
//                            routingNumberTIL.setHint("Routing Number");
//                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
//                           if (confirmRoutingNumberET.getText().toString().trim().length()  == 9 && routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
//                                isConfRoutNum = true;
//                                routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
//                                routingNumberTIL.setHint("Routing Number");
//                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_black));
//                                confirmRoutingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
//                                confirmRoutingNumberTIL.setHint("Confirm Routing Number");
//                                Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_green));
//
//                            } else {
//                                isConfRoutNum = false;
////                            confirmRoutingNumberTIL.setHint("Password doesn’t match");
//                            }
//                        }
//                    }
//                    enableOrDisableNext();
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    try {
//                        if (s.length() > 0 && s.toString().trim().length() == 0) {
//                            confirmRoutingNumberET.setText("");
//                        } else if (s.length() > 0 && s.toString().contains(" ")) {
//                            confirmRoutingNumberET.setText(s.toString().trim());
//                            confirmRoutingNumberET.setSelection(s.toString().trim().length());
//                        }
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });


            checkAccNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 - i1 > 1) {
                        checkAccNumberET.setText(charSequence);
                        checkAccNumberET.setSelection(charSequence.toString().length());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.toString().trim().length() == 17) {
                            isaccountNum = true;
                            checkAccNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            checkAccNumberTIL.setHint("Checking Account Number");
                            Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));

                        } else {
                            if (confirmAccNumberET.getText().toString().trim().length() > 0) {
                                checkAccNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                                confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                confirmAccNumberTIL.setHint("Account Number doesn’t match");
                                Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
                                endIcon2IV.setVisibility(View.GONE);
                                isaccountNum = false;
                            } else if (confirmAccNumberET.getText().toString().trim().length() == 0) {
                                confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                confirmAccNumberTIL.setHint("Confirm Account Number");
                                Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.light_gray));
                                checkAccNumberTIL.setHint("Checking Account Number");
                                if (confirmAccNumberErrorLL.getVisibility() == View.VISIBLE) {
                                    confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                }
                            }
                        }
                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            checkAccNumberET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            checkAccNumberET.setText(s.toString().trim());
                            checkAccNumberET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confirmAccNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 - i1 > 1) {
                        confirmAccNumberET.setText(charSequence);
                        confirmAccNumberET.setSelection(charSequence.toString().length());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (i2 > 2) {
                            if (charSequence != null && charSequence.length() == 17) {
                                isConfirm = true;
                            }
                        }
                        if (charSequence.toString().trim().length() > 4 && charSequence.toString().trim().length() < 17) {
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                            endIcon2IV.setVisibility(View.GONE);
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                            confirmAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getResources().getColor(R.color.primary_green));
                        } else if (checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            isConfirm = true;
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            checkAccNumberTIL.setHint("Checking Account Number");
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            confirmAccNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_green));
                            endIcon2IV.setVisibility(View.VISIBLE);
                        } else if (confirmAccNumberET.getText().toString().trim().length() < 17) {
                            isConfirm = true;
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                            confirmAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            endIcon2IV.setVisibility(View.GONE);
                            Utils.setUpperHintColor(confirmAccNumberTIL, getResources().getColor(R.color.primary_green));
                        } else {
                            isConfirm = false;
                        }
                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            confirmAccNumberET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            confirmAccNumberET.setText(s.toString().trim());
                            confirmAccNumberET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusWatchers() {
        try {
            nameOnBankET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {

                        if (nameOnBankET.getText().toString().length() > 0 && !nameOnBankET.getText().toString().substring(0, 1).equals(" ")) {
                            nameOnBankET.setText(nameOnBankET.getText().toString().substring(0, 1).toUpperCase() + nameOnBankET.getText().toString().substring(1).toLowerCase());
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
                        } else {
                            nameOnBankTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.light_gray));
                            nameOnBankErrorLL.setVisibility(View.VISIBLE);
                            nameOnBankErrorTV.setText("Field Required");
                        }
                    } else {
                        nameOnBankErrorLL.setVisibility(View.GONE);
                        focusedID = nameOnBankET.getId();
                        nameOnBankTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(nameOnBankTIL, getColor(R.color.primary_green));
                        nameOnBankET.setSelection(nameOnBankET.getText().toString().trim().length());
                    }
                }
            });

            routingNumberET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (b) {
                            if (!Utils.isKeyboardVisible)
                                Utils.shwForcedKeypad(AddManualBankAccount.this);
                            routingNumberTIL.setHint("Routing Number");
                            routingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
                            routingNumberErrorLL.setVisibility(View.GONE);
                        } else {
                            routingNumberET.setHint("");
                            if (routingNumberET.getText().toString().trim().length() == 0) {
                                routingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.light_gray));
                                routingNumberErrorLL.setVisibility(View.VISIBLE);
                                routingNumberErrorTV.setText("Field Required");
                            } else if (routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
                                routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_black));
                            } else if (routingNumberET.getText().toString().trim().length() < 9) {
                                routingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.error_red));
                                routingNumberErrorLL.setVisibility(View.VISIBLE);
                                routingNumberErrorTV.setText("Enter valid Routing Number");
                            } else if (routingNumberET.getText().toString().length() >= 0 && confirmRoutingNumberET.getText().toString().length() >= 0 &&
                                    !routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
                                routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_black));
                                confirmRoutingNumberTIL.setHint("Routing Number doesn’t match");
                                endIconIV.setVisibility(View.GONE);
                            } else {
                                routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_black));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confirmRoutingNumberET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(AddManualBankAccount.this);
                        confirmRoutingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_green));
                        confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                        confirmRoutingNumberErrorLL.setVisibility(View.GONE);
                    } else {
                        confirmRoutingNumberET.setHint("");
                        if (confirmRoutingNumberET.getText().toString().trim().length() == 0) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.light_gray));
                            confirmRoutingNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmRoutingNumberErrorTV.setText("Field Required");
                            endIconIV.setVisibility(View.GONE);
                        } else if (routingNumberET.getText().toString().trim().length() == 9 && confirmRoutingNumberET.getText().toString().trim().length() == 9 &&
                                routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            endIconIV.setVisibility(View.VISIBLE);
                        } else if (confirmRoutingNumberET.getText().toString().length() > 0
                                && !routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.error_red));
                            confirmRoutingNumberTIL.setHint("Routing Number doesn’t match");
                            endIconIV.setVisibility(View.GONE);
                        } else {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                        }
                    }

                }
            });

            checkAccNumberET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (b) {
                            if (!Utils.isKeyboardVisible)
                                Utils.shwForcedKeypad(AddManualBankAccount.this);
                            checkAccNumberTIL.setHint("Checking Account Number");
                            checkAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                            checkAccNumberErrorLL.setVisibility(View.GONE);
                        } else {
                            checkAccNumberET.setHint("");
                            if (checkAccNumberET.getText().toString().trim().length() == 0) {
                                checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.light_gray));
                                checkAccNumberErrorLL.setVisibility(View.VISIBLE);
                                checkAccNumberErrorTV.setText("Field Required");
                            } else if (checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                                checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                confirmAccNumberTIL.setHint("Confirm Account Number");
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_black));
                            } else if (checkAccNumberET.getText().toString().trim().length() < 17) {
                                checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.error_red));
                                checkAccNumberErrorLL.setVisibility(View.VISIBLE);
                                checkAccNumberErrorTV.setText("Enter valid Account Number");
                            } else if (checkAccNumberET.getText().toString().length() > 4 && confirmAccNumberET.getText().toString().length() > 4 &&
                                    !checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                                checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_black));
                                confirmAccNumberTIL.setHint("Account Number doesn’t match");
                            } else {
                                checkAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_black));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            confirmAccNumberET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(AddManualBankAccount.this);
                        confirmAccNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_green));
                        confirmAccNumberTIL.setHint("Confirm Account Number");
                        confirmAccNumberErrorLL.setVisibility(View.GONE);
                    } else {
                        confirmAccNumberET.setHint("");
                        if (confirmAccNumberET.getText().toString().trim().length() == 0) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.light_gray));
                            confirmAccNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmAccNumberErrorTV.setText("Field Required");
                            endIcon2IV.setVisibility(View.GONE);
                        } else if (checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                            endIcon2IV.setVisibility(View.VISIBLE);
                        } else if (checkAccNumberET.getText().toString().length() > 0 && confirmAccNumberET.getText().toString().length() > 0
                                && !checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
                            confirmAccNumberTIL.setHint("Account Number doesn’t match");
                            endIcon2IV.setVisibility(View.GONE);
                        } else {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                        }
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableOrDisableNext() {
        try {
            if (isName && isRoutNum && isConfRoutNum && isaccountNum && isConfirm) {
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
}
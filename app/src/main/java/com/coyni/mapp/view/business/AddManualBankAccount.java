package com.coyni.mapp.view.business;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddManualBankAccount extends BaseActivity {

    private TextView descriptionTV, nameOnBankErrorTV, routingNumberErrorTV, confirmRoutingNumberErrorTV, checkAccNumberErrorTV,confirmAccNumberErrorTV;
    private TextInputLayout nameOnBankTIL, routingNumberTIL, confirmRoutingNumberTIL, checkAccNumberTIL, confirmAccNumberTIL;
    private TextInputEditText nameOnBankET, routingNumberET, confirmRoutingNumberET, checkAccNumberET, confirmAccNumberET;
    private LinearLayout backLL, nameOnBankErrorLL, routingNumberErrorLL, confirmRoutingNumberErrorLL, checkAccNumberErrorLL, confirmAccNumberErrorLL;
    public boolean isName = false, isRoutNum = false, isRoutNumCon = false, isaccountNum = false, isConfirm = false, isAddEnabled = false;
    public static int focusedID = 0;
    private Long mLastClickTime = 0L;
    private MyApplication objMyApplication;
    private CardView addCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual_bank_account);

        initfields();
        textWatchers();
        focusWatchers();

    }

    private void initfields() {
        setSpannableText();
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

        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() == 5) {
                            isRoutNum = true;
                            routingNumberErrorLL.setVisibility(View.GONE);
                            if (routingNumberET.hasFocus())
                                Utils.setUpperHintColor(routingNumberTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() < 5 && charSequence.length() > 0) {
                            isRoutNum = false;
                            routingNumberErrorLL.setVisibility(View.GONE);
                        } else if (charSequence.length() == 0) {
                            isRoutNum = false;
                        }
                        enableOrDisableNext();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
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
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (i2 > 2) {
                            if (charSequence != null && charSequence.length() < 51) {
                                isRoutNumCon = true;
                            }
                        }
                        if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 14) {
                            isRoutNumCon = true;
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            confirmRoutingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getResources().getColor(R.color.primary_green));
                        } else if (routingNumberET.getText().toString().trim().equals(confirmRoutingNumberET.getText().toString().trim())) {
                            isRoutNumCon = true;
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            routingNumberTIL.setHint("Checking Routing Number");
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            confirmRoutingNumberTIL.setHint("Confirm Routing Number");
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_green));
                        } else {
                            isRoutNumCon = false;
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
                        if (checkAccNumberET.getText().toString().trim().length() > 1 && checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            isaccountNum = true;

                            checkAccNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                            checkAccNumberTIL.setHint("Checking Account Number");
                            Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));

                        } else {
                            isaccountNum = false;

                            if (confirmAccNumberET.getText().toString().trim().length() > 0) {
                                checkAccNumberTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                                Utils.setUpperHintColor(checkAccNumberTIL, getColor(R.color.primary_green));
                                confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                confirmAccNumberTIL.setHint("Account Number doesn’t match");
                                Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
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
                            if (charSequence != null && charSequence.length() < 51) {
                                isConfirm = true;
                            }
                        }
                        if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 14) {
                            isConfirm = true;
                            confirmAccNumberErrorLL.setVisibility(View.GONE);
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
                    if (!b) {
                        routingNumberET.setHint("");
                        if (routingNumberET.getText().toString().trim().length() > 1) {
                            routingNumberErrorLL.setVisibility(View.GONE);
                            routingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_black));

                        } else if (routingNumberET.getText().toString().trim().length() < 5 && routingNumberET.getText().toString().trim().length() > 0) {
                            routingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.error_red));
                            routingNumberErrorLL.setVisibility(View.VISIBLE);
                            routingNumberErrorTV.setText("Minimum 2 Digits Required");

                        } else {
                            routingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.light_gray));
                            routingNumberErrorLL.setVisibility(View.VISIBLE);
                            routingNumberErrorTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(AddManualBankAccount.this);
                        routingNumberTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(routingNumberTIL, getColor(R.color.primary_green));
                        routingNumberErrorLL.setVisibility(View.GONE);
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
                        confirmRoutingNumberTIL.setHint("Confirm Account Number");
                        confirmAccNumberErrorLL.setVisibility(View.GONE);
                    } else {
                        confirmAccNumberET.setHint("");
                        if (confirmAccNumberET.getText().toString().trim().length() == 0) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.light_gray));
                            confirmAccNumberErrorLL.setVisibility(View.VISIBLE);
                            confirmAccNumberErrorTV.setText("Field Required");
                        } else if (checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.primary_black));
                            confirmRoutingNumberTIL.setHint("Confirm Account Number");
                        } else if (checkAccNumberET.getText().toString().length() > 0 && confirmAccNumberET.getText().toString().length() > 0 && !checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            confirmRoutingNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmRoutingNumberTIL, getColor(R.color.error_red));
                            confirmRoutingNumberTIL.setHint("Account Number doesn’t match");
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
                            } else if (checkAccNumberET.getText().toString().length() > 0 && confirmAccNumberET.getText().toString().length() > 0 && !checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
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
                        } else if (checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.primary_black));
                            confirmAccNumberTIL.setHint("Confirm Account Number");
                        } else if (checkAccNumberET.getText().toString().length() > 0 && confirmAccNumberET.getText().toString().length() > 0 && !checkAccNumberET.getText().toString().trim().equals(confirmAccNumberET.getText().toString().trim())) {
                            confirmAccNumberTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(confirmAccNumberTIL, getColor(R.color.error_red));
                            confirmAccNumberTIL.setHint("Account Number doesn’t match");
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
//            if (isName && isRoutNum && isaccountNum && isConfirm) {
            isAddEnabled = true;
            addCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
//
//            } else {
//                isAddEnabled = false;
//                binding.addCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSpannableText() {
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
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(Utils.mondayURL));
                    startActivity(i);
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
        ss.setSpan(clickableSpan, strMessage.length() - 24, strMessage.length()-1, 0);
        descriptionTV.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionTV.setHighlightColor(Color.TRANSPARENT);

        descriptionTV.setText(ss);


    }

}
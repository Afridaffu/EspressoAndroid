package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.signet.SignetRequest;
import com.greenbox.coyni.model.signet.SignetResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class AddPaymentSignetActivity extends AppCompatActivity {
    TextInputLayout etlName, etlWalletId, etlAddress1, etlAddress2, etlCity, etlState, etlZipCode;
    TextInputEditText etName, etWalletId, etAddress1, etAddress2, etCity, etState, etZipCode;
    LinearLayout nameErrorLL, walletErrorLL, address1ErrorLL, address2ErrorLL, cityErrorLL, stateErrorLL, zipErrorLL, layoutClose;
    MyApplication objMyApplication;
    CardView cvAdd;
    ConstraintLayout clStates;
    BusinessDashboardViewModel businessDashboardViewModel;
    Long mLastClickTime = 0L;
    ProgressDialog progressDialog;
    Dialog preDialog;
    Boolean isName, isWallet, isAddress1 = false, isCity = false, isState = false, isZipcode = false, isAddEnabled = false;
    TextView nameErrorTV, walletErrorTV, address1ErrorTV, cityErrorTV, stateErrorTV, zipErrorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_payment_signet);
            initialization();
            initObserver();
            focusWatchers();
            textWatchers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            layoutClose = findViewById(R.id.layoutClose);
            clStates = findViewById(R.id.clStates);
            nameErrorLL = findViewById(R.id.nameErrorLL);
            walletErrorLL = findViewById(R.id.walletErrorLL);
            address1ErrorLL = findViewById(R.id.address1ErrorLL);
            address2ErrorLL = findViewById(R.id.address2ErrorLL);
            cityErrorLL = findViewById(R.id.cityErrorLL);
            stateErrorLL = findViewById(R.id.stateErrorLL);
            zipErrorLL = findViewById(R.id.zipErrorLL);
            etlName = findViewById(R.id.etlName);
            etlWalletId = findViewById(R.id.etlWalletId);
            etlAddress1 = findViewById(R.id.etlAddress1);
            etlAddress2 = findViewById(R.id.etlAddress2);
            etlCity = findViewById(R.id.etlCity);
            etlState = findViewById(R.id.etlState);
            etlZipCode = findViewById(R.id.etlZipCode);
            etName = findViewById(R.id.etName);
            etWalletId = findViewById(R.id.etWalletId);
            etAddress1 = findViewById(R.id.etAddress1);
            etAddress2 = findViewById(R.id.etAddress2);
            etCity = findViewById(R.id.etCity);
            etState = findViewById(R.id.etState);
            etZipCode = findViewById(R.id.etZipCode);
            nameErrorTV = findViewById(R.id.nameErrorTV);
            walletErrorTV = findViewById(R.id.walletErrorTV);
            address1ErrorTV = findViewById(R.id.address1ErrorTV);
            cityErrorTV = findViewById(R.id.cityErrorTV);
            stateErrorTV = findViewById(R.id.stateErrorTV);
            zipErrorTV = findViewById(R.id.zipErrorTV);
            cvAdd = findViewById(R.id.cvAdd);

            etlName.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            etlWalletId.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            etlState.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

            clStates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.hideKeypad(AddPaymentSignetActivity.this);
                    Utils.populateStates(AddPaymentSignetActivity.this, etState, objMyApplication);
                }
            });

            etState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.hideKeypad(AddPaymentSignetActivity.this);
                    Utils.populateStates(AddPaymentSignetActivity.this, etState, objMyApplication);
                }
            });

            etlState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.hideKeypad(AddPaymentSignetActivity.this);
                    Utils.populateStates(AddPaymentSignetActivity.this, etState, objMyApplication);
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    Utils.hideKeypad(AddPaymentSignetActivity.this);
                }
            });

            cvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isAddEnabled) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            progressDialog = Utils.showProgressDialog(AddPaymentSignetActivity.this);
                            SignetRequest obj = new SignetRequest();
                            obj.setAccountCategory("Signet");
                            obj.setAccountName(etName.getText().toString());
                            obj.setAccountNumber(etWalletId.getText().toString());
                            obj.setAccountType("Savings");
                            obj.setAddressLine1(etAddress1.getText().toString().trim());
                            obj.setAddressLine2(etAddress2.getText().toString().trim());
                            obj.setBankAccountName("Signet");
                            obj.setBankName("Signet");
//                                obj.setCountry(etCountry.getText().toString().trim());
                            obj.setCountry("US");
                            obj.setState(etState.getText().toString().trim());
                            obj.setCity(etCity.getText().toString().trim());
                            obj.setZipCode(etZipCode.getText().toString().trim());
                            obj.setDefault(true);
                            if (Utils.checkInternet(AddPaymentSignetActivity.this)) {
                                businessDashboardViewModel.saveSignetBank(obj);
                            } else {
                                Utils.displayAlert(getString(R.string.internet), AddPaymentSignetActivity.this, "", "");
                            }
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
        businessDashboardViewModel.getSignetResponseMutableLiveData().observe(this, new Observer<SignetResponse>() {
            @Override
            public void onChanged(SignetResponse signetResponse) {
                progressDialog.dismiss();
                if (signetResponse != null) {
                    if (signetResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                        displaySuccess();
                    } else {
                        Utils.displayAlert(signetResponse.getError().getErrorDescription(),
                                AddPaymentSignetActivity.this, "", signetResponse.getError().getFieldErrors().get(0));
                    }
                }
            }
        });

    }

    private void focusWatchers() {
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (!b) {
                        etName.setHint("");
                        if (etName.getText().toString().trim().length() > 1) {
                            isName = true;
                            nameErrorLL.setVisibility(GONE);
                            etlName.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                        } else if (etName.getText().toString().trim().length() == 1) {
                            isName = false;
                            etlName.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                            nameErrorLL.setVisibility(VISIBLE);
                            nameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            isName = false;
                            etlName.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlName, getColor(R.color.light_gray));
                            nameErrorLL.setVisibility(VISIBLE);
                            nameErrorTV.setText(R.string.field_required);
                        }
                        if (etName.getText().toString().length() > 0 && !etName.getText().toString().substring(0, 1).equals(" ")) {
                            etName.setText(etName.getText().toString().substring(0, 1).toUpperCase() + etName.getText().toString().substring(1));
                            etName.setSelection(etName.getText().toString().length());
                        }

                    } else {
                        etName.setHint("Enter Name");
                        etlName.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlName, getColor(R.color.primary_green));
                        InputMethodManager imm = (InputMethodManager) AddPaymentSignetActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(etName, 0);
                        nameErrorLL.setVisibility(GONE);
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etWalletId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (!b) {
                        etWalletId.setHint("");
                        if (etWalletId.getText().toString().trim().length() > 1) {
                            isWallet = true;
                            walletErrorLL.setVisibility(GONE);
                            etlWalletId.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlWalletId, getColor(R.color.primary_black));
                        }  else if (etWalletId.getText().toString().trim().length() == 1) {
                            isWallet = false;
                            etlWalletId.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(etlWalletId, getColor(R.color.error_red));
                            walletErrorLL.setVisibility(VISIBLE);
                            walletErrorTV.setText("Minimum 2 Characters Required");
                        }else {
                            isWallet = false;
                            etlWalletId.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlWalletId, getColor(R.color.light_gray));
                            walletErrorLL.setVisibility(VISIBLE);
                            walletErrorTV.setText(R.string.field_required);
                        }
                    } else {
                        etWalletId.setHint("XXXXXXXXXXXX XXXX");
                        etlWalletId.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlWalletId, getColor(R.color.primary_green));
                        walletErrorLL.setVisibility(GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etAddress1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (!b) {
                        etAddress1.setHint("");
                        if (etAddress1.getText().toString().trim().length() > 0) {
                            address1ErrorLL.setVisibility(GONE);
                            etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_black));

                        } else {
                            etlAddress1.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.light_gray));
                            address1ErrorLL.setVisibility(VISIBLE);
                            address1ErrorTV.setText(R.string.field_required);
                        }
                        if (etAddress1.getText().toString().length() > 0 && !etAddress1.getText().toString().substring(0, 1).equals(" ")) {
                            etAddress1.setText(etAddress1.getText().toString().substring(0, 1).toUpperCase() + etAddress1.getText().toString().substring(1));
                            etAddress1.setSelection(etAddress1.getText().toString().length());
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_black));
                        }
                    } else {
                        etAddress1.setHint("Billing Address Line 1");
                        etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_green));
                        address1ErrorLL.setVisibility(GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etAddress2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (!b) {
                        etAddress2.setHint("");
                        if (etAddress2.getText().toString().trim().length() > 0) {
                            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlAddress2, getColor(R.color.primary_black));

                        } else {
                            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlAddress2, getColor(R.color.light_gray));
                        }
                        if (etAddress2.getText().toString().length() > 0 && !etAddress2.getText().toString().substring(0, 1).equals(" ")) {
                            etAddress2.setText(etAddress2.getText().toString().substring(0, 1).toUpperCase() + etAddress2.getText().toString().substring(1));
                            etAddress2.setSelection(etAddress2.getText().toString().length());
                        }
                    } else {
                        etAddress2.setHint("Billing Address Line 2 (Optional)");
                        etlAddress2.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlAddress2, getColor(R.color.primary_green));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (!b) {
                        etCity.setHint("");
                        if (etCity.getText().toString().trim().length() > 0) {
                            cityErrorLL.setVisibility(GONE);
                            etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlCity, getColor(R.color.primary_black));

                        } else {
                            etlCity.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlCity, getColor(R.color.light_gray));
                            cityErrorLL.setVisibility(VISIBLE);
                            cityErrorTV.setText(R.string.field_required);
                        }
                        if (etCity.getText().toString().length() > 0 && !etCity.getText().toString().substring(0, 1).equals(" ")) {
                            etCity.setText(etCity.getText().toString().substring(0, 1).toUpperCase() + etCity.getText().toString().substring(1));
                            etCity.setSelection(etCity.getText().toString().length());
                            Utils.setUpperHintColor(etlCity, getColor(R.color.primary_black));
                        }
                    } else {
                        etCity.setHint("City");
                        etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlCity, getColor(R.color.primary_green));
                        cityErrorLL.setVisibility(GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etState.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {

                    Utils.hideKeypad(AddPaymentSignetActivity.this);

                    if (!b) {
                        if (etState.getText().toString().trim().length() > 0) {
                            stateErrorLL.setVisibility(GONE);
                            etlState.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlState, getColor(R.color.primary_black));

                        } else {
                            etlState.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlState, getColor(R.color.error_red));
                            stateErrorLL.setVisibility(VISIBLE);
                            stateErrorTV.setText(R.string.field_required);
                        }
                    } else {
                        Utils.hideKeypad(AddPaymentSignetActivity.this);
                        etlState.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlState, getColor(R.color.primary_green));
                        stateErrorLL.setVisibility(GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (!b) {
                        etZipCode.setHint("");
                        if (etZipCode.getText().toString().trim().length() == 5) {
                            zipErrorLL.setVisibility(GONE);
                            etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_black));
                        } else if (etZipCode.getText().toString().trim().length() > 0 && etZipCode.getText().toString().trim().length() < 5) {
                            etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                            zipErrorLL.setVisibility(VISIBLE);
                            zipErrorTV.setText("Minimum 5 Digits Required");
                        } else if (etZipCode.getText().toString().trim().length() == 0) {
                            etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(etlZipCode, getColor(R.color.light_gray));
                            zipErrorLL.setVisibility(VISIBLE);
                            zipErrorTV.setText(R.string.field_required);
                        }
                    } else {
                        Utils.shwForcedKeypad(AddPaymentSignetActivity.this);
                        etZipCode.setHint("Zip Code");
                        etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_green));
                        zipErrorLL.setVisibility(GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void textWatchers() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (i2 > 2) {
                        if (charSequence != null && charSequence.length() < 31) {
                            isName = true;
                        }
                    }
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 61) {
                        isName = true;
                        nameErrorLL.setVisibility(GONE);
                        etlName.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlName, getResources().getColor(R.color.primary_green));
                    } else {
                        isName = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!etName.hasFocus() && etName.getText().toString().trim().length() > 1) {
                        etlName.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                    }
                    String str = etName.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        etName.setText("");
                        etName.setSelection(etName.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        etName.setText(str.trim());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etName.setText(etName.getText().toString().replaceAll("\\.", ""));
                        etName.setSelection(etName.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etName.setText("");
                        etName.setSelection(etName.getText().length());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etWalletId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (i2 > 2) {
                        if (charSequence != null && charSequence.length() < 51) {
                            isWallet = true;
                        }
                    }
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 61) {
                        isWallet = true;
                        walletErrorLL.setVisibility(GONE);
                        etlWalletId.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlWalletId, getResources().getColor(R.color.primary_green));
                    } else {
                        isWallet = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!etWalletId.hasFocus() && etWalletId.getText().toString().trim().length() > 1) {
                        etlWalletId.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(etlWalletId, getColor(R.color.primary_black));
                    }
                    String str = etWalletId.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        etWalletId.setText("");
                        etWalletId.setSelection(etWalletId.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etWalletId.setText(etWalletId.getText().toString().replaceAll("\\.", ""));
                        etWalletId.setSelection(etWalletId.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etWalletId.setText("");
                        etWalletId.setSelection(etWalletId.getText().length());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 101) {
                        isAddress1 = true;
                        address1ErrorLL.setVisibility(GONE);
                        etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlAddress1, getResources().getColor(R.color.primary_green));
                    } else {
                        isAddress1 = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etAddress1.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        etAddress1.setText("");
                        etAddress1.setSelection(etAddress1.getText().length());
                        address2ErrorLL.setVisibility(GONE);
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        etAddress1.setText(str.trim());
                    } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etAddress1.setText("");
                        etAddress1.setSelection(etAddress1.getText().length());
                        address2ErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        etAddress2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.length() > 0) {
                        Utils.setUpperHintColor(etlAddress2, getResources().getColor(R.color.primary_black));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = etAddress2.getText().toString();
                    if (str.substring(0).equals(" ")) {
                        etAddress2.setText("");
                        etAddress2.setSelection(etAddress2.getText().length());
//                            address1ErrorLL.setVisibility(GONE);
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        etAddress2.setText(str.trim());
                    } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etAddress2.setText("");
                        etAddress2.setSelection(etAddress2.getText().length());
//                            address1ErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 51) {
                        isCity = true;
                        cityErrorLL.setVisibility(GONE);
                        etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlCity, getResources().getColor(R.color.primary_green));
                    } else {
                        isCity = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etCity.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        etCity.setText("");
                        etCity.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        etCity.setText(str.trim());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etCity.setText(etCity.getText().toString().replaceAll("\\.", ""));
                        etCity.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etCity.setText("");
                        etCity.setSelection(etCity.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0) {
                        isState = true;
                        stateErrorLL.setVisibility(GONE);
                        Utils.setUpperHintColor(etlState, getColor(R.color.primary_black));
                    } else {
                        isState = false;
                        Utils.setUpperHintColor(etlState, getColor(R.color.light_gray));
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() == 5) {
                        isZipcode = true;
                        zipErrorLL.setVisibility(GONE);
                        etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlZipCode, getResources().getColor(R.color.primary_green));
                    } else {
                        isZipcode = false;
                        zipErrorLL.setVisibility(GONE);
                        etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlZipCode, getResources().getColor(R.color.primary_green));
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etZipCode.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        etZipCode.setText("");
                        etZipCode.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etZipCode.setText(etZipCode.getText().toString().replaceAll("\\.", ""));
                        etZipCode.setSelection(etZipCode.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etZipCode.setText("");
                        etZipCode.setSelection(etZipCode.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void enableOrDisableNext() {
        try {
            if (isName && isWallet && isAddress1 && isCity && isZipcode && isState) {
                isAddEnabled = true;
                cvAdd.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isAddEnabled = false;
                cvAdd.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displaySuccess() {
        try {
            CardView cvDone;
            TextView tvMessage;
            preDialog = new Dialog(AddPaymentSignetActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.activity_all_done_success);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            preDialog.getWindow().setAttributes(lp);
            preDialog.setCancelable(false);
            preDialog.show();
            cvDone = preDialog.findViewById(R.id.cvDone);
            tvMessage = preDialog.findViewById(R.id.tvMessage);
            tvMessage.setText("Your Signet Account has been successfully authorized and added to your payment methods.");

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        objMyApplication.setSignet(true);
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            preDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            etName.requestFocus();
            Utils.shwForcedKeypad(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
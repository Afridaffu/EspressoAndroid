package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.cards.CardDeleteResponse;
import com.greenbox.coyni.model.cards.CardEditRequest;
import com.greenbox.coyni.model.cards.CardEditResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MaskEditText.widget.MaskEditText;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CardNumberEditText;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class EditCardActivity extends AppCompatActivity {
    PaymentsList selectedCard;
    MyApplication objMyApplication;
    TextInputEditText etName, etAddress1, etAddress2, etCity, etState, etZipcode, etCountry;
    CardNumberEditText etlCard;
    MaskEditText etExpiry;
    PaymentMethodsViewModel paymentMethodsViewModel;
    CardView cvSave, cvRemove;
    ProgressDialog dialog, pDialog;
    ConstraintLayout clStates;
    LinearLayout address1ErrorLL, cityErrorLL, stateErrorLL, zipErrorLL, layoutBack, expiryErrorLL;
    TextView address1ErrorTV, cityErrorTV, stateErrorTV, zipErrorTV;
    TextInputLayout etlState, etlAddress1, etlAddress2, etlCity, etlZipCode, etlExpiry, etlName;
    TextView tvCard, expiryErrorTV;
    Boolean isExpiry = false, isAddress1 = false, isCity = false, isState = false, isZipcode = false, isAddEnabled = false;
    Long mLastClickTime = 0L;
    public static EditCardActivity editCardActivity;
    String strScreen = "";
    int diffMonths = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_card);

            initialization();
            initObserver();
            textWatchers();
            focusWatchers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Utils.tempStateName = "";
        super.onBackPressed();
    }

    private void initialization() {
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);

            editCardActivity = this;
            objMyApplication = (MyApplication) getApplicationContext();
            selectedCard = objMyApplication.getSelectedCard();
            etName = findViewById(R.id.etName);
            etlCard = findViewById(R.id.etlCard);
            etExpiry = findViewById(R.id.etExpiry);
            etAddress1 = findViewById(R.id.etAddress1);
            etAddress2 = findViewById(R.id.etAddress2);
            etCity = findViewById(R.id.etCity);
            etState = findViewById(R.id.etState);
            etZipcode = findViewById(R.id.etZipcode);
            etCountry = findViewById(R.id.etCountry);
            tvCard = findViewById(R.id.tvCard);
            cvSave = findViewById(R.id.cvSave);
            cvRemove = findViewById(R.id.cvRemove);
            layoutBack = findViewById(R.id.layoutBack);
            expiryErrorLL = findViewById(R.id.expiryErrorLL);
            expiryErrorTV = findViewById(R.id.expiryErrorTV);
            address1ErrorLL = findViewById(R.id.address1ErrorLL);
            address1ErrorTV = findViewById(R.id.address1ErrorTV);
            cityErrorLL = findViewById(R.id.cityErrorLL);
            cityErrorTV = findViewById(R.id.cityErrorTV);
            stateErrorLL = findViewById(R.id.stateErrorLL);
            stateErrorTV = findViewById(R.id.stateErrorTV);
            zipErrorLL = findViewById(R.id.zipErrorLL);
            zipErrorTV = findViewById(R.id.zipErrorTV);
            clStates = findViewById(R.id.clStates);
            etlState = findViewById(R.id.etlState);
            etlAddress1 = findViewById(R.id.etlAddress1);
            etlCity = findViewById(R.id.etlCity);
            etlZipCode = findViewById(R.id.etlZipCode);
            etlExpiry = findViewById(R.id.etlExpiry);
            etlName = findViewById(R.id.etlName);
            etlAddress2 = findViewById(R.id.etlAddress2);
            etAddress1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            etAddress2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            etCity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

            etlName.setBoxStrokeColorStateList(Utils.getNormalColorState());
            etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState());
            etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState());
            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState());
            etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState());
            etlState.setBoxStrokeColorStateList(Utils.getNormalColorState());
            etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState());

            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            etName.setEnabled(false);
//            etExpiry.setEnabled(false);
            etlCard.disableEditText();
            etlCard.enableHint();
            etlCard.setFrom("EDIT_CARD");
            if (selectedCard != null) {
                etName.setText(Utils.capitalize(selectedCard.getName()));
                etlCard.setText(selectedCard.getFirstSix().replace(" ", "").replaceAll("(.{4})", "$1 ").trim() + " ****" + selectedCard.getLastFour());
                etExpiry.setText(selectedCard.getExpiryDate());
                etAddress1.setText(selectedCard.getAddressLine1());
                etAddress2.setText(selectedCard.getAddressLine2());
                etCity.setText(selectedCard.getCity());
                etState.setText(selectedCard.getState());
                etZipcode.setText(selectedCard.getZipCode());
                etlCard.setImage(selectedCard.getCardBrand());
                etlCard.hideCamera();
                isAddress1 = true;
                isCity = true;
                isState = true;
                isZipcode = true;
                if (selectedCard.getExpired()) {
                    isExpiry = false;
                    etExpiry.setEnabled(true);
                    etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                } else {
                    isExpiry = true;
                    etExpiry.setEnabled(true);
                }
                enableOrDisableNext();
                switch (selectedCard.getCardBrand().toUpperCase().replace(" ", "")) {
                    case "VISA":
                        tvCard.setText(Utils.capitalize(selectedCard.getCardBrand() + " " + selectedCard.getCardType() + " ****" + selectedCard.getLastFour()));
                        break;
                    case "MASTERCARD":
                        tvCard.setText(Utils.capitalize(selectedCard.getCardBrand() + " " + selectedCard.getCardType() + " ****" + selectedCard.getLastFour()));
                        break;
                    case "AMERICANEXPRESS":
                        tvCard.setText(Utils.capitalize("American Express Card ****" + selectedCard.getLastFour()));
                        break;
                    case "DISCOVER":
                        tvCard.setText(Utils.capitalize("Discover Card ****" + selectedCard.getLastFour()));
                        break;
                }
            }
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
            }
            layoutBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    finish();
                }
            });

            clStates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(EditCardActivity.this);
                    Utils.populateStates(EditCardActivity.this, etState, objMyApplication);
                }
            });

            etState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(EditCardActivity.this);
                    Utils.populateStates(EditCardActivity.this, etState, objMyApplication);
                }
            });

            etlState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(EditCardActivity.this);
                    Utils.populateStates(EditCardActivity.this, etState, objMyApplication);
                }
            });

            cvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!selectedCard.getExpired()) {
                            Intent i = new Intent();
                            i.putExtra("screen", "editcard");
                            i.putExtra("action", "remove");
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            pDialog = Utils.showProgressDialog(EditCardActivity.this);
                            paymentMethodsViewModel.deleteCards(selectedCard.getId());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isAddEnabled) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            CardEditRequest request = new CardEditRequest();
                            request.setAddressLine1(etAddress1.getText().toString().trim());
                            request.setAddressLine2(etAddress2.getText().toString().trim());
                            request.setName(etName.getText().toString().trim());
                            request.setExpiryDate(etExpiry.getText().toString().trim());
                            request.setCity(etCity.getText().toString().trim());
                            request.setState(etState.getText().toString().trim());
                            request.setZipCode(etZipcode.getText().toString().trim());
                            request.setDefaultForAllWithDrawals(true);
                            if (etCountry.getText().toString().trim().equals("United States")) {
                                request.setCountry("US");
                            } else {
                                request.setCountry(Utils.getStrCCode());
                            }
                            dialog = Utils.showProgressDialog(EditCardActivity.this);
                            paymentMethodsViewModel.editCards(request, selectedCard.getId());
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
        paymentMethodsViewModel.getCardEditResponseMutableLiveData().observe(this, new Observer<CardEditResponse>() {
            @Override
            public void onChanged(CardEditResponse cardEditResponse) {
                dialog.dismiss();
                if (cardEditResponse != null) {
                    try {
                        if (cardEditResponse.getStatus().toLowerCase().equals("success")) {
                            Utils.showCustomToast(EditCardActivity.this, cardEditResponse.getData(), R.drawable.ic_custom_tick, "");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!strScreen.equals("withdraw") && !strScreen.equals("buy")) {
                                            objMyApplication.setSelectedCard(null);
                                        }
                                        onBackPressed();
                                        finish();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, 2000);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), EditCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), EditCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getCardDeleteResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                pDialog.dismiss();
                if (cardDeleteResponse.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(EditCardActivity.this, "Card has been removed.", R.drawable.ic_custom_tick, "");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!strScreen.equals("withdraw") && !strScreen.equals("buy")) {
                                    objMyApplication.setSelectedCard(null);
                                }
                                onBackPressed();
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, 2000);
                }
            }
        });
    }

    private void focusWatchers() {
        try {
            etExpiry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etExpiry.setHint("");
                            if (etExpiry.getText().toString().trim().length() > 0) {
                                if (validateExpiry()) {
                                    isExpiry = true;
                                    expiryErrorLL.setVisibility(GONE);
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.primary_black));
                                } else {
                                    isExpiry = false;
                                    expiryErrorLL.setVisibility(VISIBLE);
                                    expiryErrorTV.setText("Please enter a valid Expiry Date");
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                                }
                            } else {
                                isExpiry = false;
                                expiryErrorLL.setVisibility(VISIBLE);
                                expiryErrorTV.setText("Field Required");
                                etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlExpiry, getColor(R.color.light_gray));
                            }
                        } else {
                            etExpiry.setHint("MM/YY");
                            etlExpiry.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlExpiry, getColor(R.color.primary_green));
                            expiryErrorLL.setVisibility(GONE);
                        }
                        enableOrDisableNext();
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
                                etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_black));

                            } else {
                                etlAddress1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.light_gray));
                                address1ErrorLL.setVisibility(VISIBLE);
                                address1ErrorTV.setText("Field Required");
                            }
                        } else {
                            etAddress1.setHint("Billing Address Line 1");
                            etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_green));
                            etAddress1.setSelection(etAddress1.getText().length());
                            if(etAddress1.getText().length()==0){
                                address1ErrorLL.setVisibility(GONE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etAddress2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        etAddress2.setHint("Billing Address Line 2(Optional)");
                        etAddress2.setSelection(etAddress2.getText().length());
                        etlAddress2.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(etlAddress2, getColor(R.color.primary_green));
                    } else {
                        if (etAddress2.getText().length()>0) {
                            etAddress2.setHint("");
                            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(etlAddress2, getColor(R.color.primary_black));
                        }
                        else {
                            etAddress2.setHint("");
                            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(etlAddress2, getColor(R.color.light_gray));
                        }
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
                                etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlCity, getColor(R.color.primary_black));

                            } else {
                                etlCity.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlCity, getColor(R.color.light_gray));
                                cityErrorLL.setVisibility(VISIBLE);
                                cityErrorTV.setText("Field Required");
                            }
                        } else {
                            etCity.setHint("City");
                            etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlCity, getColor(R.color.primary_green));
                            etCity.setSelection(etCity.getText().length());
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
                        if (!b) {
                            if (etState.getText().toString().trim().length() > 0) {
                                stateErrorLL.setVisibility(GONE);
                                etlState.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlState, getColor(R.color.primary_black));

                            } else {
                                etlState.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlState, getColor(R.color.light_gray));
                                stateErrorLL.setVisibility(VISIBLE);
                                stateErrorTV.setText("Field Required");
                            }
                        } else {
                            etlState.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlState, getColor(R.color.primary_green));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etZipcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etZipcode.setHint("");
                            if (etZipcode.getText().toString().trim().length() > 0 && etZipcode.getText().toString().trim().length() > 4) {
                                isZipcode = true;
                                zipErrorLL.setVisibility(GONE);
                                etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_black));
                            } else if (etZipcode.getText().toString().trim().length() == 0) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.light_gray));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Field Required");
                            } else {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Minimum 5 Characters Required");
                                isZipcode = false;
                            }
                        } else {
                            etZipcode.setHint("Zip Code");
                            etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_green));
                            etZipcode.setSelection(etZipcode.getText().length());
                            zipErrorLL.setVisibility(GONE);
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

    private void textWatchers() {
        etExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 6) {
                        if (validateExpiry()) {
                            isExpiry = true;
                            expiryErrorLL.setVisibility(GONE);
                        } else {
                            isExpiry = false;
                            expiryErrorLL.setVisibility(VISIBLE);
                            expiryErrorTV.setText("Please enter a valid Expiry Date");
                        }
                    } else {
                        isExpiry = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etExpiry.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etExpiry.setText("");
                        etExpiry.setSelection(etExpiry.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etExpiry.setText(etExpiry.getText().toString().replaceAll("\\.", ""));
                        etExpiry.setSelection(etExpiry.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etExpiry.setText("");
                        etExpiry.setSelection(etExpiry.getText().length());
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
                    if (str.substring(0).equals(" ")) {
                        etAddress1.setText("");
                        etAddress1.setSelection(etAddress1.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etAddress1.setText("");
                        etAddress1.setSelection(etAddress1.getText().length());
                        address1ErrorLL.setVisibility(GONE);
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
                if (etAddress2.getText().toString().length() > 0) {
                    Utils.setUpperHintColor(etlAddress2, getColor(R.color.primary_black));
                } else {
                    Utils.setUpperHintColor(etlAddress2, getColor(R.color.light_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = etAddress2.getText().toString();
                if (str.substring(0).equals(" ")) {
                    etAddress2.setText("");
                    etAddress2.setSelection(etAddress2.getText().length());
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
//                        etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(etlCity, getResources().getColor(R.color.primary_green));
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
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etCity.setText("");
                        etCity.setSelection(etCity.getText().length());
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
                    } else {
                        isState = false;
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

        etZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() > 4) {
                        isZipcode = true;
                        zipErrorLL.setVisibility(GONE);
                    } else {
//                        zipErrorLL.setVisibility(VISIBLE);
//                        zipErrorTV.setText("Zip Code must have at least 5 numbers");
                        isZipcode = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etZipcode.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etZipcode.setText("");
                        etZipcode.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etZipcode.setText(etZipcode.getText().toString().replaceAll("\\.", ""));
                        etZipcode.setSelection(etZipcode.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etZipcode.setText("");
                        etZipcode.setSelection(etZipcode.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void enableOrDisableNext() {
        try {
            if (isAddress1 && isCity && isZipcode && isState && isExpiry) {
                isAddEnabled = true;
                cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isAddEnabled = false;
                cvSave.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayAlertNew(String msg, final Context context, String headerText) {
        try {
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView header = dialog.findViewById(R.id.tvHead);
            TextView message = dialog.findViewById(R.id.tvMessage);
            CardView actionCV = dialog.findViewById(R.id.cvAction);
            TextView actionText = dialog.findViewById(R.id.tvAction);

            if (!headerText.equals("")) {
                header.setVisibility(View.VISIBLE);
                header.setText(headerText);
            }

            actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    objMyApplication.setSelectedCard(null);
                    onBackPressed();
                    finish();
                }
            });

            message.setText(msg);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validateExpiry() {
        Boolean value = true;
        try {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            if (!etExpiry.getText().toString().equals("") && etExpiry.getText().toString().length() == 5) {
                Year year = Year.parse(etExpiry.getText().toString().split("/")[1], DateTimeFormatter.ofPattern("yy"));
                String strDate = "01/" + etExpiry.getText().toString().split("/")[0] + "/" + year.toString();
                diffMonths = objMyApplication.monthsBetweenDates(new Date(), objMyApplication.getDate(strDate));
            }
            String year = "";
            SimpleDateFormat ydf = new SimpleDateFormat("yy");
            year = ydf.format(Calendar.getInstance().getTime());
            if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) < Integer.parseInt(year)) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[0]) == 0 || Integer.parseInt(etExpiry.getText().toString().split("/")[0]) > 12) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) <= Integer.parseInt(year) && Integer.parseInt(etExpiry.getText().toString().split("/")[0]) < month) {
                value = false;
            } else if (diffMonths != -1 && diffMonths > Integer.parseInt(getString(R.string.expirydate))) {
                value = false;
            }
        } catch (Exception ex) {
            value = false;
            ex.printStackTrace();
        }
        return value;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //etExpiry.requestFocus();
            etExpiry.clearFocus();
            etAddress1.clearFocus();
            etAddress2.clearFocus();
            etCity.clearFocus();
            etZipcode.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
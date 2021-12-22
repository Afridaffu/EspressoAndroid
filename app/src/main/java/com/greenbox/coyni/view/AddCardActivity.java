package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.internal.LinkedTreeMap;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.cards.CardRequest;
import com.greenbox.coyni.model.cards.CardResponse;
import com.greenbox.coyni.model.cards.CardResponseData;
import com.greenbox.coyni.model.cards.CardTypeRequest;
import com.greenbox.coyni.model.cards.CardTypeResponse;
import com.greenbox.coyni.model.preauth.PreAuthData;
import com.greenbox.coyni.model.preauth.PreAuthRequest;
import com.greenbox.coyni.model.preauth.PreAuthResponse;
import com.greenbox.coyni.model.publickey.PublicKeyResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.encryption.AESEncrypt;
import com.greenbox.coyni.utils.encryption.EncryptRequest;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.utils.outline_et.CardNumberEditText;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;
import com.microblink.blinkcard.MicroblinkSDK;
import com.microblink.blinkcard.entities.recognizers.Recognizer;
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle;
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.blinkcard.uisettings.ActivityRunner;
import com.microblink.blinkcard.uisettings.BlinkCardUISettings;
import com.santalu.maskara.widget.MaskEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.UUID;

public class AddCardActivity extends AppCompatActivity {
    String strPublicKey = "";
    PaymentMethodsViewModel paymentMethodsViewModel;
    MyApplication objMyApplication;
    RelativeLayout layoutCard, layoutAddress;
    LinearLayout layoutClose, nameErrorLL, expiryErrorLL, cvvErrorLL, layoutExpiry;
    LinearLayout address1ErrorLL, cityErrorLL, stateErrorLL, zipErrorLL;
    public LinearLayout cardErrorLL;
    View divider1, divider2;
    TextView tvCardHead, nameErrorTV, expiryErrorTV, cvvErrorTV, address1ErrorTV, cityErrorTV, stateErrorTV, zipErrorTV;
    public TextView cardErrorTV;
    CardView cvNext, cvAddCard;
    String strName = "", strCardNo = "", strExpiry = "", strCvv = "", strAdd1 = "", strAdd2 = "", strCity = "", strState = "", strZip = "", strCountry = "";
    TextInputEditText etName, etCVV, etAddress1, etAddress2, etCity, etState, etZipCode, etCountry, etPreAmount;
    CardNumberEditText etCardNumber;
    TextInputLayout etlState, etlName, etlExpiry, etlCVV, etlAddress1, etlCity, etlZipCode;
    MaskEditText etExpiry;
    ConstraintLayout clStates;
    Long mLastClickTime = 0L;
    Dialog preDialog, preAuthDialog;
    CardResponseData cardResponseData;
    ProgressDialog progressDialog;
    public static AddCardActivity addCardActivity;
    CardTypeResponse objCard;
    Boolean isName = false, isExpiry = false, isCvv = false, isNextEnabled = false;
    Boolean isAddress1 = false, isCity = false, isState = false, isZipcode = false, isAddEnabled = false;
    public Boolean isCard = false;
    TextView tvError;
    private BlinkCardRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    CustomKeyboard ctKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_addcard);
            addCardActivity = this;
            initialization();
            textWatchers();
            focusWatchers();
            etName.setText(objMyApplication.getStrUserName());
            Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (layoutAddress.getVisibility() == View.VISIBLE) {
                layoutCard.setVisibility(View.VISIBLE);
                layoutAddress.setVisibility(View.GONE);
                divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                //etCVV.setText("");
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            layoutCard = findViewById(R.id.layoutCard);
            layoutAddress = findViewById(R.id.layoutAddress);
            layoutClose = findViewById(R.id.layoutClose);
            nameErrorLL = findViewById(R.id.nameErrorLL);
            cardErrorLL = findViewById(R.id.cardErrorLL);
            expiryErrorLL = findViewById(R.id.expiryErrorLL);
            cvvErrorLL = findViewById(R.id.cvvErrorLL);
            layoutExpiry = findViewById(R.id.layoutExpiry);
            etlName = findViewById(R.id.etlName);
            etlExpiry = findViewById(R.id.etlExpiry);
            etlCVV = findViewById(R.id.etlCVV);
            divider1 = findViewById(R.id.divider1);
            divider2 = findViewById(R.id.divider2);
            tvCardHead = findViewById(R.id.tvCardHead);
            nameErrorTV = findViewById(R.id.nameErrorTV);
            cardErrorTV = findViewById(R.id.cardErrorTV);
            expiryErrorTV = findViewById(R.id.expiryErrorTV);
            cvvErrorTV = findViewById(R.id.cvvErrorTV);
            cvNext = findViewById(R.id.cvNext);
            cvAddCard = findViewById(R.id.cvAddCard);
            etName = findViewById(R.id.etName);
            etCardNumber = findViewById(R.id.etlCard);
            etExpiry = findViewById(R.id.etExpiry);
            etCVV = findViewById(R.id.etCVV);
            etAddress1 = findViewById(R.id.etAddress1);
            etAddress2 = findViewById(R.id.etAddress2);
            etCity = findViewById(R.id.etCity);
            etState = findViewById(R.id.etState);
            etZipCode = findViewById(R.id.etZipCode);
            etCountry = findViewById(R.id.etCountry);
            clStates = findViewById(R.id.clStates);
            etlState = findViewById(R.id.etlState);
            address1ErrorLL = findViewById(R.id.address1ErrorLL);
            address1ErrorTV = findViewById(R.id.address1ErrorTV);
            cityErrorLL = findViewById(R.id.cityErrorLL);
            cityErrorTV = findViewById(R.id.cityErrorTV);
            stateErrorLL = findViewById(R.id.stateErrorLL);
            stateErrorTV = findViewById(R.id.stateErrorTV);
            zipErrorLL = findViewById(R.id.zipErrorLL);
            zipErrorTV = findViewById(R.id.zipErrorTV);
            etlAddress1 = findViewById(R.id.etlAddress1);
            etlCity = findViewById(R.id.etlCity);
            etlZipCode = findViewById(R.id.etlZipCode);
            etCardNumber.requestCNETFocus();

            etCardNumber.setFrom("ADD_CARD");
            MicroblinkSDK.setLicenseKey(Utils.blinkCardKey, this);
            mRecognizer = new BlinkCardRecognizer();
            mRecognizer.setExtractCvv(false);
            mRecognizer.setExtractIban(false);
            // bundle recognizers into RecognizerBundle
            mRecognizerBundle = new RecognizerBundle(mRecognizer);

            etAddress1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            etAddress2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            etCity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            paymentMethodsViewModel.getPublicKey(objMyApplication.getUserId());
            objMyApplication.getStates();
            if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit")) {
                tvCardHead.setText("Add New Debit Card");
            } else {
                tvCardHead.setText("Add New Credit Card");
            }

            clStates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateStates(AddCardActivity.this, etState, objMyApplication);
                }
            });

            etState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateStates(AddCardActivity.this, etState, objMyApplication);
                }
            });

            etlState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateStates(AddCardActivity.this, etState, objMyApplication);
                }
            });

            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isNextEnabled && validation()) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            layoutCard.setVisibility(View.GONE);
                            layoutAddress.setVisibility(View.VISIBLE);
                            divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                            divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                            strName = etName.getText().toString().trim();
                            strCardNo = etCardNumber.getText().toString().trim().replace(" ", "");
                            strExpiry = etExpiry.getText().toString().trim();
                            strCvv = etCVV.getText().toString().trim();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isAddEnabled) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            progressDialog = Utils.showProgressDialog(AddCardActivity.this);
                            strAdd1 = etAddress1.getText().toString().trim();
                            strAdd2 = etAddress2.getText().toString().trim();
                            strCity = etCity.getText().toString().trim();
                            strState = etState.getText().toString().trim();
                            strZip = etZipCode.getText().toString().trim();
                            strCountry = Utils.getStrCCode();
                            prepareJson();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            divider1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (layoutAddress.getVisibility() == View.VISIBLE) {
                        layoutCard.setVisibility(View.VISIBLE);
                        layoutAddress.setVisibility(View.GONE);
                        divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                    }
                }
            });

            etCardNumber.getCardReaderIVRef().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startScanning();
                }
            });

            if (objMyApplication.getMyProfile() != null) {
                etAddress1.setText(objMyApplication.getMyProfile().getData().getAddressLine1());
                etAddress2.setText(objMyApplication.getMyProfile().getData().getAddressLine2());
                etCity.setText(objMyApplication.getMyProfile().getData().getCity());
                etState.setText(objMyApplication.getMyProfile().getData().getState());
                etZipCode.setText(objMyApplication.getMyProfile().getData().getZipCode());
                isAddress1 = true;
                isCity = true;
                isState = true;
                isZipcode = true;
                enableOrDisableNext();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        paymentMethodsViewModel.getPublicKeyResponseMutableLiveData().observe(this, new Observer<PublicKeyResponse>() {
            @Override
            public void onChanged(PublicKeyResponse publicKeyResponse) {
                if (publicKeyResponse != null) {
                    strPublicKey = publicKeyResponse.getData().getPublicKey();
                }
            }
        });

        paymentMethodsViewModel.getCardResponseMutableLiveData().observe(this, new Observer<CardResponse>() {
            @Override
            public void onChanged(CardResponse cardResponse) {
                try {
                    progressDialog.dismiss();
                    if (cardResponse != null) {
                        cardResponseData = cardResponse.getData();
                        Error errData = cardResponse.getError();
                        if (errData == null || cardResponse.getStatus().toString().toLowerCase().equals("success")) {
                            if (cardResponseData.getStatus().toLowerCase().contains("authorize") || cardResponseData.getStatus().toLowerCase().contains("approve") || cardResponseData.getStatus().toLowerCase().equals("pending_settlement")) {
                                displayPreAuth();
                            } else if (cardResponseData.getStatus().toLowerCase().equals("failed") || (cardResponseData.getResponse() != null && cardResponseData.getResponse().toLowerCase().equals("declined"))) {
                                Utils.displayAlert("Card details are invalid, please try with a valid card", AddCardActivity.this, "", cardResponse.getError().getFieldErrors().get(0));
                            }
                        } else {
                            Utils.displayAlert(errData.getErrorDescription(), AddCardActivity.this, "", cardResponse.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (
                        Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(AddCardActivity.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (apiError != null) {
                        if (apiError.getError() != null) {
                            if (!apiError.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), AddCardActivity.this, "", "");
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), AddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else if (apiError.getData() != null) {
                            Utils.displayAlert(((LinkedTreeMap) apiError.getData()).get("msg").toString(), AddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getPreAuthResponseMutableLiveData().observe(this, new Observer<PreAuthResponse>() {
            @Override
            public void onChanged(PreAuthResponse preAuthResponse) {
                preDialog.dismiss();
                if (preAuthResponse != null) {
                    PreAuthData objData = preAuthResponse.getData();
                    Error errData = preAuthResponse.getError();
                    if (errData == null) {
                        if (objData.getStatus().toLowerCase().equals("success")) {
                            displayPreAuthSuccess();
                        } else {
                            displayPreAuthFail();
                        }
                    } else {
                        Utils.displayAlert(errData.getErrorDescription().toString(), AddCardActivity.this, "", preAuthResponse.getError().getFieldErrors().get(0));
                    }
                }
            }
        });

        paymentMethodsViewModel.getCardTypeResponseMutableLiveData().observe(this, new Observer<CardTypeResponse>() {
            @Override
            public void onChanged(CardTypeResponse cardTypeResponse) {
                if (cardTypeResponse != null) {
                    objCard = cardTypeResponse;
                    if (!etCardNumber.getText().toString().trim().equals("")) {
                        etCardNumber.setImage(cardTypeResponse.getData().getCardBrand());
                    }
                }
            }
        });

        paymentMethodsViewModel.getPreAuthErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (preDialog != null) {
                    preDialog.dismiss();
                }
                if (apiError != null) {
                    if (apiError.getError() != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), AddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), AddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        }
                    } else if (apiError.getData() != null) {
                        etPreAmount.setText("");
                        ctKey.clearData();
                        if (!((LinkedTreeMap) apiError.getData()).get("attempts").toString().equals("")) {
                            double value = Double.parseDouble(((LinkedTreeMap) apiError.getData()).get("attempts").toString());
                            int attempt = (int) value;
                            tvError.setVisibility(VISIBLE);
                            if (attempt != 3) {
                                if (3 - attempt == 1) {
                                    tvError.setText("Incorrect amount " + (3 - attempt) + " try left.");
                                } else {
                                    tvError.setText("Incorrect amount " + (3 - attempt) + " tries left.");
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            tvError.setVisibility(GONE);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }, 3000);
                            } else {
                                displayPreAuthFail();
                            }
                        } else if (!((LinkedTreeMap) apiError.getData()).get("msg").toString().contains("incorrect")) {
                            Utils.displayAlert(((LinkedTreeMap) apiError.getData()).get("msg").toString(), AddCardActivity.this, "", "");
                        } else {
                            displayPreAuthFail();
                        }
                    }
                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (!objCard.getData().getCardBrand().toLowerCase().contains("american") && !etCardNumber.getText().toString().equals("") && etCardNumber.getText().toString().length() < 19) {
                etCardNumber.requestCNETFocus();
                Utils.displayAlert("Invalid Card Number", AddCardActivity.this, "", "");
                return value = false;
            } else if (objCard.getData().getCardBrand().toLowerCase().contains("american") && !etCardNumber.getText().toString().equals("") && etCardNumber.getText().toString().length() != 18) {
                etCardNumber.requestFocus();
                Utils.displayAlert("Invalid Card Number", AddCardActivity.this, "", "");
                return value = false;
            } else if (etExpiry.getText().toString().equals("")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Expiry Date is required", AddCardActivity.this, "", "");
                return value = false;
            } else if (!etExpiry.getText().toString().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter valid Expiry Date", AddCardActivity.this, "", "");
                return value = false;
            } else if (!validateExpiry()) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter valid Expiry Date", AddCardActivity.this, "", "");
                return value = false;
            } else if (etCVV.getText().toString().equals("")) {
                etCVV.requestFocus();
                Utils.displayAlert("CVV is required", AddCardActivity.this, "", "");
                return value = false;
            } else if (!objCard.getData().getValid()) {
                Utils.displayAlert("Invalid request! Please check the card and try again.", AddCardActivity.this, "", "");
                return value = false;
            } else if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit") && objCard.getData().getCardType().toLowerCase().equals("credit")) {
                Utils.displayAlert("Invalid request! Please add Debit Card only.", AddCardActivity.this, "", "");
                return value = false;
            } else if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("credit") && objCard.getData().getCardType().toLowerCase().equals("debit")) {
                Utils.displayAlert("Invalid request! Please add Credit Card only.", AddCardActivity.this, "", "");
                return value = false;
            } else if (!objCard.getData().getCardBrand().toLowerCase().equals("visa") && !objCard.getData().getCardBrand().toLowerCase().contains("master") && !objCard.getData().getCardBrand().toLowerCase().contains("american") && !objCard.getData().getCardBrand().toLowerCase().contains("discover")) {
                Utils.displayAlert("GreenBox system supports only MASTERCARD, VISA, AMERICAN EXPRESS and DISCOVER", AddCardActivity.this, "", "");
                return value = false;
            } else if (!etCVV.getText().toString().equals("") && etCVV.getText().toString().length() < 3) {
                etCVV.requestFocus();
                Utils.displayAlert("Please enter valid CVV/CVC.", AddCardActivity.this, "", "");
                return value = false;
            } else if (!objCard.getData().getCardBrand().toLowerCase().equals("visa") && !objCard.getData().getCardBrand().toLowerCase().contains("master") && getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit")) {
                Utils.displayAlert("GreenBox system supports only MASTERCARD, VISA Debit cards", AddCardActivity.this, "", "");
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private Boolean validateExpiry() {
        Boolean value = true;
        try {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            String year = "";
            SimpleDateFormat ydf = new SimpleDateFormat("yy");
            year = ydf.format(Calendar.getInstance().getTime());
            if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) < Integer.parseInt(year)) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[0]) == 0 || Integer.parseInt(etExpiry.getText().toString().split("/")[0]) > 12) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) <= Integer.parseInt(year) && Integer.parseInt(etExpiry.getText().toString().split("/")[0]) < month) {
                value = false;
            }
        } catch (Exception ex) {
            value = false;
            ex.printStackTrace();
        }
        return value;
    }

    private void focusWatchers() {
        try {
            etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (etName.getText().toString().length() > 0 && !etName.getText().toString().substring(0, 1).equals(" ")) {
                            etName.setText(etName.getText().toString().substring(0, 1).toUpperCase() + etName.getText().toString().substring(1).toLowerCase());
                        }
                    }
                    return false;
                }
            });

            etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etName.setHint("");
                            if (etName.getText().toString().trim().length() > 1) {
                                isName = true;
                                nameErrorLL.setVisibility(GONE);
                                etlName.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                            } else if (etName.getText().toString().trim().length() == 1) {
                                isName = false;
                                etlName.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                                nameErrorLL.setVisibility(VISIBLE);
                                nameErrorTV.setText("Minimum 2 Characters Required");
                            } else {
                                isName = false;
                                etlName.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                                Utils.setUpperHintColor(etlName, getColor(R.color.light_gray));
                                nameErrorLL.setVisibility(VISIBLE);
                                nameErrorTV.setText("Field Required");
                            }

                            if (etName.getText().toString().length() > 0 && !etName.getText().toString().substring(0, 1).equals(" ")) {
                                etName.setText(etName.getText().toString().substring(0, 1).toUpperCase() + etName.getText().toString().substring(1).toLowerCase());
                            }

                        } else {
                            etName.setHint("Name on card");
                            etlName.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlName, getColor(R.color.primary_green));
                            InputMethodManager imm = (InputMethodManager) AddCardActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(etName, 0);
                        }
                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

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
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                                    expiryErrorLL.setVisibility(VISIBLE);
                                    expiryErrorTV.setText("Please enter valid Expiry Date");
                                }
                            } else {
                                isExpiry = false;
                                etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                                Utils.setUpperHintColor(etlExpiry, getColor(R.color.light_gray));
                                expiryErrorLL.setVisibility(VISIBLE);
                                expiryErrorTV.setText("Field Required");
                            }
                        } else {
                            etExpiry.setHint("MM/YY");
                            etlExpiry.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlExpiry, getColor(R.color.primary_green));
                        }
                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etCVV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etCVV.setHint("");
                            if (etCVV.getText().toString().trim().length() < 3) {
                                isCvv = false;
                                etlCVV.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                cvvErrorLL.setVisibility(VISIBLE);
                                if (etCVV.getText().toString().trim().length() == 0) {
                                    cvvErrorTV.setText("Field Required");
                                    Utils.setUpperHintColor(etlCVV, getColor(R.color.light_gray));
                                } else {
                                    cvvErrorTV.setText("Please enter valid CVV");
                                    Utils.setUpperHintColor(etlCVV, getColor(R.color.error_red));
                                }
                            } else {
                                isCvv = true;
                                cvvErrorLL.setVisibility(GONE);
                                etlCVV.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlCVV, getColor(R.color.primary_black));
                            }
                        } else {
                            etCVV.setHint("123");
                            etlCVV.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlCVV, getColor(R.color.primary_green));
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
                            if (etAddress1.getText().toString().trim().length() > 0) {
                                address1ErrorLL.setVisibility(GONE);
                                etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_black));

                            } else {
                                etlAddress1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.error_red));
                                address1ErrorLL.setVisibility(VISIBLE);
                                address1ErrorTV.setText("Field Required");
                            }
                        } else {
                            etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_green));
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
                            if (etCity.getText().toString().trim().length() > 0) {
                                cityErrorLL.setVisibility(GONE);
                                etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlCity, getColor(R.color.primary_black));

                            } else {
                                etlCity.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlCity, getColor(R.color.error_red));
                                cityErrorLL.setVisibility(VISIBLE);
                                cityErrorTV.setText("Field Required");
                            }
                        } else {
                            etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlCity, getColor(R.color.primary_green));
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
                                Utils.setUpperHintColor(etlState, getColor(R.color.error_red));
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

            etZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            if (etZipCode.getText().toString().trim().length() == 5) {
                                zipErrorLL.setVisibility(GONE);
                                etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_black));

                            } else if (etZipCode.getText().toString().trim().length() > 0 && etZipCode.getText().toString().trim().length() < 5) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Minimum 5 Characters Required");
                            } else if (etZipCode.getText().toString().trim().length() == 0) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Field Required");
                            }
//                            else {
//                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
//                                zipErrorLL.setVisibility(VISIBLE);
//                                zipErrorTV.setText("Zip Code must have at least 5 numbers");
//                                isZipcode = false;
//                            }
                        } else {
                            etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_green));
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
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
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
                        etlName.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                    }
                    String str = etName.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        etName.setText("");
                        etName.setSelection(etName.getText().length());
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

        etExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 6) {
                        isExpiry = true;
                        expiryErrorLL.setVisibility(GONE);
                        etlExpiry.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlExpiry, getResources().getColor(R.color.primary_green));
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

        etCVV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 2 && charSequence.toString().trim().length() < 5) {
                        isCvv = true;
                        cvvErrorLL.setVisibility(GONE);
                        etlCVV.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlCVV, getResources().getColor(R.color.primary_green));
                    } else {
                        isCvv = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etCVV.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etCVV.setText("");
                        etCVV.setSelection(etCVV.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etCVV.setText(etCVV.getText().toString().replaceAll("\\.", ""));
                        etCVV.setSelection(etCVV.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etCVV.setText("");
                        etCVV.setSelection(etCVV.getText().length());
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
//                        zipErrorLL.setVisibility(VISIBLE);
//                        zipErrorTV.setText("Zip Code must have at least 5 numbers");
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
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
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
            if (isName && isCard && isExpiry && isCvv) {
                isNextEnabled = true;
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isNextEnabled = false;
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
            if (isAddress1 && isCity && isZipcode && isState) {
                isAddEnabled = true;
                cvAddCard.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isAddEnabled = false;
                cvAddCard.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void prepareJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("addressLine1", strAdd1);
            jsonObject.put("addressLine2", strAdd2);
            jsonObject.put("cardNumber", strCardNo);
            jsonObject.put("city", strCity);
            jsonObject.put("country", strCountry);
            jsonObject.put("cvc", strCvv);
            jsonObject.put("defaultForAllWithDrawals", true);
            jsonObject.put("expiryDate", strExpiry);
            jsonObject.put("name", strName);
            jsonObject.put("state", strState);
            jsonObject.put("zipCode", strZip);
            String strUUID = UUID.randomUUID().toString();
            EncryptRequest encrypt = AESEncrypt.encryptPayload(strUUID, jsonObject.toString(), strPublicKey);
            if (encrypt != null) {
                CardRequest request = new CardRequest();
                request.setKey(Base64.getEncoder().encodeToString(encrypt.getEncryptKey()));
                request.setPayload(encrypt.getEncryptData());
                paymentMethodsViewModel.saveCards(request);
            } else {
                progressDialog.dismiss();
            }
        } catch (Exception ex) {
            progressDialog.dismiss();
            ex.printStackTrace();
        }
    }

    private void displayPreAuth() {
        try {
            LinearLayout layoutPClose;
            TextView tvMessage;
            preAuthDialog = new Dialog(AddCardActivity.this, R.style.DialogTheme);
            preAuthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preAuthDialog.setContentView(R.layout.preauthorization);
            Window window = preAuthDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            preAuthDialog.getWindow().setAttributes(lp);
            preAuthDialog.show();
            layoutPClose = preAuthDialog.findViewById(R.id.layoutPClose);
            tvMessage = preAuthDialog.findViewById(R.id.tvMessage);
            tvError = preAuthDialog.findViewById(R.id.tvError);
            etPreAmount = preAuthDialog.findViewById(R.id.etAmount);
            ctKey = preAuthDialog.findViewById(R.id.ckb);
            ctKey.setKeyAction("Verify");
            ctKey.setScreenName("addcard");
            ctKey.disableButton();
            InputConnection ic = etPreAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            tvMessage.setText("A temporary hold was placed on your card and will be removed by the end of this verification process. Please check your Bank/Card statement for a charge from " + cardResponseData.getDescriptorName() + " and enter the amount below.");
            etPreAmount.setShowSoftInputOnFocus(false);
            etPreAmount.setEnabled(false);
            layoutPClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preAuthDialog.dismiss();
                }
            });
            etPreAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(AddCardActivity.this, v);
                }
            });
            etPreAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Utils.hideSoftKeypad(AddCardActivity.this, view);
                }
            });
            etPreAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0) {
                        ctKey.enableButton();
                    } else {
                        ctKey.disableButton();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void verifyClick() {
        try {
            if (!etPreAmount.getText().toString().trim().equals("")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                displayAuthorization();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("amount", etPreAmount.getText().toString().trim());
                jsonObject.put("transactionId", cardResponseData.getTransactionId());
                jsonObject.put("name", strName);
                jsonObject.put("state", strState);
                jsonObject.put("zipCode", strZip);
                jsonObject.put("city", strCity);
                jsonObject.put("country", strCountry);
                jsonObject.put("addressLine1", strAdd1);
                jsonObject.put("addressLine2", strAdd2);
                jsonObject.put("cardNumber", strCardNo);
                String strUUID = UUID.randomUUID().toString();
                EncryptRequest encrypt = AESEncrypt.encryptPayload(strUUID, jsonObject.toString(), strPublicKey);
                if (encrypt != null) {
                    PreAuthRequest request = new PreAuthRequest();
                    request.setKey(Base64.getEncoder().encodeToString(encrypt.getEncryptKey()));
                    request.setPayload(encrypt.getEncryptData());
                    paymentMethodsViewModel.preAuthVerify(request);
                } else {
                    preDialog.dismiss();
                }
            } else {
                Utils.displayAlert("Please enter Amount", AddCardActivity.this, "", "");
            }
        } catch (Exception ex) {
            preDialog.dismiss();
            ex.printStackTrace();
        }
    }

    private void displayPreAuthSuccess() {
        try {
            CardView cvDone;
            preAuthDialog.dismiss();
            preDialog = new Dialog(AddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.activity_all_done_card);
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

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //preDialog.dismiss();
//                        Intent i = new Intent(AddCardActivity.this, PaymentMethodsActivity.class);
//                        startActivity(i);
//                        finish();
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

    private void displayPreAuthFail() {
        try {
            CardView cvAddBank;
            preDialog = new Dialog(AddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.preauthfailed);
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
            cvAddBank = preDialog.findViewById(R.id.cvAddBank);

            cvAddBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preDialog.dismiss();
                    onBackPressed();
                    finish();
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

    private void displayAuthorization() {
        try {
            CircularProgressIndicator cpProgress;
            preDialog = new Dialog(AddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.authorization);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            preDialog.getWindow().setAttributes(lp);
            preDialog.show();
            cpProgress = preDialog.findViewById(R.id.cpProgress);
            cpProgress.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getCardype(String strCard) {
        try {
            CardTypeRequest request = new CardTypeRequest();
            request.setCardNumber(strCard.toString().replace(" ", ""));
            paymentMethodsViewModel.cardType(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clearControls() {
        try {
            etExpiry.setText("");
            etCVV.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // method within MyActivity from previous step
    public void startScanning() {
        // Settings for BlinkCardActivity
        BlinkCardUISettings settings = new BlinkCardUISettings(mRecognizerBundle);
        // tweak settings as you wish
        // Start activity
        ActivityRunner.startActivityForResult(this, 123, settings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                mRecognizerBundle.loadFromIntent(data);
                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                // you can get the result by invoking getResult on recognizer
                BlinkCardRecognizer.Result result = mRecognizer.getResult();
                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    // result is valid, you can use it however you wish
                    Log.e("number", result.getCardNumber());
                    Log.e("owner", result.getOwner());
                    Log.e("number", result.getExpiryDate().toString());
                    result.getCardNumber();
                    etCardNumber.setText(result.getCardNumber());
                    etCardNumber.setSelection();
                    etCardNumber.removeError();
                    etCardNumber.requestCNETFocus();
                    cardErrorLL.setVisibility(GONE);
                }
            }
        }
    }
}
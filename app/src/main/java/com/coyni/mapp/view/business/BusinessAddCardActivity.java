package com.coyni.mapp.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.internal.LinkedTreeMap;
import com.coyni.mapp.R;
import com.coyni.mapp.intro_slider.AutoScrollViewPager;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.Error;
import com.coyni.mapp.model.cards.CardResponse;
import com.coyni.mapp.model.cards.CardResponseData;
import com.coyni.mapp.model.cards.CardTypeRequest;
import com.coyni.mapp.model.cards.CardTypeResponse;
import com.coyni.mapp.model.preauth.PreAuthData;
import com.coyni.mapp.model.preauth.PreAuthResponse;
import com.coyni.mapp.model.publickey.PublicKeyResponse;
import com.coyni.mapp.utils.MaskEditText.widget.MaskEditText;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.encryption.AESEncrypt;
import com.coyni.mapp.utils.encryption.EncryptRequest;
import com.coyni.mapp.utils.keyboards.CustomKeyboard;
import com.coyni.mapp.utils.outline_et.CardNumberEditText;
import com.coyni.mapp.viewmodel.PaymentMethodsViewModel;
//import com.microblink.blinkcard.MicroblinkSDK;
//import com.microblink.blinkcard.entities.recognizers.Recognizer;
//import com.microblink.blinkcard.entities.recognizers.RecognizerBundle;
//import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer;
//import com.microblink.blinkcard.uisettings.ActivityRunner;
//import com.microblink.blinkcard.uisettings.BlinkCardUISettings;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class BusinessAddCardActivity extends AppCompatActivity {
    String strPublicKey = "";
    PaymentMethodsViewModel paymentMethodsViewModel;
    MyApplication objMyApplication;
    RelativeLayout layoutCard, layoutAddress;
    LinearLayout layoutClose, nameErrorLL, expiryErrorLL, layoutExpiry;
    LinearLayout address1ErrorLL, cityErrorLL, stateErrorLL, zipErrorLL;
    public LinearLayout cardErrorLL;
    View divider1, divider2;
    TextView tvCardHead, nameErrorTV, expiryErrorTV, address1ErrorTV, cityErrorTV, stateErrorTV, zipErrorTV;
    public TextView cardErrorTV;
    CardView cvNext, cvAddCard;
    String strName = "", strCardNo = "", strExpiry = "", strAdd1 = "", strAdd2 = "", strCity = "", strState = "", strZip = "", strCountry = "";
    TextInputEditText etName, etAddress1, etAddress2, etCity, etState, etZipCode, etCountry, etPreAmount;
    CardNumberEditText etCardNumber;
    TextInputLayout etlState, etlName, etlExpiry, etlAddress1, etlAddress2, etlCity, etlZipCode,etlAmount;
    MaskEditText etExpiry;
    ConstraintLayout clStates;
    Long mLastClickTime = 0L;
    Dialog preDialog, preAuthDialog;
    CardResponseData cardResponseData;
    Dialog progressDialog;
    public static BusinessAddCardActivity businessaddCardActivity;
    CardTypeResponse objCard;
    Boolean isName = false, isExpiry = false, isNextEnabled = false;
    Boolean isAddress1 = false, isCity = false, isState = false, isZipcode = false, isAddEnabled = false;
    public Boolean isCard = false, isScan = false, isCardClear = false, isLicense = false;
    TextView tvError;
//    private BlinkCardRecognizer mRecognizer;
//    private RecognizerBundle mRecognizerBundle;
    CustomKeyboard ctKey;

    IdentityPagerAdapter identityPagerAdapter;
    static AutoScrollViewPager viewPager;
    int pagerPosition = 0, diffMonths = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_business_add_card);
            businessaddCardActivity = this;
            identityPagerAdapter = new IdentityPagerAdapter();
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(identityPagerAdapter);
            viewPager.setPagingEnabled(false);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.e("onPageScrolled", "onPageScrolled " + position);

                }

                @Override
                public void onPageSelected(int position) {
                    pagerPosition = position;
                    if (position == 0) {
                        divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                    } else if (position == 1) {
                        divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            initialization();
            textWatchers();
            focusWatchers();
            //etName.setText(objMyApplication.getStrUserName());
//            Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (pagerPosition == 1) {
//                layoutCard.setVisibility(View.VISIBLE);
//                layoutAddress.setVisibility(View.GONE);
                viewPager.setCurrentItem(0);
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
            layoutCard = findViewById(R.id.b_layoutCard);
            layoutAddress = findViewById(R.id.b_layoutAddress);
            layoutClose = findViewById(R.id.b_layoutClose);
            nameErrorLL = findViewById(R.id.b_nameErrorLL);
            cardErrorLL = findViewById(R.id.b_cardErrorLL);
            expiryErrorLL = findViewById(R.id.b_expiryErrorLL);
            layoutExpiry = findViewById(R.id.b_layoutExpiry);
            etlName = findViewById(R.id.b_etlName);
            etlExpiry = findViewById(R.id.b_etlExpiry);
            divider1 = findViewById(R.id.b_divider1);
            divider2 = findViewById(R.id.b_divider2);
            tvCardHead = findViewById(R.id.b_tvCardHead);
            nameErrorTV = findViewById(R.id.b_nameErrorTV);
            cardErrorTV = findViewById(R.id.b_cardErrorTV);
            expiryErrorTV = findViewById(R.id.b_expiryErrorTV);
            cvNext = findViewById(R.id.b_cvNext);
            cvAddCard = findViewById(R.id.b_cvAddCard);
            etName = findViewById(R.id.b_etName);
            etCardNumber = findViewById(R.id.b_etlCard);
            etExpiry = findViewById(R.id.b_etExpiry);
            etAddress1 = findViewById(R.id.b_etAddress1);
            etAddress2 = findViewById(R.id.b_etAddress2);
            etCity = findViewById(R.id.b_etCity);
            etState = findViewById(R.id.b_etState);
            etZipCode = findViewById(R.id.b_etZipCode);
            etCountry = findViewById(R.id.b_etCountry);
            clStates = findViewById(R.id.b_clStates);
            etlState = findViewById(R.id.b_etlState);
            address1ErrorLL = findViewById(R.id.b_address1ErrorLL);
            address1ErrorTV = findViewById(R.id.b_address1ErrorTV);
            cityErrorLL = findViewById(R.id.b_cityErrorLL);
            cityErrorTV = findViewById(R.id.b_cityErrorTV);
            stateErrorLL = findViewById(R.id.b_stateErrorLL);
            stateErrorTV = findViewById(R.id.b_stateErrorTV);
            zipErrorLL = findViewById(R.id.b_zipErrorLL);
            zipErrorTV = findViewById(R.id.b_zipErrorTV);
            etlAddress1 = findViewById(R.id.b_etlAddress1);
            etlAddress2 = findViewById(R.id.b_etlAddress2);
            etlCity = findViewById(R.id.b_etlCity);
            etlZipCode = findViewById(R.id.b_etlZipCode);


//            etCardNumber.requestCNETFocus();
            etName.requestFocus();
            etCardNumber.setFrom("ADD_CARD");
            etName.setHint("Name on Card");
//            try {
//                MicroblinkSDK.setLicenseKey(Utils.blinkCardKey, this);
//                mRecognizer = new BlinkCardRecognizer();
//                mRecognizer.setExtractCvv(false);
//                mRecognizer.setExtractIban(false);
//                // bundle recognizers into RecognizerBundle
//                mRecognizerBundle = new RecognizerBundle(mRecognizer);
//            } catch (Exception ex) {
//                if (ex.toString().toLowerCase().contains("invalidlicencekeyexception")) {
//                    isLicense = true;
//                }
//                ex.printStackTrace();
//            }
            etAddress1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            etAddress2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            etCity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
//            paymentMethodsViewModel.getPublicKey(objMyApplication.getUserId());
            paymentMethodsViewModel.getPublicKey(objMyApplication.getLoginUserId());
            objMyApplication.getStates();
            if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit")) {
                tvCardHead.setText("Add New Debit Card");
            } else {
                tvCardHead.setText("Add New Credit Card");
            }

            etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));


            etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
            etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
            etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
            etlState.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
            etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));

            clStates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(BusinessAddCardActivity.this);
                    Utils.populateStates(BusinessAddCardActivity.this, etState, objMyApplication);
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
                        Utils.hideKeypad(BusinessAddCardActivity.this);
                    Utils.populateStates(BusinessAddCardActivity.this, etState, objMyApplication);
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
                        Utils.hideKeypad(BusinessAddCardActivity.this);
                    Utils.populateStates(BusinessAddCardActivity.this, etState, objMyApplication);
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
//                            layoutCard.setVisibility(View.GONE);
//                            layoutAddress.setVisibility(View.VISIBLE);
                            viewPager.setCurrentItem(1);
                            divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                            divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                            strName = etName.getText().toString().trim();
                            strCardNo = etCardNumber.getText().toString().trim().replace(" ", "");
                            strExpiry = etExpiry.getText().toString().trim();

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
                            progressDialog = Utils.showProgressDialog(BusinessAddCardActivity.this);
                            strAdd1 = etAddress1.getText().toString().trim();
                            strAdd2 = etAddress2.getText().toString().trim();
                            strCity = etCity.getText().toString().trim();
                            strState = etState.getText().toString().trim();
                            strZip = etZipCode.getText().toString().trim();
                            strCountry = Utils.getStrCCode();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    prepareJson();
                                }
                            }, 100);
//                            prepareJson();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            divider1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (pagerPosition == 1) {
                            viewPager.setCurrentItem(0);
                            divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                            divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            divider2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (pagerPosition == 0 && isNextEnabled) {
                            viewPager.setCurrentItem(1);
                            divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                            divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etCardNumber.getCardReaderIVRef().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (!isLicense) {
//                        startScanning();
//                    } else {
//                        Utils.hideKeypad(BusinessAddCardActivity.this, view);
//                        Utils.displayAlert("License has expired", BusinessAddCardActivity.this, "", "");
//                    }
                }
            });

//            if (objMyApplication.getMyProfile() != null) {
//                etAddress1.setText(objMyApplication.getMyProfile().getData().getAddressLine1());
//                etAddress2.setText(objMyApplication.getMyProfile().getData().getAddressLine2());
//                etCity.setText(objMyApplication.getMyProfile().getData().getCity());
//                etState.setText(objMyApplication.getMyProfile().getData().getState());
//                etZipCode.setText(objMyApplication.getMyProfile().getData().getZipCode());
//                if (!etAddress1.getText().toString().trim().equals("") && !etCity.getText().toString().trim().equals("") &&
//                        !etState.getText().toString().trim().equals("") && !etZipCode.getText().toString().trim().equals("")) {
//                    isAddress1 = true;
//                    isCity = true;
//                    isState = true;
//                    isZipcode = true;
//                }
//                enableOrDisableNext();
//            }
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
//                                Utils.displayAlert("Card details are invalid, please try with a valid card", AddCardActivity.this, "", cardResponse.getError().getFieldErrors().get(0));
                                displayAlert("Card details are invalid, please try with a valid card", "");
                            }
                        } else {
//                            Utils.displayAlert(errData.getErrorDescription(), AddCardActivity.this, "", cardResponse.getError().getFieldErrors().get(0));
                            if (errData != null && !errData.getErrorDescription().equals("")) {
                                displayAlert(errData.getErrorDescription(), "");
                            } else {
                                displayAlert(cardResponse.getError().getFieldErrors().get(0), "");
                            }
                        }
                    }
                } catch (
                        Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(BusinessAddCardActivity.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (apiError != null) {
                        if (apiError.getError() != null) {
                            if (!apiError.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BusinessAddCardActivity.this, "", "");
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BusinessAddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else if (apiError.getData() != null) {
                            Utils.displayAlert(((LinkedTreeMap) apiError.getData()).get("msg").toString(), BusinessAddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
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
                        Utils.displayAlert(errData.getErrorDescription().toString(), BusinessAddCardActivity.this, "", preAuthResponse.getError().getFieldErrors().get(0));
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
                        if (isScan) {
                            isScan = false;
                            if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit") && objCard.getData().getCardType().toLowerCase().equals("credit")) {
                                Utils.displayAlert("Invalid request! Please add Debit Card only.", BusinessAddCardActivity.this, "", "");
                                etCardNumber.setText("");
                                etCardNumber.hideBrandIcon();
                            } else if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("credit") && objCard.getData().getCardType().toLowerCase().equals("debit")) {
                                Utils.displayAlert("Invalid request! Please add Credit Card only.", BusinessAddCardActivity.this, "", "");
                                etCardNumber.setText("");
                                etCardNumber.hideBrandIcon();
                            } else if (!objCard.getData().getCardBrand().toLowerCase().equals("visa") && !objCard.getData().getCardBrand().toLowerCase().contains("master") && !objCard.getData().getCardBrand().toLowerCase().contains("american") && !objCard.getData().getCardBrand().toLowerCase().contains("discover")) {
                                Utils.displayAlert("coyni system supports only MASTERCARD, VISA, AMERICAN EXPRESS and DISCOVER", BusinessAddCardActivity.this, "", "");
                                etCardNumber.setText("");
                                etCardNumber.hideBrandIcon();
                            }
                        }
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
                            Utils.displayAlert(apiError.getError().getErrorDescription(), BusinessAddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BusinessAddCardActivity.this, "", apiError.getError().getFieldErrors().get(0));
                        }
                    } else if (apiError.getData() != null) {
                        etPreAmount.setText("");
                        ctKey.clearData();
                        if (!((LinkedTreeMap) apiError.getData()).get("attempts").toString().equals("")) {
                            double value = Utils.doubleParsing(((LinkedTreeMap) apiError.getData()).get("attempts").toString());
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
                            Utils.displayAlert(((LinkedTreeMap) apiError.getData()).get("msg").toString(), BusinessAddCardActivity.this, "", "");
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
                Utils.displayAlert("Invalid Card Number", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (objCard.getData().getCardBrand().toLowerCase().contains("american") && !etCardNumber.getText().toString().equals("") && etCardNumber.getText().toString().length() != 18) {
                etCardNumber.requestFocus();
                Utils.displayAlert("Invalid Card Number", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (etExpiry.getText().toString().equals("")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Expiry Date is required", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (!etExpiry.getText().toString().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter a valid Expiry Date", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (!validateExpiry()) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter a valid Expiry Date", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (!objCard.getData().getValid()) {
                Utils.displayAlert("Invalid request! Please check the card and try again.", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit") && objCard.getData().getCardType().toLowerCase().equals("credit")) {
                Utils.displayAlert("Invalid request! Please add Debit Card only.", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("credit") && objCard.getData().getCardType().toLowerCase().equals("debit")) {
                Utils.displayAlert("Invalid request! Please add Credit Card only.", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (!objCard.getData().getCardBrand().toLowerCase().equals("visa") && !objCard.getData().getCardBrand().toLowerCase().contains("master") && !objCard.getData().getCardBrand().toLowerCase().contains("american") && !objCard.getData().getCardBrand().toLowerCase().contains("discover")) {
                Utils.displayAlert("coyni system supports only MASTERCARD, VISA, AMERICAN EXPRESS and DISCOVER", BusinessAddCardActivity.this, "", "");
                return value = false;
            } else if (!objCard.getData().getCardBrand().toLowerCase().equals("visa") && !objCard.getData().getCardBrand().toLowerCase().contains("master") && getIntent().getStringExtra("card") != null && getIntent().getStringExtra("card").equals("debit")) {
                Utils.displayAlert("coyni system supports only MASTERCARD, VISA Debit cards", BusinessAddCardActivity.this, "", "");
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
            if (!etExpiry.getText().toString().equals("") && etExpiry.getText().toString().length() == 5) {
                Year year = Year.parse(etExpiry.getText().toString().split("/")[1], DateTimeFormatter.ofPattern("yy"));
                String strDate = "01/" + etExpiry.getText().toString().split("/")[0] + "/" + year.toString();
                diffMonths = objMyApplication.monthsBetweenDates(new Date(), getDate(strDate));
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

    private Date getDate(String date) {
        Date dtExpiry = null;
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
            dtExpiry = spf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dtExpiry;
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
                                etlName.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                            } else if (etName.getText().toString().trim().length() == 1) {
                                isName = false;
                                etlName.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                                nameErrorLL.setVisibility(VISIBLE);
                                nameErrorTV.setText("Minimum 2 Characters Required");
                            } else {
                                isName = false;
                                etlName.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
//                                Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                                Utils.setUpperHintColor(etlName, getColor(R.color.light_gray));
                                nameErrorLL.setVisibility(VISIBLE);
                                nameErrorTV.setText("Field Required");
                            }

                            if (etName.getText().toString().length() > 0 && !etName.getText().toString().substring(0, 1).equals(" ")) {
                                etName.setText(etName.getText().toString().substring(0, 1).toUpperCase() + etName.getText().toString().substring(1));
                            }

                        } else {
//                            etName.setHint("Name on Card");
                            etlName.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlName, getColor(R.color.primary_green));
                            InputMethodManager imm = (InputMethodManager) BusinessAddCardActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);
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
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.primary_black));
                                } else {
                                    isExpiry = false;
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                                    expiryErrorLL.setVisibility(VISIBLE);
                                    expiryErrorTV.setText("Please enter a valid Expiry Date");
                                }
                            } else {
                                isExpiry = false;
                                etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
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


            etAddress1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etAddress1.setHint("");
                            if (etAddress1.getText().toString().trim().length() > 0) {
                                address1ErrorLL.setVisibility(GONE);
                                etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_black));

                            } else {
                                etlAddress1.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.light_gray));
                                address1ErrorLL.setVisibility(VISIBLE);
                                address1ErrorTV.setText("Field Required");
                            }
                        } else {
//                            etAddress1.setHint("Billing Address Line 1");
                            etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_green));
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
                                etlAddress2.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlAddress2, getColor(R.color.primary_black));

                            } else {
                                etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlAddress2, getColor(R.color.light_gray));
                            }
                        } else {
//                            etAddress2.setHint("Billing Address Line 2 (Optional)");
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
                                etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlCity, getColor(R.color.primary_black));

                            } else {
                                etlCity.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlCity, getColor(R.color.light_gray));
                                cityErrorLL.setVisibility(VISIBLE);
                                cityErrorTV.setText("Field Required");
                            }
                        } else {
//                            etCity.setHint("City");
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
                                etlState.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlState, getColor(R.color.primary_black));

                            } else {
                                etlState.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
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
                            etZipCode.setHint("");
                            if (etZipCode.getText().toString().trim().length() == 5) {
                                zipErrorLL.setVisibility(GONE);
                                etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_black));

                            } else if (etZipCode.getText().toString().trim().length() > 0 && etZipCode.getText().toString().trim().length() < 5) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Minimum 5 Digits Required");
                            } else if (etZipCode.getText().toString().trim().length() == 0) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState(BusinessAddCardActivity.this));
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.light_gray));
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
//                            etZipCode.setHint("Zip Code");
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
                        etlName.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
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
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 6 && validateExpiry()) {
                        isExpiry = true;
                        expiryErrorLL.setVisibility(GONE);
                        etlExpiry.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlExpiry, getResources().getColor(R.color.primary_green));
                    } else {
                        isExpiry = false;
                        if (charSequence.toString().trim().length() == 0 && isCardClear) {
                            expiryErrorLL.setVisibility(GONE);
                            etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState(BusinessAddCardActivity.this));
                            Utils.setUpperHintColor(etlExpiry, getResources().getColor(R.color.light_gray));
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
            if (isName && isCard && isExpiry) {
                isNextEnabled = true;
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                viewPager.setPagingEnabled(true);
            } else {
                isNextEnabled = false;
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                viewPager.setPagingEnabled(false);
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
            jsonObject.put("defaultForAllWithDrawals", true);
            jsonObject.put("expiryDate", strExpiry);
            jsonObject.put("name", strName);
            jsonObject.put("state", strState);
            jsonObject.put("zipCode", strZip);
            String strUUID = UUID.randomUUID().toString();
            EncryptRequest encrypt = AESEncrypt.encryptPayload(strUUID, jsonObject.toString(), strPublicKey);
            if (encrypt != null) {
//                CardRequest request = new CardRequest();
//                request.setKey(Base64.getEncoder().encodeToString(encrypt.getEncryptKey()));
//                request.setPayload(encrypt.getEncryptData());
//                paymentMethodsViewModel.saveCards(request);
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
            preAuthDialog = new Dialog(BusinessAddCardActivity.this, R.style.DialogTheme);
            preAuthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preAuthDialog.setContentView(R.layout.preauthorization);
            Window window = preAuthDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            preAuthDialog.getWindow().setAttributes(lp);
            preAuthDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

            preAuthDialog.show();
            layoutPClose = preAuthDialog.findViewById(R.id.layoutPClose);
            tvMessage = preAuthDialog.findViewById(R.id.tvMessage);
            tvError = preAuthDialog.findViewById(R.id.tvError);
            etPreAmount = preAuthDialog.findViewById(R.id.etAmount);
            etlAmount = preAuthDialog.findViewById(R.id.etlAmuont);
            ctKey = preAuthDialog.findViewById(R.id.ckb);
            ctKey.setKeyAction("Verify", BusinessAddCardActivity.this);
            ctKey.setScreenName("addcard");
            ctKey.disableButton();
            InputConnection ic = etPreAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            tvMessage.setText("A temporary hold was placed on your card and will be removed by the end of this verification process. Please check your Bank/Card statement for a charge from " + cardResponseData.getDescriptorName().toLowerCase() + " and enter the amount below.");
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
                    Utils.hideSoftKeypad(BusinessAddCardActivity.this, v);
                }
            });
            etPreAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Utils.hideSoftKeypad(BusinessAddCardActivity.this, view);
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
                        ctKey.clearData();
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
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
//                                PreAuthRequest request = new PreAuthRequest();
//                                request.setKey(Base64.getEncoder().encodeToString(encrypt.getEncryptKey()));
//                                request.setPayload(encrypt.getEncryptData());
//                                paymentMethodsViewModel.preAuthVerify(request);
                            } else {
                                preDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 100);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("amount", etPreAmount.getText().toString().trim());
//                jsonObject.put("transactionId", cardResponseData.getTransactionId());
//                jsonObject.put("name", strName);
//                jsonObject.put("state", strState);
//                jsonObject.put("zipCode", strZip);
//                jsonObject.put("city", strCity);
//                jsonObject.put("country", strCountry);
//                jsonObject.put("addressLine1", strAdd1);
//                jsonObject.put("addressLine2", strAdd2);
//                jsonObject.put("cardNumber", strCardNo);
//                String strUUID = UUID.randomUUID().toString();
//                EncryptRequest encrypt = AESEncrypt.encryptPayload(strUUID, jsonObject.toString(), strPublicKey);
//                if (encrypt != null) {
//                    PreAuthRequest request = new PreAuthRequest();
//                    request.setKey(Base64.getEncoder().encodeToString(encrypt.getEncryptKey()));
//                    request.setPayload(encrypt.getEncryptData());
//                    paymentMethodsViewModel.preAuthVerify(request);
//                } else {
//                    preDialog.dismiss();
//                }
            } else {
                Utils.displayAlert("Please enter Amount", BusinessAddCardActivity.this, "", "");
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
            preDialog = new Dialog(BusinessAddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.activity_all_done_card);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            preDialog.getWindow().setAttributes(lp);
            preDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
            preDialog.setCancelable(false);
            preDialog.show();
            cvDone = preDialog.findViewById(R.id.cvDone);

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        objMyApplication.setCardSave(true);
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
            if (preAuthDialog != null) {
                preAuthDialog.dismiss();
            }
            CardView cvAddBank;
            preDialog = new Dialog(BusinessAddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.preauthfailed);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            preDialog.getWindow().setAttributes(lp);
            preDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

            preDialog.setCancelable(false);
            preDialog.show();
            cvAddBank = preDialog.findViewById(R.id.cvAddBank);

            cvAddBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //preDialog.dismiss();
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
            preDialog = new Dialog(BusinessAddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.authorization);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            preDialog.getWindow().setAttributes(lp);
            preDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            isCardClear = true;
            etExpiry.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayAlert(String msg, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(BusinessAddCardActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = getResources().getDisplayMetrics();
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
                try {
                    dialog.dismiss();
                    if (layoutAddress.getVisibility() == View.VISIBLE) {
//                        layoutCard.setVisibility(View.VISIBLE);
//                        layoutAddress.setVisibility(View.GONE);
                        viewPager.setCurrentItem(0);
                        divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                        etCardNumber.setText("");
                        etExpiry.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
    }

    // method within MyActivity from previous step
//    public void startScanning() {
//        // Settings for BlinkCardActivity
//        BlinkCardUISettings settings = new BlinkCardUISettings(mRecognizerBundle);
//        // tweak settings as you wish
//        // Start activity
//        ActivityRunner.startActivityForResult(this, 123, settings);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                // load the data into all recognizers bundled within your RecognizerBundle
//                mRecognizerBundle.loadFromIntent(data);
//                // now every recognizer object that was bundled within RecognizerBundle
//                // has been updated with results obtained during scanning session
//                // you can get the result by invoking getResult on recognizer
//                BlinkCardRecognizer.Result result = mRecognizer.getResult();
//                if (result.getResultState() == Recognizer.Result.State.Valid) {
//                    // result is valid, you can use it however you wish
//                    Log.e("number", result.getCardNumber());
//                    Log.e("owner", result.getOwner());
//                    Log.e("number", result.getExpiryDate().toString());
//                    result.getCardNumber();
//                    isScan = true;
//                    etCardNumber.setText(result.getCardNumber());
//                    etCardNumber.setSelection();
//                    etCardNumber.removeError();
//                    etCardNumber.requestCNETFocus();
//                    cardErrorLL.setVisibility(GONE);
//                }
//            }
        }
    }

    class IdentityPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layoutCard;
                    break;
                case 1:
                    resId = R.id.layoutAddress;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // No super
        }
    }
}
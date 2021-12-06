package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.santalu.maskara.widget.MaskEditText;

import org.json.JSONObject;

import java.util.Base64;
import java.util.UUID;

public class AddCardActivity extends AppCompatActivity {
    String strPublicKey = "";
    PaymentMethodsViewModel paymentMethodsViewModel;
    MyApplication objMyApplication;
    RelativeLayout layoutCard, layoutAddress;
    LinearLayout layoutClose;
    View divider1, divider2;
    TextView tvCardHead;
    CardView cvNext, cvAddCard;
    String strName = "", strCardNo = "", strExpiry = "", strCvv = "", strAdd1 = "", strAdd2 = "", strCity = "", strState = "", strZip = "", strCountry = "";
    TextInputEditText etName, etCVV, etAddress1, etAddress2, etCity, etState, etZipCode, etCountry, etPreAmount;
    CardNumberEditText etCardNumber;
    TextInputLayout etlState;
    MaskEditText etExpiry;
    ConstraintLayout clStates;
    Long mLastClickTime = 0L;
    Dialog preDialog;
    CardResponseData cardResponseData;
    ProgressDialog progressDialog;
    public static AddCardActivity addCardActivity;
    CardTypeResponse objCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_addcard);
            addCardActivity = this;
            initialization();
            initObserver();
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
            divider1 = findViewById(R.id.divider1);
            divider2 = findViewById(R.id.divider2);
            tvCardHead = findViewById(R.id.tvCardHead);
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        layoutCard.setVisibility(View.GONE);
                        layoutAddress.setVisibility(View.VISIBLE);
                        divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                        strName = etName.getText().toString().trim();
                        strCardNo = etCardNumber.getText().toString().trim();
                        strExpiry = etExpiry.getText().toString().trim();
                        strCvv = etCVV.getText().toString().trim();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
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
//                        strCountry = etCountry.getText().toString().trim();
                        strCountry = Utils.getStrCCode();
                        prepareJson();
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
                    }
                }
            });
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
                                Utils.displayAlert("Card details are invalid, please try with a valid card", AddCardActivity.this, "");
                            }
                        } else {
                            Utils.displayAlert(errData.getErrorDescription(), AddCardActivity.this, "");
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
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), AddCardActivity.this, "");
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), AddCardActivity.this, "");
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
                progressDialog.dismiss();
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
                        Utils.displayAlert(errData.getErrorDescription().toString(), AddCardActivity.this, "");
                    }
                }
            }
        });

        paymentMethodsViewModel.getCardTypeResponseMutableLiveData().observe(this, new Observer<CardTypeResponse>() {
            @Override
            public void onChanged(CardTypeResponse cardTypeResponse) {
                if (cardTypeResponse != null) {
                    objCard = cardTypeResponse;
                    etCardNumber.setImage(cardTypeResponse.getData().getCardBrand());
                }
            }
        });
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
            CustomKeyboard ctKey = new CustomKeyboard(AddCardActivity.this);
            preDialog = new Dialog(AddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.preauthorization);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            preDialog.getWindow().setAttributes(lp);
            preDialog.show();
            layoutPClose = preDialog.findViewById(R.id.layoutPClose);
            tvMessage = preDialog.findViewById(R.id.tvMessage);
            etPreAmount = preDialog.findViewById(R.id.etAmount);
            ctKey = preDialog.findViewById(R.id.ckb);
            ctKey.setKeyAction("Verify");
            ctKey.setScreenName("addcard");
            InputConnection ic = etPreAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            tvMessage.setText("A temporary hold was placed on your card and will be removed by the end of this verification process. Please check your bank/card statement for a charge from " + cardResponseData.getDescriptorName() + " and enter the amount below.");
            etPreAmount.setShowSoftInputOnFocus(false);
            layoutPClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preDialog.dismiss();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void verifyClick() {
        try {
            if (!etPreAmount.getText().toString().trim().equals("")) {
                JSONObject jsonObject = new JSONObject();
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
                    progressDialog = Utils.showProgressDialog(AddCardActivity.this);
                    PreAuthRequest request = new PreAuthRequest();
                    request.setKey(Base64.getEncoder().encodeToString(encrypt.getEncryptKey()));
                    request.setPayload(encrypt.getEncryptData());
                    paymentMethodsViewModel.preAuthVerify(request);
                }
            } else {
                Utils.displayAlert("Please enter Amount", AddCardActivity.this, "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayPreAuthSuccess() {
        try {
            CardView cvDone;
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
            preDialog.show();
            cvDone = preDialog.findViewById(R.id.cvDone);

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preDialog.dismiss();
                    onBackPressed();
                    finish();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayPreAuthFail() {
        try {
            CardView cvDone;
            preDialog = new Dialog(AddCardActivity.this, R.style.DialogTheme);
            preDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            preDialog.setContentView(R.layout.activity_cards_authorization_failed);
            Window window = preDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            preDialog.getWindow().setAttributes(lp);
            preDialog.show();
            cvDone = preDialog.findViewById(R.id.cvDone);

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preDialog.dismiss();
                    onBackPressed();
                    finish();
                }
            });
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

}
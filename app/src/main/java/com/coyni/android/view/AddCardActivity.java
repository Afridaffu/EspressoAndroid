package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coyni.android.model.cards.CardType;
import com.coyni.android.model.cards.CardTypeRequest;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.model.user.PublicKeyResponse;
import com.coyni.android.utils.CryptUtilNew;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.viewmodel.BuyViewModel;
import com.santalu.maskara.widget.MaskEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCardActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    TextInputEditText etName, etCVV;
    MaskEditText etNumber, etExpiry;
    CardsDataItem selectedCard;
    ImageView imgCardType;
    CardType objCard;
    DashboardViewModel dashboardViewModel;
    BuyViewModel buyViewModel;
    TextView tvHeading, tvHead;
    ProgressDialog dialog;
    String strPublicKey;
    public static String strEncryptedPublicKey, strEncryptedPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_card);
            initialization();
            initObserver();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    etName.setText(data.getStringExtra("name"));
                    etNumber.setText(data.getStringExtra("number"));
                    etExpiry.setText(data.getStringExtra("expiry"));
                    etCVV.setText(data.getStringExtra("cvv"));
                    CardTypeRequest request = new CardTypeRequest();
                    request.setCardNumber(getIntent().getStringExtra("number").replace(" ", "").substring(0, 6));
                    dashboardViewModel.cardType(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(AddCardActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddCardActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddCardActivity.this, this, false);
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(AddCardActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            etName = (TextInputEditText) findViewById(R.id.etName);
            etNumber = (MaskEditText) findViewById(R.id.etNumber);
            etExpiry = (MaskEditText) findViewById(R.id.etExpiry);
            etCVV = (TextInputEditText) findViewById(R.id.etCVV);
            imgCardType = (ImageView) findViewById(R.id.imgCardType);
            tvHeading = (TextView) findViewById(R.id.tvHeading);
            tvHead = (TextView) findViewById(R.id.tvHead);
            //cvScan = (CardView) findViewById(R.id.cvScan);
            LinearLayout layoutProceed = (LinearLayout) findViewById(R.id.layoutProceed);
//            etName.setText(Utils.capitalize(objMyApplication.getStrUser()));
//            etName.setEnabled(false);
            if (getIntent().getStringExtra("fromProfilePaymentMethods") != null && getIntent().getStringExtra("fromProfilePaymentMethods").equals("fromProfilePaymentMethods")) {
                tvHeading.setText("Debit / Credit Cards");
                tvHead.setText("Add Card details");
            }
            if (getIntent().getStringExtra("from").equals("InvalidCard")) {
                etName.setText(getIntent().getStringExtra("name"));
                etNumber.setText(getIntent().getStringExtra("number"));
                etExpiry.setText(getIntent().getStringExtra("expiry"));
                etCVV.setText(getIntent().getStringExtra("cvv"));
                CardTypeRequest request = new CardTypeRequest();
                request.setCardNumber(getIntent().getStringExtra("number").replace(" ", "").substring(0, 6));
                dashboardViewModel.cardType(request);
            }
            if (objMyApplication.getSelectedCard() != null && getIntent().getStringExtra("from") == null) {
                selectedCard = objMyApplication.getSelectedCard();
                etName.setText(Utils.capitalize(selectedCard.getName()));
                etNumber.setText("**** **** **** " + selectedCard.getLastFour());
                etExpiry.setText(selectedCard.getExpiryDate());
                imgCardType.setVisibility(View.VISIBLE);
                if (selectedCard.getCardBrand().toLowerCase().equals("visa")) {
                    imgCardType.setImageResource(R.drawable.ic_visa);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("master")) {
                    imgCardType.setImageResource(R.drawable.ic_master);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("american")) {
                    imgCardType.setVisibility(View.VISIBLE);
                    imgCardType.setImageResource(R.drawable.ic_amex);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("discover")) {
                    imgCardType.setVisibility(View.VISIBLE);
                    imgCardType.setImageResource(R.drawable.ic_discover);
                }
                etName.setEnabled(false);
                etNumber.setEnabled(false);
                etCVV.setVisibility(View.GONE);
                //cvScan.setVisibility(View.GONE);
            }
            etExpiry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        etExpiry.setHint("MM/YY");
                    } else {
                        etExpiry.setHint("");
                    }
                }
            });

            etNumber.addTextChangedListener(new TextWatcher() {
                private static final char space = ' ';

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (count > 2) {
                            if (s != null && s.length() >= 15) {
                                CardTypeRequest request = new CardTypeRequest();
                                request.setCardNumber(s.toString().replace(" ", ""));
                                dashboardViewModel.cardType(request);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() == 7) {
                            CardTypeRequest request = new CardTypeRequest();
                            request.setCardNumber(s.toString().replace(" ", ""));
                            dashboardViewModel.cardType(request);
                        } else if (s.toString().trim().length() == 0) {
                            imgCardType.setVisibility(View.GONE);
                            etExpiry.setText("");
                            etCVV.setText("");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validation()) {
                        try {
                            dialog = new ProgressDialog(AddCardActivity.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            buyViewModel.getPublicKey(objMyApplication.getStrEmail());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        getPublicKeyObserver();
                        Intent i = new Intent(AddCardActivity.this, AddressActivity.class);
                        i.putExtra("name", etName.getText().toString());
                        i.putExtra("number", etNumber.getText().toString().replace(" ", ""));
                        i.putExtra("number2", etNumber.getText().toString());
                        i.putExtra("expiry", etExpiry.getText().toString());
                        i.putExtra("cvv", etCVV.getText().toString());
                        i.putExtra("strEncryptedPublicKey", strEncryptedPublicKey);
                        if (getIntent().getStringExtra("from") != null && !getIntent().getStringExtra("from").equals("")) {
                            i.putExtra("from", getIntent().getStringExtra("from"));
                        }
                        i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                        i.putExtra("type", getIntent().getStringExtra("type"));
                        i.putExtra("fromProfilePaymentMethods", getIntent().getStringExtra("fromProfilePaymentMethods"));
                        //startActivity(i);
                        startActivityForResult(i, 1);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        dashboardViewModel.getCardTypeMutableLiveData().observe(this, new Observer<CardType>() {
            @Override
            public void onChanged(CardType cardType) {
                if (cardType != null) {
                    objCard = cardType;
                    Boolean isCardVisible = false;
                    if (!cardType.getData().getCardBrand().equals("")) {
                        if (cardType.getData().getCardBrand().toLowerCase().equals("visa")) {
                            isCardVisible = true;
                            imgCardType.setImageResource(R.drawable.ic_visa);
                        } else if (cardType.getData().getCardBrand().toLowerCase().contains("master")) {
                            isCardVisible = true;
                            imgCardType.setImageResource(R.drawable.ic_master);
                        } else if (cardType.getData().getCardBrand().toLowerCase().contains("american")) {
                            isCardVisible = true;
                            imgCardType.setImageResource(R.drawable.ic_amex);
                        } else if (cardType.getData().getCardBrand().toLowerCase().contains("discover")) {
                            isCardVisible = true;
                            imgCardType.setImageResource(R.drawable.ic_discover);
                        }
                        if (isCardVisible) {
                            if (etNumber.getText().toString().trim().length() > 0) {
                                imgCardType.setVisibility(View.VISIBLE);
                            } else {
                                imgCardType.setVisibility(View.GONE);
                            }
                        } else {
                            imgCardType.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etName.getText().toString().equals("")) {
                etName.requestFocus();
                Utils.displayAlert("Card holder Name is required", AddCardActivity.this);
                return value = false;
            } else if (etNumber.getText().toString().equals("")) {
                etNumber.requestFocus();
                Utils.displayAlert("Card Number is required", AddCardActivity.this);
                return value = false;
            } else if (!objCard.getData().getCardBrand().toLowerCase().contains("american") && getIntent().getStringExtra("from") != null && !etNumber.getText().toString().equals("") && etNumber.getText().toString().length() < 19) {
                etNumber.requestFocus();
                Utils.displayAlert("Invalid Card Number", AddCardActivity.this);
                return value = false;
            } else if (objCard.getData().getCardBrand().toLowerCase().contains("american") && getIntent().getStringExtra("from") != null && !etNumber.getText().toString().equals("") && etNumber.getText().toString().length() != 18) {
                etNumber.requestFocus();
                Utils.displayAlert("Invalid Card Number", AddCardActivity.this);
                return value = false;
            } else if (etExpiry.getText().toString().equals("")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Expiry Date is required", AddCardActivity.this);
                return value = false;
            } else if (!etExpiry.getText().toString().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter valid Expiry Date", AddCardActivity.this);
                return value = false;
            } else if (!validateExpiry()) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter valid Expiry Date", AddCardActivity.this);
                return value = false;
            } else if (getIntent().getStringExtra("from") != null && etCVV.getText().toString().equals("")) {
                etCVV.requestFocus();
                Utils.displayAlert("CVV is required", AddCardActivity.this);
                return value = false;
            } else if (!objCard.getData().getValid()) {
                Utils.displayAlert("Invalid request! Please check the card and try again.", AddCardActivity.this);
                return value = false;
            } else if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("instantpay") && objCard.getData().getCardType().toLowerCase().equals("credit")) {
                Utils.displayAlert("Invalid request! Please add Debit Card only.", AddCardActivity.this);
                return value = false;
            } else if (!objCard.getData().getCardBrand().toLowerCase().equals("visa") && !objCard.getData().getCardBrand().toLowerCase().contains("master") && !objCard.getData().getCardBrand().toLowerCase().contains("american") && !objCard.getData().getCardBrand().toLowerCase().contains("discover")) {
                Utils.displayAlert("GreenBox system supports only MASTERCARD, VISA, AMERICAN EXPRESS and DISCOVER", AddCardActivity.this);
                return value = false;
            } else if (getIntent().getStringExtra("from") != null && !etCVV.getText().toString().equals("") && etCVV.getText().toString().length() < 3) {
                etCVV.requestFocus();
                Utils.displayAlert("Please enter valid CVV/CVC.", AddCardActivity.this);
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
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) <= Integer.parseInt(year) && Integer.parseInt(etExpiry.getText().toString().split("/")[0]) < month) {
                value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void getPublicKeyObserver() {
        buyViewModel.getPublicKeyLiveData().observe(this, new Observer<PublicKeyResponse>() {
            @Override
            public void onChanged(PublicKeyResponse publicKeyResponse) {
                dialog.dismiss();
                if (publicKeyResponse != null) {
                    strPublicKey = publicKeyResponse.getData().getPublicKey();
                    try {
                        strEncryptedPublicKey = CryptUtilNew.encryptMessage(strPublicKey);
                        objMyApplication.setStrEncryptedPublicKey(strEncryptedPublicKey);
                    } finally {

                    }
                }
            }
        });
    }

}
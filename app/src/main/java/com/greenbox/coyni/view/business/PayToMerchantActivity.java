package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.businesswallet.WalletName;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;


public class PayToMerchantActivity extends AppCompatActivity implements TextWatcher {
    private MyApplication objMyApplication;
    private EditText payRequestET, addNoteET;
    private Dialog cvvDialog, prevDialog;
    private SQLiteDatabase mydatabase;
    private Cursor dsFacePin, dsTouchID, dsPermanentToken;
    private DashboardViewModel dashboardViewModel;
    private BuyTokenViewModel buyTokenViewModel;
    private PayViewModel payViewModel;
    private CoyniViewModel coyniViewModel;
    private LinearLayout lyPayClose, lyBalance, payRequestLL, addNoteClickLL;
    private ImageView imgProfile, imgConvert;
    private TextView profileTitle, tvName, accAddress, tvCurrency, coyniTV, availBal, requestTV, payTV, addNoteTV;
    private TransactionLimitResponse objResponse;
    private float fontSize, dollarFont;
    private WalletName cynWallet;
    private Boolean isFaceLock = false, isTouchId = false;
    private String strAmount = "", strWalletId = "", strLimit = "", strUserName = "", recipientAddress = "", strToken = "";
    private Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    private Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    private Long mLastClickTime = 0L;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private static int FOR_RESULT = 235;
    private boolean isAuthenticationCalled = false, isPayClickable = false, isReqClickable = false, isPayClick = false;
    private ProgressDialog pDialog;
    private int requestedToUserId = 0;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomKeyboard cKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_to_merchant);
        try {
//            pDialog.dismiss();
            initialization();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (start == 0 && after == 0) {
            payRequestET.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, fontSize));
            tvCurrency.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, dollarFont));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null) {

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == payRequestET.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    payRequestET.setHint("");
                    convertUSDValue();
                    if (editable.length() > 8) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                    } else if (editable.length() > 5) {
                        payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else {
                        payRequestET.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, fontSize));
                        tvCurrency.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, dollarFont));
                    }
                    if (Double.parseDouble(editable.toString().replace(",", "")) > 0) {
                        disableButtons(false);
                    } else {
                        disableButtons(true);
                    }
                    //payRequestET.setSelection(payRequestET.getText().length());
                } else if (editable.toString().equals(".")) {
                    payRequestET.setText("");
                    disableButtons(true);
                } else if (editable.length() == 0) {
                    payRequestET.setHint("0.00");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
                    cKey.clearData();
                } else {
                    payRequestET.setText("");
                    cynValue = 0.0;
                    usdValue = 0.0;
                    cynValidation = 0.0;
                    disableButtons(true);
                    cKey.clearData();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

        private void initialization() {

            try {
                objMyApplication = (MyApplication) getApplicationContext();
                payRequestET = findViewById(R.id.merchantAmountET);
                lyPayClose = findViewById(R.id.payToMerchantClose);
//        imgProfile = findViewById(R.id.imgProfile);
//        profileTitle = findViewById(R.id.profileTitle);
//        tvName = findViewById(R.id.tvName);
//        accAddress = findViewById(R.id.accountAddress);
                tvCurrency = findViewById(R.id.tvCurrency);
                coyniTV = findViewById(R.id.tvCYN);
                availBal = findViewById(R.id.tvAvailableBal);
//        requestTV = findViewById(R.id.tvRequest);
//        payTV = findViewById(R.id.tvPay);
//        addNoteTV = findViewById(R.id.addNoteTV);
                imgConvert = findViewById(R.id.imageConvert);
//        lyBalance = findViewById(R.id.lyBalance);
//        payRequestLL = findViewById(R.id.payRequestLL);
//                addNoteClickLL = findViewById(R.id.addNoteClickLL);
                dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
//        buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
//        payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
//        coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
                fontSize = payRequestET.getTextSize();
                dollarFont = tvCurrency.getTextSize();
//                availBal.setText(Utils.USNumberFormat(objMyApplication.getGBTBalance()));
//                avaBal = objMyApplication.getGBTBalance();
//                cynWallet = objMyApplication.getGbtWallet();
//                payRequestET.requestFocus();
//                payRequestET.setShowSoftInputOnFocus(false);


                if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                    strWalletId = getIntent().getStringExtra("walletId");
                    if (Utils.checkInternet(PayToMerchantActivity.this)) {
                        dashboardViewModel.getUserDetail(strWalletId);
                    } else {
                        Utils.displayAlert(getString(R.string.internet), PayToMerchantActivity.this, "", "");
                    }
                }

                payRequestET.addTextChangedListener(this);
                if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                    payRequestET.setText(getIntent().getStringExtra("amount"));
                    USFormat(payRequestET);
                    payRequestET.setEnabled(false);
                } else {
                    //enableButtons();
                    cKey = (CustomKeyboard) findViewById(R.id.ckb);
                    InputConnection ic = payRequestET.onCreateInputConnection(new EditorInfo());
                    cKey.setInputConnection(ic);
                }
                payRequestET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.hideSoftKeypad(PayToMerchantActivity.this, v);
                    }
                });

                payRequestET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        try {
                            Utils.hideSoftKeypad(PayToMerchantActivity.this, view);
                            if (!b) {
                                if (!payRequestET.getText().toString().equals("")) {
                                    InputFilter[] FilterArray = new InputFilter[1];
                                    FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                                    payRequestET.setFilters(FilterArray);
                                    USFormat(payRequestET);
                                }
                            } else {
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                                payRequestET.setFilters(FilterArray);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                lyPayClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });

                imgConvert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (payRequestET.getText().toString().trim().length() > 0) {
                                convertDecimal();
                                if (coyniTV.getVisibility() == View.GONE) {
                                    coyniTV.setVisibility(View.VISIBLE);
                                    tvCurrency.setVisibility(View.INVISIBLE);
                                    payRequestET.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                                } else {
                                    coyniTV.setVisibility(View.GONE);
                                    tvCurrency.setVisibility(View.VISIBLE);
                                    payRequestET.setGravity(Gravity.CENTER_VERTICAL);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
//            SetToken();
                SetFaceLock();
                SetTouchId();
//            enableButtons();
//            calculateFee("10");
                if (Utils.checkInternet(PayToMerchantActivity.this)) {
                    TransactionLimitRequest obj = new TransactionLimitRequest();
                    obj.setTransactionType(Integer.parseInt(Utils.payType));
                    obj.setTransactionSubType(Integer.parseInt(Utils.paySubType));
    //                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                    pDialog = Utils.showProgressDialog(PayToMerchantActivity.this);
                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                        buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                    } else {
                        buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


    private void initObservers() {
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {

                if (userDetails != null) {
                    try {
                        bindUserInfo(userDetails);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, userName, userWalletAddre,merchantType;
            ImageView userProfile;
            tvName = findViewById(R.id.merchantyNameTV);
            userName = findViewById(R.id.userProfileTextTV);
            userProfile = findViewById(R.id.userProfileIV);
            userWalletAddre = findViewById(R.id.accountAddress);
            merchantType = findViewById(R.id.merchantTypeTV);
            requestedToUserId = userDetails.getData().getUserId();
            if (userDetails.getData().getFullName().length() > 20) {
                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
            } else {
                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            }
//            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            strUserName = Utils.capitalize(userDetails.getData().getFullName());
            String imageTextNew = "";
            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            userName.setText(imageTextNew);
            if (userDetails.getData().getWalletId().length() > Integer.parseInt(getString(R.string.waddress_length))) {
                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            } else {
                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
            }
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                Glide.with(PayToMerchantActivity.this)
                        .load(userDetails.getData().getImage())
                        .placeholder(R.drawable.ic_profilelogo)
                        .into(userProfile);
            } else {
                userProfile.setVisibility(View.GONE);
                userName.setVisibility(View.VISIBLE);
            }
            recipientAddress = "";
            recipientAddress = userDetails.getData().getWalletId().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertUSDValue() {
        try {
            usdValue = Double.parseDouble(payRequestET.getText().toString().trim().replace(",", ""));
            cynValue = (usdValue + (usdValue * (feeInPercentage / 100))) + feeInAmount;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void disableButtons(Boolean value) {
        try {
            if (value) {
                payRequestLL.setBackgroundResource(R.drawable.payrequest_bgcolor);
                isPayClickable = false;
                isReqClickable = false;
            } else {
                payRequestLL.setBackgroundResource(R.drawable.payrequest_activebg);
                isPayClickable = true;
                isReqClickable = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetFaceLock() {
        try {
            isFaceLock = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFaceLock = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetTouchId() {
        try {
            isTouchId = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsTouchID.moveToFirst();
            if (dsTouchID.getCount() > 0) {
                String value = dsTouchID.getString(1);
                if (value.equals("true")) {
                    isTouchId = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouchId = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(PayToMerchantActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(PayToMerchantActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }
    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if (editable.length() > 12) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                payRequestET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            } else {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                payRequestET.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, fontSize));
                tvCurrency.setTextSize(Utils.pixelsToSp(PayToMerchantActivity.this, dollarFont));
            }
            payRequestET.setFilters(FilterArray);
            payRequestET.setSelection(payRequestET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            payRequestET.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertDecimal() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            payRequestET.setFilters(FilterArray);
            USFormat(payRequestET);
            payRequestET.setSelection(payRequestET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
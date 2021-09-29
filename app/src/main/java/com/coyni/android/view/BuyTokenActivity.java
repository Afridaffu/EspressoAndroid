package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coyni.android.adapters.BanksListAdapter;
import com.coyni.android.adapters.CardsListAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BankDeleteResponseData;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.bank.SignOnData;
import com.coyni.android.model.bank.SyncAccount;
import com.coyni.android.model.cards.CardDeleteResponse;
import com.coyni.android.model.cards.Cards;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.model.transactions.LimitResponseData;
import com.coyni.android.model.transactions.TransactionLimitRequest;
import com.coyni.android.model.transactions.TransactionLimitResponse;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;
import com.coyni.android.viewmodel.SendViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.android.R;

import java.util.ArrayList;
import java.util.List;

public class BuyTokenActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    LinearLayout layoutBuy;
    TextInputEditText etPay, etGet;
    SendViewModel sendViewModel;
    BuyViewModel buyViewModel;
    LinearLayout layoutInfo;
    Double dget = 0.0, maxValue = 0.0;
    String strType = "", strSignOn = "", strCVC = "", strLimit = "";
    List<CardsDataItem> listAllCards = new ArrayList<>();
    TextInputLayout etlPay, etlGet;
    TextView tvButton, tvDebitHead, title, tvPay, tvGet, tvExchange, tvHead, tvPFee, tvAdd;
    List<CardsDataItem> listSorted;
    List<BanksDataItem> listBSorted;
    Boolean isBank = false, isInstantPay = false, isCard = false, isDel = false;
    RecyclerView rvCards, rvBank;
    SignOnData signOnData;
    TransactionLimitResponse objResponse;
    Dialog popupDelete;
    ProgressDialog dialog;
    CardsListAdapter cardsListAdapter;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_buy_token);
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
    protected void onResume() {
        super.onResume();
        try {
            if (rvCards != null && rvCards.getAdapter() != null && rvCards.getAdapter().getItemCount() > 0) {
                rvCards.getAdapter().notifyDataSetChanged();
            }
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(BuyTokenActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(BuyTokenActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(BuyTokenActivity.this, this, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", BuyTokenActivity.this);
                } else {
                    dialog = new ProgressDialog(BuyTokenActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    buyViewModel.meSyncAccount();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            super.onActivityResult(requestCode, resultCode, data);
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (after < count) {
            isDel = true;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == etPay.getEditableText()) {
            try {
                if (s.length() > 0 && !s.toString().equals(".") && !s.toString().equals(".00")) {
                    calculateFee();
                    if (isInstantPay) {
                        etGet.setText(s.toString());
//                        USFormat(etPay);
//                        USFormat(etGet);
                    }
                } else if (s.toString().equals(".")) {
                    etPay.setText("");
                } else if (s.length() == 0) {
                    etGet.setText(" ");
                    isDel = false;
                } else {
                    etPay.removeTextChangedListener(BuyTokenActivity.this);
                    etPay.setText("");
                    etPay.addTextChangedListener(BuyTokenActivity.this);
                    etGet.setText(" ");
                    isDel = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (s == etGet.getEditableText()) {
            if (s.toString().equals(".")) {
                etGet.setText("");
            }
        }
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(BuyTokenActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            title = (TextView) toolbar.findViewById(R.id.title);
            tvDebitHead = (TextView) findViewById(R.id.tvDebitHead);
            tvButton = (TextView) findViewById(R.id.tvButton);
            tvPay = (TextView) findViewById(R.id.tvPay);
            tvGet = (TextView) findViewById(R.id.tvGet);
            tvExchange = (TextView) findViewById(R.id.tvExchange);
            tvHead = (TextView) findViewById(R.id.tvHead);
            tvPFee = (TextView) findViewById(R.id.tvPFee);
            tvAdd = (TextView) findViewById(R.id.tvAdd);
            layoutBuy = (LinearLayout) findViewById(R.id.layoutBuy);
            etPay = (TextInputEditText) findViewById(R.id.etPay);
            etGet = (TextInputEditText) findViewById(R.id.etGet);
            etlPay = (TextInputLayout) findViewById(R.id.etlPay);
            etlGet = (TextInputLayout) findViewById(R.id.etlGet);
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            RelativeLayout layoutCard = (RelativeLayout) findViewById(R.id.layoutCard);
            RelativeLayout layoutBank = (RelativeLayout) findViewById(R.id.layoutBank);
            RelativeLayout layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
            //LinearLayout layoutCards = (LinearLayout) findViewById(R.id.layoutCards);
            CardView cvAdd = (CardView) findViewById(R.id.cvAdd);
            CardView cvAddBank = (CardView) findViewById(R.id.cvAddBank);
            ImageView imgLogo = (ImageView) findViewById(R.id.imgLogo);
            layoutInfo = (LinearLayout) findViewById(R.id.layoutInfo);
            CardView cvInfo = (CardView) findViewById(R.id.cvInfo);
            scrollView = (ScrollView) findViewById(R.id.scrollView);
            rvCards = (RecyclerView) findViewById(R.id.rvCards);
            rvBank = (RecyclerView) findViewById(R.id.rvBank);
            title.setText("Buy Token");
            tvDebitHead.setText("Credit / Debit  Cards");
            tvButton.setText("Buy Now");
            tvPay.setText("USD");
            tvGet.setText(getString(R.string.currency));
            tvExchange.setVisibility(View.VISIBLE);
            isInstantPay = false;
            etPay.requestFocus();
            imgLogo.setBackgroundResource(R.drawable.ic_buyt_trans);
            if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("card")) {
                layoutCard.setVisibility(View.VISIBLE);
                layoutBank.setVisibility(View.GONE);
                strType = "card";
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    buyViewModel.meCards();
                } else {
                    Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                }
            } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("bank")) {
                layoutCard.setVisibility(View.GONE);
                layoutBank.setVisibility(View.VISIBLE);
                strType = "bank";
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    buyViewModel.meBanks();
                    if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                        buyViewModel.meSignOn();
                    } else {
                        strSignOn = objMyApplication.getStrSignOnError();
                        signOnData = objMyApplication.getSignOnData();
                    }
                } else {
                    Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                }
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    TransactionLimitRequest obj = new TransactionLimitRequest();
                    obj.setTransactionType(Integer.parseInt(Utils.addType));
                    obj.setTransactionSubType(Integer.parseInt(Utils.bankType));
                    buyViewModel.transactionLimits(obj);
                } else {
                    Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                }
            } else {
                layoutCard.setVisibility(View.VISIBLE);
                layoutBank.setVisibility(View.GONE);
                imgLogo.setBackgroundResource(R.drawable.ic_withdraw_gift);
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    buyViewModel.meCards();
                } else {
                    Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                }
                bindInstantPay();
            }
            layoutBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Utils.checkInternet(BuyTokenActivity.this)) {
                            Utils.hideKeypad(BuyTokenActivity.this, v);
                            etPay.clearFocus();
                            if (validation()) {
                                Intent i = new Intent(BuyTokenActivity.this, BuyTokenPreviewActivity.class);
                                i.putExtra("pay", etPay.getText().toString());
                                i.putExtra("get", String.valueOf(dget));
                                i.putExtra("type", getIntent().getStringExtra("type"));
                                i.putExtra("cardtype", strType.toLowerCase());
                                startActivity(i);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BuyTokenActivity.this, AddCardActivity.class);
                    i.putExtra("from", "add");
                    i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                    i.putExtra("type", getIntent().getStringExtra("type"));
                    startActivity(i);
                }
            });

            cvAddBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        UserTracker userTracker = objMyApplication.getUserTracker();
                        if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                                && userTracker.getData().getPersonIdentified()) {
                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                Intent i = new Intent(BuyTokenActivity.this, WebViewActivity.class);
                                i.putExtra("signon", signOnData);
                                startActivityForResult(i, 1);
                            } else {
                                Utils.displayAlert(strSignOn, BuyTokenActivity.this);
                            }
                        } else {
                            Intent i = new Intent(BuyTokenActivity.this, MainActivity.class);
                            i.putExtra("withsig", "payment");
                            startActivity(i);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etPay.addTextChangedListener(this);
            etGet.addTextChangedListener(this);

            etPay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && (etPay.getText().toString().length() == 1 || etPay.getText().toString().length() == 0)) {
                        etPay.removeTextChangedListener(BuyTokenActivity.this);
                        etPay.setText("");
                        etPay.addTextChangedListener(BuyTokenActivity.this);
                    } else if (hasFocus) {
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                        etPay.setFilters(FilterArray);
                    } else {
                        formatValue();
                    }
                }
            });

            etPay.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        formatValue();
                    }
                    return false;
                }
            });

            etGet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        etGet.setText("");
                    }
                }
            });

            layoutInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        cvInfo.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cvInfo.setVisibility(View.GONE);
                            }
                        }, Integer.parseInt(getString(R.string.time)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    formatValue();
                    return false;
                }
            });

            if (Build.VERSION.SDK_INT >= 23) {
                scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (!rvCards.hasFocus()) {
                            Utils.hideKeypad(BuyTokenActivity.this, v);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        sendViewModel.getTransferFeeMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
                try {
                    if (transferFeeResponse != null) {
                        String strMsg = Utils.feeCalculation(transferFeeResponse);
                        tvPFee.setText(strMsg);
                        if (!strMsg.equals("")) {
                            layoutInfo.setVisibility(View.VISIBLE);
                        } else {
                            layoutInfo.setVisibility(View.INVISIBLE);
                        }
                        objMyApplication.setTransferFeeResponse(transferFeeResponse);
                        if (!isInstantPay) {
                            if (!etPay.getText().toString().equals("") && !etPay.getText().toString().equals("0")) {
                                Double pay = Double.parseDouble(etPay.getText().toString().replace(",", ""));
                                Double fee = transferFeeResponse.getData().getFee();
                                dget = pay - fee;
                                if (dget > 0) {
                                    etGet.setText(Utils.convertBigDecimalUSDC(String.valueOf(dget)));
                                } else {
                                    etGet.setText("0");
                                }
//                                USFormat(etPay);
//                                USFormat(etGet);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getCardsMutableLiveData().observe(this, new Observer<Cards>() {
            @Override
            public void onChanged(Cards cards) {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
                if (cards != null) {
                    listAllCards = cards.getData().getItems();
                    if (isInstantPay) {
                        List<CardsDataItem> listDebit = new ArrayList<>();
                        if (listAllCards != null && listAllCards.size() > 0) {
                            for (int i = 0; i < listAllCards.size(); i++) {
                                if (listAllCards.get(i).getCardType().toLowerCase().equals("debit")) {
                                    listDebit.add(listAllCards.get(i));
                                }
                            }
                        }
                        bindCards(listDebit);
                    } else {
                        bindCards(cards.getData().getItems());
                    }
                }
            }
        });

        buyViewModel.getDelCardResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                dialog.dismiss();
                popupDelete.dismiss();
                if (cardDeleteResponse != null) {
//                    Utils.displayAlert(cardDeleteResponse.getData().getMessage(), BuyTokenActivity.this);
                    Utils.displayCloseAlert(cardDeleteResponse.getData().getMessage(), BuyTokenActivity.this);
                    if (Utils.checkInternet(BuyTokenActivity.this)) {
                        buyViewModel.meCards();
                    } else {
                        Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                    }
                }
            }
        });

        buyViewModel.getBanksMutableLiveData().observe(this, new Observer<Banks>() {
            @Override
            public void onChanged(Banks banks) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (banks != null) {
                    List<BanksDataItem> banksByType = new ArrayList<>();
                    List<BanksDataItem> listItems = banks.getData().getItems();
                    if (listItems != null && listItems.size() > 0) {
                        for (int i = 0; i < listItems.size(); i++) {
                            if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("bank") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                banksByType.add(banks.getData().getItems().get(i));
                            }
                        }
                    }
                    bindBanks(banksByType);
                    objMyApplication.setListBanks(banksByType);
                }
            }
        });

        buyViewModel.getLimitMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;
                    setDailyWeekLimit(objResponse.getData());
                }
            }
        });

        buyViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                if (signOn != null) {
                    if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                        strSignOn = "";
                        signOnData = signOn.getData();
                        if (objMyApplication.getResolveUrl()) {
                            callResolveFlow();
                        }
                    } else {
                        strSignOn = signOn.getError().getErrorDescription();
                    }
                }
            }
        });

        buyViewModel.getSyncAccountMutableLiveData().observe(this, new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (syncAccount != null) {
                    if (syncAccount.getStatus().toLowerCase().equals("success")) {
                        dialog = new ProgressDialog(BuyTokenActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        buyViewModel.meBanks();
//                        Utils.displayAlert("You added a new Bank Account to your profile!", BuyTokenActivity.this);
                        Utils.displayCloseAlert("You added a new Bank Account to your profile!", BuyTokenActivity.this);
                    }
                }
            }
        });

        buyViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            buyViewModel.meSignOn();
                        } else {
                            if (apiError.getError().getFieldErrors() != null) {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BuyTokenActivity.this);
                            } else {
                                if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                                    objMyApplication.displayAlert(BuyTokenActivity.this, getString(R.string.session));
                                } else {
                                    Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenActivity.this);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
//                dialog.dismiss();
//                popupDelete.dismiss();
//                if (bankDeleteResponseData != null) {
//                    Utils.displayAlert(bankDeleteResponseData.getData(), BuyTokenActivity.this);
//                    if (Utils.checkInternet(BuyTokenActivity.this)) {
//                        buyViewModel.meBanks();
//                    } else {
//                        Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
//                    }
//                }
                dialog.dismiss();
                popupDelete.dismiss();
                Context context = new ContextThemeWrapper(BuyTokenActivity.this, R.style.Theme_QuickCard);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                builder.setTitle(R.string.app_name);
                builder.setMessage(bankDeleteResponseData.getData());
                AlertDialog dilog = builder.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dilog.dismiss();
                        if (bankDeleteResponseData != null) {
                            if (Utils.checkInternet(BuyTokenActivity.this)) {
                                buyViewModel.meBanks();
                            } else {
                                Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                            }
                        }
                    }
                }, Integer.parseInt(context.getString(R.string.closealert)));
            }
        });

        sendViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            buyViewModel.meSignOn();
                        } else {
                            if (apiError.getError().getFieldErrors() != null) {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BuyTokenActivity.this);
                            } else {
                                if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                                    objMyApplication.displayAlert(BuyTokenActivity.this, getString(R.string.session));
                                } else {
                                    Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenActivity.this);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void callResolveFlow() {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                Intent i = new Intent(BuyTokenActivity.this, WebViewActivity.class);
                i.putExtra("signon", signOnData);
                startActivityForResult(i, 1);
            } else {
                Utils.displayAlert(strSignOn, BuyTokenActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            String strPay = etPay.getText().toString().trim().replace("\"", "");
            String strGet = etGet.getText().toString().trim().replace("\"", "");
            if (strPay.equals("") || Double.parseDouble(strPay.replace(",", "")) == 0.0) {
                if (!isInstantPay) {
                    Utils.displayAlert("You Pay is required", BuyTokenActivity.this);
                } else {
                    Utils.displayAlert("Withdraw amount is required", BuyTokenActivity.this);
                }
                return value = false;
            } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("bank") && !isBank) {
                Utils.displayAlert("Please select Bank", BuyTokenActivity.this);
                return value = false;
            } else if ((listSorted != null && listSorted.size() == 0) || (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("card") && !isCard)) {
                Utils.displayAlert("Please select Card", BuyTokenActivity.this);
                return value = false;
            } else if (Double.parseDouble(strPay.replace(",", "")) < objResponse.getData().getMinimumLimit()) {
                Utils.displayAlert("Amount should be greater than or equal to " + Utils.convertBigDecimalUSDC(String.valueOf(objResponse.getData().getMinimumLimit())) + " USD", BuyTokenActivity.this);
                return value = false;
            } else if (objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(strPay.replace(",", "")) > maxValue) {
                //Utils.displayAlert("Amount should be less than or equal to your limit.", BuyTokenActivity.this);
                if (strLimit.equals("daily")) {
                    Utils.displayAlert("Amount exceeds daily limit", BuyTokenActivity.this);
                } else if (strLimit.equals("week")) {
                    Utils.displayAlert("Amount exceeds weekly limit", BuyTokenActivity.this);
                }
                return value = false;
            } else if (!strGet.equals("") && Double.parseDouble(strGet.replace(",", "")) < 0.0) {
                Utils.displayAlert("Deposit amount cannot be negative.", BuyTokenActivity.this);
                return value = false;
            } else if (!strType.toLowerCase().equals("bank") && !isInstantPay && strCVC.equals("")) {
                Utils.displayAlert("CVV is required", BuyTokenActivity.this);
                if (rvCards != null && rvCards.getAdapter() != null && rvCards.getAdapter().getItemCount() > 0) {
                    rvCards.getAdapter().notifyDataSetChanged();
                }
                return value = false;
            } else if (!strType.toLowerCase().equals("bank") && !isInstantPay && !strCVC.equals("") && strCVC.length() < 3) {
                Utils.displayAlert("Please enter valid CVV/CVC.", BuyTokenActivity.this);
                return value = false;
            } else if (isInstantPay && Double.parseDouble(strPay.replace(",", "")) > objMyApplication.getGBTBalance()) {
                Utils.displayAlert(getString(R.string.walletBalValidation), BuyTokenActivity.this);
                return value = false;
            } else {
                if (isInstantPay) {
                    TransferFeeResponse transferFeeResponse = objMyApplication.getTransferFeeResponse();
                    Double total, yPay;
                    yPay = Double.parseDouble(strPay.replace(",", ""));
                    total = yPay + transferFeeResponse.getData().getFee();
                    if (total > objMyApplication.getGBTBalance()) {
                        Utils.displayAlert("Insufficient balance for this transaction.", BuyTokenActivity.this);
                        return value = false;
                    } else if (objResponse.getData().getTokenLimitFlag() && total > maxValue) {
                        if (strLimit.equals("daily")) {
                            Utils.displayAlert("Amount exceeds daily limit", BuyTokenActivity.this);
                        } else if (strLimit.equals("week")) {
                            Utils.displayAlert("Amount exceeds weekly limit", BuyTokenActivity.this);
                        }
                        return value = false;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void bindCards(List<CardsDataItem> listCards) {
        try {
            listSorted = new ArrayList<>();
            listSorted = listCards;
            if (listCards != null && listCards.size() > 0) {
                rvCards.setVisibility(View.VISIBLE);
                tvHead.setVisibility(View.VISIBLE);
                if (isInstantPay) {
                    cardsListAdapter = new CardsListAdapter(listCards, BuyTokenActivity.this, "instant");
                } else {
                    cardsListAdapter = new CardsListAdapter(listCards, BuyTokenActivity.this, "buy");
                }
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                rvCards.setLayoutManager(mLayoutManager);
                rvCards.setItemAnimator(new DefaultItemAnimator());
                rvCards.setAdapter(cardsListAdapter);
            } else {
                rvCards.setVisibility(View.GONE);
                tvHead.setVisibility(View.GONE);
                isCard = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cardType(String type, int position) {
        try {
            if (!isInstantPay) {
                strType = type;
            }
            isCard = true;
            CardsDataItem item = listSorted.get(position);
            objMyApplication.setSelectedCard(item);
            calculateFee();
            cardTransactionLimit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cvvValue(String strCVV) {
        try {
            strCVC = strCVV;
            objMyApplication.setStrCvv(strCVV);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void selectedBank() {
        isBank = true;
    }

    private void calculateFee() {
        try {
            if (!etPay.getText().toString().trim().equals("") && !etPay.getText().toString().trim().equals("0")) {
                TransferFeeRequest request = new TransferFeeRequest();
                request.setTokens(etPay.getText().toString().trim().replace(",", ""));
                if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("withdraw")) {
                    request.setTxnType(Utils.withdrawType);
                } else {
                    request.setTxnType(Utils.addType);
                }
                if (strType.toLowerCase().equals("debit")) {
                    request.setTxnSubType(Utils.debitType);
                } else if (strType.toLowerCase().equals("credit")) {
                    request.setTxnSubType(Utils.creditType);
                } else if (strType.toLowerCase().equals("bank")) {
                    request.setTxnSubType(Utils.bankType);
                } else if (strType.toLowerCase().equals("instant")) {
                    request.setTxnSubType(Utils.instantType);
                }
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    sendViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateGBTFee() {
        try {
            if (!etGet.getText().toString().trim().equals("")) {
                TransferFeeRequest request = new TransferFeeRequest();
                request.setTokens(etGet.getText().toString().trim());
                if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("withdraw")) {
                    request.setTxnType(Utils.withdrawType);
                } else {
                    request.setTxnType(Utils.addType);
                }
                if (strType.toLowerCase().equals("debit")) {
                    request.setTxnSubType(Utils.debitType);
                } else if (strType.toLowerCase().equals("credit")) {
                    request.setTxnSubType(Utils.creditType);
                } else if (strType.toLowerCase().equals("bank")) {
                    request.setTxnSubType(Utils.bankType);
                } else if (strType.toLowerCase().equals("instant")) {
                    request.setTxnSubType(Utils.instantType);
                }
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    sendViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindInstantPay() {
        try {
            tvDebitHead.setText("Debit Cards");
            title.setText("Withdraw Tokens");
            tvButton.setText("Continue");
            etlPay.setHint("Withdraw Amount");
            etlGet.setHint("Deposit Amount");
            tvPay.setText(getString(R.string.currency));
            tvGet.setText("USD");
            tvAdd.setText("Add Debit Card");
            tvExchange.setVisibility(View.GONE);
            if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("instantpay")) {
                strType = "instant";
            }
            isInstantPay = true;
            if (Utils.checkInternet(BuyTokenActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
                obj.setTransactionSubType(Integer.parseInt(Utils.instantType));
                buyViewModel.transactionLimits(obj);
            } else {
                Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<CardsDataItem> sortCards(List<CardsDataItem> lstCards) {
        listSorted = new ArrayList<>();
        try {
            int position = -1;
            if (lstCards != null && lstCards.size() > 0) {
                for (int i = 0; i < lstCards.size(); i++) {
                    if (lstCards.get(i).getDefaultForAllWithDrawals() != null && lstCards.get(i).getDefaultForAllWithDrawals()) {
                        listSorted.add(lstCards.get(i));
                        position = i;
                        break;
                    }
                }
                for (int i = 0; i < lstCards.size(); i++) {
                    if (i != position) {
                        listSorted.add(lstCards.get(i));
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listSorted;
    }

    private void bindBanks(List<BanksDataItem> listBanks) {
        BanksListAdapter banksListAdapter;
        try {
            if (listBanks != null && listBanks.size() > 0) {
                rvBank.setVisibility(View.VISIBLE);
                banksListAdapter = new BanksListAdapter(sortBank(listBanks), BuyTokenActivity.this, "buy");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(BuyTokenActivity.this);
                rvBank.setLayoutManager(mLayoutManager);
                rvBank.setItemAnimator(new DefaultItemAnimator());
                rvBank.setAdapter(banksListAdapter);

            } else {
                rvBank.setVisibility(View.GONE);
                isBank = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<BanksDataItem> sortBank(List<BanksDataItem> lstBanks) {
        listBSorted = new ArrayList<>();
        try {
            int position = -1;
            if (lstBanks != null && lstBanks.size() > 0) {
                for (int i = 0; i < lstBanks.size(); i++) {
                    if (lstBanks.get(i).getDefault() != null && lstBanks.get(i).getDefault()) {
                        listBSorted.add(lstBanks.get(i));
                        position = i;
                        break;
                    }
                }
                for (int i = 0; i < lstBanks.size(); i++) {
                    if (i != position) {
                        listBSorted.add(lstBanks.get(i));
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listBSorted;
    }

    private void cardTransactionLimit() {
        try {
            if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("card")) {
                if (Utils.checkInternet(BuyTokenActivity.this)) {
                    TransactionLimitRequest obj = new TransactionLimitRequest();
                    obj.setTransactionType(Integer.parseInt(Utils.addType));
                    if (strType.toLowerCase().equals("debit")) {
                        obj.setTransactionSubType(Integer.parseInt(Utils.debitType));
                    } else if (strType.toLowerCase().equals("credit")) {
                        obj.setTransactionSubType(Integer.parseInt(Utils.creditType));
                    }
                    buyViewModel.transactionLimits(obj);
                } else {
                    Utils.displayAlert(getString(R.string.internet), BuyTokenActivity.this);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
            if (objLimit.getTokenLimitFlag()) {
                tvHead.setVisibility(View.VISIBLE);
                Double week = 0.0, daily = 0.0;
                String strCurrency = "", strAmount = "";
                if (objLimit.getWeeklyAccountLimit() != null && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("NA") && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("unlimited")) {
                    week = Double.parseDouble(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Double.parseDouble(objLimit.getDailyAccountLimit());
                }
                if (strType.toLowerCase().equals("instant")) {
//                    strCurrency = " " + getString(R.string.currency);
                    strCurrency = " USD";
                } else {
                    strCurrency = " USD";
                }
                if ((week == 0 || week < 0) && daily > 0) {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
                    tvHead.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if ((daily == 0 || daily < 0) && week > 0) {
                    strLimit = "week";
                    maxValue = week;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(week));
                    tvHead.setText("Your weekly limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    tvHead.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + " USD");
                    strLimit = "unlimited";
                } else {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
                    tvHead.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                }
            } else {
                tvHead.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showCardDeletePopup(CardsDataItem objData) {
        try {
            ImageView imgClose;
            TextView tvHead, tvMessage, tvCancel;
            CardView cvRemove;
            popupDelete = new Dialog(BuyTokenActivity.this, R.style.DialogTheme);
            popupDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupDelete.setContentView(R.layout.deletepopup);
            Window window = popupDelete.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupDelete.getWindow().setAttributes(lp);
            popupDelete.show();
            imgClose = (ImageView) popupDelete.findViewById(R.id.imgClose);
            tvHead = (TextView) popupDelete.findViewById(R.id.tvHead);
            tvMessage = (TextView) popupDelete.findViewById(R.id.tvMessage);
            tvCancel = (TextView) popupDelete.findViewById(R.id.tvCancel);
            cvRemove = (CardView) popupDelete.findViewById(R.id.cvRemove);
            tvHead.setText("Remove " + Utils.capitalize(objData.getCardType()) + " Card");
            tvMessage.setText("Are you sure want to remove this " + Utils.capitalize(objData.getCardType()) + " Card");
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            cvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyViewModel.deleteCards(String.valueOf(objData.getId()));
                    dialog = new ProgressDialog(BuyTokenActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showBankDeletePopup(BanksDataItem objData) {
        try {
            ImageView imgClose;
            TextView tvHead, tvMessage, tvCancel;
            CardView cvRemove;
            popupDelete = new Dialog(BuyTokenActivity.this, R.style.DialogTheme);
            popupDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupDelete.setContentView(R.layout.deletepopup);
            Window window = popupDelete.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupDelete.getWindow().setAttributes(lp);
            popupDelete.show();
            imgClose = (ImageView) popupDelete.findViewById(R.id.imgClose);
            tvHead = (TextView) popupDelete.findViewById(R.id.tvHead);
            tvMessage = (TextView) popupDelete.findViewById(R.id.tvMessage);
            tvCancel = (TextView) popupDelete.findViewById(R.id.tvCancel);
            cvRemove = (CardView) popupDelete.findViewById(R.id.cvRemove);
            tvHead.setText("Remove Bank Account");
            tvMessage.setText("Are you sure want to remove this Bank Account");
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            cvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyViewModel.deleteBanks(String.valueOf(objData.getId()));
                    dialog = new ProgressDialog(BuyTokenActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editCard(CardsDataItem item) {
        try {
            objMyApplication.setSelectedCard(item);
            Intent i = new Intent(BuyTokenActivity.this, AddEditCardDetailsActivity.class);
            i.putExtra("subtype", getIntent().getStringExtra("subtype"));
            i.putExtra("type", getIntent().getStringExtra("type"));
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void USFormat(TextInputEditText etAmount) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            if (!strAmount.equals("")) {
                etAmount.removeTextChangedListener(BuyTokenActivity.this);
                etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                etAmount.setSelection(etAmount.getText().length());
                etAmount.addTextChangedListener(BuyTokenActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void formatValue() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            etPay.setFilters(FilterArray);
            USFormat(etPay);
            USFormat(etGet);
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            etPay.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
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
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BankDeleteResponseData;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.bank.SignOnData;
import com.coyni.android.model.bank.SyncAccount;
import com.coyni.android.model.transactions.LimitResponseData;
import com.coyni.android.model.transactions.TransactionLimitRequest;
import com.coyni.android.model.transactions.TransactionLimitResponse;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.SendViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.coyni.android.viewmodel.BuyViewModel;

import java.util.ArrayList;
import java.util.List;

public class WithdrawTokenActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    TextInputEditText etPay, etGet;
    SendViewModel sendViewModel;
    BuyViewModel buyViewModel;
    TextView tvHead, tvPFee, tvAdd, tvBankHead;
    LinearLayout layoutBuy;
    ImageView imgInfo;
    Double dget = 0.0, maxValue = 0.0;
    List<BanksDataItem> listBSorted;
    String strLimit = "";
    Boolean isBank = false, isDel = false, isSignet = false;
    SignOnData signOnData;
    TransactionLimitResponse objResponse;
    String strSignOn = "";
    Dialog popupDelete;
    ProgressDialog dialog;
    RecyclerView rvBank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_withdraw_token);
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
            if (isSignet) {
                if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                    dialog = new ProgressDialog(WithdrawTokenActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    buyViewModel.meBanks();
                } else {
                    Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this);
                }
            }
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(WithdrawTokenActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WithdrawTokenActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WithdrawTokenActivity.this, this, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", WithdrawTokenActivity.this);
                } else {
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
                    etGet.setText(s.toString());
//                    USFormat(etPay);
//                    USFormat(etGet);
                } else if (s.toString().equals(".")) {
                    etPay.setText("");
                } else if (s.length() == 0) {
                    etGet.setText(" ");
                    isDel = false;
                } else {
                    etPay.removeTextChangedListener(WithdrawTokenActivity.this);
                    etPay.setText("");
                    etPay.addTextChangedListener(WithdrawTokenActivity.this);
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
            Utils.statusBar(WithdrawTokenActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            etPay = (TextInputEditText) findViewById(R.id.etPay);
            etGet = (TextInputEditText) findViewById(R.id.etGet);
            layoutBuy = (LinearLayout) findViewById(R.id.layoutBuy);
            tvHead = (TextView) findViewById(R.id.tvHead);
            tvPFee = (TextView) findViewById(R.id.tvPFee);
            tvAdd = (TextView) findViewById(R.id.tvAdd);
            tvBankHead = (TextView) findViewById(R.id.tvBankHead);
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            CardView cvAddBank = (CardView) findViewById(R.id.cvAddBank);
            CardView cvInfo = (CardView) findViewById(R.id.cvInfo);
            imgInfo = (ImageView) findViewById(R.id.imgInfo);
            RelativeLayout layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            etPay.requestFocus();
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                buyViewModel.meBanks();
                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                    buyViewModel.meSignOn();
                } else {
                    strSignOn = objMyApplication.getStrSignOnError();
                    signOnData = objMyApplication.getSignOnData();
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this);
            }
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
                if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                    obj.setTransactionSubType(Integer.parseInt(Utils.bankType));
                    tvBankHead.setText("Bank Account");
                    tvAdd.setText("Add New Bank");
                } else {
                    obj.setTransactionSubType(Integer.parseInt(Utils.signetType));
                    tvBankHead.setText("Signet Account");
                    tvAdd.setText("Add Signet Account");
                }
                buyViewModel.transactionLimits(obj);
            } else {
                Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this);
            }

            layoutBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                        if (validation()) {
                            Intent i = new Intent(WithdrawTokenActivity.this, WithdrawTokenPreviewActivity.class);
                            i.putExtra("pay", etPay.getText().toString());
                            i.putExtra("get", String.valueOf(dget));
                            i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                            startActivity(i);
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this);
                    }
                }
            });

            cvAddBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                            UserTracker userTracker = objMyApplication.getUserTracker();
                            if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                                    && userTracker.getData().getPersonIdentified()) {
                                if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                    Intent i = new Intent(WithdrawTokenActivity.this, WebViewActivity.class);
                                    i.putExtra("signon", signOnData);
                                    startActivityForResult(i, 1);
                                } else {
                                    Utils.displayAlert(strSignOn, WithdrawTokenActivity.this);
                                }
                            } else {
                                Intent i = new Intent(WithdrawTokenActivity.this, MainActivity.class);
                                i.putExtra("withsig", "payment");
                                startActivity(i);
                            }
                        } else {
                            isSignet = true;
                            Intent i = new Intent(WithdrawTokenActivity.this, MainActivity.class);
                            i.putExtra("withsig", "signet");
                            i.putExtra("status", "add");
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
                    if (hasFocus && etPay.getText().toString().length() == 1) {
                        etPay.removeTextChangedListener(WithdrawTokenActivity.this);
                        etPay.setText("");
                        etPay.addTextChangedListener(WithdrawTokenActivity.this);
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

            imgInfo.setOnClickListener(new View.OnClickListener() {
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

//            layoutBank.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    formatValue();
//                    return false;
//                }
//            });

            if (Build.VERSION.SDK_INT >= 23) {
                scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        Utils.hideKeypad(WithdrawTokenActivity.this, v);
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
                            imgInfo.setVisibility(View.VISIBLE);
                        } else {
                            imgInfo.setVisibility(View.INVISIBLE);
                        }
                        objMyApplication.setTransferFeeResponse(transferFeeResponse);
//                        if (!etPay.getText().toString().equals("") && !etPay.getText().toString().equals("0")) {
//                            Double pay = Double.parseDouble(etPay.getText().toString().replace(",", ""));
//                            Double fee = transferFeeResponse.getData().getFee();
//                            dget = pay - fee;
//                            etGet.setText(Utils.convertBigDecimalUSDC(String.valueOf(dget)));
////                            USFormat(etPay);
////                            USFormat(etGet);
//                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getBanksMutableLiveData().observe(this, new Observer<Banks>() {
            @Override
            public void onChanged(Banks banks) {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (banks != null) {
                        List<BanksDataItem> banksByType = new ArrayList<>();
                        if (banks.getData().getItems() != null && banks.getData().getItems().size() > 0) {
                            List<BanksDataItem> listItems = banks.getData().getItems();
                            for (int i = 0; i < listItems.size(); i++) {
                                if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                                    if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("bank") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                        banksByType.add(banks.getData().getItems().get(i));
                                    }
                                } else {
                                    if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("signet") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                        banksByType.add(banks.getData().getItems().get(i));
                                    }
                                }
                            }
                        }
                        bindBanks(banksByType);
                        if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                            objMyApplication.setListBanks(banksByType);
                        } else {
                            objMyApplication.setSignetBanks(banksByType);
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
//                    Utils.displayAlert(bankDeleteResponseData.getData(), WithdrawTokenActivity.this);
//                    if (Utils.checkInternet(WithdrawTokenActivity.this)) {
//                        buyViewModel.meBanks();
//                    } else {
//                        Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this);
//                    }
//                }
                dialog.dismiss();
                popupDelete.dismiss();
                Context context = new ContextThemeWrapper(WithdrawTokenActivity.this, R.style.Theme_QuickCard);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                builder.setTitle(R.string.app_name);
                builder.setMessage(bankDeleteResponseData.getData());
                AlertDialog dilog = builder.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dilog.dismiss();
                        if (bankDeleteResponseData != null) {
                            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                                buyViewModel.meBanks();
                            } else {
                                Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this);
                            }
                        }
                    }
                }, Integer.parseInt(context.getString(R.string.closealert)));
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
                if (syncAccount != null) {
                    if (syncAccount.getStatus().toLowerCase().equals("success")) {
                        buyViewModel.meBanks();
//                        Utils.displayAlert("You added a new Bank Account to your profile!", WithdrawTokenActivity.this);
                        Utils.displayCloseAlert("You added a new Bank Account to your profile!", WithdrawTokenActivity.this);
                    }
                }
            }
        });

        buyViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (apiError != null) {
                    if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                        objMyApplication.setResolveUrl(true);
                        buyViewModel.meSignOn();
                    } else if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(WithdrawTokenActivity.this, getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawTokenActivity.this);
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), WithdrawTokenActivity.this);
                    }
                }
            }
        });
    }

    private void calculateFee() {
        try {
            if (!etPay.getText().toString().trim().equals("")) {
                TransferFeeRequest request = new TransferFeeRequest();
                request.setTokens(etPay.getText().toString().trim().replace(",", ""));
                request.setTxnType(Utils.withdrawType);
                if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                    request.setTxnSubType(Utils.bankType);
                } else {
                    request.setTxnSubType(Utils.signetType);
                }
                if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                    sendViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            String strPay = etPay.getText().toString().trim().replace("\"", "").replace(",", "");
            if (strPay.equals("") || Double.parseDouble(strPay) == 0.0) {
                Utils.displayAlert("Withdraw amount is required", WithdrawTokenActivity.this);
                return value = false;
            } else if ((listBSorted != null && listBSorted.size() == 0) || !isBank) {
                Utils.displayAlert("Please select Account", WithdrawTokenActivity.this);
                return value = false;
            } else if (Double.parseDouble(strPay) < objResponse.getData().getMinimumLimit()) {
                Utils.displayAlert("Amount should be greater than or equal to " + objResponse.getData().getMinimumLimit() + " " + getString(R.string.currency), WithdrawTokenActivity.this);
                return value = false;
            } else if (objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(strPay) > maxValue) {
                if (strLimit.equals("daily")) {
                    Utils.displayAlert("Amount exceeds daily limit", WithdrawTokenActivity.this);
                } else if (strLimit.equals("week")) {
                    Utils.displayAlert("Amount exceeds weekly limit", WithdrawTokenActivity.this);
                }
                return value = false;
            } else if (Double.parseDouble(strPay) > objMyApplication.getGBTBalance()) {
                Utils.displayAlert(getString(R.string.walletBalValidation), WithdrawTokenActivity.this);
                return value = false;
            } else {
                TransferFeeResponse transferFeeResponse = objMyApplication.getTransferFeeResponse();
                Double total, yPay;
                yPay = Double.parseDouble(strPay);
                total = yPay + transferFeeResponse.getData().getFee();
                if (total > objMyApplication.getGBTBalance()) {
                    Utils.displayAlert("Insufficient balance for this transaction.", WithdrawTokenActivity.this);
                    return value = false;
                } else if (objResponse.getData().getTokenLimitFlag() && total > maxValue) {
                    //Utils.displayAlert("Insufficient balance for this transaction.", WithdrawTokenActivity.this);
                    if (strLimit.equals("daily")) {
                        Utils.displayAlert("Amount exceeds daily limit", WithdrawTokenActivity.this);
                    } else if (strLimit.equals("week")) {
                        Utils.displayAlert("Amount exceeds weekly limit", WithdrawTokenActivity.this);
                    }
                    return value = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void bindBanks(List<BanksDataItem> listBanks) {
        BanksListAdapter banksListAdapter;
        try {
            rvBank = (RecyclerView) findViewById(R.id.rvBank);
            if (listBanks != null && listBanks.size() > 0) {
                rvBank.setVisibility(View.VISIBLE);
                banksListAdapter = new BanksListAdapter(sortBank(listBanks), WithdrawTokenActivity.this, "withdraw");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(WithdrawTokenActivity.this);
                rvBank.setLayoutManager(mLayoutManager);
                rvBank.setItemAnimator(new DefaultItemAnimator());
                rvBank.setAdapter(banksListAdapter);
            } else {
                rvBank.setVisibility(View.GONE);
                listBSorted = new ArrayList<>();
                listBSorted = listBanks;
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

    public void bankSelected(Boolean value) {
        isBank = value;
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
            if (objLimit.getTokenLimitFlag()) {
                tvHead.setVisibility(View.VISIBLE);
                Double week = 0.0, daily = 0.0;
//                String strCurrency = " " + getString(R.string.currency), strAmount = "";
                String strCurrency = " USD", strAmount = "";
                if (objLimit.getWeeklyAccountLimit() != null && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("NA") && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("unlimited")) {
                    week = Double.parseDouble(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Double.parseDouble(objLimit.getDailyAccountLimit());
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

    public void showBankDeletePopup(BanksDataItem objData) {
        try {
            ImageView imgClose;
            TextView tvHead, tvMessage, tvCancel;
            CardView cvRemove;
            popupDelete = new Dialog(WithdrawTokenActivity.this, R.style.DialogTheme);
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
            tvHead.setText("Remove " + objData.getAccountCategory().replace("Account", "") + " Account");
            tvMessage.setText("Are you sure want to remove this " + objData.getAccountCategory().replace("Account", "") + " Account");
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
                    dialog = new ProgressDialog(WithdrawTokenActivity.this, R.style.MyAlertDialogStyle);
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

    public void editBank(BanksDataItem objData) {
        try {
            objMyApplication.setEditSignet(objData);
            isSignet = true;
            Intent i = new Intent(WithdrawTokenActivity.this, MainActivity.class);
            i.putExtra("withsig", "signet");
            i.putExtra("status", "edit");
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void callResolveFlow() {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                Intent i = new Intent(WithdrawTokenActivity.this, WebViewActivity.class);
                i.putExtra("signon", signOnData);
                startActivityForResult(i, 1);
            } else {
                Utils.displayAlert(strSignOn, WithdrawTokenActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void USFormat(TextInputEditText etAmount) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(WithdrawTokenActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.setSelection(etAmount.getText().length() - 3);
            etAmount.addTextChangedListener(WithdrawTokenActivity.this);
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
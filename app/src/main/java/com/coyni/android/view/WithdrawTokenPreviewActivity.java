package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.model.APIError;
import com.coyni.android.model.GlideApp;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.giftcard.Brand;
import com.coyni.android.model.giftcard.Items;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.withdraw.GiftCardWithDrawInfo;
import com.coyni.android.model.withdraw.RecipientDetail;
import com.coyni.android.model.withdraw.WithdrawRequest;
import com.coyni.android.model.withdraw.WithdrawResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;

import java.util.ArrayList;
import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import in.shadowfax.proswipebutton.ProSwipeButton;

public class WithdrawTokenPreviewActivity extends AppCompatActivity {
    RelativeLayout layoutProgress, layoutHead;
    MyApplication objMyApplication;
    int i = 0;
    ProSwipeButton proSwipeBtn;
    CircularProgressIndicator circularProgress;
    TransferFeeResponse transferFeeResponse;
    BuyViewModel buyViewModel;
    TextView tvAmount, tvProcessingFee, tvTotal, tvCardNo, tvBalance, tvType, tvAmtHead;
    TextView tvEmail, tvRemBalance, tvGiftAmount, tvBank;
    String strBankId = "0", strCardId = "0", strType = "", strTansId = "", strBank = "";
    Double amount = 0.0;
    Brand objBrand;
    Items selectedItem;
    Boolean isProgress = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_withdraw_token_preview);
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
    public void onBackPressed() {
        if (!isProgress) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        try {
            layoutHead.setVisibility(View.VISIBLE);
            layoutProgress.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(WithdrawTokenPreviewActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WithdrawTokenPreviewActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WithdrawTokenPreviewActivity.this, this, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (Utils.checkInternet(WithdrawTokenPreviewActivity.this)) {
                        if (getIntent().getStringExtra("screen") == null) {
                            withdrawToken();
                        } else {
                            withdrawGiftCardToken();
                        }
                        progressScreen();
                    } else {
                        Utils.displayAlert(getString(R.string.internet), WithdrawTokenPreviewActivity.this);
                    }
                }
            });
        } else {
            Utils.displayAlert("Failure: Unable to verify user's identity", WithdrawTokenPreviewActivity.this);
        }
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(WithdrawTokenPreviewActivity.this);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            circularProgress = findViewById(R.id.circular_progress);
            layoutHead = (RelativeLayout) findViewById(R.id.layoutHead);
            layoutProgress = (RelativeLayout) findViewById(R.id.layoutProgress);
            tvProcessingFee = (TextView) findViewById(R.id.tvProcessingFee);
            RelativeLayout layoutBank = (RelativeLayout) findViewById(R.id.layoutBank);
            RelativeLayout layoutGift = (RelativeLayout) findViewById(R.id.layoutGift);
            ImageView imgLogo;
            tvAmount = (TextView) findViewById(R.id.tvAmount);
            tvTotal = (TextView) findViewById(R.id.tvTotal);
            tvCardNo = (TextView) findViewById(R.id.tvCardNo);
            tvType = (TextView) findViewById(R.id.tvType);
            tvBank = (TextView) findViewById(R.id.tvBank);
            imgLogo = (ImageView) findViewById(R.id.imgLogo);
            tvBalance = (TextView) findViewById(R.id.tvBalance);
            tvAmtHead = (TextView) findViewById(R.id.tvAmtHead);
            proSwipeBtn = (ProSwipeButton) findViewById(R.id.btnConfirm);
            proSwipeBtn.setSwipeDistance(0.6f);
            proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                @Override
                public void onSwipeConfirm() {
                    // user has swiped the btn. Perform your async operation now
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            proSwipeBtn.showResultIcon(true, true);
                            if (Utils.checkInternet(WithdrawTokenPreviewActivity.this)) {
                                if (objMyApplication.getCoyniPin()) {
                                    Utils.checkAuthentication(WithdrawTokenPreviewActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    if (getIntent().getStringExtra("screen") == null) {
                                        withdrawToken();
                                    } else {
                                        withdrawGiftCardToken();
                                    }
                                    progressScreen();
                                }
                            } else {
                                Utils.displayAlert(getString(R.string.internet), WithdrawTokenPreviewActivity.this);
                            }
                        }
                    }, 500);

                }
            });

            if (getIntent().getStringExtra("screen") == null) {
                layoutBank.setVisibility(View.VISIBLE);
                layoutGift.setVisibility(View.GONE);
                if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                    tvAmtHead.setText(getString(R.string.youget));
                } else {
                    tvAmtHead.setText(getString(R.string.youget));
                    tvBank.setVisibility(View.GONE);
                }
                imgLogo.setBackgroundResource(R.drawable.ic_withdraw_trans);
                if (objMyApplication.getSelectedBank() != null) {
                    BanksDataItem selectBank = objMyApplication.getSelectedBank();
                    if (selectBank.getAccountCategory().toLowerCase().equals("signet")) {
                        String actNum = "";
                        if (selectBank.getAccoutNumber() != null && selectBank.getAccoutNumber().length() > 4) {
                            actNum = "*** " + selectBank.getAccoutNumber().substring(selectBank.getAccoutNumber().length() - 4);
                        } else {
                            actNum = selectBank.getAccoutNumber();
                        }
                        tvCardNo.setText("Signet Wallet ID :" + actNum);
                    } else {
                        if (selectBank.getBankName() != null && selectBank.getBankName().length() > 10) {
                            tvBank.setText(selectBank.getBankName().substring(0, 10) + "...");
                        } else {
                            tvBank.setText(selectBank.getBankName());
                        }
                        strBank = selectBank.getBankName();
                        if (selectBank.getAccoutNumber().length() > 4) {
                            tvCardNo.setText("**** " + selectBank.getAccoutNumber().substring(selectBank.getAccoutNumber().length() - 4));
                        } else {
                            tvCardNo.setText("**** " + selectBank.getAccoutNumber());
                        }
                    }
                    strBankId = String.valueOf(selectBank.getId());
                }

                if (getIntent().getStringExtra("type") != null && !getIntent().getStringExtra("type").equals("")) {
                    strType = getIntent().getStringExtra("type");
                }
                transferFeeResponse = objMyApplication.getTransferFeeResponse();
                Double total = 0.0, pfee = 0.0, bal = 0.0, amt = 0.0;
                if (transferFeeResponse != null) {
                    pfee = transferFeeResponse.getData().getFee();
                    if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                        tvProcessingFee.setText(Utils.USNumberFormat((pfee)) + " USD");
                    } else {
                        tvProcessingFee.setText(Utils.USNumberFormat((pfee)) + " " + getString(R.string.currency));
                    }
                } else {
                    pfee = 2.0;
                }

                if (getIntent().getStringExtra("pay") != null && !getIntent().getStringExtra("pay").equals("")) {
                    amt = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""));
                    tvAmount.setText(Utils.USNumberFormat(amt));
                    amount = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""));
                    if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                        tvType.setText(" " + getString(R.string.currency));
                    } else {
                        tvType.setText(" USD");
                    }
                    total = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", "")) + pfee;
                    if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                        tvTotal.setText(Utils.convertBigDecimalUSDC(getIntent().getStringExtra("pay")) + " USD");
                    } else {
//                        tvTotal.setText(Utils.convertBigDecimalUSDC(getIntent().getStringExtra("pay")) + " " + getString(R.string.currency));
                        tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
                    }
                }

                if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                    bal = amt + objMyApplication.getGBTBalance();
                } else {
                    bal = objMyApplication.getGBTBalance() - total;
                }
                tvBalance.setText(Utils.USNumberFormat((bal)) + " " + getString(R.string.currency));

            } else if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                layoutBank.setVisibility(View.GONE);
                layoutGift.setVisibility(View.VISIBLE);
                tvAmtHead.setText("Gift Card Amount");
                imgLogo.setBackgroundResource(R.drawable.ic_withdraw_gift);
                tvEmail = (TextView) findViewById(R.id.tvEmail);
                TextView tvBrand = (TextView) findViewById(R.id.tvBrand);
                tvGiftAmount = (TextView) findViewById(R.id.tvGiftAmount);
                TextView tvGiftPrFee = (TextView) findViewById(R.id.tvGiftPrFee);
                TextView tvGiftTotal = (TextView) findViewById(R.id.tvGiftTotal);
                tvRemBalance = (TextView) findViewById(R.id.tvRemBalance);
                ImageView imgBrand = (ImageView) findViewById(R.id.imgBrand);
                Double amt = 0.0, fee = 0.0, total = 0.0, bal = 0.0;
                if (getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").equals("")) {
                    tvEmail.setText(getIntent().getStringExtra("email"));
                }
                if (getIntent().getSerializableExtra("brand") != null && !getIntent().getSerializableExtra("brand").equals("")) {
                    objBrand = (Brand) getIntent().getSerializableExtra("brand");
                    tvBrand.setText("For " + objBrand.getBrandName());
                    if (objBrand.getImageUrls().get_80w326ppi() != null && !objBrand.getImageUrls().get_80w326ppi().equals("")) {
                        GlideApp.with(WithdrawTokenPreviewActivity.this)
                                .load(objBrand.getImageUrls().get_80w326ppi())
                                .into(imgBrand);
                    }
                }
                if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                    amt = Double.parseDouble(getIntent().getStringExtra("amount"));
                    tvGiftAmount.setText(Utils.USNumberFormat(amt) + " USD");
                }
                if (getIntent().getStringExtra("fee") != null && !getIntent().getStringExtra("fee").equals("")) {
                    fee = Double.parseDouble(getIntent().getStringExtra("fee"));
                    tvGiftPrFee.setText(Utils.USNumberFormat(fee) + " " + getString(R.string.currency));
                }
                if (getIntent().getSerializableExtra("selectedItem") != null && !getIntent().getSerializableExtra("selectedItem").equals("")) {
                    selectedItem = (Items) getIntent().getSerializableExtra("selectedItem");
                }
                total = amt + fee;
//                tvAmount.setText(Utils.convertBigDecimalUSDC(String.valueOf(total)));
                tvAmount.setText(Utils.USNumberFormat(amt));
                tvGiftTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
                bal = objMyApplication.getGBTBalance() - total;
                tvRemBalance.setText(Utils.USNumberFormat(bal) + " " + getString(R.string.currency));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyViewModel.getWithTokResponseMutableLiveData().observe(this, new Observer<WithdrawResponse>() {
            @Override
            public void onChanged(WithdrawResponse withdrawResponse) {
                if (withdrawResponse != null && withdrawResponse.getStatus().toLowerCase().equals("success")) {
                    strTansId = withdrawResponse.getData().getGbxTransactionId();
                } else {

                }
            }
        });

        buyViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    isProgress = false;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    layoutHead.setVisibility(View.VISIBLE);
                    layoutProgress.setVisibility(View.GONE);
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                                objMyApplication.displayAlert(WithdrawTokenPreviewActivity.this, getString(R.string.session));
                            } else {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), WithdrawTokenPreviewActivity.this);
                            }
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), WithdrawTokenPreviewActivity.this);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void progressScreen() {
        try {
            CountDownTimer mCountDownTimer;
            circularProgress.setCurrentProgress(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            layoutHead.setVisibility(View.GONE);
            layoutProgress.setVisibility(View.VISIBLE);
            isProgress = true;
            mCountDownTimer = new CountDownTimer(10000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    i++;
                    circularProgress.setCurrentProgress((int) i * 100 / (10000 / 1000));

                }

                @Override
                public void onFinish() {
                    //Do what you want
                    i++;
                    circularProgress.setCurrentProgress(100);
                    if (!strTansId.equals("")) {
                        Intent i = new Intent(WithdrawTokenPreviewActivity.this, WithdrawTokenProgressActivity.class);
                        if (getIntent().getStringExtra("screen") == null) {
                            i.putExtra("amount", tvAmount.getText().toString());
                            i.putExtra("balance", tvBalance.getText().toString());
                            i.putExtra("cardno", tvCardNo.getText().toString());
                            i.putExtra("transId", strTansId);
                            i.putExtra("screen", strType);
                            i.putExtra("bank", strBank);
                            i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                        } else {
                            i.putExtra("screen", getIntent().getStringExtra("screen"));
                            i.putExtra("giftamt", tvAmount.getText().toString().trim());
                            i.putExtra("balance", tvRemBalance.getText().toString());
                            i.putExtra("email", tvEmail.getText().toString());
                            i.putExtra("orderId", strTansId);
                            i.putExtra("brand", objBrand);
                            i.putExtra("brandamt", tvGiftAmount.getText().toString().trim());
                        }
                        startActivity(i);
                    } else {
                        isProgress = false;
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
                        layoutHead.setVisibility(View.VISIBLE);
                        layoutProgress.setVisibility(View.GONE);
                        //Utils.displayAlert("Unable to process your transaction.", WithdrawTokenPreviewActivity.this);
                    }
                }
            };
            mCountDownTimer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawToken() {
        try {
            WithdrawRequest request = new WithdrawRequest();
//            request.setBankId(strBankId);
//            request.setCardId(strCardId);
            request.setBankId(Long.parseLong(strBankId));
            request.setCardId(Long.parseLong(strCardId));
            request.setGiftCardWithDrawInfo(null);
//            request.setTokens(Utils.convertBigDecimalUSDC(String.valueOf(amount)));
            request.setTokens(amount);
            if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                request.setWithdrawType(Utils.bankType);
            } else {
                request.setSignetWalletId(objMyApplication.getSelectedBank().getAccoutNumber());
                request.setWithdrawType(Utils.signetType);
            }
            if (Utils.checkInternet(WithdrawTokenPreviewActivity.this)) {
                buyViewModel.withdrawTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawGiftCardToken() {
        try {
            if (objBrand != null) {
                WithdrawRequest request = new WithdrawRequest();
//                request.setBankId("0");
//                request.setCardId("0");
                request.setBankId(Long.parseLong("0"));
                request.setCardId(Long.parseLong("0"));
                RecipientDetail objReceipient = new RecipientDetail();
                GiftCardWithDrawInfo objGift = new GiftCardWithDrawInfo();
                List<RecipientDetail> recipientDetails = new ArrayList<>();
                if (getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").equals("")) {
                    objReceipient.setEmail(getIntent().getStringExtra("email"));
                }
                if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                    objReceipient.setAmount(Double.parseDouble(getIntent().getStringExtra("amount")));
                }
                if (getIntent().getStringExtra("fName") != null && !getIntent().getStringExtra("fName").equals("")) {
                    objReceipient.setFirstName(getIntent().getStringExtra("fName"));
                }
                if (getIntent().getStringExtra("lName") != null && !getIntent().getStringExtra("lName").equals("")) {
                    objReceipient.setLastName(getIntent().getStringExtra("lName"));
                }
                recipientDetails.add(objReceipient);
                objGift.setRecipientDetails(recipientDetails);
                objGift.setGiftCardCurrency(selectedItem.getCurrencyCode());
                objGift.setGiftCardName(objBrand.getBrandName());
                objGift.setTotalAmount(Double.parseDouble(getIntent().getStringExtra("amount")));
                objGift.setUtid(selectedItem.getUtid());
                request.setGiftCardWithDrawInfo(objGift);
//                request.setTokens(getIntent().getStringExtra("amount"));
                request.setTokens(Double.parseDouble(getIntent().getStringExtra("amount")));
                request.setWithdrawType(Utils.giftcardType);
                if (Utils.checkInternet(WithdrawTokenPreviewActivity.this)) {
                    buyViewModel.withdrawTokens(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
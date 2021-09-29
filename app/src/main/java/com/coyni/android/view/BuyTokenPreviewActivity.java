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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.buytoken.BuyTokenFailure;
import com.coyni.android.model.buytoken.BuyTokenRequest;
import com.coyni.android.model.buytoken.BuyTokenResponse;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.withdraw.WithdrawRequest;
import com.coyni.android.model.withdraw.WithdrawResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import in.shadowfax.proswipebutton.ProSwipeButton;

public class BuyTokenPreviewActivity extends AppCompatActivity {
    RelativeLayout layoutProgress, layoutHead;
    MyApplication objMyApplication;
    int i = 0;
    ProSwipeButton proSwipeBtn;
    CircularProgressIndicator circularProgress;
    TransferFeeResponse transferFeeResponse;
    BuyViewModel buyViewModel;
    TextView tvAmount, tvProcessingFee, tvTotal, tvCardNo, tvBalance, tvTitle, tvType, tvAmtHead, tvSending, tvBank, tvSubMsg;
    ImageView imgCard;
    String strBankId = "", strCardId = "", strSubType = "", strType = "", strTransId = "";
    Double amount = 0.0, total = 0.0;
    CardsDataItem selectedCard;
    BanksDataItem selectedBank;
    Boolean isProgress = false;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    TextView tvPayHead, tvPayAmtHead, tvBalHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_buy_token_preview);
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
            objMyApplication.userInactive(BuyTokenPreviewActivity.this, this, false);
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
        objMyApplication.userInactive(BuyTokenPreviewActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(BuyTokenPreviewActivity.this, this, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (Utils.checkInternet(BuyTokenPreviewActivity.this)) {
                        if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                            buyToken();
                        } else {
                            withdrawToken();
                        }
                        progressScreen();
                    } else {
                        Utils.displayAlert(getString(R.string.internet), BuyTokenPreviewActivity.this);
                    }
                }
            });
        } else {
            Utils.displayAlert("Failure: Unable to verify user's identity", BuyTokenPreviewActivity.this);
        }
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(BuyTokenPreviewActivity.this);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            circularProgress = findViewById(R.id.circular_progress);
            layoutHead = (RelativeLayout) findViewById(R.id.layoutHead);
            layoutProgress = (RelativeLayout) findViewById(R.id.layoutProgress);
            tvProcessingFee = (TextView) findViewById(R.id.tvProcessingFee);
            tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
            tvAmount = (TextView) findViewById(R.id.tvAmount);
            tvTotal = (TextView) findViewById(R.id.tvTotal);
            tvCardNo = (TextView) findViewById(R.id.tvCardNo);
            tvType = (TextView) findViewById(R.id.tvType);
            tvSending = (TextView) findViewById(R.id.tvSending);
            tvSubMsg = (TextView) findViewById(R.id.tvSubMsg);
            imgCard = (ImageView) findViewById(R.id.imgCard);
            tvBalance = (TextView) findViewById(R.id.tvBalance);
            tvAmtHead = (TextView) findViewById(R.id.tvAmtHead);
            tvBank = (TextView) findViewById(R.id.tvBank);
            tvPayHead = (TextView) findViewById(R.id.tvPayHead);
            tvPayAmtHead = (TextView) findViewById(R.id.tvPayAmtHead);
            tvBalHead = (TextView) findViewById(R.id.tvBalHead);
            if (objMyApplication.getSelectedCard() != null) {
                selectedCard = objMyApplication.getSelectedCard();
                strCardId = String.valueOf(selectedCard.getId());
                if (selectedCard.getCardBrand().toLowerCase().equals("visa")) {
                    imgCard.setImageResource(R.drawable.ic_visa);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("master")) {
                    imgCard.setImageResource(R.drawable.ic_master);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("american")) {
                    imgCard.setImageResource(R.drawable.ic_amex);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("discover")) {
                    imgCard.setImageResource(R.drawable.ic_discover);
                }
                tvCardNo.setText("**** " + selectedCard.getLastFour());
            }
            if (getIntent().getStringExtra("type") != null && !getIntent().getStringExtra("type").equals("")) {
                strType = getIntent().getStringExtra("type");
            }
            if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                tvTitle.setText("Buy Token");
                tvAmtHead.setText(getString(R.string.youget));
                tvSending.setText("Processing Transaction");
                tvSubMsg.setText("Please Standby");
                tvPayHead.setText(getString(R.string.paymethod));
                tvPayAmtHead.setText("You Pay Amount");
                tvBalHead.setText(getString(R.string.estbal));
                if (strType.equals("bank")) {
                    tvBalHead.setVisibility(View.GONE);
                    tvBalance.setVisibility(View.GONE);
                } else {
                    tvBalHead.setVisibility(View.VISIBLE);
                    tvBalance.setVisibility(View.VISIBLE);
                }
            } else {
                tvTitle.setText("Withdraw Tokens");
                tvAmtHead.setText(getString(R.string.youget));
                tvSending.setText("Processing Transaction");
                tvSubMsg.setText("Please Standby");
                tvPayHead.setText(getString(R.string.paymethod));
                tvPayAmtHead.setText("Grand Total");
                tvBalHead.setText(getString(R.string.estbal));
            }
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
                            if (Utils.checkInternet(BuyTokenPreviewActivity.this)) {
                                if (objMyApplication.getCoyniPin()) {
                                    Utils.checkAuthentication(BuyTokenPreviewActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                                        buyToken();
                                    } else {
                                        withdrawToken();
                                    }
                                    progressScreen();
                                }
                            } else {
                                Utils.displayAlert(getString(R.string.internet), BuyTokenPreviewActivity.this);
                            }
                        }
                    }, 500);
                }
            });
            transferFeeResponse = objMyApplication.getTransferFeeResponse();
            Double pfee = 0.0, bal = 0.0, amt = 0.0;
            if (transferFeeResponse != null) {
                pfee = transferFeeResponse.getData().getFee();
//                tvProcessingFee.setText(Utils.convertBigDecimalUSDC(String.valueOf(pfee)) + " " + getString(R.string.currency));
                tvProcessingFee.setText(Utils.USNumberFormat(pfee) + " " + getString(R.string.currency));
            } else {
                pfee = 2.0;
            }
            if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                if (getIntent().getStringExtra("get") != null && !getIntent().getStringExtra("get").equals("")) {
                    String strAmt = "";
                    strAmt = Utils.convertBigDecimalUSDC(getIntent().getStringExtra("get").replace(",", ""));
                    tvAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmt)));
                    amt = Double.parseDouble(getIntent().getStringExtra("get").replace(",", ""));
                    amount = Double.parseDouble(getIntent().getStringExtra("get").replace(",", ""));
//                if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
//                    tvType.setText(" " + getString(R.string.currency));
//                } else {
//                    tvType.setText(" USD");
//                }
                    tvType.setText(" " + getString(R.string.currency));
                }
            } else {
                if (getIntent().getStringExtra("pay") != null && !getIntent().getStringExtra("pay").equals("")) {
                    String strAmt = "";
                    strAmt = Utils.convertBigDecimalUSDC(getIntent().getStringExtra("pay").replace(",", ""));
                    tvAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmt)));
                    amt = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""));
                    amount = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""));
                }
                tvType.setText(" USD");
            }
            if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                if (getIntent().getStringExtra("pay") != null && !getIntent().getStringExtra("pay").equals("")) {
//                if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
//                    tvTotal.setText(getIntent().getStringExtra("pay") + " USD");
//                } else {
//                    tvTotal.setText(getIntent().getStringExtra("pay") + " " + getString(R.string.currency));
//                }
                    tvTotal.setText(getIntent().getStringExtra("pay") + " USD");
                    total = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""));
                }
            } else {
                total = amount + pfee;
                tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
            }
            if (strType != null && (strType.equals("card") || strType.equals("bank"))) {
                bal = amt + objMyApplication.getGBTBalance();
            } else {
                bal = objMyApplication.getGBTBalance() - total;
            }
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
            imgCard.setVisibility(View.VISIBLE);
            tvBank.setVisibility(View.GONE);
            if (strType != null && strType.equals("card") && getIntent().getStringExtra("cardtype") != null && getIntent().getStringExtra("cardtype").equals("debit")) {
                strSubType = Utils.debitType;
            } else if (strType != null && strType.equals("card") && getIntent().getStringExtra("cardtype") != null && getIntent().getStringExtra("cardtype").equals("credit")) {
                strSubType = Utils.creditType;
            } else if (strType != null && strType.equals("bank")) {
                strSubType = Utils.bankType;
//                imgCard.setImageResource(R.drawable.ic_boa);
                imgCard.setVisibility(View.GONE);
                tvBank.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tvCardNo.setLayoutParams(params);
                if (objMyApplication.getSelectedBank() != null) {
                    selectedBank = objMyApplication.getSelectedBank();
                    strBankId = String.valueOf(selectedBank.getId());
                    if (selectedBank.getAccoutNumber().length() > 4) {
                        tvCardNo.setText("**** " + selectedBank.getAccoutNumber().substring(selectedBank.getAccoutNumber().length() - 4));
                    } else {
                        tvCardNo.setText("**** " + selectedBank.getAccoutNumber());
                    }
                    if (selectedBank.getBankName() != null && selectedBank.getBankName().length() > 10) {
                        tvBank.setText(selectedBank.getBankName().substring(0, 10) + "...");
                    } else {
                        tvBank.setText(selectedBank.getBankName());
                    }
                }
            } else {
                strSubType = Utils.instantType;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyViewModel.getBuyTokResponseMutableLiveData().observe(this, new Observer<BuyTokenResponse>() {
            @Override
            public void onChanged(BuyTokenResponse buyTokenResponse) {
                if (buyTokenResponse != null) {
                    strTransId = buyTokenResponse.getData().getGbxTransactionId();
                }
            }
        });
        buyViewModel.getWithTokResponseMutableLiveData().observe(this, new Observer<WithdrawResponse>() {
            @Override
            public void onChanged(WithdrawResponse withdrawResponse) {
                if (withdrawResponse != null && withdrawResponse.getStatus().toLowerCase().equals("success")) {
                    strTransId = withdrawResponse.getData().getGbxTransactionId();
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
                                objMyApplication.displayAlert(BuyTokenPreviewActivity.this, getString(R.string.session));
                            } else {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), BuyTokenPreviewActivity.this);
                            }
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), BuyTokenPreviewActivity.this);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getBuyTokenFailureMutableLiveData().observe(this, new Observer<BuyTokenFailure>() {
            @Override
            public void onChanged(BuyTokenFailure buyTokenFailure) {
                if (buyTokenFailure != null) {
                    try {
                        isProgress = false;
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
                        layoutHead.setVisibility(View.VISIBLE);
                        layoutProgress.setVisibility(View.GONE);
                        Utils.displayAlert("Unable to process the transaction.", BuyTokenPreviewActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
                    if (!strTransId.equals("")) {
                        Intent i = new Intent(BuyTokenPreviewActivity.this, BuyTokenProgressActivity.class);
                        i.putExtra("amount", tvAmount.getText().toString());
                        if (strType.equals("card") || strType.equals("withdraw")) {
                            i.putExtra("type", selectedCard.getCardBrand().toLowerCase());
                            i.putExtra("cardtype", selectedCard.getCardType().toLowerCase());
                        } else if (strType.equals("bank")) {
                            i.putExtra("type", "bank");
                            i.putExtra("bank", selectedBank.getBankName());
                        }
                        i.putExtra("balance", tvBalance.getText().toString());
                        i.putExtra("cardno", tvCardNo.getText().toString());
                        i.putExtra("transId", strTransId);
                        i.putExtra("screen", strType);
                        startActivity(i);
                    } else {
                        isProgress = false;
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
                        layoutHead.setVisibility(View.VISIBLE);
                        layoutProgress.setVisibility(View.GONE);
                    }
                }
            };
            mCountDownTimer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyToken() {
        try {
            BuyTokenRequest request = new BuyTokenRequest();
            request.setBankId(strBankId);
            request.setCardId(strCardId);
            request.setCvc(objMyApplication.getStrCvv());
            request.setTokens(Utils.convertBigDecimalUSDC(String.valueOf(total)));
            request.setTxnSubType(strSubType);
            if (Utils.checkInternet(BuyTokenPreviewActivity.this)) {
                buyViewModel.buyTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawToken() {
        try {
            WithdrawRequest request = new WithdrawRequest();
//            request.setBankId(strBankId);
//            request.setCardId(strCardId);
            if (!strBankId.equals("")) {
                request.setBankId(Long.parseLong(strBankId));
            } else {
                request.setBankId(Long.parseLong("0"));
            }
            request.setCardId(Long.parseLong(strCardId));
            request.setGiftCardWithDrawInfo(null);
//            request.setTokens(Utils.convertBigDecimalUSDC(String.valueOf(amount)));
            request.setTokens(amount);
            request.setWithdrawType(Utils.instantType);
            if (Utils.checkInternet(BuyTokenPreviewActivity.this)) {
                buyViewModel.withdrawTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
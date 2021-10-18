package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.model.transactions.buycreditcard.BuyCreditCard;
import com.coyni.android.model.transactions.buycreditcard.BuyCreditCardData;
import com.coyni.android.model.transactions.payrequest.PayRequest;
import com.coyni.android.model.transactions.payrequest.PayRequestData;
import com.coyni.android.model.transactions.withdraw.WithdrawGiftCard;
import com.coyni.android.model.transactions.withdraw.WithdrawGiftCardData;
import com.coyni.android.model.transactions.withdraw.WithdrawSignet;
import com.coyni.android.model.transactions.withdraw.WithdrawSignetData;
import com.coyni.android.model.transactions.withdraw.WithdrawTransDetails;
import com.coyni.android.model.transactions.withdraw.WithdrawTransDetailsData;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.TransDetailsViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TransactionDetailsActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    TransDetailsViewModel transDetailsViewModel;
    String strGbxId = "", strTransType = "", strTransSubtype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transaction_details);
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
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(TransactionDetailsActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(TransactionDetailsActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(TransactionDetailsActivity.this, this, false);
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(TransactionDetailsActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            transDetailsViewModel = new ViewModelProvider(this).get(TransDetailsViewModel.class);
            if (getIntent().getStringExtra("gbxTxnId") != null && !getIntent().getStringExtra("gbxTxnId").equals("")) {
                strGbxId = getIntent().getStringExtra("gbxTxnId");
            }
            if (getIntent().getStringExtra("txnType") != null && !getIntent().getStringExtra("txnType").equals("")) {
                getTransType(getIntent().getStringExtra("txnType"));
            }
            if (getIntent().getStringExtra("txnSubType") != null && !getIntent().getStringExtra("txnSubType").equals("")) {
                getTransSubType(getIntent().getStringExtra("txnSubType"));
            }
            if (Utils.checkInternet(TransactionDetailsActivity.this)) {
                if (getIntent().getStringExtra("txnType").toLowerCase().replace(" ", "").equals("buytoken")) {
                    transDetailsViewModel.getBuyCardTransDetails(strGbxId, strTransType, strTransSubtype);
                } else if (getIntent().getStringExtra("txnType").toLowerCase().replace(" ", "").equals("pay/request")) {
                    transDetailsViewModel.getPayReqTransDetails(strGbxId, strTransType, strTransSubtype);
                } else {
                    String strType = getIntent().getStringExtra("txnSubType");
                    if (strType != null && strType.toLowerCase().replace(" ", "").equals("giftcard")) {
                        transDetailsViewModel.getGiftTransDetails(strGbxId, strTransType, strTransSubtype);
                    } else if (strType != null && (strType.toLowerCase().replace(" ", "").equals("bankaccount") || strType.toLowerCase().replace(" ", "").equals("signet"))) {
                        transDetailsViewModel.getSignetTransDetails(strGbxId, strTransType, strTransSubtype);
                    } else if (getIntent().getStringExtra("txnSubType") != null) {
                        transDetailsViewModel.getWithdrawTransDetails(strGbxId, strTransType, strTransSubtype);
                    }
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), TransactionDetailsActivity.this);
            }
            objMyApplication.setFromWhichFragment("transdetails");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        transDetailsViewModel.getBuyCreditCardMutableLiveData().observe(this, new Observer<BuyCreditCard>() {
            @Override
            public void onChanged(BuyCreditCard buyCreditCard) {
                if (buyCreditCard != null) {
                    if (buyCreditCard.getStatus().toUpperCase().equals("SUCCESS")) {
                        enableLayout("buy");
                        bindCardTransaction(buyCreditCard.getData());
                    }
                }
            }
        });

        transDetailsViewModel.getPayRequestMutableLiveData().observe(this, new Observer<PayRequest>() {
            @Override
            public void onChanged(PayRequest payRequest) {
                if (payRequest != null) {
                    if (payRequest.getStatus().toUpperCase().equals("SUCCESS")) {
                        enableLayout("pay");
                        bindPayTransaction(payRequest.getData());
                    }
                }
            }
        });

        transDetailsViewModel.getWithdrawTransDetailsMutableLiveData().observe(this, new Observer<WithdrawTransDetails>() {
            @Override
            public void onChanged(WithdrawTransDetails withdrawTransDetails) {
                if (withdrawTransDetails != null) {
                    if (withdrawTransDetails.getStatus().toUpperCase().equals("SUCCESS")) {
                        enableLayout("withdraw");
                        bindInstantPay(withdrawTransDetails.getData());
                    }
                }
            }
        });

        transDetailsViewModel.getWithdrawGiftCardMutableLiveData().observe(this, new Observer<WithdrawGiftCard>() {
            @Override
            public void onChanged(WithdrawGiftCard withdrawGiftCard) {
                if (withdrawGiftCard != null) {
                    if (withdrawGiftCard.getStatus().toUpperCase().equals("SUCCESS")) {
                        enableLayout("gift");
                        bindGiftCard(withdrawGiftCard.getData());
                    }
                }
            }
        });

        transDetailsViewModel.getWithdrawSignetMutableLiveData().observe(this, new Observer<WithdrawSignet>() {
            @Override
            public void onChanged(WithdrawSignet withdrawSignet) {
                if (withdrawSignet != null) {
                    if (withdrawSignet.getStatus().toUpperCase().equals("SUCCESS")) {
                        enableLayout("signet");
                        bindSignet(withdrawSignet.getData());
                    }
                }
            }
        });
    }

    private void getTransType(String strType) {
        switch (strType.toLowerCase().replace(" ", "")) {
            case "buytoken":
                strTransType = Utils.addType;
                break;
            case "withdraw":
                strTransType = Utils.withdrawType;
                break;
            case "pay/request":
                strTransType = Utils.payType;
                break;
        }
    }

    private void getTransSubType(String strType) {
        switch (strType.toLowerCase().replace(" ", "")) {
            case "debitcard":
                strTransSubtype = Utils.debitType;
                break;
            case "creditcard":
                strTransSubtype = Utils.creditType;
                break;
            case "instantpay":
                strTransSubtype = Utils.instantType;
                break;
            case "giftcard":
                strTransSubtype = Utils.giftcardType;
                break;
            case "bankaccount":
                strTransSubtype = Utils.bankType;
                break;
            case "signet":
                strTransSubtype = Utils.signetType;
                break;
            case "sent":
                strTransSubtype = Utils.paySubType;
                break;
            case "received":
                strTransSubtype = Utils.requestSubType;
                break;
        }
    }

    private void enableLayout(String strType) {
        try {
            switch (strType) {
                case "buy":
                    findViewById(R.id.btCardContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.btBankContainer).setVisibility(View.GONE);
                    findViewById(R.id.btPayContainer).setVisibility(View.GONE);
                    findViewById(R.id.btGiftContainer).setVisibility(View.GONE);
                    findViewById(R.id.btSignetContainer).setVisibility(View.GONE);
                    break;
                case "withdraw":
                    findViewById(R.id.btCardContainer).setVisibility(View.GONE);
                    findViewById(R.id.btPayContainer).setVisibility(View.GONE);
                    findViewById(R.id.btGiftContainer).setVisibility(View.GONE);
                    findViewById(R.id.btSignetContainer).setVisibility(View.GONE);
                    findViewById(R.id.btBankContainer).setVisibility(View.VISIBLE);
                    break;
                case "pay":
                    findViewById(R.id.btCardContainer).setVisibility(View.GONE);
                    findViewById(R.id.btPayContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.btBankContainer).setVisibility(View.GONE);
                    findViewById(R.id.btGiftContainer).setVisibility(View.GONE);
                    findViewById(R.id.btSignetContainer).setVisibility(View.GONE);
                    break;
                case "gift":
                    findViewById(R.id.btCardContainer).setVisibility(View.GONE);
                    findViewById(R.id.btPayContainer).setVisibility(View.GONE);
                    findViewById(R.id.btBankContainer).setVisibility(View.GONE);
                    findViewById(R.id.btSignetContainer).setVisibility(View.GONE);
                    findViewById(R.id.btGiftContainer).setVisibility(View.VISIBLE);
                    break;
                case "signet":
                    findViewById(R.id.btCardContainer).setVisibility(View.GONE);
                    findViewById(R.id.btPayContainer).setVisibility(View.GONE);
                    findViewById(R.id.btBankContainer).setVisibility(View.GONE);
                    findViewById(R.id.btGiftContainer).setVisibility(View.GONE);
                    findViewById(R.id.btSignetContainer).setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindCardTransaction(BuyCreditCardData objData) {
        try {
            RelativeLayout layoutCardInfo, layoutCardDetails, layoutLedgerInfo, layoutLedgerDetails, layoutBankDetails;
            TextView tvDate, tvStatus, tvTansType, tvYouPay, tvYouGet, tvPFee, tvBalance;
            TextView tvTotal, tvName, tvCardNumber, tvDescpName, tvReferenceId, tvCardInfo;
            TextView tvCName, tvBankNumber;
            CardView cvStatus;
            ImageView imgType, imgCardCopy, imgCardArrow, imgLedgerArrow, imgBTitleArrow;
            RelativeLayout layoutBTitle, layoutBuy;
            cvStatus = (CardView) findViewById(R.id.cvStatus);
            imgType = (ImageView) findViewById(R.id.imgType);
            imgCardCopy = (ImageView) findViewById(R.id.imgCardCopy);
            imgCardArrow = (ImageView) findViewById(R.id.imgCardArrow);
            imgLedgerArrow = (ImageView) findViewById(R.id.imgLedgerArrow);
            imgBTitleArrow = (ImageView) findViewById(R.id.imgBTitleArrow);
            tvDate = (TextView) findViewById(R.id.tvDate);
            tvStatus = (TextView) findViewById(R.id.tvStatus);
            tvTansType = (TextView) findViewById(R.id.tvTansType);
            tvYouPay = (TextView) findViewById(R.id.tvYouPay);
            tvYouGet = (TextView) findViewById(R.id.tvYouGet);
            tvPFee = (TextView) findViewById(R.id.tvPFee);
            tvBalance = (TextView) findViewById(R.id.tvBalance);
            tvTotal = (TextView) findViewById(R.id.tvTotal);
            tvName = (TextView) findViewById(R.id.tvName);
            tvCardNumber = (TextView) findViewById(R.id.tvCardNumber);
            tvDescpName = (TextView) findViewById(R.id.tvDescpName);
            tvReferenceId = (TextView) findViewById(R.id.tvReferenceId);
            tvCardInfo = (TextView) findViewById(R.id.tvCardInfo);
            tvCName = (TextView) findViewById(R.id.tvCName);
            tvBankNumber = (TextView) findViewById(R.id.tvBankNumber);
            layoutCardInfo = (RelativeLayout) findViewById(R.id.layoutCardInfo);
            layoutCardDetails = (RelativeLayout) findViewById(R.id.layoutCardDetails);
            layoutLedgerInfo = (RelativeLayout) findViewById(R.id.layoutLedgerInfo);
            layoutLedgerDetails = (RelativeLayout) findViewById(R.id.layoutLedgerDetails);
            layoutBankDetails = (RelativeLayout) findViewById(R.id.layoutBankDetails);
            layoutBTitle = (RelativeLayout) findViewById(R.id.layoutBTitle);
            layoutBuy = (RelativeLayout) findViewById(R.id.layoutBuy);
            if (objData.getTransactionSubtype().toLowerCase().replace(" ", "").equals("bankaccount")) {
                tvCardInfo.setText("Bank Information");
            } else {
                tvCardInfo.setText("Card Information");
            }
//            tvDate.setText(Utils.transactionDate(objData.getCreatedDate()).toUpperCase());
            tvDate.setText(objMyApplication.transactionDate(objData.getCreatedDate()).toUpperCase());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strCreatedDate = objMyApplication.compareTransactionDate(objData.getCreatedDate());
            String strCurDate = spf.format(Calendar.getInstance().getTime());
//            if (Utils.convertDate(objData.getCreatedDate()).equals(Utils.convertDate(strCurDate))) {
            if (Utils.convertDate(strCreatedDate).equals(Utils.convertDate(strCurDate))) {
                tvDate.setText("Today " + objMyApplication.transactionTime(objData.getCreatedDate()).toUpperCase());
            }
            tvStatus.setText(objData.getStatus());
            tvTansType.setText(objData.getTransactionSubtype());
            tvYouPay.setText(USFormat(objData.getYouPay()));
            tvTotal.setText(USFormat(objData.getYouPay()));
            tvYouGet.setText(USFormat(objData.getYouGet()));
            switch (objData.getStatus().toLowerCase().replace(" ", "")) {
                case Utils.transInProgress:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#D45858"));
                    break;
            }
            if (objData.getCardNumber() != null && !objData.getCardNumber().equals("")) {
                if (objData.getCardNumber().length() > 4) {
                    tvCardNumber.setText("**** " + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
                } else {
                    tvCardNumber.setText("**** " + objData.getCardNumber());
                }
            } else {
                tvCardNumber.setText("");
            }
            switch (objData.getCardBrand()) {
                case "MASTERCARD":
                    imgType.setImageResource(R.drawable.ic_master);
                    break;
                case "VISA":
                    imgType.setImageResource(R.drawable.ic_visa);
                    break;
                case "AMERICAN EXPRESS":
                    imgType.setImageResource(R.drawable.ic_amex);
                    break;
                case "DISCOVER":
                    imgType.setImageResource(R.drawable.ic_discover);
                    break;
            }
            tvBalance.setText(USFormat(objData.getAccountBalance()));
            tvName.setText(objData.getCardHolderName());
            tvCName.setText(objData.getNameOnBankAccount());
            //tvBankNumber.setText(objData.getBankAccountNumber());
            if (objData.getBankAccountNumber() != null && !objData.getBankAccountNumber().equals("")) {
                if (objData.getBankAccountNumber().length() > 4) {
                    tvBankNumber.setText("**** " + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
                } else {
                    tvBankNumber.setText("**** " + objData.getBankAccountNumber());
                }
            } else {
                tvBankNumber.setText("");
            }
            //tvDescpName.setText(objData.getDescriptorName());
            if (objData.getDescriptorName().length() > 10) {
                tvDescpName.setText(objData.getDescriptorName().substring(0, 10) + "...");
            } else {
                tvDescpName.setText(objData.getDescriptorName());
            }
            if (objData.getReferenceId().length() > 10) {
                tvReferenceId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                tvReferenceId.setText(objData.getReferenceId());
            }
            tvPFee.setText(USFormat(objData.getProcessingFee().replace(" NA", "")));
            imgCardCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
            tvReferenceId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            layoutBTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutBuy.getVisibility() == View.VISIBLE) {
                        layoutBuy.setVisibility(View.GONE);
                        imgBTitleArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutBuy.setVisibility(View.VISIBLE);
                        imgBTitleArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            layoutCardInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objData.getTransactionSubtype().toLowerCase().replace(" ", "").equals("bankaccount")) {
                        if (layoutBankDetails.getVisibility() == View.VISIBLE) {
                            layoutBankDetails.setVisibility(View.GONE);
                            imgCardArrow.setImageResource(R.drawable.ic_down_arrow_2);
                        } else {
                            layoutBankDetails.setVisibility(View.VISIBLE);
                            imgCardArrow.setImageResource(R.drawable.ic_up_arrow);
                        }
                    } else {
                        if (layoutCardDetails.getVisibility() == View.VISIBLE) {
                            layoutCardDetails.setVisibility(View.GONE);
                            imgCardArrow.setImageResource(R.drawable.ic_down_arrow_2);
                        } else {
                            layoutCardDetails.setVisibility(View.VISIBLE);
                            imgCardArrow.setImageResource(R.drawable.ic_up_arrow);
                        }
                    }
                }
            });

            layoutLedgerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutLedgerDetails.getVisibility() == View.VISIBLE) {
                        layoutLedgerDetails.setVisibility(View.GONE);
                        imgLedgerArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutLedgerDetails.setVisibility(View.VISIBLE);
                        imgLedgerArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindPayTransaction(PayRequestData objData) {
        try {
            RelativeLayout layoutRecpInfo, layoutRecpDetails, layoutMsgInfo, layoutMsgDetails, layoutLedgerInfo, layoutLedgerDetails;
            TextView tvDate, tvStatus, tvTansType, tvAmtSent, tvPFee, tvBalance, tvPRPfee;
            TextView tvTotal, tvActAddress, tvUserName, tvReferenceId, tvMessage, tvAmtHead;
            CardView cvStatus;
            ImageView imgPayCopy, imgRecpArrow, imgLedgerArrow, imgMsgArrow, imgAddCopy, imgPTitleArrow;
            RelativeLayout layoutTotal, layoutTitle, layoutPay;
            TextView tvPayInfo, tvMsgInfo;
            cvStatus = (CardView) findViewById(R.id.cvPayStatus);
            imgPayCopy = (ImageView) findViewById(R.id.imgPayCopy);
            imgRecpArrow = (ImageView) findViewById(R.id.imgRecpArrow);
            imgLedgerArrow = (ImageView) findViewById(R.id.imgLedgerArrow);
            imgMsgArrow = (ImageView) findViewById(R.id.imgMsgArrow);
            imgAddCopy = (ImageView) findViewById(R.id.imgAddCopy);
            imgPTitleArrow = (ImageView) findViewById(R.id.imgPTitleArrow);
            tvDate = (TextView) findViewById(R.id.tvPDate);
            tvStatus = (TextView) findViewById(R.id.tvPayStatus);
            tvTansType = (TextView) findViewById(R.id.tvPayTansType);
            tvAmtSent = (TextView) findViewById(R.id.tvAmtSent);
            tvAmtHead = (TextView) findViewById(R.id.tvAmtHead);
            tvPFee = (TextView) findViewById(R.id.tvPayPFee);
            tvBalance = (TextView) findViewById(R.id.tvPayBalance);
            tvTotal = (TextView) findViewById(R.id.tvPayTotal);
            tvActAddress = (TextView) findViewById(R.id.tvActAddress);
            tvUserName = (TextView) findViewById(R.id.tvUserName);
            tvReferenceId = (TextView) findViewById(R.id.tvPReferenceId);
            tvMessage = (TextView) findViewById(R.id.tvMessage);
            tvPRPfee = (TextView) findViewById(R.id.tvPRPfee);
            tvPayInfo = (TextView) findViewById(R.id.tvPayInfo);
            tvMsgInfo = (TextView) findViewById(R.id.tvMsgInfo);
            layoutRecpInfo = (RelativeLayout) findViewById(R.id.layoutRecpInfo);
            layoutRecpDetails = (RelativeLayout) findViewById(R.id.layoutRecpDetails);
            layoutLedgerInfo = (RelativeLayout) findViewById(R.id.lyPayLedgerInfo);
            layoutLedgerDetails = (RelativeLayout) findViewById(R.id.lyPayLedgerDetails);
            layoutMsgInfo = (RelativeLayout) findViewById(R.id.layoutMsgInfo);
            layoutMsgDetails = (RelativeLayout) findViewById(R.id.layoutMsgDetails);
            layoutTotal = (RelativeLayout) findViewById(R.id.layoutPayTotal);
            layoutTitle = (RelativeLayout) findViewById(R.id.layoutTitle);
            layoutPay = (RelativeLayout) findViewById(R.id.layoutPay);
            if (objData.getTransactionSubtype().toLowerCase().replace(" ", "").equals("senttokens")) {
                tvAmtHead.setText("Amount Sent");
                tvPayInfo.setText("Recipient Information");
                tvMsgInfo.setText("Message to Recipient");
                tvPFee.setText(USFormat(objData.getProcessingFee().replace(" NA", "")));
                if (objData.getRecipientWalletAddress().length() > 10) {
                    tvActAddress.setText(objData.getRecipientWalletAddress().substring(0, 10) + "...");
                } else {
                    tvActAddress.setText(objData.getRecipientWalletAddress());
                }
                tvTotal.setText(USFormat(objData.getTotalAmount()));
                tvAmtSent.setText(USFormat(objData.getAmount()));
                tvUserName.setText(objData.getRecipientName());
                tvPRPfee.setVisibility(View.VISIBLE);
                tvPFee.setVisibility(View.VISIBLE);
                layoutTotal.setVisibility(View.VISIBLE);
            } else {
                tvAmtHead.setText("Amount Received");
                tvPayInfo.setText("Sender Information");
                tvMsgInfo.setText("Message From Sender");
                tvAmtSent.setText(objData.getAmountReceived());
                tvActAddress.setText(objData.getSenderWalletAddress().substring(0, 10) + "...");
                tvUserName.setText(objData.getSenderName());
                tvPRPfee.setVisibility(View.GONE);
                tvPFee.setVisibility(View.GONE);
                layoutTotal.setVisibility(View.GONE);
            }
//            tvDate.setText(Utils.transactionDate(objData.getCreatedDate()).toUpperCase());
            tvDate.setText(objMyApplication.transactionDate(objData.getCreatedDate()).toUpperCase());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strCreatedDate = objMyApplication.compareTransactionDate(objData.getCreatedDate());
            String strCurDate = spf.format(Calendar.getInstance().getTime());
//            if (Utils.convertDate(objData.getCreatedDate()).equals(Utils.convertDate(strCurDate))) {
            if (Utils.convertDate(strCreatedDate).equals(Utils.convertDate(strCurDate))) {
                tvDate.setText("Today " + objMyApplication.transactionTime(objData.getCreatedDate()).toUpperCase());
            }
            tvStatus.setText(objData.getStatus());
            tvTansType.setText(objData.getTransactionSubtype());
            switch (objData.getStatus().toLowerCase().replace(" ", "")) {
                case Utils.transInProgress:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    cvStatus.setCardBackgroundColor(Color.parseColor("#D45858"));
                    break;
            }

            tvBalance.setText(USFormat(objData.getAccountBalance()));
            tvMessage.setText(objData.getSenderMessage());
            if (objData.getReferenceId().length() > 10) {
                tvReferenceId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                tvReferenceId.setText(objData.getReferenceId());
            }
            imgAddCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getRecipientWalletAddress(), TransactionDetailsActivity.this);
                }
            });

            tvActAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objData.getTransactionSubtype().toLowerCase().replace(" ", "").equals("senttokens")) {
                        Utils.copyText(objData.getRecipientWalletAddress(), TransactionDetailsActivity.this);
                    } else {
                        Utils.copyText(objData.getSenderWalletAddress(), TransactionDetailsActivity.this);
                    }
                }
            });

            imgPayCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            tvReferenceId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            layoutTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutPay.getVisibility() == View.VISIBLE) {
                        layoutPay.setVisibility(View.GONE);
                        imgPTitleArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutPay.setVisibility(View.VISIBLE);
                        imgPTitleArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });
            layoutRecpInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutRecpDetails.getVisibility() == View.VISIBLE) {
                        layoutRecpDetails.setVisibility(View.GONE);
                        imgRecpArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutRecpDetails.setVisibility(View.VISIBLE);
                        imgRecpArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            layoutMsgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutMsgDetails.getVisibility() == View.VISIBLE) {
                        layoutMsgDetails.setVisibility(View.GONE);
                        imgMsgArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutMsgDetails.setVisibility(View.VISIBLE);
                        imgMsgArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            layoutLedgerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutLedgerDetails.getVisibility() == View.VISIBLE) {
                        layoutLedgerDetails.setVisibility(View.GONE);
                        imgLedgerArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutLedgerDetails.setVisibility(View.VISIBLE);
                        imgLedgerArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindInstantPay(WithdrawTransDetailsData objData) {
        try {
            TextView tvWDate, tvStatus, tvWTansType, tvWAmount, tvExchangeRate, tvRAmount, tvWPFee;
            TextView tvWTotal, tvWName, tvWCardNumber, tvWithdrawId, tvWReferenceId, tvWBalance, tvWDesc;
            CardView cvWStatus;
            RelativeLayout layoutWInfo, layoutWithdrawDetails, layoutWTitle, layoutWithdraw, lyWLedgerInfo, lyWLedgerDetails;
            ImageView imgWIdCopy, imgRefCopy, imgWType, imgTitleArrow, imgWArrow, imgWLedgerArrow;
            cvWStatus = (CardView) findViewById(R.id.cvWStatus);
            tvWDate = (TextView) findViewById(R.id.tvWDate);
            tvStatus = (TextView) findViewById(R.id.tvWStatus);
            tvWTansType = (TextView) findViewById(R.id.tvWTansType);
            tvWAmount = (TextView) findViewById(R.id.tvWAmount);
            tvExchangeRate = (TextView) findViewById(R.id.tvExchangeRate);
            tvRAmount = (TextView) findViewById(R.id.tvRAmount);
            tvWPFee = (TextView) findViewById(R.id.tvWPFee);
            tvWTotal = (TextView) findViewById(R.id.tvWTotal);
            tvWName = (TextView) findViewById(R.id.tvWName);
            tvWCardNumber = (TextView) findViewById(R.id.tvWCardNumber);
            tvWithdrawId = (TextView) findViewById(R.id.tvWithdrawId);
            tvWReferenceId = (TextView) findViewById(R.id.tvWReferenceId);
            tvWBalance = (TextView) findViewById(R.id.tvWBalance);
            tvWDesc = (TextView) findViewById(R.id.tvWDesc);
            imgWIdCopy = (ImageView) findViewById(R.id.imgWIdCopy);
            imgRefCopy = (ImageView) findViewById(R.id.imgRefCopy);
            imgWType = (ImageView) findViewById(R.id.imgWType);
            imgWArrow = (ImageView) findViewById(R.id.imgWArrow);
            imgTitleArrow = (ImageView) findViewById(R.id.imgWTitleArrow);
            imgWLedgerArrow = (ImageView) findViewById(R.id.imgWLedgerArrow);
            layoutWInfo = (RelativeLayout) findViewById(R.id.layoutWInfo);
            layoutWithdrawDetails = (RelativeLayout) findViewById(R.id.layoutWithdrawDetails);
            layoutWTitle = (RelativeLayout) findViewById(R.id.layoutWTitle);
            layoutWithdraw = (RelativeLayout) findViewById(R.id.layoutWithdraw);
            lyWLedgerInfo = (RelativeLayout) findViewById(R.id.lyWLedgerInfo);
            lyWLedgerDetails = (RelativeLayout) findViewById(R.id.lyWLedgerDetails);
//            tvWDate.setText(Utils.transactionDate(objData.getCreatedDate()).toUpperCase());
            tvWDate.setText(objMyApplication.transactionDate(objData.getCreatedDate()).toUpperCase());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strCreatedDate = objMyApplication.compareTransactionDate(objData.getCreatedDate());
            String strCurDate = spf.format(Calendar.getInstance().getTime());
//            if (Utils.convertDate(objData.getCreatedDate()).equals(Utils.convertDate(strCurDate))) {
            if (Utils.convertDate(strCreatedDate).equals(Utils.convertDate(strCurDate))) {
                tvWDate.setText("Today " + objMyApplication.transactionTime(objData.getCreatedDate()).toUpperCase());
            }
            tvStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase().replace(" ", "")) {
                case Utils.transInProgress:
                    cvWStatus.setCardBackgroundColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    cvWStatus.setCardBackgroundColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    cvWStatus.setCardBackgroundColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    cvWStatus.setCardBackgroundColor(Color.parseColor("#D45858"));
                    break;
            }

            switch (objData.getCardBrand()) {
                case "MASTERCARD":
                    imgWType.setImageResource(R.drawable.ic_master);
                    break;
                case "VISA":
                    imgWType.setImageResource(R.drawable.ic_visa);
                    break;
                case "AMERICAN EXPRESS":
                    imgWType.setImageResource(R.drawable.ic_amex);
                    break;
                case "DISCOVER":
                    imgWType.setImageResource(R.drawable.ic_discover);
                    break;
            }

            tvWTansType.setText(objData.getTransactionSubtype());

            tvWAmount.setText(USFormat(objData.getWithdrawAmount().replace(" NA", "")));
            tvExchangeRate.setText(objData.getExchangeRate());
            tvRAmount.setText(USFormat(objData.getReceivedAmount().replace(" NA", "")));
            tvWPFee.setText(USFormat(objData.getProcessingFee().replace(" NA", "")));
            tvWTotal.setText(USFormat(objData.getTotalAmount().replace(" NA", "")));
            tvWBalance.setText(USFormat(objData.getAccountBalance().replace(" NA", "")));
            tvWName.setText(objData.getCardHolderName());
            if (objData.getDescription() != null && !objData.getDescription().equals("")) {
                tvWDesc.setText(objData.getDescription());
            } else {
                tvWDesc.setText("");
            }
            if (objData.getCardNumber() != null && !objData.getCardNumber().equals("")) {
                if (objData.getCardNumber().length() > 4) {
                    tvWCardNumber.setText("**** " + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
                } else {
                    tvWCardNumber.setText("**** " + objData.getCardNumber());
                }
            } else {
                tvWCardNumber.setText("");
            }
            if (!objData.getWithdrawalId().equals("") && objData.getWithdrawalId().length() > 10) {
                //imgWIdCopy.setVisibility(View.VISIBLE);
                tvWithdrawId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                //imgWIdCopy.setVisibility(View.GONE);
                tvWithdrawId.setText(objData.getWithdrawalId());
            }
            if (!objData.getReferenceId().equals("") && objData.getReferenceId().length() > 10) {
                tvWReferenceId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                tvWReferenceId.setText(objData.getReferenceId());
            }
            imgWIdCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
                }
            });

            tvWithdrawId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
                }
            });
            imgRefCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            tvWReferenceId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
            layoutWTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutWithdraw.getVisibility() == View.VISIBLE) {
                        layoutWithdraw.setVisibility(View.GONE);
                        imgTitleArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutWithdraw.setVisibility(View.VISIBLE);
                        imgTitleArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });
            layoutWInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutWithdrawDetails.getVisibility() == View.VISIBLE) {
                        layoutWithdrawDetails.setVisibility(View.GONE);
                        imgWArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        layoutWithdrawDetails.setVisibility(View.VISIBLE);
                        imgWArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            lyWLedgerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWLedgerDetails.getVisibility() == View.VISIBLE) {
                        lyWLedgerDetails.setVisibility(View.GONE);
                        imgWLedgerArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWLedgerDetails.setVisibility(View.VISIBLE);
                        imgWLedgerArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindGiftCard(WithdrawGiftCardData objData) {
        try {
            TextView tvWGDate, tvWGStatus, tvWGTansType, tvWGName, tvWGCAmount, tvGExchangeRate, tvGYouPay;
            TextView tvWGCFee, tvWGTotal, tvWGBalance, tvWGRName, tvWGREmail, tvTransId, tvWGReferenceId;
            CardView cvWGStatus;
            RelativeLayout layoutWGTitle, lyWithdrawGift, layoutWGInfo, lyWithGCDetails, lyWGLedgerInfo, lyWGLedgerDetails;
            ImageView imgWGArrow, imgWGTitleArrow, imgWGLedgerArrow, imgWGIdCopy, imgGRefCopy;
            cvWGStatus = (CardView) findViewById(R.id.cvWGStatus);
            tvWGDate = (TextView) findViewById(R.id.tvWGDate);
            tvWGStatus = (TextView) findViewById(R.id.tvWGStatus);
            tvWGTansType = (TextView) findViewById(R.id.tvWGTansType);
            tvWGName = (TextView) findViewById(R.id.tvWGName);
            tvWGCAmount = (TextView) findViewById(R.id.tvWGCAmount);
            tvGExchangeRate = (TextView) findViewById(R.id.tvGExchangeRate);
            tvGYouPay = (TextView) findViewById(R.id.tvGYouPay);
            tvWGCFee = (TextView) findViewById(R.id.tvWGCFee);
            tvWGTotal = (TextView) findViewById(R.id.tvWGTotal);
            tvWGBalance = (TextView) findViewById(R.id.tvWGBalance);
            tvWGRName = (TextView) findViewById(R.id.tvWGRName);
            tvWGREmail = (TextView) findViewById(R.id.tvWGREmail);
            tvTransId = (TextView) findViewById(R.id.tvTransId);
            tvWGReferenceId = (TextView) findViewById(R.id.tvWGReferenceId);
            imgWGArrow = (ImageView) findViewById(R.id.imgWGArrow);
            imgWGTitleArrow = (ImageView) findViewById(R.id.imgWGTitleArrow);
            imgWGLedgerArrow = (ImageView) findViewById(R.id.imgWGLedgerArrow);
            imgWGIdCopy = (ImageView) findViewById(R.id.imgWGIdCopy);
            imgGRefCopy = (ImageView) findViewById(R.id.imgGRefCopy);
            layoutWGInfo = (RelativeLayout) findViewById(R.id.layoutWGInfo);
            lyWithGCDetails = (RelativeLayout) findViewById(R.id.lyWithGCDetails);
            layoutWGTitle = (RelativeLayout) findViewById(R.id.layoutWGTitle);
            lyWithdrawGift = (RelativeLayout) findViewById(R.id.lyWithdrawGift);
            lyWGLedgerInfo = (RelativeLayout) findViewById(R.id.lyWGLedgerInfo);
            lyWGLedgerDetails = (RelativeLayout) findViewById(R.id.lyWGLedgerDetails);
//            tvWGDate.setText(Utils.transactionDate(objData.getCreatedDate()).toUpperCase());
            tvWGDate.setText(objMyApplication.transactionDate(objData.getCreatedDate()).toUpperCase());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strCreatedDate = objMyApplication.compareTransactionDate(objData.getCreatedDate());
            String strCurDate = spf.format(Calendar.getInstance().getTime());
//            if (Utils.convertDate(objData.getCreatedDate()).equals(Utils.convertDate(strCurDate))) {
            if (Utils.convertDate(strCreatedDate).equals(Utils.convertDate(strCurDate))) {
                tvWGDate.setText("Today " + objMyApplication.transactionTime(objData.getCreatedDate()).toUpperCase());
            }
            tvWGStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase().replace(" ", "")) {
                case Utils.transInProgress:
                    cvWGStatus.setCardBackgroundColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    cvWGStatus.setCardBackgroundColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    cvWGStatus.setCardBackgroundColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    cvWGStatus.setCardBackgroundColor(Color.parseColor("#D45858"));
                    break;
            }

            tvWGTansType.setText(objData.getTransactionSubtype());
            tvWGName.setText(objData.getGiftCardName());
            tvWGCAmount.setText(objData.getGiftCardAmount().replace(" NA", ""));
            tvGExchangeRate.setText(objData.getExchangeRate());
            tvGYouPay.setText(USFormat(objData.getYouPay().replace(" NA", "")));
            tvWGCFee.setText(USFormat(objData.getGiftCardFee().replace(" NA", "")));
            tvWGTotal.setText(USFormat(objData.getTotalPaidAmount().replace(" NA", "")));
            tvWGBalance.setText(USFormat(objData.getAccountBalance().replace(" NA", "")));
            tvWGRName.setText(objData.getRecipientName());
            tvWGREmail.setText(objData.getRecipientEmail());

            if (!objData.getOrderId().equals("") && objData.getOrderId().length() > 10) {
                imgWGIdCopy.setVisibility(View.VISIBLE);
                tvTransId.setText(objData.getOrderId().substring(0, 10) + "...");
            } else {
                imgWGIdCopy.setVisibility(View.GONE);
                tvTransId.setText(objData.getOrderId());
            }
            if (!objData.getReferenceId().equals("") && objData.getReferenceId().length() > 10) {
                tvWGReferenceId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                tvWGReferenceId.setText(objData.getReferenceId());
            }
            imgWGIdCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getOrderId(), TransactionDetailsActivity.this);
                }
            });

            tvTransId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getOrderId(), TransactionDetailsActivity.this);
                }
            });

            imgGRefCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            tvWGReferenceId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            layoutWGTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWithdrawGift.getVisibility() == View.VISIBLE) {
                        lyWithdrawGift.setVisibility(View.GONE);
                        imgWGTitleArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWithdrawGift.setVisibility(View.VISIBLE);
                        imgWGTitleArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });
            layoutWGInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWithGCDetails.getVisibility() == View.VISIBLE) {
                        lyWithGCDetails.setVisibility(View.GONE);
                        imgWGArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWithGCDetails.setVisibility(View.VISIBLE);
                        imgWGArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            lyWGLedgerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWGLedgerDetails.getVisibility() == View.VISIBLE) {
                        lyWGLedgerDetails.setVisibility(View.GONE);
                        imgWGLedgerArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWGLedgerDetails.setVisibility(View.VISIBLE);
                        imgWGLedgerArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindSignet(WithdrawSignetData objData) {
        try {
            TextView tvWBDate, tvWBStatus, tvWBTansType, tvWAmount, tvBExchangeRate, tvReceiveAmt, tvWBPFee, tvWithdrawalId;
            TextView tvWBTotal, tvWBBalance, tvBankInfo, tvWBName, tvWalletHead, tvWalletId, tvBankAcNumber, tvWBReferenceId, tvSDesc;
            CardView cvWBStatus;
            RelativeLayout layoutWBTitle, lyWithdrawBank, layoutWBInfo, lyWithBDetails, lyWBLedgerInfo, lyWBLedgerDetails;
            ImageView imgWBTitleArrow, imgWBArrow, imgWBLedgerArrow, imgWBIdCopy, imgBRefCopy, imgBWIDCopy;
            LinearLayout lyBankDetails, layoutWalletId;
            cvWBStatus = (CardView) findViewById(R.id.cvWBStatus);
            tvWBDate = (TextView) findViewById(R.id.tvWBDate);
            tvWBStatus = (TextView) findViewById(R.id.tvWBStatus);
            tvWBTansType = (TextView) findViewById(R.id.tvWBTansType);
            tvWAmount = (TextView) findViewById(R.id.tvWBAmount);
            tvBExchangeRate = (TextView) findViewById(R.id.tvBExchangeRate);
            tvReceiveAmt = (TextView) findViewById(R.id.tvReceiveAmt);
            tvWBPFee = (TextView) findViewById(R.id.tvWBPFee);
            tvWBTotal = (TextView) findViewById(R.id.tvWBTotal);
            tvWBBalance = (TextView) findViewById(R.id.tvWBBalance);
            tvBankInfo = (TextView) findViewById(R.id.tvBankInfo);
            tvWBName = (TextView) findViewById(R.id.tvWBName);
            tvWalletHead = (TextView) findViewById(R.id.tvWalletHead);
            tvWalletId = (TextView) findViewById(R.id.tvWalletId);
            tvBankAcNumber = (TextView) findViewById(R.id.tvBankAcNumber);
            tvWithdrawalId = (TextView) findViewById(R.id.tvWithdrawalId);
            tvWBReferenceId = (TextView) findViewById(R.id.tvWBReferenceId);
            tvSDesc = (TextView) findViewById(R.id.tvSDesc);
            imgWBArrow = (ImageView) findViewById(R.id.imgWBArrow);
            imgWBTitleArrow = (ImageView) findViewById(R.id.imgWBTitleArrow);
            imgWBLedgerArrow = (ImageView) findViewById(R.id.imgWBLedgerArrow);
            imgWBIdCopy = (ImageView) findViewById(R.id.imgWBIdCopy);
            imgBRefCopy = (ImageView) findViewById(R.id.imgBRefCopy);
            imgBWIDCopy = (ImageView) findViewById(R.id.imgBWIDCopy);
            layoutWBInfo = (RelativeLayout) findViewById(R.id.layoutWBInfo);
            lyWithBDetails = (RelativeLayout) findViewById(R.id.lyWithBDetails);
            layoutWBTitle = (RelativeLayout) findViewById(R.id.layoutWBTitle);
            lyWithdrawBank = (RelativeLayout) findViewById(R.id.lyWithdrawBank);
            lyWBLedgerInfo = (RelativeLayout) findViewById(R.id.lyWBLedgerInfo);
            lyWBLedgerDetails = (RelativeLayout) findViewById(R.id.lyWBLedgerDetails);
            lyBankDetails = (LinearLayout) findViewById(R.id.lyBankDetails);
            layoutWalletId = (LinearLayout) findViewById(R.id.layoutWalletId);
            if (objData.getTransactionSubtype().toLowerCase().replace(" ", "").equals("signet")) {
                tvBankInfo.setText("Signet Account Information");
                tvWBName.setText(objData.getNameOnBank());
                tvWalletHead.setText("Wallet ID");
                lyBankDetails.setVisibility(View.GONE);
                layoutWalletId.setVisibility(View.VISIBLE);
            } else {
                tvBankInfo.setText("Bank Account Information");
                tvWBName.setText(objData.getNameOnBankAccount());
                tvWalletHead.setText("Bank Account Number");
                lyBankDetails.setVisibility(View.VISIBLE);
                layoutWalletId.setVisibility(View.GONE);
                //tvBankAcNumber.setText(objData.getBankAccountNumber());
                if (objData.getBankAccountNumber() != null && !objData.getBankAccountNumber().equals("")) {
                    if (objData.getBankAccountNumber().length() > 4) {
                        tvBankAcNumber.setText("**** " + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
                    } else {
                        tvBankAcNumber.setText("**** " + objData.getBankAccountNumber());
                    }
                } else {
                    tvBankAcNumber.setText("");
                }
            }
//            tvWBDate.setText(Utils.transactionDate(objData.getCreatedDate()).toUpperCase());
            tvWBDate.setText(objMyApplication.transactionDate(objData.getCreatedDate()).toUpperCase());
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strCreatedDate = objMyApplication.compareTransactionDate(objData.getCreatedDate());
            String strCurDate = spf.format(Calendar.getInstance().getTime());
//            if (Utils.convertDate(objData.getCreatedDate()).equals(Utils.convertDate(strCurDate))) {
            if (Utils.convertDate(strCreatedDate).equals(Utils.convertDate(strCurDate))) {
                tvWBDate.setText("Today " + objMyApplication.transactionTime(objData.getCreatedDate()).toUpperCase());
            }
            tvWBStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase().replace(" ", "")) {
                case Utils.transInProgress:
                    cvWBStatus.setCardBackgroundColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    cvWBStatus.setCardBackgroundColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    cvWBStatus.setCardBackgroundColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    cvWBStatus.setCardBackgroundColor(Color.parseColor("#D45858"));
                    break;
            }

            tvWBTansType.setText(objData.getTransactionSubtype());
            tvWAmount.setText(USFormat(objData.getWithdrawAmount()));
            tvBExchangeRate.setText(objData.getExchangeRate());
            tvReceiveAmt.setText(USFormat(objData.getReceivedAmount().replace(" NA", "")));
            tvWBPFee.setText(USFormat(objData.getProcessingFee().replace(" NA", "")));
            tvWBTotal.setText(USFormat(objData.getTotalAmount().replace(" NA", "")));
            if (objData.getDescription() != null && !objData.getDescription().equals("")) {
                tvSDesc.setText(objData.getDescription());
            } else {
                tvSDesc.setText("");
            }
            if (objData.getAccountBalance() != null) {
                tvWBBalance.setText(USFormat(objData.getAccountBalance().replace(" NA", "")));
            } else {
                tvWBBalance.setText("");
            }

            if (objData.getWalletId() != null && !objData.getWalletId().equals("") && objData.getWalletId().length() > 10) {
                tvWalletId.setText(objData.getWalletId().substring(0, 10) + "...");
            } else if (objData.getWalletId() != null) {
                tvWalletId.setText(objData.getWalletId());
            } else {
                tvWalletId.setText("");
            }

            if (!objData.getWithdrawalId().equals("") && objData.getWithdrawalId().length() > 10) {
                //imgWBIdCopy.setVisibility(View.VISIBLE);
                tvWithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                //imgWBIdCopy.setVisibility(View.GONE);
                tvWithdrawalId.setText(objData.getWithdrawalId());
            }
            if (!objData.getReferenceId().equals("") && objData.getReferenceId().length() > 10) {
                tvWBReferenceId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                tvWBReferenceId.setText(objData.getReferenceId());
            }
            imgBWIDCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getWalletId(), TransactionDetailsActivity.this);
                }
            });

            tvWalletId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getWalletId(), TransactionDetailsActivity.this);
                }
            });

            imgWBIdCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
                }
            });

            tvWithdrawalId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
                }
            });

            imgBRefCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            tvWBReferenceId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

            layoutWBTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWithdrawBank.getVisibility() == View.VISIBLE) {
                        lyWithdrawBank.setVisibility(View.GONE);
                        imgWBTitleArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWithdrawBank.setVisibility(View.VISIBLE);
                        imgWBTitleArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            layoutWBInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWithBDetails.getVisibility() == View.VISIBLE) {
                        lyWithBDetails.setVisibility(View.GONE);
                        imgWBArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWithBDetails.setVisibility(View.VISIBLE);
                        imgWBArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

            lyWBLedgerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lyWBLedgerDetails.getVisibility() == View.VISIBLE) {
                        lyWBLedgerDetails.setVisibility(View.GONE);
                        imgWBLedgerArrow.setImageResource(R.drawable.ic_down_arrow_2);
                    } else {
                        lyWBLedgerDetails.setVisibility(View.VISIBLE);
                        imgWBLedgerArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(String strValue) {
        String strAmount = "";
        try {
            if (strValue != null && !strValue.equals("") && strValue.contains(" ")) {
                strAmount = Utils.convertBigDecimalUSDC(strValue.split(" ")[0].trim().replace(",", ""));
                strAmount = Utils.USNumberFormat(Double.parseDouble(strAmount)) + " " + strValue.split(" ")[1];
            } else {
                strAmount = strValue;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strAmount;
    }

}
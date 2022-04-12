package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class TransactionDetailsActivity extends AppCompatActivity {
    DashboardViewModel dashboardViewModel;
    MyApplication objMyApplication;
    String strGbxTxnIdType = "";
    int txnType;
    Integer txnSubType;
    ProgressDialog progressDialog;
    CardView cancelTxnCV;

    // Control Method Types
    private static final String PAY_REQUEST = "PayRequest";
    private static final String BUY_TOKEN = "BuyTokenDebitAndCreditCard";
    private static final String BUY_BANK = "BuyTokenBank";
    private static final String BUY_SIGNET = "BuyTokenSignet";
    private static final String WITH_GIFT = "WithdrawGiftCard";
    private static final String WITH_Instant = "WithdrawInstantPay";
    private static final String WITH_BANK = "WithdrawBankAccount";
    private static final String WITH_SIGNET = "WithdrawSignet";
    private static final String BUSINESS_PAYOUT = "businessPayout";
    private static final String CANCELLED_WITH = "cancelledWithdrawBank";
    private static final String FAILED_WITH = "failedWithdrawBank";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_transaction_details);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            if (getIntent().getStringExtra(Utils.gbxTxnIdType) != null && !getIntent().getStringExtra(Utils.gbxTxnIdType).equals("")) {
                strGbxTxnIdType = getIntent().getStringExtra(Utils.gbxTxnIdType);
            }
            if (getIntent().getStringExtra(Utils.txnType) != null && !getIntent().getStringExtra(Utils.txnType).equals("")) {
                //txnType = Integer.parseInt(getIntent().getStringExtra("txnType"));
                switch (getIntent().getStringExtra(Utils.txnType).toLowerCase()) {
                    case "pay / request":
                        txnType = Integer.parseInt(Utils.payType);
                        break;
                    case "buy token":
                    case "buy tokens":
                        txnType = Integer.parseInt(Utils.addType);
                        break;
                    case "withdraw":
                        txnType = Integer.parseInt(Utils.withdrawType);
                        break;
                    case "business payout":
                        txnType = Integer.parseInt(Utils.businessType);
                        break;
                    case "merchant payout":
                        txnType = Integer.parseInt(Utils.merchantType);
                        break;
                    case "canceled bank withdraw":
                        txnType = Utils.cancelledWithdraw;
                        break;
                    case "failed bank withdraw":
                        txnType = Utils.failedWithdraw;
                        break;
                    case "paid order":
                        txnType = Utils.paidInvoice;
                }
            }
            if (getIntent().getStringExtra(Utils.txnSubType) != null && !getIntent().getStringExtra(Utils.txnSubType).equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (getIntent().getStringExtra(Utils.txnSubType).toLowerCase()) {
                    case "sent":
                        txnSubType = Integer.parseInt(Utils.paySubType);
                        break;
                    case "received":
                        txnSubType = Integer.parseInt(Utils.requestSubType);
                        break;
                    case "bank account":
                        txnSubType = Integer.parseInt(Utils.bankType);
                        break;
                    case "credit card":
                        txnSubType = Integer.parseInt(Utils.creditType);
                        break;
                    case "debit card":
                        txnSubType = Integer.parseInt(Utils.debitType);
                        break;
                    case "gift card":
                        txnSubType = Integer.parseInt(Utils.giftcardType);
                        break;
                    case "instant pay":
                        txnSubType = Integer.parseInt(Utils.instantType);
                        break;
                    case "signet":
                        txnSubType = Integer.parseInt(Utils.signetType);
                        break;
                    default:
                        txnSubType = null;
                        break;
                }
            }

            if (Utils.checkInternet(TransactionDetailsActivity.this)) {
                progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
            } else {
                Utils.displayAlert(getString(R.string.internet), TransactionDetailsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        dashboardViewModel.getTransactionDetailsMutableLiveData().observe(this, transactionDetails -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (transactionDetails != null && transactionDetails.getStatus().equalsIgnoreCase("Success")) {
                switch (transactionDetails.getData().getTransactionType().toLowerCase()) {
                    case "pay / request":
                        ControlMethod(PAY_REQUEST);
                        payRequest(transactionDetails.getData());
                        break;
                    case "buy token":
                    case "buy tokens":
                        switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                            case "credit card":
                            case "debit card":
                                ControlMethod(BUY_TOKEN);
                                buyTokenCreditDebit(transactionDetails.getData());
                                break;
                            case "bank account":
                                ControlMethod(BUY_BANK);
                                buyTokenBankAccount(transactionDetails.getData());
                                break;
                            case "signet":
                                ControlMethod(BUY_SIGNET);
                                buyTokenSignet(transactionDetails.getData());
                                break;
                        }
                        break;
                    case "withdraw":
                        switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                            case "gift card":
                                ControlMethod(WITH_GIFT);
                                withdrawGiftCard(transactionDetails.getData());
                                break;
                            case "instant pay":
                                ControlMethod(WITH_Instant);
                                withdrawInstant(transactionDetails.getData());
                                break;
                            case "bank account":
                                ControlMethod(WITH_BANK);
                                withdrawBank(transactionDetails.getData());
                                break;
                            case "signet":
                                ControlMethod(WITH_SIGNET);
                                withdrawSignet(transactionDetails.getData());
                                break;
                        }
                        break;
                    case "business payout": {

                        ControlMethod(BUSINESS_PAYOUT);
                        businessPayout(transactionDetails.getData());
                    }
                    break;
                    case "canceled bank withdraw": {
                        ControlMethod(CANCELLED_WITH);
                        cancelledWithdraw(transactionDetails.getData());
                    }
                    break;
                    case "failed bank withdraw": {
                        ControlMethod(FAILED_WITH);
                        failedWithdraw(transactionDetails.getData());
                    }
                    break;
                }
            }
        });

        dashboardViewModel.getCancelBuyTokenResponseMutableLiveData().observe(this, cancelBuyTokenResponse -> {
            try {
                progressDialog.dismiss();
                if (cancelBuyTokenResponse != null && cancelBuyTokenResponse.getStatus().equalsIgnoreCase("Success")) {
                    Utils.showCustomToast(TransactionDetailsActivity.this, "Transaction cancelled successfully.", R.drawable.ic_custom_tick, "");
                    //progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                    dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void payRequest(TransactionData objData) {
        try {
            TextView headerTV, amount, descrptn, completed, datetime, fee, total, balance;
            TextView refid, name, accountadress;
            LinearLayout lyPRClose, lyRefId, lyAccAdd;
            headerTV = findViewById(R.id.headerTV);
            amount = findViewById(R.id.amountTV);
            descrptn = findViewById(R.id.descrptnTV);
            completed = findViewById(R.id.payreqStatusTV);
            datetime = findViewById(R.id.dateTimeTV);
            fee = findViewById(R.id.feeTV);
            total = findViewById(R.id.totalTV);
            balance = findViewById(R.id.balanceTV);
            refid = findViewById(R.id.refidTV);
            name = findViewById(R.id.nameTV);
            accountadress = findViewById(R.id.accAddrsTV);
            lyPRClose = findViewById(R.id.lyPRClose);
            lyRefId = findViewById(R.id.lyRefId);
            lyAccAdd = findViewById(R.id.lyAccAdd);


            if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
                headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype().replace("Tokens", ""));
            }

            if (objData.getTransactionSubtype() != null) {
                if (objData.getTransactionSubtype().equals("Sent")) {
                    amount.setText(Utils.convertTwoDecimal(objData.getAmount().replace("CYN", "").trim()));
                    //                amount.setText(objData.getAmount().replace("CYN", "").trim());
                    name.setText(objData.getRecipientName());
                    accountadress.setText((objData.getRecipientWalletAddress().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "..."));
                    fee.setText(Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()) + " CYN");
                    total.setText(Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()) + " CYN");
                    lyAccAdd.setOnClickListener(view -> Utils.copyText(objData.getRecipientWalletAddress(), TransactionDetailsActivity.this));
                } else {
                    amount.setText(Utils.convertTwoDecimal(objData.getAmountReceived().replace("CYN", "").trim()));
                    findViewById(R.id.payreqTAmountLL).setVisibility(View.GONE);
                    findViewById(R.id.payreqPfLL).setVisibility(View.GONE);
                    name.setText(objData.getSenderName());
                    accountadress.setText((objData.getSenderWalletAddress().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "..."));
                    lyAccAdd.setOnClickListener(view -> Utils.copyText(objData.getSenderWalletAddress(), TransactionDetailsActivity.this));
                }
            }


            if (objData.getSenderName() != null && !objData.getSenderMessage().equals("")) {
                descrptn.setText("\"" + objData.getSenderMessage() + "\"");
            } else {
                descrptn.setVisibility(View.GONE);
            }


            if (objData.getCreatedDate() != null) {
                datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            }

            if (objData.getAccountBalance() != null) {
                balance.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
            }

            if (objData.getReferenceId() != null) {

                if (objData.getReferenceId().length() > 10) {
                    refid.setText(objData.getReferenceId().substring(0, 10) + "...");
                } else {
                    refid.setText(objData.getReferenceId());
                }

                lyRefId.setOnClickListener(view -> Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this));

            }

            if (objData.getStatus() != null) {
                completed.setText(objData.getStatus());
                switch (objData.getStatus().toLowerCase()) {
                    case "completed":
                        completed.setTextColor(getResources().getColor(R.color.completed_status));
                        completed.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case "in progress":
                        completed.setTextColor(getResources().getColor(R.color.inprogress_status));
                        completed.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case "pending":
                        completed.setTextColor(getResources().getColor(R.color.pending_status));
                        completed.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case "failed":
                        completed.setTextColor(getResources().getColor(R.color.failed_status));
                        completed.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }

            lyPRClose.setOnClickListener(view -> onBackPressed());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void buyTokenCreditDebit(TransactionData objData) {
        TextView headerTV, amount, status, datetime, fee, total, balance, purchaseAmountTV, successadd, chargeback;
        TextView refid, name, descriptorName, cardNumber, expiryDate, depositIDTV;
        LinearLayout lyPRClose;
        ImageView refIdIV, depositIDIV, cardBrandIV;
        LinearLayout depositID, referenceID;

        headerTV = findViewById(R.id.headTV);
        amount = findViewById(R.id.tvAmount);
        status = findViewById(R.id.statusTV);
        datetime = findViewById(R.id.datetimeTV);
        purchaseAmountTV = findViewById(R.id.purchaseTV);
        fee = findViewById(R.id.processingFeeTV);
        total = findViewById(R.id.totalAmountTV);
        balance = findViewById(R.id.accBalTV);
        depositID = findViewById(R.id.depositIDLL);
        referenceID = findViewById(R.id.referenceIDLL);
        refid = findViewById(R.id.referenceIDTV);
        descriptorName = findViewById(R.id.descriNameTV);
        name = findViewById(R.id.cardHoldernameTV);
        cardNumber = findViewById(R.id.cardnumTV);
        expiryDate = findViewById(R.id.expdateTV);
        successadd = findViewById(R.id.successadd);
        chargeback = findViewById(R.id.chargeback);
        depositIDTV = findViewById(R.id.depositid);
        lyPRClose = findViewById(R.id.previous);
        cardBrandIV = findViewById(R.id.cardBrandIV);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getYouGet() != null) {
            amount.setText(Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        }

        if (objData.getStatus() != null) {
            status.setText(objData.getStatus());

            switch (objData.getStatus().toLowerCase()) {
                case "completed":
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case "in progress":
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case "pending":
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case "failed":
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getYouPay() != null) {
            purchaseAmountTV.setText(objData.getYouPay());
        }

        if (objData.getCardBrand() != null) {
            switch (objData.getCardBrand()) {
                case Utils.MASTERCARD:
                    cardBrandIV.setImageResource(R.drawable.ic_master);
                    break;
                case Utils.VISA:
                    cardBrandIV.setImageResource(R.drawable.ic_visa);
                    break;
                case Utils.AMERICANEXPRESS:
                    cardBrandIV.setImageResource(R.drawable.ic_amex);
                    break;
                case Utils.DISCOVER:
                    cardBrandIV.setImageResource(R.drawable.ic_discover);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }


        if (objData.getYouGet() != null && objData.getProcessingFee() != null) {
            Double purchaseAmount = Double.parseDouble(objData.getYouGet().replace("CYN", "").trim());
            Double processingFee = Double.parseDouble(objData.getProcessingFee().replace("USD", "").trim());
            purchaseAmountTV.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
            fee.setText("$" + Utils.convertTwoDecimal((objData.getProcessingFee().replace("USD", "").trim())));
            total.setText("$" + Utils.convertTwoDecimal(String.valueOf(purchaseAmount + processingFee)));
        }

        if (objData.getAccountBalance() != null) {
            balance.setText(Utils.convertTwoDecimal((objData.getAccountBalance().replace("CYN", "").trim())) + " CYN");
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                refid.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                refid.setText(objData.getReferenceId());
            }

            referenceID.setOnClickListener(view -> Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this));

        }

        if (objData.getDescriptorName() != null) {
            descriptorName.setText(objData.getDescriptorName());
        }

        if (objData.getCardHolderName() != null) {
            name.setText(objData.getCardHolderName());
        }

        if (objData.getCardNumber() != null) {
            cardNumber.setText("\u2022\u2022\u2022\u2022" + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
        }

        if (objData.getCardExpiryDate() != null) {
            expiryDate.setText(objData.getCardExpiryDate());
        }

        if (objData.getDepositId() != null) {
            if (objData.getDepositId().length() > 10) {
                depositIDTV.setText(objData.getDepositId().substring(0, 10) + "...");
            } else {
                depositIDTV.setText(objData.getDepositId());
            }

            depositID.setOnClickListener(view -> Utils.copyText(objData.getDepositid(), TransactionDetailsActivity.this));
        }

        lyPRClose.setOnClickListener(view -> onBackPressed());
    }

    @SuppressLint("SetTextI18n")
    private void buyTokenBankAccount(TransactionData objData) {
        TextView headerTV, amount, status, datetime, fee, total, balance, purchaseamount, successadd, chargeback;
        TextView refid, name, accountadress, descriptorname, depositIDTV, bankAccNumTV, bankNameTV, nameOnAccTV;
        LinearLayout lyPRClose, btBankReference, btBankDepositID, descriptorLL;
        ImageView previousBtn, depositIDIV;

        headerTV = findViewById(R.id.btbankheaderTV);
        amount = findViewById(R.id.btbankamountTV);
        status = findViewById(R.id.btbankStatusTV);
        datetime = findViewById(R.id.btbankDatetimeTV);
        purchaseamount = findViewById(R.id.btBankpurchaseamntTV);
        fee = findViewById(R.id.btBankprocessingfeeTV);
        total = findViewById(R.id.btbankTotalTV);
        refid = findViewById(R.id.btbankRefidTV);
        btBankReference = findViewById(R.id.btbankReferenceLL);
        btBankDepositID = findViewById(R.id.btbankDepositIDLL);
        descriptorname = findViewById(R.id.btbankDescrptorTV);
        depositIDTV = findViewById(R.id.btbankDepositIDTV);
        lyPRClose = findViewById(R.id.btbankprevious);
        bankAccNumTV = findViewById(R.id.btbankaccountTV);
        bankNameTV = findViewById(R.id.btbanknameTV);
        nameOnAccTV = findViewById(R.id.btbanknameACTV);
        cancelTxnCV = findViewById(R.id.cancelTxnCV);
        descriptorLL = findViewById(R.id.descriptorLL);

        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getYouGet() != null) {
            amount.setText(objData.getYouGet().replace("CYN", "").trim());
        }
        cancelTxnCV.setVisibility(View.GONE);

        if (objData.getStatus() != null) {
            status.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    cancelTxnCV.setVisibility(View.VISIBLE);
                    break;
                case Utils.transPending:
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
                case Utils.transCancelled: {
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    if (descriptorLL.getVisibility() == View.VISIBLE) {
                        descriptorLL.setVisibility(View.GONE);
                    }
                }
                break;
            }
        }

        if (objData.getCreatedDate() != null) {
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getYouGet() != null) {
            purchaseamount.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        }

        if (objData.getProcessingFee() != null) {
            fee.setText("$" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("USD", "").trim()));
        }

        if (objData.getYouPay() != null) {
            total.setText("$" + Utils.convertTwoDecimal(objData.getYouPay().replace("USD", "").trim()));
        }

        if (objData.getBankAccountNumber() != null && objData.getBankAccountNumber().length() > 4) {
            bankAccNumTV.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                refid.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                refid.setText(objData.getReferenceId());
            }

            btBankReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (objData.getDescriptorName() != null) {
            descriptorname.setText(objData.getDescriptorName());
        }

        if (objData.getDepositId() != null) {
            if (objData.getDepositId().length() > 10) {
                depositIDTV.setText(objData.getDepositId().substring(0, 10) + "...");
            } else {
                depositIDTV.setText(objData.getDepositId());
            }
            btBankDepositID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Utils.copyText(objData.getDepositId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (objData.getBankName() != null) {
            bankNameTV.setText(objData.getBankName());
        }

        if (objData.getNameOnBankAccount() != null) {
            nameOnAccTV.setText(objData.getNameOnBankAccount());
        }

        cancelTxnCV.setOnClickListener(view -> {
            cancelPopup();
        });

        lyPRClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void buyTokenSignet(TransactionData objData) {
        TextView headerText, amount, status, date, purchaseAmount, processingFee, totalAmount, depositID, referenceID, descriptorName, nameOnAccount, walletID;
        LinearLayout copyRefID, copyDepositeID;
        headerText = findViewById(R.id.buySignetHeaderTV);
        amount = findViewById(R.id.signetAmountTV);
        status = findViewById(R.id.signetStatusTV);
        date = findViewById(R.id.signetdateTimeTV);
        purchaseAmount = findViewById(R.id.signetPurchaseTV);
        processingFee = findViewById(R.id.signetProcessingFeeTV);
        totalAmount = findViewById(R.id.signetTotalAmountTV);
        depositID = findViewById(R.id.signetDepositIDTV);
        referenceID = findViewById(R.id.signetReferIDTV);
        descriptorName = findViewById(R.id.signetDescNameTV);
        nameOnAccount = findViewById(R.id.signetNameOnAccountTV);
        walletID = findViewById(R.id.signetWalletIdTV);
        copyRefID = findViewById(R.id.copyRefID);
        copyDepositeID = findViewById(R.id.copyDepositID);


    }

    @SuppressLint("SetTextI18n")
    private void withdrawGiftCard(TransactionData objData) {
        TextView headerMsdTV, amountTV;
        TextView status, dateandtime, withGiftcardname, subtotal, fee, grandtotal, refid, withid, recipientname, email;
        LinearLayout previous, giftCardWithdrawID, giftcardReferenceID;
        ImageView withIdIV, refIDIV;

        headerMsdTV = findViewById(R.id.withGiftheadTV);
        amountTV = findViewById(R.id.withdrawGiftamount);
        status = findViewById(R.id.withdrawGiftStatusTV);
        dateandtime = findViewById(R.id.withGiftdateTimeTV);
        withGiftcardname = findViewById(R.id.withdrawGiftcardnameTV);
        subtotal = findViewById(R.id.withdrawGiftsubtotatlTV);
        fee = findViewById(R.id.withdrawGiftprofeeTV);
        grandtotal = findViewById(R.id.withdrawGiftgrandtotalTV);
        refid = findViewById(R.id.withdrawGiftrefidTV);
        withid = findViewById(R.id.withgiftid);
        recipientname = findViewById(R.id.withGiftRecipientNameTV);
        email = findViewById(R.id.withGiftReciEmailTV);
        giftCardWithdrawID = findViewById(R.id.giftCardwithdrawIDLL);
        giftcardReferenceID = findViewById(R.id.giftcardReferenceIDLL);
        previous = findViewById(R.id.withGiftprevious);
        withIdIV = findViewById(R.id.withdrawGiftIdIV);
        refIDIV = findViewById(R.id.withdrawRefIDIV);

        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            headerMsdTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getGiftCardAmount() != null) {
            amountTV.setText(Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));
        }

        if (objData.getStatus() != null) {
            status.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                case Utils.transCancelled:
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getGiftCardName() != null) {
            withGiftcardname.setText(objData.getGiftCardName());
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                refid.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                refid.setText(objData.getReferenceId());
            }

            giftcardReferenceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getRecipientName() != null) {
            recipientname.setText(Utils.capitalize(objData.getRecipientName()));
        }

        if (objData.getRecipientEmail() != null) {
            email.setText(objData.getRecipientEmail());
        }

        Double subtotall = null;
        if (objData.getTotalPaidAmount() != null) {
            subtotall = Double.parseDouble(objData.getTotalPaidAmount().replace("CYN", "").trim());
            grandtotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotall)));
        }

        if (objData.getGiftCardAmount() != null) {
            subtotal.setText("" + Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));
        }

        if (objData.getGiftCardFee() != null) {
            fee.setText("" + Utils.convertTwoDecimal(objData.getGiftCardFee().replace(" CYN", "").trim()));
        }

        if (objData.getCreatedDate() != null) {
            dateandtime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getWithdrawId() != null) {
            if (objData.getWithdrawId().length() > 10) {
                withid.setText(objData.getWithdrawId().substring(0, 10) + "...");
            } else {
                withid.setText(objData.getWithdrawId());
            }
            giftCardWithdrawID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getWithdrawId(), TransactionDetailsActivity.this);
                }
            });
        }
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void withdrawInstant(TransactionData objData) {
        LinearLayout previous, withdrawID, referenceID;
        TextView withiheader, withiamount, withidescription, withistatus, withidatetime, withiwithdrawalAmount, withiprocefee, withitotal, withiaccountbal, withiwithdrawalId, withirefId;
        TextView withicardHolderName, withicardnumber, withiexpirydate;
        ImageView withiwithdrawalid, withirefIDIV, withicardbrand;

        previous = findViewById(R.id.withInstantprevious);
        withiheader = findViewById(R.id.withinheaderTV);
        withiamount = findViewById(R.id.withinamount);
        withidescription = findViewById(R.id.withindescrptnTV);
        withistatus = findViewById(R.id.withinstatus);
        withidatetime = findViewById(R.id.withindateTimeTV);
        withiwithdrawalAmount = findViewById(R.id.withinwithdrawamount);
        withiprocefee = findViewById(R.id.withinprocessingfee);
        withitotal = findViewById(R.id.withintotalamount);
        withiaccountbal = findViewById(R.id.withinaccountbal);
        withiwithdrawalId = findViewById(R.id.withinwithdrawidTV);
        withiwithdrawalid = findViewById(R.id.withinwithIDIV);
        withirefId = findViewById(R.id.withinrefid);
        withirefIDIV = findViewById(R.id.withinrefIDIV);
        withdrawID = findViewById(R.id.withiwithdrawalLL);
        referenceID = findViewById(R.id.withiReferenceIDLL);
        withicardHolderName = findViewById(R.id.withincardholdername);
        withicardbrand = findViewById(R.id.withincardbrandIV);
        withicardnumber = findViewById(R.id.withincardnumTV);
        withiexpirydate = findViewById(R.id.withinexpdateTV);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            withiheader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getReceivedAmount() != null) {
            withiamount.setText(Utils.convertTwoDecimal(objData.getReceivedAmount().replace("USD", "").trim()));
        }

        if (objData.getRemarks() != null && !objData.getRemarks().equals("")) {
            withidescription.setText("\"" + objData.getRemarks() + "\"");
        } else {
            withidescription.setVisibility(View.GONE);
        }

        if (objData.getStatus() != null) {
            withistatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case "completed":
                    withistatus.setTextColor(getResources().getColor(R.color.completed_status));
                    withistatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case "in progress":
                case "inprogress":
                    withistatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    withistatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case "pending":
                    withistatus.setTextColor(getResources().getColor(R.color.pending_status));
                    withistatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case "failed":
                    withistatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withistatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            withidatetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getWithdrawAmount() != null) {
            withiwithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getProcessingFee() != null) {
            withiprocefee.setText(Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getTotalAmount() != null) {
            withitotal.setText(Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()) + " CYN");
        }


        if (objData.getAccountBalance() != null) {
            withiaccountbal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getWithdrawalId() != null) {
            if (objData.getWithdrawalId().length() > 10) {
                withiwithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                withiwithdrawalId.setText(objData.getWithdrawalId());
            }

            withdrawID.setOnClickListener(view -> Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this));
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                withirefId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                withirefId.setText(objData.getReferenceId());
            }

            referenceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getCardHolderName() != null) {
            withicardHolderName.setText(objData.getCardHolderName());
        }

        if (objData.getCardNumber() != null) {
            withicardnumber.setText("\u2022\u2022\u2022\u2022" + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
        }

        if (objData.getCardBrand() != null) {
            switch (objData.getCardBrand()) {
                case Utils.MASTERCARD:
                    withicardbrand.setImageResource(R.drawable.ic_master);
                    break;
                case Utils.VISA:
                    withicardbrand.setImageResource(R.drawable.ic_visa);
                    break;
                case Utils.AMERICANEXPRESS:
                    withicardbrand.setImageResource(R.drawable.ic_amex);
                    break;
                case Utils.DISCOVER:
                    withicardbrand.setImageResource(R.drawable.ic_discover);
                    break;
            }
        }

        if (objData.getCardExpiryDate() != null) {
            withiexpirydate.setText(objData.getCardExpiryDate());
        }

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void withdrawBank(TransactionData objData) {
        TextView withBankHeader, withBankAmount, withBankDescription, withBankStatus, withBankDateTime, withBankWithdrawalAmount, withBankProcessingFee, withBankTotal, withBankAccountBal, withBankWithdrawalId, withBankRefId;
        TextView withBankNameOnAccount, withBankName, withBankAccount;
//        ImageView withbankwithdrawalid, withbankrefIDIV;
        LinearLayout withBankCloseLL, withBankWithdrawalID, withBankReference;

        withBankHeader = findViewById(R.id.withBankHeaderTV);
        withBankAmount = findViewById(R.id.withBankAmount);
        withBankDescription = findViewById(R.id.withBankDescrptnTV);
        withBankStatus = findViewById(R.id.withBankStatusTV);
        withBankDateTime = findViewById(R.id.withbankDatetimeTV);
        withBankWithdrawalAmount = findViewById(R.id.withBankWithdrawAmount);
        withBankProcessingFee = findViewById(R.id.withBankProcessFee);
        withBankTotal = findViewById(R.id.withBankTotalTV);
        withBankAccountBal = findViewById(R.id.withBankAccBalanceTV);
        withBankWithdrawalId = findViewById(R.id.withBankWithdrawidTV);
        withBankRefId = findViewById(R.id.withBankReferenceIDTV);
        withBankWithdrawalID = findViewById(R.id.withBankWithdrawalIDLL);
        withBankReference = findViewById(R.id.withBankReferenceIDLL);
        withBankNameOnAccount = findViewById(R.id.withBankNameOnAccountTV);
        withBankName = findViewById(R.id.withBankBanknameTV);
        withBankAccount = findViewById(R.id.withBankbankaccountTV);

        withBankCloseLL = findViewById(R.id.withbankCloseLL);

        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            withBankHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getWithdrawAmount() != null) {
            if (objData.getWithdrawAmount().toLowerCase().contains("cyn")) {
                withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
            } else {
                withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("USD", "").trim()));

            }
        }

        if (objData.getRemarks() != null && !objData.getRemarks().equals("")) {
            withBankDescription.setText("\"" + objData.getRemarks() + "\"");
        } else {
            withBankDescription.setVisibility(View.GONE);

        }
        if (objData.getStatus() != null) {
            withBankStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    withBankStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                case Utils.transInProgress:
                    withBankStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    withBankStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            withBankDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }
//
//        Double subtotal = Double.parseDouble(objData.getWithdrawAmount().replace("USD", "").trim());
//        Double procesFee = Double.parseDouble(objData.getProcessingFee().replace("CYN", "").trim());

//        withBankWithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("USD", "").trim()));
        if (objData.getWithdrawAmount() != null) {
            if (objData.getWithdrawAmount().toLowerCase().contains("cyn")) {
                withBankWithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
            } else {
                withBankWithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("USD", "").trim()));

            }
        }

        if (objData.getProcessingFee() != null) {
            withBankProcessingFee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        }

        if (objData.getTotalAmount() != null) {
            withBankTotal.setText("" + Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()));
        }

        if (objData.getAccountBalance() != null)
            withBankAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");

        if (objData.getWithdrawalId() != null) {
            if (objData.getWithdrawalId().length() > 10) {
                withBankWithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                withBankWithdrawalId.setText(objData.getWithdrawalId());
            }

            withBankWithdrawalID.setOnClickListener(view -> Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this));

        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                withBankRefId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                withBankRefId.setText(objData.getReferenceId());
            }

            withBankReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getNameOnBankAccount() != null) {
            withBankNameOnAccount.setText(objData.getNameOnBankAccount());
        }

        if (objData.getBankName() != null) {
            withBankName.setText(objData.getBankName());
        }


        if (objData.getBankAccountNumber() != null && objData.getBankAccountNumber().length() > 4) {
            withBankAccount.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        }

        withBankCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void withdrawSignet(TransactionData objData) {

        TextView withBankHeader, withBankAmount, withBankDescription, withBankStatus, withBankDateTime, withBankWithdrawalAmount, withBankProcessingFee, withBankTotal, withBankAccountBal, withBankWithdrawalId, withBankRefId;
        TextView withBankNameOnAccount, withBankName, withBankAccount, signetTextTV;
//        ImageView withbankwithdrawalid, withbankrefIDIV;
        LinearLayout withBankCloseLL, withBankWithdrawalID, withBankReference;

        withBankHeader = findViewById(R.id.withBankHeaderTV);
        withBankAmount = findViewById(R.id.withBankAmount);
        withBankDescription = findViewById(R.id.withBankDescrptnTV);
        withBankStatus = findViewById(R.id.withBankStatusTV);
        withBankDateTime = findViewById(R.id.withbankDatetimeTV);
        withBankWithdrawalAmount = findViewById(R.id.withBankWithdrawAmount);
        withBankProcessingFee = findViewById(R.id.withBankProcessFee);
        withBankTotal = findViewById(R.id.withBankTotalTV);
        withBankAccountBal = findViewById(R.id.withBankAccBalanceTV);
        withBankWithdrawalId = findViewById(R.id.withBankWithdrawidTV);
        withBankRefId = findViewById(R.id.withBankReferenceIDTV);
        withBankWithdrawalID = findViewById(R.id.withBankWithdrawalIDLL);
        withBankReference = findViewById(R.id.withBankReferenceIDLL);
        withBankNameOnAccount = findViewById(R.id.withBankNameOnAccountTV);
        withBankName = findViewById(R.id.withBankBanknameTV);
        withBankAccount = findViewById(R.id.withBankbankaccountTV);
        signetTextTV = findViewById(R.id.bankNameORSignetTV);
        withBankCloseLL = findViewById(R.id.withbankCloseLL);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            withBankHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getWithdrawAmount() != null) {
            withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        }
//        if (objData.getDescription().equals("")) {
//            withbankdescription.setVisibility(View.GONE);
//        } else {
//            withbankdescription.setText("\"" + objData.getDescription() + "\"");
//        }
        if (objData.getStatus() != null) {
            withBankStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    withBankStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                case Utils.transInProgress:
                    withBankStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    withBankStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                case Utils.transCancelled:
                    withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            withBankDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }


        if (objData.getWithdrawAmount() != null) {
            withBankWithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        }

        if (objData.getProcessingFee() != null) {
            withBankProcessingFee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        }

        if (objData.getTotalAmount() != null) {
            withBankTotal.setText("" + Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()));
        }

        if (objData.getAccountBalance() != null) {
            withBankAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getWithdrawalId() != null) {
            if (objData.getWithdrawalId().length() > 10) {
                withBankWithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                withBankWithdrawalId.setText(objData.getWithdrawalId());
            }

            withBankWithdrawalID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
                }
            });
        }


        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10)
                withBankRefId.setText(objData.getReferenceId().substring(0, 10) + "...");
            else {
                withBankRefId.setText(objData.getReferenceId());
            }

            withBankReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getNameOnBank() != null) {
            withBankNameOnAccount.setText(objData.getNameOnBank());
        }

        signetTextTV.setText("Signet Wallet ID");
//        signetTextTV.setPadding(0, 0, 0, 20);
//        findViewById(R.id.nameOnAccount).setPadding(0, 10, 0, 0);
//        findViewById(R.id.withdrawIDTV).setPadding(30, 30, 0, 0);
        if (objData.getWalletId() != null) {
            if (objData.getWalletId().length() > 20) {
                withBankName.setText(objData.getWalletId().substring(0, 20) + "...");
            } else {
                withBankName.setText(objData.getWalletId());
            }
        }

        findViewById(R.id.bankAccountLL).setVisibility(View.GONE);


        withBankCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (withBankDescription.getVisibility() == View.VISIBLE) {
            withBankDescription.setVisibility(View.GONE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void businessPayout(TransactionData businessPayoutData) {
        try {
            TextView headerNameTV, amountTV, statusTV, dateAndTime, payoutID, refID, payoutDate, totalAmount, totalTransactions, depositID;
            LinearLayout copyPayoutID, copyReferenceIDLL;
            String payoutId = "";

            headerNameTV = findViewById(R.id.mPayoutheaderTV);
            amountTV = findViewById(R.id.merchantamountTV);
            statusTV = findViewById(R.id.merchantstatusTV);
            dateAndTime = findViewById(R.id.merchantdateTv);
            payoutID = findViewById(R.id.mPayoutIdTV);
            copyPayoutID = findViewById(R.id.copyPayoutIDLL);
            copyReferenceIDLL = findViewById(R.id.copuRefIDLL);
            refID = findViewById(R.id.mreferenceIdTV);
            payoutDate = findViewById(R.id.merchantPIdateTV);
            totalAmount = findViewById(R.id.mPItotalamountTV);
            totalTransactions = findViewById(R.id.mPItotaltransactionsTV);
            depositID = findViewById(R.id.mPIdeposittoTV);

            if (businessPayoutData.getTransactionType() != null) {
                headerNameTV.setText(businessPayoutData.getTransactionType());
            }

            if (businessPayoutData.getTotalAmount() != null) {
                amountTV.setText(Utils.convertTwoDecimal(businessPayoutData.getTotalAmount().replace("CYN", "").trim()));
            }

            if (businessPayoutData.getStatus() != null) {
                statusTV.setText(businessPayoutData.getStatus());
                switch (businessPayoutData.getStatus().toLowerCase()) {
                    case "completed":
                        statusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case "in progress":
                    case "inprogress":
                        statusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case "pending":
                        statusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case "failed":
                    case "cancelled":
                        statusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }

            if (businessPayoutData.getCreatedDate() != null) {
                dateAndTime.setText(objMyApplication.convertZoneLatestTxn(businessPayoutData.getCreatedDate()));
            }

            if (businessPayoutData.getPayoutId() != null) {
                if (businessPayoutData.getPayoutId().length() > 10) {
                    payoutId = businessPayoutData.getPayoutId().substring(0, 10) + "...";
                    payoutID.setText(Html.fromHtml("<u>" + payoutId + "</u>"));
                } else {
                    //                payoutID.setText(businessPayoutData.getPayoutId());
                    payoutId = businessPayoutData.getPayoutId();
                    payoutID.setText(Html.fromHtml("<u>" + payoutId + "</u>"));
                }

                copyPayoutID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.copyText(businessPayoutData.getPayoutId(), TransactionDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if (businessPayoutData.getReferenceId() != null) {
                if (businessPayoutData.getReferenceId().length() > 10) {
                    refID.setText(businessPayoutData.getReferenceId().substring(0, 10) + "...");
                } else {
                    refID.setText(businessPayoutData.getReferenceId());
                }

                copyReferenceIDLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.copyText(businessPayoutData.getReferenceId(), TransactionDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


            if (businessPayoutData.getPayoutCreatedDate() != null && businessPayoutData.getPayoutUpdatedDate() != null) {
                String startDate = objMyApplication.convertPayoutDateTimeZone(businessPayoutData.getPayoutCreatedDate());
                String endDate = objMyApplication.convertPayoutDateTimeZone(businessPayoutData.getPayoutUpdatedDate());

                payoutDate.setText(startDate + " to " + endDate);
            }

            if (businessPayoutData.getTotalAmount() != null) {
                totalAmount.setText(Utils.convertTwoDecimal(businessPayoutData.getTotalAmount().replace("CYN", "").trim()) + " ");
            }

            if (businessPayoutData.getTotalTransactions() != null) {
                totalTransactions.setText(businessPayoutData.getTotalTransactions());
            }


            if (businessPayoutData.getDepositTo() != null) {
                if (businessPayoutData.getDepositTo().length() > 10) {
                    depositID.setText(businessPayoutData.getDepositTo().substring(0, 10) + "...");
                } else {
                    depositID.setText(businessPayoutData.getDepositTo());
                }
            }

            findViewById(R.id.Mpayoutll).setOnClickListener(view -> finish());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void failedWithdraw(TransactionData failedData) {
        try {
            TextView transactionTypeTV, amountTV, statusTV, dateAndTime, refID, reasonFailed, achWithTV, achRefID, withdrawAmountTV, processingFeeTV, totalAmountTV;
            LinearLayout copyReferenceIDLL, copyACHWithID;

            transactionTypeTV = findViewById(R.id.headerTxtTV);
            amountTV = findViewById(R.id.amountTV);
            statusTV = findViewById(R.id.statusTV);
            dateAndTime = findViewById(R.id.dateAndTimeTV);
            refID = findViewById(R.id.referenceIDTV);
            copyReferenceIDLL = findViewById(R.id.copyRefIDLL);
            reasonFailed = findViewById(R.id.reasonForFailed);
            achWithTV = findViewById(R.id.ACHWithdrawalIDTV);
            copyACHWithID = findViewById(R.id.copyACHWithdrawalIDTV);
            achRefID = findViewById(R.id.achReferenceIDTV);
            withdrawAmountTV = findViewById(R.id.withdrawAmountTV);
            processingFeeTV = findViewById(R.id.withBankProcessFee);
            totalAmountTV = findViewById(R.id.totalAmountTV);

            if (failedData.getTransactionType() != null) {
                transactionTypeTV.setText(failedData.getTransactionType());
            }


            amountTV.setText(Utils.convertTwoDecimal(failedData.getWithdrawAmount().replace("CYN", "").trim()));


            if (failedData.getStatus() != null) {
                statusTV.setText(failedData.getStatus());
                switch (failedData.getStatus().toLowerCase()) {
                    case "completed":
                        statusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case "in progress":
                    case "inprogress":
                        statusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case "pending":
                        statusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case "failed":
                    case "cancelled":
                        statusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }

            if (failedData.getCreatedDate() != null) {
                dateAndTime.setText(objMyApplication.convertZoneLatestTxn(failedData.getCreatedDate()));
            }

            if (failedData.getReferenceId() != null) {
                if (failedData.getReferenceId().length() > 10) {
                    refID.setText(failedData.getReferenceId().substring(0, 10) + "...");
                } else {
                    refID.setText(failedData.getReferenceId());
                }

                copyReferenceIDLL.setOnClickListener(view -> {
                    try {
                        Utils.copyText(failedData.getReferenceId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }


            if (failedData.getFailedReason() != null) {
                reasonFailed.setText(failedData.getFailedReason());
            }

            if (failedData.getAchReferenceId() != null) {
                if (failedData.getWithdrawId().length() > 10) {
                    achWithTV.setText(failedData.getWithdrawId().substring(0, 10) + "...");
                } else {
                    achWithTV.setText(failedData.getWithdrawId());
                }

                copyACHWithID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.copyText(failedData.getWithdrawId(), TransactionDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            String achRefId = "";
            if (failedData.getAchReferenceId() != null) {
                if (failedData.getAchReferenceId().length() > 10) {
                    achRefId = failedData.getAchReferenceId().substring(0, 10) + "...";
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                } else {
                    achRefId = failedData.getAchReferenceId();
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                }
            }

            if (failedData.getWithdrawAmount() != null) {
                withdrawAmountTV.setText(Utils.convertTwoDecimal(failedData.getWithdrawAmount().replace("CYN", "").trim()));
            }

            if (failedData.getProcessingFee() != null) {
                processingFeeTV.setText(Utils.convertTwoDecimal(failedData.getProcessingFee().replace("CYN", "").trim()));
            }

            if (failedData.getTotalAmount() != null) {
                totalAmountTV.setText(Utils.convertTwoDecimal(failedData.getTotalAmount().replace("CYN", "").trim()));
            }
        } catch (Resources.NotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("SetTextI18n")
    private void cancelledWithdraw(TransactionData cancelledData) {
        try {
            TextView transactionTypeTV, amountTV, statusTV, dateAndTime, refID, reasonFailed, achWithTV, achRefID, withdrawAmountTV, processingFeeTV, totalAmountTV;
            LinearLayout copyReferenceIDLL, copyACHWithID;

            transactionTypeTV = findViewById(R.id.headerTxtTV);
            amountTV = findViewById(R.id.amountTV);
            statusTV = findViewById(R.id.statusTV);
            dateAndTime = findViewById(R.id.dateAndTimeTV);
            refID = findViewById(R.id.referenceIDTV);
            copyReferenceIDLL = findViewById(R.id.copyRefIDLL);
            reasonFailed = findViewById(R.id.reasonForFailed);
            achWithTV = findViewById(R.id.ACHWithdrawalIDTV);
            copyACHWithID = findViewById(R.id.copyACHWithdrawalIDTV);
            achRefID = findViewById(R.id.achReferenceIDTV);
            withdrawAmountTV = findViewById(R.id.withdrawAmountTV);
            processingFeeTV = findViewById(R.id.withBankProcessFee);
            totalAmountTV = findViewById(R.id.totalAmountTV);

            if (cancelledData.getTransactionType() != null) {
                transactionTypeTV.setText(cancelledData.getTransactionType());
            }


            amountTV.setText(Utils.convertTwoDecimal(cancelledData.getWithdrawAmount().replace("CYN", "").trim()));


            if (cancelledData.getStatus() != null) {
                statusTV.setText(cancelledData.getStatus());
                switch (cancelledData.getStatus().toLowerCase()) {
                    case "completed":
                        statusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case "in progress":
                    case "inprogress":
                        statusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case "pending":
                        statusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case "failed":
                    case "cancelled":
                        statusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }


            if (cancelledData.getCreatedDate() != null) {
                dateAndTime.setText(objMyApplication.convertZoneLatestTxn(cancelledData.getCreatedDate()));
            }

            if (cancelledData.getReferenceId() != null) {
                if (cancelledData.getReferenceId().length() > 10)
                    refID.setText(cancelledData.getReferenceId().substring(0, 10) + "...");
                else {
                    refID.setText(cancelledData.getReferenceId());
                }

                copyReferenceIDLL.setOnClickListener(view -> {
                    try {
                        Utils.copyText(cancelledData.getReferenceId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            findViewById(R.id.failedReTV).setVisibility(View.GONE);
            reasonFailed.setVisibility(View.GONE);

            if (cancelledData.getAchReferenceId() != null) {
                if (cancelledData.getWithdrawId().length() > 10) {
                    achWithTV.setText(cancelledData.getWithdrawId().substring(0, 10) + "...");
                } else {
                    achWithTV.setText(cancelledData.getWithdrawId());
                }

                copyACHWithID.setOnClickListener(view -> {
                    try {
                        Utils.copyText(cancelledData.getWithdrawId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }


            String achRefId;
            if (cancelledData.getAchReferenceId() != null) {
                if (cancelledData.getAchReferenceId().length() > 10) {
                    achRefId = cancelledData.getAchReferenceId().substring(0, 10) + "...";
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                } else {
                    achRefId = cancelledData.getAchReferenceId();
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                }
            }

            if (cancelledData.getWithdrawAmount() != null) {
                withdrawAmountTV.setText(Utils.convertTwoDecimal(cancelledData.getWithdrawAmount().replace("CYN", "").trim()));
            }

            if (cancelledData.getProcessingFee() != null) {
                processingFeeTV.setText(Utils.convertTwoDecimal(cancelledData.getProcessingFee().replace("CYN", "").trim()));
            }

            if (cancelledData.getTotalAmount() != null) {
                totalAmountTV.setText(Utils.convertTwoDecimal(cancelledData.getTotalAmount().replace("CYN", "").trim()));
            }

        } catch (Resources.NotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case PAY_REQUEST: {
                    findViewById(R.id.payrequest).setVisibility(View.VISIBLE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case BUY_TOKEN: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.VISIBLE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case BUY_BANK: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case BUY_SIGNET: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.VISIBLE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case WITH_GIFT: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case WITH_Instant: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case WITH_BANK:
                case WITH_SIGNET: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                }
                break;
                case BUSINESS_PAYOUT: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.VISIBLE);
                }
                break;
                case CANCELLED_WITH:
                case FAILED_WITH: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.VISIBLE);

                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cancelPopup() {
        try {
            final Dialog dialog = new Dialog(TransactionDetailsActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.canceltransaction);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvNo = dialog.findViewById(R.id.tvNo);
            TextView tvYes = dialog.findViewById(R.id.tvYes);

            tvNo.setOnClickListener(v -> dialog.dismiss());
            tvYes.setOnClickListener(v -> {
                try {
                    dialog.dismiss();
                    progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                    dashboardViewModel.cancelBuyToken(strGbxTxnIdType);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
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
            if (getIntent().getStringExtra("gbxTxnIdType") != null && !getIntent().getStringExtra("gbxTxnIdType").equals("")) {
                strGbxTxnIdType = getIntent().getStringExtra("gbxTxnIdType");
            }
            if (getIntent().getStringExtra("txnType") != null && !getIntent().getStringExtra("txnType").equals("")) {
                //txnType = Integer.parseInt(getIntent().getStringExtra("txnType"));
                switch (getIntent().getStringExtra("txnType").toLowerCase()) {
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
                }
            }
            if (getIntent().getStringExtra("txnSubType") != null && !getIntent().getStringExtra("txnSubType").equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (getIntent().getStringExtra("txnSubType").toLowerCase()) {
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
                        ControlMethod("payrequest");
                        payRequest(transactionDetails.getData());
                        break;
                    case "buy token":
                    case "buy tokens":
                        switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                            case "credit card":
                            case "debit card":
                                ControlMethod("buytoken");
                                buyTokenCreditDebit(transactionDetails.getData());
                                break;
                            case "bank account":
                                ControlMethod("buytokenbank");
                                buyTokenBankAccount(transactionDetails.getData());
                                break;
                            case "signet":
                                ControlMethod("buytokensignet");
                                buyTokenSignet(transactionDetails.getData());
                                break;
                        }
                        break;
                    case "withdraw":
                        switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                            case "gift card":
                                ControlMethod("withdrawgift");
                                withdrawGiftCard(transactionDetails.getData());
                                break;
                            case "instant pay":
                                ControlMethod("withdrawinstant");
                                withdrawInstant(transactionDetails.getData());
                                break;
                            case "bank account":
                                ControlMethod("withdrawbankaccount");
                                withdrawBank(transactionDetails.getData());
                                break;
                            case "signet":
                                ControlMethod("withdrawsignet");
                                withdrawSignet(transactionDetails.getData());
                                break;
                        }
                        break;
                    case "business payout": {

                        ControlMethod("businessPayout");
                        businessPayout(transactionDetails.getData());
                    }
                    break;
                    case "canceled bank withdraw": {
                        ControlMethod("cancelledWithdraw");
                        cancelledWithdraw(transactionDetails.getData());
                    }
                    break;
                    case "failed bank withdraw": {
                        ControlMethod("failedWithdraw");
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
            headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype().replace("Tokens", ""));

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
            if (!objData.getSenderMessage().equals("")) {
                descrptn.setText("\"" + objData.getSenderMessage() + "\"");
            } else {
                descrptn.setVisibility(View.GONE);
            }
            completed.setText(objData.getStatus());
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            balance.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
            refid.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");


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

            lyPRClose.setOnClickListener(view -> onBackPressed());
            lyRefId.setOnClickListener(view -> Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this));

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

        headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());

        amount.setText(Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));

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
        datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        purchaseAmountTV.setText(objData.getYouPay());

        switch (objData.getCardBrand()) {
            case "MASTERCARD":
                cardBrandIV.setImageResource(R.drawable.ic_master);
                break;
            case "VISA":
                cardBrandIV.setImageResource(R.drawable.ic_visa);
                break;
            case "AMERICAN EXPRESS":
                cardBrandIV.setImageResource(R.drawable.ic_amex);
                break;
            case "DISCOVER":
                cardBrandIV.setImageResource(R.drawable.ic_discover);
                break;
        }


        Double purchaseAmount = Double.parseDouble(objData.getYouGet().replace("CYN", "").trim());
        Double processingFee = Double.parseDouble(objData.getProcessingFee().replace("USD", "").trim());
        datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        purchaseAmountTV.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        fee.setText("$" + Utils.convertTwoDecimal((objData.getProcessingFee().replace("USD", "").trim())));
        total.setText("$" + Utils.convertTwoDecimal(String.valueOf(purchaseAmount + processingFee)));
        balance.setText(Utils.convertTwoDecimal((objData.getAccountBalance().replace("CYN", "").trim())) + " CYN");

        refid.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        descriptorName.setText(objData.getDescriptorName());
        name.setText(objData.getCardHolderName());
        cardNumber.setText("\u2022\u2022\u2022\u2022" + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
        expiryDate.setText(objData.getCardExpiryDate());
        depositIDTV.setText(objData.getDepositid().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        lyPRClose.setOnClickListener(view -> onBackPressed());
        referenceID.setOnClickListener(view -> Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this));
        depositID.setOnClickListener(view -> Utils.copyText(objData.getDepositid(), TransactionDetailsActivity.this));

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
        headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());

        amount.setText(objData.getYouGet().replace("CYN", ""));

        status.setText(objData.getStatus());
        cancelTxnCV.setVisibility(View.GONE);
        switch (objData.getStatus().toLowerCase()) {
            case "completed":
                status.setTextColor(getResources().getColor(R.color.completed_status));
                status.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
                status.setTextColor(getResources().getColor(R.color.inprogress_status));
                status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                cancelTxnCV.setVisibility(View.VISIBLE);
                break;
            case "pending":
                status.setTextColor(getResources().getColor(R.color.pending_status));
                status.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case "failed":
                status.setTextColor(getResources().getColor(R.color.failed_status));
                status.setBackgroundResource(R.drawable.txn_failed_bg);
                break;
            case "cancelled": {
                status.setTextColor(getResources().getColor(R.color.failed_status));
                status.setBackgroundResource(R.drawable.txn_failed_bg);
                descriptorLL.setVisibility(View.GONE);
                break;
            }
        }
        datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        purchaseamount.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        fee.setText("$" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("USD", "").trim()));
        total.setText("$" + Utils.convertTwoDecimal(objData.getYouPay().replace("USD", "").trim()));
        bankAccNumTV.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        refid.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        descriptorname.setText(objData.getDescriptorName());
        depositIDTV.setText(objData.getDepositid().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        bankNameTV.setText(objData.getBankName());

        nameOnAccTV.setText(objData.getNameOnBankAccount());

        cancelTxnCV.setOnClickListener(view -> {
            cancelPopup();
        });

        lyPRClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
        btBankDepositID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Utils.copyText(objData.getDepositid(), TransactionDetailsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        headerMsdTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        amountTV.setText(Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));

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

        withGiftcardname.setText(objData.getGiftCardName());

        refid.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        recipientname.setText(Utils.capitalize(objData.getRecipientName()));
        email.setText(objData.getRecipientEmail());

        Double subtotatl = Double.parseDouble(objData.getTotalPaidAmount().replace("CYN", "").trim());

        subtotal.setText("" + Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));
        fee.setText("" + Utils.convertTwoDecimal(objData.getGiftCardFee().replace(" CYN", "").trim()));
        grandtotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotatl)));

        dateandtime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        if (objData.getWithdrawId().length() > 10) {
            withid.setText(objData.getWithdrawId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        } else {
            withid.setText(objData.getWithdrawId());
        }
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        giftcardReferenceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
            }
        });
        giftCardWithdrawID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getWithdrawId(), TransactionDetailsActivity.this);
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


        withiheader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        withiamount.setText(Utils.convertTwoDecimal(objData.getReceivedAmount().replace("USD", "").trim()));
//        if (objData.getDescription().equals("")) {
//            withidescription.setVisibility(View.GONE);
//        } else {
//            withidescription.setText("\"" + objData.getDescription() + "\"");
//        }
        if (objData.getRemarks().equals("")) {
            withidescription.setVisibility(View.GONE);
        } else {
            withidescription.setText("\"" + objData.getRemarks() + "\"");
        }
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

        withidatetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        withiwithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()) + " CYN");

        withiprocefee.setText(Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()) + " CYN");

        withitotal.setText(Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()) + " CYN");

        withiaccountbal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");

        if (objData.getWithdrawalId().length() > 10) {
            withiwithdrawalId.setText(objData.getWithdrawalId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        } else {
            withiwithdrawalId.setText(objData.getWithdrawalId());
        }

        withirefId.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");

        withicardHolderName.setText(objData.getCardHolderName());

        withicardnumber.setText("\u2022\u2022\u2022\u2022" + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));

        switch (objData.getCardBrand()) {
            case "MASTERCARD":
                withicardbrand.setImageResource(R.drawable.ic_master);
                break;
            case "VISA":
                withicardbrand.setImageResource(R.drawable.ic_visa);
                break;
            case "AMERICAN EXPRESS":
                withicardbrand.setImageResource(R.drawable.ic_amex);
                break;
            case "DISCOVER":
                withicardbrand.setImageResource(R.drawable.ic_discover);
                break;
        }

        withiexpirydate.setText(objData.getCardExpiryDate());

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        referenceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
            }
        });
        withdrawID.setOnClickListener(view -> Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this));


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
//        withbankwithdrawalid = findViewById(R.id.withBankwithdrawIDIV);
//        withbankrefIDIV = findViewById(R.id.withBankReferenceIDIV);


        withBankHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
//        if (objData.getDescription().equals("")) {
//            withbankdescription.setVisibility(View.GONE);
//        } else {
//            withbankdescription.setText("\"" + objData.getDescription() + "\"");
//        }
        if (objData.getRemarks().equals("")) {
            withBankDescription.setVisibility(View.GONE);
        } else {
            withBankDescription.setText("\"" + objData.getRemarks() + "\"");
        }
        withBankStatus.setText(objData.getStatus());
        switch (objData.getStatus().toLowerCase()) {
            case "completed":
                withBankStatus.setTextColor(getResources().getColor(R.color.completed_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
            case "inprogress":
                withBankStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
            case "pending":
                withBankStatus.setTextColor(getResources().getColor(R.color.pending_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case "failed":
                withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                break;
        }

        withBankDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        Double subtotal = Double.parseDouble(objData.getWithdrawAmount().replace("CYN", "").trim());
        Double procesFee = Double.parseDouble(objData.getProcessingFee().replace("CYN", "").trim());

        withBankWithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        withBankProcessingFee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        withBankTotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotal + procesFee)));

        withBankAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");

        if (objData.getWithdrawalId().length() > 10) {
            withBankWithdrawalId.setText(objData.getWithdrawalId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        } else {
            withBankWithdrawalId.setText(objData.getWithdrawalId());
        }

        withBankRefId.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");

        withBankNameOnAccount.setText(objData.getNameOnBankAccount());

        withBankName.setText(objData.getBankName());


        withBankAccount.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        withBankCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        withBankReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
            }
        });
        withBankWithdrawalID.setOnClickListener(view -> Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this));

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
//        withbankwithdrawalid = findViewById(R.id.withBankwithdrawIDIV);
//        withbankrefIDIV = findViewById(R.id.withBankReferenceIDIV);


        withBankHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
//        if (objData.getDescription().equals("")) {
//            withbankdescription.setVisibility(View.GONE);
//        } else {
//            withbankdescription.setText("\"" + objData.getDescription() + "\"");
//        }
        withBankStatus.setText(objData.getStatus());
        switch (objData.getStatus().toLowerCase()) {
            case "completed":
                withBankStatus.setTextColor(getResources().getColor(R.color.completed_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
            case "inprogress":
                withBankStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
            case "pending":
                withBankStatus.setTextColor(getResources().getColor(R.color.pending_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case "failed":
            case "cancelled":
                withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                break;
        }

        withBankDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        Double subtotal = Double.parseDouble(objData.getWithdrawAmount().replace("CYN", "").trim());
        Double procesFee = Double.parseDouble(objData.getProcessingFee().replace("CYN", "").trim());

        withBankWithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        withBankProcessingFee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        withBankTotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotal + procesFee)));

        withBankAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");

        if (objData.getWithdrawalId().length() > 10) {
            withBankWithdrawalId.setText(objData.getWithdrawalId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        } else {
            withBankWithdrawalId.setText(objData.getWithdrawalId());
        }

        if (objData.getReferenceId().length() > 10)
            withBankRefId.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
        else {
            withBankRefId.setText(objData.getReferenceId());
        }
        withBankNameOnAccount.setText(objData.getNameOnBank());

        signetTextTV.setText("Signet Wallet ID");
//        signetTextTV.setPadding(0, 0, 0, 20);
//        findViewById(R.id.nameOnAccount).setPadding(0, 10, 0, 0);
//        findViewById(R.id.withdrawIDTV).setPadding(30, 30, 0, 0);
        if (objData.getWithdrawalId().length() > 20) {
            withBankName.setText(objData.getWalletId().substring(0, 20) + "...");
        } else {
            withBankName.setText(objData.getWalletId());
        }
        findViewById(R.id.bankAccountLL).setVisibility(View.GONE);


        withBankCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        withBankReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
            }
        });
        withBankWithdrawalID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
            }
        });

        withBankDescription.setVisibility(View.GONE);

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

            headerNameTV.setText(businessPayoutData.getTransactionType());

            amountTV.setText(Utils.convertTwoDecimal(businessPayoutData.getTotalAmount().replace("CYN", "").trim()));

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

            dateAndTime.setText(objMyApplication.convertZoneLatestTxn(businessPayoutData.getCreatedDate()));

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

            payoutDate.setText(businessPayoutData.getPayoutDate());

            totalAmount.setText(Utils.convertTwoDecimal(businessPayoutData.getTotalAmount().replace("CYN", "").trim()) + " ");

            totalTransactions.setText(businessPayoutData.getTotalTransactions());


            if (businessPayoutData.getDepositTo().length() > 10) {
                depositID.setText(businessPayoutData.getDepositTo().substring(0, 10) + "...");
            } else {
                depositID.setText(businessPayoutData.getDepositTo());
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

            transactionTypeTV.setText(failedData.getTransactionType());

            statusTV.setText(failedData.getStatus());

            amountTV.setText(Utils.convertTwoDecimal(failedData.getWithdrawAmount().replace("CYN", "").trim()));


            try {
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
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            dateAndTime.setText(objMyApplication.convertZoneLatestTxn(failedData.getCreatedDate()));

            if (failedData.getReferenceId().length() > 10)
                refID.setText(failedData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            else {
                refID.setText(failedData.getReferenceId());
            }

            copyReferenceIDLL.setOnClickListener(view -> {
                try {
                    Utils.copyText(failedData.getReferenceId(), TransactionDetailsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            reasonFailed.setText(failedData.getFailedReason());

            if (failedData.getAchReferenceId() != null) {
                if (failedData.getWithdrawId().length() > 10) {
                    achWithTV.setText(failedData.getWithdrawId().substring(0, 10) + "...");
                } else {
                    achWithTV.setText(failedData.getWithdrawId());
                }
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

            String achRefId = "";
            if (failedData.getAchReferenceId().length() > 10) {
                achRefId = failedData.getAchReferenceId().substring(0, 10) + "...";
                achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
            } else {
                achRefId = failedData.getAchReferenceId();
                achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
            }

            withdrawAmountTV.setText(Utils.convertTwoDecimal(failedData.getWithdrawAmount().replace("CYN", "").trim()));

            processingFeeTV.setText(Utils.convertTwoDecimal(failedData.getProcessingFee().replace("CYN", "").trim()));

            totalAmountTV.setText(Utils.convertTwoDecimal(failedData.getTotalAmount().replace("CYN", "").trim()));
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

            transactionTypeTV.setText(cancelledData.getTransactionType());

            statusTV.setText(cancelledData.getStatus());

            amountTV.setText(Utils.convertTwoDecimal(cancelledData.getWithdrawAmount().replace("CYN", "").trim()));


            try {
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
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            dateAndTime.setText(objMyApplication.convertZoneLatestTxn(cancelledData.getCreatedDate()));

            if (cancelledData.getReferenceId().length() > 10)
                refID.setText(cancelledData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
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
            findViewById(R.id.failedReTV).setVisibility(View.GONE);
            reasonFailed.setVisibility(View.GONE);

            if (cancelledData.getAchReferenceId() != null) {
                if (cancelledData.getWithdrawId().length() > 10) {
                    achWithTV.setText(cancelledData.getWithdrawId().substring(0, 10) + "...");
                } else {
                    achWithTV.setText(cancelledData.getWithdrawId());
                }
            }
            copyACHWithID.setOnClickListener(view -> {
                try {
                    Utils.copyText(cancelledData.getWithdrawId(), TransactionDetailsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            String achRefId;
            if (cancelledData.getAchReferenceId().length() > 10) {
                achRefId = cancelledData.getAchReferenceId().substring(0, 10) + "...";
                achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
            } else {
                achRefId = cancelledData.getAchReferenceId();
                achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
            }

            withdrawAmountTV.setText(Utils.convertTwoDecimal(cancelledData.getWithdrawAmount().replace("CYN", "").trim()));

            processingFeeTV.setText(Utils.convertTwoDecimal(cancelledData.getProcessingFee().replace("CYN", "").trim()));

            totalAmountTV.setText(Utils.convertTwoDecimal(cancelledData.getTotalAmount().replace("CYN", "").trim()));
        } catch (Resources.NotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "payrequest": {
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
                case "buytoken": {
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
                case "buytokenbank": {
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
                case "buytokensignet": {
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
                case "withdrawgift": {
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
                case "withdrawinstant": {
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
                case "withdrawbankaccount":
                case "withdrawsignet": {
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
                case "businessPayout": {
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
                case "cancelledWithdraw":
                case "failedWithdraw": {
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
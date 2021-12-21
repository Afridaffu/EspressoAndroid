package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class TransactionDetailsActivity extends AppCompatActivity {
    DashboardViewModel dashboardViewModel;
    MyApplication objMyApplication;
    String strGbxTxnIdType = "";
    int txnType, txnSubType;
    ProgressDialog progressDialog;

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
        dashboardViewModel.getTransactionDetailsMutableLiveData().observe(this, new Observer<TransactionDetails>() {
            @Override
            public void onChanged(TransactionDetails transactionDetails) {
                progressDialog.dismiss();
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
                                    ControlMethod("buytoken");
                                    buyTokenCreditDebit(transactionDetails.getData());
                                    break;
                                case "debit card":
                                    ControlMethod("buytoken");
                                    buyTokenCreditDebit(transactionDetails.getData());
                                    break;
                                case "bank account":
                                    ControlMethod("buytokenbank");
                                    buyTokenBankAccount(transactionDetails.getData());
                                    break;
                            }
                            break;
                        case "withdraw":
                            switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                                case "gift card":
                                    ControlMethod("withdrawgift");
                                    withdrawGiftcard(transactionDetails.getData());
                                    break;
                                case "instant pay":
                                    ControlMethod("withdrawinstant");
                                    withdrawInstant(transactionDetails.getData());
                                    break;
                                case "bank account":
                                    ControlMethod("withdrawbankaccount");
                                    withdrawBank(transactionDetails.getData());
                                    break;
                            }
                            break;
                    }
                }
            }
        });
    }


    private void payRequest(TransactionData objData) {
        try {
            TextView headerTV, amount, descrptn, completed, datetime, fee, total, balance;
            TextView refid, name, accountadress;
            LinearLayout lyPRClose, lyRefId, lyAccAdd, payreqPfLL, payreqTAmountLL;
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
                accountadress.setText((objData.getRecipientWalletAddress().substring(0, 10) + "..."));
                fee.setText(Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()) + " CYN");
                total.setText(Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()) + " CYN");
                lyAccAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.copyText(objData.getRecipientWalletAddress(), TransactionDetailsActivity.this);
                    }
                });
            } else {
                amount.setText(Utils.convertTwoDecimal(objData.getAmountReceived().replace("CYN", "").trim()));
                findViewById(R.id.payreqTAmountLL).setVisibility(View.GONE);
                findViewById(R.id.payreqPfLL).setVisibility(View.GONE);
                name.setText(objData.getSenderName());
                accountadress.setText((objData.getSenderWalletAddress().substring(0, 10) + "..."));
                lyAccAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.copyText(objData.getSenderWalletAddress(), TransactionDetailsActivity.this);
                    }
                });
            }
            if (!objData.getSenderMessage().equals("")) {
                descrptn.setText("\"" + objData.getSenderMessage() + "\"");
            } else {
                descrptn.setVisibility(View.GONE);
            }
            completed.setText(objData.getStatus());
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            balance.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
            refid.setText(objData.getReferenceId().substring(0, 10) + "...");


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

            lyPRClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            lyRefId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyTokenCreditDebit(TransactionData objData) {
        TextView headerTV, amount, status, datetime, fee, total, balance, purchaseamount, successadd, chargeback;
        TextView refid, name, accountadress, descriptorname, cardNumber, expiryDate, depositIDTV;
        LinearLayout lyPRClose, lyRefId, lyAccAdd;
        ImageView refIdIV, depositIDIV, cardBrandIV;
        LinearLayout depositID,referenceID;

        headerTV = findViewById(R.id.headTV);
        amount = findViewById(R.id.tvAmount);
        status = findViewById(R.id.statusTV);
        datetime = findViewById(R.id.datetimeTV);
        purchaseamount = findViewById(R.id.purchaseTV);
        fee = findViewById(R.id.processingFeeTV);
        total = findViewById(R.id.totalAmountTV);
        balance = findViewById(R.id.accBalTV);
        depositID=findViewById(R.id.depositIDLL);
        referenceID=findViewById(R.id.referenceIDLL);
        refid = findViewById(R.id.referenceIDTV);
        descriptorname = findViewById(R.id.descriNameTV);
        name = findViewById(R.id.cardHoldernameTV);
        cardNumber = findViewById(R.id.cardnumTV);
        expiryDate = findViewById(R.id.expdateTV);
        successadd = findViewById(R.id.successadd);
        chargeback = findViewById(R.id.chargeback);
        depositIDTV = findViewById(R.id.depositid);
        lyPRClose = findViewById(R.id.previous);
        refIdIV = findViewById(R.id.refIdIV);
        depositIDIV = findViewById(R.id.depositIdIV);
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
        purchaseamount.setText(objData.getYouPay());

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
        purchaseamount.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        fee.setText("$" + Utils.convertTwoDecimal((objData.getProcessingFee().replace("USD", "").trim())));
        total.setText("$" + Utils.convertTwoDecimal(String.valueOf(purchaseAmount + processingFee)));
        balance.setText(Utils.convertTwoDecimal((objData.getAccountBalance().replace("CYN", "").trim())) + " CYN");

        refid.setText(objData.getReferenceId().substring(0, 10) + "...");
        descriptorname.setText(objData.getDescriptorName());
        name.setText(objData.getCardHolderName());
        cardNumber.setText(objData.getCardNumber().substring(cardNumber.length() - 1));
        expiryDate.setText(objData.getCardExpiryDate());
        depositIDTV.setText(objData.getDepositid().substring(0, 10) + "...");
//        successadd.setText(("Successfully added "+"["+trasactionDetails.getData().getYouGet()+"]" +" to token account"));
//        chargeback.setText(("["+trasactionDetails.getData().getReferenceId().substring(0, 8) + "...")+"(hyper");
        lyPRClose.setOnClickListener(new View.OnClickListener() {
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
        depositID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getDepositid(), TransactionDetailsActivity.this);
            }
        });

    }

    private void buyTokenBankAccount(TransactionData objData) {
        TextView headerTV, amount, status, datetime, fee, total, balance, purchaseamount, successadd, chargeback;
        TextView refid, name, accountadress, descriptorname, depositIDTV, bankAccNumTV, bankNameTV, nameOnAccTV;
        LinearLayout lyPRClose, btBankReference, btBankDepositID;
        ImageView previousBtn, refIdIV, depositIDIV;

        headerTV = findViewById(R.id.btbankheaderTV);
        amount = findViewById(R.id.btbankamountTV);
        status = findViewById(R.id.btbankStatusTV);
        datetime = findViewById(R.id.btbankDatetimeTV);
        purchaseamount = findViewById(R.id.btBankpurchaseamntTV);
        fee = findViewById(R.id.btBankprocessingfeeTV);
        total = findViewById(R.id.btbankTotalTV);
        refid = findViewById(R.id.btbankRefidTV);
        btBankReference=findViewById(R.id.btbankReferenceLL);
        btBankDepositID=findViewById(R.id.btbankDepositIDLL);
        descriptorname = findViewById(R.id.btbankDescrptorTV);
        depositIDTV = findViewById(R.id.btbankDepositIDTV);
        lyPRClose = findViewById(R.id.btbankprevious);
        refIdIV = findViewById(R.id.btbankrefIV);
        depositIDIV = findViewById(R.id.btbankDeposiIV);
        bankAccNumTV = findViewById(R.id.btbankaccountTV);
        bankNameTV = findViewById(R.id.btbanknameTV);
        nameOnAccTV = findViewById(R.id.btbanknameACTV);
        headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());

        amount.setText(objData.getYouGet().replace("CYN", ""));

        status.setText(objData.getStatus());

        switch (objData.getStatus().toLowerCase()) {
            case "completed":
                status.setTextColor(getResources().getColor(R.color.completed_status));
                status.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
                status.setTextColor(getResources().getColor(R.color.inprogress_status));
                status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                findViewById(R.id.cancelTxnCV).setVisibility(View.VISIBLE);
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


//        Double purchaseAmount = Double.parseDouble(objData.getYouGet().replace("CYN", ""));
//        Double processingFee = Double.parseDouble(objData.getProcessingFee().replace("USD", ""));
        datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        purchaseamount.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        fee.setText("$" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("USD", "").trim()));
        total.setText("$" + Utils.convertTwoDecimal(objData.getYouPay().replace("USD", "").trim()));
        bankAccNumTV.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        refid.setText(objData.getReferenceId().substring(0, 10) + "...");
        descriptorname.setText(objData.getDescriptorName());
        depositIDTV.setText(objData.getDepositid().substring(0, 10) + "...");
            bankNameTV.setText(objData.getBankName());


            nameOnAccTV.setText(objData.getNameOnBankAccount());
//        successadd.setText(("Successfully added "+"["+trasactionDetails.getData().getYouGet()+"]" +" to token account"));
//        chargeback.setText(("["+trasactionDetails.getData().getReferenceId().substring(0, 8) + "...")+"(hyper");
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

    private void withdrawGiftcard(TransactionData objData) {
        TextView headerMsdTV, amountTV;
        TextView status, dateandtime, withGiftcardname, subtotal, fee, grandtotal, refid, withid, recipientname, email;
        LinearLayout previous,giftCardWithdrawID,giftcardReferenceID;
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
        giftCardWithdrawID=findViewById(R.id.giftCardwithdrawIDLL);
        giftcardReferenceID=findViewById(R.id.giftcardReferenceIDLL);
        previous = findViewById(R.id.withGiftprevious);
        withIdIV = findViewById(R.id.withdrawGiftIdIV);
        refIDIV = findViewById(R.id.withdrawRefIDIV);

        headerMsdTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        amountTV.setText(Utils.convertTwoDecimal(objData.getTotalPaidAmount().replace("CYN", "").trim()));

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

        refid.setText(objData.getReferenceId().substring(0, 10) + "...");
        recipientname.setText(objData.getRecipientName());
        email.setText(objData.getRecipientEmail());

        Double subtotatl = Double.parseDouble(objData.getTotalPaidAmount().replace("CYN", "").trim());
        Double procesFee = Double.parseDouble(objData.getGiftCardFee().replace("CYN", "").trim());

        subtotal.setText("" + Utils.convertTwoDecimal(objData.getTotalPaidAmount().replace("CYN", "").trim()));
        fee.setText("" + Utils.convertTwoDecimal(objData.getGiftCardFee().replace(" CYN", "").trim()));
        grandtotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotatl + procesFee)));

        dateandtime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        if (objData.getWithdrawId().length() > 10) {
            withid.setText(objData.getWithdrawId().substring(0, 10) + "...");
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

    private void withdrawInstant(TransactionData objData) {
        LinearLayout previous,withdrawID,referenceID;
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
        withdrawID=findViewById(R.id.withiwithdrawalLL);
        referenceID=findViewById(R.id.withiReferenceIDLL);
        withicardHolderName = findViewById(R.id.withincardholdername);
        withicardbrand = findViewById(R.id.withincardbrandIV);
        withicardnumber = findViewById(R.id.withincardnumTV);
        withiexpirydate = findViewById(R.id.withinexpdateTV);


        withiheader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        withiamount.setText(Utils.convertTwoDecimal(objData.getReceivedAmount().replace("USD", "").trim()));
        if (objData.getDescription().equals("")) {
            withidescription.setVisibility(View.GONE);
        } else {
            withidescription.setText("\"" + objData.getDescription() + "\"");
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
            withiwithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
        } else {
            withiwithdrawalId.setText(objData.getWithdrawalId());
        }

        withirefId.setText(objData.getReferenceId().substring(0, 10) + "...");

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
        withdrawID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
            }
        });


    }

    private void withdrawBank(TransactionData objData) {
        TextView withbankheader, withbankamount, withbankdescription, withbankstatus, withbankdatetime, withbankwithdrawalAmount, withbankprocefee, withbanktotal, withbankaccountbal, withbankwithdrawalId, withbankrefId;
        TextView withbanknameonaccount, withbankname, withbankaccount;
        ImageView withbankwithdrawalid, withbankrefIDIV;
        LinearLayout withBankCloseLL,withBankWithdrawalID,withBankReference;

        withbankheader = findViewById(R.id.withbankheaderTV);
        withbankamount = findViewById(R.id.withBankAmount);
        withbankdescription = findViewById(R.id.withBankDescrptnTV);
        withbankstatus = findViewById(R.id.withBankStatusTV);
        withbankdatetime = findViewById(R.id.withbankDatetimeTV);
        withbankwithdrawalAmount = findViewById(R.id.withBankWithdrawAmount);
        withbankprocefee = findViewById(R.id.withBankProcessFee);
        withbanktotal = findViewById(R.id.withBankTotalTV);
        withbankaccountbal = findViewById(R.id.withBankAccBalanceTV);
        withbankwithdrawalId = findViewById(R.id.withBankWithdrawidTV);
        withbankrefId = findViewById(R.id.withBankReferenceIDTV);
        withBankWithdrawalID=findViewById(R.id.withBankWithdrawalIDLL);
        withBankReference=findViewById(R.id.withBankReferenceIDLL);
        withbanknameonaccount = findViewById(R.id.withBankNameOnAccountTV);
        withbankname = findViewById(R.id.withBankBanknameTV);
        withbankaccount = findViewById(R.id.withBankbankaccountTV);

        withBankCloseLL = findViewById(R.id.withbankCloseLL);
        withbankwithdrawalid = findViewById(R.id.withBankwithdrawIDIV);
        withbankrefIDIV = findViewById(R.id.withBankReferenceIDIV);


        withbankheader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        withbankamount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        if (objData.getDescription().equals("")) {
            withbankdescription.setVisibility(View.GONE);
        } else {
            withbankdescription.setText("\"" + objData.getDescription() + "\"");
        }
        withbankstatus.setText(objData.getStatus());
        switch (objData.getStatus().toLowerCase()) {
            case "completed":
                withbankstatus.setTextColor(getResources().getColor(R.color.completed_status));
                withbankstatus.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
            case "inprogress":
                withbankstatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                withbankstatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
            case "pending":
                withbankstatus.setTextColor(getResources().getColor(R.color.pending_status));
                withbankstatus.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case "failed":
                withbankstatus.setTextColor(getResources().getColor(R.color.failed_status));
                withbankstatus.setBackgroundResource(R.drawable.txn_failed_bg);
                break;
        }

        withbankdatetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));

        Double subtotal = Double.parseDouble(objData.getWithdrawAmount().replace("CYN", "").trim());
        Double procesFee = Double.parseDouble(objData.getProcessingFee().replace("CYN", "").trim());

        withbankwithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        withbankprocefee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        withbanktotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotal + procesFee)));

        withbankaccountbal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");

        if (objData.getWithdrawalId().length() > 10) {
            withbankwithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
        } else {
            withbankwithdrawalId.setText(objData.getWithdrawalId());
        }

        withbankrefId.setText(objData.getReferenceId().substring(0, 10) + "...");

            withbanknameonaccount.setText(objData.getNameOnBankAccount());

            withbankname.setText(objData.getBankName());



        withbankaccount.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
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
                }
                break;
                case "buytoken": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.VISIBLE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                }
                break;
                case "buytokenbank": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                }
                break;
                case "withdrawgift": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                }
                break;
                case "withdrawinstant": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                }
                break;
                case "withdrawbankaccount": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.VISIBLE);
                }
                break;
//                case "paymentMethods": {
//                    findViewById(R.id.payrequest).setVisibility(View.GONE);
//                    findViewById(R.id.externalBank).setVisibility(View.GONE);
//                    findViewById(R.id.paymentMethods).setVisibility(View.VISIBLE);
//                    findViewById(R.id.firstError).setVisibility(View.GONE);
//                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
//                }
//                break;
//                case "externalBank": {
//                    findViewById(R.id.payrequest).setVisibility(View.GONE);
//                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
//                    findViewById(R.id.externalBank).setVisibility(View.VISIBLE);
//                    findViewById(R.id.firstError).setVisibility(View.GONE);
//                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
//                }
//                break;
//                case "firstError": {
//                    findViewById(R.id.payrequest).setVisibility(View.GONE);
//                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
//                    findViewById(R.id.externalBank).setVisibility(View.GONE);
//                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
//                    findViewById(R.id.firstError).setVisibility(View.VISIBLE);
//                }
//                break;
//                case "banksuccess": {
//                    findViewById(R.id.payrequest).setVisibility(View.GONE);
//                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
//                    findViewById(R.id.externalBank).setVisibility(View.GONE);
//                    findViewById(R.id.firstError).setVisibility(View.GONE);
//                    findViewById(R.id.banksuccess).setVisibility(View.VISIBLE);
//                }
//                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
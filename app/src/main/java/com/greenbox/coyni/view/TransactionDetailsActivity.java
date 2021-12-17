package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
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
                        txnType = Integer.parseInt(Utils.addType);
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
            LinearLayout lyPRClose, lyRefId, lyAccAdd;
            CardView cvStatus;
            headerTV = findViewById(R.id.headerTV);
            amount = findViewById(R.id.amountTV);
            descrptn = findViewById(R.id.descrptnTV);
            completed = findViewById(R.id.tvStatus);
            datetime = findViewById(R.id.dateTimeTV);
            fee = findViewById(R.id.feeTV);
            total = findViewById(R.id.totalTV);
            balance = findViewById(R.id.balanceTV);
            refid = findViewById(R.id.refidTV);
            name = findViewById(R.id.nameTV);
            accountadress = findViewById(R.id.accAddrsTV);
            cvStatus = findViewById(R.id.cvStatus);
            lyPRClose = findViewById(R.id.lyPRClose);
            lyRefId = findViewById(R.id.lyRefId);
            lyAccAdd = findViewById(R.id.lyAccAdd);
            headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype().replace("Tokens", ""));

            if (objData.getTransactionSubtype().equals("Sent Tokens")) {
                amount.setText(objData.getAmount().replace("CYN", "").trim());
            } else {
                amount.setText(objData.getAmountReceived().replace("CYN", "").trim());
            }
            descrptn.setText(objData.getSenderMessage());
            completed.setText(objData.getStatus());

            switch (objData.getStatus().toLowerCase()) {
                case "completed":
                    completed.setTextColor(getResources().getColor(R.color.completed_status));
                    cvStatus.setCardBackgroundColor(getResources().getColor(R.color.txn_completed_trans));
                    break;
                case "inprogress":
                    completed.setTextColor(getResources().getColor(R.color.inprogress_status));
                    cvStatus.setCardBackgroundColor(getResources().getColor(R.color.txn_inprogress_trans));
                    break;
                case "pending":
                    completed.setTextColor(getResources().getColor(R.color.pending_status));
                    cvStatus.setCardBackgroundColor(getResources().getColor(R.color.txn_pending_trans));
                    break;
                case "failed":
                    completed.setTextColor(getResources().getColor(R.color.failed_status));
                    cvStatus.setCardBackgroundColor(getResources().getColor(R.color.txn_failed_trans));
                    break;
            }


            datetime.setText(Utils.convertTxnDate(objData.getCreatedDate()));
            try {
                Double processingFee = Double.parseDouble(objData.getProcessingFee().replace("CYN", ""));
                Double sentamnt = Double.parseDouble(objData.getTotalAmount().replace("CYN", ""));
                amount.setText(Utils.convertTwoDecimalPoints(sentamnt));
                fee.setText(Utils.convertTwoDecimalPoints(processingFee) + " CYN");
//                total.setText(Utils.convertTwoDecimalPoints(sentamnt + processingFee) + " CYN");
                total.setText(Utils.convertTwoDecimalPoints(sentamnt) + " CYN");
            } catch (Exception e) {
                e.printStackTrace();
            }
            balance.setText(objData.getAccountBalance());
            refid.setText(objData.getReferenceId().substring(0, 10) + "...");

            if (objData.getTransactionSubtype().equals("Sent Tokens")) {
                name.setText(objData.getRecipientName());
                accountadress.setText((objData.getRecipientWalletAddress().substring(0, 10) + "..."));
            } else {
                name.setText(objData.getSenderName());
                accountadress.setText((objData.getSenderWalletAddress().substring(0, 10) + "..."));
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
            lyAccAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getRecipientWalletAddress(), TransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "payrequest": {
                    findViewById(R.id.payrequest).setVisibility(View.VISIBLE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                }
                break;
                case "paymentMethods": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.VISIBLE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                }
                break;
                case "externalBank": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                }
                break;
                case "firstError": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.VISIBLE);
                }
                break;
                case "banksuccess": {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
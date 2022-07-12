package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.buytoken.CancelBuyTokenResponse;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class MerchantTransactionDetailsActivity extends BaseActivity {


    private DashboardViewModel dashboardViewModel;
    private MyApplication objMyApplication;
    private String strGbxTxnIdType = "";
    private int txnType;
    private Integer txnSubType;
    private String gbxID, txnTypeStr, txnSubTypeStr;
    private TransactionData transactionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_merchant_transaction_details);
            if (getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID) != null) {
                gbxID = getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID);
            } else {
                gbxID = "";
            }
            if (getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_TYPE) != null) {
                txnTypeStr = getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_TYPE);
            } else {
                txnTypeStr = "";
            }
            if (getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_SUB_TYPE) != null) {
                txnSubTypeStr = getIntent().getStringExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_SUB_TYPE);
            } else {
                txnSubTypeStr = "";
            }
//            transactionData = (TransactionData) getIntent().getSerializableExtra(Utils.SELECTED_MERCHANT_TRANSACTION);
            initialization();
            initObserver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initialization() {
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            if (txnTypeStr != null) {
                switch (txnTypeStr.toLowerCase()) {
                    case Utils.refundtxntype:
                        txnType = Utils.refund;
                        break;
                    case Utils.saleOrdertxntype:
                        txnType = Utils.saleOrder;
                        break;
                    case Utils.monthlyServiceFeetxntype:
                        txnType = Utils.monthlyservicefee;
                        break;
                    case Utils.merchantPayouttxntype:
                        txnType = Utils.merchantPayout;
                        break;
                }
            }
            if (txnSubTypeStr != null) {
                switch (txnSubTypeStr.toLowerCase()) {
                    case Utils.tokensub:
                        txnSubType = Utils.token;
                        break;
                    case Utils.transfersub:
                        txnSubType = Utils.transfer;
                        break;
                    case Utils.sentt:
                        txnSubType = Utils.sent;
                        break;
                    default:
                        txnSubType = null;

                }
            }

            if (Utils.checkInternet(MerchantTransactionDetailsActivity.this)) {
                showProgressDialog();
                dashboardViewModel.getTransactionDetails(gbxID, txnType, txnSubType);

            } else {
                Utils.displayAlert(getString(R.string.internet), MerchantTransactionDetailsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initObserver() {
        try {

            dashboardViewModel.getTransactionDetailsMutableLiveData().observe(this, new Observer<TransactionDetails>() {
                @Override
                public void onChanged(TransactionDetails transactionDetails) {
                    dismissDialog();
                    if (transactionDetails != null && transactionDetails.getData() != null && transactionDetails.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        transactionData = transactionDetails.getData();
                        switch (transactionData.getTransactionType().toLowerCase()) {
                            case Utils.refundtxntype:
                                controlMethod(Utils.refundCM);
                                refundMerchant(transactionDetails);
                                break;
                            case Utils.saleOrdertxntype:
                                controlMethod(Utils.saleorderCM);
                                saleOrderMerchant(transactionDetails);
                                break;
                            case Utils.monthlyServiceFeetxntype:
                                controlMethod(Utils.monthlyservicefeeCM);
                                monthlyServiceFeeMerchant(transactionDetails);
                                break;
                            case Utils.merchantPayouttxntype:
                            case Utils.businessPayouttxntype:
                                controlMethod(Utils.merchantpayoutCM);
                                merchantPayout(transactionDetails);
                                break;
                        }

                    } else {
                        if (transactionDetails.getError().getErrorDescription() != null && !transactionDetails.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(transactionDetails.getError().getErrorDescription(), MerchantTransactionDetailsActivity.this, "", transactionDetails.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(transactionDetails.getError().getFieldErrors().get(0), MerchantTransactionDetailsActivity.this, "", "");
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void refundMerchant(TransactionDetails objData) {
        try {
            ImageView refundcopyIV;
            TextView refundheadertv, refundamounttv, refundReasontv, refundStatustv, refundDatetimetv, refundreferencetv, refundfeetv, refundtotalAmounttv, refundreciptantNametv, refundReciptantEmailtv, refundOTIdatetv, refundOTIgrossamounttv, refundOTIFeetv, refundOTINetamounttv, refundOTIrefrencetv;
            LinearLayout refundCloseLL, refundcopyLL;

            refundheadertv = findViewById(R.id.RefundheaderTV);
            refundcopyLL = findViewById(R.id.RefundcopyLL);
            refundCloseLL = findViewById(R.id.RefundCloseLL);
            refundamounttv = findViewById(R.id.RefundamountTV);
            refundReasontv = findViewById(R.id.refundReasonTV);
            refundStatustv = findViewById(R.id.refundStatusTV);
            refundDatetimetv = findViewById(R.id.refundDatetimeTV);
            refundreferencetv = findViewById(R.id.refundreferenceTV);
            refundfeetv = findViewById(R.id.refundfeeTV);
            refundtotalAmounttv = findViewById(R.id.refundtotalAmountTV);
            refundreciptantNametv = findViewById(R.id.refundreciptantNameTV);
            refundReciptantEmailtv = findViewById(R.id.refundReciptantEmailTV);
            refundOTIdatetv = findViewById(R.id.refundOTIdateTV);
            refundOTIgrossamounttv = findViewById(R.id.refundOTIgrossamountTV);
            refundOTIFeetv = findViewById(R.id.refundOTIFeeTV);
            refundOTINetamounttv = findViewById(R.id.refundOTINetamountTV);
            refundOTIrefrencetv = findViewById(R.id.refundOTIrefrenceTV);

            if (objData.getData().getTransactionType() != null && objData.getData().getTransactionSubtype() != null) {
                refundheadertv.setText(objData.getData().getTransactionType() + " - " + objData.getData().getTransactionSubtype());
            }
            if (objData.getData().getStatus() != null) {
                refundStatustv.setText(objData.getData().getStatus());
                switch (objData.getData().getStatus().toLowerCase()) {
                    case Utils.transCompleted: {
                        refundStatustv.setTextColor(getResources().getColor(R.color.completed_status));
                        refundStatustv.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    }
                    case Utils.transinprogress:
                        refundStatustv.setTextColor(getResources().getColor(R.color.inprogress_status));
                        refundStatustv.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.refundd:
                    case Utils.partialrefund:
                    case Utils.transPending: {
                        refundStatustv.setTextColor(getResources().getColor(R.color.pending_status));
                        refundStatustv.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    }
                    case Utils.transCancelled:
                    case Utils.transFailed: {
                        refundStatustv.setTextColor(getResources().getColor(R.color.failed_status));
                        refundStatustv.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                    }
                }
            }
            if (objData.getData().getRefundAmount() != null) {
                refundamounttv.setText(Utils.convertTwoDecimal(objData.getData().getRefundAmount().replace("CYN", "").trim()));
            }

            if (objData.getData().getReferenceId().length() > 10) {
                refundreferencetv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            } else {
                refundreferencetv.setText(objData.getData().getReferenceId());
            }
            if (objData.getData().getSaleOrderReferenceId().length() > 10) {
                refundOTIrefrencetv.setText(objData.getData().getSaleOrderReferenceId().substring(0, 10) + "...");
                refundOTIrefrencetv.setPaintFlags(refundOTIrefrencetv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                refundOTIrefrencetv.setText(objData.getData().getSaleOrderReferenceId());
            }
            String saleOrderId = objData.getData().getSaleOrderReferenceId();
            refundOTIrefrencetv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MerchantTransactionDetailsActivity.this, MerchantTransactionDetailsActivity.class)
                            .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_TYPE, Utils.saleOrdertxntype)
                            .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_SUB_TYPE, Utils.tokensub)
                            .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID, saleOrderId));
                }
            });
            if (objData.getData().getRecipientName() != null) {
                refundreciptantNametv.setText(Utils.capitalize(objData.getData().getRecipientName()));
            }
            if (objData.getData().getRecipientEmail() != null) {
                refundReciptantEmailtv.setText(objData.getData().getRecipientEmail());
            }
            if (objData.getData().getCreatedDate() != null) {
                refundDatetimetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            }
            if (objData.getData().getFees() != null) {
                refundfeetv.setText(Utils.convertTwoDecimal(objData.getData().getFees().replace("CYN", "").trim()));
            }
            if (objData.getData().getTotalAmount() != null) {
                refundtotalAmounttv.setText(Utils.convertTwoDecimal(objData.getData().getTotalAmount().replace("CYN", "").trim()));
            }
//            if (objData.getData().getSaleOrderDateAndTime() != null) {
//                refundOTIdatetv.setText(objMyApplication.exportDate(objData.getData().getSaleOrderDateAndTime()));
//
//            }
            if (objData.getData().getSaleOrderDateAndTime() != null) {
                String datee = objData.getData().getSaleOrderDateAndTime();

                if (datee != null && !datee.equals("")) {
                    if (datee.contains(".")) {
                        datee = datee.substring(0, datee.lastIndexOf("."));
                    }
                    refundOTIdatetv.setText(objMyApplication.convertZoneDateTime(datee, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss"));
                }
            }
            if (objData.getData().getSaleOrderGrossAmount() != null) {
                refundOTIgrossamounttv.setText(Utils.convertTwoDecimal(objData.getData().getSaleOrderGrossAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getFees() != null) {
                refundOTIFeetv.setText(Utils.convertTwoDecimal(objData.getData().getSaleOrderFees().replace("CYN", "").trim()));
            }
            if (objData.getData().getSaleOrderNetAmount() != null) {
                refundOTINetamounttv.setText(Utils.convertTwoDecimal(objData.getData().getSaleOrderNetAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getRemarks() != null) {
                refundReasontv.setText(objData.getData().getRemarks());
            }
            if (objData.getData().getRemarks().equals("")) {
                refundReasontv.setVisibility(View.GONE);
            } else {
                refundReasontv.setText("\"" + objData.getData().getRemarks() + "\"");
            }
            refundCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            refundcopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getReferenceId(), MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(MerchantTransactionDetailsActivity.this, MerchantTransactionListActivity.class)
//                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//    }

    @SuppressLint("SetTextI18n")
    private void saleOrderMerchant(TransactionDetails objData) {
        try {
            ImageView refundIV;
            TextView salesheaderTV, salesAmount_TV, salesStatusTV, salesDatetimeTV, salesReferenceidTV, salesfeesTV,RefundTV, salesreserveTV, salesnetamountTV, salesMerchantBalanceTV, salessendernameTVTV, salessenderemailTV;
            LinearLayout saleorderclosell, SalesReferencecopyLL, salesreserveLL;

            SalesReferencecopyLL = findViewById(R.id.SalesReferenceCopyLL);
            saleorderclosell = findViewById(R.id.Saleorderclosell);
            refundIV = findViewById(R.id.RefundIV);
            RefundTV = findViewById(R.id.RefundTV);
            salesheaderTV = findViewById(R.id.SalesheaderTV);
            salesAmount_TV = findViewById(R.id.SalesAmount_TV);
            salesStatusTV = findViewById(R.id.SalesStatusTV);
            salesDatetimeTV = findViewById(R.id.SalesDatetimeTV);
            salesReferenceidTV = findViewById(R.id.SalesReferenceidTV);
            salesfeesTV = findViewById(R.id.SalesfeesidTV);
            salesreserveTV = findViewById(R.id.SalesreserveTV);
            salesreserveLL = findViewById(R.id.salesreservell);
            salesnetamountTV = findViewById(R.id.SalesnetamountTV);
            salesMerchantBalanceTV = findViewById(R.id.SalesMerchantBalanceTV);
            salessendernameTVTV = findViewById(R.id.SalessendernameTVTV);
            salessenderemailTV = findViewById(R.id.SalessenderemailTV);
            if (objData.getData().getTransactionType() != null && objData.getData().getTransactionSubtype() != null) {
                salesheaderTV.setText(objData.getData().getTransactionType() + " - " + objData.getData().getTransactionSubtype());
            }
            if (objData.getData().getStatus() != null) {
                salesStatusTV.setText(objData.getData().getStatus());
                switch (objData.getData().getStatus().toLowerCase()) {
                    case Utils.transinprogress:
                        salesStatusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        salesStatusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transCompleted: {
                        salesStatusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        salesStatusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    }
                    case Utils.refundd: {
                        salesStatusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        salesStatusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                           refundIV.setEnabled(false);
                           refundIV.setImageResource(R.drawable.refund_disable_icon);
                        RefundTV.setTextColor(getColor(R.color.light_gray));
                        break;
                    }
                    case Utils.partialrefund:
                    case Utils.transPending: {
                        salesStatusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        salesStatusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    }
                    case Utils.transCancelled:
                    case Utils.transFailed: {
                        salesStatusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        salesStatusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                    }
                }
            }
            if (objData.getData().getReferenceId() != null) {
                if (objData.getData().getReferenceId().length() > 10) {
                    salesReferenceidTV.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                } else {
                    salesReferenceidTV.setText(objData.getData().getReferenceId());
                }
            }
            if (objData.getData().getCreatedDate() != null) {
                salesDatetimeTV.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            }
            if (objData.getData().getGrossAmount() != null) {
                salesAmount_TV.setText(Utils.convertTwoDecimal(objData.getData().getGrossAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getFees() != null) {
                salesfeesTV.setText(Utils.convertTwoDecimal(objData.getData().getFees().replace("CYN", "").trim()));
            }
            if (objData.getData().getNetAmount() != null) {
                salesnetamountTV.setText(Utils.convertTwoDecimal(objData.getData().getNetAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getAccountBalance() != null) {
                salesMerchantBalanceTV.setText(Utils.convertTwoDecimal(objData.getData().getAccountBalance().replace("CYN", "").trim()));
            }
            if (objMyApplication.isReserveEnabled() && objData.getData().getReserve() != null && !objData.getData().getReserve().equals("")) {
                salesreserveLL.setVisibility(View.VISIBLE);
                salesreserveTV.setText(Utils.convertTwoDecimal(objData.getData().getReserve().replace("CYN", "").trim()));
            } else {
                salesreserveLL.setVisibility(View.GONE);
            }
            if (objData.getData().getSenderName() != null) {
                salessendernameTVTV.setText(objData.getData().getSenderName());
            }
            if (objData.getData().getSenderEmail() != null) {
                salessenderemailTV.setText(objData.getData().getSenderEmail());
            }


            saleorderclosell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
                refundIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MerchantTransactionDetailsActivity.this, RefundTransactionActivity.class);
                        intent.putExtra(Utils.SELECTED_MERCHANT_TRANSACTION, transactionData);
                        intent.putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID, gbxID);
                        startActivity(intent);
                    }
                });

            SalesReferencecopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getData().getReferenceId(), MerchantTransactionDetailsActivity.this);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void monthlyServiceFeeMerchant(TransactionDetails objData) {
        try {
            ImageView msFeecopyIV;
            TextView MSFheadertv, MSFamounttv, MSFstatustv, MSFdatetv, MSfreferenceIdtv, MSFmerchantbalancetv;
            LinearLayout msfeeclosell, msFeecopyLL;

            msFeecopyLL = findViewById(R.id.MSFeecopyLL);
            msfeeclosell = findViewById(R.id.Msfeeclosell);
            MSFheadertv = findViewById(R.id.MSFheaderTV);
            MSFamounttv = findViewById(R.id.MSFamountTV);
            MSFstatustv = findViewById(R.id.MSFstatusTV);
            MSFdatetv = findViewById(R.id.MSFdateTV);
            MSfreferenceIdtv = findViewById(R.id.MSfreferenceIdTV);
            MSFmerchantbalancetv = findViewById(R.id.MSFmerchantbalanceTV);
            if (objData.getData().getTransactionType() != null) {
                MSFheadertv.setText(objData.getData().getTransactionType());
            }
            if (objData.getData().getStatus() != null) {
                MSFstatustv.setText(objData.getData().getStatus());
                switch (objData.getData().getStatus().toLowerCase()) {
                    case Utils.transCompleted:
                        MSFstatustv.setTextColor(getResources().getColor(R.color.completed_status));
                        MSFstatustv.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case Utils.transinprogress:
                        MSFstatustv.setTextColor(getResources().getColor(R.color.inprogress_status));
                        MSFstatustv.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transPending:
                        MSFstatustv.setTextColor(getResources().getColor(R.color.pending_status));
                        MSFstatustv.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case Utils.transCancelled:
                    case Utils.transFailed: {
                        MSFstatustv.setTextColor(getResources().getColor(R.color.failed_status));
                        MSFstatustv.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                    }
                }
            }
            if (objData.getData().getGrossAmount() != null) {
                MSFamounttv.setText(Utils.convertTwoDecimal(objData.getData().getGrossAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getReferenceId() != null) {
                if (objData.getData().getReferenceId().length() > 10) {
                    MSfreferenceIdtv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                } else {
                    MSfreferenceIdtv.setText(objData.getData().getReferenceId());
                }
            }
            if (objData.getData().getCreatedDate() != null) {
                MSFdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            }
            if (objData.getData().getAccountBalance() != null) {
                MSFmerchantbalancetv.setText(Utils.convertTwoDecimal(objData.getData().getAccountBalance().replace("CYN", "").trim()));
            }

            msfeeclosell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            msFeecopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getReferenceId(), MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void merchantPayout(TransactionDetails objData) {
        try {
            TextView mPayoutheadertv, merchantamounttv, merchantstatustv, merchantdatetv, mPayoutIdtv, mreferenceIdtv, merchantPIdatetv, mPItotalamounttv, mPItotaltransactionstv, mPIdeposittotv, salessenderemailTV;
            LinearLayout mpayoutll, referencecopyLL, payoutcopyll;


            mpayoutll = findViewById(R.id.Mpayoutll);
            mPayoutheadertv = findViewById(R.id.mPayoutheaderTV);
            merchantamounttv = findViewById(R.id.merchantamountTV);
            merchantstatustv = findViewById(R.id.merchantstatusTV);
            merchantdatetv = findViewById(R.id.merchantdateTv);
            mPayoutIdtv = findViewById(R.id.mPayoutIdTV);
            mreferenceIdtv = findViewById(R.id.mreferenceIdTV);
            merchantPIdatetv = findViewById(R.id.merchantPIdateTV);
            mPItotalamounttv = findViewById(R.id.mPItotalamountTV);
            mPItotaltransactionstv = findViewById(R.id.mPItotaltransactionsTV);
            mPIdeposittotv = findViewById(R.id.mPIdeposittoTV);
            referencecopyLL = findViewById(R.id.copuRefIDLL);
            payoutcopyll = findViewById(R.id.copyPayoutIDLL);

            if (objData.getData().getTransactionType() != null) {
                mPayoutheadertv.setText(objData.getData().getTransactionType());
            }
            if (objData.getData().getStatus() != null) {
                merchantstatustv.setText(objData.getData().getStatus());
                switch (objData.getData().getStatus().toLowerCase()) {
                    case Utils.transCompleted:
                        merchantstatustv.setTextColor(getResources().getColor(R.color.completed_status));
                        merchantstatustv.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case Utils.transinprogress:
                        merchantstatustv.setTextColor(getResources().getColor(R.color.inprogress_status));
                        merchantstatustv.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transPending:
                        merchantstatustv.setTextColor(getResources().getColor(R.color.pending_status));
                        merchantstatustv.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case Utils.transCancelled:
                    case Utils.transFailed: {
                        merchantstatustv.setTextColor(getResources().getColor(R.color.failed_status));
                        merchantstatustv.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                    }
                }
            }
            if (objData.getData().getReferenceId() != null) {
                if (objData.getData().getReferenceId().length() > 10) {
                    mreferenceIdtv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
//                    mreferenceIdtv.setPaintFlags(mreferenceIdtv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    mreferenceIdtv.setText(objData.getData().getReferenceId());
//                    mreferenceIdtv.setPaintFlags(mreferenceIdtv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }

            if (objData.getData().getDepositTo() != null) {
                if (objData.getData().getDepositTo().length() > 10) {
                    mPIdeposittotv.setText(objData.getData().getDepositTo().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                    mPIdeposittotv.setPaintFlags(mPIdeposittotv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    mPIdeposittotv.setText(objData.getData().getDepositTo());
                    mPIdeposittotv.setPaintFlags(mPIdeposittotv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }

            if (objData.getData().getPayoutId() != null) {
                if (objData.getData().getPayoutId().length() > 10) {
                    mPayoutIdtv.setText(objData.getData().getPayoutId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                    mPayoutIdtv.setPaintFlags(mPayoutIdtv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                } else {
                    mPayoutIdtv.setText(objData.getData().getPayoutId());
                }
            }
            if (objData.getData().getCreatedDate() != null) {
//                String datee = objData.getData().getCreatedDate();
//                merchantdatetv.setText(objMyApplication.convertZoneDateTime(datee, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss"));
                merchantdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            }

            if (objData.getData().getPayoutCreatedDate() != null && objData.getData().getPayoutUpdatedDate() != null) {
                String startDate = objMyApplication.convertZoneLatestTxndate(objData.getData().getPayoutCreatedDate()).toUpperCase();
                String endDate = objMyApplication.convertZoneLatestTxndate(objData.getData().getPayoutUpdatedDate()).toUpperCase();

                merchantPIdatetv.setText(startDate + " to " + endDate);
            }
//            if (objData.getData().getPayoutUpdatedDate() != null) {
//                merchantPIdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getPayoutUpdatedDate()));
//            }

            if (objData.getData().getAmountSent() != null) {
                merchantamounttv.setText(Utils.convertTwoDecimal(objData.getData().getAmountSent().replace("CYN", "").trim()));
            }
            if (objData.getData().getTotalAmount() != null) {
                mPItotalamounttv.setText(Utils.convertTwoDecimal(objData.getData().getTotalAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getTotalTransactions() != null) {
                mPItotaltransactionstv.setText(Utils.convertToWithoutDecimal(String.valueOf(objData.getData().getTotalTransactions())));
            }
            mpayoutll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            referencecopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getReferenceId(), MerchantTransactionDetailsActivity.this);
                }
            });
            payoutcopyll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getPayoutId(), MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void controlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case Utils.refundCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.VISIBLE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.GONE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.GONE);
                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.GONE);
                }
                break;
                case Utils.saleorderCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.GONE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.VISIBLE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.GONE);
                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.GONE);
                }
                break;
                case Utils.monthlyservicefeeCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.GONE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.GONE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.VISIBLE);
                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.GONE);
                }
                break;
                case Utils.merchantpayoutCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.GONE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.GONE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.GONE);
                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
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
    private TransactionListPosted selectedTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_merchant_transaction_details);
            selectedTransaction = (TransactionListPosted) getIntent().getSerializableExtra(Utils.SELECTED_MERCHANT_TRANSACTION);
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
            if (selectedTransaction != null && !selectedTransaction.equals("")) {
                //txnType = Integer.parseInt(getIntent().getStringExtra("txnType"));
                switch (selectedTransaction.getTxnTypeDn().toLowerCase()) {
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
            if (selectedTransaction != null && !selectedTransaction.equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (selectedTransaction.getTxnSubTypeDn().toLowerCase()) {

                    case Utils.tokensub:
                        txnSubType = Integer.valueOf(Utils.token);
                        break;
                    case Utils.transfersub:
                        txnSubType = Integer.valueOf(Utils.transfer);
                        break;
                    default:
                        txnSubType = null;

                }
            }

            if (Utils.checkInternet(MerchantTransactionDetailsActivity.this)) {
                showProgressDialog();
                dashboardViewModel.getTransactionDetails(selectedTransaction.getGbxTransactionId(), txnType, txnSubType);

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
                    if (transactionDetails != null && transactionDetails.getData() != null && transactionDetails.getStatus().equalsIgnoreCase(Utils.SUCCESS)){
                        switch (transactionDetails.getData().getTransactionType().toLowerCase()){

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
                                controlMethod(Utils.merchantpayoutCM);
                                merchantPayout(transactionDetails);
                                break;
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
            LinearLayout refundCloseLL,refundcopyLL;

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

            refundheadertv.setText(objData.getData().getTransactionType());
            refundStatustv.setText(objData.getData().getStatus());
            switch (objData.getData().getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    refundStatustv.setTextColor(getResources().getColor(R.color.completed_status));
                    refundStatustv.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    refundStatustv.setTextColor(getResources().getColor(R.color.inprogress_status));
                    refundStatustv.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    refundStatustv.setTextColor(getResources().getColor(R.color.pending_status));
                    refundStatustv.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    refundStatustv.setTextColor(getResources().getColor(R.color.failed_status));
                    refundStatustv.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
            if(objData.getData().getRefundAmount()!=null) {
                refundamounttv.setText(Utils.convertTwoDecimal(objData.getData().getRefundAmount().replace("USD", "").trim()));
            }

            if (objData.getData().getReferenceId().length()>10) {
                refundOTIrefrencetv.setText(objData.getData().getReferenceId().substring(0,10)+"...");
                refundOTIrefrencetv.setPaintFlags(refundOTIrefrencetv.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                refundreferencetv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            }else {
                refundreferencetv.setText(objData.getData().getReferenceId());
                refundOTIrefrencetv.setText(objData.getData().getReferenceId());
            }
            refundreciptantNametv.setText(Utils.capitalize(objData.getData().getRecipientName()));
            refundReciptantEmailtv.setText(objData.getData().getRecipientEmail());
            refundDatetimetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            refundfeetv.setText(Utils.convertTwoDecimal(objData.getData().getFees().replace("CYN", "").trim()));
            refundtotalAmounttv.setText(Utils.convertTwoDecimal(objData.getData().getTotalAmount().replace("CYN", "").trim()));
            refundOTIdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getDateAndTime()));
            refundOTIgrossamounttv.setText(Utils.convertTwoDecimal(objData.getData().getGrossAmount().replace("CYN", "").trim()));
            refundOTIFeetv.setText(Utils.convertTwoDecimal(objData.getData().getFees().replace("CYN", "").trim()));
            refundOTINetamounttv.setText(Utils.convertTwoDecimal(objData.getData().getNetAmount().replace("USD", "").trim()));
            refundReasontv.setText(objData.getData().getRemarks());
            if (objData.getData().getRemarks().equals("")) {
                refundReasontv.setVisibility(View.GONE);
            } else {
                refundReasontv.setText("\"" + objData.getData().getRemarks() + "\"");
            }
            refundCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            refundcopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getReferenceId(),MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void saleOrderMerchant(TransactionDetails objData) {
        try {
            ImageView refundIV;
            TextView salesheaderTV, salesAmount_TV, salesStatusTV, salesDatetimeTV, salesReferenceidTV, salesfeesTV, salesreserveTV, salesnetamountTV, salesMerchantBalanceTV, salessendernameTVTV, salessenderemailTV;
            LinearLayout saleorderclosell,SalesReferencecopyLL;

            SalesReferencecopyLL = findViewById(R.id.SalesReferenceCopyLL);
            saleorderclosell = findViewById(R.id.Saleorderclosell);
            refundIV = findViewById(R.id.RefundIV);
            salesheaderTV = findViewById(R.id.SalesheaderTV);
            salesAmount_TV = findViewById(R.id.SalesAmount_TV);
            salesStatusTV = findViewById(R.id.SalesStatusTV);
            salesDatetimeTV = findViewById(R.id.SalesDatetimeTV);
            salesReferenceidTV = findViewById(R.id.SalesReferenceidTV);
            salesfeesTV = findViewById(R.id.SalesfeesidTV);
            salesreserveTV = findViewById(R.id.SalesreserveTV);
            salesnetamountTV = findViewById(R.id.SalesnetamountTV);
            salesMerchantBalanceTV = findViewById(R.id.SalesMerchantBalanceTV);
            salessendernameTVTV = findViewById(R.id.SalessendernameTVTV);
            salessenderemailTV = findViewById(R.id.SalessenderemailTV);

            salesheaderTV.setText(objData.getData().getTransactionType() + " - " + objData.getData().getTransactionSubtype());
            salesStatusTV.setText(objData.getData().getStatus());
            switch (objData.getData().getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    salesStatusTV.setTextColor(getResources().getColor(R.color.completed_status));
                    salesStatusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    salesStatusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                    salesStatusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    salesStatusTV.setTextColor(getResources().getColor(R.color.pending_status));
                    salesStatusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    salesStatusTV.setTextColor(getResources().getColor(R.color.failed_status));
                    salesStatusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
            if (objData.getData().getReferenceId().length()>10) {
                salesReferenceidTV.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            }else {
                salesReferenceidTV.setText(objData.getData().getReferenceId());
            }
            salesDatetimeTV.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            if (objData.getData().getGrossAmount() != null) {
                salesAmount_TV.setText(Utils.convertTwoDecimal(objData.getData().getGrossAmount().replace("CYN", "").trim()));
            }
            salesfeesTV.setText(Utils.convertTwoDecimal(objData.getData().getFees().replace("CYN", "").trim()));
            salesnetamountTV.setText(Utils.convertTwoDecimal(objData.getData().getNetAmount().replace("USD", "").trim()));
            salesMerchantBalanceTV.setText(Utils.convertTwoDecimal(objData.getData().getAccountBalance().replace("CYN", "").trim()));
//            salesreserveTV.setText(Utils.convertTwoDecimal(objData.getData().getReserve().replace("CYN", "").trim()));
            salessendernameTVTV.setText(objData.getData().getSenderName());
            salessenderemailTV.setText(objData.getData().getSenderEmail());


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
            TextView MSFheadertv, MSFamounttv,MSFstatustv, MSFdatetv, MSfreferenceIdtv, MSFmerchantbalancetv;
            LinearLayout msfeeclosell,msFeecopyLL;

            msFeecopyLL = findViewById(R.id.MSFeecopyLL);
            msfeeclosell = findViewById(R.id.Msfeeclosell);
            MSFheadertv = findViewById(R.id.MSFheaderTV);
            MSFamounttv = findViewById(R.id.MSFamountTV);
            MSFstatustv = findViewById(R.id.MSFstatusTV);
            MSFdatetv = findViewById(R.id.MSFdateTV);
            MSfreferenceIdtv = findViewById(R.id.MSfreferenceIdTV);
            MSFmerchantbalancetv = findViewById(R.id.MSFmerchantbalanceTV);

            MSFheadertv.setText(objData.getData().getTransactionType());

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
                case Utils.transFailed:
                    MSFstatustv.setTextColor(getResources().getColor(R.color.failed_status));
                    MSFstatustv.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
            if(objData.getData().getGrossAmount()!=null) {
                MSFamounttv.setText(Utils.convertTwoDecimal(objData.getData().getGrossAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getReferenceId().length()>10) {
                MSfreferenceIdtv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            }else {
                MSfreferenceIdtv.setText(objData.getData().getReferenceId());
            }
            MSFdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            MSFmerchantbalancetv.setText(Utils.convertTwoDecimal(objData.getData().getAccountBalance().replace("CYN", "").trim()));



            msfeeclosell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            msFeecopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getReferenceId(),MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void merchantPayout(TransactionDetails objData) {
        try {
            TextView mPayoutheadertv, merchantamounttv, merchantstatustv, merchantdatetv, mPayoutIdtv, mreferenceIdtv, merchantPIdatetv, mPItotalamounttv, mPItotaltransactionstv, mPIdeposittotv, salessenderemailTV;
            LinearLayout mpayoutll,referencecopyLL,payoutcopyll;


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


            mPayoutheadertv.setText(objData.getData().getTransactionType() + " - " + objData.getData().getTransactionSubtype());
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
                case Utils.transFailed:
                    merchantstatustv.setTextColor(getResources().getColor(R.color.failed_status));
                    merchantstatustv.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
            if (objData.getData().getReferenceId().length()>10) {
                mreferenceIdtv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                mPIdeposittotv.setText(objData.getData().getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                mPIdeposittotv.setPaintFlags(mPIdeposittotv.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);


            }else {
                mreferenceIdtv.setText(objData.getData().getReferenceId());
                mPIdeposittotv.setText(objData.getData().getReferenceId());

            }
            if (objData.getData().getPayoutId().length()>10) {
                mPayoutIdtv.setText(objData.getData().getPayoutId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
                mPayoutIdtv.setPaintFlags(mPayoutIdtv.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

            }else {
                mPayoutIdtv.setText(objData.getData().getPayoutId());
            }
            merchantdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getCreatedDate()));
            merchantPIdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getData().getPayoutDate()));

            if (objData.getData().getGrossAmount() != null) {
                merchantamounttv.setText(Utils.convertTwoDecimal(objData.getData().getGrossAmount().replace("CYN", "").trim()));
            }
            if (objData.getData().getTotalAmount() != null) {
                mPItotalamounttv.setText(Utils.convertTwoDecimal(objData.getData().getTotalAmount().replace("CYN", "").trim()));
            }
            mPItotaltransactionstv.setText(objData.getData().getTotalTransactions());

            mpayoutll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            referencecopyLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getReferenceId(),MerchantTransactionDetailsActivity.this);
                }
            });
            payoutcopyll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getData().getPayoutId(),MerchantTransactionDetailsActivity.this);
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
package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
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
    private int txnType, txnSubType;
//    ProgressDialog progressDialog;

    //    CardView cancelTxnCV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_merchant_transaction_details);
            TransactionListPosted selectedTransaction = (TransactionListPosted) getIntent().getSerializableExtra(Utils.SELECTED_MERCHANT_TRANSACTION);
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
            if (getIntent().getStringExtra(Utils.gbxTxnIdType) != null && !getIntent().getStringExtra(Utils.gbxTxnIdType).equals("")) {
                strGbxTxnIdType = getIntent().getStringExtra(Utils.gbxTxnIdType);
            }
            if (getIntent().getStringExtra(Utils.txnType) != null && !getIntent().getStringExtra(Utils.txnType).equals("")) {
                //txnType = Integer.parseInt(getIntent().getStringExtra("txnType"));
                switch (getIntent().getStringExtra(Utils.txnType).toLowerCase()) {
                    case Utils.refundtxntype:
                        txnType = Utils.refund;
                        break;
                    case Utils.saleOrdertxntype:
                        txnType = Utils.saleOrder;
                        break;
                    case Utils.monthlyServiceFeetxntype:
                        txnType = Utils.monthlyservicefee;
                        break;
//                    case Utils.merchantPayouttxntype:
//                        txnType = Utils.merchantPayout;
//                        break;
                }
            }
            if (getIntent().getStringExtra(Utils.txnSubType) != null && !getIntent().getStringExtra(Utils.txnSubType).equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (getIntent().getStringExtra(Utils.txnSubType).toLowerCase()) {

                    case Utils.tokensub:
                        txnSubType = Utils.token;
                        break;
                    case Utils.transfersub:
                        txnSubType = Utils.transfer;
                        break;

                }
            }

            if (Utils.checkInternet(MerchantTransactionDetailsActivity.this)) {
//                progressDialog = Utils.showProgressDialog(MerchantTransactionDetailsActivity.this);
                dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
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
//                    if (progressDialog != null) {
//                        progressDialog.dismiss();
//                    }
                    if (transactionDetails != null && transactionDetails.getStatus().equalsIgnoreCase(Utils.Success)) {
                        switch (transactionDetails.getData().getTransactionType().toLowerCase()) {
                            case Utils.refundtxntype:
                                ControlMethod(Utils.refundCM);
                                refundMerchant(transactionDetails.getData());
                                break;
                            case Utils.saleOrdertxntype:
                                ControlMethod(Utils.saleorderCM);
                                saleorderMerchant(transactionDetails.getData());
                                break;
                            case Utils.monthlyServiceFeetxntype:
                                ControlMethod(Utils.monthlyservicefeeCM);
                                monthlyservicefeeMerchant(transactionDetails.getData());
                                break;
//                            case Utils.merchantPayouttxntype:
//                                ControlMethod(Utils.merchantmayoutCM);
//                                merchantpayout(transactionDetails.getData());
//                                break;
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refundMerchant(TransactionData objData) {
        try {
            ImageView refundcopyIV;
            TextView refundheadertv, refundamounttv, refundReasontv, refundStatustv, refundDatetimetv, refundreferencetv, refundfeetv, refundtotalAmounttv, refundreciptantNametv, refundReciptantEmailtv, refundOTIdatetv, refundOTIgrossamounttv, refundOTIFeetv, refundOTINetamounttv, refundOTIrefrencetv;
            LinearLayout refundCloseLL;

            refundheadertv = findViewById(R.id.RefundheaderTV);
            refundcopyIV = findViewById(R.id.RefundcopyIV);
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

            refundheadertv.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
            refundStatustv.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
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
            refundamounttv.setText(Utils.convertTwoDecimal(objData.getAmount().replace("CYN", "").trim()));
            refundreferencetv.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            refundreciptantNametv.setText(Utils.capitalize(objData.getRecipientName()));
            refundReciptantEmailtv.setText(objData.getRecipientEmail());
            refundOTIrefrencetv.setText(objData.getReferenceId());
            refundDatetimetv.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            refundfeetv.setText(Utils.convertTwoDecimal(objData.getFees().replace("CYN", "").trim()));
            refundtotalAmounttv.setText(Utils.convertTwoDecimal(objData.getRefundAmount().replace("CYN", "").trim()));
            refundOTIdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getDateAndTime()));
            refundOTIgrossamounttv.setText(Utils.convertTwoDecimal(objData.getGrossAmount().replace("CYN", "").trim()));
            refundOTIFeetv.setText(Utils.convertTwoDecimal(objData.getFees().replace("CYN", "").trim()));
            refundOTINetamounttv.setText(Utils.convertTwoDecimal(objData.getNetAmount().replace("CYN", "").trim()));


            refundCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            refundcopyIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(),MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void saleorderMerchant(TransactionData objData) {
        try {
            ImageView refundIV, SalesReferencecopyIV;
            TextView salesheaderTV, salesAmount_TV, salesStatusTV, salesDatetimeTV, salesReferenceidTV, salesfeesTV, salesreserveTV, salesnetamountTV, salesMerchantBalanceTV, salessendernameTVTV, salessenderemailTV;
            LinearLayout saleorderclosell;

            SalesReferencecopyIV = findViewById(R.id.SalesReferenceCopyIV);
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

            salesheaderTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
            salesStatusTV.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
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

            salesReferenceidTV.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            salesDatetimeTV.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            salesAmount_TV.setText(Utils.convertTwoDecimal(objData.getAmount().replace("CYN", "").trim()));
            salesfeesTV.setText(Utils.convertTwoDecimal(objData.getFees().replace("CYN", "").trim()));
            salesnetamountTV.setText(Utils.convertTwoDecimal(objData.getNetAmount().replace("CYN", "").trim()));
            salesMerchantBalanceTV.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()));
            salesreserveTV.setText(Utils.convertTwoDecimal(objData.getReserve().replace("CYN", "").trim()));
            salessendernameTVTV.setText(objData.getSenderName());
            salessenderemailTV.setText(objData.getSenderEmail());


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
            SalesReferencecopyIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), MerchantTransactionDetailsActivity.this);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void monthlyservicefeeMerchant(TransactionData objData) {
        try {
            ImageView msFeecopyIV;
            TextView MSFheadertv, MSFamounttv,MSFstatustv, MSFdatetv, MSfreferenceIdtv, MSFmerchantbalancetv;
            LinearLayout msfeeclosell;

            msFeecopyIV = findViewById(R.id.MSFeecopyIV);
            msfeeclosell = findViewById(R.id.Msfeeclosell);
            MSFheadertv = findViewById(R.id.MSFheaderTV);
            MSFamounttv = findViewById(R.id.MSFamountTV);
            MSFstatustv = findViewById(R.id.MSFstatusTV);
            MSFdatetv = findViewById(R.id.MSFdateTV);
            MSfreferenceIdtv = findViewById(R.id.MSfreferenceIdTV);
            MSFmerchantbalancetv = findViewById(R.id.MSFmerchantbalanceTV);

            MSFheadertv.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());

            MSFstatustv.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
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
            MSFamounttv.setText(Utils.convertTwoDecimal(objData.getAmount().replace("CYN", "").trim()));

            MSfreferenceIdtv.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            MSFdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            MSFmerchantbalancetv.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()));



            msfeeclosell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            msFeecopyIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(objData.getReferenceId(),MerchantTransactionDetailsActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void merchantpayout(TransactionData objData) {
//        try {
//            ImageView referencecopyIV, payoutcopyIV;
//            TextView mPayoutheadertv, merchantamounttv, merchantstatustv, merchantdatetv, mPayoutIdtv, mreferenceIdtv, merchantPIdatetv, mPItotalamounttv, mPItotaltransactionstv, mPIdeposittotv, salessenderemailTV;
//            LinearLayout mpayoutll;
//
//
//            mpayoutll = findViewById(R.id.Mpayoutll);
//            mPayoutheadertv = findViewById(R.id.mPayoutheaderTV);
//            merchantamounttv = findViewById(R.id.merchantamountTV);
//            merchantstatustv = findViewById(R.id.merchantstatusTV);
//            merchantdatetv = findViewById(R.id.merchantdateTv);
//            mPayoutIdtv = findViewById(R.id.mPayoutIdTV);
//            mreferenceIdtv = findViewById(R.id.mreferenceIdTV);
//            merchantPIdatetv = findViewById(R.id.merchantPIdateTV);
//            mPItotalamounttv = findViewById(R.id.mPItotalamountTV);
//            mPItotaltransactionstv = findViewById(R.id.mPItotaltransactionsTV);
//            mPIdeposittotv = findViewById(R.id.mPIdeposittoTV);
//            referencecopyIV = findViewById(R.id.referenceCopyIV);
//            payoutcopyIV = findViewById(R.id.payoutCopyIV);
//
//
//
//            merchantstatustv.setText(objData.getStatus());
//            switch (objData.getStatus().toLowerCase()) {
//                case Utils.transCompleted:
//                    merchantstatustv.setTextColor(getResources().getColor(R.color.completed_status));
//                    merchantstatustv.setBackgroundResource(R.drawable.txn_completed_bg);
//                    break;
//                case Utils.transinprogress:
//                    merchantstatustv.setTextColor(getResources().getColor(R.color.inprogress_status));
//                    merchantstatustv.setBackgroundResource(R.drawable.txn_inprogress_bg);
//                    break;
//                case Utils.transPending:
//                    merchantstatustv.setTextColor(getResources().getColor(R.color.pending_status));
//                    merchantstatustv.setBackgroundResource(R.drawable.txn_pending_bg);
//                    break;
//                case Utils.transFailed:
//                    merchantstatustv.setTextColor(getResources().getColor(R.color.failed_status));
//                    merchantstatustv.setBackgroundResource(R.drawable.txn_failed_bg);
//                    break;
//            }
//
////            mreferenceIdtv.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
////            merchantdatetv.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
////
//
//            mpayoutll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//            referencecopyIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Utils.copyText(objData.getReferenceId(),MerchantTransactionDetailsActivity.this);
//                }
//            });
//            payoutcopyIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Utils.copyText(objData.getReferenceId(),MerchantTransactionDetailsActivity.this);
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case Utils.refundCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.VISIBLE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.GONE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.GONE);
//                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.GONE);
                }
                break;
                case Utils.saleorderCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.GONE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.VISIBLE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.GONE);
//                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.GONE);
                }
                break;
                case Utils.monthlyservicefeeCM: {
                    findViewById(R.id.MTDrefund).setVisibility(View.GONE);
                    findViewById(R.id.MTDsalesorder).setVisibility(View.GONE);
                    findViewById(R.id.MTDservicefee).setVisibility(View.VISIBLE);
//                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.GONE);
                }
                break;
//                case Utils.merchantmayoutCM: {
//                    findViewById(R.id.MTDrefund).setVisibility(View.GONE);
//                    findViewById(R.id.MTDsalesorder).setVisibility(View.GONE);
//                    findViewById(R.id.MTDservicefee).setVisibility(View.GONE);
//                    findViewById(R.id.MTDmerchantPayout).setVisibility(View.VISIBLE);
//                }
//                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
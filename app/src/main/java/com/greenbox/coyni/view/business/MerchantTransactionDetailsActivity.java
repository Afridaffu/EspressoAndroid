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

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.buytoken.CancelBuyTokenResponse;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionDetailsActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class MerchantTransactionDetailsActivity extends AppCompatActivity {


    private DashboardViewModel dashboardViewModel;
    private MyApplication objMyApplication;
    private String strGbxTxnIdType = "";
    private int txnType, txnSubType;
    ProgressDialog progressDialog;

    //    CardView cancelTxnCV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_merchant_transaction_details);
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
                    case Utils.Refundtxntype:
                        txnType = Utils.refund;
                        break;
                    case Utils.SaleOrdertxntype:
                        txnType = Utils.saleOrder;
                        break;
                    case Utils.MonthlyServiceFeetxntype:
                        txnType = Utils.monthlyservicefee;
                        break;

                }
            }
            if (getIntent().getStringExtra(Utils.txnSubType) != null && !getIntent().getStringExtra(Utils.txnSubType).equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (getIntent().getStringExtra(Utils.txnSubType).toLowerCase()) {

                    case Utils.Tokensub:
                        txnSubType = Utils.token;
                        break;

                }
            }

            if (Utils.checkInternet(MerchantTransactionDetailsActivity.this)) {
                progressDialog = Utils.showProgressDialog(MerchantTransactionDetailsActivity.this);
                dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
            } else {
                Utils.displayAlert(getString(R.string.internet), MerchantTransactionDetailsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initObserver() {
        dashboardViewModel.getTransactionDetailsMutableLiveData().observe(this, new Observer<TransactionDetails>() {
            @Override
            public void onChanged(TransactionDetails transactionDetails) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (transactionDetails != null && transactionDetails.getStatus().equalsIgnoreCase(Utils.Success)) {
                    switch (transactionDetails.getData().getTransactionType().toLowerCase()) {
                        case Utils.Refundtxntype:
                            ControlMethod(Utils.refundCM);
                            refundMerchant(transactionDetails.getData());
                            break;
                        case Utils.SaleOrdertxntype:
                            ControlMethod(Utils.saleorderCM);
                            saleorderMerchant(transactionDetails.getData());
                            break;
                        case Utils.MonthlyServiceFeetxntype:
                            ControlMethod(Utils.monthlyservicefeeCM);
//                            payRequest(transactionDetails.getData());
                            break;
                    }
                }
            }
        });

        dashboardViewModel.getCancelBuyTokenResponseMutableLiveData().observe(this, new Observer<CancelBuyTokenResponse>() {
            @Override
            public void onChanged(CancelBuyTokenResponse cancelBuyTokenResponse) {
                try {
                    progressDialog.dismiss();
                    if (cancelBuyTokenResponse != null && cancelBuyTokenResponse.getStatus().equalsIgnoreCase(Utils.Success)) {
                        Utils.showCustomToast(MerchantTransactionDetailsActivity.this, "Transaction cancelled successfully.", R.drawable.ic_custom_tick, "");
                        //progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                        dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void refundMerchant(TransactionData objData) {
        try {
            ImageView refundcopy;
            TextView refundheadertv, refundamounttv, refundReasontv, refundStatustv, refundDatetimetv, refundreferencetv, refundfeetv, refundtotalAmounttv, refundreciptantNametv, refundReciptantEmailtv, refundOTIdatetv, refundOTIgrossamounttv, refundOTIFeetv, refundOTINetamounttv, refundOTIrefrencetv;
            LinearLayout lyCloseLL;

            refundheadertv = findViewById(R.id.RefundheaderTV);
            lyCloseLL = findViewById(R.id.lycloseLL);
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

            lyCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saleorderMerchant(TransactionData objData) {
        try {
            ImageView refundIV, backIV, salesReferenceidIV;
            TextView salesheaderTV, salesAmount_TV, salesStatusTV, salesDatetimeTV, salesReferenceidTV, salesfeesidTV, salesreserveTV, salesnetamountTV, salesMerchantBalanceTV, salessendernameTVTV, salessenderemailTV;


            salesReferenceidIV = findViewById(R.id.SalesReferenceidIV);
            backIV = findViewById(R.id.BackIV);
            refundIV = findViewById(R.id.RefundIV);
            salesheaderTV = findViewById(R.id.SalesheaderTV);
            salesAmount_TV = findViewById(R.id.SalesAmount_TV);
            salesStatusTV = findViewById(R.id.SalesStatusTV);
            salesDatetimeTV = findViewById(R.id.SalesDatetimeTV);
            salesReferenceidTV = findViewById(R.id.SalesReferenceidTV);
            salesfeesidTV = findViewById(R.id.SalesfeesidTV);
            salesreserveTV = findViewById(R.id.SalesreserveTV);
            salesnetamountTV = findViewById(R.id.SalesnetamountTV);
            salesMerchantBalanceTV = findViewById(R.id.SalesMerchantBalanceTV);
            salessendernameTVTV = findViewById(R.id.SalessendernameTVTV);
            salessenderemailTV = findViewById(R.id.SalessenderemailTV);

            backIV.setOnClickListener(new View.OnClickListener() {
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


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
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

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
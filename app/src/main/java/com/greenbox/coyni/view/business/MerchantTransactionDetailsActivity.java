package com.greenbox.coyni.view.business;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class MerchantTransactionDetailsActivity extends BaseActivity {


    private BusinessDashboardViewModel businessDashboardViewModel;
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
            TransactionListPosted selectedTransaction = (TransactionListPosted) getIntent().getSerializableExtra(Utils.SELECTED_MERCHANT_TRANSACTION);
            initialization();
            initObserver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialization() {
        try {
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            if (getIntent().getStringExtra("gbxTxnIdType") != null && !getIntent().getStringExtra("gbxTxnIdType").equals("")) {
                strGbxTxnIdType = getIntent().getStringExtra("gbxTxnIdType");
            }
            if (getIntent().getStringExtra("txnType") != null && !getIntent().getStringExtra("txnType").equals("")) {
                //txnType = Integer.parseInt(getIntent().getStringExtra("txnType"));
                switch (getIntent().getStringExtra("txnType").toLowerCase()) {
                    case "Refund":
                        txnType = Utils.refund;
                        break;
                    case "Sale Order":
                        txnType = Utils.saleOrder;
                        break;
                }
            }
            if (getIntent().getStringExtra("txnSubType") != null && !getIntent().getStringExtra("txnSubType").equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (getIntent().getStringExtra("txnSubType").toLowerCase()) {

                    case "bank account":
                        txnSubType = Integer.parseInt(Utils.bankType);
                        break;
                    case "debit card":
                        txnSubType = Integer.parseInt(Utils.debitType);
                        break;
                    case "signet":
                        txnSubType = Integer.parseInt(Utils.signetType);
                        break;

                }
            }

            if (Utils.checkInternet(MerchantTransactionDetailsActivity.this)) {
//                progressDialog = Utils.showProgressDialog(MerchantTransactionDetailsActivity.this);
//                businessDashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
            } else {
                Utils.displayAlert(getString(R.string.internet), MerchantTransactionDetailsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initObserver() {
    }
}
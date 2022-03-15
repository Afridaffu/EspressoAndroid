package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;
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
    }

    private void initObserver() {
    }
}
package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionDetailsActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class BusinessSalesOrderActivity extends AppCompatActivity {

    private ImageView refundIV, backIV, salesReferenceidIV;
    private TextView salesheaderTV, salesAmount_TV, salesStatusTV, salesDatetimeTV, salesReferenceidTV, salesfeesidTV, salesreserveTV, salesnetamountTV, salesMerchantBalanceTV, salessendernameTVTV, salessenderemailTV;
    private DashboardViewModel dashboardViewModel;
    private MyApplication objMyApplication;
    private String strGbxTxnIdType = "";
    int txnType, txnSubType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_sales_order);

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
                Intent intent = new Intent(BusinessSalesOrderActivity.this, RefundTransactionActivity.class);
                startActivity(intent);
            }
        });
    }

}
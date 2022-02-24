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
    DashboardViewModel dashboardViewModel;
    MyApplication objMyApplication;
    String strGbxTxnIdType = "";
    int txnType, txnSubType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_sales_order);

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
        backIV = findViewById(R.id.BackIV);
        refundIV = findViewById(R.id.RefundIV);
    }


//        private void withdrawGiftcard(TransactionData objData) {
//
//

//            salesReferenceidIV = findViewById(R.id.SalesReferenceidIV);
//
//            salesheaderTV = findViewById(R.id.SalesheaderTV);
//            salesAmount_TV = findViewById(R.id.SalesAmount_TV);
//            salesStatusTV = findViewById(R.id.SalesStatusTV);
//            salesDatetimeTV = findViewById(R.id.SalesDatetimeTV);
//            salesReferenceidTV = findViewById(R.id.SalesReferenceidTV);
//            salesfeesidTV = findViewById(R.id.SalesfeesidTV);
//            salesreserveTV = findViewById(R.id.SalesreserveTV);
//            salesnetamountTV = findViewById(R.id.SalesnetamountTV);
//            salesMerchantBalanceTV = findViewById(R.id.SalesMerchantBalanceTV);
//            salessendernameTVTV = findViewById(R.id.SalessendernameTVTV);
//            salessenderemailTV = findViewById(R.id.SalessenderemailTV);
//
//
//            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
//            objMyApplication = (MyApplication) getApplicationContext();
//            salesheaderTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
//            salesAmount_TV.setText(Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));
//
//            salesStatusTV.setText(objData.getStatus());
//            switch (objData.getStatus().toLowerCase()) {
//                case "completed":
//                    salesStatusTV.setTextColor(getResources().getColor(R.color.completed_status));
//                    salesStatusTV.setBackgroundResource(R.drawable.txn_completed_bg);
//                    break;
//                case "in progress":
//                    salesStatusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
//                    salesStatusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
//                    break;
//                case "pending":
//                    salesStatusTV.setTextColor(getResources().getColor(R.color.pending_status));
//                    salesStatusTV.setBackgroundResource(R.drawable.txn_pending_bg);
//                    break;
//                case "failed":
//                    salesStatusTV.setTextColor(getResources().getColor(R.color.failed_status));
//                    salesStatusTV.setBackgroundResource(R.drawable.txn_failed_bg);
//                    break;
//            }
//
//
//            salesDatetimeTV.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
//            salesReferenceidTV.setText(objData.getReferenceId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
//
//            salesfeesidTV.setText("" + Utils.convertTwoDecimal(objData.getGiftCardFee().replace(" CYN", "").trim()));
//            salessendernameTVTV.setText(objData.getSenderName());
//
//        }
    }


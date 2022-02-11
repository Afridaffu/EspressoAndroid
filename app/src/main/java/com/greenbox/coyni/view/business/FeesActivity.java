package com.greenbox.coyni.view.business;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.fee.Fees;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class FeesActivity extends AppCompatActivity {
    private LinearLayout bpbackBtn;
    private TextView salesOrderDollTV, salesOrderPerTV, refundDollTV, refundPerTV, tvEBADoll, tvEBAPer, instantPayDollTV, instantPayPerTV, signetAccDollTV, signetAccPerTV,
            giftCardDollTV, giftCardPerTV, fdwDollTV, fdwPerTV, buyTokenEBADollTV, buyTokenEBAPerTV, buytokenSignetDollTV, buytokenSignetPerTV, monthlyFeeDollTV, monthlyFeePerTV;
    BusinessDashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_fees);

        try {
            bpbackBtn = findViewById(R.id.bpbackBtn);
            salesOrderDollTV = findViewById(R.id.salesOrderDollTV);
            salesOrderPerTV = findViewById(R.id.salesOrderPerTV);
            refundDollTV = findViewById(R.id.refundDollTV);
            refundPerTV = findViewById(R.id.refundPerTV);
            tvEBADoll = findViewById(R.id.tvEBADoll);
            tvEBAPer = findViewById(R.id.tvEBAPer);
            instantPayDollTV = findViewById(R.id.instantPayDollTV);
            instantPayPerTV = findViewById(R.id.instantPayPerTV);
            signetAccDollTV = findViewById(R.id.signetAccDollTV);
            signetAccPerTV = findViewById(R.id.signetAccPerTV);
            giftCardDollTV = findViewById(R.id.giftCardDollTV);
            giftCardPerTV = findViewById(R.id.giftCardPerTV);
            fdwDollTV = findViewById(R.id.fdwDollTV);
            fdwPerTV = findViewById(R.id.fdwPerTV);
            buyTokenEBADollTV = findViewById(R.id.buyTokenEBADollTV);
            buyTokenEBAPerTV = findViewById(R.id.buyTokenEBAPerTV);
            buytokenSignetDollTV = findViewById(R.id.buytokenSignetDollTV);
            buytokenSignetPerTV = findViewById(R.id.buytokenSignetPerTV);
            monthlyFeeDollTV = findViewById(R.id.monthlyFeeDollTV);
            monthlyFeePerTV = findViewById(R.id.monthlyFeePerTV);

            viewModel=new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

            bpbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            try {
                viewModel.meFees(123);
                initObserver();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObserver() {
        viewModel.getFeesMutableLiveData().observe(this, new Observer<Fees>() {
            @Override
            public void onChanged(Fees fees) {
                if (fees.getStatus().equalsIgnoreCase("SUCCESS")){

                    try {
                        salesOrderDollTV.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("$" + fees.getData().getTransactionSaleOrderTokenFeeInDollar()))));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

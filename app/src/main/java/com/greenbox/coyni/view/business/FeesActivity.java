package com.greenbox.coyni.view.business;

import android.annotation.SuppressLint;
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
    TextView salesOrderDollTV, salesOrderPerTV, refundDollTV, refundPerTV, tvEBADoll, tvEBAPer, instantPayDollTV, instantPayPerTV, signetAccDollTV, signetAccPerTV,
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Fees fees) {
                if (fees.getStatus().equalsIgnoreCase("SUCCESS")){

                    try {
                        //withdrawal
                        tvEBADoll.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalBankFeeInDollar()))));
                        tvEBAPer.setText( fees.getData().getWithdrawalBankFeeInPercent().split("\\.")[0]+"%");
                        instantPayDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalInstantFeeInDollar()))));
                        instantPayPerTV.setText( fees.getData().getWithdrawalInstantFeeInPercent().split("\\.")[0]+"%");
                        signetAccDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalSignetFeeInDollar()))));
                        signetAccPerTV.setText( fees.getData().getWithdrawalSignetFeeInPercent().split("\\.")[0]+"%");
                        giftCardDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalGiftcardFeeInDollar()))));
                        giftCardPerTV.setText( fees.getData().getWithdrawalGiftcardFeeInPercent().split("\\.")[0]+"%");
                        fdwDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalFailedBankFeeInDollar()))));
                        fdwPerTV.setText( fees.getData().getWithdrawalFailedBankFeeInPercent().split("\\.")[0]+"%");

                        //buy token
                        buyTokenEBADollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenBankFeeInDollar()))));
                        buyTokenEBAPerTV.setText( fees.getData().getBuyTokenBankFeeInPercent().split("\\.")[0]+"%");
                        buytokenSignetDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenSignetFeeInDollar()))));
                        buytokenSignetPerTV.setText( fees.getData().getBuyTokenSignetFeeInPercent().split("\\.")[0]+"%");

                        //other fees
                        monthlyFeeDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getMonthlyServiceFeeInDollar()))));
                        monthlyFeePerTV.setText( fees.getData().getMonthlyServiceFeeInPercent().split("\\.")[0]+"%");

                        //transactions
                        salesOrderDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getTransactionSaleOrderTokenFeeInDollar()))));
                        salesOrderPerTV.setText( fees.getData().getTransactionSaleOrderTokenFeeInPercent().split("\\.")[0]+"%");
                        refundDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getTransactionRefundFeeInDollar()))));
                        refundPerTV.setText( fees.getData().getTransactionRefundFeeInPercent().split("\\.")[0]+"%");

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

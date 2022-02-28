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
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class FeesActivity extends BaseActivity {
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
            showProgressDialog();
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
                dismissDialog();
                if (fees.getStatus().equalsIgnoreCase("SUCCESS")){

                    try {
                            //withdrawal
                        if (fees.getData().getWithdrawalBankFeeInDollar() != null) {
                            tvEBADoll.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalBankFeeInDollar()))));
                        } else {
                            tvEBADoll.setText("");
                        }

                        if (fees.getData().getWithdrawalBankFeeInPercent() != null) {
                            tvEBAPer.setText( fees.getData().getWithdrawalBankFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            tvEBAPer.setText("");
                        }

                        if (fees.getData().getWithdrawalInstantFeeInDollar() != null) {
                            instantPayDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalInstantFeeInDollar()))));
                        } else {
                            instantPayDollTV.setText("");
                        }

                        if (fees.getData().getWithdrawalInstantFeeInPercent() != null) {
                            instantPayPerTV.setText( fees.getData().getWithdrawalInstantFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            instantPayPerTV.setText("");
                        }

                        if (fees.getData().getWithdrawalSignetFeeInDollar() != null) {
                            signetAccDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalSignetFeeInDollar()))));
                        } else {
                            signetAccDollTV.setText("");
                        }

                        if (fees.getData().getWithdrawalSignetFeeInPercent() != null) {
                            signetAccPerTV.setText( fees.getData().getWithdrawalSignetFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            signetAccPerTV.setText("");
                        }

                        if (fees.getData().getWithdrawalGiftcardFeeInDollar() != null) {
                            giftCardDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalGiftcardFeeInDollar()))));
                        } else {
                            giftCardDollTV.setText("");
                        }

                        if (fees.getData().getWithdrawalGiftcardFeeInPercent() != null) {
                            giftCardPerTV.setText( fees.getData().getWithdrawalGiftcardFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            giftCardPerTV.setText("");
                        }

                        if (fees.getData().getWithdrawalFailedBankFeeInDollar() != null) {
                            fdwDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalFailedBankFeeInDollar()))));
                        } else {
                            fdwDollTV.setText("");
                        }

                        if (fees.getData().getWithdrawalFailedBankFeeInPercent() != null) {
                            fdwPerTV.setText( fees.getData().getWithdrawalFailedBankFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            fdwPerTV.setText("");
                        }

                            //buy token

                        if (fees.getData().getBuyTokenBankFeeInDollar() != null) {
                            buyTokenEBADollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenBankFeeInDollar()))));
                        } else {
                            buyTokenEBADollTV.setText("");
                        }

                        if (fees.getData().getBuyTokenBankFeeInPercent() != null) {
                            buyTokenEBAPerTV.setText( fees.getData().getBuyTokenBankFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            buyTokenEBAPerTV.setText("");
                        }

                        if (fees.getData().getBuyTokenSignetFeeInDollar() != null) {
                            buytokenSignetDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenSignetFeeInDollar()))));
                        } else {
                            buytokenSignetDollTV.setText("");
                        }

                        if (fees.getData().getBuyTokenSignetFeeInPercent() != null) {
                            buytokenSignetPerTV.setText( fees.getData().getBuyTokenSignetFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            buytokenSignetPerTV.setText("");
                        }

                            //other fees
                        if (fees.getData().getMonthlyServiceFeeInDollar() != null) {
                            monthlyFeeDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getMonthlyServiceFeeInDollar()))));
                        } else {
                            monthlyFeeDollTV.setText("");
                        }

                        if (fees.getData().getMonthlyServiceFeeInPercent() != null) {
                            monthlyFeePerTV.setText( fees.getData().getMonthlyServiceFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            monthlyFeePerTV.setText("");
                        }

                            //transactions
                        if (fees.getData().getTransactionSaleOrderTokenFeeInDollar() != null){
                            salesOrderDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getTransactionSaleOrderTokenFeeInDollar()))));
                        } else{
                            salesOrderDollTV.setText("");
                        }

                        if (fees.getData().getTransactionSaleOrderTokenFeeInPercent() != null){
                            salesOrderPerTV.setText( fees.getData().getTransactionSaleOrderTokenFeeInPercent().split("\\.")[0]+"%");
                        }  else{
                            salesOrderPerTV.setText("");
                        }

                        if (fees.getData().getTransactionRefundFeeInDollar() != null) {
                            refundDollTV.setText("$ " + Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(fees.getData().getTransactionRefundFeeInDollar()))));
                        } else {
                            refundDollTV.setText("");
                        }

                        if (fees.getData().getTransactionSaleOrderTokenFeeInPercent() != null){
                            refundPerTV.setText( fees.getData().getTransactionRefundFeeInPercent().split("\\.")[0]+"%");
                        } else {
                            refundPerTV.setText("");
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

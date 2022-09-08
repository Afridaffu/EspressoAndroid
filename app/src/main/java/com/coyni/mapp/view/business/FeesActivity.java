package com.coyni.mapp.view.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.model.fee.Fees;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;

public class
FeesActivity extends BaseActivity {
    private LinearLayout bpbackBtn;
    private TextView salesOrderDollTV, salesOrderPerTV, refundDollTV, refundPerTV, tvEBADoll, tvEBAPer, instantPayDollTV, instantPayPerTV, signetAccDollTV, signetAccPerTV,
            giftCardDollTV, giftCardPerTV, fdwDollTV, fdwPerTV, buyTokenEBADollTV, buyTokenEBAPerTV, buytokenSignetDollTV, buytokenSignetPerTV, monthlyFeeDollTV, monthlyFeePerTV;
    private BusinessDashboardViewModel viewModel;
    private MyApplication objMyApplication;
    private int feeStructure_id;

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

            viewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();

            feeStructure_id = objMyApplication.getAccountType();
            if (feeStructure_id == Utils.SHARED_ACCOUNT) {
                feeStructure_id = Utils.BUSINESS_ACCOUNT;
            }
            bpbackBtn.setOnClickListener(v -> finish());
            showProgressDialog();
            try {
                viewModel.meFees(feeStructure_id);
                initObserver();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObserver() {
        viewModel.getFeesMutableLiveData().observe(this, new Observer<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Fees fees) {
                dismissDialog();
                try {
                    if (fees.getStatus().equalsIgnoreCase("SUCCESS")) {
                        objMyApplication.setFees(fees);

                        try {
                            //withdrawal
                            if (fees.getData().getWithdrawalBankFeeInDollar() != null) {
                                tvEBADoll.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalBankFeeInDollar()))));
                            } else {
                                tvEBADoll.setText("");
                            }

                            if (fees.getData().getWithdrawalBankFeeInPercent() != null) {
                                tvEBAPer.setText(Utils.convertBigDecimalUSDC((fees.getData().getWithdrawalBankFeeInPercent()))+"%");
                            } else {
                                tvEBAPer.setText("");
                            }

                            if (fees.getData().getWithdrawalInstantFeeInDollar() != null) {
                                instantPayDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalInstantFeeInDollar()))));
                            } else {
                                instantPayDollTV.setText("");
                            }

                            if (fees.getData().getWithdrawalInstantFeeInPercent() != null) {
                                instantPayPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalInstantFeeInPercent())+"%");
                            } else {
                                instantPayPerTV.setText("");
                            }

                            if (fees.getData().getWithdrawalSignetFeeInDollar() != null) {
                                signetAccDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalSignetFeeInDollar()))));
                            } else {
                                signetAccDollTV.setText("");
                            }

                            if (fees.getData().getWithdrawalSignetFeeInPercent() != null) {
                                signetAccPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalSignetFeeInPercent()) + "%");
                            } else {
                                signetAccPerTV.setText("");
                            }

                            if (fees.getData().getWithdrawalGiftcardFeeInDollar() != null) {
                                giftCardDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalGiftcardFeeInDollar()))));
                            } else {
                                giftCardDollTV.setText("");
                            }

                            if (fees.getData().getWithdrawalGiftcardFeeInPercent() != null) {
                                giftCardPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalGiftcardFeeInPercent())+ "%");
                            } else {
                                giftCardPerTV.setText("");
                            }

                            if (fees.getData().getWithdrawalFailedBankFeeInDollar() != null) {
                                fdwDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalFailedBankFeeInDollar()))));
                            } else {
                                fdwDollTV.setText("");
                            }

                            if (fees.getData().getWithdrawalFailedBankFeeInPercent() != null) {
                                fdwPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalFailedBankFeeInPercent()) + "%");
                            } else {
                                fdwPerTV.setText("");
                            }

                            //buy token

                            if (fees.getData().getBuyTokenBankFeeInDollar() != null) {
                                buyTokenEBADollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenBankFeeInDollar()))));
                            } else {
                                buyTokenEBADollTV.setText("");
                            }

                            if (fees.getData().getBuyTokenBankFeeInPercent() != null) {
                                buyTokenEBAPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenBankFeeInPercent()) + "%");
                            } else {
                                buyTokenEBAPerTV.setText("");
                            }

                            if (fees.getData().getBuyTokenSignetFeeInDollar() != null) {
                                buytokenSignetDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenSignetFeeInDollar()))));
                            } else {
                                buytokenSignetDollTV.setText("");
                            }

                            if (fees.getData().getBuyTokenSignetFeeInPercent() != null) {
                                buytokenSignetPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenSignetFeeInPercent()) + "%");
                            } else {
                                buytokenSignetPerTV.setText("");
                            }

                            //other fees
                            if (fees.getData().getMonthlyServiceFeeInDollar() != null) {
                                monthlyFeeDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getMonthlyServiceFeeInDollar()))));
                            } else {
                                monthlyFeeDollTV.setText("");
                            }

                            if (fees.getData().getMonthlyServiceFeeInPercent() != null) {
                                monthlyFeePerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getMonthlyServiceFeeInPercent()) + "%");
                            } else {
                                monthlyFeePerTV.setText("");
                            }

                            //transactions
                            if (fees.getData().getTransactionSaleOrderTokenFeeInDollar() != null) {
                                salesOrderDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getTransactionSaleOrderTokenFeeInDollar()))));
                            } else {
                                salesOrderDollTV.setText("");
                            }

                            if (fees.getData().getTransactionSaleOrderTokenFeeInPercent() != null) {
                                salesOrderPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getTransactionSaleOrderTokenFeeInPercent()) + "%");
                            } else {
                                salesOrderPerTV.setText("");
                            }

                            if (fees.getData().getTransactionRefundFeeInDollar() != null) {
                                refundDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getTransactionRefundFeeInDollar()))));
                            } else {
                                refundDollTV.setText("");
                            }

                            if (fees.getData().getTransactionSaleOrderTokenFeeInPercent() != null) {
                                refundPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getTransactionRefundFeeInPercent()) + "%");
                            } else {
                                refundPerTV.setText("");
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Utils.displayAlert(fees.getError().getErrorDescription(), FeesActivity.this, "", fees.getError().getFieldErrors().get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

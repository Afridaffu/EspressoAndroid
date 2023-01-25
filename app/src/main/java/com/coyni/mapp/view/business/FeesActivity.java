package com.coyni.mapp.view.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
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
    private LinearLayout bpbackBtn, wdSignetLL, wdCogentLL;
    private TextView salesOrderEcomDollTV, salesOrdeEcomrPerTV, refundDollTV, refundPerTV, tvEBADoll, tvEBAPer, instantPayDollTV, instantPayPerTV, CogentAccDollTV, CogentAccPerTV, SignetAccDollTV, SignetAccPerTV, salesOrderRetailDollTV, salesOrderRetailPerTV,
            giftCardDollTV, giftCardPerTV, fdwDollTV, fdwPerTV, buyTokenEBADollTV, buyTokenEBAPerTV, buytokenCogentDollTV, buytokenCogentPerTV, buytokenSignetDollTV, buytokenSignetPerTV, monthlyFeeDollTV, monthlyFeePerTV;
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
            wdSignetLL = findViewById(R.id.wdSignetLL);
            wdCogentLL = findViewById(R.id.wdCogentLL);
            salesOrderEcomDollTV = findViewById(R.id.salesOrderEcomDollTV);
            salesOrdeEcomrPerTV = findViewById(R.id.salesOrderEcomPerTV);
            salesOrderRetailDollTV = findViewById(R.id.salesOrderRetailDollTV);
            salesOrderRetailPerTV = findViewById(R.id.salesOrderRetailPerTV);
            refundDollTV = findViewById(R.id.refundDollTV);
            refundPerTV = findViewById(R.id.refundPerTV);
            tvEBADoll = findViewById(R.id.tvEBADoll);
            tvEBAPer = findViewById(R.id.tvEBAPer);
            instantPayDollTV = findViewById(R.id.instantPayDollTV);
            instantPayPerTV = findViewById(R.id.instantPayPerTV);
            CogentAccDollTV = findViewById(R.id.CogentAccDollTV);
            CogentAccPerTV = findViewById(R.id.CogentAccPerTV);
            SignetAccDollTV = findViewById(R.id.SignetAccDollTV);
            SignetAccPerTV = findViewById(R.id.SignetAccPerTV);
            giftCardDollTV = findViewById(R.id.giftCardDollTV);
            giftCardPerTV = findViewById(R.id.giftCardPerTV);
            fdwDollTV = findViewById(R.id.fdwDollTV);
            fdwPerTV = findViewById(R.id.fdwPerTV);
            buyTokenEBADollTV = findViewById(R.id.buyTokenEBADollTV);
            buyTokenEBAPerTV = findViewById(R.id.buyTokenEBAPerTV);
            buytokenCogentDollTV = findViewById(R.id.buytokenCogentDollTV);
            buytokenCogentPerTV = findViewById(R.id.buytokenCogentPerTV);
            buytokenSignetDollTV = findViewById(R.id.buytokenSignetDollTV);
            buytokenSignetPerTV = findViewById(R.id.buytokenSignetPerTV);
            monthlyFeeDollTV = findViewById(R.id.monthlyFeeDollTV);
            monthlyFeePerTV = findViewById(R.id.monthlyFeePerTV);

            viewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();

            wdSignetLL.setVisibility(objMyApplication.isSignetEnabled() ? View.VISIBLE : View.GONE);
            wdCogentLL.setVisibility(objMyApplication.isCogentEnabled() ? View.VISIBLE : View.GONE);

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
                                tvEBADoll.setText("$ 0.00");
                            }

                            if (fees.getData().getWithdrawalBankFeeInPercent() != null) {
                                tvEBAPer.setText(Utils.convertBigDecimalUSDC((fees.getData().getWithdrawalBankFeeInPercent())) + "%");
                            } else {
                                tvEBAPer.setText("0.00%");
                            }

                            if (fees.getData().getWithdrawalInstantFeeInDollar() != null) {
                                instantPayDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalInstantFeeInDollar()))));
                            } else {
                                instantPayDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getWithdrawalInstantFeeInPercent() != null) {
                                instantPayPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalInstantFeeInPercent()) + "%");
                            } else {
                                instantPayPerTV.setText("0.00%");
                            }

                            if (fees.getData().getWithdrawalCogentFeeInDollar() != null) {
                                CogentAccDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalCogentFeeInDollar()))));
                            } else {
                                CogentAccDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getWithdrawalCogentFeeInPercent() != null) {
                                CogentAccPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalCogentFeeInPercent()) + "%");
                            } else {
                                CogentAccPerTV.setText("0.00%");
                            }

                            if (fees.getData().getWithdrawalSignetFeeInDollar() != null) {
                                SignetAccDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalSignetFeeInDollar()))));
                            } else {
                                SignetAccDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getWithdrawalSignetFeeInPercent() != null) {
                                SignetAccPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalSignetFeeInPercent()) + "%");
                            } else {
                                SignetAccPerTV.setText("0.00%");
                            }

                            if (fees.getData().getWithdrawalGiftcardFeeInDollar() != null) {
                                giftCardDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalGiftcardFeeInDollar()))));
                            } else {
                                giftCardDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getWithdrawalGiftcardFeeInPercent() != null) {
                                giftCardPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalGiftcardFeeInPercent()) + "%");
                            } else {
                                giftCardPerTV.setText("0.00%");
                            }

                            if (fees.getData().getWithdrawalFailedBankFeeInDollar() != null) {
                                fdwDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalFailedBankFeeInDollar()))));
                            } else {
                                fdwDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getWithdrawalFailedBankFeeInPercent() != null) {
                                fdwPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getWithdrawalFailedBankFeeInPercent()) + "%");
                            } else {
                                fdwPerTV.setText("0.00%");
                            }

                            //buy token

                            if (fees.getData().getBuyTokenBankFeeInDollar() != null) {
                                buyTokenEBADollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenBankFeeInDollar()))));
                            } else {
                                buyTokenEBADollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getBuyTokenBankFeeInPercent() != null) {
                                buyTokenEBAPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenBankFeeInPercent()) + "%");
                            } else {
                                buyTokenEBAPerTV.setText("0.00%");
                            }

                            if (fees.getData().getBuyTokenCogentFeeInDollar() != null) {
                                buytokenCogentDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenCogentFeeInDollar()))));
                            } else {
                                buytokenCogentDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getBuyTokenCogentFeeInPercent() != null) {
                                buytokenCogentPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenCogentFeeInPercent()) + "%");
                            } else {
                                buytokenCogentPerTV.setText("0.00%");
                            }

                            if (fees.getData().getBuyTokenSignetFeeInDollar() != null) {
                                buytokenSignetDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenSignetFeeInDollar()))));
                            } else {
                                buytokenSignetDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getBuyTokenSignetFeeInPercent() != null) {
                                buytokenSignetPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getBuyTokenSignetFeeInPercent()) + "%");
                            } else {
                                buytokenSignetPerTV.setText("0.00%");
                            }

                            //other fees
                            if (fees.getData().getMonthlyServiceFeeInDollar() != null) {
                                monthlyFeeDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getMonthlyServiceFeeInDollar()))));
                            } else {
                                monthlyFeeDollTV.setText("$ 0.00");
                            }

                            if (fees.getData().getMonthlyServiceFeeInPercent() != null) {
                                monthlyFeePerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getMonthlyServiceFeeInPercent()) + "%");
                            } else {
                                monthlyFeePerTV.setText("0.00%");
                            }

                            //transactions
                            //  v2.0
//                            if (fees.getData().getTransactionSaleOrderTokenFeeInDollar() != null) {
//                                salesOrderDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getTransactionSaleOrderTokenFeeInDollar()))));
//                            } else {
//                                salesOrderDollTV.setText("$ 0.00");
//                            }

                            //  v2.3 changes
                            if (fees.getData().getSaleOrderEcommerceFeeInDollar() != null) {
                                salesOrderEcomDollTV.setText("$ " + Utils.convertTwoDecimalPoints(fees.getData().getSaleOrderEcommerceFeeInDollar()));
                            } else {
                                salesOrderEcomDollTV.setText("$ 0.00");
                            }
                            if (fees.getData().getSaleOrderEcommerceFeeInPercent() != null) {
                                salesOrdeEcomrPerTV.setText(Utils.convertTwoDecimalPoints(fees.getData().getSaleOrderEcommerceFeeInPercent()) + "%");
                            } else {
                                salesOrdeEcomrPerTV.setText("0.00%");
                            }
                            if (fees.getData().getSaleOrderRetailFeeInDollar() != null) {
                                salesOrderRetailDollTV.setText("$ " + Utils.convertTwoDecimalPoints(fees.getData().getSaleOrderRetailFeeInDollar()));
                            } else {
                                salesOrderRetailDollTV.setText("$ 0.00");
                            }
                            if (fees.getData().getSaleOrderRetailFeeInPercent() != null) {
                                salesOrderRetailPerTV.setText(Utils.convertTwoDecimalPoints(fees.getData().getSaleOrderRetailFeeInPercent()) + "%");
                            } else {
                                salesOrderRetailPerTV.setText("0.00%");
                            }

                            if (fees.getData().getTransactionRefundFeeInDollar() != null) {
                                refundDollTV.setText("$ " + Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSDC(fees.getData().getTransactionRefundFeeInDollar()))));
                            } else {
                                refundDollTV.setText("$ 0.00");
                            }

//                            if (fees.getData().getTransactionSaleOrderTokenFeeInPercent() != null) {
                            if (fees.getData().getTransactionRefundFeeInPercent() != null) {
                                refundPerTV.setText(Utils.convertBigDecimalUSDC(fees.getData().getTransactionRefundFeeInPercent()) + "%");
                            } else {
                                refundPerTV.setText("0.00%");
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

package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.model.users.AccountLimitsData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;

public class AccountLimitsActivity extends BaseActivity {
    TextView withdrawTokenBankLimit, withdrawTokenInstantLimit, withdrawTokenGiftcardLimit, buyTokenBankLimit, buyTokenDebitcardLimit, buyTokenCreditcardLimit, payRequestTranLimit,
            b_monthlyProcessingVolume, b_highTicketLimit, b_buyBankAccount, b_buySignetAccount, b_withdrawBankAccount, b_withDrawinstantPay, b_withDrawgiftCard, b_withdrawSignetAccount;
    ScrollView personalAccountLimitsSv, businessAccountLimitsSv;
    LinearLayout backBtn;
    AccountLimitsViewModel accountLimitsViewModel;
    MyApplication objMyApplication;
    int userType = 0;
    int b_userType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_limits);
        backBtn = findViewById(R.id.alBackbtn);
        accountLimitsViewModel = new ViewModelProvider(this).get(AccountLimitsViewModel.class);
        objMyApplication = (MyApplication) getApplicationContext();


        //Bersonal Account Limits....
        personalAccountLimitsSv = (ScrollView) findViewById(R.id.Personal_AccountLimitsSV);
        withdrawTokenBankLimit = (TextView) findViewById(R.id.tvWithTokenBankLimit);
        withdrawTokenInstantLimit = (TextView) findViewById(R.id.tvWithTokenInstantLimit);
        withdrawTokenGiftcardLimit = findViewById(R.id.tvWithdrawTokenGiftCardLimit);
        buyTokenBankLimit = findViewById(R.id.tvBuyTokenBankLimit);
        buyTokenCreditcardLimit = findViewById(R.id.tvBuyTokenCreditLimit);
        buyTokenDebitcardLimit = findViewById(R.id.tvBuyTokenDebitLimit);
        payRequestTranLimit = findViewById(R.id.tvPayRequestTransactionLimit);


        //Business Account Limits....
        businessAccountLimitsSv = (ScrollView) findViewById(R.id.Business_AccountLimitsSV);
        b_monthlyProcessingVolume = (TextView) findViewById(R.id.TvMonthlyProcessingVolume);
        b_highTicketLimit = (TextView) findViewById(R.id.TVHighTicketLimit);
        b_buyBankAccount = findViewById(R.id.TV_B_BUYBankAccount);
        b_buySignetAccount = findViewById(R.id.TV_B_BuySignetAccount);
        b_withdrawBankAccount = findViewById(R.id.TV_B_WithdrawBankAccount);
        b_withDrawinstantPay = findViewById(R.id.Tv_B_InstantPay);
        b_withDrawgiftCard = findViewById(R.id.TV_B_GiftCard);
        b_withdrawSignetAccount = findViewById(R.id.TV_B_WithdrawSignetAccount);

        initObserver();
        showProgressDialog();
        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            personalAccountLimitsSv.setVisibility(View.VISIBLE);
            businessAccountLimitsSv.setVisibility(View.GONE);
//            accountLimitsViewModel.meAccountLimits(userType);
            accountLimitsViewModel.meAccountLimits(Utils.userTypeCust);
        }else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
            businessAccountLimitsSv.setVisibility(View.VISIBLE);
            personalAccountLimitsSv.setVisibility(View.GONE);
//            accountLimitsViewModel.meAccountLimits(b_userType);
            accountLimitsViewModel.meAccountLimits(Utils.userTypeBusiness);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initObserver() {

        try {
            accountLimitsViewModel.getUserAccountLimitsMutableLiveData().observe(this, new Observer<AccountLimits>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onChanged(AccountLimits accountLimits) {

                    if (accountLimits.getStatus().equalsIgnoreCase("SUCCESS")) {

                        if (dialog!=null){
                            dialog.dismiss();
                        }
                        //Pay Request Limits
                        if (accountLimits.getData().getPayRequestTokenType() == 1) {
                            payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Day");
                        } else if (accountLimits.getData().getPayRequestTokenType() == 2) {
                            payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Week");
                        } else if (accountLimits.getData().getPayRequestTokenType() == 3) {
                            payRequestTranLimit.setText("No Limit");
                        } else if (accountLimits.getData().getPayRequestTokenType() == 4) {
                            payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getPayRequestTokenType() == 5) {
                            payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Month");
                        }


                        // Buy Token Bank Limit
                        if (accountLimits.getData().getBuyTokenBankAccountType() == 1) {
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Day");
                            b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Day");

                        } else if (accountLimits.getData().getBuyTokenBankAccountType() == 2) {
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Week");
                            b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Week");

                        } else if (accountLimits.getData().getBuyTokenBankAccountType() == 3) {
                            buyTokenBankLimit.setText("No Limit");
                            b_buyBankAccount.setText("No Limit");
                        } else if (accountLimits.getData().getBuyTokenBankAccountType() == 4) {
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Transaction");
                            b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Transaction");

                        } else if (accountLimits.getData().getBuyTokenBankAccountType() == 5) {
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Month");
                            b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Month");

                        }

                        // Buytoken Signet Account

                        if (accountLimits.getData().getBuyTokenSignetType() == 1) {
                            b_buySignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Day");

                        } else if (accountLimits.getData().getBuyTokenSignetType() == 2) {
                            b_buySignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Week");

                        } else if (accountLimits.getData().getBuyTokenSignetType() == 3) {
                            b_buySignetAccount.setText("No Limit");
                        } else if (accountLimits.getData().getBuyTokenSignetType() == 4) {
                            b_buySignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getBuyTokenSignetType() == 5) {
                            b_buySignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Month");
                        }

                        // Buy Token Debit Limit
                        if (accountLimits.getData().getBuyTokenCardType() == 1) {
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Day");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 2) {
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Week");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 3) {
                            buyTokenDebitcardLimit.setText("No Limit");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 4) {
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 5) {
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Month");
                        }

                        // Buy Token Credit Limit
                        if (accountLimits.getData().getBuyTokenCardType() == 1) {
                            buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Day");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 2) {
                            buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Week");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 3) {
                            buyTokenCreditcardLimit.setText("No Limit");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 4) {
                            buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getBuyTokenCardType() == 5) {
                            buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Month");
                        }

                        // Withdraw Token Bank Limit
                        if (accountLimits.getData().getWithdrawsBankAccountType() == 1) {
                            withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Day");
                            b_withdrawBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Day");

                        } else if (accountLimits.getData().getWithdrawsBankAccountType() == 2) {
                            withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Week");
                            b_withdrawBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Week");

                        } else if (accountLimits.getData().getWithdrawsBankAccountType() == 3) {
                            withdrawTokenBankLimit.setText("No Limit");
                            b_withdrawBankAccount.setText("No Limit");

                        } else if (accountLimits.getData().getWithdrawsBankAccountType() == 4) {
                            withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Transaction");
                            b_withdrawBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Transaction");

                        } else if (accountLimits.getData().getWithdrawsBankAccountType() == 5) {
                            withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Month");
                            b_withdrawBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Month");

                        }

                        // Withdraw Token Instant Limit
                        if (accountLimits.getData().getWithdrawsInstantPayType() == 1) {
                            withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Day");
                            b_withDrawinstantPay.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Day");

                        } else if (accountLimits.getData().getWithdrawsInstantPayType() == 2) {
                            withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Week");
                            b_withDrawinstantPay.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Week");

                        } else if (accountLimits.getData().getWithdrawsInstantPayType() == 3) {
                            withdrawTokenInstantLimit.setText("No Limit");
                            b_withDrawinstantPay.setText("No Limit");
                        } else if (accountLimits.getData().getWithdrawsInstantPayType() == 4) {
                            withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Transaction");
                            b_withDrawinstantPay.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getWithdrawsInstantPayType() == 5) {
                            withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Month");
                            b_withDrawinstantPay.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Month");
                        }

                        // Withdraw Token GiftCard Limit
                        if (accountLimits.getData().getWithdrawsGiftCardType() == 1) {
                            withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Day");
                            b_withDrawgiftCard.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Day");
                        } else if (accountLimits.getData().getWithdrawsGiftCardType() == 2) {
                            withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Week");
                            b_withDrawgiftCard.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Week");
                        } else if (accountLimits.getData().getWithdrawsGiftCardType() == 3) {
                            withdrawTokenGiftcardLimit.setText("No Limit");
                            b_withDrawgiftCard.setText("No Limit");
                        } else if (accountLimits.getData().getWithdrawsGiftCardType() == 4) {
                            withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Transaction");
                            b_withDrawgiftCard.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getWithdrawsGiftCardType() == 5) {
                            withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Month");
                            b_withDrawgiftCard.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Month");
                        }


                        // Withdraw Signet Account......
                        if (accountLimits.getData().getWithdrawsSignetType() == 1) {
                            b_withdrawSignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Day");

                        } else if (accountLimits.getData().getWithdrawsSignetType() == 2) {
                            b_withdrawSignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Week");

                        } else if (accountLimits.getData().getWithdrawsSignetType() == 3) {
                            b_withdrawSignetAccount.setText("No Limit");
                        } else if (accountLimits.getData().getWithdrawsSignetType() == 4) {
                            b_withdrawSignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getWithdrawsSignetType() == 5) {
                            b_withdrawSignetAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Month");
                        }

                        //

                        if (accountLimits.getData().getTransactionHighTicketType() == 1) {
                            b_highTicketLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Day");

                        } else if (accountLimits.getData().getTransactionHighTicketType() == 2) {
                            b_highTicketLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Week");

                        } else if (accountLimits.getData().getTransactionHighTicketType() == 3) {
                            b_highTicketLimit.setText("No Limit");
                        } else if (accountLimits.getData().getTransactionHighTicketType() == 4) {
                            b_highTicketLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Transaction");
                        } else if (accountLimits.getData().getTransactionHighTicketType() == 5) {
                            b_highTicketLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Month");
                        }

                            b_monthlyProcessingVolume.setText("0.00/Month");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
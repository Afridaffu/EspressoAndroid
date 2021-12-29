package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;

public class AccountLimitsActivity extends AppCompatActivity {
    TextView withdrawTokenBankLimit,withdrawTokenInstantLimit,withdrawTokenGiftcardLimit,buyTokenBankLimit,buyTokenDebitcardLimit,buyTokenCreditcardLimit,payRequestTranLimit;
    LinearLayout backBtn;
    AccountLimitsViewModel accountLimitsViewModel;
    ProgressDialog dialog;
    int userType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_limits);
        backBtn=findViewById(R.id.alBackbtn);
        accountLimitsViewModel = new ViewModelProvider(this).get(AccountLimitsViewModel.class);
        withdrawTokenBankLimit=(TextView)findViewById(R.id.tvWithTokenBankLimit);
        withdrawTokenInstantLimit=(TextView)findViewById(R.id.tvWithTokenInstantLimit);
        withdrawTokenGiftcardLimit=findViewById(R.id.tvWithdrawTokenGiftCardLimit);
        buyTokenBankLimit=findViewById(R.id.tvBuyTokenBankLimit);
        buyTokenCreditcardLimit=findViewById(R.id.tvBuyTokenCreditLimit);
        buyTokenDebitcardLimit=findViewById(R.id.tvBuyTokenDebitLimit);
        payRequestTranLimit=findViewById(R.id.tvPayRequestTransactionLimit);
        initObserver();
        accountLimitsViewModel.meAccountLimits(userType);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initObserver(){

        try {
            accountLimitsViewModel.getUserAccountLimitsMutableLiveData().observe(this, new Observer<AccountLimits>() {
                @Override
                public void onChanged(AccountLimits accountLimits) {

                    if (accountLimits.getStatus().equalsIgnoreCase("SUCCESS")){

                         //Pay Request Limits
                            if (accountLimits.getData().getPayRequestTokenType()==1){
                                payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Day");
                            }
                            else
                            if (accountLimits.getData().getPayRequestTokenType()==2){
                                payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Week");
                            }
                            else
                            if (accountLimits.getData().getPayRequestTokenType()==3){
                                payRequestTranLimit.setText("No Limit");
                            }
                            else
                            if (accountLimits.getData().getPayRequestTokenType()==4){
                                payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getPayRequestTokenTxnLimit()))) + "/Transaction");
                            }


                        // Buy Token Bank Limit
                        if (accountLimits.getData().getBuyTokenBankAccountType()==1){
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Day");
                        }
                        else
                        if (accountLimits.getData().getBuyTokenBankAccountType()==2){
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Week");
                        }
                        else
                        if (accountLimits.getData().getBuyTokenBankAccountType()==3){
                            buyTokenBankLimit.setText("No Limit");
                        }
                        else
                        if (accountLimits.getData().getBuyTokenBankAccountType()==4){
                            buyTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenBankAccountTxnLimit()))) + "/Transaction");
                        }



                        // Buy Token Debit Limit
                        if (accountLimits.getData().getBuyTokenCardType()==1){
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Day");
                        }
                        else
                        if (accountLimits.getData().getBuyTokenCardType()==2){
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Week");
                        }
                        else
                        if (accountLimits.getData().getBuyTokenCardType()==3){
                            buyTokenDebitcardLimit.setText("No Limit");
                        }
                        else
                        if (accountLimits.getData().getBuyTokenCardType()==4){
                            buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Transaction");
                        }

                    }


                    // Buy Token Credit Limit
                    if (accountLimits.getData().getBuyTokenCardType()==1){
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Day");
                    }
                    else
                    if (accountLimits.getData().getBuyTokenCardType()==2){
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Week");
                    }
                    else
                    if (accountLimits.getData().getBuyTokenCardType()==3){
                        buyTokenCreditcardLimit.setText("No Limit");
                    }
                    else
                    if (accountLimits.getData().getBuyTokenCardType()==4){
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getBuyTokenCardTxnLimit()))) + "/Transaction");
                    }

                    // Withdraw Token Bank Limit
                    if (accountLimits.getData().getWithdrawsBankAccountType()==1){
                        withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Day");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsBankAccountType()==2){
                        withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Week");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsBankAccountType()==3){
                        withdrawTokenBankLimit.setText("No Limit");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsBankAccountType()==4){
                        withdrawTokenBankLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsBankAccountTxnLimit()))) + "/Transaction");
                    }

                    // Withdraw Token Instant Limit
                    if (accountLimits.getData().getWithdrawsInstantPayType()==1){
                        withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Day");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsInstantPayType()==2){
                        withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Week");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsInstantPayType()==3){
                        withdrawTokenInstantLimit.setText("No Limit");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsInstantPayType()==4){
                        withdrawTokenInstantLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsInstantPayTxnLimit()))) + "/Transaction");
                    }

                    // Withdraw Token GiftCard Limit
                    if (accountLimits.getData().getWithdrawsGiftCardType()==1){
                        withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Day");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsGiftCardType()==2){
                        withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Week");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsGiftCardType()==3){
                        withdrawTokenGiftcardLimit.setText("No Limit");
                    }
                    else
                    if (accountLimits.getData().getWithdrawsGiftCardType()==4){
                        withdrawTokenGiftcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getWithdrawsGiftCardTxnLimit()))) + "/Transaction");
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
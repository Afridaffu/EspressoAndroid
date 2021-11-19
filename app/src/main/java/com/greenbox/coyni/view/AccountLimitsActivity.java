package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;

public class AccountLimitsActivity extends AppCompatActivity {
    TextView withdrawTokenBankLimit,withdrawTokenInstantLimit,withdrawTokenGiftcardLimit,buyTokenBankLimit,buyTokenDebitcardLimit,buyTokenCreditcardLimit;
    ImageView backBtn;
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

        accountLimitsViewModel.getUserAccountLimitsMutableLiveData().observe(this, new Observer<AccountLimits>() {
            @Override
            public void onChanged(AccountLimits accountLimits) {

                if (accountLimits.getStatus().equals("SUCCESS")){

                    if(accountLimits.getData().getTokenWithdrawalBankDayLimit() > 0
                            && accountLimits.getData().getTokenWithdrawalBankWeekLimit()<=0){
                       withdrawTokenBankLimit
                               .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalBankDayLimit()))) + "/Day");
                    }
                    else if (accountLimits.getData().getTokenWithdrawalBankDayLimit() <= 0
                            && accountLimits.getData().getTokenWithdrawalBankWeekLimit() > 0) {
                        withdrawTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalBankWeekLimit())))+ "/Week");
                    } else if (accountLimits.getData().getTokenWithdrawalBankDayLimit() > 0
                            && accountLimits.getData().getTokenWithdrawalBankWeekLimit() > 0) {
                        withdrawTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalBankDayLimit())))+ "/Day");
                    } else {
                        withdrawTokenBankLimit.setText("No Limit");
                    }


                    if(accountLimits.getData().getTokenWithdrawalInstantpayDayLimit() > 0
                            &&accountLimits.getData().getTokenWithdrawalInstantpayWeekLimit()<=0){
                        withdrawTokenInstantLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalInstantpayDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().getTokenWithdrawalInstantpayDayLimit() <= 0
                            && accountLimits.getData().getTokenWithdrawalInstantpayWeekLimit() > 0) {
                        withdrawTokenInstantLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalInstantpayWeekLimit()))) + "/Week");
                    } else if (accountLimits.getData().getTokenWithdrawalInstantpayDayLimit() > 0
                            && accountLimits.getData().getTokenWithdrawalInstantpayWeekLimit() > 0) {
                        withdrawTokenInstantLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalInstantpayDayLimit())))+ "/Day");
                    } else {
                        withdrawTokenInstantLimit.setText("No Limit");
                    }


                    if(accountLimits.getData().getTokenWithdrawalGiftcardDayLimit() > 0
                            &&accountLimits.getData().getTokenWithdrawalGiftcardWeekLimit()<=0){
                        withdrawTokenGiftcardLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalGiftcardDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().getTokenWithdrawalGiftcardDayLimit() <= 0
                            && accountLimits.getData().getTokenWithdrawalGiftcardWeekLimit() > 0) {
                        withdrawTokenGiftcardLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalGiftcardWeekLimit()))) + "/Week");
                    } else if (accountLimits.getData().getTokenWithdrawalGiftcardDayLimit() > 0
                            && accountLimits.getData().getTokenWithdrawalGiftcardWeekLimit() > 0) {
                        withdrawTokenGiftcardLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalGiftcardDayLimit()))) + "/Day");
                    } else {
                        withdrawTokenGiftcardLimit.setText("No Limit");
                    }


                    if(accountLimits.getData().getTokenBuyBankDayLimit() > 0
                            &&accountLimits.getData().getTokenBuyBankWeekLimit()<=0){
                        buyTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyBankDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().getTokenBuyBankDayLimit() <= 0
                            && accountLimits.getData().getTokenBuyBankWeekLimit() > 0) {
                        buyTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyBankWeekLimit()))) + "/Week");
                    } else if (accountLimits.getData().getTokenBuyBankDayLimit() > 0
                            && accountLimits.getData().getTokenBuyBankWeekLimit() > 0) {
                        buyTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyBankDayLimit()))) +"/Day");
                    } else {
                        buyTokenBankLimit.setText("No Limit");
                    }


                    //DebitCard Limit
                    if(accountLimits.getData().getTokenBuyDebitCardDayLimit() > 0
                            &&accountLimits.getData().getTokenBuyDebitCardWeekLimit()<=0){
                        buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyDebitCardDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().getTokenBuyDebitCardDayLimit() <= 0 && accountLimits.getData().getTokenBuyDebitCardWeekLimit() > 0) {
                        buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyDebitCardWeekLimit()))) + "/Week");
                    } else if (accountLimits.getData().getTokenBuyDebitCardDayLimit() > 0 && accountLimits.getData().getTokenBuyDebitCardWeekLimit() > 0) {
                        buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyDebitCardDayLimit()))) +"/Day");
                    } else {
                        buyTokenDebitcardLimit.setText("No Limit");
                    }

                    //CreditCard Limit

                    if(accountLimits.getData().getTokenBuyCardDayLimit() > 0&&accountLimits.getData().getTokenBuyCardWeekLimit()<=0){
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyCardDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().getTokenBuyCardDayLimit() <= 0 && accountLimits.getData().getTokenBuyCardWeekLimit() > 0) {
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyCardWeekLimit()))) + "/Week");
                    } else if (accountLimits.getData().getTokenBuyCardDayLimit() > 0 && accountLimits.getData().getTokenBuyCardWeekLimit() > 0) {
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyCardDayLimit()))) +"/Day");
                    } else {
                        buyTokenCreditcardLimit.setText("No Limit");
                    }


                }

            }
        });
    }
}
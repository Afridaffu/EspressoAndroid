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

        accountLimitsViewModel.getUserAccountLimitsMutableLiveData().observe(this, new Observer<AccountLimits>() {
            @Override
            public void onChanged(AccountLimits accountLimits) {

                if (accountLimits.getStatus().equals("SUCCESS")){

                    if(accountLimits.getData().isTokenWithdrawalBankDayFlag()){
                       withdrawTokenBankLimit
                               .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalBankDayLimit()))) + "/Day");
                    }
                    else if (accountLimits.getData().isTokenWithdrawalBankWeekFlag()) {
                        withdrawTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalBankWeekLimit())))+ "/Week");
                    } else {
                        withdrawTokenBankLimit.setText("No Limit");
                    }


                    if(accountLimits.getData().isTokenWithdrawalInstantpayDayFlag()){
                        withdrawTokenInstantLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalInstantpayDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().isTokenWithdrawalInstantpayWeekFlag()) {
                        withdrawTokenInstantLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalInstantpayWeekLimit()))) + "/Week");
                    }
                    else {
                        withdrawTokenInstantLimit.setText("No Limit");
                    }


                    if(accountLimits.getData().isTokenWithdrawalGiftcardDayFlag()){
                        withdrawTokenGiftcardLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalGiftcardDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().isTokenWithdrawalGiftcardWeekFlag()) {
                        withdrawTokenGiftcardLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenWithdrawalGiftcardWeekLimit()))) + "/Week");
                    } else {
                        withdrawTokenGiftcardLimit.setText("No Limit");
                    }


                    if(accountLimits.getData().isTokenBuyBankDayFlag()){
                        buyTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyBankDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().isTokenBuyBankWeekFlag()) {
                        buyTokenBankLimit
                                .setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyBankWeekLimit()))) + "/Week");
                    }  else {
                        buyTokenBankLimit.setText("No Limit");
                    }


                    //DebitCard Limit
                    if(accountLimits.getData().isTokenBuyDebitCardDayFlag()){
                        buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyDebitCardDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().isTokenBuyBankWeekFlag()) {
                        buyTokenDebitcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyDebitCardWeekLimit()))) + "/Week");
                    }  else {
                        buyTokenDebitcardLimit.setText("No Limit");
                    }

                    //CreditCard Limit

                    if(accountLimits.getData().isTokenBuyCardDayFlag()){
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyCardDayLimit())))+ "/Day");
                    }
                    else if (accountLimits.getData().isTokenBuyCardWeekFlag()) {
                        buyTokenCreditcardLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenBuyCardWeekLimit()))) + "/Week");
                    } else {
                        buyTokenCreditcardLimit.setText("No Limit");
                    }

                       //PayRequestTran Limit
                    if(accountLimits.getData().isTokenSendDayFlag()){
                        payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenSendDayLimit())))+ "/Transaction");
                    }
                    else if (accountLimits.getData().isTokenSendWeekFlag()) {
                        payRequestTranLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + accountLimits.getData().getTokenSendWeekLimit()))) + "/Transaction");
                    }  else {
                        buyTokenCreditcardLimit.setText("No Limit");
                    }



                }

            }
        });
    }
}
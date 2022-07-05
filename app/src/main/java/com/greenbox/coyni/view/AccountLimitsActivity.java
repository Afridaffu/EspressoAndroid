package com.greenbox.coyni.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    TextView mWithdrawBankLimit, mWithdrawInstantLimit, mWithdrawGiftCardLimit, mBuyBankLimit, mBuyDebitCardLimit, mBuyCreditCardLimit, payRequestTranLimit,
            b_monthlyProcessingVolume, b_highTicketLimit, b_buyBankAccount, b_buySignetAccount, b_withdrawBankAccount, b_withDrawinstantPay, b_withDrawgiftCard, b_withdrawSignetAccount;
    ScrollView personalAccountLimitsSv, businessAccountLimitsSv;
    LinearLayout backBtn, business_AccountLimitsLL;
    AccountLimitsViewModel accountLimitsViewModel;
    MyApplication objMyApplication;
    private final int DAILY = 1, WEEKLY = 2, NOLIMIT = 3, PERTRANSACTION = 4, MONTHLY = 5;
    private final String NOLIMIT_STR = "No Limit";
    private final String dayStr = "/Day", weekStr = "/Week", transactionStr = "/Transaction", monthStr = "/Month";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_account_limits);
        backBtn = findViewById(R.id.alBackbtn);
        accountLimitsViewModel = new ViewModelProvider(this).get(AccountLimitsViewModel.class);
        objMyApplication = (MyApplication) getApplicationContext();


        //Personal Account Limits....
        personalAccountLimitsSv = (ScrollView) findViewById(R.id.Personal_AccountLimitsSV);
        mWithdrawBankLimit = (TextView) findViewById(R.id.tvWithTokenBankLimit);
        mWithdrawInstantLimit = (TextView) findViewById(R.id.tvWithTokenInstantLimit);
        mWithdrawGiftCardLimit = findViewById(R.id.tvWithdrawTokenGiftCardLimit);
        mBuyBankLimit = findViewById(R.id.tvBuyTokenBankLimit);
        mBuyCreditCardLimit = findViewById(R.id.tvBuyTokenCreditLimit);
        mBuyDebitCardLimit = findViewById(R.id.tvBuyTokenDebitLimit);
        payRequestTranLimit = findViewById(R.id.tvPayRequestTransactionLimit);

        //Business Account Limits....
        business_AccountLimitsLL = findViewById(R.id.Business_AccountLimitsLL);
        b_monthlyProcessingVolume = (TextView) findViewById(R.id.TvMonthlyProcessingVolume);
        b_highTicketLimit = (TextView) findViewById(R.id.TVHighTicketLimit);
        b_buyBankAccount = findViewById(R.id.TV_B_BUYBankAccount);
        b_buySignetAccount = findViewById(R.id.TV_B_BuySignetAccount);
        b_withdrawBankAccount = findViewById(R.id.TV_B_WithdrawBankAccount);
        b_withDrawinstantPay = findViewById(R.id.Tv_B_InstantPay);
        b_withDrawgiftCard = findViewById(R.id.TV_B_GiftCard);
        b_withdrawSignetAccount = findViewById(R.id.TV_B_WithdrawSignetAccount);

        try {
            showProgressDialog();
            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                personalAccountLimitsSv.setVisibility(View.VISIBLE);
                business_AccountLimitsLL.setVisibility(View.GONE);
                accountLimitsViewModel.meAccountLimits(Utils.userTypeCust);
            } else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                business_AccountLimitsLL.setVisibility(View.VISIBLE);
                personalAccountLimitsSv.setVisibility(View.GONE);
                accountLimitsViewModel.meAccountLimits(Utils.userTypeBusiness);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initObserver();

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
                @Override
                public void onChanged(AccountLimits accountLimits) {
                    dismissDialog();
                    try {
                        if (accountLimits.getData() != null && accountLimits.getStatus().equalsIgnoreCase("SUCCESS")) {
                            setAccountLimitsData(accountLimits.getData());
                        } else {
                            try {
                                Utils.displayAlert(accountLimits.getError().getErrorDescription(), AccountLimitsActivity.this, "", accountLimits.getError().getFieldErrors().get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAccountLimitsData(AccountLimitsData data) {
        try {

            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                /* For Personal Account */
                setPayRequestData(data);
                setBuyTokensData(data);
                setWithdrawData(data);
            } else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT){
                /* For Merchant Account */
                setMerchantMerchantProcessingData(data);
                setMerchantBuyTokensDataTypeTwo(data);
                setMerchantWithdrawDataTypeTwo(data);
            }


        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setMerchantMerchantProcessingData(AccountLimitsData data) {
        b_monthlyProcessingVolume.setText(" ");
        switch (data.getTransactionHighTicketType()) {
            case DAILY:
                b_highTicketLimit.setText(getUsFormat(data.getTransactionHighTicketTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                b_highTicketLimit.setText(getUsFormat(data.getTransactionHighTicketTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                b_highTicketLimit.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                b_highTicketLimit.setText(getUsFormat(data.getTransactionHighTicketTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                b_highTicketLimit.setText(getUsFormat(data.getTransactionHighTicketTxnLimit()).concat(monthStr));
                break;
        }
    }

    private void setWithdrawData(AccountLimitsData data) {
        // Withdraw Token Bank Limit
        switch (data.getWithdrawsBankAccountType()) {
            case 1:
                mWithdrawBankLimit.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(dayStr));
                break;
            case 2:
                mWithdrawBankLimit.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(weekStr));
                break;
            case 3:
                mWithdrawBankLimit.setText(NOLIMIT_STR);
                break;
            case 4:
                mWithdrawBankLimit.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(transactionStr));
                break;
            case 5:
                mWithdrawBankLimit.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(monthStr));
                break;
        }

        // Withdraw Token Instant Limit
        switch (data.getWithdrawsInstantPayType()) {
            case DAILY:
                mWithdrawInstantLimit.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                mWithdrawInstantLimit.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                mWithdrawInstantLimit.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                mWithdrawInstantLimit.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                mWithdrawInstantLimit.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(monthStr));
                break;
        }

        // Withdraw Token GiftCard Limit
        switch (data.getWithdrawsGiftCardType()) {
            case DAILY:
                mWithdrawGiftCardLimit.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                mWithdrawGiftCardLimit.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                mWithdrawGiftCardLimit.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                mWithdrawGiftCardLimit.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                mWithdrawGiftCardLimit.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(monthStr));
                break;
        }
    }

    private void setBuyTokensData(AccountLimitsData data) {
        switch (data.getBuyTokenBankAccountType()) {
            case DAILY:
                mBuyBankLimit.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(dayStr));
//                b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + data.getBuyTokenBankAccountTxnLimit()))) + "/Day");
                break;
            case WEEKLY:
                mBuyBankLimit.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(weekStr));
//                b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + data.getBuyTokenBankAccountTxnLimit()))) + "/Week");
                break;
            case NOLIMIT:
                mBuyBankLimit.setText(NOLIMIT_STR);
//                b_buyBankAccount.setText("No Limit");
                break;
            case PERTRANSACTION:
                mBuyBankLimit.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(transactionStr));
//                b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + data.getBuyTokenBankAccountTxnLimit()))) + "/Transaction");
                break;
            case MONTHLY:
                mBuyBankLimit.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(monthStr));
//                b_buyBankAccount.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + data.getBuyTokenBankAccountTxnLimit()))) + "/Month");
                break;
        }
        switch (data.getBuyTokenCardType()) {
            case DAILY:
                mBuyDebitCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(dayStr));
                mBuyCreditCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                mBuyDebitCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(weekStr));
                mBuyCreditCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                mBuyDebitCardLimit.setText(NOLIMIT_STR);
                mBuyCreditCardLimit.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                mBuyDebitCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(transactionStr));
                mBuyCreditCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                mBuyDebitCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(monthStr));
                mBuyCreditCardLimit.setText(getUsFormat(data.getBuyTokenCardTxnLimit()).concat(monthStr));
                break;
        }
    }


    private void setMerchantWithdrawDataTypeTwo(AccountLimitsData data) {
        // Withdraw Token Bank Limit
        switch (data.getWithdrawsBankAccountType()) {
            case 1:
                b_withdrawBankAccount.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(dayStr));
                break;
            case 2:
                b_withdrawBankAccount.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(weekStr));
                break;
            case 3:
                b_withdrawBankAccount.setText(NOLIMIT_STR);
                break;
            case 4:
                b_withdrawBankAccount.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(transactionStr));
                break;
            case 5:
                b_withdrawBankAccount.setText(getUsFormat(data.getWithdrawsBankAccountTxnLimit()).concat(monthStr));
                break;
        }

        // Withdraw Token Instant Limit
        switch (data.getWithdrawsInstantPayType()) {
            case DAILY:
                b_withDrawinstantPay.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                b_withDrawinstantPay.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                b_withDrawinstantPay.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                b_withDrawinstantPay.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                b_withDrawinstantPay.setText(getUsFormat(data.getWithdrawsInstantPayTxnLimit()).concat(monthStr));
                break;
        }

        // Withdraw Token GiftCard Limit
        switch (data.getWithdrawsGiftCardType()) {
            case DAILY:
                b_withDrawgiftCard.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                b_withDrawgiftCard.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                b_withDrawgiftCard.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                b_withDrawgiftCard.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                b_withDrawgiftCard.setText(getUsFormat(data.getWithdrawsGiftCardTxnLimit()).concat(monthStr));
                break;
        }

        switch (data.getWithdrawsSignetType()) {
            case DAILY:
                b_withdrawSignetAccount.setText(getUsFormat(data.getWithdrawsSignetTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                b_withdrawSignetAccount.setText(getUsFormat(data.getWithdrawsSignetTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                b_withdrawSignetAccount.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                b_withdrawSignetAccount.setText(getUsFormat(data.getWithdrawsSignetTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                b_withdrawSignetAccount.setText(getUsFormat(data.getWithdrawsSignetTxnLimit()).concat(monthStr));
                break;
        }
    }

    private void setMerchantBuyTokensDataTypeTwo(AccountLimitsData data) {
        switch (data.getBuyTokenBankAccountType()) {
            case DAILY:
                b_buyBankAccount.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                b_buyBankAccount.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                b_buyBankAccount.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                b_buyBankAccount.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                b_buyBankAccount.setText(getUsFormat(data.getBuyTokenBankAccountTxnLimit()).concat(monthStr));
                break;
        }

        switch (data.getBuyTokenSignetType()) {
            case DAILY:
                b_buySignetAccount.setText(getUsFormat(data.getBuyTokenSignetTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                b_buySignetAccount.setText(getUsFormat(data.getBuyTokenSignetTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                b_buySignetAccount.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                b_buySignetAccount.setText(getUsFormat(data.getBuyTokenSignetTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                b_buySignetAccount.setText(getUsFormat(data.getBuyTokenSignetTxnLimit()).concat(monthStr));
                break;
        }
    }

    private void setPayRequestData(AccountLimitsData data) {
        switch (data.getPayRequestTokenType()) {
            case DAILY:
                payRequestTranLimit.setText(getUsFormat(data.getPayRequestTokenTxnLimit()).concat(dayStr));
                break;
            case WEEKLY:
                payRequestTranLimit.setText(getUsFormat(data.getPayRequestTokenTxnLimit()).concat(weekStr));
                break;
            case NOLIMIT:
                payRequestTranLimit.setText(NOLIMIT_STR);
                break;
            case PERTRANSACTION:
                payRequestTranLimit.setText(getUsFormat(data.getPayRequestTokenTxnLimit()).concat(transactionStr));
                break;
            case MONTHLY:
                payRequestTranLimit.setText(getUsFormat(data.getPayRequestTokenTxnLimit()).concat(monthStr));
                break;
        }
    }

    private String getUsFormat(String s) {
        return Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(s)));
    }
}
package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class CheckOutPaymentActivity extends AppCompatActivity {

    private BusinessDashboardViewModel businessDashboardViewModel;
    private MyApplication myApplication;
    private TextView mTokenBalance;
    private double availableBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_payment);
        initFields();
        walletAPICall();
        initObservers();
    }


    private void initFields() {
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        myApplication = (MyApplication) getApplicationContext();
        mTokenBalance = findViewById(R.id.available_token_balance_tv);
    }

    private void initObservers() {
        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse != null) {
                    if (businessWalletResponse.getStatus() != null && businessWalletResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (businessWalletResponse.getData() != null && businessWalletResponse.getData().getWalletNames() != null) {
                            availableBalance = businessWalletResponse.getData().getWalletNames().get(0).getAvailabilityToUse();
                            mTokenBalance.setText(Utils.convertTwoDecimal(String.valueOf(availableBalance)));
                        }
                    }
                }
            }
        });
    }

    private void walletAPICall() {

        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletType(Utils.TOKEN);
        walletRequest.setUserId(String.valueOf(myApplication.getLoginUserId()));

        if (Utils.checkInternet(CheckOutPaymentActivity.this)) {
            businessDashboardViewModel.meMerchantWallet(walletRequest);
        } else {
            Utils.displayAlert(getString(R.string.internet), CheckOutPaymentActivity.this, "", "");
        }
    }

    @Override
    public void onBackPressed() {
        if (myApplication.getCheckOutModel()!= null && myApplication.getCheckOutModel().isCheckOutFlag()){
            myApplication.setCheckOutModel(null);
        }
        super.onBackPressed();
    }
}
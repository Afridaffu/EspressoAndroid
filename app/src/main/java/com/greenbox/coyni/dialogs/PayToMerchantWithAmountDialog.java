package com.greenbox.coyni.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.ScanActivity;
import com.greenbox.coyni.view.business.PayToMerchantActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class PayToMerchantWithAmountDialog extends BaseDialog {

    private DashboardViewModel dashboardViewModel;
    private LinearLayout copyAddressLL;
    private String amount;
    UserDetails userDetails;
    private TextView payAmount,recipientAddressTV;
    private String recipientAddress="";


    public PayToMerchantWithAmountDialog(Context context) {
        super(context);
    }


    public PayToMerchantWithAmountDialog(Context context,String strAmount,UserDetails userDetails) {
        super(context);
        amount = strAmount;
        this.userDetails = userDetails;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_merchant_with_amount);
        payAmount = findViewById(R.id.amountPayTV);
        recipientAddressTV = findViewById(R.id.recipientAddTV);
        copyAddressLL = findViewById(R.id.copyRecipientLL);
//        dashboardViewModel = new ViewModelProvider(ScanActivity.scanActivity).get(DashboardViewModel.class);
//        dashboardViewModel.getUserDetail(walletId);
//
//        dashboardViewModel.getUserDetailsMutableLiveData().observe(ScanActivity.scanActivity, new Observer<UserDetails>() {
//            @Override
//            public void onChanged(UserDetails userDetails) {
//                if (userDetails != null && userDetails.getStatus().equalsIgnoreCase("SUCCESS")){
//                    dbaName.setText(userDetails.getData().getFullName());
//                }
//            }
//        });
        bindUserInfo(userDetails);

        if (amount != null){
            payAmount.setText(amount);
        }
        if (recipientAddress.length() > 13) {
            recipientAddressTV.setText(recipientAddress.substring(0, 13) + "...");
        } else {
            recipientAddressTV.setText(recipientAddress);
        }




    }
    @SuppressLint("SetTextI18n")
    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, userName, userWalletAddre;
            ImageView userProfile;
            tvName = findViewById(R.id.payingDbaNameTV);
            userName = findViewById(R.id.userProfileTextTV);
            userProfile = findViewById(R.id.userProfileIV);
            userWalletAddre = findViewById(R.id.accountAddressTV);

//            requestedToUserId = userDetails.getData().getUserId();
            if (userDetails.getData().getFullName().length() > 20) {
                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
            } else {
                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            }
//            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
//            strUserName = Utils.capitalize(userDetails.getData().getFullName());
            String imageTextNew = "";
            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            userName.setText(imageTextNew);
            if (userDetails.getData().getWalletId().length() > Integer.parseInt("10")) {
                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId().substring(0, Integer.parseInt("10")) + "...");
            } else {
                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
            }
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                userProfile.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                Glide.with(ScanActivity.scanActivity)
                        .load(userDetails.getData().getImage())
                        .placeholder(R.drawable.ic_profilelogo)
                        .into(userProfile);
            } else {
                userProfile.setVisibility(View.GONE);
                userName.setVisibility(View.VISIBLE);
            }
            recipientAddress = "";
            recipientAddress = userDetails.getData().getWalletId().toString();

            copyAddressLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(recipientAddress,ScanActivity.scanActivity);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

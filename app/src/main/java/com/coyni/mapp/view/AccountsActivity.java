package com.coyni.mapp.view;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.businesswallet.WalletResponseData;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

import java.util.List;

public class AccountsActivity extends BaseActivity {

    TextView userShortInfoTV, userNameTV, userBalanceTV;
    ImageView imgProfile, accountsCloseIV;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_accounts);

        objMyApplication = (MyApplication) getApplicationContext();
        userShortInfoTV = findViewById(R.id.tvUserInfo);
        imgProfile = findViewById(R.id.imgProfile);
        userNameTV = findViewById(R.id.userNameTV);
        userBalanceTV = findViewById(R.id.userBalanceTV);
        accountsCloseIV = findViewById(R.id.accountsCloseIV);
        bindImage();

        accountsCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void bindImage() {
        try {

//            setUserBalance(objMyApplication.getWalletResponseData());
            setUserBalance(objMyApplication.getCurrentUserData().getMerchantWalletResponse());

            userNameTV.setText(objMyApplication.getStrUserName());
            imgProfile.setVisibility(View.GONE);
            userShortInfoTV.setVisibility(View.VISIBLE);
            String imageString = objMyApplication.getMyProfile().getData().getImage();
            String imageTextNew = "";
            imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            userShortInfoTV.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                imgProfile.setVisibility(View.VISIBLE);
                userShortInfoTV.setVisibility(View.GONE);
                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(imageString, imgProfile, R.drawable.ic_profile_male_user);
            } else {
                imgProfile.setVisibility(View.GONE);
                userShortInfoTV.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                userShortInfoTV.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setUserBalance(WalletResponseData walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getWalletNames();
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
//                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
//                        objMyApplication.setGbtWallet(walletInfo.get(i));
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getAvailabilityToUse()));
//                    userBalanceTV.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
                    userBalanceTV.setText(strAmount);
                    objMyApplication.setGBTBalance(walletInfo.get(i).getAvailabilityToUse(), walletInfo.get(0).getWalletType());
//                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
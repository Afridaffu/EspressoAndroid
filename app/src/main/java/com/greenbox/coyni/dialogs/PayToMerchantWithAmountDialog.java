package com.greenbox.coyni.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.ScanActivity;

public class PayToMerchantWithAmountDialog extends BaseDialog {

    private LinearLayout copyAddressLL;
    private String amount;
    UserDetails userDetails;
    private TextView payAmount, recipientAddressTV, tv_lable, accountType, availableBalance, userName;
    private String recipientAddress = "";
    private boolean screenCheck;
    Boolean isFaceLock = false, isTouchId = false;
    private MotionLayout slideToConfirm;
    private CardView im_lock_;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private final String pay = "payTransaction";
    private double balance;

    public PayToMerchantWithAmountDialog(Context context) {
        super(context);
    }

    public PayToMerchantWithAmountDialog(Context context, String strAmount, UserDetails userDetails, boolean isShowIcon, double balance) {
        super(context);
        amount = strAmount;
        this.userDetails = userDetails;
        this.screenCheck = isShowIcon;
        this.balance = balance;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_merchant_with_amount);
        payAmount = findViewById(R.id.amountPayTV);
        accountType = findViewById(R.id.accountType);
        availableBalance = findViewById(R.id.availBalTV);
        recipientAddressTV = findViewById(R.id.recipientAddTV);
        copyAddressLL = findViewById(R.id.copyRecipientLL);
        slideToConfirm = findViewById(R.id.slideToConfirmML);
        userName = findViewById(R.id.userNameTV);
        tv_lable = findViewById(R.id.tv_lable);
        im_lock_ = findViewById(R.id.im_lock_);

        bindUserInfo(userDetails);

        if (amount != null) {
            amount=amount.replace(",","").trim();
            payAmount.setText(Utils.USNumberFormat(Double.parseDouble(amount)));
        }
        if (recipientAddress.length() > 13) {
            recipientAddressTV.setText(recipientAddress.substring(0, 13) + "...");
        } else {
            recipientAddressTV.setText(recipientAddress);
        }

        if (availableBalance != null) {
            availableBalance.setText("Available: " + Utils.USNumberFormat(balance) + "CYN");
        }

        if (userDetails.getData().getFullName() != null) {
            userName.setText("Paying " + userDetails.getData().getFullName());
        }

        slideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                LogUtils.v("TAG", "onTransitionStarted");
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                LogUtils.v("TAG", progress + " progress percent " + Utils.slidePercentage);
                try {
                    if (progress > Utils.slidePercentage) {
                        im_lock_.setAlpha(1.0f);
                        motionLayout.setTransition(R.id.middle, R.id.end);
                        motionLayout.transitionToState(motionLayout.getEndState());
                        slideToConfirm.setInteractionEnabled(false);
                        tv_lable.setText("Verifying");
                        dismiss();
                        getOnDialogClickListener().onDialogClicked(pay, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                LogUtils.v("TAG", "onTransitionCompleted");
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                LogUtils.v("TAG", "onTransitionTrigger");
            }
        });


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

            if (screenCheck) {

                findViewById(R.id.profile_account_profile_image).setVisibility(View.GONE);
                findViewById(R.id.merchantNameLL).setVisibility(View.GONE);
                userWalletAddre.setVisibility(View.GONE);
                findViewById(R.id.viewLineV).setVisibility(View.GONE);

            }
//            requestedToUserId = userDetails.getData().getUserId();
            if (userDetails.getData().getFullName() != null) {
                if (userDetails.getData().getFullName().length() > 20) {
                    tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
                } else {
                    tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
                }
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
            recipientAddress = userDetails.getData().getWalletId();

            copyAddressLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(recipientAddress, ScanActivity.scanActivity);
//                    listener.onDialogClicked();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

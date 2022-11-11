package com.coyni.mapp.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.coyni.mapp.R;
import com.coyni.mapp.model.payrequest.TransferPayRequest;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

public class PayToMerchantWithAmountDialog extends BaseDialog {

    private LinearLayout copyAddressLL;
    private String amount;
    private UserDetails userDetails;
    private TextView payAmount, recipientAddressTV, tv_lable, tv_lable_verify, accountType, availableBalance, userName, bTypeValue;
    private String recipientAddress = "";
    private boolean screenCheck;
    Boolean isFaceLock = false, isTouchId = false;
    private MotionLayout slideToConfirm;
    private CardView im_lock_;
    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    private final String pay = "paidOrder";
    private final String payPersonal = "payPersonal";
    private double balance;
    private MyApplication myApplication;
    private String businessTypeValue;

    public PayToMerchantWithAmountDialog(Context context, String strAmount, UserDetails userDetails, boolean isShowIcon, double balance, MyApplication bTypeResp) {
        super(context);
        amount = strAmount;
        this.userDetails = userDetails;
        this.screenCheck = isShowIcon;
        this.balance = balance;
        this.myApplication = bTypeResp;
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
        tv_lable_verify = findViewById(R.id.tv_lable_verify);
        im_lock_ = findViewById(R.id.im_lock_);
        bTypeValue = findViewById(R.id.businessTypeTV);

        bindUserInfo(userDetails);


        if (amount != null) {
            amount = amount.replace(",", "").trim();
            payAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(amount)));
        }
        if (recipientAddress.length() > 13) {
            recipientAddressTV.setText(recipientAddress.substring(0, 13) + "...");
        } else {
            recipientAddressTV.setText(recipientAddress);
        }

        if (availableBalance != null) {
            availableBalance.setText("Available: " + Utils.USNumberFormat(balance) + "CYN");
        }

        if (userDetails != null && userDetails.getData() != null && userDetails.getData().getDbaName() != null) {
            if (userDetails.getData().getDbaName().length() > 21) {
                userName.setText("Paying " + userDetails.getData().getDbaName().substring(0, 21) + "...");
            } else {
                userName.setText("Paying " + userDetails.getData().getDbaName());
            }
        } else if (userDetails != null && userDetails.getData() != null && userDetails.getData().getFullName() != null) {
            String fullName = userDetails.getData().getFullName();
            if (fullName.length() > 21) {
                userName.setText("Paying " + fullName.substring(0, 21) + "...");
            } else {
                userName.setText("Paying " + fullName);
            }
        }
//        if (businessTypeValue != null) {
//            if (businessTypeValue.length() >= 21) {
//                bTypeValue.setText(businessTypeValue.substring(0, 21) + "...");
//            } else {
//                bTypeValue.setText(businessTypeValue);
//            }
//        }
        if (myApplication.getBusinessTypeResp() != null && myApplication.getBusinessTypeResp().getData() != null) {
            for (int i = 0; i < myApplication.getBusinessTypeResp().getData().size(); i++) {
                try {
                    if (userDetails != null && userDetails.getData() != null && userDetails.getData().getBusinessType() != null && userDetails.getData().getBusinessType().toLowerCase().trim().equals(myApplication.getBusinessTypeResp().getData().get(i).getKey().toLowerCase().trim())) {
                        businessTypeValue = myApplication.getBusinessTypeResp().getData().get(i).getValue();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (businessTypeValue != null && businessTypeValue.length() > 0) {
            try {
                businessTypeValue = businessTypeValue.split("\\(")[0];
                if (businessTypeValue.length() >= 21) {
                    bTypeValue.setText("(" + businessTypeValue.substring(0, 21) + "..." + ")");
                } else {
                    bTypeValue.setText("(" + businessTypeValue + ")");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//                        tv_lable.setVisibility(View.GONE);
//                        tv_lable_verify.setVisibility(View.VISIBLE);
                        dismiss();
                        if (Utils.PERSONAL_ACCOUNT == userDetails.getData().getAccountType()) {
                            payTransactionRequest();
                            getOnDialogClickListener().onDialogClicked(payPersonal, null);
                        } else {
                            getOnDialogClickListener().onDialogClicked(pay, null);
                        }
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
            TextView tvName;
            ImageView userProfile;
            tvName = findViewById(R.id.payingDbaNameTV);
            LinearLayout feeLL = findViewById(R.id.processing_fee_ll);
            TextView proc_fee = findViewById(R.id.processing_fees);
//            userName = findViewById(R.id.userProfileTextTV);
            userProfile = findViewById(R.id.userProfileIV);

            if (screenCheck) {

                findViewById(R.id.profile_account_profile_image).setVisibility(View.GONE);
                findViewById(R.id.payingDbaNameTV).setVisibility(View.GONE);
                findViewById(R.id.businessTypeTV).setVisibility(View.GONE);
                findViewById(R.id.viewLineV).setVisibility(View.GONE);

            }
//            requestedToUserId = userDetails.getData().getUserId();
//            if (userDetails != null && userDetails.getData() != null && userDetails.getData().getFullName() != null) {
//                if (userDetails.getData().getFullName().length() > 20) {
//                    tvName.setText(Utils.capitalize(userDetails.getData().getFullName()).substring(0, 20) + "...");
//                } else {
//                    tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
//                }
//            }
            if (userDetails != null && userDetails.getData() != null) {
                if (userDetails.getData().getDbaName() != null) {
                    if (userDetails.getData().getDbaName().length() >= 21) {
                        tvName.setText((userDetails.getData().getDbaName().substring(0, 21)) + "...");
                    } else {
                        tvName.setText(userDetails.getData().getDbaName());
                    }
                } else {
                    tvName.setText(userDetails.getData().getFirstName().concat(" " + userDetails.getData().getLastName()));
                }

                if (Utils.PERSONAL_ACCOUNT == userDetails.getData().getAccountType()) {
                    feeLL.setVisibility(View.VISIBLE);
                    proc_fee.setText(Utils.convertBigDecimalUSDC(String.valueOf(myApplication.getProcessingFee())));
                }

                if (userDetails.getData().getAccountType() == Utils.PERSONAL_ACCOUNT){
                    accountType.setText("Token Account");
                }

            }
//            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
//            strUserName = Utils.capitalize(userDetails.getData().getFullName());
//            String imageTextNew = "";
//            if (userDetails != null && userDetails.getData() != null && userDetails.getData().getFirstName() != null && userDetails.getData().getLastName() != null) {
//                imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
//                        userDetails.getData().getLastName().substring(0, 1).toUpperCase();
//            }
//            userName.setText(imageTextNew);
//            if (userDetails.getData().getWalletId().length() > 10) {
//                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId().substring(0, 10) + "...");
//            } else {
//                userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
//            }
            if (userDetails.getData().getImage() != null && !userDetails.getData().getImage().trim().equals("")) {
                DisplayImageUtility utility = DisplayImageUtility.getInstance(getContext());
                utility.addImage(userDetails.getData().getImage(), userProfile, R.drawable.acct_profile);
            } else {
                userProfile.setImageResource(R.drawable.acct_profile);
            }
            recipientAddress = "";
            recipientAddress = userDetails.getData().getWalletId();

            copyAddressLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(recipientAddress, getContext());
//                    listener.onDialogClicked();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransactionRequest() {
        try {
            TransferPayRequest request = new TransferPayRequest();
            request.setTokens(payAmount.getText().toString().trim().replace(",", ""));
            request.setRemarks("");
            request.setRecipientWalletId(recipientAddress);
            myApplication.setTransferPayRequest(request);
            myApplication.setWithdrawAmount(Double.parseDouble(amount));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

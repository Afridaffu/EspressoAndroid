package com.greenbox.coyni.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.RetEmailAdapter;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class GiftCardBindingLayoutActivity extends AppCompatActivity {
    String strScreen = "", fee = "";
    TextView giftCardTypeTV, giftCardAmountTV, giftCardDescTV, refIDTV, gcProcessingTV, learnMoreTV, tvMessage;
    LinearLayout refIDLL;
    CardView doneCV, cvTryAgain;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gift_card_binding_layout);
            initialization();
            if (getIntent().getStringExtra("status") != null && !getIntent().getStringExtra("status").equals("")) {
                strScreen = getIntent().getStringExtra("status");
                ControlMethod(strScreen);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            giftCardTypeTV = findViewById(R.id.giftCardTypeTV);
            giftCardAmountTV = findViewById(R.id.giftCardAmountTV);
            giftCardDescTV = findViewById(R.id.giftCardDescTV);
            refIDTV = findViewById(R.id.refIDTV);
            gcProcessingTV = findViewById(R.id.gcProcessingTV);
            learnMoreTV = findViewById(R.id.learnMoreTV);
            refIDLL = findViewById(R.id.refIDLL);
            doneCV = findViewById(R.id.doneCV);

            tvMessage = findViewById(R.id.tvMessage);
            cvTryAgain = findViewById(R.id.cvTryAgain);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "inprogress": {
                    findViewById(R.id.inProgressContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.failedContainer).setVisibility(View.GONE);

                    if (objMyApplication.getSelectedBrandResponse() != null) {
                        giftCardTypeTV.setText(objMyApplication.getSelectedBrandResponse().getData().getBrands().get(0).getItems().get(0).getRewardName());
                    }
                    if (objMyApplication.getGcWithdrawRequest() != null) {
                        giftCardAmountTV.setText(Utils.convertBigDecimalUSDC(objMyApplication.getGcWithdrawRequest().getGiftCardWithDrawInfo().getTotalAmount().toString()));
                        giftCardDescTV.setText(objMyApplication.getGcWithdrawRequest().getGiftCardWithDrawInfo().getGiftCardName() + " gift card sent to " +
                                objMyApplication.getGcWithdrawRequest().getGiftCardWithDrawInfo().getRecipientDetails().get(0).getFirstName() + " " + objMyApplication.getGcWithdrawRequest().getGiftCardWithDrawInfo().getRecipientDetails().get(0).getLastName() + " at " +
                                objMyApplication.getGcWithdrawRequest().getGiftCardWithDrawInfo().getRecipientDetails().get(0).getEmail()+".");
                        gcProcessingTV.setText("We are processing your request, please allow a few minutes for your " + objMyApplication.getGcWithdrawRequest().getGiftCardWithDrawInfo().getGiftCardName() + " gift card to be");
                    }

                    if (objMyApplication.getWithdrawResponse() != null) {
                        refIDTV.setText(objMyApplication.getWithdrawResponse().getData().getGbxTransactionId().substring(0, 15));
                    }

                    learnMoreTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.populateLearnMore(GiftCardBindingLayoutActivity.this);
                        }
                    });

                    refIDLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.copyText(objMyApplication.getWithdrawResponse().getData().getGbxTransactionId(), GiftCardBindingLayoutActivity.this);
                        }
                    });

                    doneCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    });
                }
                break;
                case "failed": {
                    findViewById(R.id.inProgressContainer).setVisibility(View.GONE);
                    findViewById(R.id.failedContainer).setVisibility(View.VISIBLE);

                    if (objMyApplication.getWithdrawResponse() != null) {
                        tvMessage.setText("The transaction failed due to error code:\n" +
                                objMyApplication.getWithdrawResponse().getError().getErrorCode() + " - " +
                                objMyApplication.getWithdrawResponse().getError().getErrorDescription() + ". Please try again.");
                    }

                    cvTryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                finish();
                                GiftCardDetails.giftCardDetails.finish();
                                GiftCardActivity.giftCardActivity.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

}
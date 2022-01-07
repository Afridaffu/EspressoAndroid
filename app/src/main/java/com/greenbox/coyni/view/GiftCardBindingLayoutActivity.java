package com.greenbox.coyni.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.RetEmailAdapter;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.model.withdraw.WithdrawResponseData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class GiftCardBindingLayoutActivity extends AppCompatActivity {
    String strScreen = "", fee = "";
    TextView giftCardTypeTV, giftCardAmountTV, giftCardDescTV, refIDTV, gcProcessingTV, learnMoreTV, tvMessage;
    LinearLayout refIDLL;
    CardView doneCV, cvTryAgain;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gift_card_binding_layout);
            initialization();
            if (getIntent().getStringExtra("status") != null && !getIntent().getStringExtra("status").equals("")) {
                strScreen = getIntent().getStringExtra("status");
                ControlMethod(strScreen, getIntent().getStringExtra("subtype"));
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

    private void ControlMethod(String methodToShow, String type) {
        try {
            switch (methodToShow) {
                case "inprogress": {
                    if (type.equals("giftcard")) {
                        findViewById(R.id.inProgressContainer).setVisibility(View.VISIBLE);
                        findViewById(R.id.failedContainer).setVisibility(View.GONE);
                        findViewById(R.id.wdInProgressContainer).setVisibility(View.GONE);
                        giftCardInProgress();
                    } else {
                        findViewById(R.id.inProgressContainer).setVisibility(View.GONE);
                        findViewById(R.id.failedContainer).setVisibility(View.GONE);
                        findViewById(R.id.wdInProgressContainer).setVisibility(View.VISIBLE);
                        withdrawInProgress(objMyApplication.getWithdrawResponse().getData());
                    }
                }
                break;
                case "failed": {
                    findViewById(R.id.inProgressContainer).setVisibility(View.GONE);
                    findViewById(R.id.failedContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.wdInProgressContainer).setVisibility(View.GONE);
                    failedTransaction(type);
                }
                break;
                case "success":
                    findViewById(R.id.inProgressContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.failedContainer).setVisibility(View.GONE);
                    findViewById(R.id.wdInProgressContainer).setVisibility(View.GONE);
                    if (type.equals("pay")) {
                        payRequestSuccess();
                    } else {
                        requestSuccess();
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void giftCardInProgress() {
        try {
            if (objMyApplication.getSelectedBrandResponse() != null) {
                giftCardTypeTV.setText(objMyApplication.getSelectedBrandResponse().getData().getBrands().get(0).getItems().get(0).getRewardName());
            }
            if (objMyApplication.getWithdrawRequest() != null) {
                giftCardAmountTV.setText(Utils.convertBigDecimalUSDC(objMyApplication.getWithdrawRequest().getGiftCardWithDrawInfo().getTotalAmount().toString()));
                giftCardDescTV.setText(objMyApplication.getWithdrawRequest().getGiftCardWithDrawInfo().getGiftCardName() + " gift card sent to " +
                        objMyApplication.getWithdrawRequest().getGiftCardWithDrawInfo().getRecipientDetails().get(0).getFirstName() + " " + objMyApplication.getWithdrawRequest().getGiftCardWithDrawInfo().getRecipientDetails().get(0).getLastName() + " at " +
                        objMyApplication.getWithdrawRequest().getGiftCardWithDrawInfo().getRecipientDetails().get(0).getEmail() + ".");
                gcProcessingTV.setText("We are processing your request, please allow a few minutes for your " + objMyApplication.getWithdrawRequest().getGiftCardWithDrawInfo().getGiftCardName() + " gift card to be");
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawInProgress(WithdrawResponseData objData) {
        try {
            Double cynValue = 0.0;
            cynValue = objMyApplication.getWithdrawAmount();
            TextView tvAmount = findViewById(R.id.tvAmount);
            TextView tvMessage = findViewById(R.id.tvWDMessage);
            TextView tvReferenceID = findViewById(R.id.tvReferenceID);
            TextView tvBalance = findViewById(R.id.tvBalance);
            TextView tvHeading = findViewById(R.id.tvHeading);
            TextView tvDescription = findViewById(R.id.tvDescription);
            LinearLayout layoutReference = findViewById(R.id.layoutReference);
            ImageView imgLogo = findViewById(R.id.imgLogo);
            CardView cvDone = findViewById(R.id.cvDone);
            if (objData.getGbxTransactionId().length() > 10) {
                tvReferenceID.setText(objData.getGbxTransactionId().substring(0, 10) + "...");
            } else {
                tvReferenceID.setText(objData.getGbxTransactionId());
            }
            String strMessage = "";
            if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                strMessage = "We are processing  your request, please allow a 3-5 business days for your coyni bank withdrawal to be reflected in your bank account. Learn More";
            } else {
                strMessage = "We are processing your request, please allow a few minutes for your coyni instant withdrawal to be reflected in your bank account. Learn More";
            }
            SpannableString ss = new SpannableString(strMessage);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.populateLearnMore(GiftCardBindingLayoutActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#00a6a2"));
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#00a6a2")), strMessage.indexOf("Learn More"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new UnderlineSpan(), strMessage.indexOf("Learn More"), strMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(clickableSpan, strMessage.indexOf("Learn More"), strMessage.length(), 0);

            tvDescription.setText(ss);

            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            tvHeading.setText("Transaction in Progress");
            imgLogo.setImageResource(R.drawable.ic_in_progress_icon);
            Double bal = cynValue + objMyApplication.getGBTBalance();
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
            tvAmount.setText("$ " + Utils.USNumberFormat(cynValue));
//            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as " + objData.getDescriptorName() + ".");
            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as Coyni.");
            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            layoutReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getGbxTransactionId(), GiftCardBindingLayoutActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payRequestSuccess() {
        try {
            Double cynValue = 0.0;
            cynValue = objMyApplication.getWithdrawAmount();
            ImageView imgLogo = findViewById(R.id.imgLogo);
            TextView giftCardTypeTV = findViewById(R.id.giftCardTypeTV);
            TextView giftCardAmountTV = findViewById(R.id.giftCardAmountTV);
            TextView giftCardDescTV = findViewById(R.id.giftCardDescTV);
            TextView goneTV = findViewById(R.id.goneTV);
            TextView tvCurrency = findViewById(R.id.tvCurrency);
            TextView gcProcessingTV = findViewById(R.id.gcProcessingTV);
            TextView refIDTV = findViewById(R.id.refIDTV);
            LinearLayout lyMessage = findViewById(R.id.lyMessage);
            CardView doneCV = findViewById(R.id.doneCV);
            gcProcessingTV.setVisibility(View.GONE);
            lyMessage.setVisibility(View.GONE);
            goneTV.setVisibility(View.GONE);
            giftCardDescTV.setVisibility(View.GONE);
            tvCurrency.setVisibility(View.VISIBLE);
            giftCardAmountTV.setText(Utils.USNumberFormat(cynValue));
            giftCardTypeTV.setText("Transaction Successful");
            imgLogo.setImageResource(R.drawable.ic_success_icon);
            if (objMyApplication.getPayRequestResponse().getData().getGbxTransactionId().length() > 10) {
                refIDTV.setText(objMyApplication.getPayRequestResponse().getData().getGbxTransactionId().substring(0, 10) + "...");
            } else {
                refIDTV.setText(objMyApplication.getPayRequestResponse().getData().getGbxTransactionId());
            }
            doneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void failedTransaction(String type) {
        try {
            if (!type.equals("pay") && !type.equals("request")) {
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
            } else {
                if (objMyApplication.getPayRequestResponse() != null) {
                    tvMessage.setText("The transaction failed due to error code:\n" +
                            objMyApplication.getPayRequestResponse().getError().getErrorCode() + " - " +
                            objMyApplication.getPayRequestResponse().getError().getErrorDescription() + ". Please try again.");
                }

                cvTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void requestSuccess() {
        try {
            ImageView imgLogo = findViewById(R.id.imgLogo);
            LinearLayout lyAmount = findViewById(R.id.lyAmount);
            TextView giftCardDescTV = findViewById(R.id.giftCardDescTV);
            TextView giftCardTypeTV = findViewById(R.id.giftCardTypeTV);
            TextView gcProcessingTV = findViewById(R.id.gcProcessingTV);
            TextView tvSuccess = findViewById(R.id.tvSuccess);
            LinearLayout lyReference = findViewById(R.id.lyReference);
            LinearLayout lyMessage = findViewById(R.id.lyMessage);
            CardView doneCV = findViewById(R.id.doneCV);
            gcProcessingTV.setVisibility(View.GONE);
            lyMessage.setVisibility(View.GONE);
            lyAmount.setVisibility(View.GONE);
            giftCardDescTV.setVisibility(View.GONE);
            lyReference.setVisibility(View.GONE);
            giftCardTypeTV.setVisibility(View.GONE);
            tvSuccess.setVisibility(View.VISIBLE);
            imgLogo.setImageResource(R.drawable.ic_success_icon);

            doneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

}
package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.buytoken.BuyTokenResponseData;
import com.greenbox.coyni.model.withdraw.WithdrawResponseData;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.viewmodel.CoyniViewModel;

public class GiftCardBindingLayoutActivity extends AppCompatActivity {
    String strScreen = "", enableType = "";
    TextView giftCardTypeTV, giftCardAmountTV, giftCardDescTV, refIDTV, gcProcessingTV, learnMoreTV, tvMessage;
    LinearLayout refIDLL;
    CardView doneCV, cvTryAgain;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    ProgressDialog pDialog;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    CoyniViewModel coyniViewModel;
    SQLiteDatabase mydatabase;
    DatabaseHandler dbHandler;
    Double cynValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gift_card_binding_layout);
            initialization();
            initObserver();
            if (getIntent().getStringExtra("status") != null && !getIntent().getStringExtra("status").equals("")) {
                strScreen = getIntent().getStringExtra("status");
                if (getIntent().getStringExtra("cynValue") != null && !getIntent().getStringExtra("cynValue").equals("")) {
                    cynValue = Double.parseDouble(getIntent().getStringExtra("cynValue"));
                }
                ControlMethod(strScreen, getIntent().getStringExtra("subtype"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Shiva Changed
    //    private void saveThumb(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblThumbPinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
//            mydatabase.execSQL("Delete from tblThumbPinLock");
//            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    private void saveThumb(String value) {
        try {
            dbHandler.clearThumbPinLockTable();
            dbHandler.insertThumbPinLock(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


//    private void saveFace(String value) {
//        try {
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
//            mydatabase.execSQL("Delete from tblFacePinLock");
//            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void saveFace(String value) {
        try {
            dbHandler.clearFacePinLockTable();
            dbHandler.insertFacePinLock(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void saveToken(String value) {
//        try {
//            objMyApplication.setStrMobileToken(value);
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblPermanentToken(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, perToken TEXT);");
//            mydatabase.execSQL("Delete from tblPermanentToken");
//            mydatabase.execSQL("INSERT INTO tblPermanentToken(id,perToken) VALUES(null,'" + value + "')");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void saveToken(String value) {
        try {
            objMyApplication.setStrMobileToken(value);
            dbHandler.clearPermanentTokenTable();
            dbHandler.insertPermanentToken(value);
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
            dbHandler = DatabaseHandler.getInstance(GiftCardBindingLayoutActivity.this);
            gcProcessingTV = findViewById(R.id.gcProcessingTV);
            learnMoreTV = findViewById(R.id.learnMoreTV);
            refIDLL = findViewById(R.id.refIDLL);
            doneCV = findViewById(R.id.doneCV);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
            tvMessage = findViewById(R.id.tvMessage);
            cvTryAgain = findViewById(R.id.cvTryAgain);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initObserver() {
        coyniViewModel.getBiometricResponseMutableLiveData().observe(this, new Observer<BiometricResponse>() {
            @Override
            public void onChanged(BiometricResponse biometricResponse) {
                pDialog.dismiss();
                if (biometricResponse != null) {
                    Log.e("bio resp", new Gson().toJson(biometricResponse));
                    saveToken(biometricResponse.getData().getToken());
//                    Utils.generateUUID(GiftCardBindingLayoutActivity.this);
                    if (!objMyApplication.isDeviceID()) {
                        Utils.generateUUID(GiftCardBindingLayoutActivity.this);
                    }
                    if (enableType.equals("FACE")) {
                        saveFace("true");
                        saveThumb("false");
                        Utils.showCustomToast(GiftCardBindingLayoutActivity.this, "Face ID has been turned on", R.drawable.ic_faceid, "authid");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                try {
//                                    Intent d = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
//                                    d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(d);
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
                                dashboardNavigation();
                            }
                        }, 2000);
                    } else if (enableType.equals("TOUCH")) {
                        saveFace("false");
                        saveThumb("true");
                        Utils.showCustomToast(GiftCardBindingLayoutActivity.this, "Touch ID has been turned on", R.drawable.ic_touch_id, "authid");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                try {
//                                    Intent d = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
//                                    d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(d);
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
                                dashboardNavigation();
                            }
                        }, 2000);
                    }
                    objMyApplication.setBiometric(true);
                }
            }
        });
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
                    } else if (type.equals("buy")) {
                        findViewById(R.id.inProgressContainer).setVisibility(View.GONE);
                        findViewById(R.id.failedContainer).setVisibility(View.GONE);
                        findViewById(R.id.wdInProgressContainer).setVisibility(View.GONE);
                        findViewById(R.id.buyInProgressContainer).setVisibility(View.VISIBLE);
                        buyInProgress(objMyApplication.getBuyTokenResponse().getData());
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
                case "Success": {
                    findViewById(R.id.inProgressContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.failedContainer).setVisibility(View.GONE);
                    findViewById(R.id.wdInProgressContainer).setVisibility(View.GONE);
                    paidTransactionSucess();
                }
                break;
                case "Failed": {
                    findViewById(R.id.inProgressContainer).setVisibility(View.GONE);
                    findViewById(R.id.failedContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.wdInProgressContainer).setVisibility(View.GONE);
                    failedPaidTransaction();
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void failedPaidTransaction() {
        if (objMyApplication.getPaidOrderResp() != null){
            tvMessage.setText(
                    "The transaction failed due to error code:\n" +
                    objMyApplication.getPaidOrderResp().getError().getErrorCode() + " - " +
                    objMyApplication.getPaidOrderResp().getError().getErrorDescription() + ". Please try again.");
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

    private void paidTransactionSucess() {
    }

    private void buyInProgress(BuyTokenResponseData objData) {
        try {
            TextView tvAmount = findViewById(R.id.tvBAmount);
            TextView tvMessage = findViewById(R.id.tvBMessage);
            TextView tvReferenceID = findViewById(R.id.tvBReferenceID);
            TextView tvBalance = findViewById(R.id.tvBBalance);
            TextView tvLearnMore = findViewById(R.id.tvBLearnMore);
            TextView tvHeading = findViewById(R.id.tvBHeading);
            TextView tvDescription = findViewById(R.id.tvDescriptionBuy);
            LinearLayout layoutReference = findViewById(R.id.layoutBReference);
            ImageView imgLogo = findViewById(R.id.imgBLogo);
            ImageView imgRefCopy = findViewById(R.id.imgBRefCopy);
            CardView cvDone = findViewById(R.id.cvBDone);
            if (objData.getGbxTransactionId().length() > 10) {
                tvReferenceID.setText(objData.getGbxTransactionId().substring(0, 10) + "...");
            } else {
                tvReferenceID.setText(objData.getGbxTransactionId());
            }

            String strMessage = "";
            if (objData.getType().toLowerCase().contains("bank")) {
                tvHeading.setText("Transaction Pending");
                imgLogo.setImageResource(R.drawable.ic_hourglass_pending_icon);
                strMessage = "We submitted your request, please allow a 3-5 business days for your coyni purchase to be reflected in your token account. Learn More";
            } else {
                tvHeading.setText("Transaction In Progress");
                imgLogo.setImageResource(R.drawable.ic_in_progress_icon);
                strMessage = "We are processing your request, please allow a few minutes for your coyni debit/credit card purchase to be reflected in your token account. Learn More";
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
            ss.setSpan(clickableSpan, strMessage.length() - 10, strMessage.length() - 1, 0);

            tvDescription.setText(ss);
            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());

            Double bal = cynValue + objMyApplication.getGBTBalance();
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
            tvAmount.setText(Utils.USNumberFormat(cynValue));
//            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as " + objData.getDescriptorName().toLowerCase() + ".");

            if (objData.getDescriptorName() != null)
                tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as " + objData.getDescriptorName().toLowerCase() + ".");
            else
                tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as coyni.");

            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dashboardNavigation();
                }
            });

            tvReferenceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getGbxTransactionId(), GiftCardBindingLayoutActivity.this);
                }
            });

            imgRefCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getGbxTransactionId(), GiftCardBindingLayoutActivity.this);
                }
            });

            tvLearnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateLearnMore(GiftCardBindingLayoutActivity.this);
                }
            });
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
                    dashboardNavigation();
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
//            ss.setSpan(clickableSpan, strMessage.indexOf("Learn More"), strMessage.length(), 0);
            ss.setSpan(clickableSpan, strMessage.length() - 10, strMessage.length() - 1, 0);

            tvDescription.setText(ss);

            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            tvHeading.setText("Transaction In Progress");
            imgLogo.setImageResource(R.drawable.ic_in_progress_icon);
            Double bal = cynValue + objMyApplication.getGBTBalance();
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
            tvAmount.setText("$ " + Utils.USNumberFormat(cynValue));
//            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as " + objData.getDescriptorName().toLowerCase() + ".");
            tvMessage.setText("This total amount of " + tvAmount.getText().toString().trim() + " will appear on your\nBank statement as coyni.");
            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dashboardNavigation();
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
            TextView tvReference = findViewById(R.id.refIDTV);
            LinearLayout lyMessage = findViewById(R.id.lyMessage);
            LinearLayout lyReference = findViewById(R.id.refIDLL);
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
                tvReference.setText(objMyApplication.getPayRequestResponse().getData().getGbxTransactionId().substring(0, 10) + "...");
            } else {
                tvReference.setText(objMyApplication.getPayRequestResponse().getData().getGbxTransactionId());
            }
            lyReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Utils.copyText(objMyApplication.getPayRequestResponse().getData().getGbxTransactionId(), GiftCardBindingLayoutActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            doneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dashboardNavigation();
                }
            });

            if (!objMyApplication.getBiometric()) {
                if (Utils.checkAuthentication(GiftCardBindingLayoutActivity.this)) {
                    if (Utils.isFingerPrint(GiftCardBindingLayoutActivity.this)) {
                        enableType = "TOUCH";
                        loadSecurePay("TOUCH");
                    } else {

                        enableType = "FACE";
                        loadSecurePay("FACE");
                    }
                }
            }
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
                            if (type.equals("giftcard")) {
                                GiftCardDetails.giftCardDetails.finish();
                                GiftCardActivity.giftCardActivity.finish();
                            } else if (type.equals("buy")) {
                                onBackPressed();
                            } else {
                                Intent i = new Intent(GiftCardBindingLayoutActivity.this, WithdrawPaymentMethodsActivity.class);
                                startActivity(i);
                            }
                            finish();
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
                    dashboardNavigation();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSecurePay(String enableType) {
        try {
            final Dialog dialog = new Dialog(GiftCardBindingLayoutActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.securepaylayout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView message = dialog.findViewById(R.id.tvMessage);
            CardView enableFaceCV = dialog.findViewById(R.id.enableFaceCV);
            LinearLayout layoutNotnowFace = dialog.findViewById(R.id.layoutNotnowFace);
            TextView dontRemindFace = dialog.findViewById(R.id.dontRemindFace);
            TextView tvEnableFace = dialog.findViewById(R.id.tvEnableFace);

            if (enableType.equals("TOUCH")) {
                tvEnableFace.setText("Enable Touch ID");
                message.setText("Enable Touch ID to Use touch to complete payments quickly and securely.");
            } else {
                tvEnableFace.setText("Enable Face ID");
                message.setText("Enable Face ID to Use face recognition to complete payments quickly and securely.");
            }

            layoutNotnowFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dontRemindFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            enableFaceCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        dialog.dismiss();
                        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                        if (!fingerprintManager.isHardwareDetected()) {
                            Log.e("Not support", "Not support");
                        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                            final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                            enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG);
                            startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                        } else {
                            pDialog = new ProgressDialog(GiftCardBindingLayoutActivity.this, R.style.MyAlertDialogStyle);
                            pDialog.setIndeterminate(false);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();
                            BiometricRequest biometricRequest = new BiometricRequest();
                            biometricRequest.setBiometricEnabled(true);
                            biometricRequest.setDeviceId(Utils.getDeviceID());
                            coyniViewModel.saveBiometric(biometricRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE && resultCode == RESULT_OK) {

                pDialog = new ProgressDialog(GiftCardBindingLayoutActivity.this, R.style.MyAlertDialogStyle);
                pDialog.setIndeterminate(false);
                pDialog.setMessage("Please wait...");
                pDialog.show();
                BiometricRequest biometricRequest = new BiometricRequest();
                biometricRequest.setBiometricEnabled(true);
                biometricRequest.setDeviceId(Utils.getDeviceID());
                coyniViewModel.saveBiometric(biometricRequest);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void dashboardNavigation() {
        try {
            Intent i;
            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                i = new Intent(GiftCardBindingLayoutActivity.this, DashboardActivity.class);
            } else {
                i = new Intent(GiftCardBindingLayoutActivity.this, BusinessDashboardActivity.class);
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
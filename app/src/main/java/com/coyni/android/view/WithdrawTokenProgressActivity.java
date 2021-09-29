package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.model.GlideApp;
import com.coyni.android.model.giftcard.Brand;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.github.jinatonic.confetti.CommonConfetti;

public class WithdrawTokenProgressActivity extends AppCompatActivity {
    TextView tvDeptAmount, tvCardNo, tvBalance, tvDepositId, tvDepositHead, tvDepIdHead, tvBank;
    ImageView imgBCopy, imgGCCopy;
    LinearLayout layoutDone;
    String strDepositId = "", strOrderId = "";
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_withdraw_token_progress);
            initialization();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    animation();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(WithdrawTokenProgressActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WithdrawTokenProgressActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WithdrawTokenProgressActivity.this, this, false);
    }


    private void initialization() {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FFFFFF"));
            }
            objMyApplication = (MyApplication) getApplicationContext();
            tvDeptAmount = (TextView) findViewById(R.id.tvDeptAmount);
            tvCardNo = (TextView) findViewById(R.id.tvCardNo);
            tvBalance = (TextView) findViewById(R.id.tvBalance);
            tvDepositId = (TextView) findViewById(R.id.tvDepositId);
            tvDepositHead = (TextView) findViewById(R.id.tvDepositHead);
            tvDepIdHead = (TextView) findViewById(R.id.tvDepIdHead);
            tvBank = (TextView) findViewById(R.id.tvBank);
            imgBCopy = (ImageView) findViewById(R.id.imgBCopy);
            imgGCCopy = (ImageView) findViewById(R.id.imgGCCopy);
            layoutDone = (LinearLayout) findViewById(R.id.layoutDone);
            RelativeLayout layoutGift, layoutBank;
            layoutGift = (RelativeLayout) findViewById(R.id.layoutGift);
            layoutBank = (RelativeLayout) findViewById(R.id.layoutBank);
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("giftcard")) {
                layoutBank.setVisibility(View.VISIBLE);
                layoutGift.setVisibility(View.GONE);
                if (getIntent().getStringExtra("subtype") != null && getIntent().getStringExtra("subtype").equals("bank")) {
                    tvDepositHead.setText("Bank Deposit Amount");
                    tvBank.setVisibility(View.VISIBLE);
                    if (getIntent().getStringExtra("cardno") != null && !getIntent().getStringExtra("cardno").equals("")) {
                        //tvCardNo.setText(getIntent().getStringExtra("cardno"));
                        if (getIntent().getStringExtra("cardno").length() > 4) {
                            tvCardNo.setText("**** " + getIntent().getStringExtra("cardno").substring(getIntent().getStringExtra("cardno").length() - 4));
                        } else {
                            tvCardNo.setText("**** " + getIntent().getStringExtra("cardno"));
                        }
                    }
                    if (getIntent().getStringExtra("bank").length() > 10) {
                        tvBank.setText(getIntent().getStringExtra("bank").substring(0, 10) + "...");
                    } else {
                        tvBank.setText(getIntent().getStringExtra("bank"));
                    }
                    tvDepIdHead.setText("Withdrawal ID");
                } else {
                    tvDepositHead.setText("Signet Account Withdraw\nAmount");
                    tvBank.setVisibility(View.GONE);
                    if (getIntent().getStringExtra("cardno") != null && !getIntent().getStringExtra("cardno").equals("")) {
                        tvCardNo.setText(getIntent().getStringExtra("cardno"));
//                        String actNum = "";
//                        if (getIntent().getStringExtra("cardno").length() > 4) {
//                            actNum = getIntent().getStringExtra("cardno").replace("Signet Wallet ID :", "").substring(0, 7) + "...";
//                        } else {
//                            actNum = getIntent().getStringExtra("cardno");
//                        }
//                        tvCardNo.setText("Signet Wallet ID :" + actNum);
                    }
                    tvDepIdHead.setText("Reference ID");
                }
                tvDeptAmount.setText(getIntent().getStringExtra("amount") + " USD");
//                if (getIntent().getStringExtra("cardno") != null && !getIntent().getStringExtra("cardno").equals("")) {
//                    tvCardNo.setText(getIntent().getStringExtra("cardno"));
//                }

                if (getIntent().getStringExtra("balance") != null && !getIntent().getStringExtra("balance").equals("")) {
                    tvBalance.setText(getIntent().getStringExtra("balance"));
                }

                if (getIntent().getStringExtra("transId") != null && !getIntent().getStringExtra("transId").equals("")) {
                    if (getIntent().getStringExtra("transId").length() > 10) {
                        tvDepositId.setText(getIntent().getStringExtra("transId").substring(0, 10) + "...");
                    } else {
                        tvDepositId.setText(getIntent().getStringExtra("transId"));
                    }
                    strDepositId = getIntent().getStringExtra("transId");
                }
            } else {
                layoutBank.setVisibility(View.GONE);
                layoutGift.setVisibility(View.VISIBLE);
                TextView tvAmount, tvSendTo, tvBrand, tvGiftAmount, tvOrderId, tvRemBalance;
                ImageView imgBrand;
                tvAmount = (TextView) findViewById(R.id.tvAmount);
                tvSendTo = (TextView) findViewById(R.id.tvSendTo);
                tvBrand = (TextView) findViewById(R.id.tvBrand);
                tvGiftAmount = (TextView) findViewById(R.id.tvGiftAmount);
                tvOrderId = (TextView) findViewById(R.id.tvOrderId);
                tvRemBalance = (TextView) findViewById(R.id.tvRemBalance);
                imgBrand = (ImageView) findViewById(R.id.imgBrand);
                if (getIntent().getStringExtra("balance") != null && !getIntent().getStringExtra("balance").equals("")) {
                    tvRemBalance.setText(getIntent().getStringExtra("balance"));
                }
                if (getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").equals("")) {
                    tvSendTo.setText(getIntent().getStringExtra("email"));
                }
                if (getIntent().getStringExtra("giftamt") != null && !getIntent().getStringExtra("giftamt").equals("")) {
//                    tvAmount.setText(Utils.convertBigDecimalUSDC(getIntent().getStringExtra("giftamt")) + " " + getString(R.string.currency));
                    tvAmount.setText(getIntent().getStringExtra("giftamt") + " " + getString(R.string.currency));
                }
                if (getIntent().getStringExtra("orderId") != null && !getIntent().getStringExtra("orderId").equals("")) {
                    if (getIntent().getStringExtra("orderId").length() > 10) {
                        tvOrderId.setText(getIntent().getStringExtra("orderId").substring(0, 10) + "...");
                    } else {
                        tvOrderId.setText(getIntent().getStringExtra("orderId"));
                    }
                    strOrderId = getIntent().getStringExtra("orderId");
                }
                if (getIntent().getStringExtra("brandamt") != null && !getIntent().getStringExtra("brandamt").equals("")) {
//                    tvGiftAmount.setText(Utils.convertBigDecimalUSDC(getIntent().getStringExtra("brandamt").split(" ")[0]) + " " + getIntent().getStringExtra("brandamt").split(" ")[1]);
                    tvGiftAmount.setText(getIntent().getStringExtra("brandamt").split(" ")[0] + " " + getIntent().getStringExtra("brandamt").split(" ")[1]);
                }
                if (getIntent().getSerializableExtra("brand") != null && !getIntent().getSerializableExtra("brand").equals("")) {
                    Brand objBrand = (Brand) getIntent().getSerializableExtra("brand");
                    tvBrand.setText("For " + objBrand.getBrandName());
                    if (objBrand.getImageUrls().get_1200w326ppi() != null && !objBrand.getImageUrls().get_1200w326ppi().equals("")) {
                        GlideApp.with(WithdrawTokenProgressActivity.this)
                                .load(objBrand.getImageUrls().get_80w326ppi())
                                .into(imgBrand);
                    }
                }
                tvOrderId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.copyText(strOrderId, WithdrawTokenProgressActivity.this);
                    }
                });
            }
            layoutDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(WithdrawTokenProgressActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            imgBCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strDepositId, WithdrawTokenProgressActivity.this);
                }
            });
            tvDepositId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strDepositId, WithdrawTokenProgressActivity.this);
                }
            });
            imgGCCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strOrderId, WithdrawTokenProgressActivity.this);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void animation() {
        try {
            ViewGroup container;
            container = findViewById(R.id.layoutConfetti);
            CommonConfetti.rainingConfetti(container, new int[]{Color.YELLOW, Color.GREEN, Color.MAGENTA})
                    .oneShot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
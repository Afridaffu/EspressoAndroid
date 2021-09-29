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
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.github.jinatonic.confetti.CommonConfetti;

public class BuyTokenProgressActivity extends AppCompatActivity {
    TextView tvDeptAmount, tvCardNo, tvBalance, tvDepositId, tvBank, tvAccountNo;
    ImageView imgCard, imgCopy;
    LinearLayout layoutDone, layoutCard, layoutBank;
    String strDepositId = "";
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_buy_token_progress);
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
            objMyApplication.userInactive( BuyTokenProgressActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive( BuyTokenProgressActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive( BuyTokenProgressActivity.this, this, false);
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
            TextView tvDepositHead = (TextView) findViewById(R.id.tvDepositHead);
            TextView tvHead = (TextView) findViewById(R.id.tvHead);
            TextView tvPayMethod = (TextView) findViewById(R.id.tvPayMethod);
            TextView tvDepIdHead = (TextView) findViewById(R.id.tvDepIdHead);
            TextView tvBalHead = (TextView) findViewById(R.id.tvBalHead);
            tvDeptAmount = (TextView) findViewById(R.id.tvDeptAmount);
            tvCardNo = (TextView) findViewById(R.id.tvCardNo);
            tvBalance = (TextView) findViewById(R.id.tvBalance);
            tvDepositId = (TextView) findViewById(R.id.tvDepositId);
            tvBank = (TextView) findViewById(R.id.tvBank);
            tvAccountNo = (TextView) findViewById(R.id.tvAccountNo);
            imgCard = (ImageView) findViewById(R.id.imgCard);
            imgCopy = (ImageView) findViewById(R.id.imgCopy);
            layoutDone = (LinearLayout) findViewById(R.id.layoutDone);
            layoutCard = (LinearLayout) findViewById(R.id.layoutCard);
            layoutBank = (LinearLayout) findViewById(R.id.layoutBank);
            if (getIntent().getStringExtra("screen") != null && getIntent().getStringExtra("screen").equals("withdraw")) {
                tvHead.setText(getString(R.string.inprog));
                tvDepositHead.setText("Instant Pay Amount");
                tvPayMethod.setText(getString(R.string.paymethod));
                tvDepIdHead.setText("Withdrawal ID");
                tvBalHead.setText("Remaining Balance");
            } else {
                if (getIntent().getStringExtra("type") != null && !getIntent().getStringExtra("type").equals("bank")) {
                    tvHead.setText("Transaction Successful");
                    tvBalHead.setVisibility(View.VISIBLE);
                    tvBalance.setVisibility(View.VISIBLE);
                } else {
                    tvHead.setText(getString(R.string.inprog));
                    tvBalHead.setVisibility(View.GONE);
                    tvBalance.setVisibility(View.GONE);
                }
                tvDepositHead.setText(getString(R.string.youget));
                tvPayMethod.setText(getString(R.string.paymethod));
                tvDepIdHead.setText("Reference ID");
                tvBalHead.setText("Account Balance");
            }
            if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                if (getIntent().getStringExtra("screen") != null && (getIntent().getStringExtra("screen").equals("instant") || getIntent().getStringExtra("screen").equals("withdraw"))) {
                    tvDeptAmount.setText(getIntent().getStringExtra("amount") + " USD");
                } else {
                    tvDeptAmount.setText(getIntent().getStringExtra("amount") + " " + getString(R.string.currency));
                }
            }
            if (getIntent().getStringExtra("cardno") != null && !getIntent().getStringExtra("cardno").equals("")) {
                tvCardNo.setText(getIntent().getStringExtra("cardno"));
                tvAccountNo.setText(getIntent().getStringExtra("cardno"));
            }
            if (getIntent().getStringExtra("type") != null && !getIntent().getStringExtra("type").equals("bank")) {
                layoutBank.setVisibility(View.GONE);
                layoutCard.setVisibility(View.VISIBLE);
                String strType = getIntent().getStringExtra("cardtype");
                if (getIntent().getStringExtra("type").toLowerCase().equals("visa")) {
                    imgCard.setImageResource(R.drawable.ic_visa);
                } else if (getIntent().getStringExtra("type").toLowerCase().contains("master")) {
                    imgCard.setImageResource(R.drawable.ic_master);
                } else if (getIntent().getStringExtra("type").toLowerCase().contains("american")) {
                    imgCard.setImageResource(R.drawable.ic_amex);
                } else if (getIntent().getStringExtra("type").toLowerCase().contains("discover")) {
                    imgCard.setImageResource(R.drawable.ic_discover);
                }
            } else {
                layoutBank.setVisibility(View.VISIBLE);
                layoutCard.setVisibility(View.GONE);
                if (getIntent().getStringExtra("bank").length() > 10) {
                    tvBank.setText(getIntent().getStringExtra("bank").substring(0, 10) + "...");
                } else {
                    tvBank.setText(getIntent().getStringExtra("bank"));
                }
            }
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

            layoutDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BuyTokenProgressActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            imgCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strDepositId, BuyTokenProgressActivity.this);
                }
            });

            tvDepositId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strDepositId, BuyTokenProgressActivity.this);
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
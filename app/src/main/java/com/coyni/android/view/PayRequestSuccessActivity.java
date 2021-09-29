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


public class PayRequestSuccessActivity extends AppCompatActivity {
    TextView tvAmount, tvWalletId, tvTransId, tvBalance;
    ImageView imgCopy;
    LinearLayout layoutDone;
    String strTransId = "";
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_request_success);
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
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(PayRequestSuccessActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(PayRequestSuccessActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(PayRequestSuccessActivity.this, this, false);
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
            tvAmount = findViewById(R.id.tvAmount);
            tvWalletId = findViewById(R.id.tvWalletId);
            tvTransId = findViewById(R.id.tvTransId);
            imgCopy = findViewById(R.id.imgCopy);
            tvBalance = findViewById(R.id.tvBalance);
            layoutDone = findViewById(R.id.layoutDone);
            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                tvWalletId.setText(getIntent().getStringExtra("walletId").substring(0, 12) + "...");
            }
            if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
                String strAmount = getIntent().getStringExtra("amount");
                tvAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount.replace(",", ""))) + " " + getString(R.string.currency));
            }
            if (getIntent().getStringExtra("balance") != null && !getIntent().getStringExtra("balance").equals("")) {
                String strBal = getIntent().getStringExtra("balance");
                tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal.replace(",", ""))) + " " + getString(R.string.currency));
            }
            if (getIntent().getStringExtra("transId") != null && !getIntent().getStringExtra("transId").equals("")) {
                strTransId = getIntent().getStringExtra("transId");
                tvTransId.setText(strTransId.substring(0, 10) + "...");
            }
            imgCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strTransId, PayRequestSuccessActivity.this);
                }
            });

            tvTransId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strTransId, PayRequestSuccessActivity.this);
                }
            });

            layoutDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PayRequestSuccessActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
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
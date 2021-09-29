package com.coyni.android.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coyni.android.R;
import com.github.jinatonic.confetti.CommonConfetti;


public class CardSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_card_success);
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

    private void initialization() {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FFFFFF"));
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (getIntent().getStringExtra("fromProfilePaymentMethods") != null) {
                            final Intent mainIntent = new Intent(CardSuccessActivity.this, BuyTokenActivityProfile.class);
                            CardSuccessActivity.this.startActivity(mainIntent);
                            CardSuccessActivity.this.finish();
                        } else {
                            Intent i = new Intent(CardSuccessActivity.this, BuyTokenActivity.class);
                            i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                            i.putExtra("type", getIntent().getStringExtra("type"));
                            startActivity(i);
                            finish();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 3000);

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
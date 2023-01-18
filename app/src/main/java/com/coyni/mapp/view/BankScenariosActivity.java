package com.coyni.mapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.Utils;

public class BankScenariosActivity extends AppCompatActivity {

    private ImageView ivBack, failedIV;
    private TextView tvHeading, tvMessage, tvButton;
    private CardView cvTryAgain;
    private Long mLastClickTime = 0L;
    private String value = "", button = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_failed);

        if (getIntent() != null)
            value = getIntent().getStringExtra(Utils.DATA);

        initView();
    }

    private void initView() {

        ivBack = findViewById(R.id.ivBack);
        failedIV = findViewById(R.id.failedIV);
        tvHeading = findViewById(R.id.tvAFailed);
        tvMessage = findViewById(R.id.tvMessage);
        cvTryAgain = findViewById(R.id.cvTryAgain);
        tvButton = findViewById(R.id.tvButton);

        ivBack.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(view -> {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000)
                return;
            mLastClickTime = SystemClock.elapsedRealtime();
            onBackPressed();
        });

        cvTryAgain.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000)
                return;
            mLastClickTime = SystemClock.elapsedRealtime();
            if (button.equalsIgnoreCase(getString(R.string.done))) {
                startActivity(new Intent(getApplicationContext(), PaymentMethodsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                onBackPressed();
            }
        });

        switch (value) {

            case "Canceled" :
                failedIV.setImageDrawable(getDrawable(R.drawable.ic_failed));
                tvHeading.setText("Session Canceled");
                tvMessage.setText("Your attempt to add a bank account has been canceled. If this is an error, please try again.");
                break;

            case "Unexpected Error" :
                failedIV.setImageDrawable(getDrawable(R.drawable.ic_caution_exclaimation_mark__icon));
                tvHeading.setText("Unexpected Error");
                tvMessage.setText("Due to an unexpected error, MX was unable to authenticate your bank account. Please try again later.");
                break;

            case "Authentication Failed" :
                failedIV.setImageDrawable(getDrawable(R.drawable.ic_failed));
                tvHeading.setText(getString(R.string.authorization_failed));
                tvMessage.setText("MX was unable to authenticate your bank account. If this is an error, please try again.");
                break;

            case "Account Type Error" :
                failedIV.setImageDrawable(getDrawable(R.drawable.ic_caution_exclaimation_mark__icon));
                tvHeading.setText("Account Type Error");
                tvMessage.setText("It looks like you tried to add a non-checking account. Please try again and add a checking account instead.");
                break;

            case "Success" :
                failedIV.setImageDrawable(getDrawable(R.drawable.ic_success_icon));
                tvHeading.setText("Bank Account(s) Added");
                tvMessage.setText("Your bank account(s) has been successfully authorized and added to your payment methods.");
                tvButton.setText(getString(R.string.done));
                button = getString(R.string.done);
                ivBack.setVisibility(View.GONE);
                break;

        }
    }
}
package com.coyni.mapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.Utils;

public class AddBankMXActivity extends BaseActivity {

    private ImageView ivBack;
    private CheckBox checkbox;
    private CardView cvStart;
    private Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_mx);

        initView();
    }

    private void initView() {

        ivBack = findViewById(R.id.ivBack);
        checkbox = findViewById(R.id.checkbox);
        cvStart = findViewById(R.id.cvStart);

        cvStart.setEnabled(false);

        ivBack.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            onBackPressed();
        });

        cvStart.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), BankScenariosActivity.class)
                    .putExtra(Utils.DATA, "Success"));
        });

        checkbox.setOnClickListener(view -> {
            if (checkbox.isChecked()) {
                cvStart.setEnabled(true);
                cvStart.setCardBackgroundColor(getColor(R.color.primary_green));
            } else {
                cvStart.setEnabled(false);
                cvStart.setCardBackgroundColor(getColor(R.color.inactive_color));
            }
        });

    }
}
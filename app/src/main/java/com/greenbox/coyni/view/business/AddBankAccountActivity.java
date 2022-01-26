package com.greenbox.coyni.view.business;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.greenbox.coyni.R;

public class AddBankAccountActivity extends AppCompatActivity {
    ImageView closeButton;
    TextView learnmoreTV;
    CardView imReday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);

        closeButton = findViewById(R.id.closeIV);
        learnmoreTV = findViewById(R.id.tvLearnMore);
        imReday = findViewById(R.id.cvNext);

        initMethod();

    }

    private void initMethod() {

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        learnmoreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog((AddBankAccountActivity.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.activity_learn_more_addbankac);
                Window window = dialog.getWindow();
//                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl=window.getAttributes();
                wl.gravity= Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        imReday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBankAccountActivity.this, BankAccoutAddedSuccessfullyActivity.class);
                startActivity(intent);
            }
        });
    }
}
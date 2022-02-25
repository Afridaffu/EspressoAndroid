package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenbox.coyni.R;

public class RefundTransactionActivity extends AppCompatActivity {

    private ImageView refundBackIV;
    private TextView etremarksTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_transaction);

        etremarksTV = findViewById(R.id.eTremarks);
        etremarksTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog((RefundTransactionActivity.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.add_note_layout);
                Window window = dialog.getWindow();
//                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.45);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.gravity = Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }
        });

        refundBackIV = findViewById(R.id.RefundbackIV);
        refundBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
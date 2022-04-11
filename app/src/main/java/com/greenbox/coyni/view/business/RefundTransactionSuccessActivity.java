package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.greenbox.coyni.R;

public class RefundTransactionSuccessActivity extends AppCompatActivity {

    private TextView tvrAmount,tvrReferenceID,tvrMessage;
    private CardView cvrDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_transaction_success);

        tvrAmount = findViewById(R.id.tvRAmount);
        tvrReferenceID = findViewById(R.id.tvRReferenceID);
        tvrMessage = findViewById(R.id.tvRMessage);

        cvrDone = findViewById(R.id.cvRDone);
        cvrDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RefundTransactionSuccessActivity.this, MerchantTransactionListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        tvrMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(RefundTransactionSuccessActivity.this, MerchantTransactionDetailsActivity.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")){
            tvrAmount.setText(getIntent().getStringExtra("amount"));
        }
        else {
            tvrAmount.setText("");
        }

        if (getIntent().getStringExtra("gbxTransID") != null && !getIntent().getStringExtra("gbxTransID").equals("")){
            String refID = getIntent().getStringExtra("gbxTransID");
            if (refID.length() > 15){
                tvrReferenceID.setText(refID.substring(0,10)+"...");
            }
            else {
                tvrReferenceID.setText(refID);
            }

//            tvrReferenceID.setText(getIntent().getStringExtra("gbxTransID"));
        }
        else {
            tvrReferenceID.setText("");
        }

    }

}
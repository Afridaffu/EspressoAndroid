package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class RefundTransactionSuccessActivity extends AppCompatActivity {

    private TextView tvrAmount,tvrReferenceID,tvrMessage;
    private CardView cvrDone;
    private LinearLayout layoutRReferenceLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_transaction_success);

        tvrAmount = findViewById(R.id.tvRAmount);
        tvrReferenceID = findViewById(R.id.tvRReferenceID);
        tvrMessage = findViewById(R.id.tvRMessage);
        layoutRReferenceLL = findViewById(R.id.layoutRReference);

        cvrDone = findViewById(R.id.cvRDone);
        cvrDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(RefundTransactionSuccessActivity.this, MerchantTransactionListActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tvrMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(RefundTransactionSuccessActivity.this,MerchantTransactionDetailsActivity.class)
                    .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID,getIntent().getStringExtra(Utils.gbxTransID))
                    .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_TYPE,Utils.refundtxntype));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (getIntent().getStringExtra(Utils.amount) != null && !getIntent().getStringExtra(Utils.amount).equals("")){
                tvrAmount.setText(getIntent().getStringExtra(Utils.amount));
            }
            else {
                tvrAmount.setText("");
            }

            if (getIntent().getStringExtra(Utils.gbxTransID) != null && !getIntent().getStringExtra(Utils.gbxTransID).equals("")){
                String refID = getIntent().getStringExtra(Utils.gbxTransID);
                if (refID.length() > 30){
                    tvrReferenceID.setText(refID.substring(0,15)+"...");
                }
                else {
                    tvrReferenceID.setText(refID);
                }
                layoutRReferenceLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.copyText(refID,RefundTransactionSuccessActivity.this);
                    }
                });

    //            tvrReferenceID.setText(getIntent().getStringExtra("gbxTransID"));
            }
            else {
                tvrReferenceID.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
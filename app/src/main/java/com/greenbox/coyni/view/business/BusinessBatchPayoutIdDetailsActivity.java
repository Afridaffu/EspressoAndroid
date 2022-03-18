package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionDetailsActivity;

public class BusinessBatchPayoutIdDetailsActivity extends AppCompatActivity {

    LinearLayout payoytBackLL,payoutRefIdLL,payoutTokenNoLL,payoutReserveIdLL;
    TextView payoutRefIdTV,payoutTokenIdTV,ReserveIdTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_id_details);
        initFields();

    }

    private void initFields() {
        payoytBackLL = findViewById(R.id.payoytBackLL);

        payoutRefIdLL = findViewById(R.id.payoutRefIdLL);
        payoutTokenNoLL = findViewById(R.id.payoutTokenNoLL);
        payoutReserveIdLL = findViewById(R.id.payoutReserveIdLL);

        payoutRefIdTV = findViewById(R.id.payoutRefIdTV);
        payoutTokenIdTV = findViewById(R.id.payoutTokenIdTV);
        ReserveIdTV = findViewById(R.id.ReserveIdTV);



        payoytBackLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        payoutRefIdLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.copyText(objData.getReferenceId(), BusinessBatchPayoutIdDetailsActivity.this);
            }
        });

        payoutTokenNoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.copyText(objData.getTokenId(), BusinessBatchPayoutIdDetailsActivity.this);
            }
        });
        payoutReserveIdLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            Utils.copyText(objData.getReserveId(), BusinessBatchPayoutIdDetailsActivity.this);
            }
        });

    }
}
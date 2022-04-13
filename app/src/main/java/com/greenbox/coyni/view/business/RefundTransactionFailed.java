package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class RefundTransactionFailed extends BaseActivity {

    private CardView cvtryAgain;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_token_transaction_failed);

        cvtryAgain = findViewById(R.id.cvTryAgain);

        cvtryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RefundTransactionFailed.this, RefundTransactionActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }
}

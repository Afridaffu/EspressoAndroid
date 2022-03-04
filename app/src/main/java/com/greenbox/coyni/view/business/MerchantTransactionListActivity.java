package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.MerchantTransactionsFilterDialog;
import com.greenbox.coyni.view.BaseActivity;

public class MerchantTransactionListActivity extends BaseActivity {

    private ImageView filterIconIV;
    private ImageView closeBtnIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_transactions_list);

        initFields();
    }

    public void showFiltersPopup() {
        MerchantTransactionsFilterDialog dialog = new MerchantTransactionsFilterDialog(MerchantTransactionListActivity.this);
        dialog.show();
    }

    private void initFields() {
        filterIconIV = findViewById(R.id.filtericonIV);
        closeBtnIV = findViewById(R.id.closeBtnIV);
        filterIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersPopup();
            }
        });
        closeBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
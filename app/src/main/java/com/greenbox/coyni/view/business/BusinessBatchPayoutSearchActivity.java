package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BatchPayoutListAdapter;
import com.greenbox.coyni.dialogs.PayoutTransactionsDetailsFiltersDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutList;
import com.greenbox.coyni.view.BaseActivity;

import java.util.Date;

public class BusinessBatchPayoutSearchActivity extends BaseActivity {

    ImageView filterIconIV, datePickIV;
    TextView applyFilterBtnCV;
    EditText filterdatePickET;
    LinearLayout dateRangePickerLL;
    Date startDateD = null;
    Date endDateD = null;
    RecyclerView recyclerViewPayouts;
    BatchPayoutList[] payoutList = new BatchPayoutList[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_search);
        initFields();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPayout);
        BatchPayoutListAdapter payoutListAdapter = new BatchPayoutListAdapter(payoutList);
//        recyclerViewPayouts.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(payoutListAdapter);
    }

    private void initFields() {
        filterIconIV = findViewById(R.id.filterIconIV);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        filterdatePickET = findViewById(R.id.filterdatePickET);
        dateRangePickerLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        recyclerViewPayouts = findViewById(R.id.recyclerViewPayout);


        filterIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersPopup();
            }
        });
    }

    public void showFiltersPopup() {
        PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this);
        dialog.show();
    }



}
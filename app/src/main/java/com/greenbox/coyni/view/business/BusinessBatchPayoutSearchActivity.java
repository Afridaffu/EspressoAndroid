package com.greenbox.coyni.view.business;

import android.content.Context;
import android.content.Intent;
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
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.dialogs.DateRangePickerDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.PayoutTransactionsDetailsFiltersDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutList;
import com.greenbox.coyni.utils.LogUtils;
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

        BatchPayoutListAdapter payoutListAdapter = new BatchPayoutListAdapter(payoutList);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPayout);
//        recyclerViewPayouts.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(payoutListAdapter);

        payoutListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.v(TAG, "Position is " + position);
                Intent i = new Intent(BusinessBatchPayoutSearchActivity.this, BusinessBatchPayoutIdDetailsActivity.class);
                startActivity(i);
            }
        });

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
                showFiltersPopup(null);
            }
        });
    }

    private void showFiltersPopup(String dateSelected) {
        PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this, dateSelected);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if(action.equals("Date_PICK_SELECTED")) {
                    showCalendarDialog();
                } else if(action.equals("Date_SELECTED")) {
                    LogUtils.v(TAG, "Date Selected " + value);
                }
            }
        });
        dialog.show();
    }


    private void showCalendarDialog() {
        DateRangePickerDialog dialog = new DateRangePickerDialog(BusinessBatchPayoutSearchActivity.this);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if(action.equals("Done")) {
                    showFiltersPopup(value+"");
                }
            }
        });
        dialog.show();
    }

}
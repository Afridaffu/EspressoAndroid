package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    ImageView filterIconIV, datePickIV,closeBtnIV;
    TextView applyFilterBtnCV;
    EditText filterdatePickET;
    LinearLayout dateRangePickerLL;
    SwipeRefreshLayout refreshpageSL;
    Date startDateD = null;
    Date endDateD = null;
    RecyclerView recyclerViewPayouts;
    BatchPayoutList[] payoutList = new BatchPayoutList[]{};
    private BusinessBatchPayoutSearchActivity businessBatchPayoutSearchActivity;

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
            public void onItemClick(int position, Object obj) {
                LogUtils.v(TAG, "Position is " + position);
                Intent i = new Intent(BusinessBatchPayoutSearchActivity.this, BusinessBatchPayoutIdDetailsActivity.class);
                startActivity(i);
            }
        });

    }

    private void initFields() {
        closeBtnIV = findViewById(R.id.closeBtnIV);
        filterIconIV = findViewById(R.id.filterIconIV);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        filterdatePickET = findViewById(R.id.filterdatePickET);
        dateRangePickerLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        recyclerViewPayouts = findViewById(R.id.recyclerViewPayout);
        refreshpageSL = findViewById(R.id.refreshpageSL);

        closeBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        filterIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersPopup();
            }
        });


        refreshpageSL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    filterIconIV.setVisibility(View.GONE);
                    filterIconIV.setImageResource(R.drawable.ic_filtericon);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showFiltersPopup() {
        PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if(action.equals("Date_SELECTED")) {
                    LogUtils.v(TAG, "Date Selected " + value);
                    filterIconIV.setImageResource(R.drawable.ic_filter_enabled);
                }
            }
        });
        dialog.show();
    }


//    private void showCalendarDialog() {
//        DateRangePickerDialog dialog = new DateRangePickerDialog(BusinessBatchPayoutSearchActivity.this);
//        dialog.setOnDialogClickListener(new OnDialogClickListener() {
//            @Override
//            public void onDialogClicked(String action, Object value) {
//                if(action.equals("Done")) {
//                    showFiltersPopup(value+"");
//                }
//            }
//        });
//        dialog.show();
//    }

}
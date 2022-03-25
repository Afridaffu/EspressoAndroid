package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BatchPayoutListAdapter;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.PayoutTransactionsDetailsFiltersDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutRequest;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusinessBatchPayoutSearchActivity extends BaseActivity implements TextWatcher {

    ImageView filterIconIV, datePickIV, closeBtnIV;
    TextView applyFilterBtnCV;
    EditText filterdatePickET, searchET;
    LinearLayout dateRangePickerLL;
    SwipeRefreshLayout refreshpageSL;
    Date startDateD = null;
    Date endDateD = null;
    RecyclerView recyclerViewPayouts;
    private List<BatchPayoutListItems> payoutList = new ArrayList<>();
    private BusinessBatchPayoutSearchActivity businessBatchPayoutSearchActivity;
    private MyApplication objMyApplication;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_search);
        initFields();
        initObserver();

        BatchPayoutListAdapter payoutListAdapter = new BatchPayoutListAdapter(BusinessBatchPayoutSearchActivity.this, payoutList);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPayout);
//       recyclerViewPayouts.setHasFixedSize(true);
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
        searchET = findViewById(R.id.payoutSearchET);

        recyclerViewPayouts.setLayoutManager(new LinearLayoutManager(this));

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        businessDashboardViewModel.getPayoutListData();

        searchET.addTextChangedListener(this);


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
                if (action.equals("Date_SELECTED")) {
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


    private void initObserver() {
        try {
            businessDashboardViewModel.getBatchPayoutListMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    dismissDialog();
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                batchPayoutListAdapter = new BatchPayoutListAdapter(BusinessBatchPayoutSearchActivity.this, payoutList);
                                recyclerViewPayouts.setAdapter(batchPayoutListAdapter);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutSearchActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void payoutAPI() {
        showProgressDialog();
        businessDashboardViewModel.getPayoutListData();
    }

    private void payoutAPI(String search) {
        showProgressDialog();
        businessDashboardViewModel.getPayoutlistData(search);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() >= 14) {
            payoutList.clear();
            BatchPayoutRequest batchPayoutRequest = new BatchPayoutRequest();
            batchPayoutRequest.setBatchId(charSequence.toString());
            payoutAPI(charSequence.toString());
        } else if (charSequence.length() > 0 && charSequence.length() < 30) {
        } else if (charSequence.toString().trim().length() == 0) {
            payoutList.clear();
            businessDashboardViewModel.getPayoutListData();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == searchET.getEditableText()) {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
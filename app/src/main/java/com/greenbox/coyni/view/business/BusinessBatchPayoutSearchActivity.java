package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
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
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutDetailsRequest;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListData;
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

public class BusinessBatchPayoutSearchActivity extends BaseActivity {

    ImageView filterIconIV, datePickIV, closeBtnIV;
    TextView applyFilterBtnCV, noPayoutTransactions, noMorePayoutTransactions, payoutLoadMoreTV;
    private ProgressBar payoutProgressBarLoadMore;
    EditText filterdatePickET, searchET;
    LinearLayout dateRangePickerLL;
    SwipeRefreshLayout refreshpageSL;
    private int totalItemCount, currentPage = 1, total = 0;
    Date startDateD = null;
    Date endDateD = null;
    RecyclerView recyclerViewPayouts;
    private List<BatchPayoutListItems> payoutList = new ArrayList<>();
    private BusinessBatchPayoutSearchActivity businessBatchPayoutSearchActivity;
    private MyApplication objMyApplication;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private DashboardViewModel dashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;
    MerchantTransactionListActivity merchantTransactionListActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_search);
        initFields();
        initObserver();

        BatchPayoutListAdapter payoutListAdapter = new BatchPayoutListAdapter(BusinessBatchPayoutSearchActivity.this, payoutList);
        RecyclerView recyclerView = findViewById(R.id.payoutRecyclerView);
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
        recyclerViewPayouts = findViewById(R.id.payoutRecyclerView);
        refreshpageSL = findViewById(R.id.payoutRefreshLayout);
        searchET = findViewById(R.id.payoutSearchET);
        noPayoutTransactions = findViewById(R.id.payoutNoTransactions);
        noMorePayoutTransactions = findViewById(R.id.payoutNoMoreTransactions);
        payoutProgressBarLoadMore = findViewById(R.id.payoutProgressBarLoadMore);
        payoutLoadMoreTV = findViewById(R.id.payoutLoadMoreTV);

        recyclerViewPayouts.setLayoutManager(new LinearLayoutManager(this));

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        BatchPayoutRequest batchPayoutRequest = null;
        businessDashboardViewModel.getPayoutListData();

//        searchET.addTextChangedListener(this);


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

        refreshpageSL.setColorSchemeColors(getResources().getColor(R.color.primary_green));

        refreshpageSL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    noMorePayoutTransactions.setVisibility(View.GONE);

                    filterIconIV.setVisibility(View.GONE);
                    filterIconIV.setImageResource(R.drawable.ic_filtericon);

                    BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
                    batchPayoutListData.setCurrentPageNo(String.valueOf(currentPage));
                    batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));


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

    private void initObserver() {
        try {
            businessDashboardViewModel.getBatchPayoutListMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                batchPayoutListAdapter = new BatchPayoutListAdapter(BusinessBatchPayoutSearchActivity.this, payoutList);
                                batchPayoutListAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, Object obj) {
                                        LogUtils.d(TAG,"onitemclick"+position +obj.toString());

                                        BatchPayoutListItems batchPayoutListItem = (BatchPayoutListItems) obj;
                                        showBatchPayoutDetails(batchPayoutListItem);
//                                        businessDashboardViewModel.batchPayoutIdDetails();
                                        //Navigate to intent
                                    }

                                });
                                recyclerViewPayouts.setAdapter(batchPayoutListAdapter);
                            }
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutSearchActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showBatchPayoutDetails(BatchPayoutListItems items) {
        try {
            Intent i = new Intent(this, BusinessBatchPayoutIdDetailsActivity.class);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void loadData() {
        try {
            currentPage = 0;
            noMorePayoutTransactions.setVisibility(View.GONE);
            BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
            batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
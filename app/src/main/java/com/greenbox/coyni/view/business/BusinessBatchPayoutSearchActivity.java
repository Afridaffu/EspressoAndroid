package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.PayoutTransactionsDetailsFiltersDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListData;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.model.reservemanual.RollingSearchRequest;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BusinessBatchPayoutSearchActivity extends BaseActivity implements TextWatcher {

    ImageView filterIconIV, datePickIV, closeBtnIV;
    TextView applyFilterBtnCV, noPayoutTransactions, noMorePayoutTransactions, payoutLoadMoreTV, cynTV;
    private ProgressBar payoutProgressBarLoadMore;
    private NestedScrollView nestedScrollView;
    private Long mLastClickTime = 0L;
    EditText filterdatePickET, searchET;
    LinearLayout dateRangePickerLL;
    SwipeRefreshLayout refreshpageSL;
    private int currentPage = 1, total = 0;
    RecyclerView recyclerViewPayouts;
    private List<BatchPayoutListItems> payoutList = new ArrayList<>();
    private BusinessDashboardViewModel businessDashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;
    private ReserveFilter batchFilter = new ReserveFilter();
    private static String applyFilter = "ApplyFilter", resetFilter = "ResetFilter";
    private MyApplication objMyApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_search);
        initFields();
        initObserver();
        loadData();
    }

    private void initFields() {
        objMyApplication = (MyApplication) getApplicationContext();
        closeBtnIV = findViewById(R.id.closeBtnIV);
        filterIconIV = findViewById(R.id.filterIconIV);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        filterdatePickET = findViewById(R.id.filterdatePickET);
        dateRangePickerLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        recyclerViewPayouts = findViewById(R.id.payoutRecyclerView);
        recyclerViewPayouts.setVisibility(View.GONE);
        refreshpageSL = findViewById(R.id.payoutRefreshLayout);
        searchET = findViewById(R.id.payoutSearchET);
        noPayoutTransactions = findViewById(R.id.payoutNoTransactions);
        noMorePayoutTransactions = findViewById(R.id.payoutNoMoreTransactions);
        payoutProgressBarLoadMore = findViewById(R.id.payoutProgressBarLoadMore);
        payoutLoadMoreTV = findViewById(R.id.payoutLoadMoreTV);
        cynTV = findViewById(R.id.CynTV);
        nestedScrollView = findViewById(R.id.nestedsV);

        recyclerViewPayouts.setLayoutManager(new LinearLayoutManager(this));

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        getBatchListData();
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showFiltersPopup();
            }

        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.e("scrollY", scrollY + "  " + v.getChildAt(0).getMeasuredHeight() + " " + v.getMeasuredHeight());
                    try {
                        Log.e("total abcd", total + "");
                        Log.e("currentPage acbd", currentPage + "");
                        if (total - 1 > currentPage) {
                            payoutProgressBarLoadMore.setVisibility(View.VISIBLE);
                            payoutLoadMoreTV.setVisibility(View.VISIBLE);
                            currentPage = currentPage + 1;
                            Log.e("CurrentPage", currentPage + "");
                            BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
                            batchPayoutListData.setCurrentPageNo(String.valueOf(currentPage));
                            batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
                            dismissDialog();
                            noMorePayoutTransactions.setVisibility(View.GONE);
                        } else {
                            noMorePayoutTransactions.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        refreshpageSL.setColorSchemeColors(getResources().getColor(R.color.primary_green));

        refreshpageSL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    batchFilter = new ReserveFilter();
                    searchET.setText("");
                    noMorePayoutTransactions.setVisibility(View.GONE);
                    filterIconIV.setImageResource(R.drawable.ic_filtericon);
                    getBatchListData();
                    dismissDialog();
//                    BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
//                    batchPayoutListData.setCurrentPageNo(String.valueOf(currentPage));
//                    batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
                    cynTV.setVisibility(View.VISIBLE);
//                    getBatchListData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showFiltersPopup() {
        try {
            if (batchFilter != null) {
                PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this, batchFilter);
            }
                PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this, batchFilter);
            dialog.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClicked(String action, Object value) {
                    if (action.equalsIgnoreCase(applyFilter)) {
                        searchET.setText("");
                        filterIconIV.setImageResource(R.drawable.ic_filter_enabled);
                        batchFilter = (ReserveFilter) value;
                        getBatchListData();
    //                    rangeDates = (RangeDates) value;
    //                    if (rangeDates == null) {
    ////                        recyclerViewPayouts.setVisibility(View.GONE);
    //                    } else {
    //                        String fromDate = Utils.formatDate(rangeDates.getUpdatedFromDate());
    //                        String toDate = Utils.formatDate(rangeDates.getUpdatedToDate());
    //                    }
                    } else if (action.equalsIgnoreCase(resetFilter)) {
                        batchFilter = (ReserveFilter) value;
                        searchET.setText("");
                        filterIconIV.setImageResource(R.drawable.ic_filtericon);
                        getBatchListData();
                        dismissDialog();

                    }
                    payoutList.clear();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initObserver() {
        try {
            businessDashboardViewModel.getRollingListResponseMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    dismissDialog();
                    refreshpageSL.setRefreshing(false);
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            payoutProgressBarLoadMore.setVisibility(View.GONE);
                            payoutLoadMoreTV.setVisibility(View.GONE);
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                batchPayoutListAdapter = new BatchPayoutListAdapter(BusinessBatchPayoutSearchActivity.this, payoutList);
                                batchPayoutListAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, Object obj) {
                                        LogUtils.d(TAG, "onitemclick" + position + obj.toString());

                                        BatchPayoutListItems batchPayoutListItem = (BatchPayoutListItems) obj;
                                        showBatchPayoutDetails(batchPayoutListItem);

                                    }

                                });
                                if (payoutList.size() > 0) {
                                    recyclerViewPayouts.setAdapter(batchPayoutListAdapter);
                                    recyclerViewPayouts.setVisibility(View.VISIBLE);
                                    noMorePayoutTransactions.setVisibility(View.GONE);
                                    cynTV.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerViewPayouts.setVisibility(View.GONE);
                                    noPayoutTransactions.setVisibility(View.VISIBLE);
                                    cynTV.setVisibility(View.GONE);
                                }
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

    private void payoutAPI(String search) {
        showProgressDialog();
        RollingSearchRequest keyRequest = new RollingSearchRequest();
        keyRequest.setSearchKey(search);
        keyRequest.setPayoutType(Utils.batchNow);
        businessDashboardViewModel.getRollingListData(keyRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 30) {
            payoutList.clear();
            payoutAPI(charSequence.toString());
            dismissDialog();
            cynTV.setVisibility(View.VISIBLE);
            noPayoutTransactions.setVisibility(View.GONE);
        } else if (charSequence.length() > 0 && charSequence.length() < 30) {
            noPayoutTransactions.setVisibility(View.VISIBLE);
            cynTV.setVisibility(View.GONE);
            recyclerViewPayouts.setVisibility(View.GONE);
        } else if (charSequence.toString().trim().length() == 0) {
            payoutList.clear();
            noPayoutTransactions.setVisibility(View.GONE);
            recyclerViewPayouts.setVisibility(View.VISIBLE);
//            businessDashboardViewModel.getPayoutListData(li);
            getBatchListData();
            dismissDialog();
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

    private void showBatchPayoutDetails(BatchPayoutListItems items) {
        try {
            Intent i = new Intent(this, BusinessBatchPayoutIdDetailsActivity.class);
            i.putExtra(Utils.SELECTED_BATCH_PAYOUT, items);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getBatchListData() {
        showProgressDialog();
        RollingListRequest listRequest = new RollingListRequest();
        listRequest.setPayoutType(Utils.batchNow);
        ArrayList<Integer> status = new ArrayList<>();
        status.add(Utils.paid);
        status.add(Utils.payoutInProgress);
        //status.add(4);
        listRequest.setStatus(status);
        if (batchFilter != null && batchFilter.isFilterApplied) {
            if (batchFilter.getUpdatedToDate() != null && batchFilter.getUpdatedFromDate() != null) {
                if (!batchFilter.getUpdatedToDate().isEmpty() && !batchFilter.getUpdatedFromDate().isEmpty()) {

                    String strF = batchFilter.getUpdatedFromDate();
                    String strL = batchFilter.getUpdatedToDate();
                    String strFromDate = Utils.payoutDate(strF);
                    String strToDate = Utils.payoutDate(strL);

                        listRequest.setFromDate(strFromDate);
                        listRequest.setToDate(strToDate);


                }
            }
        }
        businessDashboardViewModel.getRollingListData(listRequest);
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
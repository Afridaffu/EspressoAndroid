package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
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
import com.greenbox.coyni.utils.MatomoConstants;
import com.greenbox.coyni.utils.MatomoUtility;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessBatchPayoutSearchActivity extends BaseActivity implements TextWatcher {

    ImageView filterIconIV, datePickIV, closeBtnIV;
    TextView applyFilterBtnCV, noPayoutTransactions, noMorePayoutTransactions, payoutLoadMoreTV, cynTV;
    private ProgressBar payoutProgressBarLoadMore;
    private NestedScrollView nestedScrollView;
    private Long mLastClickTime = 0L;
    private EditText searchET;
    private SwipeRefreshLayout refreshpageSL;
    private int currentPage = 1, total = 0;
    private RecyclerView recyclerView;
    private CardView recyclerViewPayouts;
    private MyApplication objMyApplication;
    private List<BatchPayoutListItems> payoutList = new ArrayList<>();
    private BusinessDashboardViewModel businessDashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;
    private ReserveFilter batchFilter = new ReserveFilter();
    private static final String applyFilter = "ApplyFilter", resetFilter = "ResetFilter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_search);
        MatomoUtility.getInstance().trackScreen(MatomoConstants.BATCH_PAYOUT_SCREEN);
        initFields();
        initObserver();
        loadData();
    }

    private void initFields() {

        objMyApplication = (MyApplication) getApplicationContext();
        closeBtnIV = findViewById(R.id.closeBtnIV);
        filterIconIV = findViewById(R.id.filterIconIV);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        datePickIV = findViewById(R.id.datePickIV);
        recyclerView = findViewById(R.id.payoutRecyclerView);
        recyclerViewPayouts = findViewById(R.id.payoutListCV);
        recyclerViewPayouts.setVisibility(View.GONE);
        refreshpageSL = findViewById(R.id.payoutRefreshLayout);
        searchET = findViewById(R.id.payoutSearchET);
        noPayoutTransactions = findViewById(R.id.payoutNoTransactions);
        noMorePayoutTransactions = findViewById(R.id.payoutNoMoreTransactions);
        payoutProgressBarLoadMore = findViewById(R.id.payoutProgressBarLoadMore);
        payoutLoadMoreTV = findViewById(R.id.payoutLoadMoreTV);
        cynTV = findViewById(R.id.CynTV);
        nestedScrollView = findViewById(R.id.nestedsV);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        getBatchListData();
        searchET.addTextChangedListener(this);

        closeBtnIV.setOnClickListener(view -> onBackPressed());

        filterIconIV.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            showFiltersPopup();
        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    try {
                        if (total - 1 > currentPage) {
                            payoutProgressBarLoadMore.setVisibility(View.VISIBLE);
                            payoutLoadMoreTV.setVisibility(View.VISIBLE);
                            currentPage = currentPage + 1;
                            BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
                            batchPayoutListData.setCurrentPageNo(String.valueOf(currentPage));
                            batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
                            dismissDialog();
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
                    noMorePayoutTransactions.setVisibility(View.VISIBLE);
                    batchFilter = new ReserveFilter();
                    searchET.setText("");
                    filterIconIV.setImageResource(R.drawable.ic_filtericon);
                    getBatchListData();
                    dismissDialog();
                    cynTV.setVisibility(View.VISIBLE);
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
                        MatomoUtility.getInstance().trackEvent(MatomoConstants.BATCH_PAYOUT_FILTERS, MatomoConstants.BATCH_PAYOUT_FILTERS_APPLIED);
                        searchET.setText("");
                        filterIconIV.setImageResource(R.drawable.ic_filter_enabled);
                        batchFilter = (ReserveFilter) value;
                        getBatchListData();
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
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            payoutProgressBarLoadMore.setVisibility(View.GONE);
                            payoutLoadMoreTV.setVisibility(View.GONE);
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                batchPayoutListAdapter = new BatchPayoutListAdapter(BusinessBatchPayoutSearchActivity.this, payoutList);
                                batchPayoutListAdapter.setOnItemClickListener((position, obj) -> {
                                    BatchPayoutListItems batchPayoutListItem = (BatchPayoutListItems) obj;
                                    showBatchPayoutDetails(batchPayoutListItem);
                                });
                                if (payoutList.size() > 0) {
                                    recyclerView.setAdapter(batchPayoutListAdapter);
                                    cynTV.setVisibility(View.VISIBLE);
                                    recyclerViewPayouts.setVisibility(View.VISIBLE);
                                    noMorePayoutTransactions.setVisibility(View.VISIBLE);
                                    noPayoutTransactions.setVisibility(View.GONE);
                                } else {
                                    recyclerViewPayouts.setVisibility(View.GONE);
                                    noMorePayoutTransactions.setVisibility(View.GONE);
                                    noPayoutTransactions.setVisibility(View.VISIBLE);
                                    cynTV.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutSearchActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));

                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutSearchActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));
                    }
                    refreshpageSL.setRefreshing(false);
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
        if (charSequence.length() > 10) {
            payoutList.clear();
            payoutAPI(charSequence.toString());
            dismissDialog();
            noPayoutTransactions.setVisibility(View.GONE);
            noMorePayoutTransactions.setVisibility(View.GONE);
        } else if (charSequence.length() > 0 && charSequence.length() < 10) {
            noPayoutTransactions.setVisibility(View.VISIBLE);
            cynTV.setVisibility(View.GONE);
            recyclerViewPayouts.setVisibility(View.GONE);
            noMorePayoutTransactions.setVisibility(View.GONE);
        } else if (charSequence.toString().trim().length() == 0) {
            payoutList.clear();
            getBatchListData();
            dismissDialog();
            recyclerViewPayouts.setVisibility(View.VISIBLE);
            noPayoutTransactions.setVisibility(View.GONE);
            noMorePayoutTransactions.setVisibility(View.GONE);

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
        status.add(Utils.payoutFailed);
        //status.add(4);
        listRequest.setStatus(status);
        if (batchFilter != null && batchFilter.isFilterApplied) {
            if (batchFilter.getUpdatedToDate() != null && batchFilter.getUpdatedFromDate() != null) {
                if (!batchFilter.getUpdatedToDate().isEmpty() && !batchFilter.getUpdatedFromDate().isEmpty()) {

                    String strFromDate = Utils.convertPreferenceZoneToUtcDateTime(batchFilter.getUpdatedFromDate() + " 00:00:00", "MM-dd-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss", objMyApplication.getStrPreference());
                    String strToDate = Utils.convertPreferenceZoneToUtcDateTime(batchFilter.getUpdatedToDate() + " 59:59:59", "MM-dd-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss", objMyApplication.getStrPreference());
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
            BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
            batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
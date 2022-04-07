package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
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
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.ReserveReleaseManualListAdapter;
import com.greenbox.coyni.adapters.ReserveReleasesRollingAdapter;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ReserveReleaseDialog;
import com.greenbox.coyni.dialogs.ReserveReleasesFilterDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListData;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.reservemanual.ManualData;
import com.greenbox.coyni.model.reservemanual.ManualItem;
import com.greenbox.coyni.model.reservemanual.ManualListResponse;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReserveReleasesActivity extends BaseActivity {

    private ImageView ivFilterIcon;
    private LinearLayout closeBtnIV, rollingLL;
    private RecyclerView reserveRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private EditText searchET;
    private TextView changeName, noTransactions, loadMore, noMoreTransactions;
    private ProgressBar progressBarLoadMore;
    private NestedScrollView reserveNestedScroll;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private ReserveReleasesRollingAdapter reserveReleasesRollingAdapter;
    private ReserveReleaseManualListAdapter reserveReleaseManualListAdapter;
    private List<BatchPayoutListItems> payoutList = new ArrayList<>();
    private List<ManualItem> manualItems = new ArrayList<>();
    private View view;
    private ReserveFilter reserveFilter = new ReserveFilter();
    private int currentPage = 0, total = 0;
    private boolean isRolling = true;
    private static String rolling = "Rolling", applyFilter = "ApplyFilter", resetFilter = "ResetFilter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        initFields();
        initObserver();
        getReserveRollingData();
        loadData();


        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_green));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noMoreTransactions.setVisibility(View.GONE);
                if (isRolling) {
                    reserveFilter = new ReserveFilter();
                    ivFilterIcon.setImageResource(R.drawable.ic_filtericon);
                    searchET.setText("");
                    searchET.clearFocus();
                    getReserveRollingData();
                } else {
                    searchET.setText("");
                    searchET.clearFocus();
                    getManualData();
                }
            }
        });

        closeBtnIV.setOnClickListener(view -> onBackPressed());
        ivFilterIcon.setOnClickListener(v -> showFiltersPopup());

        rollingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReserveManualDialog();
            }
        });
    }

    private void loadData() {
        try {
//            if (isRolling) {
//                currentPage = 0;
//                noMoreTransactions.setVisibility(View.GONE);
//                BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
//                batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
//            } else {
//                currentPage = 0;
//                noMoreTransactions.setVisibility(View.GONE);
//                ManualData manualData = new ManualData();
//                manualData.setPageSize(Utils.pageSize);
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showReserveManualDialog() {
        ReserveReleaseDialog dialog = new ReserveReleaseDialog(ReserveReleasesActivity.this, isRolling);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(rolling)) {
                    isRolling = true;
                    ivFilterIcon.setImageResource(R.drawable.ic_filtericon);
                    ivFilterIcon.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    changeName.setText(R.string.rolling_releases);
                    reserveRecyclerView.setVisibility(View.VISIBLE);
                    searchET.setText("");
                    searchET.clearFocus();
                    dialog.dismiss();
                    getReserveRollingData();
                } else {
                    isRolling = false;
                    ivFilterIcon.setVisibility(View.GONE);
                    reserveRecyclerView.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    changeName.setText(R.string.manual_releases);
                    searchET.setText("");
                    searchET.clearFocus();
                    dialog.dismiss();
                    getManualData();
                    reserveFilter = new ReserveFilter();
                }
            }
        });
        dialog.show();
    }

    private void getManualData() {
        showProgressDialog();
        clearAdapterData();
        businessDashboardViewModel.getManualListData();
    }

    private void getReserveRollingData() {
        showProgressDialog();
        clearAdapterData();
        RollingListRequest listRequest = new RollingListRequest();
        listRequest.setPayoutType(Utils.reserveRelease);
        ArrayList<Integer> status = new ArrayList<>();
        if (reserveFilter != null && reserveFilter.isFilterApplied) {
            if (reserveFilter.isOpen()) {
                status.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatusType());
            }
            if (reserveFilter.isOnHold()) {
                status.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatusType());
            }
            if (reserveFilter.isReleased()) {
                status.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatusType());
            }
            if (reserveFilter.isCancelled()) {
                status.add(Utils.ROLLING_LIST_STATUS.CANCELED.getStatusType());
            }

            //Add dates condition here
        } else {
            status.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.CANCELED.getStatusType());

        }
        listRequest.setStatus(status);
        businessDashboardViewModel.getPayoutListData(listRequest);
    }

    private void clearAdapterData() {
        reserveReleasesRollingAdapter = new ReserveReleasesRollingAdapter(ReserveReleasesActivity.this, new ArrayList<>());
        reserveRecyclerView.setAdapter(reserveReleasesRollingAdapter);
        reserveRecyclerView.setLayoutManager(new LinearLayoutManager(ReserveReleasesActivity.this));
    }

    private void initFields() {
        closeBtnIV = findViewById(R.id.closeBtnIV);
        searchET = findViewById(R.id.searchET);
        ivFilterIcon = findViewById(R.id.ivFilterIcon);
        rollingLL = findViewById(R.id.rollingLL);
        reserveRecyclerView = findViewById(R.id.rvRollingList);
        refreshLayout = findViewById(R.id.refreshLayoutRL);
        reserveNestedScroll = findViewById(R.id.reserveNestedSV);
        progressBarLoadMore  = findViewById(R.id.progressBarLoadMoreTV);
        noTransactions = findViewById(R.id.noTransactionsTV);
        noMoreTransactions = findViewById(R.id.noMoreTransactionsTV);
        loadMore = findViewById(R.id.loadMoreTV);
        view = findViewById(R.id.viewV);
        changeName = findViewById(R.id.tvChangeName);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

        reserveNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.e("scrollY", scrollY + "  " + v.getChildAt(0).getMeasuredHeight() + " " + v.getMeasuredHeight());
                    try {
                        if (isRolling) {

                        } else {
                            Log.e("total abcd", total + "");
                            Log.e("currentPage acbd", currentPage + "");
                            if (total - 1 > currentPage) {
                                progressBarLoadMore.setVisibility(View.VISIBLE);
                                loadMore.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;
                                Log.e("CurrentPage", currentPage + "");
                                ManualData manualData = new ManualData();
                                manualData.setCurrentPageNo(currentPage);
                                manualData.setPageSize(Utils.pageSize);

                                noMoreTransactions.setVisibility(View.GONE);
                            } else {
                                noMoreTransactions.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    private void initObserver() {
        try {
            businessDashboardViewModel.getBatchPayoutListMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    dismissDialog();
                    refreshLayout.setRefreshing(false);
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                reserveReleasesRollingAdapter = new ReserveReleasesRollingAdapter(ReserveReleasesActivity.this, payoutList);
                                reserveReleasesRollingAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, Object obj) {
                                        startActivity(new Intent(getApplicationContext(), ReserveDetailsActivity.class));
                                    }

                                });
                                if (payoutList.size() > 0) {
                                    noTransactions.setVisibility(View.GONE);
                                    reserveRecyclerView.setAdapter(reserveReleasesRollingAdapter);
                                    reserveRecyclerView.setLayoutManager(new LinearLayoutManager(ReserveReleasesActivity.this));
                                } else {
                                    noTransactions.setVisibility(View.VISIBLE);
                                }

                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveReleasesActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));

                        }
                    }
                }
            });

            businessDashboardViewModel.getManualListResponseMutableLiveData().observe(this, new Observer<ManualListResponse>() {
                @Override
                public void onChanged(ManualListResponse manualListResponse) {
                    dismissDialog();
                    refreshLayout.setRefreshing(false);
                    if (manualListResponse != null) {
                        if (manualListResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (manualListResponse.getData().getItems() != null) {
                                manualItems = manualListResponse.getData().getItems();
                                reserveReleaseManualListAdapter = new ReserveReleaseManualListAdapter(ReserveReleasesActivity.this, manualItems);

                                if (manualItems.size() > 0) {
                                    noTransactions.setVisibility(View.GONE);
                                    reserveRecyclerView.setAdapter(reserveReleaseManualListAdapter);
                                    reserveRecyclerView.setLayoutManager(new LinearLayoutManager(ReserveReleasesActivity.this));
                                } else {
                                    noTransactions.setVisibility(View.VISIBLE);
                                }

                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveReleasesActivity.this, "", manualListResponse.getError().getFieldErrors().get(0));

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showFiltersPopup() {
        ReserveReleasesFilterDialog showReserveReleaseDialog = new ReserveReleasesFilterDialog(ReserveReleasesActivity.this, reserveFilter);
        showReserveReleaseDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
//                LogUtils.d(TAG, "onclickkk" + action + value);
                if (action.equalsIgnoreCase(applyFilter)) {
                    reserveFilter = (ReserveFilter) value;
                    ivFilterIcon.setImageResource(R.drawable.ic_filter_enabled);
                    getReserveRollingData();
                } else if (action.equalsIgnoreCase(resetFilter)) {
                    reserveFilter = (ReserveFilter) value;
                    ivFilterIcon.setImageResource(R.drawable.ic_filtericon);
                    getReserveRollingData();
                }
            }
        });
        showReserveReleaseDialog.show();
    }
}
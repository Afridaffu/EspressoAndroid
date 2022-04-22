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
import com.greenbox.coyni.model.SearchKeyRequest;
import com.greenbox.coyni.model.reservemanual.ManualData;
import com.greenbox.coyni.model.reservemanual.ManualItem;
import com.greenbox.coyni.model.reservemanual.ManualListResponse;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.model.reservemanual.RollingSearchRequest;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReserveReleasesActivity extends BaseActivity implements TextWatcher {

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
    private List<BatchPayoutListItems> rollingList = new ArrayList<>();
    private List<ManualItem> manualItems = new ArrayList<>();
    ArrayList<String> statusList = new ArrayList<>();
    private View view;
    private ReserveFilter reserveFilter = new ReserveFilter();
    private int currentPage = 0, total = 0;
    private boolean isRolling = true;
    private Long mLastClickTime = 0L;
    private static String rolling = "Rolling", applyFilter = "ApplyFilter", resetFilter = "ResetFilter";
    private final String date = "date", batchID = "BatchId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        initFields();
        initObserver();
        getReserveRollingData();

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
        ivFilterIcon.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            showFiltersPopup();
        });

        rollingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReserveManualDialog();
            }
        });
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
            if(!reserveFilter.isOpen() && !reserveFilter.isOnHold()
                    && !reserveFilter.isReleased() && !reserveFilter.isCancelled()) {
                status.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatusType());
                status.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatusType());
                status.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatusType());
                status.add(Utils.ROLLING_LIST_STATUS.CANCELED.getStatusType());
            } else {
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
            }
            if(reserveFilter.getUpdatedToDate() != null && reserveFilter.getUpdatedFromDate() != null) {
                if (!reserveFilter.getUpdatedToDate().isEmpty() && !reserveFilter.getUpdatedFromDate().isEmpty()) {
                    String strFromDate = Utils.formatDate(reserveFilter.getUpdatedFromDate());
                    listRequest.setFromDate(strFromDate);
                    String strToDate = Utils.formatDate(reserveFilter.getUpdatedToDate());
                   listRequest.setToDate(strToDate);
                }
            }

        } else {

            status.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatusType());
            status.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatusType());
            status.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.CANCELED.getStatusType());
        }

        listRequest.setStatus(status);
        businessDashboardViewModel.getRollingListData(listRequest);

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

        searchET.addTextChangedListener(this);

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

        reserveNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.e("scrollY", scrollY + "  " + v.getChildAt(0).getMeasuredHeight() + " " + v.getMeasuredHeight());
                    try {
                        if (isRolling) {
                            if (total - 1 > currentPage) {
                                progressBarLoadMore.setVisibility(View.VISIBLE);
                                loadMore.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;
                                Log.e("CurrentPage", currentPage + "");
                                BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
                                batchPayoutListData.setCurrentPageNo(String.valueOf(currentPage));
                                batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));

                                noMoreTransactions.setVisibility(View.GONE);
                            } else {
                                noMoreTransactions.setVisibility(View.VISIBLE);
                            }

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
            businessDashboardViewModel.getRollingListResponseMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse rollingListData) {
                    dismissDialog();
                    refreshLayout.setRefreshing(false);
                    if (rollingListData != null) {
                        if (rollingListData.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            progressBarLoadMore.setVisibility(View.GONE);
                            loadMore.setVisibility(View.GONE);
                            if (rollingListData.getData().getItems() != null) {
                                rollingList = rollingListData.getData().getItems();
                                sortingStatus();
                                reserveReleasesRollingAdapter = new ReserveReleasesRollingAdapter(ReserveReleasesActivity.this, rollingList);
                                reserveRecyclerView.setLayoutManager(new LinearLayoutManager(ReserveReleasesActivity.this));
                                reserveRecyclerView.setAdapter(reserveReleasesRollingAdapter);
                                reserveReleasesRollingAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, Object obj) {
                                        showTransactionDetails((BatchPayoutListItems) obj);
                                    }

                                });
                                if (rollingList.size() > 0) {
                                    noTransactions.setVisibility(View.GONE);
                                    reserveRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    noTransactions.setVisibility(View.VISIBLE);
                                    reserveRecyclerView.setVisibility(View.GONE);
                                    noMoreTransactions.setVisibility(View.GONE);
                                }

                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveReleasesActivity.this, "", rollingListData.getError().getFieldErrors().get(0));

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
                            progressBarLoadMore.setVisibility(View.GONE);
                            loadMore.setVisibility(View.GONE);
                            if (manualListResponse.getData().getItems() != null) {
                                manualItems = manualListResponse.getData().getItems();
                                reserveReleaseManualListAdapter = new ReserveReleaseManualListAdapter(ReserveReleasesActivity.this, manualItems);

                                if (manualItems.size() > 0) {
                                    noTransactions.setVisibility(View.GONE);
                                    noMoreTransactions.setVisibility(View.GONE);
                                    reserveRecyclerView.setVisibility(View.VISIBLE);
                                    reserveRecyclerView.setAdapter(reserveReleaseManualListAdapter);
                                    reserveRecyclerView.setLayoutManager(new LinearLayoutManager(ReserveReleasesActivity.this));
                                } else {
                                    reserveRecyclerView.setVisibility(View.GONE);
                                    noTransactions.setVisibility(View.VISIBLE);
                                    noMoreTransactions.setVisibility(View.GONE);
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

    private void sortingStatus() {
        statusList = new ArrayList<String>();
        statusList.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatus());
        statusList.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatus());
        statusList.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatus());
        statusList.add(Utils.ROLLING_LIST_STATUS.CANCELED.getStatus());
        Collections.sort(rollingList, new Comparator<BatchPayoutListItems>() {
            @Override
            public int compare(BatchPayoutListItems o1, BatchPayoutListItems o2) {
                int o1Index = statusList.indexOf(o1.getStatus());
                int o2Index = statusList.indexOf(o2.getStatus());
                if(o1Index > o2Index) {
                    return 1;
                } else if(o1Index < o2Index) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private void showTransactionDetails(BatchPayoutListItems rollingListItem) {

        try {
            Intent idDetails = new Intent(ReserveReleasesActivity.this, ReserveDetailsActivity.class);
            idDetails.putExtra(Utils.DATA, rollingListItem);
            startActivity(idDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFiltersPopup() {
        ReserveReleasesFilterDialog showReserveReleaseDialog = new ReserveReleasesFilterDialog(ReserveReleasesActivity.this, reserveFilter);
        showReserveReleaseDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                searchET.setText("");
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
        showReserveReleaseDialog.setCanceledOnTouchOutside(true);
    }
    private void manualAPI(String search) {
        SearchKeyRequest request = new SearchKeyRequest();
        request.setSearchKey(search);
        businessDashboardViewModel.getManualListData(request);
    }

    private void RollingAPI(String searchKey) {

        RollingSearchRequest keyRequest = new RollingSearchRequest();
        keyRequest.setSearchKey(searchKey);
        keyRequest.setPayoutType(Utils.reserveRelease);
        businessDashboardViewModel.getRollingListData(keyRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        int id_length = charSequence.toString().trim().length();
        if(isRolling) {
            if (id_length >= 14 && id_length<=34 ) {
                rollingList.clear();
                RollingAPI(charSequence.toString());
                noTransactions.setVisibility(View.GONE);
                noMoreTransactions.setVisibility(View.GONE);
                reserveRecyclerView.setVisibility(View.VISIBLE);
            } else if (id_length > 0 && id_length <10) {
                noTransactions.setVisibility(View.VISIBLE);
                noMoreTransactions.setVisibility(View.GONE);
                reserveRecyclerView.setVisibility(View.GONE);
            } else if (id_length == 0) {
                rollingList.clear();
                noTransactions.setVisibility(View.GONE);
                noMoreTransactions.setVisibility(View.GONE);
                reserveRecyclerView.setVisibility(View.VISIBLE);
                getReserveRollingData();
                dismissDialog();
            }

        } else {
            if (id_length >= 10 && id_length<=34 ) {
                manualItems.clear();
                manualAPI(charSequence.toString());
                noMoreTransactions.setVisibility(View.GONE);
                reserveRecyclerView.setVisibility(View.VISIBLE);
            } else if (id_length > 0 && id_length < 10) {
                noTransactions.setVisibility(View.VISIBLE);
                noMoreTransactions.setVisibility(View.GONE);
                reserveRecyclerView.setVisibility(View.GONE);
            } else if (id_length == 0) {
                manualItems.clear();
                noTransactions.setVisibility(View.GONE);
                noMoreTransactions.setVisibility(View.GONE);
                reserveRecyclerView.setVisibility(View.VISIBLE);
                getManualData();
                dismissDialog();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (editable == searchET.getEditableText()) {

        }

    }
}

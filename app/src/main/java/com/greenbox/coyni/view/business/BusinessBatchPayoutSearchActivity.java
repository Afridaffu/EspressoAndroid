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
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutRequest;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.RangeDates;
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
    TextView applyFilterBtnCV, noPayoutTransactions, noMorePayoutTransactions, payoutLoadMoreTV, cynTV;
    private ProgressBar payoutProgressBarLoadMore;
    private NestedScrollView nestedScrollView;
    private Long mLastClickTime = 0L, mLastClickTimeFilters = 0L;
    EditText filterDatePickET, searchET;
    LinearLayout dateRangePickerLL;
    SwipeRefreshLayout refreshPageSL;
    private int totalItemCount, currentPage = 1, total = 0;
    Date startDateD = null;
    Date endDateD = null;
    private String strFromDate = "", strToDate = "";
    private Boolean isFilters = false;
    private RangeDates rangeDates;
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
        loadData();

    }

    private void initFields() {
        closeBtnIV = findViewById(R.id.closeBtnIV);
        filterIconIV = findViewById(R.id.filterIconIV);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        filterDatePickET = findViewById(R.id.filterdatePickET);
        dateRangePickerLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);
        recyclerViewPayouts = findViewById(R.id.payoutRecyclerView);
        recyclerViewPayouts.setVisibility(View.GONE);
        refreshPageSL = findViewById(R.id.payoutRefreshLayout);
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

//        showProgressDialog();
//
//        RollingListRequest listRequest = new RollingListRequest();
//        listRequest.setPayoutType(Utils.batchNow);
//        businessDashboardViewModel.getPayoutListData(listRequest);

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

        refreshPageSL.setColorSchemeColors(getResources().getColor(R.color.primary_green));

        refreshPageSL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dismissDialog();
                try {
                    noMorePayoutTransactions.setVisibility(View.GONE);

                    filterIconIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
//                    filterIconIV.setImageResource(R.drawable.ic_filtericon);

                    BatchPayoutListData batchPayoutListData = new BatchPayoutListData();
                    batchPayoutListData.setCurrentPageNo(String.valueOf(currentPage));
                    batchPayoutListData.setPageSize(String.valueOf(Utils.pageSize));
                    cynTV.setVisibility(View.VISIBLE);
//                    businessDashboardViewModel.getPayoutListData(listRequest);
                    getBatchListData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshPageSL.setRefreshing(false);
                searchET.setText("");
                cynTV.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showFiltersPopup() {
        PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equals("dates")) {
                    rangeDates = (RangeDates) value;
                    if (rangeDates == null) {
//                        recyclerViewPayouts.setVisibility(View.GONE);
                    } else {
                        String fromDate = Utils.formatDate(rangeDates.getUpdatedFromDate());
                        String toDate = Utils.formatDate(rangeDates.getUpdatedToDate());
                        filterIconIV.setImageResource(R.drawable.ic_filter_enabled);
                        showProgressDialog();
                        businessDashboardViewModel.getPayoutlistdata(fromDate, toDate);
                    }
                }
                payoutList.clear();
            }
        });
        dialog.show();
    }


    private void initObserver() {
        try {
            businessDashboardViewModel.getRollingListResponseMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    dismissDialog();
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            payoutProgressBarLoadMore.setVisibility(View.GONE);
                            payoutLoadMoreTV.setVisibility(View.GONE);
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                getBatchPayoutList();
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutSearchActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));

                        }
                    }
                }
            });

            businessDashboardViewModel.getBatchPayoutListMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    dismissDialog();
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            payoutProgressBarLoadMore.setVisibility(View.GONE);
                            payoutLoadMoreTV.setVisibility(View.GONE);
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                getBatchPayoutList();
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

    private void getBatchPayoutList() {
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
                                }else {
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

//    private void payoutAPI() {
//        showProgressDialog();
//        businessDashboardViewModel.getPayoutListData();
//    }

    private void payoutAPI(String search) {
        showProgressDialog();
        businessDashboardViewModel.getPayoutlistData(search);
    }

    private void payoutAPI(String fromDate, String toDate) {
        showProgressDialog();
        businessDashboardViewModel.getPayoutlistdata(fromDate, toDate);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 12) {
            payoutList.clear();
            BatchPayoutRequest batchPayoutRequest = new BatchPayoutRequest();
            batchPayoutRequest.setBatchId(charSequence.toString());
            payoutAPI(charSequence.toString());
            cynTV.setVisibility(View.VISIBLE);
            noPayoutTransactions.setVisibility(View.GONE);
        } else if (charSequence.length() > 0 && charSequence.length() < 12) {
            noPayoutTransactions.setVisibility(View.VISIBLE);
            cynTV.setVisibility(View.GONE);
            recyclerViewPayouts.setVisibility(View.GONE);
        } else if (charSequence.toString().trim().length() == 0) {
            payoutList.clear();
            noPayoutTransactions.setVisibility(View.GONE);
            recyclerViewPayouts.setVisibility(View.VISIBLE);
//            businessDashboardViewModel.getPayoutListData(li);
            getBatchListData();
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
    private void getBatchListData(){
        showProgressDialog();
        RollingListRequest listRequest = new RollingListRequest();
        listRequest.setPayoutType(Utils.batchNow);
        ArrayList<Integer> status = new ArrayList<>();
        status.add(Utils.paid);
        //status.add(4);
        listRequest.setStatus(status);
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
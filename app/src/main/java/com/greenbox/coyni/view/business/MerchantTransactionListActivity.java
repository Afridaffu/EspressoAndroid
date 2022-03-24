package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.MerchantTransactionListPostedNewAdapter;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.dialogs.MerchantTransactionsFilterDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MerchantTransactionListActivity extends BaseActivity implements TextWatcher {
    private TransactionListPendingAdapter transactionListPendingAdapter;
    private MerchantTransactionListPostedNewAdapter transactionListPostedAdapter;
    private Long mLastClickTime = 0L, mLastClickTimeFilters = 0L;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int totalItemCount, currentPage = 1, total = 0;
    private ExpandableHeightRecyclerView rvTransactionsPending, getRvTransactionsPosted;
    private Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    private MyApplication objMyApplication;
    private LinearLayout layoutTransactionspending, layoutTransactionsposted, clearTextLL;
    private TextView noTransactionTV, noMoreTransactionTV;
    private DashboardViewModel dashboardViewModel;
    private TransactionList transactionList;
    private TextView pendingTxt, loadMoreTV;
    private ImageView closeBtn, filterIV;
    private List<TransactionListPosted> globalData = new ArrayList<>();
    private EditText searchET;
    private List<TransactionListPending> globalPending = new ArrayList<>();
    private List<TransactionListPosted> globalPosted = new ArrayList<>();

    private ArrayList<Integer> transactionType = new ArrayList<Integer>();
    private ArrayList<Integer> transactionSubType = new ArrayList<Integer>();
    private ArrayList<Integer> txnStatus = new ArrayList<Integer>();

    private String strStartAmount = "", strEndAmount = "", strFromDate = "", strToDate = "", strSelectedDate = "", tempStrSelectedDate = "";
    private long startDateLong = 0L, endDateLong = 0L, tempStartDateLong = 0L, tempEndDateLong = 0L;
    private Date startDateD = null;
    private Date endDateD = null;
    private View bottomCorners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_merchant_transactions_list);

        try {
            initFields();
            initObservers();
            loadData();

            filterIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showFilterDialog();
                }
            });

            closeBtn.setOnClickListener(view -> finish());
            searchET.addTextChangedListener(this);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        noMoreTransactionTV.setVisibility(View.GONE);

                        globalPending.clear();
                        globalPosted.clear();
                        transactionType.clear();
                        transactionSubType.clear();
                        txnStatus.clear();
                        currentPage = 0;
                        strFromDate = "";
                        strToDate = "";
                        strStartAmount = "";
                        strEndAmount = "";
                        startDateD = null;
                        endDateD = null;
                        startDateLong = 0L;
                        endDateLong = 0L;
                        strSelectedDate = "";
                        filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));

                        TransactionListRequest transactionListRequest = new TransactionListRequest();
                        transactionListRequest.setPageNo(String.valueOf(currentPage));
                        transactionListRequest.setWalletCategory(Utils.walletCategory);
                        transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

                        transactionsAPI(transactionListRequest);
                        objMyApplication.initializeTransactionSearch();
                        objMyApplication.setTransactionListSearch(transactionListRequest);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_green));

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        Log.e("scrollY", scrollY + "  " + v.getChildAt(0).getMeasuredHeight() + " " + v.getMeasuredHeight());
                        try {
                            Log.e("total abcd", total + "");
                            Log.e("currentPage acbd", currentPage + "");
                            if (total - 1 > currentPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                loadMoreTV.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;
                                Log.e("CurrentPage", currentPage + "");
                                TransactionListRequest transactionListRequest = new TransactionListRequest();
                                transactionListRequest.setPageNo(String.valueOf(currentPage));
                                transactionListRequest.setWalletCategory(Utils.walletCategory);
                                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
                                transactionsAPI(transactionListRequest);
                                objMyApplication.initializeTransactionSearch();
                                objMyApplication.setTransactionListSearch(transactionListRequest);

                                noMoreTransactionTV.setVisibility(View.GONE);
                            } else {
                                noMoreTransactionTV.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence.length() > 30) {
            globalPending.clear();
            globalPosted.clear();
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setGbxTransactionId(charSequence.toString());
            transactionsAPI(transactionListRequest);
        } else if (charSequence.length() > 0 && charSequence.length() < 30) {
            layoutTransactionsposted.setVisibility(View.GONE);
            layoutTransactionspending.setVisibility(View.GONE);
            pendingTxt.setVisibility(View.GONE);
            noTransactionTV.setVisibility(View.VISIBLE);
        } else if (charSequence.toString().trim().length() == 0) {
            globalPending.clear();
            globalPosted.clear();
            objMyApplication.getTransactionListSearch().setPageNo("0");
            transactionsAPI(objMyApplication.getTransactionListSearch());
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

    private void initFields() {
        bottomCorners=findViewById(R.id.bottom_corners);
        closeBtn = findViewById(R.id.closeBtnIV);
        filterIV = findViewById(R.id.filtericonIV);
        nestedScrollView = findViewById(R.id.nestedSV);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        progressBar = findViewById(R.id.progressBarLoadMore);
        loadMoreTV = findViewById(R.id.loadMoreTV);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        objMyApplication = (MyApplication) getApplicationContext();
        rvTransactionsPending = findViewById(R.id.transactionListPendingRV);
        getRvTransactionsPosted = findViewById(R.id.transactionListPostedRV);
        layoutTransactionspending = findViewById(R.id.layoutLLPending);
        layoutTransactionsposted = findViewById(R.id.layoutLLposted);
        noTransactionTV = findViewById(R.id.noTransactions);
        noMoreTransactionTV = findViewById(R.id.noMoreTransactions);
        pendingTxt = findViewById(R.id.pendingTV);
        searchET = findViewById(R.id.searchET);

    }

    private void initObservers() {
        dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
            @Override
            public void onChanged(TransactionList transactionList) {
                dismissDialog();
                try {
                    if (transactionList != null) {
                        if (transactionList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            progressBar.setVisibility(View.GONE);
                            loadMoreTV.setVisibility(View.GONE);
                            try {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(MerchantTransactionListActivity.this);
                                LinearLayoutManager nLayoutManager = new LinearLayoutManager(MerchantTransactionListActivity.this);

                                if (transactionList.getData() != null) {
                                    if (transactionList.getData().getItems() != null) {
                                        if (transactionList.getData().getItems().getPendingTransactions() != null) {
                                            globalPending.addAll(transactionList.getData().getItems().getPendingTransactions());
                                        }
                                        if (transactionList.getData().getItems().getPostedTransactions() != null) {
                                            globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                        }
                                    }
                                    total = transactionList.getData().getTotalPages();
                                }

                                getRvTransactionsPosted.setNestedScrollingEnabled(false);
                                rvTransactionsPending.setNestedScrollingEnabled(false);
                                rvTransactionsPending.setExpanded(true);
                                getRvTransactionsPosted.setExpanded(true);

                                if (globalPending.size() > 0 || globalPosted.size() > 0) {
                                    noTransactionTV.setVisibility(View.GONE);
                                }

                                layoutTransactionspending.setVisibility(globalPending.size() == 0 ? View.GONE : View.VISIBLE);
                                layoutTransactionsposted.setVisibility(globalPosted.size() == 0 ? View.GONE : View.VISIBLE);

                                if (globalPending.size() > 0) {
                                    transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, MerchantTransactionListActivity.this);
                                    pendingTxt.setVisibility(View.VISIBLE);
                                    rvTransactionsPending.setLayoutManager(mLayoutManager);
                                    rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
                                    rvTransactionsPending.setAdapter(transactionListPendingAdapter);
                                    if (currentPage > 0) {
                                        int myPos = globalPending.size() - transactionList.getData().getItems().getPendingTransactions().size();
                                        rvTransactionsPending.scrollToPosition(myPos);
                                    } else {
                                        rvTransactionsPending.scrollToPosition(0);
                                    }
                                } else {
                                    pendingTxt.setVisibility(View.GONE);
                                }

                                if (globalPosted.size() > 0) {
                                    transactionListPostedAdapter = new MerchantTransactionListPostedNewAdapter(globalPosted, MerchantTransactionListActivity.this);
                                    getRvTransactionsPosted.setLayoutManager(nLayoutManager);
                                    getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
                                    transactionListPostedAdapter.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position, Object obj) {
                                            LogUtils.v(TAG, "Clicked at position " + position);
                                            showTransactionDetails((TransactionListPosted) obj);
                                        }
                                    });
                                    getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);
                                    if (currentPage > 0) {
                                        int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                        getRvTransactionsPosted.scrollToPosition(myPos);
                                    } else {
                                        getRvTransactionsPosted.scrollToPosition(0);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.displayAlert(transactionList.getError().getErrorDescription(), MerchantTransactionListActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), MerchantTransactionListActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadData() {
        try {
            globalPending.clear();
            globalPosted.clear();
            transactionType.clear();
            transactionSubType.clear();
            txnStatus.clear();
            currentPage = 0;
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
            startDateD = null;
            endDateD = null;
            startDateLong = 0L;
            endDateLong = 0L;
            strSelectedDate = "";
            filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));

            noMoreTransactionTV.setVisibility(View.GONE);
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setTransactionType(getDefaultTransactionTypes());
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

            transactionsAPI(transactionListRequest);
            objMyApplication.initializeTransactionSearch();
            objMyApplication.setTransactionListSearch(transactionListRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void transactionsAPI(TransactionListRequest transactionListRequest) {
        showProgressDialog();
        dashboardViewModel.meTransactionList(transactionListRequest);
    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.saleOrder);
        transactionType.add(Utils.refund);
        transactionType.add(Utils.merchantPayout);
        transactionType.add(Utils.monthlyServiceFee);
        return transactionType;
    }

    private void showTransactionDetails(TransactionListPosted selectedTransaction) {
        Intent inDetails = new Intent(MerchantTransactionListActivity.this, MerchantTransactionDetailsActivity.class);
        inDetails.putExtra(Utils.SELECTED_MERCHANT_TRANSACTION, selectedTransaction);
        startActivity(inDetails);
    }

    private void showFilterDialog() {
        MerchantTransactionsFilterDialog filterDialog = new MerchantTransactionsFilterDialog(MerchantTransactionListActivity.this);
        filterDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(Utils.applyFilter)) {
                    globalPending.clear();
                    globalPosted.clear();
                    TransactionListRequest listRequest = (TransactionListRequest) value;
                    if (listRequest != null) {
                        isFilters = true;
                    }
                    if (isFilters) {
                        filterIV.setImageDrawable(getDrawable(R.drawable.ic_filter_enabled));
                    } else {
                        filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                    }

                    transactionsAPI(listRequest);
                } else if (action.equals("Date_SELECTED")) {
                    LogUtils.v(TAG, "Date Selected " + value);
                    filterIV.setImageResource(R.drawable.ic_filter_enabled);
                }
            }
        });
        filterDialog.show();
    }
}
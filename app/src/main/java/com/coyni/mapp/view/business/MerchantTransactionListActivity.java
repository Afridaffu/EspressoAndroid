package com.coyni.mapp.view.business;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.MerchantTransactionListPostedNewAdapter;
import com.coyni.mapp.adapters.OnItemClickListener;
import com.coyni.mapp.adapters.TransactionListPendingAdapter;
import com.coyni.mapp.dialogs.MerchantTransactionsFilterDialog;
import com.coyni.mapp.dialogs.OnDialogClickListener;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.transaction.TransactionList;
import com.coyni.mapp.model.transaction.TransactionListPending;
import com.coyni.mapp.model.transaction.TransactionListPosted;
import com.coyni.mapp.model.transaction.TransactionListRequest;
import com.coyni.mapp.utils.ExpandableHeightRecyclerView;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MatomoConstants;
import com.coyni.mapp.utils.MatomoUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class MerchantTransactionListActivity extends BaseActivity implements TextWatcher, OnKeyboardVisibilityListener {
    private TransactionListPendingAdapter transactionListPendingAdapter;
    private MerchantTransactionListPostedNewAdapter transactionListPostedAdapter;
    private Long mLastClickTime = 0L;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int currentPage = 1, total = 0;
    private ExpandableHeightRecyclerView rvTransactionsPending, getRvTransactionsPosted;
    private Boolean isFilters = false;
    private MyApplication objMyApplication;
    private LinearLayout layoutTransactionsPending, layoutTransactionsPosted, clearTextLL;
    private TextView noTransactionTV, noMoreTransactionTV;
    private DashboardViewModel dashboardViewModel;
    private TextView pendingTxt, loadMoreTV;
    private ImageView closeBtn, filterIV;
    private EditText searchET;
    private List<TransactionListPending> globalPending = new ArrayList<>();
    private List<TransactionListPosted> globalPosted = new ArrayList<>();
    private View bottomCorners;
    private TransactionListRequest filterTransactionList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_merchant_transactions_list);
        MatomoUtility.getInstance().trackScreen(MatomoConstants.MERCHANT_TRANSACTIONS_SCREEN);

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
                        searchET.clearFocus();
                        currentPage = 0;
                        filterTransactionList = null;
                        filterIV.setImageResource(R.drawable.ic_filtericon);
//                        loadData();
//                        TransactionListRequest transactionListRequest = new TransactionListRequest();
//                        transactionListRequest.setMerchantTransactions(true);
////                        transactionListRequest.setTransactionType(getDefaultTransactionTypes());
//                        transactionListRequest.setPageNo(String.valueOf(currentPage));
//                        transactionListRequest.setWalletCategory(Utils.walletCategory);
//                        transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

//                        getTransactions(transactionListRequest);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    searchET.setText("");
                }
            });

            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_green));

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        try {
                            if (total - 1 > currentPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                loadMoreTV.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;
                                TransactionListRequest transactionListRequest = new TransactionListRequest();
                                transactionListRequest.setMerchantTransactions(true);
                                transactionListRequest.setPageNo(String.valueOf(currentPage));
                                transactionListRequest.setWalletCategory(Utils.walletCategory);
//                                transactionListRequest.setTransactionType(getDefaultTransactionTypes());
                                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
                                getTransactions(transactionListRequest);
                                dismissDialog();
                                noMoreTransactionTV.setVisibility(View.GONE);
                                noTransactionTV.setVisibility(View.GONE);
                            } else {
                                noTransactionTV.setVisibility(View.GONE);
//                                noMoreTransactionTV.setVisibility(View.VISIBLE);
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
            transactionListRequest.setMerchantTransactions(true);
            transactionListRequest.setWalletCategory(Utils.walletCategory);
//            transactionListRequest.setTransactionType(getDefaultTransactionTypes());
            transactionListRequest.setGbxTransactionId(charSequence.toString());
            getTransactions(transactionListRequest);
            dismissDialog();
        } else if (charSequence.length() > 0 && charSequence.length() < 30) {
            layoutTransactionsPosted.setVisibility(View.GONE);
            layoutTransactionsPending.setVisibility(View.GONE);
            pendingTxt.setVisibility(View.GONE);
            noTransactionTV.setVisibility(View.VISIBLE);
        } else if (charSequence.toString().trim().length() == 0) {
            if (filterTransactionList != null) {
                globalPending.clear();
                globalPosted.clear();
                getTransactions(filterTransactionList);
            } else {
                loadData();
            }
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

    private void initFields() {
        setKeyboardVisibilityListener(this);
        bottomCorners = findViewById(R.id.bottom_corners);
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
        layoutTransactionsPending = findViewById(R.id.layoutLLPending);
        layoutTransactionsPosted = findViewById(R.id.layoutLLposted);
        noTransactionTV = findViewById(R.id.noTransactions);
        noMoreTransactionTV = findViewById(R.id.noMoreTransactions);
        pendingTxt = findViewById(R.id.pendingTV);
        searchET = findViewById(R.id.searchET);
    }

    void initObservers() {
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
                                    noMoreTransactionTV.setVisibility(View.VISIBLE);
                                } else {
//                                    noTransactionTV.setVisibility(View.VISIBLE);
                                    noMoreTransactionTV.setVisibility(View.GONE);
                                }

                                layoutTransactionsPending.setVisibility(globalPending.size() == 0 ? View.GONE : View.VISIBLE);
                                layoutTransactionsPosted.setVisibility(globalPosted.size() == 0 ? View.GONE : View.VISIBLE);

                                if (globalPending.size() > 0) {
                                    transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, MerchantTransactionListActivity.this);
                                    pendingTxt.setVisibility(View.VISIBLE);
                                    rvTransactionsPending.setLayoutManager(mLayoutManager);
                                    rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
//                                    rvTransactionsPending.setAdapter(transactionListPendingAdapter);
                                    if (currentPage > 0) {
                                        int myPos = globalPending.size() - transactionList.getData().getItems().getPendingTransactions().size();
                                        rvTransactionsPending.scrollToPosition(myPos);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    } else {
                                        rvTransactionsPending.scrollToPosition(0);
                                        noTransactionTV.setVisibility(View.GONE);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    noTransactionTV.setVisibility(View.VISIBLE);
                                    pendingTxt.setVisibility(View.GONE);
                                }

                                if (globalPosted.size() > 0) {
                                    bottomCorners.setVisibility(View.VISIBLE);
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
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    } else {
                                        getRvTransactionsPosted.scrollToPosition(0);
                                        noTransactionTV.setVisibility(View.GONE);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    noTransactionTV.setVisibility(View.VISIBLE);
                                    bottomCorners.setVisibility(View.GONE);
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
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void loadData() {
        try {
            globalPending.clear();
            globalPosted.clear();
            currentPage = 0;
            total = 0;
//            filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
            noMoreTransactionTV.setVisibility(View.GONE);
            noTransactionTV.setVisibility(View.GONE);
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setMerchantTransactions(true);
//            transactionListRequest.setTransactionType(getDefaultTransactionTypes());
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageNo(String.valueOf(currentPage));
            getTransactions(transactionListRequest);
            objMyApplication.initializeTransactionSearch();
            objMyApplication.setTransactionListSearch(transactionListRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getTransactions(TransactionListRequest transactionListRequest) {
        showProgressDialog();
        dashboardViewModel.meTransactionList(transactionListRequest);
    }

    private void showTransactionDetails(TransactionListPosted selectedTransaction) {
        Intent inDetails = new Intent(MerchantTransactionListActivity.this, MerchantTransactionDetailsActivity.class);
        inDetails.putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID, selectedTransaction.getGbxTransactionId());
        inDetails.putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_TYPE, selectedTransaction.getTxnTypeDn());
        inDetails.putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_SUB_TYPE, selectedTransaction.getTxnSubTypeDn());
        startActivity(inDetails);
    }

    private void showFilterDialog() {
        if (filterTransactionList != null && filterTransactionList.isManualUpdate()) {
            filterTransactionList.setTransactionType(new ArrayList<>());
            filterTransactionList.setManualUpdate(false);

        }
        MerchantTransactionsFilterDialog filterDialog = new MerchantTransactionsFilterDialog(MerchantTransactionListActivity.this, filterTransactionList);
        filterDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(Utils.applyFilter)) {
                    MatomoUtility.getInstance().trackEvent(MatomoConstants.MERCHANT_FILTERS, MatomoConstants.MERCHANT_FILTERS_APPLIED);
                    dismissDialog();
                    globalPending.clear();
                    globalPosted.clear();
                    filterTransactionList = (TransactionListRequest) value;

                    if (filterTransactionList.getTransactionType() == null
                            || filterTransactionList.getTransactionType().size() == 0) {
                        filterTransactionList.setMerchantTransactions(true);
                        filterTransactionList.setManualUpdate(true);
                    }
                    if (filterTransactionList.isFilters()) {
                        filterIV.setImageResource(R.drawable.ic_filter_enabled);
                    } else {
                        filterIV.setImageResource(R.drawable.ic_filtericon);
                    }
                    getTransactions(filterTransactionList);
                }
//                else if (action.equals("Date_SELECTED")) {
//                    LogUtils.v(TAG, "Date Selected " + value);
////                    filterIV.setImageResource(R.drawable.ic_filter_enabled);
//                }
                else if (action.equals(Utils.resetFilter)) {
                    filterIV.setImageResource(R.drawable.ic_filtericon);
                    filterTransactionList = null;
                    loadData();
                    dismissDialog();
                }
            }
        });
        filterDialog.show();
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Utils.isKeyboardVisible = visible;
    }

}
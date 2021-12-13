package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.adapters.TransactionListPostedAdapter;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {
    TransactionListPendingAdapter transactionListPendingAdapter;
    TransactionListPostedAdapter transactionListPostedAdapter;
    static Context context;
    NestedScrollView nestedScrollView;
    public ProgressBar progressBar;
    public int totalItemCount, currentPage = 0, total = 0;
    RecyclerView rvTransactionsPending, getRvTransactionsPosted;
    Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    MyApplication objMyApplication;
    LinearLayout layoutTransactionspending, layoutTransactionsposted;
    TextView noTransactionTV;
    public DashboardViewModel dashboardViewModel;
    TransactionList transactionList;
    TextView pendingTxt;
    ImageView closeBtn;
    List<TransactionListPosted> globalData = new ArrayList<>();
    List<TransactionListPending> globalPending = new ArrayList<>();
    List<TransactionListPosted> globalPosted = new ArrayList<>();
    public static TransactionListActivity transactionListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_transaction_details);
        try {
            transactionListActivity = this;
            closeBtn = findViewById(R.id.closeBtnIV);
            nestedScrollView = findViewById(R.id.nestedSV);
            progressBar = findViewById(R.id.progressBarLoadMore);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            rvTransactionsPending = (RecyclerView) findViewById(R.id.transactionListPendingRV);
            getRvTransactionsPosted = findViewById(R.id.transactionListPostedRV);
            layoutTransactionspending = findViewById(R.id.layoutLLPending);
            layoutTransactionsposted = findViewById(R.id.layoutLLposted);
            noTransactionTV = findViewById(R.id.noTransactions);
            pendingTxt = findViewById(R.id.pendingTV);
//            getRvTransactionsPosted.addItemDecoration(new DividerItemDecoration(TransactionListActivity.this,90));
            transactionList = objMyApplication.getTransactionList();
//            totalItemCount = transactionList.getData().getItems().getPendingTransactionsCount();
            initObservers();
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNo(String.valueOf(currentPage));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
            dashboardViewModel.meTransactionList(transactionListRequest);
            dashboardViewModel.mePreferences();

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                        try {
//                            progressBar.setVisibility(View.VISIBLE);
//                            transactionListRequest.setPageNo(String.valueOf(currentPage));
//                            transactionListRequest.setWalletCategory(Utils.walletCategory);
//                            int pagination = Integer.parseInt(transactionListRequest.getPageSize()) + Utils.pageSize;
//                            transactionListRequest.setPageSize(String.valueOf(pagination));
//                            dashboardViewModel.meTransactionList(transactionListRequest);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        try {
                            if (TransactionListActivity.transactionListActivity.total > TransactionListActivity.transactionListActivity.currentPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                TransactionListActivity.transactionListActivity.currentPage = TransactionListActivity.transactionListActivity.currentPage + 1;
                                Log.e("CurrentPage", TransactionListActivity.transactionListActivity.currentPage + "");
                                TransactionListRequest transactionListRequest = new TransactionListRequest();
                                transactionListRequest.setPageNo(String.valueOf(TransactionListActivity.transactionListActivity.currentPage));
                                transactionListRequest.setWalletCategory(Utils.walletCategory);
                                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
                                TransactionListActivity.transactionListActivity.dashboardViewModel.meTransactionList(transactionListRequest);

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

    private void initObservers() {
        dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
            @Override
            public void onChanged(TransactionList transactionList) {
                if (transactionList != null && transactionList.getData().getItems() != null && transactionList.getStatus().equalsIgnoreCase("SUCCESS")) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(TransactionListActivity.this);
                        LinearLayoutManager nLayoutManager = new LinearLayoutManager(TransactionListActivity.this);
                        //Pending RV
                        globalPending.addAll(transactionList.getData().getItems().getPendingTransactions());
                        globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                        if (globalPending.size() > 0) {
//                            globalData.addAll(transactionList.getData().getItems().getPendingTransactions());
                            noTransactionTV.setVisibility(View.GONE);
                            layoutTransactionspending.setVisibility(View.VISIBLE);
                            total = transactionList.getData().getTotalPages();
                            objMyApplication.setTransactionList(transactionList);
                            transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, TransactionListActivity.this);
                            pendingTxt.setVisibility(View.VISIBLE);
                            rvTransactionsPending.setLayoutManager(mLayoutManager);
                            rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
                            rvTransactionsPending.setAdapter(transactionListPendingAdapter);

                        }
                        //Posted RV
                        if (globalPosted.size() > 0) {
//                            globalData.addAll(transactionList.getData().getItems().getPostedTransactions());
                            noTransactionTV.setVisibility(View.GONE);
                            layoutTransactionsposted.setVisibility(View.VISIBLE);
                            transactionListPostedAdapter = new TransactionListPostedAdapter(globalPosted, TransactionListActivity.this);
                            getRvTransactionsPosted.setLayoutManager(nLayoutManager);
                            getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
                            getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);
                            if (currentPage > 0) {
                                int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                getRvTransactionsPosted.scrollToPosition(myPos);
                            }
                        }

//                        if (globalData.size() > 0) {
//                            noTransactionTV.setVisibility(View.GONE);
//                            layoutTransactionsposted.setVisibility(View.VISIBLE);
//                            layoutTransactionspending.setVisibility(View.GONE);
//                            transactionListPostedAdapter = new TransactionListPostedAdapter(globalData, TransactionListActivity.this);
//                            getRvTransactionsPosted.setLayoutManager(nLayoutManager);
//                            getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
//                            getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);
//                        } else {
//                            noTransactionTV.setVisibility(View.VISIBLE);
//                            layoutTransactionsposted.setVisibility(View.GONE);
//                        }
                        if (transactionListPendingAdapter.getItemCount() == 0) {
//                                noTransactionTV.setVisibility(View.VISIBLE);
                            layoutTransactionspending.setVisibility(View.GONE);
                            pendingTxt.setVisibility(View.GONE);
                        } else if (transactionListPostedAdapter.getItemCount() == 0) {
                            layoutTransactionsposted.setVisibility(View.GONE);
                        } else if (transactionListPostedAdapter.getItemCount() == 0
                                && transactionListPendingAdapter.getItemCount() == 0) {
                            layoutTransactionsposted.setVisibility(View.GONE);
                            layoutTransactionspending.setVisibility(View.GONE);
                            noTransactionTV.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    loadMore(tokenTransactions.getData().getItems());
                }
            }
        });

    }


    private void loadMore(List<TransactionListPending> listItems) {
        try {
            transactionListPendingAdapter.addLoadingView();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        transactionListPendingAdapter.removeLoadingView();
                        transactionListPendingAdapter.addData(listItems);
                        transactionListPendingAdapter.notifyDataSetChanged();
//                        pbLoader.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 2000);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
//            TransactionListRequest transactionListRequest=new TransactionListRequest();
//            transactionListRequest.setPageNo(String.valueOf(currentPage));
//            transactionListRequest.setWalletCategory(Utils.walletCategory);
//            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
//            if (Utils.checkInternet(context)) {
//                dashboardViewModel.meTransactionList(transactionListRequest);
//                dashboardViewModel.meWallet();
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
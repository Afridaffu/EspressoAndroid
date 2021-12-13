package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.adapters.TransactionListPostedAdapter;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;

public class TransactionListActivity extends AppCompatActivity {
    TransactionListPendingAdapter transactionListPendingAdapter;
    TransactionListPostedAdapter transactionListPostedAdapter;
    static Context context;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    public int totalItemCount, currentPage = 0, total = 0;
    RecyclerView rvTransactionsPending, getRvTransactionsPosted;
    Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    MyApplication objMyApplication;
    LinearLayout layoutTransactionspending, layoutTransactionsposted;
    TextView noTransactionTV;
    DashboardViewModel dashboardViewModel;
    TransactionList transactionList;
    TextView pendingTxt;
    ImageView closeBtn,filterIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_transaction_details);
        try {
            closeBtn = findViewById(R.id.closeBtnIV);
            filterIV=findViewById(R.id.filterIconIV);

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

            filterIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        // custom dialog
                        final Dialog dialog = new Dialog(TransactionListActivity.this);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.activity_filters);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        Window window = dialog.getWindow();

                         int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,height);

                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.BOTTOM;
                        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);

                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                }
            });
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
                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            transactionListRequest.setPageNo(String.valueOf(currentPage));
                            transactionListRequest.setWalletCategory(Utils.walletCategory);
                            int pagination=Integer.parseInt(transactionListRequest.getPageSize())+Utils.pageSize;
                            transactionListRequest.setPageSize(String.valueOf(pagination));
                            dashboardViewModel.meTransactionList(transactionListRequest);
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
                        if (transactionList.getData().getItems().getPendingTransactionsCount() != 0) {
                            noTransactionTV.setVisibility(View.GONE);
                            layoutTransactionspending.setVisibility(View.VISIBLE);
                            objMyApplication.setTransactionList(transactionList);
                            transactionListPendingAdapter = new TransactionListPendingAdapter(transactionList.getData().getItems().getPendingTransactions(), TransactionListActivity.this);
                            pendingTxt.setVisibility(View.VISIBLE);
                            rvTransactionsPending.setLayoutManager(mLayoutManager);
                            rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
                            rvTransactionsPending.setAdapter(transactionListPendingAdapter);
                        }
                        //Posted RV
                        if (transactionList.getData().getItems().getPostedTransactions().size() != 0) {
                            noTransactionTV.setVisibility(View.GONE);
                            layoutTransactionsposted.setVisibility(View.VISIBLE);
                            transactionListPostedAdapter = new TransactionListPostedAdapter(transactionList.getData().getItems().getPostedTransactions(), TransactionListActivity.this);
                            getRvTransactionsPosted.setLayoutManager(nLayoutManager);
                            getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
                            getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);
                        }
                        if (transactionListPendingAdapter.getItemCount() == 0) {
//                                noTransactionTV.setVisibility(View.VISIBLE);
                            layoutTransactionspending.setVisibility(View.GONE);
                            pendingTxt.setVisibility(View.GONE);
                        } else if (transactionListPostedAdapter.getItemCount() == 0) {
                            layoutTransactionsposted.setVisibility(View.GONE);
                        } else if (transactionListPostedAdapter.getItemCount() == 0 && transactionListPendingAdapter.getItemCount() == 0) {
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
//        try {
            TransactionListRequest transactionListRequest=new TransactionListRequest();
//            transactionListRequest.setPageNo(String.valueOf(currentPage));
//            transactionListRequest.setWalletCategory(Utils.walletCategory);
//            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
//            if (Utils.checkInternet(context)) {
//                dashboardViewModel.meTransactionList(transactionListRequest);
//                dashboardViewModel.meWallet();
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        try {
            progressBar.setVisibility(View.VISIBLE);
            transactionListRequest.setPageNo(String.valueOf(currentPage));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
//        int pagination = Integer.parseInt(transactionListRequest.getPageSize()) + Utils.pageSize;
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
            dashboardViewModel.meTransactionList(transactionListRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
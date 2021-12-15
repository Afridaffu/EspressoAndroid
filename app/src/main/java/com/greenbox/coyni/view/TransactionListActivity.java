package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.adapters.TransactionListPostedAdapter;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TransactionListActivity extends AppCompatActivity {
    TransactionListPendingAdapter transactionListPendingAdapter;
    TransactionListPostedAdapter transactionListPostedAdapter;
    static Context context;
    Long mLastClickTime = 0L;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    public ProgressBar progressBar;
    public int totalItemCount, currentPage = 0, total = 0;
    RecyclerView rvTransactionsPending, getRvTransactionsPosted;
    Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    MyApplication objMyApplication;
    LinearLayout layoutTransactionspending, layoutTransactionsposted;
    TextView noTransactionTV,noMoreTransactionTV;
    public DashboardViewModel dashboardViewModel;
    TransactionList transactionList;
    TextView pendingTxt;
    ImageView closeBtn,filterIV;
    List<TransactionListPosted> globalData = new ArrayList<>();
    EditText searchET;
    List<TransactionListPending> globalPending = new ArrayList<>();
    List<TransactionListPosted> globalPosted = new ArrayList<>();
    public static TransactionListActivity transactionListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_transaction_list);
        try {
            transactionListActivity = this;
            closeBtn = findViewById(R.id.closeBtnIV);
            filterIV=findViewById(R.id.filterIconIV);

            nestedScrollView = findViewById(R.id.nestedSV);
            swipeRefreshLayout=findViewById(R.id.refreshLayout);
            progressBar = findViewById(R.id.progressBarLoadMore);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            rvTransactionsPending = (RecyclerView) findViewById(R.id.transactionListPendingRV);
            getRvTransactionsPosted = findViewById(R.id.transactionListPostedRV);
            layoutTransactionspending = findViewById(R.id.layoutLLPending);
            layoutTransactionsposted = findViewById(R.id.layoutLLposted);
            noTransactionTV = findViewById(R.id.noTransactions);
            noMoreTransactionTV=findViewById(R.id.noMoreTransactions);
            pendingTxt = findViewById(R.id.pendingTV);
            searchET=findViewById(R.id.searchET);
            initObservers();

            filterIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                        LinearLayout dateRPickerLL;
                        // custom dialog
                        final Dialog dialog = new Dialog(TransactionListActivity.this);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.activity_filters);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dateRPickerLL=dialog.findViewById(R.id.dateRangePickerLL);
                        EditText getDateFromPickerET=dialog.findViewById(R.id.datePickET);
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


                        //Click Actions

                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                            calendar.clear();

                            long today = MaterialDatePicker.todayInUtcMilliseconds();

                            calendar.setTimeInMillis(today);

                            calendar.set(Calendar.MONTH, Calendar.JANUARY);
                            long january = calendar.getTimeInMillis();

                            calendar.set(Calendar.MONTH, Calendar.MARCH);
                            long march = calendar.getTimeInMillis();

                            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                            long december = calendar.getTimeInMillis();


                            CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
                            constraintBuilder.setValidator(new DateValidatorWeekdays());

                            //MaterialDatePicker
//        MaterialDatePicker.Builder<Pair<Long,Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
//        builder.setTitleText("SELECT A DATE");
//        builder.setSelection();


//
                            MaterialDatePicker.Builder<Pair<Long,Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                            builder.setTitleText("SELECT A DATE");
                            final MaterialDatePicker materialDatePicker = builder.build();
                            builder.setTheme(R.style.Calender);
                    dateRPickerLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                materialDatePicker.show(getSupportFragmentManager(),"");
                                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long,Long>>() {
                                    @Override
                                    public void onPositiveButtonClick(Pair<Long,Long> selection) {

                                        Long start_date=selection.first;
                                        Long end_date=selection.second;
                                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd,yyyy");
                                        getDateFromPickerET.setText(simpleDateFormat.format(start_date)+" - "+simpleDateFormat.format(end_date));
                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                   String search_key=charSequence.toString();
                   List<TransactionListPending> filterList=new ArrayList<>();
                   List<TransactionListPosted> filterList1=new ArrayList<>();
                   int pindex=0,poindex=0;
                    if (globalPending.size()>0){
                        for (int iteration=0;iteration<globalPending.size();iteration++){
                            pindex=globalPending.get(iteration).getGbxTransactionId().toLowerCase().indexOf(search_key.toLowerCase());
                            if (pindex==0){
                                filterList.add(globalPending.get(iteration));
                            }
                        }
                        if (filterList.size()>0){
                            transactionListPendingAdapter.updateList(filterList);
                        }

                    }
                    if (globalPosted.size()>0){
                        for (int iteration=0;iteration<globalPosted.size();iteration++){
                            poindex=globalPosted.get(iteration).getGbxTransactionId().toLowerCase().indexOf(search_key.toLowerCase());
                            if (poindex==0){
                                filterList1.add(globalPosted.get(iteration));
                            }
                        }
                        if (filterList1.size()>0){
                            transactionListPostedAdapter.updateList(filterList1);
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                            globalPosted.clear();
                            globalPending.clear();
                            currentPage = 0;
                            TransactionListRequest transactionListRequest = new TransactionListRequest();
                            transactionListRequest.setPageNo(String.valueOf(currentPage));
                            transactionListRequest.setWalletCategory(Utils.walletCategory);
                            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
                            dashboardViewModel.meTransactionList(transactionListRequest);
                            dashboardViewModel.mePreferences();
                            swipeRefreshLayout.setRefreshing(false);
                }
            });
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.status));


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
                                noMoreTransactionTV.setVisibility(View.GONE);
                            }
                            if (TransactionListActivity.transactionListActivity.total == TransactionListActivity.transactionListActivity.currentPage) {
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
                            pendingTxt.setVisibility(View.GONE);
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
            globalPending.clear();
            globalPosted.clear();
            currentPage=0;
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNo(String.valueOf(currentPage));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
            dashboardViewModel.meTransactionList(transactionListRequest);
            dashboardViewModel.mePreferences();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
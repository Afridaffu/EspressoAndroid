package com.greenbox.coyni.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TransactionListActivity extends AppCompatActivity {
    TransactionListPendingAdapter transactionListPendingAdapter;
    TransactionListPostedAdapter transactionListPostedAdapter;
    static Context context;
    Long mLastClickTime = 0L;
    NestedScrollView nestedScrollView;
    public ProgressBar progressBar;
    public int totalItemCount, currentPage = 0, total = 0;
    RecyclerView rvTransactionsPending, getRvTransactionsPosted;
    Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    MyApplication objMyApplication;
    LinearLayout layoutTransactionspending, layoutTransactionsposted;
    TextView noTransactionTV, noMoreTransactionTV;
    public DashboardViewModel dashboardViewModel;
    TransactionList transactionList;
    TextView pendingTxt;
    ImageView closeBtn, filterIV;
    List<TransactionListPosted> globalData = new ArrayList<>();
    EditText searchET;
    List<TransactionListPending> globalPending = new ArrayList<>();
    List<TransactionListPosted> globalPosted = new ArrayList<>();
    public static TransactionListActivity transactionListActivity;

    private ArrayList<Integer> transactionType = new ArrayList<Integer>();
    private ArrayList<Integer> transactionSubType = new ArrayList<Integer>();
    private ArrayList<Integer> txnStatus = new ArrayList<Integer>();

    public String strStartAmount = "", strEndAmount = "";

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
            filterIV = findViewById(R.id.filterIconIV);

            nestedScrollView = findViewById(R.id.nestedSV);
            progressBar = findViewById(R.id.progressBarLoadMore);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            rvTransactionsPending = (RecyclerView) findViewById(R.id.transactionListPendingRV);
            getRvTransactionsPosted = findViewById(R.id.transactionListPostedRV);
            layoutTransactionspending = findViewById(R.id.layoutLLPending);
            layoutTransactionsposted = findViewById(R.id.layoutLLposted);
            noTransactionTV = findViewById(R.id.noTransactions);
            noMoreTransactionTV = findViewById(R.id.noMoreTransactions);
            pendingTxt = findViewById(R.id.pendingTV);
            searchET = findViewById(R.id.searchET);
            initObservers();

            filterIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showFiltersPopup();
                }
            });
            closeBtn.setOnClickListener(view -> finish());

            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String search_key = charSequence.toString();
                    List<TransactionListPending> filterList = new ArrayList<>();
                    List<TransactionListPosted> filterList1 = new ArrayList<>();
                    int pindex = 0, poindex = 0;
                    if (globalPending.size() > 0) {
                        for (int iteration = 0; iteration < globalPending.size(); iteration++) {
                            pindex = globalPending.get(iteration).getGbxTransactionId().toLowerCase().indexOf(search_key.toLowerCase());
                            if (pindex == 0) {
                                filterList.add(globalPending.get(iteration));
                            }
                        }
                        if (filterList.size() > 0) {
                            transactionListPendingAdapter.updateList(filterList);
                        }

                    }
                    if (globalPosted.size() > 0) {
                        for (int iteration = 0; iteration < globalPosted.size(); iteration++) {
                            poindex = globalPosted.get(iteration).getGbxTransactionId().toLowerCase().indexOf(search_key.toLowerCase());
                            if (poindex == 0) {
                                filterList1.add(globalPosted.get(iteration));
                            }
                        }
                        if (filterList1.size() > 0) {
                            transactionListPostedAdapter.updateList(filterList1);
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        try {
                            Log.e("total abcd", total + "");
                            Log.e("currentPage acbd", currentPage + "");
                            if (total - 1 > currentPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;
                                Log.e("CurrentPage", currentPage + "");
                                TransactionListRequest transactionListRequest = new TransactionListRequest();
                                transactionListRequest.setPageNo(String.valueOf(currentPage));
                                transactionListRequest.setWalletCategory(Utils.walletCategory);
                                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

                                if (transactionType.size() > 0) {
                                    transactionListRequest.setTransactionType(transactionType);
                                }
                                if (transactionSubType.size() > 0) {
                                    transactionListRequest.setTransactionSubType(transactionSubType);
                                }
                                if (txnStatus.size() > 0) {
                                    transactionListRequest.setTxnStatus(txnStatus);
                                }
                                if (!strStartAmount.trim().equals("")) {
                                    transactionListRequest.setFromAmount(strStartAmount.replace(",", ""));
                                    transactionListRequest.setFromAmountOperator(">=");
                                }
                                if (!strEndAmount.trim().equals("")) {
                                    transactionListRequest.setToAmount(strEndAmount.replace(",", ""));
                                    transactionListRequest.setToAmountOperator("<=");
                                }

                                dashboardViewModel.meTransactionList(transactionListRequest);
                                noMoreTransactionTV.setVisibility(View.GONE);
                            } else {
                                noMoreTransactionTV.setVisibility(View.VISIBLE);
                            }
//                            if (total == TransactionListActivity.transactionListActivity.currentPage) {
//                                noMoreTransactionTV.setVisibility(View.VISIBLE);
//                            }

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

        try {
            dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
                @Override
                public void onChanged(TransactionList transactionList) {
                    try {
                        if (transactionList != null && transactionList.getData().getItems() != null && transactionList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            progressBar.setVisibility(View.GONE);
                            try {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(TransactionListActivity.this);
                                LinearLayoutManager nLayoutManager = new LinearLayoutManager(TransactionListActivity.this);
                                //Pending RV
                                globalPending.addAll(transactionList.getData().getItems().getPendingTransactions());
                                globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                total = transactionList.getData().getTotalPages();
                                if (globalPending.size() > 0) {
                                    //                            globalData.addAll(transactionList.getData().getItems().getPendingTransactions());
                                    noTransactionTV.setVisibility(View.GONE);
                                    layoutTransactionspending.setVisibility(View.VISIBLE);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            transactionType.clear();
            transactionSubType.clear();
            txnStatus.clear();
            noMoreTransactionTV.setVisibility(View.GONE);
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNo(String.valueOf(TransactionListActivity.transactionListActivity.currentPage));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
            if (transactionType.size() > 0) {
                transactionListRequest.setTransactionType(transactionType);
            }
            if (transactionSubType.size() > 0) {
                transactionListRequest.setTransactionSubType(transactionSubType);
            }
            if (txnStatus.size() > 0) {
                transactionListRequest.setTxnStatus(txnStatus);
            }
            if (!strStartAmount.trim().equals("")) {
                transactionListRequest.setFromAmount(strStartAmount.replace(",", ""));
                transactionListRequest.setFromAmountOperator(">=");
            }
            if (!strEndAmount.trim().equals("")) {
                transactionListRequest.setToAmount(strEndAmount.replace(",", ""));
                transactionListRequest.setToAmountOperator("<=");
            }
            dashboardViewModel.meTransactionList(transactionListRequest);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showFiltersPopup() {
        // custom dialog
        final Dialog dialog = new Dialog(TransactionListActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_filters);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, height);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        Chip transTypePR = dialog.findViewById(R.id.transTypePR);
        Chip transTypeBT = dialog.findViewById(R.id.transTypeBT);
        Chip transTypeSO = dialog.findViewById(R.id.transTypeSO);
        Chip transTypeWithdraw = dialog.findViewById(R.id.transTypeWithdraw);
        Chip transTypeRefund = dialog.findViewById(R.id.transTypeRefund);
        Chip transTypeAT = dialog.findViewById(R.id.transTypeAT);
        Chip transTypePI = dialog.findViewById(R.id.transTypePI);

        Chip transSubTypeSent = dialog.findViewById(R.id.transSubTypeSent);
        Chip transSubTypeReceived = dialog.findViewById(R.id.transSubTypeReceived);
        Chip transSubTypeBA = dialog.findViewById(R.id.transSubTypeBA);
        Chip transSubTypeCC = dialog.findViewById(R.id.transSubTypeCC);
        Chip transSubTypeDC = dialog.findViewById(R.id.transSubTypeDC);
        Chip transSubTypeSignet = dialog.findViewById(R.id.transSubTypeSignet);
        Chip transSubTypeIP = dialog.findViewById(R.id.transSubTypeIP);
        Chip transSubTypeGiftCard = dialog.findViewById(R.id.transSubTypeGiftCard);
        Chip transSubTypeSOToken = dialog.findViewById(R.id.transSubTypeSOToken);
        Chip transSubTypeFW = dialog.findViewById(R.id.transSubTypeFW);
        Chip transSubTypeCW = dialog.findViewById(R.id.transSubTypeCW);

        Chip transStatusPending = dialog.findViewById(R.id.transStatusPending);
        Chip transStatusCompleted = dialog.findViewById(R.id.transStatusCompleted);
        Chip transStatusCanceled = dialog.findViewById(R.id.transStatusCanceled);
        Chip transStatusInProgress = dialog.findViewById(R.id.transStatusInProgress);
        Chip transStatusFailed = dialog.findViewById(R.id.transStatusFailed);

        EditText transAmountStartET = dialog.findViewById(R.id.transAmountStartET);
        EditText transAmountEndET = dialog.findViewById(R.id.transAmountEndET);

        CardView applyFilterBtnCV = dialog.findViewById(R.id.applyFilterBtnCV);
        LinearLayout dateRangePickerLL = dialog.findViewById(R.id.dateRangePickerLL);

        if (transactionType.size() > 0) {
            for (int i = 0; i < transactionType.size(); i++) {
                switch (transactionType.get(i)) {
                    case Utils.payRequest:
                        transTypePR.setChecked(true);
                        break;

                    case Utils.buyTokens:
                        transTypeBT.setChecked(true);
                        break;

                    case Utils.saleOrder:
                        transTypeSO.setChecked(true);
                        break;

                    case Utils.withdraw:
                        transTypeWithdraw.setChecked(true);
                        break;

                    case Utils.refund:
                        transTypeRefund.setChecked(true);
                        break;

                    case Utils.accountTransfer:
                        transTypeAT.setChecked(true);
                        break;

                    case Utils.paidInvoice:
                        transTypePI.setChecked(true);
                        break;


                }
            }
        }

        if (transactionSubType.size() > 0) {
            for (int i = 0; i < transactionSubType.size(); i++) {
                switch (transactionSubType.get(i)) {
                    case Utils.sent:
                        transSubTypeSent.setChecked(true);
                        break;

                    case Utils.received:
                        transSubTypeReceived.setChecked(true);
                        break;

                    case Utils.bankAccount:
                        transSubTypeBA.setChecked(true);
                        break;

                    case Utils.creditCard:
                        transSubTypeCC.setChecked(true);
                        break;

                    case Utils.debitCard:
                        transSubTypeDC.setChecked(true);
                        break;

                    case Utils.signet:
                        transSubTypeSignet.setChecked(true);
                        break;

                    case Utils.instantPay:
                        transSubTypeIP.setChecked(true);
                        break;

                    case Utils.giftCard:
                        transSubTypeGiftCard.setChecked(true);
                        break;

                    case Utils.saleOrderToken:
                        transSubTypeSOToken.setChecked(true);
                        break;

//                    case Utils.failedWithdraw:
//                        transTypePI.setChecked(true);
//                        break;

                    case Utils.cancelledWithdraw:
                        transSubTypeCW.setChecked(true);
                        break;

                }
            }
        }

        if (txnStatus.size() > 0) {
            for (int i = 0; i < txnStatus.size(); i++) {
                switch (txnStatus.get(i)) {
                    case Utils.pending:
                        transStatusPending.setChecked(true);
                        break;

                    case Utils.completed:
                        transStatusCompleted.setChecked(true);
                        break;

                    case Utils.cancelled:
                        transStatusCanceled.setChecked(true);
                        break;

                    case Utils.inProgress:
                        transStatusInProgress.setChecked(true);
                        break;

                    case Utils.failed:
                        transStatusFailed.setChecked(true);
                        break;

                }
            }
        }


        if (!strStartAmount.equals("")) {

        }

        if (!strEndAmount.equals("")) {

        }

        transTypePR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.payRequest);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.payRequest) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.buyTokens);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.buyTokens) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeSO.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.saleOrder);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.saleOrder) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeWithdraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.withdraw);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.withdraw) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeRefund.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.refund);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.refund) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeAT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
//                    transactionType.add(Utils.accountTransfer);
                    transTypeAT.setChecked(false);
                } else {
//                    for (int i=0; i < transactionType.size();i++){
//                        if(transactionType.get(i) == Utils.refund){
//                            transactionType.remove(i);
//                            break;
//                        }
//                    }
                    transTypeAT.setChecked(false);
                }
            }
        });

        transTypePI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.paidInvoice);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.paidInvoice) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });


        transSubTypeSent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.sent);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.sent) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeReceived.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.received);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.received) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeBA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.bankAccount);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.bankAccount) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeCC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.creditCard);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.creditCard) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeDC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.debitCard);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.debitCard) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeSignet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.signet);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.signet) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeIP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.instantPay);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.instantPay) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeGiftCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.giftCard);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.giftCard) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeSOToken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.saleOrderToken);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.saleOrderToken) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeFW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.failedWithdraw);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.failedWithdraw) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transSubTypeCW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionSubType.add(Utils.cancelledWithdraw);
                } else {
                    for (int i = 0; i < transactionSubType.size(); i++) {
                        if (transactionSubType.get(i) == Utils.cancelledWithdraw) {
                            transactionSubType.remove(i);
                            break;
                        }
                    }
                }
            }
        });


        transStatusPending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.pending);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.pending) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.completed);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.completed) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusCanceled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
//                    txnStatus.add(Utils.completed);
                    transStatusCanceled.setChecked(false);
                } else {
                    transStatusCanceled.setChecked(false);
//                    for (int i=0; i < txnStatus.size();i++){
//                        if(txnStatus.get(i) == Utils.completed){
//                            txnStatus.remove(i);
//                            break;
//                        }
//                    }
                }
            }
        });

        transStatusInProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.inProgress);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.inProgress) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusFailed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.failed);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.failed) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transAmountStartET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {
                    transAmountStartET.setText("");
                }
            }
        });

        transAmountEndET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {
                    transAmountEndET.setText("");
                }
            }
        });

        transAmountStartET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    USFormat(transAmountStartET, "START");
                }
            }
        });

        transAmountEndET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    USFormat(transAmountEndET, "END");
                }
            }
        });

        transAmountStartET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    USFormat(transAmountStartET, "START");
                }
                return false;
            }
        });

        transAmountEndET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    USFormat(transAmountEndET, "END");
                }
                return false;
            }
        });

        applyFilterBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("type", transactionType.size() + "");
                Log.e("subtype", transactionSubType.size() + "");
                Log.e("status", txnStatus.size() + "");

                globalPending.clear();
                globalPosted.clear();
                currentPage = 0;
                TransactionListRequest transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNo(String.valueOf(currentPage));
                transactionListRequest.setWalletCategory(Utils.walletCategory);
                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

                if (transactionType.size() > 0) {
                    transactionListRequest.setTransactionType(transactionType);
                }
                if (transactionSubType.size() > 0) {
                    transactionListRequest.setTransactionSubType(transactionSubType);
                }
                if (txnStatus.size() > 0) {
                    transactionListRequest.setTxnStatus(txnStatus);
                }
                if (!transAmountStartET.getText().toString().trim().equals("")) {
                    transactionListRequest.setFromAmount(transAmountStartET.getText().toString().replace(",", ""));
                    transactionListRequest.setFromAmountOperator(">=");
                }
                if (!transAmountEndET.getText().toString().trim().equals("")) {
                    transactionListRequest.setToAmount(transAmountEndET.getText().toString().replace(",", ""));
                    transactionListRequest.setToAmountOperator("<=");
                }

                dashboardViewModel.meTransactionList(transactionListRequest);
                noMoreTransactionTV.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });


        dateRangePickerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("SELECT A DATE");
                builder.setTheme(R.style.Theme_Calender);
                final MaterialDatePicker materialDatePicker = builder.build();

                materialDatePicker.show(getSupportFragmentManager(), "");
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    private void USFormat(EditText etAmount, String mode) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.setSelection(etAmount.getText().length());
            if (mode.equals("START")) {
                strStartAmount = Utils.USNumberFormat(Double.parseDouble(strAmount));
            } else {
                strEndAmount = Utils.USNumberFormat(Double.parseDouble(strAmount));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
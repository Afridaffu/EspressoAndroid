package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
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
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.adapters.TransactionListPostedNewAdapter;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.MatomoConstants;
import com.greenbox.coyni.utils.MatomoUtility;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class TransactionListActivity extends BaseActivity implements TextWatcher {
    TransactionListPendingAdapter transactionListPendingAdapter;
    TransactionListPostedNewAdapter transactionListPostedAdapter;
    static Context context;
    Long mLastClickTime = 0L, mLastClickTimeFilters = 0L;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    public ProgressBar progressBar;
    public int totalItemCount, currentPage = 0, total = 0;
    ExpandableHeightRecyclerView rvTransactionsPending, getRvTransactionsPosted;
    Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    MyApplication objMyApplication;
    LinearLayout layoutTransactionspending, layoutTransactionsposted;
    TextView noTransactionTV, noMoreTransactionTV;
    public DashboardViewModel dashboardViewModel;
    TransactionList transactionList;
    TextView pendingTxt, loadMoreTV;
    ImageView closeBtn, filterIV;
    List<TransactionListPosted> globalData = new ArrayList<>();
    EditText searchET;
    List<TransactionListPending> globalPending = new ArrayList<>();
    List<TransactionListPosted> globalPosted = new ArrayList<>();

    private ArrayList<Integer> transactionType = new ArrayList<Integer>();
    private ArrayList<Integer> transactionSubType = new ArrayList<Integer>();
    private ArrayList<Integer> txnStatus = new ArrayList<Integer>();

    private ArrayList<Integer> tempTransactionType = new ArrayList<Integer>();
    private ArrayList<Integer> tempTransactionSubType = new ArrayList<Integer>();
    private ArrayList<Integer> tempTxnStatus = new ArrayList<Integer>();

    public String strStartAmount = "", strEndAmount = "", strFromDate = "", strToDate = "", strSelectedDate = "", tempStrSelectedDate = "";
    public long startDateLong = 0L, endDateLong = 0L, tempStartDateLong = 0L, tempEndDateLong = 0L;
    Date startDateD = null;
    Date endDateD = null;
    public static TransactionListActivity transactionListActivity;

    boolean isSwipeToRefresh = false;

//    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_transaction_list);
        MatomoUtility.getInstance().trackScreen(MatomoConstants.CUSTOMER_TRANSACTIONS_SCREEN);
        try {
            transactionListActivity = this;
            closeBtn = findViewById(R.id.closeBtnIV);
            filterIV = findViewById(R.id.filterIconIV);

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
            initObservers();
            loadData();
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
            searchET.addTextChangedListener(this);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        noMoreTransactionTV.setVisibility(View.GONE);

                        isSwipeToRefresh = true;
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
                        transactionListRequest.setTransactionType(getDefaultTransactionTypes());
                        transactionListRequest.setPageNo(String.valueOf(currentPage));
                        transactionListRequest.setWalletCategory(Utils.walletCategory);
                        transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

                        transactionsAPI(transactionListRequest);
                        objMyApplication.initializeTransactionSearch();
                        objMyApplication.setTransactionListSearch(transactionListRequest);

                        if (searchET.getText().length() > 0 && !searchET.getText().equals("")) {
                            searchET.setText("");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//                    swipeRefreshLayout.setRefreshing(false);
                    searchET.setText("");
                }
            });

            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_green));

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        Log.e("scrollY", scrollY + "  " + v.getChildAt(0).getMeasuredHeight() + " " + v.getMeasuredHeight());
                        try {
//                            isFilters = false;
//                            searchET.setText("");
//                            searchET.clearFocus();
                            Log.e("total abcd", total + "");
                            Log.e("currentPage acbd", currentPage + "");
                            if (total - 1 > currentPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                loadMoreTV.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;
                                Log.e("CurrentPage", currentPage + "");
                                TransactionListRequest transactionListRequest = new TransactionListRequest();
                                transactionListRequest.setTransactionType(getDefaultTransactionTypes());
                                transactionListRequest.setPageNo(String.valueOf(currentPage));
                                transactionListRequest.setWalletCategory(Utils.walletCategory);
                                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

                                if (isFilters) {
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

                                    if (!strFromDate.equals("")) {
                                        transactionListRequest.setUpdatedFromDate(objMyApplication.exportDate(strFromDate));
                                        transactionListRequest.setUpdatedFromDateOperator(">=");
                                    }
                                    if (!strToDate.equals("")) {
                                        transactionListRequest.setUpdatedToDate(objMyApplication.exportDate(strToDate));
                                        transactionListRequest.setUpdatedToDateOperator("<=");
                                    }
                                } else {
//                                    globalPending.clear();
//                                    globalPosted.clear();
//                                    transactionType.clear();
//                                    transactionSubType.clear();
//                                    txnStatus.clear();
//                                    currentPage = 0;
//                                    strFromDate = "";
//                                    strToDate = "";
//                                    strStartAmount = "";
//                                    strEndAmount = "";
//                                    startDateD = null;
//                                    endDateD = null;
//                                    startDateLong = 0L;
//                                    endDateLong = 0L;
//                                    strSelectedDate = "";
//                                    filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                                }

                                transactionsAPI(transactionListRequest);
                                objMyApplication.initializeTransactionSearch();
                                objMyApplication.setTransactionListSearch(transactionListRequest);

                                noMoreTransactionTV.setVisibility(View.GONE);
                            } else {
//                                noMoreTransactionTV.setVisibility(View.VISIBLE);
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
            if (!isSwipeToRefresh) {
                globalPending.clear();
                globalPosted.clear();
                objMyApplication.getTransactionListSearch().setPageNo("0");
                transactionsAPI(objMyApplication.getTransactionListSearch());
            }
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

    private void initObservers() {
        dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
            @Override
            public void onChanged(TransactionList transactionList) {
                try {
                    if (transactionList != null && transactionList.getData().getItems() != null) {
                        if (transactionList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            progressBar.setVisibility(View.GONE);
                            loadMoreTV.setVisibility(View.GONE);
                            try {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(TransactionListActivity.this);
                                LinearLayoutManager nLayoutManager = new LinearLayoutManager(TransactionListActivity.this);
                                //Pending RV
                                globalPending.addAll(transactionList.getData().getItems().getPendingTransactions());
                                globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                total = transactionList.getData().getTotalPages();

                                getRvTransactionsPosted.setNestedScrollingEnabled(false);
                                rvTransactionsPending.setNestedScrollingEnabled(false);
                                rvTransactionsPending.setExpanded(true);
                                getRvTransactionsPosted.setExpanded(true);

                                if (globalPending.size() > 0 && globalPosted.size() == 0) {
                                    noTransactionTV.setVisibility(View.GONE);
                                    layoutTransactionspending.setVisibility(View.VISIBLE);
                                    layoutTransactionsposted.setVisibility(View.GONE);
                                    transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, TransactionListActivity.this);
                                    pendingTxt.setVisibility(View.VISIBLE);
                                    rvTransactionsPending.setLayoutManager(mLayoutManager);
                                    rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
                                    rvTransactionsPending.setAdapter(transactionListPendingAdapter);
                                    if (currentPage > 0) {
                                        int myPos = globalPending.size() - transactionList.getData().getItems().getPendingTransactions().size();
                                        rvTransactionsPending.scrollToPosition(myPos);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    } else {
                                        rvTransactionsPending.scrollToPosition(0);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    }
                                } else if (globalPending.size() == 0 && globalPosted.size() > 0) {
                                    noTransactionTV.setVisibility(View.GONE);
                                    layoutTransactionspending.setVisibility(View.GONE);
                                    pendingTxt.setVisibility(View.GONE);
                                    layoutTransactionsposted.setVisibility(View.VISIBLE);
                                    transactionListPostedAdapter = new TransactionListPostedNewAdapter(globalPosted, TransactionListActivity.this);
                                    getRvTransactionsPosted.setLayoutManager(nLayoutManager);
                                    getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
                                    getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);
                                    if (currentPage > 0) {
                                        int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                        getRvTransactionsPosted.scrollToPosition(myPos);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    } else {
                                        getRvTransactionsPosted.scrollToPosition(0);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    }
                                } else if (globalPending.size() > 0 && globalPosted.size() > 0) {
                                    noTransactionTV.setVisibility(View.GONE);
                                    layoutTransactionspending.setVisibility(View.VISIBLE);
                                    layoutTransactionsposted.setVisibility(View.VISIBLE);
                                    transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, TransactionListActivity.this);
                                    pendingTxt.setVisibility(View.VISIBLE);
                                    rvTransactionsPending.setLayoutManager(mLayoutManager);
                                    rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
                                    rvTransactionsPending.setAdapter(transactionListPendingAdapter);

                                    transactionListPostedAdapter = new TransactionListPostedNewAdapter(globalPosted, TransactionListActivity.this);
                                    getRvTransactionsPosted.setLayoutManager(nLayoutManager);
                                    getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
                                    getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);

                                    if (currentPage > 0) {
                                        int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                        getRvTransactionsPosted.scrollToPosition(myPos);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    } else {
                                        getRvTransactionsPosted.scrollToPosition(0);
                                        noMoreTransactionTV.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    noTransactionTV.setVisibility(View.VISIBLE);
                                    layoutTransactionspending.setVisibility(View.GONE);
                                    layoutTransactionsposted.setVisibility(View.GONE);
                                    pendingTxt.setVisibility(View.GONE);

                                }

//                                //Posted RV
//                                if (globalPosted.size() > 0) {
//                                    noTransactionTV.setVisibility(View.GONE);
//                                    layoutTransactionsposted.setVisibility(View.VISIBLE);
//                                    transactionListPostedAdapter = new TransactionListPostedAdapter(globalPosted, TransactionListActivity.this);
//                                    getRvTransactionsPosted.setLayoutManager(nLayoutManager);
//                                    getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
//                                    getRvTransactionsPosted.setAdapter(transactionListPostedAdapter);
//                                    if (currentPage > 0) {
//                                        int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
//                                        getRvTransactionsPosted.scrollToPosition(myPos);
//                                    }
//                                }

//                                if (transactionListPendingAdapter.getItemCount() == 0) {
//                                    layoutTransactionspending.setVisibility(View.GONE);
//                                    pendingTxt.setVisibility(View.GONE);
//                                } else if (transactionListPostedAdapter.getItemCount() == 0) {
//                                    layoutTransactionsposted.setVisibility(View.GONE);
//                                } else if (transactionListPostedAdapter.getItemCount() == 0
//                                        && transactionListPendingAdapter.getItemCount() == 0) {
//                                    layoutTransactionsposted.setVisibility(View.GONE);
//                                    layoutTransactionspending.setVisibility(View.GONE);
//                                    pendingTxt.setVisibility(View.GONE);
//                                    noTransactionTV.setVisibility(View.VISIBLE);
//                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.displayAlert(transactionList.getError().getErrorDescription(), TransactionListActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), TransactionListActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
                isSwipeToRefresh = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        EditText getDateFromPickerET = dialog.findViewById(R.id.datePickET);
        TextView resetFiltersTV = dialog.findViewById(R.id.resetFiltersTV);

        tempTransactionType = new ArrayList<Integer>();
        tempTransactionSubType = new ArrayList<Integer>();
        tempTxnStatus = new ArrayList<Integer>();

        if (isFilters) {

            tempTransactionType.clear();
            tempTransactionType.addAll(transactionType);

            if (tempTransactionType.size() > 0) {
                for (int i = 0; i < tempTransactionType.size(); i++) {
                    switch (tempTransactionType.get(i)) {
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

            tempTransactionSubType.clear();
            tempTransactionSubType.addAll(transactionSubType);
            if (tempTransactionSubType.size() > 0) {
                for (int i = 0; i < tempTransactionSubType.size(); i++) {
                    switch (tempTransactionSubType.get(i)) {
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

//                    case Utils.saleOrderToken:
//                        transSubTypeSOToken.setChecked(true);
//                        break;

                        case Utils.failedWithdraw:
                            transSubTypeFW.setChecked(true);
                            break;

                        case Utils.cancelledWithdraw:
                            transSubTypeCW.setChecked(true);
                            break;

                    }
                }
            }

            tempTxnStatus.clear();
            tempTxnStatus.addAll(txnStatus);
            if (tempTxnStatus.size() > 0) {
                for (int i = 0; i < tempTxnStatus.size(); i++) {
                    switch (tempTxnStatus.get(i)) {
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

            if (!strStartAmount.trim().equals("")) {
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                transAmountStartET.setFilters(FilterArray);
                transAmountStartET.setText(strStartAmount);
            }

            if (!strEndAmount.trim().equals("")) {
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                transAmountEndET.setFilters(FilterArray);
                transAmountEndET.setText(strEndAmount);
            }

            if (!strSelectedDate.equals("")) {
                getDateFromPickerET.setText(strSelectedDate);
            }
        } else {
            transactionType.clear();
            transactionSubType.clear();
            txnStatus.clear();
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
            startDateD = null;
            endDateD = null;
            startDateLong = 0L;
            endDateLong = 0L;
            isFilters = false;
            strSelectedDate = "";
            filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
//            searchET.setText("");
        }

        resetFiltersTV.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                return;
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime();
            transactionType.clear();
            transactionSubType.clear();
            txnStatus.clear();
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
            startDateD = null;
            endDateD = null;
            startDateLong = 0L;
            endDateLong = 0L;
            isFilters = false;
            strSelectedDate = "";
            filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));

            searchET.removeTextChangedListener(TransactionListActivity.this);
            searchET.setText("");
            searchET.addTextChangedListener(TransactionListActivity.this);
            transAmountStartET.setText("");
            transAmountEndET.setText("");
            getDateFromPickerET.setText("");

            transAmountStartET.clearFocus();
            searchET.clearFocus();
            transAmountEndET.clearFocus();
            getDateFromPickerET.clearFocus();

            transTypePR.setChecked(false);
            transTypeBT.setChecked(false);
            transTypeSO.setChecked(false);
            transTypeWithdraw.setChecked(false);
            transTypeRefund.setChecked(false);
            transTypeAT.setChecked(false);
            transTypePI.setChecked(false);

            transSubTypeSent.setChecked(false);
            transSubTypeReceived.setChecked(false);
            transSubTypeBA.setChecked(false);
            transSubTypeCC.setChecked(false);
            transSubTypeDC.setChecked(false);
            transSubTypeSignet.setChecked(false);
            transSubTypeIP.setChecked(false);
            transSubTypeGiftCard.setChecked(false);
            transSubTypeSOToken.setChecked(false);
            transSubTypeFW.setChecked(false);
            transSubTypeCW.setChecked(false);

            transStatusPending.setChecked(false);
            transStatusCompleted.setChecked(false);
            transStatusCanceled.setChecked(false);
            transStatusInProgress.setChecked(false);
            transStatusFailed.setChecked(false);

            transAmountStartET.setText("");
            transAmountEndET.setText("");
            getDateFromPickerET.setText("");

            globalPending.clear();
            globalPosted.clear();
            currentPage = 0;
            total = 0;
            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setTransactionType(getDefaultTransactionTypes());
            transactionListRequest.setPageNo(String.valueOf(currentPage));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
            transactionsAPI(transactionListRequest);
            objMyApplication.initializeTransactionSearch();
            objMyApplication.setTransactionListSearch(transactionListRequest);
        });

        transTypePR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    tempTransactionType.add(Utils.payRequest);
                } else {
                    for (int i = 0; i < tempTransactionType.size(); i++) {
                        if (tempTransactionType.get(i) == Utils.payRequest) {
                            tempTransactionType.remove(i);
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
                    tempTransactionType.add(Utils.buyTokens);
                } else {
                    for (int i = 0; i < tempTransactionType.size(); i++) {
                        if (tempTransactionType.get(i) == Utils.buyTokens) {
                            tempTransactionType.remove(i);
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
                    tempTransactionType.add(Utils.saleOrder);
                } else {
                    for (int i = 0; i < tempTransactionType.size(); i++) {
                        if (tempTransactionType.get(i) == Utils.saleOrder) {
                            tempTransactionType.remove(i);
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
                    tempTransactionType.add(Utils.withdraw);
                } else {
                    for (int i = 0; i < tempTransactionType.size(); i++) {
                        if (tempTransactionType.get(i) == Utils.withdraw) {
                            tempTransactionType.remove(i);
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
                    tempTransactionType.add(Utils.refund);
                } else {
                    for (int i = 0; i < tempTransactionType.size(); i++) {
                        if (tempTransactionType.get(i) == Utils.refund) {
                            tempTransactionType.remove(i);
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
                    tempTransactionType.add(Utils.paidInvoice);
                } else {
                    for (int i = 0; i < tempTransactionType.size(); i++) {
                        if (tempTransactionType.get(i) == Utils.paidInvoice) {
                            tempTransactionType.remove(i);
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
                    tempTransactionSubType.add(Utils.sent);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.sent) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.received);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.received) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.bankAccount);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.bankAccount) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.creditCard);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.creditCard) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.debitCard);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.debitCard) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.signet);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.signet) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.instantPay);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.instantPay) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.giftCard);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.giftCard) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.saleOrderToken);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.saleOrderToken) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.failedWithdraw);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.failedWithdraw) {
                            tempTransactionSubType.remove(i);
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
                    tempTransactionSubType.add(Utils.cancelledWithdraw);
                } else {
                    for (int i = 0; i < tempTransactionSubType.size(); i++) {
                        if (tempTransactionSubType.get(i) == Utils.cancelledWithdraw) {
                            tempTransactionSubType.remove(i);
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
                    tempTxnStatus.add(Utils.pending);
                } else {
                    for (int i = 0; i < tempTxnStatus.size(); i++) {
                        if (tempTxnStatus.get(i) == Utils.pending) {
                            tempTxnStatus.remove(i);
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
                    tempTxnStatus.add(Utils.completed);
                } else {
                    for (int i = 0; i < tempTxnStatus.size(); i++) {
                        if (tempTxnStatus.get(i) == Utils.completed) {
                            tempTxnStatus.remove(i);
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
                    tempTxnStatus.add(Utils.cancelled);
//                    transStatusCanceled.setChecked(false);
                } else {
//                    transStatusCanceled.setChecked(false);
                    for (int i = 0; i < tempTxnStatus.size(); i++) {
                        if (tempTxnStatus.get(i) == Utils.cancelled) {
                            tempTxnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusInProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tempTxnStatus.add(Utils.inProgress);
                } else {
                    for (int i = 0; i < tempTxnStatus.size(); i++) {
                        if (tempTxnStatus.get(i) == Utils.inProgress) {
                            tempTxnStatus.remove(i);
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
                    tempTxnStatus.add(Utils.failed);
                } else {
                    for (int i = 0; i < tempTxnStatus.size(); i++) {
                        if (tempTxnStatus.get(i) == Utils.failed) {
                            tempTxnStatus.remove(i);
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
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)))});
                    USFormat(transAmountStartET, "START");

                    try {

                        if (!transAmountStartET.getText().toString().equals("") && !transAmountStartET.getText().toString().equals("")) {

                            Double startAmount = Utils.doubleParsing(transAmountStartET.getText().toString().replace(",", "").trim());
                            Double endAmount = Utils.doubleParsing(transAmountEndET.getText().toString().replace(",", "").trim());
                            if (endAmount < startAmount) {
                                Utils.displayAlert("'From Amount' should not be greater than 'To Amount'", TransactionListActivity.this, "", "");

                                transAmountStartET.setText("");
                                strStartAmount = "";
                                transAmountEndET.setText("");
                                strEndAmount = "";
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)))});
                }
            }
        });

        transAmountEndET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (transAmountStartET.getText().toString().equals("")) {
                    transAmountStartET.setText("0.00");
                }

                if (!hasFocus) {
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)))});
                    USFormat(transAmountEndET, "END");
                    try {

                        if (!transAmountEndET.getText().toString().equals("") && !transAmountEndET.getText().toString().equals("")) {

                            Double startAmount = Utils.doubleParsing(transAmountStartET.getText().toString().replace(",", "").trim());
                            Double endAmount = Utils.doubleParsing(transAmountEndET.getText().toString().replace(",", "").trim());
                            if (endAmount < startAmount) {
                                Utils.displayAlert("'From Amount' should not be greater than 'To Amount'", TransactionListActivity.this, "", "");
                                transAmountStartET.setText("");
                                strStartAmount = "";
                                transAmountEndET.setText("");
                                strEndAmount = "";
                            }
                        }
                        if (transAmountStartET.getText().toString().equals("")) {
                            transAmountStartET.setText("0.00");
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)))});
                }
            }
        });

        transAmountStartET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)))});
                    USFormat(transAmountStartET, "START");
                    transAmountStartET.clearFocus();
                }

                return false;
            }
        });

        transAmountEndET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)))});
                    USFormat(transAmountEndET, "END");
                    transAmountEndET.clearFocus();
                    if (transAmountStartET.getText().toString().equals("")) {
                        transAmountStartET.setText("0.00");
                    }
                }
                return false;
            }
        });

        applyFilterBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatomoUtility.getInstance().trackEvent(MatomoConstants.CUSTOMER_FILTERS, MatomoConstants.CUSTOMER_FILTERS_APPLIED);
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                pendingTxt.setVisibility(View.GONE);
                layoutTransactionspending.setVisibility(View.GONE);
                layoutTransactionsposted.setVisibility(View.GONE);
                nestedScrollView.smoothScrollTo(0, 0);

                globalPending.clear();
                globalPosted.clear();
                currentPage = 0;
                total = 0;
                isFilters = false;
                searchET.removeTextChangedListener(TransactionListActivity.this);
                searchET.setText("");
                searchET.addTextChangedListener(TransactionListActivity.this);
                transAmountStartET.clearFocus();
                transAmountEndET.clearFocus();
                TransactionListRequest transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNo(String.valueOf(currentPage));
                transactionListRequest.setWalletCategory(Utils.walletCategory);
                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
                transactionType = new ArrayList<>();
                transactionType.addAll(tempTransactionType);
                if (transactionType.size() > 0) {
                    isFilters = true;
                    transactionListRequest.setTransactionType(transactionType);
                }
                transactionSubType = new ArrayList<>();
                transactionSubType.addAll(tempTransactionSubType);
                if (transactionSubType.size() > 0) {
                    isFilters = true;
                    transactionListRequest.setTransactionSubType(transactionSubType);
                }
                txnStatus = new ArrayList<>();
                txnStatus.addAll(tempTxnStatus);
                if (txnStatus.size() > 0) {
                    isFilters = true;
                    transactionListRequest.setTxnStatus(txnStatus);
                }
                if (!transAmountStartET.getText().toString().trim().equals("")) {
                    isFilters = true;
                    transactionListRequest.setFromAmount(transAmountStartET.getText().toString().replace(",", ""));
                    transactionListRequest.setFromAmountOperator(">=");
                } else {
                    strStartAmount = "";
                }
                if (!transAmountEndET.getText().toString().trim().equals("")) {
                    isFilters = true;
                    transactionListRequest.setToAmount(transAmountEndET.getText().toString().replace(",", ""));
                    transactionListRequest.setToAmountOperator("<=");

                    if (transAmountStartET.getText().toString().trim().equals("") || transAmountStartET.getText().toString().trim().equals("0.00")) {
                        transactionListRequest.setFromAmount("0.00");
                        transactionListRequest.setFromAmountOperator(">=");
                        strStartAmount = "0.00";
                    }
                } else {
                    strEndAmount = "";
                }
                if (!strFromDate.equals("")) {
                    isFilters = true;
                    transactionListRequest.setUpdatedFromDate(objMyApplication.exportDate(strFromDate));
                    transactionListRequest.setUpdatedFromDateOperator(">=");
                }
                if (!strToDate.equals("")) {
                    isFilters = true;
                    transactionListRequest.setUpdatedToDate(objMyApplication.exportDate(strToDate));
                    transactionListRequest.setUpdatedToDateOperator("<=");
                }
                if (isFilters) {
                    filterIV.setImageDrawable(getDrawable(R.drawable.ic_filter_enabled));
                } else {
                    filterIV.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                }

                if (!transAmountStartET.getText().toString().equals("") && !transAmountEndET.getText().toString().equals("")) {
                    Double startAmount = Utils.doubleParsing(transAmountStartET.getText().toString().replace(",", "").trim());
                    Double endAmount = Utils.doubleParsing(transAmountEndET.getText().toString().replace(",", "").trim());
                    if (endAmount < startAmount) {
                        Utils.displayAlert("'From Amount' should not be greater than 'To Amount'", TransactionListActivity.this, "", "");
                        transAmountStartET.setText("");
                        strStartAmount = "";
                        transAmountEndET.setText("");
                        strEndAmount = "";
                    } else {
                        transactionsAPI(transactionListRequest);
                        objMyApplication.initializeTransactionSearch();
                        objMyApplication.setTransactionListSearch(transactionListRequest);
                        noMoreTransactionTV.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                } else {
                    transactionsAPI(transactionListRequest);
                    objMyApplication.initializeTransactionSearch();
                    objMyApplication.setTransactionListSearch(transactionListRequest);
                    noMoreTransactionTV.setVisibility(View.GONE);
                    dialog.dismiss();

                }

            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);

        dialog.show();

        dateRangePickerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                showCalendar(getDateFromPickerET);
            }
        });

        getDateFromPickerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                showCalendar(getDateFromPickerET);
            }
        });

    }

    private void USFormat(EditText etAmount, String mode) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            etAmount.setSelection(etAmount.getText().length());
            if (mode.equals("START")) {
                strStartAmount = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
            } else {
                strEndAmount = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showCalendar(EditText getDateFromPickerET) {
        // custom dialog
        final Dialog dialog = new Dialog(TransactionListActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, height);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        ImageView closeIV = dialog.findViewById(R.id.closeIV);
        TextView doneTV = dialog.findViewById(R.id.doneTV);
        TextView rangeDateTV = dialog.findViewById(R.id.rangeDateTV);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        doneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    if (!strSelectedDate.equals("")) {
                        getDateFromPickerET.setText(strSelectedDate);
                        new Date(startDateLong).getYear();
                        Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String sDate = formatter.format(startDateLong);
                        String eDate = formatter.format(endDateLong);

                        strFromDate = sDate.split("-")[2] + "-" + Utils.changeFormat(Integer.parseInt(sDate.split("-")[1])) + "-" + Utils.changeFormat(Integer.parseInt(sDate.split("-")[0])) + " 00:00:00.000";

                        Log.e("myear", mYear + " " + mMonth + " " + mDay + " " + strFromDate);

                        if (Integer.parseInt(Utils.changeFormat(Integer.parseInt(eDate.split("-")[0]))) == mDay
                                && Integer.parseInt(Utils.changeFormat(Integer.parseInt(eDate.split("-")[1]))) == (mMonth + 1)
                                && Integer.parseInt(Utils.changeFormat(Integer.parseInt(eDate.split("-")[2]))) == mYear) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                            String str = sdf.format(new Date());
                            strToDate = eDate.split("-")[2] + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[1])) + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[0])) + " " + str;
                        } else {
                            strToDate = eDate.split("-")[2] + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[1])) + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[0])) + " 23:59:59.000";
                        }

                        Log.e("strFromDate", strFromDate);
                        Log.e("strToDate", strToDate);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });


        com.greenbox.coyni.utils.verticalcalendar.CalendarPicker calendarPicker = dialog.findViewById(R.id.calendar_view);
        Calendar startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        Date backwardDate = new Date(startDate.getTime().getTime() - 31556952000L);
        Calendar endDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//        endDate.add(Calendar.MONTH, 12); // Add 6 months ahead from current date
        calendarPicker.setRangeDate(backwardDate, endDate.getTime());
        calendarPicker.showDayOfWeekTitle(true);
        calendarPicker.setMode(com.greenbox.coyni.utils.verticalcalendar.CalendarPicker.SelectionMode.RANGE);
        calendarPicker.scrollToDate(endDate.getTime());


        try {
            if (!strSelectedDate.equals("")) {
                rangeDateTV.setText(strSelectedDate);
                calendarPicker.setSelectionDate(new Date(startDateLong), new Date(endDateLong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendarPicker.setOnRangeSelectedListener((date, date2, s, s2) -> {

            SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");

            try {
                startDateD = f.parse(s);
                endDateD = f.parse(s2);
                startDateLong = startDateD.getTime();
                endDateLong = endDateD.getTime();
                Log.e("startDate long", startDateLong + "  " + endDateLong);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            strSelectedDate = simpleDateFormat.format(startDateLong) + " - " + simpleDateFormat.format(endDateLong);
            rangeDateTV.setText(strSelectedDate);

            return null;
        });

        calendarPicker.setOnStartSelectedListener(new Function2<Date, String, Unit>() {
            @Override
            public Unit invoke(Date date, String s) {
                SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");

                try {
                    startDateD = f.parse(s);
                    endDateD = f.parse(s);
                    startDateLong = startDateD.getTime();
                    endDateLong = endDateD.getTime();
                    Log.e("startDate long", startDateLong + "  " + endDateLong);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                strSelectedDate = simpleDateFormat.format(startDateLong) + " - " + simpleDateFormat.format(endDateLong);
                rangeDateTV.setText(strSelectedDate);

                return null;
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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
            transactionListRequest.setPageNo(String.valueOf(currentPage));
            transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

            transactionsAPI(transactionListRequest);
            objMyApplication.initializeTransactionSearch();
            objMyApplication.setTransactionListSearch(transactionListRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Double getUSFormat(String amount) {
        String strAmount = "";
        try {
//            strAmount = Utils.convertBigDecimalUSDC(amount.trim().replace(",", ""));
            strAmount = amount.trim().replace(",", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.doubleParsing(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
    }

    private void transactionsAPI(TransactionListRequest transactionListRequest) {
        try {
//            progressDialog = Utils.showProgressDialog(this);


            dashboardViewModel.meTransactionList(transactionListRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.payRequest);
        transactionType.add(Utils.withdraw);
        transactionType.add(Utils.buyTokens);
        transactionType.add(Utils.refund);
        transactionType.add(Utils.paidInvoice);
        transactionType.add(Utils.businessPayout);
        return transactionType;
    }

    private ArrayList<Integer> getDefaultTransactionSubTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.sent);
        transactionType.add(Utils.received);
        transactionType.add(Utils.debitCard);
        transactionType.add(Utils.creditCard);
        transactionType.add(Utils.bankAccount);
        transactionType.add(Utils.instantPay);
        transactionType.add(Utils.giftCard);

        return transactionType;
    }
}
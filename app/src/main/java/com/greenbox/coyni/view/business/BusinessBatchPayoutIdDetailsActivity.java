package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.MerchantTransactionListPostedNewAdapter;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.PayoutDetailsTransactionsAdapter;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
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
import java.util.List;

public class BusinessBatchPayoutIdDetailsActivity extends BaseActivity {

    LinearLayout payoytBackLL, payoutRefIdLL, payoutTokenNoLL, payoutReserveIdLL;
//    private MerchantTransactionListPostedNewAdapter transactionListPostedAdapter;
    private PayoutDetailsTransactionsAdapter payoutDetailsTransactionsAdapter;
    private NestedScrollView nestedScrollView;
    private TransactionListPendingAdapter transactionListPendingAdapter;
    private ExpandableHeightRecyclerView rvTransactionsPending, getRvTransactionsPosted;
    private LinearLayout layoutTransactionspending, layoutTransactionsposted, clearTextLL;
    TextView payoutRefIdTV, payoutTokenIdTV, ReserveIdTV;
    private int totalItemCount, currentPage = 1, total = 0;
    private DashboardViewModel dashboardViewModel;
    private List<TransactionListPending> globalPending = new ArrayList<>();
    private List<TransactionListPosted> globalPosted = new ArrayList<>();
    private MyApplication objMyApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_id_details);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        initFields();
        initObservers();

    }

    private void initFields() {
        try {
            payoytBackLL = findViewById(R.id.payoytBackLL);

            payoutRefIdLL = findViewById(R.id.payoutRefIdLL);
            payoutTokenNoLL = findViewById(R.id.payoutTokenNoLL);
            payoutReserveIdLL = findViewById(R.id.payoutReserveIdLL);

            payoutRefIdTV = findViewById(R.id.payoutRefIdTV);
            payoutTokenIdTV = findViewById(R.id.payoutTokenIdTV);
            ReserveIdTV = findViewById(R.id.ReserveIdTV);

            layoutTransactionspending = findViewById(R.id.layoutLLPending);
            layoutTransactionsposted = findViewById(R.id.layoutLLposted);
            rvTransactionsPending = findViewById(R.id.transactionListPendingRV);
            getRvTransactionsPosted = findViewById(R.id.transactionListPostedRV);
            nestedScrollView = findViewById(R.id.nestedSV);


            payoytBackLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            payoutRefIdLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//          Utils.copyText(objData.getReferenceId(), BusinessBatchPayoutIdDetailsActivity.this);
                }
            });

            payoutTokenNoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//          Utils.copyText(objData.getTokenId(), BusinessBatchPayoutIdDetailsActivity.this);
                }
            });
            payoutReserveIdLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//          Utils.copyText(objData.getReserveId(), BusinessBatchPayoutIdDetailsActivity.this);
                }
            });

            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setTransactionType(getDefaultTransactionTypes());
//        transactionListRequest.setWalletCategory(Utils.walletCategory);
            transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));

            transactionsAPI(transactionListRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.saleOrder);
        transactionType.add(Utils.refund);
        transactionType.add(Utils.merchantPayout);
        transactionType.add(Utils.monthlyServiceFee);
        return transactionType;
    }

    private void transactionsAPI(TransactionListRequest transactionListRequest) {
//        showProgressDialog();
        dashboardViewModel.meTransactionList(transactionListRequest);
    }

    private void initObservers() {
        dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
            @Override
            public void onChanged(TransactionList transactionList) {
                dismissDialog();
                try {
                    if (transactionList != null) {
                        if (transactionList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            LogUtils.d(TAG, "list" + transactionList.getData().getItems().getPostedTransactions());
                            try {
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(BusinessBatchPayoutIdDetailsActivity.this);
                                LinearLayoutManager nLayoutManager = new LinearLayoutManager(BusinessBatchPayoutIdDetailsActivity.this);

                                if (transactionList.getData() != null) {
                                    if (transactionList.getData().getItems() != null) {
                                        if (transactionList.getData().getItems().getPendingTransactions() != null) {
                                            globalPending.addAll(transactionList.getData().getItems().getPendingTransactions());
                                            LogUtils.v(TAG, " Get Pending Transactions");
                                        }
                                        if (transactionList.getData().getItems().getPostedTransactions() != null) {
                                            globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                            LogUtils.v(TAG, " Get Posted Transactions");

                                        }
                                    }
                                    total = transactionList.getData().getTotalPages();
                                }
                                getRvTransactionsPosted.setNestedScrollingEnabled(false);
                                rvTransactionsPending.setNestedScrollingEnabled(false);
                                rvTransactionsPending.setExpanded(true);
                                getRvTransactionsPosted.setExpanded(true);

                                if (globalPending.size() > 0) {
                                    transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, BusinessBatchPayoutIdDetailsActivity.this);
//                                    pendingTxt.setVisibility(View.VISIBLE);
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
//                                    pendingTxt.setVisibility(View.GONE);
                                }

                                if (globalPosted.size() > 0) {
                                    //bottomCorners.setVisibility(View.VISIBLE);
                                    payoutDetailsTransactionsAdapter = new PayoutDetailsTransactionsAdapter(globalPosted, BusinessBatchPayoutIdDetailsActivity.this);
                                    getRvTransactionsPosted.setLayoutManager(nLayoutManager);
                                    getRvTransactionsPosted.setItemAnimator(new DefaultItemAnimator());
                                    getRvTransactionsPosted.setAdapter(payoutDetailsTransactionsAdapter);
                                    if (currentPage > 0) {
                                        int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                        getRvTransactionsPosted.scrollToPosition(myPos);
                                    } else {
                                        getRvTransactionsPosted.scrollToPosition(0);
                                    }
                                } else {
//                                    bottomCorners.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.displayAlert(transactionList.getError().getErrorDescription(), BusinessBatchPayoutIdDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutIdDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
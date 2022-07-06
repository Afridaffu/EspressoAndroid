package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
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
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutDetailsRequest;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutIdDetailsData;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutIdDetailsResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessBatchPayoutIdDetailsActivity extends BaseActivity {

    private LinearLayout payoytBackLL, payoutRefIdLL, payoutTokenNoLL, payoutReserveIdLL, reserveIDLL;
    //    private MerchantTransactionListPostedNewAdapter transactionListPostedAdapter;
    private PayoutDetailsTransactionsAdapter payoutDetailsTransactionsAdapter;
    private TransactionListPendingAdapter transactionListPendingAdapter;
    private ExpandableHeightRecyclerView rvTransactionsPending, getRvTransactionsPosted;
    private LinearLayout layoutTransactionspending, layoutTransactionsposted, clearTextLL;
    private TextView payoutRefIdTV, payoutTokenIdTV, ReserveIdTV;
    private int totalItemCount, currentPage = 1, total = 0;
    private DashboardViewModel dashboardViewModel;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private List<TransactionListPending> globalPending = new ArrayList<>();
    private List<TransactionListPosted> globalPosted = new ArrayList<>();
    private MyApplication objMyApplication;
    private TextView bathPayoutIdTV, payoutIDAmountTV, payoutStatusTV, payoutIDdateTimeTV, noTransactions, merchantNoMoreTransactions;
    private BatchPayoutListItems selectedItem;
    private CardView cardViewList;
    BatchPayoutIdDetailsData objData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_id_details);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

        selectedItem = (BatchPayoutListItems) getIntent().getSerializableExtra(Utils.SELECTED_BATCH_PAYOUT);
        initFields();
        initObservers();
        BatchPayoutDetailsRequest batchPayoutDetailsRequest = new BatchPayoutDetailsRequest();
        batchPayoutDetailsRequest.setPayoutId(selectedItem.getBatchId());
        businessDashboardViewModel.batchPayoutIdDetails(batchPayoutDetailsRequest);

    }

    private void initFields() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();

            payoytBackLL = findViewById(R.id.payoytBackLL);

            payoutRefIdLL = findViewById(R.id.payoutRefIdLL);
            payoutTokenNoLL = findViewById(R.id.payoutTokenNoLL);
            payoutReserveIdLL = findViewById(R.id.payoutReserveIdLL);
            reserveIDLL = findViewById(R.id.reserveReleaseIDLL);

            bathPayoutIdTV = findViewById(R.id.bathPayoutIdTV);
            payoutIDAmountTV = findViewById(R.id.payoutIDAmountTV);
            payoutStatusTV = findViewById(R.id.payoutStatusTV);
            payoutIDdateTimeTV = findViewById(R.id.payoutIDdateTimeTV);

            payoutRefIdTV = findViewById(R.id.payoutRefIdTV);
            payoutTokenIdTV = findViewById(R.id.payoutTokenIdTV);
            ReserveIdTV = findViewById(R.id.ReserveIdTV);

            merchantNoMoreTransactions = findViewById(R.id.merchantNoMoreTransactions);
            noTransactions = findViewById(R.id.noTransactions);
            cardViewList = findViewById(R.id.cardViewList);

            layoutTransactionspending = findViewById(R.id.layoutLLPending);
            layoutTransactionsposted = findViewById(R.id.layoutLLposted);
            rvTransactionsPending = findViewById(R.id.transactionListPendingRV);
            getRvTransactionsPosted = findViewById(R.id.transactionListPostedRV);


            payoytBackLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TransactionListRequest transactionListRequest = new TransactionListRequest();
            transactionListRequest.setMerchantTransactions(true);
//            transactionListRequest.setTransactionType(getDefaultTransactionTypes());
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
        showProgressDialog();
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
//                                        if (transactionList.getData().getItems().getPendingTransactions() != null) {
//                                            noTransactions.setVisibility(View.GONE);
//                                            globalPending.addAll(transactionList.getData().getItems().getPendingTransactions());
//                                            LogUtils.v(TAG, " Get Pending Transactions");
//                                        }
                                        if (transactionList.getData().getItems().getPostedTransactions() != null) {
                                            globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                            LogUtils.v(TAG, " Get Posted Transactions");

                                        }
                                    }
                                    total = transactionList.getData().getTotalPages();
                                }
                                getRvTransactionsPosted.setNestedScrollingEnabled(false);
                                getRvTransactionsPosted.setExpanded(true);
//                                rvTransactionsPending.setNestedScrollingEnabled(false);
//                                rvTransactionsPending.setExpanded(true);
//
//                                if (globalPending.size() > 0) {
//                                    noTransactions.setVisibility(View.GONE);
//                                    transactionListPendingAdapter = new TransactionListPendingAdapter(globalPending, BusinessBatchPayoutIdDetailsActivity.this);
////                                    pendingTxt.setVisibility(View.VISIBLE);
//                                    rvTransactionsPending.setLayoutManager(mLayoutManager);
//                                    rvTransactionsPending.setItemAnimator(new DefaultItemAnimator());
//                                    rvTransactionsPending.setAdapter(transactionListPendingAdapter);
//                                    if (currentPage > 0) {
//                                        int myPos = globalPending.size() - transactionList.getData().getItems().getPendingTransactions().size();
//                                        rvTransactionsPending.scrollToPosition(myPos);
//                                    } else {
//                                        rvTransactionsPending.scrollToPosition(0);
//                                    }
//                                } else {
////                                    pendingTxt.setVisibility(View.GONE);
//                                }

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
                                    noTransactions.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.displayAlert(transactionList.getError().getErrorDescription(), BusinessBatchPayoutIdDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                        }
                    } else {
                        merchantNoMoreTransactions.setVisibility(View.GONE);
                        Utils.displayAlert(getString(R.string.something_went_wrong), BusinessBatchPayoutIdDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getBatchPayoutIdDetailsResponseMutableLiveData().observe(this, new Observer<BatchPayoutIdDetailsResponse>() {
            @Override
            public void onChanged(BatchPayoutIdDetailsResponse batchPayoutIdDetailsResponse) {
                if (batchPayoutIdDetailsResponse != null && batchPayoutIdDetailsResponse.getStatus() != null && batchPayoutIdDetailsResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    if (batchPayoutIdDetailsResponse.getData() != null) {
                        showDetails(batchPayoutIdDetailsResponse.getData());
                    }
                }

            }
        });
    }

    private void showDetails(BatchPayoutIdDetailsData objData) {

        if (objData.getBatchId() != null && !objData.getBatchId().equals("")) {
            bathPayoutIdTV.setText(objData.getBatchId());
        }
        if (objData.getAmount() != null && !objData.getAmount().equals("")) {
            payoutIDAmountTV.setText(Utils.convertTwoDecimal(objData.getAmount()));
        }
//        if (objData.getStatus() != null && !objData.getStatus().equals("")) {
//            payoutStatusTV.setText(objData.getStatus());
//        }
        if (objData.getStatus() != null && !objData.getStatus().equals("")) {
            payoutStatusTV.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case "closed":
                    payoutStatusTV.setTextColor(getColor(R.color.completed_status));
                    payoutStatusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case "in progress":
                    payoutStatusTV.setTextColor(getColor(R.color.inprogress_status));
                    payoutStatusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    payoutStatusTV.setTextColor(getColor(R.color.inprogress_status));
                    break;
                case "paid":
                    payoutStatusTV.setTextColor(getColor(R.color.completed_status));
                    payoutStatusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                    payoutStatusTV.setTextColor(getColor(R.color.active_green));
                    break;
                case "failed":
                    payoutStatusTV.setTextColor(getColor(R.color.failed_status));
                    payoutStatusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                    payoutStatusTV.setTextColor(getColor(R.color.error_red));
                    break;
            }
        }
        if (objData.getPayoutDate() != null && !objData.getPayoutDate().equals("")) {
            String date = objData.getPayoutDate();
            if (date.contains(".")) {
                String formattedDate = date.substring(0, date.lastIndexOf("."));
                payoutIDdateTimeTV.setText(objMyApplication.convertZoneDateTime(formattedDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mm a").toLowerCase());
            }
        }

        if (objData.getPayoutReferenceId() != null && !objData.getPayoutReferenceId().equals("")) {
            if (objData.getPayoutReferenceId().length() > 10) {
                payoutRefIdLL.setOnClickListener(v -> Utils.copyText(objData.getPayoutReferenceId(), BusinessBatchPayoutIdDetailsActivity.this));
                SpannableString content = new SpannableString(objData.getPayoutReferenceId().substring(0, 10));
                content = SpannableString.valueOf(content+ "...");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                payoutRefIdTV.setText(content );
            } else {
                payoutRefIdTV.setText(objData.getPayoutReferenceId());
            }
        }
        if (objData.getTokenAccount() != null && !objData.getTokenAccount().equals("")) {
            if (objData.getTokenAccount().length() > 10) {
                payoutTokenNoLL.setOnClickListener(v -> Utils.copyText(objData.getTokenAccount(), BusinessBatchPayoutIdDetailsActivity.this));
                SpannableString content = new SpannableString(objData.getTokenAccount().substring(0, 10));
                content = SpannableString.valueOf(content+ "...");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                payoutTokenIdTV.setText(content);
            } else {
                payoutTokenIdTV.setText(objData.getTokenAccount());
            }
        }
        if (objMyApplication.isReserveEnabled()) {
            if (objData.getReserveWalletId() != null && !objData.getReserveWalletId().equals("")) {
                payoutReserveIdLL.setOnClickListener(v -> Utils.copyText(objData.getReserveWalletId(), BusinessBatchPayoutIdDetailsActivity.this));
                reserveIDLL.setVisibility(View.VISIBLE);
                if (objData.getReserveWalletId().length() > 10) {
                    reserveIDLL.setVisibility(View.VISIBLE);
                    SpannableString content = new SpannableString(objData.getReserveWalletId().substring(0, 10));
                    content = SpannableString.valueOf(content+ "...");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    ReserveIdTV.setText(content);
                } else {
                    reserveIDLL.setVisibility(View.VISIBLE);
                    ReserveIdTV.setText(objData.getReserveWalletId());
                }
            }
        } else {
            reserveIDLL.setVisibility(View.GONE);
        }
    }
}
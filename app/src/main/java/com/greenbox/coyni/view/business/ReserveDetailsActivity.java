package com.greenbox.coyni.view.business;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.ReserveDetailsListAdapter;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.reserveIdDetails.DetailsRequest;
import com.greenbox.coyni.model.reserveIdDetails.DetailsResponse;
import com.greenbox.coyni.model.reserverule.RollingRuleResponse;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReserveDetailsActivity extends BaseActivity {

    private LinearLayout backButton, lycopy, releasedIDcopy;
    private TextView reserveID, amount, statusTV, releaseDateTime, reserveRegular, cancel, reserveIDTV;
    private TextView releasedAMT, releasedDateTime, transID, tvDBAName, onHoldAmt;
    private RecyclerView recyclerViewRv;
    private CardView details, onHoldCv;
    private CardView released;
    private MyApplication myApplication;

    private String status = "", reserveAmount = "", batchId = "", timeDate = "", timeDateTemp = "", reserveRules = "";

    private DashboardViewModel dashboardViewModel;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private ReserveDetailsListAdapter reserveDetailsListAdapter;
    private List<TransactionListPending> globalPending = new ArrayList<>();
    private List<TransactionListPosted> globalPosted = new ArrayList<>();
    private int totalItemCount, currentPage = 1, total = 0;

    private final String date = "date";
    private final String batchID = "BatchId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_details);
        initFields();
        initObserver();
        displayingDetails();

    }

    private void initFields() {

        backButton = findViewById(R.id.lyRDClose);
        reserveID = findViewById(R.id.tvReserveID);
        lycopy = findViewById(R.id.lycopy);
        myApplication = (MyApplication) getApplicationContext();
        reserveIDTV = findViewById(R.id.reserveIDTV);
        amount = findViewById(R.id.tvamount);
        statusTV = findViewById(R.id.reserveStatusTV);
        releaseDateTime = findViewById(R.id.releasedateTimeTV);
        cancel = findViewById(R.id.canceledTV);
        reserveRegular = findViewById(R.id.reserveRegularTV);

        releasedAMT = findViewById(R.id.tvreleasedAMT);
        releasedDateTime = findViewById(R.id.ReleasedDateTimeTv);
        transID = findViewById(R.id.tvtransID);
        releasedIDcopy = findViewById(R.id.releasedIDcopyLL);
        tvDBAName = findViewById(R.id.tvDBAName);

        details = findViewById(R.id.detailsCV);
        onHoldCv = findViewById(R.id.onHoldCV);
        onHoldAmt = findViewById(R.id.onHoldAmtTV);
        released = findViewById(R.id.releasedCV);

        recyclerViewRv = findViewById(R.id.recyclerviewRV);

        backButton.setOnClickListener(v -> finish());
        lycopy.setOnClickListener(v -> Utils.copyText(reserveIDTV.getText().toString(), ReserveDetailsActivity.this));
        releasedIDcopy.setOnClickListener(v -> Utils.copyText(transID.getText().toString(), ReserveDetailsActivity.this));

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        businessDashboardViewModel.getRollingRuleDetails();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        TransactionListRequest transactionListRequest = new TransactionListRequest();
        transactionListRequest.setTransactionType(getDefaultTransactionTypes());
        transactionListRequest.setTxnStatus(getDefaultStatus());

        String createdDate = "2022-01-27 01:54:50.23";
        if (createdDate.trim().contains(" ")) {
            createdDate = createdDate.substring(0, createdDate.lastIndexOf(" "));
        }
        String fromDate = createdDate + " 00:00:00.00";
        String toDate = createdDate + " 23:59:59.00";
        transactionListRequest.setUpdatedFromDate(fromDate);
        transactionListRequest.setUpdatedFromDateOperator(">=");
        transactionListRequest.setUpdatedToDate(toDate);
        transactionListRequest.setUpdatedToDateOperator("<=");
        dashboardViewModel.meTransactionList(transactionListRequest);

    }

    private void getReserveDetails(String payoutId) {
        DetailsRequest detailsRequest = new DetailsRequest();
        detailsRequest.setPayoutId(payoutId);
        detailsRequest.setPayoutType(Utils.reserveRelease);
        businessDashboardViewModel.getReserveIdDetails(detailsRequest);
    }

    private void initObserver() {
        try {
            businessDashboardViewModel.getRollingRuleResponseMutableLiveData().observe(this, new Observer<RollingRuleResponse>() {
                @Override
                public void onChanged(RollingRuleResponse ruleResponse) {
                    if (ruleResponse != null) {
                        if (ruleResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (ruleResponse.getData() != null) {
                                reserveRules = ruleResponse.getData().getReserveAmount().split("\\.")[0] + "% / " + ruleResponse.getData().getReservePeriod() + "days " + ruleResponse.getData().getReserveType();

                                if (!reserveRules.equals("") && reserveRules != null) {

                                    reserveRegular.setText(reserveRules);
                                }

                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", ruleResponse.getError().getFieldErrors().get(0));

                        }
                    }

                }
            });

            dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
                @Override
                public void onChanged(TransactionList transactionList) {
                    try {
                        if (transactionList != null) {
                            if (transactionList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                                LogUtils.d(TAG, "list" + transactionList.getData().getItems().getPostedTransactions());
                                try {
                                    LinearLayoutManager nLayoutManager = new LinearLayoutManager(ReserveDetailsActivity.this);

                                    if (transactionList.getData() != null) {
                                        if (transactionList.getData().getItems() != null) {
                                            if (transactionList.getData().getItems().getPostedTransactions() != null) {
                                                globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                                LogUtils.v(TAG, " Get Posted Transactions");
                                            }
                                        }
                                    }
                                    if (globalPosted.size() > 0) {
                                        reserveDetailsListAdapter = new ReserveDetailsListAdapter(globalPosted, ReserveDetailsActivity.this);
                                        recyclerViewRv.setLayoutManager(nLayoutManager);
                                        recyclerViewRv.setItemAnimator(new DefaultItemAnimator());
                                        recyclerViewRv.setAdapter(reserveDetailsListAdapter);
                                        if (currentPage > 0) {
                                            int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                            recyclerViewRv.scrollToPosition(myPos);
                                        } else {
                                            recyclerViewRv.scrollToPosition(0);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.displayAlert(transactionList.getError().getErrorDescription(), ReserveDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            businessDashboardViewModel.getDetailsResponseMutableLiveData().observe(this, new Observer<DetailsResponse>() {
                @Override
                public void onChanged(DetailsResponse detailsResponse) {
                    if (detailsResponse != null) {
                        if (detailsResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (detailsResponse.getData() != null) {
                                if (detailsResponse.getData().getPayoutReferenceId() != null && detailsResponse.getData().getPayoutReferenceId().equals("")) {
                                    transID.setText(detailsResponse.getData().getPayoutReferenceId());
                                }
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", detailsResponse.getError().getFieldErrors().get(0));

                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.saleOrder);
        return transactionType;
    }

    private ArrayList<Integer> getDefaultStatus() {

        ArrayList<Integer> transactionStatus = new ArrayList<>();
        transactionStatus.add(Utils.completed);
        return transactionStatus;
    }

    private void displayingDetails() {

        BatchPayoutListItems selected = (BatchPayoutListItems) getIntent().getSerializableExtra(Utils.requestSub);
        if(selected.getStatus().equalsIgnoreCase(Utils.RELEASED)) {
            getReserveDetails(selected.getBatchId());
        }
        if (myApplication.getReserveBalance() != null) {
            reserveAmount = Utils.convertTwoDecimal(String.valueOf(myApplication.getReserveBalance()));
            amount.setText(reserveAmount + " CYN");
        }
        if (selected != null && selected.getCreatedAt() != null) {
            timeDate = selected.getCreatedAt();

            if (timeDate.contains(".")) {
                timeDate = timeDate.substring(0, timeDate.lastIndexOf("."));
            }
            timeDate = myApplication.convertZoneDateTime(timeDate, "yyyy-MM-dd hh:mm:ss", "MM/dd/yyyy @ hh:mm a");
        }
        releaseDateTime.setText(timeDate);

        if (selected != null && selected.getBatchId() != null) {
            batchId = selected.getBatchId();
            reserveID.setText(batchId);
            reserveIDTV.setText(batchId);
        }
        if (selected != null && selected.getScheduledRelease() != null) {
            timeDateTemp = selected.getScheduledRelease();

            if (timeDateTemp.contains(".")) {
                timeDateTemp = timeDateTemp.substring(0, timeDateTemp.lastIndexOf("."));
            }
        }
        String arg1 = myApplication.convertZoneDateTime(timeDateTemp, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy");
        String arg2 = Utils.convertTwoDecimal(selected.getReserveAmount()) + " CYN";
        String cancel_desc = getResources().getString(R.string.reserve_canceled_description, arg1, arg2);

        //22  - 22+arg1.length;    length - (64 + arg2.length) length - 64
        SpannableString spannableString = new SpannableString(cancel_desc);

        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.primary_black)), 22, 22 + arg1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 22, 22 + arg1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.primary_black)), cancel_desc.length() - (63 + arg2.length()), cancel_desc.length() - 63, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), cancel_desc.length() - (63 + arg2.length()), cancel_desc.length() - 63, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


        if (selected != null && selected.getStatus() != null) {
            status = selected.getStatus();
            if (!status.equals("")) {
                if (status.equalsIgnoreCase(Utils.RELEASED)) {
                    released.setVisibility(View.VISIBLE);
                    details.setVisibility(View.GONE);
                    onHoldCv.setVisibility(View.GONE);
                    statusTV.setText(Utils.complete);
                    statusTV.setTextColor(getColor(R.color.completed_status));
                    statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                } else if (status.equalsIgnoreCase(Utils.ONHOLD)) {
                    released.setVisibility(View.GONE);
                    details.setVisibility(View.GONE);
                    onHoldCv.setVisibility(View.VISIBLE);
                    onHoldAmt.setText(Html.fromHtml(getResources().getString(R.string.release_scheduled_for, "<font color='#151515'><b>" + myApplication.convertZoneDateTime(timeDateTemp, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy") + "</b></font>", "<font color='#151515'><b>" + Utils.convertTwoDecimal(getIntent().getStringExtra("amount")) + " CYN</b></font>")));
                    statusTV.setText(status);
                    statusTV.setTextColor(getColor(R.color.pending_color));
                    statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                } else {
                    released.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    onHoldCv.setVisibility(View.GONE);
                    cancel.setText(spannableString);
                    statusTV.setText(status);
                    statusTV.setTextColor(getColor(R.color.failed_status));
                    statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                }
            }
        }

        //Released Details
        if (selected != null && selected.getReserveAmount() != null) {
            releasedAMT.setText(selected.getReserveAmount() + " CYN");
        }

        tvDBAName.setText(myApplication.getStrUserName());

    }

}